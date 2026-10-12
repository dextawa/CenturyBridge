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
element read or written as X is still an X), and X<?> is raw X. The last column names the game
classes of V's type arguments that W no longer has: the row then depends on how that class's death
is bridged (renamed to a W class, the drift may vanish; a facade object is not the W object vanilla
puts in the collection).

  python generic_drift.py <V jar> <W jar> <out.tsv> [--wall <inter-to-mojang.tsv>]
  python generic_drift.py <V jar> <W jar> <out.tsv> [--wall ...] --wiring <step wiring.json>

(--wall: V is the last intermediary release and W the first mojang-named one; V's class and member
names are translated through the table before W is looked up.) With --wiring the rows are the same
and a "closed" column says what closes each one: "rename" (the step's classRenames to W classes make
the signatures agree), the wiring map that names the member for that usage, or "ledger"; the open
count is printed.
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
    "new": ("ctorRedirects",),
    "super": ("superCtorReshapes", "superCtorAdapts"),
    "get": ("fieldRedirects", "fieldGetters", "instanceFieldGetters"),
    "put": ("fieldRedirects", "instanceFieldSetters"),
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


def map_type(sig, cls):
    """a type signature with every class name passed through cls (inner classes of a parameterized
    outer, LOuter<..>.Inner<..>;, by their binary name Outer$Inner) and type variables as T*"""
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
            out.append("T*;")
            i = sig.index(";", i) + 1
        else:
            out.append(c)
            i += 1
    return "".join(out)


def canon(sig):
    """what erasure-level code cannot tell apart taken out: wildcard variance, X<?> -> X"""
    return re.sub(r"<\*+>", "", re.sub(r"[+-](?=[LT\[])", "", sig))


def args_of(sig):
    k = sig.find("<")
    return sig[k:] if k >= 0 else ""


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

    def drifts(x, y, cls):
        a, b = args_of(canon(map_type(x, cls))), args_of(canon(map_type(y, lambda c: c)))
        return bool(a and b and a != b)

    def gone(x):
        return {c for c in re.findall(r"L([^;<>.]+)", args_of(x)) if c in V and tcls(c) not in W}

    rows = []
    for name, vc in V.items():
        wc = W.get(tcls(name))
        if not wc or not vc.access & ACC_PUBLIC:
            continue
        final_cls = vc.access & ACC_FINAL
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
            pairs = [(f"param {i}", x, y) for i, (x, y) in enumerate(zip(a[0], b[0]))] + [("return", a[1], b[1])]
            hit = [(w, x, y) for w, x, y in pairs if drifts(x, y, tcls)]
            if not hit:
                continue
            renamed_ok = all(not drifts(x, y, rcls) for _, x, y in hit)
            lacks = set().union(*(gone(x) for _, x, _ in hit))
            if m.name == "<init>":
                usages = ["new"] + ([] if final_cls else ["super"])
            else:
                usages = ["call"]
                if not (m.access & (ACC_STATIC | ACC_FINAL) or final_cls):
                    usages.append("override")
            for u in usages:
                rows.append([u, name, m.name, m.desc, ",".join(w for w, _, _ in hit), m.signature, o.signature,
                             " ".join(sorted(lacks)), renamed_ok])
        wf = {(f[0], f[1]): f for f in wc.fields}
        for nm, ds, acc, sig in vc.fields:
            if not sig or acc & ACC_PRIVATE or not acc & (ACC_PUBLIC | ACC_PROTECTED):
                continue
            o = wf.get((tmember(fmap, name, nm), tdesc(ds)))
            if not o or not o[3] or not drifts(sig, o[3], tcls):
                continue
            renamed_ok = not drifts(sig, o[3], rcls)
            for u in ["get"] + ([] if acc & ACC_FINAL or vc.access & ACC_INTERFACE else ["put"]):
                rows.append([u, name, nm, ds, "type", sig, o[3], " ".join(sorted(gone(sig))), renamed_ok])
    rows.sort(key=lambda r: (r[1], r[2], r[3], r[0]))

    header = ["usage", "owner", "name", "desc", "changed", "V signature", "W signature", "V classes W lacks"]
    if wiring is not None:
        ledger = wiring.get("ledger", {})

        def supers(c):
            """c and every V supertype of it"""
            out, todo = set(), [c]
            while todo:
                x = todo.pop()
                if x in out:
                    continue
                out.add(x)
                k = V.get(x)
                if k:
                    todo += ([k.super] if k.super else []) + list(k.interfaces)
            return out

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
            u, owner, nm, ds = r[0], r[1], r[2], r[3]
            if r[8]:
                return "rename"
            key = owner + "." + nm + (":" if u in ("get", "put") else "") + ds
            if u == "override":
                c = override_closed(owner, nm + ds)
                if c:
                    return c
            else:
                for mp in USAGE_MAPS[u]:
                    if key in wiring.get(mp, {}):
                        return mp
            for k in (key, owner):
                if k in ledger:
                    return "ledger"
            return ""
        header.append("closed")
        for r in rows:
            r.append(closed(r))
    with open(out, "w", encoding="utf-8", newline="\n") as f:
        f.write("\t".join(header) + "\n")
        for r in rows:
            f.write("\t".join(r[:8] + r[9:]) + "\n")
    live = sum(1 for r in rows if not r[7])
    msg = f"{len(rows)} member usages with generic drift ({live} between classes both releases have)"
    if wiring is not None:
        msg += f"; open {sum(1 for r in rows if not r[9])}"
    print(msg + f" -> {out}")


if __name__ == "__main__":
    main()
