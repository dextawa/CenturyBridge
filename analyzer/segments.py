"""Per-boundary checkpoint chain and death attribution.

The corpus scan tells us WHICH symbols are damaged; this walks every release
boundary between source and target and tells us WHERE each one died or changed
signature. That table is the work order for per-segment shims: each boundary's
kill list is exactly the shim set that segment must carry.

Checkpoints:
  1.20.1 .. 1.21.11   intermediary tiny per version (identity maintained by FabricMC)
  26.1 .. 26.2        unobfuscated client-jar inventories (identity = Mojmap name,
                      maintained by us from here on -- the post-intermediary era)
"""

from __future__ import annotations

import json
from collections import Counter, defaultdict
from pathlib import Path

import mappings
from inventory import Inventory
from translate import Translator

DATA = Path(__file__).resolve().parent.parent / "data"
SOURCE, TARGET = "1.20.1", "26.2"

RELEASES = [
    "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6",
    "1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "1.21.5", "1.21.6",
    "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11",
    "26.1", "26.1.1", "26.1.2", "26.2",
]
LAST_INT = "1.21.11"


class IntCheckpoint:
    """A version that still has intermediary: check by intermediary identity."""

    def __init__(self, version: str):
        self.version = version
        self.m = mappings.load(version)

    def class_alive(self, int_name: str) -> bool:
        return int_name in self.m.classes

    def member_status(self, kind: str, name: str, old_descs: set[str]) -> str:
        table = self.m.methods if kind == "m" else self.m.fields
        entries = table.get(name)
        if not entries:
            return "gone"
        if not old_descs or old_descs & {d for _, d in entries}:
            return "intact"
        return "desc_changed"


class NamedCheckpoint:
    """Post-intermediary version: check by Mojmap identity via the LAST_INT translator."""

    def __init__(self, version: str, t: Translator):
        self.version = version
        self.inv = Inventory(version)
        self.t = t

    def class_alive(self, int_name: str) -> bool:
        named = self.t.named_class(int_name)
        return named is not None and self.inv.has_class(named)

    def member_status(self, kind: str, name: str, old_descs: set[str]) -> str:
        named = self.t.named_member(kind, name)
        if named is None:
            return "gone"
        owner_named = self.t.member_owner(kind, name)
        if owner_named is None:
            return "gone"
        descs_named = {self.t.named_desc(d) for d in old_descs} if old_descs else set()
        if descs_named:
            best = "gone"
            for dn in descs_named:
                lvl = self.inv.resolve_member(kind, owner_named, named, dn)
                if lvl == "L1_intact":
                    return "intact"
                if lvl == "L2_desc_changed":
                    best = "desc_changed"
                elif lvl == "L3_owner_gone" and best == "gone":
                    best = "gone"
            return best
        lvl = self.inv.resolve_member(kind, owner_named, named, None)
        return {"L1_intact": "intact", "L2_desc_changed": "desc_changed"}.get(lvl, "gone")


def build_chain() -> list:
    t = Translator(LAST_INT)
    # small helper the NamedCheckpoint needs: intermediary member -> named owner
    owner_idx: dict[tuple[str, str], str] = {}
    for kind, owner_int, name_int, named_owner, named_name, _ in t.members:
        if named_owner and (kind, name_int) not in owner_idx:
            owner_idx[(kind, name_int)] = named_owner
    t.member_owner = lambda kind, name: owner_idx.get((kind, name))

    chain = []
    for v in RELEASES[1:]:
        if RELEASES.index(v) <= RELEASES.index(LAST_INT):
            print(f"checkpoint {v} (intermediary)")
            chain.append(IntCheckpoint(v))
        else:
            print(f"checkpoint {v} (named inventory)")
            chain.append(NamedCheckpoint(v, t))
    return chain


def main() -> None:
    scan = json.loads((DATA / "reports" / f"scan-{SOURCE}-to-{TARGET}.json").read_text(encoding="utf-8"))
    old = mappings.load(SOURCE)
    t_old, t_mid = Translator(SOURCE), Translator(LAST_INT)

    # damaged symbol -> number of mods affected (KPI only)
    sym_mods: Counter[str] = Counter()
    def walk(m):
        yield m
        for s in m.get("jij", []):
            yield from walk(s)
    for m in scan:
        if not m.get("meta", {}).get("kpi", True):
            continue
        seen = set()
        for u in walk(m):
            for sym in u.get("damaged", {}):
                if sym not in seen:
                    seen.add(sym)
                    sym_mods[sym] += 1

    chain = build_chain()

    died_at: dict[str, Counter] = defaultdict(Counter)     # boundary -> weighted deaths
    changed_at: dict[str, Counter] = defaultdict(Counter)  # boundary -> weighted sig changes
    examples: dict[str, list] = defaultdict(list)

    from report import readable  # reuse the naming helper

    for sym, weight in sym_mods.items():
        kind_tag, _, rest = sym.partition(":")
        prev = SOURCE
        if kind_tag == "C":
            for cp in chain:
                boundary = f"{prev}->{cp.version}"
                if not cp.class_alive(rest):
                    died_at[boundary][sym] = weight
                    examples[boundary].append((weight, readable(sym, t_old, t_mid), "died"))
                    break
                prev = cp.version
        else:
            kind = "m" if kind_tag == "M" else "f"
            _, _, name = rest.partition("|")
            table = old.methods if kind == "m" else old.fields
            old_descs = {d for _, d in table.get(name, set())}
            state = "intact"
            for cp in chain:
                boundary = f"{prev}->{cp.version}"
                s = cp.member_status(kind, name, old_descs)
                if s == "gone":
                    died_at[boundary][sym] = weight
                    examples[boundary].append((weight, readable(sym, t_old, t_mid), "died"))
                    break
                if s == "desc_changed" and state == "intact":
                    changed_at[boundary][sym] = weight
                    examples[boundary].append((weight, readable(sym, t_old, t_mid), "sig"))
                    state = "desc_changed"
                prev = cp.version

    lines = [f"# 死亡归因：{SOURCE} → {TARGET} 语料受损符号 × 21 条边界", ""]
    lines.append("| 边界 | 死亡符号数 | 波及(加权) | 签名变形数 | 波及(加权) |")
    lines.append("|------|-----------|-----------|-----------|-----------|")
    boundaries = [f"{RELEASES[i]}->{RELEASES[i+1]}" for i in range(len(RELEASES) - 1)]
    for b in boundaries:
        d, c = died_at.get(b, {}), changed_at.get(b, {})
        lines.append(f"| {b} | {len(d)} | {sum(d.values())} | {len(c)} | {sum(c.values())} |")
    lines.append("")
    for b in boundaries:
        ex = sorted(examples.get(b, []), reverse=True)[:8]
        if not ex:
            continue
        lines.append(f"## {b}")
        for w, name, what in ex:
            lines.append(f"- `{name}` ({'死亡' if what == 'died' else '签名'}, 波及 {w} 模组)")
        lines.append("")

    out = DATA / "reports" / "death-attribution.md"
    out.write_text("\n".join(lines), encoding="utf-8")
    print("\n".join(lines[:30]))
    print(f"\nfull -> {out}")


if __name__ == "__main__":
    main()
