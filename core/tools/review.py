"""Review forged bridges by the model's own confidence.

A body that compiles is not the same as a body that behaves. javac catches
wrong names and wrong types; it cannot catch a bridge that returns a plausible
value the original would never have returned. The model usually knows when it
is guessing, so it now says so, and this is how that gets read.

  review.py <verified.json>...            summary by rating
  review.py <verified.json>... --low      print the low-confidence bodies
  review.py <verified.json>... --medium   print medium too
"""
import collections
import json
import sys


def main():
    paths = [a for a in sys.argv[1:] if not a.startswith("--")]
    flags = {a for a in sys.argv[1:] if a.startswith("--")}

    recs = []
    for p in paths:
        recs.extend(json.load(open(p, encoding="utf-8")))

    by = collections.Counter(r.get("confidence") or "unrated" for r in recs)
    tomb = sum(1 for r in recs
               if "UnsupportedOperationException" in (r.get("body") or ""))
    print(f"{len(recs)} verified bridges ({tomb} tombstones)")
    for level in ("high", "medium", "low", "unrated"):
        if by[level]:
            print(f"  {by[level]:4d}  {level}")

    show = []
    if "--low" in flags or "--all" in flags:
        show.append("low")
    if "--medium" in flags or "--all" in flags:
        show.append("medium")
    if not show:
        print("\npass --low or --medium to inspect those bodies")
        return

    for level in show:
        picked = [r for r in recs if (r.get("confidence") or "") == level]
        if not picked:
            continue
        print(f"\n{'=' * 60}\n{level.upper()} confidence: {len(picked)}\n")
        for r in picked:
            print(f"--- {r['owner']}.{r['name']}{r['oldDesc']}  [{r['shimKind']}]")
            if r.get("confidenceWhy"):
                print(f"    model says: {r['confidenceWhy']}")
            for line in (r.get("body") or "").splitlines()[:6]:
                print(f"    {line}")
            print()


if __name__ == "__main__":
    main()
