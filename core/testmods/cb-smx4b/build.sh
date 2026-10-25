#!/usr/bin/env bash
# cb-smx4b: a 1.20.4-era mod (intermediary names, no refmap, no fabric-api) whose mixins name 1.20.4 members that step
# 1.20.4 -> 1.20.5 restores and routes 1.20.5's own path through when a converted mod mixes them (CbMixedMembers):
# Arrow.setEffectsFromItem (a cancellable HEAD injector), Inventory.hurtArmor, Piglin.makeSound,
# LivingEntity.broadcastBreakEvent(hand), Fireball / ThrowableItemProjectile.getItemRaw. ServerDriverMixin runs Driver
# once the world is loaded: "CB-SMX4B ok|FAIL <case> <detail>", ending "CB-SMX4B summary ok=N fail=M". native/ is
# cb-smx4bn, a 1.20.5 mod (not converted) with mixins on the 1.20.5 methods those routes go through.
#
#   bash core/testmods/cb-smx4b/build.sh <out.jar>     (from the repository root; javac only;
#   CB_DATA=<data dir> when it is not ./data, CB_CPSEP=";" on Windows)
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
MIXIN="${CB_MIXIN:-$DATA/runtime/client-1.20.4/libraries/net/fabricmc/sponge-mixin/0.17.3+mixin.0.8.7/sponge-mixin-0.17.3+mixin.0.8.7.jar}"
[ -f "$MIXIN" ] || MIXIN=$(find "$DATA"/runtime/client-1.20.4/libraries -name 'sponge-mixin-*.jar' | head -1)
LIBS=$(find "$DATA"/runtime/client-1.20.4/libraries -name '*.jar' ! -name 'sponge-mixin-*' | tr '\n' "${CB_CPSEP:-:}")
javac --release 17 -nowarn -proc:none -cp "$DATA/jars/client-1.20.4-intermediary.jar${CB_CPSEP:-:}$MIXIN${CB_CPSEP:-:}$LIBS" -d "$CLS" \
    $(find core/testmods/cb-smx4b/src -name '*.java')
cp core/testmods/cb-smx4b/res/* "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
