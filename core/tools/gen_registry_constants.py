#!/usr/bin/env python3
"""V static constants a climb step folded into records or moved, found by what they ARE: the object registered
under one id (a Block, an Item), a ResourceKey, a Holder or a TagKey. 26.2 replaced Blocks.WHITE_BED ...
Blocks.BLACK_BED by one ColorCollection<Block> Blocks.BED (white() ... black()), the eight copper variants of a
block by a WeatheringCopperCollection (weathering().exposed(), waxed().oxidized(), ...), and the villager trade
keys the same way. A V-era mod's GETSTATIC Blocks.WHITE_BED:Block is a NoSuchFieldError on W.

Both sides are measured on the real game, not guessed from names: core/tools/RegistryDump.java boots each
release's built-in registries and prints, for every public static field of the named classes (and every record
component of one, two levels down), the registry and id of what it holds. A V field the step's damage table reads
(get-static) and W's owner no longer declares with that descriptor is answered by the W field or record
component of the owner (its classRenames target) holding the same kind of thing under the same registry and id,
its value of the same runtime class (classRenames applied) -- exactly one, a plain field preferred over a record component. A field W still declares under a supertype of V's
type (26.2's Potions: Holder<Potion> where V had Holder$Reference<Potion>) is answered with a cast when the dump
measures W's value to be of V's type. The answer is a generated static getter in the
step layer, rt/CbRegistryConstants (Blocks_WHITE_BED() { return Blocks.BED.white(); }), wired as a fieldGetter in
<V>__<W>.json.d/regconst-gen.json. A key another fragment wires is left to it; constants with no unique
counterpart are listed on stdout.

Regenerate (dumps on beta2: no Java on this PC):
  javac --release 21 -d <tmp> core/tools/RegistryDump.java; copy RegistryDump.class to beta2, then for V and W
  java -cp .:<client-X.jar>:<client-X libraries> RegistryDump <owner classes, dotted> > dump-X.tsv
  (owners: the get-static owners of the step's damage table, client/blaze3d classes excluded: a dedicated
  bootstrap cannot initialise them; for W also the classes constants moved to, e.g. 26.2's
  StructureProcessorTypes, BlockItemIds, predicates/entity/EntitySubPredicates -- a constant found in no class
  of W's own owner is answered from any other dumped class, still exactly one)
  python3 core/tools/gen_registry_constants.py <V> <W> dump-V.tsv dump-W.tsv
"""
import collections
import glob
import json
import os
import re
import sys


def read_dump(path):
    out = collections.defaultdict(list)  # owner -> [(field, path, kind, registry, id, runtime class)]
    for line in open(path, encoding="utf-8"):
        if line.startswith("["):
            continue  # the game's logger
        f = line.rstrip("\n").split("\t")
        if len(f) == 7:
            out[f[0]].append(tuple(f[1:]))
    return out


def java_type(desc):
    assert desc.startswith("L") and desc.endswith(";"), desc
    return desc[1:-1].replace("/", ".").replace("$", ".")


def main():
    v, w, vdump, wdump = sys.argv[1:5]
    step = f"{v}__{w}"
    d = f"core/segments/steps/{step}.json.d"
    out_json = f"{d}/regconst-gen.json"
    wu = w.replace(".", "_")
    pkg = f"top.dext.centurybridge.step.v{wu}.rt"
    cls = "CbRegistryConstants"
    out_java = f"core/shims/steps/{w}/src/{pkg.replace('.', '/')}/{cls}.java"
    V, W = read_dump(vdump), read_dump(wdump)
    cr, wired = {}, set()
    for f in sorted(glob.glob(f"{d}/*.json")):
        if f.replace("\\", "/") == out_json:
            continue
        j = json.load(open(f, encoding="utf-8"))
        cr.update(j.get("classRenames", {}))
        for sec in ("fieldRedirects", "fieldGetters", "fieldRenames", "ledger", "covers"):
            wired.update(j.get(sec, {}).keys())
    # W's declared static fields: (owner, name) -> desc, from the dump's owners only is not enough; read W's jar
    rows = set()
    for line in open(f"core/segments/damage/{step}.damage.tsv", encoding="utf-8"):
        f = line.rstrip("\n").split("\t")
        if len(f) >= 4 and f[0].startswith("get-static") and not f[3].startswith("("):
            rows.add((f[1], f[2], f[3]))
    getters, basis, left = {}, {}, []
    for owner, name, desc in sorted(rows):
        key = f"{owner}.{name}:{desc}"
        if key in wired:
            continue
        mine = [x for x in V.get(owner, []) if x[0] == name and x[1] == ""]
        if len(mine) != 1:
            continue
        _, _, kind, reg, rid, vcls = mine[0]
        wowner = cr.get(owner, owner)
        vcls = cr.get(vcls, vcls)
        # the same object: same registry and id, and a value of the same (translated) runtime class -- 26.2's
        # structure processor registry holds MapCodecs under the ids V's StructureProcessorTypes had
        cands = [(wowner,) + x for x in W.get(wowner, []) if x[2:6] == (kind, reg, rid, vcls)]
        same = [x for x in cands if x[1] == name and x[2] == ""]
        cast = ""
        if same:
            # W still declares it, as a supertype of V's type (26.2 Potions: Holder<Potion> for V's
            # Holder$Reference<Potion>): answered with a cast when W's value is measured to be of V's type
            if same[0][6] != desc[1:-1]:
                left.append((key, f"{kind} {reg} {rid}: W's {name} holds a {same[0][6]}"))
                continue
            cands, cast = same, f"({java_type(desc)}) "
        if not cands:
            # moved to another class of the dump (StructureProcessorType.RULE -> StructureProcessorTypes.RULE)
            cands = [(o,) + x for o in sorted(W) if o != wowner for x in W[o] if x[2:6] == (kind, reg, rid, vcls)]
        plain = [x for x in cands if x[2] == ""]
        pick = plain if plain else cands
        if len(pick) != 1:
            left.append((key, f"{kind} {reg} {rid}: " + (", ".join(x[0] + "." + x[1] + x[2] for x in pick) or "no W constant")))
            continue
        wo, g, path = pick[0][0], pick[0][1], pick[0][2]
        expr = f"{cast}{java_type('L' + wo + ';')}.{g}{path}"
        mname = f"{owner.rsplit('/', 1)[1].replace('$', '_')}_{name}"
        getters[key] = (mname, java_type(desc), expr)
        basis[key] = f"{kind} {reg} {rid}: V {owner.rsplit('/', 1)[1]}.{name}, W {wo.rsplit('/', 1)[1]}.{g}{path}" + (
            f" (holds a {pick[0][6].rsplit('/', 1)[1]})" if cast else "")
    lines = [
        f"package {pkg};",
        "",
        "/**",
        f" * GENERATED by core/tools/gen_registry_constants.py -- regenerate, never hand-edit.",
        f" * {v} static constants {w} folded into records or moved: each answers the {w} constant holding the",
        " * object registered under the same registry and id (both sides measured by core/tools/RegistryDump.java).",
        " */",
        '@SuppressWarnings({"rawtypes", "unchecked"})',
        f"public final class {cls} {{",
        f"    private {cls}() {{}}",
    ]
    for key in sorted(getters):
        mname, rt, expr = getters[key]
        lines += ["", f"    /** {basis[key]} */", f"    public static {rt} {mname}() {{", f"        return {expr};", "    }"]
    lines += ["}", ""]
    os.makedirs(os.path.dirname(out_java), exist_ok=True)
    with open(out_java, "w", encoding="utf-8", newline="\n") as o:
        o.write("\n".join(lines))
    owner_path = f"top/dext/centurybridge/step/v{wu}/rt/{cls}"
    out = {
        "_regconst_generated": f"core/tools/gen_registry_constants.py {v} {w} <dumps>: V constants answered by the W "
                               "constant holding the same registered object / key; regenerate, never hand-edit",
        "fieldGetters": {k: f"{owner_path}#{getters[k][0]}" for k in sorted(getters)},
        "_regconst_basis": {k: basis[k] for k in sorted(basis)},
    }
    with open(out_json, "w", encoding="utf-8", newline="\n") as o:
        json.dump(out, o, indent=1, ensure_ascii=False)
        o.write("\n")
    per = collections.Counter(k.split(".")[0] for k in getters)
    for k, n in per.most_common():
        print(f"  {n:4d} {k}")
    print(f"-> {out_json}: {len(getters)} fieldGetters ({out_java}); {len(left)} without a unique counterpart:")
    for k, why in left:
        print(f"  open {k}: {why}")


if __name__ == "__main__":
    main()
