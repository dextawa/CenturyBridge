#!/usr/bin/env bash
# cb-s6c: s6b's client checks of the 1.21.5 -> 1.21.6 step (D:/tmp/main/client_checks/s6b.md, items 0, 1a, 1b). Two jars:
#   cb-s6c.jar  a 1.21.5-era client mod (intermediary names, no refmap) depending on fabric-rendering-v1,
#               fabric-keybindings-v0 and fabric-client-tags-api-v1 (the last two are gone from 1.21.6's Fabric API):
#               HUD layers (add / attachAfter / attachBefore / remove / replace, duplicate id, missing anchor),
#               Font.drawInBatch in a world render callback, a FabricKeyBinding pressed for real, ClientTags.isInLocal,
#               MultilineTextField / MultiLineEditBox / ToastManager, and a test screen in three modes
#               (GuiGraphics endPortal fill, flush, renderItem with z, positioned tooltip, SplashRenderer masks;
#               renderBlurredBackground + setTooltipForNextRenderPass; PANORAMA + clearTooltipForNextRenderPass;
#               s6h: RenderSystem.enableScissor / disableScissor around GuiGraphics fills, judged on the framebuffer's
#               pixels), with screenshots; prints "CB-S6C ok|FAIL ..", "CB-S6C N/T", then stops the client.
#               Climb it one step: boot_client_remote.sh with CB_FROM=1.21.5, T=1.21.6.
#   cb-s6n.jar  a native 1.21.6 mod: @Inject(HEAD) on Gui.<init>, GuiGraphics.renderDeferredTooltip, GuiGraphics.enableScissor /
#               disableScissor and GuiGraphics.ScissorStack.peek, counted in
#               system properties the climbed driver reports (the native-W check). CB_NATIVE_MODS on the climbed run.
# Not covered: the rendering-data-attachment chunk-region check (needs a custom block model).
#   bash core/testmods/cb-s6c/build.sh <outDir>   (from the repository root on beta2; CB_DATA=<data dir>)
set -euo pipefail
OUTD="$(cd "$1" && pwd)"
DATA="${CB_DATA:-data}"
build() { # <ver> <src> <res> <jar> [extra classpath]
    local cls cp j
    cls=$(mktemp -d)
    cp="$DATA/jars/client-$1-intermediary.jar${5:+:$5}"
    for j in $(find "$DATA/runtime/client-$1/libraries" -name '*.jar'); do cp="$cp:$j"; done
    javac --release 21 -nowarn -proc:none -cp "$cp" -d "$cls" $(find "$2" -name '*.java')
    cp -r "$3"/. "$cls/"
    (cd "$cls" && jar cf "$OUTD/$4" .)
    rm -rf "$cls"
    echo "-> $OUTD/$4"
}
# 1.21.5's Fabric API modules (jar-in-jar) as the compile classpath
FAPI=$(mktemp -d)
FJAR="$(cd "$(dirname "$(ls "$DATA"/runtime/client-1.21.5/mods/fabric-api-*.jar | head -1)")" && pwd)/$(basename "$(ls "$DATA"/runtime/client-1.21.5/mods/fabric-api-*.jar | head -1)")"
(cd "$FAPI" && unzip -q "$FJAR" 'META-INF/jars/*')
FCP=$(find "$FAPI/META-INF/jars" -name '*.jar' | tr '\n' ':')
build 1.21.5 core/testmods/cb-s6c/src core/testmods/cb-s6c/res cb-s6c.jar "${FCP%:}"
build 1.21.6 core/testmods/cb-s6c/native/src core/testmods/cb-s6c/native/res cb-s6n.jar
rm -rf "$FAPI"
