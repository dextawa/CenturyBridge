#!/usr/bin/env bash
# cb-smx6: a 1.20.6-era mod (intermediary names, no refmap, no fabric-api) whose mixin cancels 1.20.6's channeling
# branch the way a 1.20.6 mod would: a cancellable HEAD injector on ThrownTrident.isChanneling (class_1685.method_31551,
# a member 1.21 dropped; step 1.20.6 -> 1.21 restores it and routes 1.21's data-driven channeling strike through it,
# CbEntityTridentMixin). ServerDriverMixin runs Driver once the world is loaded: "CB-SMX6 ok|FAIL <case> <detail>",
# ending "CB-SMX6 summary ok=N fail=M".
#
#   bash core/testmods/cb-smx6/build.sh <out.jar>     (from the repository root; javac only;
#   CB_DATA=<data dir> when it is not ./data, CB_CPSEP=";" on Windows)
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
MIXIN="${CB_MIXIN:-$DATA/runtime/client-1.20.6/libraries/net/fabricmc/sponge-mixin/0.17.3+mixin.0.8.7/sponge-mixin-0.17.3+mixin.0.8.7.jar}"
LIBS=$(find "$DATA"/runtime/client-1.20.6/libraries -name '*.jar' ! -name 'sponge-mixin-*' | tr '\n' "${CB_CPSEP:-:}")
# MixinExtras (TeleportLocalMixin's @Local): the copy Fabric Loader nests and initializes for every mod
NEST=$(mktemp -d)
python3 - "$(find "$DATA"/runtime/client-1.20.6/libraries -name 'fabric-loader-*.jar' | sed -n 1p)" "$NEST" <<'PYNEST'
import os, sys, zipfile
with zipfile.ZipFile(sys.argv[1]) as z:
    for n in z.namelist():
        if n.startswith("META-INF/jars/mixinextras-") and n.endswith(".jar"):
            open(os.path.join(sys.argv[2], os.path.basename(n)), "wb").write(z.read(n))
PYNEST
LIBS="$LIBS$(ls "$NEST"/*.jar | tr '\n' "${CB_CPSEP:-:}")"
javac --release 21 -nowarn -proc:none -cp "$DATA/jars/client-1.20.6-intermediary.jar${CB_CPSEP:-:}$MIXIN${CB_CPSEP:-:}$LIBS" -d "$CLS" \
    $(find core/testmods/cb-smx6/src -name '*.java')
cp core/testmods/cb-smx6/res/* "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS" "$NEST"
echo "-> $OUT"
