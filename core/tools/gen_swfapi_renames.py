"""Fabric API renames of the 1.21.11 -> 26.1 step (the namespace wall), checked member by member.

26.1's Fabric API took Mojang's names: classes moved (ItemGroupEvents -> creativetab/v1/CreativeModeTabEvents)
and members were renamed (ServerTickEvents.START_WORLD_TICK -> START_LEVEL_TICK), with no aliases left behind.
A rename is a bridge only where it is exactly that: the W class must carry every public or protected member
of the V class -- under its own name or the renamed one -- with V's descriptor once V's names are translated
(Minecraft classes through the wall table, Fabric API classes through the class pairing itself), the same
static-ness, no narrower access, no new finality, no abstract method where V had a body, the same class kind
and every V supertype still a supertype. Then every reference converts in place:

  classRenames   V class -> W class (the converter's last pass renames every reference, the hierarchy included)
  methodRenames  "V owner.name(V desc)" -> new name; a lambda built for a renamed SAM follows (engine)
  fieldRenames   "V owner.name:V desc" -> new name
  overrideRenames  a mod's own implementation / override of a renamed instance method takes the new name
                   ("superCalls": "rename": its super.old(..) follows) -- vanilla and Fabric API call the new one

Which class becomes which, and which member name becomes which, comes from a hint table
(core/tools/swfapi-renames.tsv: Fabric's own rename list, PR #5060, plus the package moves it leaves out).
A hint is a claim; the bytecode decides. A kind flip, new finality or a lost supertype refuses the pair
(reported, nothing emitted); a member W does not carry with V's shape -- gone, or the name with another
descriptor -- and an abstract method W added are "drift": the pair is still emitted (every other reference
converts) and the member is listed for a hand bridge in the area's own fragment. Pairs are settled to a
fixpoint: a refused pair's class no longer translates, so members that name it drift in turn. Intermediary
member ids (a Fabric API class overriding vanilla's method_NNNN) are compared under the name the whole-jar
wall rename gives them.

  python gen_swfapi_renames.py <V fabric-api.jar> <W fabric-api.jar> <V game stub> <W game jar>
         <data dir> <hints.tsv> <step wiring.json> <out report.tsv>
(the wall table is the one core/segments/wall-tables.tsv names for target 26.1, read from <data dir>/mappings)
writes <step wiring>.d/swfapi-renames-gen.json -- regenerate, never hand-edit.
"""
import collections
import io
import json
import os
import re
import sys
import zipfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import classfile  # noqa: E402
import wall_tables  # noqa: E402
from classfile import ACC_ABSTRACT, ACC_FINAL, ACC_INTERFACE, ACC_PRIVATE, ACC_PROTECTED, ACC_PUBLIC, ACC_STATIC, ACC_SYNTHETIC  # noqa: E402

API = "net/fabricmc/fabric/api/"
ACC_BRIDGE = 0x40


def load_fapi(path):
    """every class of a Fabric API jar and its nested module jars"""
    out = {}

    def scan(z):
        for n in z.namelist():
            if n.endswith(".class") and not n.startswith("META-INF/"):
                c = classfile.read(z.read(n))
                if c:
                    out.setdefault(c.name, c)
            elif n.startswith("META-INF/jars/") and n.endswith(".jar"):
                scan(zipfile.ZipFile(io.BytesIO(z.read(n))))
    scan(zipfile.ZipFile(path))
    return out


def load_game(path):
    out = {}
    with zipfile.ZipFile(path) as z:
        for n in z.namelist():
            if n.endswith(".class"):
                c = classfile.read(z.read(n))
                if c:
                    out[c.name] = c
    return out


def public(c):
    return c.access & ACC_PUBLIC


def members(c):
    """public / protected, non-synthetic members: ('M'|'F', name, desc, access)"""
    out = []
    for m in c.methods:
        if m.access & (ACC_PUBLIC | ACC_PROTECTED) and not m.access & (ACC_SYNTHETIC | ACC_BRIDGE) and m.name != "<clinit>":
            out.append(("M", m.name, m.desc, m.access))
    for n, d, a, _ in c.fields:
        if a & (ACC_PUBLIC | ACC_PROTECTED) and not a & ACC_SYNTHETIC:
            out.append(("F", n, d, a))
    return out


def main():
    vjar, wjar, vgame, wgame, data_dir, hints_path, wiring_path, out_report = sys.argv[1:9]
    table = wall_tables.table_for("26.1", data_dir)  # the step's target picks the table (wall-tables.tsv)
    V, W = load_fapi(vjar), load_fapi(wjar)
    VG, WG = load_game(vgame), load_game(wgame)
    cmap, mmap, ambiguous = {}, {}, set()
    for line in open(table, encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 3 and f[0] == "C":
            cmap[f[1]] = f[2]
        elif len(f) >= 3:
            # the whole-jar wall rename gives an intermediary member id its mojang name by bare id
            # (JarRemapper.Table.members): a Fabric API class overriding a vanilla method_NNNN carries it
            k, v = f[1].rsplit(".", 1)[1], f[2].rsplit(".", 1)[1]
            if mmap.setdefault(k, v) != v:
                ambiguous.add(k)
    for k in ambiguous:
        del mmap[k]

    def mn(n):
        return mmap.get(n, n) if re.match(r"(method|field|comp)_\d+$", n) else n

    chint, nhint = {}, collections.defaultdict(dict)
    for line in open(hints_path, encoding="utf-8"):
        if line.startswith("#") or not line.strip():
            continue
        f = line.rstrip("\n").split("\t")
        if f[0] == "C":
            chint[f[1]] = f[2]
        elif f[0] == "N":
            nhint[f[1]][f[2]] = f[3]

    def mc(c):
        """a V Minecraft class's W name (nested classes follow their outer class)"""
        if c in cmap:
            return cmap[c]
        d = c.rfind("$")
        return mc(c[:d]) + c[d:] if d > 0 and c.startswith("net/minecraft/") else c

    # candidate pairs: every public V API class W lacks under its own name
    pairs = {}
    for c, k in V.items():
        # a class W keeps under its V name is not renamed -- unless the hints say W's class of that name is
        # another one (V's FabricRecipeManager is W's FabricRecipeAccess; W's FabricRecipeManager is V's
        # FabricServerRecipeManager): the renames apply all at once, so the names may swap
        if not c.startswith(API) or (c in W and c not in chint) or not public(k) or c.endswith("package-info"):
            continue
        w = chint.get(c)
        if w is None:
            d = c.rfind("$")
            outer = c[:d] if d > 0 else None
            while outer and outer not in chint and "$" in outer:
                outer = outer[:outer.rfind("$")]
            if outer and outer in chint:
                w = chint[outer] + c[len(outer):]
        if w is not None and w in W:
            pairs[c] = w
    refused = {}

    def tc(c):
        if c in pairs:
            return pairs[c]
        if c in V and c in W:
            return c
        return mc(c)

    def td(d):
        return re.sub(r"L([^;]+);", lambda m: "L" + tc(m.group(1)) + ";", d)

    def wsupers(c):
        out, todo = set(), [c]
        while todo:
            x = todo.pop()
            if x in out or x is None:
                continue
            out.add(x)
            k = W.get(x) or WG.get(x)
            if k:
                todo.append(k.super)
                todo.extend(k.interfaces)
        return out

    def judge(vc, wc):
        """(problems, drift list, renames list) for the pair vc -> wc"""
        vk, wk = V[vc], W[wc]
        problems, drift, ren = [], [], []
        if (vk.access ^ wk.access) & ACC_INTERFACE:
            problems.append("class/interface kind flipped")
        if wk.access & ACC_FINAL and not vk.access & ACC_FINAL:
            problems.append("final in W")
        if not public(wk):
            problems.append("not public in W")
        sup = wsupers(wc)
        for s in [vk.super] + list(vk.interfaces):
            if s and s != "java/lang/Object" and tc(s) not in sup:
                problems.append(f"lost supertype {s} ({tc(s)})")
        wm = {(m[0], m[1], m[2]): m for m in members(wk)}
        wbyname = collections.defaultdict(list)
        for m in members(wk):
            wbyname[(m[0], m[1])].append(m)
        hint = nhint.get(vc, {})
        for kind, n, d, a in members(vk):
            nd = td(d)
            nn = hint.get(n, mn(n))
            hit = wm.get((kind, nn, nd)) or (wm.get((kind, n, nd)) if nn != n else None)
            if hit is None:
                alts = wbyname.get((kind, nn), []) + (wbyname.get((kind, n), []) if nn != n else [])
                drift.append((kind, n, d, nn, ("same name, other shapes: " + " | ".join(x[2] for x in alts))
                              if alts else "no member of the name in W"))
                continue
            ha = hit[3]
            why = []
            if (a ^ ha) & ACC_STATIC:
                why.append("static-ness")
            if a & ACC_PUBLIC and not ha & ACC_PUBLIC:
                why.append("narrower access")
            if ha & ACC_FINAL and not a & ACC_FINAL and kind == "M":
                why.append("final in W")
            if ha & ACC_ABSTRACT and not a & ACC_ABSTRACT:
                why.append("abstract in W, had a body in V")
            if why:
                drift.append((kind, n, d, hit[1], ", ".join(why)))
                continue
            if hit[1] != mn(n):
                ren.append((kind, n, d, hit[1], a))
        # an abstract method W added: a mod class implementing V's type lacks it (AbstractMethodError)
        vnames = {(k, hint.get(n, mn(n)), td(d)) for k, n, d, _ in members(vk)}
        for kind, n, d, a in members(wk):
            if kind == "M" and a & ACC_ABSTRACT and (kind, n, d) not in vnames:
                drift.append(("A", n, d, n, "abstract in W, V had no such method"))
        return problems, drift, ren

    # settle the pairs: a refused pair no longer translates, which may turn members of others into drift
    while True:
        bad = {}
        for vc, wc in pairs.items():
            p, _, _ = judge(vc, wc)
            if p:
                bad[vc] = p
        if not bad:
            break
        for vc, p in bad.items():
            refused[vc] = (pairs.pop(vc), p)

    # renamed members of classes both releases keep
    frag = {"classRenames": {}, "methodRenames": {}, "fieldRenames": {}, "overrideRenames": []}
    report = []
    hints_used = set()

    def emit(vc, wc, ren, drift):
        vk = V[vc]
        for kind, n, d, nn, a in ren:
            hints_used.add((vc, n))
            if kind == "F":
                frag["fieldRenames"][f"{vc}.{n}:{d}"] = nn
            else:
                frag["methodRenames"][f"{vc}.{n}{d}"] = nn
                if not a & (ACC_STATIC | ACC_FINAL | ACC_PRIVATE) and not vk.access & ACC_FINAL and n != "<init>":
                    frag["overrideRenames"].append({
                        "old": n + d, "new": nn, "onlyUnder": vc, "superCalls": "rename",
                        "basis": f"swfapi: 26.1's Fabric API renamed {vc.rsplit('/', 1)[1]}.{n} to {nn} (same descriptor);"
                                 " vanilla and Fabric API call the new name"})
            report.append((vc, wc, kind, n, d, "rename", nn))
        for kind, n, d, nn, why in drift:
            report.append((vc, wc, kind, n, d, "drift", f"{nn}: {why}"))

    for vc in sorted(pairs):
        wc = pairs[vc]
        _, drift, ren = judge(vc, wc)
        frag["classRenames"][vc] = wc
        report.append((vc, wc, "C", "", "", "class-rename", f"{len(ren)} member renames, {len(drift)} drifted"))
        emit(vc, wc, ren, drift)
    for vc in sorted(c for c in V if c.startswith(API) and c in W and c not in pairs and c not in refused and public(V[c])):
        _, drift, ren = judge(vc, vc)
        emit(vc, vc, ren, drift)
    for vc in sorted(refused):
        wc, p = refused[vc]
        report.append((vc, wc, "C", "", "", "refused", "; ".join(p[:6]) + (" ..." if len(p) > 6 else "")))
    for vc in sorted(c for c in V if c.startswith(API) and c not in W and c not in pairs and c not in refused
                     and public(V[c]) and not c.endswith("package-info")):
        report.append((vc, "-", "C", "", "", "gone", "no hint, or the hinted class is not in W"))
    for vc, ms in sorted(nhint.items()):
        for n, nn in sorted(ms.items()):
            if (vc, n) not in hints_used and not any(r[0] == vc and r[3] == n for r in report):
                report.append((vc, "-", "?", n, "", "hint-unused", nn))

    # what another area of the step already wires is left to it
    base = json.load(open(wiring_path, encoding="utf-8")) if os.path.exists(wiring_path) else {}
    others = json.loads(json.dumps(base))
    d = wiring_path + ".d"
    for f in sorted(os.listdir(d)) if os.path.isdir(d) else []:
        if f.endswith(".json") and f != "swfapi-renames-gen.json":
            for k, v in json.load(open(os.path.join(d, f), encoding="utf-8")).items():
                if isinstance(v, dict):
                    others.setdefault(k, {}).update(v)
    for sec in ("classRenames", "methodRenames", "fieldRenames"):
        for k in [k for k in frag[sec] if k in others.get(sec, {})]:
            del frag[sec][k]
    frag = {k: v for k, v in frag.items() if v}
    os.makedirs(d, exist_ok=True)
    with open(os.path.join(d, "swfapi-renames-gen.json"), "w", encoding="utf-8", newline="\n") as f:
        f.write(json.dumps(frag, indent=1, ensure_ascii=False) + "\n")
    with open(out_report, "w", encoding="utf-8", newline="\n") as f:
        f.write("vclass\twclass\tkind\tname\tdesc\tfate\tdetail\n")
        for r in report:
            f.write("\t".join(r) + "\n")
    fates = collections.Counter(r[5] for r in report)
    print(f"{len(pairs)} class renames, {len(refused)} refused; members: {dict(fates)}")
    print(f"  -> {sum(len(v) for v in frag.values())} entries in {d}/swfapi-renames-gen.json")


if __name__ == "__main__":
    main()
