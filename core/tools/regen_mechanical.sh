#!/bin/bash
# Regenerate every climb step's generated mechanical.json (core/tools/gen_step_reshapes.py) from the step's
# damage table, V's code jar, W's stub (and W's code jar, whose bodies pair same-typed arguments by use).
# Run from a checkout root (the main one or a worktree); data comes from ./data, or the main checkout's.
#   bash core/tools/regen_mechanical.sh <report dir> [step ...]     (default: every step that has one)
set -u
OUT=${1:?report dir}
shift
D=data
[ -d "$D/jars" ] || D=$(git rev-parse --path-format=absolute --git-common-dir)/../data
J=$D/jars
steps=("$@")
if [ ${#steps[@]} -eq 0 ]; then
  for f in core/segments/steps/*.json.d/mechanical.json; do
    s=${f#core/segments/steps/}
    steps+=("${s%.json.d/mechanical.json}")
  done
fi
mkdir -p "$OUT"
for step in "${steps[@]}"; do
  V=${step%__*}
  W=${step#*__}
  case "$V" in 26.*) vjar=$J/client-$V.jar ;; *) vjar=$J/client-$V-intermediary-code.jar ;; esac
  extra=()
  case "$W" in
    26.*) wjar=$J/client-$W.jar ;;
    *) wjar=$J/client-$W-intermediary.jar; extra+=(--wcode "$J/client-$W-intermediary-code.jar") ;;
  esac
  [ "$step" = "1.21.11__26.1" ] && extra+=(--wall "$D/mappings/inter-to-mojang-1.21.11-26.1.tsv")
  echo "== $step"
  python3 core/tools/gen_step_reshapes.py "core/segments/damage/$step.damage.tsv" "$vjar" "$wjar" \
    "core/segments/steps/$step.json" "top/dext/centurybridge/step/v${W//./_}/rt/Guard" "$OUT/$step.tsv" "${extra[@]}" \
    || exit 1
done
