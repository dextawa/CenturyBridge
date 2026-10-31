#!/usr/bin/env bash
# cb-s215: a 1.21.4-era mod (intermediary names, no refmap, no fabric-api) exercising what step 1.21.4 -> 1.21.5 gives
# back; native/ is cb-s215n, a 1.21.5 mod (never converted) reading through 1.21.5's own API what the converted mod
# built. Both log "CB-S215[N] ok|FAIL <case> <detail>" and a summary once the world is loaded.
#
#   bash core/testmods/cb-s215/build.sh <out-dir>     (from the repository root; javac only)
#   -> <out-dir>/cb-s215.jar (against the 1.21.4 stub) and <out-dir>/cb-s215n.jar (against the 1.21.5 stub)
#   CB_DATA=<data dir> when it is not ./data, CB_CPSEP=";" on Windows
#   CB_GT4=<fabric-gametest-api-v1 2.0.26 jar> CB_GT5=<3.1.3 jar>: also build the game test parts: cb-s215's 1.21.4
#   test class (src-gt/, listed in its "fabric-gametest" entrypoint, which only the game test module reads) and
#   cb-s215n's test_instance data (native/res-gt/; its test function registers only under -Dfabric-api.gametest)
set -euo pipefail
OUT="$(cd "$1" && pwd)"
DATA="${CB_DATA:-data}"
S="${CB_CPSEP:-:}"
build() { # <ver> <src> <res> <jar> <release> [<gt src> <gt jar>]
    local CLS LIBS SRCS
    CLS=$(mktemp -d)
    LIBS=$(find "$DATA"/runtime/client-$1/libraries -name '*.jar' | tr '\n' "$S")
    SRCS=$(find "$2" -name '*.java')
    if [ -n "${7:-}" ]; then LIBS="$LIBS$S$7"; [ -d "$6" ] && SRCS="$SRCS $(find "$6" -name '*.java')"; fi
    javac --release "$5" -nowarn -proc:none -cp "$DATA/jars/client-$1-intermediary.jar$S$LIBS" -d "$CLS" $SRCS
    cp -r "$3"/* "$CLS/"
    if [ -n "${7:-}" ] && [ -d "$3-gt" ]; then cp -r "$3-gt"/* "$CLS/"; fi
    (cd "$CLS" && jar cf "$4" .)
    rm -rf "$CLS"
    echo "-> $4"
}
build 1.21.4 core/testmods/cb-s215/src core/testmods/cb-s215/res "$OUT/cb-s215.jar" 21 core/testmods/cb-s215/src-gt "${CB_GT4:-}"
build 1.21.5 core/testmods/cb-s215/native/src core/testmods/cb-s215/native/res "$OUT/cb-s215n.jar" 21 core/testmods/cb-s215/native/src-gt "${CB_GT5:-}"
