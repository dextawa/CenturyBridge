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

A method can also have moved onto a sibling: an object held, like H's own instance, by a singleton -- a W class G
with a public static no-argument method answering G (Minecraft.getInstance()) and a public instance field of H's
type. 26.2 split LevelRenderer's level state into LevelExtractor (Minecraft.levelRenderer / Minecraft.levelExtractor):
LevelRenderer.setSectionDirty is minecraft.levelExtractor.setSectionDirty. Such a path answers only for the instance
the singleton holds (the generated static checks self is G's and throws IllegalStateException otherwise: the moved
state lives on the singleton's sibling, not on another instance). On the sibling path the method may also have been
renamed: the one public method of D with the translated descriptor whose body is the same code answers -- or, when
the code moved with its state and reads differently, the one W's callers call from exactly the caller methods V's
called H's from (LevelRenderer.allChanged and LevelExtractor.allChanged: KeyboardHandler.handleDebugKeys,
Minecraft's and VideoSettingsScreen's lambdas, setLevel, and the method of the class itself that drives the frame),
under a name that still says the same (one a prefix of the other once get/is is dropped: extractLevel / extract).

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

    # singletons: G with a public static ()LG; method; (G, getter, field holding an instance) -> its sibling paths
    singletons = {}
    gfields = {}
    for G, k in W.items():
        get = next((m.name for m in k.methods if m.access & bytecode.ACC_PUBLIC and m.access & bytecode.ACC_STATIC
                    and m.desc == "()L" + G + ";"), None)
        if get is None:
            continue
        fs = [(fl.name, fl.desc[1:-1]) for fl in k.fields if fl.access & bytecode.ACC_PUBLIC
              and not fl.access & bytecode.ACC_STATIC and fl.desc.startswith("L")]
        gfields[G] = dict(fs)
        for held, ht in fs:
            singletons[(G, get, held)] = [("." + f, t) for f, t in fs if f != held and t != ht]

    def held_type(G, held):
        return gfields[G][held]

    def stem(n):
        for pre in ("get", "is"):
            if n.startswith(pre) and len(n) > len(pre) and n[len(pre)].isupper():
                n = n[len(pre):]
        return n.lower()

    def same_stem(a, b):
        """caller evidence names the method by where it is called; the name must still say the same thing
        (getSectionStatistics / sectionStatistics, extractLevel / extract), not merely run at the same moment"""
        x, y = stem(a), stem(b)
        return x == y or x.startswith(y) or y.startswith(x)

    call_index = {}

    def callers(jar, owner, name, desc, H, D):
        """the methods calling owner.name(desc) in jar, as 'Class.method' (lambdas as their enclosing method; a caller
        in V's H named as W's D, the class its code moved to; other classes through classRenames)"""
        key = id(jar)
        if key not in call_index:
            idx = collections.defaultdict(set)
            for c, k in jar.items():
                for m in k.methods:
                    base = m.name.split("$")[1] if m.name.startswith("lambda$") else m.name
                    for op, a in m.insns or []:
                        if isinstance(a, tuple) and a[0] == "ref" and a[1] == "M":
                            idx[(a[2], a[3], a[4])].add((c, base))
            call_index[key] = idx
        out = set()
        for c, base in call_index[key].get((owner, name, desc), ()):
            c = D if H is not None and c == H else (tc(c) if jar is V else c)
            out.add(c + "." + base)
        return out

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
                hits.append((path, D, name, None))
            else:
                near.append(path + " (" + D.rsplit("/", 1)[1] + ": body differs)")
        if not hits:
            for (G, get, held), sibs in singletons.items():
                if held_type(G, held) != wh:
                    continue
                for sib, D in sibs:
                    cands = []
                    for s2 in supers(D, W):
                        k2 = W.get(s2)
                        for m2 in (k2.methods if k2 else []):
                            if m2.desc == wdesc and m2.access & bytecode.ACC_PUBLIC and not m2.access & bytecode.ACC_STATIC                                     and same(vm, m2, H, s2):
                                cands.append(m2.name)
                    if len(set(cands)) == 1:
                        hits.append((sib, D, cands[0], (G, get, held)))
                        continue
                    # the code moved with its state and so reads differently; the method that W's callers call
                    # where V's called H's -- the same caller methods, none else -- is the one it became
                    vcall = callers(V, H, name, desc, H, D)
                    if not vcall:
                        continue
                    cands = []
                    for s2 in supers(D, W):
                        k2 = W.get(s2)
                        for m2 in (k2.methods if k2 else []):
                            if m2.desc == wdesc and m2.access & bytecode.ACC_PUBLIC and not m2.access & bytecode.ACC_STATIC                                     and same_stem(name, m2.name) and callers(W, s2, m2.name, m2.desc, None, None) == vcall:
                                cands.append(m2.name)
                    if len(set(cands)) == 1:
                        hits.append((sib, D, cands[0], (G, get, held), "callers"))
        if len(set(h[:4] for h in hits)) == 1:
            path, D, wname, single = hits[0][:4]
            how = "called where V called it: " + ", ".join(sorted(callers(V, H, name, desc, H, D))) if len(hits[0]) > 4 else "same code"
            out[key] = (path, D, name, desc, wdesc, H, wname, single)
            if single:
                G, get, held = single
                basis[key] = (f"{H.rsplit('/', 1)[1]}.{name} is {D.rsplit('/', 1)[1]}.{wname} ({how}), the sibling "
                              f"{G.rsplit('/', 1)[1]}.{get}(){path} of {G.rsplit('/', 1)[1]}.{get}().{held}")
            else:
                basis[key] = f"{H.rsplit('/', 1)[1]}.{name} is {D.rsplit('/', 1)[1]}.{name} (same code), reached as self{path}"
        elif hits or near:
            left.append((key, ("ambiguous: " + ", ".join(h[0] + " " + h[2] for h in hits)) if hits else "; ".join(near)))
    classes = collections.defaultdict(list)
    for key, (path, D, name, desc, wdesc, H, wname, single) in sorted(out.items()):
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
            path, D, name, desc, wdesc, H, wname, single = out[key]
            args, ret = parse_args(wdesc)
            mname = f"{H.rsplit('/', 1)[1].replace('$', '_')}_{name}"
            n = sum(1 for k in keys if k != key and out[k][2] == name and out[k][5] == H)
            if n:
                mname += "_" + format(zlib.crc32(desc.encode()), "08x")
            params = ", ".join([f"{jtype('L' + tc(H) + ';')} self"] + [f"{jtype(a)} a{i}" for i, a in enumerate(args)])
            argl = ", ".join(f"a{i}" for i in range(len(args)))
            if single:
                G, get, held = single
                gj = jtype("L" + G + ";")
                call = f"g{path}.{wname}({argl})"
                pre = [f"        {gj} g = {gj}.{get}();",
                       f"        if (self != g.{held}) {{",
                       f"            throw new IllegalStateException(\"{w} keeps this state on {G.rsplit('/', 1)[1]}.{get}(){path}, "
                       f"which serves only {G.rsplit('/', 1)[1]}.{get}().{held}\");",
                       "        }"]
            else:
                call = f"self{path}.{name}({argl})"
                pre = []
            body = (call if ret == "V" else "return " + call) + ";"
            lines += ["", f"    /** {basis[key]} */", f"    public static {jtype(ret)} {mname}({params}) {{"] + pre + [
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
