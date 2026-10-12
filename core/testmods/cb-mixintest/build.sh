#!/usr/bin/env bash
# cb-mixintest: a 1.20.1-era mod (intermediary names, no fabric-api) whose mixins aim at members the
# climb's steps 1.20.1 -> 1.20.2 and 1.20.2 -> 1.20.3 reshape, rename or redirect: @Invoker (instance /
# static, redirected, reshaped, renamed onto a superclass), @Accessor (field getter, renamed, static
# renamed), @Shadow methods and fields (redirected, reshaped, renamed onto a superclass, renamed in
# place, field getter), injector selectors through override hooks (argument map, wrap converter, a
# narrowed parameter type, a void method that answers a value), and @At anchors the steps move (a
# hook's new shape, a reshaped call, a rename under @Redirect). The refmap maps Mojang-style source
# names to intermediary like an annotation processor's would.
#
# ServerDriverMixin runs Driver at the end of MinecraftServer.loadLevel: every case logs
# "CB-MIXINTEST ok|FAIL <case> <detail>", every handler "CB-MIXINTEST ran <case> <values>", and the
# run ends with "CB-MIXINTEST summary ok=N fail=M". Native on a 1.20.1 server it is the reference; the
# climbed jar on 1.20.2 / 1.20.3 servers is the test.
#
#   bash core/testmods/cb-mixintest/build.sh <out.jar>     (from the repository root; javac only;
#   CB_DATA=<data dir> when it is not ./data, CB_CPSEP=";" on Windows)
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d) # not TMP: on Windows that names the JVM's own temp directory
DATA="${CB_DATA:-data}"
# Mixin's annotations as 1.20.1-era mods were compiled against them: sponge-mixin 0.17.4 turned
# @Redirect's "at" into an array, which the 0.17.3 a Fabric 1.20.x server runs (and MixinExtras)
# reads as one @At
MIXIN="${CB_MIXIN:-$DATA/runtime/client-1.20.3/libraries/net/fabricmc/sponge-mixin/0.17.3+mixin.0.8.7/sponge-mixin-0.17.3+mixin.0.8.7.jar}"
LIBS=$(find "$DATA"/runtime/client-1.20.1/libraries -name '*.jar' ! -name 'sponge-mixin-*' | tr '\n' "${CB_CPSEP:-:}")
javac --release 17 -nowarn -proc:none -cp "$DATA/jars/client-1.20.1-intermediary.jar${CB_CPSEP:-:}$MIXIN${CB_CPSEP:-:}$LIBS" -d "$CLS" \
    $(find core/testmods/cb-mixintest/src -name '*.java')
cp core/testmods/cb-mixintest/res/* "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
