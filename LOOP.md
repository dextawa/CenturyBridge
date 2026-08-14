# CB 循环任务 v2：1.20.1 → 1.20.4 走廊达标

> **v2 目标（用户设定，周期 9 修订）**：分段完工判据从"模组百分比"改为 **kill-list 闭合**——每段语料观测的运行时损伤引用清到 <50 条且全部记账（shim/重定向/quirk/墓碑逐条对应），残留只允许真正不可修的语义漂移尾巴。**一段不闭合，不进下一段**（用户："走一路爆一路零件迟早 1% 都不到"——per-segment 可接受损失在 21 段链上单调累积，必须逐段清零）。本轮推进到 **1.20.4**。里程碑时更新 README；完成/阻塞 PushNotification。
> 当前基线（side 标注后 @1.20.2）：server-clean = 180/270 = **66.7%**（direct 160 + degraded 21 −1 重叠修正后 181→以 sided2 为准 181/270=67%）。

## v2 Backlog

- [x] C1 side 标注：SideAnnotator（client 1970 类 / datagen 213 类，源版本 1.20.1）+ 调用方 datagen 启发式（modid/datagen|/data/ 包）+ 判定/报告分侧
- [x] C2 shim 第二批 @1.20.2：Advancement 族（Builder.method_709 反射恢复 trigger + method_705 包装；method_694 换返回桥；Advancement.method_686 record 桥）、StatusEffect 双钩子降参桥、ShapedRecipe.outputFromJson 静态重定向、**FluidDrainable 实例重定向**（新机制：接口方法 Mixin 加不了 → 调用侧 receiver 变 arg0）。桥 jar 10KB / 9 mixin + Statics。boot 验证 mixin 全部 apply（含内部类目标）"Done (4.575s)"
- [ ] C3 kill-list 闭合 @1.20.2（当前 **196/270 = 72.6%**，残留 565→? 条；批次 3 已交付：Trackers 身份图模式救活 Recipe.getId ×30 + Advancement.getId ×28、fieldRedirects 新机制、method_5563 重实现、Fertilizable 实例重定向；Mixin 包规则实锤：非 mixin 类必须住独立 rt 包）；shim 第三批工单：Recipe.getId 复合桥（RecipeManager 建 IdentityHashMap + 接口实例重定向，×30）、criterion 注册族（class_4558.method_27853/class_174.method_767，×26，需研 1.20.2 注册路径）、class_5258.field_24388 字段重定向（新机制 fieldRedirects）、class_1291.method_5563、class_1856.method_8102、class_2256.method_9651（接口→实例重定向）、class_2989/2985 进度加载器桥
- [ ] C4 分段 1.20.2→1.20.3 同套待遇（shim 桥 1.20.3 工单 + 1.20.4 stub/运行时桥；1.20.3→1.20.4 零 diff 白送）
- [ ] C5 全语料扫荡 @1.20.4 + server-1.20.4 实机验收
- [ ] C6 README 更新（战果数字 + 走廊状态表）+ commit/push + PushNotification 叫醒用户

## 原始 Backlog（v1，已全部完成）

> 目标：这条链上第一节分段达到"生产完整"——不只验证与摘除，还包括该边界的 shim 桥、墓碑桩、真实受损模组的实机验收。链条设计自底向上，做完这节的方法论直接复制到后续节。
> 完成或阻塞时 PushNotification 通知用户。行为层验证：用户用 D:\Documents\MCProxyAgent（-javaagent 代理）+ ViaVersion 连测试服游玩确认。

## Backlog

- [x] B1 语料扫荡 @1.20.2：全 270 jar convert，统计受损模组清单 + top 受损符号（= shim 工单表），写入 core/out-1.20.2-sweep/
- [x] B2 intermediary 编译类路径：datagen 新命令 remap-jar（ASM ClassRemapper：官方 1.20.2 client.jar obf→intermediary），产出供 javac 编译 shim 用的类路径 jar
- [ ] B3 1.20.2 运行时桥：按 B1 工单写 shim mixin（预计 FriendlyByteBuf.writeNbt / Ingredient.fromJson / Recipe.getId / Shaped(less)RecipeBuilder 一族），组装 centurybridge-1.20.2 运行时 jar
- [x] B4 墓碑桩生成器（ASM 合成惰性失败桩，per-jar-layer Tombstones 类，内联 UOE+athrow 零外部依赖，消息带边界归因）
- [x] B5 实机验收：potionparticlepack（writeNbt 受损 + JIJ 内嵌 mixin 死亡）convert + 桥，server-1.20.2 "Done (5.019s)"
- [x] B8 死静态方法的调用侧重定向：Chain 加载 shims-<ver>.json 的 staticRedirects/covers；转换期 invokestatic 改指运行时 Statics 类；首条 Ingredient.fromJson 实测生效（bewitchment ×2 调用点）
- [x] B6 回归：270 jar 完整管线重跑，引擎零错误；分布 direct 126→143 / partial 118→71 / degraded 4→12 / degraded_partial 22→44（partial 下降主因：此前虚高——apply 必崩的 L2 mixin 现被正确摘除）
- [x] B7 汇总报告 + PushNotification 叫用户

## 进度日志

- **周期 1（B1 完成）**：270 jar 全过，引擎零错误。判定 @1.20.2：direct 126 / partial 118 / degraded_partial 22 / degraded 4（对比 @1.21.11 direct 仅 44——距离梯度实证）。受损引用 1988 条，工单表 top：`class_2540.method_10794`(FriendlyByteBuf.writeNbt, L2×44)、`class_1856.method_52177`(Ingredient.fromJson, L3×29)、`class_26.method_17924`(DimensionDataStorage.computeIfAbsent, L2×23)、`class_2447/class_2450`(配方 Builder 族 ×~40)、`class_1735.method_48931`(Slot.setByPlayer ×18)、`class_1860.method_8114`(Recipe.getId, L3×15)、`class_161$class_162`(Advancement$Builder 族)。B3 shim 按此表排序。下周期做 B2（intermediary 编译类路径 remap-jar）。
- **周期 2（B2 完成）**：`remap-jar` 命令落地（TinyMappings.Obf 视图 + ASM ClassRemapper，SKIP_CODE 出 stub jar 绕开 frame 问题）。官方 1.20.2 client.jar（23MB）→ `data/jars/client-1.20.2-intermediary.jar`（7536 类）。javap 验证：`class_2540.method_10794` 新签名收 `class_2520`（Tag 超类，shim=一行委托老重载）；`class_1856.method_8089` 变 boolean 参。下周期 B3：按工单写 1.20.2 shim mixin 并组装运行时桥。
- **周期 3（B3 首批 + GitHub 同步建立）**：仓库推送至 github.com/dextawa/CenturyBridge（初始提交 + 凭据缓存可用）。六个 shim mixin 编译打包 `centurybridge-0.3.0+1.20.2.jar`：FriendlyByteBuf.writeNbt 窄参重载(×44)、Slot.setByPlayer 单参桥(×18)、BlockPointer record 老访问器桥(×20，注意 method_10120=BlockState 非 pos)、Ingredient.fromJson codec 复活+toJson 无参桥(×50)、DimensionDataStorage Factory 包装桥(×23，DataFixTypes=null 待实测)、EditBox.tick no-op(×18, client)。1.20.1 intermediary stub jar 顺手产出（老签名查询基建）。**已知延后项**：配方 Builder 族 unlockedBy（criterion 包装非平凡）、Advancement$Builder、Recipe.getId(id 已移出 Recipe，不可简单桥)、class_5258.field_24388(字段桥语义存疑)——先走墓碑。循环节奏改为 300s。下周期 B5：挑受损模组实机验收本批 shim。
- **周期 4（B5 完成 + 两个引擎缺口修复）**：实机验收暴露并修复：① Mixin 禁止非私有 static 方法 → 静态复活从 shim 移除，立项 B8 调用侧重定向；② **JIJ 嵌套 jar 需要完整递归 triage**（potionparticlepack 内嵌老 cardinal-components 的 MixinPlayerManager 注入 method_14570 签名变化，apply 硬崩）→ JarProcessor.processEntries 递归化 + MixinInfo.applyFatal 策略（L3 全摘；显式描述符的 inject-target/@At L2 也摘）。最终 "Done (5.019s)"：桥 + 受损模组 + 回归模组全部上线。待办顺延：B4 墓碑桩、B6 回归、B8 静态重定向。
- **周期 10（批次 4：criterion 族主力）**：AbstractCriterion 擦除桥（返回 T 的 bound 从 class_195 变 class_8788 接口——运行时对象同一，转型即桥）、Criteria.method_767 双端反射静态重定向（两个版本都 private，模组本靠 AW 触达）、RecipeSerializer 双方法实例重定向（json 走 codec()、buf 降参）、PacketByteBuf.method_30617 一行桥。**201/270 = 74.4%，残留 565→421 条**，boot "Done (4.652s)"。账上明确未修：class_5335（loot 序列化器接口整体删除→需要 facade 类机制，engine backlog）、class_3244 字段/方法迁移（需 instanceFieldRenames 机制）、class_195.method_807（参数类型 class_5267 存亡待查）、Advancement/Builder 序列化（method_698/689）。
- **周期 11（facade 类 + 字段改名两机制落地）**：classRenames（死类引用经 ASM ClassRemapper 一致性改名到 CB 命名空间 facade——不往 net/minecraft 塞类，零 loader 风险；组合律：调用点描述符同步改写，桥 mixin 直接按 facade 类型声明重载即匹配）+ fieldRenames（实例字段原地改名，继承解析触达父类新址）。条目：class_5335/class_5267 facade、class_3244.field_14127(connection)→父类 field_45013 + CB AW 放开 protected（发现新损伤类别：**模组自带 AW 引用死成员**，转换器需重写 AW 文件——记入 backlog）。修复 owner-被改名时成员误报。**202/270 = 74.8%，残留 421→392**，boot "Done (4.686s)"（AW intermediary 命名空间验证通过）。
- **周期 12（批次 5：第七机制 + 75% 线突破）**：methodRenames 机制（方法迁移改名，继承解析触达父类新址）+ 首条目 disconnect(method_14367→父类 method_52396)；Entity.method_5678 近似重加（骑乘偏移，默认 0.0）；ItemPredicate.fromJson Optional 化重定向（ANY 常量也死了→空 Builder 造 match-all）；RecipeManager.deserialize protected 化反射重定向；ShapelessRecipe.getId 键补充。**204/270 = 75.6%（75% 线过），残留 392→366**，boot "Done (4.585s)"。账上延后：进度序列化对（Builder.method_698/Advancement.method_689，需 1.20.2 侧序列化路径设计）、loot 序列化集群（class_120/85$90 族）、criterion conditions 族杂项。
- **周期 13（批次 6：进度序列化对复活）**：Advancement.method_689（toBuilder）在 1.20.2 Builder 上从 record 组件完整重建；Builder.method_698（toJson）经 scratch-id build + 新版 method_53621 序列化；LocationPredicate 同款 Optional 化对（fromJson 重定向 + ANY 字段重定向，空 Builder 造 match-all，build=method_9023）。**205/270 = 75.9%，残留 366→342**，boot "Done (4.772s)"。
- **周期 14（SymbolAudit 工具 + 批次 7）**：`audit` 命令落地——残留报告全量符号（246 个）对照双 stub 一表输出"老签名/新命运（GONE/DESC-CHANGED/MOVED）"，考古成本从每符号一轮降为一次。批次 7 按表填：Optional 化 fromJson 五连（damage/damageSource/distance/entity predicate + entityPredicate-in-json）、class_26.method_20786 Factory 包装、class_163.method_716 PlacedAdvancement 拆包。**206/270 = 76.3%，残留 342→325**，boot "Done (4.731s)"。表上判明的账：滚轮族(method_25401)=client 侧；criterion Builder 族=datagen 味 quirk 层记账；class_193 字段跨 owner 迁移+类型变=结构性，需 facade 设计。
- **周期 15（批次 8）**：Builder.parent(Advancement) 与 grantCriterion(Advancement,name) 经 Trackers 桥接；ANY 常量三连字段重定向（Item/Entity/Nbt Predicate，空 Builder/null-nbt 构造）；ShapedRecipe.Serializer 读取对（新版自带单参 method_8163 + codec 接口）。**残留 325→305**（206/270 = 76.3% 持平——剩余损伤在 partial 模组内叠加分布），boot "Done (4.541s)"。quirk 层已记账：class_2035/2102 ANY（构造链过深）、criterion Builder 族、loot 集群、client 滚轮族。
- **周期 16（quirk 记账机制上线）**：shims 数据加 ledger 表（符号/类级 → 分类原因，18 条初始账目：loot codec 集群、criterion 容器/工厂族、谓词 Builder 族、深链 ANY 等），JarProcessor 查账扣除，Main 输出**未记账残留**（真正的闭合指标）。附带修复：ledger 外层类回退（内部类命中）、实例重定向接住 INVOKESPECIAL（super 调用）。**未记账 202 / 已记账 274 / server-clean 212/270 = 78.5%**，boot "Done (4.663s)"。距 <50 闭合线还差 ~152 条，剩余是平坦长尾（每符号 1-3×）。
- **周期 5（B8+B4 完成 + 两个正确性修复）**：调用点改写 pass 落地（ASM 全量重发射）：静态重定向查 shims-1.20.2.json 表改 owner；L3 方法调用改指 per-jar 合成 Tombstones 类（UOE+athrow，消息含卒年边界）。实测立刻抓到两个正确性 bug 并修复：① shim 已覆盖的签名被误墓碑（会把能跑的调用改成必炸）→ Chain.shimCovers 覆盖面表；② 模组自有类上重写死接口方法（如 Recipe.getId 的实现）被误墓碑 → 成员检查限定 net/minecraft owner。复测：bewitchment 双 fromJson 调用点重定向 ✓、双层嵌套 (bundled)(bundled) triage ✓、potionparticlepack 升为 ok_degraded。剩余：B6 全语料回归、B7 汇总通知。
- **周期 6（B6+B7，循环收官）**：完整管线全语料回归零崩溃，direct 143/270；服务端最终回归 "Done (4.703s)"。**Backlog 全部完成，1.20.1→1.20.2 分段达到"生产完整"定义**：分段数据 + 验证归因 + mixin triage（递归 JIJ）+ 六个 shim mixin + 静态重定向 + 墓碑桩 + 实机验收。方法论就绪，可复制到 1.20.2→1.20.3 及后续分段。
- **行为层测试指引（留给用户）**：`data/runtime/server-1.20.2` 里 `java -jar fabric-server.jar nogui` 起服（mods 含桥+potionparticlepack+crop），用 MCProxyAgent(-javaagent) + ViaVersion 连入游玩：确认 crop 的作物生长修改生效、potionparticlepack 的药水粒子正常、无 Tombstone 异常弹出。
