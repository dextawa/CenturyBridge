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

# A mixin listed in the config but missing from src is fatal at PREPARE, before
# a single class loads -- worse than a compile error, because the build looks
# clean and the game dies on launch. Catch it here.
python3 - "$SHIM" <<'PYGATE'
import json, os, sys, glob
shim = sys.argv[1]
cfgs = glob.glob(os.path.join(shim, "resources", "*.mixins.json"))
if cfgs:
    cfg = json.load(open(cfgs[0], encoding="utf-8"))
    pkg = cfg["package"].replace(".", "/")
    srcdir = os.path.join(shim, "src", pkg)
    present = {f[:-5] for f in os.listdir(srcdir) if f.endswith(".java")}
    ghosts = [m for side in ("mixins", "client", "server")
              for m in cfg.get(side, []) if m not in present]
    if ghosts:
        print(f"mixin config lists {len(ghosts)} classes with no source: "
              f"{ghosts[:5]}{'...' if len(ghosts) > 5 else ''}")
        sys.exit(1)

    # A class mixin whose target is an interface fails at PREPARE and takes the
    # whole game with it, so refuse to package one.
    import re, struct, zipfile
    stub = os.environ.get("CB_STUB", "")
    if stub and os.path.exists(stub):
        ifaces = set()
        with zipfile.ZipFile(stub) as z:
            for n in z.namelist():
                if not n.endswith(".class") or not n.startswith("net/minecraft/"):
                    continue
                d = z.read(n)
                idx, cnt, i = 10, struct.unpack(">H", d[8:10])[0], 1
                sz = {7:3, 8:3, 16:3, 19:3, 20:3, 15:4, 9:5, 10:5, 11:5,
                      12:5, 17:5, 18:5, 3:5, 4:5, 5:9, 6:9}
                while i < cnt:
                    t = d[idx]
                    if t in (5, 6):
                        idx += 9; i += 2; continue
                    idx += 3 + struct.unpack(">H", d[idx+1:idx+3])[0] if t == 1 else sz.get(t, 3)
                    i += 1
                if struct.unpack(">H", d[idx:idx+2])[0] & 0x0200:
                    ifaces.add(n[:-6])
        bad = []
        for f in os.listdir(srcdir):
            if not f.endswith(".java"):
                continue
            txt = open(os.path.join(srcdir, f), encoding="utf-8").read()
            m = re.search(r"@Mixin\(net\.minecraft\.(\S+?)\.class\)", txt)
            if m and not re.search(r"public\s+interface", txt):
                owner = "net/minecraft/" + m.group(1).replace(".", "$")
                if owner in ifaces:
                    bad.append(f[:-5])
        if bad:
            print(f"class mixins targeting interfaces: {bad[:5]}")
            sys.exit(1)
PYGATE
[ $? -eq 0 ] || { echo "build aborted: mixin config out of sync with sources"; exit 1; }

echo "compiling $VERSION shims..."
find "$SHIM/src" -name '*.java' > "$OUT/sources-$VERSION.txt"
javac -nowarn -proc:none -cp "$CP" -d "$CLASSES" "@$OUT/sources-$VERSION.txt"

# fabric.mod.json, the mixin config and the access widener are mod SOURCE and
# live under resources/; they are copied in at package time, not compiled.
cp "$SHIM"/resources/* "$CLASSES"/

JAR="$OUT/centurybridge-0.3.0+$VERSION.jar"
jar cf "$JAR" -C "$CLASSES" .
echo "-> $JAR"
