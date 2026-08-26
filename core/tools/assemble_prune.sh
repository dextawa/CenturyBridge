#!/usr/bin/env bash
# 标准化的 assemble+prune+build 一条龙: 把 verified body 落地到锚点树, prune 掉
# 树上下文编不过的 (verify 单体过 != 树上过), build.sh 过所有 gate.
#   assemble_prune.sh <mcver> <verified.json> [--merge]
set -uo pipefail
cd /d/Documents/centurybridge
VER="$1"; VERIFIED="$2"; MERGE="${3:---merge}"
PKGDIR=$(echo "core/shims/$VER/src/top/dext/centurybridge/shims/v${VER//./_}")
SHIMS="core/segments/shims-$VER.json"
STUB="data/jars/client-$VER-intermediary.jar"
STATICS="core/shims/$VER/src/top/dext/centurybridge/rt/v${VER//./_}/Statics.java"
echo "== assemble $VERIFIED -> $VER 树 =="
python3 core/tools/assemble.py $MERGE "$VERIFIED" "$SHIMS" "$PKGDIR" 2>&1 | tail -4
# build classpath like build.sh
CP="$STUB"
for jar in $(find data/runtime/client-$VER/libraries -name '*.jar' 2>/dev/null); do CP="$CP;$jar"; done
for jar in $(find data/runtime/client-$VER/.fabric/processedMods -name '*.jar' 2>/dev/null); do CP="$CP;$jar"; done
for jar in $(find data/runtime -name 'sponge-mixin-*.jar' 2>/dev/null | head -1); do CP="$CP;$jar"; done
SRCDIR="core/shims/$VER/src"
echo "== prune 循环 (英文 javac, prune_failed 解析) =="
for round in $(seq 1 20); do
  find "$SRCDIR" -name '*.java' > /tmp/srclist_$VER.txt
  javac -J-Duser.language=en -J-Duser.country=US -Xmaxerrs 4000 -encoding UTF-8 -proc:none --release 17 -cp "$CP" -d /tmp/cbcls_$VER @/tmp/srclist_$VER.txt 2> /tmp/cberr_$VER.txt
  nerr=$(grep -c ": error:" /tmp/cberr_$VER.txt)
  [ "$nerr" = "0" ] && { echo "round $round: CLEAN"; break; }
  grep -oE "\S+\.java:[0-9]+: error:" /tmp/cberr_$VER.txt | sed 's|:[0-9]*: error:||' | sed 's|.*[\/]||' | sort -u > /tmp/errbase_$VER.txt
  : > /tmp/errfiles_$VER.txt
  while read b; do find "$SRCDIR" -name "$b"; done < /tmp/errbase_$VER.txt | sort -u > /tmp/errfiles_$VER.txt
  pr=$(python3 core/tools/prune_failed.py /tmp/cberr_$VER.txt "$SHIMS" core/out-diff/retry-prune-$VER.json $(cat /tmp/errfiles_$VER.txt) 2>&1 | grep -oE "total pruned [0-9]+")
  echo "round $round: $nerr errors, $pr"
  [ "$pr" = "total pruned 0" ] && { echo "STUCK (需手动: 语法坏body/非marker方法)"; grep ": error:" /tmp/cberr_$VER.txt | head -3; break; }
done
rm -rf /tmp/cbcls_$VER
