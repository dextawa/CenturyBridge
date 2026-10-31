#!/usr/bin/env bash
# cb-s205fr: a 1.20.1-era client mod calling the Fabric renderer API (BlendMode, RendererAccess / MaterialFinder) and
# RenderType.translucent() and a RenderType.create of its own (default composite state, buffer size 1536) once the client has started ("CB-S205FR PASS|FAIL ..." per call, tally "CB-S205FR n/n").
#   bash core/testmods/cb-s205fr/build.sh <outDir>   (from the repository root on beta2; CB_DATA=<data dir>) -> <outDir>/cb-s205fr.jar
# Climb it from 1.20.1 with boot_client_remote.sh: CB_READY='CB-S205FR [0-9]+/[0-9]+' CB_SHOTS="".
set -euo pipefail
OUTD="$(cd "$1" && pwd)"
DATA="${CB_DATA:-data}"
cls=$(mktemp -d); nest=$(mktemp -d)
FAPI=$(ls "$DATA"/runtime/client-1.20.1/mods/fabric-api-*.jar | head -1)
(cd "$nest" && unzip -q -o "$FAPI" 'META-INF/jars/fabric-renderer-api-v1-*.jar' 'META-INF/jars/fabric-lifecycle-events-v1-*.jar' 'META-INF/jars/fabric-api-base-*.jar')
# the mod's access widener opens RenderType.create: compiled against the stub it widens, as loom would
# (make_aw_stub.sh; the engine must be compiled in core/build/classes)
stub="$cls.stub.jar"
CB_DATA="$DATA" bash core/tools/make_aw_stub.sh 1.20.1 "$DATA/jars/client-1.20.1-intermediary.jar" "$stub" \
    core/testmods/cb-s205fr/res/cb_s205fr.accesswidener > /dev/null
cp="$stub"
for j in $(find "$DATA/runtime/client-1.20.1/libraries" "$nest" -name '*.jar'); do cp="$cp:$j"; done
javac --release 17 -nowarn -proc:none -cp "$cp" -d "$cls" $(find core/testmods/cb-s205fr/src -name '*.java')
cp -r core/testmods/cb-s205fr/res/. "$cls/"
(cd "$cls" && jar cf "$OUTD/cb-s205fr.jar" .)
rm -rf "$cls" "$nest" "$stub"
echo "-> $OUTD/cb-s205fr.jar"
