# CenturyBridge

[简体中文](#简体中文) | [English](#english)

---

## 简体中文

**让停更的老版本 Minecraft 模组在新版本上继续活着。**

CenturyBridge 是一个跨版本模组兼容层（Fabric）：把已停更的老版本模组 jar 放进 `cbmods/`，经转换后与当前版本的模组同场运行。心智模型是 Wine/Proton——在新版本上重建老版本的 API 表面，而不是移植模组本身。它的价值在长尾：那几千个永远不会有人手动移植的小模组。

> ⚠️ **早期原型阶段，距离可用发布还有相当距离。** 走廊进度：1.20.1 → 1.20.2 / 1.20.4 **build 四门全绿、桥 jar 产物落地**——这两个最老中间站曾因 wiring 表与 `Statics` 实现错配（fieldRedirect 该为 fieldGetter、instanceRedirect receiver 基类/子类错配、无源 cover 旁路真桥等）四门不过，现已逐项修净：fieldGetter 迁移、死 ctor 工厂/墓碑/删幻影、receiver 归一到基类、41 个无源 cover 清理（删后真桥生效或真损伤诚实暴露）。pristine 测试集转这两站全 ok_direct / 0 unaccounted、BytecodeVerify 零损伤（**真机服务端验收待补**）。→ 1.21.1 / 1.21.4 / 1.21.8 / 1.21.11 shim 树已立起、mixin config 已同步、build 四门全绿。→ **26.2 全链贯通**：跨过命名空间墙后，服务端带桥与转换模组 "Done"。
>
> **真机验收（2026-10，Linux 服务器 + Java 25）**：MC 26.2 的 server bundler 要求 class file version 69（Java 25）；Linux 上 Java 25 无 Windows 的 netty loopback 问题。单跳转换（pristine 源 → 目标，走廊内部逐段组合后压平成直达）实测：9 个 pristine 小模组从 1.20.2 / 1.21.4 单跳到 26.2 服务端 "Done"（含跨 20 段+命名墙的 1.20.2→26.2）；2 个 1.20.1 模组单跳到 1.21.1 服务端 "Done"。期间修复三个真机暴露的缺陷：模组自带 access widener 过命名墙未翻译（→ official 命名空间）、空 lang json 导致 26.2 Language 解析器崩、构造-后注册物品的占位 key 泄漏进 DataComponentInitializers。
>
> **大型模组真机（2026-10，pristine 源）**：下载 Ad Astra（548 类）、MythicMetals（金属工具/护甲）等真 pristine 1.20.1 大型模组单跳 26.2，逐层真机剥通并逐一补桥，形成两大**内容子系统**的忠实桥：**工具族**（26.2 删 PickaxeItem/SwordItem/TieredItem、Tier 接口变 ToolMaterial record——facade + Tier→record 转换 + 挖掘等级→方块标签映射，经 `Properties.pickaxe/sword` 真挂 TOOL 组件）、**护甲族**（删 ArmorItem、ArmorMaterial 接口变 8 参 record——facade + 转换 + `Properties.humanoidArmor` 真挂 ARMOR 组件）。另系统修：方块属性 `extends final`（Enum/Boolean/IntegerProperty facade）、构造器参数重排全族（26.2 Properties 挪末位的新约定，`gen_ctor_reshapes` 工具自动安全重排）、`owner::new` 构造器方法引用（invokedynamic handle 重写）、SpawnEgg/RecordItem/MobEffectInstance 的 DataComponent/Holder 迁移。
>
> **mixin 深注入是固有降级区**：mod 用 mixin 深度 hook vanilla 方法**内部**（@ModifyConstant 改方法体常量、@Inject 注入被重构成 lambda 的目标、@At selector 的栈类型），当 vanilla 在 26.x 重构了那个方法体，这些注入点静态无法预判是否仍匹配（CB 工作在名/签名级，够不着方法体内部与运行时栈）——是 mixin 跨版本的**固有极限，非 CB 可完全忠实桥**。CB 的务实解是降级：墙后 body-level injector（@ModifyConstant 等）失配时 require=0 软失败（mod 加载、该 hook 功能静默失效），而非让整个 mod 崩。真机见于 reach-entity-attributes（Yarn refmap + @Constant）、Ad Astra（lambda-target）、MythicMetals FireworkMixin（@At 栈类型）。
>
> **继承 vanilla 变 final/删除/改类型的类——曾以为是硬墙，现已系统性桥接**：mod `extends`/`implements` 一个 vanilla 在后续版本**变 final、被删、或 class↔interface 互换**的类，加载期 JVM 直接拒绝（VerifyError/IncompatibleClassChangeError）。三类各有解：① **变 final 但类还在**（Ingredient class_1856、Enchantment class_1887、ItemPredicate class_2073 等）——CB 自带 access widener 以 `extendable` 条目让 fabric loader 运行时去掉 ACC_FINAL，mod 的 extends 合法化且对象**仍是真实例**（instanceof 成立、传回 vanilla 不 ClassCastException），这是对存活 final 类的正解；② **被删类**（SaplingGenerator class_2647、玻璃 class_4262/2368、音乐碟 class_1813 等）——facade 继承其存活的祖先或替代类（superRename/classRename 挂上），并尽量桥接真实行为（如 class_2647 把 mod 的树特征 key 对 live registry 解析并 place，树苗真能长）；③ **class↔interface 翻转**（ArmorMaterial class_1741 interface→record 等）——facade interface 复刻旧契约 + superRename 把 mod 的 implements 指过去。仅当桥接会要求重写 mod 自身逻辑（超出 Wine/Proton 的 API-表面重建范畴）时才降级为 ledger。全量回归显示这主要打击深度继承 vanilla 的大型模组；经上述桥接后，剩余的多是客户端渲染类（见下客户端 facade 未夹具区）。
>
> **全量 corpus 回归（270 真实 pristine 1.20.1 模组，四目标站，引擎零崩溃、270 产物全出）**：初测 1.20.2 = 213 ok_direct / 44 degraded / 13 broken_hierarchy；1.20.4 = 206 / 44 / 20；1.21.4 = 153 / 63 / 54；26.2 = 58 / 175 / 37。走廊越远 ok_direct 越降、degraded/未记账越升（每段损耗单调累积）。**随后系统性消解 broken_hierarchy**：extends-final（存活 final 类）用 access widener `extendable` 让 fabric loader 运行时去 final（四站）；类型翻转与被删类用 facade+superRename/classRename（ArmorMaterial class_1741、SaplingGenerator class_2647 含忠实生长桥、玻璃方块 class_4262/2368、音乐碟 class_1813 等）。**broken 收敛**：1.20.2 **13→5**、1.20.4 **20→7**、1.21.4 **54→20**、26.2 **37→25**（26.2 的内容类 facade 早先已建）。剩余 broken 以**客户端渲染**的类型翻转/被删类为主（ParticleRenderType class_3999、模型 class_1093/756 等，落在下述客户端 facade 未夹具实测区）+ 少数深度继承 vanilla 的大型模组（它们同时触及 mixin 深注入等多重固有限制）。
>
> **已知未覆盖**：① 大型模组真机**剥到 mixin 深注入层**（内容子系统已桥通），完整到 "Done" 还需墙后 injector 降级的 triage 判据补全——周期 62 已把 require=0 降级扩到全部 require-based 注入器（@Inject/@Redirect/@ModifyArg 等），drifted hook 降级为静默 no-op 而非崩；② 客户端渲染 facade 无客户端夹具实测（缺素材）；③ 1.20.x 中间站 wiring **已收尾**：1.20.2 / 1.20.4 build 四门全绿、桥 jar 产物落地（真机服务端验收待补）；④ 转换 CB 自身产物（接力/逐级落地）当前会残留旧版 facade/Statics 引用而崩，非设计用法但引擎应拒绝而非默默产坏 jar。

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

**走廊状态**：**1.20.1 → 1.20.4：gapcheck 层面曾判闭合，但 build 四门未过** ⚠️ —— 以 owner 限定的 stub 对拆（`stub-diff`）丈量，两个边界共 7228 个受损符号在 gapcheck 的 OPEN 计数上归零（可用桥接、重定向、facade 或墓碑）。**但这不等于 build 通过**：真机验收重新用 `wiring-check` 四门校验发现，1.20.4 的 wiring 表引用了约 240 个 `Statics` 里实际不存在或签名错配的成员（fieldRedirect 该登记为 fieldGetter、instanceRedirect 的 helper receiver 用了基类而 wiring 按子类登记等），build 因此 abort。gapcheck 度量的是"受损符号有无处置条目"，不校验那条目指向的实现是否真的存在且签名吻合——这正是 `wiring-check` 存在的理由，也是"闭合"与"可 build"的差距所在。修复进行中（已将 111 条 fieldRedirect 错配迁为 fieldGetter，dangling 317→243）。

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

### 接入新版本：分段更新清单

Minecraft 每出一个新版本，链头加一节分段。固定流程（顺序即依赖）：

1. **版本表**：`Chain.RELEASES` 追加新版本号
2. **映射与 stub**：下载官方 client.jar。intermediary 时代：取 `intermediary-<ver>.tiny`，`remap-jar` 产出 `client-<ver>-intermediary.jar`；**26.x（年号时代）jar 无混淆，直接用真名 jar**
3. **段数据**：`gen-segment <from> <to>` 生成冻结分段 JSON（映射 diff；26.x 边界在 mojang 名字空间上做）
4. **伤害面测量**：`stub-diff`（owner 限定）产出边界 diff TSV，`gapcheck.py` 扩入新边界；`class-fate` 分类整类死亡
5. **锻造工单**：`forge-orders` 按 diff 生成逐符号工单 → `forge.py`（模型只写方法体）→ `verify.py`（javac 判官，错误回喂重试）→ `assemble.py` 落地源码 + wiring（**落地后立即 commit**）→ `prune_failed.py` / `prune_dead_targets.py` 修剪树上下文败者进重试单
6. **shim 树前移**：`port_delta.py` 从上一版本树增量前移（包 token 自动改名）；对新 stub 编译，编译器当检测器清错；死目标进 dropped 清单；wiring 差量前移进 `shims-<ver>.json` 并对照 stub 机器验证
7. **语料回归**：全 corpus `convert` @新版本；`BytecodeVerify` dataflow 全绿；统计未记账残留
8. **闭合判据（硬门）**：`gapcheck` OPEN = 0，未记账 < 50 且全部记账（桥/重定向/facade/墓碑逐条对应）——**一段不闭合，不进下一段**
9. **实机验收**：`build.sh <ver>` 出桥 jar（class 版本按代际钉板：1.20.x=17，其余=21）；服务端 "Done" 零错；客户端行为链自测（注册/放置/GUI/持久化）
10. **收尾**：README 战果更新，commit/push

**跨命名空间墙（1.21.11→26.1）特别段**：intermediary 停更，此边界是整 jar 改名段——`remap-mojang`（组合映射 TSV）+ refmap/accesswidener 文本重写。年号时代（26.x）的段数据不能用 tiny 映射 diff：先 `stub-diff` 真名 jar（含私有成员，供 mixin triage 用），再由 `gen_frontier_segment.py` 产出 intermediary 名的 gone 表、`augment_frontier_segment.py` 回填 desc-changed；shim 树用 `translate_tree.py` 整树源码级过墙，运行时类（Statics/facade）的成员保持 token 名。

### 社区：如何构建、如何提交一个新版本

**先决条件**：JDK 21+、Python 3、bash（Windows 用 Git Bash）。仓库不携带任何游戏数据（版权原因）——首次构建前需要自备工作集：

```bash
# 1) 站起目标版本的离线运行时（官方库 + Fabric loader 库 + 资产）
python3 core/tools/bootstrap_client.py <mcVersion> data/runtime/client-<mcVersion>

# 2) intermediary 时代还需要:官方 client.jar 放到 data/jars/client-<ver>-obf.jar,
#    intermediary-<ver>.tiny 放到 data/mappings/,然后产出编译用 stub:
java -cp "core/build/classes;<asm>;<gson>" top.dext.centurybridge.Main      remap-jar data/mappings/intermediary-<ver>.tiny data/jars/client-<ver>-obf.jar      data/jars/client-<ver>-intermediary.jar
# 26.x（年号时代）jar 无混淆,直接放 data/jars/client-<ver>.jar 即可

# 3) 编译引擎（ASM/gson 取自任一运行时的 libraries）
LIBS=$(find data/runtime/*/libraries -name 'asm*.jar' -o -name 'gson*.jar' | tr '
' ';')
javac -nowarn -cp "$LIBS" -d core/build/classes $(find core/src -name '*.java')

# 4) 构建某版本的桥 jar（四道门卫全绿才出产物）
./build.sh <mcVersion>            # -> build/centurybridge-0.3.0+<ver>.jar
# 26.x 例：CB_STUB=data/jars/client-26.2.jar CB_LIBS=data/runtime/server-26.2/libraries ./build.sh 26.2
```

**提交一个新版本的支持**（PR 流程）：

1. 按上文《接入新版本：分段更新清单》十步走完；提交物是**仓库内的这些路径**：`core/shims/<ver>/`（shim 树）、`core/segments/shims-<ver>.json`（wiring）、`Chain.RELEASES` 的版本表项，以及（若引擎有改动）`core/src/`
2. PR 必须全绿的硬门：`./build.sh <ver>` 出 jar；`gapcheck.py` 该边界 OPEN = 0（或每个 OPEN 有工单/账目）；语料可用时 `convert` 零引擎错误 + `BytecodeVerify` 零损伤
3. **每次锻造落地立即独立 commit**（不要攒大提交——出问题按 commit 粒度回滚）；commit 信息写清跨了哪堵墙、修了什么、剪了什么
4. 生成物不入库：`data/`、`core/out-*/`、`build/` 都在 .gitignore；段 diff JSON 可由 `gen-segment` 再生
5. 实机验收证据（服务端 "Done" 日志、客户端行为链截图）贴进 PR 描述

---

## English

**Keep abandoned Minecraft mods alive on modern versions.**

CenturyBridge is a cross-version mod compatibility layer for Fabric: drop abandoned old-version mod jars into `cbmods/`, and after conversion they run alongside current-version mods. The mental model is Wine/Proton — rebuild the old version's API surface on top of the new version, instead of porting each mod. The value is in the long tail: the thousands of small mods nobody will ever port by hand.

> ⚠️ **Early prototype.** Corridor status: 1.20.1 → 1.20.2 / 1.20.4 **all four build gates green, bridge jars produced** (these two oldest intermediate stops previously failed the gates on wiring-vs-`Statics` mismatches — fieldRedirect that should be fieldGetter, instanceRedirect receiver base/subclass mismatch, unsourced covers shadowing real bridges — now each fixed: fieldGetter migration, dead-ctor factories/tombstones, receiver normalized to the base class, 41 unsourced covers cleared; the pristine test set converts to both at ok_direct / 0 unaccounted with zero dataflow damage — **real-server acceptance still to be run**), → 1.21.1 **self-tested end to end** (a real mod exercised in-game through its full behavior loop), → 1.21.4 / 1.21.8 / 1.21.11 shim trees standing, each server reaching "Done" (869 forged bridges for the 1.21.2 wall landed and synced across the trees; the 270-mod corpus converts with zero dataflow damage), → **26.2 end to end**: across the namespace wall, a 26.2 server reaches "Done" with the bridge and converted mods, and a 26.2 client reaches the title screen with three converted 1.20.1 mods and a 1.20.1-era GUI test mod renders fully on the 1.21.4 / 1.21.8 / 1.21.11 / 26.2 clients (DrawContext-over-extractor facade, constructor redirects, orphaned-override hooks; 1.21.8 / 1.21.11 servers reach "Done" too; wall-symbol forging still in progress). Still far from a usable release.

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

### Adding a new version: the segment update checklist

Every new Minecraft release adds one segment at the head of the chain. The fixed procedure (order = dependency):

1. **Release table**: append the version to `Chain.RELEASES`
2. **Mappings & stub**: download the official client.jar. Intermediary era: fetch `intermediary-<ver>.tiny` and produce `client-<ver>-intermediary.jar` via `remap-jar`; **26.x (year-versioned era) ships unobfuscated — use the real-name jar directly**
3. **Segment data**: `gen-segment <from> <to>` builds the frozen segment JSON (mapping diff; 26.x boundaries diff in the mojang namespace)
4. **Damage surface**: `stub-diff` (owner-qualified) emits the boundary diff TSV; extend `gapcheck.py` with the new boundary; `class-fate` classifies whole-class deaths
5. **Bridge forging**: `forge-orders` emits per-symbol work orders → `forge.py` (the model writes method bodies only) → `verify.py` (javac is the judge; errors fed back on retry) → `assemble.py` lands sources + wiring (**commit immediately after landing**) → `prune_failed.py` / `prune_dead_targets.py` cut in-tree failures into the retry queue
6. **Shim tree forward-port**: `port_delta.py` moves the previous version's tree forward incrementally (package tokens rewritten); compile against the new stub and drive compiler errors to zero; dead targets go to the dropped list; wiring deltas move into `shims-<ver>.json`, machine-validated against the stub
7. **Corpus regression**: `convert` the full corpus at the new version; `BytecodeVerify` dataflow all green; count unaccounted residuals
8. **Closure gate (hard)**: `gapcheck` OPEN = 0, unaccounted < 50 and every symbol accounted (bridge / redirect / facade / tombstone) — **a segment that is not closed does not advance**
9. **Live acceptance**: `build.sh <ver>` produces the bridge jar (class-file level pinned per era: 17 for 1.20.x, 21 after); server reaches "Done" with zero errors; client behavior-loop self-test (register / place / GUI / persistence)
10. **Wrap-up**: update README results, commit/push

**The namespace wall (1.21.11→26.1) is a special segment**: intermediary ended there, so this boundary is a whole-jar rename — `remap-mojang` (composed mapping TSV) plus textual refmap/accesswidener rewriting. Year-era (26.x) segment data cannot come from a tiny-mapping diff: `stub-diff` the real-name jars (private members included, for mixin triage), then `gen_frontier_segment.py` emits the intermediary-named gone lists and `augment_frontier_segment.py` fills in desc-changed; the shim tree crosses in source form via `translate_tree.py`, with runtime classes (Statics/facades) keeping their token member names.

### Community: how to build, how to submit a new version

**Prerequisites**: JDK 21+, Python 3, bash (Git Bash on Windows). The repo carries no game data (copyright) — stand up a local working set before the first build:

```bash
# 1) stand up an offline runtime for the target version (vanilla + Fabric loader libs + assets)
python3 core/tools/bootstrap_client.py <mcVersion> data/runtime/client-<mcVersion>

# 2) intermediary era only: drop the official client.jar at data/jars/client-<ver>-obf.jar,
#    intermediary-<ver>.tiny into data/mappings/, then produce the compile stub:
java -cp "core/build/classes;<asm>;<gson>" top.dext.centurybridge.Main      remap-jar data/mappings/intermediary-<ver>.tiny data/jars/client-<ver>-obf.jar      data/jars/client-<ver>-intermediary.jar
# 26.x (year era) ships unobfuscated: just place data/jars/client-<ver>.jar

# 3) compile the engine (ASM/gson come from any runtime's libraries)
LIBS=$(find data/runtime/*/libraries -name 'asm*.jar' -o -name 'gson*.jar' | tr '
' ';')
javac -nowarn -cp "$LIBS" -d core/build/classes $(find core/src -name '*.java')

# 4) build a version's bridge jar (artifact only appears if all four gates pass)
./build.sh <mcVersion>            # -> build/centurybridge-0.3.0+<ver>.jar
# 26.x example: CB_STUB=data/jars/client-26.2.jar CB_LIBS=data/runtime/server-26.2/libraries ./build.sh 26.2
```

**Submitting support for a new version** (PR flow):

1. Walk the ten-step checklist above ("Adding a new version"); the deliverables are **these in-repo paths**: `core/shims/<ver>/` (the shim tree), `core/segments/shims-<ver>.json` (wiring), the `Chain.RELEASES` entry, plus `core/src/` if the engine changed
2. Hard gates a PR must pass: `./build.sh <ver>` produces the jar; `gapcheck.py` reports OPEN = 0 for the boundary (or every OPEN symbol has a forge order / ledger line); with a corpus available, `convert` runs with zero engine errors and `BytecodeVerify` reports zero damage
3. **Commit immediately after every forge landing** (no batched mega-commits — problems are rolled back at commit granularity); say in the message which wall was crossed, what was fixed, what was pruned
4. Generated artifacts stay out of the repo: `data/`, `core/out-*/`, `build/` are gitignored; segment diff JSONs regenerate via `gen-segment`
5. Attach live-acceptance evidence (server "Done" log, client behavior-loop screenshots) to the PR description

### License

See [LICENSE](LICENSE).
