#!/usr/bin/env bash
# Land the re-forged tree-context bridges of one intermediary-era tree, then
# run every gate. Commit right after if the numbers look sane.
#   bash core/tools/land_ctx.sh 1.21.8
set -euo pipefail
v="$1"; pkg="v${v//./_}"
cd /d/Documents/centurybridge
ls core/out-diff/v-ctx-$v/*.json >/dev/null 2>&1 || { echo "no verified output for $v"; exit 1; }
CB_DEST_STUB=data/jars/client-$v-intermediary.jar python3 core/tools/assemble.py --merge \
    core/out-diff/v-ctx-$v/*.json core/segments/shims-$v.json core/shims/$v/src/top/dext/centurybridge/shims/$pkg
JAVA_TOOL_OPTIONS=-Duser.language=en CB_FAPI=data/runtime/client-1.21.4/.fabric/processedMods bash build.sh $v 2>&1 | grep -E 'mixin-check|aborted|error:|->'
python3 core/tools/gapcheck.py 2>&1 | grep -A1 "^=== .*->$v:" | head -3
git status --short core/shims/$v core/segments/shims-$v.json | wc -l
