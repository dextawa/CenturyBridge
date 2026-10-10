"""Stand up an offline Fabric client runtime for one Minecraft version.

Downloads the vanilla libraries + asset index, the Fabric loader libraries, and
writes classpath/jvmargs files for a KnotClient launch. The asset store is
content-addressed, so an existing store from another version is reused and only
missing objects are fetched.

  python bootstrap_client.py <mcVersion> <outDir> [--assets-from <existingRuntime>]
"""
import hashlib
import json
import os
import sys
import urllib.request
from concurrent.futures import ThreadPoolExecutor

MANIFEST = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"
FABRIC_META = "https://meta.fabricmc.net/v2/versions/loader/{v}"


def fetch(url, dest=None, tries=3):
    for i in range(tries):
        try:
            with urllib.request.urlopen(url, timeout=120) as r:
                data = r.read()
            if dest:
                os.makedirs(os.path.dirname(dest), exist_ok=True)
                with open(dest, "wb") as f:
                    f.write(data)
            return data
        except Exception:
            if i == tries - 1:
                raise


# Mojang's library rules name the OS; natives ship as ordinary rule-gated
# artifacts (lwjgl-*-natives-<os>.jar), so the rule filter alone picks the right
# ones. Every natives jar is arch-partitioned inside (linux/x64, linux/arm64...),
# so arch constraints are ignored: LWJGL extracts the one matching the JVM.
OS_NAME = {"win32": "windows", "darwin": "osx"}.get(sys.platform, "linux")


def rules_allow(rules):
    allowed = False
    for r in rules:
        name = r.get("os", {}).get("name")
        if name and name != OS_NAME:
            continue
        allowed = r["action"] == "allow"
    return allowed


def maven_path(coord):
    group, artifact, version = coord.split(":")[:3]
    return f"{group.replace('.', '/')}/{artifact}/{version}/{artifact}-{version}.jar"


def main():
    ver = sys.argv[1]
    out = sys.argv[2]
    assets_from = None
    if "--assets-from" in sys.argv:
        assets_from = sys.argv[sys.argv.index("--assets-from") + 1]
    os.makedirs(out, exist_ok=True)

    # ---- vanilla version json ----
    mf = json.loads(fetch(MANIFEST))
    vurl = next(v["url"] for v in mf["versions"] if v["id"] == ver)
    vj = json.loads(fetch(vurl))

    # ---- vanilla libraries ----
    libdir = os.path.join(out, "libraries")
    cp = []
    jobs = []
    for lib in vj["libraries"]:
        art = lib.get("downloads", {}).get("artifact")
        if not art:
            continue
        rules = lib.get("rules")
        if rules and not rules_allow(rules):
            continue
        dest = os.path.join(libdir, art["path"])
        cp.append(dest)
        if not os.path.exists(dest):
            jobs.append((art["url"], dest))
    with ThreadPoolExecutor(max_workers=8) as pool:
        list(pool.map(lambda j: fetch(j[0], j[1]), jobs))
    print(f"vanilla libs: {len(cp)} ({len(jobs)} downloaded)")

    # ---- fabric loader ----
    loaders = json.loads(fetch(FABRIC_META.format(v=ver)))
    loader_ver = loaders[0]["loader"]["version"]
    profile = json.loads(fetch(
        FABRIC_META.format(v=ver) + f"/{loader_ver}/profile/json"))
    for lib in profile["libraries"]:
        coord = lib["name"]
        base = lib.get("url", "https://maven.fabricmc.net/")
        path = maven_path(coord)
        dest = os.path.join(libdir, path.replace("/", os.sep))
        cp.append(dest)
        if not os.path.exists(dest):
            fetch(base.rstrip("/") + "/" + path, dest)
    main_class = profile["mainClass"]
    print(f"fabric loader {loader_ver}, mainClass {main_class}")

    # ---- client jar (the intermediary-era launch uses the obf jar) ----
    client = os.path.join(out, f"client-{ver}-obf.jar")
    if not os.path.exists(client):
        src = f"data/jars/client-{ver}-obf.jar"
        if os.path.exists(src):
            import shutil
            shutil.copy(src, client)
        else:
            fetch(vj["downloads"]["client"]["url"], client)
    cp.append(client)

    # ---- assets ----
    aidx = vj["assetIndex"]
    assets = os.path.join(out, "assets")
    if assets_from and os.path.isdir(os.path.join(assets_from, "assets", "objects")):
        # content-addressed store: link/copy the whole objects dir reference
        # (windows: just reuse the directory via the launch flag instead)
        assets = os.path.join(assets_from, "assets")
        print(f"reusing asset store: {assets}")
    os.makedirs(os.path.join(assets, "indexes"), exist_ok=True)
    idx_path = os.path.join(assets, "indexes", aidx["id"] + ".json")
    if not os.path.exists(idx_path):
        fetch(aidx["url"], idx_path)
    idx = json.load(open(idx_path, encoding="utf-8"))
    objdir = os.path.join(assets, "objects")
    missing = []
    for obj in idx["objects"].values():
        h = obj["hash"]
        p = os.path.join(objdir, h[:2], h)
        if not os.path.exists(p):
            missing.append((f"https://resources.download.minecraft.net/{h[:2]}/{h}", p))
    print(f"assets: {len(idx['objects'])} total, {len(missing)} to download")
    with ThreadPoolExecutor(max_workers=16) as pool:
        list(pool.map(lambda j: fetch(j[0], j[1]), missing))

    # ---- launch files ----
    with open(os.path.join(out, "classpath.txt"), "w", encoding="utf-8") as f:
        f.write(os.pathsep.join(os.path.abspath(p) for p in cp))
    with open(os.path.join(out, "jvmargs.txt"), "w", encoding="utf-8") as f:
        cp_fwd = os.pathsep.join(os.path.abspath(p).replace("\\", "/") for p in cp)
        f.write(f"-Xmx3G\n-cp\n{cp_fwd}\n")
    with open(os.path.join(out, "launch-info.json"), "w", encoding="utf-8") as f:
        json.dump({"mainClass": main_class, "assetIndex": aidx["id"],
                   "assetsDir": os.path.abspath(assets)}, f, indent=1)
    os.makedirs(os.path.join(out, "mods"), exist_ok=True)
    print(f"READY: {out} (assetIndex {aidx['id']})")


if __name__ == "__main__":
    main()
