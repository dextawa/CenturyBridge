#!/usr/bin/env bash
# server_stub.sh <ver>: a dedicated-server classpath for build.sh's server-check gate, from Mojang's
# server bundler: the inner server jar remapped to intermediary (classes only) and the bundled
# libraries, under data/runtime/server-<ver>/. Run from ~/cbwork on beta2 (engine in core/build/classes,
# tiny mappings in restore/mappings).
set -euo pipefail
V="$1"
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
OUT="data/runtime/server-$V"
WORK=$(mktemp -d)
python3 - "$V" "$WORK/bundler.jar" <<'EOF'
import json, sys, urllib.request
v, dest = sys.argv[1], sys.argv[2]
mf = json.load(urllib.request.urlopen("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"))
vj = json.load(urllib.request.urlopen(next(x["url"] for x in mf["versions"] if x["id"] == v)))
urllib.request.urlretrieve(vj["downloads"]["server"]["url"], dest)
EOF
mkdir -p "$OUT/libraries" "$OUT/.fabric/remappedJars/minecraft-$V-cb"
(cd "$WORK" && unzip -q -o bundler.jar 'META-INF/versions/*' 'META-INF/libraries/*')
cp -r "$WORK/META-INF/libraries/." "$OUT/libraries/"
INNER=$(ls "$WORK"/META-INF/versions/*/server-*.jar | head -1)
"$JAVA" -Xmx4g -cp "core/build/classes:$LIBS" top.dext.centurybridge.Main remap-jar "restore/mappings/intermediary-$V.tiny" "$INNER" \
    "$OUT/.fabric/remappedJars/minecraft-$V-cb/server-intermediary.jar"
rm -rf "$WORK"
echo "server-$V: $(du -sh "$OUT" | cut -f1)"
