# CenturyBridge

[简体中文](#简体中文) | [English](#english)

---

## 简体中文

**让停更的老版本 Minecraft 模组在新版本上继续活着。**

CenturyBridge 是一个跨版本模组兼容层（Fabric）：把已停更的老版本模组 jar 放进 `cbmods/`，经转换后与当前版本的模组同场运行。心智模型是 Wine/Proton——在新版本上重建老版本的 API 表面，而不是移植模组本身。它的价值在长尾：那几千个永远不会有人手动移植的小模组。

> ⚠️ **早期原型阶段**。走廊进度：1.20.1 → 1.20.2 / 1.20.4 **全量闭合**，→ 1.21.1 **端到端实测通过**（真实模组在游戏内完整行为链自测），→ 1.21.4 已立起（等待桥体生成收尾）；链路已规划至 **26.2**（年号版本时代）。距离可用发布还有相当距离。

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

**走廊状态**：**1.20.1 → 1.20.4 走廊全量闭合** ✅ —— 以 owner 限定的 stub 对拆（`stub-diff`）重新丈量后，两个边界共 7228 个受损符号，**每一个都有显式处置**：可用桥接、重定向、facade，或注明被排除机制的墓碑。gapcheck 的 OPEN 计数在两段均为 0。全 corpus（270 个死亡模组）未记账运行时引用 **35 → 9**，账目 1806 条全带原因。模组构建绿灯（74KB，含 72 个 AI 生成并经 javac 验证的桥接类）。

- **1.21.1 走廊端到端实测** ✅：ironchests（1.20.1 版）转换后在 1.21.1 客户端完成完整行为链——注册 `/give`、放置渲染、GUI 打开、容器存取、退出重进**持久化确认**；couplings 验证网络桥（裸信道 → 新 payload 体系）与签名 jar 处理。跨过 1.20.5 组件墙的六类运行时损伤（NBT 钩子尾参、Block.use 拆分孤儿、DFU 接口化、fabric-api 自身 API 删除等）全部有桥
- **独立审查 + corpus 级验证加固**：审查代理复核引擎全部高危改写路径，7 项缺陷全数修复（栈深重算、hook 表按目标版本数据化门控、mixin 方法体纳入改写、签名 jar 摘除、indy 库主盲区等）；自建字节码数据流校验器对两条走廊 4.7 万类全量过检零损伤，防"改坏字节码静默发货"
- **1.21.4 走廊已立起**：shim 树跨过 1.21.2 大墙前移（GUI 绘制全族、配方 RegistryKey 化、PreparedRecipes 改钩），wiring 对照 stub 机器验证，corpus 270/270 转换、60 直通，~400 个新墙符号待锻
- **26.2 链路已勘明**：1.21.11 后 Minecraft 转年号版本制，jar 无混淆、intermediary 停更——1.21.11→26.1 将以整 jar 命名空间重映射（intermediary→mojang）作为一节特殊分段
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

> ⚠️ **Early prototype.** Corridor status: 1.20.1 → 1.20.2 / 1.20.4 **fully closed**, → 1.21.1 **self-tested end to end** (a real mod exercised in-game through its full behavior loop), → 1.21.4 standing (bridge forging in progress); the chain is charted through **26.2** (the year-versioned era). Still far from a usable release.

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

**Corridor status**: **the 1.20.1 → 1.20.4 corridor is fully closed** ✅ — re-measured with an owner-qualified stub-to-stub diff (`stub-diff`), the two boundaries carry 7228 damaged symbols and **every one now has an explicit disposition**: a working bridge, a redirect, a facade, or a tombstone naming the mechanism that was ruled out. gapcheck reports OPEN = 0 on both. Across the 270-mod corpus, unaccounted runtime refs fell **35 → 9** with 1806 reason-tagged ledger entries. The mod builds green (74KB, including 72 AI-forged, javac-verified bridge classes).

- **1.21.1 corridor self-tested end to end** ✅: ironchests (built for 1.20.1) runs its full behavior loop on a 1.21.1 client after conversion — registration via `/give`, placement and rendering, GUI opening, container transfer, and **persistence confirmed across leave/re-enter**; couplings exercises the networking bridge (raw channels → payload registry) and signed-jar handling. All six runtime walls past the 1.20.5 component rework (trailing-lookup NBT hooks, the Block.use split orphan, DFU interfaceization, fabric-api's own API removals, and more) are bridged
- **Independent review + corpus-scale hardening**: a review agent re-audited every high-risk rewrite path; all 7 findings fixed (max-stack recomputation, per-target data-driven hook gating, mixin method bodies included in rewriting, signed-jar stripping, an invokedynamic library-owner blind spot, and more). A purpose-built dataflow verifier now checks all 47k classes of both corridors — zero damage — so "converted fine" can no longer hide "shipped broken bytecode"
- **1.21.4 corridor standing**: the shim tree crossed the big 1.21.2 wall (the whole GUI blit family, registry-keyed recipes, the PreparedRecipes hook), wiring machine-validated against the stub; 270/270 corpus conversion, 60 direct, ~400 new wall symbols queued for forging
- **The road to 26.2 is charted**: after 1.21.11 Minecraft switched to year-based versions, ships unobfuscated, and Fabric intermediary ended — the 1.21.11→26.1 segment will be a whole-jar namespace remap (intermediary→mojang)
- **Server-runtime clean rate (1.20.1→1.20.2): 94.8%** (270 real abandoned mods; 256 convert with zero unknown residuals). Eight rewrite mechanisms: shim overload injection, static/instance/field redirects, tombstone stubs, facade class renames, field/method relocation renames, plus identity trackers (Recipe/Advancement getId revival)
- Measurement is **side-annotated**: datagen references (dev-time code compiled into mod jars that never executes in play) and client-only residuals are accounted separately — the raw static number (53%) systematically overstates damage
- Real abandoned 1.20.1 mods (including damaged ones) reach "Done" with zero errors on Fabric 1.20.2 / 1.21.1 / 26.2 servers
- Full 270-mod corpus regression: zero engine crashes; every converted jar loads, residual symbols fail lazily with boundary-attributed error messages
- Early exploration data (full 1.20.1→26.2 corridor): 77.5% architecturally coverable, 55.4% after dependency-availability correction

### Building

No build system yet: `javac` plus the ASM/gson jars compiles `core/`. See `LOOP.md` for command examples. Proper Gradle packaging is on the roadmap.

### License

See [LICENSE](LICENSE).
