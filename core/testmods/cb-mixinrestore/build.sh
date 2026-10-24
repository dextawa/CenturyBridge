#!/usr/bin/env bash
# cb-mixinrestore: a 1.20.1-era mod (intermediary names, no refmap, no fabric-api) whose injectors aim at
# overrides a later release dropped (1.20.5: AbstractFurnaceBlockEntity.stillValid, now inherited from
# BaseContainerBlockEntity). MixinBridge restoreOverride puts the override back as a super call in a
# priority-1 restoring mixin; the mod's injector then hooks it. ServerDriverMixin runs Driver once the
# world is loaded: "CB-MIXINRESTORE ok|FAIL <case> <detail>", ending "CB-MIXINRESTORE summary ok=N fail=M".
#
#   bash core/testmods/cb-mixinrestore/build.sh <out.jar>     (from the repository root; javac only;
#   CB_DATA=<data dir> when it is not ./data, CB_CPSEP=";" on Windows)
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
MIXIN="${CB_MIXIN:-$DATA/runtime/client-1.20.3/libraries/net/fabricmc/sponge-mixin/0.17.3+mixin.0.8.7/sponge-mixin-0.17.3+mixin.0.8.7.jar}"
LIBS=$(find "$DATA"/runtime/client-1.20.1/libraries -name '*.jar' ! -name 'sponge-mixin-*' | tr '\n' "${CB_CPSEP:-:}")
javac --release 17 -nowarn -proc:none -cp "$DATA/jars/client-1.20.1-intermediary.jar${CB_CPSEP:-:}$MIXIN${CB_CPSEP:-:}$LIBS" -d "$CLS" \
    $(find core/testmods/cb-mixinrestore/src -name '*.java')
cp core/testmods/cb-mixinrestore/res/* "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
