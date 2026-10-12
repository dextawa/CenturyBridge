"""Fabric API modules a release's Fabric API no longer ships.

A mod's fabric.mod.json may depend on a single Fabric API module ("fabric-networking-v0"). When the
next release's Fabric API drops that module, Fabric Loader refuses the mod before a class loads --
whatever the bridges give back. Each step's wiring maps such dependencies (depRenames "id":
"<provider>@<range>") onto whatever provides the API on W: CenturyBridge's layer when it gives the
module back, or the module that took its place.

  python fapi_modules.py <runtime dir> <V> <W> [<V> <W> ...]
(<runtime dir>/client-<ver>/mods/fabric-api-*.jar; the module ids are the fabric.mod.json ids of its
nested jars, and the ids they "provide")
"""
import glob
import io
import json
import os
import sys
import zipfile


def modules(runtime, ver):
    jars = glob.glob(os.path.join(runtime, f"client-{ver}", "mods", "fabric-api-*.jar"))
    if not jars:
        return None
    ids = set()
    with zipfile.ZipFile(jars[0]) as z:
        for n in z.namelist():
            if n.startswith("META-INF/jars/") and n.endswith(".jar"):
                with zipfile.ZipFile(io.BytesIO(z.read(n))) as inner:
                    try:
                        fmj = json.loads(inner.read("fabric.mod.json").decode("utf-8"), strict=False)
                    except (KeyError, ValueError):
                        continue
                    ids.add(fmj.get("id"))
                    for p in fmj.get("provides", []):
                        ids.add(p if isinstance(p, str) else p.get("id"))
        try:
            top = json.loads(z.read("fabric.mod.json").decode("utf-8"), strict=False)
            ids.add(top.get("id"))
            for p in top.get("provides", []):
                ids.add(p if isinstance(p, str) else p.get("id"))
        except (KeyError, ValueError):
            pass
    return {i for i in ids if i}


def main():
    runtime = sys.argv[1]
    a = sys.argv[2:]
    for i in range(0, len(a), 2):
        v, w = a[i], a[i + 1]
        mv, mw = modules(runtime, v), modules(runtime, w)
        if mv is None or mw is None:
            print(f"{v} -> {w}: no fabric-api jar for {'V' if mv is None else 'W'}")
            continue
        gone = sorted(mv - mw)
        print(f"{v} -> {w}: {len(gone)} module(s) gone: {' '.join(gone)}")


if __name__ == "__main__":
    main()
