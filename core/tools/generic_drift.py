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

Three more differences are none, all read off the signatures alone:
  A. Bound-only: with every class type parameter compared by its place instead of its erasure the
     two signatures agree -- only a parameter's declared bound moved (LootDataType<T>: Object ->
     Validatable, registryKey() answering ResourceKey<Registry<T>> on both sides): every value has
     the type it had. Not for constructors, nor for any member of an interface or of a class one of
     whose constructors a mod reaches unreported: where a mod picks the argument a narrowed bound is
     damage, and that row must stay.
  B. Declaration-site variance (infer_variance): a W class whose type parameter its members only
     take (contravariant) or only hand out (covariant), through its supertypes and nested types,
     judged at every nesting level the way the JDK table is (OneShot<E>: E only taken, so
     BabyFollowAdult.create answering OneShot<LivingEntity> where V answered OneShot<AgeableMob> is
     safe to the caller). A use it cannot classify (a class outside the jar, java.util.List, an
     array, a method type parameter bounded by it) counts both ways and keeps the row.
  C. Removed type parameter (DROP in main): W's class declares V's type parameters minus some, in
     order and by name (26.2: CubicSpline<C, I> -> CubicSpline<I>, its Builder / Multipoint / Constant
     alike). With the removed parameters' arguments taken out of V's signature the two agree, and
     the taken arguments name no type variable the member still uses elsewhere: the values are the
     same objects (a CubicSpline is a CubicSpline). Only where the removed parameter types no value
     W still links: no kept parameter is bounded by it bare, every V instance member using it
     outside those argument positions is gone from W (26.1.2's apply(C) -- damage the probe's table
     carries), and no V supertype whose arguments use it is still a W supertype.
--dropped <tsv> lists the rows (or positions) these rules took, with the rule.

Columns past the signatures: the game classes of V's type arguments W no longer has (the row then
depends on how that class's death is bridged: renamed to a W class, the drift may vanish; a facade
object is not the W object vanilla puts in the collection), and "phantom-key" when the drift lies
only inside the type arguments of a phantom class (phantom_classes: one whose type parameters type
no value of its own -- ResourceKey<T>, TagKey<T>, ContextKey<T>). Such a value passes unchanged; it
is a type witness, and the damage is wherever the key fetches a value (a registry lookup answering
the changed element type). It closes when those places are converted -- "genericWitnesses".

  python generic_drift.py <V jar> <W jar> <out.tsv> [--wall <inter-to-mojang.tsv>] [--dropped <tsv>]
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


def drop_type_args(sig, drop):
    """Rule C's rewrite: a type signature with the type arguments at drop[binary name] (indices) taken out
    of each such class's argument list, and the type-variable names those taken arguments named"""
    gone = set()

    def tvars(node, acc):
        if node[0] == "T":
            acc.add(node[1])
        elif node[0] == "[":
            tvars(node[1], acc)
        elif node[0] == "L":
            for a in node[2]:
                tvars(a, acc)

    def ty(i):
        c = sig[i]
        if c in "+-[":
            s, e = ty(i + 1)
            return c + s, e
        if c == "*" or c in "ZBCSIJFDV":
            return c, i + 1
        if c == "T":
            e = sig.index(";", i)
            return sig[i:e + 1], e + 1
        j, name, parts = i + 1, "", ["L"]
        while True:
            k = j
            while sig[k] not in "<;.":
                k += 1
            seg = sig[j:k]
            name = seg if not name else name + "$" + seg
            parts.append(seg)
            j = k
            if sig[j] == "<":
                j += 1
                idx, args, rm = 0, [], drop.get(name, ())
                while sig[j] != ">":
                    e = type_end(sig, j)
                    if idx in rm:
                        tvars(parse_type(sig, j)[0], gone)
                    else:
                        args.append(ty(j)[0])
                    j, idx = e, idx + 1
                j += 1
                if args:
                    parts.append("<" + "".join(args) + ">")
            if sig[j] == ".":
                parts.append(".")
                j += 1
                continue
            parts.append(";")
            return "".join(parts), j + 1
    return ty(0)[0], gone


def sig_tvars(sig):
    """the type-variable names a type signature uses"""
    return set(re.findall(r"(?<![^<;()\[+\-:])T([^;<>:]+);", sig))


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


def wtree(sig, i=0):
    """one type signature as a tree that keeps wildcards: ("L", binary name, ((wildcard, node), ..),
    (outer argument nodes, ..)) with wildcard "" / "+" / "-" / "*" (node None) | ("T", name) |
    ("[", element) | ("P", primitive). Returns (node, end)."""
    c = sig[i]
    if c == "[":
        n, e = wtree(sig, i + 1)
        return ("[", n), e
    if c == "T":
        e = sig.index(";", i)
        return ("T", sig[i + 1:e]), e + 1
    if c != "L":
        return ("P", c), i + 1
    j, name, args, outer = i + 1, "", (), []
    while True:
        k = j
        while sig[k] not in "<;.":
            k += 1
        name = sig[j:k] if not name else name + "$" + sig[j:k]
        j = k
        outer += [n for _, n in args if n is not None]
        args = ()
        if sig[j] == "<":
            j += 1
            lst = []
            while sig[j] != ">":
                if sig[j] == "*":
                    lst.append(("*", None))
                    j += 1
                    continue
                w = sig[j] if sig[j] in "+-" else ""
                n, j = wtree(sig, j + len(w))
                lst.append((w, n))
            args = tuple(lst)
            j += 1
        if sig[j] == ".":
            j += 1
            continue
        return ("L", name, args, tuple(outer)), j + 1


def formal_list(sig):
    """[(name, [bound type signatures])] of the <...> section of a generic signature"""
    out = []
    if sig and sig.startswith("<"):
        i = 1
        while sig[i] != ">":
            j = sig.index(":", i)
            name, bounds = sig[i:j], []
            i = j
            while sig[i] == ":":
                i += 1
                if sig[i] in "LT[":
                    e = type_end(sig, i)
                    bounds.append(sig[i:e])
                    i = e
            out.append((name, bounds))
    return out


def infer_variance(classes, jdk_variance):
    """Declaration-site variance of every type parameter of every generic class here (W's), read off
    the class files: which way values of the parameter can flow between an instance and whoever holds
    it. "out" when a member hands one out (a method return, a field read), "in" when a member takes
    one (a method parameter, a field write), through nested types by the variance of the type they
    sit in (Consumer<T> taken is T out of the instance: the holder's consumer is handed T values) and
    by use-site wildcards (? extends keeps only a nested type's out-flow, ? super only its in-flow).
    Counted: every instance member but private ones and constructors, the generic supertypes (C<T>
    is a D<T>: D's flows of T are C's), any mention in a nested class (both ways: an inner class
    reaches the instance's values), and a method's own type parameter bounded by it (both ways). The
    bound of another class parameter (P extends Predicate<T>) is no flow: it only restricts which
    arguments are legal; the values the instance holds as P are typed by P's argument, which sub
    judges by P's own variance (an unchanged argument passes the same values). A class outside this
    jar, or a JDK class outside the JDK's variance table, flows both ways.
    The least fixed point of these flows; a parameter left with none (a phantom witness: ResourceKey
    <T>) is then pinned to both ways and the fixed point taken again, so nothing reached through it
    counts as one-way. Answers name -> [marker per parameter]: "+" only out (covariant: a holder may
    read it as a supertype), "-" only in (contravariant: a holder may hand it a subtype), "." both."""
    gen = {n: formal_list(c.signature) for n, c in classes.items() if c.signature and c.signature.startswith("<")}
    idx = {n: {p: k for k, (p, _) in enumerate(fs)} for n, fs in gen.items()}
    flows = {n: [set() for _ in fs] for n, fs in gen.items()}
    BOTH = {"out", "in"}

    def flows_of(cls, k):
        if cls in flows:
            return flows[cls][k] if k < len(flows[cls]) else BOTH
        t = jdk_variance.get(cls, "")
        m = t[k] if k < len(t) else "."
        return {"out"} if m == "+" else {"in"} if m == "-" else BOTH

    def walk(node, pol, vars_, add):
        """the occurrences of vars_ in node at polarity pol (+1 out, -1 in, 0 both)"""
        t = node[0]
        if t == "T":
            if node[1] in vars_:
                add(node[1], pol)
        elif t == "[":
            walk(node[1], 0, vars_, add)   # an array is read and written
        elif t == "L":
            for n in node[3]:
                walk(n, 0, vars_, add)
            for k, (w, sub) in enumerate(node[2]):
                if w == "*":
                    continue
                fl = flows_of(node[1], k)
                if w == "+":
                    fl = fl & {"out"}
                elif w == "-":
                    fl = fl & {"in"}
                for f in list(fl):
                    walk(sub, 0 if pol == 0 else (pol if f == "out" else -pol), vars_, add)

    # the occurrences per class: (tree, polarity, names in scope as the class's parameters)
    occ, nested = {}, {}
    for n in gen:
        names = set(idx[n])
        c = classes[n]
        lst = []
        k = _skip_args(c.signature, 0)
        while k < len(c.signature):
            e = type_end(c.signature, k)
            lst.append((wtree(c.signature[k:e])[0], 1, names))
            k = e
        for m in c.methods:
            if not m.signature or m.access & (ACC_STATIC | ACC_PRIVATE) or m.name in ("<init>", "<clinit>"):
                continue
            sp = classfile.split_signature(m.signature)
            own = formal_list(m.signature)
            scope = names - {p for p, _ in own}
            if sp is None:
                lst.append((None, 0, scope))
                continue
            for _, bs in own:
                lst += [(wtree(b)[0], 0, scope) for b in bs]
            lst += [(wtree(p)[0], -1, scope) for p in sp[0]]
            lst.append((wtree(sp[1])[0], 1, scope))
            if "^" in m.signature:
                th = m.signature[m.signature.index("^"):]
                lst += [(("T", p), 0, scope) for p in scope if "T" + p + ";" in th]
        for fn, fd, acc, sig in c.fields:
            if sig and not acc & (ACC_STATIC | ACC_PRIVATE):
                lst.append((wtree(sig)[0], 1 if acc & ACC_FINAL else 0, names))
        occ[n] = lst
        nested[n] = set()
    # a nested class that mentions the parameters of an enclosing one (not declaring the name again)
    for n, c in classes.items():
        own = set(p for p, _ in formal_list(c.signature)) if c.signature else set()
        sigs = [c.signature or ""] + [m.signature or "" for m in c.methods] + [f[3] or "" for f in c.fields]
        cur = n
        while "$" in cur:
            cur = cur[:cur.rfind("$")]
            if cur in gen:
                for p in idx[cur]:
                    if p not in own and any(re.search(r"(?:^|(?<=[<;(\[)*+\-:^]))T" + re.escape(p) + ";", s) for s in sigs):
                        nested[cur].add(p)
            k = classes.get(cur)
            if k is not None and k.signature:
                own |= set(p for p, _ in formal_list(k.signature))

    def run():
        changed = True
        while changed:
            changed = False
            for n in gen:
                fl = flows[n]

                def add(p, pol):
                    nonlocal changed
                    s = fl[idx[n][p]]
                    new = BOTH if pol == 0 else {"out"} if pol > 0 else {"in"}
                    if not new <= s:
                        s |= new
                        changed = True
                for p in nested[n]:
                    add(p, 0)
                for tree, pol, scope in occ[n]:
                    if tree is None:
                        for p in scope:
                            add(p, 0)
                    else:
                        walk(tree, pol, scope, add)
    run()
    while True:
        dead = [(n, k) for n in gen for k, s in enumerate(flows[n]) if not s]
        if not dead:
            break
        for n, k in dead:
            flows[n][k] |= BOTH
        run()
    return {n: ["+" if s == {"out"} else "-" if s == {"in"} else "." for s in fl] for n, fl in flows.items()}




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
    dropped_out = None
    if "--dropped" in args:
        k = args.index("--dropped")
        dropped_out = args[k + 1]
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
    VAR = infer_variance(W, JDK_VARIANCE)
    # W's names of the classes V has without type parameters: a class 1.20.3 made generic
    # (CommandFunction -> CommandFunction<T>) answered V code nothing typed by them, so W's
    # arguments on it are no drift; V code reaches its values through its V members, diffed apart
    NG = {tcls(n) for n, c in V.items() if not (c.signature and c.signature.startswith("<"))}

    # Rule C: a class type parameter W removed (CubicSpline<C, I> -> CubicSpline<I>): V's binary name ->
    # the indices of its formal parameters W no longer declares, where W's parameters are V's minus
    # those (in order, by name). Taken only where the removed parameter types no value W still links:
    #   - no kept parameter is bounded by it bare (its erasure would depend on it);
    #   - every V member of the class that uses it outside the removed argument positions is gone from
    #     W (that member's damage is in the probe's table, not hidden here: 26.1.2's apply(C));
    #   - no V supertype whose arguments use it is still a W supertype (26.1.2's CubicSpline is a
    #     BoundedFloatFunction<C>; 26.2's is none).
    def formal_names(c):
        return formal_params(c.signature) if c is not None and c.signature else []

    def super_sigs(c):
        """the supertype signatures after a class signature's formal section"""
        s = c.signature or ""
        i = _skip_args(s, 0) if s.startswith("<") else 0
        out = []
        while i < len(s):
            e = type_end(s, i)
            out.append(s[i:e])
            i = e
        return out

    DROP = {}
    for n, vc in V.items():
        if tcls(n) not in W:
            continue
        pv, pw = formal_names(vc), formal_names(W.get(tcls(n)))
        if not pv or len(pw) >= len(pv):
            continue
        keep, k = [], 0
        for i, p in enumerate(pv):
            if k < len(pw) and p == pw[k]:
                keep.append(i)
                k += 1
        if k == len(pw):
            DROP[n] = tuple(i for i in range(len(pv)) if i not in keep)

    def drop_ok(n):
        vc, wc = V[n], W[tcls(n)]
        removed = {formal_params(vc.signature)[i] for i in DROP[n]}
        for p, bounds in formal_list(vc.signature):
            if p not in removed and any(sig_tvars(b) & removed and b.startswith("T") for b in bounds):
                return False
        wm = {(m.name, m.desc) for m in wc.methods} | {(f[0], f[1]) for f in wc.fields}
        for m in vc.methods:
            if m.signature and not m.access & ACC_STATIC and (tmember(mmap, n, m.name), tdesc(m.desc)) in wm:
                parts = classfile.split_signature(m.signature) or ([], "V")
                if any(sig_tvars(drop_type_args(t, DROP)[0]) & removed for t in list(parts[0]) + [parts[1]]):
                    return False
        for nm, ds, acc, sig in vc.fields:
            if sig and not acc & ACC_STATIC and (tmember(fmap, n, nm), tdesc(ds)) in wm \
                    and sig_tvars(drop_type_args(sig, DROP)[0]) & removed:
                return False
        wsup = {re.sub(r"<.*", "", t)[1:].rstrip(";") for t in super_sigs(wc)} if wc.signature else \
            set(([wc.super] if wc.super else []) + list(wc.interfaces))
        for t in super_sigs(vc):
            if sig_tvars(drop_type_args(t, DROP)[0]) & removed and tcls(re.sub(r"<.*", "", t)[1:].rstrip(";")) in wsup:
                return False
        return True
    DROP = {n: ix for n, ix in DROP.items() if drop_ok(n)}

    def dropped_param(x, y, cls, bx, by, vsig):
        """Rule C: with the removed type parameters' arguments taken out of V's x, x and W's y agree, and
        what those arguments named types nothing else of the member"""
        x2, gone = drop_type_args(x, DROP)
        if x2 == x:
            return False
        parts = classfile.split_signature(vsig) if vsig else ([x], "V")
        rest = set()
        for t in list(parts[0]) + [parts[1]]:
            rest |= sig_tvars(drop_type_args(t, DROP)[0])
        return not gone & rest and not drifts(x2, y, cls, bx, by)

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

    def class_tokens(classes, name):
        """the class type parameters in scope in a class (as class_bounds), each as a token naming its
        place -- nesting depth from the outermost class and index -- instead of its erasure"""
        chain = []
        cur = name
        while True:
            chain.append(cur)
            d = cur.rfind("$")
            if d < 0:
                break
            cur = cur[:d]
        t = {}
        for depth, c in enumerate(reversed(chain)):
            k = classes.get(c)
            if k is not None and k.signature:
                for i, p in enumerate(formal_params(k.signature)):
                    t[p] = f"cb!tv/{depth}/{i}"
        return t

    def bound_only(x, y, cls, bx, by, vsig, wsig, vct, wct):
        """Rule A: the drift of V's x to W's y lies only in the declared bound of a class type
        parameter (LootDataType<T> : Object -> Validatable, both answering ResourceKey<Registry<T>>):
        with every class type parameter compared by its place instead of its erasure (a member's own
        type parameters still by erasure: bx / by) the two agree, so every value has the type it had
        -- the same T of the same instance. Where the mod picks T (constructing or extending the
        class, or implementing an interface) a narrowed bound is real damage; the caller keeps those
        rows. vsig / wsig: the member's generic signatures (None for a field), vct / wct:
        class_tokens of each side."""
        mv, mw = set(formal_params(vsig)), set(formal_params(wsig))
        tv = dict(bx, **{p: t for p, t in vct.items() if p not in mv})
        tw = dict(by, **{p: t for p, t in wct.items() if p not in mw})
        a = args_of(canon(map_type(x, cls, tv)))
        b = args_of(canon(map_type(strip_phantom(y, NG), lambda c: c, tw)))
        return a == b

    def safe(x, y, cls, inward, bx, by, infer=True):
        """the drift of V's x to W's y is only a bound moving the way values can still flow. Inward (the
        mod hands the value to vanilla) a covariant argument -- ? extends, or a position the type only
        produces from (Optional, Stream, Supplier, Iterable; a W class whose parameter infer_variance
        finds only handed out) -- may widen and a contravariant one (? super, Consumer, Predicate,
        Comparator; a W class whose parameter is only taken) narrow; outward the other way round. The
        same judgment applies inside nested arguments (Optional<CollectionPredicate<T, ..>>). infer=
        False: only the JDK table and only the outermost arguments (what the table judged before
        infer_variance; kept to tell which rule drops a row)"""
        a = wtree(map_type(x, cls, bx))[0]
        b = wtree(map_type(strip_phantom(y, NG), lambda c: c, by))[0]
        if a[0] != "L" or b[0] != "L" or a[1] != b[1]:
            return False
        return sub(a, b, infer, True) if inward else sub(b, a, infer, True)

    def flat(n):
        """a wtree node back to its canonical signature (wildcards and X<?> as canon has them)"""
        if n is None:
            return "*"
        if n[0] == "T":
            return "T" + n[1] + ";"
        if n[0] == "[":
            return "[" + flat(n[1])
        if n[0] == "P":
            return n[1]
        args = [a for a in n[2]]
        s = "L" + n[1] + "".join(flat(o) for o in n[3])
        if args and not all(w == "*" for w, _ in args):
            s += "<" + "".join(flat(sn) for _, sn in args) + ">"
        return s + ";"

    def sub(a, b, infer, top=False):
        """the value typed a may stand where b is wanted: the same class with each argument judged by
        its variance, or (no arguments) a W subtype"""
        if flat(a) == flat(b):
            return True
        if a[0] != "L" or b[0] != "L":
            return False
        if not a[2] and not b[2] and not a[3] and not b[3]:
            return wsub(a[1], b[1])
        same_args = [flat(o) for o in a[3]] == [flat(o) for o in b[3]] and \
            [flat(n) for _, n in a[2]] == [flat(n) for _, n in b[2]]
        if a[1] != b[1]:
            # another class with the same arguments (List<X> where Collection<X> is wanted)
            return same_args and wsub(a[1], b[1])
        if len(a[2]) != len(b[2]) or [flat(o) for o in a[3]] != [flat(o) for o in b[3]]:
            return False
        if not top and not infer:
            return False
        table = JDK_VARIANCE.get(a[1], "") or (VAR.get(a[1], "") if infer else "")
        for k, ((mv, na), (mw, nb)) in enumerate(zip(a[2], b[2])):
            if flat(na) == flat(nb):
                continue
            if "*" in (mv, mw) or na is None or nb is None:
                return False
            tv = table[k] if k < len(table) and table[k] in "+-" else ""
            if mv and mw:
                m = mv if mv == mw else ""
            elif mv or mw:
                m = (mv or mw) if (mv or mw) == tv else ""
            else:
                m = tv
            if not m:
                return False
            if not (sub(na, nb, infer) if m == "+" else sub(nb, na, infer)):
                return False
        return True

    def gone(x):
        return {c for c in re.findall(r"L([^;<>.]+)", args_of(x)) if c in V and tcls(c) not in W}

    rows, dropped = [], []

    def add(usage, owner, name, desc, hit, vsig, wsig, bx, by):
        """one row for a usage whose drifted positions are hit = [(where, V sig, W sig, inward)]"""
        rows.append({
            "usage": usage, "owner": owner, "name": name, "desc": desc,
            "changed": ",".join(w for w, _, _, _ in hit), "vsig": vsig, "wsig": wsig,
            "lacks": " ".join(sorted(set().union(*(gone(x) for _, x, _, _ in hit)))),
            "note": "phantom-key" if all(not drifts(x, y, tcls, bx, by, phantom=True) for _, x, y, _ in hit) else "",
            "renamed": all(not drifts(x, y, rcls, bx, by) or safe(x, y, rcls, inw, bx, by) for _, x, y, inw in hit),
        })

    def judge(u, hit, bx, by, mvars, a_ok, vsig, wsig, vct, wct):
        """the drifted positions of one usage the earlier rules keep, then those rules C, A and B drop
        (each position with the rule that took it)"""
        hit = [h for h in hit if not safe(h[1], h[2], tcls, h[3], bx, by, infer=False)
               and not (u != "override" and caller_typed(h[1], h[2], tcls, bx, by, mvars))]
        keep, took = [], []
        for h in hit:
            if dropped_param(h[1], h[2], tcls, bx, by, vsig):
                took.append((h[0], "C"))
            elif a_ok and u not in ("new", "super") and bound_only(h[1], h[2], tcls, bx, by, vsig, wsig, vct, wct):
                took.append((h[0], "A"))
            elif safe(h[1], h[2], tcls, h[3], bx, by):
                took.append((h[0], "B"))
            else:
                keep.append(h)
        return hit, keep, took

    def emit(u, owner, name, desc, hit, keep, took, vsig, wsig, bx, by):
        if keep:
            add(u, owner, name, desc, keep, vsig, wsig, bx, by)
        if took:
            dropped.append({"usage": u, "owner": owner, "name": name, "desc": desc,
                            "rule": ",".join(f"{w}:{r}" for w, r in took),
                            "whole": "row" if not keep else "positions", "vsig": vsig, "wsig": wsig})

    for name, vc in V.items():
        wc = W.get(tcls(name))
        if not wc or not vc.access & ACC_PUBLIC:
            continue
        final_cls = vc.access & ACC_FINAL
        vcb, wcb = class_bounds(V, name), class_bounds(W, tcls(name))
        vct, wct = class_tokens(V, name), class_tokens(W, tcls(name))
        wm = {(m.name, m.desc): m for m in wc.methods}
        # Rule A holds only where every way a mod picks the class's type arguments is itself reported:
        # not an interface (implementing it picks them, and no member says so), and each constructor a
        # mod reaches is either gone from W (the damage table) or drifted here (its rows stay)
        a_ok = not wc.access & ACC_INTERFACE
        for m in vc.methods:
            if a_ok and m.name == "<init>" and m.access & (ACC_PUBLIC | ACC_PROTECTED) and not m.access & ACC_SYNTHETIC:
                o = wm.get((m.name, tdesc(m.desc)))
                if o is None:
                    continue
                a, b = (classfile.split_signature(m.signature), classfile.split_signature(o.signature)) \
                    if m.signature and o.signature else (None, None)
                if not a or not b or not any(drifts(x, y, tcls, formal_bounds(m.signature, vcb), formal_bounds(o.signature, wcb))
                                             for x, y in zip(a[0], b[0])):
                    a_ok = False
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
            static = m.access & ACC_STATIC
            bx = formal_bounds(m.signature, {} if static else vcb)
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
                hit, keep, took = judge(u, hit, bx, by, mvars, a_ok, m.signature, o.signature,
                                        {} if static else vct, {} if static else wct)
                emit(u, name, m.name, m.desc, hit, keep, took, m.signature, o.signature, bx, by)
        wf = {(f[0], f[1]): f for f in wc.fields}
        for nm, ds, acc, sig in vc.fields:
            if not sig or acc & ACC_PRIVATE or not acc & (ACC_PUBLIC | ACC_PROTECTED):
                continue
            o = wf.get((tmember(fmap, name, nm), tdesc(ds)))
            static = acc & ACC_STATIC
            bx, by = ({}, {}) if static else (vcb, wcb)
            if not o or not o[3] or not drifts(sig, o[3], tcls, bx, by):
                continue
            for u in ["get"] + ([] if acc & ACC_FINAL or vc.access & ACC_INTERFACE else ["put"]):
                hit, keep, took = judge(u, [("type", sig, o[3], u == "put")], bx, by, set(), a_ok, None, None,
                                        {} if static else vct, {} if static else wct)
                emit(u, name, nm, ds, hit, keep, took, sig, o[3], bx, by)
    rows.sort(key=lambda r: (r["owner"], r["name"], r["desc"], r["usage"]))
    dropped.sort(key=lambda r: (r["owner"], r["name"], r["desc"], r["usage"]))

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
    if dropped_out:
        dcols = ["usage", "owner", "name", "desc", "rule", "whole", "vsig", "wsig"]
        with open(dropped_out, "w", encoding="utf-8", newline="\n") as f:
            f.write("\t".join(["usage", "owner", "name", "desc", "rule (position:rule)", "dropped",
                               "V signature", "W signature"]) + "\n")
            for r in dropped:
                f.write("\t".join(r[c] for c in dcols) + "\n")
    live = sum(1 for r in rows if not r["lacks"])
    msg = f"{len(rows)} member usages with generic drift ({live} between classes both releases have, " \
          f"{sum(1 for r in rows if r['note'])} phantom-key)"
    if wiring is not None:
        msg += f"; open {sum(1 for r in rows if not r['closed'])}"
    print(msg + f" -> {out}")


if __name__ == "__main__":
    main()
