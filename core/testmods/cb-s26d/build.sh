#!/usr/bin/env bash
# cb-s26d: s26d's block-outline / WorldRenderEvents client check of the 26.1.2 -> 26.2 step (client_checks/s26.md,
# "Block outline and AFTER_ENTITIES at 26.2"). Two jars:
#   cb-s26d.jar   a 1.21.8-era client mod (intermediary names, no refmap) with Fabric API 0.136 WorldRenderEvents
#                 listeners (START, END, AFTER_ENTITIES, BEFORE_BLOCK_OUTLINE, BLOCK_OUTLINE; BLOCK_OUTLINE refuses and
#                 draws its own cross in phase 2). Climb it from 1.21.8: boot_client_remote.sh with CB_FROM=1.21.8, T=26.2.
#   cb-s26dn.jar  a native 26.2 mod and the driver: @Inject(HEAD) on LevelRenderer.submitBlockOutline / submitHitOutline,
#                 PreparedFrame.executeSolid / executeTranslucent, a 26.2 Fabric API LevelRenderEvents.BEFORE_BLOCK_OUTLINE
#                 listener (refuses in phase 3); judges the counters and the per-frame order, prints "CB-S26D ...", stops
#                 the client. CB_NATIVE_MODS on the climbed run.
#   bash core/testmods/cb-s26d/build.sh <outDir>   (from the repository root on beta2; CB_DATA=<data dir>)
set -euo pipefail
OUTD="$(cd "$1" && pwd)"
DATA="${CB_DATA:-data}"
JDK="${CB_JDK:-$HOME/cbtest/jdk-25.0.4.1+1/bin}"
build() { # <stub jar> <runtime dir> <release> <src> <res> <jar> [extra classpath]
    local cls cp j
    cls=$(mktemp -d)
    cp="$1${7:+:$7}"
    for j in $(find "$2/libraries" -name '*.jar'); do cp="$cp:$j"; done
    "$JDK/javac" --release "$3" -nowarn -proc:none -cp "$cp" -d "$cls" $(find "$4" -name '*.java')
    cp -r "$5"/. "$cls/"
    (cd "$cls" && "$JDK/jar" cf "$OUTD/$6" .)
    rm -rf "$cls"
    echo "-> $OUTD/$6"
}
fapi() { # <runtime dir>: the Fabric API modules (jar-in-jar) of that runtime as a classpath, unpacked into $FAPI
    local f
    f="$(ls "$1"/mods/fabric-api-*.jar | head -1)"
    rm -rf "$FAPI" && mkdir -p "$FAPI"
    (cd "$FAPI" && unzip -q "$(cd "$(dirname "$f")" && pwd)/$(basename "$f")" 'META-INF/jars/*')
    find "$FAPI/META-INF/jars" -name '*.jar' | tr '\n' ':'
}
FAPI=$(mktemp -d)
FCP=$(fapi "$DATA/runtime/client-1.21.8")
build "$DATA/jars/client-1.21.8-intermediary.jar" "$DATA/runtime/client-1.21.8" 21 \
    core/testmods/cb-s26d/src core/testmods/cb-s26d/res cb-s26d.jar "${FCP%:}"
FCP=$(fapi "$DATA/runtime/client-26.2")
build "$DATA/jars/client-26.2.jar" "$DATA/runtime/client-26.2" 25 \
    core/testmods/cb-s26d/native/src core/testmods/cb-s26d/native/res cb-s26dn.jar "${FCP%:}"
rm -rf "$FAPI"
