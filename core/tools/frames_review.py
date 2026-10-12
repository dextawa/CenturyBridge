"""Frames hooks whose missing arguments nobody has judged yet.

An override hook that carries its arguments (frames) rewrites the mod's super.old(..) into
super.new(..) and reads the values the old shape has no place for back from the frame the
synthesized new shape pushed. When the mod's old method is entered directly -- the mod's own call,
not vanilla's -- there is no frame, and the value is its type's zero: fine for Screen.renderBackground's
mouse position, a NullPointerException for TagType.skip's NbtAccounter or DispenserBlock.dispense's
BlockState. Every such argument needs a judgment in "frameDefaults": a static computing what V used,
or "zero: <why>" when the zero value is V's meaning. This lists the ones without.

  python frames_review.py <step wiring.json> [out.tsv]
"""
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from generic_drift import read_wiring  # noqa: E402

ARG = re.compile(r"\[*(?:L[^;]+;|[ZBCSIJFD])")


def main():
    wiring = read_wiring(sys.argv[1])
    defaults = wiring.get("frameDefaults", {})
    rows = []
    for h in wiring.get("overrideHooks", []):
        if not isinstance(h, dict) or not h.get("frames"):
            continue
        new = h["new"]
        na = ARG.findall(new[new.index("(") + 1:new.index(")")])
        passed = set(h.get("args", range(len(ARG.findall(h["old"][h["old"].index("(") + 1:h["old"].index(")")])))))
        judged = defaults.get(h["old"], {})
        for j, t in enumerate(na):
            if j not in passed and str(j) not in judged:
                rows.append((h.get("onlyUnder", ""), h["old"], new, str(j), t))
    print(f"frames hooks: {len(rows)} argument(s) the old shape lacks with no frameDefaults judgment")
    if len(sys.argv) > 2:
        with open(sys.argv[2], "w", encoding="utf-8", newline="\n") as f:
            f.write("onlyUnder\told\tnew\targ\ttype\n")
            for r in rows:
                f.write("\t".join(r) + "\n")


if __name__ == "__main__":
    main()
