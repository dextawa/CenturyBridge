#!/usr/bin/env bash
# Build a CenturyBridge mod jar for one Minecraft version.
#
#   ./build.sh 1.20.4 [outDir]
#
# Needs an intermediary-namespace stub for that version plus the Fabric loader
# libraries; both come from a local runtime the repo deliberately does not
# carry (game jars are not ours to distribute). Point CB_STUB and CB_LIBS at
# them, or let the script find them under data/ if you have a working tree.
set -euo pipefail

VERSION="${1:?usage: build.sh <mcVersion> [outDir]}"
OUT="${2:-build}"
SHIM="core/shims/$VERSION"
[ -d "$SHIM" ] || { echo "no shim tree for $VERSION"; exit 1; }

STUB="${CB_STUB:-data/jars/client-$VERSION-intermediary.jar}"
[ -f "$STUB" ] || { echo "stub not found: $STUB (set CB_STUB)"; exit 1; }

# Loader-side deps the shims compile against: mixin, and whatever the game
# itself pulls in (gson, datafixerupper, joml, netty...).
LIBS="${CB_LIBS:-data/runtime/client-$VERSION/libraries}"
CP="$STUB"
if [ -d "$LIBS" ]; then
    while IFS= read -r jar; do CP="$CP:$jar"; done < <(find "$LIBS" -name '*.jar')
fi
for extra in $(find data/runtime -name 'sponge-mixin-*.jar' 2>/dev/null | head -1); do
    CP="$CP:$extra"
done
# Windows toolchains want ';' between classpath entries
case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) CP="${CP//:/;}";; esac

CLASSES="$OUT/classes-$VERSION"
rm -rf "$CLASSES"
mkdir -p "$CLASSES" "$OUT"

echo "compiling $VERSION shims..."
find "$SHIM/src" -name '*.java' > "$OUT/sources-$VERSION.txt"
javac -nowarn -proc:none -cp "$CP" -d "$CLASSES" "@$OUT/sources-$VERSION.txt"

# fabric.mod.json, the mixin config and the access widener are mod SOURCE and
# live under resources/; they are copied in at package time, not compiled.
cp "$SHIM"/resources/* "$CLASSES"/

JAR="$OUT/centurybridge-0.3.0+$VERSION.jar"
jar cf "$JAR" -C "$CLASSES" .
echo "-> $JAR"
