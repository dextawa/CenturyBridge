"""Build a symbol inventory for an unobfuscated Minecraft version (26.x+).

Downloads the client jar via piston-meta, scans every class, and stores
declared classes/members plus hierarchy links for resolution walks.
"""

from __future__ import annotations

import gzip
import json
import sys
import urllib.request
import zipfile
from pathlib import Path

import classfile

DATA_DIR = Path(__file__).resolve().parent.parent / "data"
MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json"


def client_jar(version: str) -> Path:
    jars = DATA_DIR / "jars"
    jars.mkdir(parents=True, exist_ok=True)
    out = jars / f"client-{version}.jar"
    if out.exists():
        return out
    manifest = json.load(urllib.request.urlopen(MANIFEST))
    entry = next(v for v in manifest["versions"] if v["id"] == version)
    vjson = json.load(urllib.request.urlopen(entry["url"]))
    url = vjson["downloads"]["client"]["url"]
    print(f"downloading client jar {version} ({vjson['downloads']['client']['size']/1e6:.0f} MB)")
    urllib.request.urlretrieve(url, out)
    return out


def build(version: str) -> Path:
    inv_dir = DATA_DIR / "inventory"
    inv_dir.mkdir(parents=True, exist_ok=True)
    out = inv_dir / f"{version}.json.gz"
    if out.exists():
        return out
    jar = client_jar(version)
    classes: dict[str, dict] = {}
    n = 0
    with zipfile.ZipFile(jar) as zf:
        for name in zf.namelist():
            if not name.endswith(".class"):
                continue
            try:
                cf = classfile.parse(zf.read(name), want_annotations=False, want_refs=False)
            except Exception as e:
                print(f"  parse failed: {name}: {e}")
                continue
            classes[cf.name] = {
                "s": cf.super_name,
                "i": cf.interfaces,
                "m": [[m.name, m.desc] for m in cf.methods],
                "f": [[f.name, f.desc] for f in cf.fields],
            }
            n += 1
    print(f"{version}: {n} classes scanned")
    with gzip.open(out, "wt", encoding="utf-8") as f:
        json.dump({"version": version, "classes": classes}, f)
    return out


class Inventory:
    def __init__(self, version: str):
        with gzip.open(build(version), "rt", encoding="utf-8") as f:
            self.classes: dict[str, dict] = json.load(f)["classes"]
        # per-class lookup: name -> {method name -> set(desc)}, same for fields
        self._m: dict[str, dict[str, set[str]]] = {}
        self._f: dict[str, dict[str, set[str]]] = {}
        for cname, c in self.classes.items():
            dm: dict[str, set[str]] = {}
            for mn, md in c["m"]:
                dm.setdefault(mn, set()).add(md)
            self._m[cname] = dm
            df: dict[str, set[str]] = {}
            for fn, fd in c["f"]:
                df.setdefault(fn, set()).add(fd)
            self._f[cname] = df

    def has_class(self, name: str) -> bool:
        return name in self.classes

    def _hierarchy(self, cls: str):
        seen = set()
        stack = [cls]
        while stack:
            c = stack.pop()
            if c in seen or c not in self.classes:
                continue
            seen.add(c)
            yield c
            info = self.classes[c]
            if info["s"]:
                stack.append(info["s"])
            stack.extend(info["i"])

    def resolve_member(self, kind: str, owner: str, name: str, desc: str | None) -> str:
        """Return survival level for a member reference against this inventory."""
        if owner not in self.classes:
            return "L3_owner_gone"
        table = self._m if kind == "m" else self._f
        name_seen = False
        for c in self._hierarchy(owner):
            descs = table.get(c, {}).get(name)
            if not descs:
                continue
            if desc is None or desc in descs:
                return "L1_intact"
            name_seen = True
        return "L2_desc_changed" if name_seen else "L3_gone"


if __name__ == "__main__":
    build(sys.argv[1])
