#!/usr/bin/env bash
# lib_step_check.sh <workDir> <V> <W> [artifactRegex]: how far one step's library damage is closed.
# The library probe (lib_probe_jar.py + Main api-probe): every public/protected member of every V library
# class the step's table (core/segments/damage-lib/<V>__<W>.tsv) names, touched once in every way a mod can.
# Control: link-checked on V's own classpath (a site that does not link on V is no damage of the step).
# Then converted one step with <workDir>'s engine and step wiring and link-checked on W with the step's
# layer (run remote_step.sh first: it builds both). Open sites: failing on W, clean on V.
# Run on beta2 from ~/cbwork; <workDir> is relative to it. Output in <workDir>/libstep/<V>__<W>/.
set -uo pipefail
cd ~/cbwork
WD="$1"; V="$2"; W="$3"; RE="${4:-}"
JAVA=~/cbtest/jdk-25.0.4.1+1/bin/java
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
SEG="${V}__${W}"
OUT="$WD/libstep/$SEG"
rm -rf "$OUT" && mkdir -p "$OUT/self"
jar_of() { case "$1" in 26.*) echo "data/jars/client-$1.jar" ;; *) echo "data/jars/client-$1-intermediary.jar" ;; esac; }
# data/runtime/client-1.20.1 holds 1.20.2's libraries; ~/cbtest/client-1.20.1 has 1.20.1's own
libdir() { case "$1" in 1.20.1) echo "$HOME/cbtest/client-1.20.1/libraries" ;; *) echo "data/runtime/client-$1/libraries" ;; esac; }
WIRING="$WD/core/segments/steps/$SEG.json"
[ "$SEG" = "1.20.1__1.20.2" ] && WIRING="$WD/core/segments/shims-1.20.2.json"
FAPI=$(ls data/runtime/client-$W/mods/fabric-api-*.jar 2>/dev/null | head -1)
python3 "$WD/core/tools/lib_probe_jar.py" "$WD/core/segments/damage-lib/$SEG.tsv" "$WD/core/segments/damage-lib/artifacts.tsv" \
    "$V" "$(libdir "$V")" "$OUT/lib-api.jar" "$RE" || exit 1
"$JAVA" -Xmx8g -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main api-probe "$OUT/lib-api.jar" "$OUT/probe-$V.jar" \
    "$(jar_of "$V")" "$(libdir "$V")" > "$OUT/probe.log" 2>&1 || { echo "api-probe failed"; tail -5 "$OUT/probe.log"; exit 1; }
cp "$OUT/probe-$V.jar" "$OUT/self/"
"$JAVA" -Xmx16g -Dcb.linkcheck.perSite=true -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main link-check "$OUT/control" - "$OUT/self" -- \
    "$(jar_of "$V")" "$(libdir "$V")" > "$OUT/control.log" 2>&1
python3 probe/probe_report.py "$OUT/probe-$V.jar.index.tsv" "$OUT/self/probe-$V.jar" "$OUT/control/link-findings.tsv" \
    "$OUT/control-report.tsv" > /dev/null 2>&1
"$JAVA" -Xmx16g -Dcb.wiring="$WIRING" -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main convert "$WD/core/segments" "$V" "$W" "$OUT/conv" \
    "$OUT/probe-$V.jar" > "$OUT/convert.log" 2>&1 || { echo "convert failed"; tail -5 "$OUT/convert.log"; exit 1; }
"$JAVA" -Xmx16g -Dcb.linkcheck.perSite=true -cp "$WD/core/build/classes:$LIBS" top.dext.centurybridge.Main link-check "$OUT/lc" "$OUT/conv/full-audit.tsv" "$OUT/conv" -- \
    "$WD/build/centurybridge-0.3.0+$W.jar" "$(jar_of "$W")" $FAPI "$(libdir "$W")" > "$OUT/linkcheck.log" 2>&1
python3 probe/probe_report.py "$OUT/probe-$V.jar.index.tsv" "$(ls "$OUT"/conv/*.jar | head -1)" "$OUT/lc/link-findings.tsv" \
    "$OUT/report.tsv" > /dev/null 2>&1
python3 - "$OUT" "$WD/core/segments/damage-lib/$SEG.tsv" <<'EOF'
import collections, sys
out, damage = sys.argv[1], sys.argv[2]
def rows(p):
    try:
        lines = open(p, encoding="utf-8").read().splitlines()
    except FileNotFoundError:
        return []
    return [l.split("\t") for l in lines[1:] if l]
art = {}
for l in open(damage, encoding="utf-8"):
    if not l.startswith("#") and not l.startswith("artifact\t"):
        f = l.rstrip("\n").split("\t")
        art[f[3].split(".", 1)[0]] = f[0]
def artifact(owner):
    while owner not in art and "$" in owner:
        owner = owner[:owner.rfind("$")]
    return art.get(owner, "?")
control = {r[0] for r in rows(out + "/control-report.tsv")}
total = sum(1 for l in open(out + "/" + [f for f in __import__("os").listdir(out) if f.endswith(".index.tsv")][0], encoding="utf-8")) - 1
opened = [r for r in rows(out + "/report.tsv") if r[0] not in control]
print(f"library probe {out.split('/')[-1]}: {len(opened)} of {total} sites open ({len(control)} sites fail on V itself, not counted)")
by = collections.Counter((artifact(r[2]), r[6]) for r in opened)
for (a, k), c in by.most_common(15):
    print(f"  {c:5} {a:45} {k}")
with open(out + "/open-lib.tsv", "w", encoding="utf-8") as o:
    o.write("artifact\tsite\tusage\towner\tname\tdesc\tbuckets\tkinds\trefs\n")
    for r in opened:
        o.write(artifact(r[2]) + "\t" + "\t".join(r) + "\n")
print("open sites ->", out + "/open-lib.tsv")
EOF
