#!/usr/bin/env bash
# Build a CenturyBridge mod jar for one Minecraft version.
#
#   ./build.sh 1.20.4 [outDir]
#
# Needs an intermediary-namespace stub for that version plus the Fabric loader
# libraries; both come from a local runtime the repo deliberately does not
# carry (game jars are not ours to distribute). Point CB_STUB and CB_LIBS at
# them, or let the script find them under data/ if you have a working tree.
# CB_STUBS names the directory holding the older releases' intermediary stubs
# (client-<ver>-intermediary.jar), which the covers gate reads.
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
# fabric-api modules: the fat jar nests them (jar-in-jar), so compile against
# the copies the loader extracts on first launch. Without these the compat
# shims (CbNetworking & friends) cannot build.
FAPI="${CB_FAPI:-data/runtime/client-$VERSION/.fabric/processedMods}"
if [ -d "$FAPI" ]; then
    while IFS= read -r jar; do CP="$CP:$jar"; done < <(find "$FAPI" -name '*.jar')
fi
for extra in $(find data/runtime -name 'sponge-mixin-*.jar' 2>/dev/null | head -1); do
    CP="$CP:$extra"
done
# Windows toolchains want ';' between classpath entries
SEP=":"
case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) CP="${CP//:/;}"; SEP=";";; esac

CLASSES="$OUT/classes-$VERSION"
rm -rf "$CLASSES"
mkdir -p "$CLASSES" "$OUT"

# A mixin listed in the config but missing from src is fatal at PREPARE, before
# a single class loads -- worse than a compile error, because the build looks
# clean and the game dies on launch. Catch it here.
python3 - "$SHIM" <<'PYGATE'
import json, os, re, sys, glob
shim = sys.argv[1]
cfgs = glob.glob(os.path.join(shim, "resources", "*.mixins.json"))
if cfgs:
    cfg = json.load(open(cfgs[0], encoding="utf-8"))
    pkg = cfg["package"].replace(".", "/")
    srcdir = os.path.join(shim, "src", pkg)
    # a source file that no longer declares its class (a prune that ate the
    # header) leaves the config pointing at nothing -- fatal at PREPARE
    present = set()
    for f in os.listdir(srcdir):
        if f.endswith(".java"):
            txt = open(os.path.join(srcdir, f), encoding="utf-8").read()
            if re.search(r"\b(class|interface)\s+" + re.escape(f[:-5]) + r"\b", txt):
                present.add(f[:-5])
    ghosts = [m for side in ("mixins", "client", "server")
              for m in cfg.get(side, []) if m not in present]
    if ghosts:
        print(f"mixin config lists {len(ghosts)} classes with no source: "
              f"{ghosts[:5]}{'...' if len(ghosts) > 5 else ''}")
        sys.exit(1)

    # The other direction: a @Mixin source the config does not list compiles,
    # passes mixin-check and satisfies the covers audit -- and never loads. 313
    # of 531 bridges in the 1.21.4 tree sat like that, all audited "covered".
    listed = {m for side in ("mixins", "client", "server") for m in cfg.get(side, [])}
    unlisted = sorted(f[:-5] for f in os.listdir(srcdir)
                      if f.endswith(".java") and f[:-5] in present and f[:-5] not in listed
                      and "@Mixin" in open(os.path.join(srcdir, f), encoding="utf-8").read())
    if unlisted:
        print(f"{len(unlisted)} @Mixin sources not listed in the mixin config (never applied): "
              f"{unlisted[:5]}{'...' if len(unlisted) > 5 else ''} -- Main server-check ... --fix lists them")
        sys.exit(1)

    # A class mixin whose target is an interface fails at PREPARE and takes the
    # whole game with it, so refuse to package one.
    import re, struct, zipfile
    stub = os.environ.get("CB_STUB", "")
    # the dots-to-$ nesting guess below only holds for intermediary names;
    # past the namespace wall mixin-check does this properly from access flags
    if stub and os.path.exists(stub) and "intermediary" in os.path.basename(stub):
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

# The wiring and mixin gates below run the engine's own checkers against the
# COMPILED tree, so the engine must be built first (see CLAUDE.md). Skipping
# them silently when it is not would ship a jar no gate has looked at.
[ -d core/build/classes/top/dext/centurybridge ] || {
    echo "engine not compiled (core/build/classes): build it first, the wiring/mixin gates need it"; exit 1; }
# past the namespace wall the bridge declares mojang names; wiring keys and
# covers are in the analysis namespace and need the table to compare
NAMETABLE=""
case "$STUB" in *intermediary*) ;; *) NAMETABLE="data/mappings/inter-to-mojang-1.21.11.tsv";; esac

echo "compiling $VERSION shims..."
find "$SHIM/src" -name '*.java' > "$OUT/sources-$VERSION.txt"
# the data-format library is shared with the converter: the same rules that upgrade a
# mod's static data files serve the bridges handed a mod's old JSON at runtime
find core/src/top/dext/centurybridge/res -name '*.java' >> "$OUT/sources-$VERSION.txt"
# Pin the class-file level to what the target's loader stack accepts: a shim
# compiled by a newer ambient JDK dies at Mixin PREPARE with "Unsupported
# class file major version" on servers bundling an older sponge-mixin.
case "$VERSION" in 1.20.*) RELEASE=17;; *) RELEASE=21;; esac
javac -nowarn -proc:none --release "$RELEASE" -cp "$CP" -d "$CLASSES" "@$OUT/sources-$VERSION.txt"

# What may load on a dedicated server is derived, not hand-placed. A bridge class
# naming a client-only type sinks at verification on a server (one blaze3d type in
# the 26.2 Statics took the whole server down at the first static redirect); a
# server-capable mixin parked in "client" never applies there, so the call site
# its cover left untouched throws NoSuchMethodError -- while every other gate and
# a client test pass. Checked against the real server jar, its libraries and the
# fabric-api modules a server loads; client-only plain classes carry
# @Environment(EnvType.CLIENT). `Main server-check ... --fix` re-sides the config.
SERVER="${CB_SERVER:-$(find "data/runtime/server-$VERSION" -name server-intermediary.jar 2>/dev/null | head -1)}"
[ -n "$SERVER" ] || SERVER="data/runtime/server-$VERSION/versions/$VERSION/server-$VERSION.jar"
[ -f "$SERVER" ] || { echo "dedicated server jar not found (set CB_SERVER): needed to verify what loads on a server"; exit 1; }
SERVER_LIBS="${CB_SERVER_LIBS:-data/runtime/server-$VERSION/libraries}"
FAPI_JAR="${CB_FAPI_JAR:-$(ls data/runtime/client-$VERSION/mods/fabric-api-*.jar 2>/dev/null | head -1)}"
[ -f "$FAPI_JAR" ] || { echo "fabric-api jar not found (set CB_FAPI_JAR): needed to verify what loads on a server"; exit 1; }
MIXIN_CFG=$(ls "$SHIM"/resources/*.mixins.json | head -1)
java -cp "core/build/classes$SEP$CP" top.dext.centurybridge.Main server-check "$CLASSES" "$MIXIN_CFG" "$SERVER" "$SERVER_LIBS" "$FAPI_JAR" \
    || { echo "build aborted: client-only code reachable on a dedicated server"; exit 1; }

# Every redirect must point at a static that actually exists WITH THE EXACT
# DESCRIPTOR the converter emits: INVOKESTATIC resolves by name+descriptor,
# so a same-name overload of another arity is still NoSuchMethodError the
# moment a mod reaches it -- that is what put the chest GUI on the floor, and
# how a dead 1-arg renderBackground cover hid behind the live 4-arg one.
# Checked on the compiled classes (javap-level truth, not source regexes), for
# every wiring key that names a runtime member: static/instance/super
# redirects, field redirects/getters, instanceof predicates, constructor
# factories, super-ctor adapters and reshape ops, lookupRt, override-hook
# wrap/unwrap/guard, facade renames.
java -cp "core/build/classes$SEP$CP" top.dext.centurybridge.Main wiring-check "$CLASSES" "core/segments/shims-$VERSION.json" "$NAMETABLE" "$STUB" \
    "${CB_STUBS:-data/jars}" "$VERSION" \
    || { echo "build aborted: dangling redirect wiring"; exit 1; }

# Everything Mixin resolves by NAME at PREPARE (accessor/invoker/shadow
# targets, injection selectors) is invisible to javac. Check all of it against
# the real target jar now, not one failure per boot. Covers are checked here
# too: the shape must be declared by a mixin on the owner, an ancestor, or an
# implemented interface (name AND descriptor), and the original member -- read
# from the older stubs in data/jars -- must not be static, since a covered call
# site keeps its invokestatic and no mixin may add a non-private static.
java -cp "core/build/classes$SEP$CP" top.dext.centurybridge.Main mixin-check "$CLASSES" "$STUB" "core/segments/shims-$VERSION.json" "$NAMETABLE" \
    "${CB_STUBS:-data/jars}" "$VERSION" \
    || { echo "build aborted: mixin targets unresolved against $STUB"; exit 1; }

# fabric.mod.json, the mixin config and the access widener are mod SOURCE and
# live under resources/; they are copied in at package time, not compiled.
cp "$SHIM"/resources/* "$CLASSES"/

JAR="$OUT/centurybridge-0.3.0+$VERSION.jar"
jar cf "$JAR" -C "$CLASSES" .

# Last gate, on the artefact rather than the sources: intermediary leaves
# members without a stable identity obfuscated, and those names change every
# release. A call to one compiles against whatever stub is on the path and then
# throws NoSuchMethodError on the real jar. Worse, when the method we overrode
# is one vanilla itself calls, the world dies on the first tick -- that is how
# class_1297.cw() reached a player three times.
python3 - "$JAR" <<'PYOBF'
import os, re, subprocess, sys, zipfile
jar = sys.argv[1]
# The premise (a 1-3 letter member name is an unstable obfuscated leftover)
# only holds while the game ships obfuscated and we compile against
# intermediary stubs. In the year-versioned era names are real -- Vec3.x()
# is API, not residue -- so the gate stands down.
if "intermediary" not in os.path.basename(os.environ.get("CB_STUB", "intermediary")):
    print("real-name era stub: obfuscated-name gate not applicable")
    sys.exit(0)
SAFE = {"get", "put", "add", "map", "of", "abs", "min", "max", "pow", "run",
        "set", "log", "cos", "sin", "tan"}
with zipfile.ZipFile(jar) as z:
    classes = [n[:-6].replace("/", ".") for n in z.namelist() if n.endswith(".class")]
bad = []
for cls in classes:
    out = subprocess.run(["javap", "-c", "-p", "-cp", jar, cls],
                         capture_output=True, text=True, errors="replace").stdout
    for m in re.finditer(r"(?:Method|Field)\s+net/minecraft/[\w$/]+\.([a-z]{1,3}\d?):", out):
        if m.group(1) not in SAFE:
            bad.append((cls.split(".")[-1], m.group(1)))
if bad:
    print(f"{len(bad)} obfuscated call sites in the packaged jar:")
    for c, n in bad[:5]:
        print(f"    {c} -> .{n}()")
    sys.exit(1)
PYOBF
[ $? -eq 0 ] || { echo "build aborted: jar calls obfuscated names"; rm -f "$JAR"; exit 1; }

echo "-> $JAR"
