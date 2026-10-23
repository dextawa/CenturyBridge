"""Tree placer / decorator members 26.1 narrowed from LevelSimulatedReader to WorldGenLevel.

26.1's trunk, foliage and root placers (and the tree decorator context, Feature's helpers) take the
WorldGenLevel where 1.21.11 took a LevelSimulatedReader. In 1.21.11 that reader always was a WorldGenLevel:
TreeFeature.doPlace receives the WorldGenLevel and hands it on, and the placers pass their own argument
along. Per damage row whose 26.1 counterpart (the wall table's name, same arity) differs from the
translated 1.21.11 descriptor only by LevelSimulatedReader -> WorldGenLevel:
  calls          callReshapes: the argument cast to WorldGenLevel (CbWallWorldGen.worldGenLevel, which
                 names the call a caller passing another reader made) -- super calls included;
  overrides      overrideHooks, for every such member a mod may override (not static, private or final in
                 1.21.11; abstract ones included): the 26.1 shape forwards to the mod's 1.21.11 method (a
                 WorldGenLevel is a LevelSimulatedReader), guarded, under the 1.21.11 class declaring it.
Every other row is listed in the report.

  python gen_wall_worldgen_level.py <damage.tsv> <V jar> <W jar> <wall table> <step wiring.json> <fragment.json> <report.tsv>

Regenerating (from the repo root):
  python3 core/tools/gen_wall_worldgen_level.py core/segments/damage/1.21.11__26.1.damage.tsv \
      data/jars/client-1.21.11-intermediary.jar data/jars/client-26.1.jar data/mappings/inter-to-mojang-1.21.11-26.1.tsv \
      core/segments/steps/1.21.11__26.1.json core/segments/steps/1.21.11__26.1.json.d/swall-worldgen-gen.json <tmp>/worldgen-report.tsv
"""
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from classfile import ACC_FINAL, ACC_PRIVATE, ACC_STATIC, split_args  # noqa: E402
from gen_step_reshapes import Hierarchy, Wall, load, wired_keys  # noqa: E402

READER_V = "Lnet/minecraft/class_3746;"
READER_W = "Lnet/minecraft/world/level/LevelSimulatedReader;"
LEVEL_W = "Lnet/minecraft/world/level/WorldGenLevel;"
CAST = "top/dext/centurybridge/step/v26_1/rt/CbWallWorldGen#worldGenLevel(Lnet/minecraft/class_3746;)Lnet/minecraft/class_5281;"
GUARD = "top/dext/centurybridge/step/v26_1/rt/Guard"
CALLS = ("call", "call-static", "call-inherited", "call-static-inherited", "call-protected", "call-static-protected")


def main():
    damage, vjar, wjar, table, wiring_path, frag, report = sys.argv[1:8]
    wall = Wall(table)
    V, W = load(vjar, False), load(wjar, False)
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
    # hooks the other fragments already give, by (old shape, class under): mechanical.json hooks each
    # vanilla subclass that overrides the member, never the abstract base a mod extends directly
    hooked = {(h.get("old"), h.get("onlyUnder")) for h in merged.get("overrideHooks", [])}
    calls, hooks, rep, seen = {}, [], [], set()
    for line in list(open(damage, encoding="utf-8"))[1:]:
        r = line.rstrip("\n").split("\t")
        usage, owner, name, desc = r[0], r[1], r[2], r[3]
        if READER_V not in desc or not name or name.startswith("<") or (usage, owner, name, desc) in seen:
            continue
        seen.add((usage, owner, name, desc))
        oa, orr = split_args(wall.desc(desc))
        wowner, wname = wall.cls(owner), wall.member(owner, name, hv)
        match = None
        for c in hw.supers(wowner):
            k = W.get(c)
            for m in (k.methods if k else []):
                if m.name != wname or m.access & ACC_PRIVATE:
                    continue
                na, nr = split_args(m.desc)
                if len(na) == len(oa) and nr == orr and m.desc != wall.desc(desc) and all(
                        a == b or (a == READER_W and b == LEVEL_W) for a, b in zip(oa, na)):
                    match = m
            if match:
                break
        if not match:
            rep.append((usage, owner, name, desc, "no 26.1 shape differing only by LevelSimulatedReader -> WorldGenLevel"))
            continue
        na, _ = split_args(match.desc)
        if usage in CALLS:
            key = f"{owner}.{name}{desc}"
            if any(f"{c}.{name}{desc}" in wired_calls for c in hv.supers(owner)):
                continue
            calls[key] = {"desc": wall.back_desc(match.desc),
                          "ops": [f"conv {CAST}" if a != b else "keep" for a, b in zip(oa, na)],
                          "basis": "26.1 takes the WorldGenLevel 1.21.11's LevelSimulatedReader always was (TreeFeature hands its WorldGenLevel on)"}
        # a member a mod may override (any usage names it): the 26.1 shape reconnected to the mod's override,
        # under the 1.21.11 class declaring it
        vdecl = next((c for c in hv.supers(owner) if V.get(c) and any(
            m.name == name and m.desc == desc for m in V[c].methods)), None)
        vm = next((m for m in V[vdecl].methods if m.name == name and m.desc == desc), None) if vdecl else None
        if vm and not vm.access & (ACC_STATIC | ACC_PRIVATE | ACC_FINAL):
            owner = vdecl
            if (name + desc, owner) in hooked or any(h["old"] == name + desc and h["onlyUnder"] == owner for h in hooks):
                continue
            hooks.append({"old": name + desc, "new": wname + wall.back_desc(match.desc), "onlyUnder": owner,
                          "guard": GUARD,
                          "basis": "26.1 calls the WorldGenLevel shape; a WorldGenLevel is the LevelSimulatedReader the 1.21.11 override takes"})
    with open(frag, "w", encoding="utf-8", newline="\n") as fh:
        json.dump({"callReshapes": dict(sorted(calls.items())), "overrideHooks": hooks}, fh, indent=1)
        fh.write("\n")
    with open(report, "w", encoding="utf-8", newline="\n") as fh:
        for row in rep:
            fh.write("\t".join(row) + "\n")
    print(f"{len(calls)} calls, {len(hooks)} hooks -> {frag}; {len(rep)} left (report {report})")


if __name__ == "__main__":
    main()
