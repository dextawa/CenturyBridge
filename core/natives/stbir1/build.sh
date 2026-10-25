#!/usr/bin/env bash
# build.sh <toolchainDir> <outDir> [--record]: reproducible build of cb_stbir1, CenturyBridge's copy of stb_image_resize v0.97
# (the natives of the Fabric mod centurybridge-stbir1, core/mods/stbir1; see cb_stbir1.c and its STBImageResize class).
# Run on beta2 only (linux x86_64), from the repository root:
#
#   bash core/natives/stbir1/build.sh ~/cbwork/agents/<name>/toolchain core/mods/stbir1/natives
#
# - The toolchain is zig $ZIG_VERSION, downloaded into <toolchainDir> once and checked against ziglang.org's published
#   sha256 (https://ziglang.org/download/index.json); it never enters the repository.
# - The sources are checked against SHA256SUMS before building: stb_image_resize.h must be byte-identical to the copy
#   LWJGL 3.3.3 builds into lwjgl_stb (github.com/LWJGL/lwjgl3, tag 3.3.3, modules/lwjgl/stb/src/main/c/stb_image_resize.h).
# - One library per platform 26.1 ships LWJGL natives for (Mojang's 26.1 version JSON: linux x64, macos x64 / arm64,
#   windows x64 / x86 / arm64), plus linux arm64 (not in Mojang's JSON; launchers that run the game there supply
#   LWJGL's own linux-arm64 natives, whose Platform maps it to linux/arm64), at the classpath path LWJGL's Library.loadSystem looks for
#   (<os>/<arch>/top/dext/centurybridge/stbir1/<platform library name>). The library is named cb_lwjgl_stbir1 because
#   loadSystem searches the per-platform path only for names containing "lwjgl" (any other name only at the jar root,
#   where the macOS and Windows builds would collide).
# - Deterministic: no debug info, stripped, no build id, no timestamp, source paths not embedded; every target is built
#   twice into separate directories and the two must be byte-identical. The outputs' sha256s are compared with
#   SHA256SUMS (--record writes them there instead, for a deliberate change of sources or flags).
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
TC="$1"; OUT="$2"; RECORD="${3:-}"
ZIG_VERSION=0.13.0
ZIG_TARBALL="zig-linux-x86_64-$ZIG_VERSION.tar.xz"
ZIG_SHA256=d45312e61ebcc48032b77bc4cf7fd6915c11fa16e4aad116b66c9468211230ea

mkdir -p "$TC"
ZIG="$TC/zig-linux-x86_64-$ZIG_VERSION/zig"
if [ ! -x "$ZIG" ]; then
    curl -sfL -o "$TC/$ZIG_TARBALL" "https://ziglang.org/download/$ZIG_VERSION/$ZIG_TARBALL"
    echo "$ZIG_SHA256  $TC/$ZIG_TARBALL" | sha256sum -c --quiet - || { echo "zig tarball checksum mismatch"; exit 1; }
    tar -xJf "$TC/$ZIG_TARBALL" -C "$TC"
    rm "$TC/$ZIG_TARBALL"
fi
[ "$("$ZIG" version)" = "$ZIG_VERSION" ] || { echo "unexpected zig version"; exit 1; }
export ZIG_GLOBAL_CACHE_DIR="$TC/cache" ZIG_LOCAL_CACHE_DIR="$TC/cache"

# sources as recorded
(cd "$HERE" && grep -E '  (stb_image_resize\.h|cb_stbir1\.c|cb_stbir1_x86\.def|THIRD-PARTY\.txt)$' SHA256SUMS | sha256sum -c --quiet -) \
    || { echo "source checksum mismatch (SHA256SUMS)"; exit 1; }

# zig target, LWJGL os/arch, library file name
TARGETS="x86_64-linux-gnu.2.17 linux/x64 libcb_lwjgl_stbir1.so
aarch64-linux-gnu.2.17 linux/arm64 libcb_lwjgl_stbir1.so
x86_64-macos.10.15 macos/x64 libcb_lwjgl_stbir1.dylib
aarch64-macos.11.0 macos/arm64 libcb_lwjgl_stbir1.dylib
x86_64-windows-gnu windows/x64 cb_lwjgl_stbir1.dll
x86-windows-gnu windows/x86 cb_lwjgl_stbir1.dll
aarch64-windows-gnu windows/arm64 cb_lwjgl_stbir1.dll"
REL=top/dext/centurybridge/stbir1
COMMON=(-O2 -shared -fPIC -fvisibility=hidden -g0 -s -w -fno-ident -ffile-prefix-map="$HERE"=. -I"$HERE")

build_one() { # <target> <out file>
    local target="$1" file="$2" extra=()
    case "$target" in
        *linux*) extra=(-Wl,--build-id=none) ;;
        *macos*) extra=(-Wl,-install_name,"$(basename "$file")") ;;  # else the install name (and the UUID) carry the build path
        x86-windows*) extra=(cb_stbir1_x86.def) ;;  # the JNI exports under plain names (see cb_stbir1.c)
    esac
    mkdir -p "$(dirname "$file")"
    (cd "$HERE" && "$ZIG" cc -target "$target" "${COMMON[@]}" "${extra[@]}" -o "$file" cb_stbir1.c)
    rm -f "$(dirname "$file")"/*.lib "$(dirname "$file")"/*.pdb   # the import library is named after the source file
    case "$target" in
        # zig 0.13's linker stamps the link time into the PE file header and takes no option against it: set it to 0
        *windows*) python3 - "$file" <<'PY'
import struct, sys
p = sys.argv[1]
b = bytearray(open(p, "rb").read())
pe = struct.unpack_from("<I", b, 0x3C)[0]
assert b[pe:pe + 4] == b"PE\0\0"
struct.pack_into("<I", b, pe + 8, 0)  # IMAGE_FILE_HEADER.TimeDateStamp
open(p, "wb").write(b)
PY
        ;;
    esac
}

WORK="$TC/work"
rm -rf "$WORK" && mkdir -p "$WORK/a" "$WORK/b"
SUMS=""
while read -r target osarch name; do
    build_one "$target" "$WORK/a/$osarch/$REL/$name"
    rm -rf "$TC/cache"   # the second build must not reuse the first one's cache
    build_one "$target" "$WORK/b/$osarch/$REL/$name"
    cmp -s "$WORK/a/$osarch/$REL/$name" "$WORK/b/$osarch/$REL/$name" || { echo "$osarch: two builds differ"; exit 1; }
    SUMS+="$(cd "$WORK/a" && sha256sum "$osarch/$REL/$name")"$'\n'
done <<< "$TARGETS"

if [ "$RECORD" = "--record" ]; then
    { grep -E '  (stb_image_resize\.h|cb_stbir1\.c|cb_stbir1_x86\.def|THIRD-PARTY\.txt)$' "$HERE/SHA256SUMS"; printf '%s' "$SUMS"; } > "$HERE/SHA256SUMS.new"
    mv "$HERE/SHA256SUMS.new" "$HERE/SHA256SUMS"
else
    diff <(printf '%s' "$SUMS") <(grep -vE '  (stb_image_resize\.h|cb_stbir1\.c|cb_stbir1_x86\.def|THIRD-PARTY\.txt)$' "$HERE/SHA256SUMS") \
        || { echo "outputs differ from SHA256SUMS"; exit 1; }
fi
rm -rf "$OUT" && mkdir -p "$OUT" && cp -r "$WORK/a/." "$OUT/"
cp "$HERE/THIRD-PARTY.txt" "$OUT/THIRD-PARTY-cb_stbir1.txt"   # the licence notice travels with the libraries
rm -rf "$WORK"
echo "cb_stbir1: $(printf '%s' "$SUMS" | wc -l) libraries -> $OUT (each built twice, byte-identical)"
