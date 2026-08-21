# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

CenturyBridge keeps abandoned Fabric mods alive on modern Minecraft: old-version mod jars are converted to run on new versions by rebuilding the old API surface on top of the new one (Wine/Proton mental model), not by porting mods. See README.md for the full architecture; PLAN.md (Chinese) is the plan/decision record; **LOOP.md (Chinese, gitignored) is the live working log — read it first to know the current corridor state and backlog, and append a progress-log entry per work cycle.**

Core concept: the version chain is split into **frozen segments** (one per release boundary, the table of symbols that died/changed there). A mod enters at its source version and is composed segment-by-segment to the target. Segments never change once closed; each new release adds one at the head.

## Layout

- `core/src/` — the production engine, pure Java (deps: ASM + gson only). `top.dext.centurybridge.Main` is the CLI. `engine/` = chain resolution, jar processing, mixin triage; `data/` = segment generation, stub diffing, remapping, bridge-order generation, side annotation.
- `core/shims/<ver>/` — per-version runtime bridge (shim mixin) source trees: `src/` + `resources/` (fabric.mod.json, mixin config, access widener). Compiled by plain javac against intermediary stub jars — no loom, no Gradle.
- `core/segments/` — tracked: `shims-<ver>.json` (wiring: covers, static/instance/field redirects, renames, ledger) and `sides-1.20.1.json`. Gitignored but regenerable: `<from>__<to>.json` segment diffs (via `gen-segment`).
- `core/tools/` — Python kill-list/forging pipeline (gitignored, drives closure work; see below).
- `core/out-*/` — generated analysis output dirs (gitignored).
- `data/` — gitignored local working set: game jars + intermediary stubs (`data/jars/`), tiny mappings (`data/mappings/`), test clients/servers (`data/runtime/`), the 270-mod corpus.
- `analyzer/`, `cb/` — archived Python research prototypes; don't develop there.
- `cbmods_test/` — small test mod jars.

## Commands

No build system yet — plain `javac`. ASM/gson jars come from a game runtime under `data/runtime/*/libraries/`. On Windows the classpath separator is `;`; in Git Bash keep paths repo-relative (POSIX-style absolute paths break java.exe).

Compile the engine (output goes to `core/build/classes`):

```bash
LIBS=$(find data/runtime/client-1.20.4/libraries/org/ow2/asm data/runtime/client-1.20.4/libraries/com/google/code/gson -name '*.jar' | tr '\n' ';')
javac -nowarn -proc:none -cp "$LIBS" -d core/build/classes $(find core/src -name '*.java')
```

Run the engine CLI (`java -cp "core/build/classes;$LIBS" top.dext.centurybridge.Main <cmd>`):

- `gen-segment <from> <to> <mappingsDir> <out.json>` / `gen-chain <from> <to> <mappingsDir> <segmentsDir>` — build segment diffs from tiny mappings
- `convert <segmentsDir> <from> <to> <outDir> <jar|dir...>` — convert mod jars through the chain; writes converted jars + a report with per-mod verdicts (direct/degraded/partial) and unaccounted-residual counts
- `remap-jar` (obf→intermediary stub for shim compilation), `remap-mojang` (whole-jar intermediary→mojang, the 1.21.11→26.1 namespace wall)
- `stub-diff`, `class-fate`, `forge-orders`, `classify`, `audit`, `gen-sides` — measurement/work-order tooling

Build the shippable mod jar for one version (needs stub + runtime libraries; `CB_STUB`/`CB_LIBS`/`CB_FAPI` override the `data/` defaults):

```bash
./build.sh 1.21.4          # -> build/centurybridge-0.3.0+<ver>.jar
```

build.sh runs four hard gates: mixin config vs sources sync, class-mixins-targeting-interfaces, dangling redirect wiring, and obfuscated-name call sites in the packaged jar. A gate failure aborts the build — fix the cause, never bypass.

Bytecode dataflow verification over converted jars (the "converted fine ≠ shipped valid bytecode" check):

```bash
javac -cp "$LIBS" -d core/build/classes core/tools/BytecodeVerify.java
java -cp "core/build/classes;$LIBS" BytecodeVerify <dirOfConvertedJars>
```

Closure check (the OPEN count is the kill list; a segment is closed only when OPEN = 0 and every damaged symbol is covered, redirected, renamed, or ledgered with a reason):

```bash
python3 core/tools/gapcheck.py
```

## The bridge-forging loop (core/tools)

AI-assisted but javac-judged: `forge-orders` emits work orders (one damaged symbol each) → `forge.py` has a small LLM write ONLY method bodies (structure/wiring decided deterministically by BridgeForge) → `verify.py` compiles each body against the real stub, rejects and retries with compiler feedback → `assemble.py` lands verified bodies into the shim tree + JSON wiring → `prune_failed.py` / `prune_dead_targets.py` cut bridges that fail in tree context → gapcheck + BytecodeVerify + a real server/client boot confirm. Model access via `CB_API_BASE` / `CB_API_KEY` / `CB_MODEL` / `CB_WORKERS` / `CB_RPM` env vars (gateway is RPM-metered — keep workers/rpm modest). `run_*.sh` in core/tools are corridor-run wrappers.

There is no unit-test suite. Verification is: corpus conversion (zero engine crashes), dataflow verify (zero damaged methods), gapcheck closure, and a real Fabric server/client reaching "Done" with the converted mods — in-game behavior self-test via mcmcp (`python core/tools/mc.py call <tool> '<json>'`, loopback port 25599).

## Working discipline (hard-learned, do not relearn)

- **Segment closure gate**: a segment advances only when its kill list is closed (unaccounted runtime refs < 50, all ledgered). Per-segment loss compounds monotonically over a 21-segment chain.
- **Bridge from the full stub-diff, not crash-by-crash.** Chasing individual crashes systematically undercounts; the owner-qualified stub-diff is the ground truth of the damage surface.
- **Commit immediately after each assemble landing.** `assemble.py --merge` has re-landed previously pruned bad bridges before; if wiring counts move anomalously (e.g. fieldRedirects dropping), stop and `git checkout` the committed baseline rather than patching forward.
- Class-file level is pinned per era in build.sh (`--release 17` for 1.20.x targets, 21 after) — a newer ambient JDK's class files kill old sponge-mixin at Mixin PREPARE.
- Mixin rules that cost real debugging: non-mixin helper classes must live in a separate `rt`/`Statics` package (mixin-package classes get transformed); `@Shadow` does not climb the hierarchy — declare it on the class that declares the member; class mixins must not target interfaces; every mixin listed in the config must exist in `src`.
- Intermediary leaves unstable members obfuscated (`a`, `cw`...) and those names shift every release — never call one from shim code; build.sh's last gate catches this.
- Git identity: commits are authored as `dext <dext@dext.top>` (already set in repo config — don't override with `-c`).
- All docs except README are Chinese; keep LOOP.md updates in Chinese, README bilingual.
