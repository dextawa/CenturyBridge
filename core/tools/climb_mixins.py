"""What a climb did to the mods' mixins, step by step, and how much of it the step wiring carried.

convert-climb writes one report per step (<climb>/steps/<V>__<W>/report.md). For every mixin the
step had to take out ("stripped mixin: ...") or injector it switched off ("disabled injector: ...",
target or anchor gone), this lists the member the mixin aimed at and whether that step's wiring says
what became of the member -- an override hook, a call reshape or redirect, a rename. MixinBridge
carries what it can of those through the wiring before triage and repair run: each such mixin member
is a "mixin bridged: ..." line, and a member the wiring maps but that could not be carried
faithfully is a "mixin not bridged: ..." line with the reason. Both are counted per step and kind.

  python climb_mixins.py <climb dir> <segments dir> [out.tsv]
"""
import collections
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from generic_drift import read_wiring  # noqa: E402

STRIP = re.compile(r"^- stripped mixin: (\S+)(?: \[[^\]]*\])? -- (.*)$")
# "target gone: @Kind Mixin.handler -> owner.method(desc)" and
# "anchor gone: @Kind Mixin.handler -> owner.method(desc) [INVOKE owner.name(desc)#n, ...]"
DISABLED = re.compile(r"^- disabled injector: (.*?): @(\w+) (\S+) -> (.*)$")
BRIDGED = re.compile(r"^- (?:\(bundled\) )*mixin bridged: (.*)$")
NOT_BRIDGED = re.compile(r"^- (?:\(bundled\) )*mixin not bridged: (.*)$")
MOD = re.compile(r"^## (\S+) -- ")


def wiring_of(segments, step):
    base = os.path.join(segments, "shims-1.20.2.json") if step == "1.20.1__1.20.2" \
        else os.path.join(segments, "steps", step + ".json")
    return read_wiring(base) if os.path.exists(base) or os.path.isdir(base + ".d") else {}


def mapping(wiring, member):
    """the wiring kind that says what became of owner.name(desc) at this step, or None"""
    owner, _, nd = member.partition(".")
    if not owner.startswith("net/"):
        owner = "net/minecraft/" + owner
    full = owner + "." + nd
    for k in ("callReshapes", "instanceRedirects", "staticRedirects", "methodRenames", "superRedirects",
              "ctorRedirects", "superCtorReshapes", "superCtorAdapts", "fieldRedirects", "fieldRenames"):
        if member in wiring.get(k, {}) or full in wiring.get(k, {}):
            return k
    for h in wiring.get("overrideHooks", []):
        if isinstance(h, dict) and h.get("old") == nd:
            return "overrideHooks"
    for h in wiring.get("convertHooks", []):
        if h.get("member") == nd:
            return "convertHooks"
    return None


def kind_of(line):
    """the bridged / not-bridged member kind: shadow, shadow field, invoker, accessor, overwrite, @Inject, @At..."""
    head = line.split(" ", 1)[0]
    if head == "shadow" and line.startswith("shadow field"):
        return "shadow field"
    if head.startswith("@") and " @At " in line:
        return "@At"
    return head


def main():
    climb, segments = sys.argv[1], sys.argv[2]
    out = sys.argv[3] if len(sys.argv) > 3 else None
    steps_dir = os.path.join(climb, "steps")
    order = sorted(os.listdir(steps_dir), key=lambda s: [int(x) if x.isdigit() else x for x in re.split(r"[._]", s)])
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
                rest = m.group(4)
                target = rest.split(" [", 1)[0].split(" (", 1)[0].replace(" ", "")
                if " [" in rest:
                    # anchor gone: the member the anchor named is what moved
                    a = re.match(r"(?:INVOKE|FIELD) (\S+?)(?:#\d+)?(?:,|\]|$)", rest.split(" [", 1)[1])
                    target = a.group(1) if a else target
                rows.append((step, mod, "disabled", m.group(2) + " " + m.group(3), m.group(1) + " " + target,
                             mapping(wiring, target) or ""))
                continue
            m = BRIDGED.match(line)
            if m:
                rows.append((step, mod, "bridged", kind_of(m.group(1)), m.group(1)[:220], ""))
                continue
            m = NOT_BRIDGED.match(line)
            if m:
                rows.append((step, mod, "not bridged", kind_of(m.group(1)), m.group(1)[:220], ""))
    by = collections.OrderedDict()
    for r in rows:
        k = by.setdefault(r[0], collections.Counter())
        k[r[2]] += 1
        if r[2] in ("stripped", "disabled") and r[5]:
            k[r[2] + " (wired)"] += 1
        if r[2] in ("bridged", "not bridged"):
            k[r[2] + ":" + r[3]] += 1
    for step, c in by.items():
        mods = len({r[1] for r in rows if r[0] == step and r[2] in ("stripped", "disabled")})
        print(f"{step:22} stripped {c['stripped']:4} (wired {c['stripped (wired)']:3})  "
              f"disabled {c['disabled']:4} (wired {c['disabled (wired)']:3})  in {mods} mods;  "
              f"bridged {c['bridged']:4}  not bridged {c['not bridged']:4}")
        for kind in sorted({r[3] for r in rows if r[0] == step and r[2] in ("bridged", "not bridged")}):
            print(f"{'':22}   {kind:14} bridged {c['bridged:' + kind]:4}  not bridged {c['not bridged:' + kind]:4}")
    print(f"total: {sum(1 for r in rows if r[2] == 'stripped')} mixins stripped, "
          f"{sum(1 for r in rows if r[2] == 'disabled')} injectors disabled "
          f"({sum(1 for r in rows if r[2] in ('stripped', 'disabled') and r[5])} aimed at a member the step's wiring maps); "
          f"{sum(1 for r in rows if r[2] == 'bridged')} mixin members bridged, "
          f"{sum(1 for r in rows if r[2] == 'not bridged')} wired but not bridged")
    if out:
        with open(out, "w", encoding="utf-8", newline="\n") as f:
            f.write("step\tmod\tkind\tmixin\twhy\twired\n")
            for r in rows:
                f.write("\t".join(r) + "\n")


if __name__ == "__main__":
    main()
