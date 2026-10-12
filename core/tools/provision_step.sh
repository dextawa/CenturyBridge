#!/usr/bin/env bash
# provision_step.sh <W>...: what building and measuring a climb step's layer needs from release W, set
# up in beta2's working set (~/cbwork/data) where it is missing:
#   data/runtime/client-W/mods/fabric-api-<latest for W>.jar   (from maven.fabricmc.net)
#   data/runtime/client-W/.fabric/processedMods/*.jar          (its modules, for compiling compat code)
#   data/runtime/server-W/                                     (server_stub.sh: build.sh's server-check)
# Run from ~/cbwork on beta2.
set -euo pipefail
cd ~/cbwork
for W in "$@"; do
    RT="data/runtime/client-$W"
    [ -d "$RT/libraries" ] || { echo "$W: no client runtime at $RT (bootstrap_client.py first)"; exit 1; }
    if ! ls "$RT"/mods/fabric-api-*.jar >/dev/null 2>&1; then
        VER=$(python3 - "$W" <<'EOF'
import re, sys, urllib.request
w = sys.argv[1]
xml = urllib.request.urlopen("https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml").read().decode()
vs = [v for v in re.findall(r"<version>([^<]+)</version>", xml) if v.endswith("+" + w)]
key = lambda v: [int(x) if x.isdigit() else -1 for x in re.split(r"[.\-]", v.split("+")[0])]
print(max(vs, key=key) if vs else "")
EOF
)
        [ -n "$VER" ] || { echo "$W: no fabric-api release on maven"; exit 1; }
        mkdir -p "$RT/mods"
        curl -fsSL -o "$RT/mods/fabric-api-$VER.jar" \
            "https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/$VER/fabric-api-$VER.jar"
        echo "$W: fabric-api $VER"
    fi
    PM="$RT/.fabric/processedMods"
    if [ -z "$(ls "$PM" 2>/dev/null)" ]; then
        mkdir -p "$PM"
        TMP=$(mktemp -d)
        unzip -q -o "$(ls "$RT"/mods/fabric-api-*.jar | head -1)" 'META-INF/jars/*' -d "$TMP" || true
        find "$TMP" -name '*.jar' -exec cp {} "$PM/" \;
        # modules that nest jars of their own (one more level)
        for j in "$PM"/*.jar; do
            if unzip -l "$j" 2>/dev/null | grep -q 'META-INF/jars/.*\.jar'; then
                T2=$(mktemp -d)
                unzip -q -o "$j" 'META-INF/jars/*' -d "$T2" || true
                find "$T2" -name '*.jar' -exec cp {} "$PM/" \;
                rm -rf "$T2"
            fi
        done
        rm -rf "$TMP"
        echo "$W: $(ls "$PM" | wc -l) fabric-api modules"
    fi
    if [ -z "$(find "data/runtime/server-$W" -name server-intermediary.jar 2>/dev/null | head -1)" ] \
            && [ ! -f "data/runtime/server-$W/versions/$W/server-$W.jar" ]; then
        case "$W" in
            26.*)
                # past the wall the server jar is plain (mojang names): Mojang's bundler as it ships
                TMP=$(mktemp -d)
                python3 - "$W" "$TMP/bundler.jar" <<'EOF'
import json, sys, urllib.request
v, dest = sys.argv[1], sys.argv[2]
mf = json.load(urllib.request.urlopen("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"))
vj = json.load(urllib.request.urlopen(next(x["url"] for x in mf["versions"] if x["id"] == v)))
urllib.request.urlretrieve(vj["downloads"]["server"]["url"], dest)
EOF
                (cd "$TMP" && unzip -q -o bundler.jar 'META-INF/versions/*' 'META-INF/libraries/*')
                mkdir -p "data/runtime/server-$W/versions/$W" "data/runtime/server-$W/libraries"
                cp "$(ls "$TMP"/META-INF/versions/*/server-*.jar | head -1)" "data/runtime/server-$W/versions/$W/server-$W.jar"
                cp -r "$TMP/META-INF/libraries/." "data/runtime/server-$W/libraries/"
                rm -rf "$TMP"
                echo "server-$W: $(du -sh "data/runtime/server-$W" | cut -f1)" ;;
            *) bash server_stub.sh "$W" ;;
        esac
    fi
    # The Fabric server's own libraries next to the game's: the loader, sponge-mixin, ASM and
    # intermediary its launcher puts on the classpath (Fabric meta's server profile for W at the
    # loader the tests run). Without them server-check cannot resolve what every server mixin
    # names (CallbackInfo) and reads each one as reaching a class the server lacks.
    if [ -z "$(find "data/runtime/server-$W/libraries/net/fabricmc" -name 'sponge-mixin-*.jar' 2>/dev/null | head -1)" ]; then
        python3 - "$W" "${CB_LOADER:-0.19.3}" "data/runtime/server-$W/libraries" <<'EOF'
import json, os, sys, urllib.request
w, loader, dest = sys.argv[1:4]
prof = json.load(urllib.request.urlopen(f"https://meta.fabricmc.net/v2/versions/loader/{w}/{loader}/server/json"))
for lib in prof["libraries"]:
    g, a, v = lib["name"].split(":")[:3]
    rel = f"{g.replace('.', '/')}/{a}/{v}/{a}-{v}.jar"
    out = os.path.join(dest, rel)
    if not os.path.exists(out):
        os.makedirs(os.path.dirname(out), exist_ok=True)
        urllib.request.urlretrieve(lib.get("url", "https://maven.fabricmc.net/").rstrip("/") + "/" + rel, out + ".part")
        os.replace(out + ".part", out)
print(f"server-{w}: {len(prof['libraries'])} Fabric server libraries (loader {loader})")
EOF
    fi
done
