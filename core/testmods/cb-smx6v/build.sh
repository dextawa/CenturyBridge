#!/usr/bin/env bash
# cb-smx6v: the control for cb-smx6 -- a native 1.21 mod (intermediary names, never converted) with cb-smx6's driver and
# no mixin on channeling. Booted with the step layer and without it (boot.sh), its bolt counts per hit are 1.21's own
# channeling, for comparison with cb-smx6's passive-mixin run. Logs "CB-SMX6V ok|FAIL <case> <detail>" and
# "CB-SMX6V summary ok=N fail=M".
#
#   bash core/testmods/cb-smx6v/build.sh <out.jar>     (from the repository root; javac only;
#   CB_DATA=<data dir> when it is not ./data, CB_CPSEP=";" on Windows)
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
MIXIN="${CB_MIXIN:-$DATA/runtime/client-1.21/libraries/net/fabricmc/sponge-mixin/0.17.3+mixin.0.8.7/sponge-mixin-0.17.3+mixin.0.8.7.jar}"
LIBS=$(find "$DATA"/runtime/client-1.21/libraries -name '*.jar' ! -name 'sponge-mixin-*' | tr '\n' "${CB_CPSEP:-:}")
javac --release 21 -nowarn -proc:none -cp "$DATA/jars/client-1.21-intermediary.jar${CB_CPSEP:-:}$MIXIN${CB_CPSEP:-:}$LIBS" -d "$CLS" \
    $(find core/testmods/cb-smx6v/src -name '*.java')
cp core/testmods/cb-smx6v/res/* "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
