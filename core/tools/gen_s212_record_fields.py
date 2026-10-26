"""fieldRenames / methodRenames for classes 1.21.2 made records (step 1.21.1 -> 1.21.2): gen_smx1_record_fields.py's
rule (a 1.21.1 private field -> the record component its constructor parameter is stored into, read off both
constructors' bytecode; a 1.21.1 getter of such a field -> the component's accessor), run for this step.

  python3 core/tools/gen_s212_record_fields.py <damage-mixin tsv(.gz)> core/segments/steps/1.21.1__1.21.2.json.d/s212-recordfields-gen.json
      (from the repository root; javap on PATH; code jars from $CB_DATA/jars, CB_DATA defaulting to data)

Regenerate, never hand-edit.
"""
import glob
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import gen_smx1_record_fields as g  # noqa: E402

OUT_NAME = "s212-recordfields-gen.json"
g.V, g.W = "1.21.1", "1.21.2"
g.WIRING = "core/segments/steps/1.21.1__1.21.2.json"


def wired():
    """(fieldRenames keys, call-rewriting keys) the step's other fragments wire (this generator's own output left out)"""
    fields, calls = set(), set()
    for p in [g.WIRING] + sorted(glob.glob(g.WIRING + ".d/*.json")):
        if p.replace("\\", "/").endswith(OUT_NAME):
            continue
        d = json.load(open(p, encoding="utf-8"))
        fields |= set(d.get("fieldRenames", {}))
        for sec in g.CALL_SECTIONS:
            calls |= set(d.get(sec, {}))
        # a getter the layer restores on the record (covers) keeps serving mods' calls; renaming it as well would wire
        # the key two ways
        calls |= set(d.get("covers", []))
    return fields, calls


g.wired = wired


def facade_classes():
    """V classes the step's classRenames send to a layer class: their members are the facade's, not the record's"""
    out = set()
    for p in [g.WIRING] + sorted(glob.glob(g.WIRING + ".d/*.json")):
        for k, v in json.load(open(p, encoding="utf-8")).get("classRenames", {}).items():
            if v.startswith("top/dext/"):
                out.add(k)
    return out


if __name__ == "__main__":
    g.main()
    out = sys.argv[2]
    d = json.load(open(out, encoding="utf-8"))
    d.pop("_smx1_recordfields_basis", None)
    fac = facade_classes()
    for sec in ("fieldRenames", "methodRenames"):
        d[sec] = {k: v for k, v in d.get(sec, {}).items() if k[:k.index(".")] not in fac}
    root = {"_s212_recordfields_basis": "gen_s212_record_fields.py (gen_smx1_record_fields' rule over both constructors' "
                                        "bytecode, step 1.21.1 -> 1.21.2); regenerate, never hand-edit"}
    root.update(d)
    with open(out, "w", encoding="utf-8", newline="\n") as o:
        json.dump(root, o, indent=1)
        o.write("\n")
