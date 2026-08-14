"""Aggregate the corpus scan into the P0 report (markdown + console summary)."""

from __future__ import annotations

import json
from collections import Counter, defaultdict
from pathlib import Path

from translate import Translator

SOURCE = "1.20.1"
TARGET = "26.2"
DATA = Path(__file__).resolve().parent.parent / "data"

BUCKETS = ["A_clean", "B_sig_bridge", "C_shim_or_degrade", "D_loadbearing_dead", "E_unscannable"]
LABELS = {
    "A_clean": "A 直通（零 shim）",
    "B_sig_bridge": "B 需签名桥（自动）",
    "C_shim_or_degrade": "C 需 facade shim / 可降级",
    "D_loadbearing_dead": "D 承重 Mixin 死亡",
    "E_unscannable": "E 无法扫描",
}


def readable(sym: str, t_old: Translator, t_mid: Translator) -> str:
    kind, _, rest = sym.partition(":")
    if kind == "C":
        named = t_mid.named_class(rest) or t_old.named_class(rest) or rest
        return named.rsplit("/", 1)[-1]
    owner, _, name = rest.partition("|")
    k = "m" if kind == "M" else "f"
    owner_named = t_mid.named_class(owner) or t_old.named_class(owner) or owner
    name_named = t_mid.named_member(k, name) or t_old.named_member(k, name) or name
    return f"{owner_named.rsplit('/', 1)[-1]}.{name_named}"


def walk_units(mod: dict):
    yield mod
    for s in mod.get("jij", []):
        yield from walk_units(s)


def main() -> None:
    scan = json.loads((DATA / "reports" / f"scan-{SOURCE}-to-{TARGET}.json").read_text(encoding="utf-8"))
    t_old, t_mid = Translator(SOURCE), Translator("1.21.11")

    kpi = [m for m in scan if m.get("meta", {}).get("kpi", True)]
    aux = [m for m in scan if not m.get("meta", {}).get("kpi", True)]

    def dist(mods):
        c = Counter(m["verdict"] for m in mods)
        n = sum(c.values()) or 1
        return c, n

    c_kpi, n_kpi = dist(kpi)
    lines = [f"# CenturyBridge P0 试点报告：{SOURCE} → {TARGET}", ""]
    lines.append(f"语料：{n_kpi} 个死亡内容模组（KPI）+ {len(aux)} 个优化/库类（单独统计）。")
    lines.append("")
    lines.append("## 四桶判定（KPI 语料）")
    lines.append("")
    lines.append("| 桶 | 数量 | 占比 |")
    lines.append("|----|------|------|")
    for b in BUCKETS:
        lines.append(f"| {LABELS[b]} | {c_kpi.get(b, 0)} | {c_kpi.get(b, 0) / n_kpi:.1%} |")
    auto = (c_kpi.get("A_clean", 0) + c_kpi.get("B_sig_bridge", 0)) / n_kpi
    coverable = auto + c_kpi.get("C_shim_or_degrade", 0) / n_kpi
    lines.append("")
    lines.append(f"**自动可跑（A+B）：{auto:.1%}** —— 仅靠重映射 + 自动签名桥。")
    lines.append(f"**架构可覆盖（A+B+C）：{coverable:.1%}** —— 加上 facade shim 与降级阶梯。")
    lines.append("")

    lines.append("## 分类别覆盖率（KPI）")
    lines.append("")
    by_cat: dict[str, list] = defaultdict(list)
    for m in kpi:
        by_cat[m.get("meta", {}).get("category", "?")].append(m)
    lines.append("| 类别 | n | A+B | A+B+C | D |")
    lines.append("|------|---|-----|-------|---|")
    for cat, mods in sorted(by_cat.items(), key=lambda kv: -len(kv[1])):
        c, n = dist(mods)
        ab = (c.get("A_clean", 0) + c.get("B_sig_bridge", 0)) / n
        abc = ab + c.get("C_shim_or_degrade", 0) / n
        lines.append(f"| {cat} | {n} | {ab:.0%} | {abc:.0%} | {c.get('D_loadbearing_dead', 0)} |")
    lines.append("")

    # ---- mixin stats ----
    total_mx = vanilla_mx = lb_mx = broken_lb = broken_deco = 0
    mods_with_mixins = 0
    for m in kpi:
        found = False
        for u in walk_units(m):
            for mx in u.get("mixins", []):
                total_mx += 1
                found = True
                if mx["vanilla"]:
                    vanilla_mx += 1
                    if mx["load_bearing"]:
                        lb_mx += 1
                        if mx["worst"] == "L3_gone":
                            broken_lb += 1
                    elif mx["worst"] == "L3_gone":
                        broken_deco += 1
        if found:
            mods_with_mixins += 1
    lines.append("## Mixin 统计（KPI）")
    lines.append("")
    lines.append(f"- 含 Mixin 的模组：{mods_with_mixins}/{n_kpi} = {mods_with_mixins / n_kpi:.0%}")
    lines.append(f"- Mixin 总数 {total_mx}，其中钩 vanilla 的 {vanilla_mx}，承重 {lb_mx}")
    if vanilla_mx:
        lines.append(f"- 目标/锚点已死：承重 {broken_lb}/{lb_mx or 1}，装饰 {broken_deco}/{vanilla_mx - lb_mx or 1}")
    lines.append("")

    # ---- shim priority table ----
    sym_mods: Counter[str] = Counter()
    sym_level: dict[str, str] = {}
    for m in kpi:
        seen: set[str] = set()
        for u in walk_units(m):
            for sym, lvl in u.get("damaged", {}).items():
                if sym not in seen:
                    seen.add(sym)
                    sym_mods[sym] += 1
                    sym_level[sym] = lvl
    lines.append("## Shim 优先级表（受损符号 × 波及模组数，前 40）")
    lines.append("")
    lines.append("| 符号 | 等级 | 波及模组 |")
    lines.append("|------|------|----------|")
    for sym, cnt in sym_mods.most_common(40):
        lines.append(f"| `{readable(sym, t_old, t_mid)}` | {sym_level[sym][:2]} | {cnt} |")
    lines.append("")

    # ---- cumulative coverage curve: if we shim the top-N symbols, how many C-mods come back? ----
    top_syms = [s for s, _ in sym_mods.most_common()]
    c_mods = [m for m in kpi if m["verdict"] == "C_shim_or_degrade"]
    lines.append("## 覆盖率曲线：修复前 N 个高频符号后，C 桶还剩多少")
    lines.append("")
    lines.append("| shim 前 N 符号 | C 桶模组恢复为 A/B |")
    lines.append("|---------------|--------------------|")
    for topn in (10, 25, 50, 100, 200):
        fixed = set(top_syms[:topn])
        recovered = 0
        for m in c_mods:
            dmg = set()
            for u in walk_units(m):
                dmg |= {s for s, l in u.get("damaged", {}).items() if l == "L3_gone"}
                for mx in u.get("mixins", []):
                    if mx["vanilla"] and mx["worst"] == "L3_gone" and not mx["load_bearing"]:
                        dmg.add("__mixin__" + mx["class"])  # mixins not shimmed by symbol fixes
            if dmg and dmg <= fixed:
                recovered += 1
        lines.append(f"| {topn} | {recovered}/{len(c_mods)} |")
    lines.append("")

    # ---- keystone dependencies ----
    dep_count: Counter[str] = Counter()
    for m in kpi:
        for d in m.get("depends", []):
            if d not in ("fabricloader", "fabric", "fabric-api", "minecraft", "java", "fabric-language-kotlin"):
                dep_count[d] += 1
    lines.append("## Keystone 依赖（语料内被依赖次数）")
    lines.append("")
    for dep, cnt in dep_count.most_common(15):
        lines.append(f"- `{dep}`: {cnt}")
    lines.append("")

    out = DATA / "reports" / f"P0-report-{SOURCE}-to-{TARGET}.md"
    out.write_text("\n".join(lines), encoding="utf-8")
    print("\n".join(lines[:40]))
    print(f"\nfull report -> {out}")


if __name__ == "__main__":
    main()
