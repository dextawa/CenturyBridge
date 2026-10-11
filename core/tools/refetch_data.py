"""Re-fetch the downloadable parts of the lost local data/ directory (run on beta2).

  python3 refetch_data.py <outDir>
writes <outDir>/jars/client-<v>-obf.jar for every intermediary-era release, the Fabric
intermediary tiny v2 mappings as <outDir>/mappings/intermediary-<v>.tiny, and Mojang's client
mappings for 1.20.1 / 1.21.4 / 1.21.11 as <outDir>/mappings/mojang-<v>.txt.
"""
import io
import json
import os
import sys
import urllib.request
import zipfile
from concurrent.futures import ThreadPoolExecutor

MANIFEST = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"
INTERMEDIARY = "https://maven.fabricmc.net/net/fabricmc/intermediary/{v}/intermediary-{v}-v2.jar"
VERSIONS = ["1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6", "1.21", "1.21.1", "1.21.2", "1.21.3",
            "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11"]
MOJANG = ["1.20.1", "1.21.4", "1.21.11"]


def fetch(url):
    for i in range(4):
        try:
            with urllib.request.urlopen(url, timeout=180) as r:
                return r.read()
        except Exception:
            if i == 3:
                raise


def main():
    out = sys.argv[1]
    os.makedirs(os.path.join(out, "jars"), exist_ok=True)
    os.makedirs(os.path.join(out, "mappings"), exist_ok=True)
    mf = json.loads(fetch(MANIFEST))
    urls = {v["id"]: v["url"] for v in mf["versions"]}

    def one(v):
        vj = json.loads(fetch(urls[v]))
        jar = os.path.join(out, "jars", f"client-{v}-obf.jar")
        if not os.path.exists(jar):
            open(jar, "wb").write(fetch(vj["downloads"]["client"]["url"]))
        if v in MOJANG:
            txt = os.path.join(out, "mappings", f"mojang-{v}.txt")
            if not os.path.exists(txt):
                open(txt, "wb").write(fetch(vj["downloads"]["client_mappings"]["url"]))
        tiny = os.path.join(out, "mappings", f"intermediary-{v}.tiny")
        if not os.path.exists(tiny):
            with zipfile.ZipFile(io.BytesIO(fetch(INTERMEDIARY.format(v=v)))) as z:
                open(tiny, "wb").write(z.read("mappings/mappings.tiny"))
        return v

    with ThreadPoolExecutor(max_workers=6) as pool:
        for v in pool.map(one, VERSIONS):
            print("ok", v, flush=True)


if __name__ == "__main__":
    main()
