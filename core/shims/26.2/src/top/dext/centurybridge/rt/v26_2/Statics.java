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

    /** 1.20.1 LootContextPredicate.EMPTY field, removed 1.20.2; field access redirected here
     *  (fieldGetter: converter rewrites GETSTATIC to INVOKESTATIC field_24388()). */
    public static net.minecraft.advancements.predicates.ContextAwarePredicate field_24388() {
        return net.minecraft.advancements.predicates.ContextAwarePredicate.create();
    }

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

    
    /** ANY-constant family, removed with the predicate rework; rebuilt as match-alls.
     *  fieldGetter target: the converter rewrites GETSTATIC class_2073.field_9640
     *  to INVOKESTATIC Statics.field_9640(), so this must be a getter method. */
    public static net.minecraft.advancements.predicates.ItemPredicate field_9640() {
        return net.minecraft.advancements.predicates.ItemPredicate.Builder.item().build();
    }

    public static net.minecraft.advancements.predicates.entity.EntityPredicate field_9599() {
        return net.minecraft.advancements.predicates.entity.EntityPredicate.Builder.entity().build();
    }

    public static net.minecraft.advancements.predicates.NbtPredicate field_9716() {
        return new net.minecraft.advancements.predicates.NbtPredicate((net.minecraft.nbt.CompoundTag) null);
    }

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

    /** was net/minecraft/world/effect/MobEffectInstance/method_5589(Lnet/minecraft/world/entity/LivingEntity;)V */
    public static void method_5589(net.minecraft.world.effect.MobEffectInstance self, net.minecraft.world.entity.LivingEntity a0) {
        self.onEffectStarted(a0);
    }

    /** was net/minecraft/world/entity/Entity/method_5621()D */
    public static double method_5621(net.minecraft.world.entity.Entity self) {
        return (double) self.getBbHeight();
    }

    /** was net/minecraft/world/entity/Mob/method_47922(Lnet/minecraft/world/entity/LivingEntity;)D */
    public static double method_47922(net.minecraft.world.entity.Mob self, net.minecraft.world.entity.LivingEntity a0) {
        return (double) (self.getBbWidth() * 2.0F * self.getBbWidth() * 2.0F + a0.getBbWidth());
    }

    /** was net/minecraft/world/entity/LivingEntity/field_6242D */
    public static double field_6242() {
        return 1.3D;
    }

    /** was net/minecraft/world/entity/ai/attributes/AttributeInstance/removeModifier(Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V */
    public static void method_6202(net.minecraft.world.entity.ai.attributes.AttributeInstance self, net.minecraft.world.entity.ai.attributes.AttributeModifier a0) {
        self.addTransientModifier(a0);
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

    /** was net/minecraft/class_1830.field_30925Ljava/lang/String; */
    public static java.lang.String field_30925() {
        return "Items";
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

    /** was net/minecraft/network/protocol/game/ClientGamePacketListener/handleUpdateTags(Lnet/minecraft/network/protocol/common/ClientboundUpdateTagsPacket;)V */
    public void handleUpdateTags(net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket a0) {
        ((net.minecraft.network.protocol.common.ClientCommonPacketListener) (Object) this).handleUpdateTags(a0);
    }

    /** was net/minecraft/network/protocol/game/ClientGamePacketListener/method_36895(Lnet/minecraft/network/protocol/common/ClientboundPingPacket;)V */
    public static void method_36895(net.minecraft.network.protocol.game.ClientGamePacketListener self, net.minecraft.network.protocol.common.ClientboundPingPacket a0) {
        a0.handle(self);
    }

    /** was net/minecraft/world/level/block/entity/RandomizableContainerBlockEntity/field_31352Ljava/lang/String; */
    public static java.lang.String field_31352() {
        return "Fuel";
    }

    /** was net/minecraft/world/level/block/entity/SignBlockEntity/method_49844(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/level/block/entity/SignText; */
    public static net.minecraft.world.level.block.entity.SignText method_49844(net.minecraft.world.level.block.entity.SignBlockEntity self, net.minecraft.world.entity.player.Player a0) {
        return self.getText(self.isFacingFrontText(a0));
    }

    /** was net/minecraft/world/level/block/entity/StructureBlockEntity/method_11368(Lnet/minecraft/server/level/ServerLevel;Z)Z */
    public static boolean method_11368(net.minecraft.world.level.block.entity.StructureBlockEntity self, net.minecraft.server.level.ServerLevel a0, boolean a1) {
        return self.placeStructureIfSameSize(a0);
    }

    /** was net/minecraft/world/level/block/entity/StructureBlockEntity/method_21865()Ljava/lang/String; */
    public static java.lang.String method_21865(net.minecraft.world.level.block.entity.StructureBlockEntity self) {
        return self.AUTHOR_TAG;
    }

    /** was net/minecraft/network/protocol/game/ServerGamePacketListener/handleClientInformation(Lnet/minecraft/network/protocol/common/ServerboundClientInformationPacket;)V */
    public void handleClientInformation(net.minecraft.network.protocol.common.ServerboundClientInformationPacket a0) {
        ((net.minecraft.network.protocol.common.ServerCommonPacketListener) (Object) this).handleClientInformation(a0);
    }

    /** was net/minecraft/network/protocol/status/ClientStatusPacketListener/handlePongResponse(Lnet/minecraft/network/protocol/ping/ClientboundPongResponsePacket;)V */
    public void handlePongResponse(net.minecraft.network.protocol.ping.ClientboundPongResponsePacket a0) {
        ((net.minecraft.network.protocol.ping.ClientPongPacketListener) (Object) this).handlePongResponse(a0);
    }

    /** was net/minecraft/server/ServerInterface/getPlayerCount()I */
    public int getPlayerCount() {
        return ((net.minecraft.server.ServerInfo) (Object) this).getPlayerCount();
    }

    /** was net/minecraft/server/ServerInterface/getMaxPlayers()I */
    public int getMaxPlayers() {
        return ((net.minecraft.server.ServerInfo) (Object) this).getMaxPlayers();
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

    /** was net/minecraft/client/Options/field_1866Z */
    public static boolean field_1866() {
        return false;
    }

    /** was net/minecraft/client/Options/field_1880Z */
    public static boolean field_1880() {
        return true;
    }

    /** was net/minecraft/server/level/PlayerMap/method_14081(JJLnet/minecraft/server/level/ServerPlayer;)V */
    public static void method_14081(net.minecraft.server.level.PlayerMap self, long a0, long a1, net.minecraft.server.level.ServerPlayer a2) {
        self.unIgnorePlayer(a2);
    }

    /** was net/minecraft/server/network/ServerLoginPacketListenerImpl/method_40085(Ljava/lang/String;)Z */
    public static boolean method_40085(net.minecraft.server.network.ServerLoginPacketListenerImpl self, java.lang.String a0) {
        return a0.chars().filter(c -> c <= 32 || c >= 127).findFirst().isEmpty();
    }

    /** was net/minecraft/client/gui/components/AbstractWidget/field_22757Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_22757() {
        return net.minecraft.resources.Identifier.parse("textures/gui/widgets.png");
    }

    /** was net/minecraft/client/gui/components/AbstractWidget/field_42117Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_42117() {
        return net.minecraft.resources.Identifier.parse("textures/gui/widgets.png");
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

    /** was net/minecraft/class_361.field_2191I */
    public static int field_2191() {
        return 147;
    }

    /** was net/minecraft/class_361.field_2193Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_2193() {
        return net.minecraft.resources.Identifier.parse("textures/gui/recipe_book.png");
    }

    /** was net/minecraft/client/gui/components/spectator/SpectatorGui/field_2199Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_2199() {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", "textures/gui/options_background.png");
    }

    /** was net/minecraft/client/gui/components/toasts/Toast/field_2207Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_2207() {
        return net.minecraft.resources.Identifier.parse("textures/gui/toasts.png");
    }

    /** was net/minecraft/server/level/ChunkMap/field_18243I */
    public static int field_18243() {
        return 34;
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

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen.field_22687Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_22687() {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath("realms", "textures/gui/realms/slot_frame.png");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab.field_22704Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_22704() {
        return net.minecraft.resources.Identifier.parse("textures/gui/recipe_book.png");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.configuration.RealmsPlayersTab.field_22706Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_22706() {
        return net.minecraft.resources.Identifier.parse("textures/gui/container/creative_inventory/creative_inventory.png");
    }

    /** was net/minecraft/com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen.field_20072I */
    public static int field_20072() {
        return 32;
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

    /** was net/minecraft/client/gui/screens/advancements/AdvancementsScreen/field_2716Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_2716() {
        return net.minecraft.resources.Identifier.parse("textures/gui/options_background.png");
    }

    /** was net/minecraft/com.mojang.realmsclient.dto.PendingInvite.field_22583Ljava/lang/String; */
    public static java.lang.String field_22583() {
        return "uuid";
    }

    /** was net/minecraft/client/gui/screens/inventory/SmithingScreen/field_42047I */
    public static int field_42047() {
        return 8;
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

    /** was net/minecraft/client/gui/screens/inventory/tooltip/ClientBundleTooltip/field_28359Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_28359() {
        return net.minecraft.resources.Identifier.parse("textures/gui/container/bundle.png");
    }

    /** was net/minecraft/world/entity/ai/behavior/FollowTemptation/field_30116D */
    public static double field_30116() {
        return 8.0;
    }

    /** was net/minecraft/world/entity/animal/allay/Allay/method_45342()Z */
    public static boolean method_45342(net.minecraft.world.entity.animal.allay.Allay self) {
        return self.isDancing();
    }

    /** was net/minecraft/client/GuiMessageTag$Icon.field_39765I */
    public static int field_39765() {
        return 0;
    }

    /** was net/minecraft/client/GuiMessageTag/field_39761Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_39761() {
        return net.minecraft.resources.Identifier.parse("textures/gui/chat_status.png");
    }

    /** was net/minecraft/network/chat/SignedMessageChain$Decoder.field_40848Lnet/minecraft/network/chat/SignedMessageChain$Decoder; */
    public static net.minecraft.network.chat.SignedMessageChain.Decoder field_40848() {
        return net.minecraft.network.chat.SignedMessageChain.Decoder.unsigned(new java.util.UUID(0L, 0L), () -> false);
    }

    /** was net/minecraft/world/entity/animal/camel/Camel/method_45348()Z */
    public static boolean method_45348(net.minecraft.world.entity.animal.camel.Camel self) {
        return self.isCamelVisuallySitting() && !self.isCamelSitting();
    }

    /** was net/minecraft/core/registries/BuiltInRegistries/field_41154Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier field_41154() {
        return net.minecraft.resources.Identifier.parse("root");
    }

    /** was net/minecraft/world/entity/Display/field_43149Ljava/lang/String; */
    public static java.lang.String field_43149() {
        return "sniffing_state";
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

    /** was net/minecraft/world/entity/animal/equine/Horse/method_6786()Lnet/minecraft/world/item/ItemStack; */
    public static net.minecraft.world.item.ItemStack method_6786(net.minecraft.world.entity.animal.equine.Horse self) {
        return new net.minecraft.world.item.ItemStack(net.minecraft.world.level.block.Blocks.STRIPPED_ACACIA_WOOD);
    }

    /** was net/minecraft/world/level/pathfinder/WalkNodeEvaluator/method_63(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/pathfinder/PathType; */
    public static net.minecraft.world.level.pathfinder.PathType method_63(net.minecraft.world.level.pathfinder.WalkNodeEvaluator self, net.minecraft.world.entity.Mob a0, net.minecraft.core.BlockPos a1) {
        return self.getPathTypeStatic(a0, a1);
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

    /** was net/minecraft/world/entity/monster/zombie/Zombie/field_41028F */
    public static float field_41028() {
        return 0.001f;
    }

    /** was net/minecraft/world/entity/player/Player/field_30643I */
    public static int field_30643() {
        return 100;
    }

    /** was net/minecraft/world/entity/player/Player/method_53968(Ljava/lang/String;)Z */
    public static boolean method_53968(net.minecraft.world.entity.player.Player self, java.lang.String a0) {
        return self.getGameProfile().name().equalsIgnoreCase(a0);
    }

    /** was net/minecraft/world/item/equipment/ArmorMaterial/method_24355()F */
    public static float method_24355(net.minecraft.world.item.equipment.ArmorMaterial self) {
        return self.toughness();
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

    /** was net/minecraft/world/level/saveddata/maps/MapItemSavedData$MapPatch.field_27895I */
    public static int field_27895() {
        return 4;
    }

    /** was net/minecraft/world/level/saveddata/maps/MapItemSavedData$MapPatch.field_27896[B */
    public static byte[] field_27896() {
        return new byte[]{(byte) -119, 80, 78, 71, 13, 10, 26, 10};
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

    /** was net/minecraft/world/level/block/entity/Hopper/field_12025Lnet/minecraft/world/phys/shapes/VoxelShape; */
    public static net.minecraft.world.phys.shapes.VoxelShape field_12025() {
        return net.minecraft.world.phys.shapes.Shapes.block();
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

    /** was net/minecraft/world/level/levelgen/structure/structures/BuriedTreasureStructure/field_37790Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37790() {
        return net.minecraft.world.level.levelgen.structure.structures.BuriedTreasureStructure.CODEC.codec();
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

    /** was net/minecraft/core/GlobalPos/method_19442()Lnet/minecraft/resources/ResourceKey; */
    public static net.minecraft.resources.ResourceKey method_19442(net.minecraft.core.GlobalPos self) {
        return self.dimension();
    }

    /** was net/minecraft/core/GlobalPos/method_19446()Lnet/minecraft/core/BlockPos; */
    public static net.minecraft.core.BlockPos method_19446(net.minecraft.core.GlobalPos self) {
        return self.pos();
    }

    /** was net/minecraft/gametest/framework/GameTestBatch/method_22154()Ljava/util/Collection; */
    public static java.util.Collection method_22154(net.minecraft.gametest.framework.GameTestBatch self) {
        return self.gameTestInfos();
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

    /** was net/minecraft/world/level/biome/MultiNoiseBiomeSource/field_24719Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24719() {
        return net.minecraft.world.level.biome.MultiNoiseBiomeSource.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/templatesystem/AxisAlignedLinearPosTest/field_24995Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24995() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.AxisAlignedLinearPosTest.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/structure/templatesystem/LinearPosTest/field_25004Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25004() {
        return net.minecraft.world.level.levelgen.structure.templatesystem.LinearPosTest.CODEC.codec();
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

    /** was net/minecraft/world/level/levelgen/placement/InSquarePlacement/field_35727Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35727() {
        return net.minecraft.world.level.levelgen.placement.InSquarePlacement.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/placement/CountOnEveryLayerPlacement/field_35716Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35716() {
        return net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement.CODEC.codec();
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

    /** was net/minecraft/world/level/levelgen/heightproviders/BiasedToBottomHeight/field_31531Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_31531() {
        return net.minecraft.world.level.levelgen.heightproviders.BiasedToBottomHeight.CODEC.codec();
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

    /** was net/minecraft/world/entity/monster/warden/Warden$1.field_39306Lnet/minecraft/world/entity/monster/warden/Warden; */
    public static net.minecraft.world.entity.monster.warden.Warden field_39306() {
        return null;
    }

    /** was net/minecraft/world/entity/vehicle/ContainerEntity/getContainerLootTable()Lnet/minecraft/resources/Identifier; */
    public net.minecraft.resources.Identifier getContainerLootTable() {
        return ((net.minecraft.world.entity.vehicle.ContainerEntity) (Object) this).getContainerLootTable().identifier();
    }

    /** was net/minecraft/world/level/levelgen/feature/treedecorators/AttachedToLeavesDecorator/field_38783Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_38783() {
        return net.minecraft.world.level.levelgen.feature.treedecorators.AttachedToLeavesDecorator.CODEC.codec();
    }

    /** was net/minecraft/world/level/levelgen/feature/trunkplacers/UpwardsBranchingTrunkPlacer/field_38793Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_38793() {
        return net.minecraft.world.level.levelgen.feature.trunkplacers.UpwardsBranchingTrunkPlacer.CODEC.codec();
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

    /** was net/minecraft/world/entity/Entity/setKnownMovement(ZLnet/minecraft/world/phys/Vec3;)V */
    public static void method_52208(net.minecraft.world.entity.Entity self, boolean a0, net.minecraft.world.phys.Vec3 a1) {
        self.setDeltaMovement(a1);
        // Can't set velocityModified flag in new API
    }

    /** was net/minecraft/world/entity/Entity/method_5741()I */
    public static int method_5741(net.minecraft.world.entity.Entity self) {
        return self.getRemainingFireTicks();
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

    /** was net/minecraft/world/entity/player/Player/field_49991Z */
    public static boolean field_49991() {
        return true;
        // CB-CONFIDENCE: low -- deleted static boolean constant with no direct replacement; value inferred as true from common feature-flag patterns but unverified
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

    /** was net/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings/method_15120()Z */
    public static boolean method_15120(net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings self) {
        return !self.getProcessors().isEmpty();
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

    /** was net/minecraft/world/entity/Entity.field_28629Z */
    public static boolean field_28629() {
        return false;
    }

    /** was net/minecraft/world/entity/Entity.field_6039F */
    public static float field_6039() {
        return 0.98F;
    }

    /** was net/minecraft/world/entity/EquipmentSlot.method_5926()I */
    public static int method_5926(net.minecraft.world.entity.EquipmentSlot self) {
        return self.getId();
    }

    /** was net/minecraft/world/entity/LivingEntity.method_18397(Lnet/minecraft/world/item/ItemStack;)Z */
    public static boolean method_18397(net.minecraft.world.entity.LivingEntity self, net.minecraft.world.item.ItemStack a0) {
        return true;
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

    /** was net/minecraft/world/entity/player/StackedContents.method_7408(Lnet/minecraft/world/item/ItemStack;)I */
    public static int method_7408(net.minecraft.world.entity.player.StackedContents self, net.minecraft.world.item.ItemStack a0) {
        return self.amounts.getInt(a0);
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

    /** was net/minecraft/util/context/ContextKeySet.method_35553(Lnet/minecraft/util/context/ContextKey;)Z */
    public static boolean method_35553(net.minecraft.util.context.ContextKeySet self, net.minecraft.util.context.ContextKey a0) {
        return self.allowed().contains(a0);
    }

    /** was net/minecraft/world/item/alchemy/Potion.method_8051(Ljava/util/Optional;Ljava/lang/String;)Ljava/lang/String; */
    public static java.lang.String method_8051(net.minecraft.world.item.alchemy.Potion self, java.util.Optional a0, java.lang.String a1) {
        return self.name();
    }

    /** was net/minecraft/world/item/crafting/Recipe.method_31584()Z */
    public static boolean method_31584(net.minecraft.world.item.crafting.Recipe self) {
        return self.showNotification();
    }

    /** was net/minecraft/world/item/crafting/Recipe.method_8113(II)Z */
    public static boolean method_8113(net.minecraft.world.item.crafting.Recipe self, int a0, int a1) {
        return true;
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

    /** was net/minecraft/commands/SharedSuggestionProvider.method_9273()Ljava/util/stream/Stream; */
    public static java.util.stream.Stream method_9273(net.minecraft.commands.SharedSuggestionProvider self) {
        return self.getAvailableSounds();
        // CB-CONFIDENCE: high -- getAvailableSounds() has the identical signature (no args, returns Stream) and is the natural successor to the deleted method_9273()
    }

    /** was net/minecraft/commands/arguments/coordinates/WorldCoordinates.method_9751()Lnet/minecraft/commands/arguments/coordinates/WorldCoordinates; */
    public static net.minecraft.commands.arguments.coordinates.WorldCoordinates method_9751(net.minecraft.commands.arguments.coordinates.WorldCoordinates self) {
        return self;
    }

    /** was net/minecraft/core/Registry.get(Lnet/minecraft/resources/Identifier;)Ljava/lang/Object; */
    public static java.lang.Object method_10223(net.minecraft.core.Registry self, net.minecraft.resources.Identifier a0) {
        return self.get(a0).orElse(null);
    }

    /** was net/minecraft/core/Registry.method_40264(Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional; */
    public static java.util.Optional method_40264(net.minecraft.core.Registry self, net.minecraft.resources.ResourceKey a0) {
        return self.getOptional(a0);
    }

    /** was net/minecraft/core/Registry.method_40270()Ljava/util/stream/Stream; */
    public static java.util.stream.Stream method_40270(net.minecraft.core.Registry self) {
        return self.getTags();
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

    /** was net/minecraft/network/chat/contents/SelectorContents.method_36339()Ljava/util/Optional; */
    public static java.util.Optional method_36339(net.minecraft.network.chat.contents.SelectorContents self) {
        return self.separator();
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

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_17110Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_17110() {
        return new net.minecraft.world.item.crafting.RecipeBookCategories();
        // CB-CONFIDENCE: medium -- field_17110 was a self-typed static (likely a default/empty singleton); the no-arg constructor is the closest equivalent, though singleton identity is not preserved
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_17114Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_17114() {
        return new net.minecraft.world.item.crafting.RecipeBookCategories();
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

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_1812Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_1812() {
        return new net.minecraft.world.item.crafting.RecipeBookCategories();
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_25624Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_25624() {
        return new net.minecraft.world.item.crafting.RecipeBookCategories();
    }

    /** was net/minecraft/world/item/crafting/RecipeBookCategories.field_25625Lnet/minecraft/world/item/crafting/RecipeBookCategories; */
    public static net.minecraft.world.item.crafting.RecipeBookCategories field_25625() {
        return new net.minecraft.world.item.crafting.RecipeBookCategories();
    }

    /** was net/minecraft/sounds/SoundEvent.method_14833()Lnet/minecraft/resources/Identifier; */
    public static net.minecraft.resources.Identifier method_14833(net.minecraft.sounds.SoundEvent self) {
        return self.location();
    }

    /** was net/minecraft/stats/RecipeBook.field_15295Ljava/util/Set; */
    public static java.util.Set field_15295() {
        return java.util.Set.of();
    }

    /** was net/minecraft/stats/RecipeBook.method_14875(Lnet/minecraft/stats/RecipeBook;)V */
    public static void method_14875(net.minecraft.stats.RecipeBook self, net.minecraft.stats.RecipeBook a0) {
        self.setBookSettings(a0.getBookSettings());
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

    /** was net/minecraft/world/entity/ai/sensing/Sensor.field_30258I */
    public static int field_30258() {
        return 2;
    }

    /** was net/minecraft/world/entity/animal/bee/Bee.method_23992(Lnet/minecraft/core/BlockPos;)Z */
    public static boolean method_23992(net.minecraft.world.entity.animal.bee.Bee self, net.minecraft.core.BlockPos a0) {
        return true;
    }

    /** was net/minecraft/world/inventory/ItemCombinerMenu$3.field_7778Lnet/minecraft/world/inventory/ItemCombinerMenu; */
    public static net.minecraft.world.inventory.ItemCombinerMenu field_7778() {
        return null;
    }

    /** was net/minecraft/world/entity/NeutralMob.isAngryAtAllPlayers(Lnet/minecraft/world/level/Level;)Z */
    public static boolean method_29923(net.minecraft.world.entity.NeutralMob self, net.minecraft.world.level.Level a0) {
        return self.isAngryAtAllPlayers((net.minecraft.server.level.ServerLevel) a0);
    }

    /** was net/minecraft/server/network/LegacyTextFilter.field_26833Ljava/util/concurrent/ExecutorService; */
    public static java.util.concurrent.ExecutorService field_26833() {
        return java.util.concurrent.Executors.newCachedThreadPool();
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

    /** was net/minecraft/client/player/ClientInput.field_3908Z */
    public static boolean field_3908() {
        return false;
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

    /** was net/minecraft/world/inventory/ItemCombinerMenuSlotDefinition.method_48365(I)Z */
    public static boolean method_48365(net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition self, int a0) {
        return a0 >= 0 && a0 < self.getNumOfInputSlots();
    }

    /** was net/minecraft/client/multiplayer/ClientCommonPacketListenerImpl.field_51520Z */
    public static boolean field_51520() {
        return false;
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

    // ---- 26.2 colour collections ----
    // 26.2 keeps the sixteen colours of a block or item in one ColorCollection field
    // (Blocks.WOOL.white()); the per-colour fields of every older release answer from it.

    public static net.minecraft.world.level.block.Block cbBlock_BROWN_GLAZED_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.GLAZED_TERRACOTTA.brown();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLUE_CONCRETE() {
        return net.minecraft.world.level.block.Blocks.CONCRETE.blue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIME_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.DYED_TERRACOTTA.lime();
    }

    public static net.minecraft.world.level.block.Block cbBlock_MAGENTA_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.DYED_TERRACOTTA.magenta();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PURPLE_BED() {
        return net.minecraft.world.level.block.Blocks.BED.purple();
    }

    public static net.minecraft.world.level.block.Block cbBlock_ORANGE_CONCRETE_POWDER() {
        return net.minecraft.world.level.block.Blocks.CONCRETE_POWDER.orange();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BROWN_CONCRETE_POWDER() {
        return net.minecraft.world.level.block.Blocks.CONCRETE_POWDER.brown();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIME_WOOL() {
        return net.minecraft.world.level.block.Blocks.WOOL.lime();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GRAY_CONCRETE() {
        return net.minecraft.world.level.block.Blocks.CONCRETE.gray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIME_CARPET() {
        return net.minecraft.world.level.block.Blocks.CARPET.lime();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLUE_CARPET() {
        return net.minecraft.world.level.block.Blocks.CARPET.blue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_ORANGE_BANNER() {
        return net.minecraft.world.level.block.Blocks.BANNER.orange();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIME_GLAZED_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.GLAZED_TERRACOTTA.lime();
    }

    public static net.minecraft.world.level.block.Block cbBlock_YELLOW_STAINED_GLASS() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS.yellow();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_BLUE_WALL_BANNER() {
        return net.minecraft.world.level.block.Blocks.WALL_BANNER.lightBlue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PINK_SHULKER_BOX() {
        return net.minecraft.world.level.block.Blocks.DYED_SHULKER_BOX.pink();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_GRAY_GLAZED_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.GLAZED_TERRACOTTA.lightGray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PURPLE_WALL_BANNER() {
        return net.minecraft.world.level.block.Blocks.WALL_BANNER.purple();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GREEN_SHULKER_BOX() {
        return net.minecraft.world.level.block.Blocks.DYED_SHULKER_BOX.green();
    }

    public static net.minecraft.world.level.block.Block cbBlock_RED_CONCRETE() {
        return net.minecraft.world.level.block.Blocks.CONCRETE.red();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLUE_STAINED_GLASS() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS.blue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLACK_BANNER() {
        return net.minecraft.world.level.block.Blocks.BANNER.black();
    }

    public static net.minecraft.world.level.block.Block cbBlock_MAGENTA_SHULKER_BOX() {
        return net.minecraft.world.level.block.Blocks.DYED_SHULKER_BOX.magenta();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLUE_WALL_BANNER() {
        return net.minecraft.world.level.block.Blocks.WALL_BANNER.blue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_RED_SHULKER_BOX() {
        return net.minecraft.world.level.block.Blocks.DYED_SHULKER_BOX.red();
    }

    public static net.minecraft.world.level.block.Block cbBlock_RED_BED() {
        return net.minecraft.world.level.block.Blocks.BED.red();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLACK_STAINED_GLASS_PANE() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS_PANE.black();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BROWN_STAINED_GLASS() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS.brown();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GRAY_STAINED_GLASS_PANE() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS_PANE.gray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_CYAN_GLAZED_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.GLAZED_TERRACOTTA.cyan();
    }

    public static net.minecraft.world.level.block.Block cbBlock_WHITE_STAINED_GLASS() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS.white();
    }

    public static net.minecraft.world.level.block.Block cbBlock_ORANGE_WOOL() {
        return net.minecraft.world.level.block.Blocks.WOOL.orange();
    }

    public static net.minecraft.world.level.block.Block cbBlock_YELLOW_GLAZED_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.GLAZED_TERRACOTTA.yellow();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLACK_CARPET() {
        return net.minecraft.world.level.block.Blocks.CARPET.black();
    }

    public static net.minecraft.world.level.block.Block cbBlock_WHITE_CONCRETE() {
        return net.minecraft.world.level.block.Blocks.CONCRETE.white();
    }

    public static net.minecraft.world.level.block.Block cbBlock_CYAN_BED() {
        return net.minecraft.world.level.block.Blocks.BED.cyan();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BROWN_WOOL() {
        return net.minecraft.world.level.block.Blocks.WOOL.brown();
    }

    public static net.minecraft.world.level.block.Block cbBlock_RED_STAINED_GLASS_PANE() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS_PANE.red();
    }

    public static net.minecraft.world.level.block.Block cbBlock_WHITE_BED() {
        return net.minecraft.world.level.block.Blocks.BED.white();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BROWN_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.DYED_TERRACOTTA.brown();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_GRAY_STAINED_GLASS_PANE() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS_PANE.lightGray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIME_CONCRETE_POWDER() {
        return net.minecraft.world.level.block.Blocks.CONCRETE_POWDER.lime();
    }

    public static net.minecraft.world.level.block.Block cbBlock_YELLOW_WALL_BANNER() {
        return net.minecraft.world.level.block.Blocks.WALL_BANNER.yellow();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GRAY_SHULKER_BOX() {
        return net.minecraft.world.level.block.Blocks.DYED_SHULKER_BOX.gray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GRAY_BED() {
        return net.minecraft.world.level.block.Blocks.BED.gray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_YELLOW_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.DYED_TERRACOTTA.yellow();
    }

    public static net.minecraft.world.level.block.Block cbBlock_YELLOW_CONCRETE_POWDER() {
        return net.minecraft.world.level.block.Blocks.CONCRETE_POWDER.yellow();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLACK_WOOL() {
        return net.minecraft.world.level.block.Blocks.WOOL.black();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PURPLE_STAINED_GLASS_PANE() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS_PANE.purple();
    }

    public static net.minecraft.world.level.block.Block cbBlock_WHITE_BANNER() {
        return net.minecraft.world.level.block.Blocks.BANNER.white();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIME_STAINED_GLASS() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS.lime();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BROWN_STAINED_GLASS_PANE() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS_PANE.brown();
    }

    public static net.minecraft.world.level.block.Block cbBlock_CYAN_BANNER() {
        return net.minecraft.world.level.block.Blocks.BANNER.cyan();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GREEN_WOOL() {
        return net.minecraft.world.level.block.Blocks.WOOL.green();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_GRAY_CONCRETE() {
        return net.minecraft.world.level.block.Blocks.CONCRETE.lightGray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIME_BED() {
        return net.minecraft.world.level.block.Blocks.BED.lime();
    }

    public static net.minecraft.world.level.block.Block cbBlock_ORANGE_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.DYED_TERRACOTTA.orange();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GRAY_BANNER() {
        return net.minecraft.world.level.block.Blocks.BANNER.gray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_BLUE_STAINED_GLASS_PANE() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS_PANE.lightBlue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_WHITE_CONCRETE_POWDER() {
        return net.minecraft.world.level.block.Blocks.CONCRETE_POWDER.white();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GREEN_BANNER() {
        return net.minecraft.world.level.block.Blocks.BANNER.green();
    }

    public static net.minecraft.world.level.block.Block cbBlock_WHITE_SHULKER_BOX() {
        return net.minecraft.world.level.block.Blocks.DYED_SHULKER_BOX.white();
    }

    public static net.minecraft.world.level.block.Block cbBlock_WHITE_WALL_BANNER() {
        return net.minecraft.world.level.block.Blocks.WALL_BANNER.white();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_BLUE_SHULKER_BOX() {
        return net.minecraft.world.level.block.Blocks.DYED_SHULKER_BOX.lightBlue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PURPLE_CONCRETE() {
        return net.minecraft.world.level.block.Blocks.CONCRETE.purple();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_GRAY_CARPET() {
        return net.minecraft.world.level.block.Blocks.CARPET.lightGray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_ORANGE_CONCRETE() {
        return net.minecraft.world.level.block.Blocks.CONCRETE.orange();
    }

    public static net.minecraft.world.level.block.Block cbBlock_MAGENTA_WOOL() {
        return net.minecraft.world.level.block.Blocks.WOOL.magenta();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GRAY_GLAZED_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.GLAZED_TERRACOTTA.gray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_GRAY_WOOL() {
        return net.minecraft.world.level.block.Blocks.WOOL.lightGray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_ORANGE_STAINED_GLASS() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS.orange();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIME_BANNER() {
        return net.minecraft.world.level.block.Blocks.BANNER.lime();
    }

    public static net.minecraft.world.level.block.Block cbBlock_MAGENTA_BED() {
        return net.minecraft.world.level.block.Blocks.BED.magenta();
    }

    public static net.minecraft.world.level.block.Block cbBlock_CYAN_CONCRETE_POWDER() {
        return net.minecraft.world.level.block.Blocks.CONCRETE_POWDER.cyan();
    }

    public static net.minecraft.world.level.block.Block cbBlock_CYAN_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.DYED_TERRACOTTA.cyan();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_BLUE_CONCRETE() {
        return net.minecraft.world.level.block.Blocks.CONCRETE.lightBlue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_CYAN_STAINED_GLASS() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS.cyan();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PURPLE_WOOL() {
        return net.minecraft.world.level.block.Blocks.WOOL.purple();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GRAY_WALL_BANNER() {
        return net.minecraft.world.level.block.Blocks.WALL_BANNER.gray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PURPLE_SHULKER_BOX() {
        return net.minecraft.world.level.block.Blocks.DYED_SHULKER_BOX.purple();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_BLUE_STAINED_GLASS() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS.lightBlue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_RED_STAINED_GLASS() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS.red();
    }

    public static net.minecraft.world.level.block.Block cbBlock_MAGENTA_WALL_BANNER() {
        return net.minecraft.world.level.block.Blocks.WALL_BANNER.magenta();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIME_SHULKER_BOX() {
        return net.minecraft.world.level.block.Blocks.DYED_SHULKER_BOX.lime();
    }

    public static net.minecraft.world.level.block.Block cbBlock_RED_WALL_BANNER() {
        return net.minecraft.world.level.block.Blocks.WALL_BANNER.red();
    }

    public static net.minecraft.world.level.block.Block cbBlock_ORANGE_GLAZED_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.GLAZED_TERRACOTTA.orange();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLUE_BANNER() {
        return net.minecraft.world.level.block.Blocks.BANNER.blue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_RED_CONCRETE_POWDER() {
        return net.minecraft.world.level.block.Blocks.CONCRETE_POWDER.red();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BROWN_BED() {
        return net.minecraft.world.level.block.Blocks.BED.brown();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_BLUE_CARPET() {
        return net.minecraft.world.level.block.Blocks.CARPET.lightBlue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_BLUE_WOOL() {
        return net.minecraft.world.level.block.Blocks.WOOL.lightBlue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_MAGENTA_CONCRETE_POWDER() {
        return net.minecraft.world.level.block.Blocks.CONCRETE_POWDER.magenta();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIME_STAINED_GLASS_PANE() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS_PANE.lime();
    }

    public static net.minecraft.world.level.block.Block cbBlock_CYAN_CONCRETE() {
        return net.minecraft.world.level.block.Blocks.CONCRETE.cyan();
    }

    public static net.minecraft.world.level.block.Block cbBlock_RED_WOOL() {
        return net.minecraft.world.level.block.Blocks.WOOL.red();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PINK_STAINED_GLASS() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS.pink();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIME_WALL_BANNER() {
        return net.minecraft.world.level.block.Blocks.WALL_BANNER.lime();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_GRAY_SHULKER_BOX() {
        return net.minecraft.world.level.block.Blocks.DYED_SHULKER_BOX.lightGray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_BLUE_CONCRETE_POWDER() {
        return net.minecraft.world.level.block.Blocks.CONCRETE_POWDER.lightBlue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_BLUE_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.DYED_TERRACOTTA.lightBlue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_GRAY_BED() {
        return net.minecraft.world.level.block.Blocks.BED.lightGray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_RED_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.DYED_TERRACOTTA.red();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GREEN_CARPET() {
        return net.minecraft.world.level.block.Blocks.CARPET.green();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_BLUE_GLAZED_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.GLAZED_TERRACOTTA.lightBlue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GRAY_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.DYED_TERRACOTTA.gray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GRAY_CONCRETE_POWDER() {
        return net.minecraft.world.level.block.Blocks.CONCRETE_POWDER.gray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_CYAN_STAINED_GLASS_PANE() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS_PANE.cyan();
    }

    public static net.minecraft.world.level.block.Block cbBlock_YELLOW_BED() {
        return net.minecraft.world.level.block.Blocks.BED.yellow();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GREEN_STAINED_GLASS() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS.green();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GREEN_CONCRETE() {
        return net.minecraft.world.level.block.Blocks.CONCRETE.green();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PURPLE_BANNER() {
        return net.minecraft.world.level.block.Blocks.BANNER.purple();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BROWN_WALL_BANNER() {
        return net.minecraft.world.level.block.Blocks.WALL_BANNER.brown();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLACK_SHULKER_BOX() {
        return net.minecraft.world.level.block.Blocks.DYED_SHULKER_BOX.black();
    }

    public static net.minecraft.world.level.block.Block cbBlock_CYAN_WALL_BANNER() {
        return net.minecraft.world.level.block.Blocks.WALL_BANNER.cyan();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BROWN_SHULKER_BOX() {
        return net.minecraft.world.level.block.Blocks.DYED_SHULKER_BOX.brown();
    }

    public static net.minecraft.world.level.block.Block cbBlock_RED_GLAZED_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.GLAZED_TERRACOTTA.red();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PINK_CARPET() {
        return net.minecraft.world.level.block.Blocks.CARPET.pink();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PURPLE_STAINED_GLASS() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS.purple();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PURPLE_CONCRETE_POWDER() {
        return net.minecraft.world.level.block.Blocks.CONCRETE_POWDER.purple();
    }

    public static net.minecraft.world.level.block.Block cbBlock_RED_BANNER() {
        return net.minecraft.world.level.block.Blocks.BANNER.red();
    }

    public static net.minecraft.world.level.block.Block cbBlock_ORANGE_SHULKER_BOX() {
        return net.minecraft.world.level.block.Blocks.DYED_SHULKER_BOX.orange();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLUE_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.DYED_TERRACOTTA.blue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_ORANGE_BED() {
        return net.minecraft.world.level.block.Blocks.BED.orange();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GREEN_STAINED_GLASS_PANE() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS_PANE.green();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIME_CONCRETE() {
        return net.minecraft.world.level.block.Blocks.CONCRETE.lime();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GRAY_WOOL() {
        return net.minecraft.world.level.block.Blocks.WOOL.gray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PURPLE_GLAZED_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.GLAZED_TERRACOTTA.purple();
    }

    public static net.minecraft.world.level.block.Block cbBlock_CYAN_CARPET() {
        return net.minecraft.world.level.block.Blocks.CARPET.cyan();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PINK_CONCRETE() {
        return net.minecraft.world.level.block.Blocks.CONCRETE.pink();
    }

    public static net.minecraft.world.level.block.Block cbBlock_MAGENTA_BANNER() {
        return net.minecraft.world.level.block.Blocks.BANNER.magenta();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BROWN_CONCRETE() {
        return net.minecraft.world.level.block.Blocks.CONCRETE.brown();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PINK_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.DYED_TERRACOTTA.pink();
    }

    public static net.minecraft.world.level.block.Block cbBlock_WHITE_WOOL() {
        return net.minecraft.world.level.block.Blocks.WOOL.white();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_BLUE_BANNER() {
        return net.minecraft.world.level.block.Blocks.BANNER.lightBlue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLUE_CONCRETE_POWDER() {
        return net.minecraft.world.level.block.Blocks.CONCRETE_POWDER.blue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLACK_CONCRETE() {
        return net.minecraft.world.level.block.Blocks.CONCRETE.black();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PINK_WOOL() {
        return net.minecraft.world.level.block.Blocks.WOOL.pink();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLACK_BED() {
        return net.minecraft.world.level.block.Blocks.BED.black();
    }

    public static net.minecraft.world.level.block.Block cbBlock_WHITE_CARPET() {
        return net.minecraft.world.level.block.Blocks.CARPET.white();
    }

    public static net.minecraft.world.level.block.Block cbBlock_MAGENTA_STAINED_GLASS_PANE() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS_PANE.magenta();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BROWN_CARPET() {
        return net.minecraft.world.level.block.Blocks.CARPET.brown();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GREEN_GLAZED_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.GLAZED_TERRACOTTA.green();
    }

    public static net.minecraft.world.level.block.Block cbBlock_MAGENTA_CARPET() {
        return net.minecraft.world.level.block.Blocks.CARPET.magenta();
    }

    public static net.minecraft.world.level.block.Block cbBlock_YELLOW_WOOL() {
        return net.minecraft.world.level.block.Blocks.WOOL.yellow();
    }

    public static net.minecraft.world.level.block.Block cbBlock_ORANGE_STAINED_GLASS_PANE() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS_PANE.orange();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLACK_GLAZED_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.GLAZED_TERRACOTTA.black();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLACK_CONCRETE_POWDER() {
        return net.minecraft.world.level.block.Blocks.CONCRETE_POWDER.black();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PURPLE_CARPET() {
        return net.minecraft.world.level.block.Blocks.CARPET.purple();
    }

    public static net.minecraft.world.level.block.Block cbBlock_YELLOW_CARPET() {
        return net.minecraft.world.level.block.Blocks.CARPET.yellow();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLUE_WOOL() {
        return net.minecraft.world.level.block.Blocks.WOOL.blue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PINK_CONCRETE_POWDER() {
        return net.minecraft.world.level.block.Blocks.CONCRETE_POWDER.pink();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GREEN_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.DYED_TERRACOTTA.green();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLUE_BED() {
        return net.minecraft.world.level.block.Blocks.BED.blue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GREEN_CONCRETE_POWDER() {
        return net.minecraft.world.level.block.Blocks.CONCRETE_POWDER.green();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PINK_WALL_BANNER() {
        return net.minecraft.world.level.block.Blocks.WALL_BANNER.pink();
    }

    public static net.minecraft.world.level.block.Block cbBlock_CYAN_SHULKER_BOX() {
        return net.minecraft.world.level.block.Blocks.DYED_SHULKER_BOX.cyan();
    }

    public static net.minecraft.world.level.block.Block cbBlock_RED_CARPET() {
        return net.minecraft.world.level.block.Blocks.CARPET.red();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLACK_WALL_BANNER() {
        return net.minecraft.world.level.block.Blocks.WALL_BANNER.black();
    }

    public static net.minecraft.world.level.block.Block cbBlock_MAGENTA_GLAZED_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.GLAZED_TERRACOTTA.magenta();
    }

    public static net.minecraft.world.level.block.Block cbBlock_YELLOW_CONCRETE() {
        return net.minecraft.world.level.block.Blocks.CONCRETE.yellow();
    }

    public static net.minecraft.world.level.block.Block cbBlock_YELLOW_BANNER() {
        return net.minecraft.world.level.block.Blocks.BANNER.yellow();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLUE_GLAZED_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.GLAZED_TERRACOTTA.blue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GRAY_STAINED_GLASS() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS.gray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GREEN_BED() {
        return net.minecraft.world.level.block.Blocks.BED.green();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PINK_STAINED_GLASS_PANE() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS_PANE.pink();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PINK_GLAZED_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.GLAZED_TERRACOTTA.pink();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PURPLE_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.DYED_TERRACOTTA.purple();
    }

    public static net.minecraft.world.level.block.Block cbBlock_MAGENTA_STAINED_GLASS() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS.magenta();
    }

    public static net.minecraft.world.level.block.Block cbBlock_YELLOW_STAINED_GLASS_PANE() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS_PANE.yellow();
    }

    public static net.minecraft.world.level.block.Block cbBlock_MAGENTA_CONCRETE() {
        return net.minecraft.world.level.block.Blocks.CONCRETE.magenta();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_GRAY_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.DYED_TERRACOTTA.lightGray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GRAY_CARPET() {
        return net.minecraft.world.level.block.Blocks.CARPET.gray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GREEN_WALL_BANNER() {
        return net.minecraft.world.level.block.Blocks.WALL_BANNER.green();
    }

    public static net.minecraft.world.level.block.Block cbBlock_WHITE_GLAZED_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.GLAZED_TERRACOTTA.white();
    }

    public static net.minecraft.world.level.block.Block cbBlock_ORANGE_WALL_BANNER() {
        return net.minecraft.world.level.block.Blocks.WALL_BANNER.orange();
    }

    public static net.minecraft.world.level.block.Block cbBlock_YELLOW_SHULKER_BOX() {
        return net.minecraft.world.level.block.Blocks.DYED_SHULKER_BOX.yellow();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BROWN_BANNER() {
        return net.minecraft.world.level.block.Blocks.BANNER.brown();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_GRAY_WALL_BANNER() {
        return net.minecraft.world.level.block.Blocks.WALL_BANNER.lightGray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLUE_SHULKER_BOX() {
        return net.minecraft.world.level.block.Blocks.DYED_SHULKER_BOX.blue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PINK_BED() {
        return net.minecraft.world.level.block.Blocks.BED.pink();
    }

    public static net.minecraft.world.level.block.Block cbBlock_WHITE_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.DYED_TERRACOTTA.white();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PINK_BANNER() {
        return net.minecraft.world.level.block.Blocks.BANNER.pink();
    }

    public static net.minecraft.world.level.block.Block cbBlock_CYAN_WOOL() {
        return net.minecraft.world.level.block.Blocks.WOOL.cyan();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_BLUE_BED() {
        return net.minecraft.world.level.block.Blocks.BED.lightBlue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLACK_TERRACOTTA() {
        return net.minecraft.world.level.block.Blocks.DYED_TERRACOTTA.black();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_GRAY_CONCRETE_POWDER() {
        return net.minecraft.world.level.block.Blocks.CONCRETE_POWDER.lightGray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_WHITE_CANDLE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE.white();
    }

    public static net.minecraft.world.level.block.Block cbBlock_ORANGE_CANDLE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE.orange();
    }

    public static net.minecraft.world.level.block.Block cbBlock_MAGENTA_CANDLE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE.magenta();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_BLUE_CANDLE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE.lightBlue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_YELLOW_CANDLE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE.yellow();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIME_CANDLE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE.lime();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PINK_CANDLE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE.pink();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GRAY_CANDLE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE.gray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_GRAY_CANDLE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE.lightGray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_CYAN_CANDLE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE.cyan();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PURPLE_CANDLE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE.purple();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLUE_CANDLE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE.blue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BROWN_CANDLE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE.brown();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GREEN_CANDLE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE.green();
    }

    public static net.minecraft.world.level.block.Block cbBlock_RED_CANDLE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE.red();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLACK_CANDLE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE.black();
    }

    public static net.minecraft.world.level.block.Block cbBlock_WHITE_CANDLE_CAKE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE_CAKE.white();
    }

    public static net.minecraft.world.level.block.Block cbBlock_ORANGE_CANDLE_CAKE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE_CAKE.orange();
    }

    public static net.minecraft.world.level.block.Block cbBlock_MAGENTA_CANDLE_CAKE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE_CAKE.magenta();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_BLUE_CANDLE_CAKE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE_CAKE.lightBlue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_YELLOW_CANDLE_CAKE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE_CAKE.yellow();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIME_CANDLE_CAKE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE_CAKE.lime();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PINK_CANDLE_CAKE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE_CAKE.pink();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GRAY_CANDLE_CAKE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE_CAKE.gray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_GRAY_CANDLE_CAKE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE_CAKE.lightGray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_CYAN_CANDLE_CAKE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE_CAKE.cyan();
    }

    public static net.minecraft.world.level.block.Block cbBlock_PURPLE_CANDLE_CAKE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE_CAKE.purple();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLUE_CANDLE_CAKE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE_CAKE.blue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BROWN_CANDLE_CAKE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE_CAKE.brown();
    }

    public static net.minecraft.world.level.block.Block cbBlock_GREEN_CANDLE_CAKE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE_CAKE.green();
    }

    public static net.minecraft.world.level.block.Block cbBlock_RED_CANDLE_CAKE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE_CAKE.red();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLACK_CANDLE_CAKE() {
        return net.minecraft.world.level.block.Blocks.DYED_CANDLE_CAKE.black();
    }

    public static net.minecraft.world.level.block.Block cbBlock_ORANGE_CARPET() {
        return net.minecraft.world.level.block.Blocks.CARPET.orange();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLUE_STAINED_GLASS_PANE() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS_PANE.blue();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_GRAY_BANNER() {
        return net.minecraft.world.level.block.Blocks.BANNER.lightGray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_WHITE_STAINED_GLASS_PANE() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS_PANE.white();
    }

    public static net.minecraft.world.level.block.Block cbBlock_LIGHT_GRAY_STAINED_GLASS() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS.lightGray();
    }

    public static net.minecraft.world.level.block.Block cbBlock_BLACK_STAINED_GLASS() {
        return net.minecraft.world.level.block.Blocks.STAINED_GLASS.black();
    }

    public static net.minecraft.world.item.Item cbItem_WHITE_WOOL() {
        return net.minecraft.world.item.Items.WOOL.white();
    }

    public static net.minecraft.world.item.Item cbItem_ORANGE_WOOL() {
        return net.minecraft.world.item.Items.WOOL.orange();
    }

    public static net.minecraft.world.item.Item cbItem_MAGENTA_WOOL() {
        return net.minecraft.world.item.Items.WOOL.magenta();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_BLUE_WOOL() {
        return net.minecraft.world.item.Items.WOOL.lightBlue();
    }

    public static net.minecraft.world.item.Item cbItem_YELLOW_WOOL() {
        return net.minecraft.world.item.Items.WOOL.yellow();
    }

    public static net.minecraft.world.item.Item cbItem_LIME_WOOL() {
        return net.minecraft.world.item.Items.WOOL.lime();
    }

    public static net.minecraft.world.item.Item cbItem_PINK_WOOL() {
        return net.minecraft.world.item.Items.WOOL.pink();
    }

    public static net.minecraft.world.item.Item cbItem_GRAY_WOOL() {
        return net.minecraft.world.item.Items.WOOL.gray();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_GRAY_WOOL() {
        return net.minecraft.world.item.Items.WOOL.lightGray();
    }

    public static net.minecraft.world.item.Item cbItem_CYAN_WOOL() {
        return net.minecraft.world.item.Items.WOOL.cyan();
    }

    public static net.minecraft.world.item.Item cbItem_PURPLE_WOOL() {
        return net.minecraft.world.item.Items.WOOL.purple();
    }

    public static net.minecraft.world.item.Item cbItem_BLUE_WOOL() {
        return net.minecraft.world.item.Items.WOOL.blue();
    }

    public static net.minecraft.world.item.Item cbItem_BROWN_WOOL() {
        return net.minecraft.world.item.Items.WOOL.brown();
    }

    public static net.minecraft.world.item.Item cbItem_GREEN_WOOL() {
        return net.minecraft.world.item.Items.WOOL.green();
    }

    public static net.minecraft.world.item.Item cbItem_RED_WOOL() {
        return net.minecraft.world.item.Items.WOOL.red();
    }

    public static net.minecraft.world.item.Item cbItem_BLACK_WOOL() {
        return net.minecraft.world.item.Items.WOOL.black();
    }

    public static net.minecraft.world.item.Item cbItem_WHITE_CANDLE() {
        return net.minecraft.world.item.Items.DYED_CANDLE.white();
    }

    public static net.minecraft.world.item.Item cbItem_ORANGE_CANDLE() {
        return net.minecraft.world.item.Items.DYED_CANDLE.orange();
    }

    public static net.minecraft.world.item.Item cbItem_MAGENTA_CANDLE() {
        return net.minecraft.world.item.Items.DYED_CANDLE.magenta();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_BLUE_CANDLE() {
        return net.minecraft.world.item.Items.DYED_CANDLE.lightBlue();
    }

    public static net.minecraft.world.item.Item cbItem_YELLOW_CANDLE() {
        return net.minecraft.world.item.Items.DYED_CANDLE.yellow();
    }

    public static net.minecraft.world.item.Item cbItem_LIME_CANDLE() {
        return net.minecraft.world.item.Items.DYED_CANDLE.lime();
    }

    public static net.minecraft.world.item.Item cbItem_PINK_CANDLE() {
        return net.minecraft.world.item.Items.DYED_CANDLE.pink();
    }

    public static net.minecraft.world.item.Item cbItem_GRAY_CANDLE() {
        return net.minecraft.world.item.Items.DYED_CANDLE.gray();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_GRAY_CANDLE() {
        return net.minecraft.world.item.Items.DYED_CANDLE.lightGray();
    }

    public static net.minecraft.world.item.Item cbItem_CYAN_CANDLE() {
        return net.minecraft.world.item.Items.DYED_CANDLE.cyan();
    }

    public static net.minecraft.world.item.Item cbItem_PURPLE_CANDLE() {
        return net.minecraft.world.item.Items.DYED_CANDLE.purple();
    }

    public static net.minecraft.world.item.Item cbItem_BLUE_CANDLE() {
        return net.minecraft.world.item.Items.DYED_CANDLE.blue();
    }

    public static net.minecraft.world.item.Item cbItem_BROWN_CANDLE() {
        return net.minecraft.world.item.Items.DYED_CANDLE.brown();
    }

    public static net.minecraft.world.item.Item cbItem_GREEN_CANDLE() {
        return net.minecraft.world.item.Items.DYED_CANDLE.green();
    }

    public static net.minecraft.world.item.Item cbItem_RED_CANDLE() {
        return net.minecraft.world.item.Items.DYED_CANDLE.red();
    }

    public static net.minecraft.world.item.Item cbItem_BLACK_CANDLE() {
        return net.minecraft.world.item.Items.DYED_CANDLE.black();
    }

    public static net.minecraft.world.item.Item cbItem_WHITE_BUNDLE() {
        return net.minecraft.world.item.Items.DYED_BUNDLE.white();
    }

    public static net.minecraft.world.item.Item cbItem_ORANGE_BUNDLE() {
        return net.minecraft.world.item.Items.DYED_BUNDLE.orange();
    }

    public static net.minecraft.world.item.Item cbItem_MAGENTA_BUNDLE() {
        return net.minecraft.world.item.Items.DYED_BUNDLE.magenta();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_BLUE_BUNDLE() {
        return net.minecraft.world.item.Items.DYED_BUNDLE.lightBlue();
    }

    public static net.minecraft.world.item.Item cbItem_YELLOW_BUNDLE() {
        return net.minecraft.world.item.Items.DYED_BUNDLE.yellow();
    }

    public static net.minecraft.world.item.Item cbItem_LIME_BUNDLE() {
        return net.minecraft.world.item.Items.DYED_BUNDLE.lime();
    }

    public static net.minecraft.world.item.Item cbItem_PINK_BUNDLE() {
        return net.minecraft.world.item.Items.DYED_BUNDLE.pink();
    }

    public static net.minecraft.world.item.Item cbItem_GRAY_BUNDLE() {
        return net.minecraft.world.item.Items.DYED_BUNDLE.gray();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_GRAY_BUNDLE() {
        return net.minecraft.world.item.Items.DYED_BUNDLE.lightGray();
    }

    public static net.minecraft.world.item.Item cbItem_CYAN_BUNDLE() {
        return net.minecraft.world.item.Items.DYED_BUNDLE.cyan();
    }

    public static net.minecraft.world.item.Item cbItem_PURPLE_BUNDLE() {
        return net.minecraft.world.item.Items.DYED_BUNDLE.purple();
    }

    public static net.minecraft.world.item.Item cbItem_BLUE_BUNDLE() {
        return net.minecraft.world.item.Items.DYED_BUNDLE.blue();
    }

    public static net.minecraft.world.item.Item cbItem_BROWN_BUNDLE() {
        return net.minecraft.world.item.Items.DYED_BUNDLE.brown();
    }

    public static net.minecraft.world.item.Item cbItem_GREEN_BUNDLE() {
        return net.minecraft.world.item.Items.DYED_BUNDLE.green();
    }

    public static net.minecraft.world.item.Item cbItem_RED_BUNDLE() {
        return net.minecraft.world.item.Items.DYED_BUNDLE.red();
    }

    public static net.minecraft.world.item.Item cbItem_BLACK_BUNDLE() {
        return net.minecraft.world.item.Items.DYED_BUNDLE.black();
    }

    public static net.minecraft.world.item.Item cbItem_YELLOW_HARNESS() {
        return net.minecraft.world.item.Items.HARNESS.yellow();
    }

    public static net.minecraft.world.item.Item cbItem_LIME_HARNESS() {
        return net.minecraft.world.item.Items.HARNESS.lime();
    }

    public static net.minecraft.world.item.Item cbItem_PINK_HARNESS() {
        return net.minecraft.world.item.Items.HARNESS.pink();
    }

    public static net.minecraft.world.item.Item cbItem_GRAY_HARNESS() {
        return net.minecraft.world.item.Items.HARNESS.gray();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_GRAY_HARNESS() {
        return net.minecraft.world.item.Items.HARNESS.lightGray();
    }

    public static net.minecraft.world.item.Item cbItem_CYAN_HARNESS() {
        return net.minecraft.world.item.Items.HARNESS.cyan();
    }

    public static net.minecraft.world.item.Item cbItem_PURPLE_HARNESS() {
        return net.minecraft.world.item.Items.HARNESS.purple();
    }

    public static net.minecraft.world.item.Item cbItem_BLUE_HARNESS() {
        return net.minecraft.world.item.Items.HARNESS.blue();
    }

    public static net.minecraft.world.item.Item cbItem_BROWN_HARNESS() {
        return net.minecraft.world.item.Items.HARNESS.brown();
    }

    public static net.minecraft.world.item.Item cbItem_GREEN_HARNESS() {
        return net.minecraft.world.item.Items.HARNESS.green();
    }

    public static net.minecraft.world.item.Item cbItem_RED_HARNESS() {
        return net.minecraft.world.item.Items.HARNESS.red();
    }

    public static net.minecraft.world.item.Item cbItem_BLACK_HARNESS() {
        return net.minecraft.world.item.Items.HARNESS.black();
    }

    public static net.minecraft.world.item.Item cbItem_WHITE_HARNESS() {
        return net.minecraft.world.item.Items.HARNESS.white();
    }

    public static net.minecraft.world.item.Item cbItem_ORANGE_HARNESS() {
        return net.minecraft.world.item.Items.HARNESS.orange();
    }

    public static net.minecraft.world.item.Item cbItem_MAGENTA_HARNESS() {
        return net.minecraft.world.item.Items.HARNESS.magenta();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_BLUE_HARNESS() {
        return net.minecraft.world.item.Items.HARNESS.lightBlue();
    }

    public static net.minecraft.world.item.Item cbItem_ORANGE_TERRACOTTA() {
        return net.minecraft.world.item.Items.DYED_TERRACOTTA.orange();
    }

    public static net.minecraft.world.item.Item cbItem_YELLOW_BANNER() {
        return net.minecraft.world.item.Items.BANNER.yellow();
    }

    public static net.minecraft.world.item.Item cbItem_MAGENTA_SHULKER_BOX() {
        return net.minecraft.world.item.Items.DYED_SHULKER_BOX.magenta();
    }

    public static net.minecraft.world.item.Item cbItem_ORANGE_BED() {
        return net.minecraft.world.item.Items.BED.orange();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_BLUE_CARPET() {
        return net.minecraft.world.item.Items.CARPET.lightBlue();
    }

    public static net.minecraft.world.item.Item cbItem_CYAN_STAINED_GLASS_PANE() {
        return net.minecraft.world.item.Items.STAINED_GLASS_PANE.cyan();
    }

    public static net.minecraft.world.item.Item cbItem_YELLOW_STAINED_GLASS() {
        return net.minecraft.world.item.Items.STAINED_GLASS.yellow();
    }

    public static net.minecraft.world.item.Item cbItem_BLACK_GLAZED_TERRACOTTA() {
        return net.minecraft.world.item.Items.GLAZED_TERRACOTTA.black();
    }

    public static net.minecraft.world.item.Item cbItem_PURPLE_CARPET() {
        return net.minecraft.world.item.Items.CARPET.purple();
    }

    public static net.minecraft.world.item.Item cbItem_BROWN_DYE() {
        return net.minecraft.world.item.Items.DYE.brown();
    }

    public static net.minecraft.world.item.Item cbItem_BLACK_BED() {
        return net.minecraft.world.item.Items.BED.black();
    }

    public static net.minecraft.world.item.Item cbItem_BLUE_CARPET() {
        return net.minecraft.world.item.Items.CARPET.blue();
    }

    public static net.minecraft.world.item.Item cbItem_MAGENTA_STAINED_GLASS_PANE() {
        return net.minecraft.world.item.Items.STAINED_GLASS_PANE.magenta();
    }

    public static net.minecraft.world.item.Item cbItem_GREEN_CONCRETE() {
        return net.minecraft.world.item.Items.CONCRETE.green();
    }

    public static net.minecraft.world.item.Item cbItem_BROWN_BANNER() {
        return net.minecraft.world.item.Items.BANNER.brown();
    }

    public static net.minecraft.world.item.Item cbItem_BLUE_STAINED_GLASS() {
        return net.minecraft.world.item.Items.STAINED_GLASS.blue();
    }

    public static net.minecraft.world.item.Item cbItem_PINK_CONCRETE() {
        return net.minecraft.world.item.Items.CONCRETE.pink();
    }

    public static net.minecraft.world.item.Item cbItem_BLUE_BANNER() {
        return net.minecraft.world.item.Items.BANNER.blue();
    }

    public static net.minecraft.world.item.Item cbItem_LIME_DYE() {
        return net.minecraft.world.item.Items.DYE.lime();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_GRAY_TERRACOTTA() {
        return net.minecraft.world.item.Items.DYED_TERRACOTTA.lightGray();
    }

    public static net.minecraft.world.item.Item cbItem_ORANGE_GLAZED_TERRACOTTA() {
        return net.minecraft.world.item.Items.GLAZED_TERRACOTTA.orange();
    }

    public static net.minecraft.world.item.Item cbItem_YELLOW_CARPET() {
        return net.minecraft.world.item.Items.CARPET.yellow();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_GRAY_BED() {
        return net.minecraft.world.item.Items.BED.lightGray();
    }

    public static net.minecraft.world.item.Item cbItem_WHITE_TERRACOTTA() {
        return net.minecraft.world.item.Items.DYED_TERRACOTTA.white();
    }

    public static net.minecraft.world.item.Item cbItem_BLACK_STAINED_GLASS_PANE() {
        return net.minecraft.world.item.Items.STAINED_GLASS_PANE.black();
    }

    public static net.minecraft.world.item.Item cbItem_BLUE_CONCRETE_POWDER() {
        return net.minecraft.world.item.Items.CONCRETE_POWDER.blue();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_GRAY_GLAZED_TERRACOTTA() {
        return net.minecraft.world.item.Items.GLAZED_TERRACOTTA.lightGray();
    }

    public static net.minecraft.world.item.Item cbItem_WHITE_GLAZED_TERRACOTTA() {
        return net.minecraft.world.item.Items.GLAZED_TERRACOTTA.white();
    }

    public static net.minecraft.world.item.Item cbItem_BLACK_TERRACOTTA() {
        return net.minecraft.world.item.Items.DYED_TERRACOTTA.black();
    }

    public static net.minecraft.world.item.Item cbItem_YELLOW_DYE() {
        return net.minecraft.world.item.Items.DYE.yellow();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_BLUE_STAINED_GLASS_PANE() {
        return net.minecraft.world.item.Items.STAINED_GLASS_PANE.lightBlue();
    }

    public static net.minecraft.world.item.Item cbItem_RED_CONCRETE() {
        return net.minecraft.world.item.Items.CONCRETE.red();
    }

    public static net.minecraft.world.item.Item cbItem_GREEN_CONCRETE_POWDER() {
        return net.minecraft.world.item.Items.CONCRETE_POWDER.green();
    }

    public static net.minecraft.world.item.Item cbItem_YELLOW_CONCRETE_POWDER() {
        return net.minecraft.world.item.Items.CONCRETE_POWDER.yellow();
    }

    public static net.minecraft.world.item.Item cbItem_CYAN_SHULKER_BOX() {
        return net.minecraft.world.item.Items.DYED_SHULKER_BOX.cyan();
    }

    public static net.minecraft.world.item.Item cbItem_PINK_CONCRETE_POWDER() {
        return net.minecraft.world.item.Items.CONCRETE_POWDER.pink();
    }

    public static net.minecraft.world.item.Item cbItem_BLACK_DYE() {
        return net.minecraft.world.item.Items.DYE.black();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_GRAY_STAINED_GLASS_PANE() {
        return net.minecraft.world.item.Items.STAINED_GLASS_PANE.lightGray();
    }

    public static net.minecraft.world.item.Item cbItem_MAGENTA_STAINED_GLASS() {
        return net.minecraft.world.item.Items.STAINED_GLASS.magenta();
    }

    public static net.minecraft.world.item.Item cbItem_GREEN_GLAZED_TERRACOTTA() {
        return net.minecraft.world.item.Items.GLAZED_TERRACOTTA.green();
    }

    public static net.minecraft.world.item.Item cbItem_LIME_CARPET() {
        return net.minecraft.world.item.Items.CARPET.lime();
    }

    public static net.minecraft.world.item.Item cbItem_CYAN_GLAZED_TERRACOTTA() {
        return net.minecraft.world.item.Items.GLAZED_TERRACOTTA.cyan();
    }

    public static net.minecraft.world.item.Item cbItem_WHITE_BED() {
        return net.minecraft.world.item.Items.BED.white();
    }

    public static net.minecraft.world.item.Item cbItem_PURPLE_BED() {
        return net.minecraft.world.item.Items.BED.purple();
    }

    public static net.minecraft.world.item.Item cbItem_RED_DYE() {
        return net.minecraft.world.item.Items.DYE.red();
    }

    public static net.minecraft.world.item.Item cbItem_BLACK_SHULKER_BOX() {
        return net.minecraft.world.item.Items.DYED_SHULKER_BOX.black();
    }

    public static net.minecraft.world.item.Item cbItem_YELLOW_SHULKER_BOX() {
        return net.minecraft.world.item.Items.DYED_SHULKER_BOX.yellow();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_BLUE_DYE() {
        return net.minecraft.world.item.Items.DYE.lightBlue();
    }

    public static net.minecraft.world.item.Item cbItem_PINK_GLAZED_TERRACOTTA() {
        return net.minecraft.world.item.Items.GLAZED_TERRACOTTA.pink();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_BLUE_BED() {
        return net.minecraft.world.item.Items.BED.lightBlue();
    }

    public static net.minecraft.world.item.Item cbItem_CYAN_CARPET() {
        return net.minecraft.world.item.Items.CARPET.cyan();
    }

    public static net.minecraft.world.item.Item cbItem_BROWN_CARPET() {
        return net.minecraft.world.item.Items.CARPET.brown();
    }

    public static net.minecraft.world.item.Item cbItem_GREEN_BANNER() {
        return net.minecraft.world.item.Items.BANNER.green();
    }

    public static net.minecraft.world.item.Item cbItem_PURPLE_DYE() {
        return net.minecraft.world.item.Items.DYE.purple();
    }

    public static net.minecraft.world.item.Item cbItem_GRAY_DYE() {
        return net.minecraft.world.item.Items.DYE.gray();
    }

    public static net.minecraft.world.item.Item cbItem_GRAY_TERRACOTTA() {
        return net.minecraft.world.item.Items.DYED_TERRACOTTA.gray();
    }

    public static net.minecraft.world.item.Item cbItem_MAGENTA_GLAZED_TERRACOTTA() {
        return net.minecraft.world.item.Items.GLAZED_TERRACOTTA.magenta();
    }

    public static net.minecraft.world.item.Item cbItem_PINK_BANNER() {
        return net.minecraft.world.item.Items.BANNER.pink();
    }

    public static net.minecraft.world.item.Item cbItem_PINK_DYE() {
        return net.minecraft.world.item.Items.DYE.pink();
    }

    public static net.minecraft.world.item.Item cbItem_BROWN_STAINED_GLASS() {
        return net.minecraft.world.item.Items.STAINED_GLASS.brown();
    }

    public static net.minecraft.world.item.Item cbItem_GRAY_CONCRETE() {
        return net.minecraft.world.item.Items.CONCRETE.gray();
    }

    public static net.minecraft.world.item.Item cbItem_MAGENTA_CONCRETE_POWDER() {
        return net.minecraft.world.item.Items.CONCRETE_POWDER.magenta();
    }

    public static net.minecraft.world.item.Item cbItem_LIME_STAINED_GLASS() {
        return net.minecraft.world.item.Items.STAINED_GLASS.lime();
    }

    public static net.minecraft.world.item.Item cbItem_WHITE_CONCRETE() {
        return net.minecraft.world.item.Items.CONCRETE.white();
    }

    public static net.minecraft.world.item.Item cbItem_BLUE_DYE() {
        return net.minecraft.world.item.Items.DYE.blue();
    }

    public static net.minecraft.world.item.Item cbItem_MAGENTA_BED() {
        return net.minecraft.world.item.Items.BED.magenta();
    }

    public static net.minecraft.world.item.Item cbItem_BLUE_SHULKER_BOX() {
        return net.minecraft.world.item.Items.DYED_SHULKER_BOX.blue();
    }

    public static net.minecraft.world.item.Item cbItem_RED_TERRACOTTA() {
        return net.minecraft.world.item.Items.DYED_TERRACOTTA.red();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_GRAY_STAINED_GLASS() {
        return net.minecraft.world.item.Items.STAINED_GLASS.lightGray();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_BLUE_CONCRETE() {
        return net.minecraft.world.item.Items.CONCRETE.lightBlue();
    }

    public static net.minecraft.world.item.Item cbItem_GREEN_BED() {
        return net.minecraft.world.item.Items.BED.green();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_BLUE_BANNER() {
        return net.minecraft.world.item.Items.BANNER.lightBlue();
    }

    public static net.minecraft.world.item.Item cbItem_ORANGE_SHULKER_BOX() {
        return net.minecraft.world.item.Items.DYED_SHULKER_BOX.orange();
    }

    public static net.minecraft.world.item.Item cbItem_MAGENTA_CARPET() {
        return net.minecraft.world.item.Items.CARPET.magenta();
    }

    public static net.minecraft.world.item.Item cbItem_YELLOW_TERRACOTTA() {
        return net.minecraft.world.item.Items.DYED_TERRACOTTA.yellow();
    }

    public static net.minecraft.world.item.Item cbItem_CYAN_BED() {
        return net.minecraft.world.item.Items.BED.cyan();
    }

    public static net.minecraft.world.item.Item cbItem_ORANGE_STAINED_GLASS() {
        return net.minecraft.world.item.Items.STAINED_GLASS.orange();
    }

    public static net.minecraft.world.item.Item cbItem_BROWN_GLAZED_TERRACOTTA() {
        return net.minecraft.world.item.Items.GLAZED_TERRACOTTA.brown();
    }

    public static net.minecraft.world.item.Item cbItem_PURPLE_BANNER() {
        return net.minecraft.world.item.Items.BANNER.purple();
    }

    public static net.minecraft.world.item.Item cbItem_GREEN_DYE() {
        return net.minecraft.world.item.Items.DYE.green();
    }

    public static net.minecraft.world.item.Item cbItem_BLACK_STAINED_GLASS() {
        return net.minecraft.world.item.Items.STAINED_GLASS.black();
    }

    public static net.minecraft.world.item.Item cbItem_PURPLE_CONCRETE() {
        return net.minecraft.world.item.Items.CONCRETE.purple();
    }

    public static net.minecraft.world.item.Item cbItem_PINK_BED() {
        return net.minecraft.world.item.Items.BED.pink();
    }

    public static net.minecraft.world.item.Item cbItem_LIME_CONCRETE_POWDER() {
        return net.minecraft.world.item.Items.CONCRETE_POWDER.lime();
    }

    public static net.minecraft.world.item.Item cbItem_BROWN_CONCRETE_POWDER() {
        return net.minecraft.world.item.Items.CONCRETE_POWDER.brown();
    }

    public static net.minecraft.world.item.Item cbItem_WHITE_DYE() {
        return net.minecraft.world.item.Items.DYE.white();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_GRAY_SHULKER_BOX() {
        return net.minecraft.world.item.Items.DYED_SHULKER_BOX.lightGray();
    }

    public static net.minecraft.world.item.Item cbItem_BLUE_TERRACOTTA() {
        return net.minecraft.world.item.Items.DYED_TERRACOTTA.blue();
    }

    public static net.minecraft.world.item.Item cbItem_GREEN_SHULKER_BOX() {
        return net.minecraft.world.item.Items.DYED_SHULKER_BOX.green();
    }

    public static net.minecraft.world.item.Item cbItem_BROWN_BED() {
        return net.minecraft.world.item.Items.BED.brown();
    }

    public static net.minecraft.world.item.Item cbItem_BROWN_TERRACOTTA() {
        return net.minecraft.world.item.Items.DYED_TERRACOTTA.brown();
    }

    public static net.minecraft.world.item.Item cbItem_RED_CARPET() {
        return net.minecraft.world.item.Items.CARPET.red();
    }

    public static net.minecraft.world.item.Item cbItem_WHITE_STAINED_GLASS() {
        return net.minecraft.world.item.Items.STAINED_GLASS.white();
    }

    public static net.minecraft.world.item.Item cbItem_BLUE_GLAZED_TERRACOTTA() {
        return net.minecraft.world.item.Items.GLAZED_TERRACOTTA.blue();
    }

    public static net.minecraft.world.item.Item cbItem_ORANGE_CONCRETE_POWDER() {
        return net.minecraft.world.item.Items.CONCRETE_POWDER.orange();
    }

    public static net.minecraft.world.item.Item cbItem_ORANGE_DYE() {
        return net.minecraft.world.item.Items.DYE.orange();
    }

    public static net.minecraft.world.item.Item cbItem_PINK_STAINED_GLASS_PANE() {
        return net.minecraft.world.item.Items.STAINED_GLASS_PANE.pink();
    }

    public static net.minecraft.world.item.Item cbItem_BROWN_STAINED_GLASS_PANE() {
        return net.minecraft.world.item.Items.STAINED_GLASS_PANE.brown();
    }

    public static net.minecraft.world.item.Item cbItem_GRAY_STAINED_GLASS() {
        return net.minecraft.world.item.Items.STAINED_GLASS.gray();
    }

    public static net.minecraft.world.item.Item cbItem_MAGENTA_CONCRETE() {
        return net.minecraft.world.item.Items.CONCRETE.magenta();
    }

    public static net.minecraft.world.item.Item cbItem_BLACK_CONCRETE_POWDER() {
        return net.minecraft.world.item.Items.CONCRETE_POWDER.black();
    }

    public static net.minecraft.world.item.Item cbItem_PINK_SHULKER_BOX() {
        return net.minecraft.world.item.Items.DYED_SHULKER_BOX.pink();
    }

    public static net.minecraft.world.item.Item cbItem_WHITE_BANNER() {
        return net.minecraft.world.item.Items.BANNER.white();
    }

    public static net.minecraft.world.item.Item cbItem_LIME_SHULKER_BOX() {
        return net.minecraft.world.item.Items.DYED_SHULKER_BOX.lime();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_GRAY_CONCRETE_POWDER() {
        return net.minecraft.world.item.Items.CONCRETE_POWDER.lightGray();
    }

    public static net.minecraft.world.item.Item cbItem_PURPLE_GLAZED_TERRACOTTA() {
        return net.minecraft.world.item.Items.GLAZED_TERRACOTTA.purple();
    }

    public static net.minecraft.world.item.Item cbItem_BLACK_BANNER() {
        return net.minecraft.world.item.Items.BANNER.black();
    }

    public static net.minecraft.world.item.Item cbItem_PINK_CARPET() {
        return net.minecraft.world.item.Items.CARPET.pink();
    }

    public static net.minecraft.world.item.Item cbItem_LIME_STAINED_GLASS_PANE() {
        return net.minecraft.world.item.Items.STAINED_GLASS_PANE.lime();
    }

    public static net.minecraft.world.item.Item cbItem_WHITE_CONCRETE_POWDER() {
        return net.minecraft.world.item.Items.CONCRETE_POWDER.white();
    }

    public static net.minecraft.world.item.Item cbItem_BROWN_SHULKER_BOX() {
        return net.minecraft.world.item.Items.DYED_SHULKER_BOX.brown();
    }

    public static net.minecraft.world.item.Item cbItem_RED_BANNER() {
        return net.minecraft.world.item.Items.BANNER.red();
    }

    public static net.minecraft.world.item.Item cbItem_CYAN_CONCRETE_POWDER() {
        return net.minecraft.world.item.Items.CONCRETE_POWDER.cyan();
    }

    public static net.minecraft.world.item.Item cbItem_BLACK_CARPET() {
        return net.minecraft.world.item.Items.CARPET.black();
    }

    public static net.minecraft.world.item.Item cbItem_GRAY_BANNER() {
        return net.minecraft.world.item.Items.BANNER.gray();
    }

    public static net.minecraft.world.item.Item cbItem_GRAY_SHULKER_BOX() {
        return net.minecraft.world.item.Items.DYED_SHULKER_BOX.gray();
    }

    public static net.minecraft.world.item.Item cbItem_CYAN_BANNER() {
        return net.minecraft.world.item.Items.BANNER.cyan();
    }

    public static net.minecraft.world.item.Item cbItem_CYAN_DYE() {
        return net.minecraft.world.item.Items.DYE.cyan();
    }

    public static net.minecraft.world.item.Item cbItem_RED_STAINED_GLASS() {
        return net.minecraft.world.item.Items.STAINED_GLASS.red();
    }

    public static net.minecraft.world.item.Item cbItem_CYAN_CONCRETE() {
        return net.minecraft.world.item.Items.CONCRETE.cyan();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_BLUE_GLAZED_TERRACOTTA() {
        return net.minecraft.world.item.Items.GLAZED_TERRACOTTA.lightBlue();
    }

    public static net.minecraft.world.item.Item cbItem_LIME_GLAZED_TERRACOTTA() {
        return net.minecraft.world.item.Items.GLAZED_TERRACOTTA.lime();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_GRAY_CARPET() {
        return net.minecraft.world.item.Items.CARPET.lightGray();
    }

    public static net.minecraft.world.item.Item cbItem_GREEN_STAINED_GLASS_PANE() {
        return net.minecraft.world.item.Items.STAINED_GLASS_PANE.green();
    }

    public static net.minecraft.world.item.Item cbItem_GREEN_CARPET() {
        return net.minecraft.world.item.Items.CARPET.green();
    }

    public static net.minecraft.world.item.Item cbItem_MAGENTA_DYE() {
        return net.minecraft.world.item.Items.DYE.magenta();
    }

    public static net.minecraft.world.item.Item cbItem_MAGENTA_BANNER() {
        return net.minecraft.world.item.Items.BANNER.magenta();
    }

    public static net.minecraft.world.item.Item cbItem_LIME_TERRACOTTA() {
        return net.minecraft.world.item.Items.DYED_TERRACOTTA.lime();
    }

    public static net.minecraft.world.item.Item cbItem_RED_SHULKER_BOX() {
        return net.minecraft.world.item.Items.DYED_SHULKER_BOX.red();
    }

    public static net.minecraft.world.item.Item cbItem_LIME_BED() {
        return net.minecraft.world.item.Items.BED.lime();
    }

    public static net.minecraft.world.item.Item cbItem_ORANGE_CARPET() {
        return net.minecraft.world.item.Items.CARPET.orange();
    }

    public static net.minecraft.world.item.Item cbItem_CYAN_STAINED_GLASS() {
        return net.minecraft.world.item.Items.STAINED_GLASS.cyan();
    }

    public static net.minecraft.world.item.Item cbItem_YELLOW_CONCRETE() {
        return net.minecraft.world.item.Items.CONCRETE.yellow();
    }

    public static net.minecraft.world.item.Item cbItem_PURPLE_CONCRETE_POWDER() {
        return net.minecraft.world.item.Items.CONCRETE_POWDER.purple();
    }

    public static net.minecraft.world.item.Item cbItem_YELLOW_STAINED_GLASS_PANE() {
        return net.minecraft.world.item.Items.STAINED_GLASS_PANE.yellow();
    }

    public static net.minecraft.world.item.Item cbItem_BLACK_CONCRETE() {
        return net.minecraft.world.item.Items.CONCRETE.black();
    }

    public static net.minecraft.world.item.Item cbItem_PURPLE_TERRACOTTA() {
        return net.minecraft.world.item.Items.DYED_TERRACOTTA.purple();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_BLUE_TERRACOTTA() {
        return net.minecraft.world.item.Items.DYED_TERRACOTTA.lightBlue();
    }

    public static net.minecraft.world.item.Item cbItem_WHITE_SHULKER_BOX() {
        return net.minecraft.world.item.Items.DYED_SHULKER_BOX.white();
    }

    public static net.minecraft.world.item.Item cbItem_GREEN_STAINED_GLASS() {
        return net.minecraft.world.item.Items.STAINED_GLASS.green();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_GRAY_CONCRETE() {
        return net.minecraft.world.item.Items.CONCRETE.lightGray();
    }

    public static net.minecraft.world.item.Item cbItem_WHITE_STAINED_GLASS_PANE() {
        return net.minecraft.world.item.Items.STAINED_GLASS_PANE.white();
    }

    public static net.minecraft.world.item.Item cbItem_BLUE_CONCRETE() {
        return net.minecraft.world.item.Items.CONCRETE.blue();
    }

    public static net.minecraft.world.item.Item cbItem_PURPLE_STAINED_GLASS_PANE() {
        return net.minecraft.world.item.Items.STAINED_GLASS_PANE.purple();
    }

    public static net.minecraft.world.item.Item cbItem_BLUE_STAINED_GLASS_PANE() {
        return net.minecraft.world.item.Items.STAINED_GLASS_PANE.blue();
    }

    public static net.minecraft.world.item.Item cbItem_GRAY_BED() {
        return net.minecraft.world.item.Items.BED.gray();
    }

    public static net.minecraft.world.item.Item cbItem_RED_CONCRETE_POWDER() {
        return net.minecraft.world.item.Items.CONCRETE_POWDER.red();
    }

    public static net.minecraft.world.item.Item cbItem_ORANGE_STAINED_GLASS_PANE() {
        return net.minecraft.world.item.Items.STAINED_GLASS_PANE.orange();
    }

    public static net.minecraft.world.item.Item cbItem_BROWN_CONCRETE() {
        return net.minecraft.world.item.Items.CONCRETE.brown();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_BLUE_CONCRETE_POWDER() {
        return net.minecraft.world.item.Items.CONCRETE_POWDER.lightBlue();
    }

    public static net.minecraft.world.item.Item cbItem_PINK_STAINED_GLASS() {
        return net.minecraft.world.item.Items.STAINED_GLASS.pink();
    }

    public static net.minecraft.world.item.Item cbItem_ORANGE_CONCRETE() {
        return net.minecraft.world.item.Items.CONCRETE.orange();
    }

    public static net.minecraft.world.item.Item cbItem_LIME_BANNER() {
        return net.minecraft.world.item.Items.BANNER.lime();
    }

    public static net.minecraft.world.item.Item cbItem_MAGENTA_TERRACOTTA() {
        return net.minecraft.world.item.Items.DYED_TERRACOTTA.magenta();
    }

    public static net.minecraft.world.item.Item cbItem_RED_BED() {
        return net.minecraft.world.item.Items.BED.red();
    }

    public static net.minecraft.world.item.Item cbItem_GREEN_TERRACOTTA() {
        return net.minecraft.world.item.Items.DYED_TERRACOTTA.green();
    }

    public static net.minecraft.world.item.Item cbItem_PURPLE_SHULKER_BOX() {
        return net.minecraft.world.item.Items.DYED_SHULKER_BOX.purple();
    }

    public static net.minecraft.world.item.Item cbItem_GRAY_CONCRETE_POWDER() {
        return net.minecraft.world.item.Items.CONCRETE_POWDER.gray();
    }

    public static net.minecraft.world.item.Item cbItem_CYAN_TERRACOTTA() {
        return net.minecraft.world.item.Items.DYED_TERRACOTTA.cyan();
    }

    public static net.minecraft.world.item.Item cbItem_ORANGE_BANNER() {
        return net.minecraft.world.item.Items.BANNER.orange();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_BLUE_SHULKER_BOX() {
        return net.minecraft.world.item.Items.DYED_SHULKER_BOX.lightBlue();
    }

    public static net.minecraft.world.item.Item cbItem_PURPLE_STAINED_GLASS() {
        return net.minecraft.world.item.Items.STAINED_GLASS.purple();
    }

    public static net.minecraft.world.item.Item cbItem_LIME_CONCRETE() {
        return net.minecraft.world.item.Items.CONCRETE.lime();
    }

    public static net.minecraft.world.item.Item cbItem_WHITE_CARPET() {
        return net.minecraft.world.item.Items.CARPET.white();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_GRAY_DYE() {
        return net.minecraft.world.item.Items.DYE.lightGray();
    }

    public static net.minecraft.world.item.Item cbItem_PINK_TERRACOTTA() {
        return net.minecraft.world.item.Items.DYED_TERRACOTTA.pink();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_GRAY_BANNER() {
        return net.minecraft.world.item.Items.BANNER.lightGray();
    }

    public static net.minecraft.world.item.Item cbItem_YELLOW_BED() {
        return net.minecraft.world.item.Items.BED.yellow();
    }

    public static net.minecraft.world.item.Item cbItem_LIGHT_BLUE_STAINED_GLASS() {
        return net.minecraft.world.item.Items.STAINED_GLASS.lightBlue();
    }

    public static net.minecraft.world.item.Item cbItem_RED_GLAZED_TERRACOTTA() {
        return net.minecraft.world.item.Items.GLAZED_TERRACOTTA.red();
    }

    public static net.minecraft.world.item.Item cbItem_GRAY_STAINED_GLASS_PANE() {
        return net.minecraft.world.item.Items.STAINED_GLASS_PANE.gray();
    }

    public static net.minecraft.world.item.Item cbItem_GRAY_CARPET() {
        return net.minecraft.world.item.Items.CARPET.gray();
    }

    public static net.minecraft.world.item.Item cbItem_RED_STAINED_GLASS_PANE() {
        return net.minecraft.world.item.Items.STAINED_GLASS_PANE.red();
    }

    public static net.minecraft.world.item.Item cbItem_GRAY_GLAZED_TERRACOTTA() {
        return net.minecraft.world.item.Items.GLAZED_TERRACOTTA.gray();
    }

    public static net.minecraft.world.item.Item cbItem_YELLOW_GLAZED_TERRACOTTA() {
        return net.minecraft.world.item.Items.GLAZED_TERRACOTTA.yellow();
    }

    public static net.minecraft.world.item.Item cbItem_BLUE_BED() {
        return net.minecraft.world.item.Items.BED.blue();
    }

    private Statics() {
    }

    // ---- 26.2 GUI model: DrawContext facade over GuiGraphicsExtractor ----
    private static final ThreadLocal<java.util.Set<Object>> CB_IN_OLD_RENDER =
        ThreadLocal.withInitial(() -> java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>()));

    // ---- 1.20.1 tooltips: appendTooltip(stack, world, list, context) -> appendHoverText ----

    /** the client level, set on the client (MinecraftLevelBridge); null on a dedicated server */
    public static volatile java.util.function.Supplier<net.minecraft.world.level.Level> cbClientLevel = () -> null;

    /**
     * The world 1.20.1 tooltip code was given: the player's world -- on the client the
     * client level; tooltips built without a player (creative search) got null then too.
     */
    public static net.minecraft.world.level.Level cbTooltipLevel() {
        return cbClientLevel.get();
    }

    /**
     * overrideHooks bridge: the appendHoverText vanilla calls, answered by the mod's old
     * appendTooltip -- its lines are added in order to what vanilla collects.
     */
    public static void cbAppendHoverText(CbLegacyTooltipItem self, net.minecraft.world.item.ItemStack stack,
            net.minecraft.world.item.Item.TooltipContext context,
            net.minecraft.world.item.component.TooltipDisplay display,
            java.util.function.Consumer<net.minecraft.network.chat.Component> tooltip,
            net.minecraft.world.item.TooltipFlag flag) {
        java.util.List<net.minecraft.network.chat.Component> lines = new java.util.ArrayList<>();
        self.method_7851(stack, cbTooltipLevel(), lines, flag);
        lines.forEach(tooltip);
    }

    /**
     * instanceRedirects: a call to the old shape -- a mod's super.appendTooltip(...) above
     * all -- runs the item's tooltip as 26.2 has it, its lines into the old list. Through a
     * converted override this re-enters the synthesized appendHoverText, whose guard sends
     * it on to the parent's appendHoverText: the super call 1.20.1 made.
     */
    public static void method_7851(net.minecraft.world.item.Item self, net.minecraft.world.item.ItemStack stack,
            net.minecraft.world.level.Level world, java.util.List<net.minecraft.network.chat.Component> tooltip,
            net.minecraft.world.item.TooltipFlag flag) {
        self.appendHoverText(stack,
            world == null ? net.minecraft.world.item.Item.TooltipContext.EMPTY
                : net.minecraft.world.item.Item.TooltipContext.of(world),
            net.minecraft.world.item.component.TooltipDisplay.DEFAULT, tooltip::add, flag);
    }

    /**
     * instanceRedirects: a converted block's super.appendTooltip(...) -- 1.20.1's
     * Block.appendTooltip was empty (the vanilla blocks that listed contents, shulker
     * boxes and the like, show them through item components on 26.2).
     */
    public static void method_9568(net.minecraft.world.level.block.Block self, net.minecraft.world.item.ItemStack stack,
            net.minecraft.world.level.BlockGetter world, java.util.List<net.minecraft.network.chat.Component> tooltip,
            net.minecraft.world.item.TooltipFlag flag) {
    }

    // ---- 1.20.1 AbstractBlock.onStateReplaced super calls (see ChunkStateReplacedBridge) ----

    /**
     * instanceRedirects: super.onStateReplaced into AbstractBlock / Block / BlockWithEntity:
     * the 1.20.1 body removed the block entity when the block itself changed.
     */
    public static void method_9536(net.minecraft.world.level.block.state.BlockBehaviour self,
            net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level world,
            net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState newState, boolean moved) {
        if (state.hasBlockEntity() && !state.is(newState.getBlock())) {
            world.removeBlockEntity(pos);
        }
    }

    /**
     * superRedirects: super.onStateReplaced into a vanilla container block (chest, barrel,
     * furnaces, brewing stand, dispenser, hopper): 1.20.1 scattered the block entity's
     * inventory and updated comparators before the AbstractBlock body.
     */
    // the converter types the receiver as the super-call owner: one overload per container block
    public static void cbStateReplacedDropping(net.minecraft.world.level.block.ChestBlock self,
            net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level world,
            net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState newState, boolean moved) {
        stateReplacedDropping(self, state, world, pos, newState, moved);
    }

    public static void cbStateReplacedDropping(net.minecraft.world.level.block.BarrelBlock self,
            net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level world,
            net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState newState, boolean moved) {
        stateReplacedDropping(self, state, world, pos, newState, moved);
    }

    public static void cbStateReplacedDropping(net.minecraft.world.level.block.AbstractFurnaceBlock self,
            net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level world,
            net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState newState, boolean moved) {
        stateReplacedDropping(self, state, world, pos, newState, moved);
    }

    public static void cbStateReplacedDropping(net.minecraft.world.level.block.BrewingStandBlock self,
            net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level world,
            net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState newState, boolean moved) {
        stateReplacedDropping(self, state, world, pos, newState, moved);
    }

    public static void cbStateReplacedDropping(net.minecraft.world.level.block.DispenserBlock self,
            net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level world,
            net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState newState, boolean moved) {
        stateReplacedDropping(self, state, world, pos, newState, moved);
    }

    public static void cbStateReplacedDropping(net.minecraft.world.level.block.HopperBlock self,
            net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level world,
            net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState newState, boolean moved) {
        stateReplacedDropping(self, state, world, pos, newState, moved);
    }

    private static void stateReplacedDropping(net.minecraft.world.level.block.state.BlockBehaviour self,
            net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level world,
            net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            net.minecraft.world.level.block.entity.BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof net.minecraft.world.Container container) {
                net.minecraft.world.Containers.dropContents(world, pos, container);
                world.updateNeighbourForOutputSignal(pos, state.getBlock());
            }
        }
        method_9536(self, state, world, pos, newState, moved);
    }

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

    /**
     * 1.20.1 ExtraCodecs.validate (class_5699.method_48112): wrap a codec with a
     * post-decode validation function. Removed from ExtraCodecs at 1.20.5 when
     * DataFixerUpper moved the capability onto Codec itself as the default
     * instance method {@code Codec.validate(Function)}. Forward to it so a mod's
     * static codec initializer (e.g. Ad Astra) that calls the old static works.
     */
    public static <T> com.mojang.serialization.Codec<T> method_48112(
            com.mojang.serialization.Codec<T> codec,
            java.util.function.Function<T, com.mojang.serialization.DataResult<T>> validator) {
        return codec.validate(validator);
    }
    /**
     * 1.20.1 ChunkPos(long) packed-long constructor (class_1923): 26.2 replaced
     * it with the static factory ChunkPos.unpack(long). Reached by both direct
     * new and ChunkPos::new method references.
     */
    public static net.minecraft.world.level.ChunkPos cbNewChunkPos(long packed) {
        return net.minecraft.world.level.ChunkPos.unpack(packed);
    }
    /**
     * 1.20.1 MushroomBlock(class_2420) constructor took (Properties,
     * ResourceKey<ConfiguredFeature>); 26.2 reordered it to (ResourceKey,
     * Properties). Reorder for a legacy call site (Ad Astra ModBlocks).
     */
    public static net.minecraft.world.level.block.MushroomBlock cbNewclass_2420(
            net.minecraft.world.level.block.state.BlockBehaviour.Properties props,
            net.minecraft.resources.ResourceKey<net.minecraft.world.level.levelgen.feature.ConfiguredFeature<?, ?>> feature) {
        return new net.minecraft.world.level.block.MushroomBlock(feature, props);
    }
    /** 1.20.x ButtonBlock(class_2269) (Properties,BlockSetType,int,boolean) ->
     *  26.2 (BlockSetType,int,Properties); the trailing boolean (arrow-sound
     *  flag) folded into BlockSetType, dropped. */
    public static net.minecraft.world.level.block.ButtonBlock cbNewclass_2269(
            net.minecraft.world.level.block.state.BlockBehaviour.Properties props,
            net.minecraft.world.level.block.state.properties.BlockSetType setType, int ticks, boolean arrows) {
        return new net.minecraft.world.level.block.ButtonBlock(setType, ticks, props);
    }

    /** 1.20.x DropExperienceBlock(class_2431) (Properties,IntProvider) ->
     *  26.2 (IntProvider,Properties). */
    public static net.minecraft.world.level.block.DropExperienceBlock cbNewclass_2431(
            net.minecraft.world.level.block.state.BlockBehaviour.Properties props,
            net.minecraft.util.valueproviders.IntProvider xp) {
        return new net.minecraft.world.level.block.DropExperienceBlock(xp, props);
    }

    /** 1.20.x FenceGateBlock(class_2349) (Properties,WoodType) ->
     *  26.2 (WoodType,Properties). */
    public static net.minecraft.world.level.block.FenceGateBlock cbNewclass_2349(
            net.minecraft.world.level.block.state.BlockBehaviour.Properties props,
            net.minecraft.world.level.block.state.properties.WoodType wood) {
        return new net.minecraft.world.level.block.FenceGateBlock(wood, props);
    }
    /** Default XP IntProvider for 1.20.1 DropExperienceBlock(Properties) (no-xp
     *  overload); 26.2 requires an IntProvider, 0 preserves the old no-drop. */
    public static net.minecraft.util.valueproviders.IntProvider cbDefaultXp() {
        return net.minecraft.util.valueproviders.ConstantInt.of(0);
    }

    /** Default leaf-particle chance for 1.20.1 LeavesBlock(Properties); 26.2
     *  added a float particle chance, vanilla leaves use 0.01. */
    public static float cbLeafParticle() {
        return 0.01F;
    }
    /** 1.20.1 Torch/WallTorchBlock took a ParticleOptions; 26.2 narrowed it to
     *  SimpleParticleType. Vanilla torch particles (FLAME/SOUL_FIRE_FLAME) are
     *  all SimpleParticleType, so cast; fall back to FLAME for the rare custom
     *  non-simple particle (a torch must still render some flame). */
    public static net.minecraft.core.particles.SimpleParticleType cbToSimpleParticle(
            net.minecraft.core.particles.ParticleOptions p) {
        return p instanceof net.minecraft.core.particles.SimpleParticleType s
            ? s : net.minecraft.core.particles.ParticleTypes.FLAME;
    }
    /** 1.20.1 FlowerBlock(MobEffect, int ticks, Properties) -> 26.2
     *  (Holder<MobEffect>, float seconds, Properties). Wrap the effect as a
     *  registry Holder and convert the stew-effect duration from ticks to
     *  seconds (26.2's makeEffectList multiplies back by 20). */
    public static net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> cbMobEffectHolder(
            net.minecraft.world.effect.MobEffect effect) {
        return net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
    }

    public static float cbTickToSeconds(int ticks) {
        return ticks / 20.0F;
    }
    /** 1.20.1 SpawnEggItem(EntityType, bgColor, highlightColor, Properties) ->
     *  26.2 SpawnEggItem(Properties): the egg's entity became data-driven, read
     *  from the stack's ENTITY_DATA component (SpawnEggItem.getType). Attach the
     *  entity type as that component so the egg still spawns the right mob; the
     *  two legacy colors are dropped (26.2 derives egg colors from the entity's
     *  own spawn-egg definition, not the item constructor). */
    @SuppressWarnings({"unchecked","rawtypes"})
    public static net.minecraft.world.item.SpawnEggItem cbNewSpawnEgg(
            net.minecraft.world.entity.EntityType<?> type, int bg, int hl,
            net.minecraft.world.item.Item.Properties props) {
        props.component((net.minecraft.core.component.DataComponentType) net.minecraft.core.component.DataComponents.ENTITY_DATA,
            net.minecraft.world.item.component.TypedEntityData.of(type, new net.minecraft.nbt.CompoundTag()));
        return new net.minecraft.world.item.SpawnEggItem(props);
    }
    /** Convert a legacy Tier facade (class_1832, the old interface mods
     *  implemented for custom tool materials) into a 26.2 ToolMaterial record.
     *  26.2 replaced the mining-LEVEL int with an incorrect-blocks TagKey, so
     *  map level 0-4 to the vanilla WOODEN/STONE/IRON/DIAMOND/NETHERITE tool
     *  tags. The repair Ingredient has no TagKey equivalent (ingredient->tag is
     *  not derivable), so repairItems degrades to an empty CB tag: the tool
     *  works, only anvil material-repair is lost (not a load/crash issue). */
    public static net.minecraft.world.item.ToolMaterial cbTierToMaterial(
            top.dext.centurybridge.facade.v26_2.class_1832 tier) {
        int level = tier.method_8024();
        net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> incorrect;
        switch (level <= 0 ? 0 : (level >= 4 ? 4 : level)) {
            case 0 -> incorrect = net.minecraft.tags.BlockTags.INCORRECT_FOR_WOODEN_TOOL;
            case 1 -> incorrect = net.minecraft.tags.BlockTags.INCORRECT_FOR_STONE_TOOL;
            case 2 -> incorrect = net.minecraft.tags.BlockTags.INCORRECT_FOR_IRON_TOOL;
            case 3 -> incorrect = net.minecraft.tags.BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
            default -> incorrect = net.minecraft.tags.BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
        }
        net.minecraft.tags.TagKey<net.minecraft.world.item.Item> repair =
            net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM,
                net.minecraft.resources.Identifier.fromNamespaceAndPath("centurybridge", "tier_repair"));
        return new net.minecraft.world.item.ToolMaterial(incorrect, tier.method_8025(),
            tier.method_8027(), tier.method_8028(), tier.method_8026(), repair);
    }
    /** 1.20.1 HoeItem took int attackDamage; 26.2 takes float. Widen. */
    public static float cbIntToFloat(int v) {
        return (float) v;
    }
    /** 1.20.1 ArmorItem.Type(class_8051) facade -> 26.2 ArmorType. */
    public static net.minecraft.world.item.equipment.ArmorType cbArmorType(
            top.dext.centurybridge.facade.v26_2.class_8051 t) {
        switch (t) {
            case field_41934: return net.minecraft.world.item.equipment.ArmorType.HELMET;
            case field_41935: return net.minecraft.world.item.equipment.ArmorType.CHESTPLATE;
            case field_41936: return net.minecraft.world.item.equipment.ArmorType.LEGGINGS;
            default:          return net.minecraft.world.item.equipment.ArmorType.BOOTS;
        }
    }

    /** 1.20.1 ArmorMaterial(class_1741) interface facade -> 26.2 ArmorMaterial
     *  record. 26.2 record: (durability, defense Map<ArmorType,Integer>,
     *  enchantValue, equipSound Holder, toughness, knockbackResistance,
     *  repairItems TagKey, assetId ResourceKey<EquipmentAsset>). Pull per-slot
     *  defense/durability from the legacy accessors; equipSound uses the generic
     *  armor-equip; repairItems degrades to an empty CB tag (ingredient->tag not
     *  derivable, anvil material-repair lost, not fatal); assetId falls back to
     *  the IRON equipment asset so the armor renders with iron textures rather
     *  than a missing-texture (custom armor model is a client-asset concern
     *  26.2 drives from data, un-portable from a 1.20.1 jar). */
    public static net.minecraft.world.item.equipment.ArmorMaterial cbArmorMaterial(
            top.dext.centurybridge.facade.v26_2.class_1741 mat) {
        java.util.Map<net.minecraft.world.item.equipment.ArmorType, Integer> defense = new java.util.EnumMap<>(net.minecraft.world.item.equipment.ArmorType.class);
        top.dext.centurybridge.facade.v26_2.class_8051[] slots = {
            top.dext.centurybridge.facade.v26_2.class_8051.field_41934,
            top.dext.centurybridge.facade.v26_2.class_8051.field_41935,
            top.dext.centurybridge.facade.v26_2.class_8051.field_41936,
            top.dext.centurybridge.facade.v26_2.class_8051.field_41937};
        int durability = 1;
        for (top.dext.centurybridge.facade.v26_2.class_8051 sl : slots) {
            try {
                defense.put(cbArmorType(sl), mat.method_48402(sl));
                durability = Math.max(durability, mat.method_48403(sl));
            } catch (Throwable ignored) { }
        }
        net.minecraft.tags.TagKey<net.minecraft.world.item.Item> repair =
            net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM,
                net.minecraft.resources.Identifier.fromNamespaceAndPath("centurybridge", "armor_repair"));
        return new net.minecraft.world.item.equipment.ArmorMaterial(
            durability, defense, mat.method_7699(),
            net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_GENERIC,
            mat.method_7700(), mat.method_24355(), repair,
            net.minecraft.world.item.equipment.EquipmentAssets.IRON);
    }

    /** was net/minecraft/world/level/pathfinder/WalkNodeEvaluator/checkNeighbourBlocks(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos$MutableBlockPos;Lnet/minecraft/world/level/pathfinder/PathType;)Lnet/minecraft/world/level/pathfinder/PathType; */
    public static net.minecraft.world.level.pathfinder.PathType method_59(net.minecraft.world.level.BlockGetter a0, net.minecraft.core.BlockPos.MutableBlockPos a1, net.minecraft.world.level.pathfinder.PathType a2) {
        return net.minecraft.world.level.pathfinder.WalkNodeEvaluator.checkNeighbourBlocks(new net.minecraft.world.level.pathfinder.PathfindingContext((net.minecraft.world.level.CollisionGetter) a0, null), a1.getX(), a1.getY(), a1.getZ(), a2);
    }

    /** was net/minecraft/world/inventory/BeaconMenu/decodeEffect(I)Lnet/minecraft/world/effect/MobEffect; */
    public static net.minecraft.world.effect.MobEffect method_53174(int a0) {
        net.minecraft.core.Holder entry = net.minecraft.world.inventory.BeaconMenu.decodeEffect(a0);
        return entry == null ? null : (net.minecraft.world.effect.MobEffect) entry.value();
    }

    /** was net/minecraft/world/inventory/CraftingMenu.slotChangedCraftingGrid(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/inventory/ResultContainer;Lnet/minecraft/world/item/crafting/RecipeHolder;)V
     *  CraftingScreenHandler.updateResult(handler, world, player, input, result, recipe): the old body ran
     *  only off the client (`!world.isClient`); 1.21.2 made that the parameter type */
    public static void method_17399(net.minecraft.world.inventory.AbstractContainerMenu a0, net.minecraft.world.level.Level a1, net.minecraft.world.entity.player.Player a2, net.minecraft.world.inventory.CraftingContainer a3, net.minecraft.world.inventory.ResultContainer a4, net.minecraft.world.item.crafting.RecipeHolder a5) {
        if (a1 instanceof net.minecraft.server.level.ServerLevel world) {
            top.dext.centurybridge.shims.v26_2.CraftingScreenHandlerInvoker.cb$updateResult(a0, world, a2, a3, a4, a5);
        }
    }

    /** was net/minecraft/world/item/CrossbowItem/getChargeDuration(Lnet/minecraft/world/item/ItemStack;)I */
    public static int method_7775(net.minecraft.world.item.ItemStack a0) {
        return net.minecraft.world.item.CrossbowItem.getChargeDuration(a0, null);
    }

    /** was net/minecraft/world/item/CrossbowItem/performShooting(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;FF)V
     *  CrossbowItem.shootAll(world, shooter, hand, crossbow, speed, divergence): an instance method of the
     *  crossbow (with an optional target) since 1.20.5 -- the stack's own crossbow item shoots, as the old
     *  static did for the stack it was given */
    public static void method_7777(net.minecraft.world.level.Level a0, net.minecraft.world.entity.LivingEntity a1, net.minecraft.world.InteractionHand a2, net.minecraft.world.item.ItemStack a3, float a4, float a5) {
        net.minecraft.world.item.CrossbowItem crossbow = a3.getItem() instanceof net.minecraft.world.item.CrossbowItem c
            ? c : (net.minecraft.world.item.CrossbowItem) net.minecraft.world.item.Items.CROSSBOW;
        crossbow.performShooting(a0, a1, a2, a3, a4, a5, null);
    }

    /** was net/minecraft/world/item/MapItem/getSavedData(Ljava/lang/Integer;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/level/saveddata/maps/MapItemSavedData; */
    public static net.minecraft.world.level.saveddata.maps.MapItemSavedData method_7997(java.lang.Integer a0, net.minecraft.world.level.Level a1) {
        return net.minecraft.world.item.MapItem.getSavedData(a0 == null ? null : new net.minecraft.world.level.saveddata.maps.MapId(a0), a1);
    }

    /** was net/minecraft/world/item/enchantment/EnchantmentHelper.onProjectileSpawned(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;Ljava/util/function/Consumer;)V */
    public static void method_60127(net.minecraft.server.level.ServerLevel a0, net.minecraft.world.item.ItemStack a1, net.minecraft.world.entity.projectile.arrow.AbstractArrow a2, java.util.function.Consumer a3) {
        net.minecraft.world.item.enchantment.EnchantmentHelper.onProjectileSpawned(a0, a1, (net.minecraft.world.entity.projectile.Projectile)a2, a3);
    }

    /** was net/minecraft/world/item/enchantment/EnchantmentHelper/getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I */
    public static int method_8225(net.minecraft.world.item.enchantment.Enchantment a0, net.minecraft.world.item.ItemStack a1) {
        // Holder.direct never equals the registry Reference the item component is keyed by (identity), so it always answered 0
        return net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(top.dext.centurybridge.rt.v26_2.Statics.cbEnchHolder(a0), a1);
    }

    /** was net/minecraft/commands/arguments/item/ItemPredicateArgument/getItemPredicate(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)Ljava/util/function/Predicate; */
    public static java.util.function.Predicate method_9804(com.mojang.brigadier.context.CommandContext a0, java.lang.String a1) {
        return net.minecraft.commands.arguments.item.ItemPredicateArgument.getItemPredicate(a0, a1);
    }

    /** was net/minecraft/nbt/NbtUtils/method_10692(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/nbt/CompoundTag; */
    public static net.minecraft.nbt.CompoundTag method_10692(net.minecraft.core.BlockPos a0) {
        net.minecraft.nbt.CompoundTag nbt = new net.minecraft.nbt.CompoundTag();
        nbt.putFloat("X", a0.getX());
        nbt.putFloat("Y", a0.getY());
        nbt.putFloat("Z", a0.getZ());
        return nbt;
    }

    /** was net/minecraft/world/level/block/entity/BlockEntity/loadStatic(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/level/block/entity/BlockEntity; */
    public static net.minecraft.world.level.block.entity.BlockEntity method_11005(net.minecraft.core.BlockPos a0, net.minecraft.world.level.block.state.BlockState a1, net.minecraft.nbt.CompoundTag a2) {
        return net.minecraft.world.level.block.entity.BlockEntity.loadStatic(a0, a1, a2, net.minecraft.core.HolderLookup.Provider.create(java.util.stream.Stream.empty()));
    }

    /** was net/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity.serverTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;)V */
    public static void method_31651(net.minecraft.world.level.Level a0, net.minecraft.core.BlockPos a1, net.minecraft.world.level.block.state.BlockState a2, net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity a3) {
        net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.serverTick((net.minecraft.server.level.ServerLevel) a0, a1, a2, a3);
    }

    /** was net/minecraft/world/level/block/state/properties/EnumProperty.create(Ljava/lang/String;Ljava/lang/Class;Ljava/util/Collection;)Lnet/minecraft/world/level/block/state/properties/EnumProperty; */
    public static net.minecraft.world.level.block.state.properties.EnumProperty method_11847(java.lang.String a0, java.lang.Class a1, java.util.Collection a2) {
        java.util.List list = a2 instanceof java.util.List ? (java.util.List) a2 : new java.util.ArrayList(a2);
        return net.minecraft.world.level.block.state.properties.EnumProperty.create(a0, a1, list);
    }

    /** was net/minecraft/world/level/chunk/storage/SerializableChunkData.read(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/ai/village/poi/PoiManager;Lnet/minecraft/world/level/chunk/storage/RegionStorageInfo;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/level/chunk/ProtoChunk;
     *  ChunkSerializer.deserialize(world, poi, key, pos, nbt): 1.21.2 split it into SerializableChunkData.parse
     *  and read; null when the nbt is not a chunk, as parse reports it */
    public static net.minecraft.world.level.chunk.ProtoChunk method_12395(net.minecraft.server.level.ServerLevel a0, net.minecraft.world.entity.ai.village.poi.PoiManager a1, net.minecraft.world.level.chunk.storage.RegionStorageInfo a2, net.minecraft.world.level.ChunkPos a3, net.minecraft.nbt.CompoundTag a4) {
        net.minecraft.world.level.chunk.storage.SerializableChunkData data =
            net.minecraft.world.level.chunk.storage.SerializableChunkData.parse(a0, a0.palettedContainerFactory(), a4);
        return data == null ? null : data.read(a0, a1, a2, a3);
    }

    /** was net/minecraft/world/level/chunk/storage/SerializableChunkData.write(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkAccess;)Lnet/minecraft/nbt/CompoundTag; */
    public static net.minecraft.nbt.CompoundTag method_12410(net.minecraft.server.level.ServerLevel a0, net.minecraft.world.level.chunk.ChunkAccess a1) {
        return net.minecraft.world.level.chunk.storage.SerializableChunkData.copyOf(a0, a1).write();
    }

    /** was net/minecraft/server/commands/BossBarCommands/register(Lcom/mojang/brigadier/CommandDispatcher;)V */
    public static void method_13053(com.mojang.brigadier.CommandDispatcher a0) {
        net.minecraft.server.commands.BossBarCommands.register(a0, null);
    }

    /** was net/minecraft/server/commands/TeamCommand/register(Lcom/mojang/brigadier/CommandDispatcher;)V */
    public static void method_13736(com.mojang.brigadier.CommandDispatcher a0) {
        net.minecraft.server.commands.TeamCommand.register(a0, null);
    }

    /** was net/minecraft/server/commands/TellRawCommand/register(Lcom/mojang/brigadier/CommandDispatcher;)V */
    public static void method_13776(com.mojang.brigadier.CommandDispatcher a0) {
        net.minecraft.server.commands.TellRawCommand.register(a0, (net.minecraft.commands.CommandBuildContext) null);
    }

    /** was net/minecraft/server/commands/TitleCommand/register(Lcom/mojang/brigadier/CommandDispatcher;)V */
    public static void method_13804(com.mojang.brigadier.CommandDispatcher a0) {
        net.minecraft.server.commands.TitleCommand.register(a0, null);
    }

    /** was net/minecraft/world/level/levelgen/structure/Structure/simpleCodec(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_42699(java.util.function.Function a0) {
        return net.minecraft.world.level.levelgen.structure.Structure.simpleCodec(a0).codec();
    }

    /** was net/minecraft/util/GsonHelper/getAsItem(Lcom/google/gson/JsonObject;Ljava/lang/String;Lnet/minecraft/world/item/Item;)Lnet/minecraft/world/item/Item; */
    public static net.minecraft.world.item.Item method_34916(com.google.gson.JsonObject a0, java.lang.String a1, net.minecraft.world.item.Item a2) {
        net.minecraft.core.Holder var3 = net.minecraft.core.Holder.direct(a2);
        return (net.minecraft.world.item.Item) net.minecraft.util.GsonHelper.getAsItem(a0, a1, var3).value();
    }

    /** was net/minecraft/world/level/lighting/LightEngine.getOcclusionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Lnet/minecraft/world/phys/shapes/VoxelShape; */
    public static net.minecraft.world.phys.shapes.VoxelShape method_51562(net.minecraft.world.level.BlockGetter a0, net.minecraft.core.BlockPos a1, net.minecraft.world.level.block.state.BlockState a2, net.minecraft.core.Direction a3) {
        return net.minecraft.world.level.lighting.LightEngine.getOcclusionShape(a2, a3);
    }

    /** was net/minecraft/world/level/block/LecternBlock/tryPlaceBook(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/item/ItemStack;)Z */
    public static boolean method_17472(net.minecraft.world.entity.Entity a0, net.minecraft.world.level.Level a1, net.minecraft.core.BlockPos a2, net.minecraft.world.level.block.state.BlockState a3, net.minecraft.world.item.ItemStack a4) {
        if (a0 instanceof net.minecraft.world.entity.LivingEntity) {
            return net.minecraft.world.level.block.LecternBlock.tryPlaceBook((net.minecraft.world.entity.LivingEntity) a0, a1, a2, a3, a4);
        }
        return false;
    }

    /** was net/minecraft/world/level/block/JigsawBlock.canAttach(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;)Z */
    public static boolean method_16546(net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo a0, net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo a1) {
        return net.minecraft.world.level.block.JigsawBlock.canAttach(net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.JigsawBlockInfo.of(a0), net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.JigsawBlockInfo.of(a1));
    }

    /** was net/minecraft/world/entity/ai/behavior/BehaviorUtils/lockGazeAndWalkToEachOther(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/LivingEntity;F)V */
    public static void method_19548(net.minecraft.world.entity.LivingEntity a0, net.minecraft.world.entity.LivingEntity a1, float a2) {
        net.minecraft.world.entity.ai.behavior.BehaviorUtils.lockGazeAndWalkToEachOther(a0, a1, a2, 0);
    }

    /** was net/minecraft/gametest/framework/StructureUtils/clearSpaceForStructure(Lnet/minecraft/world/level/levelgen/structure/BoundingBox;ILnet/minecraft/server/level/ServerLevel;)V */
    public static void method_22246(net.minecraft.world.level.levelgen.structure.BoundingBox a0, int a1, net.minecraft.server.level.ServerLevel a2) {
        net.minecraft.gametest.framework.StructureUtils.clearSpaceForStructure(a0, a2);
    }

    /** was net/minecraft/gametest/framework/StructureUtils/findNearestTest(Lnet/minecraft/core/BlockPos;ILnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/core/BlockPos; */
    public static net.minecraft.core.BlockPos method_22255(net.minecraft.core.BlockPos a0, int a1, net.minecraft.server.level.ServerLevel a2) {
        return net.minecraft.gametest.framework.StructureUtils.findNearestTest(a0, a1, a2).orElse(null);
    }

    /** was net/minecraft/gametest/framework/StructureUtils/findTestBlocks(Lnet/minecraft/core/BlockPos;ILnet/minecraft/server/level/ServerLevel;)Ljava/util/Collection; */
    public static java.util.Collection method_22258(net.minecraft.core.BlockPos a0, int a1, net.minecraft.server.level.ServerLevel a2) {
        return net.minecraft.gametest.framework.StructureUtils.findTestBlocks(a0, a1, a2).collect(java.util.stream.Collectors.toList());
    }

    /** was net/minecraft/world/entity/ai/behavior/StopAttackingIfTargetInvalid.create(Ljava/util/function/Predicate;)Lnet/minecraft/world/entity/ai/behavior/BehaviorControl; */
    public static net.minecraft.world.entity.ai.behavior.BehaviorControl method_47138(java.util.function.Predicate a0) {
        return net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid.create((net.minecraft.server.level.ServerLevel world, net.minecraft.world.entity.LivingEntity entity) -> a0.test(entity));
    }

    /** was net/minecraft/world/entity/ai/behavior/StopAttackingIfTargetInvalid.create(Ljava/util/function/Predicate;Ljava/util/function/BiConsumer;Z)Lnet/minecraft/world/entity/ai/behavior/BehaviorControl; */
    public static net.minecraft.world.entity.ai.behavior.BehaviorControl method_47139(java.util.function.Predicate a0, java.util.function.BiConsumer a1, boolean a2) {
        net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid.StopAttackCondition cbPredicate = (net.minecraft.server.level.ServerLevel world, net.minecraft.world.entity.LivingEntity entity) -> a0.test(entity);
        net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid.TargetErasedCallback cbConsumer = (net.minecraft.server.level.ServerLevel world, java.lang.Object obj, net.minecraft.world.entity.LivingEntity entity) -> a1.accept(obj, entity);
        return net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid.create(cbPredicate, cbConsumer, a2);
    }

    /** was net/minecraft/world/level/levelgen/presets/WorldPresets/fromSettings(Lnet/minecraft/core/Registry;)Ljava/util/Optional; */
    public static java.util.Optional method_41594(net.minecraft.core.Registry a0) {
        return net.minecraft.world.level.levelgen.presets.WorldPresets.fromSettings(new net.minecraft.world.level.levelgen.WorldDimensions(a0));
    }

    /** was net/minecraft/world/level/levelgen/presets/WorldPresets.createNormalWorldDimensions(Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/level/levelgen/WorldDimensions; */
    public static net.minecraft.world.level.levelgen.WorldDimensions method_41598(net.minecraft.core.RegistryAccess a0) {
        return net.minecraft.world.level.levelgen.presets.WorldPresets.createNormalWorldDimensions((net.minecraft.core.HolderLookup.Provider)a0);
    }

    /** was net/minecraft/world/level/levelgen/presets/WorldPresets.getNormalOverworld(Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/level/dimension/LevelStem; */
    public static net.minecraft.world.level.dimension.LevelStem method_41599(net.minecraft.core.RegistryAccess a0) {
        return net.minecraft.world.level.levelgen.presets.WorldPresets.getNormalOverworld((net.minecraft.core.HolderLookup.Provider) a0);
    }

    /** was net/minecraft/world/entity/animal/frog/FrogAi/getTemptations()Lnet/minecraft/world/item/crafting/Ingredient; */
    public static net.minecraft.world.item.crafting.Ingredient method_41366() {
        return (net.minecraft.world.item.crafting.Ingredient) net.minecraft.world.entity.animal.frog.FrogAi.getTemptations();
    }

    /** was net/minecraft/world/level/levelgen/WorldDimensions.withOverworld(Lnet/minecraft/core/Registry;Ljava/util/Map;Lnet/minecraft/world/level/chunk/ChunkGenerator;)Ljava/util/Map; */
    public static java.util.Map method_45520(net.minecraft.core.Registry a0, java.util.Map a1, net.minecraft.world.level.chunk.ChunkGenerator a2) {
        return net.minecraft.world.level.levelgen.WorldDimensions.withOverworld((net.minecraft.core.HolderLookup) a0, a1, a2);
    }

    /** was net/minecraft/world/level/block/entity/DecoratedPotPatterns/bootstrap(Lnet/minecraft/core/Registry;)Ljava/lang/String; */
    public static java.lang.String method_49207(net.minecraft.core.Registry a0) {
        return net.minecraft.world.level.block.entity.DecoratedPotPatterns.bootstrap(a0).assetId().toString();
    }

    /** was net/minecraft/util/CommonLinks/extendRealms(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String; */
    public static java.lang.String method_49719(java.lang.String a0, java.lang.String a1) {
        return net.minecraft.util.CommonLinks.extendRealms(a0, java.util.UUID.fromString(a1));
    }

    /** was net/minecraft/util/CommonLinks/extendRealms(Ljava/lang/String;Ljava/lang/String;Z)Ljava/lang/String; */
    public static java.lang.String method_49720(java.lang.String a0, java.lang.String a1, boolean a2) {
        return net.minecraft.util.CommonLinks.extendRealms(a0, java.util.UUID.fromString(a1), a2);
    }

    /** was net/minecraft/data/loot/packs/TradeRebalanceLootTableProvider/create(Lnet/minecraft/data/PackOutput;)Lnet/minecraft/data/loot/LootTableProvider; */
    public static net.minecraft.data.loot.LootTableProvider method_53853(net.minecraft.data.PackOutput a0) {
        return net.minecraft.data.loot.packs.TradeRebalanceLootTableProvider.create(a0, new java.util.concurrent.CompletableFuture<>());
    }

    /** was net/minecraft/world/level/block/CrafterBlock.getPotentialResults(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/CraftingInput;)Ljava/util/Optional; */
    public static java.util.Optional method_54475(net.minecraft.world.level.Level a0, net.minecraft.world.item.crafting.CraftingInput a1) {
        return net.minecraft.world.level.block.CrafterBlock.getPotentialResults((net.minecraft.server.level.ServerLevel) a0, a1);
    }

    /** was net/minecraft/world/level/levelgen/structure/pools/alias/PoolAliasBindings/bootstrap(Lnet/minecraft/core/Registry;)Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_54509(net.minecraft.core.Registry a0) {
        return net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBindings.bootstrap(a0).codec();
    }

    /** was net/minecraft/world/item/MaceItem/canSmashAttack(Lnet/minecraft/world/entity/player/Player;)Z */
    public static boolean method_58659(net.minecraft.world.entity.player.Player a0) {
        return net.minecraft.world.item.MaceItem.canSmashAttack(a0);
    }
}
