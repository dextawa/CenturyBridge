"""Calls 26.1 retyped from Identifier to sprite Material (and back), converted in place.

26.1 gave model textures their own type: what 1.21.11's model datagen API (TextureMapping, TexturedModel,
ModelTemplate, BlockModelGenerators ...) took and answered as a texture Identifier is a
client.resources.model.sprite.Material -- the same id plus a forceTranslucent flag 1.21.11 did not have.
A call whose 26.1 counterpart (the same member under the wall table's name, same arity) differs from the
translated 1.21.11 descriptor only by Identifier <-> Material in some positions is reshaped in place
(callReshapes): an Identifier argument becomes new Material(id) (not force-translucent: the material
1.21.11's id stood for), a Material answer becomes its id. Every other row is listed in the report.

  python gen_wall_material.py <damage.tsv> <V jar> <W jar> <wall table> <step wiring.json> <fragment.json> <report.tsv>

Regenerating (from the repo root):
  python3 core/tools/gen_wall_material.py core/segments/damage/1.21.11__26.1.damage.tsv \
      data/jars/client-1.21.11-intermediary.jar data/jars/client-26.1.jar data/mappings/inter-to-mojang-1.21.11-26.1.tsv \
      core/segments/steps/1.21.11__26.1.json core/segments/steps/1.21.11__26.1.json.d/swall-material-gen.json <tmp>/material-report.tsv
"""
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from classfile import ACC_PRIVATE, ACC_STATIC, split_args  # noqa: E402
from gen_step_reshapes import Hierarchy, Wall, load, wired_keys  # noqa: E402

ID_V = "Lnet/minecraft/class_2960;"
ID_W = "Lnet/minecraft/resources/Identifier;"
MAT = "Lnet/minecraft/client/resources/model/sprite/Material;"
HELPER = "top/dext/centurybridge/step/v26_1/rt/CbWallMaterials"
TO_MAT = f"{HELPER}#material({ID_V}){MAT}"
TO_ID = f"{HELPER}#id({MAT}){ID_V}"
CALLS = ("call", "call-static", "call-inherited", "call-static-inherited")


def main():
    damage, vjar, wjar, table, wiring_path, frag, report = sys.argv[1:8]
    wall = Wall(table)
    V = load(vjar, False)
    W = load(wjar, False)
    hv, hw = Hierarchy(V), Hierarchy(W)
    merged = json.load(open(wiring_path, encoding="utf-8")) if os.path.exists(wiring_path) else {}
    d = wiring_path + ".d"
    for f in sorted(os.listdir(d)) if os.path.isdir(d) else []:
        if f.endswith(".json") and f != os.path.basename(frag):
            for k, v in json.load(open(os.path.join(d, f), encoding="utf-8")).items():
                if isinstance(v, dict):
                    merged.setdefault(k, {}).update(v)
                elif isinstance(v, list):
                    merged.setdefault(k, []).extend(v)
    wired_calls, _, _ = wired_keys(merged)
    renamed = set(merged.get("classRenames", {}))
    out, rep, seen = {}, [], set()
    for line in list(open(damage, encoding="utf-8"))[1:]:
        r = line.rstrip("\n").split("\t")
        usage, owner, name, desc = r[0], r[1], r[2], r[3]
        if usage not in CALLS or not name or name.startswith("<") or (owner, name, desc) in seen:
            continue
        seen.add((owner, name, desc))
        key = f"{owner}.{name}{desc}"
        if any(f"{c}.{name}{desc}" in wired_calls for c in hv.supers(owner)) or owner in renamed:
            continue
        oa, orr = split_args(wall.desc(desc))
        if ID_W not in oa and orr != ID_W:
            continue
        wowner, wname = wall.cls(owner), wall.member(owner, name, hv)
        cands = []
        for c in hw.supers(wowner):
            k = W.get(c)
            for m in (k.methods if k else []):
                if m.name != wname or m.access & ACC_PRIVATE:
                    continue
                na, nr = split_args(m.desc)
                if len(na) != len(oa):
                    continue
                pairs = list(zip(oa, na)) + [(orr, nr)]
                if all(a == b or (a == ID_W and b == MAT) for a, b in pairs[:-1]) and (
                        orr == nr or (orr == ID_W and nr == MAT)) and m.desc != wall.desc(desc):
                    cands.append(m)
            if cands:
                break
        if len(cands) != 1:
            rep.append((owner, name, desc, f"{len(cands)} 26.1 shapes differing only by Identifier -> Material"))
            continue
        m = cands[0]
        na, nr = split_args(m.desc)
        ops = [f"conv {TO_MAT}" if a != b else "keep" for a, b in zip(oa, na)]
        entry = {"desc": wall.back_desc(m.desc), "ops": ops,
                 "basis": "26.1 retyped the texture Identifier as a sprite Material (the id, not force-translucent)"}
        if orr != nr:
            entry["ret"] = f"conv {TO_ID}"
        out[key] = entry
    with open(frag, "w", encoding="utf-8", newline="\n") as fh:
        json.dump({"callReshapes": dict(sorted(out.items()))}, fh, indent=1)
        fh.write("\n")
    with open(report, "w", encoding="utf-8", newline="\n") as fh:
        for row in rep:
            fh.write("\t".join(row) + "\n")
    print(f"{len(out)} calls -> {frag}; {len(rep)} left (report {report})")


if __name__ == "__main__":
    main()
