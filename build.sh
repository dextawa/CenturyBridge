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
# CB_SHIM / CB_WIRING: a climb step's own bridge layer (core/shims/steps/<W>, wiring
# core/segments/steps/<V>__<W>.json) instead of the version's tree and shims-<ver>.json
SHIM="${CB_SHIM:-core/shims/$VERSION}"
[ -d "$SHIM" ] || { echo "no shim tree for $VERSION"; exit 1; }
WIRING="${CB_WIRING:-core/segments/shims-$VERSION.json}"
# a climb step's wiring (<V>__<W>.json): its layer serves mods entering at V only, so the gates look up
# original members in V's stub alone (SourceStubs, -Dcb.sourceStubs=previous), not every older release's
case "$(basename "$WIRING")" in *__*.json) SRC_STUBS="-Dcb.sourceStubs=previous";; *) SRC_STUBS="";; esac
# CB_DATA: the working-set directory (game jars, runtimes, mappings). A git worktree has none of its
# own; point CB_DATA at the main checkout's data instead of linking it in (a link inside a worktree
# turns removing the worktree into deleting the data).
DATA="${CB_DATA:-data}"

# the compile stub: the intermediary stub through 1.21.11; past the namespace wall the plain jar
case "$VERSION" in 26.*) DEFAULT_STUB="$DATA/jars/client-$VERSION.jar" ;; *) DEFAULT_STUB="$DATA/jars/client-$VERSION-intermediary.jar" ;; esac
STUB="${CB_STUB:-$DEFAULT_STUB}"
[ -f "$STUB" ] || { echo "stub not found: $STUB (set CB_STUB)"; exit 1; }
FAPI_JAR="${CB_FAPI_JAR:-$(ls $DATA/runtime/client-$VERSION/mods/fabric-api-*.jar 2>/dev/null | head -1)}"

# A climb step's layer (core/shims/steps/<W>) compiles against the game with the access it has at
# runtime, as the anchor trees 1.21.4 and 26.2 do: W's plain stub with every Fabric API module's
# access widener plus the layer's own cb-step-<W>.accesswidener applied with the loader's semantics
# (core/tools/make_aw_stub.sh -> Main apply-aw, data/AwRules.java). Without it a member the layer's
# AW opens (a private field, a protected constructor) compiles nowhere, and the layer had to reach it
# by reflection or a mixin accessor. When CB_STUB is unset build.sh makes that stub itself and keeps
# it under the work dir's build/awstub/ (never data/), keyed by a hash of the layer's AW, W's Fabric
# API jar, the plain stub and the AW-applying code: any of them changing rebuilds it. javac, and the
# wiring and mixin gates below, all judge against this one stub. CB_STUB set = that stub, as given.
# Step 1's layer is the 1.20.2 tree (core/shims/1.20.2, its access widener cb.accesswidener): the same.
case "$SHIM" in
core/shims/steps/*|*/core/shims/steps/*|core/shims/1.20.2|*/core/shims/1.20.2)
    if [ -z "${CB_STUB:-}" ]; then
        LAYER_AW=$(ls "$SHIM"/resources/*.accesswidener 2>/dev/null | head -1)
        [ -n "$LAYER_AW" ] || { echo "no access widener in $SHIM/resources"; exit 1; }
        [ -f "$FAPI_JAR" ] || { echo "fabric-api jar not found (set CB_FAPI_JAR): needed for the layer's access-widened stub"; exit 1; }
        [ -d core/build/classes/top/dext/centurybridge ] || {
            echo "engine not compiled (core/build/classes): it applies the access wideners to the stub"; exit 1; }
        AW_KEY=$(sha256sum "$LAYER_AW" "$FAPI_JAR" "$STUB" core/src/top/dext/centurybridge/data/AwRules.java \
                     core/src/top/dext/centurybridge/data/ApplyAw.java core/tools/make_aw_stub.sh \
                 | cut -d' ' -f1 | sha256sum | cut -c1-16)
        # the name keeps "intermediary" where the plain stub has it: the gates below tell the era by it
        AW_STUB="build/awstub/$(basename "$STUB" .jar)-aw.jar"
        if [ ! -f "$AW_STUB" ] || [ "$(cat "$AW_STUB.key" 2>/dev/null)" != "$AW_KEY" ]; then
            mkdir -p build/awstub
            rm -f "$AW_STUB" "$AW_STUB.key"
            CB_DATA="$DATA" CB_FAPI_JAR="$FAPI_JAR" bash core/tools/make_aw_stub.sh "$VERSION" "$STUB" "$AW_STUB.tmp" "$LAYER_AW" \
                || { rm -f "$AW_STUB.tmp"; echo "build aborted: could not make the layer's access-widened stub"; exit 1; }
            mv "$AW_STUB.tmp" "$AW_STUB"
            echo "$AW_KEY" > "$AW_STUB.key"
        else
            echo "access-widened stub up to date: $AW_STUB"
        fi
        STUB="$AW_STUB"
        # the interface-target gate below reads the stub from the environment
        export CB_STUB="$STUB"
    fi;;
esac

# Loader-side deps the shims compile against: mixin, and whatever the game
# itself pulls in (gson, datafixerupper, joml, netty...).
LIBS="${CB_LIBS:-$DATA/runtime/client-$VERSION/libraries}"
# Mixin's own annotations come first, and from the OLDEST sponge-mixin in the working set: 0.17.4
# declares Redirect.at / ModifyVariable.at as At[] where 0.17.3 has a single At, so a bridge compiled
# against 0.17.4 encodes `at` as an array -- and 0.17.3 (what Fabric Loader 0.19.3 servers run) reads
# that as a ClassCastException at the first mixin applied (MixinExtras' redirect wrapper). The single
# encoding is the one every Mixin version reads.
# (sed -n 1p, not head -1: head exits after one line, and sort still writing the rest of a listing
# longer than one pipe write dies of SIGPIPE -- under pipefail that ended this script with no output)
MIXIN_API=$(find "$DATA/runtime" -name 'sponge-mixin-*.jar' 2>/dev/null | awk -F/ '{print $NF "\t" $0}' | sort -V | sed -n 1p | cut -f2)
CP="$STUB"
[ -n "$MIXIN_API" ] && CP="$MIXIN_API:$CP"
if [ -d "$LIBS" ]; then
    while IFS= read -r jar; do CP="$CP:$jar"; done < <(find "$LIBS" -name '*.jar')
fi
# fabric-api modules: the fat jar nests them (jar-in-jar), so compile against
# the copies the loader extracts on first launch. Without these the compat
# shims (CbNetworking & friends) cannot build.
FAPI="${CB_FAPI:-$DATA/runtime/client-$VERSION/.fabric/processedMods}"
if [ -d "$FAPI" ]; then
    while IFS= read -r jar; do CP="$CP:$jar"; done < <(find "$FAPI" -name '*.jar')
fi
# MixinExtras: Fabric Loader nests it (META-INF/jars/mixinextras-fabric-*.jar) and provides it to every mod at run
# time. Older runtimes extracted it into processedMods; newer ones (Loader 0.19 on 1.21.9) do not, so compile against
# the copy the release's own loader jar carries -- the same MixinExtras the game runs.
case "$CP" in *mixinextras*) ;; *)
    LOADER_JAR=$(find "$LIBS" -name 'fabric-loader-*.jar' 2>/dev/null | sed -n 1p)
    if [ -n "$LOADER_JAR" ]; then
        MX=$(python3 - "$LOADER_JAR" "$OUT/mixinextras" <<'PY'
import sys, zipfile, os
src, out = sys.argv[1], sys.argv[2]
with zipfile.ZipFile(src) as z:
    for n in z.namelist():
        if n.startswith("META-INF/jars/mixinextras-") and n.endswith(".jar"):
            os.makedirs(out, exist_ok=True)
            p = os.path.join(out, os.path.basename(n))
            with open(p, "wb") as f:
                f.write(z.read(n))
            print(p)
            break
PY
)
        [ -n "$MX" ] && CP="$CP:$MX"
    fi;;
esac
# Windows toolchains want ';' between classpath entries
SEP=":"
case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) CP="${CP//:/;}"; SEP=";";; esac

CLASSES="$OUT/classes-$VERSION"
rm -rf "$CLASSES"
mkdir -p "$CLASSES" "$OUT"

# Mods the layer depends on that CenturyBridge releases separately (core/mods/<mod>: fabric.mod.json "depends" names
# their id; centurybridge-stbir1 for the wall layer): built with their own build.sh into $OUT/deps/, put on the compile
# classpath, and handed to the wiring gate (-Dcb.depJars), since wiring may point converted call sites at their public
# classes (DepJars.java: only jars whose mod id the layer declares count). They are not packaged into the layer: the player installs them as their own mods.
DEP_JARS=""
# (each core/mods/<mod>/build.sh <outDir> writes <outDir>/<id>-<version>.jar)
while read -r dep DJ; do
    mkdir -p "$OUT/deps"
    CB_DATA="$DATA" bash "$dep/build.sh" "$OUT/deps" > "$OUT/deps/build-$(basename "$dep").log" 2>&1 \
        || { cat "$OUT/deps/build-$(basename "$dep").log"; echo "build aborted: dependency $dep"; exit 1; }
    tail -1 "$OUT/deps/build-$(basename "$dep").log"
    DJ="$OUT/deps/$DJ"
    [ -f "$DJ" ] || { echo "build aborted: $dep/build.sh did not write $DJ"; exit 1; }
    CP="$CP$SEP$DJ"
    DEP_JARS="${DEP_JARS:+$DEP_JARS$SEP}$DJ"
done < <(python3 - "$SHIM/resources/fabric.mod.json" <<'PYDEP'
import glob, json, os, sys
deps = json.load(open(sys.argv[1], encoding="utf-8")).get("depends", {})
for fmj in sorted(glob.glob("core/mods/*/resources/fabric.mod.json")):
    m = json.load(open(fmj, encoding="utf-8"))
    if m["id"] in deps:
        print(os.path.dirname(os.path.dirname(fmj)), f"{m['id']}-{m['version']}.jar")
PYDEP
)

# MixinExtras (@WrapOperation & co.): Fabric Loader 0.15+ nests it (META-INF/jars/mixinextras-fabric-*.jar) and
# initializes it for every mod, so a layer uses it like any Fabric mod. javac and the gates read the loader's own nested
# copy, extracted under $OUT: the runtime's processedMods holds one only once that client has launched.
LOADER_JAR=$(find "$LIBS" -name 'fabric-loader-*.jar' 2>/dev/null | sed -n 1p)
if [ -n "$LOADER_JAR" ]; then
    rm -rf "$OUT/loader-nested" && mkdir -p "$OUT/loader-nested"
    python3 - "$LOADER_JAR" "$OUT/loader-nested" <<'PYNEST' || { echo "build aborted: could not read $LOADER_JAR"; exit 1; }
import os, sys, zipfile
with zipfile.ZipFile(sys.argv[1]) as z:
    for n in z.namelist():
        if n.startswith("META-INF/jars/mixinextras-") and n.endswith(".jar"):
            with open(os.path.join(sys.argv[2], os.path.basename(n)), "wb") as f:
                f.write(z.read(n))
PYNEST
    for j in "$OUT"/loader-nested/*.jar; do [ -f "$j" ] && CP="$CP$SEP$j"; done
fi

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
    for f in (os.listdir(srcdir) if os.path.isdir(srcdir) else []):
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
    unlisted = sorted(f[:-5] for f in (os.listdir(srcdir) if os.path.isdir(srcdir) else [])
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
        for f in (os.listdir(srcdir) if os.path.isdir(srcdir) else []):
            if not f.endswith(".java"):
                continue
            txt = open(os.path.join(srcdir, f), encoding="utf-8").read()
            m = re.search(r"@Mixin\s*\(\s*(?:value\s*=\s*)?net\.minecraft\.(\S+?)\.class\)", txt)
            if m and not re.search(r"public\s+interface", txt):
                owner = "net/minecraft/" + m.group(1).replace(".", "$")
                if owner in ifaces:
                    bad.append(f[:-5])
        if bad:
            print(f"class mixins targeting interfaces: {bad[:5]}")
            sys.exit(1)
PYGATE
[ $? -eq 0 ] || { echo "build aborted: mixin config out of sync with sources"; exit 1; }

# Climb step layers only (the 1.20.2 tree is step 1's layer): a layer mixin that merges members sits below the mods'
# priority (CbVanillaReads.PRIORITY), or Mixin keeps a converted mod's INVOKE / other non-HEAD/RETURN/TAIL injectors out
# of the restored member; and such a restorer carries no injectors into W's own methods (they move to a default-priority
# <name>Hooks mixin), except a route declared CbVanillaReads.ROUTE_FIRST. Rules: core/tools/mixin_priority.py.
case "$SHIM" in
    core/shims/steps/*|core/shims/1.20.2|*/core/shims/steps/*|*/core/shims/1.20.2)
        python3 core/tools/mixin_priority.py "$SHIM" || { echo "build aborted: layer mixin priorities"; exit 1; } ;;
esac

# The wiring and mixin gates below run the engine's own checkers against the
# COMPILED tree, so the engine must be built first (see CLAUDE.md). Skipping
# them silently when it is not would ship a jar no gate has looked at.
[ -d core/build/classes/top/dext/centurybridge ] || {
    echo "engine not compiled (core/build/classes): build it first, the wiring/mixin gates need it"; exit 1; }
# past the namespace wall the bridge declares mojang names; wiring keys and
# covers are in the analysis namespace and need the table to compare -- the
# table that spells names the way this release declares them (core/segments/wall-tables.tsv)
NAMETABLE=""
case "$STUB" in *intermediary*) ;; *) NAMETABLE="$(python3 core/tools/wall_tables.py "$VERSION" "$DATA")";; esac

echo "compiling $VERSION shims..."
find "$SHIM/src" -name '*.java' > "$OUT/sources-$VERSION.txt"
# the data-format library is shared with the converter: the same rules that upgrade a
# mod's static data files serve the bridges handed a mod's old JSON at runtime
find core/src/top/dext/centurybridge/res -name '*.java' >> "$OUT/sources-$VERSION.txt"
# Pin the class-file level to what the target's loader stack accepts: a shim
# compiled by a newer ambient JDK dies at Mixin PREPARE with "Unsupported
# class file major version" on servers bundling an older sponge-mixin.
# (1.20.5 moved the game itself to Java 21: its class files cannot be read under --release 17)
case "$VERSION" in 1.20|1.20.[1-4]) RELEASE=17;; *) RELEASE=21;; esac
javac -nowarn -proc:none --release "$RELEASE" -cp "$CP" -d "$CLASSES" "@$OUT/sources-$VERSION.txt"

# What may load on a dedicated server is derived, not hand-placed. A bridge class
# naming a client-only type sinks at verification on a server (one blaze3d type in
# the 26.2 Statics took the whole server down at the first static redirect); a
# server-capable mixin parked in "client" never applies there, so the call site
# its cover left untouched throws NoSuchMethodError -- while every other gate and
# a client test pass. Checked against the real server jar, its libraries and the
# fabric-api modules a server loads; client-only plain classes carry
# @Environment(EnvType.CLIENT). `Main server-check ... --fix` re-sides the config.
SERVER="${CB_SERVER:-$(find "$DATA/runtime/server-$VERSION" -name server-intermediary.jar 2>/dev/null | head -1)}"
[ -n "$SERVER" ] || SERVER="$DATA/runtime/server-$VERSION/versions/$VERSION/server-$VERSION.jar"
[ -f "$SERVER" ] || { echo "dedicated server jar not found (set CB_SERVER): needed to verify what loads on a server"; exit 1; }
SERVER_LIBS="${CB_SERVER_LIBS:-$DATA/runtime/server-$VERSION/libraries}"
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
# wrap/unwrap/guard, facade renames. A member a vanilla class has only through an
# interface the target's Fabric API injects into it is judged on that interface
# (-Dcb.fapi: the modules the shims compiled against). Wiring keyed on a game library
# type is judged against the target's libraries (-Dcb.libs).
java $SRC_STUBS -Dcb.fapi="$FAPI" -Dcb.libs="$LIBS" -Dcb.depJars="$DEP_JARS" -Dcb.layerModJson="$SHIM/resources/fabric.mod.json" -cp "core/build/classes$SEP$CP" top.dext.centurybridge.Main wiring-check "$CLASSES" "$WIRING" "$NAMETABLE" "$STUB" \
    "${CB_STUBS:-$DATA/jars}" "$VERSION" \
    || { echo "build aborted: dangling redirect wiring"; exit 1; }

# Everything Mixin resolves by NAME at PREPARE (accessor/invoker/shadow
# targets, injection selectors) is invisible to javac. Check all of it against
# the real target jar now, not one failure per boot. Covers are checked here
# too: the shape must be declared by a mixin on the owner, an ancestor, or an
# implemented interface (name AND descriptor), and the original member -- read
# from the older stubs in data/jars -- must not be static, since a covered call
# site keeps its invokestatic and no mixin may add a non-private static.
# Injection points are matched in the target method's body (the intermediary stubs have no code: the release's
# code jar; past the wall the plain jar), against the require the mixin config gives: a selector that resolves
# while its @At call moved elsewhere is a "Critical injection failure" at the first boot, and a handler whose
# descriptor no longer fits the target is an InvalidInjectionException (MixinCheck's javadoc).
case "$VERSION" in 26.*) CODE_JAR_DEFAULT="$DATA/jars/client-$VERSION.jar" ;; *) CODE_JAR_DEFAULT="$DATA/jars/client-$VERSION-intermediary-code.jar" ;; esac
java $SRC_STUBS -Dcb.libs="$LIBS" -Dcb.fapi="$FAPI" -Dcb.codeJar="${CB_CODE_JAR:-$CODE_JAR_DEFAULT}" -Dcb.mixinConfigDir="$SHIM/resources" \
    -Dcb.depJars="$DEP_JARS" -Dcb.layerModJson="$SHIM/resources/fabric.mod.json" -cp "core/build/classes$SEP$CP" top.dext.centurybridge.Main mixin-check "$CLASSES" "$STUB" "$WIRING" "$NAMETABLE" \
    "${CB_STUBS:-$DATA/jars}" "$VERSION" \
    || { echo "build aborted: mixin targets unresolved against $STUB"; exit 1; }

# fabric.mod.json, the mixin config, the access widener and the resource pack
# (assets/: CenturyBridge's own models and textures) are mod SOURCE and live
# under resources/; they are copied in at package time, not compiled.
cp -r "$SHIM"/resources/. "$CLASSES"/
# A layer that gives back whole libraries its release dropped nests them verbatim (nested-libraries.tsv: the
# artifacts, checked against the source release's version-JSON sha1; core/tools/nest_libraries.py).
# Native libraries CenturyBridge builds itself (core/natives/<lib>/build.sh, reproducible; sha256s in its SHA256SUMS) live
# under the layer's natives/ at the classpath path LWJGL's Library loader looks for ({os}/{arch}/...).
if [ -d "$SHIM/natives" ]; then
    cp -r "$SHIM"/natives/. "$CLASSES"/
fi
if [ -f "$SHIM/resources/nested-libraries.tsv" ]; then
    python3 core/tools/nest_libraries.py "$SHIM/resources" "$DATA/runtime" "$CLASSES"         || { echo "build aborted: nested libraries"; exit 1; }
fi

JAR="$OUT/centurybridge-0.3.0+$VERSION.jar"
jar cf "$JAR" -C "$CLASSES" .

# Last gate, on the artefact rather than the sources: intermediary leaves
# members without a stable identity obfuscated, and those names change every
# release. A call to one compiles against whatever stub is on the path and then
# throws NoSuchMethodError on the real jar. Worse, when the method we overrode
# is one vanilla itself calls, the world dies on the first tick -- that is how
# class_1297.cw() reached a player three times.
python3 - "$JAR" "$STUB" <<'PYOBF'
import os, re, subprocess, sys, zipfile
jar = sys.argv[1]
# The premise (a 1-3 letter member name is an unstable obfuscated leftover)
# only holds while the game ships obfuscated and we compile against
# intermediary stubs. In the year-versioned era names are real -- Vec3.x()
# is API, not residue -- so the gate stands down.
# (the stub this build compiled against -- build.sh's own default included, which CB_STUB alone misses)
if "intermediary" not in os.path.basename(sys.argv[2]):
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
