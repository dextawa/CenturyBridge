#!/usr/bin/env bash
# cb-s205: s205's checks of the 1.20.4 -> 1.20.5 step and the 1.20.4-era pieces later layers give back. Two jars:
#   cb-s205.jar   a 1.20.4-era mod (intermediary names, no refmap): resource-condition recipes (server), the dropped core
#                 shaders, cycleEffect, and a client mixin on 1.20.4's private GameRenderer.loadEffect (cb.s205.mixin,
#                 "CB-S205C ..."). Climb it with CB_FROM=1.20.4 (boot_climb.sh / boot_client_remote.sh).
#   cb-s205n.jar  a native 1.21.2 client mod: @WrapOperation of checkEntityPostEffect's setPostEffect calls and HEAD on
#                 setPostEffect, beside the 1.21.2 layer's loadEffect route. Run it as CB_NATIVE_MODS beside the climbed
#                 cb-s205 (whose driver reads it), and with the layers alone (its own driver: "CB-S205N ...").
#   bash core/testmods/cb-s205/build.sh <outDir>   (from the repository root on beta2; CB_DATA=<data dir>)
set -euo pipefail
OUTD="$(cd "$1" && pwd)"
DATA="${CB_DATA:-data}"
build() { # <ver> <release> <src> <res> <jar>
    local cls cp j mx
    cls=$(mktemp -d)
    cp="$DATA/jars/client-$1-intermediary.jar"
    for j in $(find "$DATA/runtime/client-$1/libraries" "$DATA/runtime/client-$1/.fabric/processedMods" -name '*.jar'); do cp="$cp:$j"; done
    case "$cp" in *mixinextras*) ;; *)
        # MixinExtras: nested in Fabric Loader's jar (as build.sh finds it)
        mx="$cls.mx"
        j="$(cd "$(dirname "$(find "$DATA/runtime/client-$1/libraries" -name 'fabric-loader-*.jar' | sed -n 1p)")" && pwd)"
        j="$j/$(basename "$(find "$DATA/runtime/client-$1/libraries" -name 'fabric-loader-*.jar' | sed -n 1p)")"
        mkdir -p "$mx"
        (cd "$mx" && unzip -qo "$j" 'META-INF/jars/mixinextras-*.jar')
        cp="$cp:$(ls "$mx"/META-INF/jars/mixinextras-*.jar | sed -n 1p)";;
    esac
    javac --release "$2" -nowarn -proc:none -cp "$cp" -d "$cls" $(find "$3" -name '*.java')
    cp -r "$4"/. "$cls/"
    (cd "$cls" && jar cf "$OUTD/$5" .)
    rm -rf "$cls" "$cls.mx"
    echo "-> $OUTD/$5"
}
build 1.20.4 17 core/testmods/cb-s205/src core/testmods/cb-s205/res cb-s205.jar
build 1.21.2 21 core/testmods/cb-s205/native/src core/testmods/cb-s205/native/res cb-s205n.jar
