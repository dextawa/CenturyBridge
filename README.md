# CenturyBridge

[简体中文](#简体中文) | [English](#english)

---

## 简体中文

**让停更的老版本 Minecraft 模组在新版本上继续活着。**

CenturyBridge 是一个跨版本模组兼容层（Fabric）：把已停更的老版本模组 jar 放进 `cbmods/`，经转换后与当前版本的模组同场运行。心智模型是 Wine/Proton——在新版本上重建老版本的 API 表面，而不是移植模组本身。它的价值在长尾：那几千个永远不会有人手动移植的小模组。

> ⚠️ **早期原型阶段**。目前首条走廊为 1.20.1 → 1.20.2（完整能力）与 1.20.1 → 1.21.11（验证/摘除/归因），已在真实服务端跑通多个真实模组；距离可用发布还有相当距离。

### 架构：分段链

版本链上**每个正式版是一个节点**，相邻版本之间是一节**冻结分段**（该边界上死亡/变形的符号表）。老分段写完永不再改；每个新版本只需在链头加一节。模组按其精确源版本起链，逐段组合到目标版本。

转换管线（每个 jar）：

1. **Mixin triage** —— 解析 mixin 配置与 refmap，逐个判定注入目标存活；apply 期必死的 mixin 自动摘除（承重者带警告），JIJ 嵌套 jar 递归处理
2. **引用验证 + 边界归因** —— 每处损伤精确标注"死于哪条边界"
3. **调用点改写** —— 死静态方法重定向到运行时 `Statics` 类；无法桥接的死方法改指合成的墓碑桩（惰性失败，带清晰错误消息），模组照常加载，只有真正触发死路径才报错
4. **运行时桥** —— 每版本一个 shim mixin 集，把老签名以重载形式注回新类（JVM 按描述符解析，老调用点零改动复活）
5. **元数据修补** —— fabric.mod.json 版本范围与依赖 id 变迁

### 仓库结构

| 目录 | 内容 |
|------|------|
| `core/` | **产品引擎**（纯 Java，依赖仅 ASM + gson）：分段生成、链式解析、jar 处理、mixin triage、重映射工具 |
| `core/shims/` | 每版本运行时桥源码（javac 直接编译，无需 loom——利用 remap-jar 生成的 intermediary stub 类路径） |
| `core/segments/` | 已生成的冻结分段数据 + shim 覆盖面表 |
| `analyzer/`, `cb/` | P0/P1 研究原型（Python，已封存，产出了覆盖率数据、死亡归因与全部管线设计） |
| `PLAN.md` | 完整项目计划与决策记录 |
| `LOOP.md` | 当前开发循环的 backlog 与进度日志 |

### 当前实测战果

**走廊状态**：**1.20.1 → 1.20.4 走廊全线闭合** ✅ —— @1.20.2 段（未记账 19 / server-clean 94.8%）与 @1.20.4 段（未记账 35 / server-clean 91.1%，账目 1507 条全带原因标签）均达 kill-list 闭合判据；1.20.2 与 1.20.4 实机服务端验收通过（1.20.1 死模组经完整分段链转换后 "Done" 零错误）。shim 栈随版本前移的维护流程（编译器检测断点 → facade/ledger 化）已实战验证。

- **服务端运行时干净率（1.20.1→1.20.2）：94.8%**（270 个真实死亡模组语料，256 个转换后预期零未知残留运行）。八种改写机制：shim 重载注入、静态/实例/字段重定向、墓碑桩、facade 类改名、字段/方法迁移改名 + 身份追踪器（Recipe/Advancement getId 复活）
- 测量口径经 **side 标注**修真：datagen 类引用（模组内编译的开发期代码，游戏内永不执行）与 client 专用残留分开记账——裸静态口径（53%）会系统性高估伤亡
- 死于 1.20.1 的真实模组（含受损模组）在 Fabric 1.20.2 / 1.21.1 / 26.2 服务端 "Done" 零错误
- 270 模组语料全量回归：引擎零崩溃；所有转换产物可加载，残留符号仅在触发时惰性报错（带边界归因的错误消息）
- 早期探索数据（1.20.1→26.2 全走廊）：架构可覆盖 77.5%，依赖封顶修正后 55.4%

### 构建

无构建系统依赖（暂时）：`javac` + ASM/gson jar 即可编译 `core/`。详见 `LOOP.md` 中的命令示例。正式的 Gradle 工程化在路线图上。

---

## English

**Keep abandoned Minecraft mods alive on modern versions.**

CenturyBridge is a cross-version mod compatibility layer for Fabric: drop abandoned old-version mod jars into `cbmods/`, and after conversion they run alongside current-version mods. The mental model is Wine/Proton — rebuild the old version's API surface on top of the new version, instead of porting each mod. The value is in the long tail: the thousands of small mods nobody will ever port by hand.

> ⚠️ **Early prototype.** The first corridor is 1.20.1 → 1.20.2 (full capability) and 1.20.1 → 1.21.11 (verify/strip/attribution). Real abandoned mods — including damaged ones — boot cleanly on real servers, but this is far from a usable release.

### Architecture: the segment chain

**Every release is a node**; between adjacent releases sits a **frozen segment** (the table of symbols that died or changed at that boundary). Old segments never change; each new release adds one segment at the head. A mod enters the chain at its exact source version and is composed segment-by-segment to the target.

Per-jar pipeline:

1. **Mixin triage** — parse mixin configs and refmaps, resolve every injection target's survival; mixins that would hard-fail at apply are stripped automatically (load-bearing ones flagged), recursing into JIJ-nested jars
2. **Reference verification with boundary attribution** — every damage report names the exact boundary that killed the symbol
3. **Call-site rewriting** — dead statics are redirected to a runtime `Statics` class; unbridgeable dead methods are redirected to synthesized tombstone stubs (lazy-fail with a descriptive message), so mods load and only fault if the dead path actually executes
4. **Runtime bridge** — a per-version set of shim mixins that re-adds old signatures as overloads on new classes (the JVM resolves by descriptor, so old call sites revive untouched)
5. **Metadata patching** — fabric.mod.json version ranges and dependency id migrations

### Repository layout

| Path | Contents |
|------|----------|
| `core/` | **Production engine** (pure Java, deps: ASM + gson only): segment generation, chain resolution, jar processing, mixin triage, remapping tools |
| `core/shims/` | Per-version runtime bridge sources (compiled with plain javac — no loom — against intermediary stub jars produced by `remap-jar`) |
| `core/segments/` | Generated frozen segment data + shim coverage tables |
| `analyzer/`, `cb/` | P0/P1 research prototypes (Python, archived; they produced the coverage numbers, death attribution, and the entire pipeline design) |
| `PLAN.md` | Full project plan and decision log (Chinese) |
| `LOOP.md` | Current development loop backlog and progress log (Chinese) |

### Current results

**Corridor status**: **the 1.20.1 → 1.20.4 corridor is fully closed** ✅ — both the @1.20.2 segment (19 unaccounted / 94.8% server-clean) and the @1.20.4 segment (35 unaccounted / 91.1% server-clean, 1507 ledger entries all reason-tagged) meet the kill-list closure criterion; live server acceptance passed on 1.20.2 and 1.20.4 (dead 1.20.1 mods reach "Done" with zero errors through the full segment chain). The per-release shim forward-port workflow (compiler-detected breakage → facade/ledger) is battle-tested.

- **Server-runtime clean rate (1.20.1→1.20.2): 94.8%** (270 real abandoned mods; 256 convert with zero unknown residuals). Eight rewrite mechanisms: shim overload injection, static/instance/field redirects, tombstone stubs, facade class renames, field/method relocation renames, plus identity trackers (Recipe/Advancement getId revival)
- Measurement is **side-annotated**: datagen references (dev-time code compiled into mod jars that never executes in play) and client-only residuals are accounted separately — the raw static number (53%) systematically overstates damage
- Real abandoned 1.20.1 mods (including damaged ones) reach "Done" with zero errors on Fabric 1.20.2 / 1.21.1 / 26.2 servers
- Full 270-mod corpus regression: zero engine crashes; every converted jar loads, residual symbols fail lazily with boundary-attributed error messages
- Early exploration data (full 1.20.1→26.2 corridor): 77.5% architecturally coverable, 55.4% after dependency-availability correction

### Building

No build system yet: `javac` plus the ASM/gson jars compiles `core/`. See `LOOP.md` for command examples. Proper Gradle packaging is on the roadmap.

### License

See [LICENSE](LICENSE).
