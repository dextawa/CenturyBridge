#!/usr/bin/env bash
# mixin_step.sh <workDir> <V> <W> [areaRegex]: the mixin side of one climb step (V -> W), as the API probe is
# step_check.sh's (which runs this at its end). Run on beta2 from ~/cbwork; <workDir> holds
# core/build/classes, core/segments and the step's layer (build/centurybridge-0.3.0+<W>.jar).
#
#  1. CB_MIXIN_GEN=1 only: the mixin probe of V (Main mixin-probe: every point of V's code a mixin can name)
#     judged raw on V (the control: must be empty) and on W (Main mixin-judge against W's real bodies): the
#     step's mixin damage table, written to $CB_MIXIN_PROBES/mixin-damage/<V>__<W>.tsv (default ~/cbwork/probe;
#     mixin_damage.py). It depends only on V's and W's code: frozen into core/segments/damage-mixin/.
#  2. the step's damage rows (core/segments/damage-mixin/<V>__<W>.tsv.gz; with CB_MIXIN_GEN=1 the fresh one)
#     alone, each its own probe mixin (mixin-probe --only: Mixin applies or rejects a mixin whole),
#     converted one step by the engine with the step's wiring, then judged on W with the step's layer: a row
#     still failing is open. Prints "mixin probe: N of M still open" (areaRegex filters by the target owner's
#     mojang name, as step_check does) and writes <workDir>/step/<V>__<W>/open-mixin.tsv.
set -uo pipefail
cd ~/cbwork
WD="$1"; V="$2"; W="$3"; AREA="${4:-}"
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
CP="$WD/core/build/classes:$LIBS"
SEG="${V}__${W}"
code_of() { case "$1" in 26.*) echo "data/jars/client-$1.jar" ;; *) echo "data/jars/client-$1-intermediary-code.jar" ;; esac; }
# The namespace wall (1.21.11 -> 26.1): the probe is intermediary-named like every 1.21.11 mod. Its raw
# verdict on 26.1 is the probe renamed by the wall table alone (Main remap-mojang: class files and the
# refmap, as the converter's wall pass renames a mod), so the damage table holds what a rename does not carry.
WALL=""
[ "$SEG" = "1.21.11__26.1" ] && WALL="$(python3 "$WD/core/tools/wall_tables.py" "$W")"
DAMAGE="$WD/core/segments/damage-mixin/$SEG.tsv"
if [ "${CB_MIXIN_GEN:-}" = 1 ]; then
    PR="${CB_MIXIN_PROBES:-probe}"
    P=$PR/mixin/$SEG; DAMAGE=$PR/mixin-damage/$SEG.tsv
    mkdir -p "$P" "$PR/mixin-damage"
    "$JAVA" -Xmx8g -cp "$CP" top.dext.centurybridge.Main mixin-probe "$(code_of "$V")" "$P/mprobe-$V.jar" > "$P/probe.log" 2>&1 \
        || { echo "mixin probe failed"; tail -3 "$P/probe.log"; exit 1; }
    "$JAVA" -Xmx8g -cp "$CP" top.dext.centurybridge.Main mixin-judge "$(code_of "$V")" "$P/mprobe-$V.jar" "$P/mprobe-$V.jar.index.tsv" \
        "$P/raw-$V.tsv" > "$P/control.log" 2>&1 || { echo "mixin control failed"; tail -3 "$P/control.log"; exit 1; }
    RAWJAR="$P/mprobe-$V.jar"
    if [ -n "$WALL" ]; then
        "$JAVA" -Xmx8g -cp "$CP" top.dext.centurybridge.Main remap-mojang "$WALL" "$P/mprobe-$V.jar" "$P/mprobe-$V-wall.jar" \
            > "$P/wall.log" 2>&1 || { echo "mixin probe wall rename failed"; tail -3 "$P/wall.log"; exit 1; }
        RAWJAR="$P/mprobe-$V-wall.jar"
    fi
    "$JAVA" -Xmx8g -cp "$CP" top.dext.centurybridge.Main mixin-judge "$(code_of "$W")" "$RAWJAR" "$P/mprobe-$V.jar.index.tsv" \
        "$P/raw-$W.tsv" > "$P/raw.log" 2>&1 || { echo "mixin raw judge failed"; tail -3 "$P/raw.log"; exit 1; }
    python3 "$WD/core/tools/mixin_damage.py" "$P/raw-$W.tsv" "$P/raw-$V.tsv" "$DAMAGE" > "$P/damage.log"
    rm -f "$P/mprobe-$V.jar" "$P/mprobe-$V-wall.jar" "$P/mprobe-$V.jar.index.tsv"
    echo "mixin control on $V: $(head -1 "$P/control.log" | sed 's/^mixin-judge: //; s/ -> .*//')"
    head -1 "$P/damage.log"
elif [ ! -f "$DAMAGE.gz" ]; then
    echo "mixin probe: no mixin damage table for $SEG"; exit 0
fi
OUT="$WD/step/$SEG"; mkdir -p "$OUT"; rm -rf "$OUT/mixin"; mkdir -p "$OUT/mixin"
# the frozen tables are kept gzipped (tens of MB of tsv in all)
if [ "${CB_MIXIN_GEN:-}" != 1 ]; then
    gzip -dc "$DAMAGE.gz" > "$OUT/mixin/damage.tsv"; DAMAGE="$OUT/mixin/damage.tsv"
fi
"$JAVA" -Xmx8g -cp "$CP" top.dext.centurybridge.Main mixin-probe "$(code_of "$V")" "$OUT/mixin/mprobe-$V.jar" --only "$DAMAGE" \
    > "$OUT/mixin/probe.log" 2>&1 || { echo "isolated mixin probe failed"; tail -3 "$OUT/mixin/probe.log"; exit 1; }
WIRING="$WD/core/segments/steps/$SEG.json"
[ "$SEG" = "1.20.1__1.20.2" ] && WIRING="$WD/core/segments/shims-1.20.2.json"
"$JAVA" -Xmx16g -Dcb.wiring="$WIRING" -Dcb.layerJar="$WD/build/centurybridge-0.3.0+$W.jar" -cp "$CP" top.dext.centurybridge.Main convert "$WD/core/segments" "$V" "$W" "$OUT/mixin/conv" \
    "$OUT/mixin/mprobe-$V.jar" > "$OUT/mixin/convert.log" 2>&1 || { echo "mixin probe convert failed"; tail -5 "$OUT/mixin/convert.log"; exit 1; }
# --source: an ordinal-pinned anchor W holds another number of times is judged by whether it names the
# counterpart of the V site (same namespace on both sides: not at the wall)
SRC=(); [ -z "$WALL" ] && SRC=(--source "$(code_of "$V")")
# at the wall: V's code and the probe index in 26.1's names (the wall table renames both as it renames mods)
if [ -n "$WALL" ]; then
    "$JAVA" -Xmx8g -cp "$CP" top.dext.centurybridge.Main remap-mojang "$WALL" "$(code_of "$V")" "$OUT/mixin/vcode-wall.jar"         > "$OUT/mixin/vwall.log" 2>&1         && python3 "$WD/core/tools/wall_index.py" "$WALL" "$OUT/mixin/mprobe-$V.jar.index.tsv" "$OUT/mixin/index-wall.tsv"         && SRC=(--source "$OUT/mixin/vcode-wall.jar" --source-index "$OUT/mixin/index-wall.tsv")
fi
"$JAVA" -Xmx8g -cp "$CP" top.dext.centurybridge.Main mixin-judge "$(code_of "$W")" "$(ls "$OUT"/mixin/conv/*.jar | head -1)" \
    "$OUT/mixin/mprobe-$V.jar.index.tsv" "$OUT/mixin/judged.tsv" --layer "$WD/build/centurybridge-0.3.0+$W.jar" "${SRC[@]}" \
    > "$OUT/mixin/judge.log" 2>&1 || { echo "mixin judge failed"; tail -3 "$OUT/mixin/judge.log"; exit 1; }
python3 - "$OUT/mixin/judged.tsv" "$DAMAGE" "$AREA" "$OUT/open-mixin.tsv" "$OUT/mixin/mprobe-$V.jar.index.tsv" <<'EOF'
import collections, os, re, sys
judged, damage, area, out, index = sys.argv[1:6]
cm = {}
names = "data/mappings/inter-to-mojang-classes.tsv"
for line in open(names if os.path.exists(names) else "data/mappings/inter-to-mojang-1.21.11.tsv", encoding="utf-8"):
    f = line.rstrip("\n").split("\t")
    if len(f) >= 3 and f[0] == "C":
        cm[f[1]] = f[2]
def moj(c):
    if c in cm:
        return cm[c]
    d = c.rfind("$")
    return moj(c[:d]) + c[d:] if d > 0 else c
def rows(p):
    with open(p, encoding="utf-8") as f:
        next(f)
        return [l.rstrip("\n").split("\t") for l in f]
dmg = {tuple(r[:5]): r for r in rows(damage)}
# C23: the probe gives each injector row whose V method a bare name selects a twin with a bare selector (usage
# "<usage>@bare", MixinProbe.BARE): a damage row of its own, as most mods write the selector that way
BARE = "@bare"
for r in rows(index):
    if r[2].endswith(BARE):
        base = (r[2][:-len(BARE)],) + tuple(r[3:7])
        if base in dmg:
            dmg[(r[2],) + base[1:]] = [r[2]] + dmg[base][1:]
inarea = {k for k in dmg if not area or re.search(area, moj(k[1]))}
still = {tuple(r[:5]): r[6] for r in rows(judged)}
open_rows = [dmg[k] + [still[k]] for k in sorted(inarea) if k in still]
print(f"mixin probe: {len(open_rows)} of {len(inarea)} mixin damage entries still open" + (f" (area /{area}/)" if area else ""))
bare = {k for k in inarea if k[0].endswith(BARE)}
spelled_open = {k for k in inarea - bare if k in still}
bare_open = {k for k in bare if k in still}
lone = sum(1 for k in bare_open if (k[0][:-len(BARE)],) + k[1:] not in spelled_open)
print(f"  spelled selectors {len(spelled_open)} of {len(inarea) - len(bare)} open; bare-selector twins {len(bare_open)} of {len(bare)} open, "
      f"{lone} of them where the spelled row is closed")
# an anchor that still stands, only a different number of times, applies (no crash) but injects at more or
# fewer places than the mod was written for: counted apart from the entries Mixin rejects or drops
drift = sum(1 for r in open_rows if r[-1].startswith("anchor count"))
print(f"  {len(open_rows) - drift} fail to apply (stripped / disabled / target or anchor gone), {drift} apply with a changed anchor count")
by = collections.Counter(r[0] for r in open_rows)
print("  " + ", ".join(f"{u} {n}" for u, n in sorted(by.items())))
with open(out, "w", encoding="utf-8") as o:
    o.write("usage\towner\tmojang\ttarget\tanchor\tordinal\tvcount\tw\tnow\n")
    for r in open_rows:
        o.write("\t".join([r[0], r[1], moj(r[1])] + r[2:]) + "\n")
print("open mixin entries ->", out)
EOF
# the conversion-time "mixed restored member" signal (MixinRepair.MIXED_DIR), kept beside the counts
: > "$OUT/mixin/mixed.tsv"
for j in "$OUT"/mixin/conv/*.jar; do
    [ -f "$j" ] && unzip -p "$j" "META-INF/centurybridge/mixed/$W.txt" >> "$OUT/mixin/mixed.tsv" 2>/dev/null || true
done
echo "mixed restored members (signal lines): $(wc -l < "$OUT/mixin/mixed.tsv")"
rm -f "$OUT"/mixin/*.jar "$OUT"/mixin/conv/*.jar "$OUT/mixin/index-wall.tsv"
# why each open row is open (Main mixin-cause: V's and W's bytecode, the step's wiring, what the converter
# said about each probe member, the layer's redirect bodies) -> cause-mixin.tsv, and how many of them the
# step's mixinLedger (gen_mixin_ledger.py) writes down as having no hook point in W. At the wall W's code is
# judged in the analysis namespace (--wall: WallInverse), like the rows and the wiring.
WALLARG=(); [ -n "$WALL" ] && WALLARG=(--wall "$WALL")
"$JAVA" -Xmx8g -cp "$CP" top.dext.centurybridge.Main mixin-cause "$(code_of "$V")" "$(code_of "$W")" "$WIRING" \
    "$OUT/open-mixin.tsv" "$OUT/cause-mixin.tsv" "$OUT/mixin/mprobe-$V.jar.index.tsv" "$OUT/mixin/conv/report.md" \
    "$WD/build/centurybridge-0.3.0+$W.jar" "${WALLARG[@]}" > "$OUT/mixin/cause.log" 2>&1 \
    && { echo "  causes (rows / targets; all in cause-mixin.tsv):"; sed -n '2,13p' "$OUT/mixin/cause.log"; } \
    || { echo "  mixin-cause failed"; tail -3 "$OUT/mixin/cause.log"; }
python3 "$WD/core/tools/gen_mixin_ledger.py" --count "$OUT/open-mixin.tsv" "$WIRING"
