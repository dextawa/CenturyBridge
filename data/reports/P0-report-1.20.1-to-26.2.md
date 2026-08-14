# CenturyBridge P0 试点报告：1.20.1 → 26.2

语料：240 个死亡内容模组（KPI）+ 30 个优化/库类（单独统计）。

## 四桶判定（KPI 语料）

| 桶 | 数量 | 占比 |
|----|------|------|
| A 直通（零 shim） | 38 | 15.8% |
| B 需签名桥（自动） | 8 | 3.3% |
| C 需 facade shim / 可降级 | 140 | 58.3% |
| D 承重 Mixin 死亡 | 53 | 22.1% |
| E 无法扫描 | 1 | 0.4% |

**自动可跑（A+B）：19.2%** —— 仅靠重映射 + 自动签名桥。
**架构可覆盖（A+B+C）：77.5%** —— 加上 facade shim 与降级阶梯。

## 分类别覆盖率（KPI）

| 类别 | n | A+B | A+B+C | D |
|------|---|-----|-------|---|
| adventure | 30 | 7% | 73% | 8 |
| decoration | 30 | 0% | 63% | 11 |
| utility | 30 | 27% | 93% | 2 |
| uncategorized | 21 | 29% | 81% | 4 |
| cursed | 20 | 25% | 65% | 7 |
| game-mechanics | 20 | 15% | 70% | 6 |
| equipment | 19 | 5% | 74% | 5 |
| datapack | 17 | 94% | 94% | 0 |
| food | 11 | 18% | 73% | 3 |
| mobs | 9 | 0% | 89% | 1 |
| optimization | 7 | 14% | 57% | 3 |
| technology | 7 | 0% | 86% | 1 |
| storage | 5 | 0% | 80% | 1 |
| management | 5 | 0% | 100% | 0 |
| magic | 5 | 20% | 80% | 1 |
| worldgen | 2 | 50% | 100% | 0 |
| social | 1 | 0% | 100% | 0 |
| economy | 1 | 0% | 100% | 0 |

## Mixin 统计（KPI）

- 含 Mixin 的模组：184/240 = 77%
- Mixin 总数 6283，其中钩 vanilla 的 5688，承重 2479
- 目标/锚点已死：承重 609/2479，装饰 1253/3209

## Shim 优先级表（受损符号 × 波及模组数，前 40）

| 符号 | 等级 | 波及模组 |
|------|------|----------|
| `class_1792$class_1793` | L3 | 117 |
| `GuiGraphics` | L3 | 95 |
| `class_4970$class_2251` | L3 | 89 |
| `InteractionResult.SUCCESS` | L2 | 86 |
| `InteractionResult.PASS` | L2 | 81 |
| `class_1761$class_7913` | L3 | 81 |
| `MultiBufferSource` | L3 | 79 |
| `class_2689$class_2690` | L3 | 76 |
| `CompoundTag.getInt` | L2 | 75 |
| `class_2350$class_2351` | L3 | 69 |
| `Player.level` | L3 | 69 |
| `ItemStack.is` | L2 | 68 |
| `class_1761$class_7704` | L3 | 67 |
| `Level.playSound` | L2 | 65 |
| `class_5617$class_5618` | L3 | 65 |
| `InteractionResultHolder` | L3 | 64 |
| `class_1299$class_4049` | L3 | 63 |
| `Player.isCreative` | L3 | 61 |
| `CompoundTag.getString` | L2 | 61 |
| `CompoundTag.getCompound` | L2 | 59 |
| `class_1761$class_8128` | L3 | 59 |
| `CompoundTag.getBoolean` | L2 | 58 |
| `class_1761$class_7914` | L3 | 58 |
| `PoseStack.mulPose` | L2 | 57 |
| `Minecraft.setScreen` | L3 | 56 |
| `class_6880$class_6883` | L3 | 56 |
| `RenderType.cutout` | L3 | 55 |
| `ItemStack.getOrCreateTag` | L3 | 55 |
| `ItemStack.getTag` | L3 | 54 |
| `class_4185$class_4241` | L3 | 53 |
| `class_4587$class_4665` | L3 | 53 |
| `GuiGraphics.pose` | L2 | 51 |
| `Screen.render` | L3 | 50 |
| `CompoundTag.contains` | L3 | 50 |
| `CompoundTag.getList` | L2 | 50 |
| `class_5614$class_5615` | L3 | 49 |
| `ServerPlayer.level` | L3 | 49 |
| `DefaultedRegistry.get` | L2 | 48 |
| `InteractionResult.FAIL` | L2 | 48 |
| `VertexConsumer.endVertex` | L3 | 48 |

## 覆盖率曲线：修复前 N 个高频符号后，C 桶还剩多少

| shim 前 N 符号 | C 桶模组恢复为 A/B |
|---------------|--------------------|
| 10 | 1/140 |
| 25 | 2/140 |
| 50 | 5/140 |
| 100 | 7/140 |
| 200 | 10/140 |

## Keystone 依赖（语料内被依赖次数）

- `fabric-resource-loader-v0`: 21
- `architectury`: 19
- `create`: 16
- `geckolib`: 10
- `cloth-config`: 10
- `patchouli`: 8
- `valkyrienskies`: 7
- `farmersdelight`: 6
- `trinkets`: 6
- `cloth-config2`: 6
- `forgeconfigapiport`: 5
- `necronomicon`: 5
- `azurelib`: 4
- `moonlight`: 4
- `porting_lib_accessors`: 3
