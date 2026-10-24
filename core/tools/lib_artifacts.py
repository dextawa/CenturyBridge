"""The libraries each release ships as its own runtime -- the artifact scope of the library damage tables.

`Main lib-diff` compares two libraries dirs, but a runtime dir also holds what the Fabric
loader brought (fabric-loader, sponge-mixin, its asm/guava pins...), in whatever loader
version was current when the dir was bootstrapped. Those are not the game's, and they make
steps where nothing changed look damaged. This tool reads the game's own lists from Mojang:

  client: the version JSON's `libraries` (every OS: an artifact gated to one OS is still
          the game's; natives classifiers skipped -- they hold no classes a mod links)
  server: META-INF/libraries.list of the server bundler jar

and writes one row per release x artifact:

  release  group/artifact  version  sides(client|server|client,server)

  python3 core/tools/lib_artifacts.py <out.tsv> <release...>

The server jar is downloaded to a temp file, its list read, the file deleted.
"""
import json
import os
import sys
import tempfile
import urllib.request
import zipfile

MANIFEST = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"


def fetch(url, dest=None, tries=3):
    for i in range(tries):
        try:
            with urllib.request.urlopen(url, timeout=300) as r:
                if dest:
                    with open(dest, "wb") as f:
                        while True:
                            b = r.read(1 << 20)
                            if not b:
                                break
                            f.write(b)
                    return None
                return r.read()
        except Exception:
            if i == tries - 1:
                raise


def coord(name):
    """'group:artifact:version[:classifier]' -> (group/artifact, version) or None for natives"""
    parts = name.split(":")
    if len(parts) > 3 and parts[3].startswith("natives"):
        return None
    return parts[0].replace(".", "/") + "/" + parts[1], parts[2]


def main():
    out, releases = sys.argv[1], sys.argv[2:]
    manifest = json.loads(fetch(MANIFEST))
    urls = {v["id"]: v["url"] for v in manifest["versions"]}
    rows = []
    for rel in releases:
        vj = json.loads(fetch(urls[rel]))
        arts = {}
        for lib in vj["libraries"]:
            c = coord(lib["name"])
            if c and lib.get("downloads", {}).get("artifact"):
                arts.setdefault(c[0], [c[1], set()])[1].add("client")
        fd, tmp = tempfile.mkstemp(suffix=".jar")
        os.close(fd)
        try:
            fetch(vj["downloads"]["server"]["url"], tmp)
            with zipfile.ZipFile(tmp) as z:
                listing = z.read("META-INF/libraries.list").decode()
        finally:
            os.remove(tmp)
        for line in listing.splitlines():
            f = line.split("\t")
            if len(f) < 2:
                continue
            c = coord(f[1])
            if c:
                arts.setdefault(c[0], [c[1], set()])[1].add("server")
        for a in sorted(arts):
            ver, sides = arts[a]
            rows.append((rel, a, ver, ",".join(sorted(sides))))
        print(f"{rel}: {len(arts)} artifacts", flush=True)
    with open(out, "w", encoding="utf-8", newline="\n") as f:
        f.write("# the game's own libraries per release (core/tools/lib_artifacts.py, from Mojang's version JSON\n")
        f.write("# and the server bundler's META-INF/libraries.list); the artifact scope of Main lib-diff\n")
        f.write("release\tartifact\tversion\tsides\n")
        for r in rows:
            f.write("\t".join(r) + "\n")


if __name__ == "__main__":
    main()
