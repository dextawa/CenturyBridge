#!/usr/bin/env python3
"""Instance methods a climb step moved onto an object the old owner holds. 26.2 split the HUD out of Gui into Hud
(Gui.setTitle is Hud.setTitle, reached as gui.hud) and moved Minecraft's screen handling into the new Gui
(Minecraft.setScreen is minecraft.gui.setScreen); a V-era mod's call minecraft.gui.setTitle(..) names a method W's
Gui no longer has.

For every instance method of the step's damage table (usages call / call-inherited / call-protected) whose
declaring V class H no longer has it on W (under its renamed name either), this looks for the object it moved to:
a public instance field f of W's H (its classRenames target), or a public no-argument instance method of it, of a
W type D that declares an instance method of the same name and translated descriptor whose body is V's body
instruction for instruction -- class names translated through the step's classRenames and member renames, V's
H read as D (the moved code reads its moved fields on D). Exactly one such path must exist. The method is then
answered by a generated static in the step layer, rt/CbDelegates (rt/CbDelegatesClient for client classes,
@Environment(CLIENT)): Gui_setTitle(Gui self, Component a0) { self.hud.setTitle(a0); }, wired as an
instanceRedirects entry in <V>__<W>.json.d/delegates-gen.json. A key another fragment wires is left to it;
candidates without such evidence are listed on stdout.

    python3 core/tools/gen_delegate_moves.py <V code jar> <W jar> <V> <W>
"""
import collections
import glob
import json
import os
import re
import sys
import zlib

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import bytecode  # noqa: E402
import wall_table  # noqa: E402

DESC_CLS = re.compile(r"L([^;<]+)([;<])")
PRIM = {"Z": "boolean", "B": "byte", "C": "char", "S": "short", "I": "int", "J": "long", "F": "float",
        "D": "double", "V": "void"}
CLIENT = ("net/minecraft/client/", "com/mojang/blaze3d/", "com/mojang/realmsclient/")


def parse_args(desc):
    i, out = 1, []
    while desc[i] != ")":
        j = i
        while desc[j] == "[":
            j += 1
        if desc[j] == "L":
            j = desc.index(";", j)
        out.append(desc[i:j + 1])
        i = j + 1
    return out, desc[i + 1:]


def jtype(d):
    dims = 0
    while d.startswith("["):
        dims += 1
        d = d[1:]
    base = PRIM[d] if d in PRIM else d[1:-1].replace("/", ".").replace("$", ".")
    return base + "[]" * dims


def main():
    vjar, wjar, v, w = sys.argv[1:5]
    step = f"{v}__{w}"
    d = f"core/segments/steps/{step}.json.d"
    out_json = f"{d}/delegates-gen.json"
    wu = w.replace(".", "_")
    pkg = f"top.dext.centurybridge.step.v{wu}.rt"
    V, W = wall_table.load(vjar), wall_table.load(wjar)
    cr, mren, wired = {}, {}, set()
    for f in sorted(glob.glob(f"{d}/*.json")):
        if f.replace("\\", "/") == out_json:
            continue
        j = json.load(open(f, encoding="utf-8"))
        cr.update(j.get("classRenames", {}))
        mren.update(j.get("_pkgmoves_member_renames", {}))
        for sec in ("instanceRedirects", "interfaceCallRedirects", "callReshapes", "methodRenames", "ledger",
                    "covers", "staticRedirects"):
            wired.update(j.get(sec, {}).keys())

    def tc(c):
        if c in cr:
            return cr[c]
        k = c.rfind("$")
        if k > 0 and c[:k] in cr and c not in V:
            return cr[c[:k]] + c[k:]
        return c

    def td(desc):
        return DESC_CLS.sub(lambda m: "L" + tc(m.group(1)) + m.group(2), desc)

    def supers(c, jar):
        out, todo = [], [c]
        while todo:
            x = todo.pop()
            if x is None or x in out:
                continue
            out.append(x)
            k = jar.get(x)
            if k:
                todo.append(k.super)
                todo.extend(k.interfaces)
        return out

    def find(c, name, desc, jar, static=False):
        for s in supers(c, jar):
            k = jar.get(s)
            if not k:
                continue
            for m in k.methods:
                if m.name == name and m.desc == desc and bool(m.access & bytecode.ACC_STATIC) == static:
                    return s, m
        return None, None

    def norm(x, H, D):
        if isinstance(x, tuple) and x:
            t = x[0]
            if t == "class":
                return ("class", D if x[1] == H else (td(x[1]) if x[1].startswith("[") else tc(x[1])))
            if t in ("ref", "mhandle"):
                o, n, ds = x[2], x[3], x[4]
                n = mren.get(o + "." + n, n)
                return (t, x[1], D if o == H else tc(o), n, td(ds))
            if t == "mtype":
                return ("mtype", td(x[1]))
            if t == "indy":
                return ("indy", x[1], td(x[2]), norm(x[3], H, D), tuple(norm(a, H, D) for a in x[4]))
            return tuple(norm(a, H, D) for a in x)
        return x

    def same(vm, wm, H, D):
        if vm.insns is None or wm.insns is None or len(vm.insns) != len(wm.insns):
            return False
        return all(op == wop and norm(a, H, D) == wa for (op, a), (wop, wa) in zip(vm.insns, wm.insns))

    rows = set()
    for line in open(f"core/segments/damage/{step}.damage.tsv", encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 4 and f[0] in ("call", "call-inherited", "call-protected") and f[3].startswith("("):
            rows.add((f[1], f[2], f[3]))
    out, basis, left = {}, {}, []
    for owner, name, desc in sorted(rows):
        H, vm = find(owner, name, desc, V)
        if H is None or name == "<init>":
            continue
        key = f"{H}.{name}{desc}"
        if key in wired or key in out:
            continue
        wh, wdesc = tc(H), td(desc)
        if wh not in W or find(wh, mren.get(H + "." + name, name), wdesc, W)[0] is not None:
            continue
        paths = []
        for s in supers(wh, W):
            k = W.get(s)
            if not k:
                continue
            for fl in k.fields:
                if fl.access & bytecode.ACC_PUBLIC and not fl.access & bytecode.ACC_STATIC and fl.desc.startswith("L"):
                    paths.append(("." + fl.name, fl.desc[1:-1]))
            for m in k.methods:
                if m.access & bytecode.ACC_PUBLIC and not m.access & bytecode.ACC_STATIC and m.desc.startswith("()L"):
                    paths.append(("." + m.name + "()", m.desc[3:-1]))
        hits, near = [], []
        for path, D in paths:
            if D not in W:
                continue
            dh, wm = find(D, name, wdesc, W)
            if wm is None or not wm.access & bytecode.ACC_PUBLIC:
                continue
            if same(vm, wm, H, dh):
                hits.append((path, D))
            else:
                near.append(path + " (" + D.rsplit("/", 1)[1] + ": body differs)")
        if len(set(hits)) == 1:
            path, D = hits[0]
            out[key] = (path, D, name, desc, wdesc, H)
            basis[key] = f"{H.rsplit('/', 1)[1]}.{name} is {D.rsplit('/', 1)[1]}.{name} (same code), reached as self{path}"
        elif hits or near:
            left.append((key, ("ambiguous: " + ", ".join(p for p, _ in hits)) if hits else "; ".join(near)))
    classes = collections.defaultdict(list)
    for key, (path, D, name, desc, wdesc, H) in sorted(out.items()):
        refs = [H, D] + [m.group(1) for m in DESC_CLS.finditer(wdesc)]
        client = any(r.startswith(CLIENT) for r in refs)
        classes["CbDelegatesClient" if client else "CbDelegates"].append(key)
    redirects = {}
    for cls, keys in classes.items():
        lines = [f"package {pkg};", ""]
        if cls.endswith("Client"):
            lines += ["import net.fabricmc.api.EnvType;", "import net.fabricmc.api.Environment;", ""]
        lines += ["/**", " * GENERATED by core/tools/gen_delegate_moves.py -- regenerate, never hand-edit.",
                  f" * {v} instance methods {w} moved onto an object the old owner holds (same code, one path).", " */"]
        if cls.endswith("Client"):
            lines.append("@Environment(EnvType.CLIENT)")
        lines += [f"public final class {cls} {{", f"    private {cls}() {{}}"]
        for key in keys:
            path, D, name, desc, wdesc, H = out[key]
            args, ret = parse_args(wdesc)
            mname = f"{H.rsplit('/', 1)[1].replace('$', '_')}_{name}"
            n = sum(1 for k in keys if k != key and out[k][2] == name and out[k][5] == H)
            if n:
                mname += "_" + format(zlib.crc32(desc.encode()), "08x")
            params = ", ".join([f"{jtype('L' + tc(H) + ';')} self"] + [f"{jtype(a)} a{i}" for i, a in enumerate(args)])
            call = f"self{path}.{name}(" + ", ".join(f"a{i}" for i in range(len(args))) + ")"
            body = (call if ret == "V" else "return " + call) + ";"
            lines += ["", f"    /** {basis[key]} */", f"    public static {jtype(ret)} {mname}({params}) {{",
                      f"        {body}", "    }"]
            redirects[key] = f"top/dext/centurybridge/step/v{wu}/rt/{cls}#{mname}"
        lines += ["}", ""]
        path_java = f"core/shims/steps/{w}/src/{pkg.replace('.', '/')}/{cls}.java"
        with open(path_java, "w", encoding="utf-8", newline="\n") as o:
            o.write("\n".join(lines))
    j = {
        "_delegates_generated": f"core/tools/gen_delegate_moves.py ... {v} {w}: instance methods moved onto an object "
                                "the old owner holds; regenerate, never hand-edit",
        "instanceRedirects": dict(sorted(redirects.items())),
        "_delegates_basis": dict(sorted(basis.items())),
    }
    with open(out_json, "w", encoding="utf-8", newline="\n") as o:
        json.dump(j, o, indent=1, ensure_ascii=False)
        o.write("\n")
    per = collections.Counter(out[k][5] + " -> " + out[k][1] for k in out)
    for k, n in per.most_common():
        print(f"  {n:4d} {k}")
    print(f"-> {out_json}: {len(redirects)} instanceRedirects; {len(left)} candidates without evidence:")
    for k, why in left:
        print(f"  open {k}: {why}")


if __name__ == "__main__":
    main()
