#!/usr/bin/env bash
# cb-sl6 (smixin20, Kilt loss 6): a 1.20.4-era mod with MixinExtras @Local on the flint-and-steel dispense behaviour
# (DispenseItemBehavior$10, renumbered $17 at 1.20.5: the anonymous-class retarget), climbed 1.20.4 -> 1.21, plus its
# native-1.21 control cb-sl6n. 1.20.4's game jar declares the local as Level, 1.21's as ServerLevel.
#   ImplicitLevelMixin  @Local Level (implicit): bindable -> retyped to ServerLevel, runs (bindImplicit = primed)
#   OrdinalLevelMixin   @Local(ordinal = 0) Level: unbindable on 1.21 -> disabled with a reported loss (ordinal = 0)
#   PrimeCountMixin     no locals (primed = 1)
#   cb-sl6n             native 1.21 @Local ServerLevel (native = 1, with and without the layers)
# Logs "CB-SL6 driven by <who> primed=.. bindImplicit=.. levelClass=.. ordinal=.. native=..", then "CB-SL6 END".
#
#   bash core/testmods/cb-sl6/build.sh <out.jar> <outNative.jar>   (from the repository root; javac only;
#   CB_DATA=<data dir> when it is not ./data, CB_CPSEP=";" on Windows)
set -euo pipefail
OUT="$(cd "$(dirname "$1")" && pwd)/$(basename "$1")"
OUTN="$(cd "$(dirname "$2")" && pwd)/$(basename "$2")"
DATA="${CB_DATA:-data}"
SEP="${CB_CPSEP:-:}"
mk() { # <ver> <release> <src> <res> <out>
    local CLS NEST LIBS
    CLS=$(mktemp -d); NEST=$(mktemp -d)
    python3 - "$(find "$DATA"/runtime/client-$1/libraries -name 'fabric-loader-*.jar' | sed -n 1p)" "$NEST" <<'PYNEST'
import os, sys, zipfile
with zipfile.ZipFile(sys.argv[1]) as z:
    for n in z.namelist():
        if n.startswith("META-INF/jars/mixinextras-") and n.endswith(".jar"):
            open(os.path.join(sys.argv[2], os.path.basename(n)), "wb").write(z.read(n))
PYNEST
    LIBS=$(find "$DATA"/runtime/client-$1/libraries -name '*.jar' | tr '\n' "$SEP")$(ls "$NEST"/*.jar | tr '\n' "$SEP")
    javac --release $2 -nowarn -proc:none -cp "$DATA/jars/client-$1-intermediary.jar$SEP$LIBS" -d "$CLS" $(find "$3" -name '*.java')
    cp "$4"/* "$CLS/"
    (cd "$CLS" && jar cf "$5" .)
    rm -rf "$CLS" "$NEST"
    echo "-> $5"
}
mk 1.20.4 17 core/testmods/cb-sl6/src core/testmods/cb-sl6/res "$OUT"
mk 1.21 21 core/testmods/cb-sl6/native/src core/testmods/cb-sl6/native/res "$OUTN"
