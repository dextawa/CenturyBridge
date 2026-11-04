#!/usr/bin/env bash
# cb-smx1d: smx1h's client check of the 1.21.10 -> 1.21.11 step's seven landings (queue_smx1.md, "smx1g handoff").
# smx1k adds: 1.21.10 RenderType subclass (ctor, setup/clear/getName, own override + super), RenderPass.bindSampler with
# GpuTexture sampler state / an atlas view / null (native GlRenderPass.bindTexture HEAD records the sampler), FaceBakery.bakeQuad.
# Two jars:
#   cb-smx1d.jar   a 1.21.10-era client mod (intermediary names): VertexConsumer addVertex(Matrix4f) call / override /
#                  super and the setColor fill, ShapeRenderer writers, CycleButton builder, Options.graphicsMode(),
#                  Realms FileUpload, ClientLevel sky / cloud colours, DebugRenderer / PathfindingRenderer statics.
#                  Climb it: boot_client_remote.sh with CB_FROM=1.21.10, T=1.21.11, a world (CB_WORLD=seed:..).
#   cb-smx1dn.jar  native 1.21.11 driver: @Inject HEAD on VertexConsumer.addVertex(Matrix4fc..), OptionInstance.get/set,
#                  Options.applyGraphicsPreset, CycleButton.Builder.build; judges, prints "CB-SMX1D n/t", stops the
#                  client. CB_NATIVE_MODS on the climbed run.
#   bash core/testmods/cb-smx1d/build.sh <outDir>   (from the repository root on beta2; CB_DATA=<data dir>)
set -euo pipefail
OUTD="$(cd "$1" && pwd)"
DATA="${CB_DATA:-data}"
JDK="${CB_JDK:-$HOME/cbtest/jdk-25.0.4.1+1/bin}"
build() { # <stub jar> <runtime dir> <release> <src> <res> <jar>
    local cls cp j
    cls=$(mktemp -d)
    cp="$1"
    for j in $(find "$2/libraries" -name '*.jar'); do cp="$cp:$j"; done
    "$JDK/javac" --release "$3" -nowarn -proc:none -cp "$cp" -d "$cls" $(find "$4" -name '*.java')
    cp -r "$5"/. "$cls/"
    (cd "$cls" && "$JDK/jar" cf "$OUTD/$6" .)
    rm -rf "$cls"
    echo "-> $OUTD/$6"
}
build "$DATA/jars/client-1.21.10-intermediary.jar" "$DATA/runtime/client-1.21.10" 21 \
    core/testmods/cb-smx1d/src core/testmods/cb-smx1d/res cb-smx1d.jar
build "$DATA/jars/client-1.21.11-intermediary.jar" "$DATA/runtime/client-1.21.11" 21 \
    core/testmods/cb-smx1d/native/src core/testmods/cb-smx1d/native/res cb-smx1dn.jar
