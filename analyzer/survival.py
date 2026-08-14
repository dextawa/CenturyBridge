"""Whole-mapping survival stats: every 1.20.1 intermediary symbol vs a 26.x inventory."""

from __future__ import annotations

import json
import sys
from collections import Counter
from pathlib import Path

from inventory import Inventory
from translate import Translator, INT_CLASS

REPORTS = Path(__file__).resolve().parent.parent / "data" / "reports"


def main(old_v: str, new_v: str) -> None:
    t = Translator(old_v)
    inv = Inventory(new_v)

    cls_total = cls_alive = 0
    for int_name, named in t.cls_int_to_named.items():
        if not INT_CLASS.match(int_name):
            continue
        cls_total += 1
        if inv.has_class(named):
            cls_alive += 1

    stats = {"classes": {"total": cls_total, "survived": cls_alive}}
    for kind, label in (("m", "methods"), ("f", "fields")):
        c: Counter[str] = Counter()
        for k, owner_int, name_int, named_owner, named_name, desc_named in t.members:
            if k != kind:
                continue
            if named_name is None or named_owner is None:
                c["untranslated"] += 1
                continue
            c[inv.resolve_member(kind, named_owner, named_name, desc_named)] += 1
        stats[label] = dict(c)

    REPORTS.mkdir(parents=True, exist_ok=True)
    out = REPORTS / f"survival-{old_v}-to-{new_v}.json"
    out.write_text(json.dumps(stats, indent=2), encoding="utf-8")

    print(f"== raw symbol survival {old_v} -> {new_v} ==")
    print(f"classes: {cls_alive}/{cls_total} = {cls_alive/cls_total:.1%}")
    for label in ("methods", "fields"):
        s = stats[label]
        total = sum(s.values())
        parts = ", ".join(
            f"{k.replace('_', ' ')} {v/total:.1%}"
            for k, v in sorted(s.items(), key=lambda kv: -kv[1])
        )
        print(f"{label} (n={total}): {parts}")
    print(f"\nsaved -> {out}")


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
