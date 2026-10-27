#!/usr/bin/env bash
# cb-s11d: step 1.21.10 -> 1.21.11's dimension effects routes on a real client.
#   - cb-s11d (src/, res/): a 1.21.10-era client mod (intermediary names, no refmap) putting its own
#     DimensionSpecialEffects in the effects table (accessor) under cb_s11d:fx, and a data pack giving the overworld's
#     dimension type 1.21.10's "effects": "cb_s11d:fx" (1.21.11 fields: OVERWORLD skybox, DEFAULT cardinal light).
#     Converted one step, 1.21.10 -> 1.21.11.
#   - cb-s11dn (native/): a native 1.21.11 client mod, not converted: mixins on every 1.21.11 call the routes keep,
#     and the driver that checks the routes' answers; prints "CB-S11D ok|FAIL ..", "CB-S11D N/T", then stops the client.
#   bash core/testmods/cb-s11d/build.sh <out-dir>   (from the repository root on beta2; CB_DATA=<data dir>)
#   -> <out-dir>/cb-s11d.jar (1.21.10, to convert), <out-dir>/cb-s11dn.jar (1.21.11)
set -euo pipefail
OUT="$(cd "$1" && pwd)"
DATA="${CB_DATA:-data}"
build() { # <ver> <src> <res> <jar>
    local CLS CP
    CLS=$(mktemp -d)
    CP="$DATA/jars/client-$1-intermediary.jar"
    for j in $(find "$DATA/runtime/client-$1/libraries" -name '*.jar'); do CP="$CP:$j"; done
    javac --release 21 -nowarn -proc:none -cp "$CP" -d "$CLS" $(find "$2" -name '*.java')
    cp -r "$3"/. "$CLS/"
    (cd "$CLS" && jar cf "$4" .)
    rm -rf "$CLS"
    echo "-> $4"
}
build 1.21.10 core/testmods/cb-s11d/src core/testmods/cb-s11d/res "$OUT/cb-s11d.jar"
build 1.21.11 core/testmods/cb-s11d/native/src core/testmods/cb-s11d/native/res "$OUT/cb-s11dn.jar"
