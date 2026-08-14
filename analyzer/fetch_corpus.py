"""Fetch a pilot corpus of DEAD 1.20.1 Fabric mods from Modrinth.

Sampling frame (per PLAN.md):
- dead = supports no release version newer than the source version
- stratified by primary category, capped per category
- optimisation / library projects are fetched into a separate bucket and
  excluded from the coverage KPI (reported separately)
"""

from __future__ import annotations

import json
import re
import sys
import time
import urllib.parse
import urllib.request
from collections import Counter
from concurrent.futures import ThreadPoolExecutor
from pathlib import Path

API = "https://api.modrinth.com/v2"
UA = "centurybridge-p0-analyzer/0.1"
SOURCE = "1.20.1"
RELEASE_PAT = re.compile(r"^(\d+)\.(\d+)(?:\.(\d+))?$")

CORPUS_DIR = Path(__file__).resolve().parent.parent / "data" / "corpus" / SOURCE
NON_KPI_CATS = {"optimisation", "library"}
PER_CATEGORY_CAP = 30
TARGET = 260
SEARCH_PAGES = 40  # x100 hits


def ver_key(v: str):
    m = RELEASE_PAT.match(v)
    if not m:
        return None
    return tuple(int(x) if x else 0 for x in m.groups())


SOURCE_KEY = ver_key(SOURCE)


def api_get(path: str, params: dict | None = None):
    url = f"{API}{path}"
    if params:
        url += "?" + urllib.parse.urlencode(params)
    req = urllib.request.Request(url, headers={"User-Agent": UA})
    for attempt in range(8):
        try:
            with urllib.request.urlopen(req, timeout=30) as r:
                return json.load(r)
        except Exception:
            if attempt == 7:
                raise
            time.sleep(min(3 * (attempt + 1), 15))


def is_dead(game_versions: list[str]) -> bool:
    for v in game_versions:
        k = ver_key(v)
        if k and k > SOURCE_KEY:
            return False
    return True


def primary_category(cats: list[str]) -> str:
    real = [c for c in cats if c not in ("fabric", "forge", "quilt", "neoforge")]
    for c in real:  # non-KPI buckets win so they never leak into the KPI corpus
        if c in NON_KPI_CATS:
            return c
    return real[0] if real else "uncategorized"


def search_dead_mods() -> list[dict]:
    picked: dict[str, dict] = {}
    per_cat: Counter[str] = Counter()
    for page in range(SEARCH_PAGES):
        facets = json.dumps([["categories:fabric"], [f"versions:{SOURCE}"], ["project_type:mod"]])
        try:
            res = api_get("/search", {"limit": 100, "offset": page * 100, "index": "downloads", "facets": facets})
        except Exception as e:
            print(f"page {page + 1}: giving up after retries ({e}); continuing with what we have")
            break
        hits = res.get("hits", [])
        if not hits:
            break
        for h in hits:
            if h["project_id"] in picked or not is_dead(h.get("versions", [])):
                continue
            cat = primary_category(h.get("categories", []))
            if per_cat[cat] >= PER_CATEGORY_CAP:
                continue
            per_cat[cat] += 1
            picked[h["project_id"]] = {
                "id": h["project_id"],
                "slug": h["slug"],
                "title": h["title"],
                "category": cat,
                "categories": h.get("categories", []),
                "downloads": h.get("downloads", 0),
                "game_versions": h.get("versions", []),
                "kpi": cat not in NON_KPI_CATS,
            }
        kpi_n = sum(1 for m in picked.values() if m["kpi"])
        print(f"page {page + 1}: dead selected so far {len(picked)} (KPI {kpi_n})")
        if kpi_n >= TARGET:
            break
        time.sleep(1.0)
    return list(picked.values())


def fetch_one(mod: dict) -> dict | None:
    jars = CORPUS_DIR / "jars"
    jars.mkdir(parents=True, exist_ok=True)
    out = jars / f"{mod['slug']}.jar"
    if out.exists():
        mod["file"] = out.name
        return mod
    try:
        versions = api_get(
            f"/project/{mod['id']}/version",
            {"loaders": '["fabric"]', "game_versions": f'["{SOURCE}"]'},
        )
        if not versions:
            return None
        files = versions[0]["files"]
        f = next((x for x in files if x.get("primary")), files[0])
        req = urllib.request.Request(f["url"], headers={"User-Agent": UA})
        with urllib.request.urlopen(req, timeout=120) as r:
            out.write_bytes(r.read())
        mod["file"] = out.name
        mod["version_id"] = versions[0]["id"]
        mod["declared_deps"] = [
            d.get("project_id") for d in versions[0].get("dependencies", []) if d.get("dependency_type") == "required"
        ]
        return mod
    except Exception as e:
        print(f"  fetch failed {mod['slug']}: {e}")
        return None


def main() -> None:
    CORPUS_DIR.mkdir(parents=True, exist_ok=True)
    mods = search_dead_mods()
    kpi = [m for m in mods if m["kpi"]]
    aux = [m for m in mods if not m["kpi"]]
    print(f"\nselected: {len(kpi)} KPI mods + {len(aux)} non-KPI (optimisation/library)")
    print("category spread:", dict(Counter(m["category"] for m in kpi).most_common()))

    fetched: list[dict] = []
    with ThreadPoolExecutor(max_workers=6) as ex:
        for r in ex.map(fetch_one, mods):
            if r:
                fetched.append(r)
    (CORPUS_DIR / "index.json").write_text(
        json.dumps(fetched, indent=1, ensure_ascii=False), encoding="utf-8"
    )
    total_mb = sum((CORPUS_DIR / "jars" / m["file"]).stat().st_size for m in fetched) / 1e6
    print(f"\nfetched {len(fetched)} jars ({total_mb:.0f} MB) -> {CORPUS_DIR}")


if __name__ == "__main__":
    main()
