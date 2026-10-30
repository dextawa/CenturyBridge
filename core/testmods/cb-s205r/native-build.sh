#!/usr/bin/env bash
# native-build.sh <W> <outDir>: cb-s205rn for W (1.20.5 or 1.21.4), not converted -> <outDir>/cb-s205rn.jar.
# native-1.20.5/ holds the shared sources (Driver, ServerDriverMixin, HolderCodecMixin) and 1.20.5's ApplyMixin
# (RecipeManager.apply); native-1.21.4/ only its own ApplyMixin (scanDirectory), which replaces 1.20.5's.
# From the repository root on beta2; CB_DATA=<data dir>. Boot it with cb-s205r (routes active) and alone (routes idle).
set -euo pipefail
W="$1"; OUTD="$(cd "$2" && pwd)"
DATA="${CB_DATA:-data}"
T=core/testmods/cb-s205r
cls=$(mktemp -d); src=$(mktemp -d)
cp -r $T/native-1.20.5/src/. "$src/"
[ "$W" = 1.20.5 ] || cp -r $T/native-$W/src/. "$src/"
cp="$DATA/jars/client-$W-intermediary.jar"
for j in $(find "$DATA/runtime/client-$W/libraries" -name '*.jar'); do cp="$cp:$j"; done
ME=$(find "$DATA/runtime" -name 'mixinextras-*.jar' | head -1)
[ -n "$ME" ] && cp="$cp:$ME"
javac --release 21 -nowarn -proc:none -cp "$cp" -d "$cls" $(find "$src" -name '*.java')
sed "s/@W@/$W/g" $T/native-1.20.5/res/fabric.mod.json > "$cls/fabric.mod.json"
cp $T/native-1.20.5/res/cb_s205rn.mixins.json "$cls/"
(cd "$cls" && jar cf "$OUTD/cb-s205rn.jar" .)
rm -rf "$cls" "$src"
echo "-> $OUTD/cb-s205rn.jar"
