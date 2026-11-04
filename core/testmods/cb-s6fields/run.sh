#!/usr/bin/env bash
# Compile the production s6fields runtime against small game-API test doubles and run focused behavior checks.
# This is not a Minecraft boot and makes no in-game coverage claim.
set -euo pipefail
OUT=$(mktemp -d)
trap 'rm -rf "$OUT"' EXIT
RT=core/shims/steps/1.21.6/src/top/dext/centurybridge/step/v1_21_6/rt
javac -nowarn -proc:none --release 21 -d "$OUT" \
    $(find core/testmods/cb-s6fields/stubs -name '*.java') \
    "$RT/CbS6MemoryFields.java" "$RT/CbS6SectionField.java" \
    core/testmods/cb-s6fields/CbS6FieldsHarness.java
java -cp "$OUT" cb.s6fields.CbS6FieldsHarness
