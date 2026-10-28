#!/usr/bin/env bash
# cb-smx6c: smx6's client checks of the 1.20.6 -> 1.21 step (D:/tmp/main/client_checks/smx6.md, FaceBakery.bakeQuad
# with the trailing model location). Two jars:
#   cb-smx6c.jar  a 1.20.6-era mod (intermediary names, no refmap): @Inject HEAD on 1.20.6's
#                 FaceBakery.bakeQuad(.., ResourceLocation) counting calls and recording the location, @Inject RETURN
#                 (cancellable) replacing the quad for minecraft:block/stone by an all-zero one. Climb it one step
#                 (boot_client_remote.sh CB_FROM=1.20.6, T=1.21).
#   cb-smx6n.jar  a native 1.21 driver: counts BlockModel.bakeFace calls, dumps a hash of every baked model's quads
#                 ("CB-SMX6 dump <id> <hash>"), reads cb-smx6c's counters when it is loaded, walls stone beside
#                 cobblestone in front of the player for a screenshot, prints "CB-SMX6 ok|FAIL ..", "CB-SMX6 N/T"
#                 and stops the client. Run it as CB_NATIVE_MODS beside the climbed mod and layers, and alone on a bare
#                 1.21 client (CB_CLIMB=0) for the vanilla dump to compare with.
#   bash core/testmods/cb-smx6c/build.sh <outDir>   (from the repository root on beta2; CB_DATA=<data dir>)
set -euo pipefail
OUTD="$(cd "$1" && pwd)"
DATA="${CB_DATA:-data}"
build() { # <ver> <src> <res> <jar>
    local cls cp j
    cls=$(mktemp -d)
    cp="$DATA/jars/client-$1-intermediary.jar"
    for j in $(find "$DATA/runtime/client-$1/libraries" -name '*.jar'); do cp="$cp:$j"; done
    for j in "$NEST"/*.jar; do cp="$cp:$j"; done
    javac --release 21 -nowarn -proc:none -cp "$cp" -d "$cls" $(find "$2" -name '*.java')
    cp -r "$3"/. "$cls/"
    (cd "$cls" && jar cf "$OUTD/$4" .)
    rm -rf "$cls"
    echo "-> $OUTD/$4"
}
# MixinExtras (the native BakeFaceWrapCounter): the copy Fabric Loader nests and initializes for every mod
NEST=$(mktemp -d)
python3 - "$(find "$DATA"/runtime/client-1.21/libraries -name 'fabric-loader-*.jar' | sed -n 1p)" "$NEST" <<'PYNEST'
import os, sys, zipfile
with zipfile.ZipFile(sys.argv[1]) as z:
    for n in z.namelist():
        if n.startswith("META-INF/jars/mixinextras-") and n.endswith(".jar"):
            open(os.path.join(sys.argv[2], os.path.basename(n)), "wb").write(z.read(n))
PYNEST
build 1.20.6 core/testmods/cb-smx6c/src core/testmods/cb-smx6c/res cb-smx6c.jar
build 1.21 core/testmods/cb-smx6c/native/src core/testmods/cb-smx6c/native/res cb-smx6n.jar
rm -rf "$NEST"
