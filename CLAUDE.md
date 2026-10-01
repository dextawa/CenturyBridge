# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

CenturyBridge keeps abandoned Fabric mods alive on modern Minecraft: old-version mod jars are converted to run on new versions by rebuilding the old API surface on top of the new one (Wine/Proton mental model), not by porting mods. See README.md for the full architecture; PLAN.md (Chinese) is the plan/decision record; **LOOP.md (Chinese, gitignored) is the live working log — read it first to know the current corridor state and backlog, and append a progress-log entry per work cycle.**

Core concept: the version chain is split into **frozen segments** (one per release boundary, the table of symbols that died/changed there). A mod enters at its source version and is composed segment-by-segment to the target. Segments never change once closed; each new release adds one at the head.

## Layout

- `core/src/` — the production engine, pure Java (deps: ASM + gson only). `top.dext.centurybridge.Main` is the CLI. `engine/` = chain resolution, jar processing, mixin triage; `data/` = segment generation, stub diffing, remapping, bridge-order generation, side annotation.
- `core/shims/<ver>/` — per-version runtime bridge (shim mixin) source trees: `src/` + `resources/` (fabric.mod.json, mixin config, access widener). Compiled by plain javac against intermediary stub jars (mojang-named plain jar for 26.x) — no loom, no Gradle. Trees exist only for anchor versions (currently 1.20.2, 1.20.4, 1.21.1, 1.21.4, 1.21.8, 1.21.11, 26.2); the full release list the chain walks is `Chain.RELEASES` in `core/src/.../engine/Chain.java`.
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
CB_STUB=data/jars/client-26.2.jar CB_LIBS=data/runtime/server-26.2/libraries ./build.sh 26.2   # year era: unobfuscated jar as stub
```

1.21.4 and 26.2 compile against access-widened stubs (`CB_STUB=data/jars/client-1.21.4-intermediary-aw.jar`, `client-26.2-aw.jar`); the plain stub fails with private-access errors.

The engine must be compiled first — build.sh refuses to run without `core/build/classes`, because its gates are engine subcommands. Gates (any failure aborts; fix the cause, never bypass):
1. mixin config ↔ sources sync, and no class mixin targeting an interface (Python, pre-compile);
2. `Main wiring-check` — every redirect/factory/getter key in `shims-<ver>.json` resolves to a compiled member with the **exact descriptor** the converter emits (a same-name overload of another arity is still NoSuchMethodError);
3. `Main mixin-check` — everything Mixin resolves by name at PREPARE (shadow/accessor/invoker targets, injection selectors, covers) exists in the real stub;
4. no obfuscated-name call sites in the packaged jar (intermediary era only; stands down for 26.x real-name stubs).

Past the 1.21.11→26.1 namespace wall, wiring keys stay in the intermediary analysis namespace and the gates translate via `data/mappings/inter-to-mojang-1.21.11.tsv`.

Producing a new version's compile stub (see README "Community" section for the full checklist): `python3 core/tools/bootstrap_client.py <ver> data/runtime/client-<ver>` stands up the offline runtime; then `Main remap-jar data/mappings/intermediary-<ver>.tiny data/jars/client-<ver>-obf.jar data/jars/client-<ver>-intermediary.jar`.

Bytecode dataflow verification over converted jars (the "converted fine ≠ shipped valid bytecode" check):

```bash
javac -cp "$LIBS" -d core/build/classes core/tools/BytecodeVerify.java
java -cp "core/build/classes;$LIBS" BytecodeVerify <dirOfConvertedJars>
```

**Independent link check — the ground truth for "is it actually bridged".** The converter's audit is self-graded (covered/redirect = "the wiring table says so"; `Chain.resolveMember` only examines intermediary names). `link-check` reads none of the engine's tables: it puts converted jars on the real target runtime (game jar + libraries + fabric-api incl. jar-in-jar + the built CB jar + JDK), folds only mixins that a config actually lists, and resolves every reference JVM-style. Findings are bucketed CLAIMED (engine says fixed, site still dangles) / BLIND (engine never saw it) / KNOWN (reported/ledgered — still a hard crash unless tombstoned) / EXTDEP (another mod's API). Kinds include ORPHAN (mod override that no longer overrides anything — silently dead), ABSTRACT, LAMBDA (SAM drift → AbstractMethodError), ICCE (static/interface mismatch).

```bash
java -cp "core/build/classes;$LIBS" top.dext.centurybridge.Main link-check <outDir> <conv>/full-audit.tsv <convDir> -- \n    build/centurybridge-0.3.0+<ver>.jar data/jars/client-<ver>-intermediary.jar data/runtime/client-<ver>/mods/fabric-api-*.jar data/runtime/client-<ver>/libraries
```

Controls that calibrate it: native fabric-api on its own runtime → 0 findings; raw 1.20.1 corpus on native 1.20.1 → ~140 platform findings (the noise floor). Never report a corridor as bridged/closed on the engine's own counts alone.

Closure check (the OPEN count is the kill list; a segment is closed only when OPEN = 0 and every damaged symbol is covered, redirected, renamed, or ledgered with a reason):

```bash
python3 core/tools/gapcheck.py
```

Fabric API is a second, separate kill list that gapcheck does not measure: `core/tools/fapi_diff.py` is its ground truth (26.x Fabric API switched to mojang-style class names). A LEDGERED symbol that mods actually call still hard-crashes at the call site, so a ledger entry is not a fix for a callable method.

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
- **Client-side changes need the client fixture, not just a server "Done".** `core/testmods/cb-guitest` is a 1.20.1-era mod that opens a DrawContext-drawing screen 12 s after boot; convert it through the chain and launch the target client (`data/runtime/client-<ver>`: `java $(tr -d '
' < jvmargs.txt) net.fabricmc.loader.impl.launch.knot.KnotClient --version <ver> --gameDir ... --assetsDir ... --assetIndex <idx> --accessToken 0 --username cbtest`), then screenshot. Servers never load client mixins, so a green server proves nothing about GUI bridges. Kill stale `java.exe` matching `client-<ver>` before relaunching (they hold the mods dir).
- Hours-long forge/queue runs must be launched detached through Task Scheduler (`core/out-diff/launch_<tag>.cmd` + `schtasks /create ... /run`); background processes started from the session shell (nohup or PowerShell Start-Process) die silently on session interrupt. Check runner liveness (`Get-CimInstance Win32_Process`, log mtime) at the start of every cycle.
- Git identity: commits are authored as `dext <dext@dext.top>` (already set in repo config — don't override with `-c`).
- All docs except README are Chinese; keep LOOP.md updates in Chinese, README bilingual.
