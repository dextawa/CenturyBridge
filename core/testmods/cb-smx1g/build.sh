#!/usr/bin/env bash
# cb-smx1g: smx1s's client check of GlUniformClose at 26.2 (queue_smx1.md, smx1r handoff item 2). Two jars:
#   cb-smx1g.jar   a 1.21.5-era client mod (intermediary names, no refmap): a mixin on GlRenderPass (class_10866) whose
#                  setPipeline HEAD puts a legacy float[] into the pass's uniforms map (@Shadow field_57873; 1.21.5 kept
#                  int[] / float[] there), checks the map answers the same array, edits it in place; at most 64 passes,
#                  one put per pass. Climb it: boot_client_remote.sh with CB_FROM=1.21.5, T=26.2.
#   cb-smx1gn.jar  native 26.2 driver: RETURN on GpuDevice.createBuffer records the buffers the 1.21.6 layer makes for
#                  legacy arrays (label "CenturyBridge 1.21.5 ..."); HEAD on GlBuffer.Direct.close counts their frees
#                  (open -> closed) and whether RenderPass.close() is on the stack; HEAD on RenderPass.close counts passes
#                  (the native-W control) and remembers the backend. Then it replays the layer's close hook
#                  (ObjectShare centurybridge:gl_uniforms_closed) on every backend that freed buffers: no further frees
#                  (idempotent). Prints "CB-SMX1G ok|FAIL ..", "CB-SMX1G n/t", stops the client. Alone (no old mod) it
#                  is the native control: passes close, no legacy buffers exist. CB_NATIVE_MODS on the climbed run.
#   bash core/testmods/cb-smx1g/build.sh <outDir>   (from the repository root on beta2; CB_DATA=<data dir>)
set -euo pipefail
OUTD="$(cd "$1" && pwd)"
DATA="${CB_DATA:-data}"
JDK="${CB_JDK:-$HOME/cbtest/jdk-25.0.4.1+1/bin}"
build() { # <stub jar> <runtime dir> <release> <src> <res> <jar>
    local cls cp j
    cls=$(mktemp -d)
    cp="$1"
    for j in $(find "$2/libraries" -name '*.jar'); do cp="$cp:$j"; done
    "$JDK/javac" --release "$3" -nowarn -proc:none -cp "$cp" -d "$cls" $(find "$4" -name '*.java')
    cp -r "$5"/. "$cls/"
    (cd "$cls" && "$JDK/jar" cf "$OUTD/$6" .)
    rm -rf "$cls"
    echo "-> $OUTD/$6"
}
build "$DATA/jars/client-1.21.5-intermediary.jar" "$DATA/runtime/client-1.21.5" 21 \
    core/testmods/cb-smx1g/src core/testmods/cb-smx1g/res cb-smx1g.jar
build "$DATA/jars/client-26.2.jar" "$DATA/runtime/client-26.2" 25 \
    core/testmods/cb-smx1g/native/src core/testmods/cb-smx1g/native/res cb-smx1gn.jar
