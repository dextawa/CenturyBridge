"""A step's mixin damage table from the mixin probe's raw verdicts (Main mixin-judge).

The mixin probe (Main mixin-probe) names every point of V a mixin can name; mixin-judge says which of
them do not survive on a release's real bodies. Judged on W raw -- no CenturyBridge, nothing converted --
that is the step's mixin damage; judged on V it is the control, and a member the control already
rejects is no damage of the step (it says the probe or the judge is wrong, and is reported as such).

  python mixin_damage.py <raw-W.tsv> <raw-V.tsv> <out.tsv>

out.tsv: usage owner target anchor ordinal vcount fate -- sorted, one row per probe member.
"""
import collections
import sys


def rows(path):
    with open(path, encoding="utf-8") as f:
        next(f)
        for line in f:
            r = line.rstrip("\n").split("\t")
            yield tuple(r[:5]), r


def main():
    raw_w, raw_v, out = sys.argv[1], sys.argv[2], sys.argv[3]
    control = {k for k, _ in rows(raw_v)}
    kept = []
    dropped = 0
    for k, r in rows(raw_w):
        if k in control:
            dropped += 1
            continue
        kept.append(r)
    kept.sort(key=lambda r: (r[1], r[2], r[0], r[3], r[4]))
    with open(out, "w", encoding="utf-8", newline="\n") as o:
        o.write("usage\towner\ttarget\tanchor\tordinal\tvcount\tfate\n")
        for r in kept:
            o.write("\t".join(r) + "\n")
    by = collections.Counter(r[0] for r in kept)
    print(f"mixin damage: {len(kept)} rows ({len(control)} control rows on V, {dropped} of them also on W) -> {out}")
    print("  " + ", ".join(f"{u} {n}" for u, n in sorted(by.items())))


if __name__ == "__main__":
    main()
