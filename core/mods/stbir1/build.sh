#!/usr/bin/env bash
# build.sh <outDir>: the Fabric mod centurybridge-stbir1 (LWJGL 3.3.3's STBImageResize, stb_image_resize v0.97, with its
# own natives) as <outDir>/centurybridge-stbir1-<version>.jar. Run from the repository root.
#
# - Java: src/ compiled by plain javac --release 21 against LWJGL 3.4.1's core jar only (26.1's; the class links nothing
#   else: org.lwjgl.system Library / Platform / MemoryUtil / Checks). CB_LWJGL overrides the jar, CB_DATA the data dir.
# - Natives: natives/ verbatim (the reproducible build of core/natives/stbir1/build.sh, sha256s in its SHA256SUMS; this
#   script checks them against it), at the classpath path LWJGL's Library.loadSystem reads ({os}/{arch}/...).
# - Resources: resources/ (fabric.mod.json, licences under META-INF/).
# - Reproducible: entries sorted, fixed timestamps, no manifest timestamp; the same inputs give the same jar.
set -euo pipefail
OUT="${1:?usage: core/mods/stbir1/build.sh <outDir>}"
HERE=core/mods/stbir1
DATA="${CB_DATA:-data}"
LWJGL="${CB_LWJGL:-$DATA/runtime/client-26.1/libraries/org/lwjgl/lwjgl/3.4.1/lwjgl-3.4.1.jar}"
[ -f "$LWJGL" ] || { echo "LWJGL 3.4.1 jar not found: $LWJGL (set CB_LWJGL)"; exit 1; }
VERSION=$(python3 -c "import json; print(json.load(open('$HERE/resources/fabric.mod.json', encoding='utf-8'))['version'])")

# the natives are the ones core/natives/stbir1 records
(cd "$HERE/natives" && grep -vE '  (stb_image_resize\.h|cb_stbir1\.c|cb_stbir1_x86\.def|THIRD-PARTY\.txt)$' \
    ../../../natives/stbir1/SHA256SUMS | sha256sum -c --quiet -) || { echo "natives differ from core/natives/stbir1/SHA256SUMS"; exit 1; }
NLIBS=$(grep -cvE '  (stb_image_resize\.h|cb_stbir1\.c|cb_stbir1_x86\.def|THIRD-PARTY\.txt)$' core/natives/stbir1/SHA256SUMS)
[ "$(find "$HERE/natives" -type f ! -name 'THIRD-PARTY-*' | wc -l)" = "$NLIBS" ] || { echo "natives/ holds files SHA256SUMS does not list"; exit 1; }

STAGE="$OUT/stbir1-classes"
rm -rf "$STAGE" && mkdir -p "$STAGE"
SEP=":"
case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=";";; esac
javac -nowarn -proc:none --release 21 -cp "$LWJGL" -d "$STAGE" $(find "$HERE/src" -name '*.java')
cp -r "$HERE/resources/." "$STAGE/"
cp -r "$HERE/natives/." "$STAGE/"
JAR="$OUT/centurybridge-stbir1-$VERSION.jar"
python3 - "$STAGE" "$JAR" <<'PY'
import os, sys, zipfile
stage, jar = sys.argv[1], sys.argv[2]
files = sorted(os.path.relpath(os.path.join(d, f), stage).replace(os.sep, "/")
               for d, _, fs in os.walk(stage) for f in fs)
with zipfile.ZipFile(jar, "w", zipfile.ZIP_DEFLATED) as z:
    z.writestr(zipfile.ZipInfo("META-INF/MANIFEST.MF", (1980, 1, 1, 0, 0, 0)), "Manifest-Version: 1.0\r\n\r\n")
    for f in files:
        info = zipfile.ZipInfo(f, (1980, 1, 1, 0, 0, 0))
        info.compress_type = zipfile.ZIP_DEFLATED
        info.external_attr = 0o644 << 16
        z.writestr(info, open(os.path.join(stage, f), "rb").read())
PY
rm -rf "$STAGE"
echo "-> $JAR ($(wc -c < "$JAR") bytes, sha256 $(sha256sum "$JAR" | cut -c1-64))"
