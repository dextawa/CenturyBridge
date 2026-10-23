#!/usr/bin/env bash
# make_aw_stub.sh <mcVersion> <stubIn> <stubOut> [<ownAw>]
# The compile stub with the access the shim tree will have in the game: every Fabric API
# module's access widener (they apply to the whole game at runtime, transitive or not)
# plus CenturyBridge's own, applied to <stubIn>. The engine must be compiled first.
# <ownAw> defaults to the anchor tree's core/shims/<ver>/resources/cb.accesswidener; a climb
# step's layer passes its cb-step-<W>.accesswidener (build.sh does this itself, see there).
# CB_DATA: the working-set directory (default data); CB_FAPI_JAR: the Fabric API fat jar.
#   core/tools/make_aw_stub.sh 26.2 data/jars/client-26.2.jar data/jars/client-26.2-aw.jar
#   core/tools/make_aw_stub.sh 1.21.4 data/jars/client-1.21.4-intermediary.jar data/jars/client-1.21.4-intermediary-aw.jar
set -euo pipefail
VERSION="$1"; IN="$2"; OUT="$3"
OWN_AW="${4:-core/shims/$VERSION/resources/cb.accesswidener}"
DATA="${CB_DATA:-data}"
FAPI="${CB_FAPI_JAR:-$(ls "$DATA"/runtime/client-$VERSION/mods/fabric-api-*.jar | head -1)}"
[ -f "$OWN_AW" ] || { echo "access widener not found: $OWN_AW"; exit 1; }
WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT
unzip -q -o -d "$WORK/fapi" "$FAPI" 'META-INF/jars/*'
AWS=()
for j in "$WORK"/fapi/META-INF/jars/*.jar; do
    aw=$(unzip -p "$j" fabric.mod.json 2>/dev/null | python3 -c 'import json,sys
d=json.loads(sys.stdin.read(),strict=False)
print(d.get("accessWidener") or d.get("classTweaker") or "")' 2>/dev/null || true)
    if [ -n "$aw" ]; then
        f="$WORK/$(basename "$j" .jar).aw"
        unzip -p "$j" "$aw" > "$f"
        AWS+=("$f")
    fi
done
AWS+=("$OWN_AW")
SEP=":"; case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=";";; esac
LIBS=$(find "$DATA"/runtime/client-*/libraries/org/ow2/asm "$DATA"/runtime/client-*/libraries/com/google/code/gson -name '*.jar' 2>/dev/null | sort -u | tr '\n' "$SEP")
echo "applying ${#AWS[@]} access wideners (fabric-api modules + $(basename "$OWN_AW")) to $IN"
java -cp "core/build/classes$SEP$LIBS" top.dext.centurybridge.Main apply-aw "$IN" "$OUT" "${AWS[@]}"
