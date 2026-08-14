# CB 循环任务：1.20.1 → 1.20.2 完整移植

> 目标：这条链上第一节分段达到"生产完整"——不只验证与摘除，还包括该边界的 shim 桥、墓碑桩、真实受损模组的实机验收。链条设计自底向上，做完这节的方法论直接复制到后续节。
> 完成或阻塞时 PushNotification 通知用户。行为层验证：用户用 D:\Documents\MCProxyAgent（-javaagent 代理）+ ViaVersion 连测试服游玩确认。

## Backlog

- [x] B1 语料扫荡 @1.20.2：全 270 jar convert，统计受损模组清单 + top 受损符号（= shim 工单表），写入 core/out-1.20.2-sweep/
- [x] B2 intermediary 编译类路径：datagen 新命令 remap-jar（ASM ClassRemapper：官方 1.20.2 client.jar obf→intermediary），产出供 javac 编译 shim 用的类路径 jar
- [ ] B3 1.20.2 运行时桥：按 B1 工单写 shim mixin（预计 FriendlyByteBuf.writeNbt / Ingredient.fromJson / Recipe.getId / Shaped(less)RecipeBuilder 一族），组装 centurybridge-1.20.2 运行时 jar
- [ ] B4 墓碑桩生成器（ASM 合成惰性失败桩，接入 JarProcessor 的 ok_partial 路径）
- [x] B5 实机验收：potionparticlepack（writeNbt 受损 + JIJ 内嵌 mixin 死亡）convert + 桥，server-1.20.2 "Done (5.019s)"
- [ ] B8 死静态方法的调用侧重定向：Mixin 禁止非私有 static（实测确认），转换期把 invokestatic 重定向到运行时 Statics 类（等长替换）；首个条目 Ingredient.fromJson(method_52177, ×29)
- [ ] B6 回归：全语料 convert 引擎零错误保持；判定分布与 B1 对比记录
- [ ] B7 汇总报告 + PushNotification 叫用户（附行为层测试指引）

## 进度日志

- **周期 1（B1 完成）**：270 jar 全过，引擎零错误。判定 @1.20.2：direct 126 / partial 118 / degraded_partial 22 / degraded 4（对比 @1.21.11 direct 仅 44——距离梯度实证）。受损引用 1988 条，工单表 top：`class_2540.method_10794`(FriendlyByteBuf.writeNbt, L2×44)、`class_1856.method_52177`(Ingredient.fromJson, L3×29)、`class_26.method_17924`(DimensionDataStorage.computeIfAbsent, L2×23)、`class_2447/class_2450`(配方 Builder 族 ×~40)、`class_1735.method_48931`(Slot.setByPlayer ×18)、`class_1860.method_8114`(Recipe.getId, L3×15)、`class_161$class_162`(Advancement$Builder 族)。B3 shim 按此表排序。下周期做 B2（intermediary 编译类路径 remap-jar）。
- **周期 2（B2 完成）**：`remap-jar` 命令落地（TinyMappings.Obf 视图 + ASM ClassRemapper，SKIP_CODE 出 stub jar 绕开 frame 问题）。官方 1.20.2 client.jar（23MB）→ `data/jars/client-1.20.2-intermediary.jar`（7536 类）。javap 验证：`class_2540.method_10794` 新签名收 `class_2520`（Tag 超类，shim=一行委托老重载）；`class_1856.method_8089` 变 boolean 参。下周期 B3：按工单写 1.20.2 shim mixin 并组装运行时桥。
- **周期 3（B3 首批 + GitHub 同步建立）**：仓库推送至 github.com/dextawa/CenturyBridge（初始提交 + 凭据缓存可用）。六个 shim mixin 编译打包 `centurybridge-0.3.0+1.20.2.jar`：FriendlyByteBuf.writeNbt 窄参重载(×44)、Slot.setByPlayer 单参桥(×18)、BlockPointer record 老访问器桥(×20，注意 method_10120=BlockState 非 pos)、Ingredient.fromJson codec 复活+toJson 无参桥(×50)、DimensionDataStorage Factory 包装桥(×23，DataFixTypes=null 待实测)、EditBox.tick no-op(×18, client)。1.20.1 intermediary stub jar 顺手产出（老签名查询基建）。**已知延后项**：配方 Builder 族 unlockedBy（criterion 包装非平凡）、Advancement$Builder、Recipe.getId(id 已移出 Recipe，不可简单桥)、class_5258.field_24388(字段桥语义存疑)——先走墓碑。循环节奏改为 300s。下周期 B5：挑受损模组实机验收本批 shim。
- **周期 4（B5 完成 + 两个引擎缺口修复）**：实机验收暴露并修复：① Mixin 禁止非私有 static 方法 → 静态复活从 shim 移除，立项 B8 调用侧重定向；② **JIJ 嵌套 jar 需要完整递归 triage**（potionparticlepack 内嵌老 cardinal-components 的 MixinPlayerManager 注入 method_14570 签名变化，apply 硬崩）→ JarProcessor.processEntries 递归化 + MixinInfo.applyFatal 策略（L3 全摘；显式描述符的 inject-target/@At L2 也摘）。最终 "Done (5.019s)"：桥 + 受损模组 + 回归模组全部上线。待办顺延：B4 墓碑桩、B6 回归、B8 静态重定向。
