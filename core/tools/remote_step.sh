#!/usr/bin/env bash
# remote_step.sh <name> <V> <W> [areaRegex]: build this checkout's bridge layer for the climb step
# V -> W on beta2 and measure the step there with step_check.sh -- no Java runs on this machine.
#
# Uploads the sources as they are in the working tree (core/src, core/shims, core/segments,
# core/tools, core/mods (the separately released mods layers depend on) with core/natives (the records their
# natives are checked against), build.sh; not data, not build output) to ~/cbwork/agents/<name>/, compiles the engine,
# builds the step's layer (step 1.20.1 -> 1.20.2 is the 1.20.2 tree with shims-1.20.2.json; every
# later step is core/shims/steps/<W> with core/segments/steps/<V>__<W>.json) with build.sh's gates,
# then runs ~/cbwork/step_check.sh on it. Prints the build gates' verdicts and the step's open count;
# the open entries land in ~/cbwork/agents/<name>/step/<V>__<W>/open.tsv (fetch with scp).
# CB_PROBE=fapi measures the Fabric API side (open list under step-fapi/ instead).
#
# Run from the root of a checkout (the main one or a worktree). beta2's working set (~/cbwork/data)
# must hold W's stub, client runtime (libraries, mods/fabric-api, .fabric/processedMods) and
# server runtime; build.sh says which file it misses.
set -euo pipefail
NAME="$1"; V="$2"; W="$3"; AREA="${4:-}"
SSH="ssh -o BatchMode=yes -o ConnectTimeout=30 -o ServerAliveInterval=30 -o ServerAliveCountMax=10 -p 20022 dext@beta2.dext.top"
TMP="${TMPDIR:-/tmp}/cb-remote-$NAME"
mkdir -p "$TMP"
rm -f "$TMP/up.tar"
tar --force-local --exclude=__pycache__ -cf "$TMP/up.tar" core/src core/shims core/segments core/tools core/mods core/natives build.sh
$SSH "mkdir -p ~/cbwork/agents/$NAME; rm -f ~/cbwork/agents/$NAME/wall.tsv"
scp -q -P 20022 -o BatchMode=yes "$TMP/up.tar" "dext@beta2.dext.top:cbwork/agents/$NAME/up.tar"
# CB_WALL_TABLE=<local candidate table>: uploaded beside the tree and exported in the remote shell, so
# build.sh's gates, Main convert (WallTable.java) and step_check's readers (wall_tables.py) all use it
WTX="echo 'wall table: deployed'"
if [ -n "${CB_WALL_TABLE:-}" ]; then
    scp -q -P 20022 -o BatchMode=yes "$CB_WALL_TABLE" "dext@beta2.dext.top:cbwork/agents/$NAME/wall.tsv"
    WTX='export CB_WALL_TABLE=$HOME/cbwork/agents/'"$NAME"'/wall.tsv; echo "wall table: $(md5sum $CB_WALL_TABLE)"'
fi
if [ "$V" = "1.20.1" ] && [ "$W" = "1.20.2" ]; then
    LAYER="bash build.sh 1.20.2 build"
else
    LAYER="CB_SHIM=core/shims/steps/$W CB_WIRING=core/segments/steps/${V}__$W.json bash build.sh $W build"
fi
$SSH "set -e; $WTX; cd ~/cbwork/agents/$NAME && rm -rf core build.sh build && tar xf up.tar && rm up.tar
  find core build.sh -name '*.sh' -o -name '*.py' | xargs sed -i 's/\r\$//'
  export PATH=\$HOME/cbtest/jdk-25.0.4.1+1/bin:\$PATH
  LIBS=\$(find ~/cbwork/data/runtime/client-1.20.4/libraries/org/ow2/asm ~/cbwork/data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ':')
  mkdir -p core/build/classes
  javac -nowarn -proc:none -cp \"\$LIBS\" -d core/build/classes \$(find core/src -name '*.java') 2>&1 | grep -v '^Note\|^注' || true
  echo '== build'
  CB_DATA=\$HOME/cbwork/data $LAYER > build.log 2>&1 || { tail -30 build.log; echo 'BUILD FAILED'; exit 1; }
  grep -E 'check:|^->' build.log
  echo '== step'
  # beta2 is shared with live game servers: at most CB_SLOTS (default 6) step checks (each a 16g convert
  # and a 32g link-check) run at once across all agents; the rest wait for a free slot
  cd ~/cbwork && mkdir -p .slots && SLOT=''
  while [ -z \"\$SLOT\" ]; do
    for i in \$(seq 1 ${CB_SLOTS:-6}); do
      exec 9>.slots/\$i
      if flock -n 9; then SLOT=\$i; break; fi
      exec 9>&-
    done
    [ -n \"\$SLOT\" ] || { echo 'waiting for a measurement slot'; sleep 30; }
  done
  CB_PROBE='${CB_PROBE:-}' ./step_check.sh agents/$NAME $V $W '$AREA'"
