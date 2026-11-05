#!/usr/bin/env bash
# cb-swtrade: a 1.20.6-era mod (intermediary names, no refmap, no fabric-api) building EnchantBookForEmeralds /
# SuspiciousStewForEmerald through constructor invokers and calling getRandomEnchantment (method_59891) through an
# invoker; Driver prints the seeded book sequences ("CBTRADE SEQ/PICK") and "CBTEST PASS|FAIL", "CBTEST SUMMARY ok/total".
# Climbed with CB_FROM=1.20.6 to 1.21.11 (the reference sequences) and to 26.1 (the wall's trade facades).
#
#   bash core/testmods/cb-swtrade/build.sh <out.jar>     (from the repository root; javac only;
#   CB_DATA=<data dir> when it is not ./data, CB_CPSEP=";" on Windows)
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
MIXIN="${CB_MIXIN:-$DATA/runtime/client-1.20.6/libraries/net/fabricmc/sponge-mixin/0.17.3+mixin.0.8.7/sponge-mixin-0.17.3+mixin.0.8.7.jar}"
LIBS=$(find "$DATA"/runtime/client-1.20.6/libraries -name '*.jar' ! -name 'sponge-mixin-*' | tr '\n' "${CB_CPSEP:-:}")
# the mod's access widener (SuspiciousStewForEmerald): compiled against the stub it widens, as loom would
# (make_aw_stub.sh; the engine must be compiled in core/build/classes)
STUB="$CLS.stub.jar"
CB_DATA="$DATA" bash core/tools/make_aw_stub.sh 1.20.6 "$DATA/jars/client-1.20.6-intermediary.jar" "$STUB"     core/testmods/cb-swtrade/res/cb_swtrade.accesswidener > /dev/null
javac --release 21 -nowarn -proc:none -cp "$STUB${CB_CPSEP:-:}$MIXIN${CB_CPSEP:-:}$LIBS" -d "$CLS" \
    $(find core/testmods/cb-swtrade/src -name '*.java')
cp core/testmods/cb-swtrade/res/* "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS" "$STUB"
echo "-> $OUT"
