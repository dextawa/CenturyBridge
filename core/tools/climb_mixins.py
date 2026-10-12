"""What a climb did to the mods' mixins, step by step, and how much of it the step wiring could carry.

convert-climb writes one report per step (<climb>/steps/<V>__<W>/report.md). For every mixin the
step had to take out ("stripped mixin: ...") or injector it switched off ("disabled injector: ..."),
this lists the member the mixin aimed at and whether that step's wiring already says what became of
the member -- an override hook, a call reshape or redirect, a rename: a mixin retargeted through
that mapping would keep working instead of being dropped.

  python climb_mixins.py <climb dir> <segments dir> [out.tsv]
"""
import collections
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from generic_drift import read_wiring  # noqa: E402

STRIP = re.compile(r"^- stripped mixin: (\S+)(?: \[[^\]]*\])? -- (.*)$")
DISABLED = re.compile(r"^- disabled injector: (.*?): @(\w+) (\S+) -> (\S+)$")
MOD = re.compile(r"^## (\S+) -- ")


def wiring_of(segments, step):
    base = os.path.join(segments, "shims-1.20.2.json") if step == "1.20.1__1.20.2" \
        else os.path.join(segments, "steps", step + ".json")
    return read_wiring(base) if os.path.exists(base) else {}


def mapping(wiring, member):
    """the wiring kind that says what became of owner.name(desc) at this step, or None"""
    owner, _, nd = member.partition(".")
    for k in ("callReshapes", "instanceRedirects", "staticRedirects", "methodRenames", "superRedirects",
              "ctorRedirects", "superCtorReshapes", "fieldRedirects", "fieldRenames"):
        if member in wiring.get(k, {}):
            return k
    for h in wiring.get("overrideHooks", []):
        if isinstance(h, dict) and h.get("old") == nd:
            return "overrideHooks"
    for h in wiring.get("convertHooks", []):
        if h.get("member") == nd:
            return "convertHooks"
    return None


def main():
    climb, segments = sys.argv[1], sys.argv[2]
    out = sys.argv[3] if len(sys.argv) > 3 else None
    steps_dir = os.path.join(climb, "steps")
    releases = ["1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6", "1.21", "1.21.1", "1.21.2", "1.21.3",
                "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11", "26.1", "26.1.1",
                "26.1.2", "26.2"]
    order = sorted(os.listdir(steps_dir), key=lambda s: releases.index(s.split("__")[0]) if s.split("__")[0] in releases else 99)
    rows = []
    for step in order:
        rep = os.path.join(steps_dir, step, "report.md")
        if not os.path.exists(rep):
            continue
        wiring = wiring_of(segments, step)
        mod = "?"
        for line in open(rep, encoding="utf-8", errors="replace"):
            line = line.rstrip("\n")
            m = MOD.match(line)
            if m:
                mod = m.group(1)
                continue
            m = STRIP.match(line)
            if m:
                why = m.group(2)
                target = re.search(r"(net/minecraft/\S+?\.\S+?\(\S*?\)\S+)", why)
                rows.append((step, mod, "stripped", m.group(1), why[:160],
                             mapping(wiring, target.group(1)) if target else ""))
                continue
            m = DISABLED.match(line)
            if m:
                target = m.group(4)
                rows.append((step, mod, "disabled", m.group(2) + " " + m.group(3), m.group(1) + " " + target,
                             mapping(wiring, target) or ""))
    by = collections.OrderedDict()
    for r in rows:
        k = by.setdefault(r[0], collections.Counter())
        k[r[2]] += 1
        if r[5]:
            k[r[2] + " (wired)"] += 1
        k["mods"] += 0
    for step, c in by.items():
        mods = len({r[1] for r in rows if r[0] == step})
        print(f"{step:22} stripped {c['stripped']:4} (wired {c['stripped (wired)']:3})  "
              f"disabled {c['disabled']:4} (wired {c['disabled (wired)']:3})  in {mods} mods")
    print(f"total: {sum(1 for r in rows if r[2] == 'stripped')} mixins stripped, "
          f"{sum(1 for r in rows if r[2] == 'disabled')} injectors disabled; "
          f"{sum(1 for r in rows if r[5])} aimed at a member the step's wiring maps")
    if out:
        with open(out, "w", encoding="utf-8", newline="\n") as f:
            f.write("step\tmod\tkind\tmixin\twhy\twired\n")
            for r in rows:
                f.write("\t".join(r) + "\n")


if __name__ == "__main__":
    main()
