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
# MixinExtras (FollowOwnerWrapMixin): the copy Fabric Loader nests and initializes for every mod
NEST=$(mktemp -d)
python3 - "$(find "$DATA"/runtime/client-1.21/libraries -name 'fabric-loader-*.jar' | sed -n 1p)" "$NEST" <<'PYNEST'
import os, sys, zipfile
with zipfile.ZipFile(sys.argv[1]) as z:
    for n in z.namelist():
        if n.startswith("META-INF/jars/mixinextras-") and n.endswith(".jar"):
            open(os.path.join(sys.argv[2], os.path.basename(n)), "wb").write(z.read(n))
PYNEST
LIBS="$LIBS$(ls "$NEST"/*.jar | tr '\n' "${CB_CPSEP:-:}")"
# the control's access widener (EnchantmentHelper.method_8220): compiled against the stub it widens, as loom would
STUB="$CLS.stub.jar"
CB_DATA="$DATA" bash core/tools/make_aw_stub.sh 1.21 "$DATA/jars/client-1.21-intermediary.jar" "$STUB" \
    core/testmods/cb-smx6v/res/cb_smx6v.accesswidener > /dev/null
javac --release 21 -nowarn -proc:none -cp "$STUB${CB_CPSEP:-:}$MIXIN${CB_CPSEP:-:}$LIBS" -d "$CLS" \
    $(find core/testmods/cb-smx6v/src -name '*.java')
cp core/testmods/cb-smx6v/res/* "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS" "$NEST" "$STUB"
echo "-> $OUT"
