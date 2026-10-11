#!/usr/bin/env bash
# make_aw_stub.sh <mcVersion> <stubIn> <stubOut>
# The compile stub with the access the shim tree will have in the game: every Fabric API
# module's access widener (they apply to the whole game at runtime, transitive or not)
# plus CenturyBridge's own, applied to <stubIn>. The engine must be compiled first.
#   core/tools/make_aw_stub.sh 26.2 data/jars/client-26.2.jar data/jars/client-26.2-aw.jar
#   core/tools/make_aw_stub.sh 1.21.4 data/jars/client-1.21.4-intermediary.jar data/jars/client-1.21.4-intermediary-aw.jar
set -euo pipefail
VERSION="$1"; IN="$2"; OUT="$3"
FAPI="${CB_FAPI_JAR:-$(ls data/runtime/client-$VERSION/mods/fabric-api-*.jar | head -1)}"
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
AWS+=("core/shims/$VERSION/resources/cb.accesswidener")
SEP=":"; case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=";";; esac
LIBS=$(find data/runtime/client-*/libraries/org/ow2/asm data/runtime/client-*/libraries/com/google/code/gson -name '*.jar' 2>/dev/null | sort -u | tr '\n' "$SEP")
echo "applying ${#AWS[@]} access wideners (fabric-api modules + CenturyBridge) to $IN"
java -cp "core/build/classes$SEP$LIBS" top.dext.centurybridge.Main apply-aw "$IN" "$OUT" "${AWS[@]}"
