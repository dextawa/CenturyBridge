#!/usr/bin/env bash
# cb-s9r: client checks of the 1.21.8 -> 1.21.9 step's two routes (selection-list isSelectedItem, AbstractWidget
# mouseClicked), per brief_mixin_step.md "routes never bypass native W mixins". Two jars:
#   cb-s9r.jar   a 1.21.8-era mod (intermediary names, no refmap): an ObjectSelectionList subclass overriding
#                isSelectedItem(index) (true for index 2, recording what it is asked), and a 1.21.8 @Inject HEAD on
#                AbstractWidget.mouseClicked(x, y, button) answering false for the button labelled "blocked". Its
#                screen holds that list and two buttons ("keep", "blocked"); ArmorCheck bakes 1.21.8's inner / outer
#                armor layers (ModelLayers.*_INNER_ARMOR / _OUTER_ARMOR). Climb it one step (CB_FROM=1.21.8).
#   cb-s9rn.jar  a native 1.21.9 mod: a @ModifyExpressionValue on entriesCanBeSelected() and an @Inject HEAD on
#                renderSelection(graphics, entry, color) in AbstractSelectionList.renderItem, an @Inject HEAD on
#                AbstractWidget.mouseClicked(MouseButtonEvent, doubleClick); its driver opens the climbed mod's screen
#                from the title screen, clicks both buttons through vanilla's Screen.mouseClicked, prints
#                compares the baked armor layers with 1.21.9's slot layers (C.*), prints
#                "CB-S9R ok|FAIL ..", "CB-S9R N/T" and stops the client.
#   bash core/testmods/cb-s9r/build.sh <outDir>   (from the repository root on beta2, engine compiled; CB_DATA=<data dir>)
#   A jar whose fabric.mod.json names an accessWidener compiles against the stub with it applied (Main apply-aw).
set -euo pipefail
OUTD="$(cd "$1" && pwd)"
DATA="$(cd "${CB_DATA:-data}" && pwd)"
build() { # <ver> <src> <res> <jar>
    local cls cp j mx loader
    cls=$(mktemp -d)
    cp="$DATA/jars/client-$1-intermediary.jar"
    # the mod's own access widener (fabric.mod.json "accessWidener"), applied to the stub as the loader applies it
    local aw
    aw=$(python3 -c 'import json,sys; print(json.load(open(sys.argv[1])).get("accessWidener",""))' "$3/fabric.mod.json")
    if [ -n "$aw" ]; then
        local libs
        libs=$(find "$DATA"/runtime/client-1.20.4/libraries/org/ow2/asm "$DATA"/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '
' ':')
        java -cp "core/build/classes:$libs" top.dext.centurybridge.Main apply-aw "$cp" "$cls.stub.jar" "$3/$aw" > /dev/null
        cp="$cls.stub.jar"
    fi
    for j in $(find "$DATA/runtime/client-$1/libraries" -name '*.jar'); do cp="$cp:$j"; done
    # MixinExtras: nested in Fabric Loader (META-INF/jars), provided to every mod at run time
    mx=$(mktemp -d)
    loader=$(find "$DATA/runtime/client-$1/libraries" -name 'fabric-loader-*.jar' | head -1)
    unzip -qo "$loader" "META-INF/jars/*" -d "$mx" || true
    for j in $(find "$mx" -name 'mixinextras*.jar'); do cp="$cp:$j"; done
    javac --release 21 -nowarn -proc:none -cp "$cp" -d "$cls" $(find "$2" -name '*.java')
    cp -r "$3"/. "$cls/"
    (cd "$cls" && jar cf "$OUTD/$4" .)
    rm -rf "$cls" "$cls.stub.jar" "$mx"
    echo "-> $OUTD/$4"
}
build 1.21.8 core/testmods/cb-s9r/src core/testmods/cb-s9r/res cb-s9r.jar
build 1.21.9 core/testmods/cb-s9r/native/src core/testmods/cb-s9r/native/res cb-s9rn.jar
