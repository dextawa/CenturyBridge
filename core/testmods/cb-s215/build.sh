#!/usr/bin/env bash
# cb-s215: a 1.21.4-era mod (intermediary names, no refmap, no fabric-api) exercising what step 1.21.4 -> 1.21.5 gives
# back; native/ is cb-s215n, a 1.21.5 mod (never converted) reading through 1.21.5's own API what the converted mod
# built. Both log "CB-S215[N] ok|FAIL <case> <detail>" and a summary once the world is loaded.
#
#   bash core/testmods/cb-s215/build.sh <out-dir>     (from the repository root; javac only)
#   -> <out-dir>/cb-s215.jar (against the 1.21.4 stub) and <out-dir>/cb-s215n.jar (against the 1.21.5 stub)
#   CB_DATA=<data dir> when it is not ./data, CB_CPSEP=";" on Windows
set -euo pipefail
OUT="$(cd "$1" && pwd)"
DATA="${CB_DATA:-data}"
S="${CB_CPSEP:-:}"
build() { # <ver> <src> <res> <jar> <release>
    local CLS LIBS
    CLS=$(mktemp -d)
    LIBS=$(find "$DATA"/runtime/client-$1/libraries -name '*.jar' | tr '\n' "$S")
    javac --release "$5" -nowarn -proc:none -cp "$DATA/jars/client-$1-intermediary.jar$S$LIBS" -d "$CLS" $(find "$2" -name '*.java')
    cp "$3"/* "$CLS/"
    (cd "$CLS" && jar cf "$4" .)
    rm -rf "$CLS"
    echo "-> $4"
}
build 1.21.4 core/testmods/cb-s215/src core/testmods/cb-s215/res "$OUT/cb-s215.jar" 21
build 1.21.5 core/testmods/cb-s215/native/src core/testmods/cb-s215/native/res "$OUT/cb-s215n.jar" 21
