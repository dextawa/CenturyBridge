"""Generic drift: members whose erased descriptor stayed while their generic types changed.

The full diff (core/segments/damage) is what the JVM refuses to link. A method that still has the
same erased descriptor links fine even when its generic signature changed -- ScoreHolderArgument's
getNames still answers a Collection, only now of ScoreHolder instead of String -- and the first
element a 1.20.2 mod takes out of it is a ClassCastException. The probe cannot see that; this lists
it. For every public or protected member a public class declares in both releases under the same
name and erased descriptor, the parameter and return type signatures (a field's type signature) are
compared; a difference in type arguments is damage, one row per way a mod can use the member (call,
override, new, super, get, put -- the damage tables' usages). What erasure-level code cannot tell
apart is not a difference: type variables compare by position, wildcard variance is dropped (an
element read or written as X is still an X), X<?> is raw X, and a bound that moves the way the
values still flow is none either (Class<? extends Entity> handed where Class<? extends
SyncedDataHolder> is wanted).

Columns past the signatures: the game classes of V's type arguments W no longer has (the row then
depends on how that class's death is bridged: renamed to a W class, the drift may vanish; a facade
object is not the W object vanilla puts in the collection), and "phantom-key" when the drift lies
only inside the type arguments of a phantom class (phantom_classes: one whose type parameters type
no value of its own -- ResourceKey<T>, TagKey<T>, ContextKey<T>). Such a value passes unchanged; it
is a type witness, and the damage is wherever the key fetches a value (a registry lookup answering
the changed element type). It closes when those places are converted -- "genericWitnesses".

  python generic_drift.py <V jar> <W jar> <out.tsv> [--wall <inter-to-mojang.tsv>]
  python generic_drift.py <V jar> <W jar> <out.tsv> [--wall ...] --wiring <step wiring.json>

(--wall: V is the last intermediary release and W the first mojang-named one; V's class and member
names are translated through the table before W is looked up.) With --wiring the rows are the same
and a "closed" column says what closes each one: "rename" (the step's classRenames to W classes make
the signatures agree), the wiring map that names the member for that usage, "ledger", or
"genericWitnesses" -- {"<member key>": {"by": [<wiring keys>], "basis": ...}} for a value that passes
unchanged while the values it stands for convert elsewhere (a key field, a serializer whose values
are converted where the mod hands them in and takes them out): closed only when every key in "by"
is wired. The open count is printed.
"""
import json
import os
import re
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402
from classfile import (ACC_FINAL, ACC_INTERFACE, ACC_PRIVATE, ACC_PROTECTED, ACC_PUBLIC,  # noqa: E402
                       ACC_STATIC, ACC_SYNTHETIC)

GAME = ("net/minecraft/", "com/mojang/blaze3d/", "com/mojang/realmsclient/")

# the wiring maps that take each usage of a member (keys owner.name(desc) / owner.name:desc)
USAGE_MAPS = {
    "call": ("staticRedirects", "instanceRedirects", "callReshapes", "superRedirects"),
    # a NEW site no ctorRedirect claims takes the constructor reshape / adapt in place too
    "new": ("ctorRedirects", "superCtorReshapes", "superCtorAdapts"),
    "super": ("superCtorReshapes", "superCtorAdapts"),
    "get": ("fieldRedirects", "fieldGetters", "instanceFieldGetters"),
    "put": ("fieldRedirects", "instanceFieldSetters"),
}

# The declared use-site variance of the JDK types whose type parameters only flow one way, per
# parameter: + it is only produced (read from), - it is only consumed (written to), . both. A value
# of such a type keeps working when its argument moves that way; the JDK documents these shapes.
JDK_VARIANCE = {
    "java/util/Optional": "+", "java/util/stream/Stream": "+", "java/util/function/Supplier": "+",
    "java/lang/Iterable": "+", "java/util/Iterator": "+", "java/util/Spliterator": "+",
    "java/util/function/Consumer": "-", "java/util/function/Predicate": "-", "java/util/Comparator": "-",
    "java/util/function/ToIntFunction": "-", "java/util/function/ToLongFunction": "-",
    "java/util/function/ToDoubleFunction": "-", "java/util/function/BiConsumer": "--",
    "java/util/function/BiPredicate": "--", "java/util/function/Function": "-+",
    "java/util/function/BiFunction": "--+", "java/util/function/IntFunction": "+",
    "java/util/function/LongFunction": "+", "java/util/function/DoubleFunction": "+",
}


def load(jar):
    classes = {}
    with zipfile.ZipFile(jar) as z:
        for n in z.namelist():
            if n.endswith(".class") and n.startswith(GAME):
                c = classfile.read(z.read(n))
                if c:
                    classes[c.name] = c
    return classes


def jdk_classes():
    """The JDK's own class headers (super, interfaces), read from the running JDK's lib/ct.sym --
    the per-release class signatures javac itself compiles --release against. Found through
    CB_CTSYM, JAVA_HOME, or the java / javac on PATH; empty when none is there (a JDK type then
    only equals itself)."""
    import shutil
    cands = [os.environ.get("CB_CTSYM")]
    if os.environ.get("JAVA_HOME"):
        cands.append(os.path.join(os.environ["JAVA_HOME"], "lib", "ct.sym"))
    for exe in ("javac", "java"):
        p = shutil.which(exe)
        if p:
            cands.append(os.path.join(os.path.dirname(os.path.dirname(os.path.realpath(p))), "lib", "ct.sym"))
    path = next((c for c in cands if c and os.path.isfile(c)), None)
    out = {}
    if not path:
        return out
    with zipfile.ZipFile(path) as z:
        best = {}
        for n in z.namelist():
            if not n.endswith(".sig") or "/java/" not in n:
                continue
            group, rest = n.split("/", 1)
            cls = rest.split("/", 1)[1][:-4]   # past the module directory
            if cls not in best or max(group) > max(best[cls].split("/", 1)[0]):
                best[cls] = n
        for cls, n in best.items():
            c = classfile.read(z.read(n))
            if c:
                out[cls] = c
    return out


def parse_type(sig, i=0):
    """one type signature as a tree: ("L", binary name, (arguments)) | ("T", name) | ("[", element)
    | ("P", primitive) | ("*",); a wildcard's variance is dropped (canon's rule). Returns (node, end)."""
    c = sig[i]
    if c in "+-":
        return parse_type(sig, i + 1)
    if c == "*":
        return ("*",), i + 1
    if c == "[":
        n, e = parse_type(sig, i + 1)
        return ("[", n), e
    if c == "T":
        e = sig.index(";", i)
        return ("T", sig[i + 1:e]), e + 1
    if c != "L":
        return ("P", c), i + 1
    j, name, args = i + 1, "", ()
    while True:
        k = j
        while sig[k] not in "<;.":
            k += 1
        name = sig[j:k] if not name else name + "$" + sig[j:k]
        j = k
        args = ()
        if sig[j] == "<":
            j += 1
            lst = []
            while sig[j] != ">":
                n, j = parse_type(sig, j)
                lst.append(n)
            args = tuple(lst)
            j += 1
        if sig[j] == ".":
            j += 1
            continue
        if all(a == ("*",) for a in args):
            args = ()   # X<?> is raw X
        return ("L", name, args), j + 1


def read_wiring(base):
    """the step's wiring, base file and fragments merged (objects by key, arrays concatenated)"""
    out = {}
    paths = [base]
    if os.path.isdir(base + ".d"):
        paths += sorted(os.path.join(base + ".d", f) for f in os.listdir(base + ".d") if f.endswith(".json"))
    for p in paths:
        for k, v in json.load(open(p, encoding="utf-8")).items():
            if isinstance(v, dict):
                out.setdefault(k, {}).update(v)
            elif isinstance(v, list):
                out.setdefault(k, []).extend(v)
    return out


def map_type(sig, cls, bounds=None):
    """a type signature with every class name passed through cls (inner classes of a parameterized
    outer, LOuter<..>.Inner<..>;, by their binary name Outer$Inner) and every type variable as its
    erasure (bounds: name -> erased bound; what the JVM checks a value of it against), T* when the
    variable is not known here"""
    out, stack, i = [], [], 0
    while i < len(sig):
        c = sig[i]
        if c == "L" or c == ".":
            j = i + 1
            while sig[j] not in "<;.":
                j += 1
            if c == "L":
                b = sig[i + 1:j]
                out.append("L" + cls(b))
            else:
                b = stack.pop() + "$" + sig[i + 1:j]
                t = cls(b)
                out.append("." + t[t.rfind("$") + 1:])
            stack.append(b)
            i = j
        elif c == ";":
            stack.pop()
            out.append(c)
            i += 1
        elif c == "T":
            j = sig.index(";", i)
            name = sig[i + 1:j]
            out.append("L" + cls(bounds[name]) + ";" if bounds and name in bounds else "T*;")
            i = j + 1
        else:
            out.append(c)
            i += 1
    return "".join(out)


def canon(sig):
    """what erasure-level code cannot tell apart taken out: wildcard variance, X<?> -> X"""
    return re.sub(r"<\*+>", "", re.sub(r"[+-](?=[LT\[])", "", sig))


def type_end(sig, j):
    """end (exclusive) of the type signature starting at j"""
    while sig[j] == "[":
        j += 1
    if sig[j] in "+-":
        return type_end(sig, j + 1)
    if sig[j] in "ZBCSIJFDV*":
        return j + 1
    if sig[j] == "T":
        return sig.index(";", j) + 1
    depth = 0
    while True:
        ch = sig[j]
        if ch == "<":
            depth += 1
        elif ch == ">":
            depth -= 1
        elif ch == ";" and depth == 0:
            return j + 1
        j += 1


def formal_params(sig):
    """the names a generic signature's <K:..;V::..;> section declares"""
    names = []
    if sig and sig.startswith("<"):
        i = 1
        while sig[i] != ">":
            j = sig.index(":", i)
            names.append(sig[i:j])
            i = j
            while sig[i] == ":":
                i += 1
                if sig[i] in "LT[":
                    i = type_end(sig, i)
    return names


def formal_bounds(sig, outer=None):
    """name -> erasure of each type parameter a generic signature declares (its leftmost bound; a
    bound that is itself a type variable resolves through the same list, then through outer)"""
    raw, order = {}, []
    if sig and sig.startswith("<"):
        i = 1
        while sig[i] != ">":
            j = sig.index(":", i)
            name = sig[i:j]
            i = j
            first = None
            while sig[i] == ":":
                i += 1
                if sig[i] in "LT[":
                    e = type_end(sig, i)
                    first = first or sig[i:e]
                    i = e
            raw[name] = first
            order.append(name)
    out = dict(outer or {})

    def erase(t, depth=0):
        if t is None or t[0] == "[":
            return "java/lang/Object"
        if t[0] == "T":
            n = t[1:-1]
            if n in raw and depth < 8:
                return erase(raw[n], depth + 1)
            return out.get(n, "java/lang/Object")
        t = t[1:-1]
        while "<" in t:
            t = re.sub(r"<[^<>]*>", "", t)
        return t.replace(".", "$")
    for n in order:
        out[n] = erase(raw[n])
    return out


def strip_phantom(sig, phantom):
    """a type signature with the type-argument lists of phantom classes taken out"""
    out, stack, i = [], [], 0
    while i < len(sig):
        c = sig[i]
        if c == "L" or c == ".":
            j = i + 1
            while sig[j] not in "<;.":
                j += 1
            b = sig[i + 1:j] if c == "L" else stack.pop() + "$" + sig[i + 1:j]
            out.append(sig[i:j])
            stack.append(b)
            i = j
            if sig[i] == "<" and b in phantom:
                i = _skip_args(sig, i)
        elif c == ";":
            stack.pop()
            out.append(c)
            i += 1
        elif c == "T":
            j = sig.index(";", i) + 1
            out.append(sig[i:j])
            i = j
        else:
            out.append(c)
            i += 1
    return "".join(out)


def _skip_args(sig, i):
    """index just past the <...> list starting at i"""
    depth = 0
    while True:
        if sig[i] == "<":
            depth += 1
        elif sig[i] == ">":
            depth -= 1
            if depth == 0:
                return i + 1
        i += 1


def phantom_classes(classes):
    """Generic classes none of whose type parameters ever types a value: no instance field and no
    instance method parameter or return of the class (or of a class nested in it that does not
    declare the name again) mentions the parameter, other than inside the type arguments of such a
    class -- the greatest such set. ResourceKey<T> is one: a key's identity is its registry and
    location, so ResourceKey<Codec<X>> and ResourceKey<MapCodec<X>> are the same value, and
    nothing is ever cast to the argument. A type argument of a phantom class is no drift."""
    gen = {n: formal_params(c.signature) for n, c in classes.items() if c.signature and c.signature.startswith("<")}
    uses = {n: [] for n in gen}
    for n, c in classes.items():
        own = set(formal_params(c.signature)) if c.signature else set()
        owners = []
        cur = n
        while True:
            if cur in gen and (cur == n or not own & set(gen[cur])):
                owners.append(cur)
            d = cur.rfind("$")
            if d < 0:
                break
            cur = cur[:d]
        if not owners:
            continue
        sigs = [(f[3], set()) for f in c.fields if f[3] and not f[2] & ACC_STATIC]
        for m in c.methods:
            if m.signature and not m.access & ACC_STATIC:
                sp = classfile.split_signature(m.signature)
                if sp:
                    shadow = set(formal_params(m.signature))
                    sigs += [(t, shadow) for t in sp[0] + [sp[1]]]
        if c.signature:
            # the generic supertypes: HierarchicalModel<E> hands E on to EntityModel<E>.setupAnim(E, ..)
            k = _skip_args(c.signature, 0) if c.signature.startswith("<") else 0
            while k < len(c.signature):
                e = type_end(c.signature, k)
                sigs.append((c.signature[k:e], set()))
                k = e
        for o in owners:
            uses[o] += sigs
    phantom = set(gen)
    changed = True
    while changed:
        changed = False
        for g in sorted(phantom):
            pats = [re.compile(r"(?:^|(?<=[<;(\[)*+\-:]))T" + re.escape(p) + ";") for p in gen[g]]
            for sig, shadow in uses[g]:
                s = strip_phantom(sig, phantom)
                if any(p not in shadow and pat.search(s) for p, pat in zip(gen[g], pats)):
                    phantom.discard(g)
                    changed = True
                    break
    return phantom


def args_of(sig):
    k = sig.find("<")
    return sig[k:] if k >= 0 else ""




def wired_keys(wiring):
    """every member key the wiring names: map keys, and under.member of the hook lists"""
    keys = set()
    for k, v in wiring.items():
        if isinstance(v, dict):
            keys |= set(v)
        elif isinstance(v, list):
            for h in v:
                if isinstance(h, dict):
                    if "under" in h and "member" in h:
                        keys.add(h["under"] + "." + h["member"])
                    for f in ("old", "member"):
                        if f in h:
                            keys.add(h[f])
    return keys


def main():
    args = sys.argv[1:]
    cmap, mmap, fmap, wiring = {}, {}, {}, None
    if "--wall" in args:
        k = args.index("--wall")
        for line in open(args[k + 1], encoding="utf-8"):
            f = line.rstrip("\n").split("\t")
            if len(f) >= 3:
                {"C": cmap, "M": mmap, "F": fmap}.get(f[0], {})[f[1]] = f[2]
        args = args[:k] + args[k + 2:]
    if "--wiring" in args:
        k = args.index("--wiring")
        wiring = read_wiring(args[k + 1])
        args = args[:k] + args[k + 2:]
    vjar, wjar, out = args[:3]
    V, W = load(vjar), load(wjar)
    renames = {k: v for k, v in (wiring or {}).get("classRenames", {}).items() if v.startswith(GAME)}

    def tcls(c):
        """V's class as W names it (the wall table)"""
        if c in cmap:
            return cmap[c]
        d = c.rfind("$")
        return tcls(c[:d]) + c[d:] if d > 0 else c

    def rcls(c):
        """V's class as a mod's converted code names it: the step's renames to a W class, then the wall"""
        return renames.get(c) or tcls(c)

    def tdesc(d):
        return re.sub(r"L([^;]+);", lambda x: "L" + tcls(x.group(1)) + ";", d)

    def tmember(table, owner, name):
        """W's name for V's member (the wall renames intermediary members; stable names stay)"""
        t = table.get(owner + "." + name)
        return t[t.rindex(".") + 1:] if t else name

    PV, PW = phantom_classes(V), phantom_classes(W)
    JDK = jdk_classes()
    # W's names of the classes V has without type parameters: a class 1.20.3 made generic
    # (CommandFunction -> CommandFunction<T>) answered V code nothing typed by them, so W's
    # arguments on it are no drift; V code reaches its values through its V members, diffed apart
    NG = {tcls(n) for n, c in V.items() if not (c.signature and c.signature.startswith("<"))}

    def class_bounds(classes, name):
        """the type parameters in scope in a class: its own, then its outer classes' (a nested class
        that is not static can use them)"""
        chain = []
        cur = name
        while True:
            chain.append(cur)
            d = cur.rfind("$")
            if d < 0:
                break
            cur = cur[:d]
        b = {}
        for c in reversed(chain):
            k = classes.get(c)
            if k is not None and k.signature:
                b = formal_bounds(k.signature, b)
        return b

    def drifts(x, y, cls, bx, by, phantom=False):
        """V's type signature x and W's y differ in type arguments (phantom=True: even with the
        arguments of phantom classes taken out); bx / by: the type variables in scope on each side"""
        if phantom:
            x, y = strip_phantom(x, PV), strip_phantom(y, PW)
        # a type variable both sides name whose bound only moved upward: whatever a V mod chose for
        # it lay within V's bound, and V's bound is a W subtype of W's (1.20.2 SimpleCriterionTrigger
        # <T extends SimpleInstance>, which 1.20.2's AbstractCriterionTriggerInstance implements) --
        # every value V code passes still fits, so the variable is the same on both sides
        same = {n for n in (bx or {}) if n in (by or {}) and wsub(cls(bx[n]), by[n])}
        if same:
            bx = {n: t for n, t in bx.items() if n not in same}
            by = {n: t for n, t in by.items() if n not in same}
        a = args_of(canon(map_type(x, cls, bx)))
        b = args_of(canon(map_type(strip_phantom(y, NG), lambda c: c, by)))
        return bool(a and b and a != b)

    def wsub(a, b):
        """a is b or a W (or JDK) subtype of it"""
        if b == "java/lang/Object":
            return True
        todo, seen = [a], set()
        while todo:
            c = todo.pop()
            if c == b:
                return True
            if c in seen:
                continue
            seen.add(c)
            k = W.get(c) or JDK.get(c)
            if k is None:
                continue
            todo += ([k.super] if k.super else []) + list(k.interfaces)
        return False

    def caller_typed(x, y, cls, bx, by, mvars):
        """V's x against W's y where W's member declares its own type variables (mvars): at a call
        the caller chooses them, so V's concrete type at such a position is fine when it lies within
        the variable's bound -- ReturnCommand.register(CommandDispatcher<T extends
        ExecutionCommandSource<T>>) handed 1.20.2's CommandDispatcher<CommandSourceStack>"""
        if not mvars:
            return False
        vn = parse_type(map_type(x, cls, bx))[0]
        wn = parse_type(strip_phantom(y, NG))[0]

        def eq(v, w):
            if w[0] == "T":
                if w[1] in mvars:
                    return v[0] == "L" and wsub(v[1], by.get(w[1], "java/lang/Object"))
                w = ("L", by.get(w[1], "java/lang/Object"), ())
            if v[0] != w[0]:
                return False
            if v[0] == "L":
                return v[1] == w[1] and (not v[2] and not w[2] or len(v[2]) == len(w[2]) and all(map(eq, v[2], w[2])))
            if v[0] == "[":
                return eq(v[1], w[1])
            return v == w
        return eq(vn, wn)

    def top_args(sig):
        """(base, [type arguments]) of a parameterized class type signature, else None"""
        if not sig.startswith("L"):
            return None
        k = sig.find("<")
        if k < 0 or 0 <= sig.find(".") < k:
            return None
        out, i = [], k + 1
        while sig[i] != ">":
            e = type_end(sig, i)
            out.append(sig[i:e])
            i = e
        return sig[:k], out

    def split_arg(t):
        """(variance marker, class, nested type arguments) of one type argument"""
        m = t[0] if t[0] in "+-" else ""
        t = t[len(m):]
        if not t.startswith("L"):
            return m, None, None
        k = t.find("<")
        return (m, t[1:-1], "") if k < 0 else (m, t[1:k], t[k:])

    def safe(x, y, cls, inward, bx, by):
        """the drift of V's x to W's y is only a bound moving the way values can still flow. Inward (the
        mod hands the value to vanilla) a covariant argument -- ? extends, or a position the JDK type
        only produces from (Optional, Stream, Supplier, Iterable) -- may widen and a contravariant one
        (? super, Consumer, Predicate, Comparator) narrow; outward the other way round"""
        a = top_args(map_type(x, cls, bx))
        b = top_args(map_type(strip_phantom(y, NG), lambda c: c, by))
        if not a or not b or a[0] != b[0] or len(a[1]) != len(b[1]):
            return False
        table = JDK_VARIANCE.get(a[0][1:], "")
        for k, (va, wa) in enumerate(zip(a[1], b[1])):
            if canon(va) == canon(wa):
                continue
            mv, cv, nv = split_arg(va)
            mw, cw, nw = split_arg(wa)
            tv = table[k] if k < len(table) and table[k] in "+-" else ""
            if mv and mw:
                m = mv if mv == mw else ""
            elif mv or mw:
                m = (mv or mw) if (mv or mw) == tv else ""
            else:
                m = tv
            if not m or cv is None or cw is None or canon(nv) != canon(nw):
                return False
            if not (wsub(cv, cw) if (m == "+") == inward else wsub(cw, cv)):
                return False
        return True

    def gone(x):
        return {c for c in re.findall(r"L([^;<>.]+)", args_of(x)) if c in V and tcls(c) not in W}

    rows = []

    def add(usage, owner, name, desc, hit, vsig, wsig, bx, by):
        """one row for a usage whose drifted positions are hit = [(where, V sig, W sig, inward)]"""
        rows.append({
            "usage": usage, "owner": owner, "name": name, "desc": desc,
            "changed": ",".join(w for w, _, _, _ in hit), "vsig": vsig, "wsig": wsig,
            "lacks": " ".join(sorted(set().union(*(gone(x) for _, x, _, _ in hit)))),
            "note": "phantom-key" if all(not drifts(x, y, tcls, bx, by, phantom=True) for _, x, y, _ in hit) else "",
            "renamed": all(not drifts(x, y, rcls, bx, by) or safe(x, y, rcls, inw, bx, by) for _, x, y, inw in hit),
        })

    for name, vc in V.items():
        wc = W.get(tcls(name))
        if not wc or not vc.access & ACC_PUBLIC:
            continue
        final_cls = vc.access & ACC_FINAL
        vcb, wcb = class_bounds(V, name), class_bounds(W, tcls(name))
        wm = {(m.name, m.desc): m for m in wc.methods}
        for m in vc.methods:
            if not m.signature or m.access & (ACC_PRIVATE | ACC_SYNTHETIC) or not m.access & (ACC_PUBLIC | ACC_PROTECTED):
                continue
            o = wm.get((tmember(mmap, name, m.name), tdesc(m.desc)))
            if not o or not o.signature:
                continue
            a, b = classfile.split_signature(m.signature), classfile.split_signature(o.signature)
            if not a or not b:
                continue
            # a static method sees no class type parameter: only its own
            bx = formal_bounds(m.signature, {} if m.access & ACC_STATIC else vcb)
            by = formal_bounds(o.signature, {} if o.access & ACC_STATIC else wcb)
            mvars = set(formal_params(o.signature))
            pairs = [(f"param {i}", x, y) for i, (x, y) in enumerate(zip(a[0], b[0]))] + [("return", a[1], b[1])]
            drifted = [(w, x, y) for w, x, y in pairs if drifts(x, y, tcls, bx, by)]
            if not drifted:
                continue
            if m.name == "<init>":
                usages = ["new"] + ([] if final_cls else ["super"])
            else:
                usages = ["call"]
                if not (m.access & (ACC_STATIC | ACC_FINAL) or final_cls):
                    usages.append("override")
            for u in usages:
                # which way each value flows for this usage: a call hands its arguments to vanilla and
                # takes the return back; an override is handed the arguments and answers the return
                hit = [(w, x, y, (w == "return") == (u == "override")) for w, x, y in drifted]
                hit = [h for h in hit if not safe(h[1], h[2], tcls, h[3], bx, by)
                       and not (u != "override" and caller_typed(h[1], h[2], tcls, bx, by, mvars))]
                if hit:
                    add(u, name, m.name, m.desc, hit, m.signature, o.signature, bx, by)
        wf = {(f[0], f[1]): f for f in wc.fields}
        for nm, ds, acc, sig in vc.fields:
            if not sig or acc & ACC_PRIVATE or not acc & (ACC_PUBLIC | ACC_PROTECTED):
                continue
            o = wf.get((tmember(fmap, name, nm), tdesc(ds)))
            bx, by = ({}, {}) if acc & ACC_STATIC else (vcb, wcb)
            if not o or not o[3] or not drifts(sig, o[3], tcls, bx, by):
                continue
            for u in ["get"] + ([] if acc & ACC_FINAL or vc.access & ACC_INTERFACE else ["put"]):
                if not safe(sig, o[3], tcls, u == "put", bx, by):
                    add(u, name, nm, ds, [("type", sig, o[3], u == "put")], sig, o[3], bx, by)
    rows.sort(key=lambda r: (r["owner"], r["name"], r["desc"], r["usage"]))

    cols = ["usage", "owner", "name", "desc", "changed", "vsig", "wsig", "lacks", "note"]
    header = ["usage", "owner", "name", "desc", "changed", "V signature", "W signature", "V classes W lacks", "note"]
    if wiring is not None:
        ledger = wiring.get("ledger", {})
        witnesses = wiring.get("genericWitnesses", {})
        wired = wired_keys(wiring)

        def supers(c):
            """c and every V supertype of it"""
            seen, todo = set(), [c]
            while todo:
                x = todo.pop()
                if x in seen:
                    continue
                seen.add(x)
                k = V.get(x)
                if k:
                    todo += ([k.super] if k.super else []) + list(k.interfaces)
            return seen

        def override_closed(owner, member):
            """the wiring kind whose override side covers every mod override of owner's member: a hook
            keyed on the owner or one of its supertypes (or on no class at all)"""
            up = supers(owner)
            for h in wiring.get("convertHooks", []):
                if h.get("member") == member and h.get("under") in up:
                    return "convertHooks"
            for h in wiring.get("overrideHooks", []):
                if isinstance(h, dict) and h.get("old") == member and (h.get("onlyUnder") in up or not h.get("onlyUnder")):
                    return "overrideHooks"
            for h in wiring.get("adapterHooks", []):
                if member in h.get("requires", []) and h.get("under") in up:
                    return "adapterHooks"
            return None

        def closed(r):
            if r["renamed"]:
                return "rename"
            u = r["usage"]
            key = r["owner"] + "." + r["name"] + (":" if u in ("get", "put") else "") + r["desc"]
            if u == "override":
                c = override_closed(r["owner"], r["name"] + r["desc"])
                if c:
                    return c
            else:
                for mp in USAGE_MAPS[u]:
                    if key in wiring.get(mp, {}):
                        return mp
            wt = witnesses.get(key)
            if wt and wt.get("by") and all(k in wired for k in wt["by"]):
                return "genericWitnesses"
            for k in (key, r["owner"]):
                if k in ledger:
                    return "ledger"
            return ""
        cols.append("closed")
        header.append("closed")
        for r in rows:
            r["closed"] = closed(r)
    with open(out, "w", encoding="utf-8", newline="\n") as f:
        f.write("\t".join(header) + "\n")
        for r in rows:
            f.write("\t".join(r[c] for c in cols) + "\n")
    live = sum(1 for r in rows if not r["lacks"])
    msg = f"{len(rows)} member usages with generic drift ({live} between classes both releases have, " \
          f"{sum(1 for r in rows if r['note'])} phantom-key)"
    if wiring is not None:
        msg += f"; open {sum(1 for r in rows if not r['closed'])}"
    print(msg + f" -> {out}")


if __name__ == "__main__":
    main()
