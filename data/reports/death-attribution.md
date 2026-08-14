# 死亡归因：1.20.1 → 26.2 语料受损符号 × 21 条边界

| 边界 | 死亡符号数 | 波及(加权) | 签名变形数 | 波及(加权) |
|------|-----------|-----------|-----------|-----------|
| 1.20.1->1.20.2 | 190 | 567 | 196 | 792 |
| 1.20.2->1.20.3 | 103 | 320 | 53 | 188 |
| 1.20.3->1.20.4 | 0 | 0 | 0 | 0 |
| 1.20.4->1.20.5 | 294 | 1683 | 455 | 2321 |
| 1.20.5->1.20.6 | 0 | 0 | 0 | 0 |
| 1.20.6->1.21 | 157 | 729 | 151 | 644 |
| 1.21->1.21.1 | 0 | 0 | 0 | 0 |
| 1.21.1->1.21.2 | 427 | 2136 | 360 | 1946 |
| 1.21.2->1.21.3 | 0 | 0 | 0 | 0 |
| 1.21.3->1.21.4 | 72 | 333 | 22 | 87 |
| 1.21.4->1.21.5 | 389 | 1604 | 173 | 1287 |
| 1.21.5->1.21.6 | 98 | 382 | 103 | 506 |
| 1.21.6->1.21.7 | 0 | 0 | 0 | 0 |
| 1.21.7->1.21.8 | 0 | 0 | 0 | 0 |
| 1.21.8->1.21.9 | 180 | 914 | 110 | 435 |
| 1.21.9->1.21.10 | 0 | 0 | 0 | 0 |
| 1.21.10->1.21.11 | 195 | 1049 | 55 | 319 |
| 1.21.11->26.1 | 594 | 4912 | 51 | 253 |
| 26.1->26.1.1 | 0 | 0 | 0 | 0 |
| 26.1.1->26.1.2 | 0 | 0 | 0 | 0 |
| 26.1.2->26.2 | 725 | 2859 | 27 | 87 |

## 1.20.1->1.20.2
- `FriendlyByteBuf.writeNbt` (签名, 波及 36 模组)
- `Ingredient.fromJson` (死亡, 波及 21 模组)
- `DimensionDataStorage.computeIfAbsent` (签名, 波及 20 模组)
- `ShapedRecipeBuilder.unlockedBy` (签名, 波及 18 模组)
- `Slot.setByPlayer` (签名, 波及 17 模组)
- `ShapelessRecipeBuilder.unlockedBy` (签名, 波及 17 模组)
- `ShapelessRecipeBuilder.save` (签名, 波及 15 模组)
- `Recipe.getId` (死亡, 波及 15 模组)

## 1.20.2->1.20.3
- `AbstractCriterionTriggerInstance` (死亡, 波及 19 模组)
- `Block.playerWillDestroy` (签名, 波及 18 模组)
- `MobEffectUtil.formatDuration` (签名, 波及 15 模组)
- `FinishedRecipe` (死亡, 波及 14 模组)
- `SimpleCriterionTrigger.createInstance` (死亡, 波及 13 模组)
- `Ingredient.toJson` (死亡, 波及 13 模组)
- `AttributeModifier.getName` (死亡, 波及 12 模组)
- `Block.getCloneItemStack` (签名, 波及 11 模组)

## 1.20.4->1.20.5
- `ItemStack.getOrCreateTag` (死亡, 波及 55 模组)
- `ItemStack.getTag` (死亡, 波及 54 模组)
- `Attributes.MOVEMENT_SPEED` (签名, 波及 42 模组)
- `ItemStack.hurtAndBreak` (签名, 波及 41 模组)
- `Attributes.ATTACK_DAMAGE` (签名, 波及 40 模组)
- `ItemStack.save` (死亡, 波及 38 模组)
- `Attributes.MAX_HEALTH` (签名, 波及 38 模组)
- `ItemStack.of` (死亡, 波及 36 模组)

## 1.20.6->1.21
- `VertexConsumer.endVertex` (死亡, 波及 48 模组)
- `BufferBuilder.begin` (死亡, 波及 33 模组)
- `Tesselator.getBuilder` (死亡, 波及 31 模组)
- `BuiltInRegistries.ENCHANTMENT` (死亡, 波及 30 模组)
- `Tesselator.end` (死亡, 波及 29 模组)
- `ModelPart.render` (签名, 波及 28 模组)
- `VertexConsumer.uv2` (死亡, 波及 27 模组)
- `EnchantmentHelper.getItemEnchantmentLevel` (签名, 波及 26 模组)

## 1.21.1->1.21.2
- `InteractionResult.SUCCESS` (签名, 波及 86 模组)
- `InteractionResult.PASS` (签名, 波及 81 模组)
- `InteractionResultHolder` (死亡, 波及 64 模组)
- `InteractionResult.FAIL` (签名, 波及 48 模组)
- `DefaultedRegistry.get` (签名, 波及 48 模组)
- `InteractionResult.CONSUME` (签名, 波及 46 模组)
- `GuiGraphics.blit` (签名, 波及 46 模组)
- `EntityType.create` (签名, 波及 46 模组)

## 1.21.3->1.21.4
- `ServerLevel.sendParticles` (签名, 波及 36 模组)
- `ItemProperties` (死亡, 波及 27 模组)
- `ItemProperties.register` (死亡, 波及 26 模组)
- `InventoryMenu.BLOCK_ATLAS` (死亡, 波及 19 模组)
- `RenderShape.ENTITYBLOCK_ANIMATED` (死亡, 波及 16 模组)
- `ItemRenderer.getModel` (死亡, 波及 16 模组)
- `ItemRenderer.render` (死亡, 波及 13 模组)
- `MetadataSectionSerializer` (死亡, 波及 11 模组)

## 1.21.4->1.21.5
- `CompoundTag.getInt` (签名, 波及 75 模组)
- `Level.playSound` (签名, 波及 65 模组)
- `Player.isCreative` (死亡, 波及 61 模组)
- `CompoundTag.getString` (签名, 波及 61 模组)
- `CompoundTag.getCompound` (签名, 波及 59 模组)
- `CompoundTag.getBoolean` (签名, 波及 58 模组)
- `PoseStack.mulPose` (签名, 波及 57 模组)
- `CompoundTag.getList` (签名, 波及 50 模组)

## 1.21.5->1.21.6
- `GuiGraphics.pose` (签名, 波及 51 模组)
- `SoundManager.play` (签名, 波及 35 模组)
- `GuiGraphics.drawString` (签名, 波及 34 模组)
- `RenderType.translucent` (死亡, 波及 27 模组)
- `GuiGraphics.drawString` (签名, 波及 27 模组)
- `class_2561$class_2562` (死亡, 波及 26 模组)
- `GuiGraphics.drawString` (签名, 波及 23 模组)
- `GuiGraphics.drawString` (签名, 波及 22 模组)

## 1.21.8->1.21.9
- `Player.level` (死亡, 波及 69 模组)
- `ServerPlayer.level` (死亡, 波及 49 模组)
- `LivingEntity.level` (死亡, 波及 41 模组)
- `Entity.level` (死亡, 波及 41 模组)
- `Entity.position` (死亡, 波及 39 模组)
- `LivingEntity.position` (死亡, 波及 34 模组)
- `Screen.keyPressed` (签名, 波及 33 模组)
- `Player.position` (死亡, 波及 32 模组)

## 1.21.10->1.21.11
- `RenderType.cutout` (死亡, 波及 55 模组)
- `CompoundTag.remove` (签名, 波及 45 模组)
- `Mth.sin` (签名, 波及 41 模组)
- `Level.getGameTime` (死亡, 波及 40 模组)
- `class_1928$class_4313` (死亡, 波及 39 模组)
- `GameRules.getBoolean` (死亡, 波及 38 模组)
- `CommandSourceStack.hasPermission` (死亡, 波及 38 模组)
- `Mth.cos` (签名, 波及 35 模组)

## 1.21.11->26.1
- `class_1792$class_1793` (死亡, 波及 117 模组)
- `GuiGraphics` (死亡, 波及 95 模组)
- `class_4970$class_2251` (死亡, 波及 89 模组)
- `class_1761$class_7913` (死亡, 波及 81 模组)
- `class_2689$class_2690` (死亡, 波及 76 模组)
- `class_2350$class_2351` (死亡, 波及 69 模组)
- `ItemStack.is` (签名, 波及 68 模组)
- `class_1761$class_7704` (死亡, 波及 67 模组)

## 26.1.2->26.2
- `MultiBufferSource` (死亡, 波及 79 模组)
- `Minecraft.setScreen` (死亡, 波及 56 模组)
- `Minecraft.screen` (死亡, 波及 41 模组)
- `class_293$class_5596.QUADS` (死亡, 波及 35 模组)
- `CriteriaTriggers` (死亡, 波及 34 模组)
- `Tesselator` (死亡, 波及 33 模组)
- `Tuple` (死亡, 波及 31 模组)
- `InventoryChangeTrigger` (死亡, 波及 22 模组)
