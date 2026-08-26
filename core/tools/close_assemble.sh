#!/usr/bin/env bash
# 标准段闭合落地: assemble --merge -> tree_prune -> orphan-cover 循环 -> build.sh
# 明确 --merge (绝不 wipe). 一条龙, 复用于每段.
#   close_assemble.sh <mcver> <verified.json>
set -uo pipefail
cd /d/Documents/centurybridge
VER="$1"; VERIFIED="$2"
PKGDIR="core/shims/$VER/src/top/dext/centurybridge/shims/v${VER//./_}"
echo "== assemble --merge =="
python3 core/tools/assemble.py --merge "$VERIFIED" "core/segments/shims-$VER.json" "$PKGDIR" 2>&1 | grep -E "covers=|landing" | tail -2
echo "== tree_prune =="
rm -rf /tmp/tpcls; mkdir -p /tmp/tpcls
python3 core/tools/tree_prune.py "$VER" 2>&1 | tail -2
echo "== orphan-cover 循环 + build =="
for r in $(seq 1 8); do
  ./build.sh "$VER" > /tmp/bo.txt 2>&1
  if grep -q "centurybridge-0.3.0+$VER.jar" /tmp/bo.txt; then echo "BUILD OK (round $r)"; break; fi
  iconv -f GBK -t UTF-8 /tmp/bo.txt 2>/dev/null | grep "cover without a bridge:" | sed 's/.*bridge: //' | sort -u > /tmp/orph.txt
  if [ ! -s /tmp/orph.txt ]; then echo "非orphan错误:"; iconv -f GBK -t UTF-8 /tmp/bo.txt 2>/dev/null | grep -iE "aborted|no source" | tail -2; break; fi
  python3 -c "
import json,io
o=[x.strip() for x in io.open('/tmp/orph.txt',encoding='utf-8') if x.strip()]
w=json.load(io.open('core/segments/shims-$VER.json',encoding='utf-8'))
c=w['covers']; n=sum(1 for x in o if x in c and (c.remove(x) or True))
io.open('core/segments/shims-$VER.json','w',encoding='utf-8',newline='\n').write(json.dumps(w,indent=1,ensure_ascii=False))
print(f'  round $r: removed {n} orphan covers')
"
done
