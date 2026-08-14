"""Dependency availability sweep + dep-capped verdict correction.

For every required dependency declared by corpus mods, ask Modrinth whether a
release supporting the TARGET version exists. Cross-era binding needs a living
new-version dependency; without one, the addon is capped by its dependency
(same-era subgraph bridging of the dep itself becomes the only path).
"""

from __future__ import annotations

import json
import time
import urllib.parse
import urllib.request
from collections import Counter
from pathlib import Path

DATA = Path(__file__).resolve().parent.parent / "data"
SOURCE, TARGET = "1.20.1", "26.2"
UA = "centurybridge-p0-analyzer/0.1"
PLATFORM = {"fabricloader", "fabric", "fabric-api", "minecraft", "java", "fabric-language-kotlin", "fabric-api-base"}
import re as _re
FABRIC_MODULE = _re.compile(r"^fabric-[a-z0-9-]+-v\d+$")  # fabric-api internal module ids
# mod id != Modrinth slug for these well-known cases
ALIASES = {
    "cloth-config2": "cloth-config",
    "roughlyenoughitems": "rei",
    "computercraft": "cc-tweaked",
    "ad_astra": "ad-astra",
}


def canonical(dep: str) -> str:
    if dep.startswith("porting_lib"):
        return "porting-lib"
    if dep.startswith("cardinal-components"):
        return "cardinal-components-api"
    return ALIASES.get(dep, dep)


def is_platform(dep: str) -> bool:
    return dep in PLATFORM or bool(FABRIC_MODULE.match(dep))


def api(path: str):
    url = f"https://api.modrinth.com/v2{path}"
    req = urllib.request.Request(url, headers={"User-Agent": UA})
    for attempt in range(6):
        try:
            with urllib.request.urlopen(req, timeout=30) as r:
                return json.load(r)
        except urllib.error.HTTPError as e:
            if e.code == 404:
                return None
            time.sleep(3)
        except Exception:
            time.sleep(3)
    return None


def resolve_project(dep_id: str):
    """Mod id != Modrinth slug in general; try direct, then search."""
    p = api(f"/project/{urllib.parse.quote(dep_id)}")
    if p:
        return p
    res = api(f"/search?limit=3&query={urllib.parse.quote(dep_id)}&facets=" + urllib.parse.quote('[["project_type:mod"]]'))
    for hit in (res or {}).get("hits", []):
        if hit["slug"] == dep_id or dep_id in hit.get("slug", ""):
            return api(f"/project/{hit['project_id']}")
    return None


def main() -> None:
    scan = json.loads((DATA / "reports" / f"scan-{SOURCE}-to-{TARGET}.json").read_text(encoding="utf-8"))
    kpi = [m for m in scan if m.get("meta", {}).get("kpi", True)]
    corpus_ids = {m.get("id") for m in scan if m.get("id")}

    demand: Counter[str] = Counter()
    dep_of_mod: dict[str, list[str]] = {}
    for m in kpi:
        ext = [canonical(d) for d in m.get("depends", []) if not is_platform(d) and d not in corpus_ids]
        dep_of_mod[m["meta"]["slug"]] = ext
        for d in set(ext):
            demand[d] += 1

    print(f"unique external deps: {len(demand)}")
    availability: dict[str, dict] = {}
    for dep, cnt in demand.most_common():
        proj = resolve_project(dep)
        if proj is None:
            availability[dep] = {"status": "not_found", "demand": cnt}
            print(f"  {dep:30s} x{cnt:<3d} -> not found on modrinth")
            continue
        gvs = proj.get("game_versions", [])
        loaders = proj.get("loaders", [])
        has_target = TARGET in gvs and "fabric" in loaders
        latest = max(
            (v for v in gvs if v.replace(".", "").isdigit()),
            key=lambda v: tuple(int(x) for x in v.split(".")),
            default="?",
        )
        availability[dep] = {
            "status": "alive_on_target" if has_target else "stale",
            "latest_fabric": latest,
            "demand": cnt,
            "slug": proj.get("slug"),
        }
        print(f"  {dep:30s} x{cnt:<3d} -> {'26.2 OK' if has_target else 'stale @ ' + latest}")
        time.sleep(0.4)

    # dep-capped verdict correction
    capped = []
    for m in kpi:
        bad = [
            d for d in dep_of_mod.get(m["meta"]["slug"], [])
            if availability.get(d, {}).get("status") != "alive_on_target"
        ]
        if bad and m["verdict"] in ("A_clean", "B_sig_bridge", "C_shim_or_degrade"):
            capped.append({"slug": m["meta"]["slug"], "verdict": m["verdict"], "capped_by": sorted(set(bad))})

    n = len(kpi)
    orig = Counter(m["verdict"] for m in kpi)
    abc = orig["A_clean"] + orig["B_sig_bridge"] + orig["C_shim_or_degrade"]
    print(f"\n== dep-capped correction ==")
    print(f"A+B+C original: {abc}/{n} = {abc/n:.1%}")
    print(f"dep-capped mods: {len(capped)} -> effective A+B+C = {(abc-len(capped))/n:.1%}")

    out = {
        "availability": availability,
        "dep_capped": capped,
        "effective_abc": (abc - len(capped)) / n,
    }
    (DATA / "reports" / "deps-availability.json").write_text(
        json.dumps(out, indent=1, ensure_ascii=False), encoding="utf-8"
    )
    lines = ["# 依赖可用性扫描（跨时代绑定前提检查）", ""]
    lines.append(f"目标版本：{TARGET}。语料外部依赖 {len(demand)} 个。")
    lines.append("")
    lines.append("| 依赖 | 需求数 | 状态 | 最新 Fabric 版本 |")
    lines.append("|------|--------|------|------------------|")
    for dep, info in sorted(availability.items(), key=lambda kv: -kv[1]["demand"]):
        st = {"alive_on_target": "✅ 26.2 在售", "stale": "⛔ 停更", "not_found": "❓ 未找到"}[info["status"]]
        lines.append(f"| `{dep}` | {info['demand']} | {st} | {info.get('latest_fabric','-')} |")
    lines.append("")
    lines.append(f"**依赖封顶修正**：原 A+B+C {abc/n:.1%} → 有效 {(abc-len(capped))/n:.1%}（{len(capped)} 个模组被停更依赖封顶，需同时代子图过桥）")
    (DATA / "reports" / "deps-availability.md").write_text("\n".join(lines), encoding="utf-8")
    print(f"saved -> {DATA / 'reports' / 'deps-availability.md'}")


if __name__ == "__main__":
    main()
