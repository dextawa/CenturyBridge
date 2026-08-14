"""Chained cross-version symbol resolution.

Naive resolution (translate old intermediary -> old Mojmap -> compare with new
inventory) counts every Mojang rename as a death. The chain fixes that:

    old intermediary ==(intermediary stable through LAST_INT)==> mid intermediary
        ==(mid ProGuard)==> mid Mojmap names ==> resolve against new inventory

Renames between old and LAST_INT are tracked by FabricMC's Matcher work; only
renames after LAST_INT (the final intermediary version, 1.21.11) stay invisible,
so results remain a mild lower bound.
"""

from __future__ import annotations

import mappings
from inventory import Inventory
from translate import Translator, INT_CLASS, INT_METHOD, INT_FIELD

LAST_INT = "1.21.11"  # final Minecraft version with a real intermediary

L1, L2, L3, STABLE = "L1_intact", "L2_desc_changed", "L3_gone", "assumed_stable"
UNKNOWN = "unknown_ns"  # looks like an unremapped source-name (no refmap); can't be judged
_RANK = {L1: 0, STABLE: 0, UNKNOWN: 0, L2: 1, L3: 2}


def _worst(a: str, b: str) -> str:
    return a if _RANK.get(a, 0) >= _RANK.get(b, 0) else b


class ChainResolver:
    def __init__(self, old_version: str, new_version: str, mid_version: str = LAST_INT):
        self.old = mappings.load(old_version)   # intermediary-ns view of old
        self.mid = mappings.load(mid_version)   # intermediary-ns view of mid
        self.t_mid = Translator(mid_version)    # mid intermediary -> mid Mojmap
        self.inv = Inventory(new_version)       # new-version named symbol inventory

    def resolve_class(self, int_name: str) -> str:
        if not int_name.startswith("net/minecraft/"):
            return STABLE
        if not INT_CLASS.match(int_name):
            if int_name not in self.old.classes:
                return UNKNOWN  # not a 1.20.1 intermediary identity: unremapped Yarn/Mojmap string
            return L1 if self.inv.has_class(int_name) else L3
        if int_name not in self.mid.classes:
            return L3  # died on or before the intermediary segment
        named = self.t_mid.named_class(int_name)
        if named is None:
            return L3
        return L1 if self.inv.has_class(named) else L3

    def resolve_member(self, kind: str, int_name: str, desc_int: str | None) -> str:
        pat = INT_METHOD if kind == "m" else INT_FIELD
        if not pat.match(int_name):
            return STABLE
        mid_table = self.mid.methods if kind == "m" else self.mid.fields
        entries = mid_table.get(int_name)
        if not entries:
            return L3  # died on or before the intermediary segment
        desc_ok = desc_int is None or any(d == desc_int for _, d in entries)
        # pick the matching declaration (or any) to locate the named owner
        owner_int = next((o for o, d in entries if desc_int is None or d == desc_int), None)
        if owner_int is None:
            owner_int = next(iter(entries))[0]
        named_owner = self.t_mid.named_class(owner_int)
        named_name = self.t_mid.named_member(kind, int_name)
        if named_owner is None or named_name is None:
            return L3
        if desc_ok and desc_int is not None:
            named_desc = self.t_mid.named_desc(desc_int)
            return self.inv.resolve_member(kind, named_owner, named_name, named_desc)
        # signature already drifted on the intermediary segment: at best L2,
        # and L3 if the name has vanished entirely by the new version
        by_name = self.inv.resolve_member(kind, named_owner, named_name, None)
        return _worst(L2, by_name)


def main() -> None:
    import json
    import sys
    from collections import Counter
    from pathlib import Path

    old_v, new_v = sys.argv[1], sys.argv[2]
    ch = ChainResolver(old_v, new_v)

    cls = Counter(ch.resolve_class(c) for c in ch.old.classes if INT_CLASS.match(c))
    stats = {"classes": dict(cls)}
    for kind, label, table in (("m", "methods", ch.old.methods), ("f", "fields", ch.old.fields)):
        c: Counter[str] = Counter()
        for name, entries in table.items():
            pat = INT_METHOD if kind == "m" else INT_FIELD
            if not pat.match(name):
                continue
            descs = {d for _, d in entries}
            for d in list(descs)[:1] if len(descs) == 1 else list(descs):
                c[ch.resolve_member(kind, name, d)] += 1
                break  # one verdict per symbol name
        stats[label] = dict(c)

    out = Path(__file__).resolve().parent.parent / "data" / "reports" / f"survival-chained-{old_v}-to-{new_v}.json"
    out.write_text(json.dumps(stats, indent=2), encoding="utf-8")

    print(f"== chained symbol survival {old_v} -> {new_v} (via {LAST_INT}) ==")
    for label in ("classes", "methods", "fields"):
        s = stats[label]
        total = sum(s.values())
        parts = ", ".join(
            f"{k.replace('_',' ')} {v/total:.1%}" for k, v in sorted(s.items(), key=lambda kv: -kv[1])
        )
        print(f"{label} (n={total}): {parts}")
    print(f"saved -> {out}")


if __name__ == "__main__":
    main()
