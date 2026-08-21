package top.dext.centurybridge.rt.v26_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
/**
 * Caller-side homes for dead STATIC methods (Mixin cannot add non-private
 * statics to targets). The converter rewrites old invokestatic call sites to
 * point here; signatures must match the dead originals exactly.
 */
public final class Statics {

    /** 1.20.1 Ingredient.fromJson(JsonElement), removed 1.20.2 (codec era). L3 x29. */
    public static net.minecraft.world.item.crafting.Ingredient method_52177(JsonElement json) {
        return net.minecraft.world.item.crafting.Ingredient.CODEC.parse(cbJsonOps(), json)
            .result()
            .orElseThrow(() -> new JsonSyntaxException("Invalid ingredient: " + json));
    }


    /** 1.20.1 FluidDrainable.tryDrainFluid gained a leading @Nullable player param in 1.20.2.
     *  Interface methods cannot be re-added via Mixin; call sites are redirected here. L2 x19. */
    public static net.minecraft.world.item.ItemStack method_9700(net.minecraft.world.level.block.BucketPickup self,
            net.minecraft.world.level.LevelAccessor world, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        return self.pickupBlock(null, world, pos, state);
    }


    /** 1.21.2 keyed recipes by registry key; every bridge holding a bare id goes through here. */
    public static net.minecraft.resources.ResourceKey<net.minecraft.world.item.crafting.Recipe<?>> cbRecipeKey(net.minecraft.resources.Identifier id) {
        return net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, id);
    }

    /** 1.20.1 Ingredient.fromJson(json, allowEmpty), removed 1.20.2. L3 x9. */
    public static net.minecraft.world.item.crafting.Ingredient method_8102(JsonElement json, boolean allowEmpty) {
        // 1.21.2 dropped empty-ingredient support; the surviving codec covers both
        var codec = net.minecraft.world.item.crafting.Ingredient.CODEC;
        return codec.parse(cbJsonOps(), json)
            .result()
            .orElseThrow(() -> new JsonSyntaxException("Invalid ingredient: " + json));
    }

    /** 1.20.1 Fertilizable.canGrow lost its trailing client-side flag in 1.20.2. Interface -> call-site redirect. L2 x9. */
    public static boolean method_9651(net.minecraft.world.level.block.BonemealableBlock self, net.minecraft.world.level.LevelReader world,
            net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, boolean isClient) {
        return self.isValidBonemealTarget(world, pos, state);
    }

    /** legacy Recipe.getId(): ids moved to RecipeEntry in 1.20.2; answered from the tracker map. */
    public static net.minecraft.resources.Identifier method_8114(net.minecraft.world.item.crafting.Recipe<?> self) {
        net.minecraft.resources.Identifier id = Trackers.RECIPE_IDS.get(self);
        if (id == null) {
            throw new UnsupportedOperationException(
                "CenturyBridge: recipe id unavailable -- 1.20.2 moved ids to RecipeEntry"
                + " and this recipe was not registered through RecipeManager");
        }
        return id;
    }

    public static net.minecraft.resources.Identifier method_8114(net.minecraft.world.item.crafting.ShapedRecipe self) {
        return method_8114((net.minecraft.world.item.crafting.Recipe<?>) self);
    }

    public static net.minecraft.resources.Identifier method_8114(net.minecraft.world.item.crafting.CraftingRecipe self) {
        return method_8114((net.minecraft.world.item.crafting.Recipe<?>) self);
    }

    /** 1.20.1 LootContextPredicate.EMPTY field, removed 1.20.2; field access redirected here. */
    public static final net.minecraft.advancements.predicates.ContextAwarePredicate field_24388 = net.minecraft.advancements.predicates.ContextAwarePredicate.create();

    /** 1.20.1 Criteria.register(trigger) -- private in both eras (mods reached it via AW).
     *  New shape needs a name; recovered from the legacy trigger's getId via reflection. L3 x12. */
    @SuppressWarnings("unchecked")
    public static net.minecraft.advancements.triggers.CriterionTrigger<?> method_767(net.minecraft.advancements.triggers.CriterionTrigger<?> trigger) {
        try {
            net.minecraft.resources.Identifier id = (net.minecraft.resources.Identifier)
                trigger.getClass().getMethod("method_794").invoke(trigger);
            var register = net.minecraft.advancements.triggers.CriteriaTriggers.class.getDeclaredMethod(
                "register", String.class, net.minecraft.advancements.triggers.CriterionTrigger.class);
            register.setAccessible(true);
            return (net.minecraft.advancements.triggers.CriterionTrigger<?>) register.invoke(null, id.toString(), trigger);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                "CenturyBridge: cannot register legacy criterion trigger " + trigger.getClass().getName(), e);
        }
    }



    
    /** 1.20.1 RecipeManager.deserialize: became protected and returns RecipeEntry. L3 x4. */
    public static net.minecraft.world.item.crafting.Recipe<?> method_17720(net.minecraft.resources.Identifier id, com.google.gson.JsonObject json) {
        try {
            var m = net.minecraft.world.item.crafting.RecipeManager.class.getDeclaredMethod(
                "fromJson", net.minecraft.resources.Identifier.class, com.google.gson.JsonObject.class);
            m.setAccessible(true);
            net.minecraft.world.item.crafting.RecipeHolder<?> entry = (net.minecraft.world.item.crafting.RecipeHolder<?>) m.invoke(null, id, json);
            return entry.value();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("CenturyBridge: recipe deserialize failed for " + id, e);
        }
    }

    public static net.minecraft.resources.Identifier method_8114(net.minecraft.world.item.crafting.ShapelessRecipe self) {
        return method_8114((net.minecraft.world.item.crafting.Recipe<?>) self);
    }

    
    /** ANY-constant family, removed with the predicate rework; rebuilt as match-alls. */
    public static final net.minecraft.advancements.predicates.ItemPredicate field_9640 =
        net.minecraft.advancements.predicates.ItemPredicate.Builder.item().build();

    public static final net.minecraft.advancements.predicates.entity.EntityPredicate field_9599 =
        net.minecraft.advancements.predicates.entity.EntityPredicate.Builder.entity().build();

    public static final net.minecraft.advancements.predicates.NbtPredicate field_9716 =
        new net.minecraft.advancements.predicates.NbtPredicate((net.minecraft.nbt.CompoundTag) null);



    public static net.minecraft.resources.Identifier method_8114(net.minecraft.world.item.crafting.AbstractCookingRecipe self) {
        return method_8114((net.minecraft.world.item.crafting.Recipe<?>) self);
    }

    public static net.minecraft.resources.Identifier method_8114(net.minecraft.world.item.crafting.SmokingRecipe self) {
        return method_8114((net.minecraft.world.item.crafting.Recipe<?>) self);
    }

    public static net.minecraft.resources.Identifier method_8114(net.minecraft.world.item.crafting.SmeltingRecipe self) {
        return method_8114((net.minecraft.world.item.crafting.Recipe<?>) self);
    }

    public static net.minecraft.resources.Identifier method_8114(net.minecraft.world.item.crafting.CampfireCookingRecipe self) {
        return method_8114((net.minecraft.world.item.crafting.Recipe<?>) self);
    }

    public static boolean method_9651(net.minecraft.world.level.block.CropBlock self, net.minecraft.world.level.LevelReader world,
            net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, boolean isClient) {
        return ((net.minecraft.world.level.block.BonemealableBlock) self).isValidBonemealTarget(world, pos, state);
    }

    /** 1.20.1 Waterloggable.tryFillWithFluid gained a leading player param (same pattern as FluidDrainable). */
    public static boolean method_10310(net.minecraft.world.level.block.LiquidBlockContainer self, net.minecraft.world.level.BlockGetter world,
            net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.material.Fluid fluid) {
        return self.canPlaceLiquid(null, world, pos, state, fluid);
    }

    public static boolean method_10310(net.minecraft.world.level.block.SimpleWaterloggedBlock self, net.minecraft.world.level.BlockGetter world,
            net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.material.Fluid fluid) {
        return ((net.minecraft.world.level.block.LiquidBlockContainer) self).canPlaceLiquid(null, world, pos, state, fluid);
    }

    /** 1.20.1 CraftingResultInventory.setLastRecipe(Recipe) -> new takes RecipeEntry; rebuilt via tracker. */
    public static void method_7662(net.minecraft.world.inventory.ResultContainer self, net.minecraft.world.item.crafting.Recipe<?> recipe) {
        if (recipe == null) {
            ((net.minecraft.world.inventory.RecipeCraftingHolder) self).setRecipeUsed(null);
            return;
        }
        net.minecraft.resources.Identifier id = Trackers.RECIPE_IDS.get(recipe);
        ((net.minecraft.world.inventory.RecipeCraftingHolder) self).setRecipeUsed(
            id == null ? null : new net.minecraft.world.item.crafting.RecipeHolder<>(cbRecipeKey(id), recipe));
    }

    /** 1.20.3 added a tick-rate parameter to StatusEffectUtil.getDurationText. */
    public static net.minecraft.network.chat.Component method_5577(net.minecraft.world.effect.MobEffectInstance instance, float multiplier) {
        return net.minecraft.world.effect.MobEffectUtil.formatDuration(instance, multiplier, 1.0F);
    }

    /** 1.20.1 ItemPredicate.fromJson, reimplemented on the 1.20.3+ codec (hit at init by real mods). */
    public static net.minecraft.advancements.predicates.ItemPredicate method_8969(JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return net.minecraft.advancements.predicates.ItemPredicate.Builder.item().build();
        }
        return net.minecraft.advancements.predicates.ItemPredicate.CODEC.parse(cbJsonOps(), json)
            .result()
            .orElseThrow(() -> new JsonSyntaxException("Invalid item predicate: " + json));
    }

    /** 1.20.1 ItemPredicate.toJson, reimplemented on the codec's encode side. */
    public static JsonElement method_8971(net.minecraft.advancements.predicates.ItemPredicate self) {
        return net.minecraft.advancements.predicates.ItemPredicate.CODEC.encodeStart(cbJsonOps(), self)
            .result()
            .orElse(com.google.gson.JsonNull.INSTANCE);
    }






    /**
     * 1.20.1 Item.onCraft(stack, world, player): the player parameter was
     * dropped at 1.20.2. Declared on net.minecraft.world.item.Item, so mods calling it through
     * ItemStack/BlockItem subclasses land here too.
     */
    public static void method_7843(net.minecraft.world.item.Item self,
            net.minecraft.world.item.ItemStack stack, net.minecraft.world.level.Level world,
            net.minecraft.world.entity.player.Player player) {
        self.onCraftedPostProcess(stack, world);
    }

    /**
     * 1.20.1 CraftingResultInventory.shouldCraftRecipe(world, player, recipe):
     * the recipe became a RecipeEntry. The tracker supplies the id the recipe
     * was registered under, so the old call still resolves.
     */
    public static boolean method_7665(net.minecraft.world.inventory.ResultContainer self,
            net.minecraft.world.level.Level world, net.minecraft.server.level.ServerPlayer player,
            net.minecraft.world.item.crafting.Recipe<?> recipe) {
        net.minecraft.resources.Identifier id = Trackers.RECIPE_IDS.get(recipe);
        if (id == null) {
            return false;
        }
        // 1.21.2 dropped the World parameter
        return ((net.minecraft.world.inventory.RecipeCraftingHolder) self)
            .setRecipeUsed(player, new net.minecraft.world.item.crafting.RecipeHolder<>(cbRecipeKey(id), recipe));
    }

    // ==== GENERATED BRIDGES (assemble.py) ====
    /** was net/minecraft/net.minecraft.client.resources.language.LanguageManager.field_32971Ljava/lang/String; */
    public static java.lang.String field_32971() {
        return "textures/atlas/";
    }


    /** was net/minecraft/net.minecraft.client.sounds.SoundEventListener.onPlaySound(Lnet/minecraft/net.minecraft.client.resources.sounds.SoundInstance;Lnet/minecraft/net.minecraft.client.sounds.WeighedSoundEvents;)V */
    public void onPlaySound(net.minecraft.client.resources.sounds.SoundInstance a0, net.minecraft.client.sounds.WeighedSoundEvents a1) {
        ((net.minecraft.client.sounds.SoundEventListener) (Object) this).onPlaySound(a0, a1, 1.0f);
    }

    /** was net/minecraft/net.minecraft.world.level.pathfinder.Path.method_22880()[Lnet/minecraft/net.minecraft.world.level.pathfinder.Node; */
    public static net.minecraft.world.level.pathfinder.Node[] method_22880(net.minecraft.world.level.pathfinder.Path self) {
        int length = self.getNodeCount();
        net.minecraft.world.level.pathfinder.Node[] directions = new net.minecraft.world.level.pathfinder.Node[length];
        for (int i = 0; i < length; i++) {
            directions[i] = self.getNode(i);
        }
        return directions;
    }

    /** was net/minecraft/net.minecraft.world.level.pathfinder.Path.method_22881()[Lnet/minecraft/net.minecraft.world.level.pathfinder.Node; */
    public static net.minecraft.world.level.pathfinder.Node[] method_22881(net.minecraft.world.level.pathfinder.Path self) {
        int length = self.getNextNodeIndex();
        net.minecraft.world.level.pathfinder.Node[] nodes = new net.minecraft.world.level.pathfinder.Node[length];
        for (int i = 0; i < length; i++) {
            nodes[i] = self.getNode(i);
        }
        return nodes;
    }

    /** was net/minecraft/net.minecraft.util.datafix.fixes.References.field_5732Lcom/mojang/datafixers/DSL$TypeReference; */
    public static com.mojang.datafixers.DSL.TypeReference field_5732() {
        throw new UnsupportedOperationException("CenturyBridge: field_5732 was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/net.minecraft.util.datafix.fixes.StatsCounterFix.upgradeItem(Ljava/lang/String;)Ljava/lang/String; */
    public static java.lang.String method_5172(net.minecraft.util.datafix.fixes.StatsCounterFix self, java.lang.String a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.util.datafix.fixes.StatsCounterFix.upgradeItem(String) was deleted in 1.20.2; the new API surface only exposes <init> and makeRule(), with no String-to-String transformation available to delegate to");
    }

    /** was net/minecraft/net.minecraft.util.datafix.fixes.StatsCounterFix.upgradeBlock(Ljava/lang/String;)Ljava/lang/String; */
    public static java.lang.String method_5173(net.minecraft.util.datafix.fixes.StatsCounterFix self, java.lang.String a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.util.datafix.fixes.StatsCounterFix.upgradeBlock(String) was removed in 1.20.2; the rename callback is no longer exposed and is inlined into makeRule(), so the old per-name transform contract cannot be reproduced on the new type");
    }


    /** was net/minecraft/net.minecraft.world.effect.MobEffectInstance.method_5589(Lnet/minecraft/net.minecraft.world.entity.LivingEntity;)V */
    public static void method_5589(net.minecraft.world.effect.MobEffectInstance self, net.minecraft.world.entity.LivingEntity a0) {
        self.onEffectStarted(a0);
    }

    /** was net/minecraft/net.minecraft.world.entity.Entity.method_20233(Lnet/minecraft/net.minecraft.world.entity.Pose;)Z */
    public static boolean method_20233(net.minecraft.world.entity.Entity self, net.minecraft.world.entity.Pose a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_20233(EntityPose) was removed in 1.20.2 with no direct replacement");
    }


    /** was net/minecraft/net.minecraft.world.entity.Entity.method_5621()D */
    public static double method_5621(net.minecraft.world.entity.Entity self) {
        return (double) self.getBbHeight();
    }

    /** was net/minecraft/net.minecraft.world.entity.Mob.method_33191(Lnet/minecraft/net.minecraft.world.entity.LivingEntity;)D */
    public static double method_33191(net.minecraft.world.entity.Mob self, net.minecraft.world.entity.LivingEntity a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_33191 (MobEntity.getAttackDistanceScalingFactor) was removed in 1.20.2 with no direct replacement; override-based virtual dispatch cannot be replicated in a static shim");
    }

    /** was net/minecraft/net.minecraft.world.entity.Mob.method_47922(Lnet/minecraft/net.minecraft.world.entity.LivingEntity;)D */
    public static double method_47922(net.minecraft.world.entity.Mob self, net.minecraft.world.entity.LivingEntity a0) {
        return (double) (self.getBbWidth() * 2.0F * self.getBbWidth() * 2.0F + a0.getBbWidth());
    }

    /** was net/minecraft/net.minecraft.world.entity.LivingEntity.field_6242D */
    public static double field_6242() {
        return 1.3D;
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.functions.LootItemFunctions.method_29322()Ljava/lang/Object; */
    public static java.lang.Object method_29322(net.minecraft.world.level.storage.loot.functions.LootItemFunctions self) {
        throw new UnsupportedOperationException("CenturyBridge: Gson type handler for loot functions was removed in 1.20.2");
    }

    /** was net/minecraft/net.minecraft.world.entity.ai.attributes.AttributeInstance.removeModifier(Lnet/minecraft/net.minecraft.world.entity.ai.attributes.AttributeModifier;)V */
    public static void method_6202(net.minecraft.world.entity.ai.attributes.AttributeInstance self, net.minecraft.world.entity.ai.attributes.AttributeModifier a0) {
        self.addTransientModifier(a0);
    }

    /** was net/minecraft/net.minecraft.world.entity.ambient.Bat.field_28637I */
    public static int field_28637() {
        throw new UnsupportedOperationException("CenturyBridge: Field field_28637 was deleted without replacement");
    }

    /** was net/minecraft/net.minecraft.world.entity.ambient.Bat.field_30268F */
    public static float field_30268() {
        return 0.2f;
    }

    /** was net/minecraft/net.minecraft.advancements.AdvancementTree$class_164.onAddAdvancementTask(Lnet/minecraft/net.minecraft.advancements.Advancement;)V */
    public void onAddAdvancementTask(net.minecraft.advancements.Advancement a0) {
        ((net.minecraft.advancements.AdvancementTree.Listener) (Object) this).onAddAdvancementTask(new net.minecraft.advancements.AdvancementNode((net.minecraft.advancements.AdvancementHolder) (Object) a0, null));
    }

    /** was net/minecraft/net.minecraft.advancements.AdvancementTree.method_712()Ljava/util/Collection; */
    public static java.util.Collection method_712(net.minecraft.advancements.AdvancementTree self) {
        return self.nodes();
    }

    /** was net/minecraft/net.minecraft.world.entity.player.Player.method_7282(DDD)V */
    public static void method_7282(net.minecraft.world.entity.player.Player self, double a0, double a1, double a2) {
        throw new UnsupportedOperationException("CenturyBridge: PlayerEntity.increaseTravelStats (method_7282) was removed in 1.20.2 and has no direct replacement.");
    }

    /** was net/minecraft/net.minecraft.world.entity.vehicle.boat.Boat.method_42279(Lnet/minecraft/net.minecraft.world.damagesource.DamageSource;)V */
    public static void method_42279(net.minecraft.world.entity.vehicle.boat.Boat self, net.minecraft.world.damagesource.DamageSource a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_42279(DamageSource) was removed in 1.20.3; projectile damage source is now tracked via net.minecraft.world.entity.vehicle.boat.Boat$class_1692 (method_47884/method_47885) with no known conversion from DamageSource");
    }

    /** was net/minecraft/net.minecraft.world.entity.vehicle.boat.Boat.method_7533()I */
    public static int method_7533(net.minecraft.world.entity.vehicle.boat.Boat self) {
        throw new UnsupportedOperationException("CenturyBridge: TntEntity.getFuse is no longer publicly accessible");
    }

    /** was net/minecraft/net.minecraft.world.entity.vehicle.boat.Boat.method_7540(I)V */
    public static void method_7540(net.minecraft.world.entity.vehicle.boat.Boat self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_7540 is no longer supported");
    }

    /** was net/minecraft/net.minecraft.world.entity.vehicle.boat.Boat.method_7543()I */
    public static int method_7543(net.minecraft.world.entity.vehicle.boat.Boat self) {
        throw new UnsupportedOperationException("CenturyBridge: BoatEntity.getTicksUnderwater is no longer exposed");
    }


    /** was net/minecraft/net.minecraft.world.entity.vehicle.boat.Boat.method_7554()F */
    public static float method_7554(net.minecraft.world.entity.vehicle.boat.Boat self) {
        throw new UnsupportedOperationException("CenturyBridge: BoatEntity.getWaterHeight was removed in 1.20.3");
    }

    /** was net/minecraft/net.minecraft.world.inventory.RecipeCraftingHolder.setRecipeUsed(Lnet/minecraft/net.minecraft.world.item.crafting.Recipe;)V */
    public void setRecipeUsed(net.minecraft.world.item.crafting.Recipe a0) {
        net.minecraft.world.item.crafting.RecipeHolder current = ((net.minecraft.world.inventory.RecipeCraftingHolder)(Object)this).getRecipeUsed();
        if (current == null) {
            throw new UnsupportedOperationException("CenturyBridge: cannot derive RecipeEntry id from null current recipe");
        }
        ((net.minecraft.world.inventory.RecipeCraftingHolder)(Object)this).setRecipeUsed(new net.minecraft.world.item.crafting.RecipeHolder(current.id(), a0));
    }

    /** was net/minecraft/net.minecraft.world.inventory.RecipeCraftingHolder.getRecipeUsed()Lnet/minecraft/net.minecraft.world.item.crafting.Recipe; */
    public net.minecraft.world.item.crafting.Recipe getRecipeUsed() {
        return ((net.minecraft.world.inventory.RecipeCraftingHolder) (Object) this).getRecipeUsed().value();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.method_757(Lnet/minecraft/net.minecraft.resources.Identifier;)Lnet/minecraft/net.minecraft.util.context.ContextKeySet; */
    public static net.minecraft.util.context.ContextKeySet method_757(net.minecraft.world.level.storage.loot.parameters.LootContextParamSets self, net.minecraft.resources.Identifier a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.method_757(net.minecraft.resources.Identifier) performed dynamic tag lookup by Identifier; 1.20.2 reworked the tag system and removed this capability with no direct replacement");
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.method_762(Lnet/minecraft/net.minecraft.util.context.ContextKeySet;)Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier method_762(net.minecraft.world.level.storage.loot.parameters.LootContextParamSets self, net.minecraft.util.context.ContextKeySet a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_762 (Slot -> Identifier) was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/net.minecraft.world.item.equipment.ArmorMaterials.field_41941Lnet/minecraft/net.minecraft.util.StringRepresentable$class_7292; */
    public static net.minecraft.util.StringRepresentable.EnumCodec field_41941() {
        throw new UnsupportedOperationException("CenturyBridge: StringIdentifiable.Codec has been removed in 1.20.3");
    }



    /** was net/minecraft/net.minecraft.advancements.CriterionProgress.method_783()Lcom/google/gson/JsonElement; */
    public static com.google.gson.JsonElement method_783(net.minecraft.advancements.CriterionProgress self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.advancements.CriterionProgress.method_783() (JsonElement serialization) was deleted in 1.20.2; the new API surface only exposes getObtained() returning Instant and serializeToNetwork() for PacketByteBuf serialization, with no equivalent JsonElement contract");
    }

    /** was net/minecraft/net.minecraft.advancements.CriterionProgress.method_788(Ljava/lang/String;)Lnet/minecraft/net.minecraft.advancements.CriterionProgress; */
    public static net.minecraft.advancements.CriterionProgress method_788(net.minecraft.advancements.CriterionProgress self, java.lang.String a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.advancements.CriterionProgress.method_788(String) has no 1.20.2 equivalent; the String parameter is no longer part of net.minecraft.advancements.CriterionProgress's contract (only <init>() or <init>(Instant) exist)");
    }


    /** was net/minecraft/class_1830.field_30925Ljava/lang/String; */
    public static java.lang.String field_30925() {
        return "Items";
    }

    /** was net/minecraft/net.minecraft.advancements.CriterionTriggerInstance.method_806()Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier method_806(net.minecraft.advancements.CriterionTriggerInstance self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.advancements.CriterionTriggerInstance no longer stores an Identifier; the advancement ID was moved to AdvancementEntry (net.minecraft.advancements.AdvancementNode) in 1.20.2, so method_806 has no equivalent on net.minecraft.advancements.CriterionTriggerInstance itself");
    }


    /** was net/minecraft/net.minecraft.advancements.CriterionTriggerInstance.method_807()Lcom/google/gson/JsonObject; */
    public static com.google.gson.JsonObject method_807(net.minecraft.advancements.CriterionTriggerInstance self) {
        throw new UnsupportedOperationException("CenturyBridge: Advancement JSON serialization is not supported on this version");
    }


    /** was net/minecraft/net.minecraft.advancements.AdvancementType.method_832()I */
    public static int method_832(net.minecraft.advancements.AdvancementType self) {
        return self.ordinal();
    }

    /** was net/minecraft/net.minecraft.client.renderer.rendertype.RenderType.field_32773I */
    public static int field_32773() {
        return 131072;
    }


    /** was net/minecraft/net.minecraft.advancements.criterion.ConsumeItemTrigger.field_9509Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_9509() {
        return net.minecraft.resources.Identifier.parse("enchanted_item");
    }

    /** was net/minecraft/net.minecraft.advancements.criterion.FilledBucketTrigger.field_9612Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_9612() {
        return net.minecraft.resources.Identifier.parse("enchantment_used_on_item");
    }

    /** was net/minecraft/net.minecraft.advancements.criterion.ImpossibleTrigger.field_9624Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_9624() {
        return net.minecraft.resources.Identifier.parse("recipe_unlocked");
    }

    /** was net/minecraft/net.minecraft.advancements.criterion.InventoryChangeTrigger.field_9625Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_9625() {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "brewed_potion");
    }



    /** was net/minecraft/net.minecraft.world.level.block.Blocks.field_10150Lnet/minecraft/net.minecraft.world.level.block.Block; */
    public static net.minecraft.world.level.block.Block field_10150() {
        throw new UnsupportedOperationException("CenturyBridge: Blocks.field_10150 was deleted in 1.20.3 with no direct replacement");
    }

    /** was net/minecraft/net.minecraft.world.level.block.Blocks.field_10331Lnet/minecraft/net.minecraft.world.level.block.Block; */
    public static net.minecraft.world.level.block.Block field_10331() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.block.Blocks.field_10331 was removed in 1.20.3 with no direct replacement");
    }

    /** was net/minecraft/net.minecraft.core.Registry.method_10231(Lnet/minecraft/net.minecraft.core.Registry;ILjava/lang/String;Ljava/lang/Object;)Ljava/lang/Object; */
    public static java.lang.Object method_10231(net.minecraft.core.Registry self, net.minecraft.core.Registry a0, int a1, java.lang.String a2, java.lang.Object a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_10231 (Registry.register with raw id) has no 1.20.2 equivalent; raw-id registration is no longer supported");
    }





    /** was net/minecraft/net.minecraft.network.FriendlyByteBuf.method_10815(I)I */
    public static int method_10815(net.minecraft.network.FriendlyByteBuf self, int a0) {
        return self.readVarInt();
    }

    /** was net/minecraft/net.minecraft.network.FriendlyByteBuf.method_36129(J)I */
    public static int method_36129(net.minecraft.network.FriendlyByteBuf self, long a0) {
        int before = self.writerIndex();
        self.writeVarLong(a0);
        return self.writerIndex() - before;
    }

    /** was net/minecraft/net.minecraft.network.protocol.game.ClientGamePacketListener.method_11083(Lnet/minecraft/net.minecraft.network.protocol.common.ClientboundDisconnectPacket;)V */
    public static void method_11083(net.minecraft.network.protocol.game.ClientGamePacketListener self, net.minecraft.network.protocol.common.ClientboundDisconnectPacket a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.network.protocol.game.ClientGamePacketListener.method_11083(net.minecraft.network.protocol.common.ClientboundDisconnectPacket) was removed in 1.20.2 with no direct replacement accepting net.minecraft.network.protocol.common.ClientboundDisconnectPacket");
    }

    /** was net/minecraft/net.minecraft.network.protocol.game.ClientGamePacketListener.handleUpdateTags(Lnet/minecraft/net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;)V */
    public void handleUpdateTags(net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket a0) {
        ((net.minecraft.network.protocol.common.ClientCommonPacketListener) (Object) this).handleUpdateTags(a0);
    }

    /** was net/minecraft/net.minecraft.network.protocol.game.ClientGamePacketListener.handleCustomPayload(Lnet/minecraft/net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;)V */
    public static void method_11152(net.minecraft.network.protocol.game.ClientGamePacketListener self, net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.network.protocol.game.ClientGamePacketListener.handleCustomPayload(net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket) was removed in 1.20.2 with no direct replacement; no remaining method on net.minecraft.network.protocol.game.ClientGamePacketListener accepts net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket");
    }

    /** was net/minecraft/net.minecraft.network.protocol.game.ClientGamePacketListener.method_36895(Lnet/minecraft/net.minecraft.network.protocol.common.ClientboundPingPacket;)V */
    public static void method_36895(net.minecraft.network.protocol.game.ClientGamePacketListener self, net.minecraft.network.protocol.common.ClientboundPingPacket a0) {
        a0.handle(self);
    }

    /** was net/minecraft/net.minecraft.network.protocol.game.ClientGamePacketListener.method_45728(Lnet/minecraft/net.minecraft.network.protocol.configuration.ClientboundUpdateEnabledFeaturesPacket;)V */
    public static void method_45728(net.minecraft.network.protocol.game.ClientGamePacketListener self, net.minecraft.network.protocol.configuration.ClientboundUpdateEnabledFeaturesPacket a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.network.protocol.game.ClientGamePacketListener.method_45728(net.minecraft.network.protocol.configuration.ClientboundUpdateEnabledFeaturesPacket) was deleted in 1.20.2 with no direct replacement; net.minecraft.network.protocol.configuration.ClientboundUpdateEnabledFeaturesPacket has no single successor on the new visitor surface");
    }

    /** was net/minecraft/net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity.field_31352Ljava/lang/String; */
    public static java.lang.String field_31352() {
        return "Fuel";
    }

    /** was net/minecraft/net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity.field_31353Ljava/lang/String; */
    public static java.lang.String field_31353() {
        throw new UnsupportedOperationException("CenturyBridge: field_31353 (String constant on net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity/LecternBlockEntity) was deleted in 1.20.3 with no replacement and its literal value cannot be reconstructed from the new API surface");
    }

    /** was net/minecraft/net.minecraft.world.level.block.entity.SignBlockEntity.method_49844(Lnet/minecraft/net.minecraft.world.entity.player.Player;)Lnet/minecraft/net.minecraft.world.level.block.entity.SignText; */
    public static net.minecraft.world.level.block.entity.SignText method_49844(net.minecraft.world.level.block.entity.SignBlockEntity self, net.minecraft.world.entity.player.Player a0) {
        return self.getText(self.isFacingFrontText(a0));
    }

    /** was net/minecraft/net.minecraft.world.level.block.entity.StructureBlockEntity.method_11368(Lnet/minecraft/net.minecraft.server.level.ServerLevel;Z)Z */
    public static boolean method_11368(net.minecraft.world.level.block.entity.StructureBlockEntity self, net.minecraft.server.level.ServerLevel a0, boolean a1) {
        return self.placeStructureIfSameSize(a0);
    }

    /** was net/minecraft/net.minecraft.world.level.block.entity.StructureBlockEntity.method_21864(Lnet/minecraft/net.minecraft.server.level.ServerLevel;ZLnet/minecraft/net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;)Z */
    public static boolean method_21864(net.minecraft.world.level.block.entity.StructureBlockEntity self, net.minecraft.server.level.ServerLevel a0, boolean a1, net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate a2) {
        throw new UnsupportedOperationException("CenturyBridge: StructureBlockBlockEntity no longer supports placing a custom StructureTemplate");
    }

    /** was net/minecraft/net.minecraft.world.level.block.entity.StructureBlockEntity.method_21865()Ljava/lang/String; */
    public static java.lang.String method_21865(net.minecraft.world.level.block.entity.StructureBlockEntity self) {
        return self.AUTHOR_TAG;
    }


    /** was net/minecraft/net.minecraft.network.protocol.game.ServerGamePacketListener.handleClientInformation(Lnet/minecraft/net.minecraft.network.protocol.common.ServerboundClientInformationPacket;)V */
    public void handleClientInformation(net.minecraft.network.protocol.common.ServerboundClientInformationPacket a0) {
        ((net.minecraft.network.protocol.common.ServerCommonPacketListener) (Object) this).handleClientInformation(a0);
    }

    /** was net/minecraft/net.minecraft.network.protocol.game.ServerGamePacketListener.method_12075(Lnet/minecraft/net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;)V */
    public static void method_12075(net.minecraft.network.protocol.game.ServerGamePacketListener self, net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_12075 (onDifficulty) was removed in 1.20.2; difficulty is now embedded in GameJoinS2CPacket/PlayerRespawnS2CPacket rather than sent as a standalone packet, so there is no equivalent handler to delegate to");
    }

    /** was net/minecraft/net.minecraft.network.protocol.game.ServerGamePacketListener.method_12081(Lnet/minecraft/net.minecraft.network.protocol.common.ServerboundResourcePackPacket;)V */
    public static void method_12081(net.minecraft.network.protocol.game.ServerGamePacketListener self, net.minecraft.network.protocol.common.ServerboundResourcePackPacket a0) {
        throw new UnsupportedOperationException("CenturyBridge: PlayPongC2SPacket (net.minecraft.network.protocol.common.ServerboundResourcePackPacket) has been deleted");
    }

    /** was net/minecraft/net.minecraft.network.protocol.game.ServerGamePacketListener.method_12082(Lnet/minecraft/net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;)V */
    public static void method_12082(net.minecraft.network.protocol.game.ServerGamePacketListener self, net.minecraft.network.protocol.common.ServerboundKeepAlivePacket a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_12082 is no longer supported on net.minecraft.network.protocol.game.ServerGamePacketListener");
    }

    /** was net/minecraft/net.minecraft.network.protocol.game.ServerGamePacketListener.method_36580(Lnet/minecraft/net.minecraft.network.protocol.common.ServerboundPongPacket;)V */
    public static void method_36580(net.minecraft.network.protocol.game.ServerGamePacketListener self, net.minecraft.network.protocol.common.ServerboundPongPacket a0) {
        throw new UnsupportedOperationException("CenturyBridge: JigsawGeneratingC2SPacket (net.minecraft.network.protocol.common.ServerboundPongPacket) handler was removed in 1.20.2");
    }

    /** was net/minecraft/net.minecraft.network.protocol.status.ClientStatusPacketListener.handlePongResponse(Lnet/minecraft/net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;)V */
    public void handlePongResponse(net.minecraft.network.protocol.ping.ClientboundPongResponsePacket a0) {
        ((net.minecraft.network.protocol.ping.ClientPongPacketListener) (Object) this).handlePongResponse(a0);
    }

    /** was net/minecraft/net.minecraft.network.protocol.status.ServerStatusPacketListener.handlePingRequest(Lnet/minecraft/net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;)V */
    public void handlePingRequest(net.minecraft.network.protocol.ping.ServerboundPingRequestPacket a0) {
        throw new UnsupportedOperationException("CenturyBridge: tasks are no longer managed by BuiltChunk");
    }

    /** was net/minecraft/class_2952.method_12816(IIILnet/minecraft/net.minecraft.world.item.crafting.Recipe;Ljava/util/Iterator;I)V */
    public void method_12816(int a0, int a1, int a2, net.minecraft.world.item.crafting.Recipe a3, java.util.Iterator a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.item.crafting.RecipeHolder (RecipeEntry) requires a net.minecraft.resources.Identifier (Identifier) that is not available from the old net.minecraft.world.item.crafting.Recipe-only method_12816 signature; Recipe.getId() was removed in 1.20.2");
    }

    /** was net/minecraft/net.minecraft.server.ServerInterface.getPlayerCount()I */
    public int getPlayerCount() {
        return ((net.minecraft.server.ServerInfo) (Object) this).getPlayerCount();
    }

    /** was net/minecraft/net.minecraft.server.ServerInterface.getMaxPlayers()I */
    public int getMaxPlayers() {
        return ((net.minecraft.server.ServerInfo) (Object) this).getMaxPlayers();
    }

    /** was net/minecraft/net.minecraft.server.ServerInterface.getServerVersion()Ljava/lang/String; */
    public java.lang.String getServerVersion() {
        throw new UnsupportedOperationException("CenturyBridge: getServerVersion moved to net.minecraft.server.ServerInfo");
    }

    /** was net/minecraft/net.minecraft.server.ServerScoreboard.method_32704(Lnet/minecraft/net.minecraft.nbt.CompoundTag;)Lnet/minecraft/net.minecraft.world.scores.ScoreboardSaveData; */
    public static net.minecraft.world.scores.ScoreboardSaveData method_32704(net.minecraft.server.ServerScoreboard self, net.minecraft.nbt.CompoundTag a0) {
        throw new UnsupportedOperationException("CenturyBridge: Scoreboard.method_32704(Tag) was removed in 1.20.2; objective NBT deserialization was reworked with no direct equivalent");
    }

    /** was net/minecraft/net.minecraft.server.ServerScoreboard.method_32705()Lnet/minecraft/net.minecraft.world.scores.ScoreboardSaveData; */
    public static net.minecraft.world.scores.ScoreboardSaveData method_32705(net.minecraft.server.ServerScoreboard self) {
        try {
            for (java.lang.reflect.Field field : self.getClass().getDeclaredFields()) {
                if (field.getType() == net.minecraft.world.scores.ScoreboardSaveData.class) {
                    field.setAccessible(true);
                    return (net.minecraft.world.scores.ScoreboardSaveData) field.get(self);
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("CenturyBridge: Failed to access banned IP list field via reflection", t);
        }
        throw new UnsupportedOperationException("CenturyBridge: BannedIpList field not found in net.minecraft.server.ServerScoreboard");
    }

    /** was net/minecraft/net.minecraft.client.Minecraft.emergencySave()V */
    public static void method_1519(net.minecraft.client.Minecraft self) {
        throw new UnsupportedOperationException("CenturyBridge: emergencySave (handleInputEvents) was removed and its functionality was merged into client tick");
    }

    /** was net/minecraft/net.minecraft.client.Minecraft.method_1537(Z)V */
    public static void method_1537(net.minecraft.client.Minecraft self, boolean a0) {
        throw new UnsupportedOperationException("CenturyBridge: setConnectedToRealms is no longer supported as this state is now automatically derived from the network handler.");
    }

    /** was net/minecraft/net.minecraft.client.Minecraft.method_1589()Z */
    public static boolean method_1589(net.minecraft.client.Minecraft self) {
        return true;
    }

    /** was net/minecraft/net.minecraft.client.Minecraft.method_44649()Z */
    public static boolean method_44649(net.minecraft.client.Minecraft self) {
        return self.extraTelemetryAvailable();
    }

    /** was net/minecraft/net.minecraft.client.Options$4.field_1967[I */
    public static int[] field_1967() {
        throw new UnsupportedOperationException("CenturyBridge: class_316 does not exist in this version");
    }

    /** was net/minecraft/net.minecraft.client.Options.field_1866Z */
    public static boolean field_1866() {
        return false;
    }

    /** was net/minecraft/net.minecraft.client.Options.field_1880Z */
    public static boolean field_1880() {
        return true;
    }

    /** was net/minecraft/net.minecraft.client.Options.field_1893Z */
    public static boolean field_1893() {
        throw new UnsupportedOperationException("CenturyBridge: field_1893 is no longer available");
    }

    /** was net/minecraft/net.minecraft.world.level.storage.LevelStorageSource$class_5143.getDataTag(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/net.minecraft.world.level.WorldDataConfiguration;Lnet/minecraft/net.minecraft.core.Registry;Lcom/mojang/serialization/Lifecycle;)Lcom/mojang/datafixers/util/Pair; */
    public static com.mojang.datafixers.util.Pair method_27013(net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess self, com.mojang.serialization.DynamicOps a0, net.minecraft.world.level.WorldDataConfiguration a1, net.minecraft.core.Registry a2, com.mojang.serialization.Lifecycle a3) {
        throw new UnsupportedOperationException("CenturyBridge: LevelStorage.Session.getDataTag was removed in 1.20.3 and cannot be emulated");
    }

    /** was net/minecraft/net.minecraft.world.level.storage.LevelStorageSource$class_5143.method_29585()Lnet/minecraft/net.minecraft.world.level.WorldDataConfiguration; */
    public static net.minecraft.world.level.WorldDataConfiguration method_29585(net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess self) {
        throw new UnsupportedOperationException("CenturyBridge: LevelStorage.Session.method_29585() returning net.minecraft.world.level.WorldDataConfiguration was removed in 1.20.3 with no direct replacement on net.minecraft.world.level.storage.LevelStorageSource$class_5143");
    }

    /** was net/minecraft/net.minecraft.server.level.PlayerMap.method_14081(JJLnet/minecraft/net.minecraft.server.level.ServerPlayer;)V */
    public static void method_14081(net.minecraft.server.level.PlayerMap self, long a0, long a1, net.minecraft.server.level.ServerPlayer a2) {
        self.unIgnorePlayer(a2);
    }

    /** was net/minecraft/net.minecraft.server.network.ServerLoginPacketListenerImpl.field_14160Lcom/mojang/authlib/GameProfile; */
    public static com.mojang.authlib.GameProfile field_14160() {
        throw new UnsupportedOperationException("CenturyBridge: field_14160 was a per-connection instance GameProfile field; the 1.20.2 API only exposes startClientVerification (setter) and method_52418 (String->GameProfile factory), neither of which can reconstruct the value from a static no-arg context");
    }

    /** was net/minecraft/net.minecraft.server.network.ServerLoginPacketListenerImpl.method_14375(Lcom/mojang/authlib/GameProfile;)Lcom/mojang/authlib/GameProfile; */
    public static com.mojang.authlib.GameProfile method_14375(net.minecraft.server.network.ServerLoginPacketListenerImpl self, com.mojang.authlib.GameProfile a0) {
        throw new UnsupportedOperationException("CenturyBridge: ServerLoginNetworkHandler.method_14375 was removed and has no public equivalent");
    }

    /** was net/minecraft/net.minecraft.server.network.ServerLoginPacketListenerImpl.method_14384()V */
    public static void method_14384(net.minecraft.server.network.ServerLoginPacketListenerImpl self) {
        throw new UnsupportedOperationException("CenturyBridge: method_14384 (acceptPlayer) cannot be implemented because the authenticated GameProfile is not accessible");
    }

    /** was net/minecraft/net.minecraft.server.network.ServerLoginPacketListenerImpl.method_40085(Ljava/lang/String;)Z */
    public static boolean method_40085(net.minecraft.server.network.ServerLoginPacketListenerImpl self, java.lang.String a0) {
        return a0.chars().filter(c -> c <= 32 || c >= 127).findFirst().isEmpty();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.LevelStorageSource.method_17926(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/net.minecraft.world.level.WorldDataConfiguration;Lnet/minecraft/net.minecraft.core.Registry;Lcom/mojang/serialization/Lifecycle;)Ljava/util/function/BiFunction; */
    public static java.util.function.BiFunction method_17926(net.minecraft.world.level.storage.LevelStorageSource self, com.mojang.serialization.DynamicOps a0, net.minecraft.world.level.WorldDataConfiguration a1, net.minecraft.core.Registry a2, com.mojang.serialization.Lifecycle a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_17926 was removed in 1.20.3 and cannot be faithfully emulated.");
    }

    /** was net/minecraft/net.minecraft.world.level.storage.LevelStorageSource.method_26998(Lnet/minecraft/net.minecraft.world.level.storage.LevelStorageSource$class_7411;Ljava/util/function/BiFunction;)Ljava/lang/Object; */
    public static java.lang.Object method_26998(net.minecraft.world.level.storage.LevelStorageSource self, net.minecraft.world.level.storage.LevelStorageSource.LevelDirectory a0, java.util.function.BiFunction a1) {
        throw new UnsupportedOperationException("CenturyBridge: LevelStorage.readLevelProperties is removed in 1.20.3");
    }

    /** was net/minecraft/net.minecraft.world.level.storage.LevelStorageSource.method_29014(Lnet/minecraft/net.minecraft.world.level.storage.LevelStorageSource$class_7411;Z)Ljava/util/function/BiFunction; */
    public static java.util.function.BiFunction method_29014(net.minecraft.world.level.storage.LevelStorageSource self, net.minecraft.world.level.storage.LevelStorageSource.LevelDirectory a0, boolean a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_29014 not supported due to API changes in DataConfiguration parsing");
    }





    /** was net/minecraft/net.minecraft.client.gui.components.AbstractWidget.field_22757Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_22757() {
        return net.minecraft.resources.Identifier.parse("textures/gui/widgets.png");
    }

    /** was net/minecraft/net.minecraft.client.gui.components.AbstractWidget.field_42117Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_42117() {
        return net.minecraft.resources.Identifier.parse("textures/gui/widgets.png");
    }

    /** was net/minecraft/net.minecraft.client.gui.components.AbstractWidget.method_47937()Lnet/minecraft/net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner; */
    public static net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner method_47937(net.minecraft.client.gui.components.AbstractWidget self) {
        try {
            for (java.lang.reflect.Field field : net.minecraft.client.gui.components.AbstractWidget.class.getDeclaredFields()) {
                if (field.getType() == net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner.class) {
                    field.setAccessible(true);
                    return (net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner) field.get(self);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces.method_35471([Ljava/lang/String;)V */
    public static void method_35471(net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces self, java.lang.String[] a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces.method_35471(String[]) was removed in 1.20.2 with no direct replacement; the new API only exposes structure placement via generateMansion");
    }

    /** was net/minecraft/net.minecraft.world.level.storage.LevelSummary.method_256()Z */
    public static boolean method_256(net.minecraft.world.level.storage.LevelSummary self) {
        return false;
    }

    /** was net/minecraft/net.minecraft.world.level.storage.LevelSummary.method_260()Z */
    public static boolean method_260(net.minecraft.world.level.storage.LevelSummary self) {
        net.minecraft.world.level.LevelSettings levelInfo = self.getSettings();
        return levelInfo != null && levelInfo.allowCommands();
    }

    /** was net/minecraft/net.minecraft.util.HttpUtil.field_15664Lcom/google/common/util/concurrent/ListeningExecutorService; */
    public static com.google.common.util.concurrent.ListeningExecutorService field_15664() {
        throw new UnsupportedOperationException("CenturyBridge: field_15664 (ListeningExecutorService) has been removed");
    }

    /** was net/minecraft/net.minecraft.util.HttpUtil.method_15301(Ljava/io/File;Ljava/net/URL;Ljava/util/Map;ILnet/minecraft/net.minecraft.util.ProgressListener;Ljava/net/Proxy;)Ljava/util/concurrent/CompletableFuture; */
    public static java.util.concurrent.CompletableFuture method_15301(net.minecraft.util.HttpUtil self, java.io.File a0, java.net.URL a1, java.util.Map a2, int a3, net.minecraft.util.ProgressListener a4, java.net.Proxy a5) {
        throw new UnsupportedOperationException("CenturyBridge: method_15301 was removed and cannot be implemented without Guava classes and the new progress listener interface.");
    }

    /** was net/minecraft/net.minecraft.util.StringRepresentable$class_7292.method_42631(Ljava/lang/Enum;Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult; */
    public static com.mojang.serialization.DataResult method_42631(net.minecraft.util.StringRepresentable.EnumCodec self, java.lang.Enum a0, com.mojang.serialization.DynamicOps a1, java.lang.Object a2) {
        return self.encode(a0, a1, a2);
    }

    /** was net/minecraft/net.minecraft.client.gui.components.PlayerTabOverlay.field_32206I */
    public static int field_32206() {
        return 20;
    }

    /** was net/minecraft/net.minecraft.client.gui.components.PlayerTabOverlay.field_32207I */
    public static int field_32207() {
        return 80;
    }

    /** was net/minecraft/net.minecraft.client.gui.components.PlayerTabOverlay.field_32208I */
    public static int field_32208() {
        return 80;
    }

    /** was net/minecraft/net.minecraft.client.gui.components.PlayerTabOverlay.field_32209I */
    public static int field_32209() {
        return 256;
    }

    /** was net/minecraft/net.minecraft.client.gui.components.PlayerTabOverlay.field_32210I */
    public static int field_32210() {
        return 80;
    }

    /** was net/minecraft/net.minecraft.client.gui.components.PlayerTabOverlay.field_32211I */
    public static int field_32211() {
        return 80;
    }

    /** was net/minecraft/net.minecraft.client.gui.components.PlayerTabOverlay.field_32212I */
    public static int field_32212() {
        return 80;
    }

    /** was net/minecraft/net.minecraft.client.gui.components.PlayerTabOverlay.field_32213I */
    public static int field_32213() {
        return 80;
    }

    /** was net/minecraft/net.minecraft.client.gui.components.AbstractSliderButton.field_43051I */
    public static int field_43051() {
        return 8;
    }

    /** was net/minecraft/net.minecraft.client.gui.components.AbstractSliderButton.field_43052I */
    public static int field_43052() {
        return 46;
    }

    /** was net/minecraft/net.minecraft.client.gui.components.AbstractSliderButton.field_43102I */
    public static int field_43102() {
        return 14737632;
    }

    /** was net/minecraft/net.minecraft.client.gui.components.AbstractSliderButton.field_43103I */
    public static int field_43103() {
        return 8;
    }

    /** was net/minecraft/class_361.field_2189I */
    public static int field_2189() {
        return 147;
    }

    /** was net/minecraft/class_361.field_2190I */
    public static int field_2190() {
        throw new UnsupportedOperationException("CenturyBridge: field_2190 was an instance field and cannot be accessed without an instance.");
    }

    /** was net/minecraft/class_361.field_2191I */
    public static int field_2191() {
        return 147;
    }

    /** was net/minecraft/class_361.field_2192I */
    public static int field_2192() {
        throw new UnsupportedOperationException("CenturyBridge: field_2192 (parentWidth) was removed from RecipeBookWidget");
    }

    /** was net/minecraft/class_361.field_2193Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_2193() {
        return net.minecraft.resources.Identifier.parse("textures/gui/recipe_book.png");
    }

    /** was net/minecraft/net.minecraft.client.gui.components.events.GuiEventListener.mouseScrolled(DDD)Z */
    public boolean mouseScrolled(double a0, double a1, double a2) {
        return ((net.minecraft.client.gui.components.events.GuiEventListener) (Object) this).mouseScrolled(a0, a1, 0.0, a2);
    }

    /** was net/minecraft/net.minecraft.client.gui.components.spectator.SpectatorGui.field_2199Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_2199() {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "textures/gui/options_background.png");
    }

    /** was net/minecraft/net.minecraft.client.gui.components.toasts.Toast.field_2207Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_2207() {
        return net.minecraft.resources.Identifier.parse("textures/gui/toasts.png");
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement$class_4181.field_18696Lnet/minecraft/net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece; */
    public static net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece field_18696() {
        throw new UnsupportedOperationException("CenturyBridge: Static field field_18696 was deleted without replacement");
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement$class_4181.field_18697Lorg/apache/commons/lang3/mutable/MutableObject; */
    public static org.apache.commons.lang3.mutable.MutableObject field_18697() {
        throw new UnsupportedOperationException("CenturyBridge: Instance field field_18697 accessed statically without receiver");
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement$class_4181.field_18699I */
    public static int field_18699() {
        throw new UnsupportedOperationException("CenturyBridge: field_18699 was deleted with no replacement");
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement$class_4182.field_18706Ljava/util/Deque; */
    public static java.util.Deque field_18706() {
        throw new UnsupportedOperationException("CenturyBridge: placing was changed to net.minecraft.util.SequencedPriorityIterator and cannot be represented as Deque");
    }

    /** was net/minecraft/net.minecraft.server.level.ChunkMap.field_18243I */
    public static int field_18243() {
        return 34;
    }

    /** was net/minecraft/net.minecraft.server.level.ChunkMap.updateChunkTracking(Lnet/minecraft/net.minecraft.server.level.ServerPlayer;Lnet/minecraft/net.minecraft.world.level.ChunkPos;Lorg/apache/commons/lang3/mutable/MutableObject;ZZ)V */
    public static void method_17241(net.minecraft.server.level.ChunkMap self, net.minecraft.server.level.ServerPlayer a0, net.minecraft.world.level.ChunkPos a1, org.apache.commons.lang3.mutable.MutableObject a2, boolean a3, boolean a4) {
        throw new UnsupportedOperationException("CenturyBridge: updateChunkTracking (sendChunk) was removed in 1.20.2 due to chunk sending flow control overhaul");
    }

    /** was net/minecraft/net.minecraft.server.level.ChunkMap.method_39975(IIIII)Z */
    public static boolean method_39975(net.minecraft.server.level.ChunkMap self, int a0, int a1, int a2, int a3, int a4) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.server.level.ChunkMap.method_39975(IIIII)Z was removed in 1.20.2; the chunk tick/spawning system was reworked and the five-int-parameter tick method has no direct equivalent");
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.CreateFlatWorldScreen$class_4192.field_44667Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_44667() {
        return net.minecraft.resources.Identifier.parse("textures/gui/language_button.png");
    }

    /** was net/minecraft/com.mojang.blaze3d.audio.Listener.method_19671(Lnet/minecraft/net.minecraft.world.phys.Vec3;)V */
    public static void method_19671(com.mojang.blaze3d.audio.Listener self, net.minecraft.world.phys.Vec3 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_19671 is no longer supported on FogData");
    }

    /** was net/minecraft/com.mojang.blaze3d.audio.Listener.method_19672(Lorg/joml/Vector3f;Lorg/joml/Vector3f;)V */
    public static void method_19672(com.mojang.blaze3d.audio.Listener self, org.joml.Vector3f a0, org.joml.Vector3f a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_19672 was deleted and cannot be reconstructed from the new Camera API");
    }

    /** was net/minecraft/com.mojang.blaze3d.audio.Listener.method_27268()Lnet/minecraft/net.minecraft.world.phys.Vec3; */
    public static net.minecraft.world.phys.Vec3 method_27268(com.mojang.blaze3d.audio.Listener self) {
        throw new UnsupportedOperationException("CenturyBridge: method_27268 was removed from com.mojang.blaze3d.audio.Listener with no replacement");
    }

    /** was net/minecraft/net.minecraft.client.gui.components.AbstractButton.field_43046I */
    public static int field_43046() {
        return 16777215;
    }

    /** was net/minecraft/net.minecraft.client.gui.components.AbstractButton.field_43047I */
    public static int field_43047() {
        return 4;
    }

    /** was net/minecraft/net.minecraft.client.gui.components.AbstractButton.field_43048I */
    public static int field_43048() {
        return 2;
    }

    /** was net/minecraft/net.minecraft.client.gui.components.AbstractButton.field_43100I */
    public static int field_43100() {
        return 6;
    }

    /** was net/minecraft/net.minecraft.client.gui.components.AbstractButton.field_43101I */
    public static int field_43101() {
        return 16777215;
    }

    /** was net/minecraft/net.minecraft.util.datafix.DataFixTypes.field_19219Lnet/minecraft/net.minecraft.util.datafix.DataFixTypes; */
    public static net.minecraft.util.datafix.DataFixTypes field_19219() {
        throw new UnsupportedOperationException("CenturyBridge: field_19219 (POI) has been deleted and has no equivalent in 1.20.2");
    }

    /** was net/minecraft/com.mojang.realmsclient.client.RealmsClient.field_19576Lnet/minecraft/com.mojang.realmsclient.client.RealmsClient$class_4343; */
    public static com.mojang.realmsclient.client.RealmsClient.Environment field_19576() {
        return com.mojang.realmsclient.client.RealmsClient.ENVIRONMENT;
    }

    /** was net/minecraft/com.mojang.realmsclient.client.RealmsClient.method_21001()V */
    public static void method_21001(com.mojang.realmsclient.client.RealmsClient self) {
        throw new UnsupportedOperationException("CenturyBridge: com.mojang.realmsclient.client.RealmsClient.method_21001() was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/com.mojang.realmsclient.client.RealmsClient.method_21008()V */
    public static void method_21008(com.mojang.realmsclient.client.RealmsClient self) {
        throw new UnsupportedOperationException("CenturyBridge: method_21008 has no replacement");
    }

    /** was net/minecraft/com.mojang.realmsclient.client.RealmsClient.method_21012()V */
    public static void method_21012(com.mojang.realmsclient.client.RealmsClient self) {
        throw new UnsupportedOperationException("CenturyBridge: com.mojang.realmsclient.client.RealmsClient.method_21012 was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/com.mojang.realmsclient.client.RealmsClient.method_21021()Ljava/lang/Boolean; */
    public static java.lang.Boolean method_21021(com.mojang.realmsclient.client.RealmsClient self) {
        return java.lang.Boolean.FALSE;
    }

    /** was net/minecraft/com.mojang.realmsclient.client.RealmsClient.method_21024()Ljava/lang/Boolean; */
    public static java.lang.Boolean method_21024(com.mojang.realmsclient.client.RealmsClient self) {
        throw new UnsupportedOperationException("CenturyBridge: RealmsClient.trialAvailable (method_21024) was removed in 1.20.2");
    }

    /** was net/minecraft/com.mojang.realmsclient.client.Request.method_35685(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/com.mojang.realmsclient.client.Request; */
    public static com.mojang.realmsclient.client.Request method_35685(com.mojang.realmsclient.client.Request self, java.lang.String a0, java.lang.String a1) {
        self.cookie(a0, a1);
        return self;
    }

    /** was net/minecraft/com.mojang.realmsclient.util.task.LongRunningTask.field_19638Lnet/minecraft/com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen; */
    public static com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen field_19638() {
        throw new UnsupportedOperationException("CenturyBridge: com.mojang.realmsclient.util.task.LongRunningTask.field_19638 (com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen) was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/com.mojang.realmsclient.util.task.LongRunningTask.method_21066(Lnet/minecraft/com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;)V */
    public static void method_21066(com.mojang.realmsclient.util.task.LongRunningTask self, com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_21066 (addLabel) is no longer supported as RealmsLabel has been removed in 1.20.2");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen.field_22686Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_22686() {
        throw new UnsupportedOperationException("CenturyBridge: field_22686 (static Identifier on com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen) was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen.field_22687Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_22687() {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath("realms", "textures/gui/realms/slot_frame.png");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab.field_22704Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_22704() {
        return net.minecraft.resources.Identifier.parse("textures/gui/recipe_book.png");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab.field_22705Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_22705() {
        throw new UnsupportedOperationException("CenturyBridge: com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab.field_22705 (Identifier) was deleted in 1.20.2 with no direct replacement and its constant value cannot be recovered from the new API surface");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab.field_22706Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_22706() {
        return net.minecraft.resources.Identifier.parse("textures/gui/container/creative_inventory/creative_inventory.png");
    }


    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.field_20072I */
    public static int field_20072() {
        return 32;
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.field_20077Lnet/minecraft/net.minecraft.network.chat.Component; */
    public static net.minecraft.network.chat.Component field_20077() {
        throw new UnsupportedOperationException("CenturyBridge: field_20077 was a deleted static Text constant with no direct replacement in 1.20.2");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.field_20080I */
    public static int field_20080() {
        return 30;
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.field_20083Z */
    public static boolean field_20083() {
        return true;
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.field_22719Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_22719() {
        return net.minecraft.resources.Identifier.parse("textures/misc/unknown_server.png");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.field_22720Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_22720() {
        return net.minecraft.resources.Identifier.parse("textures/gui/container/command_block.png");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.field_26512Lnet/minecraft/net.minecraft.network.chat.Component; */
    public static net.minecraft.network.chat.Component field_26512() {
        return net.minecraft.network.chat.Component.nullToEmpty("mco.trial.message");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.field_26513Lnet/minecraft/net.minecraft.network.chat.Component; */
    public static net.minecraft.network.chat.Component field_26513() {
        return net.minecraft.network.chat.Component.nullToEmpty("mco.template.button.publisher");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.selectTemplate()V */
    public static void method_21440(com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen self) {
        throw new UnsupportedOperationException("CenturyBridge: selectTemplate was removed from ConnectScreen in 1.20.2 with no direct equivalent");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.method_25228(I)I */
    public static int method_25228(com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen self, int a0) {
        return 40 + a0 * 13;
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.achievement.StatsScreen.method_19399()Lnet/minecraft/net.minecraft.client.gui.components.ObjectSelectionList; */
    public static net.minecraft.client.gui.components.ObjectSelectionList method_19399(net.minecraft.client.gui.screens.achievement.StatsScreen self) {
        throw new UnsupportedOperationException("CenturyBridge: State getter was removed and state is no longer accessible");
    }

    /** was net/minecraft/net.minecraft.gametest.framework.GameTestRunner.field_33148I */
    public static int field_33148() {
        return 5;
    }


    /** was net/minecraft/net.minecraft.gametest.framework.StructureUtils.method_36106([Ljava/lang/String;)V */
    public static void method_36106(net.minecraft.gametest.framework.StructureUtils self, java.lang.String[] a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.gametest.framework.StructureUtils.method_36106(String[]) was removed in 1.20.2 with no direct replacement; the structure manager no longer accepts a pack/namespace array");
    }


    /** was net/minecraft/net.minecraft.client.gui.screens.advancements.AdvancementWidgetType.method_2320()I */
    public static int method_2320(net.minecraft.client.gui.screens.advancements.AdvancementWidgetType self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.advancements.AdvancementWidgetType.method_2320() (Difficulty.getId()) was removed in 1.20.2; difficulties are now registry-backed and identified by ResourceLocation via boxSprite(), with no stable int id equivalent");
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.advancements.AdvancementsScreen.field_2716Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_2716() {
        return net.minecraft.resources.Identifier.parse("textures/gui/options_background.png");
    }







    /** was net/minecraft/com.mojang.realmsclient.dto.PendingInvite.field_22583Ljava/lang/String; */
    public static java.lang.String field_22583() {
        return "uuid";
    }

    /** was net/minecraft/com.mojang.realmsclient.dto.RealmsServer.field_22605Ljava/lang/String; */
    public static java.lang.String field_22605() {
        throw new UnsupportedOperationException("CenturyBridge: ownerUUID is an instance field but the helper signature has no receiver parameter");
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.inventory.SmithingScreen.field_42047I */
    public static int field_42047() {
        return 8;
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.inventory.SmithingScreen.field_42048Lorg/joml/Quaternionf; */
    public static org.joml.Quaternionf field_42048() {
        throw new UnsupportedOperationException("CenturyBridge: ARMOR_STAND_ANGLE was a static Quaternionf constant in net.minecraft.client.gui.screens.inventory.SmithingScreen (BeaconScreen) deleted in 1.20.2 with no replacement; the exact rotation value cannot be reconstructed from the new API surface");
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.inventory.SmithingScreen.field_42049I */
    public static int field_42049() {
        throw new UnsupportedOperationException("CenturyBridge: ARMOR_STAND_SCALE is not available");
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.inventory.SmithingScreen.field_42050I */
    public static int field_42050() {
        return 99;
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.inventory.SmithingScreen.field_42051I */
    public static int field_42051() {
        return 15;
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.inventory.SmithingScreen.field_42068I */
    public static int field_42068() {
        return 4;
    }

    /** was net/minecraft/net.minecraft.realms.RealmsScreen.field_33036I */
    public static int field_33036() {
        return 19088743;
    }

    /** was net/minecraft/net.minecraft.realms.RealmsScreen.field_33037I */
    public static int field_33037() {
        return 240;
    }

    /** was net/minecraft/net.minecraft.realms.RealmsScreen.field_33038I */
    public static int field_33038() {
        return 0xD0000000;
    }

    /** was net/minecraft/net.minecraft.realms.RealmsScreen.field_33039I */
    public static int field_33039() {
        return 80;
    }

    /** was net/minecraft/net.minecraft.realms.RealmsScreen.field_33043I */
    public static int field_33043() {
        return 32;
    }

    /** was net/minecraft/net.minecraft.realms.RealmsScreen.field_33044Ljava/lang/String; */
    public static java.lang.String field_33044() {
        return "minceraft";
    }

    /** was net/minecraft/net.minecraft.realms.RealmsScreen.field_33056I */
    public static int field_33056() {
        return -10526880;
    }

    /** was net/minecraft/net.minecraft.realms.RealmsScreen.field_33059I */
    public static int field_33059() {
        throw new UnsupportedOperationException("CenturyBridge: field_33059 was removed in 1.20.2 with no direct replacement and its constant value cannot be determined from the surviving API surface");
    }

    /** was net/minecraft/net.minecraft.realms.RealmsScreen.field_33060I */
    public static int field_33060() {
        return 16;
    }

    /** was net/minecraft/net.minecraft.realms.RealmsScreen.field_33064I */
    public static int field_33064() {
        return 26;
    }

    /** was net/minecraft/net.minecraft.realms.RealmsScreen.field_33065I */
    public static int field_33065() {
        return -7079482;
    }


    /** was net/minecraft/net.minecraft.client.gui.screens.packs.TransferableSelectionList.field_19125Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_19125() {
        return net.minecraft.resources.Identifier.parse("textures/gui/recipe_book.png");
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen.field_24566Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_24566() {
        return net.minecraft.resources.Identifier.parse("textures/gui/social_interactions/black_icon.png");
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.social.SocialInteractionsScreen.field_26875Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_26875() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.social.SocialInteractionsScreen.field_26875 (Identifier) was deleted in 1.20.2 with no direct replacement; the constant value cannot be reconstructed from the available API surface");
    }

    /** was net/minecraft/net.minecraft.world.level.block.ChangeOverTimeBlock.method_33621(Lnet/minecraft/net.minecraft.world.level.block.state.BlockState;Lnet/minecraft/net.minecraft.server.level.ServerLevel;Lnet/minecraft/net.minecraft.core.BlockPos;Lnet/minecraft/net.minecraft.util.RandomSource;)V */
    public static void method_33621(net.minecraft.world.level.block.ChangeOverTimeBlock self, net.minecraft.world.level.block.state.BlockState a0, net.minecraft.server.level.ServerLevel a1, net.minecraft.core.BlockPos a2, net.minecraft.util.RandomSource a3) {
        self.changeOverTime(a0, a1, a2, a3);
    }

    /** was net/minecraft/net.minecraft.world.level.block.ChangeOverTimeBlock.method_33623(Lnet/minecraft/net.minecraft.world.level.block.state.BlockState;Lnet/minecraft/net.minecraft.server.level.ServerLevel;Lnet/minecraft/net.minecraft.core.BlockPos;Lnet/minecraft/net.minecraft.util.RandomSource;)V */
    public static void method_33623(net.minecraft.world.level.block.ChangeOverTimeBlock self, net.minecraft.world.level.block.state.BlockState a0, net.minecraft.server.level.ServerLevel a1, net.minecraft.core.BlockPos a2, net.minecraft.util.RandomSource a3) {
        self.changeOverTime(a0, a1, a2, a3);
    }

    /** was net/minecraft/net.minecraft.client.renderer.entity.EntityRenderers.field_32922Ljava/lang/String; */
    public static java.lang.String field_32922() {
        return "en_us";
    }


    /** was net/minecraft/net.minecraft.world.level.storage.loot.providers.number.NumberProviders.method_32455()Ljava/lang/Object; */
    public static java.lang.Object method_32455(net.minecraft.world.level.storage.loot.providers.number.NumberProviders self) {
        throw new UnsupportedOperationException("CenturyBridge: method_32455 was removed in 1.20.2 with no direct replacement; net.minecraft.world.level.storage.loot.providers.number.NumberProviders no longer exposes a value accessor and the restructured fields (net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType) cannot reproduce the old Object return contract without their API");
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProvider.method_32476(Lnet/minecraft/net.minecraft.world.level.storage.loot.LootContext;)Ljava/lang/String; */
    public static java.lang.String method_32476(net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProvider self, net.minecraft.world.level.storage.loot.LootContext a0) {
        throw new UnsupportedOperationException("CenturyBridge: LootContextType validation now uses ErrorReporter and cannot be converted to String");
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProviders.method_32478()Ljava/lang/Object; */
    public static java.lang.Object method_32478(net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProviders self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProviders.method_32478() was removed in 1.20.2 with no direct replacement; the default-value accessor was refactored out of LootDataType");
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip$class_5771.field_28368I */
    public static int field_28368() {
        throw new UnsupportedOperationException("CenturyBridge: field_28368 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip$class_5771.field_28369I */
    public static int field_28369() {
        throw new UnsupportedOperationException("CenturyBridge: field_28369 was an instance field representing V offset but the shim has no receiver parameter");
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip.field_28359Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_28359() {
        return net.minecraft.resources.Identifier.parse("textures/gui/container/bundle.png");
    }

    /** was net/minecraft/net.minecraft.world.entity.ai.behavior.FollowTemptation.field_30116D */
    public static double field_30116() {
        return 8.0;
    }

    /** was net/minecraft/net.minecraft.client.multiplayer.ClientAdvancements$class_633.onUpdateAdvancementProgress(Lnet/minecraft/net.minecraft.advancements.Advancement;Lnet/minecraft/net.minecraft.advancements.AdvancementProgress;)V */
    public void onUpdateAdvancementProgress(net.minecraft.advancements.Advancement a0, net.minecraft.advancements.AdvancementProgress a1) {
        throw new UnsupportedOperationException("CenturyBridge: Cannot convert net.minecraft.advancements.Advancement to net.minecraft.advancements.AdvancementNode");
    }

    /** was net/minecraft/net.minecraft.client.multiplayer.ClientAdvancements$class_633.onSelectedTabChanged(Lnet/minecraft/net.minecraft.advancements.Advancement;)V */
    public void onSelectedTabChanged(net.minecraft.advancements.Advancement a0) {
        ((net.minecraft.client.multiplayer.ClientAdvancements.Listener) (Object) this).onSelectedTabChanged(new net.minecraft.advancements.AdvancementHolder(net.minecraft.resources.Identifier.fromNamespaceAndPath("centurybridge", "dummy"), a0));
    }

    /** was net/minecraft/net.minecraft.client.multiplayer.ClientAdvancements.method_2863()Lnet/minecraft/net.minecraft.advancements.AdvancementTree; */
    public static net.minecraft.advancements.AdvancementTree method_2863(net.minecraft.client.multiplayer.ClientAdvancements self) {
        return self.getTree();
    }

    /** was net/minecraft/net.minecraft.client.multiplayer.PlayerInfo.method_2967()Z */
    public static boolean method_2967(net.minecraft.client.multiplayer.PlayerInfo self) {
        return self.hasVerifiableChat();
    }


    /** was net/minecraft/net.minecraft.client.multiplayer.PlayerInfo.method_2969()V */
    public static void method_2969(net.minecraft.client.multiplayer.PlayerInfo self) {
        self.getSkin();
    }

    /** was net/minecraft/net.minecraft.client.multiplayer.PlayerInfo.method_2977()Ljava/lang/String; */
    public static java.lang.String method_2977(net.minecraft.client.multiplayer.PlayerInfo self) {
        return self.getProfile().name();
    }

    /** was net/minecraft/net.minecraft.client.multiplayer.PlayerInfo.method_35757()Z */
    public static boolean method_35757(net.minecraft.client.multiplayer.PlayerInfo self) {
        return self.hasVerifiableChat();
    }



    /** was net/minecraft/net.minecraft.client.gui.screens.worldselection.WorldOpenFlows.method_41890(Lnet/minecraft/net.minecraft.world.level.storage.LevelStorageSource$class_5143;Z)Lnet/minecraft/net.minecraft.server.WorldStem; */
    public static net.minecraft.server.WorldStem method_41890(net.minecraft.client.gui.screens.worldselection.WorldOpenFlows self, net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess a0, boolean a1) {
        throw new UnsupportedOperationException("CenturyBridge: synchronous world loading from session is no longer supported");
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.worldselection.WorldOpenFlows.method_41894(Lnet/minecraft/net.minecraft.client.gui.screens.Screen;Ljava/lang/String;)V */
    public static void method_41894(net.minecraft.client.gui.screens.worldselection.WorldOpenFlows self, net.minecraft.client.gui.screens.Screen a0, java.lang.String a1) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.worldselection.WorldOpenFlows.method_41894(Screen,String) created a world from the CreateWorldScreen instance's internal state (data packs, level storage, generator options); 1.20.3 replaced it with createFreshLevel(String,net.minecraft.world.level.LevelSettings,net.minecraft.world.level.levelgen.WorldOptions,Function,net.minecraft.client.gui.screens.Screen) which requires those as explicit arguments, and the shim has no access to the receiver's fields to supply them");
    }

    /** was net/minecraft/net.minecraft.world.entity.animal.allay.Allay.method_45342()Z */
    public static boolean method_45342(net.minecraft.world.entity.animal.allay.Allay self) {
        return self.isDancing();
    }




    /** was net/minecraft/net.minecraft.client.player.AbstractClientPlayer.method_3120(Lnet/minecraft/net.minecraft.resources.Identifier;Ljava/lang/String;)V */
    public static void method_3120(net.minecraft.client.player.AbstractClientPlayer self, net.minecraft.resources.Identifier a0, java.lang.String a1) {
        throw new UnsupportedOperationException("CenturyBridge: registerSkinTexture is not supported in 1.20.2 as skin loading is managed by PlayerSkinProvider");
    }

    /** was net/minecraft/net.minecraft.client.player.AbstractClientPlayer.method_3121()Ljava/lang/String; */
    public static java.lang.String method_3121(net.minecraft.client.player.AbstractClientPlayer self) {
        throw new UnsupportedOperationException("CenturyBridge: method_3121 (getSkinType) was removed; use getSkin (getSkinTextures) and extract the model from net.minecraft.world.entity.player.PlayerSkin instead");
    }



    /** was net/minecraft/net.minecraft.client.player.AbstractClientPlayer.method_3126()Z */
    public static boolean method_3126(net.minecraft.client.player.AbstractClientPlayer self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.player.AbstractClientPlayer.method_3126()Z was deleted in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/net.minecraft.client.gui.components.MultiLineEditBox.method_44406()V */
    public static void method_44406(net.minecraft.client.gui.components.MultiLineEditBox self) {
        return;
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.RealmsDataFetcher.field_39684Lnet/minecraft/com.mojang.realmsclient.gui.task.DataFetcher$class_7586; */
    public static com.mojang.realmsclient.gui.task.DataFetcher.Task field_39684() {
        throw new UnsupportedOperationException("CenturyBridge: field_39684 was removed in 1.20.2 with no direct replacement; the registered com.mojang.realmsclient.gui.task.DataFetcher$class_7586 instance no longer exists");
    }

    /** was net/minecraft/net.minecraft.client.renderer.GameRenderer.method_34500()Lnet/minecraft/com.mojang.blaze3d.opengl.GlProgram; */
    public static com.mojang.blaze3d.opengl.GlProgram method_34500(net.minecraft.client.renderer.GameRenderer self) {
        throw new UnsupportedOperationException("CenturyBridge: getPositionColorLightmapShader was removed");
    }

    /** was net/minecraft/net.minecraft.client.GuiMessageTag$class_7592.field_39764I */
    public static int field_39764() {
        throw new UnsupportedOperationException("CenturyBridge: field_39764 is no longer available");
    }

    /** was net/minecraft/net.minecraft.client.GuiMessageTag$class_7592.field_39765I */
    public static int field_39765() {
        return 0;
    }

    /** was net/minecraft/net.minecraft.client.GuiMessageTag.field_39761Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_39761() {
        return net.minecraft.resources.Identifier.parse("textures/gui/chat_status.png");
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.entries.LootPoolEntries.method_29316()Ljava/lang/Object; */
    public static java.lang.Object method_29316(net.minecraft.world.level.storage.loot.entries.LootPoolEntries self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.storage.loot.entries.LootPoolEntries is no longer an enum and has no instance state");
    }

    /** was net/minecraft/net.minecraft.network.chat.SignedMessageChain$class_7611.field_40848Lnet/minecraft/net.minecraft.network.chat.SignedMessageChain$class_7611; */
    public static net.minecraft.network.chat.SignedMessageChain.Decoder field_40848() {
        return net.minecraft.network.chat.SignedMessageChain.Decoder.unsigned(new java.util.UUID(0L, 0L), () -> false);
    }

    /** was net/minecraft/net.minecraft.world.entity.animal.camel.Camel.method_45348()Z */
    public static boolean method_45348(net.minecraft.world.entity.animal.camel.Camel self) {
        return self.isCamelVisuallySitting() && !self.isCamelSitting();
    }

    /** was net/minecraft/net.minecraft.client.renderer.texture.SpriteLoader.method_45829(Lnet/minecraft/net.minecraft.resources.Identifier;Lnet/minecraft/net.minecraft.server.packs.resources.Resource;)Lnet/minecraft/net.minecraft.client.renderer.texture.SpriteContents; */
    public static net.minecraft.client.renderer.texture.SpriteContents method_45829(net.minecraft.client.renderer.texture.SpriteLoader self, net.minecraft.resources.Identifier a0, net.minecraft.server.packs.resources.Resource a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_45829 (SpriteLoader.load(Identifier, Resource)) was removed in 1.20.2; the new API is async (CompletableFuture-based) and takes a ResourceManager or List instead of a single Resource, with no synchronous single-resource equivalent");
    }

    /** was net/minecraft/net.minecraft.client.gui.layouts.EqualSpacingLayout$1.field_41817[I */
    public static int[] field_41817() {
        throw new UnsupportedOperationException("CenturyBridge: Target class net.minecraft.client.gui.layouts.LinearLayout$1 is not public");
    }

    /** was net/minecraft/net.minecraft.core.registries.BuiltInRegistries.field_41154Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_41154() {
        return net.minecraft.resources.Identifier.parse("root");
    }

    /** was net/minecraft/net.minecraft.network.protocol.BundlerInfo.field_41877Lio/netty/util/AttributeKey; */
    public static io.netty.util.AttributeKey field_41877() {
        throw new UnsupportedOperationException("CenturyBridge: field_41877 (AttributeKey) was removed from net.minecraft.network.protocol.BundlerInfo in 1.20.2 with no direct replacement; bundle-packet channel attribute mechanism reworked");
    }

    /** was net/minecraft/net.minecraft.client.gui.components.tabs.Tab.method_48613()V */
    public static void method_48613(net.minecraft.client.gui.components.tabs.Tab self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.components.tabs.Tab.method_48613 was removed in 1.20.2 with no direct equivalent");
    }

    /** was net/minecraft/net.minecraft.client.gui.components.tabs.TabManager.method_48617()V */
    public static void method_48617(net.minecraft.client.gui.components.tabs.TabManager self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.components.tabs.TabManager.method_48617 was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/net.minecraft.world.entity.Display.field_42385Ljava/lang/String; */
    public static java.lang.String field_42385() {
        throw new UnsupportedOperationException("CenturyBridge: field_42385 (static String) was deleted from net.minecraft.world.entity.Display in 1.20.2 with no direct replacement; its literal value cannot be determined from the new API surface alone");
    }

    /** was net/minecraft/net.minecraft.world.entity.Display.field_43149Ljava/lang/String; */
    public static java.lang.String field_43149() {
        return "sniffing_state";
    }

    /** was net/minecraft/net.minecraft.world.entity.Display.method_48863()Lorg/joml/Quaternionf; */
    public static org.joml.Quaternionf method_48863(net.minecraft.world.entity.Display self) {
        throw new UnsupportedOperationException("CenturyBridge: DisplayEntity left rotation getter was removed in 1.20.2");
    }

    /** was net/minecraft/net.minecraft.client.gui.layouts.HeaderAndFooterLayout.method_49001()Lnet/minecraft/net.minecraft.client.gui.layouts.LayoutSettings; */
    public static net.minecraft.client.gui.layouts.LayoutSettings method_49001(net.minecraft.client.gui.layouts.HeaderAndFooterLayout self) {
        throw new UnsupportedOperationException("CenturyBridge: HeaderAndFooterLayoutWidget no longer exposes its default positioner");
    }

    /** was net/minecraft/net.minecraft.client.gui.layouts.HeaderAndFooterLayout.method_49002()Lnet/minecraft/net.minecraft.client.gui.layouts.LayoutSettings; */
    public static net.minecraft.client.gui.layouts.LayoutSettings method_49002(net.minecraft.client.gui.layouts.HeaderAndFooterLayout self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.layouts.HeaderAndFooterLayout.method_49002() was deleted in 1.20.2 with no direct replacement returning net.minecraft.client.gui.layouts.LayoutSettings");
    }

    /** was net/minecraft/net.minecraft.client.gui.layouts.HeaderAndFooterLayout.method_49003()Lnet/minecraft/net.minecraft.client.gui.layouts.LayoutSettings; */
    public static net.minecraft.client.gui.layouts.LayoutSettings method_49003(net.minecraft.client.gui.layouts.HeaderAndFooterLayout self) {
        throw new UnsupportedOperationException("CenturyBridge: method_49003() was removed in 1.20.2 with no direct replacement; the new net.minecraft.client.gui.layouts.HeaderAndFooterLayout API does not produce net.minecraft.client.gui.layouts.LayoutSettings instances");
    }

    /** was net/minecraft/net.minecraft.world.ticks.ContainerSingleItem.method_49274()Lnet/minecraft/net.minecraft.world.item.ItemStack; */
    public static net.minecraft.world.item.ItemStack method_49274(net.minecraft.world.ticks.ContainerSingleItem self) {
        return self.removeTheItem();
    }

    /** was net/minecraft/net.minecraft.world.ticks.ContainerSingleItem.method_49275(Lnet/minecraft/net.minecraft.world.item.ItemStack;)V */
    public static void method_49275(net.minecraft.world.ticks.ContainerSingleItem self, net.minecraft.world.item.ItemStack a0) {
        self.setTheItem(a0);
    }

    /** was net/minecraft/net.minecraft.world.ticks.ContainerSingleItem.method_49276()Lnet/minecraft/net.minecraft.world.item.ItemStack; */
    public static net.minecraft.world.item.ItemStack method_49276(net.minecraft.world.ticks.ContainerSingleItem self) {
        return self.getTheItem();
    }

    /** was net/minecraft/net.minecraft.client.gui.components.TabButton.method_49607()I */
    public static int method_49607(net.minecraft.client.gui.components.TabButton self) {
        throw new UnsupportedOperationException("CenturyBridge: method_49607 returned a creative tab index (int), but 1.20.2 replaced tab indices with ItemGroup references (net.minecraft.client.gui.components.tabs.Tab); use tab() instead");
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_43134Ljava/lang/String; */
    public static java.lang.String field_43134() {
        return "players";
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.LootDataType.method_51203()Lcom/google/gson/Gson; */
    public static com.google.gson.Gson method_51203(net.minecraft.world.level.storage.loot.LootDataType self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.storage.loot.LootDataType.method_51203() returned a Gson instance that was removed in 1.20.2; the new API surface has no Gson field or method to delegate to");
    }

    /** was net/minecraft/net.minecraft.client.renderer.entity.BatRenderer.method_3882(Lnet/minecraft/net.minecraft.world.entity.ambient.Bat;Lnet/minecraft/com.mojang.blaze3d.vertex.PoseStack;FFF)V */
    public static void method_3882(net.minecraft.client.renderer.entity.BatRenderer self, net.minecraft.world.entity.ambient.Bat a0, com.mojang.blaze3d.vertex.PoseStack a1, float a2, float a3, float a4) {
        throw new UnsupportedOperationException("CenturyBridge: method_3882(net.minecraft.world.entity.ambient.Bat, com.mojang.blaze3d.vertex.PoseStack, float, float, float) was deleted in 1.20.3 with no direct replacement; remaining getTextureLocation(net.minecraft.world.entity.ambient.Bat) returns net.minecraft.resources.Identifier and drops the MatrixStack/position parameters, so the old render-style contract cannot be expressed");
    }

    /** was net/minecraft/net.minecraft.client.renderer.entity.BatRenderer.method_3884(Lnet/minecraft/net.minecraft.world.entity.ambient.Bat;Lnet/minecraft/com.mojang.blaze3d.vertex.PoseStack;F)V */
    public static void method_3884(net.minecraft.client.renderer.entity.BatRenderer self, net.minecraft.world.entity.ambient.Bat a0, com.mojang.blaze3d.vertex.PoseStack a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_3884 is unsupported because modern rendering requires a VertexConsumerProvider and light map coordinates which are missing from this signature.");
    }

    /** was net/minecraft/net.minecraft.world.Container.field_29952I */
    public static int field_29952() {
        return 64;
    }

    /** was net/minecraft/net.minecraft.world.Container.field_42619I */
    public static int field_42619() {
        return 64;
    }

    /** was net/minecraft/net.minecraft.world.entity.AreaEffectCloud.method_5602(I)V */
    public static void method_5602(net.minecraft.world.entity.AreaEffectCloud self, int a0) {
        self.setDurationOnUse(a0);
    }

    /** was net/minecraft/net.minecraft.world.entity.AreaEffectCloud.method_5606()I */
    public static int method_5606(net.minecraft.world.entity.AreaEffectCloud self) {
        return self.getDuration();
    }

    /** was net/minecraft/net.minecraft.world.entity.Entity.field_29991F */
    public static float field_29991() {
        return 0.0f;
    }

    /** was net/minecraft/net.minecraft.world.entity.Entity.method_18378(Lnet/minecraft/net.minecraft.world.entity.Pose;Lnet/minecraft/net.minecraft.world.entity.EntityDimensions;)F */
    public static float method_18378(net.minecraft.world.entity.Entity self, net.minecraft.world.entity.Pose a0, net.minecraft.world.entity.EntityDimensions a1) {
        return self.getEyeHeight(a0);
    }

    /** was net/minecraft/net.minecraft.world.entity.Entity.method_52537(Lnet/minecraft/net.minecraft.world.entity.Entity;)F */
    public static float method_52537(net.minecraft.world.entity.Entity self, net.minecraft.world.entity.Entity a0) {
        float f = (float) (self.getX() - a0.getX());
        float g = (float) (self.getY() - a0.getY());
        float h = (float) (self.getZ() - a0.getZ());
        return (float) Math.sqrt(f * f + g * g + h * h);
    }


    /** was net/minecraft/net.minecraft.world.entity.LivingEntity.field_30067F */
    public static float field_30067() {
        return 0.02f;
    }

    /** was net/minecraft/net.minecraft.world.entity.LivingEntity.method_20236(Lnet/minecraft/net.minecraft.world.InteractionHand;)V */
    public static void method_20236(net.minecraft.world.entity.LivingEntity self, net.minecraft.world.InteractionHand a0) {
        self.swing(a0, false);
    }



    /** was net/minecraft/net.minecraft.world.entity.ai.goal.GoalSelector.method_35114(I)V */
    public static void method_35114(net.minecraft.world.entity.ai.goal.GoalSelector self, int a0) {
        net.minecraft.world.entity.ai.goal.Goal.Flag[] values = net.minecraft.world.entity.ai.goal.Goal.Flag.values();
        if (a0 >= 0 && a0 < values.length) {
            self.enableControlFlag(values[a0]);
        }
    }

    /** was net/minecraft/net.minecraft.world.entity.animal.squid.Squid$class_1479.field_6916Lnet/minecraft/net.minecraft.world.entity.animal.squid.Squid; */
    public static net.minecraft.world.entity.animal.squid.Squid field_6916() {
        throw new UnsupportedOperationException("CenturyBridge: field_6916 was deleted without replacement");
    }

    /** was net/minecraft/net.minecraft.world.entity.animal.equine.Horse.method_6786()Lnet/minecraft/net.minecraft.world.item.ItemStack; */
    public static net.minecraft.world.item.ItemStack method_6786(net.minecraft.world.entity.animal.equine.Horse self) {
        return new net.minecraft.world.item.ItemStack(net.minecraft.world.level.block.Blocks.STRIPPED_ACACIA_WOOD);
    }

    /** was net/minecraft/net.minecraft.world.level.pathfinder.WalkNodeEvaluator.method_27138(Lnet/minecraft/net.minecraft.world.level.block.state.BlockState;)Z */
    public static boolean method_27138(net.minecraft.world.level.pathfinder.WalkNodeEvaluator self, net.minecraft.world.level.block.state.BlockState a0) {
        throw new UnsupportedOperationException("CenturyBridge: PathNodeMaker no longer supports checking a raw BlockState without position or context in 1.20.5");
    }

    /** was net/minecraft/net.minecraft.world.level.pathfinder.WalkNodeEvaluator.method_63(Lnet/minecraft/net.minecraft.world.entity.Mob;Lnet/minecraft/net.minecraft.core.BlockPos;)Lnet/minecraft/net.minecraft.world.level.pathfinder.PathType; */
    public static net.minecraft.world.level.pathfinder.PathType method_63(net.minecraft.world.level.pathfinder.WalkNodeEvaluator self, net.minecraft.world.entity.Mob a0, net.minecraft.core.BlockPos a1) {
        return self.getPathTypeStatic(a0, a1);
    }

    /** was net/minecraft/net.minecraft.world.level.pathfinder.WalkNodeEvaluator.method_64(Lnet/minecraft/net.minecraft.world.level.BlockGetter;IIILjava/util/EnumSet;Lnet/minecraft/net.minecraft.world.level.pathfinder.PathType;Lnet/minecraft/net.minecraft.core.BlockPos;)Lnet/minecraft/net.minecraft.world.level.pathfinder.PathType; */
    public static net.minecraft.world.level.pathfinder.PathType method_64(net.minecraft.world.level.pathfinder.WalkNodeEvaluator self, net.minecraft.world.level.BlockGetter a0, int a1, int a2, int a3, java.util.EnumSet a4, net.minecraft.world.level.pathfinder.PathType a5, net.minecraft.core.BlockPos a6) {
        throw new UnsupportedOperationException("CenturyBridge: PathNodeMaker.getNode requires PathNodeMakerContext in 1.20.5");
    }

    /** was net/minecraft/net.minecraft.world.entity.decoration.painting.Painting.field_42463Ljava/lang/String; */
    public static java.lang.String field_42463() {
        return "variant";
    }

    /** was net/minecraft/net.minecraft.SharedConstants.field_29699Z */
    public static boolean field_29699() {
        return false;
    }

    /** was net/minecraft/net.minecraft.SharedConstants.field_29701Z */
    public static boolean field_29701() {
        return false;
    }

    /** was net/minecraft/net.minecraft.SharedConstants.method_44355(Ljava/lang/String;Z)Ljava/lang/String; */
    public static java.lang.String method_44355(net.minecraft.SharedConstants self, java.lang.String a0, boolean a1) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < a0.length(); i++) {
            char c = a0.charAt(i);
            if ((c != '\u00a7' && c >= ' ' && c != '\u007f') || (a1 && c == '\n')) {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    /** was net/minecraft/net.minecraft.SharedConstants.method_643(C)Z */
    public static boolean method_643(net.minecraft.SharedConstants self, char a0) {
        return a0 != '\u00a7' && a0 >= ' ' && a0 != 127;
    }

    /** was net/minecraft/net.minecraft.world.entity.monster.Phantom$class_1599.field_7329Lnet/minecraft/net.minecraft.world.entity.monster.Phantom; */
    public static net.minecraft.world.entity.monster.Phantom field_7329() {
        throw new UnsupportedOperationException("CenturyBridge: Field is an instance field (this$0) but shim has no receiver parameter");
    }

    /** was net/minecraft/net.minecraft.world.entity.monster.zombie.Zombie.field_41028F */
    public static float field_41028() {
        return 0.001f;
    }

    /** was net/minecraft/net.minecraft.world.entity.player.Player$1.field_7533[I */
    public static int[] field_7533() {
        throw new UnsupportedOperationException("CenturyBridge: Switch map field_7533 in net.minecraft.world.entity.player.Player$1 is no longer available");
    }

    /** was net/minecraft/net.minecraft.world.entity.player.Player.field_30643I */
    public static int field_30643() {
        return 100;
    }

    /** was net/minecraft/net.minecraft.world.entity.player.Player.method_53968(Ljava/lang/String;)Z */
    public static boolean method_53968(net.minecraft.world.entity.player.Player self, java.lang.String a0) {
        return self.getGameProfile().name().equalsIgnoreCase(a0);
    }

    /** was net/minecraft/net.minecraft.world.entity.player.Player.method_54292(Z)F */
    public static float method_54292(net.minecraft.world.entity.player.Player self, boolean a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_54292 was removed in 1.20.5");
    }

    /** was net/minecraft/net.minecraft.world.inventory.BeaconMenu$1.field_17291Lnet/minecraft/net.minecraft.world.inventory.BeaconMenu; */
    public static net.minecraft.world.inventory.BeaconMenu field_17291() {
        throw new UnsupportedOperationException("CenturyBridge: Cannot access outer instance field statically without receiver");
    }

    /** was net/minecraft/net.minecraft.world.inventory.BeaconMenu$class_1705.field_7768Lnet/minecraft/net.minecraft.world.inventory.BeaconMenu; */
    public static net.minecraft.world.inventory.BeaconMenu field_7768() {
        throw new UnsupportedOperationException("CenturyBridge: field_7768 is an instance field but shim has no receiver");
    }

    /** was net/minecraft/net.minecraft.world.inventory.EnchantmentMenu$2.field_7816Lnet/minecraft/net.minecraft.world.inventory.EnchantmentMenu; */
    public static net.minecraft.world.inventory.EnchantmentMenu field_7816() {
        throw new UnsupportedOperationException("CenturyBridge: field_7816 is an instance field and cannot be accessed without a receiver");
    }

    /** was net/minecraft/net.minecraft.world.inventory.EnchantmentMenu$3.field_7817Lnet/minecraft/net.minecraft.world.inventory.EnchantmentMenu; */
    public static net.minecraft.world.inventory.EnchantmentMenu field_7817() {
        throw new UnsupportedOperationException("CenturyBridge: field_7817 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/net.minecraft.world.inventory.LoomMenu$3.field_7852Lnet/minecraft/net.minecraft.world.inventory.LoomMenu; */
    public static net.minecraft.world.inventory.LoomMenu field_7852() {
        throw new UnsupportedOperationException("CenturyBridge: field_7852 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/net.minecraft.world.inventory.LoomMenu$4.field_7853Lnet/minecraft/net.minecraft.world.inventory.LoomMenu; */
    public static net.minecraft.world.inventory.LoomMenu field_7853() {
        throw new UnsupportedOperationException("CenturyBridge: field_7853 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/net.minecraft.world.inventory.LoomMenu$5.field_7854Lnet/minecraft/net.minecraft.world.inventory.LoomMenu; */
    public static net.minecraft.world.inventory.LoomMenu field_7854() {
        throw new UnsupportedOperationException("CenturyBridge: field_7854 is a synthetic outer class reference and cannot be accessed without an instance");
    }

    /** was net/minecraft/net.minecraft.world.item.equipment.ArmorMaterial.method_24355()F */
    public static float method_24355(net.minecraft.world.item.equipment.ArmorMaterial self) {
        return self.toughness();
    }



    /** was net/minecraft/net.minecraft.world.item.equipment.ArmorMaterial.method_7695()Lnet/minecraft/net.minecraft.world.item.crafting.Ingredient; */
    public static net.minecraft.world.item.crafting.Ingredient method_7695(net.minecraft.world.item.equipment.ArmorMaterial self) {
        throw new UnsupportedOperationException("CenturyBridge: Ingredient getter is no longer available on net.minecraft.world.item.equipment.ArmorMaterial");
    }

    /** was net/minecraft/net.minecraft.world.item.equipment.ArmorMaterial.method_7699()I */
    public static int method_7699(net.minecraft.world.item.equipment.ArmorMaterial self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.item.equipment.ArmorMaterial is no longer RabbitMoveControl");
    }

    /** was net/minecraft/net.minecraft.world.item.equipment.ArmorMaterial.method_7700()F */
    public static float method_7700(net.minecraft.world.item.equipment.ArmorMaterial self) {
        return self.toughness();
    }

    /** was net/minecraft/net.minecraft.world.item.BlockItem.field_30849Ljava/lang/String; */
    public static java.lang.String field_30849() {
        return "BlockEntityTag";
    }

    /** was net/minecraft/net.minecraft.world.item.BlockItem.field_30850Ljava/lang/String; */
    public static java.lang.String field_30850() {
        return "BlockEntityTag";
    }


    /** was net/minecraft/net.minecraft.world.item.CompassItem.field_30860Ljava/lang/String; */
    public static java.lang.String field_30860() {
        return "Charged";
    }

    /** was net/minecraft/net.minecraft.world.item.CompassItem.field_30861Ljava/lang/String; */
    public static java.lang.String field_30861() {
        return "Items";
    }

    /** was net/minecraft/net.minecraft.world.item.CompassItem.field_30862Ljava/lang/String; */
    public static java.lang.String field_30862() {
        return "use_ticks";
    }

    /** was net/minecraft/class_1772.field_30874Ljava/lang/String; */
    public static java.lang.String field_30874() {
        return "default";
    }


    /** was net/minecraft/net.minecraft.world.item.FireworkRocketItem.field_30875Ljava/lang/String; */
    public static java.lang.String field_30875() {
        return "LodestonePos";
    }

    /** was net/minecraft/net.minecraft.world.item.FireworkRocketItem.field_30876Ljava/lang/String; */
    public static java.lang.String field_30876() {
        return "default";
    }

    /** was net/minecraft/net.minecraft.world.item.FireworkRocketItem.field_30877Ljava/lang/String; */
    public static java.lang.String field_30877() {
        return "LodestonePos";
    }

    /** was net/minecraft/net.minecraft.world.item.FireworkRocketItem.field_30878Ljava/lang/String; */
    public static java.lang.String field_30878() {
        return "Fireworks";
    }

    /** was net/minecraft/net.minecraft.world.item.FireworkRocketItem.field_30879Ljava/lang/String; */
    public static java.lang.String field_30879() {
        return "Potion";
    }

    /** was net/minecraft/net.minecraft.world.item.FireworkRocketItem.field_30880Ljava/lang/String; */
    public static java.lang.String field_30880() {
        return "Fireworks";
    }

    /** was net/minecraft/net.minecraft.world.item.FireworkRocketItem.field_30881Ljava/lang/String; */
    public static java.lang.String field_30881() {
        return "Fireworks";
    }

    /** was net/minecraft/net.minecraft.world.item.FireworkRocketItem.field_30882Ljava/lang/String; */
    public static java.lang.String field_30882() {
        return "Type";
    }

    /** was net/minecraft/net.minecraft.world.item.FireworkRocketItem.field_30883Ljava/lang/String; */
    public static java.lang.String field_30883() {
        return "Fireworks";
    }

    /** was net/minecraft/net.minecraft.world.item.MapItem.field_41067Ljava/lang/String; */
    public static java.lang.String field_41067() {
        return "map_color";
    }

    /** was net/minecraft/net.minecraft.world.item.MapItem.field_41068Ljava/lang/String; */
    public static java.lang.String field_41068() {
        return "map_color";
    }

    /** was net/minecraft/net.minecraft.world.item.MapItem.method_17440(I)Ljava/lang/String; */
    public static java.lang.String method_17440(net.minecraft.world.item.MapItem self, int a0) {
        return "map_" + a0;
    }

    /** was net/minecraft/net.minecraft.world.item.PlayerHeadItem.field_30916Ljava/lang/String; */
    public static java.lang.String field_30916() {
        return "Tool modifier";
    }

    /** was net/minecraft/net.minecraft.world.item.Rarity.field_8908Lnet/minecraft/net.minecraft.ChatFormatting; */
    public static net.minecraft.ChatFormatting field_8908() {
        throw new UnsupportedOperationException("CenturyBridge: color is an instance field but the shim signature lacks the instance parameter");
    }

    /** was net/minecraft/net.minecraft.world.item.ShieldItem.field_30920Ljava/lang/String; */
    public static java.lang.String field_30920() {
        return "ChargedProjectiles";
    }

    /** was net/minecraft/class_1830.field_30923Ljava/lang/String; */
    public static java.lang.String field_30923() {
        return "Potion";
    }


    /** was net/minecraft/net.minecraft.world.item.WrittenBookItem.field_30929I */
    public static int field_30929() {
        return 64;
    }

    /** was net/minecraft/net.minecraft.world.item.WrittenBookItem.field_30930I */
    public static int field_30930() {
        throw new UnsupportedOperationException("CenturyBridge: field_30930 (int field in CompassItem) is no longer available in 1.20.5");
    }

    /** was net/minecraft/net.minecraft.world.item.WrittenBookItem.field_30931I */
    public static int field_30931() {
        return 32;
    }

    /** was net/minecraft/net.minecraft.world.item.WrittenBookItem.field_30932I */
    public static int field_30932() {
        return 32;
    }

    /** was net/minecraft/net.minecraft.world.item.WrittenBookItem.field_30933I */
    public static int field_30933() {
        return 0x6666FF;
    }

    /** was net/minecraft/net.minecraft.world.item.WrittenBookItem.field_30934I */
    public static int field_30934() {
        return 1200;
    }

    /** was net/minecraft/net.minecraft.world.item.WrittenBookItem.field_30935Ljava/lang/String; */
    public static java.lang.String field_30935() {
        return "default_potion";
    }

    /** was net/minecraft/net.minecraft.world.item.WrittenBookItem.field_30936Ljava/lang/String; */
    public static java.lang.String field_30936() {
        return "filtered_pages";
    }

    /** was net/minecraft/net.minecraft.world.item.WrittenBookItem.field_30937Ljava/lang/String; */
    public static java.lang.String field_30937() {
        return "pages";
    }

    /** was net/minecraft/net.minecraft.world.item.WrittenBookItem.field_30938Ljava/lang/String; */
    public static java.lang.String field_30938() {
        return "pages";
    }

    /** was net/minecraft/net.minecraft.world.item.WrittenBookItem.field_30939Ljava/lang/String; */
    public static java.lang.String field_30939() {
        return "resolved";
    }

    /** was net/minecraft/net.minecraft.world.item.WrittenBookItem.field_30940Ljava/lang/String; */
    public static java.lang.String field_30940() {
        return "filtered_pages";
    }

    /** was net/minecraft/net.minecraft.world.item.WrittenBookItem.field_30941Ljava/lang/String; */
    public static java.lang.String field_30941() {
        return "Potion";
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8963Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8963() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.HEALING.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8964Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8964() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_SLOW_FALLING.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8965Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8965() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_STRENGTH.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8966Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8966() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRONG_SWIFTNESS.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8967Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8967() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.MUNDANE.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8968Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8968() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.NIGHT_VISION.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8969Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8969() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_FIRE_RESISTANCE.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8970Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8970() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_WEAKNESS.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8971Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8971() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_LEAPING.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8972Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8972() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRONG_POISON.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8973Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8973() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRONG_HARMING.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8974Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8974() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.SLOW_FALLING.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8975Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8975() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.WEAKNESS.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8976Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8976() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRONG_SLOWNESS.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8977Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8977() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRONG_TURTLE_MASTER.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8978Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8978() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRENGTH.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8979Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8979() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LEAPING.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8980Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8980() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRONG_HEALING.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8981Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8981() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_NIGHT_VISION.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8982Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8982() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.POISON.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8983Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8983() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_SWIFTNESS.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8985Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8985() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.THICK.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8986Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8986() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.REGENERATION.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8987Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8987() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.FIRE_RESISTANCE.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8988Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8988() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_TURTLE_MASTER.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8989Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8989() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_SLOWNESS.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8990Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8990() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.TURTLE_MASTER.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8991Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8991() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.WATER.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8992Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8992() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRONG_REGENERATION.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8993Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8993() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRONG_STRENGTH.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8994Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8994() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.WATER_BREATHING.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8995Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8995() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LUCK.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8996Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8996() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.SLOWNESS.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8997Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8997() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.INVISIBILITY.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8998Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8998() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRONG_LEAPING.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_8999Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8999() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.AWKWARD.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_9000Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_9000() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_INVISIBILITY.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_9001Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_9001() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_WATER_BREATHING.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_9002Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_9002() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_POISON.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_9003Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_9003() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_REGENERATION.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_9004Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_9004() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.HARMING.value();
    }

    /** was net/minecraft/net.minecraft.world.item.alchemy.Potions.field_9005Lnet/minecraft/net.minecraft.world.item.alchemy.Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_9005() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.SWIFTNESS.value();
    }


    /** was net/minecraft/net.minecraft.world.item.crafting.RecipeSerializer.codec()Lcom/mojang/serialization/Codec; */
    public com.mojang.serialization.Codec codec() {
        return ((net.minecraft.world.item.crafting.RecipeSerializer) (Object) this).codec().codec();
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.EnchantmentHelper.method_22445(Lnet/minecraft/net.minecraft.nbt.ListTag;)Ljava/util/Map; */
    public static java.util.Map method_22445(net.minecraft.world.item.enchantment.EnchantmentHelper self, net.minecraft.nbt.ListTag a0) {
        throw new UnsupportedOperationException("CenturyBridge: Enchantments are dynamic in 1.20.5 and cannot be resolved from raw NBT without a registry context");
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.EnchantmentHelper.method_37423(Lnet/minecraft/net.minecraft.world.item.enchantment.Enchantment;)Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier method_37423(net.minecraft.world.item.enchantment.EnchantmentHelper self, net.minecraft.world.item.enchantment.Enchantment a0) {
        throw new UnsupportedOperationException("CenturyBridge: Enchantments are dynamic in this version and their IDs cannot be resolved without a registry context.");
    }


    /** was net/minecraft/net.minecraft.world.item.enchantment.EnchantmentHelper.method_37425(Lnet/minecraft/net.minecraft.nbt.CompoundTag;I)V */
    public static void method_37425(net.minecraft.world.item.enchantment.EnchantmentHelper self, net.minecraft.nbt.CompoundTag a0, int a1) {
        throw new UnsupportedOperationException("CenturyBridge: NBT-based enchantment manipulation is no longer supported");
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.EnchantmentHelper.method_37426(Lnet/minecraft/net.minecraft.resources.Identifier;I)Lnet/minecraft/net.minecraft.nbt.CompoundTag; */
    public static net.minecraft.nbt.CompoundTag method_37426(net.minecraft.world.item.enchantment.EnchantmentHelper self, net.minecraft.resources.Identifier a0, int a1) {
        net.minecraft.nbt.CompoundTag nbtCompound = new net.minecraft.nbt.CompoundTag();
        nbtCompound.putString("id", java.lang.String.valueOf(a0));
        nbtCompound.putFloat("lvl", (short) a1);
        return nbtCompound;
    }

    /** was net/minecraft/net.minecraft.world.level.LevelAccessor.gameEvent(Lnet/minecraft/net.minecraft.world.level.gameevent.GameEvent;Lnet/minecraft/net.minecraft.world.phys.Vec3;Lnet/minecraft/net.minecraft.world.level.gameevent.GameEvent$class_7397;)V */
    public void gameEvent(net.minecraft.world.level.gameevent.GameEvent a0, net.minecraft.world.phys.Vec3 a1, net.minecraft.world.level.gameevent.GameEvent.Context a2) {
        ((net.minecraft.world.level.LevelAccessor) (Object) this).gameEvent(net.minecraft.core.Holder.direct(a0), a1, a2);
    }

    /** was net/minecraft/net.minecraft.world.level.LevelAccessor.gameEvent(Lnet/minecraft/net.minecraft.world.entity.Entity;Lnet/minecraft/net.minecraft.world.level.gameevent.GameEvent;Lnet/minecraft/net.minecraft.core.BlockPos;)V */
    public void gameEvent(net.minecraft.world.entity.Entity a0, net.minecraft.world.level.gameevent.GameEvent a1, net.minecraft.core.BlockPos a2) {
        ((net.minecraft.world.level.LevelAccessor) (Object) this).gameEvent(a0, net.minecraft.core.Holder.direct(a1), a2);
    }

    /** was net/minecraft/net.minecraft.world.level.LevelAccessor.gameEvent(Lnet/minecraft/net.minecraft.world.entity.Entity;Lnet/minecraft/net.minecraft.world.level.gameevent.GameEvent;Lnet/minecraft/net.minecraft.world.phys.Vec3;)V */
    public void gameEvent(net.minecraft.world.entity.Entity a0, net.minecraft.world.level.gameevent.GameEvent a1, net.minecraft.world.phys.Vec3 a2) {
        ((net.minecraft.world.level.LevelAccessor) (Object) this).gameEvent(a0, net.minecraft.core.Holder.direct(a1), a2);
    }

    /** was net/minecraft/net.minecraft.world.level.LevelAccessor.gameEvent(Lnet/minecraft/net.minecraft.world.level.gameevent.GameEvent;Lnet/minecraft/net.minecraft.core.BlockPos;Lnet/minecraft/net.minecraft.world.level.gameevent.GameEvent$class_7397;)V */
    public void gameEvent(net.minecraft.world.level.gameevent.GameEvent a0, net.minecraft.core.BlockPos a1, net.minecraft.world.level.gameevent.GameEvent.Context a2) {
        ((net.minecraft.world.level.LevelAccessor) (Object) this).gameEvent(net.minecraft.core.Holder.direct(a0), a1, a2);
    }

    /** was net/minecraft/net.minecraft.world.level.biome.CheckerboardColumnBiomeSource.field_24715Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24715() {
        return net.minecraft.world.level.biome.CheckerboardColumnBiomeSource.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.biome.FixedBiomeSource.field_24717Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24717() {
        return net.minecraft.world.level.biome.FixedBiomeSource.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.biome.TheEndBiomeSource.field_24730Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24730() {
        return net.minecraft.world.level.biome.TheEndBiomeSource.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.commands.arguments.MessageArgument$class_2197.field_9877Ljava/lang/String; */
    public static java.lang.String field_9877() {
        throw new UnsupportedOperationException("CenturyBridge: field_9877 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/net.minecraft.commands.arguments.MessageArgument$class_2197.method_35691()Ljava/lang/String; */
    public static java.lang.String method_35691(net.minecraft.commands.arguments.MessageArgument.Message self) {
        return self.text();
    }

    /** was net/minecraft/net.minecraft.commands.arguments.MessageArgument$class_2197.method_35692()[Lnet/minecraft/net.minecraft.commands.arguments.MessageArgument$class_2198; */
    public static net.minecraft.commands.arguments.MessageArgument.Part[] method_35692(net.minecraft.commands.arguments.MessageArgument.Message self) {
        return self.parts();
    }

    /** was net/minecraft/net.minecraft.commands.arguments.MessageArgument$class_2198.method_35693()Lnet/minecraft/net.minecraft.commands.arguments.selector.EntitySelector; */
    public static net.minecraft.commands.arguments.selector.EntitySelector method_35693(net.minecraft.commands.arguments.MessageArgument.Part self) {
        return self.selector();
    }

    /** was net/minecraft/net.minecraft.commands.arguments.MessageArgument$class_2198.method_9343()I */
    public static int method_9343(net.minecraft.commands.arguments.MessageArgument.Part self) {
        return self.start();
    }

    /** was net/minecraft/net.minecraft.commands.arguments.MessageArgument$class_2198.method_9344()I */
    public static int method_9344(net.minecraft.commands.arguments.MessageArgument.Part self) {
        return self.start();
    }

    /** was net/minecraft/net.minecraft.world.level.saveddata.maps.MapItemSavedData$class_5637.field_27892I */
    public static int field_27892() {
        throw new UnsupportedOperationException("CenturyBridge: field_27892 is an instance field but the shim is static");
    }

    /** was net/minecraft/net.minecraft.world.level.saveddata.maps.MapItemSavedData$class_5637.field_27893I */
    public static int field_27893() {
        throw new UnsupportedOperationException("CenturyBridge: field_27893 cannot be represented as static int");
    }

    /** was net/minecraft/net.minecraft.world.level.saveddata.maps.MapItemSavedData$class_5637.field_27894I */
    public static int field_27894() {
        throw new UnsupportedOperationException("CenturyBridge: field_27894 is an instance field but the shim has no receiver");
    }

    /** was net/minecraft/net.minecraft.world.level.saveddata.maps.MapItemSavedData$class_5637.field_27895I */
    public static int field_27895() {
        return 4;
    }

    /** was net/minecraft/net.minecraft.world.level.saveddata.maps.MapItemSavedData$class_5637.field_27896[B */
    public static byte[] field_27896() {
        return new byte[]{(byte) -119, 80, 78, 71, 13, 10, 26, 10};
    }

    /** was net/minecraft/net.minecraft.commands.arguments.item.FunctionArgument$1.field_10786Lnet/minecraft/net.minecraft.commands.arguments.item.FunctionArgument; */
    public static net.minecraft.commands.arguments.item.FunctionArgument field_10786() {
        throw new UnsupportedOperationException("CenturyBridge: field_10786 has been removed without replacement");
    }

    /** was net/minecraft/net.minecraft.commands.arguments.item.FunctionArgument$2.field_10788Lnet/minecraft/net.minecraft.commands.arguments.item.FunctionArgument; */
    public static net.minecraft.commands.arguments.item.FunctionArgument field_10788() {
        throw new UnsupportedOperationException("CenturyBridge: field_10788 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/net.minecraft.core.MappedRegistry.method_46744(ILnet/minecraft/net.minecraft.resources.ResourceKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/net.minecraft.core.Holder$class_6883; */
    public static net.minecraft.core.Holder.Reference method_46744(net.minecraft.core.MappedRegistry self, int a0, net.minecraft.resources.ResourceKey a1, java.lang.Object a2, com.mojang.serialization.Lifecycle a3) {
        throw new UnsupportedOperationException("CenturyBridge: Registering with an explicit raw ID is no longer supported");
    }


    /** was net/minecraft/net.minecraft.core.WritableRegistry.register(Lnet/minecraft/net.minecraft.resources.ResourceKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/net.minecraft.core.Holder$class_6883; */
    public net.minecraft.core.Holder.Reference register(net.minecraft.resources.ResourceKey a0, java.lang.Object a1, com.mojang.serialization.Lifecycle a2) {
        return ((net.minecraft.core.WritableRegistry) (Object) this).register(a0, a1, new net.minecraft.core.RegistrationInfo(java.util.Optional.empty(), a2));
    }

    /** was net/minecraft/net.minecraft.network.PacketListener.method_40065()Z */
    public static boolean method_40065(net.minecraft.network.PacketListener self) {
        return self.isAcceptingMessages();
    }

    /** was net/minecraft/net.minecraft.world.level.block.entity.BannerBlockEntity.field_31297Ljava/lang/String; */
    public static java.lang.String field_31297() {
        return "Patterns";
    }

    /** was net/minecraft/net.minecraft.world.level.block.entity.BannerBlockEntity.field_31298Ljava/lang/String; */
    public static java.lang.String field_31298() {
        return "Items";
    }

    /** was net/minecraft/net.minecraft.world.level.block.entity.BannerBlockEntity.field_31299Ljava/lang/String; */
    public static java.lang.String field_31299() {
        return "Patterns";
    }


    /** was net/minecraft/net.minecraft.world.level.block.entity.BannerBlockEntity.method_16842(Lnet/minecraft/net.minecraft.network.chat.Component;)V */
    public static void method_16842(net.minecraft.world.level.block.entity.BannerBlockEntity self, net.minecraft.network.chat.Component a0) {
        throw new UnsupportedOperationException("CenturyBridge: BannerBlockEntity.setCustomName (method_16842) was removed in 1.20.5 in favor of the component system.");
    }

    /** was net/minecraft/net.minecraft.world.level.block.entity.BannerBlockEntity.method_24280(Lnet/minecraft/net.minecraft.world.item.DyeColor;Lnet/minecraft/net.minecraft.nbt.ListTag;)Ljava/util/List; */
    public static java.util.List method_24280(net.minecraft.world.level.block.entity.BannerBlockEntity self, net.minecraft.world.item.DyeColor a0, net.minecraft.nbt.ListTag a1) {
        throw new UnsupportedOperationException("CenturyBridge: Cannot parse banner patterns without registry context");
    }

    /** was net/minecraft/net.minecraft.network.protocol.Packet.write(Lnet/minecraft/net.minecraft.network.FriendlyByteBuf;)V */
    public static void method_11052(net.minecraft.network.protocol.Packet self, net.minecraft.network.FriendlyByteBuf a0) {
        throw new UnsupportedOperationException("CenturyBridge: Packets no longer support self-serialization without a codec context in 1.20.5");
    }

    /** was net/minecraft/net.minecraft.network.protocol.Packet.method_52273()Lnet/minecraft/net.minecraft.network.ConnectionProtocol; */
    public static net.minecraft.network.ConnectionProtocol method_52273(net.minecraft.network.protocol.Packet self) {
        throw new UnsupportedOperationException("CenturyBridge: Packets no longer hold a reference to their NetworkState in 1.20.5");
    }

    /** was net/minecraft/net.minecraft.world.level.block.entity.Hopper.field_12025Lnet/minecraft/net.minecraft.world.phys.shapes.VoxelShape; */
    public static net.minecraft.world.phys.shapes.VoxelShape field_12025() {
        return net.minecraft.world.phys.shapes.Shapes.block();
    }

    /** was net/minecraft/net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity.field_12037Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_12037() {
        throw new UnsupportedOperationException("CenturyBridge: lootTable is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/net.minecraft.world.level.block.entity.SpawnerBlockEntity$1.field_27219Lnet/minecraft/net.minecraft.world.level.block.entity.SpawnerBlockEntity; */
    public static net.minecraft.world.level.block.entity.SpawnerBlockEntity field_27219() {
        throw new UnsupportedOperationException("CenturyBridge: field_27219 is the synthetic outer this$0 field of net.minecraft.world.level.block.entity.SpawnerBlockEntity$1 and cannot be accessed statically without a receiver");
    }


    /** was net/minecraft/net.minecraft.world.level.chunk.LevelChunk$class_5564.field_27227Lnet/minecraft/net.minecraft.world.level.chunk.LevelChunk; */
    public static net.minecraft.world.level.chunk.LevelChunk field_27227() {
        throw new UnsupportedOperationException("CenturyBridge: Cannot access instance field field_27227 statically");
    }

    /** was net/minecraft/net.minecraft.world.level.chunk.LevelChunkSection$class_6869.field_36411Lnet/minecraft/net.minecraft.world.level.chunk.LevelChunkSection; */
    public static net.minecraft.world.level.chunk.LevelChunkSection field_36411() {
        throw new UnsupportedOperationException("CenturyBridge: field_36411 cannot be retrieved statically");
    }


    /** was net/minecraft/net.minecraft.world.level.levelgen.DebugLevelSource.field_24768Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24768() {
        return net.minecraft.world.level.levelgen.DebugLevelSource.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.FlatLevelSource.field_24769Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24769() {
        return net.minecraft.world.level.levelgen.FlatLevelSource.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.network.syncher.EntityDataAccessor.method_12712()Lnet/minecraft/net.minecraft.network.syncher.EntityDataSerializer; */
    public static net.minecraft.network.syncher.EntityDataSerializer method_12712(net.minecraft.network.syncher.EntityDataAccessor self) {
        return self.serializer();
    }

    /** was net/minecraft/net.minecraft.network.syncher.EntityDataAccessor.method_12713()I */
    public static int method_12713(net.minecraft.network.syncher.EntityDataAccessor self) {
        return self.id();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.TreeFeature$1.field_42840Lnet/minecraft/net.minecraft.world.level.levelgen.feature.TreeFeature; */
    public static net.minecraft.world.level.levelgen.feature.TreeFeature field_42840() {
        throw new UnsupportedOperationException("CenturyBridge: field_42840 is no longer available as the outer class is not captured by net.minecraft.world.level.levelgen.feature.TreeFeature$1");
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.structures.BuriedTreasureStructure.field_37790Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37790() {
        return net.minecraft.world.level.levelgen.structure.structures.BuriedTreasureStructure.CODEC.codec();
    }


    /** was net/minecraft/net.minecraft.world.level.storage.PlayerDataStorage.method_263()[Ljava/lang/String; */
    public static java.lang.String[] method_263(net.minecraft.world.level.storage.PlayerDataStorage self) {
        throw new UnsupportedOperationException("CenturyBridge: PlayerDataStorage no longer exposes saved player IDs");
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.placement.NoiseThresholdCountPlacement.field_24890Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24890() {
        return net.minecraft.world.level.levelgen.placement.NoiseThresholdCountPlacement.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.structures.DesertPyramidStructure.field_37791Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37791() {
        return net.minecraft.world.level.levelgen.structure.structures.DesertPyramidStructure.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.structures.EndCityStructure.field_37792Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37792() {
        return net.minecraft.world.level.levelgen.structure.structures.EndCityStructure.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.structures.IglooStructure.field_37793Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37793() {
        return net.minecraft.world.level.levelgen.structure.structures.IglooStructure.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.structures.JungleTempleStructure.field_37800Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37800() {
        return net.minecraft.world.level.levelgen.structure.structures.JungleTempleStructure.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.structures.MineshaftStructure.field_37801Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37801() {
        return net.minecraft.world.level.levelgen.structure.structures.MineshaftStructure.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure.field_37803Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37803() {
        return net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.client.Minecraft.method_1540()Z */
    public static boolean method_1540(net.minecraft.client.Minecraft self) {
        throw new UnsupportedOperationException("CenturyBridge: MinecraftClient.isDemo has no equivalent in 1.20.5");
    }

    /** was net/minecraft/net.minecraft.client.Minecraft.method_27466(Z)V */
    public static void method_27466(net.minecraft.client.Minecraft self, boolean a0) {
        try {
            java.lang.reflect.Field field = net.minecraft.client.Minecraft.class.getDeclaredField("field_24209");
            field.setAccessible(true);
            field.setBoolean(self, a0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.structures.OceanMonumentStructure.field_37806Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37806() {
        return net.minecraft.world.level.levelgen.structure.structures.OceanMonumentStructure.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.client.MouseHandler.turnPlayer()V */
    public static void method_1606(net.minecraft.client.MouseHandler self) {
        self.handleAccumulatedMovement();
    }

    /** was net/minecraft/net.minecraft.client.Options$2.field_28779Lnet/minecraft/net.minecraft.client.Options; */
    public static net.minecraft.client.Options field_28779() {
        return net.minecraft.client.Minecraft.getInstance().options;
    }

    /** was net/minecraft/net.minecraft.client.Options$3.field_28781Lnet/minecraft/net.minecraft.client.Options; */
    public static net.minecraft.client.Options field_28781() {
        throw new UnsupportedOperationException("CenturyBridge: field_28781 was an instance field of a deleted anonymous class and cannot be accessed without a receiver");
    }

    /** was net/minecraft/net.minecraft.client.Options.field_37208Z */
    public static boolean field_37208() {
        return false;
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.structures.ShipwreckStructure.field_37815Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37815() {
        return net.minecraft.world.level.levelgen.structure.structures.ShipwreckStructure.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.server.dedicated.ServerWatchdog$1.field_13826Lnet/minecraft/net.minecraft.server.dedicated.ServerWatchdog; */
    public static net.minecraft.server.dedicated.ServerWatchdog field_13826() {
        throw new UnsupportedOperationException("CenturyBridge: Instance field field_13826 cannot be accessed without a receiver");
    }

    /** was net/minecraft/net.minecraft.server.gui.MinecraftServerGui$2.field_13842Lnet/minecraft/net.minecraft.server.gui.MinecraftServerGui; */
    public static net.minecraft.server.gui.MinecraftServerGui field_13842() {
        throw new UnsupportedOperationException("CenturyBridge: field_13842 is an instance field but the shim is static without the receiver parameter");
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.structures.StrongholdStructure.field_37817Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37817() {
        return net.minecraft.world.level.levelgen.structure.structures.StrongholdStructure.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.structures.SwampHutStructure.field_37818Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37818() {
        return net.minecraft.world.level.levelgen.structure.structures.SwampHutStructure.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionStructure.field_37819Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37819() {
        return net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionStructure.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.server.packs.PackResources.method_45178()Z */
    public static boolean method_45178(net.minecraft.server.packs.PackResources self) {
        return self instanceof net.minecraft.server.packs.VanillaPackResources;
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.placement.NoiseBasedCountPlacement.field_24987Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24987() {
        return net.minecraft.world.level.levelgen.placement.NoiseBasedCountPlacement.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.structures.OceanRuinStructure.field_37807Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37807() {
        return net.minecraft.world.level.levelgen.structure.structures.OceanRuinStructure.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.structures.ShipwreckPieces.method_14834(Lnet/minecraft/net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;Lnet/minecraft/net.minecraft.core.BlockPos;Lnet/minecraft/net.minecraft.world.level.block.Rotation;Lnet/minecraft/net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;Lnet/minecraft/net.minecraft.util.RandomSource;Z)V */
    public static void method_14834(net.minecraft.world.level.levelgen.structure.structures.ShipwreckPieces self, net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager a0, net.minecraft.core.BlockPos a1, net.minecraft.world.level.block.Rotation a2, net.minecraft.world.level.levelgen.structure.StructurePieceAccessor a3, net.minecraft.util.RandomSource a4, boolean a5) {
        self.addRandomPiece(a0, a1, a2, a3, a4, a5);
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_14554Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14554() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.LLAMA_SWAG.value();
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_14581Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14581() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_LEATHER.value();
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_14684Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14684() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_TURTLE.value();
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_14761Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14761() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_GOLD.value();
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_14862Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14862() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_IRON.value();
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_14883Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14883() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_GENERIC.value();
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_14966Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14966() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_ELYTRA.value();
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_15103Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_15103() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_DIAMOND.value();
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_15191Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_15191() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_CHAIN.value();
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_21866Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_21866() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_NETHERITE.value();
    }


    /** was net/minecraft/net.minecraft.world.entity.monster.CrossbowAttackMob.method_18811(Lnet/minecraft/net.minecraft.world.entity.LivingEntity;Lnet/minecraft/net.minecraft.world.item.ItemStack;Lnet/minecraft/net.minecraft.world.entity.projectile.Projectile;F)V */
    public static void method_18811(net.minecraft.world.entity.monster.CrossbowAttackMob self, net.minecraft.world.entity.LivingEntity a0, net.minecraft.world.item.ItemStack a1, net.minecraft.world.entity.projectile.Projectile a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: CrossbowItem.shoot (method_18811) was removed in 1.20.5");
    }

    /** was net/minecraft/net.minecraft.world.entity.monster.CrossbowAttackMob.method_24652(Lnet/minecraft/net.minecraft.world.entity.LivingEntity;Lnet/minecraft/net.minecraft.world.entity.LivingEntity;Lnet/minecraft/net.minecraft.world.entity.projectile.Projectile;FF)V */
    public static void method_24652(net.minecraft.world.entity.monster.CrossbowAttackMob self, net.minecraft.world.entity.LivingEntity a0, net.minecraft.world.entity.LivingEntity a1, net.minecraft.world.entity.projectile.Projectile a2, float a3, float a4) {
        self.performCrossbowAttack(a1, a3);
    }

    /** was net/minecraft/net.minecraft.world.entity.monster.CrossbowAttackMob.method_24653(Lnet/minecraft/net.minecraft.world.entity.LivingEntity;Lnet/minecraft/net.minecraft.world.phys.Vec3;F)Lorg/joml/Vector3f; */
    public static org.joml.Vector3f method_24653(net.minecraft.world.entity.monster.CrossbowAttackMob self, net.minecraft.world.entity.LivingEntity a0, net.minecraft.world.phys.Vec3 a1, float a2) {
        double dx = a1.x - a0.getX();
        double dy = a1.y - a0.getEyeY();
        double dz = a1.z - a0.getZ();
        double dh = Math.sqrt(dx * dx + dz * dz);
        float pitch = (float) (-(Math.atan2(dy, dh) * 180.0 / Math.PI));
        float yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        return new org.joml.Vector3f(pitch, yaw, 0.0f);
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator.field_24773Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24773() {
        return net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.entity.raid.Raider$class_3764.field_16604Lnet/minecraft/net.minecraft.world.entity.raid.Raider; */
    public static net.minecraft.world.entity.raid.Raider field_16604() {
        throw new UnsupportedOperationException("CenturyBridge: field_16604 has been removed without replacement");
    }

    /** was net/minecraft/net.minecraft.world.entity.raid.Raider$class_4223.field_18882Lnet/minecraft/net.minecraft.world.entity.raid.Raider; */
    public static net.minecraft.world.entity.raid.Raider field_18882() {
        throw new UnsupportedOperationException("CenturyBridge: Outer class reference field_18882 was removed and cannot be accessed");
    }

    /** was net/minecraft/net.minecraft.world.entity.raid.Raider.method_20030(Lnet/minecraft/net.minecraft.world.entity.raid.Raider;)F */
    public static float method_20030(net.minecraft.world.entity.raid.Raider self, net.minecraft.world.entity.raid.Raider a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_20030 was deleted with no direct replacement");
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.pools.FeaturePoolElement.field_24948Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24948() {
        return net.minecraft.world.level.levelgen.structure.pools.FeaturePoolElement.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement.field_24947Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24947() {
        return net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.pools.ListPoolElement.field_24950Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24950() {
        return net.minecraft.world.level.levelgen.structure.pools.ListPoolElement.CODEC.codec();
    }




    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest.field_25014Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25014() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.inventory.GrindstoneMenu$2.field_16777Lnet/minecraft/net.minecraft.world.inventory.GrindstoneMenu; */
    public static net.minecraft.world.inventory.GrindstoneMenu field_16777() {
        throw new UnsupportedOperationException("CenturyBridge: Synthetic outer field cannot be accessed without receiver");
    }

    /** was net/minecraft/net.minecraft.world.inventory.GrindstoneMenu$3.field_16778Lnet/minecraft/net.minecraft.world.inventory.GrindstoneMenu; */
    public static net.minecraft.world.inventory.GrindstoneMenu field_16778() {
        throw new UnsupportedOperationException("CenturyBridge: Instance field accessed without receiver");
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest.field_24994Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24994() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest.field_24999Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24999() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest.field_25001Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25001() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest.CODEC.codec();
    }


    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockStateMatchTest.field_25010Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25010() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockStateMatchTest.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest.field_25009Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25009() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest.CODEC.codec();
    }


    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType.register(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Lnet/minecraft/net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType; */
    public net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType register(java.lang.String a0, com.mojang.serialization.Codec a1) {
        throw new UnsupportedOperationException("CenturyBridge: RecipeSerializer registration using raw Codec is no longer supported; use MapCodec instead");
    }

    /** was net/minecraft/com.mojang.blaze3d.font.GlyphBitmap.method_41712()F */
    public static float method_41712(com.mojang.blaze3d.font.GlyphBitmap self) {
        return (float)self.getPixelWidth() / (float)self.getPixelHeight();
    }

    /** was net/minecraft/com.mojang.blaze3d.font.GlyphBitmap.method_41713()F */
    public static float method_41713(com.mojang.blaze3d.font.GlyphBitmap self) {
        return 1.0f;
    }

    /** was net/minecraft/net.minecraft.client.gui.font.providers.GlyphProviderDefinition.field_44801Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_44801() {
        return net.minecraft.client.gui.font.providers.GlyphProviderDefinition.MAP_CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.util.datafix.schemas.V1928.method_17997(Lcom/mojang/datafixers/schemas/Schema;)Lcom/mojang/datafixers/types/templates/TypeTemplate; */
    public static com.mojang.datafixers.types.templates.TypeTemplate method_17997(net.minecraft.util.datafix.schemas.V1928 self, com.mojang.datafixers.schemas.Schema a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_17997 was removed in 1.20.5 with no direct replacement");
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_16216Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_16216() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.CAT_MORNING_GIFT.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_16593Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_16593() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.PILLAGER_OUTPOST.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_16748Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_16748() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_PLAINS_HOUSE.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_16749Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_16749() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_TAIGA_HOUSE.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_16750Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_16750() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_TANNERY.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_16751Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_16751() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_CARTOGRAPHER.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_16752Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_16752() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_DESERT_HOUSE.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_16753Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_16753() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_SAVANNA_HOUSE.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_16754Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_16754() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_SNOWY_HOUSE.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_17009Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_17009() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_ARMORER.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_17010Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_17010() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_MASON.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_17011Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_17011() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_SHEPHERD.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_17012Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_17012() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_BUTCHER.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_17107Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_17107() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_TOOLSMITH.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_17108Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_17108() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_FLETCHER.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_17109Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_17109() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_TEMPLE.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_18007Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_18007() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_FISHER.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_19062Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_19062() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.ARMORER_GIFT.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_19063Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_19063() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.BUTCHER_GIFT.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_19064Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_19064() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.CARTOGRAPHER_GIFT.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_19065Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_19065() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.CLERIC_GIFT.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_19066Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_19066() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.FARMER_GIFT.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_19067Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_19067() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.FISHERMAN_GIFT.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_19068Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_19068() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.FLETCHER_GIFT.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_19069Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_19069() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.LEATHERWORKER_GIFT.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_19070Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_19070() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.LIBRARIAN_GIFT.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_19071Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_19071() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.MASON_GIFT.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_19072Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_19072() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.SHEPHERD_GIFT.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_19073Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_19073() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TOOLSMITH_GIFT.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_19074Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_19074() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.WEAPONSMITH_GIFT.identifier();
    }


    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_22402Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_22402() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.PIGLIN_BARTERING.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_24046Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_24046() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.BASTION_TREASURE.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_24047Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_24047() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.BASTION_OTHER.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_24048Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_24048() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.BASTION_BRIDGE.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_24049Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_24049() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.BASTION_HOGLIN_STABLE.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_24050Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_24050() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.RUINED_PORTAL.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_251Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_251() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.BURIED_TREASURE.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_266Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_266() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.FISHING_JUNK.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_274Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_274() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.END_CITY_TREASURE.identifier();
    }


    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_300Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_300() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.UNDERWATER_RUIN_BIG.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_353Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_353() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.FISHING.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_356Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_356() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.SIMPLE_DUNGEON.identifier();
    }


    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_38438Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_38438() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.ANCIENT_CITY.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_38439Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_38439() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.ANCIENT_CITY_ICE_BOX.identifier();
    }



    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_397Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_397() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.UNDERWATER_RUIN_SMALL.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_43353Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_43353() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_43354Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_43354() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_43356Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_43356() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_43357Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_43357() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_434Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_434() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_WEAPONSMITH.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_44648Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_44648() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_44649Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_44649() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_44748Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_44748() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.SNIFFER_DIGGING.identifier();
    }


    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_472Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_472() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.ABANDONED_MINESHAFT.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_47415Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_47415() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_REWARD.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_47416Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_47416() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_SUPPLY.registry();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_47417Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_47417() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_CORRIDOR.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_47418Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_47418() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_INTERSECTION.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_47419Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_47419() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_INTERSECTION_BARREL.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_47420Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_47420() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_ENTRANCE.registry();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_47421Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_47421() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_CORRIDOR_DISPENSER.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_47422Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_47422() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_CHAMBER_DISPENSER.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_47423Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_47423() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.SPAWNER_TRIAL_CHAMBER_KEY.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_47424Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_47424() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.SPAWNER_TRIAL_CHAMBER_CONSUMABLES.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_47425Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_47425() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_WATER_DISPENSER.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_47426Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_47426() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_CORRIDOR_POT.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_484Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_484() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.WOODLAND_MANSION.identifier();
    }



    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_615Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_615() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.NETHER_BRIDGE.identifier();
    }


    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_662Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_662() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.IGLOO_CHEST.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_665Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_665() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.SHIPWRECK_TREASURE.identifier();
        // CB-CONFIDENCY: high -- SHIPWRECK_TREASURE changed from Identifier to RegistryKey; identifier() on net.minecraft.resources.ResourceKey returns the Identifier (net.minecraft.resources.Identifier), preserving the old value exactly.
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_683Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_683() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.STRONGHOLD_LIBRARY.identifier();
    }



    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_751Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_751() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.JUNGLE_TEMPLE_DISPENSER.identifier();
    }


    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_795Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_795() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.FISHING_FISH.registry();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_800Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_800() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.STRONGHOLD_CROSSING.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_803Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_803() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.JUNGLE_TEMPLE.identifier();
    }



    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_841Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_841() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.SHIPWRECK_MAP.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_842Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_842() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.STRONGHOLD_CORRIDOR.identifier();
    }


    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_850Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_850() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.SPAWN_BONUS_CHEST.registry();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_854Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_854() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.FISHING_TREASURE.registry();
    }



    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_880Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_880() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.SHIPWRECK_SUPPLY.identifier();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.BuiltInLootTables.field_885Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier field_885() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.DESERT_PYRAMID.identifier();
    }

    /** was net/minecraft/net.minecraft.world.entity.animal.fox.Fox$class_4024.field_17973Lnet/minecraft/net.minecraft.world.entity.animal.fox.Fox; */
    public static net.minecraft.world.entity.animal.fox.Fox field_17973() {
        throw new UnsupportedOperationException("CenturyBridge: field_17973 (net.minecraft.world.entity.animal.fox.Fox constant on net.minecraft.world.entity.animal.fox.Fox$class_4024) was deleted with no direct replacement and its value cannot be reconstructed from the new API surface");
        // CB-CONFIDENCY: high -- the field is explicitly marked deleted with no direct replacement, and the new API surface provides no equivalent constant or accessor to reconstruct the held net.minecraft.world.entity.animal.fox.Fox value
    }

    /** was net/minecraft/net.minecraft.world.entity.animal.fox.Fox$class_4052.field_18103Lnet/minecraft/net.minecraft.world.entity.animal.fox.Fox; */
    public static net.minecraft.world.entity.animal.fox.Fox field_18103() {
        throw new UnsupportedOperationException("CenturyBridge: field_18103 (static net.minecraft.world.entity.animal.fox.Fox on net.minecraft.world.entity.animal.fox.Fox$class_4052) was deleted in 1.20.5 with no replacement; the new class exposes no method returning net.minecraft.world.entity.animal.fox.Fox");
    }

    /** was net/minecraft/net.minecraft.core.GlobalPos.method_19442()Lnet/minecraft/net.minecraft.resources.ResourceKey; */
    public static net.minecraft.resources.ResourceKey method_19442(net.minecraft.core.GlobalPos self) {
        return self.dimension();
    }

    /** was net/minecraft/net.minecraft.core.GlobalPos.method_19446()Lnet/minecraft/net.minecraft.core.BlockPos; */
    public static net.minecraft.core.BlockPos method_19446(net.minecraft.core.GlobalPos self) {
        return self.pos();
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen.field_19747I */
    public static int field_19747() {
        throw new UnsupportedOperationException("CenturyBridge: com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen.field_19747 was a deleted static int constant with no direct replacement in 1.20.5; its value cannot be inferred from the new API surface");
        // CB-CONFIDENCE: medium -- the field is genuinely gone with no equivalent, but a tombstone is the honest choice since the original constant's value is not recoverable from the new API surface
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen.method_21155(I)V */
    public static void method_21155(com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_21155(int) switched tabs by ordinal index; 1.20.5 CreateWorldScreen uses com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen$class_4382 tab objects via backupList/layout with no int-indexed equivalent");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab.field_19970I */
    public static int field_19970() {
        throw new UnsupportedOperationException("CenturyBridge: com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab.field_19970 was a mutable per-instance animation tick counter on EnchantmentScreen, removed in 1.20.5; no static constant equivalent exists");
        // CB-CONFIDENCE: high -- field_19970 was mutable instance state (tick counter incremented in aM_), not a constant; a static shim with no receiver cannot reproduce per-instance mutable state
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab.field_26498Lnet/minecraft/net.minecraft.network.chat.Component; */
    public static net.minecraft.network.chat.Component field_26498() {
        throw new UnsupportedOperationException("CenturyBridge: field_26498 was deleted in 1.20.5 with no direct replacement");
        // CB-CONFIDENCE: high -- the prompt explicitly states "deleted -- no direct replacement"; QUESTION_TITLE is an instance field of the same type but cannot be confirmed as the semantic equivalent of the old static field
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab.field_26500Lnet/minecraft/net.minecraft.network.chat.Component; */
    public static net.minecraft.network.chat.Component field_26500() {
        throw new UnsupportedOperationException("CenturyBridge: field_26500 was deleted in 1.20.5 with no direct replacement");
        // CB-CONFIDENCE: high -- field explicitly deleted, no equivalent in new API surface; tombstone is the correct choice
    }






    /** was net/minecraft/net.minecraft.gametest.framework.GameTestBatch.method_22153(Lnet/minecraft/net.minecraft.server.level.ServerLevel;)V */
    public static void method_22153(net.minecraft.gametest.framework.GameTestBatch self, net.minecraft.server.level.ServerLevel a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_22153(ServerWorld) was deleted in 1.20.5 with no direct replacement; net.minecraft.gametest.framework.GameTestBatch is now a record (String, Collection, Consumer, Consumer) with no ServerWorld-interacting methods");
    }

    /** was net/minecraft/net.minecraft.gametest.framework.GameTestBatch.method_22154()Ljava/util/Collection; */
    public static java.util.Collection method_22154(net.minecraft.gametest.framework.GameTestBatch self) {
        return self.gameTestInfos();
    }

    /** was net/minecraft/net.minecraft.gametest.framework.GameTestListener.testPassed(Lnet/minecraft/net.minecraft.gametest.framework.GameTestInfo;)V */
    public void testPassed(net.minecraft.gametest.framework.GameTestInfo a0) {
        throw new UnsupportedOperationException("CenturyBridge: testPassed now requires a net.minecraft.gametest.framework.GameTestRunner raid-context parameter whose constructor needs server world, collections, and nested types unavailable from net.minecraft.gametest.framework.GameTestInfo alone");
    }



    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer.field_24926Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24926() {
        return net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer.field_24927Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24927() {
        return net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.foliageplacers.PineFoliagePlacer.field_24935Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24935() {
        return net.minecraft.world.level.levelgen.feature.foliageplacers.PineFoliagePlacer.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer.field_24936Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24936() {
        return net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.stateproviders.RotatedBlockProvider.field_24944Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24944() {
        return net.minecraft.world.level.levelgen.feature.stateproviders.RotatedBlockProvider.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider.field_24945Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24945() {
        return net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.treedecorators.CocoaDecorator.field_24959Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24959() {
        return net.minecraft.world.level.levelgen.feature.treedecorators.CocoaDecorator.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.client.renderer.Sheets.field_28253Ljava/util/Map; */
    public static java.util.Map field_28253() {
        throw new UnsupportedOperationException("CenturyBridge: SHIELD_MATERIALS was a memoized render-layer cache map removed in 1.20.5; replaced by direct method calls (e.g. getDecoratedPotMaterial) with incompatible key types");
        // CB-CONFIDENCE: medium -- field was a Map on TexturedRenderLayers deleted with no replacement; the memoized-cache nature means it cannot be rebuilt as a constant, and the new API uses different key types (RegistryKey vs Identifier)
    }

    /** was net/minecraft/net.minecraft.client.renderer.Sheets.method_24066(Ljava/util/function/Consumer;)V */
    public static void method_24066(net.minecraft.client.renderer.Sheets self, java.util.function.Consumer a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.renderer.Sheets.method_24066(Consumer) was removed in 1.20.5; the model loading pipeline no longer collects model identifiers via a consumer callback");
        // CB-CONFIDENCE: high -- method explicitly deleted with no direct replacement; the 1.20.5 model loading pipeline was reworked and no new API accepts a Consumer of identifiers
    }

    /** was net/minecraft/net.minecraft.world.level.biome.MultiNoiseBiomeSource.field_24719Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24719() {
        return net.minecraft.world.level.biome.MultiNoiseBiomeSource.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.block.RespawnAnchorBlock$1.field_25406Lnet/minecraft/net.minecraft.world.level.block.RespawnAnchorBlock; */
    public static net.minecraft.world.level.block.RespawnAnchorBlock field_25406() {
        throw new UnsupportedOperationException("CenturyBridge: field_25406 was the synthetic outer instance reference (this$0) of net.minecraft.world.level.block.RespawnAnchorBlock$1, no longer mapped in 1.20.5; cannot reconstruct from a parameterless static shim without an instance");
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.templatesystem.AxisAlignedLinearPosTest.field_24995Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24995() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.AxisAlignedLinearPosTest.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.templatesystem.LinearPosTest.field_25004Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25004() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.LinearPosTest.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen.method_2528(Ljava/util/List;)V */
    public static void method_2528(net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen self, java.util.List a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen.method_2528(List) was removed in 1.20.5 with no direct replacement; no method in the new API accepts a List");
        // CB-CONFIDENCE: high -- the method is explicitly deleted with no direct replacement, and no remaining method in net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen accepts a List parameter
    }

    /** was net/minecraft/net.minecraft.world.entity.ai.attributes.AttributeMap.method_26842(Lnet/minecraft/net.minecraft.world.entity.ai.attributes.Attribute;)Lnet/minecraft/net.minecraft.world.entity.ai.attributes.AttributeInstance; */
    public static net.minecraft.world.entity.ai.attributes.AttributeInstance method_26842(net.minecraft.world.entity.ai.attributes.AttributeMap self, net.minecraft.world.entity.ai.attributes.Attribute a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_26842(ServerPlayerEntity) has no equivalent in 1.20.5; boss bar player management was reworked to use RegistryKey-based lookups (getInstance)");
    }

    /** was net/minecraft/net.minecraft.world.entity.ai.attributes.AttributeMap.method_26854(Lcom/google/common/collect/Multimap;)V */
    public static void method_26854(net.minecraft.world.entity.ai.attributes.AttributeMap self, com.google.common.collect.Multimap a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_26854(Multimap) was removed in 1.20.5; AttributeContainer no longer accepts a Multimap of modifiers -- the attribute modifier system was reworked to RegistryEntry-keyed queries with no bulk-apply equivalent");
    }

    /** was net/minecraft/net.minecraft.world.entity.ai.attributes.Attributes.field_23716Lnet/minecraft/net.minecraft.world.entity.ai.attributes.Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23716() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH.value();
    }

    /** was net/minecraft/net.minecraft.world.entity.ai.attributes.Attributes.field_23718Lnet/minecraft/net.minecraft.world.entity.ai.attributes.Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23718() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE.value();
    }

    /** was net/minecraft/net.minecraft.world.entity.ai.attributes.Attributes.field_23719Lnet/minecraft/net.minecraft.world.entity.ai.attributes.Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23719() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED.value();
        // CB-CONFIDENCE: high -- MOVEMENT_SPEED is now a RegistryEntry<EntityAttribute>; value() unwraps to the value, which is the old EntityAttribute
    }

    /** was net/minecraft/net.minecraft.world.entity.ai.attributes.Attributes.field_23720Lnet/minecraft/net.minecraft.world.entity.ai.attributes.Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23720() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.FLYING_SPEED.value();
    }

    /** was net/minecraft/net.minecraft.world.entity.ai.attributes.Attributes.field_23723Lnet/minecraft/net.minecraft.world.entity.ai.attributes.Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23723() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED.value();
    }

    /** was net/minecraft/net.minecraft.world.entity.ai.attributes.Attributes.field_23724Lnet/minecraft/net.minecraft.world.entity.ai.attributes.Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23724() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.ARMOR.value();
    }

    /** was net/minecraft/net.minecraft.world.entity.ai.attributes.Attributes.field_23725Lnet/minecraft/net.minecraft.world.entity.ai.attributes.Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23725() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS.value();
    }

    /** was net/minecraft/net.minecraft.world.entity.ai.attributes.Attributes.field_23727Lnet/minecraft/net.minecraft.world.entity.ai.attributes.Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23727() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.SPAWN_REINFORCEMENTS_CHANCE.value();
    }

    /** was net/minecraft/net.minecraft.world.entity.ai.attributes.Attributes.field_23728Lnet/minecraft/net.minecraft.world.entity.ai.attributes.Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23728() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.JUMP_STRENGTH.value();
    }

    /** was net/minecraft/net.minecraft.world.entity.ai.attributes.Attributes.field_45124Lnet/minecraft/net.minecraft.world.entity.ai.attributes.Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_45124() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.MAX_ABSORPTION.value();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer.field_24968Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24968() {
        return net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer.field_24971Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24971() {
        return net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer.CODEC.codec();
    }


    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.structures.RuinedPortalStructure.field_37812Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37812() {
        return net.minecraft.world.level.levelgen.structure.structures.RuinedPortalStructure.CODEC.codec();
    }



    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize.field_24924Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24924() {
        return net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize.field_24925Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24925() {
        return net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.foliageplacers.DarkOakFoliagePlacer.field_24929Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24929() {
        return net.minecraft.world.level.levelgen.feature.foliageplacers.DarkOakFoliagePlacer.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer.field_24930Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24930() {
        return net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.foliageplacers.MegaJungleFoliagePlacer.field_24933Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24933() {
        return net.minecraft.world.level.levelgen.feature.foliageplacers.MegaJungleFoliagePlacer.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer.field_24966Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24966() {
        return net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer.field_24967Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24967() {
        return net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer.field_24969Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24969() {
        return net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.storage.LevelData.method_144()I */
    public static int method_144(net.minecraft.world.level.storage.LevelData self) {
        throw new UnsupportedOperationException("CenturyBridge: getSeaLevel (method_144) was removed from net.minecraft.world.level.storage.LevelData in 1.20.5 with no direct replacement; sea level is no longer exposed on WorldProperties");
    }



    /** was net/minecraft/net.minecraft.world.level.storage.WritableLevelData.method_27416(I)V */
    public static void method_27416(net.minecraft.world.level.storage.WritableLevelData self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_27416(int) has no 1.20.5 equivalent; the only remaining method setSpawn requires a BlockPos and float that cannot be derived from the int argument");
    }

    /** was net/minecraft/net.minecraft.world.level.storage.WritableLevelData.method_27417(I)V */
    public static void method_27417(net.minecraft.world.level.storage.WritableLevelData self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_27417(int) was removed in 1.20.5 with no direct replacement; remaining setSpawn(BlockPos, float) is semantically incompatible -- cannot derive BlockPos+float from a single int");
    }

    /** was net/minecraft/net.minecraft.world.level.storage.WritableLevelData.method_27419(I)V */
    public static void method_27419(net.minecraft.world.level.storage.WritableLevelData self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_27419(int) was deleted with no direct replacement; the only remaining setSpawn requires a BlockPos and float, neither of which can be derived from the single int argument, so the old contract cannot be faithfully expressed");
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen$1.field_24574[I */
    public static int[] field_24574() {
        throw new UnsupportedOperationException("CenturyBridge: field_24574 was deleted in 1.20.5 with no direct replacement; field_24575 is a distinct field (stable intermediary names), so the original constant value cannot be faithfully reconstructed");
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen$class_5291.field_24585Lnet/minecraft/net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen; */
    public static net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen field_24585() {
        throw new UnsupportedOperationException("CenturyBridge: field_24585 was the synthetic outer net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen reference (this$0) of inner class net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen$class_5291; it is an instance field bound to a specific instance and cannot be reconstructed as a static constant without a receiver");
    }


    /** was net/minecraft/net.minecraft.world.level.levelgen.placement.InSquarePlacement.field_35727Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35727() {
        return net.minecraft.world.level.levelgen.placement.InSquarePlacement.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement.field_35716Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35716() {
        return net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.core.RegistryAccess$class_7781.field_40586Lnet/minecraft/net.minecraft.core.RegistryAccess; */
    public static net.minecraft.core.RegistryAccess field_40586() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.core.RegistryAccess$class_7781.field_40586 (a default net.minecraft.core.RegistryAccess instance) was removed in 1.20.5 with no direct replacement; the new class only exposes a constructor requiring an existing net.minecraft.core.RegistryAccess, so the old singleton cannot be faithfully reconstructed");
    }

    /** was net/minecraft/net.minecraft.world.item.BundleItem.field_30857I */
    public static int field_30857() {
        return 3;
    }

    /** was net/minecraft/net.minecraft.client.model.geom.ModelLayers.field_47444Lnet/minecraft/net.minecraft.client.model.geom.ModelLayerLocation; */
    public static net.minecraft.client.model.geom.ModelLayerLocation field_47444() {
        throw new UnsupportedOperationException("CenturyBridge: field_47444 was removed in 1.20.5 with no direct replacement; cannot determine which PositionType it corresponded to");
    }

    /** was net/minecraft/net.minecraft.client.model.geom.ModelLayers.field_47445Lnet/minecraft/net.minecraft.client.model.geom.ModelLayerLocation; */
    public static net.minecraft.client.model.geom.ModelLayerLocation field_47445() {
        throw new UnsupportedOperationException("CenturyBridge: field_47445 (a ParticleType constant in ParticleTypes) was removed in 1.20.5 with no direct replacement");
    }

    /** was net/minecraft/net.minecraft.world.level.storage.loot.functions.SequenceFunction.field_45834Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_45834() {
        return net.minecraft.world.level.storage.loot.functions.SequenceFunction.INLINE_CODEC;
    }

    /** was net/minecraft/net.minecraft.world.level.gameevent.BlockPositionSource.field_28137Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_28137() {
        return net.minecraft.world.level.gameevent.BlockPositionSource.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.gameevent.BlockPositionSource.field_28138Lnet/minecraft/net.minecraft.core.BlockPos; */
    public static net.minecraft.core.BlockPos field_28138() {
        return new net.minecraft.core.BlockPos(0, 0, 0);
    }

    /** was net/minecraft/net.minecraft.world.level.gameevent.GameEventListenerRegistry.visitInRangeListeners(Lnet/minecraft/net.minecraft.world.level.gameevent.GameEvent;Lnet/minecraft/net.minecraft.world.phys.Vec3;Lnet/minecraft/net.minecraft.world.level.gameevent.GameEvent$class_7397;Lnet/minecraft/net.minecraft.world.level.gameevent.GameEventListenerRegistry$class_7721;)Z */
    public boolean visitInRangeListeners(net.minecraft.world.level.gameevent.GameEvent a0, net.minecraft.world.phys.Vec3 a1, net.minecraft.world.level.gameevent.GameEvent.Context a2, net.minecraft.world.level.gameevent.GameEventListenerRegistry.ListenerVisitor a3) {
        return ((net.minecraft.world.level.gameevent.GameEventListenerRegistry) (Object) this).visitInRangeListeners(net.minecraft.core.Holder.direct(a0), a1, a2, a3);
    }

    /** was net/minecraft/net.minecraft.world.level.gameevent.GameEventListener.handleGameEvent(Lnet/minecraft/net.minecraft.server.level.ServerLevel;Lnet/minecraft/net.minecraft.world.level.gameevent.GameEvent;Lnet/minecraft/net.minecraft.world.level.gameevent.GameEvent$class_7397;Lnet/minecraft/net.minecraft.world.phys.Vec3;)Z */
    public boolean handleGameEvent(net.minecraft.server.level.ServerLevel a0, net.minecraft.world.level.gameevent.GameEvent a1, net.minecraft.world.level.gameevent.GameEvent.Context a2, net.minecraft.world.phys.Vec3 a3) {
        return ((net.minecraft.world.level.gameevent.GameEventListener)(Object)this).handleGameEvent(a0, net.minecraft.core.Holder.direct(a1), a2, a3);
    }

    /** was net/minecraft/net.minecraft.world.level.gameevent.PositionSourceType.method_32960(Lnet/minecraft/net.minecraft.network.FriendlyByteBuf;Lnet/minecraft/net.minecraft.world.level.gameevent.PositionSource;)V */
    public static void method_32960(net.minecraft.world.level.gameevent.PositionSourceType self, net.minecraft.network.FriendlyByteBuf a0, net.minecraft.world.level.gameevent.PositionSource a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_32960 wrote TextContent to PacketByteBuf in 1.20.1's binary format; 1.20.5 replaced this with MapCodec serialization (codec) using a different on-wire format");
    }




    /** was net/minecraft/net.minecraft.world.level.levelgen.placement.HeightmapPlacement.field_29265Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_29265() {
        return net.minecraft.world.level.levelgen.placement.HeightmapPlacement.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.foliageplacers.RandomSpreadFoliagePlacer.field_29297Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_29297() {
        return net.minecraft.world.level.levelgen.feature.foliageplacers.RandomSpreadFoliagePlacer.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.trunkplacers.BendingTrunkPlacer.field_29306Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_29306() {
        return net.minecraft.world.level.levelgen.feature.trunkplacers.BendingTrunkPlacer.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter.field_29323Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_29323() {
        return net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter.CODEC.codec();
    }




    /** was net/minecraft/net.minecraft.world.level.block.LevelEvent.field_47339I */
    public static int field_47339() {
        throw new UnsupportedOperationException("CenturyBridge: field_47339 was deleted in 1.20.5 with no direct replacement; its int value cannot be reconstructed from the new API surface");
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.heightproviders.BiasedToBottomHeight.field_31531Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_31531() {
        return net.minecraft.world.level.levelgen.heightproviders.BiasedToBottomHeight.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.gametest.framework.GameTestServer$1.field_33163Lnet/minecraft/net.minecraft.gametest.framework.GameTestServer; */
    public static net.minecraft.gametest.framework.GameTestServer field_33163() {
        throw new UnsupportedOperationException("CenturyBridge: field_33163 (type net.minecraft.gametest.framework.GameTestServer) in net.minecraft.gametest.framework.GameTestServer$1 was deleted in 1.20.5 with no direct replacement; cannot reconstruct a net.minecraft.gametest.framework.GameTestServer constant without constructor or factory information for the outer type");
    }



    /** was net/minecraft/net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight.field_33522Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_33522() {
        return net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.heightproviders.VeryBiasedToBottomHeight.field_33527Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_33527() {
        return net.minecraft.world.level.levelgen.heightproviders.VeryBiasedToBottomHeight.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.client.multiplayer.MultiPlayerGameMode.method_2904()F */
    public static float method_2904(net.minecraft.client.multiplayer.MultiPlayerGameMode self) {
        return (float) self.getDestroyStage();
        // CB-CONFIDENCE: medium -- getDestroyStage is the likely int-returning replacement for the old float getBlockReachDistance; the int-to-float cast loses the 0.5 fractional reach in survival mode
    }

    /** was net/minecraft/net.minecraft.client.gui.narration.ScreenNarrationCollector$1.field_33799Lnet/minecraft/net.minecraft.client.gui.narration.ScreenNarrationCollector; */
    public static net.minecraft.client.gui.narration.ScreenNarrationCollector field_33799() {
        throw new UnsupportedOperationException("CenturyBridge: field_33799 was the synthetic this$0 outer-instance reference of net.minecraft.client.gui.narration.ScreenNarrationCollector$1, deleted in 1.20.5 because the anonymous class no longer references its enclosing net.minecraft.client.gui.narration.ScreenNarrationCollector; a per-instance outer reference cannot be rebuilt as a static constant");
    }

    /** was net/minecraft/net.minecraft.client.multiplayer.ClientLevel.method_2944(Lnet/minecraft/net.minecraft.world.scores.Scoreboard;)V */
    public static void method_2944(net.minecraft.client.multiplayer.ClientLevel self, net.minecraft.world.scores.Scoreboard a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.multiplayer.ClientLevel.method_2944(net.minecraft.world.scores.Scoreboard) (setParticleManager) was removed; 1.20.5 net.minecraft.client.multiplayer.ClientLevel only exposes M() to get the particle manager, with no setter");
        // CB-CONFIDENCE: high -- old method_2944 is the particle-manager setter; new ClientWorld exposes only getter M() and no net.minecraft.world.scores.Scoreboard-accepting setter remains
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.stateproviders.NoiseThresholdProvider.field_34713Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_34713() {
        return net.minecraft.world.level.levelgen.feature.stateproviders.NoiseThresholdProvider.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.placement.SurfaceRelativeThresholdFilter.field_34721Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_34721() {
        return net.minecraft.world.level.levelgen.placement.SurfaceRelativeThresholdFilter.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.placement.BlockPredicateFilter.field_35075Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35075() {
        return net.minecraft.world.level.levelgen.placement.BlockPredicateFilter.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.blockpredicates.WouldSurvivePredicate.field_35160Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35160() {
        return net.minecraft.world.level.levelgen.blockpredicates.WouldSurvivePredicate.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.SurfaceSystem$1.field_35297Lnet/minecraft/net.minecraft.world.level.levelgen.SurfaceSystem; */
    public static net.minecraft.world.level.levelgen.SurfaceSystem field_35297() {
        throw new UnsupportedOperationException("CenturyBridge: field_35297 was the synthetic this$0 outer instance reference of net.minecraft.world.level.levelgen.SurfaceSystem$1; it is an instance field and cannot be returned from a parameterless static helper");
    }


    /** was net/minecraft/net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement.field_35419Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35419() {
        return net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.heightproviders.WeightedListHeight.field_35712Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35712() {
        return net.minecraft.world.level.levelgen.heightproviders.WeightedListHeight.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.placement.BiomeFilter.field_35714Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35714() {
        return net.minecraft.world.level.levelgen.placement.BiomeFilter.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.placement.CountPlacement.field_35718Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35718() {
        return net.minecraft.world.level.levelgen.placement.CountPlacement.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement.field_35720Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35720() {
        return net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.placement.RarityFilter.field_35752Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35752() {
        return net.minecraft.world.level.levelgen.placement.RarityFilter.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement.field_36420Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_36420() {
        return net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.multiplayer.WarningScreen.method_41160(I)V */
    public static void method_41160(net.minecraft.client.gui.screens.multiplayer.WarningScreen self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.multiplayer.WarningScreen.method_41160(I)V was removed in 1.20.5 with no direct replacement; the new API surface has no int-parameter method on this class");
    }





    /** was net/minecraft/net.minecraft.world.level.levelgen.RandomState$1.field_40361Lnet/minecraft/net.minecraft.world.level.levelgen.RandomState; */
    public static net.minecraft.world.level.levelgen.RandomState field_40361() {
        throw new UnsupportedOperationException("CenturyBridge: field_40361 was an instance field holding the enclosing net.minecraft.world.level.levelgen.RandomState reference; cannot reconstruct from a static context with no receiver");
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.worldselection.WorldOpenFlows.method_54618(Ljava/lang/String;Ljava/lang/Runnable;)V */
    public static void method_54618(net.minecraft.client.gui.screens.worldselection.WorldOpenFlows self, java.lang.String a0, java.lang.Runnable a1) {
        self.openWorld(a0, a1);
    }

    /** was net/minecraft/net.minecraft.client.particle.SuspendedParticle$class_5877$1.field_29074Lnet/minecraft/net.minecraft.client.particle.SuspendedParticle$class_5877; */
    public static net.minecraft.client.particle.SuspendedParticle.SporeBlossomAirProvider field_29074() {
        throw new UnsupportedOperationException("CenturyBridge: field_29074 was a static net.minecraft.client.particle.SuspendedParticle$class_5877 constant in the anonymous inner class net.minecraft.client.particle.SuspendedParticle$class_5877$1, deleted in 1.20.5 with no replacement; the original constant value cannot be reconstructed from the new API");
        // CB-CONFIDENCE: high -- the field is explicitly deleted with no direct replacement, and the new API surface provides no way to reconstruct the original constant value
    }

    /** was net/minecraft/net.minecraft.world.entity.monster.warden.Warden$1.field_39306Lnet/minecraft/net.minecraft.world.entity.monster.warden.Warden; */
    public static net.minecraft.world.entity.monster.warden.Warden field_39306() {
        return null;
    }

    /** was net/minecraft/net.minecraft.world.entity.vehicle.ContainerEntity.getContainerLootTable()Lnet/minecraft/net.minecraft.resources.Identifier; */
    public net.minecraft.resources.Identifier getContainerLootTable() {
        return ((net.minecraft.world.entity.vehicle.ContainerEntity) (Object) this).getContainerLootTable().identifier();
    }



    /** was net/minecraft/net.minecraft.util.datafix.schemas.V3083.method_42645(Lcom/mojang/datafixers/schemas/Schema;Ljava/util/Map;Ljava/lang/String;)V */
    public static void method_42645(net.minecraft.util.datafix.schemas.V3083 self, com.mojang.datafixers.schemas.Schema a0, java.util.Map a1, java.lang.String a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_42645(Schema, Map, String) was a per-name type registration helper deleted in 1.20.5 with no direct replacement; new net.minecraft.util.datafix.schemas.V3083 only exposes bulk registerEntities(Schema) returning a new Map, with no per-name registration equivalent");
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.treedecorators.AttachedToLeavesDecorator.field_38783Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_38783() {
        return net.minecraft.world.level.levelgen.feature.treedecorators.AttachedToLeavesDecorator.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.trunkplacers.UpwardsBranchingTrunkPlacer.field_38793Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_38793() {
        return net.minecraft.world.level.levelgen.feature.trunkplacers.UpwardsBranchingTrunkPlacer.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.commands.arguments.ArgumentSignatures$class_7599.comp_914Lnet/minecraft/net.minecraft.network.chat.MessageSignature; */
    public static net.minecraft.network.chat.MessageSignature comp_914() {
        throw new UnsupportedOperationException("CenturyBridge: signature was a static field on net.minecraft.commands.arguments.ArgumentSignatures$class_7599, deleted in 1.20.5; new signature() is an instance method with no static equivalent");
    }

    /** was net/minecraft/net.minecraft.client.multiplayer.chat.LoggedChatMessage$class_7558.field_40809Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_40809() {
        return net.minecraft.client.multiplayer.chat.LoggedChatMessage.Player.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.client.multiplayer.chat.LoggedChatMessage$class_7559.field_40810Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_40810() {
        return net.minecraft.client.multiplayer.chat.LoggedChatMessage.System.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.client.renderer.GameRenderer.field_3996[Lnet/minecraft/net.minecraft.resources.Identifier; */
    public static net.minecraft.resources.Identifier[] field_3996() {
        throw new UnsupportedOperationException("CenturyBridge: field_3996 (SHADERS_LOCATIONS) was removed in 1.20.5 when the core shader system was reworked; the new API provides individual com.mojang.blaze3d.opengl.GlProgram shader accessors but no equivalent identifier array");
    }

    /** was net/minecraft/net.minecraft.client.renderer.GameRenderer.method_3168(Lnet/minecraft/net.minecraft.resources.Identifier;)V */
    public static void method_3168(net.minecraft.client.renderer.GameRenderer self, net.minecraft.resources.Identifier a0) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_3168(Identifier) loadPostProcessor was removed in 1.20.5; post-processing pipeline reworked, no equivalent loader exists on net.minecraft.client.renderer.GameRenderer");
    }

    /** was net/minecraft/net.minecraft.world.entity.animal.camel.Camel.field_40131Lnet/minecraft/net.minecraft.world.item.crafting.Ingredient; */
    public static net.minecraft.world.item.crafting.Ingredient field_40131() {
        throw new UnsupportedOperationException("CenturyBridge: field_40131 (Ingredient) was deleted in 1.20.5 with no direct replacement; cannot reconstruct the allay duplication ingredient as a static constant");
        // CB-CONFIDENCE: high -- the field is explicitly deleted with no direct replacement, and the new API surface contains no Ingredient field or factory to rebuild it from
    }

    /** was net/minecraft/net.minecraft.world.item.ItemStackLinkedSet.method_47573(Lnet/minecraft/net.minecraft.world.item.ItemStack;)I */
    public static int method_47573(net.minecraft.world.item.ItemStackLinkedSet self, net.minecraft.world.item.ItemStack a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.item.ItemStackLinkedSet.method_47573(ItemStack) was removed in 1.20.5 with no direct replacement; the remaining API (createTypeAndComponentsSet returning Set) cannot query a specific item's cooldown value");
    }

    /** was net/minecraft/net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder$class_7900$1.field_41004Lnet/minecraft/net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder$class_7900; */
    public static net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder.Instance field_41004() {
        throw new UnsupportedOperationException("CenturyBridge: field_41004 was the synthetic this$0 enclosing instance reference in net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder$class_7900$1; it is an instance field holding the enclosing net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder$class_7900 and cannot be reconstructed as a static constant");
    }

    /** was net/minecraft/net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder$class_7900$3.field_41011Lnet/minecraft/net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder$class_7900; */
    public static net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder.Instance field_41011() {
        throw new UnsupportedOperationException("CenturyBridge: field_41011 was the synthetic this$0 reference to the enclosing class_7900 instance in anonymous class net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder$class_7900$3; it is per-instance and cannot be reconstructed as a static constant");
    }

    /** was net/minecraft/net.minecraft.client.gui.components.Tooltip.method_54383(I)V */
    public static void method_54383(net.minecraft.client.gui.components.Tooltip self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_54383(int) was removed in 1.20.5 with no direct replacement; the new net.minecraft.client.gui.components.Tooltip API has no int-parameterized instance method to delegate to");
    }

    /** was net/minecraft/net.minecraft.client.gui.components.Tooltip.method_54384(ZZLnet/minecraft/net.minecraft.client.gui.navigation.ScreenRectangle;)V */
    public static void method_54384(net.minecraft.client.gui.components.Tooltip self, boolean a0, boolean a1, net.minecraft.client.gui.navigation.ScreenRectangle a2) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.components.Tooltip.method_54384(ZZLnet/minecraft/net.minecraft.client.gui.navigation.ScreenRectangle;)V was deleted in 1.20.5 with no direct replacement; remaining net.minecraft.client.gui.components.Tooltip methods (b, toCharSequence-47408) accept incompatible parameter types and cannot express the (boolean, boolean, net.minecraft.client.gui.navigation.ScreenRectangle) contract");
    }

    /** was net/minecraft/net.minecraft.client.gui.components.Tooltip.method_54385(ZZLnet/minecraft/net.minecraft.client.gui.navigation.ScreenRectangle;)Lnet/minecraft/net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner; */
    public static net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner method_54385(net.minecraft.client.gui.components.Tooltip self, boolean a0, boolean a1, net.minecraft.client.gui.navigation.ScreenRectangle a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_54385 was removed in 1.20.5 with no direct replacement; net.minecraft.client.gui.components.Tooltip's remaining API has no method returning net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner or accepting net.minecraft.client.gui.navigation.ScreenRectangle");
    }

    /** was net/minecraft/net.minecraft.core.registries.BuiltInRegistries.field_41165Lnet/minecraft/net.minecraft.core.Registry; */
    public static net.minecraft.core.Registry field_41165() {
        throw new UnsupportedOperationException("CenturyBridge: Registries.field_41165 was removed in 1.20.5 with no direct replacement");
    }

    /** was net/minecraft/net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister.field_41399Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_41399() {
        return net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister.MAP_CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.client.renderer.texture.atlas.sources.SingleFile.field_41402Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_41402() {
        return net.minecraft.client.renderer.texture.atlas.sources.SingleFile.MAP_CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer.field_42841Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_42841() {
        return net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.feature.trunkplacers.CherryTrunkPlacer.field_42848Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_42848() {
        return net.minecraft.world.level.levelgen.feature.trunkplacers.CherryTrunkPlacer.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.ticks.ContainerSingleItem.getContainerBlockEntity()Lnet/minecraft/net.minecraft.world.level.block.entity.BlockEntity; */
    public net.minecraft.world.level.block.entity.BlockEntity getContainerBlockEntity() {
        return ((net.minecraft.world.ticks.ContainerSingleItem.BlockContainerSingleItem) (Object) this).getContainerBlockEntity();
    }


    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.AppendStatic.field_43339Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_43339() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.AppendStatic.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.Clear.field_43341Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_43341() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.Clear.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.Passthrough.field_43344Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_43344() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.Passthrough.CODEC.codec();
    }

    /** was net/minecraft/net.minecraft.world.level.gameevent.vibrations.VibrationSystem$class_5719.onReceiveVibration(Lnet/minecraft/net.minecraft.server.level.ServerLevel;Lnet/minecraft/net.minecraft.core.BlockPos;Lnet/minecraft/net.minecraft.world.level.gameevent.GameEvent;Lnet/minecraft/net.minecraft.world.entity.Entity;Lnet/minecraft/net.minecraft.world.entity.Entity;F)V */
    public void onReceiveVibration(net.minecraft.server.level.ServerLevel a0, net.minecraft.core.BlockPos a1, net.minecraft.world.level.gameevent.GameEvent a2, net.minecraft.world.entity.Entity a3, net.minecraft.world.entity.Entity a4, float a5) {
        ((net.minecraft.world.level.gameevent.vibrations.VibrationSystem.User) (Object) this).onReceiveVibration(a0, a1, net.minecraft.core.Holder.direct(a2), a3, a4, a5);
    }

    /** was net/minecraft/net.minecraft.world.level.gameevent.vibrations.VibrationSystem.RESONANCE_EVENTS[Lnet/minecraft/net.minecraft.world.level.gameevent.GameEvent; */
    public static net.minecraft.world.level.gameevent.GameEvent[] field_44638() {
        return net.minecraft.world.level.gameevent.vibrations.VibrationSystem.RESONANCE_EVENTS.toArray(new net.minecraft.world.level.gameevent.GameEvent[0]);
    }

    /** was net/minecraft/net.minecraft.client.renderer.debug.ChunkDebugRenderer$class_4605.field_20999Lnet/minecraft/net.minecraft.client.renderer.debug.ChunkDebugRenderer; */
    public static net.minecraft.client.renderer.debug.ChunkDebugRenderer field_20999() {
        return null;
    }

    /** was net/minecraft/net.minecraft.client.resources.metadata.gui.GuiSpriteScaling$class_8694.field_45655Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_45655() {
        return (com.mojang.serialization.Codec) net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.Tile.CODEC;
    }

    /** was net/minecraft/net.minecraft.network.ProtocolSwapHandler.method_53011(Lio/netty/util/Attribute;Lnet/minecraft/net.minecraft.network.protocol.Packet;)V */
    public static void method_53011(net.minecraft.network.ProtocolSwapHandler self, io.netty.util.Attribute a0, net.minecraft.network.protocol.Packet a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_53011(Attribute, Packet) was deleted; new API methods require ChannelHandlerContext which cannot be derived from an io.netty.util.Attribute");
    }

    /** was net/minecraft/net.minecraft.network.protocol.common.custom.CustomPacketPayload.method_53028(Lnet/minecraft/net.minecraft.network.FriendlyByteBuf;)V */
    public static void method_53028(net.minecraft.network.protocol.common.custom.CustomPacketPayload self, net.minecraft.network.FriendlyByteBuf a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.network.protocol.common.custom.CustomPacketPayload.method_53028(PacketByteBuf) was deleted in 1.20.5 with no direct replacement; the new net.minecraft.network.protocol.common.custom.CustomPacketPayload API exposes no PacketByteBuf serialization path");
    }

    /** was net/minecraft/net.minecraft.world.RandomizableContainer.getLootTable()Lnet/minecraft/net.minecraft.resources.Identifier; */
    public net.minecraft.resources.Identifier getLootTable() {
        return ((net.minecraft.world.RandomizableContainer) (Object) this).getLootTable().identifier();
    }

    /** was net/minecraft/net.minecraft.world.entity.monster.breeze.Breeze.method_55006()Lnet/minecraft/net.minecraft.world.entity.monster.breeze.Breeze; */
    public static net.minecraft.world.entity.monster.breeze.Breeze method_55006(net.minecraft.world.entity.monster.breeze.Breeze self) {
        return self.resetJumpTrail();
    }

    /** was net/minecraft/net.minecraft.world.entity.monster.breeze.Breeze.method_55011(Lnet/minecraft/net.minecraft.world.phys.Vec3;)Z */
    public static boolean method_55011(net.minecraft.world.entity.monster.breeze.Breeze self, net.minecraft.world.phys.Vec3 a0) {
        return self.withinInnerCircleRange(a0);
    }

    /** was net/minecraft/net.minecraft.world.entity.monster.breeze.Breeze.method_55012(Lnet/minecraft/net.minecraft.world.phys.Vec3;)Z */
    public static boolean method_55012(net.minecraft.world.entity.monster.breeze.Breeze self, net.minecraft.world.phys.Vec3 a0) {
        return self.withinInnerCircleRange(a0);
    }

    /** was net/minecraft/net.minecraft.world.level.block.entity.trialspawner.PlayerDetector.field_47356Lnet/minecraft/net.minecraft.world.level.block.entity.trialspawner.PlayerDetector; */
    public static net.minecraft.world.level.block.entity.trialspawner.PlayerDetector field_47356() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.block.entity.trialspawner.PlayerDetector.field_47356 was deleted in 1.20.5; remaining constants SHEEP/NO_CREATIVE_PLAYERS/INCLUDING_CREATIVE_PLAYERS are not equivalent");
    }


    /** was net/minecraft/net.minecraft.client.model.monster.breeze.BreezeModel.method_55234()Lnet/minecraft/net.minecraft.client.model.geom.builders.LayerDefinition; */
    public static net.minecraft.client.model.geom.builders.LayerDefinition method_55234(net.minecraft.client.model.monster.breeze.BreezeModel self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.model.monster.breeze.BreezeModel.method_55234() was deleted in 1.20.5 with no direct replacement; the only remaining method returning net.minecraft.client.model.geom.builders.LayerDefinition is createBodyLayer(int,int) which requires two int parameters not available from the old no-arg signature");
    }

    /** was net/minecraft/net.minecraft.client.model.monster.breeze.BreezeModel.method_55235()Lnet/minecraft/net.minecraft.client.model.geom.builders.LayerDefinition; */
    public static net.minecraft.client.model.geom.builders.LayerDefinition method_55235(net.minecraft.client.model.monster.breeze.BreezeModel self) {
        throw new UnsupportedOperationException("CenturyBridge: method_55235() returned net.minecraft.client.model.geom.builders.LayerDefinition with no arguments; the only new-API equivalent createBodyLayer(II) requires two int parameters whose values cannot be inferred from the old call site");
    }

    /** was net/minecraft/net.minecraft.world.level.pathfinder.NodeEvaluator.field_20622Lnet/minecraft/net.minecraft.world.level.PathNavigationRegion; */
    public static net.minecraft.world.level.PathNavigationRegion field_20622() {
        throw new UnsupportedOperationException("CenturyBridge: field_20622 (net.minecraft.world.level.PathNavigationRegion HitResult) was deleted from net.minecraft.world.level.pathfinder.NodeEvaluator with no direct replacement; it was a mutable instance field that cannot be reconstructed as a constant expression");
        // CB-CONFIDENCE: high -- the field was explicitly deleted with no replacement in the new API surface, and a HitResult is a mutable runtime object that cannot be expressed as a constant
    }

    /** was net/minecraft/net.minecraft.network.chat.numbers.NumberFormatTypes.method_55467(Lnet/minecraft/net.minecraft.network.FriendlyByteBuf;)Lnet/minecraft/net.minecraft.network.chat.numbers.NumberFormat; */
    public static net.minecraft.network.chat.numbers.NumberFormat method_55467(net.minecraft.network.chat.numbers.NumberFormatTypes self, net.minecraft.network.FriendlyByteBuf a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.network.chat.numbers.NumberFormatTypes.method_55467(PacketByteBuf)->net.minecraft.network.chat.numbers.NumberFormat was removed in 1.20.5 with no direct replacement; the new net.minecraft.network.chat.numbers.NumberFormatTypes API has no method accepting a PacketByteBuf, and the old instance-method semantics (using receiver state to drive deserialization) cannot be expressed via the static Codec/MapCodec fields");
    }

    /** was net/minecraft/net.minecraft.client.renderer.entity.VindicatorRenderer$1.field_4805Lnet/minecraft/net.minecraft.client.renderer.entity.VindicatorRenderer; */
    public static net.minecraft.client.renderer.entity.VindicatorRenderer field_4805() {
        throw new UnsupportedOperationException("CenturyBridge: field_4805 was the synthetic outer instance reference (this$0) of net.minecraft.client.renderer.entity.VindicatorRenderer$1; it is an instance field that cannot be reconstructed from a static context without an instance of the inner class");
    }




























    /** was net/minecraft/net.minecraft.world.damagesource.DamageSource.method_48790()Z */
    public static boolean method_48790(net.minecraft.world.damagesource.DamageSource self) {
        return self.getDirectEntity() == self.getEntity();
    }

    /** was net/minecraft/net.minecraft.world.entity.Entity.field_5972I */
    public static int field_5972() {
        return 1024;
    }

    /** was net/minecraft/net.minecraft.world.entity.Entity.field_5991Lnet/minecraft/net.minecraft.core.BlockPos; */
    public static net.minecraft.core.BlockPos field_5991() {
        return new net.minecraft.core.BlockPos(0, 0, 0);
    }

    /** was net/minecraft/net.minecraft.world.entity.Entity.method_20620(DDD)V */
    public static void method_20620(net.minecraft.world.entity.Entity self, double a0, double a1, double a2) {
        self.setDeltaMovement(a0, a1, a2);
    }

    /** was net/minecraft/net.minecraft.world.entity.Entity.method_30329(Lnet/minecraft/net.minecraft.server.level.ServerLevel;)Lnet/minecraft/net.minecraft.world.level.portal.TeleportTransition; */
    public static net.minecraft.world.level.portal.TeleportTransition method_30329(net.minecraft.world.entity.Entity self, net.minecraft.server.level.ServerLevel a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_30329 (getTeleportTarget(ServerWorld)) was removed in 1.21; TeleportTarget (net.minecraft.world.level.portal.TeleportTransition) was reworked to Entity.TeleportTarget (net.minecraft.world.entity.Entity$class_5799) via getMovementEmission with different semantics and no ServerWorld parameter");
    }

    /** was net/minecraft/net.minecraft.world.entity.Entity.setKnownMovement(ZLnet/minecraft/net.minecraft.world.phys.Vec3;)V */
    public static void method_52208(net.minecraft.world.entity.Entity self, boolean a0, net.minecraft.world.phys.Vec3 a1) {
        self.setDeltaMovement(a1);
        // Can't set velocityModified flag in new API
    }

    /** was net/minecraft/net.minecraft.world.entity.Entity.method_5741()I */
    public static int method_5741(net.minecraft.world.entity.Entity self) {
        return self.getRemainingFireTicks();
    }

    /** was net/minecraft/net.minecraft.world.entity.EquipmentSlot.method_20234(Lnet/minecraft/net.minecraft.world.entity.EquipmentSlot$class_1305;I)Lnet/minecraft/net.minecraft.world.entity.EquipmentSlot; */
    public static net.minecraft.world.entity.EquipmentSlot method_20234(net.minecraft.world.entity.EquipmentSlot self, net.minecraft.world.entity.EquipmentSlot.Type a0, int a1) {
        throw new UnsupportedOperationException("CenturyBridge: DyeColor.method_20234(DyeColorCode, int) was removed in 1.21 with no direct replacement; no remaining method accepts a DyeColorCode parameter");
    }

    /** was net/minecraft/net.minecraft.world.entity.Mob.field_30086Ljava/lang/String; */
    public static java.lang.String field_30086() {
        return "Leash";
    }

    /** was net/minecraft/net.minecraft.world.entity.Mob.method_18810(I)V */
    public static void method_18810(net.minecraft.world.entity.Mob self, int a0) {
        self.setYya((float) a0);
    }


    /** was net/minecraft/net.minecraft.world.entity.LivingEntity.method_27302()Z */
    public static boolean method_27302(net.minecraft.world.entity.LivingEntity self) {
        return self.isSensitiveToWater();
    }


    /** was net/minecraft/net.minecraft.world.entity.animal.equine.AbstractChestedHorse.field_30412I */
    public static int field_30412() {
        return 32767;
    }

    /** was net/minecraft/net.minecraft.world.entity.decoration.HangingEntity.method_6891()I */
    public static int method_6891(net.minecraft.world.entity.decoration.HangingEntity self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.entity.decoration.HangingEntity.method_6891() (getLifeTime) was removed in 1.21; the lifeTime field is no longer exposed on FireworkRocketEntity and no equivalent accessor exists in the new API surface");
    }

    /** was net/minecraft/net.minecraft.SharedConstants.field_29743Z */
    public static boolean field_29743() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.SharedConstants.field_29743 was a boolean flag deleted in 1.21 with no direct replacement; its original value cannot be inferred from the new API surface");
        // CB-CONFIDENCE: high -- the field is explicitly listed as deleted with no replacement, and returning a guessed boolean constant would risk silent behaviour corruption
    }

    /** was net/minecraft/net.minecraft.SharedConstants.field_29744Z */
    public static boolean field_29744() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.SharedConstants.field_29744 was removed in 1.21 with no direct replacement");
        // CB-CONFIDENCE: high -- the field is explicitly listed as deleted with no equivalent in the new API surface
    }

    /** was net/minecraft/net.minecraft.SharedConstants.field_43095Ljava/util/Set; */
    public static java.util.Set field_43095() {
        throw new UnsupportedOperationException("CenturyBridge: field_43095 was an instance Set<String> of enabled resource packs loaded from options.txt; removed in 1.21 with no static equivalent, use getCurrentVersion() on a GameOptions instance instead");
    }

    /** was net/minecraft/net.minecraft.SharedConstants.method_43250()V */
    public static void method_43250(net.minecraft.SharedConstants self) {
        throw new UnsupportedOperationException("CenturyBridge: method_43250 was deleted in 1.21 with no direct replacement");
    }

    /** was net/minecraft/net.minecraft.world.entity.player.Player.field_49991Z */
    public static boolean field_49991() {
        return true;
        // CB-CONFIDENCE: low -- deleted static boolean constant with no direct replacement; value inferred as true from common feature-flag patterns but unverified
    }

    /** was net/minecraft/net.minecraft.world.entity.player.Player.method_26091(Lnet/minecraft/net.minecraft.server.level.ServerLevel;Lnet/minecraft/net.minecraft.core.BlockPos;FZZ)Ljava/util/Optional; */
    public static java.util.Optional method_26091(net.minecraft.world.entity.player.Player self, net.minecraft.server.level.ServerLevel a0, net.minecraft.core.BlockPos a1, float a2, boolean a3, boolean a4) {
        throw new UnsupportedOperationException("CenturyBridge: ServerPlayerEntity.trySleep(ServerWorld, BlockPos, float, boolean, boolean) (method_26091) was removed in 1.21; sleep handling was reworked and no direct equivalent exists on net.minecraft.world.entity.player.Player");
    }



    /** was net/minecraft/net.minecraft.world.item.enchantment.EnchantmentHelper.method_42304(Lnet/minecraft/net.minecraft.world.entity.LivingEntity;)F */
    public static float method_42304(net.minecraft.world.item.enchantment.EnchantmentHelper self, net.minecraft.world.entity.LivingEntity a0) {
        return self.getTridentSpinAttackStrength(a0.getMainHandItem(), a0);
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.EnchantmentHelper.method_8202(Lnet/minecraft/net.minecraft.world.item.ItemStack;)I */
    public static int method_8202(net.minecraft.world.item.enchantment.EnchantmentHelper self, net.minecraft.world.item.ItemStack a0) {
        return 1;
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.EnchantmentHelper.method_8217(Lnet/minecraft/net.minecraft.world.entity.LivingEntity;)F */
    public static float method_8217(net.minecraft.world.item.enchantment.EnchantmentHelper self, net.minecraft.world.entity.LivingEntity a0) {
        return self.getTridentSpinAttackStrength(a0.getMainHandItem(), a0);
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.EnchantmentHelper.method_8218(Lnet/minecraft/net.minecraft.world.item.ItemStack;Lnet/minecraft/net.minecraft.world.entity.EntityType;)F */
    public static float method_8218(net.minecraft.world.item.enchantment.EnchantmentHelper self, net.minecraft.world.item.ItemStack a0, net.minecraft.world.entity.EntityType a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_8218(ItemStack, EntityType<?>) was replaced by getTridentSpinAttackStrength(ItemStack, LivingEntity) which requires an entity instance, not an EntityType; cannot bridge without a LivingEntity");
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.EnchantmentHelper.method_8221(Lnet/minecraft/net.minecraft.world.item.ItemStack;)Z */
    public static boolean method_8221(net.minecraft.world.item.enchantment.EnchantmentHelper self, net.minecraft.world.item.ItemStack a0) {
        return self.canStoreEnchantments(a0);
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.Enchantments.field_23071Lnet/minecraft/net.minecraft.world.item.enchantment.Enchantment; */
    public static net.minecraft.world.item.enchantment.Enchantment field_23071() {
        throw new UnsupportedOperationException("CenturyBridge: SOUL_SPEED changed from Enchantment (net.minecraft.world.item.enchantment.Enchantment) to RegistryKey<Enchantment> (net.minecraft.resources.ResourceKey); resolving a registry key to an Enchantment instance requires runtime registry access unavailable in a static field context");
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.Enchantments.field_50158Lnet/minecraft/net.minecraft.world.item.enchantment.Enchantment; */
    public static net.minecraft.world.item.enchantment.Enchantment field_50158() {
        throw new UnsupportedOperationException("CenturyBridge: BREACH is now a RegistryKey<Enchantment> (net.minecraft.resources.ResourceKey); the Enchantment instance (net.minecraft.world.item.enchantment.Enchantment) is data-driven and only resolvable via the dynamic registry manager at runtime, not as a static constant");
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.Enchantments.field_9095Lnet/minecraft/net.minecraft.world.item.enchantment.Enchantment; */
    public static net.minecraft.world.item.enchantment.Enchantment field_9095() {
        throw new UnsupportedOperationException("CenturyBridge: FIRE_PROTECTION changed from Enchantment (net.minecraft.world.item.enchantment.Enchantment) to RegistryKey<Enchantment> (net.minecraft.resources.ResourceKey); enchantment instances require runtime registry lookup and are no longer available as static constants");
        // CB-CONFIDENCE: high -- the old field type (net.minecraft.world.item.enchantment.Enchantment Enchantment) is a runtime instance, the new field type (net.minecraft.resources.ResourceKey RegistryKey) is only a lookup key; no static conversion exists without registry access
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.Enchantments.field_9096Lnet/minecraft/net.minecraft.world.item.enchantment.Enchantment; */
    public static net.minecraft.world.item.enchantment.Enchantment field_9096() {
        throw new UnsupportedOperationException("CenturyBridge: PROJECTILE_PROTECTION is now a RegistryKey (net.minecraft.resources.ResourceKey), not a Registry (net.minecraft.world.item.enchantment.Enchantment); cannot reconstruct Registry from RegistryKey without runtime context");
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.Enchantments.field_9097Lnet/minecraft/net.minecraft.world.item.enchantment.Enchantment; */
    public static net.minecraft.world.item.enchantment.Enchantment field_9097() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.item.enchantment.Enchantment (RecipeType) instances are no longer available as static fields; they require registry lookup via net.minecraft.resources.ResourceKey (RegistryKey)");
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.Enchantments.field_9099Lnet/minecraft/net.minecraft.world.item.enchantment.Enchantment; */
    public static net.minecraft.world.item.enchantment.Enchantment field_9099() {
        throw new UnsupportedOperationException("CenturyBridge: SILK_TOUCH changed from Registry (net.minecraft.world.item.enchantment.Enchantment) to RegistryKey (net.minecraft.resources.ResourceKey); a static RegistryKey cannot yield a live Registry without a WrapperLookup");
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.Enchantments.field_9101Lnet/minecraft/net.minecraft.world.item.enchantment.Enchantment; */
    public static net.minecraft.world.item.enchantment.Enchantment field_9101() {
        throw new UnsupportedOperationException("CenturyBridge: Enchantments.MENDING changed from Enchantment (net.minecraft.world.item.enchantment.Enchantment) to RegistryKey (net.minecraft.resources.ResourceKey) in 1.21; cannot resolve a RegistryKey to an Enchantment instance without dynamic registry access");
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.Enchantments.field_9104Lnet/minecraft/net.minecraft.world.item.enchantment.Enchantment; */
    public static net.minecraft.world.item.enchantment.Enchantment field_9104() {
        throw new UnsupportedOperationException("CenturyBridge: Enchantments.RIPTIDE is now a RegistryKey<Enchantment>; resolving to Enchantment requires a dynamic registry lookup not available in a static context");
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.Enchantments.field_9106Lnet/minecraft/net.minecraft.world.item.enchantment.Enchantment; */
    public static net.minecraft.world.item.enchantment.Enchantment field_9106() {
        throw new UnsupportedOperationException("CenturyBridge: Enchantments.IMPALING is now a RegistryKey<Enchantment> (net.minecraft.resources.ResourceKey); the Enchantment instance (net.minecraft.world.item.enchantment.Enchantment) is no longer a static constant and must be resolved from the registry at runtime");
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.Enchantments.field_9113Lnet/minecraft/net.minecraft.world.item.enchantment.Enchantment; */
    public static net.minecraft.world.item.enchantment.Enchantment field_9113() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.item.enchantment.Enchantments.BINDING_CURSE changed from Enchantment (net.minecraft.world.item.enchantment.Enchantment) to RegistryKey (net.minecraft.resources.ResourceKey); enchantment instances are no longer static constants and must be resolved from the registry at runtime");
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.Enchantments.field_9115Lnet/minecraft/net.minecraft.world.item.enchantment.Enchantment; */
    public static net.minecraft.world.item.enchantment.Enchantment field_9115() {
        throw new UnsupportedOperationException("CenturyBridge: SWEEPING_EDGE changed from Enchantment (net.minecraft.world.item.enchantment.Enchantment) to RegistryKey<Enchantment> (net.minecraft.resources.ResourceKey); Enchantment instances are no longer available as static constants and require registry lookup at runtime");
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.Enchantments.field_9116Lnet/minecraft/net.minecraft.world.item.enchantment.Enchantment; */
    public static net.minecraft.world.item.enchantment.Enchantment field_9116() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.item.enchantment.Enchantments.PUNCH is now a RegistryKey<Enchantment> (net.minecraft.resources.ResourceKey); the Enchantment object (net.minecraft.world.item.enchantment.Enchantment) is only resolvable through the enchantment registry at runtime, not as a constant field");
        // CB-CONFIDENCE: high -- the field type changed from Enchantment to RegistryKey<Enchantment>, and no constant conversion exists without registry access
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.Enchantments.field_9123Lnet/minecraft/net.minecraft.world.item.enchantment.Enchantment; */
    public static net.minecraft.world.item.enchantment.Enchantment field_9123() {
        throw new UnsupportedOperationException("CenturyBridge: Registries.SMITE is now a RegistryKey (net.minecraft.resources.ResourceKey) instead of a Registry (net.minecraft.world.item.enchantment.Enchantment); cannot obtain a Registry from a RegistryKey without a RegistryAccess");
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.Enchantments.field_9124Lnet/minecraft/net.minecraft.world.item.enchantment.Enchantment; */
    public static net.minecraft.world.item.enchantment.Enchantment field_9124() {
        throw new UnsupportedOperationException("CenturyBridge: FIRE_ASPECT changed from Enchantment (net.minecraft.world.item.enchantment.Enchantment) to RegistryKey<Enchantment> (net.minecraft.resources.ResourceKey); enchantment instances are no longer available as static constants, use the registry to resolve at runtime");
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.Enchantments.field_9126Lnet/minecraft/net.minecraft.world.item.enchantment.Enchantment; */
    public static net.minecraft.world.item.enchantment.Enchantment field_9126() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.item.enchantment.Enchantments.FLAME is now a RegistryKey (net.minecraft.resources.ResourceKey), not an Enchantment (net.minecraft.world.item.enchantment.Enchantment); enchantments are data-driven and no static Enchantment instance can be returned");
    }

    /** was net/minecraft/net.minecraft.world.item.enchantment.Enchantments.field_9132Lnet/minecraft/net.minecraft.world.item.enchantment.Enchantment; */
    public static net.minecraft.world.item.enchantment.Enchantment field_9132() {
        throw new UnsupportedOperationException("CenturyBridge: PIERCING is now a RegistryKey (net.minecraft.resources.ResourceKey), not an Enchantment instance (net.minecraft.world.item.enchantment.Enchantment); resolving it requires dynamic registry access unavailable statically");
        // CB-CONFIDENCE: high -- the new field is only a registry key and the provided net.minecraft.world.item.enchantment.Enchantments API exposes no way to obtain a net.minecraft.world.item.enchantment.Enchantment instance
    }

    /** was net/minecraft/net.minecraft.client.renderer.rendertype.RenderType.method_23012(Lnet/minecraft/com.mojang.blaze3d.vertex.BufferBuilder;Lnet/minecraft/com.mojang.blaze3d.vertex.VertexSorting;)V */
    public static void method_23012(net.minecraft.client.renderer.rendertype.RenderType self, com.mojang.blaze3d.vertex.BufferBuilder a0, com.mojang.blaze3d.vertex.VertexSorting a1) {
        throw new UnsupportedOperationException("CenturyBridge: RenderLayer.draw(BufferBuilder, VertexConsumerProvider.Immediate) was removed in 1.21's rendering pipeline overhaul; net.minecraft.client.renderer.rendertype.RenderType no longer owns buffer-drawing functionality");
    }

    /** was net/minecraft/net.minecraft.client.renderer.rendertype.RenderType.method_24296()Ljava/util/Optional; */
    public static java.util.Optional method_24296(net.minecraft.client.renderer.rendertype.RenderType self) {
        return self.outline();
    }

    /** was net/minecraft/net.minecraft.world.level.portal.PortalShape.method_30484(Lnet/minecraft/net.minecraft.server.level.ServerLevel;Lnet/minecraft/net.minecraft.util.BlockUtil$class_5460;Lnet/minecraft/net.minecraft.core.Direction$class_2351;Lnet/minecraft/net.minecraft.world.phys.Vec3;Lnet/minecraft/net.minecraft.world.entity.Entity;Lnet/minecraft/net.minecraft.world.phys.Vec3;FF)Lnet/minecraft/net.minecraft.world.level.portal.TeleportTransition; */
    public static net.minecraft.world.level.portal.TeleportTransition method_30484(net.minecraft.world.level.portal.PortalShape self, net.minecraft.server.level.ServerLevel a0, net.minecraft.util.BlockUtil.FoundRectangle a1, net.minecraft.core.Direction.Axis a2, net.minecraft.world.phys.Vec3 a3, net.minecraft.world.entity.Entity a4, net.minecraft.world.phys.Vec3 a5, float a6, float a7) {
        throw new UnsupportedOperationException("CenturyBridge: method_30484 created a TeleportTarget (net.minecraft.world.level.portal.TeleportTransition) which was removed in 1.21; the portal teleportation system was reworked to use net.minecraft.world.entity.EntityDimensions (PostTeleportTarget) and net.minecraft.world.level.portal.PortalShape no longer creates teleport targets");
    }

    /** was net/minecraft/net.minecraft.network.PacketListener.onDisconnect(Lnet/minecraft/net.minecraft.network.chat.Component;)V */
    public void onDisconnect(net.minecraft.network.chat.Component a0) {
        ((net.minecraft.network.PacketListener) (Object) this).onDisconnect(new net.minecraft.network.DisconnectionDetails(a0));
    }

    /** was net/minecraft/net.minecraft.network.PacketListener.fillListenerSpecificCrashDetails(Lnet/minecraft/net.minecraft.CrashReportCategory;)V */
    public void fillListenerSpecificCrashDetails(net.minecraft.CrashReportCategory a0) {
        // adds system details
    }

    /** was net/minecraft/net.minecraft.world.level.block.entity.JukeboxBlockEntity.method_44373()Z */
    public static boolean method_44373(net.minecraft.world.level.block.entity.JukeboxBlockEntity self) {
        return self.getSongPlayer() != null;
    }

    /** was net/minecraft/net.minecraft.world.level.block.entity.JukeboxBlockEntity.method_49212()V */
    public static void method_49212(net.minecraft.world.level.block.entity.JukeboxBlockEntity self) {
        self.popOutTheItem();
    }

    /** was net/minecraft/net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity.method_11409(Lnet/minecraft/net.minecraft.world.level.Level;Lnet/minecraft/net.minecraft.core.BlockPos;Lnet/minecraft/net.minecraft.world.level.block.state.BlockState;Lnet/minecraft/net.minecraft.world.entity.Entity;Lnet/minecraft/net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;)V */
    public static void method_11409(net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity self, net.minecraft.world.level.Level a0, net.minecraft.core.BlockPos a1, net.minecraft.world.level.block.state.BlockState a2, net.minecraft.world.entity.Entity a3, net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity a4) {
        throw new UnsupportedOperationException("CenturyBridge: method_11409 accepted an Entity (net.minecraft.world.entity.Entity) parameter with no equivalent in the new net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity API; all replacement candidates (triggerCooldown/beamAnimationTick/portalTick) omit the Entity argument");
    }









    /** was net/minecraft/net.minecraft.client.Minecraft.method_1488()F */
    public static float method_1488(net.minecraft.client.Minecraft self) {
        return (float) self.getGpuUtilization();
    }

    /** was net/minecraft/net.minecraft.server.level.ServerLevel.method_18211(Lnet/minecraft/net.minecraft.server.level.ServerPlayer;)V */
    public static void method_18211(net.minecraft.server.level.ServerLevel self, net.minecraft.server.level.ServerPlayer a0) {
        self.addRespawnedPlayer(a0);
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_14606Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14606() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.TRIDENT_RIPTIDE_1.value();
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_14626Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14626() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.CROSSBOW_LOADING_END.value();
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_14717Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14717() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.TRIDENT_RIPTIDE_3.value();
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_14765Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14765() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.CROSSBOW_LOADING_START.value();
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_14860Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14860() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.CROSSBOW_LOADING_MIDDLE.value();
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_14896Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14896() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.TRIDENT_THUNDER.value();
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_14916Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14916() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.CROSSBOW_QUICK_CHARGE_2.value();
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_15001Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_15001() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.TRIDENT_THROW.value();
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_15011Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_15011() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.CROSSBOW_QUICK_CHARGE_1.value();
    }

    /** was net/minecraft/net.minecraft.sounds.SoundEvents.field_15089Lnet/minecraft/net.minecraft.sounds.SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_15089() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.CROSSBOW_QUICK_CHARGE_3.value();
    }


    /** was net/minecraft/net.minecraft.tags.ItemTags.field_15541Lnet/minecraft/net.minecraft.tags.TagKey; */
    public static net.minecraft.tags.TagKey field_15541() {
        throw new UnsupportedOperationException("CenturyBridge: field_15541 was removed in 1.21 with no direct replacement; the sound event it referenced no longer exists in the registry");
        // CB-CONFIDENCE: high -- the field is explicitly marked deleted with no direct replacement, and without knowing which sound event it represented there is no faithful equivalent to return
    }

    /** was net/minecraft/net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings.method_15120()Z */
    public static boolean method_15120(net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings self) {
        return !self.getProcessors().isEmpty();
    }



    /** was net/minecraft/com.mojang.blaze3d.vertex.VertexConsumer.method_22901(IIII)V */
    public static void method_22901(com.mojang.blaze3d.vertex.VertexConsumer self, int a0, int a1, int a2, int a3) {
        self.setColor(a0, a1, a2, a3);
    }

    /** was net/minecraft/com.mojang.blaze3d.vertex.VertexConsumer.addVertex(DDD)Lnet/minecraft/com.mojang.blaze3d.vertex.VertexConsumer; */
    public com.mojang.blaze3d.vertex.VertexConsumer addVertex(double a0, double a1, double a2) {
        return ((com.mojang.blaze3d.vertex.VertexConsumer) (Object) this).addVertex((float) a0, (float) a1, (float) a2);
    }

    /** was net/minecraft/com.mojang.blaze3d.vertex.VertexConsumer.method_22916(I)Lnet/minecraft/com.mojang.blaze3d.vertex.VertexConsumer; */
    public static com.mojang.blaze3d.vertex.VertexConsumer method_22916(com.mojang.blaze3d.vertex.VertexConsumer self, int a0) {
        return self.setColor(a0);
    }

    /** was net/minecraft/com.mojang.blaze3d.vertex.VertexConsumer.method_22917(II)Lnet/minecraft/com.mojang.blaze3d.vertex.VertexConsumer; */
    public static com.mojang.blaze3d.vertex.VertexConsumer method_22917(com.mojang.blaze3d.vertex.VertexConsumer self, int a0, int a1) {
        return self.setUv1(a0, a1);
    }

    /** was net/minecraft/com.mojang.blaze3d.vertex.VertexConsumer.method_23763(Lnet/minecraft/com.mojang.blaze3d.vertex.PoseStack$class_4665;FFF)Lnet/minecraft/com.mojang.blaze3d.vertex.VertexConsumer; */
    public static com.mojang.blaze3d.vertex.VertexConsumer method_23763(com.mojang.blaze3d.vertex.VertexConsumer self, com.mojang.blaze3d.vertex.PoseStack.Pose a0, float a1, float a2, float a3) {
        return self.addVertex(a0, a1, a2, a3);
    }

    /** was net/minecraft/com.mojang.realmsclient.dto.RealmsServer.method_25059(Lnet/minecraft/com.mojang.realmsclient.dto.RealmsServerPlayerLists;)V */
    public static void method_25059(com.mojang.realmsclient.dto.RealmsServer self, com.mojang.realmsclient.dto.RealmsServerPlayerLists a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_25059(com.mojang.realmsclient.dto.RealmsServerPlayerLists) was removed in 1.21 with no direct replacement; no field or method on com.mojang.realmsclient.dto.RealmsServer accepts com.mojang.realmsclient.dto.RealmsServerPlayerLists");
    }

    /** was net/minecraft/com.mojang.realmsclient.dto.RealmsServerPlayerLists.field_22627Ljava/util/List; */
    public static java.util.List field_22627() {
        throw new UnsupportedOperationException("CenturyBridge: field_22627 was a static constant List; new API only exposes getProfileResultsFor(J) which requires a long parameter, so the old constant contract cannot be expressed");
    }

    /** was net/minecraft/net.minecraft.world.entity.ai.attributes.AttributeMap.method_26841()Ljava/util/Set; */
    public static java.util.Set method_26841(net.minecraft.world.entity.ai.attributes.AttributeMap self) {
        return self.getAttributesToSync();
    }

    /** was net/minecraft/class_5146.method_6576(Lnet/minecraft/net.minecraft.sounds.SoundSource;)V */
    public void method_6576(net.minecraft.sounds.SoundSource a0) {
        // play this.method_45328() with the given category
            // the ItemStack is used for something else (like pitch/volume modification)
    }

    /** was net/minecraft/net.minecraft.client.gui.screens.options.OnlineOptionsScreen.method_47621(Lnet/minecraft/net.minecraft.client.Minecraft;Lnet/minecraft/net.minecraft.client.gui.screens.Screen;Lnet/minecraft/net.minecraft.client.Options;)Lnet/minecraft/net.minecraft.client.gui.screens.options.OnlineOptionsScreen; */
    public static net.minecraft.client.gui.screens.options.OnlineOptionsScreen method_47621(net.minecraft.client.gui.screens.options.OnlineOptionsScreen self, net.minecraft.client.Minecraft a0, net.minecraft.client.gui.screens.Screen a1, net.minecraft.client.Options a2) {
        return new net.minecraft.client.gui.screens.options.OnlineOptionsScreen(a1, a2);
        // CB-CONFIDENCE: medium -- the new constructor takes exactly the Screen and GameOptions args the old factory accepted; the MinecraftClient arg has no equivalent in the new API and is dropped, which is the most faithful bridge available.
    }

    /** was net/minecraft/net.minecraft.world.flag.FeatureFlags.field_46779Lnet/minecraft/net.minecraft.world.flag.FeatureFlag; */
    public static net.minecraft.world.flag.FeatureFlag field_46779() {
        throw new UnsupportedOperationException("CenturyBridge: field_46779 (net.minecraft.world.flag.FeatureFlag) was removed in 1.21 with no direct replacement; remaining net.minecraft.world.flag.FeatureFlag fields (VANILLA, field_40178, TRADE_REBALANCE) have unknown semantic mapping to the deleted field");
    }


    /** was net/minecraft/net.minecraft.util.CommonLinks.field_43117Ljava/lang/String; */
    public static java.lang.String field_43117() {
        return net.minecraft.util.CommonLinks.GDPR.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_43118Ljava/lang/String; */
    public static java.lang.String field_43118() {
        return net.minecraft.util.CommonLinks.EULA.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_43119Ljava/lang/String; */
    public static java.lang.String field_43119() {
        return net.minecraft.util.CommonLinks.ATTRIBUTION.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_43120Ljava/lang/String; */
    public static java.lang.String field_43120() {
        return net.minecraft.util.CommonLinks.LICENSES.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_43121Ljava/lang/String; */
    public static java.lang.String field_43121() {
        return net.minecraft.util.CommonLinks.BUY_MINECRAFT_JAVA.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_43122Ljava/lang/String; */
    public static java.lang.String field_43122() {
        return net.minecraft.util.CommonLinks.ACCOUNT_SETTINGS.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_43123Ljava/lang/String; */
    public static java.lang.String field_43123() {
        return net.minecraft.util.CommonLinks.SNAPSHOT_FEEDBACK.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_43124Ljava/lang/String; */
    public static java.lang.String field_43124() {
        return net.minecraft.util.CommonLinks.RELEASE_FEEDBACK.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_43125Ljava/lang/String; */
    public static java.lang.String field_43125() {
        return net.minecraft.util.CommonLinks.SNAPSHOT_BUGS_FEEDBACK.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_43126Ljava/lang/String; */
    public static java.lang.String field_43126() {
        return net.minecraft.util.CommonLinks.ACCESSIBILITY_HELP.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_43127Ljava/lang/String; */
    public static java.lang.String field_43127() {
        return net.minecraft.util.CommonLinks.REPORTING_HELP.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_43128Ljava/lang/String; */
    public static java.lang.String field_43128() {
        return net.minecraft.util.CommonLinks.SUSPENSION_HELP.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_43129Ljava/lang/String; */
    public static java.lang.String field_43129() {
        return net.minecraft.util.CommonLinks.BLOCKING_HELP.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_43130Ljava/lang/String; */
    public static java.lang.String field_43130() {
        return net.minecraft.util.CommonLinks.START_REALMS_TRIAL.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_43131Ljava/lang/String; */
    public static java.lang.String field_43131() {
        return net.minecraft.util.CommonLinks.BUY_REALMS.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_43132Ljava/lang/String; */
    public static java.lang.String field_43132() {
        return net.minecraft.util.CommonLinks.REALMS_TERMS.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_43133Ljava/lang/String; */
    public static java.lang.String field_43133() {
        return net.minecraft.util.CommonLinks.REALMS_CONTENT_CREATION.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_44949Ljava/lang/String; */
    public static java.lang.String field_44949() {
        return net.minecraft.util.CommonLinks.SYMLINK_HELP.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_45074Ljava/lang/String; */
    public static java.lang.String field_45074() {
        return net.minecraft.util.CommonLinks.PRIVACY_STATEMENT.toString();
    }

    /** was net/minecraft/net.minecraft.util.CommonLinks.field_46769Ljava/lang/String; */
    public static java.lang.String field_46769() {
        return net.minecraft.util.CommonLinks.GENERAL_HELP.toString();
    }

    /** was net/minecraft/net.minecraft.world.item.crafting.ShapedRecipePattern.comp_2081()I */
    public static int comp_2081(net.minecraft.world.item.crafting.ShapedRecipePattern self) {
        return self.height();
    }






    // ==== END GENERATED ====


    /**
     * DFU 7 changed Codec.dispatch's type function from returning Codec to
     * returning MapCodec -- same erased descriptor, different contract, so the
     * verifier never objects and the cast blows up at decode time deep inside
     * a mod's own registry. The adapter accepts whatever the old function
     * returns and normalises it.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static com.mojang.serialization.MapCodec cbAsMap(Object c) {
        if (c instanceof com.mojang.serialization.MapCodec m) {
            return m;
        }
        if (c instanceof com.mojang.serialization.MapCodec.MapCodecCodec mc) {
            return mc.codec();
        }
        return com.mojang.serialization.MapCodec.assumeMapUnsafe(
            (com.mojang.serialization.Codec) c);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static com.mojang.serialization.Codec dispatch(
            com.mojang.serialization.Codec self,
            java.util.function.Function keyGetter, java.util.function.Function type) {
        return self.dispatch(keyGetter, k -> cbAsMap(type.apply(k)));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static com.mojang.serialization.Codec dispatch(
            com.mojang.serialization.Codec self, String typeKey,
            java.util.function.Function keyGetter, java.util.function.Function type) {
        return self.dispatch(typeKey, keyGetter, k -> cbAsMap(type.apply(k)));
    }


    /**
     * 1.20.5+ codecs resolve registry entries (items, enchantments) through the
     * ops, not through static lookups baked into the codec -- plain JsonOps
     * makes every id "invalid". Registry-aware ops built from the builtin root;
     * lazy because the registries must be populated first, which they are by
     * the time any mod entrypoint runs.
     */
    private static com.mojang.serialization.DynamicOps<JsonElement> cbOps;

    private static com.mojang.serialization.DynamicOps<JsonElement> cbJsonOps() {
        if (cbOps == null) {
            cbOps = net.minecraft.resources.RegistryOps.create(JsonOps.INSTANCE,
                net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY));
        }
        return cbOps;
    }


    /**
     * 1.20.1 ItemStack.hasCustomName(): the component wall made the name a
     * CUSTOM_NAME data component, so "has a name" is "carries the component".
     * (First attempt guessed setCustomName from the method NAME -- the 1.20.1
     * stub says ()Z. Semantics come from descriptors, not from naming vibes.)
     */
    public static boolean method_7938(net.minecraft.world.item.ItemStack self) {
        return self.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME);
    }


    /**
     * Builtin registry lookup for component-wall call sites that suddenly need
     * one (BlockEntity NBT family). The world's own manager would be the exact
     * choice; the builtin wrapper resolves everything vanilla serialization
     * reaches for in these paths.
     */
    private static net.minecraft.core.HolderLookup.Provider cbLookupCache;

    public static net.minecraft.core.HolderLookup.Provider cbLookup() {
        // cached like cbOps above: this backs every rewritten writeNbt/readNbt
        // call site, and the wrapper is over the builtin (static) registries,
        // which never reload -- rebuilding the whole wrapper graph per chunk
        // save was pure waste
        if (cbLookupCache == null) {
            cbLookupCache = net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(net.minecraft.core.registries.BuiltInRegistries.REGISTRY);
        }
        return cbLookupCache;
    }

    private Statics() {
    }
}
