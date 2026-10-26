#!/usr/bin/env bash
# cb-smx4c: smx4's client checks of the 1.21.3 -> 1.21.4 step that are still valid after the 2026-10-25 audit
# (D:/tmp/main/client_checks/smx4.md: 4, 4c, 6, 7). Two jars:
#   cb-smx4c.jar  a 1.21.3-era mod (intermediary names, no refmap): @Inject HEAD on DynamicTexture / TextureAtlas /
#                 FontTexture.load(ResourceManager) counting per class (4b); an ObjectSelectionList subclass overriding
#                 getMaxScroll / getScrollbarPosition / getScrollAmount and calling the scroll members, with a 1.21.3
#                 @Inject RETURN on AbstractSelectionList.getMaxScroll (6); an AbstractScrollWidget subclass overriding
#                 innerPadding (6); a 1.21.3 @Inject on AbstractWidget.clicked answering false for one button (7c).
#                 Its driver opens the test screen once a world is joined, asks for real input ("input ready"), prints
#                 "CB-SMX4 ok|FAIL ..", then raises a done flag. Climb it one step (CB_FROM=1.21.3, T=1.21.4).
#   cb-smx4n.jar  a native 1.21.4 mod: @Inject HEAD/RETURN and a @Redirect of Map.put in TextureManager.register (4c),
#                 the registered texture map dump (4a, "CB-SMX4 tex <id> <class>"), a 1.21.4 @Inject RETURN on
#                 AbstractWidget.isMouseOver counted inside AbstractWidget.mouseClicked (7b). Its driver waits for the
#                 climbed driver's done flag (or runs alone: the bare 1.21.4 dump to compare with, CB_CLIMB=0), prints
#                 "CB-SMX4 N/T" and stops the client.
#   bash core/testmods/cb-smx4c/build.sh <outDir>   (from the repository root on beta2; CB_DATA=<data dir>)
set -euo pipefail
OUTD="$(cd "$1" && pwd)"
DATA="${CB_DATA:-data}"
build() { # <ver> <src> <res> <jar>
    local cls cp j
    cls=$(mktemp -d)
    cp="$DATA/jars/client-$1-intermediary.jar"
    for j in $(find "$DATA/runtime/client-$1/libraries" -name '*.jar'); do cp="$cp:$j"; done
    javac --release 21 -nowarn -proc:none -cp "$cp" -d "$cls" $(find "$2" -name '*.java')
    cp -r "$3"/. "$cls/"
    (cd "$cls" && jar cf "$OUTD/$4" .)
    rm -rf "$cls"
    echo "-> $OUTD/$4"
}
build 1.21.3 core/testmods/cb-smx4c/src core/testmods/cb-smx4c/res cb-smx4c.jar
build 1.21.4 core/testmods/cb-smx4c/native/src core/testmods/cb-smx4c/native/res cb-smx4n.jar
