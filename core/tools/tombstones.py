"""Close the tool-decidable half of the kill list.

CLASS_GONE and constructor damage cannot be bridged by any mechanism the JVM
offers: you cannot Mixin a method onto a class that no longer exists, and you
cannot add a constructor back to one that does. What you CAN do is make the
failure legible -- a tombstone that names the symbol and the boundary it died
at, instead of a bare NoSuchMethodError three frames deep in someone's mod.

These land as ledger entries with a specific reason, which is what lets the
closure check treat them as accounted rather than ignored.
"""
import json
import sys
from collections import Counter, defaultdict


def main():
    orders_paths = sys.argv[1:-1]
    shims_path = sys.argv[-1]

    shims = json.load(open(shims_path, encoding="utf-8"))
    ledger = shims.setdefault("ledger", {})

    # group by owner: one ledger line per class beats 579 identical ones
    by_owner = defaultdict(lambda: {"n": 0, "fates": Counter(), "side": ""})
    for p in orders_paths:
        for o in json.load(open(p, encoding="utf-8")):
            if o["shimKind"] != "TOMBSTONE":
                continue
            e = by_owner[o["owner"]]
            e["n"] += 1
            e["fates"][o["fate"]] += 1
            e["side"] = o["side"]

    added = skipped = 0
    for owner, e in sorted(by_owner.items()):
        key = owner.split("$")[0]  # ledger resolves inner classes to the outer
        if key in ledger:
            skipped += 1
            continue
        fates = ", ".join(f"{k}x{v}" for k, v in e["fates"].most_common())
        if "CLASS_GONE" in e["fates"]:
            why = ("class deleted upstream; no facade exists, so references fail "
                   "lazily with an attributed message")
        else:
            why = ("constructor signature changed; a constructor cannot be re-added "
                   "by Mixin or redirected, so callers fail lazily")
        ledger[key] = f"tombstone [{e['side']}]: {why} ({e['n']} symbols: {fates})"
        added += 1

    with open(shims_path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(shims, f, indent=1, ensure_ascii=False)

    total = sum(e["n"] for e in by_owner.values())
    print(f"tombstone owners: {len(by_owner)} covering {total} symbols")
    print(f"  ledger entries added: {added} (already present: {skipped})")
    print(f"  ledger now: {len(ledger)}")


if __name__ == "__main__":
    main()
