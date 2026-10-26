#!/usr/bin/env bash
# cb-s9c: s9's client checks of the 1.21.8 -> 1.21.9 step (D:/tmp/main/client_checks/s9.md, items 2, 3, 4b/4c and
# batch items 1 and 9): a 1.21.8-era client mod (intermediary names, no refmap; its access widener opens
# EntityRenderers.register / BlockEntityRenderers.register) with
#   - a LivingEntityRenderer subclass for zombies (HumanoidModel, render override: super.render, then a quad one
#     block above the entity through buffers.getBuffer(RenderType.entityCutout(..))) and a RenderLayer that draws the
#     model again translucent (s9 2a-c); an entity drawn into a screen through EntityRenderDispatcher.render with the
#     game's buffer source (2d);
#   - block entity renderers: (i) a BlockEntityRenderer for barrels (a cube turning with partialTick and game time,
#     ShapeRenderer.renderLineBox around it, the held item through ItemRenderer.renderStatic), (ii) a ChestRenderer
#     subclass (super.render, then a quad), (iii) a lambda for furnaces (s9 3a; crack overlay 3b by eye);
#   - skins (4b offline default, 4c PlayerSkin record), RenderTarget.viewWidth/viewHeight (batch 9).
# Once a world is joined it spawns a client-side zombie, places the blocks, screenshots day / night / glowing, prints
# "CB-S9C ok|FAIL <check> <detail>" and "CB-S9C N/T", then stops the client. Climb it one step (CB_FROM=1.21.8,
# T=1.21.9).
#   bash core/testmods/cb-s9c/build.sh <out.jar>   (from the repository root on beta2; CB_DATA=<data dir>)
# CB_STUB=<jar>: the compile stub with the mod's access widener applied (the two register methods it opens), e.g.
#   core/tools/make_aw_stub.sh 1.21.8 data/jars/client-1.21.8-intermediary.jar <out.jar> core/testmods/cb-s9c/res/cb_s9c.accesswidener
set -euo pipefail
OUT="$1"
JAR="$(cd "$(dirname "$OUT")" && pwd)/$(basename "$OUT")"
CLS=$(mktemp -d)
DATA="${CB_DATA:-data}"
RT="$DATA/runtime/client-1.21.8"
CP="${CB_STUB:-$DATA/jars/client-1.21.8-intermediary.jar}"
for j in $(find "$RT/libraries" -name '*.jar'); do CP="$CP:$j"; done
javac --release 21 -nowarn -proc:none -cp "$CP" -d "$CLS" $(find core/testmods/cb-s9c/src -name '*.java')
cp -r core/testmods/cb-s9c/res/. "$CLS/"
(cd "$CLS" && jar cf "$JAR" .)
rm -rf "$CLS"
echo "-> $OUT"
