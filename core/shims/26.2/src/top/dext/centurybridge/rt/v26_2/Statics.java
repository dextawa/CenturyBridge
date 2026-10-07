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

    // INVOKESTATIC resolves by exact descriptor and the converter emits the call site's OWNER as the
    // receiver type: the subclass-owner redirects (class_1811 / class_1747 / class_1826) need their own overloads.
    public static void method_7843(net.minecraft.world.item.ProjectileWeaponItem self,
            net.minecraft.world.item.ItemStack stack, net.minecraft.world.level.Level world,
            net.minecraft.world.entity.player.Player player) {
        method_7843((net.minecraft.world.item.Item) self, stack, world, player);
    }

    public static void method_7843(net.minecraft.world.item.BlockItem self,
            net.minecraft.world.item.ItemStack stack, net.minecraft.world.level.Level world,
            net.minecraft.world.entity.player.Player player) {
        method_7843((net.minecraft.world.item.Item) self, stack, world, player);
    }

    public static void method_7843(net.minecraft.world.item.SpawnEggItem self,
            net.minecraft.world.item.ItemStack stack, net.minecraft.world.level.Level world,
            net.minecraft.world.entity.player.Player player) {
        method_7843((net.minecraft.world.item.Item) self, stack, world, player);
    }

    /** class_1732 (RecipeUnlocker) is the INTERFACE owner of setLastRecipe: the emitted receiver is RecipeCraftingHolder, not ResultContainer */
    public static void method_7662(net.minecraft.world.inventory.RecipeCraftingHolder self, net.minecraft.world.item.crafting.Recipe<?> recipe) {
        if (recipe == null) {
            self.setRecipeUsed(null);
            return;
        }
        net.minecraft.resources.Identifier id = Trackers.RECIPE_IDS.get(recipe);
        self.setRecipeUsed(id == null ? null : new net.minecraft.world.item.crafting.RecipeHolder<>(cbRecipeKey(id), recipe));
    }

    /** class_1732.shouldCraftRecipe(world, player, recipe) through the interface owner (see method_7665(ResultContainer, ...)) */
    public static boolean method_7665(net.minecraft.world.inventory.RecipeCraftingHolder self,
            net.minecraft.world.level.Level world, net.minecraft.server.level.ServerPlayer player,
            net.minecraft.world.item.crafting.Recipe<?> recipe) {
        net.minecraft.resources.Identifier id = Trackers.RECIPE_IDS.get(recipe);
        if (id == null) {
            return false;
        }
        return self.setRecipeUsed(player, new net.minecraft.world.item.crafting.RecipeHolder<>(cbRecipeKey(id), recipe));
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
    /** was net/minecraft/client/resources/language/LanguageManager/field_32971Ljava/lang/String; */
    public static java.lang.String field_32971() {
        return "textures/atlas/";
    }

    /** was net/minecraft/world/level/pathfinder/Path/method_22880()[Lnet/minecraft/world/level/pathfinder/Node; */
    public static net.minecraft.world.level.pathfinder.Node[] method_22880(net.minecraft.world.level.pathfinder.Path self) {
        int length = self.getNodeCount();
        net.minecraft.world.level.pathfinder.Node[] directions = new net.minecraft.world.level.pathfinder.Node[length];
        for (int i = 0; i < length; i++) {
            directions[i] = self.getNode(i);
        }
        return directions;
    }

    /** was net/minecraft/world/level/pathfinder/Path/method_22881()[Lnet/minecraft/world/level/pathfinder/Node; */
    public static net.minecraft.world.level.pathfinder.Node[] method_22881(net.minecraft.world.level.pathfinder.Path self) {
        int length = self.getNextNodeIndex();
        net.minecraft.world.level.pathfinder.Node[] nodes = new net.minecraft.world.level.pathfinder.Node[length];
        for (int i = 0; i < length; i++) {
            nodes[i] = self.getNode(i);
        }
        return nodes;
    }

    /** was net/minecraft/util/datafix/fixes/References/field_5732Lcom/mojang/datafixers/DSL$TypeReference; */
    public static com.mojang.datafixers.DSL.TypeReference field_5732() {
        throw new UnsupportedOperationException("CenturyBridge: field_5732 was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/util/datafix/fixes/StatsCounterFix/upgradeItem(Ljava/lang/String;)Ljava/lang/String; */
    public static java.lang.String method_5172(net.minecraft.util.datafix.fixes.StatsCounterFix self, java.lang.String a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.util.datafix.fixes.StatsCounterFix.upgradeItem(String) was deleted in 1.20.2; the new API surface only exposes <init> and makeRule(), with no String-to-String transformation available to delegate to");
    }

    /** was net/minecraft/util/datafix/fixes/StatsCounterFix/upgradeBlock(Ljava/lang/String;)Ljava/lang/String; */
    public static java.lang.String method_5173(net.minecraft.util.datafix.fixes.StatsCounterFix self, java.lang.String a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.util.datafix.fixes.StatsCounterFix.upgradeBlock(String) was removed in 1.20.2; the rename callback is no longer exposed and is inlined into makeRule(), so the old per-name transform contract cannot be reproduced on the new type");
    }


    /** was net/minecraft/world/effect/MobEffectInstance/method_5589(Lnet/minecraft/world/entity/LivingEntity;)V */
    public static void method_5589(net.minecraft.world.effect.MobEffectInstance self, net.minecraft.world.entity.LivingEntity a0) {
        self.onEffectStarted(a0);
    }

    /** was net/minecraft/world/entity/Entity/method_20233(Lnet/minecraft/world/entity/Pose;)Z */
    public static boolean method_20233(net.minecraft.world.entity.Entity self, net.minecraft.world.entity.Pose a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_20233(EntityPose) was removed in 1.20.2 with no direct replacement");
    }


    /** was net/minecraft/world/entity/Entity/method_5621()D */
    public static double method_5621(net.minecraft.world.entity.Entity self) {
        return (double) self.getBbHeight();
    }

    /** was net/minecraft/world/entity/Mob/method_33191(Lnet/minecraft/world/entity/LivingEntity;)D */
    public static double method_33191(net.minecraft.world.entity.Mob self, net.minecraft.world.entity.LivingEntity a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_33191 (MobEntity.getAttackDistanceScalingFactor) was removed in 1.20.2 with no direct replacement; override-based virtual dispatch cannot be replicated in a static shim");
    }

    /** was net/minecraft/world/entity/Mob/method_47922(Lnet/minecraft/world/entity/LivingEntity;)D */
    public static double method_47922(net.minecraft.world.entity.Mob self, net.minecraft.world.entity.LivingEntity a0) {
        return (double) (self.getBbWidth() * 2.0F * self.getBbWidth() * 2.0F + a0.getBbWidth());
    }

    /** was net/minecraft/world/entity/LivingEntity/field_6242D */
    public static double field_6242() {
        return 1.3D;
    }

    /** was net/minecraft/world/level/storage/loot/functions/LootItemFunctions/method_29322()Ljava/lang/Object; */
    public static java.lang.Object method_29322(net.minecraft.world.level.storage.loot.functions.LootItemFunctions self) {
        throw new UnsupportedOperationException("CenturyBridge: Gson type handler for loot functions was removed in 1.20.2");
    }

    /** was net/minecraft/world/entity/ai/attributes/AttributeInstance/removeModifier(Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V */
    public static void method_6202(net.minecraft.world.entity.ai.attributes.AttributeInstance self, net.minecraft.world.entity.ai.attributes.AttributeModifier a0) {
        self.addTransientModifier(a0);
    }

    /** was net/minecraft/world/entity/ambient/Bat/field_28637I */
    public static int field_28637() {
        throw new UnsupportedOperationException("CenturyBridge: Field field_28637 was deleted without replacement");
    }

    /** was net/minecraft/world/entity/ambient/Bat/field_30268F */
    public static float field_30268() {
        return 0.2f;
    }

    /** was net/minecraft/advancements/AdvancementTree$Listener.onAddAdvancementTask(Lnet/minecraft/advancements/Advancement;)V */
    public void onAddAdvancementTask(net.minecraft.advancements.Advancement a0) {
        ((net.minecraft.advancements.AdvancementTree.Listener) (Object) this).onAddAdvancementTask(new net.minecraft.advancements.AdvancementNode((net.minecraft.advancements.AdvancementHolder) (Object) a0, null));
    }

    /** was net/minecraft/advancements/AdvancementTree/method_712()Ljava/util/Collection; */
    public static java.util.Collection method_712(net.minecraft.advancements.AdvancementTree self) {
        return self.nodes();
    }

    /** was net/minecraft/world/entity/player/Player/method_7282(DDD)V */
    public static void method_7282(net.minecraft.world.entity.player.Player self, double a0, double a1, double a2) {
        throw new UnsupportedOperationException("CenturyBridge: PlayerEntity.increaseTravelStats (method_7282) was removed in 1.20.2 and has no direct replacement.");
    }

    /** was net/minecraft/world/entity/vehicle/boat/Boat/method_42279(Lnet/minecraft/world/damagesource/DamageSource;)V */
    public static void method_42279(net.minecraft.world.entity.vehicle.boat.Boat self, net.minecraft.world.damagesource.DamageSource a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_42279(DamageSource) was removed in 1.20.3; projectile damage source is now tracked via net.minecraft.world.entity.vehicle.boat.Boat$class_1692 (method_47884/method_47885) with no known conversion from DamageSource");
    }

    /** was net/minecraft/world/entity/vehicle/boat/Boat/method_7533()I */
    public static int method_7533(net.minecraft.world.entity.vehicle.boat.Boat self) {
        throw new UnsupportedOperationException("CenturyBridge: TntEntity.getFuse is no longer publicly accessible");
    }

    /** was net/minecraft/world/entity/vehicle/boat/Boat/method_7540(I)V */
    public static void method_7540(net.minecraft.world.entity.vehicle.boat.Boat self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_7540 is no longer supported");
    }

    /** was net/minecraft/world/entity/vehicle/boat/Boat/method_7543()I */
    public static int method_7543(net.minecraft.world.entity.vehicle.boat.Boat self) {
        throw new UnsupportedOperationException("CenturyBridge: BoatEntity.getTicksUnderwater is no longer exposed");
    }


    /** was net/minecraft/world/entity/vehicle/boat/Boat/method_7554()F */
    public static float method_7554(net.minecraft.world.entity.vehicle.boat.Boat self) {
        throw new UnsupportedOperationException("CenturyBridge: BoatEntity.getWaterHeight was removed in 1.20.3");
    }

    /** was net/minecraft/world/inventory/RecipeCraftingHolder/setRecipeUsed(Lnet/minecraft/world/item/crafting/Recipe;)V */
    public void setRecipeUsed(net.minecraft.world.item.crafting.Recipe a0) {
        net.minecraft.world.item.crafting.RecipeHolder current = ((net.minecraft.world.inventory.RecipeCraftingHolder)(Object)this).getRecipeUsed();
        if (current == null) {
            throw new UnsupportedOperationException("CenturyBridge: cannot derive RecipeEntry id from null current recipe");
        }
        ((net.minecraft.world.inventory.RecipeCraftingHolder)(Object)this).setRecipeUsed(new net.minecraft.world.item.crafting.RecipeHolder(current.id(), a0));
    }

    /** was net/minecraft/world/inventory/RecipeCraftingHolder/getRecipeUsed()Lnet/minecraft/world/item/crafting/Recipe; */
    public net.minecraft.world.item.crafting.Recipe getRecipeUsed() {
        return ((net.minecraft.world.inventory.RecipeCraftingHolder) (Object) this).getRecipeUsed().value();
    }

    /** was net/minecraft/world/level/storage/loot/parameters/LootContextParamSets/method_757(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/util/context/ContextKeySet; */
    public static net.minecraft.util.context.ContextKeySet method_757(net.minecraft.world.level.storage.loot.parameters.LootContextParamSets self, net.minecraft.resources.Identifier a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.method_757(net.minecraft.resources.Identifier) performed dynamic tag lookup by Identifier; 1.20.2 reworked the tag system and removed this capability with no direct replacement");
    }

    /** was net/minecraft/world/level/storage/loot/parameters/LootContextParamSets/method_762(Lnet/minecraft/util/context/ContextKeySet;)Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_762(net.minecraft.world.level.storage.loot.parameters.LootContextParamSets self, net.minecraft.util.context.ContextKeySet a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_762 (Slot -> Identifier) was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/world/item/equipment/ArmorMaterials/field_41941Lnet/minecraft/util/StringRepresentable$EnumCodec; */
    public static net.minecraft.util.StringRepresentable.EnumCodec field_41941() {
        throw new UnsupportedOperationException("CenturyBridge: StringIdentifiable.Codec has been removed in 1.20.3");
    }



    /** was net/minecraft/advancements/CriterionProgress/method_783()Lcom/google/gson/JsonElement; */
    public static com.google.gson.JsonElement method_783(net.minecraft.advancements.CriterionProgress self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.advancements.CriterionProgress.method_783() (JsonElement serialization) was deleted in 1.20.2; the new API surface only exposes getObtained() returning Instant and serializeToNetwork() for PacketByteBuf serialization, with no equivalent JsonElement contract");
    }

    /** was net/minecraft/advancements/CriterionProgress/method_788(Ljava/lang/String;)Lnet/minecraft/advancements/CriterionProgress; */
    public static net.minecraft.advancements.CriterionProgress method_788(net.minecraft.advancements.CriterionProgress self, java.lang.String a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.advancements.CriterionProgress.method_788(String) has no 1.20.2 equivalent; the String parameter is no longer part of net.minecraft.advancements.CriterionProgress's contract (only <init>() or <init>(Instant) exist)");
    }


    /** was net/minecraft/class_1830.field_30925Ljava/lang/String; */
    public static java.lang.String field_30925() {
        return "Items";
    }

    /** was net/minecraft/advancements/CriterionTriggerInstance/method_806()Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_806(net.minecraft.advancements.CriterionTriggerInstance self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.advancements.CriterionTriggerInstance no longer stores an Identifier; the advancement ID was moved to AdvancementEntry (net.minecraft.advancements.AdvancementNode) in 1.20.2, so method_806 has no equivalent on net.minecraft.advancements.CriterionTriggerInstance itself");
    }


    /** was net/minecraft/advancements/CriterionTriggerInstance/method_807()Lcom/google/gson/JsonObject; */
    public static com.google.gson.JsonObject method_807(net.minecraft.advancements.CriterionTriggerInstance self) {
        throw new UnsupportedOperationException("CenturyBridge: Advancement JSON serialization is not supported on this version");
    }

    /** 1.20.1 toJson(AdvancementEntityPredicateSerializer): the wired shape carries the class_5267 facade argument */
    public static com.google.gson.JsonObject method_807(net.minecraft.advancements.CriterionTriggerInstance self,
            top.dext.centurybridge.facade.v26_2.class_5267 serializer) {
        return method_807(self);
    }


    /** was net/minecraft/advancements/AdvancementType/method_832()I */
    public static int method_832(net.minecraft.advancements.AdvancementType self) {
        return self.ordinal();
    }

    /** was net/minecraft/client/renderer/rendertype/RenderType/field_32773I */
    public static int field_32773() {
        return 131072;
    }


    /** was net/minecraft/advancements/criterion/ConsumeItemTrigger/field_9509Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_9509() {
        return net.minecraft.resources.Identifier.parse("enchanted_item");
    }

    /** was net/minecraft/advancements/criterion/FilledBucketTrigger/field_9612Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_9612() {
        return net.minecraft.resources.Identifier.parse("enchantment_used_on_item");
    }

    /** was net/minecraft/advancements/criterion/ImpossibleTrigger/field_9624Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_9624() {
        return net.minecraft.resources.Identifier.parse("recipe_unlocked");
    }

    /** was net/minecraft/advancements/criterion/InventoryChangeTrigger/field_9625Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_9625() {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "brewed_potion");
    }



    /** was net/minecraft/world/level/block/Blocks/field_10150Lnet/minecraft/world/level/block/Block; */
    public static net.minecraft.world.level.block.Block field_10150() {
        throw new UnsupportedOperationException("CenturyBridge: Blocks.field_10150 was deleted in 1.20.3 with no direct replacement");
    }

    /** was net/minecraft/world/level/block/Blocks/field_10331Lnet/minecraft/world/level/block/Block; */
    public static net.minecraft.world.level.block.Block field_10331() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.block.Blocks.field_10331 was removed in 1.20.3 with no direct replacement");
    }

    /** was net/minecraft/core/Registry/method_10231(Lnet/minecraft/core/Registry;ILjava/lang/String;Ljava/lang/Object;)Ljava/lang/Object; */
    public static java.lang.Object method_10231(net.minecraft.core.Registry self, net.minecraft.core.Registry a0, int a1, java.lang.String a2, java.lang.Object a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_10231 (Registry.register with raw id) has no 1.20.2 equivalent; raw-id registration is no longer supported");
    }





    /** was net/minecraft/network/FriendlyByteBuf/method_10815(I)I */
    public static int method_10815(net.minecraft.network.FriendlyByteBuf self, int a0) {
        return self.readVarInt();
    }

    /** was net/minecraft/network/FriendlyByteBuf/method_36129(J)I */
    public static int method_36129(net.minecraft.network.FriendlyByteBuf self, long a0) {
        int before = self.writerIndex();
        self.writeVarLong(a0);
        return self.writerIndex() - before;
    }

    /** was net/minecraft/network/protocol/game/ClientGamePacketListener/method_11083(Lnet/minecraft/network/protocol/common/ClientboundDisconnectPacket;)V */
    public static void method_11083(net.minecraft.network.protocol.game.ClientGamePacketListener self, net.minecraft.network.protocol.common.ClientboundDisconnectPacket a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.network.protocol.game.ClientGamePacketListener.method_11083(net.minecraft.network.protocol.common.ClientboundDisconnectPacket) was removed in 1.20.2 with no direct replacement accepting net.minecraft.network.protocol.common.ClientboundDisconnectPacket");
    }

    /** was net/minecraft/network/protocol/game/ClientGamePacketListener/handleUpdateTags(Lnet/minecraft/network/protocol/common/ClientboundUpdateTagsPacket;)V */
    public void handleUpdateTags(net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket a0) {
        ((net.minecraft.network.protocol.common.ClientCommonPacketListener) (Object) this).handleUpdateTags(a0);
    }

    /** was net/minecraft/network/protocol/game/ClientGamePacketListener/handleCustomPayload(Lnet/minecraft/network/protocol/common/ClientboundCustomPayloadPacket;)V */
    public static void method_11152(net.minecraft.network.protocol.game.ClientGamePacketListener self, net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.network.protocol.game.ClientGamePacketListener.handleCustomPayload(net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket) was removed in 1.20.2 with no direct replacement; no remaining method on net.minecraft.network.protocol.game.ClientGamePacketListener accepts net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket");
    }

    /** was net/minecraft/network/protocol/game/ClientGamePacketListener/method_36895(Lnet/minecraft/network/protocol/common/ClientboundPingPacket;)V */
    public static void method_36895(net.minecraft.network.protocol.game.ClientGamePacketListener self, net.minecraft.network.protocol.common.ClientboundPingPacket a0) {
        a0.handle(self);
    }

    /** was net/minecraft/network/protocol/game/ClientGamePacketListener/method_45728(Lnet/minecraft/network/protocol/configuration/ClientboundUpdateEnabledFeaturesPacket;)V */
    public static void method_45728(net.minecraft.network.protocol.game.ClientGamePacketListener self, net.minecraft.network.protocol.configuration.ClientboundUpdateEnabledFeaturesPacket a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.network.protocol.game.ClientGamePacketListener.method_45728(net.minecraft.network.protocol.configuration.ClientboundUpdateEnabledFeaturesPacket) was deleted in 1.20.2 with no direct replacement; net.minecraft.network.protocol.configuration.ClientboundUpdateEnabledFeaturesPacket has no single successor on the new visitor surface");
    }

    /** was net/minecraft/world/level/block/entity/RandomizableContainerBlockEntity/field_31352Ljava/lang/String; */
    public static java.lang.String field_31352() {
        return "Fuel";
    }

    /** was net/minecraft/world/level/block/entity/RandomizableContainerBlockEntity/field_31353Ljava/lang/String; */
    public static java.lang.String field_31353() {
        throw new UnsupportedOperationException("CenturyBridge: field_31353 (String constant on net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity/LecternBlockEntity) was deleted in 1.20.3 with no replacement and its literal value cannot be reconstructed from the new API surface");
    }

    /** was net/minecraft/world/level/block/entity/SignBlockEntity/method_49844(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/level/block/entity/SignText; */
    public static net.minecraft.world.level.block.entity.SignText method_49844(net.minecraft.world.level.block.entity.SignBlockEntity self, net.minecraft.world.entity.player.Player a0) {
        return self.getText(self.isFacingFrontText(a0));
    }

    /** was net/minecraft/world/level/block/entity/StructureBlockEntity/method_11368(Lnet/minecraft/server/level/ServerLevel;Z)Z */
    public static boolean method_11368(net.minecraft.world.level.block.entity.StructureBlockEntity self, net.minecraft.server.level.ServerLevel a0, boolean a1) {
        return self.placeStructureIfSameSize(a0);
    }

    /** was net/minecraft/world/level/block/entity/StructureBlockEntity/method_21864(Lnet/minecraft/server/level/ServerLevel;ZLnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;)Z */
    public static boolean method_21864(net.minecraft.world.level.block.entity.StructureBlockEntity self, net.minecraft.server.level.ServerLevel a0, boolean a1, net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate a2) {
        throw new UnsupportedOperationException("CenturyBridge: StructureBlockBlockEntity no longer supports placing a custom StructureTemplate");
    }

    /** was net/minecraft/world/level/block/entity/StructureBlockEntity/method_21865()Ljava/lang/String; */
    public static java.lang.String method_21865(net.minecraft.world.level.block.entity.StructureBlockEntity self) {
        return self.AUTHOR_TAG;
    }


    /** was net/minecraft/network/protocol/game/ServerGamePacketListener/handleClientInformation(Lnet/minecraft/network/protocol/common/ServerboundClientInformationPacket;)V */
    public void handleClientInformation(net.minecraft.network.protocol.common.ServerboundClientInformationPacket a0) {
        ((net.minecraft.network.protocol.common.ServerCommonPacketListener) (Object) this).handleClientInformation(a0);
    }

    /** was net/minecraft/network/protocol/game/ServerGamePacketListener/method_12075(Lnet/minecraft/network/protocol/common/ServerboundCustomPayloadPacket;)V */
    public static void method_12075(net.minecraft.network.protocol.game.ServerGamePacketListener self, net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_12075 (onDifficulty) was removed in 1.20.2; difficulty is now embedded in GameJoinS2CPacket/PlayerRespawnS2CPacket rather than sent as a standalone packet, so there is no equivalent handler to delegate to");
    }

    /** was net/minecraft/network/protocol/game/ServerGamePacketListener/method_12081(Lnet/minecraft/network/protocol/common/ServerboundResourcePackPacket;)V */
    public static void method_12081(net.minecraft.network.protocol.game.ServerGamePacketListener self, net.minecraft.network.protocol.common.ServerboundResourcePackPacket a0) {
        throw new UnsupportedOperationException("CenturyBridge: PlayPongC2SPacket (net.minecraft.network.protocol.common.ServerboundResourcePackPacket) has been deleted");
    }

    /** was net/minecraft/network/protocol/game/ServerGamePacketListener/method_12082(Lnet/minecraft/network/protocol/common/ServerboundKeepAlivePacket;)V */
    public static void method_12082(net.minecraft.network.protocol.game.ServerGamePacketListener self, net.minecraft.network.protocol.common.ServerboundKeepAlivePacket a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_12082 is no longer supported on net.minecraft.network.protocol.game.ServerGamePacketListener");
    }

    /** was net/minecraft/network/protocol/game/ServerGamePacketListener/method_36580(Lnet/minecraft/network/protocol/common/ServerboundPongPacket;)V */
    public static void method_36580(net.minecraft.network.protocol.game.ServerGamePacketListener self, net.minecraft.network.protocol.common.ServerboundPongPacket a0) {
        throw new UnsupportedOperationException("CenturyBridge: JigsawGeneratingC2SPacket (net.minecraft.network.protocol.common.ServerboundPongPacket) handler was removed in 1.20.2");
    }

    /** was net/minecraft/network/protocol/status/ClientStatusPacketListener/handlePongResponse(Lnet/minecraft/network/protocol/ping/ClientboundPongResponsePacket;)V */
    public void handlePongResponse(net.minecraft.network.protocol.ping.ClientboundPongResponsePacket a0) {
        ((net.minecraft.network.protocol.ping.ClientPongPacketListener) (Object) this).handlePongResponse(a0);
    }

    /** was net/minecraft/network/protocol/status/ServerStatusPacketListener/handlePingRequest(Lnet/minecraft/network/protocol/ping/ServerboundPingRequestPacket;)V */
    public void handlePingRequest(net.minecraft.network.protocol.ping.ServerboundPingRequestPacket a0) {
        throw new UnsupportedOperationException("CenturyBridge: tasks are no longer managed by BuiltChunk");
    }

    /** was net/minecraft/class_2952.method_12816(IIILnet/minecraft/world/item/crafting/Recipe;Ljava/util/Iterator;I)V */
    public void method_12816(int a0, int a1, int a2, net.minecraft.world.item.crafting.Recipe a3, java.util.Iterator a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.item.crafting.RecipeHolder (RecipeEntry) requires a net.minecraft.resources.Identifier (Identifier) that is not available from the old net.minecraft.world.item.crafting.Recipe-only method_12816 signature; Recipe.getId() was removed in 1.20.2");
    }

    /** was net/minecraft/server/ServerInterface/getPlayerCount()I */
    public int getPlayerCount() {
        return ((net.minecraft.server.ServerInfo) (Object) this).getPlayerCount();
    }

    /** was net/minecraft/server/ServerInterface/getMaxPlayers()I */
    public int getMaxPlayers() {
        return ((net.minecraft.server.ServerInfo) (Object) this).getMaxPlayers();
    }

    /** was net/minecraft/server/ServerInterface/getServerVersion()Ljava/lang/String; */
    public java.lang.String getServerVersion() {
        throw new UnsupportedOperationException("CenturyBridge: getServerVersion moved to net.minecraft.server.ServerInfo");
    }

    /** was net/minecraft/server/ServerScoreboard/method_32704(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/scores/ScoreboardSaveData; */
    public static net.minecraft.world.scores.ScoreboardSaveData method_32704(net.minecraft.server.ServerScoreboard self, net.minecraft.nbt.CompoundTag a0) {
        throw new UnsupportedOperationException("CenturyBridge: Scoreboard.method_32704(Tag) was removed in 1.20.2; objective NBT deserialization was reworked with no direct equivalent");
    }

    /** was net/minecraft/server/ServerScoreboard/method_32705()Lnet/minecraft/world/scores/ScoreboardSaveData; */
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

    /** was net/minecraft/client/Options$4.field_1967[I */
    public static int[] field_1967() {
        throw new UnsupportedOperationException("CenturyBridge: class_316 does not exist in this version");
    }

    /** was net/minecraft/client/Options/field_1866Z */
    public static boolean field_1866() {
        return false;
    }

    /** was net/minecraft/client/Options/field_1880Z */
    public static boolean field_1880() {
        return true;
    }

    /** was net/minecraft/client/Options/field_1893Z */
    public static boolean field_1893() {
        throw new UnsupportedOperationException("CenturyBridge: field_1893 is no longer available");
    }

    /** was net/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess.getDataTag(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/world/level/WorldDataConfiguration;Lnet/minecraft/core/Registry;Lcom/mojang/serialization/Lifecycle;)Lcom/mojang/datafixers/util/Pair; */
    public static com.mojang.datafixers.util.Pair method_27013(net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess self, com.mojang.serialization.DynamicOps a0, net.minecraft.world.level.WorldDataConfiguration a1, net.minecraft.core.Registry a2, com.mojang.serialization.Lifecycle a3) {
        throw new UnsupportedOperationException("CenturyBridge: LevelStorage.Session.getDataTag was removed in 1.20.3 and cannot be emulated");
    }

    /** was net/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess.method_29585()Lnet/minecraft/world/level/WorldDataConfiguration; */
    public static net.minecraft.world.level.WorldDataConfiguration method_29585(net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess self) {
        throw new UnsupportedOperationException("CenturyBridge: LevelStorage.Session.method_29585() returning net.minecraft.world.level.WorldDataConfiguration was removed in 1.20.3 with no direct replacement on net.minecraft.world.level.storage.LevelStorageSource$class_5143");
    }

    /** was net/minecraft/server/level/PlayerMap/method_14081(JJLnet/minecraft/server/level/ServerPlayer;)V */
    public static void method_14081(net.minecraft.server.level.PlayerMap self, long a0, long a1, net.minecraft.server.level.ServerPlayer a2) {
        self.unIgnorePlayer(a2);
    }

    /** was net/minecraft/server/network/ServerLoginPacketListenerImpl/field_14160Lcom/mojang/authlib/GameProfile; */
    public static com.mojang.authlib.GameProfile field_14160() {
        throw new UnsupportedOperationException("CenturyBridge: field_14160 was a per-connection instance GameProfile field; the 1.20.2 API only exposes startClientVerification (setter) and method_52418 (String->GameProfile factory), neither of which can reconstruct the value from a static no-arg context");
    }

    /** was net/minecraft/server/network/ServerLoginPacketListenerImpl/method_14375(Lcom/mojang/authlib/GameProfile;)Lcom/mojang/authlib/GameProfile; */
    public static com.mojang.authlib.GameProfile method_14375(net.minecraft.server.network.ServerLoginPacketListenerImpl self, com.mojang.authlib.GameProfile a0) {
        throw new UnsupportedOperationException("CenturyBridge: ServerLoginNetworkHandler.method_14375 was removed and has no public equivalent");
    }

    /** was net/minecraft/server/network/ServerLoginPacketListenerImpl/method_14384()V */
    public static void method_14384(net.minecraft.server.network.ServerLoginPacketListenerImpl self) {
        throw new UnsupportedOperationException("CenturyBridge: method_14384 (acceptPlayer) cannot be implemented because the authenticated GameProfile is not accessible");
    }

    /** was net/minecraft/server/network/ServerLoginPacketListenerImpl/method_40085(Ljava/lang/String;)Z */
    public static boolean method_40085(net.minecraft.server.network.ServerLoginPacketListenerImpl self, java.lang.String a0) {
        return a0.chars().filter(c -> c <= 32 || c >= 127).findFirst().isEmpty();
    }

    /** was net/minecraft/world/level/storage/LevelStorageSource/method_17926(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/world/level/WorldDataConfiguration;Lnet/minecraft/core/Registry;Lcom/mojang/serialization/Lifecycle;)Ljava/util/function/BiFunction; */
    public static java.util.function.BiFunction method_17926(net.minecraft.world.level.storage.LevelStorageSource self, com.mojang.serialization.DynamicOps a0, net.minecraft.world.level.WorldDataConfiguration a1, net.minecraft.core.Registry a2, com.mojang.serialization.Lifecycle a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_17926 was removed in 1.20.3 and cannot be faithfully emulated.");
    }

    /** was net/minecraft/world/level/storage/LevelStorageSource/method_26998(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelDirectory;Ljava/util/function/BiFunction;)Ljava/lang/Object; */
    public static java.lang.Object method_26998(net.minecraft.world.level.storage.LevelStorageSource self, net.minecraft.world.level.storage.LevelStorageSource.LevelDirectory a0, java.util.function.BiFunction a1) {
        throw new UnsupportedOperationException("CenturyBridge: LevelStorage.readLevelProperties is removed in 1.20.3");
    }

    /** was net/minecraft/world/level/storage/LevelStorageSource/method_29014(Lnet/minecraft/world/level/storage/LevelStorageSource$LevelDirectory;Z)Ljava/util/function/BiFunction; */
    public static java.util.function.BiFunction method_29014(net.minecraft.world.level.storage.LevelStorageSource self, net.minecraft.world.level.storage.LevelStorageSource.LevelDirectory a0, boolean a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_29014 not supported due to API changes in DataConfiguration parsing");
    }





    /** was net/minecraft/client/gui/components/AbstractWidget/field_22757Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_22757() {
        return net.minecraft.resources.Identifier.parse("textures/gui/widgets.png");
    }

    /** was net/minecraft/client/gui/components/AbstractWidget/field_42117Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_42117() {
        return net.minecraft.resources.Identifier.parse("textures/gui/widgets.png");
    }

    /** was net/minecraft/world/level/levelgen/structure/structures/WoodlandMansionPieces/method_35471([Ljava/lang/String;)V */
    public static void method_35471(net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces self, java.lang.String[] a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionPieces.method_35471(String[]) was removed in 1.20.2 with no direct replacement; the new API only exposes structure placement via generateMansion");
    }

    /** was net/minecraft/world/level/storage/LevelSummary/method_256()Z */
    public static boolean method_256(net.minecraft.world.level.storage.LevelSummary self) {
        return false;
    }

    /** was net/minecraft/world/level/storage/LevelSummary/method_260()Z */
    public static boolean method_260(net.minecraft.world.level.storage.LevelSummary self) {
        net.minecraft.world.level.LevelSettings levelInfo = self.getSettings();
        return levelInfo != null && levelInfo.allowCommands();
    }

    /** was net/minecraft/util/HttpUtil/field_15664Lcom/google/common/util/concurrent/ListeningExecutorService; */
    public static com.google.common.util.concurrent.ListeningExecutorService field_15664() {
        throw new UnsupportedOperationException("CenturyBridge: field_15664 (ListeningExecutorService) has been removed");
    }

    /** was net/minecraft/util/HttpUtil/method_15301(Ljava/io/File;Ljava/net/URL;Ljava/util/Map;ILnet/minecraft/util/ProgressListener;Ljava/net/Proxy;)Ljava/util/concurrent/CompletableFuture; */
    public static java.util.concurrent.CompletableFuture method_15301(net.minecraft.util.HttpUtil self, java.io.File a0, java.net.URL a1, java.util.Map a2, int a3, net.minecraft.util.ProgressListener a4, java.net.Proxy a5) {
        throw new UnsupportedOperationException("CenturyBridge: method_15301 was removed and cannot be implemented without Guava classes and the new progress listener interface.");
    }

    /** was net/minecraft/util/StringRepresentable$EnumCodec.method_42631(Ljava/lang/Enum;Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult; */
    public static com.mojang.serialization.DataResult method_42631(net.minecraft.util.StringRepresentable.EnumCodec self, java.lang.Enum a0, com.mojang.serialization.DynamicOps a1, java.lang.Object a2) {
        return self.encode(a0, a1, a2);
    }

    /** was net/minecraft/client/gui/components/PlayerTabOverlay/field_32206I */
    public static int field_32206() {
        return 20;
    }

    /** was net/minecraft/client/gui/components/PlayerTabOverlay/field_32207I */
    public static int field_32207() {
        return 80;
    }

    /** was net/minecraft/client/gui/components/PlayerTabOverlay/field_32208I */
    public static int field_32208() {
        return 80;
    }

    /** was net/minecraft/client/gui/components/PlayerTabOverlay/field_32209I */
    public static int field_32209() {
        return 256;
    }

    /** was net/minecraft/client/gui/components/PlayerTabOverlay/field_32210I */
    public static int field_32210() {
        return 80;
    }

    /** was net/minecraft/client/gui/components/PlayerTabOverlay/field_32211I */
    public static int field_32211() {
        return 80;
    }

    /** was net/minecraft/client/gui/components/PlayerTabOverlay/field_32212I */
    public static int field_32212() {
        return 80;
    }

    /** was net/minecraft/client/gui/components/PlayerTabOverlay/field_32213I */
    public static int field_32213() {
        return 80;
    }

    /** was net/minecraft/client/gui/components/AbstractSliderButton/field_43051I */
    public static int field_43051() {
        return 8;
    }

    /** was net/minecraft/client/gui/components/AbstractSliderButton/field_43052I */
    public static int field_43052() {
        return 46;
    }

    /** was net/minecraft/client/gui/components/AbstractSliderButton/field_43102I */
    public static int field_43102() {
        return 14737632;
    }

    /** was net/minecraft/client/gui/components/AbstractSliderButton/field_43103I */
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

    /** was net/minecraft/class_361.field_2193Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_2193() {
        return net.minecraft.resources.Identifier.parse("textures/gui/recipe_book.png");
    }

    /** was net/minecraft/client/gui/components/events/GuiEventListener/mouseScrolled(DDD)Z */
    public boolean mouseScrolled(double a0, double a1, double a2) {
        return ((net.minecraft.client.gui.components.events.GuiEventListener) (Object) this).mouseScrolled(a0, a1, 0.0, a2);
    }

    /** was net/minecraft/client/gui/components/spectator/SpectatorGui/field_2199Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_2199() {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "textures/gui/options_background.png");
    }

    /** was net/minecraft/client/gui/components/toasts/Toast/field_2207Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_2207() {
        return net.minecraft.resources.Identifier.parse("textures/gui/toasts.png");
    }

    /** was net/minecraft/world/level/levelgen/structure/pools/JigsawPlacement$PieceState.field_18696Lnet/minecraft/world/level/levelgen/structure/PoolElementStructurePiece; */
    public static net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece field_18696() {
        throw new UnsupportedOperationException("CenturyBridge: Static field field_18696 was deleted without replacement");
    }

    /** was net/minecraft/world/level/levelgen/structure/pools/JigsawPlacement$PieceState.field_18697Lorg/apache/commons/lang3/mutable/MutableObject; */
    public static org.apache.commons.lang3.mutable.MutableObject field_18697() {
        throw new UnsupportedOperationException("CenturyBridge: Instance field field_18697 accessed statically without receiver");
    }

    /** was net/minecraft/world/level/levelgen/structure/pools/JigsawPlacement$PieceState.field_18699I */
    public static int field_18699() {
        throw new UnsupportedOperationException("CenturyBridge: field_18699 was deleted with no replacement");
    }

    /** was net/minecraft/world/level/levelgen/structure/pools/JigsawPlacement$Placer.field_18706Ljava/util/Deque; */
    public static java.util.Deque field_18706() {
        throw new UnsupportedOperationException("CenturyBridge: placing was changed to net.minecraft.util.SequencedPriorityIterator and cannot be represented as Deque");
    }

    /** was net/minecraft/server/level/ChunkMap/field_18243I */
    public static int field_18243() {
        return 34;
    }

    /** was net/minecraft/server/level/ChunkMap/updateChunkTracking(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/level/ChunkPos;Lorg/apache/commons/lang3/mutable/MutableObject;ZZ)V */
    public static void method_17241(net.minecraft.server.level.ChunkMap self, net.minecraft.server.level.ServerPlayer a0, net.minecraft.world.level.ChunkPos a1, org.apache.commons.lang3.mutable.MutableObject a2, boolean a3, boolean a4) {
        throw new UnsupportedOperationException("CenturyBridge: updateChunkTracking (sendChunk) was removed in 1.20.2 due to chunk sending flow control overhaul");
    }

    /** was net/minecraft/server/level/ChunkMap/method_39975(IIIII)Z */
    public static boolean method_39975(net.minecraft.server.level.ChunkMap self, int a0, int a1, int a2, int a3, int a4) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.server.level.ChunkMap.method_39975(IIIII)Z was removed in 1.20.2; the chunk tick/spawning system was reworked and the five-int-parameter tick method has no direct equivalent");
    }

    /** was net/minecraft/client/gui/screens/CreateFlatWorldScreen$DetailsList.field_44667Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_44667() {
        return net.minecraft.resources.Identifier.parse("textures/gui/language_button.png");
    }

    /** was net/minecraft/client/gui/components/AbstractButton/field_43046I */
    public static int field_43046() {
        return 16777215;
    }

    /** was net/minecraft/client/gui/components/AbstractButton/field_43047I */
    public static int field_43047() {
        return 4;
    }

    /** was net/minecraft/client/gui/components/AbstractButton/field_43048I */
    public static int field_43048() {
        return 2;
    }

    /** was net/minecraft/client/gui/components/AbstractButton/field_43100I */
    public static int field_43100() {
        return 6;
    }

    /** was net/minecraft/client/gui/components/AbstractButton/field_43101I */
    public static int field_43101() {
        return 16777215;
    }

    /** was net/minecraft/util/datafix/DataFixTypes/field_19219Lnet/minecraft/util/datafix/DataFixTypes; */
    public static net.minecraft.util.datafix.DataFixTypes field_19219() {
        throw new UnsupportedOperationException("CenturyBridge: field_19219 (POI) has been deleted and has no equivalent in 1.20.2");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen.field_22686Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_22686() {
        throw new UnsupportedOperationException("CenturyBridge: field_22686 (static Identifier on com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen) was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen.field_22687Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_22687() {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath("realms", "textures/gui/realms/slot_frame.png");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab.field_22704Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_22704() {
        return net.minecraft.resources.Identifier.parse("textures/gui/recipe_book.png");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab.field_22705Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_22705() {
        throw new UnsupportedOperationException("CenturyBridge: com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab.field_22705 (Identifier) was deleted in 1.20.2 with no direct replacement and its constant value cannot be recovered from the new API surface");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab.field_22706Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_22706() {
        return net.minecraft.resources.Identifier.parse("textures/gui/container/creative_inventory/creative_inventory.png");
    }


    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.field_20072I */
    public static int field_20072() {
        return 32;
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.field_20077Lnet/minecraft/network/chat/Component; */
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

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.field_22719Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_22719() {
        return net.minecraft.resources.Identifier.parse("textures/misc/unknown_server.png");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.field_22720Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_22720() {
        return net.minecraft.resources.Identifier.parse("textures/gui/container/command_block.png");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.field_26512Lnet/minecraft/network/chat/Component; */
    public static net.minecraft.network.chat.Component field_26512() {
        return net.minecraft.network.chat.Component.nullToEmpty("mco.trial.message");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.field_26513Lnet/minecraft/network/chat/Component; */
    public static net.minecraft.network.chat.Component field_26513() {
        return net.minecraft.network.chat.Component.nullToEmpty("mco.template.button.publisher");
    }

    /** was net/minecraft/gametest/framework/GameTestRunner/field_33148I */
    public static int field_33148() {
        return 5;
    }


    /** was net/minecraft/gametest/framework/StructureUtils/method_36106([Ljava/lang/String;)V */
    public static void method_36106(net.minecraft.gametest.framework.StructureUtils self, java.lang.String[] a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.gametest.framework.StructureUtils.method_36106(String[]) was removed in 1.20.2 with no direct replacement; the structure manager no longer accepts a pack/namespace array");
    }

    /** was net/minecraft/client/gui/screens/advancements/AdvancementsScreen/field_2716Lnet/minecraft/resources/Identifier; */
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

    /** was net/minecraft/client/gui/screens/inventory/SmithingScreen/field_42047I */
    public static int field_42047() {
        return 8;
    }

    /** was net/minecraft/client/gui/screens/inventory/SmithingScreen/field_42048Lorg/joml/Quaternionf; */
    public static org.joml.Quaternionf field_42048() {
        throw new UnsupportedOperationException("CenturyBridge: ARMOR_STAND_ANGLE was a static Quaternionf constant in net.minecraft.client.gui.screens.inventory.SmithingScreen (BeaconScreen) deleted in 1.20.2 with no replacement; the exact rotation value cannot be reconstructed from the new API surface");
    }

    /** was net/minecraft/client/gui/screens/inventory/SmithingScreen/field_42049I */
    public static int field_42049() {
        throw new UnsupportedOperationException("CenturyBridge: ARMOR_STAND_SCALE is not available");
    }

    /** was net/minecraft/client/gui/screens/inventory/SmithingScreen/field_42050I */
    public static int field_42050() {
        return 99;
    }

    /** was net/minecraft/client/gui/screens/inventory/SmithingScreen/field_42051I */
    public static int field_42051() {
        return 15;
    }

    /** was net/minecraft/client/gui/screens/inventory/SmithingScreen/field_42068I */
    public static int field_42068() {
        return 4;
    }

    /** was net/minecraft/realms/RealmsScreen/field_33036I */
    public static int field_33036() {
        return 19088743;
    }

    /** was net/minecraft/realms/RealmsScreen/field_33037I */
    public static int field_33037() {
        return 240;
    }

    /** was net/minecraft/realms/RealmsScreen/field_33038I */
    public static int field_33038() {
        return 0xD0000000;
    }

    /** was net/minecraft/realms/RealmsScreen/field_33039I */
    public static int field_33039() {
        return 80;
    }

    /** was net/minecraft/realms/RealmsScreen/field_33043I */
    public static int field_33043() {
        return 32;
    }

    /** was net/minecraft/realms/RealmsScreen/field_33044Ljava/lang/String; */
    public static java.lang.String field_33044() {
        return "minceraft";
    }

    /** was net/minecraft/realms/RealmsScreen/field_33056I */
    public static int field_33056() {
        return -10526880;
    }

    /** was net/minecraft/realms/RealmsScreen/field_33059I */
    public static int field_33059() {
        throw new UnsupportedOperationException("CenturyBridge: field_33059 was removed in 1.20.2 with no direct replacement and its constant value cannot be determined from the surviving API surface");
    }

    /** was net/minecraft/realms/RealmsScreen/field_33060I */
    public static int field_33060() {
        return 16;
    }

    /** was net/minecraft/realms/RealmsScreen/field_33064I */
    public static int field_33064() {
        return 26;
    }

    /** was net/minecraft/realms/RealmsScreen/field_33065I */
    public static int field_33065() {
        return -7079482;
    }


    /** was net/minecraft/client/gui/screens/packs/TransferableSelectionList/field_19125Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_19125() {
        return net.minecraft.resources.Identifier.parse("textures/gui/recipe_book.png");
    }

    /** was net/minecraft/client/gui/screens/debug/GameModeSwitcherScreen/field_24566Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_24566() {
        return net.minecraft.resources.Identifier.parse("textures/gui/social_interactions/black_icon.png");
    }

    /** was net/minecraft/client/gui/screens/social/SocialInteractionsScreen/field_26875Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_26875() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.client.gui.screens.social.SocialInteractionsScreen.field_26875 (Identifier) was deleted in 1.20.2 with no direct replacement; the constant value cannot be reconstructed from the available API surface");
    }

    /** was net/minecraft/world/level/block/ChangeOverTimeBlock/method_33621(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V */
    public static void method_33621(net.minecraft.world.level.block.ChangeOverTimeBlock self, net.minecraft.world.level.block.state.BlockState a0, net.minecraft.server.level.ServerLevel a1, net.minecraft.core.BlockPos a2, net.minecraft.util.RandomSource a3) {
        self.changeOverTime(a0, a1, a2, a3);
    }

    /** was net/minecraft/world/level/block/ChangeOverTimeBlock/method_33623(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V */
    public static void method_33623(net.minecraft.world.level.block.ChangeOverTimeBlock self, net.minecraft.world.level.block.state.BlockState a0, net.minecraft.server.level.ServerLevel a1, net.minecraft.core.BlockPos a2, net.minecraft.util.RandomSource a3) {
        self.changeOverTime(a0, a1, a2, a3);
    }

    /** was net/minecraft/client/renderer/entity/EntityRenderers/field_32922Ljava/lang/String; */
    public static java.lang.String field_32922() {
        return "en_us";
    }


    /** was net/minecraft/world/level/storage/loot/providers/number/NumberProviders/method_32455()Ljava/lang/Object; */
    public static java.lang.Object method_32455(net.minecraft.world.level.storage.loot.providers.number.NumberProviders self) {
        throw new UnsupportedOperationException("CenturyBridge: method_32455 was removed in 1.20.2 with no direct replacement; net.minecraft.world.level.storage.loot.providers.number.NumberProviders no longer exposes a value accessor and the restructured fields (net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType) cannot reproduce the old Object return contract without their API");
    }

    /** was net/minecraft/world/level/storage/loot/providers/score/ScoreboardNameProvider/method_32476(Lnet/minecraft/world/level/storage/loot/LootContext;)Ljava/lang/String; */
    public static java.lang.String method_32476(net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProvider self, net.minecraft.world.level.storage.loot.LootContext a0) {
        throw new UnsupportedOperationException("CenturyBridge: LootContextType validation now uses ErrorReporter and cannot be converted to String");
    }

    /** was net/minecraft/world/level/storage/loot/providers/score/ScoreboardNameProviders/method_32478()Ljava/lang/Object; */
    public static java.lang.Object method_32478(net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProviders self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProviders.method_32478() was removed in 1.20.2 with no direct replacement; the default-value accessor was refactored out of LootDataType");
    }

    /** was net/minecraft/client/gui/screens/inventory/tooltip/ClientBundleTooltip$class_5771.field_28368I */
    public static int field_28368() {
        throw new UnsupportedOperationException("CenturyBridge: field_28368 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/client/gui/screens/inventory/tooltip/ClientBundleTooltip$class_5771.field_28369I */
    public static int field_28369() {
        throw new UnsupportedOperationException("CenturyBridge: field_28369 was an instance field representing V offset but the shim has no receiver parameter");
    }

    /** was net/minecraft/client/gui/screens/inventory/tooltip/ClientBundleTooltip/field_28359Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_28359() {
        return net.minecraft.resources.Identifier.parse("textures/gui/container/bundle.png");
    }

    /** was net/minecraft/world/entity/ai/behavior/FollowTemptation/field_30116D */
    public static double field_30116() {
        return 8.0;
    }

    /** was net/minecraft/client/multiplayer/ClientAdvancements$Listener.onUpdateAdvancementProgress(Lnet/minecraft/advancements/Advancement;Lnet/minecraft/advancements/AdvancementProgress;)V */
    public void onUpdateAdvancementProgress(net.minecraft.advancements.Advancement a0, net.minecraft.advancements.AdvancementProgress a1) {
        throw new UnsupportedOperationException("CenturyBridge: Cannot convert net.minecraft.advancements.Advancement to net.minecraft.advancements.AdvancementNode");
    }

    /** was net/minecraft/client/multiplayer/ClientAdvancements$Listener.onSelectedTabChanged(Lnet/minecraft/advancements/Advancement;)V */
    public void onSelectedTabChanged(net.minecraft.advancements.Advancement a0) {
        ((net.minecraft.client.multiplayer.ClientAdvancements.Listener) (Object) this).onSelectedTabChanged(new net.minecraft.advancements.AdvancementHolder(net.minecraft.resources.Identifier.fromNamespaceAndPath("centurybridge", "dummy"), a0));
    }

    /** was net/minecraft/world/entity/animal/allay/Allay/method_45342()Z */
    public static boolean method_45342(net.minecraft.world.entity.animal.allay.Allay self) {
        return self.isDancing();
    }

    /** was net/minecraft/client/GuiMessageTag$Icon.field_39764I */
    public static int field_39764() {
        throw new UnsupportedOperationException("CenturyBridge: field_39764 is no longer available");
    }

    /** was net/minecraft/client/GuiMessageTag$Icon.field_39765I */
    public static int field_39765() {
        return 0;
    }

    /** was net/minecraft/client/GuiMessageTag/field_39761Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_39761() {
        return net.minecraft.resources.Identifier.parse("textures/gui/chat_status.png");
    }

    /** was net/minecraft/world/level/storage/loot/entries/LootPoolEntries/method_29316()Ljava/lang/Object; */
    public static java.lang.Object method_29316(net.minecraft.world.level.storage.loot.entries.LootPoolEntries self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.storage.loot.entries.LootPoolEntries is no longer an enum and has no instance state");
    }

    /** was net/minecraft/network/chat/SignedMessageChain$Decoder.field_40848Lnet/minecraft/network/chat/SignedMessageChain$Decoder; */
    public static net.minecraft.network.chat.SignedMessageChain.Decoder field_40848() {
        return net.minecraft.network.chat.SignedMessageChain.Decoder.unsigned(new java.util.UUID(0L, 0L), () -> false);
    }

    /** was net/minecraft/world/entity/animal/camel/Camel/method_45348()Z */
    public static boolean method_45348(net.minecraft.world.entity.animal.camel.Camel self) {
        return self.isCamelVisuallySitting() && !self.isCamelSitting();
    }

    /** was net/minecraft/client/gui/layouts/EqualSpacingLayout$1.field_41817[I */
    public static int[] field_41817() {
        throw new UnsupportedOperationException("CenturyBridge: Target class net.minecraft.client.gui.layouts.LinearLayout$1 is not public");
    }

    /** was net/minecraft/core/registries/BuiltInRegistries/field_41154Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_41154() {
        return net.minecraft.resources.Identifier.parse("root");
    }

    /** was net/minecraft/network/protocol/BundlerInfo/field_41877Lio/netty/util/AttributeKey; */
    public static io.netty.util.AttributeKey field_41877() {
        throw new UnsupportedOperationException("CenturyBridge: field_41877 (AttributeKey) was removed from net.minecraft.network.protocol.BundlerInfo in 1.20.2 with no direct replacement; bundle-packet channel attribute mechanism reworked");
    }

    /** was net/minecraft/world/entity/Display/field_42385Ljava/lang/String; */
    public static java.lang.String field_42385() {
        throw new UnsupportedOperationException("CenturyBridge: field_42385 (static String) was deleted from net.minecraft.world.entity.Display in 1.20.2 with no direct replacement; its literal value cannot be determined from the new API surface alone");
    }

    /** was net/minecraft/world/entity/Display/field_43149Ljava/lang/String; */
    public static java.lang.String field_43149() {
        return "sniffing_state";
    }

    /** was net/minecraft/world/entity/Display/method_48863()Lorg/joml/Quaternionf; */
    public static org.joml.Quaternionf method_48863(net.minecraft.world.entity.Display self) {
        throw new UnsupportedOperationException("CenturyBridge: DisplayEntity left rotation getter was removed in 1.20.2");
    }

    /** was net/minecraft/world/ticks/ContainerSingleItem/method_49274()Lnet/minecraft/world/item/ItemStack; */
    public static net.minecraft.world.item.ItemStack method_49274(net.minecraft.world.ticks.ContainerSingleItem self) {
        return self.removeTheItem();
    }

    /** was net/minecraft/world/ticks/ContainerSingleItem/method_49275(Lnet/minecraft/world/item/ItemStack;)V */
    public static void method_49275(net.minecraft.world.ticks.ContainerSingleItem self, net.minecraft.world.item.ItemStack a0) {
        self.setTheItem(a0);
    }

    /** was net/minecraft/world/ticks/ContainerSingleItem/method_49276()Lnet/minecraft/world/item/ItemStack; */
    public static net.minecraft.world.item.ItemStack method_49276(net.minecraft.world.ticks.ContainerSingleItem self) {
        return self.getTheItem();
    }

    /** was net/minecraft/util/CommonLinks/field_43134Ljava/lang/String; */
    public static java.lang.String field_43134() {
        return "players";
    }

    /** was net/minecraft/world/level/storage/loot/LootDataType/method_51203()Lcom/google/gson/Gson; */
    public static com.google.gson.Gson method_51203(net.minecraft.world.level.storage.loot.LootDataType self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.storage.loot.LootDataType.method_51203() returned a Gson instance that was removed in 1.20.2; the new API surface has no Gson field or method to delegate to");
    }

    /** was net/minecraft/world/Container/field_29952I */
    public static int field_29952() {
        return 64;
    }

    /** was net/minecraft/world/Container/field_42619I */
    public static int field_42619() {
        return 64;
    }

    /** was net/minecraft/world/entity/AreaEffectCloud/method_5602(I)V */
    public static void method_5602(net.minecraft.world.entity.AreaEffectCloud self, int a0) {
        self.setDurationOnUse(a0);
    }

    /** was net/minecraft/world/entity/AreaEffectCloud/method_5606()I */
    public static int method_5606(net.minecraft.world.entity.AreaEffectCloud self) {
        return self.getDuration();
    }

    /** was net/minecraft/world/entity/Entity/field_29991F */
    public static float field_29991() {
        return 0.0f;
    }

    /** was net/minecraft/world/entity/Entity/method_18378(Lnet/minecraft/world/entity/Pose;Lnet/minecraft/world/entity/EntityDimensions;)F */
    public static float method_18378(net.minecraft.world.entity.Entity self, net.minecraft.world.entity.Pose a0, net.minecraft.world.entity.EntityDimensions a1) {
        return self.getEyeHeight(a0);
    }

    /** was net/minecraft/world/entity/Entity/method_52537(Lnet/minecraft/world/entity/Entity;)F */
    public static float method_52537(net.minecraft.world.entity.Entity self, net.minecraft.world.entity.Entity a0) {
        float f = (float) (self.getX() - a0.getX());
        float g = (float) (self.getY() - a0.getY());
        float h = (float) (self.getZ() - a0.getZ());
        return (float) Math.sqrt(f * f + g * g + h * h);
    }


    /** was net/minecraft/world/entity/LivingEntity/field_30067F */
    public static float field_30067() {
        return 0.02f;
    }

    /** was net/minecraft/world/entity/LivingEntity/method_20236(Lnet/minecraft/world/InteractionHand;)V */
    public static void method_20236(net.minecraft.world.entity.LivingEntity self, net.minecraft.world.InteractionHand a0) {
        self.swing(a0, false);
    }



    /** was net/minecraft/world/entity/ai/goal/GoalSelector/method_35114(I)V */
    public static void method_35114(net.minecraft.world.entity.ai.goal.GoalSelector self, int a0) {
        net.minecraft.world.entity.ai.goal.Goal.Flag[] values = net.minecraft.world.entity.ai.goal.Goal.Flag.values();
        if (a0 >= 0 && a0 < values.length) {
            self.enableControlFlag(values[a0]);
        }
    }

    /** was net/minecraft/world/entity/animal/squid/Squid$SquidRandomMovementGoal.field_6916Lnet/minecraft/world/entity/animal/squid/Squid; */
    public static net.minecraft.world.entity.animal.squid.Squid field_6916() {
        throw new UnsupportedOperationException("CenturyBridge: field_6916 was deleted without replacement");
    }

    /** was net/minecraft/world/entity/animal/equine/Horse/method_6786()Lnet/minecraft/world/item/ItemStack; */
    public static net.minecraft.world.item.ItemStack method_6786(net.minecraft.world.entity.animal.equine.Horse self) {
        return new net.minecraft.world.item.ItemStack(net.minecraft.world.level.block.Blocks.STRIPPED_ACACIA_WOOD);
    }

    /** was net/minecraft/world/level/pathfinder/WalkNodeEvaluator/method_27138(Lnet/minecraft/world/level/block/state/BlockState;)Z */
    public static boolean method_27138(net.minecraft.world.level.pathfinder.WalkNodeEvaluator self, net.minecraft.world.level.block.state.BlockState a0) {
        throw new UnsupportedOperationException("CenturyBridge: PathNodeMaker no longer supports checking a raw BlockState without position or context in 1.20.5");
    }

    /** was net/minecraft/world/level/pathfinder/WalkNodeEvaluator/method_63(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/pathfinder/PathType; */
    public static net.minecraft.world.level.pathfinder.PathType method_63(net.minecraft.world.level.pathfinder.WalkNodeEvaluator self, net.minecraft.world.entity.Mob a0, net.minecraft.core.BlockPos a1) {
        return self.getPathTypeStatic(a0, a1);
    }

    /** was net/minecraft/world/level/pathfinder/WalkNodeEvaluator/method_64(Lnet/minecraft/world/level/BlockGetter;IIILjava/util/EnumSet;Lnet/minecraft/world/level/pathfinder/PathType;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/pathfinder/PathType; */
    public static net.minecraft.world.level.pathfinder.PathType method_64(net.minecraft.world.level.pathfinder.WalkNodeEvaluator self, net.minecraft.world.level.BlockGetter a0, int a1, int a2, int a3, java.util.EnumSet a4, net.minecraft.world.level.pathfinder.PathType a5, net.minecraft.core.BlockPos a6) {
        throw new UnsupportedOperationException("CenturyBridge: PathNodeMaker.getNode requires PathNodeMakerContext in 1.20.5");
    }

    /** was net/minecraft/world/entity/decoration/painting/Painting/field_42463Ljava/lang/String; */
    public static java.lang.String field_42463() {
        return "variant";
    }

    /** was net/minecraft/SharedConstants/field_29699Z */
    public static boolean field_29699() {
        return false;
    }

    /** was net/minecraft/SharedConstants/field_29701Z */
    public static boolean field_29701() {
        return false;
    }

    /** was net/minecraft/SharedConstants/method_44355(Ljava/lang/String;Z)Ljava/lang/String; */
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

    /** was net/minecraft/SharedConstants/method_643(C)Z */
    public static boolean method_643(net.minecraft.SharedConstants self, char a0) {
        return a0 != '\u00a7' && a0 >= ' ' && a0 != 127;
    }

    /** was net/minecraft/world/entity/monster/Phantom$PhantomLookControl.field_7329Lnet/minecraft/world/entity/monster/Phantom; */
    public static net.minecraft.world.entity.monster.Phantom field_7329() {
        throw new UnsupportedOperationException("CenturyBridge: Field is an instance field (this$0) but shim has no receiver parameter");
    }

    /** was net/minecraft/world/entity/monster/zombie/Zombie/field_41028F */
    public static float field_41028() {
        return 0.001f;
    }

    /** was net/minecraft/world/entity/player/Player$1.field_7533[I */
    public static int[] field_7533() {
        throw new UnsupportedOperationException("CenturyBridge: Switch map field_7533 in net.minecraft.world.entity.player.Player$1 is no longer available");
    }

    /** was net/minecraft/world/entity/player/Player/field_30643I */
    public static int field_30643() {
        return 100;
    }

    /** was net/minecraft/world/entity/player/Player/method_53968(Ljava/lang/String;)Z */
    public static boolean method_53968(net.minecraft.world.entity.player.Player self, java.lang.String a0) {
        return self.getGameProfile().name().equalsIgnoreCase(a0);
    }

    /** was net/minecraft/world/entity/player/Player/method_54292(Z)F */
    public static float method_54292(net.minecraft.world.entity.player.Player self, boolean a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_54292 was removed in 1.20.5");
    }

    /** was net/minecraft/world/inventory/BeaconMenu$1.field_17291Lnet/minecraft/world/inventory/BeaconMenu; */
    public static net.minecraft.world.inventory.BeaconMenu field_17291() {
        throw new UnsupportedOperationException("CenturyBridge: Cannot access outer instance field statically without receiver");
    }

    /** was net/minecraft/world/inventory/BeaconMenu$PaymentSlot.field_7768Lnet/minecraft/world/inventory/BeaconMenu; */
    public static net.minecraft.world.inventory.BeaconMenu field_7768() {
        throw new UnsupportedOperationException("CenturyBridge: field_7768 is an instance field but shim has no receiver");
    }

    /** was net/minecraft/world/inventory/EnchantmentMenu$2.field_7816Lnet/minecraft/world/inventory/EnchantmentMenu; */
    public static net.minecraft.world.inventory.EnchantmentMenu field_7816() {
        throw new UnsupportedOperationException("CenturyBridge: field_7816 is an instance field and cannot be accessed without a receiver");
    }

    /** was net/minecraft/world/inventory/EnchantmentMenu$3.field_7817Lnet/minecraft/world/inventory/EnchantmentMenu; */
    public static net.minecraft.world.inventory.EnchantmentMenu field_7817() {
        throw new UnsupportedOperationException("CenturyBridge: field_7817 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/world/inventory/LoomMenu$3.field_7852Lnet/minecraft/world/inventory/LoomMenu; */
    public static net.minecraft.world.inventory.LoomMenu field_7852() {
        throw new UnsupportedOperationException("CenturyBridge: field_7852 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/world/inventory/LoomMenu$4.field_7853Lnet/minecraft/world/inventory/LoomMenu; */
    public static net.minecraft.world.inventory.LoomMenu field_7853() {
        throw new UnsupportedOperationException("CenturyBridge: field_7853 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/world/inventory/LoomMenu$5.field_7854Lnet/minecraft/world/inventory/LoomMenu; */
    public static net.minecraft.world.inventory.LoomMenu field_7854() {
        throw new UnsupportedOperationException("CenturyBridge: field_7854 is a synthetic outer class reference and cannot be accessed without an instance");
    }

    /** was net/minecraft/world/item/equipment/ArmorMaterial/method_24355()F */
    public static float method_24355(net.minecraft.world.item.equipment.ArmorMaterial self) {
        return self.toughness();
    }



    /** was net/minecraft/world/item/equipment/ArmorMaterial/method_7695()Lnet/minecraft/world/item/crafting/Ingredient; */
    public static net.minecraft.world.item.crafting.Ingredient method_7695(net.minecraft.world.item.equipment.ArmorMaterial self) {
        throw new UnsupportedOperationException("CenturyBridge: Ingredient getter is no longer available on net.minecraft.world.item.equipment.ArmorMaterial");
    }

    /** was net/minecraft/world/item/equipment/ArmorMaterial/method_7699()I */
    public static int method_7699(net.minecraft.world.item.equipment.ArmorMaterial self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.item.equipment.ArmorMaterial is no longer RabbitMoveControl");
    }

    /** was net/minecraft/world/item/equipment/ArmorMaterial/method_7700()F */
    public static float method_7700(net.minecraft.world.item.equipment.ArmorMaterial self) {
        return self.toughness();
    }

    /** was net/minecraft/world/item/BlockItem/field_30849Ljava/lang/String; */
    public static java.lang.String field_30849() {
        return "BlockEntityTag";
    }

    /** was net/minecraft/world/item/BlockItem/field_30850Ljava/lang/String; */
    public static java.lang.String field_30850() {
        return "BlockEntityTag";
    }


    /** was net/minecraft/world/item/CompassItem/field_30860Ljava/lang/String; */
    public static java.lang.String field_30860() {
        return "Charged";
    }

    /** was net/minecraft/world/item/CompassItem/field_30861Ljava/lang/String; */
    public static java.lang.String field_30861() {
        return "Items";
    }

    /** was net/minecraft/world/item/CompassItem/field_30862Ljava/lang/String; */
    public static java.lang.String field_30862() {
        return "use_ticks";
    }

    /** was net/minecraft/class_1772.field_30874Ljava/lang/String; */
    public static java.lang.String field_30874() {
        return "default";
    }


    /** was net/minecraft/world/item/FireworkRocketItem/field_30875Ljava/lang/String; */
    public static java.lang.String field_30875() {
        return "LodestonePos";
    }

    /** was net/minecraft/world/item/FireworkRocketItem/field_30876Ljava/lang/String; */
    public static java.lang.String field_30876() {
        return "default";
    }

    /** was net/minecraft/world/item/FireworkRocketItem/field_30877Ljava/lang/String; */
    public static java.lang.String field_30877() {
        return "LodestonePos";
    }

    /** was net/minecraft/world/item/FireworkRocketItem/field_30878Ljava/lang/String; */
    public static java.lang.String field_30878() {
        return "Fireworks";
    }

    /** was net/minecraft/world/item/FireworkRocketItem/field_30879Ljava/lang/String; */
    public static java.lang.String field_30879() {
        return "Potion";
    }

    /** was net/minecraft/world/item/FireworkRocketItem/field_30880Ljava/lang/String; */
    public static java.lang.String field_30880() {
        return "Fireworks";
    }

    /** was net/minecraft/world/item/FireworkRocketItem/field_30881Ljava/lang/String; */
    public static java.lang.String field_30881() {
        return "Fireworks";
    }

    /** was net/minecraft/world/item/FireworkRocketItem/field_30882Ljava/lang/String; */
    public static java.lang.String field_30882() {
        return "Type";
    }

    /** was net/minecraft/world/item/FireworkRocketItem/field_30883Ljava/lang/String; */
    public static java.lang.String field_30883() {
        return "Fireworks";
    }

    /** was net/minecraft/world/item/MapItem/field_41067Ljava/lang/String; */
    public static java.lang.String field_41067() {
        return "map_color";
    }

    /** was net/minecraft/world/item/MapItem/field_41068Ljava/lang/String; */
    public static java.lang.String field_41068() {
        return "map_color";
    }

    /** was net/minecraft/world/item/MapItem/method_17440(I)Ljava/lang/String; */
    public static java.lang.String method_17440(net.minecraft.world.item.MapItem self, int a0) {
        return "map_" + a0;
    }

    /** was net/minecraft/world/item/PlayerHeadItem/field_30916Ljava/lang/String; */
    public static java.lang.String field_30916() {
        return "Tool modifier";
    }

    /** was net/minecraft/world/item/Rarity/field_8908Lnet/minecraft/ChatFormatting; */
    public static net.minecraft.ChatFormatting field_8908() {
        throw new UnsupportedOperationException("CenturyBridge: color is an instance field but the shim signature lacks the instance parameter");
    }

    /** was net/minecraft/world/item/ShieldItem/field_30920Ljava/lang/String; */
    public static java.lang.String field_30920() {
        return "ChargedProjectiles";
    }

    /** was net/minecraft/class_1830.field_30923Ljava/lang/String; */
    public static java.lang.String field_30923() {
        return "Potion";
    }


    /** was net/minecraft/world/item/WrittenBookItem/field_30929I */
    public static int field_30929() {
        return 64;
    }

    /** was net/minecraft/world/item/WrittenBookItem/field_30930I */
    public static int field_30930() {
        throw new UnsupportedOperationException("CenturyBridge: field_30930 (int field in CompassItem) is no longer available in 1.20.5");
    }

    /** was net/minecraft/world/item/WrittenBookItem/field_30931I */
    public static int field_30931() {
        return 32;
    }

    /** was net/minecraft/world/item/WrittenBookItem/field_30932I */
    public static int field_30932() {
        return 32;
    }

    /** was net/minecraft/world/item/WrittenBookItem/field_30933I */
    public static int field_30933() {
        return 0x6666FF;
    }

    /** was net/minecraft/world/item/WrittenBookItem/field_30934I */
    public static int field_30934() {
        return 1200;
    }

    /** was net/minecraft/world/item/WrittenBookItem/field_30935Ljava/lang/String; */
    public static java.lang.String field_30935() {
        return "default_potion";
    }

    /** was net/minecraft/world/item/WrittenBookItem/field_30936Ljava/lang/String; */
    public static java.lang.String field_30936() {
        return "filtered_pages";
    }

    /** was net/minecraft/world/item/WrittenBookItem/field_30937Ljava/lang/String; */
    public static java.lang.String field_30937() {
        return "pages";
    }

    /** was net/minecraft/world/item/WrittenBookItem/field_30938Ljava/lang/String; */
    public static java.lang.String field_30938() {
        return "pages";
    }

    /** was net/minecraft/world/item/WrittenBookItem/field_30939Ljava/lang/String; */
    public static java.lang.String field_30939() {
        return "resolved";
    }

    /** was net/minecraft/world/item/WrittenBookItem/field_30940Ljava/lang/String; */
    public static java.lang.String field_30940() {
        return "filtered_pages";
    }

    /** was net/minecraft/world/item/WrittenBookItem/field_30941Ljava/lang/String; */
    public static java.lang.String field_30941() {
        return "Potion";
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8963Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8963() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.HEALING.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8964Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8964() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_SLOW_FALLING.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8965Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8965() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_STRENGTH.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8966Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8966() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRONG_SWIFTNESS.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8967Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8967() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.MUNDANE.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8968Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8968() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.NIGHT_VISION.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8969Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8969() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_FIRE_RESISTANCE.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8970Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8970() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_WEAKNESS.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8971Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8971() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_LEAPING.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8972Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8972() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRONG_POISON.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8973Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8973() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRONG_HARMING.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8974Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8974() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.SLOW_FALLING.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8975Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8975() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.WEAKNESS.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8976Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8976() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRONG_SLOWNESS.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8977Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8977() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRONG_TURTLE_MASTER.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8978Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8978() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRENGTH.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8979Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8979() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LEAPING.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8980Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8980() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRONG_HEALING.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8981Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8981() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_NIGHT_VISION.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8982Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8982() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.POISON.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8983Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8983() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_SWIFTNESS.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8985Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8985() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.THICK.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8986Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8986() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.REGENERATION.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8987Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8987() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.FIRE_RESISTANCE.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8988Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8988() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_TURTLE_MASTER.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8989Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8989() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_SLOWNESS.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8990Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8990() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.TURTLE_MASTER.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8991Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8991() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.WATER.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8992Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8992() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRONG_REGENERATION.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8993Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8993() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRONG_STRENGTH.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8994Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8994() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.WATER_BREATHING.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8995Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8995() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LUCK.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8996Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8996() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.SLOWNESS.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8997Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8997() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.INVISIBILITY.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8998Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8998() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.STRONG_LEAPING.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_8999Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_8999() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.AWKWARD.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_9000Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_9000() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_INVISIBILITY.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_9001Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_9001() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_WATER_BREATHING.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_9002Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_9002() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_POISON.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_9003Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_9003() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.LONG_REGENERATION.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_9004Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_9004() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.HARMING.value();
    }

    /** was net/minecraft/world/item/alchemy/Potions/field_9005Lnet/minecraft/world/item/alchemy/Potion; */
    public static net.minecraft.world.item.alchemy.Potion field_9005() {
        return (net.minecraft.world.item.alchemy.Potion) net.minecraft.world.item.alchemy.Potions.SWIFTNESS.value();
    }


    /** was net/minecraft/world/item/crafting/RecipeSerializer/codec()Lcom/mojang/serialization/Codec; */
    public com.mojang.serialization.Codec codec() {
        return ((net.minecraft.world.item.crafting.RecipeSerializer) (Object) this).codec().codec();
    }


    /** was net/minecraft/world/level/LevelAccessor/gameEvent(Lnet/minecraft/world/level/gameevent/GameEvent;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V */
    public void gameEvent(net.minecraft.world.level.gameevent.GameEvent a0, net.minecraft.world.phys.Vec3 a1, net.minecraft.world.level.gameevent.GameEvent.Context a2) {
        ((net.minecraft.world.level.LevelAccessor) (Object) this).gameEvent(net.minecraft.core.Holder.direct(a0), a1, a2);
    }

    /** was net/minecraft/world/level/LevelAccessor/gameEvent(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/gameevent/GameEvent;Lnet/minecraft/core/BlockPos;)V */
    public void gameEvent(net.minecraft.world.entity.Entity a0, net.minecraft.world.level.gameevent.GameEvent a1, net.minecraft.core.BlockPos a2) {
        ((net.minecraft.world.level.LevelAccessor) (Object) this).gameEvent(a0, net.minecraft.core.Holder.direct(a1), a2);
    }

    /** was net/minecraft/world/level/LevelAccessor/gameEvent(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/gameevent/GameEvent;Lnet/minecraft/world/phys/Vec3;)V */
    public void gameEvent(net.minecraft.world.entity.Entity a0, net.minecraft.world.level.gameevent.GameEvent a1, net.minecraft.world.phys.Vec3 a2) {
        ((net.minecraft.world.level.LevelAccessor) (Object) this).gameEvent(a0, net.minecraft.core.Holder.direct(a1), a2);
    }

    /** was net/minecraft/world/level/LevelAccessor/gameEvent(Lnet/minecraft/world/level/gameevent/GameEvent;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V */
    public void gameEvent(net.minecraft.world.level.gameevent.GameEvent a0, net.minecraft.core.BlockPos a1, net.minecraft.world.level.gameevent.GameEvent.Context a2) {
        ((net.minecraft.world.level.LevelAccessor) (Object) this).gameEvent(net.minecraft.core.Holder.direct(a0), a1, a2);
    }

    /** was net/minecraft/world/level/biome/CheckerboardColumnBiomeSource/field_24715Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24715() {
        return net.minecraft.world.level.biome.CheckerboardColumnBiomeSource.CODEC.codec();
    }

    /** was net/minecraft/world/level/biome/FixedBiomeSource/field_24717Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24717() {
        return net.minecraft.world.level.biome.FixedBiomeSource.CODEC.codec();
    }

    /** was net/minecraft/world/level/biome/TheEndBiomeSource/field_24730Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24730() {
        return net.minecraft.world.level.biome.TheEndBiomeSource.CODEC.codec();
    }

    /** was net/minecraft/commands/arguments/MessageArgument$Message.field_9877Ljava/lang/String; */
    public static java.lang.String field_9877() {
        throw new UnsupportedOperationException("CenturyBridge: field_9877 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/commands/arguments/MessageArgument$Message.method_35691()Ljava/lang/String; */
    public static java.lang.String method_35691(net.minecraft.commands.arguments.MessageArgument.Message self) {
        return self.text();
    }

    /** was net/minecraft/commands/arguments/MessageArgument$Message.method_35692()[Lnet/minecraft/commands/arguments/MessageArgument$Part; */
    public static net.minecraft.commands.arguments.MessageArgument.Part[] method_35692(net.minecraft.commands.arguments.MessageArgument.Message self) {
        return self.parts();
    }

    /** was net/minecraft/commands/arguments/MessageArgument$Part.method_35693()Lnet/minecraft/commands/arguments/selector/EntitySelector; */
    public static net.minecraft.commands.arguments.selector.EntitySelector method_35693(net.minecraft.commands.arguments.MessageArgument.Part self) {
        return self.selector();
    }

    /** was net/minecraft/commands/arguments/MessageArgument$Part.method_9343()I */
    public static int method_9343(net.minecraft.commands.arguments.MessageArgument.Part self) {
        return self.start();
    }

    /** was net/minecraft/commands/arguments/MessageArgument$Part.method_9344()I */
    public static int method_9344(net.minecraft.commands.arguments.MessageArgument.Part self) {
        return self.start();
    }

    /** was net/minecraft/world/level/saveddata/maps/MapItemSavedData$MapPatch.field_27892I */
    public static int field_27892() {
        throw new UnsupportedOperationException("CenturyBridge: field_27892 is an instance field but the shim is static");
    }

    /** was net/minecraft/world/level/saveddata/maps/MapItemSavedData$MapPatch.field_27893I */
    public static int field_27893() {
        throw new UnsupportedOperationException("CenturyBridge: field_27893 cannot be represented as static int");
    }

    /** was net/minecraft/world/level/saveddata/maps/MapItemSavedData$MapPatch.field_27894I */
    public static int field_27894() {
        throw new UnsupportedOperationException("CenturyBridge: field_27894 is an instance field but the shim has no receiver");
    }

    /** was net/minecraft/world/level/saveddata/maps/MapItemSavedData$MapPatch.field_27895I */
    public static int field_27895() {
        return 4;
    }

    /** was net/minecraft/world/level/saveddata/maps/MapItemSavedData$MapPatch.field_27896[B */
    public static byte[] field_27896() {
        return new byte[]{(byte) -119, 80, 78, 71, 13, 10, 26, 10};
    }

    /** was net/minecraft/commands/arguments/item/FunctionArgument$1.field_10786Lnet/minecraft/commands/arguments/item/FunctionArgument; */
    public static net.minecraft.commands.arguments.item.FunctionArgument field_10786() {
        throw new UnsupportedOperationException("CenturyBridge: field_10786 has been removed without replacement");
    }

    /** was net/minecraft/commands/arguments/item/FunctionArgument$2.field_10788Lnet/minecraft/commands/arguments/item/FunctionArgument; */
    public static net.minecraft.commands.arguments.item.FunctionArgument field_10788() {
        throw new UnsupportedOperationException("CenturyBridge: field_10788 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/core/MappedRegistry/method_46744(ILnet/minecraft/resources/ResourceKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/core/Holder$Reference; */
    public static net.minecraft.core.Holder.Reference method_46744(net.minecraft.core.MappedRegistry self, int a0, net.minecraft.resources.ResourceKey a1, java.lang.Object a2, com.mojang.serialization.Lifecycle a3) {
        throw new UnsupportedOperationException("CenturyBridge: Registering with an explicit raw ID is no longer supported");
    }


    /** was net/minecraft/core/WritableRegistry/register(Lnet/minecraft/resources/ResourceKey;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/core/Holder$Reference; */
    public net.minecraft.core.Holder.Reference register(net.minecraft.resources.ResourceKey a0, java.lang.Object a1, com.mojang.serialization.Lifecycle a2) {
        return ((net.minecraft.core.WritableRegistry) (Object) this).register(a0, a1, new net.minecraft.core.RegistrationInfo(java.util.Optional.empty(), a2));
    }

    /** was net/minecraft/network/PacketListener/method_40065()Z */
    public static boolean method_40065(net.minecraft.network.PacketListener self) {
        return self.isAcceptingMessages();
    }

    /** was net/minecraft/world/level/block/entity/BannerBlockEntity/field_31297Ljava/lang/String; */
    public static java.lang.String field_31297() {
        return "Patterns";
    }

    /** was net/minecraft/world/level/block/entity/BannerBlockEntity/field_31298Ljava/lang/String; */
    public static java.lang.String field_31298() {
        return "Items";
    }

    /** was net/minecraft/world/level/block/entity/BannerBlockEntity/field_31299Ljava/lang/String; */
    public static java.lang.String field_31299() {
        return "Patterns";
    }


    /** was net/minecraft/world/level/block/entity/BannerBlockEntity/method_16842(Lnet/minecraft/network/chat/Component;)V */
    public static void method_16842(net.minecraft.world.level.block.entity.BannerBlockEntity self, net.minecraft.network.chat.Component a0) {
        throw new UnsupportedOperationException("CenturyBridge: BannerBlockEntity.setCustomName (method_16842) was removed in 1.20.5 in favor of the component system.");
    }

    /** was net/minecraft/world/level/block/entity/BannerBlockEntity/method_24280(Lnet/minecraft/world/item/DyeColor;Lnet/minecraft/nbt/ListTag;)Ljava/util/List; */
    public static java.util.List method_24280(net.minecraft.world.level.block.entity.BannerBlockEntity self, net.minecraft.world.item.DyeColor a0, net.minecraft.nbt.ListTag a1) {
        throw new UnsupportedOperationException("CenturyBridge: Cannot parse banner patterns without registry context");
    }

    /** was net/minecraft/network/protocol/Packet/write(Lnet/minecraft/network/FriendlyByteBuf;)V */
    public static void method_11052(net.minecraft.network.protocol.Packet self, net.minecraft.network.FriendlyByteBuf a0) {
        throw new UnsupportedOperationException("CenturyBridge: Packets no longer support self-serialization without a codec context in 1.20.5");
    }

    /** was net/minecraft/network/protocol/Packet/method_52273()Lnet/minecraft/network/ConnectionProtocol; */
    public static net.minecraft.network.ConnectionProtocol method_52273(net.minecraft.network.protocol.Packet self) {
        throw new UnsupportedOperationException("CenturyBridge: Packets no longer hold a reference to their NetworkState in 1.20.5");
    }

    /** was net/minecraft/world/level/block/entity/Hopper/field_12025Lnet/minecraft/world/phys/shapes/VoxelShape; */
    public static net.minecraft.world.phys.shapes.VoxelShape field_12025() {
        return net.minecraft.world.phys.shapes.Shapes.block();
    }

    /** was net/minecraft/world/level/block/entity/RandomizableContainerBlockEntity/field_12037Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_12037() {
        throw new UnsupportedOperationException("CenturyBridge: lootTable is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/world/level/block/entity/SpawnerBlockEntity$1.field_27219Lnet/minecraft/world/level/block/entity/SpawnerBlockEntity; */
    public static net.minecraft.world.level.block.entity.SpawnerBlockEntity field_27219() {
        throw new UnsupportedOperationException("CenturyBridge: field_27219 is the synthetic outer this$0 field of net.minecraft.world.level.block.entity.SpawnerBlockEntity$1 and cannot be accessed statically without a receiver");
    }


    /** was net/minecraft/world/level/chunk/LevelChunk$RebindableTickingBlockEntityWrapper.field_27227Lnet/minecraft/world/level/chunk/LevelChunk; */
    public static net.minecraft.world.level.chunk.LevelChunk field_27227() {
        throw new UnsupportedOperationException("CenturyBridge: Cannot access instance field field_27227 statically");
    }

    /** was net/minecraft/world/level/chunk/LevelChunkSection$1BlockCounter.field_36411Lnet/minecraft/world/level/chunk/LevelChunkSection; */
    public static net.minecraft.world.level.chunk.LevelChunkSection field_36411() {
        throw new UnsupportedOperationException("CenturyBridge: field_36411 cannot be retrieved statically");
    }


    /** was net/minecraft/world/level/levelgen/DebugLevelSource/field_24768Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24768() {
        return net.minecraft.world.level.levelgen.DebugLevelSource.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/FlatLevelSource/field_24769Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24769() {
        return net.minecraft.world.level.levelgen.FlatLevelSource.CODEC.codec();
    }

    /** was net/minecraft/network/syncher/EntityDataAccessor/method_12712()Lnet/minecraft/network/syncher/EntityDataSerializer; */
    public static net.minecraft.network.syncher.EntityDataSerializer method_12712(net.minecraft.network.syncher.EntityDataAccessor self) {
        return self.serializer();
    }

    /** was net/minecraft/network/syncher/EntityDataAccessor/method_12713()I */
    public static int method_12713(net.minecraft.network.syncher.EntityDataAccessor self) {
        return self.id();
    }

    /** was net/minecraft/world/level/levelgen/feature/TreeFeature$1.field_42840Lnet/minecraft/world/level/levelgen/feature/TreeFeature; */
    public static net.minecraft.world.level.levelgen.feature.TreeFeature field_42840() {
        throw new UnsupportedOperationException("CenturyBridge: field_42840 is no longer available as the outer class is not captured by net.minecraft.world.level.levelgen.feature.TreeFeature$1");
    }

    /** was net/minecraft/world/level/levelgen/structure/structures/BuriedTreasureStructure/field_37790Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37790() {
        return net.minecraft.world.level.levelgen.structure.structures.BuriedTreasureStructure.CODEC.codec();
    }


    /** was net/minecraft/world/level/storage/PlayerDataStorage/method_263()[Ljava/lang/String; */
    public static java.lang.String[] method_263(net.minecraft.world.level.storage.PlayerDataStorage self) {
        throw new UnsupportedOperationException("CenturyBridge: PlayerDataStorage no longer exposes saved player IDs");
    }

    /** was net/minecraft/world/level/levelgen/placement/NoiseThresholdCountPlacement/field_24890Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24890() {
        return net.minecraft.world.level.levelgen.placement.NoiseThresholdCountPlacement.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/structures/DesertPyramidStructure/field_37791Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37791() {
        return net.minecraft.world.level.levelgen.structure.structures.DesertPyramidStructure.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/structures/EndCityStructure/field_37792Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37792() {
        return net.minecraft.world.level.levelgen.structure.structures.EndCityStructure.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/structures/IglooStructure/field_37793Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37793() {
        return net.minecraft.world.level.levelgen.structure.structures.IglooStructure.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/structures/JungleTempleStructure/field_37800Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37800() {
        return net.minecraft.world.level.levelgen.structure.structures.JungleTempleStructure.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/structures/MineshaftStructure/field_37801Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37801() {
        return net.minecraft.world.level.levelgen.structure.structures.MineshaftStructure.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/structures/NetherFortressStructure/field_37803Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37803() {
        return net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/structures/OceanMonumentStructure/field_37806Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37806() {
        return net.minecraft.world.level.levelgen.structure.structures.OceanMonumentStructure.CODEC.codec();
    }

    /** was net/minecraft/client/Options/field_37208Z */
    public static boolean field_37208() {
        return false;
    }

    /** was net/minecraft/world/level/levelgen/structure/structures/ShipwreckStructure/field_37815Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37815() {
        return net.minecraft.world.level.levelgen.structure.structures.ShipwreckStructure.CODEC.codec();
    }

    /** was net/minecraft/server/dedicated/ServerWatchdog$1.field_13826Lnet/minecraft/server/dedicated/ServerWatchdog; */
    public static net.minecraft.server.dedicated.ServerWatchdog field_13826() {
        throw new UnsupportedOperationException("CenturyBridge: Instance field field_13826 cannot be accessed without a receiver");
    }

    /** was net/minecraft/server/gui/MinecraftServerGui$2.field_13842Lnet/minecraft/server/gui/MinecraftServerGui; */
    public static net.minecraft.server.gui.MinecraftServerGui field_13842() {
        throw new UnsupportedOperationException("CenturyBridge: field_13842 is an instance field but the shim is static without the receiver parameter");
    }

    /** was net/minecraft/world/level/levelgen/structure/structures/StrongholdStructure/field_37817Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37817() {
        return net.minecraft.world.level.levelgen.structure.structures.StrongholdStructure.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/structures/SwampHutStructure/field_37818Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37818() {
        return net.minecraft.world.level.levelgen.structure.structures.SwampHutStructure.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/structures/WoodlandMansionStructure/field_37819Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37819() {
        return net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionStructure.CODEC.codec();
    }

    /** was net/minecraft/server/packs/PackResources/method_45178()Z */
    public static boolean method_45178(net.minecraft.server.packs.PackResources self) {
        return self instanceof net.minecraft.server.packs.VanillaPackResources;
    }

    /** was net/minecraft/world/level/levelgen/placement/NoiseBasedCountPlacement/field_24987Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24987() {
        return net.minecraft.world.level.levelgen.placement.NoiseBasedCountPlacement.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/structures/OceanRuinStructure/field_37807Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37807() {
        return net.minecraft.world.level.levelgen.structure.structures.OceanRuinStructure.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/structures/ShipwreckPieces/method_14834(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/world/level/levelgen/structure/StructurePieceAccessor;Lnet/minecraft/util/RandomSource;Z)V */
    public static void method_14834(net.minecraft.world.level.levelgen.structure.structures.ShipwreckPieces self, net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager a0, net.minecraft.core.BlockPos a1, net.minecraft.world.level.block.Rotation a2, net.minecraft.world.level.levelgen.structure.StructurePieceAccessor a3, net.minecraft.util.RandomSource a4, boolean a5) {
        self.addRandomPiece(a0, a1, a2, a3, a4, a5);
    }

    /** was net/minecraft/sounds/SoundEvents/field_14554Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14554() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.LLAMA_SWAG.value();
    }

    /** was net/minecraft/sounds/SoundEvents/field_14581Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14581() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_LEATHER.value();
    }

    /** was net/minecraft/sounds/SoundEvents/field_14684Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14684() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_TURTLE.value();
    }

    /** was net/minecraft/sounds/SoundEvents/field_14761Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14761() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_GOLD.value();
    }

    /** was net/minecraft/sounds/SoundEvents/field_14862Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14862() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_IRON.value();
    }

    /** was net/minecraft/sounds/SoundEvents/field_14883Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14883() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_GENERIC.value();
    }

    /** was net/minecraft/sounds/SoundEvents/field_14966Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14966() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_ELYTRA.value();
    }

    /** was net/minecraft/sounds/SoundEvents/field_15103Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_15103() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_DIAMOND.value();
    }

    /** was net/minecraft/sounds/SoundEvents/field_15191Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_15191() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_CHAIN.value();
    }

    /** was net/minecraft/sounds/SoundEvents/field_21866Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_21866() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_NETHERITE.value();
    }


    /** was net/minecraft/world/entity/monster/CrossbowAttackMob/method_18811(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/projectile/Projectile;F)V */
    public static void method_18811(net.minecraft.world.entity.monster.CrossbowAttackMob self, net.minecraft.world.entity.LivingEntity a0, net.minecraft.world.item.ItemStack a1, net.minecraft.world.entity.projectile.Projectile a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: CrossbowItem.shoot (method_18811) was removed in 1.20.5");
    }

    /** was net/minecraft/world/entity/monster/CrossbowAttackMob/method_24652(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/projectile/Projectile;FF)V */
    public static void method_24652(net.minecraft.world.entity.monster.CrossbowAttackMob self, net.minecraft.world.entity.LivingEntity a0, net.minecraft.world.entity.LivingEntity a1, net.minecraft.world.entity.projectile.Projectile a2, float a3, float a4) {
        self.performCrossbowAttack(a1, a3);
    }

    /** was net/minecraft/world/entity/monster/CrossbowAttackMob/method_24653(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/phys/Vec3;F)Lorg/joml/Vector3f; */
    public static org.joml.Vector3f method_24653(net.minecraft.world.entity.monster.CrossbowAttackMob self, net.minecraft.world.entity.LivingEntity a0, net.minecraft.world.phys.Vec3 a1, float a2) {
        double dx = a1.x - a0.getX();
        double dy = a1.y - a0.getEyeY();
        double dz = a1.z - a0.getZ();
        double dh = Math.sqrt(dx * dx + dz * dz);
        float pitch = (float) (-(Math.atan2(dy, dh) * 180.0 / Math.PI));
        float yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        return new org.joml.Vector3f(pitch, yaw, 0.0f);
    }

    /** was net/minecraft/world/level/levelgen/NoiseBasedChunkGenerator/field_24773Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24773() {
        return net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator.CODEC.codec();
    }

    /** was net/minecraft/world/entity/raid/Raider$ObtainRaidLeaderBannerGoal.field_16604Lnet/minecraft/world/entity/raid/Raider; */
    public static net.minecraft.world.entity.raid.Raider field_16604() {
        throw new UnsupportedOperationException("CenturyBridge: field_16604 has been removed without replacement");
    }

    /** was net/minecraft/world/entity/raid/Raider$HoldGroundAttackGoal.field_18882Lnet/minecraft/world/entity/raid/Raider; */
    public static net.minecraft.world.entity.raid.Raider field_18882() {
        throw new UnsupportedOperationException("CenturyBridge: Outer class reference field_18882 was removed and cannot be accessed");
    }

    /** was net/minecraft/world/entity/raid/Raider/method_20030(Lnet/minecraft/world/entity/raid/Raider;)F */
    public static float method_20030(net.minecraft.world.entity.raid.Raider self, net.minecraft.world.entity.raid.Raider a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_20030 was deleted with no direct replacement");
    }

    /** was net/minecraft/world/level/levelgen/structure/pools/FeaturePoolElement/field_24948Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24948() {
        return net.minecraft.world.level.levelgen.structure.pools.FeaturePoolElement.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/pools/EmptyPoolElement/field_24947Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24947() {
        return net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/pools/ListPoolElement/field_24950Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24950() {
        return net.minecraft.world.level.levelgen.structure.pools.ListPoolElement.CODEC.codec();
    }




    /** was net/minecraft/world/level/levelgen/structure/templatesystem/TagMatchTest/field_25014Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25014() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest.CODEC.codec();
    }

    /** was net/minecraft/world/inventory/GrindstoneMenu$2.field_16777Lnet/minecraft/world/inventory/GrindstoneMenu; */
    public static net.minecraft.world.inventory.GrindstoneMenu field_16777() {
        throw new UnsupportedOperationException("CenturyBridge: Synthetic outer field cannot be accessed without receiver");
    }

    /** was net/minecraft/world/inventory/GrindstoneMenu$3.field_16778Lnet/minecraft/world/inventory/GrindstoneMenu; */
    public static net.minecraft.world.inventory.GrindstoneMenu field_16778() {
        throw new UnsupportedOperationException("CenturyBridge: Instance field accessed without receiver");
    }

    /** was net/minecraft/world/level/levelgen/structure/templatesystem/AlwaysTrueTest/field_24994Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24994() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/templatesystem/BlockMatchTest/field_24999Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24999() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/templatesystem/BlockStateMatchTest/field_25001Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25001() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest.CODEC.codec();
    }


    /** was net/minecraft/world/level/levelgen/structure/templatesystem/RandomBlockStateMatchTest/field_25010Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25010() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockStateMatchTest.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/templatesystem/RandomBlockMatchTest/field_25009Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25009() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest.CODEC.codec();
    }


    /** was net/minecraft/world/level/levelgen/structure/templatesystem/RuleTestType/register(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Lnet/minecraft/world/level/levelgen/structure/templatesystem/RuleTestType; */
    public net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType register(java.lang.String a0, com.mojang.serialization.Codec a1) {
        throw new UnsupportedOperationException("CenturyBridge: RecipeSerializer registration using raw Codec is no longer supported; use MapCodec instead");
    }

    /** was net/minecraft/client/gui/font/providers/GlyphProviderDefinition/field_44801Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_44801() {
        return net.minecraft.client.gui.font.providers.GlyphProviderDefinition.MAP_CODEC.codec();
    }

    /** was net/minecraft/util/datafix/schemas/V1928/method_17997(Lcom/mojang/datafixers/schemas/Schema;)Lcom/mojang/datafixers/types/templates/TypeTemplate; */
    public static com.mojang.datafixers.types.templates.TypeTemplate method_17997(net.minecraft.util.datafix.schemas.V1928 self, com.mojang.datafixers.schemas.Schema a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_17997 was removed in 1.20.5 with no direct replacement");
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_16216Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_16216() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.CAT_MORNING_GIFT.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_16593Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_16593() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.PILLAGER_OUTPOST.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_16748Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_16748() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_PLAINS_HOUSE.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_16749Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_16749() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_TAIGA_HOUSE.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_16750Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_16750() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_TANNERY.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_16751Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_16751() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_CARTOGRAPHER.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_16752Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_16752() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_DESERT_HOUSE.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_16753Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_16753() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_SAVANNA_HOUSE.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_16754Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_16754() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_SNOWY_HOUSE.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_17009Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_17009() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_ARMORER.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_17010Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_17010() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_MASON.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_17011Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_17011() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_SHEPHERD.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_17012Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_17012() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_BUTCHER.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_17107Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_17107() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_TOOLSMITH.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_17108Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_17108() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_FLETCHER.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_17109Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_17109() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_TEMPLE.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_18007Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_18007() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_FISHER.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_19062Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_19062() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.ARMORER_GIFT.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_19063Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_19063() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.BUTCHER_GIFT.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_19064Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_19064() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.CARTOGRAPHER_GIFT.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_19065Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_19065() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.CLERIC_GIFT.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_19066Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_19066() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.FARMER_GIFT.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_19067Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_19067() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.FISHERMAN_GIFT.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_19068Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_19068() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.FLETCHER_GIFT.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_19069Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_19069() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.LEATHERWORKER_GIFT.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_19070Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_19070() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.LIBRARIAN_GIFT.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_19071Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_19071() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.MASON_GIFT.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_19072Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_19072() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.SHEPHERD_GIFT.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_19073Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_19073() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TOOLSMITH_GIFT.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_19074Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_19074() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.WEAPONSMITH_GIFT.identifier();
    }


    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_22402Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_22402() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.PIGLIN_BARTERING.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_24046Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_24046() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.BASTION_TREASURE.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_24047Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_24047() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.BASTION_OTHER.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_24048Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_24048() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.BASTION_BRIDGE.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_24049Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_24049() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.BASTION_HOGLIN_STABLE.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_24050Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_24050() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.RUINED_PORTAL.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_251Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_251() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.BURIED_TREASURE.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_266Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_266() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.FISHING_JUNK.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_274Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_274() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.END_CITY_TREASURE.identifier();
    }


    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_300Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_300() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.UNDERWATER_RUIN_BIG.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_353Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_353() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.FISHING.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_356Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_356() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.SIMPLE_DUNGEON.identifier();
    }


    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_38438Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_38438() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.ANCIENT_CITY.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_38439Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_38439() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.ANCIENT_CITY_ICE_BOX.identifier();
    }



    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_397Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_397() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.UNDERWATER_RUIN_SMALL.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_43353Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_43353() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_43354Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_43354() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_43356Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_43356() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_43357Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_43357() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_434Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_434() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.VILLAGE_WEAPONSMITH.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_44648Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_44648() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_44649Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_44649() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_44748Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_44748() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.SNIFFER_DIGGING.identifier();
    }


    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_472Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_472() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.ABANDONED_MINESHAFT.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_47415Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_47415() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_REWARD.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_47416Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_47416() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_SUPPLY.registry();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_47417Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_47417() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_CORRIDOR.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_47418Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_47418() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_INTERSECTION.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_47419Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_47419() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_INTERSECTION_BARREL.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_47420Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_47420() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_ENTRANCE.registry();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_47421Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_47421() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_CORRIDOR_DISPENSER.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_47422Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_47422() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_CHAMBER_DISPENSER.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_47423Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_47423() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.SPAWNER_TRIAL_CHAMBER_KEY.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_47424Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_47424() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.SPAWNER_TRIAL_CHAMBER_CONSUMABLES.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_47425Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_47425() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_WATER_DISPENSER.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_47426Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_47426() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.TRIAL_CHAMBERS_CORRIDOR_POT.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_484Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_484() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.WOODLAND_MANSION.identifier();
    }



    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_615Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_615() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.NETHER_BRIDGE.identifier();
    }


    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_662Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_662() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.IGLOO_CHEST.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_665Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_665() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.SHIPWRECK_TREASURE.identifier();
        // CB-CONFIDENCY: high -- SHIPWRECK_TREASURE changed from Identifier to RegistryKey; identifier() on net.minecraft.resources.ResourceKey returns the Identifier (net.minecraft.resources.Identifier), preserving the old value exactly.
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_683Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_683() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.STRONGHOLD_LIBRARY.identifier();
    }



    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_751Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_751() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.JUNGLE_TEMPLE_DISPENSER.identifier();
    }


    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_795Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_795() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.FISHING_FISH.registry();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_800Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_800() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.STRONGHOLD_CROSSING.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_803Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_803() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.JUNGLE_TEMPLE.identifier();
    }



    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_841Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_841() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.SHIPWRECK_MAP.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_842Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_842() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.STRONGHOLD_CORRIDOR.identifier();
    }


    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_850Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_850() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.SPAWN_BONUS_CHEST.registry();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_854Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_854() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.FISHING_TREASURE.registry();
    }



    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_880Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_880() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.SHIPWRECK_SUPPLY.identifier();
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables/field_885Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_885() {
        return net.minecraft.world.level.storage.loot.BuiltInLootTables.DESERT_PYRAMID.identifier();
    }

    /** was net/minecraft/world/entity/animal/fox/Fox$FoxBreedGoal.field_17973Lnet/minecraft/world/entity/animal/fox/Fox; */
    public static net.minecraft.world.entity.animal.fox.Fox field_17973() {
        throw new UnsupportedOperationException("CenturyBridge: field_17973 (net.minecraft.world.entity.animal.fox.Fox constant on net.minecraft.world.entity.animal.fox.Fox$class_4024) was deleted with no direct replacement and its value cannot be reconstructed from the new API surface");
        // CB-CONFIDENCY: high -- the field is explicitly marked deleted with no direct replacement, and the new API surface provides no equivalent constant or accessor to reconstruct the held net.minecraft.world.entity.animal.fox.Fox value
    }

    /** was net/minecraft/world/entity/animal/fox/Fox$FoxFollowParentGoal.field_18103Lnet/minecraft/world/entity/animal/fox/Fox; */
    public static net.minecraft.world.entity.animal.fox.Fox field_18103() {
        throw new UnsupportedOperationException("CenturyBridge: field_18103 (static net.minecraft.world.entity.animal.fox.Fox on net.minecraft.world.entity.animal.fox.Fox$class_4052) was deleted in 1.20.5 with no replacement; the new class exposes no method returning net.minecraft.world.entity.animal.fox.Fox");
    }

    /** was net/minecraft/core/GlobalPos/method_19442()Lnet/minecraft/resources/ResourceKey; */
    public static net.minecraft.resources.ResourceKey method_19442(net.minecraft.core.GlobalPos self) {
        return self.dimension();
    }

    /** was net/minecraft/core/GlobalPos/method_19446()Lnet/minecraft/core/BlockPos; */
    public static net.minecraft.core.BlockPos method_19446(net.minecraft.core.GlobalPos self) {
        return self.pos();
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen.field_19747I */
    public static int field_19747() {
        throw new UnsupportedOperationException("CenturyBridge: com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen.field_19747 was a deleted static int constant with no direct replacement in 1.20.5; its value cannot be inferred from the new API surface");
        // CB-CONFIDENCE: medium -- the field is genuinely gone with no equivalent, but a tombstone is the honest choice since the original constant's value is not recoverable from the new API surface
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab.field_19970I */
    public static int field_19970() {
        throw new UnsupportedOperationException("CenturyBridge: com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab.field_19970 was a mutable per-instance animation tick counter on EnchantmentScreen, removed in 1.20.5; no static constant equivalent exists");
        // CB-CONFIDENCE: high -- field_19970 was mutable instance state (tick counter incremented in aM_), not a constant; a static shim with no receiver cannot reproduce per-instance mutable state
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab.field_26498Lnet/minecraft/network/chat/Component; */
    public static net.minecraft.network.chat.Component field_26498() {
        throw new UnsupportedOperationException("CenturyBridge: field_26498 was deleted in 1.20.5 with no direct replacement");
        // CB-CONFIDENCE: high -- the prompt explicitly states "deleted -- no direct replacement"; QUESTION_TITLE is an instance field of the same type but cannot be confirmed as the semantic equivalent of the old static field
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab.field_26500Lnet/minecraft/network/chat/Component; */
    public static net.minecraft.network.chat.Component field_26500() {
        throw new UnsupportedOperationException("CenturyBridge: field_26500 was deleted in 1.20.5 with no direct replacement");
        // CB-CONFIDENCE: high -- field explicitly deleted, no equivalent in new API surface; tombstone is the correct choice
    }






    /** was net/minecraft/gametest/framework/GameTestBatch/method_22153(Lnet/minecraft/server/level/ServerLevel;)V */
    public static void method_22153(net.minecraft.gametest.framework.GameTestBatch self, net.minecraft.server.level.ServerLevel a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_22153(ServerWorld) was deleted in 1.20.5 with no direct replacement; net.minecraft.gametest.framework.GameTestBatch is now a record (String, Collection, Consumer, Consumer) with no ServerWorld-interacting methods");
    }

    /** was net/minecraft/gametest/framework/GameTestBatch/method_22154()Ljava/util/Collection; */
    public static java.util.Collection method_22154(net.minecraft.gametest.framework.GameTestBatch self) {
        return self.gameTestInfos();
    }

    /** was net/minecraft/gametest/framework/GameTestListener/testPassed(Lnet/minecraft/gametest/framework/GameTestInfo;)V */
    public void testPassed(net.minecraft.gametest.framework.GameTestInfo a0) {
        throw new UnsupportedOperationException("CenturyBridge: testPassed now requires a net.minecraft.gametest.framework.GameTestRunner raid-context parameter whose constructor needs server world, collections, and nested types unavailable from net.minecraft.gametest.framework.GameTestInfo alone");
    }



    /** was net/minecraft/world/level/levelgen/feature/foliageplacers/AcaciaFoliagePlacer/field_24926Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24926() {
        return net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/foliageplacers/BlobFoliagePlacer/field_24927Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24927() {
        return net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/foliageplacers/PineFoliagePlacer/field_24935Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24935() {
        return net.minecraft.world.level.levelgen.feature.foliageplacers.PineFoliagePlacer.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/foliageplacers/SpruceFoliagePlacer/field_24936Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24936() {
        return net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/stateproviders/RotatedBlockProvider/field_24944Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24944() {
        return net.minecraft.world.level.levelgen.feature.stateproviders.RotatedBlockProvider.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/stateproviders/SimpleStateProvider/field_24945Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24945() {
        return net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/treedecorators/CocoaDecorator/field_24959Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24959() {
        return net.minecraft.world.level.levelgen.feature.treedecorators.CocoaDecorator.CODEC.codec();
    }

    /** was net/minecraft/client/renderer/Sheets/field_28253Ljava/util/Map; */
    public static java.util.Map field_28253() {
        throw new UnsupportedOperationException("CenturyBridge: SHIELD_MATERIALS was a memoized render-layer cache map removed in 1.20.5; replaced by direct method calls (e.g. getDecoratedPotMaterial) with incompatible key types");
        // CB-CONFIDENCE: medium -- field was a Map on TexturedRenderLayers deleted with no replacement; the memoized-cache nature means it cannot be rebuilt as a constant, and the new API uses different key types (RegistryKey vs Identifier)
    }

    /** was net/minecraft/world/level/biome/MultiNoiseBiomeSource/field_24719Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24719() {
        return net.minecraft.world.level.biome.MultiNoiseBiomeSource.CODEC.codec();
    }

    /** was net/minecraft/world/level/block/RespawnAnchorBlock$1.field_25406Lnet/minecraft/world/level/block/RespawnAnchorBlock; */
    public static net.minecraft.world.level.block.RespawnAnchorBlock field_25406() {
        throw new UnsupportedOperationException("CenturyBridge: field_25406 was the synthetic outer instance reference (this$0) of net.minecraft.world.level.block.RespawnAnchorBlock$1, no longer mapped in 1.20.5; cannot reconstruct from a parameterless static shim without an instance");
    }

    /** was net/minecraft/world/level/levelgen/structure/templatesystem/AxisAlignedLinearPosTest/field_24995Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24995() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.AxisAlignedLinearPosTest.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/templatesystem/LinearPosTest/field_25004Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25004() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.LinearPosTest.CODEC.codec();
    }

    /** was net/minecraft/world/entity/ai/attributes/AttributeMap/method_26842(Lnet/minecraft/world/entity/ai/attributes/Attribute;)Lnet/minecraft/world/entity/ai/attributes/AttributeInstance; */
    public static net.minecraft.world.entity.ai.attributes.AttributeInstance method_26842(net.minecraft.world.entity.ai.attributes.AttributeMap self, net.minecraft.world.entity.ai.attributes.Attribute a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_26842(ServerPlayerEntity) has no equivalent in 1.20.5; boss bar player management was reworked to use RegistryKey-based lookups (getInstance)");
    }

    /** was net/minecraft/world/entity/ai/attributes/AttributeMap/method_26854(Lcom/google/common/collect/Multimap;)V */
    public static void method_26854(net.minecraft.world.entity.ai.attributes.AttributeMap self, com.google.common.collect.Multimap a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_26854(Multimap) was removed in 1.20.5; AttributeContainer no longer accepts a Multimap of modifiers -- the attribute modifier system was reworked to RegistryEntry-keyed queries with no bulk-apply equivalent");
    }

    /** was net/minecraft/world/entity/ai/attributes/Attributes/field_23716Lnet/minecraft/world/entity/ai/attributes/Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23716() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH.value();
    }

    /** was net/minecraft/world/entity/ai/attributes/Attributes/field_23718Lnet/minecraft/world/entity/ai/attributes/Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23718() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE.value();
    }

    /** was net/minecraft/world/entity/ai/attributes/Attributes/field_23719Lnet/minecraft/world/entity/ai/attributes/Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23719() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED.value();
        // CB-CONFIDENCE: high -- MOVEMENT_SPEED is now a RegistryEntry<EntityAttribute>; value() unwraps to the value, which is the old EntityAttribute
    }

    /** was net/minecraft/world/entity/ai/attributes/Attributes/field_23720Lnet/minecraft/world/entity/ai/attributes/Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23720() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.FLYING_SPEED.value();
    }

    /** was net/minecraft/world/entity/ai/attributes/Attributes/field_23723Lnet/minecraft/world/entity/ai/attributes/Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23723() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED.value();
    }

    /** was net/minecraft/world/entity/ai/attributes/Attributes/field_23724Lnet/minecraft/world/entity/ai/attributes/Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23724() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.ARMOR.value();
    }

    /** was net/minecraft/world/entity/ai/attributes/Attributes/field_23725Lnet/minecraft/world/entity/ai/attributes/Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23725() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS.value();
    }

    /** was net/minecraft/world/entity/ai/attributes/Attributes/field_23727Lnet/minecraft/world/entity/ai/attributes/Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23727() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.SPAWN_REINFORCEMENTS_CHANCE.value();
    }

    /** was net/minecraft/world/entity/ai/attributes/Attributes/field_23728Lnet/minecraft/world/entity/ai/attributes/Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23728() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.JUMP_STRENGTH.value();
    }

    /** was net/minecraft/world/entity/ai/attributes/Attributes/field_45124Lnet/minecraft/world/entity/ai/attributes/Attribute; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_45124() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.MAX_ABSORPTION.value();
    }

    /** was net/minecraft/world/level/levelgen/feature/trunkplacers/ForkingTrunkPlacer/field_24968Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24968() {
        return net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/trunkplacers/StraightTrunkPlacer/field_24971Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24971() {
        return net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer.CODEC.codec();
    }


    /** was net/minecraft/world/level/levelgen/structure/structures/RuinedPortalStructure/field_37812Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37812() {
        return net.minecraft.world.level.levelgen.structure.structures.RuinedPortalStructure.CODEC.codec();
    }



    /** was net/minecraft/world/level/levelgen/feature/featuresize/ThreeLayersFeatureSize/field_24924Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24924() {
        return net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/featuresize/TwoLayersFeatureSize/field_24925Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24925() {
        return net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/foliageplacers/DarkOakFoliagePlacer/field_24929Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24929() {
        return net.minecraft.world.level.levelgen.feature.foliageplacers.DarkOakFoliagePlacer.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/foliageplacers/FancyFoliagePlacer/field_24930Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24930() {
        return net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/foliageplacers/MegaJungleFoliagePlacer/field_24933Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24933() {
        return net.minecraft.world.level.levelgen.feature.foliageplacers.MegaJungleFoliagePlacer.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/trunkplacers/DarkOakTrunkPlacer/field_24966Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24966() {
        return net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/trunkplacers/FancyTrunkPlacer/field_24967Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24967() {
        return net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/trunkplacers/GiantTrunkPlacer/field_24969Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24969() {
        return net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer.CODEC.codec();
    }

    /** was net/minecraft/world/level/storage/LevelData/method_144()I */
    public static int method_144(net.minecraft.world.level.storage.LevelData self) {
        throw new UnsupportedOperationException("CenturyBridge: getSeaLevel (method_144) was removed from net.minecraft.world.level.storage.LevelData in 1.20.5 with no direct replacement; sea level is no longer exposed on WorldProperties");
    }



    /** was net/minecraft/world/level/storage/WritableLevelData/method_27416(I)V */
    public static void method_27416(net.minecraft.world.level.storage.WritableLevelData self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_27416(int) has no 1.20.5 equivalent; the only remaining method setSpawn requires a BlockPos and float that cannot be derived from the int argument");
    }

    /** was net/minecraft/world/level/storage/WritableLevelData/method_27417(I)V */
    public static void method_27417(net.minecraft.world.level.storage.WritableLevelData self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_27417(int) was removed in 1.20.5 with no direct replacement; remaining setSpawn(BlockPos, float) is semantically incompatible -- cannot derive BlockPos+float from a single int");
    }

    /** was net/minecraft/world/level/storage/WritableLevelData/method_27419(I)V */
    public static void method_27419(net.minecraft.world.level.storage.WritableLevelData self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_27419(int) was deleted with no direct replacement; the only remaining setSpawn requires a BlockPos and float, neither of which can be derived from the single int argument, so the old contract cannot be faithfully expressed");
    }

    /** was net/minecraft/client/gui/screens/debug/GameModeSwitcherScreen$1.field_24574[I */
    public static int[] field_24574() {
        throw new UnsupportedOperationException("CenturyBridge: field_24574 was deleted in 1.20.5 with no direct replacement; field_24575 is a distinct field (stable intermediary names), so the original constant value cannot be faithfully reconstructed");
    }


    /** was net/minecraft/world/level/levelgen/placement/InSquarePlacement/field_35727Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35727() {
        return net.minecraft.world.level.levelgen.placement.InSquarePlacement.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/placement/CountOnEveryLayerPlacement/field_35716Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35716() {
        return net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement.CODEC.codec();
    }

    /** was net/minecraft/core/RegistryAccess$1FrozenAccess.field_40586Lnet/minecraft/core/RegistryAccess; */
    public static net.minecraft.core.RegistryAccess field_40586() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.core.RegistryAccess$class_7781.field_40586 (a default net.minecraft.core.RegistryAccess instance) was removed in 1.20.5 with no direct replacement; the new class only exposes a constructor requiring an existing net.minecraft.core.RegistryAccess, so the old singleton cannot be faithfully reconstructed");
    }

    /** was net/minecraft/world/item/BundleItem/field_30857I */
    public static int field_30857() {
        return 3;
    }

    /** was net/minecraft/world/level/storage/loot/functions/SequenceFunction/field_45834Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_45834() {
        return net.minecraft.world.level.storage.loot.functions.SequenceFunction.INLINE_CODEC;
    }

    /** was net/minecraft/world/level/gameevent/BlockPositionSource/field_28137Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_28137() {
        return net.minecraft.world.level.gameevent.BlockPositionSource.CODEC.codec();
    }

    /** was net/minecraft/world/level/gameevent/BlockPositionSource/field_28138Lnet/minecraft/core/BlockPos; */
    public static net.minecraft.core.BlockPos field_28138() {
        return new net.minecraft.core.BlockPos(0, 0, 0);
    }

    /** was net/minecraft/world/level/gameevent/GameEventListenerRegistry/visitInRangeListeners(Lnet/minecraft/world/level/gameevent/GameEvent;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/gameevent/GameEvent$Context;Lnet/minecraft/world/level/gameevent/GameEventListenerRegistry$ListenerVisitor;)Z */
    public boolean visitInRangeListeners(net.minecraft.world.level.gameevent.GameEvent a0, net.minecraft.world.phys.Vec3 a1, net.minecraft.world.level.gameevent.GameEvent.Context a2, net.minecraft.world.level.gameevent.GameEventListenerRegistry.ListenerVisitor a3) {
        return ((net.minecraft.world.level.gameevent.GameEventListenerRegistry) (Object) this).visitInRangeListeners(net.minecraft.core.Holder.direct(a0), a1, a2, a3);
    }

    /** was net/minecraft/world/level/gameevent/GameEventListener/handleGameEvent(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/gameevent/GameEvent;Lnet/minecraft/world/level/gameevent/GameEvent$Context;Lnet/minecraft/world/phys/Vec3;)Z */
    public boolean handleGameEvent(net.minecraft.server.level.ServerLevel a0, net.minecraft.world.level.gameevent.GameEvent a1, net.minecraft.world.level.gameevent.GameEvent.Context a2, net.minecraft.world.phys.Vec3 a3) {
        return ((net.minecraft.world.level.gameevent.GameEventListener)(Object)this).handleGameEvent(a0, net.minecraft.core.Holder.direct(a1), a2, a3);
    }

    /** was net/minecraft/world/level/gameevent/PositionSourceType/method_32960(Lnet/minecraft/network/FriendlyByteBuf;Lnet/minecraft/world/level/gameevent/PositionSource;)V */
    public static void method_32960(net.minecraft.world.level.gameevent.PositionSourceType self, net.minecraft.network.FriendlyByteBuf a0, net.minecraft.world.level.gameevent.PositionSource a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_32960 wrote TextContent to PacketByteBuf in 1.20.1's binary format; 1.20.5 replaced this with MapCodec serialization (codec) using a different on-wire format");
    }




    /** was net/minecraft/world/level/levelgen/placement/HeightmapPlacement/field_29265Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_29265() {
        return net.minecraft.world.level.levelgen.placement.HeightmapPlacement.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/foliageplacers/RandomSpreadFoliagePlacer/field_29297Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_29297() {
        return net.minecraft.world.level.levelgen.feature.foliageplacers.RandomSpreadFoliagePlacer.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/trunkplacers/BendingTrunkPlacer/field_29306Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_29306() {
        return net.minecraft.world.level.levelgen.feature.trunkplacers.BendingTrunkPlacer.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/placement/SurfaceWaterDepthFilter/field_29323Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_29323() {
        return net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter.CODEC.codec();
    }




    /** was net/minecraft/world/level/block/LevelEvent/field_47339I */
    public static int field_47339() {
        throw new UnsupportedOperationException("CenturyBridge: field_47339 was deleted in 1.20.5 with no direct replacement; its int value cannot be reconstructed from the new API surface");
    }

    /** was net/minecraft/world/level/levelgen/heightproviders/BiasedToBottomHeight/field_31531Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_31531() {
        return net.minecraft.world.level.levelgen.heightproviders.BiasedToBottomHeight.CODEC.codec();
    }

    /** was net/minecraft/gametest/framework/GameTestServer$1.field_33163Lnet/minecraft/gametest/framework/GameTestServer; */
    public static net.minecraft.gametest.framework.GameTestServer field_33163() {
        throw new UnsupportedOperationException("CenturyBridge: field_33163 (type net.minecraft.gametest.framework.GameTestServer) in net.minecraft.gametest.framework.GameTestServer$1 was deleted in 1.20.5 with no direct replacement; cannot reconstruct a net.minecraft.gametest.framework.GameTestServer constant without constructor or factory information for the outer type");
    }



    /** was net/minecraft/world/level/levelgen/heightproviders/TrapezoidHeight/field_33522Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_33522() {
        return net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/heightproviders/VeryBiasedToBottomHeight/field_33527Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_33527() {
        return net.minecraft.world.level.levelgen.heightproviders.VeryBiasedToBottomHeight.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/stateproviders/NoiseThresholdProvider/field_34713Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_34713() {
        return net.minecraft.world.level.levelgen.feature.stateproviders.NoiseThresholdProvider.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/placement/SurfaceRelativeThresholdFilter/field_34721Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_34721() {
        return net.minecraft.world.level.levelgen.placement.SurfaceRelativeThresholdFilter.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/placement/BlockPredicateFilter/field_35075Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35075() {
        return net.minecraft.world.level.levelgen.placement.BlockPredicateFilter.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/blockpredicates/WouldSurvivePredicate/field_35160Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35160() {
        return net.minecraft.world.level.levelgen.blockpredicates.WouldSurvivePredicate.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/SurfaceSystem$1.field_35297Lnet/minecraft/world/level/levelgen/SurfaceSystem; */
    public static net.minecraft.world.level.levelgen.SurfaceSystem field_35297() {
        throw new UnsupportedOperationException("CenturyBridge: field_35297 was the synthetic this$0 outer instance reference of net.minecraft.world.level.levelgen.SurfaceSystem$1; it is an instance field and cannot be returned from a parameterless static helper");
    }


    /** was net/minecraft/world/level/levelgen/placement/RandomOffsetPlacement/field_35419Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35419() {
        return net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/heightproviders/WeightedListHeight/field_35712Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35712() {
        return net.minecraft.world.level.levelgen.heightproviders.WeightedListHeight.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/placement/BiomeFilter/field_35714Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35714() {
        return net.minecraft.world.level.levelgen.placement.BiomeFilter.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/placement/CountPlacement/field_35718Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35718() {
        return net.minecraft.world.level.levelgen.placement.CountPlacement.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/placement/EnvironmentScanPlacement/field_35720Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35720() {
        return net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/placement/RarityFilter/field_35752Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35752() {
        return net.minecraft.world.level.levelgen.placement.RarityFilter.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/placement/RandomSpreadStructurePlacement/field_36420Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_36420() {
        return net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement.CODEC.codec();
    }





    /** was net/minecraft/world/level/levelgen/RandomState$1.field_40361Lnet/minecraft/world/level/levelgen/RandomState; */
    public static net.minecraft.world.level.levelgen.RandomState field_40361() {
        throw new UnsupportedOperationException("CenturyBridge: field_40361 was an instance field holding the enclosing net.minecraft.world.level.levelgen.RandomState reference; cannot reconstruct from a static context with no receiver");
    }

    /** was net/minecraft/world/entity/monster/warden/Warden$1.field_39306Lnet/minecraft/world/entity/monster/warden/Warden; */
    public static net.minecraft.world.entity.monster.warden.Warden field_39306() {
        return null;
    }

    /** was net/minecraft/world/entity/vehicle/ContainerEntity/getContainerLootTable()Lnet/minecraft/resources/Identifier; */
    public net.minecraft.resources.Identifier getContainerLootTable() {
        return ((net.minecraft.world.entity.vehicle.ContainerEntity) (Object) this).getContainerLootTable().identifier();
    }



    /** was net/minecraft/util/datafix/schemas/V3083/method_42645(Lcom/mojang/datafixers/schemas/Schema;Ljava/util/Map;Ljava/lang/String;)V */
    public static void method_42645(net.minecraft.util.datafix.schemas.V3083 self, com.mojang.datafixers.schemas.Schema a0, java.util.Map a1, java.lang.String a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_42645(Schema, Map, String) was a per-name type registration helper deleted in 1.20.5 with no direct replacement; new net.minecraft.util.datafix.schemas.V3083 only exposes bulk registerEntities(Schema) returning a new Map, with no per-name registration equivalent");
    }

    /** was net/minecraft/world/level/levelgen/feature/treedecorators/AttachedToLeavesDecorator/field_38783Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_38783() {
        return net.minecraft.world.level.levelgen.feature.treedecorators.AttachedToLeavesDecorator.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/trunkplacers/UpwardsBranchingTrunkPlacer/field_38793Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_38793() {
        return net.minecraft.world.level.levelgen.feature.trunkplacers.UpwardsBranchingTrunkPlacer.CODEC.codec();
    }

    /** was net/minecraft/commands/arguments/ArgumentSignatures$Entry.comp_914Lnet/minecraft/network/chat/MessageSignature; */
    public static net.minecraft.network.chat.MessageSignature comp_914() {
        throw new UnsupportedOperationException("CenturyBridge: signature was a static field on net.minecraft.commands.arguments.ArgumentSignatures$class_7599, deleted in 1.20.5; new signature() is an instance method with no static equivalent");
    }

    /** was net/minecraft/client/multiplayer/chat/LoggedChatMessage$Player.field_40809Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_40809() {
        return net.minecraft.client.multiplayer.chat.LoggedChatMessage.Player.CODEC.codec();
    }

    /** was net/minecraft/client/multiplayer/chat/LoggedChatMessage$System.field_40810Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_40810() {
        return net.minecraft.client.multiplayer.chat.LoggedChatMessage.System.CODEC.codec();
    }

    /** was net/minecraft/client/renderer/GameRenderer/field_3996[Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier[] field_3996() {
        throw new UnsupportedOperationException("CenturyBridge: field_3996 (SHADERS_LOCATIONS) was removed in 1.20.5 when the core shader system was reworked; the new API provides individual com.mojang.blaze3d.opengl.GlProgram shader accessors but no equivalent identifier array");
    }

    /** was net/minecraft/world/entity/animal/camel/Camel/field_40131Lnet/minecraft/world/item/crafting/Ingredient; */
    public static net.minecraft.world.item.crafting.Ingredient field_40131() {
        throw new UnsupportedOperationException("CenturyBridge: field_40131 (Ingredient) was deleted in 1.20.5 with no direct replacement; cannot reconstruct the allay duplication ingredient as a static constant");
        // CB-CONFIDENCE: high -- the field is explicitly deleted with no direct replacement, and the new API surface contains no Ingredient field or factory to rebuild it from
    }

    /** was net/minecraft/world/item/ItemStackLinkedSet/method_47573(Lnet/minecraft/world/item/ItemStack;)I */
    public static int method_47573(net.minecraft.world.item.ItemStackLinkedSet self, net.minecraft.world.item.ItemStack a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.item.ItemStackLinkedSet.method_47573(ItemStack) was removed in 1.20.5 with no direct replacement; the remaining API (createTypeAndComponentsSet returning Set) cannot query a specific item's cooldown value");
    }

    /** was net/minecraft/world/entity/ai/behavior/declarative/BehaviorBuilder$Instance$1.field_41004Lnet/minecraft/world/entity/ai/behavior/declarative/BehaviorBuilder$Instance; */
    public static net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder.Instance field_41004() {
        throw new UnsupportedOperationException("CenturyBridge: field_41004 was the synthetic this$0 enclosing instance reference in net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder$class_7900$1; it is an instance field holding the enclosing net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder$class_7900 and cannot be reconstructed as a static constant");
    }

    /** was net/minecraft/world/entity/ai/behavior/declarative/BehaviorBuilder$Instance$3.field_41011Lnet/minecraft/world/entity/ai/behavior/declarative/BehaviorBuilder$Instance; */
    public static net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder.Instance field_41011() {
        throw new UnsupportedOperationException("CenturyBridge: field_41011 was the synthetic this$0 reference to the enclosing class_7900 instance in anonymous class net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder$class_7900$3; it is per-instance and cannot be reconstructed as a static constant");
    }

    /** was net/minecraft/core/registries/BuiltInRegistries/field_41165Lnet/minecraft/core/Registry; */
    public static net.minecraft.core.Registry field_41165() {
        throw new UnsupportedOperationException("CenturyBridge: Registries.field_41165 was removed in 1.20.5 with no direct replacement");
    }

    /** was net/minecraft/client/renderer/texture/atlas/sources/DirectoryLister/field_41399Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_41399() {
        return net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister.MAP_CODEC.codec();
    }

    /** was net/minecraft/client/renderer/texture/atlas/sources/SingleFile/field_41402Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_41402() {
        return net.minecraft.client.renderer.texture.atlas.sources.SingleFile.MAP_CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/foliageplacers/CherryFoliagePlacer/field_42841Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_42841() {
        return net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/trunkplacers/CherryTrunkPlacer/field_42848Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_42848() {
        return net.minecraft.world.level.levelgen.feature.trunkplacers.CherryTrunkPlacer.CODEC.codec();
    }

    /** was net/minecraft/world/ticks/ContainerSingleItem/getContainerBlockEntity()Lnet/minecraft/world/level/block/entity/BlockEntity; */
    public net.minecraft.world.level.block.entity.BlockEntity getContainerBlockEntity() {
        return ((net.minecraft.world.ticks.ContainerSingleItem.BlockContainerSingleItem) (Object) this).getContainerBlockEntity();
    }


    /** was net/minecraft/world/level/levelgen/structure/templatesystem/rule/blockentity/AppendStatic/field_43339Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_43339() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.AppendStatic.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/templatesystem/rule/blockentity/Clear/field_43341Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_43341() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.Clear.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/templatesystem/rule/blockentity/Passthrough/field_43344Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_43344() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.Passthrough.CODEC.codec();
    }

    /** was net/minecraft/world/level/gameevent/vibrations/VibrationSystem$User.onReceiveVibration(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;F)V */
    public void onReceiveVibration(net.minecraft.server.level.ServerLevel a0, net.minecraft.core.BlockPos a1, net.minecraft.world.level.gameevent.GameEvent a2, net.minecraft.world.entity.Entity a3, net.minecraft.world.entity.Entity a4, float a5) {
        ((net.minecraft.world.level.gameevent.vibrations.VibrationSystem.User) (Object) this).onReceiveVibration(a0, a1, net.minecraft.core.Holder.direct(a2), a3, a4, a5);
    }

    /** was net/minecraft/world/level/gameevent/vibrations/VibrationSystem/RESONANCE_EVENTS[Lnet/minecraft/world/level/gameevent/GameEvent; */
    public static net.minecraft.world.level.gameevent.GameEvent[] field_44638() {
        return net.minecraft.world.level.gameevent.vibrations.VibrationSystem.RESONANCE_EVENTS.toArray(new net.minecraft.world.level.gameevent.GameEvent[0]);
    }

    /** was net/minecraft/client/resources/metadata/gui/GuiSpriteScaling$Tile.field_45655Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_45655() {
        return (com.mojang.serialization.Codec) net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.Tile.CODEC;
    }

    /** was net/minecraft/network/ProtocolSwapHandler/method_53011(Lio/netty/util/Attribute;Lnet/minecraft/network/protocol/Packet;)V */
    public static void method_53011(net.minecraft.network.ProtocolSwapHandler self, io.netty.util.Attribute a0, net.minecraft.network.protocol.Packet a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_53011(Attribute, Packet) was deleted; new API methods require ChannelHandlerContext which cannot be derived from an io.netty.util.Attribute");
    }

    /** was net/minecraft/network/protocol/common/custom/CustomPacketPayload/method_53028(Lnet/minecraft/network/FriendlyByteBuf;)V */
    public static void method_53028(net.minecraft.network.protocol.common.custom.CustomPacketPayload self, net.minecraft.network.FriendlyByteBuf a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.network.protocol.common.custom.CustomPacketPayload.method_53028(PacketByteBuf) was deleted in 1.20.5 with no direct replacement; the new net.minecraft.network.protocol.common.custom.CustomPacketPayload API exposes no PacketByteBuf serialization path");
    }

    /** was net/minecraft/world/RandomizableContainer/getLootTable()Lnet/minecraft/resources/Identifier; */
    public net.minecraft.resources.Identifier getLootTable() {
        return ((net.minecraft.world.RandomizableContainer) (Object) this).getLootTable().identifier();
    }

    /** was net/minecraft/world/entity/monster/breeze/Breeze/method_55006()Lnet/minecraft/world/entity/monster/breeze/Breeze; */
    public static net.minecraft.world.entity.monster.breeze.Breeze method_55006(net.minecraft.world.entity.monster.breeze.Breeze self) {
        return self.resetJumpTrail();
    }

    /** was net/minecraft/world/entity/monster/breeze/Breeze/method_55011(Lnet/minecraft/world/phys/Vec3;)Z */
    public static boolean method_55011(net.minecraft.world.entity.monster.breeze.Breeze self, net.minecraft.world.phys.Vec3 a0) {
        return self.withinInnerCircleRange(a0);
    }

    /** was net/minecraft/world/entity/monster/breeze/Breeze/method_55012(Lnet/minecraft/world/phys/Vec3;)Z */
    public static boolean method_55012(net.minecraft.world.entity.monster.breeze.Breeze self, net.minecraft.world.phys.Vec3 a0) {
        return self.withinInnerCircleRange(a0);
    }

    /** was net/minecraft/world/level/block/entity/trialspawner/PlayerDetector/field_47356Lnet/minecraft/world/level/block/entity/trialspawner/PlayerDetector; */
    public static net.minecraft.world.level.block.entity.trialspawner.PlayerDetector field_47356() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.block.entity.trialspawner.PlayerDetector.field_47356 was deleted in 1.20.5; remaining constants SHEEP/NO_CREATIVE_PLAYERS/INCLUDING_CREATIVE_PLAYERS are not equivalent");
    }

    /** was net/minecraft/world/level/pathfinder/NodeEvaluator/field_20622Lnet/minecraft/world/level/PathNavigationRegion; */
    public static net.minecraft.world.level.PathNavigationRegion field_20622() {
        throw new UnsupportedOperationException("CenturyBridge: field_20622 (net.minecraft.world.level.PathNavigationRegion HitResult) was deleted from net.minecraft.world.level.pathfinder.NodeEvaluator with no direct replacement; it was a mutable instance field that cannot be reconstructed as a constant expression");
        // CB-CONFIDENCE: high -- the field was explicitly deleted with no replacement in the new API surface, and a HitResult is a mutable runtime object that cannot be expressed as a constant
    }

    /** was net/minecraft/network/chat/numbers/NumberFormatTypes/method_55467(Lnet/minecraft/network/FriendlyByteBuf;)Lnet/minecraft/network/chat/numbers/NumberFormat; */
    public static net.minecraft.network.chat.numbers.NumberFormat method_55467(net.minecraft.network.chat.numbers.NumberFormatTypes self, net.minecraft.network.FriendlyByteBuf a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.network.chat.numbers.NumberFormatTypes.method_55467(PacketByteBuf)->net.minecraft.network.chat.numbers.NumberFormat was removed in 1.20.5 with no direct replacement; the new net.minecraft.network.chat.numbers.NumberFormatTypes API has no method accepting a PacketByteBuf, and the old instance-method semantics (using receiver state to drive deserialization) cannot be expressed via the static Codec/MapCodec fields");
    }




























    /** was net/minecraft/world/damagesource/DamageSource/method_48790()Z */
    public static boolean method_48790(net.minecraft.world.damagesource.DamageSource self) {
        return self.getDirectEntity() == self.getEntity();
    }

    /** was net/minecraft/world/entity/Entity/field_5972I */
    public static int field_5972() {
        return 1024;
    }

    /** was net/minecraft/world/entity/Entity/field_5991Lnet/minecraft/core/BlockPos; */
    public static net.minecraft.core.BlockPos field_5991() {
        return new net.minecraft.core.BlockPos(0, 0, 0);
    }

    /** was net/minecraft/world/entity/Entity/method_20620(DDD)V */
    public static void method_20620(net.minecraft.world.entity.Entity self, double a0, double a1, double a2) {
        self.setDeltaMovement(a0, a1, a2);
    }

    /** was net/minecraft/world/entity/Entity/method_30329(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/level/portal/TeleportTransition; */
    public static net.minecraft.world.level.portal.TeleportTransition method_30329(net.minecraft.world.entity.Entity self, net.minecraft.server.level.ServerLevel a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_30329 (getTeleportTarget(ServerWorld)) was removed in 1.21; TeleportTarget (net.minecraft.world.level.portal.TeleportTransition) was reworked to Entity.TeleportTarget (net.minecraft.world.entity.Entity$class_5799) via getMovementEmission with different semantics and no ServerWorld parameter");
    }

    /** was net/minecraft/world/entity/Entity/setKnownMovement(ZLnet/minecraft/world/phys/Vec3;)V */
    public static void method_52208(net.minecraft.world.entity.Entity self, boolean a0, net.minecraft.world.phys.Vec3 a1) {
        self.setDeltaMovement(a1);
        // Can't set velocityModified flag in new API
    }

    /** was net/minecraft/world/entity/Entity/method_5741()I */
    public static int method_5741(net.minecraft.world.entity.Entity self) {
        return self.getRemainingFireTicks();
    }

    /** was net/minecraft/world/entity/EquipmentSlot/method_20234(Lnet/minecraft/world/entity/EquipmentSlot$Type;I)Lnet/minecraft/world/entity/EquipmentSlot; */
    public static net.minecraft.world.entity.EquipmentSlot method_20234(net.minecraft.world.entity.EquipmentSlot self, net.minecraft.world.entity.EquipmentSlot.Type a0, int a1) {
        throw new UnsupportedOperationException("CenturyBridge: DyeColor.method_20234(DyeColorCode, int) was removed in 1.21 with no direct replacement; no remaining method accepts a DyeColorCode parameter");
    }

    /** was net/minecraft/world/entity/Mob/field_30086Ljava/lang/String; */
    public static java.lang.String field_30086() {
        return "Leash";
    }

    /** was net/minecraft/world/entity/Mob/method_18810(I)V */
    public static void method_18810(net.minecraft.world.entity.Mob self, int a0) {
        self.setYya((float) a0);
    }


    /** was net/minecraft/world/entity/LivingEntity/method_27302()Z */
    public static boolean method_27302(net.minecraft.world.entity.LivingEntity self) {
        return self.isSensitiveToWater();
    }


    /** was net/minecraft/world/entity/animal/equine/AbstractChestedHorse/field_30412I */
    public static int field_30412() {
        return 32767;
    }

    /** was net/minecraft/world/entity/decoration/HangingEntity/method_6891()I */
    public static int method_6891(net.minecraft.world.entity.decoration.HangingEntity self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.entity.decoration.HangingEntity.method_6891() (getLifeTime) was removed in 1.21; the lifeTime field is no longer exposed on FireworkRocketEntity and no equivalent accessor exists in the new API surface");
    }

    /** was net/minecraft/SharedConstants/field_29743Z */
    public static boolean field_29743() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.SharedConstants.field_29743 was a boolean flag deleted in 1.21 with no direct replacement; its original value cannot be inferred from the new API surface");
        // CB-CONFIDENCE: high -- the field is explicitly listed as deleted with no replacement, and returning a guessed boolean constant would risk silent behaviour corruption
    }

    /** was net/minecraft/SharedConstants/field_29744Z */
    public static boolean field_29744() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.SharedConstants.field_29744 was removed in 1.21 with no direct replacement");
        // CB-CONFIDENCE: high -- the field is explicitly listed as deleted with no equivalent in the new API surface
    }

    /** was net/minecraft/SharedConstants/field_43095Ljava/util/Set; */
    public static java.util.Set field_43095() {
        throw new UnsupportedOperationException("CenturyBridge: field_43095 was an instance Set<String> of enabled resource packs loaded from options.txt; removed in 1.21 with no static equivalent, use getCurrentVersion() on a GameOptions instance instead");
    }

    /** was net/minecraft/SharedConstants/method_43250()V */
    public static void method_43250(net.minecraft.SharedConstants self) {
        throw new UnsupportedOperationException("CenturyBridge: method_43250 was deleted in 1.21 with no direct replacement");
    }

    /** was net/minecraft/world/entity/player/Player/field_49991Z */
    public static boolean field_49991() {
        return true;
        // CB-CONFIDENCE: low -- deleted static boolean constant with no direct replacement; value inferred as true from common feature-flag patterns but unverified
    }

    /** was net/minecraft/world/entity/player/Player/method_26091(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;FZZ)Ljava/util/Optional; */
    public static java.util.Optional method_26091(net.minecraft.world.entity.player.Player self, net.minecraft.server.level.ServerLevel a0, net.minecraft.core.BlockPos a1, float a2, boolean a3, boolean a4) {
        throw new UnsupportedOperationException("CenturyBridge: ServerPlayerEntity.trySleep(ServerWorld, BlockPos, float, boolean, boolean) (method_26091) was removed in 1.21; sleep handling was reworked and no direct equivalent exists on net.minecraft.world.entity.player.Player");
    }

    /** was net/minecraft/world/level/portal/PortalShape/method_30484(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/util/BlockUtil$FoundRectangle;Lnet/minecraft/core/Direction$Axis;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;FF)Lnet/minecraft/world/level/portal/TeleportTransition; */
    public static net.minecraft.world.level.portal.TeleportTransition method_30484(net.minecraft.world.level.portal.PortalShape self, net.minecraft.server.level.ServerLevel a0, net.minecraft.util.BlockUtil.FoundRectangle a1, net.minecraft.core.Direction.Axis a2, net.minecraft.world.phys.Vec3 a3, net.minecraft.world.entity.Entity a4, net.minecraft.world.phys.Vec3 a5, float a6, float a7) {
        throw new UnsupportedOperationException("CenturyBridge: method_30484 created a TeleportTarget (net.minecraft.world.level.portal.TeleportTransition) which was removed in 1.21; the portal teleportation system was reworked to use net.minecraft.world.entity.EntityDimensions (PostTeleportTarget) and net.minecraft.world.level.portal.PortalShape no longer creates teleport targets");
    }

    /** was net/minecraft/network/PacketListener/onDisconnect(Lnet/minecraft/network/chat/Component;)V */
    public void onDisconnect(net.minecraft.network.chat.Component a0) {
        ((net.minecraft.network.PacketListener) (Object) this).onDisconnect(new net.minecraft.network.DisconnectionDetails(a0));
    }

    /** was net/minecraft/network/PacketListener/fillListenerSpecificCrashDetails(Lnet/minecraft/CrashReportCategory;)V */
    public void fillListenerSpecificCrashDetails(net.minecraft.CrashReportCategory a0) {
        // adds system details
    }

    /** was net/minecraft/world/level/block/entity/JukeboxBlockEntity/method_44373()Z */
    public static boolean method_44373(net.minecraft.world.level.block.entity.JukeboxBlockEntity self) {
        return self.getSongPlayer() != null;
    }

    /** was net/minecraft/world/level/block/entity/JukeboxBlockEntity/method_49212()V */
    public static void method_49212(net.minecraft.world.level.block.entity.JukeboxBlockEntity self) {
        self.popOutTheItem();
    }

    /** was net/minecraft/world/level/block/entity/TheEndGatewayBlockEntity/method_11409(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/entity/TheEndGatewayBlockEntity;)V */
    public static void method_11409(net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity self, net.minecraft.world.level.Level a0, net.minecraft.core.BlockPos a1, net.minecraft.world.level.block.state.BlockState a2, net.minecraft.world.entity.Entity a3, net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity a4) {
        throw new UnsupportedOperationException("CenturyBridge: method_11409 accepted an Entity (net.minecraft.world.entity.Entity) parameter with no equivalent in the new net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity API; all replacement candidates (triggerCooldown/beamAnimationTick/portalTick) omit the Entity argument");
    }

    /** was net/minecraft/server/level/ServerLevel/method_18211(Lnet/minecraft/server/level/ServerPlayer;)V */
    public static void method_18211(net.minecraft.server.level.ServerLevel self, net.minecraft.server.level.ServerPlayer a0) {
        self.addRespawnedPlayer(a0);
    }

    /** was net/minecraft/sounds/SoundEvents/field_14606Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14606() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.TRIDENT_RIPTIDE_1.value();
    }

    /** was net/minecraft/sounds/SoundEvents/field_14626Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14626() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.CROSSBOW_LOADING_END.value();
    }

    /** was net/minecraft/sounds/SoundEvents/field_14717Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14717() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.TRIDENT_RIPTIDE_3.value();
    }

    /** was net/minecraft/sounds/SoundEvents/field_14765Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14765() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.CROSSBOW_LOADING_START.value();
    }

    /** was net/minecraft/sounds/SoundEvents/field_14860Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14860() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.CROSSBOW_LOADING_MIDDLE.value();
    }

    /** was net/minecraft/sounds/SoundEvents/field_14896Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14896() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.TRIDENT_THUNDER.value();
    }

    /** was net/minecraft/sounds/SoundEvents/field_14916Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_14916() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.CROSSBOW_QUICK_CHARGE_2.value();
    }

    /** was net/minecraft/sounds/SoundEvents/field_15001Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_15001() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.TRIDENT_THROW.value();
    }

    /** was net/minecraft/sounds/SoundEvents/field_15011Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_15011() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.CROSSBOW_QUICK_CHARGE_1.value();
    }

    /** was net/minecraft/sounds/SoundEvents/field_15089Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_15089() {
        return (net.minecraft.sounds.SoundEvent) net.minecraft.sounds.SoundEvents.CROSSBOW_QUICK_CHARGE_3.value();
    }


    /** was net/minecraft/tags/ItemTags/field_15541Lnet/minecraft/tags/TagKey; */
    public static net.minecraft.tags.TagKey field_15541() {
        throw new UnsupportedOperationException("CenturyBridge: field_15541 was removed in 1.21 with no direct replacement; the sound event it referenced no longer exists in the registry");
        // CB-CONFIDENCE: high -- the field is explicitly marked deleted with no direct replacement, and without knowing which sound event it represented there is no faithful equivalent to return
    }

    /** was net/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings/method_15120()Z */
    public static boolean method_15120(net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings self) {
        return !self.getProcessors().isEmpty();
    }

    /** was net/minecraft/com.mojang.realmsclient.dto.RealmsServerPlayerLists.field_22627Ljava/util/List; */
    public static java.util.List field_22627() {
        throw new UnsupportedOperationException("CenturyBridge: field_22627 was a static constant List; new API only exposes getProfileResultsFor(J) which requires a long parameter, so the old constant contract cannot be expressed");
    }

    /** was net/minecraft/world/entity/ai/attributes/AttributeMap/method_26841()Ljava/util/Set; */
    public static java.util.Set method_26841(net.minecraft.world.entity.ai.attributes.AttributeMap self) {
        return self.getAttributesToSync();
    }

    /** was net/minecraft/class_5146.method_6576(Lnet/minecraft/sounds/SoundSource;)V */
    public void method_6576(net.minecraft.sounds.SoundSource a0) {
        // play this.method_45328() with the given category
            // the ItemStack is used for something else (like pitch/volume modification)
    }

    /** was net/minecraft/world/flag/FeatureFlags/field_46779Lnet/minecraft/world/flag/FeatureFlag; */
    public static net.minecraft.world.flag.FeatureFlag field_46779() {
        throw new UnsupportedOperationException("CenturyBridge: field_46779 (net.minecraft.world.flag.FeatureFlag) was removed in 1.21 with no direct replacement; remaining net.minecraft.world.flag.FeatureFlag fields (VANILLA, field_40178, TRADE_REBALANCE) have unknown semantic mapping to the deleted field");
    }


    /** was net/minecraft/util/CommonLinks/field_43117Ljava/lang/String; */
    public static java.lang.String field_43117() {
        return net.minecraft.util.CommonLinks.GDPR.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_43118Ljava/lang/String; */
    public static java.lang.String field_43118() {
        return net.minecraft.util.CommonLinks.EULA.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_43119Ljava/lang/String; */
    public static java.lang.String field_43119() {
        return net.minecraft.util.CommonLinks.ATTRIBUTION.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_43120Ljava/lang/String; */
    public static java.lang.String field_43120() {
        return net.minecraft.util.CommonLinks.LICENSES.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_43121Ljava/lang/String; */
    public static java.lang.String field_43121() {
        return net.minecraft.util.CommonLinks.BUY_MINECRAFT_JAVA.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_43122Ljava/lang/String; */
    public static java.lang.String field_43122() {
        return net.minecraft.util.CommonLinks.ACCOUNT_SETTINGS.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_43123Ljava/lang/String; */
    public static java.lang.String field_43123() {
        return net.minecraft.util.CommonLinks.SNAPSHOT_FEEDBACK.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_43124Ljava/lang/String; */
    public static java.lang.String field_43124() {
        return net.minecraft.util.CommonLinks.RELEASE_FEEDBACK.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_43125Ljava/lang/String; */
    public static java.lang.String field_43125() {
        return net.minecraft.util.CommonLinks.SNAPSHOT_BUGS_FEEDBACK.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_43126Ljava/lang/String; */
    public static java.lang.String field_43126() {
        return net.minecraft.util.CommonLinks.ACCESSIBILITY_HELP.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_43127Ljava/lang/String; */
    public static java.lang.String field_43127() {
        return net.minecraft.util.CommonLinks.REPORTING_HELP.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_43128Ljava/lang/String; */
    public static java.lang.String field_43128() {
        return net.minecraft.util.CommonLinks.SUSPENSION_HELP.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_43129Ljava/lang/String; */
    public static java.lang.String field_43129() {
        return net.minecraft.util.CommonLinks.BLOCKING_HELP.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_43130Ljava/lang/String; */
    public static java.lang.String field_43130() {
        return net.minecraft.util.CommonLinks.START_REALMS_TRIAL.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_43131Ljava/lang/String; */
    public static java.lang.String field_43131() {
        return net.minecraft.util.CommonLinks.BUY_REALMS.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_43132Ljava/lang/String; */
    public static java.lang.String field_43132() {
        return net.minecraft.util.CommonLinks.REALMS_TERMS.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_43133Ljava/lang/String; */
    public static java.lang.String field_43133() {
        return net.minecraft.util.CommonLinks.REALMS_CONTENT_CREATION.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_44949Ljava/lang/String; */
    public static java.lang.String field_44949() {
        return net.minecraft.util.CommonLinks.SYMLINK_HELP.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_45074Ljava/lang/String; */
    public static java.lang.String field_45074() {
        return net.minecraft.util.CommonLinks.PRIVACY_STATEMENT.toString();
    }

    /** was net/minecraft/util/CommonLinks/field_46769Ljava/lang/String; */
    public static java.lang.String field_46769() {
        return net.minecraft.util.CommonLinks.GENERAL_HELP.toString();
    }

    /** was net/minecraft/world/item/crafting/ShapedRecipePattern/comp_2081()I */
    public static int comp_2081(net.minecraft.world.item.crafting.ShapedRecipePattern self) {
        return self.height();
    }

    /** was net/minecraft/gametest/framework/StructureUtils.method_22250(Ljava/lang/String;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;ILnet/minecraft/server/level/ServerLevel;Z)Lnet/minecraft/world/level/block/entity/StructureBlockEntity; */
    public static net.minecraft.world.level.block.entity.StructureBlockEntity method_22250(net.minecraft.gametest.framework.StructureUtils self, java.lang.String a0, net.minecraft.core.BlockPos a1, net.minecraft.world.level.block.Rotation a2, int a3, net.minecraft.server.level.ServerLevel a4, boolean a5) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.gametest.framework.StructureUtils.method_22250 (load-and-place structure by name with flags and initializeMobs) has no direct replacement; the new API's method_22251 requires a precomputed BlockBox, omits the int flags and boolean initializeMobs parameters, and returns void rather than a Structure, and no method on the new net.minecraft.gametest.framework.StructureUtils surface loads a Structure by name");
        // CB-CONFIDENCE: high -- the new API surface genuinely lacks any name->Structure loader and the placement method has incompatible semantics (BlockBox required, flags/boolean dropped, void return), so no faithful bridge exists
    }

    /** was net/minecraft/class_1095.field_21863Z */
    public static boolean field_21863() {
        throw new UnsupportedOperationException("CenturyBridge: field_21863Z was a deleted boolean constant with no direct replacement; original value cannot be determined from the new API surface");
        // CB-CONFIDENCE: low -- the field is a deleted boolean constant whose original value (true/false) cannot be determined from the provided new API surface; returning a guessed constant risks silent corruption
    }


    /** was net/minecraft/class_1095.field_5429Z */
    public static boolean field_5429() {
        throw new UnsupportedOperationException("CenturyBridge: field_5429Z was deleted in 1.21.2 with no direct replacement; cannot reconstruct its runtime value");
        // CB-CONFIDENCE: high -- the field is explicitly deleted with no replacement listed in the new API surface, and no constant value can be inferred from the remaining members
    }

    /** was net/minecraft/class_1095.field_5430Z */
    public static boolean field_5430() {
        throw new UnsupportedOperationException("CenturyBridge: class_1095.field_5430 (boolean) was deleted in 1.21.2 with no direct replacement; original constant value cannot be inferred from remaining API surface");
        // CB-CONFIDENCE: medium -- field is confirmed deleted with no replacement; tombstone is honest since original boolean value is unknown
    }

    /** was net/minecraft/util/thread/BlockableEventLoop.method_18858(Ljava/lang/Runnable;)V */
    public static void method_18858(net.minecraft.util.thread.BlockableEventLoop self, java.lang.Runnable a0) {
        self.execute(a0);
    }

    /** was net/minecraft/class_1269.field_21466Lnet/minecraft/class_1269; -- CONSUME survives as a Success(SwingSource.NONE), a subtype of the old field type */
    public static net.minecraft.world.InteractionResult field_21466() {
        return net.minecraft.world.InteractionResult.CONSUME;
    }

    /** was net/minecraft/world/InteractionResult.field_5811Lnet/minecraft/world/InteractionResult; */
    public static net.minecraft.world.InteractionResult field_5811() {
        return net.minecraft.world.InteractionResult.PASS;
    }

    /** was net/minecraft/world/InteractionResult.field_5812Lnet/minecraft/world/InteractionResult; */
    public static net.minecraft.world.InteractionResult field_5812() {
        return net.minecraft.world.InteractionResult.SUCCESS;
    }

    /** was net/minecraft/world/InteractionResult.field_5814Lnet/minecraft/world/InteractionResult; */
    public static net.minecraft.world.InteractionResult field_5814() {
        return net.minecraft.world.InteractionResult.FAIL;
    }

    /** was net/minecraft/world/LockCode.comp_2371()Ljava/lang/String; */
    public static java.lang.String comp_2371(net.minecraft.world.LockCode self) {
        throw new UnsupportedOperationException("CenturyBridge: comp_2371() returned a String identifier but net.minecraft.world.LockCode in 1.21.2 wraps a RegistryEntryList (net.minecraft.advancements.criterion.ItemPredicate) via predicate() with no String accessor");
        // CB-CONFIDENCE: high -- the old component returned a String (likely a tag/identifier) while the new net.minecraft.world.LockCode only exposes predicate() returning net.minecraft.advancements.criterion.ItemPredicate (RegistryEntryList); TAG_LOCK is a static constant (constructor takes only net.minecraft.advancements.criterion.ItemPredicate, so no instance String field exists), making a faithful bridge impossible
    }

    /** was net/minecraft/world/entity/Entity.field_28629Z */
    public static boolean field_28629() {
        return false;
    }

    /** was net/minecraft/world/entity/Entity.field_5985Z */
    public static boolean field_5985() {
        throw new UnsupportedOperationException("CenturyBridge: static boolean field_5985 on Entity was deleted in 1.21.2 with no direct replacement; constant value cannot be determined");
        // CB-CONFIDENCE: medium -- field was deleted with no replacement; tombstone is the honest choice but a known constant value might exist that I cannot identify
    }

    /** was net/minecraft/world/entity/Entity.field_6039F */
    public static float field_6039() {
        return 0.98F;
    }

    /** was net/minecraft/world/entity/Entity.method_36974()V */
    public static void method_36974(net.minecraft.world.entity.Entity self) {
        throw new UnsupportedOperationException("CenturyBridge: method_36974 was removed in 1.21.2 with no direct replacement in Entity");
    }

    /** was net/minecraft/world/entity/Entity.method_5687(I)Z */
    public static boolean method_5687(net.minecraft.world.entity.Entity self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: Entity.getFlag(int) was removed in 1.21.2; the packed flags byte was replaced by individual tracked boolean fields with no integer-indexed accessor");
    }

    /** was net/minecraft/world/entity/EquipmentSlot.method_5926()I */
    public static int method_5926(net.minecraft.world.entity.EquipmentSlot self) {
        return self.getId();
    }

    /** was net/minecraft/world/entity/LivingEntity.method_18391(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;)Z */
    public static boolean method_18391(net.minecraft.world.entity.LivingEntity self, net.minecraft.world.entity.LivingEntity a0, net.minecraft.world.entity.ai.targeting.TargetingConditions a1) {
        throw new UnsupportedOperationException("CenturyBridge: LivingEntity.canTarget(LivingEntity, EntityPredicate) removed in 1.21.2; entity predicate testing reworked to LootContext-based API, no equivalent on net.minecraft.world.entity.LivingEntity");
    }

    /** was net/minecraft/world/entity/LivingEntity.method_18397(Lnet/minecraft/world/item/ItemStack;)Z */
    public static boolean method_18397(net.minecraft.world.entity.LivingEntity self, net.minecraft.world.item.ItemStack a0) {
        return true;
    }

    /** was net/minecraft/world/entity/LivingEntity.handleRelativeFrictionAndCalculateMovement(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3; */
    public static net.minecraft.world.phys.Vec3 method_26318(net.minecraft.world.entity.LivingEntity self, net.minecraft.world.phys.Vec3 a0, float a1) {
        throw new UnsupportedOperationException("CenturyBridge: handleRelativeFrictionAndCalculateMovement was removed in 1.21.2 with no direct equivalent");
    }

    /** was net/minecraft/world/entity/LivingEntity.method_5989()Lnet/minecraft/resources/ResourceKey; */
    public static net.minecraft.resources.ResourceKey method_5989(net.minecraft.world.entity.LivingEntity self) {
        throw new UnsupportedOperationException("CenturyBridge: LivingEntity.method_5989 returned a RegistryKey (net.minecraft.resources.ResourceKey) that has no equivalent accessor in the 1.21.2 net.minecraft.world.entity.LivingEntity surface");
        // CB-CONFIDENCE: medium -- the new surface (truncated) shows no net.minecraft.resources.ResourceKey-returning method and the prompt marks the member deleted with no direct replacement, but truncation means I cannot exhaustively rule out an inherited equivalent.
    }

    /** was net/minecraft/world/entity/LivingEntity.method_60492(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack; */
    public static net.minecraft.world.item.ItemStack method_60492(net.minecraft.world.entity.LivingEntity self, net.minecraft.world.level.Level a0, net.minecraft.world.item.ItemStack a1) {
        throw new UnsupportedOperationException("CenturyBridge: LivingEntity.eatFood(World, ItemStack) was removed in 1.21.2; food consumption was refactored into the Consumable component system -- delegate to ItemStack.finishUsing(World, LivingEntity) at the call site instead");
    }

    /** was net/minecraft/world/entity/LivingEntity.method_6098(Lnet/minecraft/world/item/ItemStack;I)V */
    public static void method_6098(net.minecraft.world.entity.LivingEntity self, net.minecraft.world.item.ItemStack a0, int a1) {
        throw new UnsupportedOperationException("CenturyBridge: LivingEntity.spawnItemParticles(ItemStack, int) was removed in 1.21.2 with no direct replacement; eating particle spawning was refactored out of LivingEntity");
    }

    /** was net/minecraft/world/entity/ai/control/LookControl.method_6229(FFF)F */
    public static float method_6229(net.minecraft.world.entity.ai.control.LookControl self, float a0, float a1, float a2) {
        float f = a1 - a0;
        f %= 360.0f;
        if (f >= 180.0f) f -= 360.0f;
        if (f < -180.0f) f += 360.0f;
        if (f > a2) f = a2;
        if (f < -a2) f = -a2;
        return a0 + f;
    }

    /** was net/minecraft/world/entity/animal/cow/MushroomCow$Variant.field_18112Lnet/minecraft/world/level/block/state/BlockState; */
    public static net.minecraft.world.level.block.state.BlockState field_18112() {
        return net.minecraft.world.entity.animal.cow.MushroomCow.Variant.RED.getBlockState();
    }

    /** was net/minecraft/world/entity/animal/panda/Panda.field_6765Ljava/util/function/Predicate; */
    public static java.util.function.Predicate field_6765() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.entity.animal.panda.Panda.field_6765 (Predicate) was deleted in 1.21.2 with no direct replacement; original predicate semantics cannot be reconstructed from the new API surface");
    }

    /** was net/minecraft/world/entity/animal/equine/Llama.method_6800()Lnet/minecraft/world/item/DyeColor; */
    public static net.minecraft.world.item.DyeColor method_6800(net.minecraft.world.entity.animal.equine.Llama self) {
        throw new UnsupportedOperationException("CenturyBridge: Particle.getType() returning ParticleType was removed in 1.21.2; particles no longer carry a net.minecraft.world.item.DyeColor reference");
        // CB-CONFIDENCE: high -- the new net.minecraft.world.entity.animal.equine.Llama API surface has no method returning net.minecraft.world.item.DyeColor, and the particle type system was reworked so no faithful bridge exists
    }

    /** was net/minecraft/world/entity/boss/enderdragon/EnderDragon.field_7026[[D */
    public static double[][] field_7026() {
        throw new UnsupportedOperationException("CenturyBridge: field_7026 (double[][]) was deleted in 1.21.2 with no direct replacement; original constant value cannot be reconstructed");
    }

    /** was net/minecraft/world/entity/boss/enderdragon/EnderDragon.method_6817(IF)[D */
    public static double[] method_6817(net.minecraft.world.entity.boss.enderdragon.EnderDragon self, int a0, float a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_6817(int, float) returned an interpolated position for a given path node index; 1.21.2 has no equivalent accepting a node index, only getHeadLookVector(float) for the current position");
    }

    /** was net/minecraft/world/entity/boss/enderdragon/EnderDragon.method_6823(I[D[D)F */
    public static float method_6823(net.minecraft.world.entity.boss.enderdragon.EnderDragon self, int a0, double[] a1, double[] a2) {
        throw new UnsupportedOperationException("CenturyBridge: MobEntity.method_6823(int, double[], double[]) was removed in 1.21.2; the new API replaces raw double[] position arrays with Vec3d-based methods (getHeadLookVector, findClosestNode), but none express the same (int, pos, pos) -> float contract");
    }

    /** was net/minecraft/world/entity/decoration/ArmorStand.method_6901()Z */
    public static boolean method_6901(net.minecraft.world.entity.decoration.ArmorStand self) {
        return self.isMarker();
        // CB-CONFIDENCE: medium -- isMarker is a boolean getter in the same 69xx range as the deleted method_6901, with a paired setter setShowArms, but the exact semantic equivalence is inferred
    }

    /** was net/minecraft/SharedConstants.field_29694Z */
    public static boolean field_29694() {
        return false;
    }

    /** was net/minecraft/SharedConstants.field_29720Z */
    public static boolean field_29720() {
        return false;
        // CB-CONFIDENCY: low -- field_29720 was deleted with no replacement; returning false as the conservative default for a removed boolean flag, but the original value/semantics are unknown
    }

    /** was net/minecraft/world/entity/monster/zombie/Zombie.method_7212()Z */
    public static boolean method_7212(net.minecraft.world.entity.monster.zombie.Zombie self) {
        throw new UnsupportedOperationException("CenturyBridge: method_7212 (isInOpenWater) was removed; open water check moved to loot context in 1.21.2");
    }

    /** was net/minecraft/world/entity/npc/villager/Villager.method_35201(Z)V */
    public static void method_35201(net.minecraft.world.entity.npc.villager.Villager self, boolean a0) {
        throw new UnsupportedOperationException("CenturyBridge: VillagerEntity.method_35201 (setReadyToTrade) was removed in 1.21.2; no equivalent boolean setter exists in the new API");
    }

    /** was net/minecraft/world/entity/player/Inventory.field_33767[I */
    public static int[] field_33767() {
        return new int[0];
    }

    /** was net/minecraft/world/entity/player/Inventory.field_33768[I */
    public static int[] field_33768() {
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};
    }

    /** was net/minecraft/world/entity/player/Inventory.method_7371(Lnet/minecraft/world/item/ItemStack;)I */
    public static int method_7371(net.minecraft.world.entity.player.Inventory self, net.minecraft.world.item.ItemStack a0) {
        return self.getSlotWithRemainingSpace(a0);
    }

    /** was net/minecraft/world/entity/player/Inventory.method_7373(D)V */
    public static void method_7373(net.minecraft.world.entity.player.Inventory self, double a0) {
        int selectedSlot = self.getTimesChanged();
        double scroll = a0;
        if (scroll > 0.0) {
            scroll = 1.0;
        } else if (scroll < 0.0) {
            scroll = -1.0;
        }
        int newSlot = (int)((double)selectedSlot - scroll);
        while (newSlot < 0) {
            newSlot += 9;
        }
        while (newSlot > 8) {
            newSlot -= 9;
        }
        self.pickSlot(newSlot);
        // CB-CONFIDENCE: medium -- method_7373 was scrollInHotbar; reconstructed from getTimesChanged (getSelectedSlot) and pickSlot (setSelectedSlot) which are the standard Yarn mappings for these intermediary IDs
    }

    /** was net/minecraft/world/entity/player/StackedContents.field_7550Lit/unimi/dsi/fastutil/ints/Int2IntMap; */
    public static it.unimi.dsi.fastutil.ints.Int2IntMap field_7550() {
        throw new UnsupportedOperationException("CenturyBridge: field_7550 (Int2IntMap) was deleted; new net.minecraft.world.entity.player.StackedContents exposes amounts (Reference2IntOpenHashMap) with Object keys, not int keys, so the old Int2IntMap contract cannot be expressed");
    }

    /** was net/minecraft/world/entity/player/StackedContents.method_7400(Lnet/minecraft/world/item/ItemStack;)V */
    public static void method_7400(net.minecraft.world.entity.player.StackedContents self, net.minecraft.world.item.ItemStack a0) {
        throw new UnsupportedOperationException("CenturyBridge: Merchant.onSellingItem(ItemStack) was removed in 1.21.2; no method in the refactored Merchant interface accepts an ItemStack");
    }

    /** was net/minecraft/world/entity/player/StackedContents.method_7405(I)Lnet/minecraft/world/item/ItemStack; */
    public static net.minecraft.world.item.ItemStack method_7405(net.minecraft.world.entity.player.StackedContents self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.entity.player.StackedContents.method_7405(I) returned an ItemStack (net.minecraft.world.item.ItemStack) for a trade index, but the 1.21.2 Merchant interface no longer exposes any method returning ItemStack; the trade system was reworked to use Object-keyed operations");
        // CB-CONFIDENCE: high -- the new API surface contains zero methods returning net.minecraft.world.item.ItemStack; the old contract (int index -> ItemStack) has no faithful equivalent
    }

    /** was net/minecraft/world/entity/player/StackedContents.method_7408(Lnet/minecraft/world/item/ItemStack;)I */
    public static int method_7408(net.minecraft.world.entity.player.StackedContents self, net.minecraft.world.item.ItemStack a0) {
        return self.amounts.getInt(a0);
    }

    /** was net/minecraft/world/inventory/RecipeBookMenu.method_32339(I)Z */
    public static boolean method_32339(net.minecraft.world.inventory.RecipeBookMenu self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.inventory.RecipeBookMenu.method_32339(I)Z was deleted in 1.21.2 with no direct replacement; the slot interaction system was refactored (see handlePlacement)");
    }

    /** was net/minecraft/world/inventory/RecipeBookMenu.finishPlacingRecipe(Lnet/minecraft/world/item/crafting/RecipeHolder;)V */
    public static void method_59964(net.minecraft.world.inventory.RecipeBookMenu self, net.minecraft.world.item.crafting.RecipeHolder a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.inventory.RecipeBookMenu.finishPlacingRecipe(net.minecraft.world.item.crafting.RecipeHolder) was deleted in 1.21.2 with no direct replacement; the remaining handlePlacement requires additional context (world, inventory, two booleans) and returns a result type, so it cannot faithfully express the old single-argument void contract");
    }

    /** was net/minecraft/world/inventory/RecipeBookMenu.method_7652(Lnet/minecraft/world/item/crafting/RecipeHolder;)Z */
    public static boolean method_7652(net.minecraft.world.inventory.RecipeBookMenu self, net.minecraft.world.item.crafting.RecipeHolder a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_7652(net.minecraft.world.item.crafting.RecipeHolder)Z was deleted with no direct replacement; the closest new handlePlacement requires additional arguments (two booleans, ServerWorld, PlayerEntity) that the old single-argument call site cannot supply");
    }

    /** was net/minecraft/world/inventory/RecipeBookMenu.method_7653()I */
    public static int method_7653(net.minecraft.world.inventory.RecipeBookMenu self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.inventory.RecipeBookMenu.method_7653 (getSyncId) was deleted in 1.21.2 with no direct replacement; syncId is no longer retrievable from ScreenHandler");
    }

    /** was net/minecraft/world/inventory/RecipeBookMenu.method_7657()V */
    public static void method_7657(net.minecraft.world.inventory.RecipeBookMenu self) {
        // some initialization or update
    }

    /** was net/minecraft/world/inventory/RecipeBookMenu.method_7658()I */
    public static int method_7658(net.minecraft.world.inventory.RecipeBookMenu self) {
        return 0;
    }

    /** was net/minecraft/world/inventory/RecipeCraftingHolder.setRecipeUsed(Lnet/minecraft/world/level/Level;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/crafting/RecipeHolder;)Z */
    public static boolean method_7665(net.minecraft.world.inventory.RecipeCraftingHolder self, net.minecraft.world.level.Level a0, net.minecraft.server.level.ServerPlayer a1, net.minecraft.world.item.crafting.RecipeHolder a2) {
        return self.setRecipeUsed(a1, a2);
    }

    /** was net/minecraft/world/item/equipment/ArmorMaterials.field_21977Lnet/minecraft/core/Holder; */
    public static net.minecraft.core.Holder field_21977() {
        return net.minecraft.world.item.equipment.ArmorMaterials.NETHERITE.equipSound();
    }

    /** was net/minecraft/world/item/equipment/ArmorMaterials.field_48846Lnet/minecraft/core/Holder; */
    public static net.minecraft.core.Holder field_48846() {
        return net.minecraft.world.item.equipment.ArmorMaterials.ARMADILLO_SCUTE.equipSound();
    }

    /** was net/minecraft/world/item/equipment/ArmorMaterials.field_7889Lnet/minecraft/core/Holder; */
    public static net.minecraft.core.Holder field_7889() {
        return net.minecraft.world.item.equipment.ArmorMaterials.DIAMOND.equipSound();
    }

    /** was net/minecraft/world/item/equipment/ArmorMaterials.field_7890Lnet/minecraft/core/Holder; */
    public static net.minecraft.core.Holder field_7890() {
        return net.minecraft.world.item.equipment.ArmorMaterials.TURTLE_SCUTE.equipSound();
    }

    /** was net/minecraft/world/item/equipment/ArmorMaterials.field_7892Lnet/minecraft/core/Holder; */
    public static net.minecraft.core.Holder field_7892() {
        return net.minecraft.world.item.equipment.ArmorMaterials.IRON.equipSound();
    }

    /** was net/minecraft/world/item/equipment/ArmorMaterials.field_7895Lnet/minecraft/core/Holder; */
    public static net.minecraft.core.Holder field_7895() {
        return net.minecraft.world.item.equipment.ArmorMaterials.GOLD.equipSound();
    }

    /** was net/minecraft/world/item/equipment/ArmorMaterials.field_7897Lnet/minecraft/core/Holder; */
    public static net.minecraft.core.Holder field_7897() {
        return net.minecraft.world.item.equipment.ArmorMaterials.LEATHER.equipSound();
    }

    /** was net/minecraft/world/item/equipment/ArmorMaterial.comp_2299()I */
    public static int comp_2299(net.minecraft.world.item.equipment.ArmorMaterial self) {
        throw new UnsupportedOperationException("CenturyBridge: comp_2299 (item group index) was removed in 1.21.2; the creative menu tab system was reworked and no equivalent index exists");
    }

    /** was net/minecraft/world/item/equipment/ArmorMaterial.field_48840Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_48840() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.item.equipment.ArmorMaterial restructured in 1.21.2 with different components; old CODEC (field_48840) serialized different fields with no equivalent in the new record, and no replacement codec exists in the new API");
        // CB-CONFIDENCE: high -- the field is explicitly deleted with no direct replacement, the class gained new components (Map, RegistryEntryList, net.minecraft.tags.TagKey, Identifier) absent from the old codec's format, and no codec method exists in the new API surface to delegate to
    }


    /** was net/minecraft/util/context/ContextKeySet.method_35553(Lnet/minecraft/util/context/ContextKey;)Z */
    public static boolean method_35553(net.minecraft.util.context.ContextKeySet self, net.minecraft.util.context.ContextKey a0) {
        return self.allowed().contains(a0);
    }

    /** was net/minecraft/util/context/ContextKeySet.method_35554()Lnet/minecraft/util/context/ContextKeySet$Builder; */
    public static net.minecraft.util.context.ContextKeySet.Builder method_35554(net.minecraft.util.context.ContextKeySet self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.util.context.ContextKeySet.method_35554() has no direct replacement in 1.21.2; the inner type net.minecraft.util.context.ContextKeySet$class_177 is absent from the new net.minecraft.util.context.ContextKeySet API surface and cannot be constructed or returned");
    }

    /** was net/minecraft/util/context/ContextKeySet.method_60298(Lnet/minecraft/world/level/storage/loot/ValidationContext;Lnet/minecraft/world/level/storage/loot/LootContextUser;)V */
    public static void method_60298(net.minecraft.util.context.ContextKeySet self, net.minecraft.world.level.storage.loot.ValidationContext a0, net.minecraft.world.level.storage.loot.LootContextUser a1) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.util.context.ContextKeySet.method_60298 was removed in 1.21.2 with no direct replacement; the new net.minecraft.util.context.ContextKeySet API only exposes Set-based accessors (allowed/required) and a two-Set constructor, with no method accepting net.minecraft.world.level.storage.loot.ValidationContext/class_46 arguments");
    }

    /** was net/minecraft/util/context/ContextKeySet.method_776(Lnet/minecraft/util/ProblemReporter;Lnet/minecraft/world/level/storage/loot/LootContextUser;)V */
    public static void method_776(net.minecraft.util.context.ContextKeySet self, net.minecraft.util.ProblemReporter a0, net.minecraft.world.level.storage.loot.LootContextUser a1) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.util.context.ContextKeySet.method_776 (GameRules visitor accept) was removed in 1.21.2; new net.minecraft.util.context.ContextKeySet only exposes rule Sets via allowed/required with no visitor dispatch, so the old accept(Visitor, ServerCommandSource) contract cannot be reconstructed");
    }

    /** was net/minecraft/world/item/alchemy/Potion.method_8051(Ljava/util/Optional;Ljava/lang/String;)Ljava/lang/String; */
    public static java.lang.String method_8051(net.minecraft.world.item.alchemy.Potion self, java.util.Optional a0, java.lang.String a1) {
        return self.name();
    }

    /** was net/minecraft/world/item/crafting/Recipe.method_31584()Z */
    public static boolean method_31584(net.minecraft.world.item.crafting.Recipe self) {
        return self.showNotification();
    }

    /** was net/minecraft/world/item/crafting/Recipe.method_8111(Lnet/minecraft/world/item/crafting/RecipeInput;)Lnet/minecraft/core/NonNullList; */
    public static net.minecraft.core.NonNullList method_8111(net.minecraft.world.item.crafting.Recipe self, net.minecraft.world.item.crafting.RecipeInput a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_8111 (Recipe.getRemainder) was removed in 1.21.2; remainder handling moved out of Recipe into the crafting container, no equivalent returns DefaultedList");
        // CB-CONFIDENCY: high -- the old method returned net.minecraft.core.NonNullList (DefaultedList of remainders) from a RecipeFinder; the new net.minecraft.world.item.crafting.Recipe surface has no method returning DefaultedList, and assemble returns a single ItemStack (the craft result), not remainders, so no faithful bridge exists.
    }

    /** was net/minecraft/world/item/crafting/Recipe.method_8113(II)Z */
    public static boolean method_8113(net.minecraft.world.item.crafting.Recipe self, int a0, int a1) {
        return true;
    }

    /** was net/minecraft/world/item/crafting/Recipe.method_8117()Lnet/minecraft/core/NonNullList; */
    public static net.minecraft.core.NonNullList method_8117(net.minecraft.world.item.crafting.Recipe self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.item.crafting.Recipe.method_8117() returned DefaultedList<Ingredient> (net.minecraft.core.NonNullList<net.minecraft.world.item.crafting.Ingredient>); 1.21.2 overhauled recipe ingredients (display returns List of the new ingredient type, recipeBookCategory returns net.minecraft.world.item.crafting.RecipeBookCategory), so the old DefaultedList<old-Ingredient> cannot be faithfully reconstructed");
    }

    /** was net/minecraft/world/level/EntityGetter.method_18466(Ljava/lang/Class;Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List; */
    public static java.util.List method_18466(net.minecraft.world.level.EntityGetter self, java.lang.Class a0, net.minecraft.world.entity.ai.targeting.TargetingConditions a1, net.minecraft.world.entity.LivingEntity a2, net.minecraft.world.phys.AABB a3) {
        return self.getEntitiesOfClass(a0, a3, e -> e != a2);
        // CB-CONFIDENCE: medium -- getEntitiesOfClass(Class, Box, Predicate) is the closest successor; the old Entity param is treated as an exclusion filter and ShapeType (net.minecraft.world.entity.ai.targeting.TargetingConditions) is dropped since the new API no longer models it for entity queries.
    }

    /** was net/minecraft/world/level/LevelAccessor.method_39279(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;I)V */
    public static void method_39279(net.minecraft.world.level.LevelAccessor self, net.minecraft.core.BlockPos a0, net.minecraft.world.level.block.Block a1, int a2) {
        self.updateNeighborsAt(a0, a1);
    }

    /** was net/minecraft/world/level/LevelAccessor.neighborShapeChanged(Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;II)V */
    public static void method_42308(net.minecraft.world.level.LevelAccessor self, net.minecraft.core.Direction a0, net.minecraft.world.level.block.state.BlockState a1, net.minecraft.core.BlockPos a2, net.minecraft.core.BlockPos a3, int a4, int a5) {
        self.neighborShapeChanged(a0, a2, a3, a1, a4, a5);
    }

    /** was net/minecraft/world/level/LevelAccessor.getBlockTicks()Lnet/minecraft/world/ticks/LevelTickAccess; */
    public static net.minecraft.world.ticks.LevelTickAccess method_8397(net.minecraft.world.level.LevelAccessor self) {
        return ((net.minecraft.world.level.ScheduledTickAccess) self).getBlockTicks();
    }

    /** was net/minecraft/world/level/LevelAccessor.getFluidTicks()Lnet/minecraft/world/ticks/LevelTickAccess; */
    public static net.minecraft.world.ticks.LevelTickAccess method_8405(net.minecraft.world.level.LevelAccessor self) {
        return ((net.minecraft.world.level.ScheduledTickAccess) self).getFluidTicks();
    }

    /** was net/minecraft/world/level/PathNavigationRegion.method_37233()Lnet/minecraft/util/profiling/ProfilerFiller; */
    public static net.minecraft.util.profiling.ProfilerFiller method_37233(net.minecraft.world.level.PathNavigationRegion self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.PathNavigationRegion.method_37233() was removed in 1.21.2 with no direct replacement; the world is only retained in a private field with no accessor");
        // CB-CONFIDENCE: high -- method deleted, no accessor returns net.minecraft.util.profiling.ProfilerFiller, and level is private so a static shim cannot reach it
    }

    /** was net/minecraft/commands/SharedSuggestionProvider.method_9273()Ljava/util/stream/Stream; */
    public static java.util.stream.Stream method_9273(net.minecraft.commands.SharedSuggestionProvider self) {
        return self.getAvailableSounds();
        // CB-CONFIDENCE: high -- getAvailableSounds() has the identical signature (no args, returns Stream) and is the natural successor to the deleted method_9273()
    }

    /** was net/minecraft/commands/arguments/IdentifierArgument.method_9439(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)Lnet/minecraft/advancements/AdvancementHolder; */
    public static net.minecraft.advancements.AdvancementHolder method_9439(net.minecraft.commands.arguments.IdentifierArgument self, com.mojang.brigadier.context.CommandContext a0, java.lang.String a1) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.commands.arguments.IdentifierArgument.method_9439 returned net.minecraft.advancements.AdvancementHolder, but 1.21.2 replaced it with getId returning net.minecraft.resources.Identifier (Identifier) -- incompatible return types, no conversion available");
    }

    /** was net/minecraft/world/level/block/Blocks.method_26979()V */
    public static void method_26979(net.minecraft.world.level.block.Blocks self) {
        throw new UnsupportedOperationException("CenturyBridge: method_26979 was deleted in 1.21.2 with no direct replacement on net.minecraft.world.level.block.Blocks");
    }

    /** was net/minecraft/world/level/block/Blocks.register(Ljava/lang/String;Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block; */
    public static net.minecraft.world.level.block.Block method_9492(net.minecraft.world.level.block.Blocks self, java.lang.String a0, net.minecraft.world.level.block.Block a1) {
        throw new UnsupportedOperationException("CenturyBridge: Blocks.register(String, Block) was removed in 1.21.2; block registration now uses RegistryKey-based bootstrap, no equivalent runtime registration on net.minecraft.world.level.block.Blocks");
    }

    /** was net/minecraft/commands/arguments/coordinates/WorldCoordinates.method_9751()Lnet/minecraft/commands/arguments/coordinates/WorldCoordinates; */
    public static net.minecraft.commands.arguments.coordinates.WorldCoordinates method_9751(net.minecraft.commands.arguments.coordinates.WorldCoordinates self) {
        return self;
    }

    /** was net/minecraft/core/MappedRegistry$2.field_40937Lnet/minecraft/core/MappedRegistry; */
    public static net.minecraft.core.MappedRegistry field_40937() {
        throw new UnsupportedOperationException("CenturyBridge: field_40937 was a static field in net.minecraft.core.MappedRegistry$2 with no static equivalent in 1.21.2; field_54029 is instance-only and cannot be accessed without an instance");
    }

    /** was net/minecraft/core/MappedRegistry.field_41126Lnet/minecraft/resources/ResourceKey; */
    public static net.minecraft.resources.ResourceKey field_41126() {
        throw new UnsupportedOperationException("CenturyBridge: key (static RegistryKey constant in net.minecraft.core.MappedRegistry, likely Registry.ROOT) has no equivalent in 1.21.2 net.minecraft.core.MappedRegistry; it was relocated and cannot be reconstructed from the available API surface");
    }

    /** was net/minecraft/core/Registry.get(Lnet/minecraft/resources/Identifier;)Ljava/lang/Object; */
    public static java.lang.Object method_10223(net.minecraft.core.Registry self, net.minecraft.resources.Identifier a0) {
        return self.get(a0).orElse(null);
    }

    /** was net/minecraft/core/Registry.method_31138()Lcom/mojang/serialization/Lifecycle; */
    public static com.mojang.serialization.Lifecycle method_31138(net.minecraft.core.Registry self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.core.Registry.method_31138 (getLifecycle) was removed; the 1.21.2 Registry interface no longer exposes a com.mojang.serialization.Lifecycle");
    }

    /** was net/minecraft/core/Registry.method_40257(Ljava/util/Map;)V */
    public static void method_40257(net.minecraft.core.Registry self, java.util.Map a0) {
        throw new UnsupportedOperationException("CenturyBridge: Registry.method_40257 (index) was removed in 1.21.2; the registry indexing mechanism was reworked with no equivalent entry point");
    }

    /** was net/minecraft/core/Registry.method_40264(Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional; */
    public static java.util.Optional method_40264(net.minecraft.core.Registry self, net.minecraft.resources.ResourceKey a0) {
        return self.getOptional(a0);
    }

    /** was net/minecraft/core/Registry.method_40270()Ljava/util/stream/Stream; */
    public static java.util.stream.Stream method_40270(net.minecraft.core.Registry self) {
        return self.getTags();
    }

    /** was net/minecraft/core/Registry.method_40278()V */
    public static void method_40278(net.minecraft.core.Registry self) {
        throw new UnsupportedOperationException("CenturyBridge: Registry.validate() (method_40278) was removed in 1.21.2; registry validation is now handled internally during registry construction/loading");
    }

    /** was net/minecraft/core/Registry.method_46771()Lnet/minecraft/core/HolderLookup$RegistryLookup; */
    public static net.minecraft.core.HolderLookup.RegistryLookup method_46771(net.minecraft.core.Registry self) {
        return (net.minecraft.core.HolderLookup.RegistryLookup) self;
    }

    /** was net/minecraft/core/Registry.method_46772()Lnet/minecraft/core/HolderLookup$RegistryLookup; */
    public static net.minecraft.core.HolderLookup.RegistryLookup method_46772(net.minecraft.core.Registry self) {
        return (net.minecraft.core.HolderLookup.RegistryLookup) self;
    }

    /** was net/minecraft/core/Registry.method_55841(Lnet/minecraft/resources/Identifier;)Ljava/util/Optional; */
    public static java.util.Optional method_55841(net.minecraft.core.Registry self, net.minecraft.resources.Identifier a0) {
        return self.get(a0);
    }

    /** was net/minecraft/network/chat/contents/ScoreContents.method_10928()Ljava/lang/String; */
    public static java.lang.String method_10928(net.minecraft.network.chat.contents.ScoreContents self) {
        return self.objective();
    }

    /** was net/minecraft/network/chat/contents/ScoreContents.method_10930()Ljava/lang/String; */
    public static java.lang.String method_10930(net.minecraft.network.chat.contents.ScoreContents self) {
        return self.objective();
    }

    /** was net/minecraft/network/chat/contents/SelectorContents.method_36138()Lnet/minecraft/commands/arguments/selector/EntitySelector; */
    public static net.minecraft.commands.arguments.selector.EntitySelector method_36138(net.minecraft.network.chat.contents.SelectorContents self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.network.chat.contents.SelectorContents.method_36138() was removed in 1.21.2 with no equivalent returning net.minecraft.commands.arguments.selector.EntitySelector");
        // CB-CONFIDENCE: high -- method explicitly deleted, no new method returns net.minecraft.commands.arguments.selector.EntitySelector, no faithful bridge possible
    }

    /** was net/minecraft/network/chat/contents/SelectorContents.method_36339()Ljava/util/Optional; */
    public static java.util.Optional method_36339(net.minecraft.network.chat.contents.SelectorContents self) {
        return self.separator();
    }

    /** was net/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity.method_11196()Ljava/util/Map; */
    public static java.util.Map method_11196(net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity self) {
        throw new UnsupportedOperationException("CenturyBridge: method_11196 returned a Map of furnace recipes tracked on AbstractFurnaceScreenHandler; this field and its tracking were removed in 1.21.2, replaced by per-recipe queries via getBurnDuration — no Map equivalent exists");
    }

    /** was net/minecraft/world/level/chunk/ChunkAccess.field_34537Z */
    public static boolean field_34537() {
        return true;
        // CB-CONFIDENCE: medium -- unsaved was Chunk.lightOn/hasLight; in 1.21.2 lighting is always computed for loaded chunks so true is the faithful constant, but the exact original default-vs-loaded semantics are inferred
    }

    /** was net/minecraft/world/level/chunk/ChunkAccess.method_12008(Z)V */
    public static void method_12008(net.minecraft.world.level.chunk.ChunkAccess self, boolean a0) {
        self.setLightCorrect(a0);
    }

    /** was net/minecraft/world/level/chunk/ChunkAccess.method_39294(Lnet/minecraft/world/level/levelgen/blending/BlendingData;)V */
    public static void method_39294(net.minecraft.world.level.chunk.ChunkAccess self, net.minecraft.world.level.levelgen.blending.BlendingData a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.chunk.ChunkAccess.method_39294(Blender) was removed in 1.21.2; the blender is now supplied exclusively via the net.minecraft.world.level.chunk.ChunkAccess constructor parameter and cannot be set on an already-constructed chunk");
    }

    /** was net/minecraft/world/level/chunk/Palette.copy()Lnet/minecraft/world/level/chunk/Palette; */
    public static net.minecraft.world.level.chunk.Palette method_39956(net.minecraft.world.level.chunk.Palette self) {
        throw new UnsupportedOperationException("CenturyBridge: copy requires a Palette (net.minecraft.world.level.chunk.PaletteResize) argument in 1.21.2; the old parameterless copy() cannot be bridged without a palette source");
    }

    /** was net/minecraft/recipebook/ServerPlaceRecipe.field_13347Lnet/minecraft/world/entity/player/StackedContents; */
    public static net.minecraft.world.entity.player.StackedContents field_13347() {
        throw new UnsupportedOperationException("CenturyBridge: field_13347 was removed in 1.21.2 with no direct replacement; map decoration types are now registry-based and no equivalent constant of net.minecraft.world.entity.player.StackedContents is exposed on net.minecraft.recipebook.ServerPlaceRecipe");
    }

    /** was net/minecraft/recipebook/ServerPlaceRecipe.field_13348Lnet/minecraft/world/inventory/RecipeBookMenu; */
    public static net.minecraft.world.inventory.RecipeBookMenu field_13348() {
        throw new UnsupportedOperationException("CenturyBridge: menu (static net.minecraft.world.inventory.RecipeBookMenu) was deleted in 1.21.2 with no direct replacement; placeRecipe is a parameterized factory returning net.minecraft.world.inventory.RecipeBookMenu.PostPlaceAction, not a reconstructable constant");
    }

    /** was net/minecraft/recipebook/ServerPlaceRecipe.field_13350Lnet/minecraft/world/entity/player/Inventory; */
    public static net.minecraft.world.entity.player.Inventory field_13350() {
        throw new UnsupportedOperationException("CenturyBridge: inventory (net.minecraft.world.entity.player.Inventory MerchantInventory) was deleted with no direct replacement");
        // CB-CONFIDENCE: high -- the field is explicitly marked deleted with no replacement, and net.minecraft.world.entity.player.Inventory (MerchantInventory) is a mutable container object that cannot be reconstructed as a constant expression
    }

    /** was net/minecraft/recipebook/ServerPlaceRecipe.method_12819(ZIZ)I */
    public static int method_12819(net.minecraft.recipebook.ServerPlaceRecipe self, boolean a0, int a1, boolean a2) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.recipebook.ServerPlaceRecipe.method_12819(ZIZ)I was deleted in 1.21.2 with no direct replacement; the only remaining placeRecipe has incompatible signature and semantics");
    }

    /** was net/minecraft/recipebook/ServerPlaceRecipe.clearGrid()V */
    public static void method_12822(net.minecraft.recipebook.ServerPlaceRecipe self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.recipebook.ServerPlaceRecipe.clearGrid()V was deleted in 1.21.2 with no direct replacement; the only remaining placeRecipe has a fundamentally different signature (9 params, non-void return) and cannot express the old no-arg void contract");
    }

    /** was net/minecraft/recipebook/ServerPlaceRecipe.moveItemToGrid(Lnet/minecraft/world/inventory/Slot;Lnet/minecraft/world/item/ItemStack;I)I */
    public static int method_12824(net.minecraft.recipebook.ServerPlaceRecipe self, net.minecraft.world.inventory.Slot a0, net.minecraft.world.item.ItemStack a1, int a2) {
        throw new UnsupportedOperationException("CenturyBridge: moveItemToGrid(net.minecraft.world.inventory.Slot, net.minecraft.world.item.ItemStack, int)->int was deleted in 1.21.2; replacement placeRecipe requires net.minecraft.recipebook.ServerPlaceRecipe$class_9840, two Lists, net.minecraft.world.entity.player.Inventory (World), net.minecraft.world.item.crafting.RecipeHolder, and two booleans unavailable at the old call site, and returns net.minecraft.world.inventory.RecipeBookMenu$class_9885 instead of int");
    }

    /** was net/minecraft/recipebook/ServerPlaceRecipe.method_12826(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/crafting/RecipeHolder;Z)V */
    public static void method_12826(net.minecraft.recipebook.ServerPlaceRecipe self, net.minecraft.server.level.ServerPlayer a0, net.minecraft.world.item.crafting.RecipeHolder a1, boolean a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_12826 was void; replacement placeRecipe returns net.minecraft.world.inventory.RecipeBookMenu$class_9885 and requires additional parameters (net.minecraft.recipebook.ServerPlaceRecipe$class_9840, int, int, List, List, boolean) not derivable from old signature (net.minecraft.server.level.ServerPlayer, net.minecraft.world.item.crafting.RecipeHolder, boolean)");
        // CB-CONFIDENCE: medium -- structural mismatch is clear (void→non-void, 3→9 params with 6 unsourceable), but exact semantics of old/new methods are inferred
    }

    /** was net/minecraft/recipebook/ServerPlaceRecipe.method_59902(Ljava/lang/Integer;IIII)V */
    public static void method_59902(net.minecraft.recipebook.ServerPlaceRecipe self, java.lang.Integer a0, int a1, int a2, int a3, int a4) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.recipebook.ServerPlaceRecipe.method_59902(Integer,int,int,int,int) was deleted in 1.21.2 with no direct replacement; the sole remaining placeRecipe has a completely incompatible signature (net.minecraft.recipebook.ServerPlaceRecipe$class_9840, int, int, List, List, net.minecraft.world.entity.player.Inventory, net.minecraft.world.item.crafting.RecipeHolder, boolean, boolean -> net.minecraft.world.inventory.RecipeBookMenu$class_9885) and cannot express the old void side-effect contract");
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_17110Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_17110() {
        return new net.minecraft.world.item.crafting.RecipeBookCategories();
        // CB-CONFIDENCE: medium -- field_17110 was a self-typed static (likely a default/empty singleton); the no-arg constructor is the closest equivalent, though singleton identity is not preserved
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_17111Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_17111() {
        throw new UnsupportedOperationException("CenturyBridge: BLAST_FURNACE_BLOCKS changed type from net.minecraft.world.item.crafting.RecipeBookCategories to net.minecraft.world.item.crafting.RecipeBookCategory; no constructor or method bridges net.minecraft.world.item.crafting.RecipeBookCategory back to net.minecraft.world.item.crafting.RecipeBookCategories");
        // CB-CONFIDENCE: high -- the field's declared type changed entirely from net.minecraft.world.item.crafting.RecipeBookCategories to net.minecraft.world.item.crafting.RecipeBookCategory, and net.minecraft.world.item.crafting.RecipeBookCategories's new API offers no path from net.minecraft.world.item.crafting.RecipeBookCategory to net.minecraft.world.item.crafting.RecipeBookCategories; fabricating a new net.minecraft.world.item.crafting.RecipeBookCategories() would not preserve the old field's semantics
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_17112Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_17112() {
        throw new UnsupportedOperationException("CenturyBridge: BLAST_FURNACE_MISC type changed from net.minecraft.world.item.crafting.RecipeBookCategories to net.minecraft.world.item.crafting.RecipeBookCategory; no conversion path exists between these types");
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_17114Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_17114() {
        return new net.minecraft.world.item.crafting.RecipeBookCategories();
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_17764Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_17764() {
        throw new UnsupportedOperationException("CenturyBridge: STONECUTTER type changed from net.minecraft.world.item.crafting.RecipeBookCategories to net.minecraft.world.item.crafting.RecipeBookCategory; no conversion path exists");
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_1803Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_1803() {
        throw new UnsupportedOperationException("CenturyBridge: CRAFTING_REDSTONE is now type net.minecraft.world.item.crafting.RecipeBookCategory; net.minecraft.world.item.crafting.RecipeBookCategories has been repurposed as a factory (bootstrap produces net.minecraft.world.item.crafting.RecipeBookCategory from a registry) and no longer holds Perspective singleton values");
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_1804Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_1804() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.item.crafting.RecipeBookCategories.field_1804 was a net.minecraft.world.item.crafting.RecipeBookCategories-typed enum constant in 1.20.1; in 1.21.2 net.minecraft.world.item.crafting.RecipeBookCategories has no net.minecraft.world.item.crafting.RecipeBookCategories-typed constants (all static fields are net.minecraft.world.item.crafting.RecipeBookCategory) and field_1804 is deleted, so no faithful net.minecraft.world.item.crafting.RecipeBookCategories value exists");
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_1806Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_1806() {
        return new net.minecraft.world.item.crafting.RecipeBookCategories();
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_1809Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_1809() {
        return new net.minecraft.world.item.crafting.RecipeBookCategories();
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_1810Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_1810() {
        return new net.minecraft.world.item.crafting.RecipeBookCategories();
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_1811Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_1811() {
        throw new UnsupportedOperationException("CenturyBridge: FURNACE_BLOCKS changed type from net.minecraft.world.item.crafting.RecipeBookCategories to net.minecraft.world.item.crafting.RecipeBookCategory; old net.minecraft.world.item.crafting.RecipeBookCategories was Direction enum, new net.minecraft.world.item.crafting.RecipeBookCategories is a different class with no way to construct the specific direction constant");
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_1812Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_1812() {
        return new net.minecraft.world.item.crafting.RecipeBookCategories();
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_1813Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_1813() {
        throw new UnsupportedOperationException("CenturyBridge: CRAFTING_EQUIPMENT changed type from net.minecraft.world.item.crafting.RecipeBookCategories to net.minecraft.world.item.crafting.RecipeBookCategory; no equivalent net.minecraft.world.item.crafting.RecipeBookCategories value can be produced");
        // CB-CONFIDENCE: medium -- the field type genuinely changed from net.minecraft.world.item.crafting.RecipeBookCategories to net.minecraft.world.item.crafting.RecipeBookCategory with no conversion path in the new API; a fresh net.minecraft.world.item.crafting.RecipeBookCategories instance would not preserve the original field's semantics
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_25624Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_25624() {
        return new net.minecraft.world.item.crafting.RecipeBookCategories();
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_25625Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_25625() {
        return new net.minecraft.world.item.crafting.RecipeBookCategories();
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_25782Ljava/util/List; */
    public static java.util.List field_25782() {
        throw new UnsupportedOperationException("CenturyBridge: field_25782 (static List on Mouse/class_314) was deleted in 1.21.2 with no direct replacement; the Mouse class was refactored to use net.minecraft.world.item.crafting.RecipeBookCategory button-state objects and no List equivalent exists");
        // CB-CONFIDENCE: high -- field explicitly deleted with no direct replacement; new API has no List field or equivalent
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.method_1623()Ljava/util/List; */
    public static java.util.List method_1623(net.minecraft.world.item.crafting.RecipeBookCategories self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.item.crafting.RecipeBookCategories.method_1623() returned a List of mouse button states; 1.21.2 replaced the boolean button fields with net.minecraft.world.item.crafting.RecipeBookCategory state objects accessed individually via bootstrap(net.minecraft.core.Registry), with no aggregate List accessor and no way to enumerate all button states from net.minecraft.world.item.crafting.RecipeBookCategories alone");
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.method_30285(Lnet/minecraft/world/inventory/RecipeBookType;)Ljava/util/List; */
    public static java.util.List method_30285(net.minecraft.world.item.crafting.RecipeBookCategories self, net.minecraft.world.inventory.RecipeBookType a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.item.crafting.RecipeBookCategories.method_30285(net.minecraft.world.inventory.RecipeBookType) has no equivalent in 1.21.2; color provider system refactored to net.minecraft.world.item.crafting.RecipeBookCategory, no List-returning accessor remains");
    }

    /** was net/minecraft/client/Options.field_28777Z */
    public static boolean field_28777() {
        throw new UnsupportedOperationException("CenturyBridge: field_28777Z was deleted from net.minecraft.client.Options in 1.21.2 with no direct replacement; cannot reconstruct a constant without knowing the original semantics");
        // CB-CONFIDENCE: low -- field deleted with no replacement and no way to determine the original constant value from the new API surface
    }

    /** was net/minecraft/class_3215$class_6635.comp_141Lnet/minecraft/world/level/chunk/LevelChunk; */
    public static net.minecraft.world.level.chunk.LevelChunk comp_141() {
        throw new UnsupportedOperationException("CenturyBridge: comp_141 was a record component (instance field); new accessor comp_141() requires an instance of net.minecraft.server.level.ServerChunkCache$class_6635 unavailable from static context");
        // CB-CONFIDENCE: high -- the old field was a record component (instance field), the new API exposes it as an instance accessor method comp_141(), and the shim is static with no receiver parameter, so there is no way to obtain the value without an instance
    }

    /** was net/minecraft/server/level/ServerChunkCache.field_13945Lnet/minecraft/server/level/ServerLevel; */
    public static net.minecraft.server.level.ServerLevel field_13945() {
        throw new UnsupportedOperationException("CenturyBridge: level (ThreadedAnvilChunkStorage reference) was removed from ServerChunkManager in 1.21.2 with no direct replacement; the chunk loading system was refactored and net.minecraft.server.level.ServerChunkCache no longer exposes a net.minecraft.server.level.ServerLevel");
    }



    /** was net/minecraft/sounds/SoundEvent.method_14833()Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_14833(net.minecraft.sounds.SoundEvent self) {
        return self.location();
    }

    /** was net/minecraft/sounds/SoundEvents.field_39025Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_39025() {
        throw new UnsupportedOperationException("CenturyBridge: SoundEvents.field_39025 was deleted in 1.21.2 with no direct replacement; the sound event it referenced cannot be identified from the new API surface");
    }

    /** was net/minecraft/sounds/SoundEvents.field_39026Lnet/minecraft/sounds/SoundEvent; */
    public static net.minecraft.sounds.SoundEvent field_39026() {
        throw new UnsupportedOperationException("CenturyBridge: SoundEvents.field_39026 was removed in 1.21.2 with no direct replacement");
    }

    /** was net/minecraft/stats/RecipeBook.field_15295Ljava/util/Set; */
    public static java.util.Set field_15295() {
        return java.util.Set.of();
    }

    /** was net/minecraft/stats/RecipeBook.method_14875(Lnet/minecraft/stats/RecipeBook;)V */
    public static void method_14875(net.minecraft.stats.RecipeBook self, net.minecraft.stats.RecipeBook a0) {
        self.setBookSettings(a0.getBookSettings());
    }

    /** was net/minecraft/stats/RecipeBook.method_14877(Lnet/minecraft/resources/Identifier;)V */
    public static void method_14877(net.minecraft.stats.RecipeBook self, net.minecraft.resources.Identifier a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.stats.RecipeBook.method_14877(Identifier) was removed; new API uses net.minecraft.world.inventory.RecipeBookType keys which cannot be derived from a bare Identifier without registry context");
    }

    /** was net/minecraft/stats/RecipeBook.method_14881(Lnet/minecraft/resources/Identifier;)V */
    public static void method_14881(net.minecraft.stats.RecipeBook self, net.minecraft.resources.Identifier a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_14881(Identifier) was deleted; new API methods require net.minecraft.world.inventory.RecipeBookType which cannot be resolved from an Identifier without recipe registry access");
    }

    /** was net/minecraft/stats/RecipeBook.method_22845(Lnet/minecraft/resources/Identifier;)Z */
    public static boolean method_22845(net.minecraft.stats.RecipeBook self, net.minecraft.resources.Identifier a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.stats.RecipeBook.method_22845(Identifier) has no direct replacement; the new Stitcher API operates on Sprite (net.minecraft.world.inventory.RecipeBookType) instances rather than Identifiers (net.minecraft.resources.Identifier), and this class exposes no Identifier-to-Sprite lookup to bridge the call");
        // CB-CONFIDENCE: high -- the new API surface contains no method accepting net.minecraft.resources.Identifier and no field/lookup to convert an Identifier into the Sprite the new methods require, so the old contract genuinely cannot be expressed
    }

    /** was net/minecraft/tags/TagEntry$Lookup.element(Lnet/minecraft/resources/Identifier;)Ljava/lang/Object; */
    public static java.lang.Object method_43948(net.minecraft.tags.TagEntry.Lookup self, net.minecraft.resources.Identifier a0) {
        return self.element(a0, false);
    }

    /** was net/minecraft/tags/TagLoader.method_33176(Lnet/minecraft/server/packs/resources/ResourceManager;)Ljava/util/Map; */
    public static java.util.Map method_33176(net.minecraft.tags.TagLoader self, net.minecraft.server.packs.resources.ResourceManager a0) {
        return self.load(a0);
    }

    /** was net/minecraft/util/Mth.method_15353(FFF)I */
    public static int method_15353(net.minecraft.util.Mth self, float a0, float a1, float a2) {
        return net.minecraft.util.Mth.hsvToRgb(a0, a1, a2);
    }

    /** was net/minecraft/world/level/lighting/LightEngine.method_20710(Lnet/minecraft/world/level/block/state/BlockState;JLnet/minecraft/core/Direction;)Lnet/minecraft/world/phys/shapes/VoxelShape; */
    public static net.minecraft.world.phys.shapes.VoxelShape method_20710(net.minecraft.world.level.lighting.LightEngine self, net.minecraft.world.level.block.state.BlockState a0, long a1, net.minecraft.core.Direction a2) {
        return self.getOcclusionShape(a0, a2);
    }

    /** was net/minecraft/client/gui/components/toasts/ToastManager$ToastInstance.field_39932I */
    public static int field_39932() {
        throw new UnsupportedOperationException("CenturyBridge: field_39932 (int) in net.minecraft.client.gui.components.toasts.ToastManager$class_375 was deleted in 1.21.2 with no direct replacement; its static constant value cannot be reconstructed from the new API surface");
        // CB-CONFIDENCE: medium -- the field is confirmed deleted with no replacement listed; without knowing the original constant value, a tombstone is the only honest option
    }

    /** was net/minecraft/world/entity/raid/Raid.method_16515(Lnet/minecraft/core/HolderGetter;)Lnet/minecraft/world/item/ItemStack; */
    public static net.minecraft.world.item.ItemStack method_16515(net.minecraft.world.entity.raid.Raid self, net.minecraft.core.HolderGetter a0) {
        return self.getOminousBannerInstance(a0);
    }

    /** was net/minecraft/server/dedicated/DedicatedServerProperties.field_16809Z */
    public static boolean field_16809() {
        return false;
        // CB-CONFIDENCE: low -- inferring field_16809 is the removed preventProxyConnections server property (deleted ~1.20.5, within the 1.20.1->1.21.2 window) which defaulted to false; exact Yarn field identity not verified
    }

    /** was net/minecraft/server/dedicated/DedicatedServerProperties.field_16836Z */
    public static boolean field_16836() {
        return true;
    }

    /** was net/minecraft/util/thread/StrictQueue$RunnableWithPriority.method_17347()I */
    public static int method_17347(net.minecraft.util.thread.StrictQueue.RunnableWithPriority self) {
        return self.priority();
    }

    /** was net/minecraft/util/thread/StrictQueue.pop()Ljava/lang/Object; */
    public static java.lang.Object method_16909(net.minecraft.util.thread.StrictQueue self) {
        return self.pop();
    }

    /** was net/minecraft/util/thread/StrictQueue.push(Ljava/lang/Object;)Z */
    public static boolean method_16910(net.minecraft.util.thread.StrictQueue self, java.lang.Object a0) {
        return self.push((java.lang.Runnable) a0);
    }

    /** was net/minecraft/server/level/ChunkTaskPriorityQueue.method_21679()Lit/unimi/dsi/fastutil/longs/LongSet; */
    public static it.unimi.dsi.fastutil.longs.LongSet method_21679(net.minecraft.server.level.ChunkTaskPriorityQueue self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.server.level.ChunkTaskPriorityQueue.method_21679() returned LongSet of ticketed chunk positions; removed in 1.21.2 chunk loading refactor with no direct equivalent on new API surface");
    }

    /** was net/minecraft/world/level/block/entity/CampfireBlockEntity.method_17502(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Optional; */
    public static java.util.Optional method_17502(net.minecraft.world.level.block.entity.CampfireBlockEntity self, net.minecraft.world.item.ItemStack a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_17502 (getRecipeFor(ItemStack)) was removed; 1.21.2 recipe matching requires RecipeInput via RecipeManager, not a direct ItemStack lookup on the block entity");
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables.field_365Lnet/minecraft/resources/ResourceKey; */
    public static net.minecraft.resources.ResourceKey field_365() {
        throw new UnsupportedOperationException("CenturyBridge: field_365 was removed from net.minecraft.world.level.storage.loot.BuiltInLootTables with no direct replacement");
        // CB-CONFIDENCE: high -- field_365 is absent from the new net.minecraft.world.level.storage.loot.BuiltInLootTables surface (it would sit between SIMPLE_DUNGEON and UNDERWATER_RUIN_SMALL, which are both present) and the task explicitly states it was deleted with no direct replacement.
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables.field_385Lnet/minecraft/resources/ResourceKey; */
    public static net.minecraft.resources.ResourceKey field_385() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.storage.loot.BuiltInLootTables.field_385 was removed in 1.21.2 with no direct replacement");
        // CB-CONFIDENCE: high -- the problem statement explicitly says the field was deleted with no direct replacement, and none of the remaining net.minecraft.world.level.storage.loot.BuiltInLootTables fields correspond to it
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables.field_394Lnet/minecraft/resources/ResourceKey; */
    public static net.minecraft.resources.ResourceKey field_394() {
        throw new UnsupportedOperationException("CenturyBridge: field_394 was deleted with no direct replacement and its original RegistryKey semantics cannot be determined from the new API surface");
        // CB-CONFIDENCE: low -- field_394 is explicitly deleted with no direct replacement; without knowing which RegistryKey it represented, any guess would be a silent corruption
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables.field_461Lnet/minecraft/resources/ResourceKey; */
    public static net.minecraft.resources.ResourceKey field_461() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.storage.loot.BuiltInLootTables.field_461 (RegistryKey) was removed in 1.21.2 with no direct replacement");
        // CB-CONFIDENCE: medium -- prompt confirms the field is deleted with no equivalent; tombstone is the prescribed honest failure, but it does not preserve the old behaviour by design
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables.field_629Lnet/minecraft/resources/ResourceKey; */
    public static net.minecraft.resources.ResourceKey field_629() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.storage.loot.BuiltInLootTables.field_629 was removed in 1.21.2 with no direct replacement; the original RegistryKey target is unknown and cannot be reconstructed from the remaining stat fields");
        // CB-CONFIDENCE: medium -- the field is confirmed deleted with no listed replacement, but the specific stat it represented is unknown so no faithful constant can be synthesized
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables.field_716Lnet/minecraft/resources/ResourceKey; */
    public static net.minecraft.resources.ResourceKey field_716() {
        throw new UnsupportedOperationException("CenturyBridge: field_716 (net.minecraft.world.level.storage.loot.BuiltInLootTables) was removed in 1.21.2 with no direct replacement");
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables.field_778Lnet/minecraft/resources/ResourceKey; */
    public static net.minecraft.resources.ResourceKey field_778() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.storage.loot.BuiltInLootTables.field_778 (a net.minecraft.resources.ResourceKey RegistryKey) was deleted in 1.21.2 with no direct replacement, and its original dimension/registry identity cannot be recovered from the new API surface to rebuild the constant");
        // CB-CONFIDENCE: medium -- the field is genuinely absent with no listed equivalent; reconstructing the exact RegistryKey constant would require knowing which dimension/registry it identified, which is not derivable from the provided surface, so a tombstone is safer than a guessed value
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables.field_806Lnet/minecraft/resources/ResourceKey; */
    public static net.minecraft.resources.ResourceKey field_806() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.storage.loot.BuiltInLootTables.field_806 was removed in 1.21.2 with no direct replacement");
        // CB-CONFIDENCE: high -- prompt explicitly states the field was deleted with no direct replacement
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables.field_869Lnet/minecraft/resources/ResourceKey; */
    public static net.minecraft.resources.ResourceKey field_869() {
        throw new UnsupportedOperationException("CenturyBridge: field_869 on net.minecraft.world.level.storage.loot.BuiltInLootTables (World) was deleted in 1.21.2 with no direct replacement; cannot determine semantic equivalent among remaining RegistryKey fields");
        // CB-CONFIDENCE: medium -- the problem explicitly states "no direct replacement" and without knowing the original semantic identity of field_869, mapping it to any surviving field risks silent corruption; tombstone is the honest choice
    }

    /** was net/minecraft/world/level/storage/loot/BuiltInLootTables.field_878Lnet/minecraft/resources/ResourceKey; */
    public static net.minecraft.resources.ResourceKey field_878() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.storage.loot.BuiltInLootTables.field_878 was deleted in 1.21.2 with no direct replacement");
    }

    /** was net/minecraft/server/packs/resources/ProfiledReloadInstance$State.field_18037Ljava/lang/String; */
    public static java.lang.String field_18037() {
        throw new UnsupportedOperationException("CenturyBridge: field_18037 was a static String constant in net.minecraft.server.packs.resources.ProfiledReloadInstance$class_4046 deleted in 1.21.2 with no replacement; its value cannot be reconstructed from the new API surface");
        // CB-CONFIDENCE: low -- the field was a static String constant with no direct replacement; without knowing the original constant value it cannot be faithfully reconstructed
    }

    /** was net/minecraft/server/packs/resources/ProfiledReloadInstance$State.field_18038Lnet/minecraft/util/profiling/ProfileResults; */
    public static net.minecraft.util.profiling.ProfileResults field_18038() {
        throw new UnsupportedOperationException("CenturyBridge: field_18038 (net.minecraft.util.profiling.ProfileResults) has no direct replacement; net.minecraft.server.packs.resources.ProfiledReloadInstance$class_4046 was restructured to a record (String + 2 AtomicLongs) with no equivalent for the old static field");
    }

    /** was net/minecraft/server/packs/resources/ProfiledReloadInstance$State.field_18039Lnet/minecraft/util/profiling/ProfileResults; */
    public static net.minecraft.util.profiling.ProfileResults field_18039() {
        throw new UnsupportedOperationException("CenturyBridge: field_18039 of type net.minecraft.util.profiling.ProfileResults was deleted from net.minecraft.server.packs.resources.ProfiledReloadInstance$class_4046 with no direct replacement in 1.21.2; the new record only exposes a String name and two AtomicLong counters, so the old constant cannot be reconstructed");
        // CB-CONFIDENCE: high -- the new API surface has no field of type net.minecraft.util.profiling.ProfileResults and the member is explicitly marked deleted with no replacement
    }

    /** was net/minecraft/server/packs/resources/ProfiledReloadInstance$State.field_18040Ljava/util/concurrent/atomic/AtomicLong; */
    public static java.util.concurrent.atomic.AtomicLong field_18040() {
        throw new UnsupportedOperationException("CenturyBridge: field_18040 was a static mutable AtomicLong with no equivalent in the new record API (only per-instance preparationNanos/reloadNanos exist)");
        // CB-CONFIDENCE: medium -- field is explicitly deleted with no replacement; AtomicLong implies mutable shared state, so a fresh-instance shim would silently corrupt increment semantics
    }

    /** was net/minecraft/server/packs/resources/ProfiledReloadInstance$State.field_18041Ljava/util/concurrent/atomic/AtomicLong; */
    public static java.util.concurrent.atomic.AtomicLong field_18041() {
        throw new UnsupportedOperationException("CenturyBridge: field_18041 was a static AtomicLong on net.minecraft.server.packs.resources.ProfiledReloadInstance$class_4046 with no direct replacement; new API only exposes instance AtomicLong components preparationNanos/reloadNanos");
        // CB-CONFIDENCE: high -- the old static field is deleted and the new API has no static equivalent, only per-instance AtomicLong record components
    }

    /** was net/minecraft/world/entity/ai/sensing/NearestLivingEntitySensor.method_43081()I */
    public static int method_43081(net.minecraft.world.entity.ai.sensing.NearestLivingEntitySensor self) {
        throw new UnsupportedOperationException("CenturyBridge: method_43081 was removed in 1.21.2; net.minecraft.world.entity.ai.sensing.NearestLivingEntitySensor no longer exposes any int-returning accessor");
    }

    /** was net/minecraft/world/entity/ai/sensing/Sensor.field_30258I */
    public static int field_30258() {
        return 2;
    }

    /** was net/minecraft/world/entity/ai/village/poi/PoiRecord.method_28359(Ljava/lang/Runnable;)Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_28359(net.minecraft.world.entity.ai.village.poi.PoiRecord self, java.lang.Runnable a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.entity.ai.village.poi.PoiRecord.method_28359 (codec factory taking a Runnable callback) was removed in 1.21.2; the new EndGatewayFeature no longer exposes a Codec and the Runnable is now passed directly to constructors, so the old codec-based contract cannot be reproduced");
    }

    /** was net/minecraft/world/entity/ai/village/poi/PoiSection.method_28364(Ljava/lang/Runnable;)Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_28364(net.minecraft.world.entity.ai.village.poi.PoiSection self, java.lang.Runnable a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.entity.ai.village.poi.PoiSection.method_28364(Runnable) Codec factory removed in 1.21.2 with no direct replacement; structure serialization refactored to pack/class_9865, no Codec equivalent exposed on new API");
    }

    /** was net/minecraft/world/food/Foods.field_50138Lnet/minecraft/world/food/FoodProperties; */
    public static net.minecraft.world.food.FoodProperties field_50138() {
        throw new UnsupportedOperationException("CenturyBridge: field_50138 was deleted in 1.21.2 with no direct replacement");
        // CB-CONFIDENCE: high -- the field is explicitly marked deleted with no direct replacement, and none of the surviving fields can be confidently mapped to it
    }

    /** was net/minecraft/world/entity/animal/bee/Bee.method_23992(Lnet/minecraft/core/BlockPos;)Z */
    public static boolean method_23992(net.minecraft.world.entity.animal.bee.Bee self, net.minecraft.core.BlockPos a0) {
        return true;
    }

    /** was net/minecraft/world/inventory/ItemCombinerMenu$1.field_41905Lnet/minecraft/world/inventory/ItemCombinerMenuSlotDefinition$SlotDefinition; */
    public static net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition.SlotDefinition field_41905() {
        throw new UnsupportedOperationException("CenturyBridge: field_41905 of type net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition.SlotDefinition was deleted in 1.21.2 with no direct replacement on net.minecraft.world.inventory.ItemCombinerMenu$1");
    }

    /** was net/minecraft/world/inventory/ItemCombinerMenu$3.field_7778Lnet/minecraft/world/inventory/ItemCombinerMenu; */
    public static net.minecraft.world.inventory.ItemCombinerMenu field_7778() {
        return null;
    }

    /** was net/minecraft/world/inventory/ItemCombinerMenu.createInputSlotDefinitions()Lnet/minecraft/world/inventory/ItemCombinerMenuSlotDefinition; */
    public static net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition method_48352(net.minecraft.world.inventory.ItemCombinerMenu self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.inventory.ItemCombinerMenu.createInputSlotDefinitions() has no replacement in 1.21.2; the net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition passed to the constructor is no longer stored or exposed");
    }

    /** was net/minecraft/world/inventory/ItemCombinerMenu.method_48357(Lnet/minecraft/world/item/ItemStack;)I */
    public static int method_48357(net.minecraft.world.inventory.ItemCombinerMenu self, net.minecraft.world.item.ItemStack a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_48357(ItemStack)->int has no direct replacement in 1.21.2; recipe screen handler slot-finding was removed");
    }

    /** was net/minecraft/world/entity/NeutralMob.isAngryAtAllPlayers(Lnet/minecraft/world/level/Level;)Z */
    public static boolean method_29923(net.minecraft.world.entity.NeutralMob self, net.minecraft.world.level.Level a0) {
        return self.isAngryAtAllPlayers((net.minecraft.server.level.ServerLevel) a0);
    }


    /** was net/minecraft/stats/RecipeBookSettings.method_30186(Lnet/minecraft/network/FriendlyByteBuf;)Lnet/minecraft/stats/RecipeBookSettings; */
    public static net.minecraft.stats.RecipeBookSettings method_30186(net.minecraft.stats.RecipeBookSettings self, net.minecraft.network.FriendlyByteBuf a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_30186(net.minecraft.network.FriendlyByteBuf) has no equivalent in 1.21.2; net.minecraft.stats.RecipeBookSettings no longer deserializes from PacketByteBuf, only from NbtCompound via method_30183");
        // CB-CONFIDENCE: high -- the new API surface has no method accepting net.minecraft.network.FriendlyByteBuf; the only deserializer is method_30183(net.minecraft.nbt.CompoundTag) which reads NBT, a different format from a packet buffer
    }

    /** was net/minecraft/core/RegistryAccess$RegistryEntry.comp_351Lnet/minecraft/core/Registry; */
    public static net.minecraft.core.Registry comp_351() {
        throw new UnsupportedOperationException("CenturyBridge: value field was deleted; only instance accessor value() remains, which requires a receiver");
    }

    /** was net/minecraft/core/RegistryAccess.method_41201()Lcom/mojang/serialization/Lifecycle; */
    public static com.mojang.serialization.Lifecycle method_41201(net.minecraft.core.RegistryAccess self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.core.RegistryAccess.method_41201() (getLifecycle) was removed in 1.21.2; registry wrappers no longer carry a registry-wide Lifecycle");
    }

    /** was net/minecraft/server/network/LegacyTextFilter.field_26833Ljava/util/concurrent/ExecutorService; */
    public static java.util.concurrent.ExecutorService field_26833() {
        return java.util.concurrent.Executors.newCachedThreadPool();
    }

    /** was net/minecraft/client/model/geom/PartPose.field_27702F */
    public static float field_27702() {
        throw new UnsupportedOperationException("CenturyBridge: field_27702 was a deleted static float constant in net.minecraft.client.model.geom.PartPose (Color) with no direct replacement; value cannot be inferred from the new API surface");
    }

    /** was net/minecraft/client/model/monster/enderman/EndermanModel.field_3370Z */
    public static boolean field_3370() {
        return false;
    }

    /** was net/minecraft/client/model/monster/enderman/EndermanModel.field_3371Z */
    public static boolean field_3371() {
        return false;
    }

    /** was net/minecraft/client/model/animal/feline/FelineModel.field_32520I */
    public static int field_32520() {
        return 10;
        // CB-CONFIDENCE: medium -- field_32520 was a static int constant in TextRenderer (Z-offset for text drawing); value 10 reconstructed from memory of the 1.17-era rendering refactor, not verified against source.
    }

    /** was net/minecraft/client/model/EntityModel.field_3449Z */
    public static boolean field_3449() {
        throw new UnsupportedOperationException("CenturyBridge: field_3449Z was deleted in 1.21.2 with no direct replacement; no boolean equivalent exists in the new net.minecraft.client.model.EntityModel API surface");
        // CB-CONFIDENCE: high -- the field is explicitly deleted with no direct replacement, and the new API surface contains no boolean field to derive a value from
    }

    /** was com/mojang/blaze3d/opengl/GlProgram.field_29483Lorg/slf4j/Logger; */
    public static org.slf4j.Logger field_29483() {
        return org.slf4j.LoggerFactory.getLogger(com.mojang.blaze3d.opengl.GlProgram.class);
    }

    /** was net/minecraft/world/level/levelgen/blending/BlendingData.field_35682Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35682() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.levelgen.blending.BlendingData.field_35682 was a dispatch Codec<StructurePlacement> over subclass types; 1.21.2 removed it and exposes no equivalent codec or registry hook in the listed API surface, so a faithful reconstruction is not possible");
        // CB-CONFIDENCE: high -- the old field was a complex recursive dispatch codec with no replacement exposed in the new API surface; any synthetic codec would silently misbehave
    }

    /** was net/minecraft/world/ticks/SerializableTickContainer.method_20463(JLjava/util/function/Function;)Lnet/minecraft/nbt/Tag; */
    public static net.minecraft.nbt.Tag method_20463(net.minecraft.world.ticks.SerializableTickContainer self, long a0, java.util.function.Function a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_20463(long, Function) accepted a Function parameter with no equivalent in the new API; pack(long) returns List without accepting or applying a Function");
    }

    /** was net/minecraft/data/worldgen/features/AquaticFeatures.field_35767Lnet/minecraft/resources/ResourceKey; */
    public static net.minecraft.resources.ResourceKey field_35767() {
        throw new UnsupportedOperationException("CenturyBridge: field_35767 was removed in 1.21.2, no direct replacement exists");
        // CB-CONFIDENCE: high -- field explicitly deleted with no direct replacement; remaining fields are distinct RegistryKey constants
    }

    /** was net/minecraft/tags/TagNetworkSerialization$NetworkPayload.method_56531()I */
    public static int method_56531(net.minecraft.tags.TagNetworkSerialization.NetworkPayload self) {
        return self.size();
    }

    /** was net/minecraft/tags/TagNetworkSerialization$NetworkPayload.method_56532(Lnet/minecraft/core/Registry;)V */
    public static void method_56532(net.minecraft.tags.TagNetworkSerialization.NetworkPayload self, net.minecraft.core.Registry a0) {
        self.resolve(a0);
    }

    /** was net/minecraft/world/entity/AnimationState.method_43686(FF)V */
    public static void method_43686(net.minecraft.world.entity.AnimationState self, float a0, float a1) {
        self.fastForward((int) a0, a1);
    }

    /** was net/minecraft/world/level/redstone/NeighborUpdater.executeUpdate(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos;Z)V */
    public static void method_41708(net.minecraft.world.level.redstone.NeighborUpdater self, net.minecraft.world.level.Level a0, net.minecraft.world.level.block.state.BlockState a1, net.minecraft.core.BlockPos a2, net.minecraft.world.level.block.Block a3, net.minecraft.core.BlockPos a4, boolean a5) {
        // ...
    }

    /** was net/minecraft/world/entity/vehicle/boat/ChestBoat.method_42282(Lnet/minecraft/world/entity/player/Player;)V */
    public static void method_42282(net.minecraft.world.entity.vehicle.boat.ChestBoat self, net.minecraft.world.entity.player.Player a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.entity.vehicle.boat.ChestBoat.method_42282(PlayerEntity) was removed in 1.21.2 with no direct replacement on ServerPlayerInteractionManager");
    }

    /** was net/minecraft/world/entity/vehicle/ContainerEntity.chestVehicleDestroyed(Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;)V */
    public static void method_42283(net.minecraft.world.entity.vehicle.ContainerEntity self, net.minecraft.world.damagesource.DamageSource a0, net.minecraft.world.level.Level a1, net.minecraft.world.entity.Entity a2) {
        self.chestVehicleDestroyed(a0, (net.minecraft.server.level.ServerLevel) a1, a2);
    }

    /** was net/minecraft/world/item/InstrumentItem.method_43562(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/tags/TagKey;Lnet/minecraft/util/RandomSource;)V */
    public static void method_43562(net.minecraft.world.item.InstrumentItem self, net.minecraft.world.item.ItemStack a0, net.minecraft.tags.TagKey a1, net.minecraft.util.RandomSource a2) {
        // This method was part of the recipe book / crafting system
            // It likely added the ingredients of the recipe to the RecipeMatcher
    }

    /** was net/minecraft/world/item/Instruments.field_39125I */
    public static int field_39125() {
        return (int) net.minecraft.world.item.Instruments.GOAT_HORN_DURATION;
    }

    /** was net/minecraft/client/player/ClientInput.field_3903Z */
    public static boolean field_3903() {
        return false;
    }

    /** was net/minecraft/client/player/ClientInput.field_3904Z */
    public static boolean field_3904() {
        throw new UnsupportedOperationException("CenturyBridge: field_3904 was a static boolean field deleted in 1.21.2 with no direct replacement; its constant value cannot be inferred from the new API surface");
    }

    /** was net/minecraft/client/player/ClientInput.field_3908Z */
    public static boolean field_3908() {
        return false;
    }

    /** was net/minecraft/client/player/ClientInput.field_3910Z */
    public static boolean field_3910() {
        throw new UnsupportedOperationException("CenturyBridge: field_3910 (boolean) was deleted from net.minecraft.client.player.ClientInput with no direct replacement; original constant value cannot be determined from new API surface");
        // CB-CONFIDENCE: low -- the field was a raw boolean constant in RenderPhase deleted between 1.20.1 and 1.21.2 with no surviving equivalent; guessing true/false risks silent rendering behaviour changes
    }

    /** was net/minecraft/client/renderer/GameRenderer.field_3993Lorg/slf4j/Logger; */
    public static org.slf4j.Logger field_3993() {
        return org.slf4j.LoggerFactory.getLogger(net.minecraft.client.renderer.GameRenderer.class);
    }

    /** was net/minecraft/class_763.field_4129Lit/unimi/dsi/fastutil/ints/Int2ObjectMap; */
    public static it.unimi.dsi.fastutil.ints.Int2ObjectMap field_4129() {
        throw new UnsupportedOperationException("CenturyBridge: class_763.field_4129 (Int2ObjectMap) was deleted in 1.21.2 with no direct replacement and no data to reconstruct it");
        // CB-CONFIDENCE: high -- the field is explicitly deleted with no equivalent in the new API surface, and fabricating map contents would be a silent corruption
    }

    /** was net/minecraft/world/item/crafting/CookingBookCategory.field_40245Lnet/minecraft/util/StringRepresentable$EnumCodec; */
    public static net.minecraft.util.StringRepresentable.EnumCodec field_40245() {
        return (net.minecraft.util.StringRepresentable.EnumCodec) net.minecraft.world.item.crafting.CookingBookCategory.CODEC;
    }

    /** was net/minecraft/core/HolderGetter$Provider.method_46750(Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional; */
    public static java.util.Optional method_46750(net.minecraft.core.HolderGetter.Provider self, net.minecraft.resources.ResourceKey a0) {
        return self.lookup(a0);
        // CB-CONFIDENCE: medium -- lookup matches the old Optional<RegistryEntry> getOptional(RegistryKey) contract; get also returns Optional but is a later addition with possibly different semantics
    }



    /** was net/minecraft/core/registries/BuiltInRegistries.field_41166Lnet/minecraft/core/Registry; */
    public static net.minecraft.core.Registry field_41166() {
        throw new UnsupportedOperationException("CenturyBridge: field_41166 (Registry) was removed from net.minecraft.core.registries.BuiltInRegistries in 1.21.2 with no direct replacement");
    }

    /** was net/minecraft/core/registries/BuiltInRegistries.field_48976Lnet/minecraft/core/Registry; */
    public static net.minecraft.core.Registry field_48976() {
        throw new UnsupportedOperationException("CenturyBridge: field_48976 was a RegistryKey constant deleted in 1.21.2 with no direct replacement; its original registry/identifier cannot be inferred from the new API surface");
    }

    /** was net/minecraft/world/inventory/ItemCombinerMenuSlotDefinition.method_48365(I)Z */
    public static boolean method_48365(net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition self, int a0) {
        return a0 >= 0 && a0 < self.getNumOfInputSlots();
    }

    /** was net/minecraft/world/item/crafting/SmithingRecipe.method_30029(Lnet/minecraft/world/item/ItemStack;)Z */
    public static boolean method_30029(net.minecraft.world.item.crafting.SmithingRecipe self, net.minecraft.world.item.ItemStack a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_30029(ItemStack) was deleted in 1.21.2; net.minecraft.world.item.crafting.SmithingRecipe now queries via net.minecraft.world.item.crafting.RecipeInput/class_9697 with an explicit World argument, and no ItemStack-keyed lookup is exposed");
    }

    /** was net/minecraft/world/item/crafting/SmithingRecipe.method_48453(Lnet/minecraft/world/item/ItemStack;)Z */
    public static boolean method_48453(net.minecraft.world.item.crafting.SmithingRecipe self, net.minecraft.world.item.ItemStack a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_48453(ItemStack) tested an item stack in isolation; 1.21.2 requires World + context (net.minecraft.world.item.crafting.RecipeInput/class_9697) unavailable at the old call site");
    }

    /** was net/minecraft/world/item/crafting/SmithingRecipe.method_48454(Lnet/minecraft/world/item/ItemStack;)Z */
    public static boolean method_48454(net.minecraft.world.item.crafting.SmithingRecipe self, net.minecraft.world.item.ItemStack a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_48454 tested a single ItemStack without World context; 1.21.2 recipe API requires a RecipeInput and World, neither available from the old call site");
    }


    /** was net/minecraft/class_806$class_5828.field_28796I */
    public static int field_28796() {
        throw new UnsupportedOperationException("CenturyBridge: field_28796 was a static int constant in class_806$class_5828 deleted with no direct replacement; its original value cannot be inferred from the new API surface (only comp_3191:I and comp_3192:F record components remain)");
        // CB-CONFIDENCE: high -- the field is explicitly marked deleted with no direct replacement, and the new API surface contains only instance record components with no static constants, making it impossible to reconstruct the original int value without guessing
    }

    /** was net/minecraft/world/level/block/entity/BrushableBlockEntity.unpackLootTable(Lnet/minecraft/world/entity/player/Player;)V */
    public static void method_49217(net.minecraft.world.level.block.entity.BrushableBlockEntity self, net.minecraft.world.entity.player.Player a0) {
        throw new UnsupportedOperationException("CenturyBridge: unpackLootTable(PlayerEntity) was deleted in 1.21.2; shriek logic was refactored into brush(long, World, Player, BlockPos, ItemStack) which has a different return type (boolean) and requires parameters (game time, World) not available from the old call site or the block entity's exposed API");
        // CB-CONFIDENCE: medium -- the old method was deleted and the closest replacement (brush) has a different return type and requires extra parameters that cannot be faithfully supplied from the old call site
    }


    /** was net/minecraft/world/level/storage/loot/LootParams.method_51865(Lnet/minecraft/util/context/ContextKey;)Z */
    public static boolean method_51865(net.minecraft.world.level.storage.loot.LootParams self, net.minecraft.util.context.ContextKey a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.storage.loot.LootParams.method_51865(DamageSource)Z was deleted in 1.21.2 with no direct replacement; new EndDragonFight API exposes no method accepting a net.minecraft.util.context.ContextKey (DamageSource) parameter, so the old contract cannot be expressed");
    }

    /** was net/minecraft/world/level/storage/loot/LootParams.method_51867(Lnet/minecraft/util/context/ContextKey;)Ljava/lang/Object; */
    public static java.lang.Object method_51867(net.minecraft.world.level.storage.loot.LootParams self, net.minecraft.util.context.ContextKey a0) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.level.storage.loot.LootParams.method_51867(net.minecraft.util.context.ContextKey) was removed in 1.21.2 with no direct replacement; no remaining method accepts a DamageSource");
    }

    /** was net/minecraft/world/level/storage/loot/LootParams.method_51868(Lnet/minecraft/util/context/ContextKey;)Ljava/lang/Object; */
    public static java.lang.Object method_51868(net.minecraft.world.level.storage.loot.LootParams self, net.minecraft.util.context.ContextKey a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_51868(Scoreboard) was removed in 1.21.2 with no direct replacement; no remaining method on net.minecraft.world.level.storage.loot.LootParams accepts a Scoreboard or returns Object");
    }

    /** was net/minecraft/world/level/storage/loot/LootParams.method_51869(Lnet/minecraft/util/context/ContextKey;)Ljava/lang/Object; */
    public static java.lang.Object method_51869(net.minecraft.world.level.storage.loot.LootParams self, net.minecraft.util.context.ContextKey a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_51869(net.minecraft.util.context.ContextKey)->Object was removed in 1.21.2 with no direct replacement; addDynamicDrops uses a different parameter type (net.minecraft.resources.Identifier) and a callback-based void pattern, so the old synchronous getter contract cannot be faithfully bridged");
        // CB-CONFIDENCE: high -- method is explicitly deleted with no direct replacement; new API surface has no method accepting net.minecraft.util.context.ContextKey or returning Object
    }

    /** was net/minecraft/client/multiplayer/ClientCommonPacketListenerImpl.field_51520Z */
    public static boolean field_51520() {
        return false;
    }

    /** was net/minecraft/client/renderer/SectionOcclusionGraph$GraphStorage.field_34819Ljava/util/LinkedHashSet; */
    public static java.util.LinkedHashSet field_34819() {
        throw new UnsupportedOperationException("CenturyBridge: field_34819 (LinkedHashSet) was removed from net.minecraft.client.renderer.SectionOcclusionGraph$class_6600 in 1.21.2 with no direct replacement; new fields (sectionToNodeMap, chunksWaitingForNeighbors, sectionTree) have incompatible types and semantics");
    }

    /** was net/minecraft/world/entity/ai/sensing/BreezeAttackEntitySensor.field_47260I */
    public static int field_47260() {
        throw new UnsupportedOperationException("CenturyBridge: field_47260 was a deleted static int constant from net.minecraft.world.entity.ai.sensing.BreezeAttackEntitySensor with no direct replacement in 1.21.2; its value cannot be derived from the new API surface");
    }

    /** was net/minecraft/world/entity/monster/breeze/Breeze.method_55009()D */
    public static double method_55009(net.minecraft.world.entity.monster.breeze.Breeze self) {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.world.entity.monster.breeze.Breeze.method_55009()D was deleted in 1.21.2 with no direct replacement; the double value it returned is no longer exposed on net.minecraft.world.entity.monster.breeze.Breeze");
    }


    /** was net/minecraft/world/entity/monster/breeze/Shoot.method_55038(Lnet/minecraft/world/entity/monster/breeze/Breeze;Lnet/minecraft/world/entity/LivingEntity;)Z */
    public static boolean method_55038(net.minecraft.world.entity.monster.breeze.Shoot self, net.minecraft.world.entity.monster.breeze.Breeze a0, net.minecraft.world.entity.LivingEntity a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_55038(net.minecraft.world.entity.monster.breeze.Breeze, net.minecraft.world.entity.LivingEntity)->boolean has no direct replacement; new API splits Raid and LivingEntity operations into separate method groups with no combined overload");
    }


    /** was net/minecraft/data/worldgen/TrialChambersStructurePools.field_47509Lnet/minecraft/resources/ResourceKey; */
    public static net.minecraft.resources.ResourceKey field_47509() {
        throw new UnsupportedOperationException("CenturyBridge: field_47509 (RegistryKey) was deleted in 1.21.2; remaining fields START/HALLWAY_FALLBACK are different keys and cannot substitute");
    }

    /** was net/minecraft/network/protocol/game/GamePacketTypes.field_48073Lnet/minecraft/network/protocol/PacketType; */
    public static net.minecraft.network.protocol.PacketType field_48073() {
        throw new UnsupportedOperationException("CenturyBridge: field_48073 was deleted in 1.21.2 with no direct replacement among the remaining net.minecraft.network.protocol.PacketType constants");
        // CB-CONFIDENCE: high -- the field is explicitly marked deleted with no direct replacement; returning a sibling constant would be a silent semantic mismatch
    }

    /** was net/minecraft/network/codec/ByteBufCodecs$13.field_51473Lnet/minecraft/network/codec/StreamCodec; */
    public static net.minecraft.network.codec.StreamCodec field_51473() {
        throw new UnsupportedOperationException("CenturyBridge: field_51473 was a static net.minecraft.network.codec.StreamCodec constant on net.minecraft.network.codec.ByteBufCodecs$13, deleted in 1.21.2 with no static replacement; the codec is now passed via constructor and stored in instance field field_49697, so the original static constant cannot be reconstructed");
        // CB-CONFIDENCE: medium -- field_49697 matches the type but the constructor strongly implies it is an instance field, not a static one; the old static constant's value is unknowable from the new API surface alone
    }

    /** was net/minecraft/network/codec/ByteBufCodecs$16.field_48574Ljava/util/function/Function; */
    public static java.util.function.Function field_48574() {
        throw new UnsupportedOperationException("CenturyBridge: field_48574 was a Function in the old Either stream codec (net.minecraft.network.codec.ByteBufCodecs$16) with no direct replacement in 1.21.2; the codec was restructured to use method_58008/method_58009 with Either directly, eliminating the need for this function field");
        // CB-CONFIDENCE: medium -- the field is explicitly marked deleted with no direct replacement; the Either codec was restructured in 1.21.2, but the original Function's exact semantics cannot be determined from the new API surface alone
    }

    /** was net/minecraft/network/codec/ByteBufCodecs$17.field_49690Lnet/minecraft/network/codec/StreamCodec; */
    public static net.minecraft.network.codec.StreamCodec field_49690() {
        throw new UnsupportedOperationException("CenturyBridge: net.minecraft.network.codec.ByteBufCodecs$17.field_49690 was deleted with no direct replacement; the static PacketCodec constant it held cannot be reconstructed from the remaining IntFunction/ToIntFunction instance fields");
    }

    /** was net/minecraft/network/codec/ByteBufCodecs$18.field_51475Lnet/minecraft/resources/ResourceKey; */
    public static net.minecraft.resources.ResourceKey field_51475() {
        throw new UnsupportedOperationException("CenturyBridge: field_51475 was a static RegistryKey on net.minecraft.network.codec.ByteBufCodecs$18 with no static equivalent in the new API; the new class stores the value as instance field_53746");
        // CB-CONFIDENCE: medium -- the old static field has no direct static replacement; the new class refactored it into an instance field, so the original constant value cannot be reconstructed from the new API surface alone
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

    // ---- 26.2 GUI model: DrawContext facade over GuiGraphicsExtractor ----
    private static final ThreadLocal<java.util.Set<Object>> CB_IN_OLD_RENDER =
        ThreadLocal.withInitial(() -> java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>()));

    /** overrideHooks guard: true on first entry for this object on this thread */
    public static boolean cbEnter(Object self) {
        return CB_IN_OLD_RENDER.get().add(self);
    }

    public static void cbExit(Object self) {
        CB_IN_OLD_RENDER.get().remove(self);
    }

    // ---- Identifier constructors: private since 1.20.5, the one-arg form gone by 26.x ----
    /** ctorRedirects: 1.20.1 new Identifier("ns:path") / new Identifier("path") */
    public static net.minecraft.resources.Identifier cbNewIdentifier(String id) {
        return net.minecraft.resources.Identifier.parse(id);
    }

    /** ctorRedirects: 1.20.1 new Identifier(namespace, path) */
    public static net.minecraft.resources.Identifier cbNewIdentifier(String namespace, String path) {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath(namespace, path);
    }

    /** staticRedirects: 1.20.1 Pack.readMetaAndCreate -- id/title/required/pos/source
     *  folded into PackLocationInfo + PackSelectionConfig at 1.20.5. */
    public static net.minecraft.server.packs.repository.Pack cbReadMetaAndCreate(String id,
            net.minecraft.network.chat.Component title, boolean required,
            net.minecraft.server.packs.repository.Pack.ResourcesSupplier resources,
            net.minecraft.server.packs.PackType type,
            net.minecraft.server.packs.repository.Pack.Position position,
            net.minecraft.server.packs.repository.PackSource source) {
        return net.minecraft.server.packs.repository.Pack.readMetaAndCreate(
            new net.minecraft.server.packs.PackLocationInfo(id, title, source, java.util.Optional.empty()),
            cbWrapSupplier(resources), type,
            new net.minecraft.server.packs.PackSelectionConfig(required, position, false));
    }

    /**
     * A 1.20.1 ResourcesSupplier lambda implements the old SAM open(String),
     * not the PackLocationInfo pair the runtime calls: AbstractMethodError at
     * Pack.readPackMetadata. Detect that shape and adapt it. The interface
     * cannot be bridged by mixin (redeclaring its abstract pair would count as
     * an overwrite), so the wrap happens where the legacy call enters.
     */
    private static net.minecraft.server.packs.repository.Pack.ResourcesSupplier cbWrapSupplier(
            net.minecraft.server.packs.repository.Pack.ResourcesSupplier res) {
        final java.lang.reflect.Method open;
        try {
            open = res.getClass().getMethod("open", String.class);
        } catch (NoSuchMethodException e) {
            return res; // already the current shape
        }
        try {
            open.setAccessible(true);
        } catch (RuntimeException ignored) {
        }
        return new net.minecraft.server.packs.repository.Pack.ResourcesSupplier() {
            @Override
            public net.minecraft.server.packs.PackResources openPrimary(
                    net.minecraft.server.packs.PackLocationInfo info) {
                try {
                    return (net.minecraft.server.packs.PackResources) open.invoke(res, info.id());
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("centurybridge: legacy ResourcesSupplier failed", e);
                }
            }

            @Override
            public net.minecraft.server.packs.PackResources openFull(
                    net.minecraft.server.packs.PackLocationInfo info,
                    net.minecraft.server.packs.repository.Pack.Metadata meta) {
                return openPrimary(info);
            }
        };
    }

    /** ctorRedirects: 1.20.1 new PathPackResources(name, path, alwaysStable) */
    public static net.minecraft.server.packs.PathPackResources cbNewPathPackResources(String name,
            java.nio.file.Path path, boolean alwaysStable) {
        return new net.minecraft.server.packs.PathPackResources(cbPackLocation(name), path);
    }

    /** ctorRedirects: 1.20.1 new ZipResourcePack(name, file, alwaysStable) */
    public static net.minecraft.server.packs.FilePackResources cbNewFilePackResources(String name,
            java.io.File file, boolean alwaysStable) {
        return (net.minecraft.server.packs.FilePackResources)
            new net.minecraft.server.packs.FilePackResources.FileResourcesSupplier(file)
                .openPrimary(cbPackLocation(name));
    }

    private static net.minecraft.server.packs.PackLocationInfo cbPackLocation(String name) {
        return new net.minecraft.server.packs.PackLocationInfo(name,
            net.minecraft.network.chat.Component.literal(name),
            net.minecraft.server.packs.repository.PackSource.DEFAULT, java.util.Optional.empty());
    }

    // --- NBT getter族: 26.x把 CompoundTag getter Optional 化了
    //     (getBoolean(k):Optional<Boolean> + getBooleanOr(k,def)); 旧的
    //     getBoolean(k) 语义 = getBooleanOr(k, 缺省值)。全语料最常用的桥之一。
    public static boolean method_10577(net.minecraft.nbt.CompoundTag self, String key) {
        return self.getBooleanOr(key, false);
    }
    public static byte method_10571(net.minecraft.nbt.CompoundTag self, String key) {
        return self.getByteOr(key, (byte) 0);
    }
    public static short method_10568(net.minecraft.nbt.CompoundTag self, String key) {
        return self.getShortOr(key, (short) 0);
    }
    public static int method_10550(net.minecraft.nbt.CompoundTag self, String key) {
        return self.getIntOr(key, 0);
    }
    public static long method_10537(net.minecraft.nbt.CompoundTag self, String key) {
        return self.getLongOr(key, 0L);
    }
    public static float method_10583(net.minecraft.nbt.CompoundTag self, String key) {
        return self.getFloatOr(key, 0.0f);
    }
    public static double method_10574(net.minecraft.nbt.CompoundTag self, String key) {
        return self.getDoubleOr(key, 0.0);
    }
    public static java.lang.String method_10558(net.minecraft.nbt.CompoundTag self, String key) {
        return self.getStringOr(key, "");
    }

    // --- NBT 引用类型 getter族 + PoseStack.mulPose: 26.x 的 Optional 化/接口化 ---
    /** CompoundTag.getCompound(k): 旧缺失返回空 tag = 26.x getCompoundOrEmpty */
    public static net.minecraft.nbt.CompoundTag method_10562(net.minecraft.nbt.CompoundTag self, String key) {
        return self.getCompoundOrEmpty(key);
    }
    /** CompoundTag.getList(k, type): 26.x ListTag 不再按元素类型过滤, type 参丢弃 */
    public static net.minecraft.nbt.ListTag method_10554(net.minecraft.nbt.CompoundTag self, String key, int type) {
        return self.getListOrEmpty(key);
    }
    /** CompoundTag.remove(k): 返回类型 void->Tag, 老调用点期望 void */
    public static void method_10551(net.minecraft.nbt.CompoundTag self, String key) {
        self.remove(key);
    }

    // --- Mth.sin/cos: 26.x 参数 float->double (sqrt 等仍是 float) ---
    /** Mth.sin(float): 26.x 只剩 sin(double), float 实参加宽 */
    public static float method_15374(float f) {
        return net.minecraft.util.Mth.sin(f);
    }
    /** Mth.cos(float): 26.x 只剩 cos(double) */
    public static float method_15362(float f) {
        return net.minecraft.util.Mth.cos(f);
    }

    /** was net/minecraft/class_5132$class_5133.method_26867(Lnet/minecraft/class_1320;)Lnet/minecraft/class_5132$class_5133; */
    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder method_26867(
            net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder self,
            net.minecraft.world.entity.ai.attributes.Attribute attr) {
        return self.add(net.minecraft.core.registries.BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attr));
    }

    /** was net/minecraft/class_5132$class_5133.method_26868(Lnet/minecraft/class_1320;D)Lnet/minecraft/class_5132$class_5133; */
    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder method_26868(
            net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder self,
            net.minecraft.world.entity.ai.attributes.Attribute attr, double value) {
        return self.add(net.minecraft.core.registries.BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attr), value);
    }

    /** was net/minecraft/class_5134.field_23721Lnet/minecraft/class_1320; */
    public static net.minecraft.world.entity.ai.attributes.Attribute field_23721() {
        return (net.minecraft.world.entity.ai.attributes.Attribute) net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE.value();
    }

    /** was net/minecraft/class_2499.method_10602(I)Lnet/minecraft/class_2487; -- ListTag.getCompound(int) Optional-ified in 26.x; old returned an empty compound when absent = getCompoundOrEmpty */
    public static net.minecraft.nbt.CompoundTag method_10602(net.minecraft.nbt.ListTag self, int i) {
        return self.getCompoundOrEmpty(i);
    }

    // ListTag primitive getters Optional-ified in 26.x; old semantics (value or default
    // when absent/wrong-type) = getXxxOr(index, default).
    /** was net/minecraft/class_2499.method_10608(I)Ljava/lang/String; */
    public static String method_10608(net.minecraft.nbt.ListTag self, int i) { return self.getStringOr(i, ""); }
    /** was net/minecraft/class_2499.method_10611(I)D */
    public static double method_10611(net.minecraft.nbt.ListTag self, int i) { return self.getDoubleOr(i, 0.0); }
    /** was net/minecraft/class_2499.method_10604(I)F */
    public static float method_10604(net.minecraft.nbt.ListTag self, int i) { return self.getFloatOr(i, 0.0f); }
    /** was net/minecraft/class_2499.method_10600(I)I */
    public static int method_10600(net.minecraft.nbt.ListTag self, int i) { return self.getIntOr(i, 0); }

    // LivingEntity.getAttribute(Attribute) holderified in 1.20.5; wrap the bare attribute.
    // One overload per call-site owner (the redirect emits the receiver's own type).
    /** was net/minecraft/class_1309.method_5996(Lnet/minecraft/class_1320;)Lnet/minecraft/class_1324; */
    public static net.minecraft.world.entity.ai.attributes.AttributeInstance method_5996(net.minecraft.world.entity.LivingEntity self, net.minecraft.world.entity.ai.attributes.Attribute a) {
        return self.getAttribute(net.minecraft.core.registries.BuiltInRegistries.ATTRIBUTE.wrapAsHolder(a));
    }
    /** was net/minecraft/class_1657.method_5996(Lnet/minecraft/class_1320;)Lnet/minecraft/class_1324; */
    public static net.minecraft.world.entity.ai.attributes.AttributeInstance method_5996(net.minecraft.world.entity.player.Player self, net.minecraft.world.entity.ai.attributes.Attribute a) {
        return self.getAttribute(net.minecraft.core.registries.BuiltInRegistries.ATTRIBUTE.wrapAsHolder(a));
    }
    /** was net/minecraft/class_1642.method_5996(Lnet/minecraft/class_1320;)Lnet/minecraft/class_1324; */
    public static net.minecraft.world.entity.ai.attributes.AttributeInstance method_5996(net.minecraft.world.entity.monster.zombie.Zombie self, net.minecraft.world.entity.ai.attributes.Attribute a) {
        return self.getAttribute(net.minecraft.core.registries.BuiltInRegistries.ATTRIBUTE.wrapAsHolder(a));
    }
    /** was net/minecraft/class_1498.method_5996(Lnet/minecraft/class_1320;)Lnet/minecraft/class_1324; */
    public static net.minecraft.world.entity.ai.attributes.AttributeInstance method_5996(net.minecraft.world.entity.animal.equine.Horse self, net.minecraft.world.entity.ai.attributes.Attribute a) {
        return self.getAttribute(net.minecraft.core.registries.BuiltInRegistries.ATTRIBUTE.wrapAsHolder(a));
    }
    /** was net/minecraft/class_1308.method_5996(Lnet/minecraft/class_1320;)Lnet/minecraft/class_1324; */
    public static net.minecraft.world.entity.ai.attributes.AttributeInstance method_5996(net.minecraft.world.entity.Mob self, net.minecraft.world.entity.ai.attributes.Attribute a) {
        return self.getAttribute(net.minecraft.core.registries.BuiltInRegistries.ATTRIBUTE.wrapAsHolder(a));
    }
    /** was net/minecraft/class_1496.method_5996(Lnet/minecraft/class_1320;)Lnet/minecraft/class_1324; */
    public static net.minecraft.world.entity.ai.attributes.AttributeInstance method_5996(net.minecraft.world.entity.animal.equine.AbstractHorse self, net.minecraft.world.entity.ai.attributes.Attribute a) {
        return self.getAttribute(net.minecraft.core.registries.BuiltInRegistries.ATTRIBUTE.wrapAsHolder(a));
    }
    /** was net/minecraft/class_3222.method_5996(Lnet/minecraft/class_1320;)Lnet/minecraft/class_1324; */
    public static net.minecraft.world.entity.ai.attributes.AttributeInstance method_5996(net.minecraft.server.level.ServerPlayer self, net.minecraft.world.entity.ai.attributes.Attribute a) {
        return self.getAttribute(net.minecraft.core.registries.BuiltInRegistries.ATTRIBUTE.wrapAsHolder(a));
    }

    // ---- InteractionResult (class_1269): enum -> sealed interface at 1.21.2. The
    //      surviving constants are Success/Fail/Pass records (subtypes of the old
    //      field type: fieldGetters above); the enum-era helpers are rebuilt here. ----
    /** was net/minecraft/class_1269.field_33562Lnet/minecraft/class_1269; -- CONSUME_PARTIAL dropped at 1.21.2; CONSUME is the nearest surviving outcome (consumes the action, no swing) */
    public static net.minecraft.world.InteractionResult field_33562() {
        return net.minecraft.world.InteractionResult.CONSUME;
    }

    /** was net/minecraft/class_1269.method_29236(Z)Lnet/minecraft/class_1269; -- sidedSuccess(isClient): 1.20.1 body = isClient ? SUCCESS : CONSUME (client swings, server consumes without swinging) */
    public static net.minecraft.world.InteractionResult method_29236(boolean isClient) {
        return isClient ? net.minecraft.world.InteractionResult.SUCCESS : net.minecraft.world.InteractionResult.CONSUME;
    }

    /** was net/minecraft/class_1269.method_23666()Z -- shouldSwing(): 1.20.1 `this == SUCCESS`; a Success now says which side swings */
    public static boolean method_23666(net.minecraft.world.InteractionResult self) {
        return self instanceof net.minecraft.world.InteractionResult.Success s
            && s.swingSource() != net.minecraft.world.InteractionResult.SwingSource.NONE;
    }

    /** was net/minecraft/class_1269.method_36360()Z -- shouldAwardStats(): 1.20.1 `SUCCESS || CONSUME` = every Success */
    public static boolean method_36360(net.minecraft.world.InteractionResult self) {
        return self instanceof net.minecraft.world.InteractionResult.Success;
    }

    // ---- TypedActionResult (class_1271, facade) <-> InteractionResult boundary ----
    /** overrideHooks unwrap: a 1.20.1 Item.use override's TypedActionResult -> the InteractionResult vanilla expects from use() */
    public static net.minecraft.world.InteractionResult cbUnwrapTyped(top.dext.centurybridge.facade.v26_2.class_1271<?> r) {
        return r == null ? net.minecraft.world.InteractionResult.PASS : r.cbToVanilla();
    }

    /** was net/minecraft/class_5328.method_29282(Lnet/minecraft/class_1937;Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1271; -- ItemUtils.startUsingInstantly, 1.20.1 body: start using, answer consume(held) */
    public static top.dext.centurybridge.facade.v26_2.class_1271<net.minecraft.world.item.ItemStack> method_29282(
            net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        player.startUsingItem(hand);
        return new top.dext.centurybridge.facade.v26_2.class_1271<>(net.minecraft.world.InteractionResult.CONSUME, player.getItemInHand(hand));
    }

    // Item.use answered TypedActionResult<ItemStack> until 1.21.2. A mod's super.use(..) /
    // item.use(..) lands here (instance redirect, incl. INVOKESPECIAL); the virtual call
    // reaches the mod's synthesized new-shape hook, whose re-entry guard falls through to
    // vanilla. One overload per call-site owner seen in the corpus (the redirect emits the
    // receiver's own type).
    /** was net/minecraft/class_1792.method_7836(Lnet/minecraft/class_1937;Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1271; */
    public static top.dext.centurybridge.facade.v26_2.class_1271<net.minecraft.world.item.ItemStack> method_7836(
            net.minecraft.world.item.Item self, net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        return top.dext.centurybridge.facade.v26_2.class_1271.cbFrom(self.use(level, player, hand), player.getItemInHand(hand));
    }
    /** was net/minecraft/class_1747.method_7836(Lnet/minecraft/class_1937;Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1271; */
    public static top.dext.centurybridge.facade.v26_2.class_1271<net.minecraft.world.item.ItemStack> method_7836(
            net.minecraft.world.item.BlockItem self, net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        return top.dext.centurybridge.facade.v26_2.class_1271.cbFrom(self.use(level, player, hand), player.getItemInHand(hand));
    }
    /** was net/minecraft/class_1755.method_7836(Lnet/minecraft/class_1937;Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1271; */
    public static top.dext.centurybridge.facade.v26_2.class_1271<net.minecraft.world.item.ItemStack> method_7836(
            net.minecraft.world.item.BucketItem self, net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        return top.dext.centurybridge.facade.v26_2.class_1271.cbFrom(self.use(level, player, hand), player.getItemInHand(hand));
    }
    /** was net/minecraft/class_1753.method_7836(Lnet/minecraft/class_1937;Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1271; */
    public static top.dext.centurybridge.facade.v26_2.class_1271<net.minecraft.world.item.ItemStack> method_7836(
            net.minecraft.world.item.BowItem self, net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        return top.dext.centurybridge.facade.v26_2.class_1271.cbFrom(self.use(level, player, hand), player.getItemInHand(hand));
    }
    /** was net/minecraft/class_1821.method_7836(Lnet/minecraft/class_1937;Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1271; */
    public static top.dext.centurybridge.facade.v26_2.class_1271<net.minecraft.world.item.ItemStack> method_7836(
            net.minecraft.world.item.ShovelItem self, net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        return top.dext.centurybridge.facade.v26_2.class_1271.cbFrom(self.use(level, player, hand), player.getItemInHand(hand));
    }

    // ---- 26.1 interact rework: Entity.interact(Player, Hand) + interactAt(Player, Vec3, Hand)
    //      merged into interact(Player, Hand, Vec3 relativeHit); Player.interactOn gained the
    //      same Vec3. Old two-arg callers had no hit location: Vec3.ZERO (= entity origin).
    //      Overrides are reconnected by overrideHooks (args map); one overload per call-site owner. ----
    /** was net/minecraft/class_1297.method_5688(Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1269; */
    public static net.minecraft.world.InteractionResult method_5688(net.minecraft.world.entity.Entity self, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        return self.interact(player, hand, net.minecraft.world.phys.Vec3.ZERO);
    }
    /** was net/minecraft/class_1309.method_5688(Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1269; */
    public static net.minecraft.world.InteractionResult method_5688(net.minecraft.world.entity.LivingEntity self, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        return self.interact(player, hand, net.minecraft.world.phys.Vec3.ZERO);
    }
    /** was net/minecraft/class_1533.method_5688(Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1269; */
    public static net.minecraft.world.InteractionResult method_5688(net.minecraft.world.entity.decoration.ItemFrame self, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        return self.interact(player, hand, net.minecraft.world.phys.Vec3.ZERO);
    }
    /** was net/minecraft/class_1297.method_5664(Lnet/minecraft/class_1657;Lnet/minecraft/class_243;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1269; -- interactAt: the hit vector is exactly what the merged method takes */
    public static net.minecraft.world.InteractionResult method_5664(net.minecraft.world.entity.Entity self, net.minecraft.world.entity.player.Player player, net.minecraft.world.phys.Vec3 hit, net.minecraft.world.InteractionHand hand) {
        return self.interact(player, hand, hit);
    }
    /** was net/minecraft/class_1427.method_5664(Lnet/minecraft/class_1657;Lnet/minecraft/class_243;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1269; */
    public static net.minecraft.world.InteractionResult method_5664(net.minecraft.world.entity.animal.golem.AbstractGolem self, net.minecraft.world.entity.player.Player player, net.minecraft.world.phys.Vec3 hit, net.minecraft.world.InteractionHand hand) {
        return self.interact(player, hand, hit);
    }
    /** was net/minecraft/class_1531.method_5664(Lnet/minecraft/class_1657;Lnet/minecraft/class_243;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1269; */
    public static net.minecraft.world.InteractionResult method_5664(net.minecraft.world.entity.decoration.ArmorStand self, net.minecraft.world.entity.player.Player player, net.minecraft.world.phys.Vec3 hit, net.minecraft.world.InteractionHand hand) {
        return self.interact(player, hand, hit);
    }
    /** was net/minecraft/class_1530.method_5664(Lnet/minecraft/class_1657;Lnet/minecraft/class_243;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1269; */
    public static net.minecraft.world.InteractionResult method_5664(net.minecraft.world.entity.decoration.HangingEntity self, net.minecraft.world.entity.player.Player player, net.minecraft.world.phys.Vec3 hit, net.minecraft.world.InteractionHand hand) {
        return self.interact(player, hand, hit);
    }
    /** was net/minecraft/class_1657.method_7287(Lnet/minecraft/class_1297;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1269; -- Player.interactOn(entity, hand) */
    public static net.minecraft.world.InteractionResult method_7287(net.minecraft.world.entity.player.Player self, net.minecraft.world.entity.Entity target, net.minecraft.world.InteractionHand hand) {
        return self.interactOn(target, hand, net.minecraft.world.phys.Vec3.ZERO);
    }
    /** was net/minecraft/class_3222.method_7287(Lnet/minecraft/class_1297;Lnet/minecraft/class_1268;)Lnet/minecraft/class_1269; */
    public static net.minecraft.world.InteractionResult method_7287(net.minecraft.server.level.ServerPlayer self, net.minecraft.world.entity.Entity target, net.minecraft.world.InteractionHand hand) {
        return self.interactOn(target, hand, net.minecraft.world.phys.Vec3.ZERO);
    }

    // =====================================================================
    // 1.21 data-driven enchantments. Enchantment is a registry value whose
    // identity is its Holder in the WORLD's dynamic registry (no static
    // Registries.ENCHANTMENT, Enchantments.* are ResourceKeys). 1.20.1 mods
    // hold bare Enchantment values; these helpers translate both ways.
    // =====================================================================
    private static net.minecraft.core.HolderLookup.Provider cbVanillaDataCache;

    /** the live dynamic registries of the side we are on; null before any world exists */
    public static net.minecraft.core.RegistryAccess cbLiveRegistryAccess() {
        net.minecraft.server.MinecraftServer s = Trackers.SERVER;
        if (s != null && s.isSameThread()) {
            return s.registryAccess();
        }
        if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
            net.minecraft.core.RegistryAccess c = CbClientRegistry.current();
            if (c != null) {
                return c;
            }
        }
        return s != null ? s.registryAccess() : null;
    }

    private static net.minecraft.core.Registry<net.minecraft.world.item.enchantment.Enchantment> cbEnchantmentRegistryOrNull() {
        net.minecraft.core.RegistryAccess ra = cbLiveRegistryAccess();
        return ra == null ? null : ra.lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);
    }

    /** the live Registry&lt;Enchantment&gt; (Registries.ENCHANTMENT reads); a frozen registry: register() fails as in vanilla */
    public static net.minecraft.core.Registry<net.minecraft.world.item.enchantment.Enchantment> cbEnchantmentRegistry() {
        net.minecraft.core.Registry<net.minecraft.world.item.enchantment.Enchantment> r = cbEnchantmentRegistryOrNull();
        if (r == null) {
            throw new IllegalStateException("CenturyBridge: enchantments are data-driven since 1.21 and live in the world's dynamic registry; none is loaded yet (called before a server/world exists)");
        }
        return r;
    }

    /** was net/minecraft/class_7923.field_41176Lnet/minecraft/class_2378; -- Registries.ENCHANTMENT: the live dynamic registry */
    public static net.minecraft.core.Registry field_41176() {
        return cbEnchantmentRegistry();
    }

    /** also answers before a world exists, from the vanilla datapack copy (right keys/values, not the live Holder identity) */
    private static net.minecraft.core.HolderLookup.RegistryLookup<net.minecraft.world.item.enchantment.Enchantment> cbEnchantmentLookup() {
        net.minecraft.core.Registry<net.minecraft.world.item.enchantment.Enchantment> r = cbEnchantmentRegistryOrNull();
        if (r != null) {
            return r;
        }
        if (cbVanillaDataCache == null) {
            cbVanillaDataCache = net.minecraft.data.registries.VanillaRegistries.createLookup();
        }
        return cbVanillaDataCache.lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);
    }

    /** a bare Enchantment for a key; the value->key pair is remembered so the live Holder can be recovered later */
    public static net.minecraft.world.item.enchantment.Enchantment cbEnchantment(net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key) {
        net.minecraft.world.item.enchantment.Enchantment e = cbEnchantmentLookup().getOrThrow(key).value();
        Trackers.ENCHANTMENT_KEYS.putIfAbsent(e, key);
        return e;
    }

    /** live Holder for a bare Enchantment: by remembered key, else by value identity, else a direct holder */
    public static net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> cbEnchHolder(net.minecraft.world.item.enchantment.Enchantment e) {
        return cbEnchHolder(e, cbEnchantmentRegistryOrNull());
    }

    public static net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> cbEnchHolder(net.minecraft.world.item.enchantment.Enchantment e, net.minecraft.core.Registry<net.minecraft.world.item.enchantment.Enchantment> reg) {
        if (reg != null && e != null) {
            net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key = Trackers.ENCHANTMENT_KEYS.get(e);
            if (key != null) {
                java.util.Optional<net.minecraft.core.Holder.Reference<net.minecraft.world.item.enchantment.Enchantment>> h = reg.get(key);
                if (h.isPresent()) {
                    return h.get();
                }
            }
            net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> h = reg.wrapAsHolder(e);
            if (h.kind() == net.minecraft.core.Holder.Kind.REFERENCE) {
                h.unwrapKey().ifPresent(k -> Trackers.ENCHANTMENT_KEYS.putIfAbsent(e, k));
                return h;
            }
        }
        return net.minecraft.core.Holder.direct(e);
    }

    public static net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> cbEnchHolder(net.minecraft.world.item.enchantment.Enchantment e, net.minecraft.world.level.Level level) {
        return cbEnchHolder(e, level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT));
    }

    private static net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> cbEnchHolder(net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key, net.minecraft.world.entity.LivingEntity entity) {
        return entity.level().registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(key);
    }

    /** a bare value out of a component/registry holder, remembered with its key */
    private static net.minecraft.world.item.enchantment.Enchantment cbEnchValue(net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> h) {
        net.minecraft.world.item.enchantment.Enchantment e = h.value();
        h.unwrapKey().ifPresent(k -> Trackers.ENCHANTMENT_KEYS.putIfAbsent(e, k));
        return e;
    }

    private static int cbLevel(net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key, net.minecraft.world.entity.LivingEntity entity) {
        return net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentLevel(cbEnchHolder(key, entity), entity);
    }

    private static int cbItemLevel(net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key, net.minecraft.world.item.ItemStack stack) {
        return net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(cbEnchantmentLookup().getOrThrow(key), stack);
    }

    private static net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> cbEnchKey(net.minecraft.world.item.enchantment.Enchantment e) {
        net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key = Trackers.ENCHANTMENT_KEYS.get(e);
        if (key != null) {
            return key;
        }
        net.minecraft.core.Registry<net.minecraft.world.item.enchantment.Enchantment> r = cbEnchantmentRegistryOrNull();
        return r == null ? null : r.getResourceKey(e).orElse(null);
    }

    // ---- Enchantments.* constants (1.20.1 Enchantment fields -> 1.21 ResourceKeys) ----
    /** was net/minecraft/class_1893.field_23071Lnet/minecraft/class_1887; -- Enchantments.SOUL_SPEED: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_23071() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.SOUL_SPEED);
    }
    /** was net/minecraft/class_1893.field_38223Lnet/minecraft/class_1887; -- Enchantments.SWIFT_SNEAK: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_38223() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.SWIFT_SNEAK);
    }
    /** was net/minecraft/class_1893.field_50157Lnet/minecraft/class_1887; -- Enchantments.DENSITY: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_50157() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.DENSITY);
    }
    /** was net/minecraft/class_1893.field_50158Lnet/minecraft/class_1887; -- Enchantments.BREACH: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_50158() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.BREACH);
    }
    /** was net/minecraft/class_1893.field_50159Lnet/minecraft/class_1887; -- Enchantments.WIND_BURST: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_50159() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.WIND_BURST);
    }
    /** was net/minecraft/class_1893.field_63420Lnet/minecraft/class_1887; -- Enchantments.LUNGE: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_63420() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.LUNGE);
    }
    /** was net/minecraft/class_1893.field_9095Lnet/minecraft/class_1887; -- Enchantments.FIRE_PROTECTION: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9095() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.FIRE_PROTECTION);
    }
    /** was net/minecraft/class_1893.field_9096Lnet/minecraft/class_1887; -- Enchantments.PROJECTILE_PROTECTION: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9096() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.PROJECTILE_PROTECTION);
    }
    /** was net/minecraft/class_1893.field_9097Lnet/minecraft/class_1887; -- Enchantments.THORNS: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9097() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.THORNS);
    }
    /** was net/minecraft/class_1893.field_9098Lnet/minecraft/class_1887; -- Enchantments.QUICK_CHARGE: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9098() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.QUICK_CHARGE);
    }
    /** was net/minecraft/class_1893.field_9099Lnet/minecraft/class_1887; -- Enchantments.SILK_TOUCH: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9099() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH);
    }
    /** was net/minecraft/class_1893.field_9100Lnet/minecraft/class_1887; -- Enchantments.LURE: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9100() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.LURE);
    }
    /** was net/minecraft/class_1893.field_9101Lnet/minecraft/class_1887; -- Enchantments.MENDING: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9101() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.MENDING);
    }
    /** was net/minecraft/class_1893.field_9103Lnet/minecraft/class_1887; -- Enchantments.POWER: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9103() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.POWER);
    }
    /** was net/minecraft/class_1893.field_9104Lnet/minecraft/class_1887; -- Enchantments.RIPTIDE: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9104() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.RIPTIDE);
    }
    /** was net/minecraft/class_1893.field_9105Lnet/minecraft/class_1887; -- Enchantments.AQUA_AFFINITY: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9105() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.AQUA_AFFINITY);
    }
    /** was net/minecraft/class_1893.field_9106Lnet/minecraft/class_1887; -- Enchantments.IMPALING: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9106() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.IMPALING);
    }
    /** was net/minecraft/class_1893.field_9107Lnet/minecraft/class_1887; -- Enchantments.BLAST_PROTECTION: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9107() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.BLAST_PROTECTION);
    }
    /** was net/minecraft/class_1893.field_9108Lnet/minecraft/class_1887; -- Enchantments.MULTISHOT: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9108() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.MULTISHOT);
    }
    /** was net/minecraft/class_1893.field_9109Lnet/minecraft/class_1887; -- Enchantments.VANISHING_CURSE: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9109() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.VANISHING_CURSE);
    }
    /** was net/minecraft/class_1893.field_9110Lnet/minecraft/class_1887; -- Enchantments.LOOTING: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9110() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.LOOTING);
    }
    /** was net/minecraft/class_1893.field_9111Lnet/minecraft/class_1887; -- Enchantments.PROTECTION: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9111() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.PROTECTION);
    }
    /** was net/minecraft/class_1893.field_9112Lnet/minecraft/class_1887; -- Enchantments.BANE_OF_ARTHROPODS: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9112() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.BANE_OF_ARTHROPODS);
    }
    /** was net/minecraft/class_1893.field_9113Lnet/minecraft/class_1887; -- Enchantments.BINDING_CURSE: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9113() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.BINDING_CURSE);
    }
    /** was net/minecraft/class_1893.field_9114Lnet/minecraft/class_1887; -- Enchantments.LUCK_OF_THE_SEA: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9114() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.LUCK_OF_THE_SEA);
    }
    /** was net/minecraft/class_1893.field_9115Lnet/minecraft/class_1887; -- Enchantments.SWEEPING_EDGE: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9115() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.SWEEPING_EDGE);
    }
    /** was net/minecraft/class_1893.field_9116Lnet/minecraft/class_1887; -- Enchantments.PUNCH: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9116() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.PUNCH);
    }
    /** was net/minecraft/class_1893.field_9117Lnet/minecraft/class_1887; -- Enchantments.CHANNELING: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9117() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.CHANNELING);
    }
    /** was net/minecraft/class_1893.field_9118Lnet/minecraft/class_1887; -- Enchantments.SHARPNESS: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9118() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.SHARPNESS);
    }
    /** was net/minecraft/class_1893.field_9119Lnet/minecraft/class_1887; -- Enchantments.UNBREAKING: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9119() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.UNBREAKING);
    }
    /** was net/minecraft/class_1893.field_9120Lnet/minecraft/class_1887; -- Enchantments.LOYALTY: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9120() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.LOYALTY);
    }
    /** was net/minecraft/class_1893.field_9121Lnet/minecraft/class_1887; -- Enchantments.KNOCKBACK: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9121() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.KNOCKBACK);
    }
    /** was net/minecraft/class_1893.field_9122Lnet/minecraft/class_1887; -- Enchantments.FROST_WALKER: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9122() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.FROST_WALKER);
    }
    /** was net/minecraft/class_1893.field_9123Lnet/minecraft/class_1887; -- Enchantments.SMITE: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9123() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.SMITE);
    }
    /** was net/minecraft/class_1893.field_9124Lnet/minecraft/class_1887; -- Enchantments.FIRE_ASPECT: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9124() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.FIRE_ASPECT);
    }
    /** was net/minecraft/class_1893.field_9125Lnet/minecraft/class_1887; -- Enchantments.INFINITY: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9125() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.INFINITY);
    }
    /** was net/minecraft/class_1893.field_9126Lnet/minecraft/class_1887; -- Enchantments.FLAME: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9126() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.FLAME);
    }
    /** was net/minecraft/class_1893.field_9127Lnet/minecraft/class_1887; -- Enchantments.RESPIRATION: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9127() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.RESPIRATION);
    }
    /** was net/minecraft/class_1893.field_9128Lnet/minecraft/class_1887; -- Enchantments.DEPTH_STRIDER: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9128() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.DEPTH_STRIDER);
    }
    /** was net/minecraft/class_1893.field_9129Lnet/minecraft/class_1887; -- Enchantments.FEATHER_FALLING: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9129() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.FEATHER_FALLING);
    }
    /** was net/minecraft/class_1893.field_9130Lnet/minecraft/class_1887; -- Enchantments.FORTUNE: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9130() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.FORTUNE);
    }
    /** was net/minecraft/class_1893.field_9131Lnet/minecraft/class_1887; -- Enchantments.EFFICIENCY: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9131() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.EFFICIENCY);
    }
    /** was net/minecraft/class_1893.field_9132Lnet/minecraft/class_1887; -- Enchantments.PIERCING: a ResourceKey since 1.21, resolved from the live dynamic registry */
    public static net.minecraft.world.item.enchantment.Enchantment field_9132() {
        return cbEnchantment(net.minecraft.world.item.enchantment.Enchantments.PIERCING);
    }

    // ---- EnchantmentHelper statics that died with the 1.20.5 components / 1.21 data rework ----
    /** was net/minecraft/class_1890.method_8222(Lnet/minecraft/class_1799;)Ljava/util/Map; -- getEnchantments(stack): Map&lt;Enchantment,Integer&gt; from the (stored-)enchantments component */
    public static java.util.Map<net.minecraft.world.item.enchantment.Enchantment, java.lang.Integer> method_8222(net.minecraft.world.item.ItemStack stack) {
        java.util.Map<net.minecraft.world.item.enchantment.Enchantment, java.lang.Integer> map = new java.util.LinkedHashMap<>();
        for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment>> en : net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentsForCrafting(stack).entrySet()) {
            map.put(cbEnchValue(en.getKey()), en.getIntValue());
        }
        return map;
    }

    /** was net/minecraft/class_1890.method_8214(Ljava/util/Map;Lnet/minecraft/class_1799;)V -- setEnchantments(map, stack) */
    public static void method_8214(java.util.Map<net.minecraft.world.item.enchantment.Enchantment, java.lang.Integer> map, net.minecraft.world.item.ItemStack stack) {
        net.minecraft.world.item.enchantment.ItemEnchantments.Mutable m =
            new net.minecraft.world.item.enchantment.ItemEnchantments.Mutable(net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
        net.minecraft.core.Registry<net.minecraft.world.item.enchantment.Enchantment> reg = cbEnchantmentRegistryOrNull();
        for (java.util.Map.Entry<net.minecraft.world.item.enchantment.Enchantment, java.lang.Integer> en : map.entrySet()) {
            if (en.getKey() != null && en.getValue() != null && en.getValue() > 0) {
                m.set(cbEnchHolder(en.getKey(), reg), en.getValue());
            }
        }
        net.minecraft.world.item.enchantment.EnchantmentHelper.setEnchantments(stack, m.toImmutable());
    }

    /** was net/minecraft/class_1890.method_8203(Lnet/minecraft/class_1887;Lnet/minecraft/class_1309;)I -- getEnchantmentLevel(ench, entity) */
    public static int method_8203(net.minecraft.world.item.enchantment.Enchantment e, net.minecraft.world.entity.LivingEntity entity) {
        return net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentLevel(cbEnchHolder(e, entity.level()), entity);
    }

    /** was net/minecraft/class_1890.method_8216(Lnet/minecraft/class_1309;)Z -- hasFrostWalker */
    public static boolean method_8216(net.minecraft.world.entity.LivingEntity e) { return cbLevel(net.minecraft.world.item.enchantment.Enchantments.FROST_WALKER, e) > 0; }
    /** was net/minecraft/class_1890.method_8200(Lnet/minecraft/class_1309;)Z -- hasAquaAffinity */
    public static boolean method_8200(net.minecraft.world.entity.LivingEntity e) { return cbLevel(net.minecraft.world.item.enchantment.Enchantments.AQUA_AFFINITY, e) > 0; }
    /** was net/minecraft/class_1890.method_25951(Lnet/minecraft/class_1309;)Z -- hasSoulSpeed */
    public static boolean method_25951(net.minecraft.world.entity.LivingEntity e) { return cbLevel(net.minecraft.world.item.enchantment.Enchantments.SOUL_SPEED, e) > 0; }
    /** was net/minecraft/class_1890.method_8226(Lnet/minecraft/class_1309;)I -- getMobLooting */
    public static int method_8226(net.minecraft.world.entity.LivingEntity e) { return cbLevel(net.minecraft.world.item.enchantment.Enchantments.LOOTING, e); }
    /** was net/minecraft/class_1890.method_8205(Lnet/minecraft/class_1309;)I -- getKnockbackBonus */
    public static int method_8205(net.minecraft.world.entity.LivingEntity e) { return cbLevel(net.minecraft.world.item.enchantment.Enchantments.KNOCKBACK, e); }
    /** was net/minecraft/class_1890.method_8199(Lnet/minecraft/class_1309;)I -- getFireAspect */
    public static int method_8199(net.minecraft.world.entity.LivingEntity e) { return cbLevel(net.minecraft.world.item.enchantment.Enchantments.FIRE_ASPECT, e); }
    /** was net/minecraft/class_1890.method_8211(Lnet/minecraft/class_1309;)I -- getRespiration */
    public static int method_8211(net.minecraft.world.entity.LivingEntity e) { return cbLevel(net.minecraft.world.item.enchantment.Enchantments.RESPIRATION, e); }
    /** was net/minecraft/class_1890.method_8232(Lnet/minecraft/class_1309;)I -- getDepthStrider */
    public static int method_8232(net.minecraft.world.entity.LivingEntity e) { return cbLevel(net.minecraft.world.item.enchantment.Enchantments.DEPTH_STRIDER, e); }
    /** was net/minecraft/class_1890.method_8234(Lnet/minecraft/class_1309;)I -- getBlockEfficiency */
    public static int method_8234(net.minecraft.world.entity.LivingEntity e) { return cbLevel(net.minecraft.world.item.enchantment.Enchantments.EFFICIENCY, e); }
    /** was net/minecraft/class_1890.method_8217(Lnet/minecraft/class_1309;)F -- getSweepingDamageRatio: 1.20.1 = 1 - 1/(level+1), 0 when absent */
    public static float method_8217(net.minecraft.world.entity.LivingEntity e) {
        int lvl = cbLevel(net.minecraft.world.item.enchantment.Enchantments.SWEEPING_EDGE, e);
        return lvl > 0 ? 1.0F - 1.0F / (float) (lvl + 1) : 0.0F;
    }
    /** was net/minecraft/class_1890.method_42304(Lnet/minecraft/class_1309;)F -- getSneakingSpeedBonus: level * 0.15 */
    public static float method_42304(net.minecraft.world.entity.LivingEntity e) { return (float) cbLevel(net.minecraft.world.item.enchantment.Enchantments.SWIFT_SNEAK, e) * 0.15F; }
    /** was net/minecraft/class_1890.method_8206(Lnet/minecraft/class_1799;)I -- getLoyalty */
    public static int method_8206(net.minecraft.world.item.ItemStack s) { return cbItemLevel(net.minecraft.world.item.enchantment.Enchantments.LOYALTY, s); }
    /** was net/minecraft/class_1890.method_8202(Lnet/minecraft/class_1799;)I -- getRiptide */
    public static int method_8202(net.minecraft.world.item.ItemStack s) { return cbItemLevel(net.minecraft.world.item.enchantment.Enchantments.RIPTIDE, s); }
    /** was net/minecraft/class_1890.method_8215(Lnet/minecraft/class_1799;)I -- getFishingSpeedBonus (Lure) */
    public static int method_8215(net.minecraft.world.item.ItemStack s) { return cbItemLevel(net.minecraft.world.item.enchantment.Enchantments.LURE, s); }
    /** was net/minecraft/class_1890.method_8223(Lnet/minecraft/class_1799;)I -- getFishingLuckBonus (Luck of the Sea) */
    public static int method_8223(net.minecraft.world.item.ItemStack s) { return cbItemLevel(net.minecraft.world.item.enchantment.Enchantments.LUCK_OF_THE_SEA, s); }
    /** was net/minecraft/class_1890.method_49189(Lnet/minecraft/class_1799;)Z -- hasSilkTouch */
    public static boolean method_49189(net.minecraft.world.item.ItemStack s) { return cbItemLevel(net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH, s) > 0; }
    /** was net/minecraft/class_1890.method_8228(Lnet/minecraft/class_1799;)Z -- hasChanneling */
    public static boolean method_8228(net.minecraft.world.item.ItemStack s) { return cbItemLevel(net.minecraft.world.item.enchantment.Enchantments.CHANNELING, s) > 0; }
    /** was net/minecraft/class_1890.method_8224(Lnet/minecraft/class_1799;)Z -- hasBindingCurse: the 1.21 definition is the PREVENT_ARMOR_CHANGE effect */
    public static boolean method_8224(net.minecraft.world.item.ItemStack s) {
        return net.minecraft.world.item.enchantment.EnchantmentHelper.has(s, net.minecraft.world.item.enchantment.EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE);
    }
    /** was net/minecraft/class_1890.method_8221(Lnet/minecraft/class_1799;)Z -- hasVanishingCurse: PREVENT_EQUIPMENT_DROP effect */
    public static boolean method_8221(net.minecraft.world.item.ItemStack s) {
        return net.minecraft.world.item.enchantment.EnchantmentHelper.has(s, net.minecraft.world.item.enchantment.EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP);
    }

    // 1.20.1 ran the victim's enchantments (doPostHurtEffects) and the attacker's
    // (doPostDamageEffects) as two calls; 1.21 runs both sides in one
    // doPostAttackEffects(level, victim, source). Mods call the old pair back to
    // back, so the second call for the same pair in the same tick is a no-op.
    private static Object cbLastPostVictim, cbLastPostAttacker;
    private static long cbLastPostTime = Long.MIN_VALUE;

    private static void cbPostAttack(net.minecraft.world.entity.Entity victim, net.minecraft.world.entity.Entity attacker) {
        if (victim == null || !(victim.level() instanceof net.minecraft.server.level.ServerLevel sl)) {
            return;
        }
        long t = sl.getGameTime();
        synchronized (Statics.class) {
            if (cbLastPostVictim == victim && cbLastPostAttacker == attacker && cbLastPostTime == t) {
                return;
            }
            cbLastPostVictim = victim;
            cbLastPostAttacker = attacker;
            cbLastPostTime = t;
        }
        net.minecraft.world.damagesource.DamageSource src;
        if (attacker instanceof net.minecraft.world.entity.player.Player pl) {
            src = victim.damageSources().playerAttack(pl);
        } else if (attacker instanceof net.minecraft.world.entity.LivingEntity l) {
            src = victim.damageSources().mobAttack(l);
        } else {
            src = victim.damageSources().generic();
        }
        net.minecraft.world.item.enchantment.EnchantmentHelper.doPostAttackEffects(sl, victim, src);
    }

    /** was net/minecraft/class_1890.method_8210(Lnet/minecraft/class_1309;Lnet/minecraft/class_1297;)V -- doPostHurtEffects(victim, attacker) */
    public static void method_8210(net.minecraft.world.entity.LivingEntity victim, net.minecraft.world.entity.Entity attacker) { cbPostAttack(victim, attacker); }
    /** was net/minecraft/class_1890.method_8213(Lnet/minecraft/class_1309;Lnet/minecraft/class_1297;)V -- doPostDamageEffects(attacker, victim) */
    public static void method_8213(net.minecraft.world.entity.LivingEntity attacker, net.minecraft.world.entity.Entity victim) { cbPostAttack(victim, attacker); }

    /** was net/minecraft/class_1890.method_37427(Lnet/minecraft/class_2487;)Lnet/minecraft/class_2960; -- getEnchantmentId(tag) */
    public static net.minecraft.resources.Identifier method_37427(net.minecraft.nbt.CompoundTag tag) {
        return net.minecraft.resources.Identifier.tryParse(tag.getStringOr("id", ""));
    }
    /** was net/minecraft/class_1890.method_37424(Lnet/minecraft/class_2487;)I -- getEnchantmentLevel(tag) */
    public static int method_37424(net.minecraft.nbt.CompoundTag tag) {
        return net.minecraft.util.Mth.clamp(tag.getIntOr("lvl", 0), 0, 255);
    }
    /** was net/minecraft/class_1890.method_37425(Lnet/minecraft/class_2487;I)V -- setEnchantmentLevel(tag, level) */
    public static void method_37425(net.minecraft.nbt.CompoundTag tag, int level) {
        tag.putShort("lvl", (short) level);
    }
    /** was net/minecraft/class_1890.method_37426(Lnet/minecraft/class_2960;I)Lnet/minecraft/class_2487; -- storeEnchantment(id, level) */
    public static net.minecraft.nbt.CompoundTag method_37426(net.minecraft.resources.Identifier id, int level) {
        net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
        tag.putString("id", String.valueOf(id));
        tag.putShort("lvl", (short) level);
        return tag;
    }
    /** was net/minecraft/class_1890.method_37423(Lnet/minecraft/class_1887;)Lnet/minecraft/class_2960; -- getEnchantmentId(ench) */
    public static net.minecraft.resources.Identifier method_37423(net.minecraft.world.item.enchantment.Enchantment e) {
        net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> k = cbEnchKey(e);
        return k == null ? null : k.identifier();
    }
    /** was net/minecraft/class_1890.method_22445(Lnet/minecraft/class_2499;)Ljava/util/Map; -- deserializeEnchantments(listTag): 1.20.1 {id, lvl} entries */
    public static java.util.Map<net.minecraft.world.item.enchantment.Enchantment, java.lang.Integer> method_22445(net.minecraft.nbt.ListTag list) {
        java.util.Map<net.minecraft.world.item.enchantment.Enchantment, java.lang.Integer> map = new java.util.LinkedHashMap<>();
        for (int i = 0; i < list.size(); i++) {
            net.minecraft.nbt.CompoundTag tag = list.getCompoundOrEmpty(i);
            net.minecraft.resources.Identifier id = method_37427(tag);
            if (id == null) {
                continue;
            }
            java.util.Optional<net.minecraft.core.Holder.Reference<net.minecraft.world.item.enchantment.Enchantment>> h =
                cbEnchantmentLookup().get(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENCHANTMENT, id));
            h.ifPresent(r -> map.put(cbEnchValue(r), method_37424(tag)));
        }
        return map;
    }

    /** was net/minecraft/class_1890.method_8204(Lnet/minecraft/class_1887;Lnet/minecraft/class_1309;)Ljava/util/Map$Entry; -- getRandomItemWith(ench, entity): a random equipped stack carrying it, else null */
    public static java.util.Map.Entry<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack> method_8204(net.minecraft.world.item.enchantment.Enchantment e, net.minecraft.world.entity.LivingEntity entity) {
        net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> h = cbEnchHolder(e, entity.level());
        java.util.List<java.util.Map.Entry<net.minecraft.world.entity.EquipmentSlot, net.minecraft.world.item.ItemStack>> hits = new java.util.ArrayList<>();
        for (net.minecraft.world.entity.EquipmentSlot slot : net.minecraft.world.entity.EquipmentSlot.values()) {
            net.minecraft.world.item.ItemStack st = entity.getItemBySlot(slot);
            if (!st.isEmpty() && net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(h, st) > 0) {
                hits.add(new java.util.AbstractMap.SimpleEntry<>(slot, st));
            }
        }
        return hits.isEmpty() ? null : hits.get(entity.getRandom().nextInt(hits.size()));
    }

    /** was net/minecraft/class_1890.method_8201(Ljava/util/Collection;Lnet/minecraft/class_1887;)Z -- isEnchantmentCompatible(existing, candidate) */
    public static boolean method_8201(java.util.Collection<net.minecraft.world.item.enchantment.Enchantment> existing, net.minecraft.world.item.enchantment.Enchantment candidate) {
        net.minecraft.core.Registry<net.minecraft.world.item.enchantment.Enchantment> reg = cbEnchantmentRegistryOrNull();
        java.util.List<net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment>> hs = new java.util.ArrayList<>();
        for (net.minecraft.world.item.enchantment.Enchantment x : existing) {
            hs.add(cbEnchHolder(x, reg));
        }
        return net.minecraft.world.item.enchantment.EnchantmentHelper.isEnchantmentCompatible(hs, cbEnchHolder(candidate, reg));
    }

    private static java.util.stream.Stream<net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment>> cbEnchantable(boolean allowTreasure) {
        return cbEnchantmentRegistry().listElements()
            .filter(h -> allowTreasure || h.is(net.minecraft.tags.EnchantmentTags.IN_ENCHANTING_TABLE))
            .map(h -> (net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment>) h);
    }

    /** was net/minecraft/class_1890.method_8233(Lnet/minecraft/class_5819;Lnet/minecraft/class_1799;IZ)Lnet/minecraft/class_1799; -- enchantItem(random, stack, level, allowTreasure) */
    public static net.minecraft.world.item.ItemStack method_8233(net.minecraft.util.RandomSource random, net.minecraft.world.item.ItemStack stack, int level, boolean allowTreasure) {
        return net.minecraft.world.item.enchantment.EnchantmentHelper.enchantItem(random, stack, level, cbEnchantable(allowTreasure));
    }
    /** was net/minecraft/class_1890.method_8230(Lnet/minecraft/class_5819;Lnet/minecraft/class_1799;IZ)Ljava/util/List; -- selectEnchantment(random, stack, level, allowTreasure) */
    public static java.util.List<net.minecraft.world.item.enchantment.EnchantmentInstance> method_8230(net.minecraft.util.RandomSource random, net.minecraft.world.item.ItemStack stack, int level, boolean allowTreasure) {
        return net.minecraft.world.item.enchantment.EnchantmentHelper.selectEnchantment(random, stack, level, cbEnchantable(allowTreasure));
    }

    // ---- Enchantment (class_1887) instance methods that became data / tags in 1.21 ----
    /** was net/minecraft/class_1887.method_8184()Ljava/lang/String; -- getDescriptionId: the translation key behind the data-driven description */
    public static java.lang.String method_8184(net.minecraft.world.item.enchantment.Enchantment self) {
        if (self.description().getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents t) {
            return t.getKey();
        }
        net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment> k = cbEnchKey(self);
        return k == null ? "enchantment.unknown" : "enchantment." + k.identifier().toLanguageKey();
    }
    /** was net/minecraft/class_1887.method_8190()Ljava/lang/String; -- getOrCreateDescriptionId */
    public static java.lang.String method_8190(net.minecraft.world.item.enchantment.Enchantment self) { return method_8184(self); }
    /** was net/minecraft/class_1887.method_8179(I)Lnet/minecraft/class_2561; -- getFullname(level): static on (Holder, int) since 1.21 */
    public static net.minecraft.network.chat.Component method_8179(net.minecraft.world.item.enchantment.Enchantment self, int level) {
        return net.minecraft.world.item.enchantment.Enchantment.getFullname(cbEnchHolder(self), level);
    }
    /** was net/minecraft/class_1887.method_8193()Z -- isTreasureOnly: the #treasure tag */
    public static boolean method_8193(net.minecraft.world.item.enchantment.Enchantment self) { return cbEnchHolder(self).is(net.minecraft.tags.EnchantmentTags.TREASURE); }
    /** was net/minecraft/class_1887.method_8195()Z -- isCurse: the #curse tag */
    public static boolean method_8195(net.minecraft.world.item.enchantment.Enchantment self) { return cbEnchHolder(self).is(net.minecraft.tags.EnchantmentTags.CURSE); }
    /** was net/minecraft/class_1887.method_25949()Z -- isTradeable: the #tradeable tag */
    public static boolean method_25949(net.minecraft.world.item.enchantment.Enchantment self) { return cbEnchHolder(self).is(net.minecraft.tags.EnchantmentTags.TRADEABLE); }
    /** was net/minecraft/class_1887.method_25950()Z -- isDiscoverable: obtainable from the table, loot or trades */
    public static boolean method_25950(net.minecraft.world.item.enchantment.Enchantment self) {
        net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> h = cbEnchHolder(self);
        return h.is(net.minecraft.tags.EnchantmentTags.IN_ENCHANTING_TABLE) || h.is(net.minecraft.tags.EnchantmentTags.ON_RANDOM_LOOT)
            || h.is(net.minecraft.tags.EnchantmentTags.TRADEABLE);
    }
    /** was net/minecraft/class_1887.method_8188(Lnet/minecraft/class_1887;)Z -- isCompatibleWith(other) */
    public static boolean method_8188(net.minecraft.world.item.enchantment.Enchantment self, net.minecraft.world.item.enchantment.Enchantment other) {
        return net.minecraft.world.item.enchantment.Enchantment.areCompatible(cbEnchHolder(self), cbEnchHolder(other));
    }
    /** was net/minecraft/class_1887.method_8180(Lnet/minecraft/class_1887;)Z -- checkCompatibility(other) */
    public static boolean method_8180(net.minecraft.world.item.enchantment.Enchantment self, net.minecraft.world.item.enchantment.Enchantment other) { return method_8188(self, other); }
    /** was net/minecraft/class_1887.method_8191(I)Lnet/minecraft/class_1887; -- byId(numeric id): the live registry's id space */
    public static net.minecraft.world.item.enchantment.Enchantment method_8191(int id) {
        net.minecraft.world.item.enchantment.Enchantment e = cbEnchantmentRegistry().byId(id);
        return e == null ? null : cbEnchValue(cbEnchHolder(e));
    }

    // ---- EnchantmentInstance (class_1889): record since 1.21 (Holder + private components) ----
    /** ctorRedirects: 1.20.1 new EnchantmentInstance(Enchantment, int) */
    public static net.minecraft.world.item.enchantment.EnchantmentInstance cbNewEnchantmentInstance(net.minecraft.world.item.enchantment.Enchantment e, int level) {
        return new net.minecraft.world.item.enchantment.EnchantmentInstance(cbEnchHolder(e), level);
    }
    /** was net/minecraft/class_1889.field_9093Lnet/minecraft/class_1887; -- instance field read (record component) */
    public static net.minecraft.world.item.enchantment.Enchantment field_9093(net.minecraft.world.item.enchantment.EnchantmentInstance self) { return cbEnchValue(self.enchantment()); }
    /** was net/minecraft/class_1889.field_9094I */
    public static int field_9094(net.minecraft.world.item.enchantment.EnchantmentInstance self) { return self.level(); }

    // ---- EnchantedBookItem (class_1772, removed 1.21.2: the book is a plain Item with STORED_ENCHANTMENTS) ----
    /** instanceOfRedirects: `x instanceof EnchantedBookItem` */
    public static boolean cbIsEnchantedBook(Object o) {
        return o instanceof net.minecraft.world.item.Item it && it == net.minecraft.world.item.Items.ENCHANTED_BOOK;
    }
    /** was net/minecraft/class_1772.method_7808(Lnet/minecraft/class_1889;)Lnet/minecraft/class_1799; -- createForEnchantment */
    public static net.minecraft.world.item.ItemStack method_7808(net.minecraft.world.item.enchantment.EnchantmentInstance inst) { return net.minecraft.world.item.enchantment.EnchantmentHelper.createBook(inst); }
    /** was net/minecraft/class_1772.method_7806(Lnet/minecraft/class_1799;)Lnet/minecraft/class_2499; -- getEnchantments(book): stored enchantments as the 1.20.1 {id, lvl} list */
    public static net.minecraft.nbt.ListTag method_7806(net.minecraft.world.item.ItemStack stack) {
        net.minecraft.nbt.ListTag list = new net.minecraft.nbt.ListTag();
        net.minecraft.world.item.enchantment.ItemEnchantments stored =
            stack.getOrDefault(net.minecraft.core.component.DataComponents.STORED_ENCHANTMENTS, net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
        for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment>> en : stored.entrySet()) {
            java.util.Optional<net.minecraft.resources.ResourceKey<net.minecraft.world.item.enchantment.Enchantment>> k = en.getKey().unwrapKey();
            if (k.isPresent()) {
                list.add(method_37426(k.get().identifier(), en.getIntValue()));
            }
        }
        return list;
    }
    /** was net/minecraft/class_1772.method_7807(Lnet/minecraft/class_1799;Lnet/minecraft/class_1889;)V -- addEnchantment(book, instance) */
    public static void method_7807(net.minecraft.world.item.ItemStack stack, net.minecraft.world.item.enchantment.EnchantmentInstance inst) {
        net.minecraft.world.item.enchantment.EnchantmentHelper.updateEnchantments(stack, m -> m.upgrade(inst.enchantment(), inst.level()));
    }

    /** was net/minecraft/class_1299$class_1300.method_5905(Ljava/lang/String;)Lnet/minecraft/class_1299; --
     *  EntityType.Builder.build(String id) took a ResourceKey since 1.21.2; the 1.20.1 string was the entity's
     *  registry id (used for the datafixer choice type). 29 corpus mods; left as-is it is a NoSuchMethodError
     *  in the mod's entrypoint. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.world.entity.EntityType method_5905(net.minecraft.world.entity.EntityType.Builder self, String id) {
        return self.build(net.minecraft.resources.ResourceKey.create(
            net.minecraft.core.registries.Registries.ENTITY_TYPE, net.minecraft.resources.Identifier.parse(id)));
    }

    /** was net/minecraft/class_5251.method_27719(Ljava/lang/String;)Lnet/minecraft/class_5251; -- TextColor.parseColor
     *  returns DataResult since 1.20.3; the 1.20.1 contract was "TextColor or null". */
    public static net.minecraft.network.chat.TextColor method_27719(String s) {
        return net.minecraft.network.chat.TextColor.parseColor(s).result().orElse(null);
    }

    /** was net/minecraft/class_1317.method_20637(Lnet/minecraft/class_1299;Lnet/minecraft/class_1317$class_1319;Lnet/minecraft/class_2902$class_2903;Lnet/minecraft/class_1317$class_4306;)V --
     *  SpawnRestriction.register(type, Location, heightmap, predicate): the class survives as SpawnPlacements, only the
     *  Location enum died (1.20.5 SpawnPlacementType constants); the corpus reaches it through the class_1317 facade enum.
     *  11 corpus mods register mob spawn placements this way, and 4 more through FabricEntityTypeBuilder.Mob. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void method_20637(net.minecraft.world.entity.EntityType type, top.dext.centurybridge.facade.v26_2.class_1317.class_1319 location,
            net.minecraft.world.level.levelgen.Heightmap.Types heightmap, net.minecraft.world.entity.SpawnPlacements.SpawnPredicate predicate) {
        net.minecraft.world.entity.SpawnPlacements.register(type, location.cbToVanilla(), heightmap, predicate);
    }
}
