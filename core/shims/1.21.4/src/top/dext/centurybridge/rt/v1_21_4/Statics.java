package top.dext.centurybridge.rt.v1_21_4;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import net.minecraft.class_1856;

/**
 * Caller-side homes for dead STATIC methods (Mixin cannot add non-private
 * statics to targets). The converter rewrites old invokestatic call sites to
 * point here; signatures must match the dead originals exactly.
 */
public final class Statics {

    /** 1.20.1 Ingredient.fromJson(JsonElement), removed 1.20.2 (codec era). L3 x29. */
    public static class_1856 method_52177(JsonElement json) {
        return class_1856.field_46095.parse(cbJsonOps(), json)
            .result()
            .orElseThrow(() -> new JsonSyntaxException("Invalid ingredient: " + json));
    }

    /** 1.20.1 FluidDrainable.tryDrainFluid gained a leading @Nullable player param in 1.20.2.
     *  Interface methods cannot be re-added via Mixin; call sites are redirected here. L2 x19. */
    public static net.minecraft.class_1799 method_9700(net.minecraft.class_2263 self,
            net.minecraft.class_1936 world, net.minecraft.class_2338 pos, net.minecraft.class_2680 state) {
        return self.method_9700(null, world, pos, state);
    }

    /** 1.21.2 keyed recipes by registry key; every bridge holding a bare id goes through here. */
    public static net.minecraft.class_5321<net.minecraft.class_1860<?>> cbRecipeKey(net.minecraft.class_2960 id) {
        return net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_52178, id);
    }

    /** 1.20.1 Ingredient.fromJson(json, allowEmpty), removed 1.20.2. L3 x9. */
    public static class_1856 method_8102(JsonElement json, boolean allowEmpty) {
        // 1.21.2 dropped empty-ingredient support; the surviving codec covers both
        var codec = class_1856.field_46095;
        return codec.parse(cbJsonOps(), json)
            .result()
            .orElseThrow(() -> new JsonSyntaxException("Invalid ingredient: " + json));
    }

    /** 1.20.1 Fertilizable.canGrow lost its trailing client-side flag in 1.20.2. Interface -> call-site redirect. L2 x9. */
    public static boolean method_9651(net.minecraft.class_2256 self, net.minecraft.class_4538 world,
            net.minecraft.class_2338 pos, net.minecraft.class_2680 state, boolean isClient) {
        return self.method_9651(world, pos, state);
    }

    /** legacy Recipe.getId(): ids moved to RecipeEntry in 1.20.2; answered from the tracker map. */
    public static net.minecraft.class_2960 method_8114(net.minecraft.class_1860<?> self) {
        net.minecraft.class_2960 id = Trackers.RECIPE_IDS.get(self);
        if (id == null) {
            throw new UnsupportedOperationException(
                "CenturyBridge: recipe id unavailable -- 1.20.2 moved ids to RecipeEntry"
                + " and this recipe was not registered through RecipeManager");
        }
        return id;
    }

    public static net.minecraft.class_2960 method_8114(net.minecraft.class_1869 self) {
        return method_8114((net.minecraft.class_1860<?>) self);
    }

    public static net.minecraft.class_2960 method_8114(net.minecraft.class_3955 self) {
        return method_8114((net.minecraft.class_1860<?>) self);
    }

    /** 1.20.1 LootContextPredicate.EMPTY field, removed 1.20.2; field access redirected here. */
    public static final net.minecraft.class_5258 field_24388 = net.minecraft.class_5258.method_27973();

    /** 1.20.1 Criteria.register(trigger) -- private in both eras (mods reached it via AW).
     *  New shape needs a name; recovered from the legacy trigger's getId via reflection. L3 x12. */
    @SuppressWarnings("unchecked")
    public static net.minecraft.class_179<?> method_767(net.minecraft.class_179<?> trigger) {
        try {
            net.minecraft.class_2960 id = (net.minecraft.class_2960)
                trigger.getClass().getMethod("method_794").invoke(trigger);
            var register = net.minecraft.class_174.class.getDeclaredMethod(
                "method_767", String.class, net.minecraft.class_179.class);
            register.setAccessible(true);
            return (net.minecraft.class_179<?>) register.invoke(null, id.toString(), trigger);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                "CenturyBridge: cannot register legacy criterion trigger " + trigger.getClass().getName(), e);
        }
    }

    
    /** 1.20.1 RecipeManager.deserialize: became protected and returns RecipeEntry. L3 x4. */
    public static net.minecraft.class_1860<?> method_17720(net.minecraft.class_2960 id, com.google.gson.JsonObject json) {
        try {
            var m = net.minecraft.class_1863.class.getDeclaredMethod(
                "method_17720", net.minecraft.class_2960.class, com.google.gson.JsonObject.class);
            m.setAccessible(true);
            net.minecraft.class_8786<?> entry = (net.minecraft.class_8786<?>) m.invoke(null, id, json);
            return entry.comp_1933();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("CenturyBridge: recipe deserialize failed for " + id, e);
        }
    }

    public static net.minecraft.class_2960 method_8114(net.minecraft.class_1867 self) {
        return method_8114((net.minecraft.class_1860<?>) self);
    }

    
    /** ANY-constant family, removed with the predicate rework; rebuilt as match-alls. */
    public static final net.minecraft.class_2073 field_9640 =
        net.minecraft.class_2073.class_2074.method_8973().method_8976();

    public static final net.minecraft.class_2048 field_9599 =
        net.minecraft.class_2048.class_2049.method_8916().method_8920();

    public static final net.minecraft.class_2105 field_9716 =
        new net.minecraft.class_2105((net.minecraft.class_2487) null);

    public static net.minecraft.class_2960 method_8114(net.minecraft.class_1874 self) {
        return method_8114((net.minecraft.class_1860<?>) self);
    }

    public static net.minecraft.class_2960 method_8114(net.minecraft.class_3862 self) {
        return method_8114((net.minecraft.class_1860<?>) self);
    }

    public static net.minecraft.class_2960 method_8114(net.minecraft.class_3861 self) {
        return method_8114((net.minecraft.class_1860<?>) self);
    }

    public static net.minecraft.class_2960 method_8114(net.minecraft.class_3920 self) {
        return method_8114((net.minecraft.class_1860<?>) self);
    }

    public static boolean method_9651(net.minecraft.class_2302 self, net.minecraft.class_4538 world,
            net.minecraft.class_2338 pos, net.minecraft.class_2680 state, boolean isClient) {
        return ((net.minecraft.class_2256) self).method_9651(world, pos, state);
    }

    /** 1.20.1 Waterloggable.tryFillWithFluid gained a leading player param (same pattern as FluidDrainable). */
    public static boolean method_10310(net.minecraft.class_2402 self, net.minecraft.class_1922 world,
            net.minecraft.class_2338 pos, net.minecraft.class_2680 state, net.minecraft.class_3611 fluid) {
        return self.method_10310(null, world, pos, state, fluid);
    }

    public static boolean method_10310(net.minecraft.class_3737 self, net.minecraft.class_1922 world,
            net.minecraft.class_2338 pos, net.minecraft.class_2680 state, net.minecraft.class_3611 fluid) {
        return ((net.minecraft.class_2402) self).method_10310(null, world, pos, state, fluid);
    }

    /** 1.20.1 CraftingResultInventory.setLastRecipe(Recipe) -> new takes RecipeEntry; rebuilt via tracker. */
    public static void method_7662(net.minecraft.class_1731 self, net.minecraft.class_1860<?> recipe) {
        if (recipe == null) {
            ((net.minecraft.class_1732) self).method_7662(null);
            return;
        }
        net.minecraft.class_2960 id = Trackers.RECIPE_IDS.get(recipe);
        ((net.minecraft.class_1732) self).method_7662(
            id == null ? null : new net.minecraft.class_8786<>(cbRecipeKey(id), recipe));
    }

    /** 1.20.3 added a tick-rate parameter to StatusEffectUtil.getDurationText. */
    public static net.minecraft.class_2561 method_5577(net.minecraft.class_1293 instance, float multiplier) {
        return net.minecraft.class_1292.method_5577(instance, multiplier, 1.0F);
    }

    /** 1.20.1 ItemPredicate.fromJson, reimplemented on the 1.20.3+ codec (hit at init by real mods). */
    public static net.minecraft.class_2073 method_8969(JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return net.minecraft.class_2073.class_2074.method_8973().method_8976();
        }
        return net.minecraft.class_2073.field_45754.parse(cbJsonOps(), json)
            .result()
            .orElseThrow(() -> new JsonSyntaxException("Invalid item predicate: " + json));
    }

    /** 1.20.1 ItemPredicate.toJson, reimplemented on the codec's encode side. */
    public static JsonElement method_8971(net.minecraft.class_2073 self) {
        return net.minecraft.class_2073.field_45754.encodeStart(cbJsonOps(), self)
            .result()
            .orElse(com.google.gson.JsonNull.INSTANCE);
    }

    /**
     * 1.20.1 Item.onCraft(stack, world, player): the player parameter was
     * dropped at 1.20.2. Declared on class_1792, so mods calling it through
     * ItemStack/BlockItem subclasses land here too.
     */
    public static void method_7843(net.minecraft.class_1792 self,
            net.minecraft.class_1799 stack, net.minecraft.class_1937 world,
            net.minecraft.class_1657 player) {
        self.method_7843(stack, world);
    }

    /**
     * 1.20.1 CraftingResultInventory.shouldCraftRecipe(world, player, recipe):
     * the recipe became a RecipeEntry. The tracker supplies the id the recipe
     * was registered under, so the old call still resolves.
     */
    public static boolean method_7665(net.minecraft.class_1731 self,
            net.minecraft.class_1937 world, net.minecraft.class_3222 player,
            net.minecraft.class_1860<?> recipe) {
        net.minecraft.class_2960 id = Trackers.RECIPE_IDS.get(recipe);
        if (id == null) {
            return false;
        }
        // 1.21.2 dropped the World parameter
        return ((net.minecraft.class_1732) self)
            .method_7665(player, new net.minecraft.class_8786<>(cbRecipeKey(id), recipe));
    }

    // ==== GENERATED BRIDGES (assemble.py) ====
    /** was net/minecraft/class_1076.field_32971Ljava/lang/String; */
    public static java.lang.String field_32971() {
        return "textures/atlas/";
    }

    /** was net/minecraft/class_11.method_22880()[Lnet/minecraft/class_9; */
    public static net.minecraft.class_9[] method_22880(net.minecraft.class_11 self) {
        int length = self.method_38();
        net.minecraft.class_9[] directions = new net.minecraft.class_9[length];
        for (int i = 0; i < length; i++) {
            directions[i] = self.method_40(i);
        }
        return directions;
    }

    /** was net/minecraft/class_11.method_22881()[Lnet/minecraft/class_9; */
    public static net.minecraft.class_9[] method_22881(net.minecraft.class_11 self) {
        int length = self.method_39();
        net.minecraft.class_9[] nodes = new net.minecraft.class_9[length];
        for (int i = 0; i < length; i++) {
            nodes[i] = self.method_40(i);
        }
        return nodes;
    }

    /** was net/minecraft/class_1208.field_5732Lcom/mojang/datafixers/DSL$TypeReference; */
    public static com.mojang.datafixers.DSL.TypeReference field_5732() {
        throw new UnsupportedOperationException("CenturyBridge: field_5732 was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/class_1214.method_5172(Ljava/lang/String;)Ljava/lang/String; */
    public static java.lang.String method_5172(net.minecraft.class_1214 self, java.lang.String a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_1214.method_5172(String) was deleted in 1.20.2; the new API surface only exposes <init> and makeRule(), with no String-to-String transformation available to delegate to");
    }

    /** was net/minecraft/class_1214.method_5173(Ljava/lang/String;)Ljava/lang/String; */
    public static java.lang.String method_5173(net.minecraft.class_1214 self, java.lang.String a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_1214.method_5173(String) was removed in 1.20.2; the rename callback is no longer exposed and is inlined into makeRule(), so the old per-name transform contract cannot be reproduced on the new type");
    }

    /** was net/minecraft/class_1293.method_5589(Lnet/minecraft/class_1309;)V */
    public static void method_5589(net.minecraft.class_1293 self, net.minecraft.class_1309 a0) {
        self.method_52523(a0);
    }

    /** was net/minecraft/class_1297.method_20233(Lnet/minecraft/class_4050;)Z */
    public static boolean method_20233(net.minecraft.class_1297 self, net.minecraft.class_4050 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_20233(EntityPose) was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/class_1297.method_5621()D */
    public static double method_5621(net.minecraft.class_1297 self) {
        return (double) self.method_17682();
    }

    /** was net/minecraft/class_1308.method_33191(Lnet/minecraft/class_1309;)D */
    public static double method_33191(net.minecraft.class_1308 self, net.minecraft.class_1309 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_33191 (MobEntity.getAttackDistanceScalingFactor) was removed in 1.20.2 with no direct replacement; override-based virtual dispatch cannot be replicated in a static shim");
    }

    /** was net/minecraft/class_1308.method_47922(Lnet/minecraft/class_1309;)D */
    public static double method_47922(net.minecraft.class_1308 self, net.minecraft.class_1309 a0) {
        return (double) (self.method_17681() * 2.0F * self.method_17681() * 2.0F + a0.method_17681());
    }

    /** was net/minecraft/class_1309.field_6242D */
    public static double field_6242() {
        return 1.3D;
    }

    /** was net/minecraft/class_131.method_29322()Ljava/lang/Object; */
    public static java.lang.Object method_29322(net.minecraft.class_131 self) {
        throw new UnsupportedOperationException("CenturyBridge: Gson type handler for loot functions was removed in 1.20.2");
    }

    /** was net/minecraft/class_1324.method_6202(Lnet/minecraft/class_1322;)V */
    public static void method_6202(net.minecraft.class_1324 self, net.minecraft.class_1322 a0) {
        self.method_26835(a0);
    }

    /** was net/minecraft/class_1420.field_28637I */
    public static int field_28637() {
        throw new UnsupportedOperationException("CenturyBridge: Field field_28637 was deleted without replacement");
    }

    /** was net/minecraft/class_1420.field_30268F */
    public static float field_30268() {
        return 0.2f;
    }

    public void method_721(net.minecraft.class_161 a0) {
        ((net.minecraft.class_163.class_164) (Object) this).method_721(new net.minecraft.class_8781((net.minecraft.class_8779) (Object) a0, null));
    }

    /** was net/minecraft/class_163.method_712()Ljava/util/Collection; */
    public static java.util.Collection method_712(net.minecraft.class_163 self) {
        return self.method_53693();
    }

    /** was net/minecraft/class_1657.method_7282(DDD)V */
    public static void method_7282(net.minecraft.class_1657 self, double a0, double a1, double a2) {
        throw new UnsupportedOperationException("CenturyBridge: PlayerEntity.increaseTravelStats (method_7282) was removed in 1.20.2 and has no direct replacement.");
    }

    /** was net/minecraft/class_1690.method_42279(Lnet/minecraft/class_1282;)V */
    public static void method_42279(net.minecraft.class_1690 self, net.minecraft.class_1282 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_42279(DamageSource) was removed in 1.20.3; projectile damage source is now tracked via class_1690$class_1692 (method_47884/method_47885) with no known conversion from DamageSource");
    }

    /** was net/minecraft/class_1690.method_7533()I */
    public static int method_7533(net.minecraft.class_1690 self) {
        throw new UnsupportedOperationException("CenturyBridge: TntEntity.getFuse is no longer publicly accessible");
    }

    /** was net/minecraft/class_1690.method_7540(I)V */
    public static void method_7540(net.minecraft.class_1690 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_7540 is no longer supported");
    }

    /** was net/minecraft/class_1690.method_7543()I */
    public static int method_7543(net.minecraft.class_1690 self) {
        throw new UnsupportedOperationException("CenturyBridge: BoatEntity.getTicksUnderwater is no longer exposed");
    }

    /** was net/minecraft/class_1690.method_7554()F */
    public static float method_7554(net.minecraft.class_1690 self) {
        throw new UnsupportedOperationException("CenturyBridge: BoatEntity.getWaterHeight was removed in 1.20.3");
    }

    public void method_7662(net.minecraft.class_1860 a0) {
        net.minecraft.class_8786 current = ((net.minecraft.class_1732)(Object)this).method_7663();
        if (current == null) {
            throw new UnsupportedOperationException("CenturyBridge: cannot derive RecipeEntry id from null current recipe");
        }
        ((net.minecraft.class_1732)(Object)this).method_7662(new net.minecraft.class_8786(current.comp_1932(), a0));
    }

    public net.minecraft.class_1860 method_7663() {
        return ((net.minecraft.class_1732) (Object) this).method_7663().comp_1933();
    }

    /** was net/minecraft/class_173.method_757(Lnet/minecraft/class_2960;)Lnet/minecraft/class_176; */
    public static net.minecraft.class_176 method_757(net.minecraft.class_173 self, net.minecraft.class_2960 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_173.method_757(class_2960) performed dynamic tag lookup by Identifier; 1.20.2 reworked the tag system and removed this capability with no direct replacement");
    }

    /** was net/minecraft/class_173.method_762(Lnet/minecraft/class_176;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_762(net.minecraft.class_173 self, net.minecraft.class_176 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_762 (Slot -> Identifier) was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/class_1740.field_41941Lnet/minecraft/class_3542$class_7292; */
    public static net.minecraft.class_3542.class_7292 field_41941() {
        throw new UnsupportedOperationException("CenturyBridge: StringIdentifiable.Codec has been removed in 1.20.3");
    }

    /** was net/minecraft/class_174.method_53697(Lnet/minecraft/class_179;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_53697(net.minecraft.class_174 self, net.minecraft.class_179 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_53697 was removed in 1.20.3 and has no replacement");
    }

    /** was net/minecraft/class_174.method_766()Ljava/lang/Iterable; */
    public static java.lang.Iterable method_766(net.minecraft.class_174 self) {
        return java.util.Collections.emptyList();
    }

    /** was net/minecraft/class_178.method_783()Lcom/google/gson/JsonElement; */
    public static com.google.gson.JsonElement method_783(net.minecraft.class_178 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_178.method_783() (JsonElement serialization) was deleted in 1.20.2; the new API surface only exposes method_786() returning Instant and method_787() for PacketByteBuf serialization, with no equivalent JsonElement contract");
    }

    /** was net/minecraft/class_178.method_788(Ljava/lang/String;)Lnet/minecraft/class_178; */
    public static net.minecraft.class_178 method_788(net.minecraft.class_178 self, java.lang.String a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_178.method_788(String) has no 1.20.2 equivalent; the String parameter is no longer part of class_178's contract (only <init>() or <init>(Instant) exist)");
    }

    /** was net/minecraft/class_179.method_794()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_794(net.minecraft.class_179 self) {
        throw new UnsupportedOperationException("CenturyBridge: Advancement.method_794() id accessor removed in 1.20.2; the id now lives on AdvancementEntry (class_184), not on Advancement (class_179)");
    }

    /** was net/minecraft/class_1830.field_30925Ljava/lang/String; */
    public static java.lang.String field_30925() {
        return "Items";
    }

    /** was net/minecraft/class_184.method_806()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_806(net.minecraft.class_184 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_184 no longer stores an Identifier; the advancement ID was moved to AdvancementEntry (class_8781) in 1.20.2, so method_806 has no equivalent on class_184 itself");
    }

    /** was net/minecraft/class_184.method_807()Lcom/google/gson/JsonObject; */
    public static com.google.gson.JsonObject method_807(net.minecraft.class_184 self) {
        throw new UnsupportedOperationException("CenturyBridge: Advancement JSON serialization is not supported on this version");
    }

    /** was net/minecraft/class_189.method_832()I */
    public static int method_832(net.minecraft.class_189 self) {
        return self.ordinal();
    }

    /** was net/minecraft/class_1921.field_32773I */
    public static int field_32773() {
        return 131072;
    }

    /** was net/minecraft/class_2010.field_9509Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_9509() {
        return net.minecraft.class_2960.method_60654("enchanted_item");
    }

    /** was net/minecraft/class_2054.field_9612Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_9612() {
        return net.minecraft.class_2960.method_60654("enchantment_used_on_item");
    }

    /** was net/minecraft/class_2062.field_9624Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_9624() {
        return net.minecraft.class_2960.method_60654("recipe_unlocked");
    }

    /** was net/minecraft/class_2066.field_9625Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_9625() {
        return net.minecraft.class_2960.method_60655("minecraft", "brewed_potion");
    }

    /** was net/minecraft/class_2152.method_9175()Ljava/lang/Float; */
    public static java.lang.Float method_9175(net.minecraft.class_2152 self) {
        return self.comp_1841();
    }

    /** was net/minecraft/class_2152.method_9177()Ljava/lang/Float; */
    public static java.lang.Float method_9177(net.minecraft.class_2152 self) {
        return self.comp_1840();
    }

    /** was net/minecraft/class_2246.field_10150Lnet/minecraft/class_2248; */
    public static net.minecraft.class_2248 field_10150() {
        throw new UnsupportedOperationException("CenturyBridge: Blocks.field_10150 was deleted in 1.20.3 with no direct replacement");
    }

    /** was net/minecraft/class_2246.field_10331Lnet/minecraft/class_2248; */
    public static net.minecraft.class_2248 field_10331() {
        throw new UnsupportedOperationException("CenturyBridge: class_2246.field_10331 was removed in 1.20.3 with no direct replacement");
    }

    /** was net/minecraft/class_2378.method_10231(Lnet/minecraft/class_2378;ILjava/lang/String;Ljava/lang/Object;)Ljava/lang/Object; */
    public static java.lang.Object method_10231(net.minecraft.class_2378 self, net.minecraft.class_2378 a0, int a1, java.lang.String a2, java.lang.Object a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_10231 (Registry.register with raw id) has no 1.20.2 equivalent; raw-id registration is no longer supported");
    }

    /** was net/minecraft/class_2540.method_10815(I)I */
    public static int method_10815(net.minecraft.class_2540 self, int a0) {
        return self.method_10816();
    }

    /** was net/minecraft/class_2540.method_36129(J)I */
    public static int method_36129(net.minecraft.class_2540 self, long a0) {
        int before = self.writerIndex();
        self.method_10791(a0);
        return self.writerIndex() - before;
    }

    /** was net/minecraft/class_2602.method_11083(Lnet/minecraft/class_2661;)V */
    public static void method_11083(net.minecraft.class_2602 self, net.minecraft.class_2661 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_2602.method_11083(class_2661) was removed in 1.20.2 with no direct replacement accepting class_2661");
    }

    public void method_11126(net.minecraft.class_2790 a0) {
        ((net.minecraft.class_8705) (Object) this).method_11126(a0);
    }

    /** was net/minecraft/class_2602.method_11152(Lnet/minecraft/class_2658;)V */
    public static void method_11152(net.minecraft.class_2602 self, net.minecraft.class_2658 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_2602.method_11152(class_2658) was removed in 1.20.2 with no direct replacement; no remaining method on class_2602 accepts class_2658");
    }

    /** was net/minecraft/class_2602.method_36895(Lnet/minecraft/class_6373;)V */
    public static void method_36895(net.minecraft.class_2602 self, net.minecraft.class_6373 a0) {
        a0.method_36949(self);
    }

    /** was net/minecraft/class_2602.method_45728(Lnet/minecraft/class_7832;)V */
    public static void method_45728(net.minecraft.class_2602 self, net.minecraft.class_7832 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_2602.method_45728(class_7832) was deleted in 1.20.2 with no direct replacement; class_7832 has no single successor on the new visitor surface");
    }

    /** was net/minecraft/class_2621.field_31352Ljava/lang/String; */
    public static java.lang.String field_31352() {
        return "Fuel";
    }

    /** was net/minecraft/class_2621.field_31353Ljava/lang/String; */
    public static java.lang.String field_31353() {
        throw new UnsupportedOperationException("CenturyBridge: field_31353 (String constant on class_2621/LecternBlockEntity) was deleted in 1.20.3 with no replacement and its literal value cannot be reconstructed from the new API surface");
    }

    /** was net/minecraft/class_2625.method_49844(Lnet/minecraft/class_1657;)Lnet/minecraft/class_8242; */
    public static net.minecraft.class_8242 method_49844(net.minecraft.class_2625 self, net.minecraft.class_1657 a0) {
        return self.method_49843(self.method_49834(a0));
    }

    /** was net/minecraft/class_2633.method_11368(Lnet/minecraft/class_3218;Z)Z */
    public static boolean method_11368(net.minecraft.class_2633 self, net.minecraft.class_3218 a0, boolean a1) {
        return self.method_54874(a0);
    }

    /** was net/minecraft/class_2633.method_21864(Lnet/minecraft/class_3218;ZLnet/minecraft/class_3499;)Z */
    public static boolean method_21864(net.minecraft.class_2633 self, net.minecraft.class_3218 a0, boolean a1, net.minecraft.class_3499 a2) {
        throw new UnsupportedOperationException("CenturyBridge: StructureBlockBlockEntity no longer supports placing a custom StructureTemplate");
    }

    /** was net/minecraft/class_2633.method_21865()Ljava/lang/String; */
    public static java.lang.String method_21865(net.minecraft.class_2633 self) {
        return self.field_31366;
    }

    public void method_12069(net.minecraft.class_2803 a0) {
        ((net.minecraft.class_8706) (Object) this).method_12069(a0);
    }

    /** was net/minecraft/class_2792.method_12075(Lnet/minecraft/class_2817;)V */
    public static void method_12075(net.minecraft.class_2792 self, net.minecraft.class_2817 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_12075 (onDifficulty) was removed in 1.20.2; difficulty is now embedded in GameJoinS2CPacket/PlayerRespawnS2CPacket rather than sent as a standalone packet, so there is no equivalent handler to delegate to");
    }

    /** was net/minecraft/class_2792.method_12081(Lnet/minecraft/class_2856;)V */
    public static void method_12081(net.minecraft.class_2792 self, net.minecraft.class_2856 a0) {
        throw new UnsupportedOperationException("CenturyBridge: PlayPongC2SPacket (class_2856) has been deleted");
    }

    /** was net/minecraft/class_2792.method_12082(Lnet/minecraft/class_2827;)V */
    public static void method_12082(net.minecraft.class_2792 self, net.minecraft.class_2827 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_12082 is no longer supported on class_2792");
    }

    /** was net/minecraft/class_2792.method_36580(Lnet/minecraft/class_6374;)V */
    public static void method_36580(net.minecraft.class_2792 self, net.minecraft.class_6374 a0) {
        throw new UnsupportedOperationException("CenturyBridge: JigsawGeneratingC2SPacket (class_6374) handler was removed in 1.20.2");
    }

    public void method_12666(net.minecraft.class_2923 a0) {
        ((net.minecraft.class_8763) (Object) this).method_12666(a0);
    }

    public void method_12697(net.minecraft.class_2935 a0) {
        throw new UnsupportedOperationException("CenturyBridge: tasks are no longer managed by BuiltChunk");
    }

    public void method_12816(int a0, int a1, int a2, net.minecraft.class_1860 a3, java.util.Iterator a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: class_8786 (RecipeEntry) requires a class_2960 (Identifier) that is not available from the old class_1860-only method_12816 signature; Recipe.getId() was removed in 1.20.2");
    }

    public int method_3788() {
        return ((net.minecraft.class_8599) (Object) this).method_3788();
    }

    public int method_3802() {
        return ((net.minecraft.class_8599) (Object) this).method_3802();
    }

    public java.lang.String method_3827() {
        throw new UnsupportedOperationException("CenturyBridge: method_3827 moved to net.minecraft.class_8599");
    }

    /** was net/minecraft/class_2995.method_32704(Lnet/minecraft/class_2487;)Lnet/minecraft/class_273; */
    public static net.minecraft.class_273 method_32704(net.minecraft.class_2995 self, net.minecraft.class_2487 a0) {
        throw new UnsupportedOperationException("CenturyBridge: Scoreboard.method_32704(Tag) was removed in 1.20.2; objective NBT deserialization was reworked with no direct equivalent");
    }

    /** was net/minecraft/class_2995.method_32705()Lnet/minecraft/class_273; */
    public static net.minecraft.class_273 method_32705(net.minecraft.class_2995 self) {
        try {
            for (java.lang.reflect.Field field : self.getClass().getDeclaredFields()) {
                if (field.getType() == net.minecraft.class_273.class) {
                    field.setAccessible(true);
                    return (net.minecraft.class_273) field.get(self);
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException("CenturyBridge: Failed to access banned IP list field via reflection", t);
        }
        throw new UnsupportedOperationException("CenturyBridge: BannedIpList field not found in class_2995");
    }

    /** was net/minecraft/class_315$4.field_1967[I */
    public static int[] field_1967() {
        throw new UnsupportedOperationException("CenturyBridge: class_316 does not exist in this version");
    }

    /** was net/minecraft/class_315.field_1866Z */
    public static boolean field_1866() {
        return false;
    }

    /** was net/minecraft/class_315.field_1880Z */
    public static boolean field_1880() {
        return true;
    }

    /** was net/minecraft/class_315.field_1893Z */
    public static boolean field_1893() {
        throw new UnsupportedOperationException("CenturyBridge: field_1893 is no longer available");
    }

    /** was net/minecraft/class_32$class_5143.method_27013(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/class_7712;Lnet/minecraft/class_2378;Lcom/mojang/serialization/Lifecycle;)Lcom/mojang/datafixers/util/Pair; */
    public static com.mojang.datafixers.util.Pair method_27013(net.minecraft.class_32.class_5143 self, com.mojang.serialization.DynamicOps a0, net.minecraft.class_7712 a1, net.minecraft.class_2378 a2, com.mojang.serialization.Lifecycle a3) {
        throw new UnsupportedOperationException("CenturyBridge: LevelStorage.Session.method_27013 was removed in 1.20.3 and cannot be emulated");
    }

    /** was net/minecraft/class_32$class_5143.method_29585()Lnet/minecraft/class_7712; */
    public static net.minecraft.class_7712 method_29585(net.minecraft.class_32.class_5143 self) {
        throw new UnsupportedOperationException("CenturyBridge: LevelStorage.Session.method_29585() returning class_7712 was removed in 1.20.3 with no direct replacement on class_32$class_5143");
    }

    /** was net/minecraft/class_3210.method_14081(JJLnet/minecraft/class_3222;)V */
    public static void method_14081(net.minecraft.class_3210 self, long a0, long a1, net.minecraft.class_3222 a2) {
        self.method_14087(a2);
    }

    /** was net/minecraft/class_3248.field_14160Lcom/mojang/authlib/GameProfile; */
    public static com.mojang.authlib.GameProfile field_14160() {
        throw new UnsupportedOperationException("CenturyBridge: field_14160 was a per-connection instance GameProfile field; the 1.20.2 API only exposes method_52417 (setter) and method_52418 (String->GameProfile factory), neither of which can reconstruct the value from a static no-arg context");
    }

    /** was net/minecraft/class_3248.method_14375(Lcom/mojang/authlib/GameProfile;)Lcom/mojang/authlib/GameProfile; */
    public static com.mojang.authlib.GameProfile method_14375(net.minecraft.class_3248 self, com.mojang.authlib.GameProfile a0) {
        throw new UnsupportedOperationException("CenturyBridge: ServerLoginNetworkHandler.method_14375 was removed and has no public equivalent");
    }

    /** was net/minecraft/class_3248.method_14384()V */
    public static void method_14384(net.minecraft.class_3248 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_14384 (acceptPlayer) cannot be implemented because the authenticated GameProfile is not accessible");
    }

    /** was net/minecraft/class_3248.method_40085(Ljava/lang/String;)Z */
    public static boolean method_40085(net.minecraft.class_3248 self, java.lang.String a0) {
        return a0.chars().filter(c -> c <= 32 || c >= 127).findFirst().isEmpty();
    }

    /** was net/minecraft/class_32.method_17926(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/class_7712;Lnet/minecraft/class_2378;Lcom/mojang/serialization/Lifecycle;)Ljava/util/function/BiFunction; */
    public static java.util.function.BiFunction method_17926(net.minecraft.class_32 self, com.mojang.serialization.DynamicOps a0, net.minecraft.class_7712 a1, net.minecraft.class_2378 a2, com.mojang.serialization.Lifecycle a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_17926 was removed in 1.20.3 and cannot be faithfully emulated.");
    }

    /** was net/minecraft/class_32.method_26998(Lnet/minecraft/class_32$class_7411;Ljava/util/function/BiFunction;)Ljava/lang/Object; */
    public static java.lang.Object method_26998(net.minecraft.class_32 self, net.minecraft.class_32.class_7411 a0, java.util.function.BiFunction a1) {
        throw new UnsupportedOperationException("CenturyBridge: LevelStorage.readLevelProperties is removed in 1.20.3");
    }

    /** was net/minecraft/class_32.method_29014(Lnet/minecraft/class_32$class_7411;Z)Ljava/util/function/BiFunction; */
    public static java.util.function.BiFunction method_29014(net.minecraft.class_32 self, net.minecraft.class_32.class_7411 a0, boolean a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_29014 not supported due to API changes in DataConfiguration parsing");
    }

    /** was net/minecraft/class_339.field_22757Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22757() {
        return net.minecraft.class_2960.method_60654("textures/gui/widgets.png");
    }

    /** was net/minecraft/class_339.field_42117Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_42117() {
        return net.minecraft.class_2960.method_60654("textures/gui/widgets.png");
    }

    /** was net/minecraft/class_3471.method_35471([Ljava/lang/String;)V */
    public static void method_35471(net.minecraft.class_3471 self, java.lang.String[] a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_3471.method_35471(String[]) was removed in 1.20.2 with no direct replacement; the new API only exposes structure placement via method_15029");
    }

    /** was net/minecraft/class_34.method_256()Z */
    public static boolean method_256(net.minecraft.class_34 self) {
        return false;
    }

    /** was net/minecraft/class_34.method_260()Z */
    public static boolean method_260(net.minecraft.class_34 self) {
        net.minecraft.class_1940 levelInfo = self.method_35505();
        return levelInfo != null && levelInfo.method_8582();
    }

    /** was net/minecraft/class_3521.field_15664Lcom/google/common/util/concurrent/ListeningExecutorService; */
    public static com.google.common.util.concurrent.ListeningExecutorService field_15664() {
        throw new UnsupportedOperationException("CenturyBridge: field_15664 (ListeningExecutorService) has been removed");
    }

    /** was net/minecraft/class_3521.method_15301(Ljava/io/File;Ljava/net/URL;Ljava/util/Map;ILnet/minecraft/class_3536;Ljava/net/Proxy;)Ljava/util/concurrent/CompletableFuture; */
    public static java.util.concurrent.CompletableFuture method_15301(net.minecraft.class_3521 self, java.io.File a0, java.net.URL a1, java.util.Map a2, int a3, net.minecraft.class_3536 a4, java.net.Proxy a5) {
        throw new UnsupportedOperationException("CenturyBridge: method_15301 was removed and cannot be implemented without Guava classes and the new progress listener interface.");
    }

    /** was net/minecraft/class_3542$class_7292.method_42631(Ljava/lang/Enum;Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult; */
    public static com.mojang.serialization.DataResult method_42631(net.minecraft.class_3542.class_7292 self, java.lang.Enum a0, com.mojang.serialization.DynamicOps a1, java.lang.Object a2) {
        return self.encode(a0, a1, a2);
    }

    /** was net/minecraft/class_355.field_32206I */
    public static int field_32206() {
        return 20;
    }

    /** was net/minecraft/class_355.field_32207I */
    public static int field_32207() {
        return 80;
    }

    /** was net/minecraft/class_355.field_32208I */
    public static int field_32208() {
        return 80;
    }

    /** was net/minecraft/class_355.field_32209I */
    public static int field_32209() {
        return 256;
    }

    /** was net/minecraft/class_355.field_32210I */
    public static int field_32210() {
        return 80;
    }

    /** was net/minecraft/class_355.field_32211I */
    public static int field_32211() {
        return 80;
    }

    /** was net/minecraft/class_355.field_32212I */
    public static int field_32212() {
        return 80;
    }

    /** was net/minecraft/class_355.field_32213I */
    public static int field_32213() {
        return 80;
    }

    /** was net/minecraft/class_357.field_43051I */
    public static int field_43051() {
        return 8;
    }

    /** was net/minecraft/class_357.field_43052I */
    public static int field_43052() {
        return 46;
    }

    /** was net/minecraft/class_357.field_43102I */
    public static int field_43102() {
        return 14737632;
    }

    /** was net/minecraft/class_357.field_43103I */
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

    /** was net/minecraft/class_361.field_2193Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_2193() {
        return net.minecraft.class_2960.method_60654("textures/gui/recipe_book.png");
    }

    /** was net/minecraft/class_365.field_2199Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_2199() {
        return net.minecraft.class_2960.method_60655("minecraft", "textures/gui/options_background.png");
    }

    /** was net/minecraft/class_368.field_2207Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_2207() {
        return net.minecraft.class_2960.method_60654("textures/gui/toasts.png");
    }

    /** was net/minecraft/class_3778$class_4181.field_18696Lnet/minecraft/class_3790; */
    public static net.minecraft.class_3790 field_18696() {
        throw new UnsupportedOperationException("CenturyBridge: Static field field_18696 was deleted without replacement");
    }

    /** was net/minecraft/class_3778$class_4181.field_18697Lorg/apache/commons/lang3/mutable/MutableObject; */
    public static org.apache.commons.lang3.mutable.MutableObject field_18697() {
        throw new UnsupportedOperationException("CenturyBridge: Instance field field_18697 accessed statically without receiver");
    }

    /** was net/minecraft/class_3778$class_4181.field_18699I */
    public static int field_18699() {
        throw new UnsupportedOperationException("CenturyBridge: field_18699 was deleted with no replacement");
    }

    /** was net/minecraft/class_3778$class_4182.field_18706Ljava/util/Deque; */
    public static java.util.Deque field_18706() {
        throw new UnsupportedOperationException("CenturyBridge: field_18706 was changed to class_8917 and cannot be represented as Deque");
    }

    /** was net/minecraft/class_3898.field_18243I */
    public static int field_18243() {
        return 34;
    }

    /** was net/minecraft/class_3898.method_17241(Lnet/minecraft/class_3222;Lnet/minecraft/class_1923;Lorg/apache/commons/lang3/mutable/MutableObject;ZZ)V */
    public static void method_17241(net.minecraft.class_3898 self, net.minecraft.class_3222 a0, net.minecraft.class_1923 a1, org.apache.commons.lang3.mutable.MutableObject a2, boolean a3, boolean a4) {
        throw new UnsupportedOperationException("CenturyBridge: method_17241 (sendChunk) was removed in 1.20.2 due to chunk sending flow control overhaul");
    }

    /** was net/minecraft/class_3898.method_39975(IIIII)Z */
    public static boolean method_39975(net.minecraft.class_3898 self, int a0, int a1, int a2, int a3, int a4) {
        throw new UnsupportedOperationException("CenturyBridge: class_3898.method_39975(IIIII)Z was removed in 1.20.2; the chunk tick/spawning system was reworked and the five-int-parameter tick method has no direct equivalent");
    }

    /** was net/minecraft/class_413$class_4192.field_44667Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_44667() {
        return net.minecraft.class_2960.method_60654("textures/gui/language_button.png");
    }

    /** was net/minecraft/class_4264.field_43046I */
    public static int field_43046() {
        return 16777215;
    }

    /** was net/minecraft/class_4264.field_43047I */
    public static int field_43047() {
        return 4;
    }

    /** was net/minecraft/class_4264.field_43048I */
    public static int field_43048() {
        return 2;
    }

    /** was net/minecraft/class_4264.field_43100I */
    public static int field_43100() {
        return 6;
    }

    /** was net/minecraft/class_4264.field_43101I */
    public static int field_43101() {
        return 16777215;
    }

    /** was net/minecraft/class_4284.field_19219Lnet/minecraft/class_4284; */
    public static net.minecraft.class_4284 field_19219() {
        throw new UnsupportedOperationException("CenturyBridge: field_19219 (POI) has been deleted and has no equivalent in 1.20.2");
    }

    /** was net/minecraft/class_4381.field_22686Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22686() {
        throw new UnsupportedOperationException("CenturyBridge: field_22686 (static Identifier on class_4381) was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/class_4381.field_22687Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22687() {
        return net.minecraft.class_2960.method_60655("realms", "textures/gui/realms/slot_frame.png");
    }

    /** was net/minecraft/class_4406.field_22704Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22704() {
        return net.minecraft.class_2960.method_60654("textures/gui/recipe_book.png");
    }

    /** was net/minecraft/class_4406.field_22705Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22705() {
        throw new UnsupportedOperationException("CenturyBridge: class_4406.field_22705 (Identifier) was deleted in 1.20.2 with no direct replacement and its constant value cannot be recovered from the new API surface");
    }

    /** was net/minecraft/class_4406.field_22706Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22706() {
        return net.minecraft.class_2960.method_60654("textures/gui/container/creative_inventory/creative_inventory.png");
    }

    /** was net/minecraft/class_4419.field_20072I */
    public static int field_20072() {
        return 32;
    }

    /** was net/minecraft/class_4419.field_20077Lnet/minecraft/class_2561; */
    public static net.minecraft.class_2561 field_20077() {
        throw new UnsupportedOperationException("CenturyBridge: field_20077 was a deleted static Text constant with no direct replacement in 1.20.2");
    }

    /** was net/minecraft/class_4419.field_20080I */
    public static int field_20080() {
        return 30;
    }

    /** was net/minecraft/class_4419.field_20083Z */
    public static boolean field_20083() {
        return true;
    }

    /** was net/minecraft/class_4419.field_22719Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22719() {
        return net.minecraft.class_2960.method_60654("textures/misc/unknown_server.png");
    }

    /** was net/minecraft/class_4419.field_22720Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22720() {
        return net.minecraft.class_2960.method_60654("textures/gui/container/command_block.png");
    }

    /** was net/minecraft/class_4419.field_26512Lnet/minecraft/class_2561; */
    public static net.minecraft.class_2561 field_26512() {
        return net.minecraft.class_2561.method_30163("mco.trial.message");
    }

    /** was net/minecraft/class_4419.field_26513Lnet/minecraft/class_2561; */
    public static net.minecraft.class_2561 field_26513() {
        return net.minecraft.class_2561.method_30163("mco.template.button.publisher");
    }

    /** was net/minecraft/class_4520.field_33148I */
    public static int field_33148() {
        return 5;
    }

    /** was net/minecraft/class_4525.method_22250(Ljava/lang/String;Lnet/minecraft/class_2338;Lnet/minecraft/class_2470;ILnet/minecraft/class_3218;Z)Lnet/minecraft/class_2633; */
    public static net.minecraft.class_2633 method_22250(net.minecraft.class_4525 self, java.lang.String a0, net.minecraft.class_2338 a1, net.minecraft.class_2470 a2, int a3, net.minecraft.class_3218 a4, boolean a5) {
        net.minecraft.class_4525.method_22251(a0, a1, new net.minecraft.class_2382(0, 1, 0), a2, a4);
        net.minecraft.class_2586 blockEntity = a4.method_8321(a1);
        return blockEntity instanceof net.minecraft.class_2633 ? (net.minecraft.class_2633) blockEntity : null;
    }

    /** was net/minecraft/class_4525.method_36106([Ljava/lang/String;)V */
    public static void method_36106(net.minecraft.class_4525 self, java.lang.String[] a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_4525.method_36106(String[]) was removed in 1.20.2 with no direct replacement; the structure manager no longer accepts a pack/namespace array");
    }

    /** was net/minecraft/class_4558$class_8788.method_27790()Ljava/util/Optional; */
    public static java.util.Optional method_27790(net.minecraft.class_4558.class_8788 self) {
        return self.comp_2029();
    }

    /** was net/minecraft/class_457.field_2716Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_2716() {
        return net.minecraft.class_2960.method_60654("textures/gui/options_background.png");
    }

    /** was net/minecraft/class_4871.field_22583Ljava/lang/String; */
    public static java.lang.String field_22583() {
        return "uuid";
    }

    /** was net/minecraft/class_4877.field_22605Ljava/lang/String; */
    public static java.lang.String field_22605() {
        throw new UnsupportedOperationException("CenturyBridge: field_22605 is an instance field but the helper signature has no receiver parameter");
    }

    /** was net/minecraft/class_4895.field_42047I */
    public static int field_42047() {
        return 8;
    }

    /** was net/minecraft/class_4895.field_42048Lorg/joml/Quaternionf; */
    public static org.joml.Quaternionf field_42048() {
        throw new UnsupportedOperationException("CenturyBridge: field_42048 was a static Quaternionf constant in class_4895 (BeaconScreen) deleted in 1.20.2 with no replacement; the exact rotation value cannot be reconstructed from the new API surface");
    }

    /** was net/minecraft/class_4895.field_42049I */
    public static int field_42049() {
        throw new UnsupportedOperationException("CenturyBridge: field_42049 is not available");
    }

    /** was net/minecraft/class_4895.field_42050I */
    public static int field_42050() {
        return 99;
    }

    /** was net/minecraft/class_4895.field_42051I */
    public static int field_42051() {
        return 15;
    }

    /** was net/minecraft/class_4895.field_42068I */
    public static int field_42068() {
        return 4;
    }

    /** was net/minecraft/class_4905.field_33036I */
    public static int field_33036() {
        return 19088743;
    }

    /** was net/minecraft/class_4905.field_33037I */
    public static int field_33037() {
        return 240;
    }

    /** was net/minecraft/class_4905.field_33038I */
    public static int field_33038() {
        return 0xD0000000;
    }

    /** was net/minecraft/class_4905.field_33039I */
    public static int field_33039() {
        return 80;
    }

    /** was net/minecraft/class_4905.field_33043I */
    public static int field_33043() {
        return 32;
    }

    /** was net/minecraft/class_4905.field_33044Ljava/lang/String; */
    public static java.lang.String field_33044() {
        return "minceraft";
    }

    /** was net/minecraft/class_4905.field_33056I */
    public static int field_33056() {
        return -10526880;
    }

    /** was net/minecraft/class_4905.field_33059I */
    public static int field_33059() {
        throw new UnsupportedOperationException("CenturyBridge: field_33059 was removed in 1.20.2 with no direct replacement and its constant value cannot be determined from the surviving API surface");
    }

    /** was net/minecraft/class_4905.field_33060I */
    public static int field_33060() {
        return 16;
    }

    /** was net/minecraft/class_4905.field_33064I */
    public static int field_33064() {
        return 26;
    }

    /** was net/minecraft/class_4905.field_33065I */
    public static int field_33065() {
        return -7079482;
    }

    /** was net/minecraft/class_521.field_19125Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_19125() {
        return net.minecraft.class_2960.method_60654("textures/gui/recipe_book.png");
    }

    /** was net/minecraft/class_5289.field_24566Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_24566() {
        return net.minecraft.class_2960.method_60654("textures/gui/social_interactions/black_icon.png");
    }

    /** was net/minecraft/class_5522.field_26875Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_26875() {
        throw new UnsupportedOperationException("CenturyBridge: class_5522.field_26875 (Identifier) was deleted in 1.20.2 with no direct replacement; the constant value cannot be reconstructed from the available API surface");
    }

    /** was net/minecraft/class_5547.method_33621(Lnet/minecraft/class_2680;Lnet/minecraft/class_3218;Lnet/minecraft/class_2338;Lnet/minecraft/class_5819;)V */
    public static void method_33621(net.minecraft.class_5547 self, net.minecraft.class_2680 a0, net.minecraft.class_3218 a1, net.minecraft.class_2338 a2, net.minecraft.class_5819 a3) {
        self.method_54764(a0, a1, a2, a3);
    }

    /** was net/minecraft/class_5547.method_33623(Lnet/minecraft/class_2680;Lnet/minecraft/class_3218;Lnet/minecraft/class_2338;Lnet/minecraft/class_5819;)V */
    public static void method_33623(net.minecraft.class_5547 self, net.minecraft.class_2680 a0, net.minecraft.class_3218 a1, net.minecraft.class_2338 a2, net.minecraft.class_5819 a3) {
        self.method_54764(a0, a1, a2, a3);
    }

    /** was net/minecraft/class_5619.field_32922Ljava/lang/String; */
    public static java.lang.String field_32922() {
        return "en_us";
    }

    /** was net/minecraft/class_5652.method_32442()Ljava/lang/Object; */
    public static java.lang.Object method_32442(net.minecraft.class_5652 self) {
        return self.field_27918;
    }

    /** was net/minecraft/class_5659.method_32455()Ljava/lang/Object; */
    public static java.lang.Object method_32455(net.minecraft.class_5659 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_32455 was removed in 1.20.2 with no direct replacement; class_5659 no longer exposes a value accessor and the restructured fields (class_5657) cannot reproduce the old Object return contract without their API");
    }

    /** was net/minecraft/class_5670.method_32476(Lnet/minecraft/class_47;)Ljava/lang/String; */
    public static java.lang.String method_32476(net.minecraft.class_5670 self, net.minecraft.class_47 a0) {
        throw new UnsupportedOperationException("CenturyBridge: LootContextType validation now uses ErrorReporter and cannot be converted to String");
    }

    /** was net/minecraft/class_5671.method_32478()Ljava/lang/Object; */
    public static java.lang.Object method_32478(net.minecraft.class_5671 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_5671.method_32478() was removed in 1.20.2 with no direct replacement; the default-value accessor was refactored out of LootDataType");
    }

    /** was net/minecraft/class_5682$class_5771.field_28368I */
    public static int field_28368() {
        throw new UnsupportedOperationException("CenturyBridge: field_28368 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/class_5682$class_5771.field_28369I */
    public static int field_28369() {
        throw new UnsupportedOperationException("CenturyBridge: field_28369 was an instance field representing V offset but the shim has no receiver parameter");
    }

    /** was net/minecraft/class_5682.field_28359Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_28359() {
        return net.minecraft.class_2960.method_60654("textures/gui/container/bundle.png");
    }

    /** was net/minecraft/class_5754.field_30116D */
    public static double field_30116() {
        return 8.0;
    }

    public void method_2865(net.minecraft.class_161 a0, net.minecraft.class_167 a1) {
        throw new UnsupportedOperationException("CenturyBridge: Cannot convert net.minecraft.class_161 to net.minecraft.class_8781");
    }

    /** was net/minecraft/class_7298.method_45342()Z */
    public static boolean method_45342(net.minecraft.class_7298 self) {
        return self.method_44359();
    }

    /** was net/minecraft/class_7591$class_7592.field_39764I */
    public static int field_39764() {
        throw new UnsupportedOperationException("CenturyBridge: field_39764 is no longer available");
    }

    /** was net/minecraft/class_7591$class_7592.field_39765I */
    public static int field_39765() {
        return 0;
    }

    /** was net/minecraft/class_7591.field_39761Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_39761() {
        return net.minecraft.class_2960.method_60654("textures/gui/chat_status.png");
    }

    /** was net/minecraft/class_75.method_29316()Ljava/lang/Object; */
    public static java.lang.Object method_29316(net.minecraft.class_75 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_75 is no longer an enum and has no instance state");
    }

    /** was net/minecraft/class_7610$class_7611.field_40848Lnet/minecraft/class_7610$class_7611; */
    public static net.minecraft.class_7610.class_7611 field_40848() {
        return net.minecraft.class_7610.class_7611.unsigned(new java.util.UUID(0L, 0L), () -> false);
    }

    /** was net/minecraft/class_7689.method_45348()Z */
    public static boolean method_45348(net.minecraft.class_7689 self) {
        return self.method_48573() && !self.method_45350();
    }

    /** was net/minecraft/class_7849$1.field_41817[I */
    public static int[] field_41817() {
        throw new UnsupportedOperationException("CenturyBridge: Target class net.minecraft.class_8667$1 is not public");
    }

    /** was net/minecraft/class_7923.field_41154Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_41154() {
        return net.minecraft.class_2960.method_60654("root");
    }

    /** was net/minecraft/class_8039.field_41877Lio/netty/util/AttributeKey; */
    public static io.netty.util.AttributeKey field_41877() {
        throw new UnsupportedOperationException("CenturyBridge: field_41877 (AttributeKey) was removed from class_8039 in 1.20.2 with no direct replacement; bundle-packet channel attribute mechanism reworked");
    }

    /** was net/minecraft/class_8113.field_42385Ljava/lang/String; */
    public static java.lang.String field_42385() {
        throw new UnsupportedOperationException("CenturyBridge: field_42385 (static String) was deleted from class_8113 in 1.20.2 with no direct replacement; its literal value cannot be determined from the new API surface alone");
    }

    /** was net/minecraft/class_8113.field_43149Ljava/lang/String; */
    public static java.lang.String field_43149() {
        return "sniffing_state";
    }

    /** was net/minecraft/class_8113.method_48863()Lorg/joml/Quaternionf; */
    public static org.joml.Quaternionf method_48863(net.minecraft.class_8113 self) {
        throw new UnsupportedOperationException("CenturyBridge: DisplayEntity left rotation getter was removed in 1.20.2");
    }

    /** was net/minecraft/class_8181.method_49274()Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 method_49274(net.minecraft.class_8181 self) {
        return self.method_54099();
    }

    /** was net/minecraft/class_8181.method_49275(Lnet/minecraft/class_1799;)V */
    public static void method_49275(net.minecraft.class_8181 self, net.minecraft.class_1799 a0) {
        self.method_54077(a0);
    }

    /** was net/minecraft/class_8181.method_49276()Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 method_49276(net.minecraft.class_8181 self) {
        return self.method_54079();
    }

    /** was net/minecraft/class_8216.field_43134Ljava/lang/String; */
    public static java.lang.String field_43134() {
        return "players";
    }

    /** was net/minecraft/class_8490.method_51203()Lcom/google/gson/Gson; */
    public static com.google.gson.Gson method_51203(net.minecraft.class_8490 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_8490.method_51203() returned a Gson instance that was removed in 1.20.2; the new API surface has no Gson field or method to delegate to");
    }

    /** was net/minecraft/class_1263.field_29952I */
    public static int field_29952() {
        return 64;
    }

    /** was net/minecraft/class_1263.field_42619I */
    public static int field_42619() {
        return 64;
    }

    /** was net/minecraft/class_1295.method_5602(I)V */
    public static void method_5602(net.minecraft.class_1295 self, int a0) {
        self.method_35043(a0);
    }

    /** was net/minecraft/class_1295.method_5606()I */
    public static int method_5606(net.minecraft.class_1295 self) {
        return self.method_5605();
    }

    /** was net/minecraft/class_1297.field_29991F */
    public static float field_29991() {
        return 0.0f;
    }

    /** was net/minecraft/class_1297.method_18378(Lnet/minecraft/class_4050;Lnet/minecraft/class_4048;)F */
    public static float method_18378(net.minecraft.class_1297 self, net.minecraft.class_4050 a0, net.minecraft.class_4048 a1) {
        return self.method_18381(a0);
    }

    /** was net/minecraft/class_1297.method_52537(Lnet/minecraft/class_1297;)F */
    public static float method_52537(net.minecraft.class_1297 self, net.minecraft.class_1297 a0) {
        float f = (float) (self.method_23317() - a0.method_23317());
        float g = (float) (self.method_23318() - a0.method_23318());
        float h = (float) (self.method_23321() - a0.method_23321());
        return (float) Math.sqrt(f * f + g * g + h * h);
    }

    /** was net/minecraft/class_1309.field_30067F */
    public static float field_30067() {
        return 0.02f;
    }

    /** was net/minecraft/class_1309.method_20236(Lnet/minecraft/class_1268;)V */
    public static void method_20236(net.minecraft.class_1309 self, net.minecraft.class_1268 a0) {
        self.method_23667(a0, false);
    }

    /** was net/minecraft/class_131.field_25217Lnet/minecraft/class_5339; */
    public static net.minecraft.class_5339 field_25217() {
        throw new UnsupportedOperationException("CenturyBridge: CHEST model layer (field_25217) was removed in 1.20.5");
    }

    /** was net/minecraft/class_131.field_25233Lnet/minecraft/class_5339; */
    public static net.minecraft.class_5339 field_25233() {
        throw new UnsupportedOperationException("CenturyBridge: LootConditionTypes.KILLED_BY_PLAYER was removed in 1.20.5");
    }

    /** was net/minecraft/class_1355.method_35114(I)V */
    public static void method_35114(net.minecraft.class_1355 self, int a0) {
        net.minecraft.class_1352.class_4134[] values = net.minecraft.class_1352.class_4134.values();
        if (a0 >= 0 && a0 < values.length) {
            self.method_6273(values[a0]);
        }
    }

    /** was net/minecraft/class_1477$class_1479.field_6916Lnet/minecraft/class_1477; */
    public static net.minecraft.class_1477 field_6916() {
        throw new UnsupportedOperationException("CenturyBridge: field_6916 was deleted without replacement");
    }

    /** was net/minecraft/class_1498.method_6786()Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 method_6786(net.minecraft.class_1498 self) {
        return new net.minecraft.class_1799(net.minecraft.class_2246.field_10103);
    }

    /** was net/minecraft/class_14.method_27138(Lnet/minecraft/class_2680;)Z */
    public static boolean method_27138(net.minecraft.class_14 self, net.minecraft.class_2680 a0) {
        throw new UnsupportedOperationException("CenturyBridge: PathNodeMaker no longer supports checking a raw BlockState without position or context in 1.20.5");
    }

    /** was net/minecraft/class_14.method_63(Lnet/minecraft/class_1308;Lnet/minecraft/class_2338;)Lnet/minecraft/class_7; */
    public static net.minecraft.class_7 method_63(net.minecraft.class_14 self, net.minecraft.class_1308 a0, net.minecraft.class_2338 a1) {
        return self.method_57625(a0, a1);
    }

    /** was net/minecraft/class_14.method_64(Lnet/minecraft/class_1922;IIILjava/util/EnumSet;Lnet/minecraft/class_7;Lnet/minecraft/class_2338;)Lnet/minecraft/class_7; */
    public static net.minecraft.class_7 method_64(net.minecraft.class_14 self, net.minecraft.class_1922 a0, int a1, int a2, int a3, java.util.EnumSet a4, net.minecraft.class_7 a5, net.minecraft.class_2338 a6) {
        throw new UnsupportedOperationException("CenturyBridge: PathNodeMaker.getNode requires PathNodeMakerContext in 1.20.5");
    }

    /** was net/minecraft/class_1534.field_42463Ljava/lang/String; */
    public static java.lang.String field_42463() {
        return "variant";
    }

    /** was net/minecraft/class_155.field_29699Z */
    public static boolean field_29699() {
        return false;
    }

    /** was net/minecraft/class_155.field_29701Z */
    public static boolean field_29701() {
        return false;
    }

    /** was net/minecraft/class_155.method_44355(Ljava/lang/String;Z)Ljava/lang/String; */
    public static java.lang.String method_44355(net.minecraft.class_155 self, java.lang.String a0, boolean a1) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < a0.length(); i++) {
            char c = a0.charAt(i);
            if ((c != '\u00a7' && c >= ' ' && c != '\u007f') || (a1 && c == '\n')) {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    /** was net/minecraft/class_155.method_643(C)Z */
    public static boolean method_643(net.minecraft.class_155 self, char a0) {
        return a0 != '\u00a7' && a0 >= ' ' && a0 != 127;
    }

    /** was net/minecraft/class_1593$class_1599.field_7329Lnet/minecraft/class_1593; */
    public static net.minecraft.class_1593 field_7329() {
        throw new UnsupportedOperationException("CenturyBridge: Field is an instance field (this$0) but shim has no receiver parameter");
    }

    /** was net/minecraft/class_1642.field_41028F */
    public static float field_41028() {
        return 0.001f;
    }

    /** was net/minecraft/class_1657$1.field_7533[I */
    public static int[] field_7533() {
        throw new UnsupportedOperationException("CenturyBridge: Switch map field_7533 in class_1657$1 is no longer available");
    }

    /** was net/minecraft/class_1657.field_30643I */
    public static int field_30643() {
        return 100;
    }

    /** was net/minecraft/class_1657.method_53968(Ljava/lang/String;)Z */
    public static boolean method_53968(net.minecraft.class_1657 self, java.lang.String a0) {
        return self.method_7334().getName().equalsIgnoreCase(a0);
    }

    /** was net/minecraft/class_1657.method_54292(Z)F */
    public static float method_54292(net.minecraft.class_1657 self, boolean a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_54292 was removed in 1.20.5");
    }

    /** was net/minecraft/class_1704$1.field_17291Lnet/minecraft/class_1704; */
    public static net.minecraft.class_1704 field_17291() {
        throw new UnsupportedOperationException("CenturyBridge: Cannot access outer instance field statically without receiver");
    }

    /** was net/minecraft/class_1704$class_1705.field_7768Lnet/minecraft/class_1704; */
    public static net.minecraft.class_1704 field_7768() {
        throw new UnsupportedOperationException("CenturyBridge: field_7768 is an instance field but shim has no receiver");
    }

    /** was net/minecraft/class_1718$2.field_7816Lnet/minecraft/class_1718; */
    public static net.minecraft.class_1718 field_7816() {
        throw new UnsupportedOperationException("CenturyBridge: field_7816 is an instance field and cannot be accessed without a receiver");
    }

    /** was net/minecraft/class_1718$3.field_7817Lnet/minecraft/class_1718; */
    public static net.minecraft.class_1718 field_7817() {
        throw new UnsupportedOperationException("CenturyBridge: field_7817 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/class_1726$3.field_7852Lnet/minecraft/class_1726; */
    public static net.minecraft.class_1726 field_7852() {
        throw new UnsupportedOperationException("CenturyBridge: field_7852 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/class_1726$4.field_7853Lnet/minecraft/class_1726; */
    public static net.minecraft.class_1726 field_7853() {
        throw new UnsupportedOperationException("CenturyBridge: field_7853 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/class_1726$5.field_7854Lnet/minecraft/class_1726; */
    public static net.minecraft.class_1726 field_7854() {
        throw new UnsupportedOperationException("CenturyBridge: field_7854 is a synthetic outer class reference and cannot be accessed without an instance");
    }

    /** was net/minecraft/class_1741.method_24355()F */
    public static float method_24355(net.minecraft.class_1741 self) {
        return self.comp_2303();
    }

    /** was net/minecraft/class_1741.method_7695()Lnet/minecraft/class_1856; */
    public static net.minecraft.class_1856 method_7695(net.minecraft.class_1741 self) {
        throw new UnsupportedOperationException("CenturyBridge: Ingredient getter is no longer available on class_1741");
    }

    /** was net/minecraft/class_1741.method_7699()I */
    public static int method_7699(net.minecraft.class_1741 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_1741 is no longer RabbitMoveControl");
    }

    /** was net/minecraft/class_1741.method_7700()F */
    public static float method_7700(net.minecraft.class_1741 self) {
        return self.comp_2303();
    }

    /** was net/minecraft/class_1747.field_30849Ljava/lang/String; */
    public static java.lang.String field_30849() {
        return "BlockEntityTag";
    }

    /** was net/minecraft/class_1747.field_30850Ljava/lang/String; */
    public static java.lang.String field_30850() {
        return "BlockEntityTag";
    }

    /** was net/minecraft/class_1747.method_38073(Lnet/minecraft/class_1799;Lnet/minecraft/class_2591;Lnet/minecraft/class_2487;)V */
    public static void method_38073(net.minecraft.class_1747 self, net.minecraft.class_1799 a0, net.minecraft.class_2591 a1, net.minecraft.class_2487 a2) {
        net.minecraft.class_1747.method_57338(a0, a1, a2);
    }

    /** was net/minecraft/class_1759.field_30860Ljava/lang/String; */
    public static java.lang.String field_30860() {
        return "Charged";
    }

    /** was net/minecraft/class_1759.field_30861Ljava/lang/String; */
    public static java.lang.String field_30861() {
        return "Items";
    }

    /** was net/minecraft/class_1759.field_30862Ljava/lang/String; */
    public static java.lang.String field_30862() {
        return "use_ticks";
    }

    /** was net/minecraft/class_1772.field_30874Ljava/lang/String; */
    public static java.lang.String field_30874() {
        return "default";
    }

    /** was net/minecraft/class_1780.method_7809(Lnet/minecraft/class_2487;Ljava/util/List;)V */
    public static void method_7809(net.minecraft.class_1780 self, net.minecraft.class_2487 a0, java.util.List a1) {
        throw new UnsupportedOperationException("CenturyBridge: Banner pattern NBT parsing requires registry context and is no longer statically available");
    }

    /** was net/minecraft/class_1781.field_30875Ljava/lang/String; */
    public static java.lang.String field_30875() {
        return "LodestonePos";
    }

    /** was net/minecraft/class_1781.field_30876Ljava/lang/String; */
    public static java.lang.String field_30876() {
        return "default";
    }

    /** was net/minecraft/class_1781.field_30877Ljava/lang/String; */
    public static java.lang.String field_30877() {
        return "LodestonePos";
    }

    /** was net/minecraft/class_1781.field_30878Ljava/lang/String; */
    public static java.lang.String field_30878() {
        return "Fireworks";
    }

    /** was net/minecraft/class_1781.field_30879Ljava/lang/String; */
    public static java.lang.String field_30879() {
        return "Potion";
    }

    /** was net/minecraft/class_1781.field_30880Ljava/lang/String; */
    public static java.lang.String field_30880() {
        return "Fireworks";
    }

    /** was net/minecraft/class_1781.field_30881Ljava/lang/String; */
    public static java.lang.String field_30881() {
        return "Fireworks";
    }

    /** was net/minecraft/class_1781.field_30882Ljava/lang/String; */
    public static java.lang.String field_30882() {
        return "Type";
    }

    /** was net/minecraft/class_1781.field_30883Ljava/lang/String; */
    public static java.lang.String field_30883() {
        return "Fireworks";
    }

    /** was net/minecraft/class_1806.field_41067Ljava/lang/String; */
    public static java.lang.String field_41067() {
        return "map_color";
    }

    /** was net/minecraft/class_1806.field_41068Ljava/lang/String; */
    public static java.lang.String field_41068() {
        return "map_color";
    }

    /** was net/minecraft/class_1806.method_17440(I)Ljava/lang/String; */
    public static java.lang.String method_17440(net.minecraft.class_1806 self, int a0) {
        return "map_" + a0;
    }

    /** was net/minecraft/class_1809.field_30916Ljava/lang/String; */
    public static java.lang.String field_30916() {
        return "Tool modifier";
    }

    /** was net/minecraft/class_1814.field_8908Lnet/minecraft/class_124; */
    public static net.minecraft.class_124 field_8908() {
        throw new UnsupportedOperationException("CenturyBridge: field_8908 is an instance field but the shim signature lacks the instance parameter");
    }

    /** was net/minecraft/class_1819.field_30920Ljava/lang/String; */
    public static java.lang.String field_30920() {
        return "ChargedProjectiles";
    }

    /** was net/minecraft/class_1830.field_30923Ljava/lang/String; */
    public static java.lang.String field_30923() {
        return "Potion";
    }

    /** was net/minecraft/class_1843.field_30929I */
    public static int field_30929() {
        return 64;
    }

    /** was net/minecraft/class_1843.field_30930I */
    public static int field_30930() {
        throw new UnsupportedOperationException("CenturyBridge: field_30930 (int field in CompassItem) is no longer available in 1.20.5");
    }

    /** was net/minecraft/class_1843.field_30931I */
    public static int field_30931() {
        return 32;
    }

    /** was net/minecraft/class_1843.field_30932I */
    public static int field_30932() {
        return 32;
    }

    /** was net/minecraft/class_1843.field_30933I */
    public static int field_30933() {
        return 0x6666FF;
    }

    /** was net/minecraft/class_1843.field_30934I */
    public static int field_30934() {
        return 1200;
    }

    /** was net/minecraft/class_1843.field_30935Ljava/lang/String; */
    public static java.lang.String field_30935() {
        return "default_potion";
    }

    /** was net/minecraft/class_1843.field_30936Ljava/lang/String; */
    public static java.lang.String field_30936() {
        return "filtered_pages";
    }

    /** was net/minecraft/class_1843.field_30937Ljava/lang/String; */
    public static java.lang.String field_30937() {
        return "pages";
    }

    /** was net/minecraft/class_1843.field_30938Ljava/lang/String; */
    public static java.lang.String field_30938() {
        return "pages";
    }

    /** was net/minecraft/class_1843.field_30939Ljava/lang/String; */
    public static java.lang.String field_30939() {
        return "resolved";
    }

    /** was net/minecraft/class_1843.field_30940Ljava/lang/String; */
    public static java.lang.String field_30940() {
        return "filtered_pages";
    }

    /** was net/minecraft/class_1843.field_30941Ljava/lang/String; */
    public static java.lang.String field_30941() {
        return "Potion";
    }

    /** was net/minecraft/class_1847.field_8963Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8963() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8963.comp_349();
    }

    /** was net/minecraft/class_1847.field_8964Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8964() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8964.comp_349();
    }

    /** was net/minecraft/class_1847.field_8965Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8965() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8965.comp_349();
    }

    /** was net/minecraft/class_1847.field_8966Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8966() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8966.comp_349();
    }

    /** was net/minecraft/class_1847.field_8967Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8967() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8967.comp_349();
    }

    /** was net/minecraft/class_1847.field_8968Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8968() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8968.comp_349();
    }

    /** was net/minecraft/class_1847.field_8969Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8969() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8969.comp_349();
    }

    /** was net/minecraft/class_1847.field_8970Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8970() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8970.comp_349();
    }

    /** was net/minecraft/class_1847.field_8971Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8971() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8971.comp_349();
    }

    /** was net/minecraft/class_1847.field_8972Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8972() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8972.comp_349();
    }

    /** was net/minecraft/class_1847.field_8973Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8973() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8973.comp_349();
    }

    /** was net/minecraft/class_1847.field_8974Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8974() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8974.comp_349();
    }

    /** was net/minecraft/class_1847.field_8975Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8975() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8975.comp_349();
    }

    /** was net/minecraft/class_1847.field_8976Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8976() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8976.comp_349();
    }

    /** was net/minecraft/class_1847.field_8977Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8977() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8977.comp_349();
    }

    /** was net/minecraft/class_1847.field_8978Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8978() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8978.comp_349();
    }

    /** was net/minecraft/class_1847.field_8979Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8979() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8979.comp_349();
    }

    /** was net/minecraft/class_1847.field_8980Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8980() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8980.comp_349();
    }

    /** was net/minecraft/class_1847.field_8981Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8981() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8981.comp_349();
    }

    /** was net/minecraft/class_1847.field_8982Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8982() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8982.comp_349();
    }

    /** was net/minecraft/class_1847.field_8983Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8983() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8983.comp_349();
    }

    /** was net/minecraft/class_1847.field_8985Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8985() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8985.comp_349();
    }

    /** was net/minecraft/class_1847.field_8986Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8986() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8986.comp_349();
    }

    /** was net/minecraft/class_1847.field_8987Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8987() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8987.comp_349();
    }

    /** was net/minecraft/class_1847.field_8988Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8988() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8988.comp_349();
    }

    /** was net/minecraft/class_1847.field_8989Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8989() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8989.comp_349();
    }

    /** was net/minecraft/class_1847.field_8990Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8990() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8990.comp_349();
    }

    /** was net/minecraft/class_1847.field_8991Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8991() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8991.comp_349();
    }

    /** was net/minecraft/class_1847.field_8992Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8992() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8992.comp_349();
    }

    /** was net/minecraft/class_1847.field_8993Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8993() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8993.comp_349();
    }

    /** was net/minecraft/class_1847.field_8994Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8994() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8994.comp_349();
    }

    /** was net/minecraft/class_1847.field_8995Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8995() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8995.comp_349();
    }

    /** was net/minecraft/class_1847.field_8996Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8996() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8996.comp_349();
    }

    /** was net/minecraft/class_1847.field_8997Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8997() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8997.comp_349();
    }

    /** was net/minecraft/class_1847.field_8998Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8998() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8998.comp_349();
    }

    /** was net/minecraft/class_1847.field_8999Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8999() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_8999.comp_349();
    }

    /** was net/minecraft/class_1847.field_9000Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_9000() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_9000.comp_349();
    }

    /** was net/minecraft/class_1847.field_9001Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_9001() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_9001.comp_349();
    }

    /** was net/minecraft/class_1847.field_9002Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_9002() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_9002.comp_349();
    }

    /** was net/minecraft/class_1847.field_9003Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_9003() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_9003.comp_349();
    }

    /** was net/minecraft/class_1847.field_9004Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_9004() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_9004.comp_349();
    }

    /** was net/minecraft/class_1847.field_9005Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_9005() {
        return (net.minecraft.class_1842) net.minecraft.class_1847.field_9005.comp_349();
    }

    public com.mojang.serialization.Codec method_53736() {
        return ((net.minecraft.class_1865) (Object) this).method_53736().codec();
    }

    /** was net/minecraft/class_1890.method_22445(Lnet/minecraft/class_2499;)Ljava/util/Map; */
    public static java.util.Map method_22445(net.minecraft.class_1890 self, net.minecraft.class_2499 a0) {
        throw new UnsupportedOperationException("CenturyBridge: Enchantments are dynamic in 1.20.5 and cannot be resolved from raw NBT without a registry context");
    }

    /** was net/minecraft/class_1890.method_37423(Lnet/minecraft/class_1887;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_37423(net.minecraft.class_1890 self, net.minecraft.class_1887 a0) {
        throw new UnsupportedOperationException("CenturyBridge: Enchantments are dynamic in this version and their IDs cannot be resolved without a registry context.");
    }

    /** was net/minecraft/class_1890.method_37424(Lnet/minecraft/class_2487;)I */
    public static int method_37424(net.minecraft.class_1890 self, net.minecraft.class_2487 a0) {
        return Math.max(0, Math.min(255, a0.method_10550("lvl")));
    }

    /** was net/minecraft/class_1890.method_37425(Lnet/minecraft/class_2487;I)V */
    public static void method_37425(net.minecraft.class_1890 self, net.minecraft.class_2487 a0, int a1) {
        throw new UnsupportedOperationException("CenturyBridge: NBT-based enchantment manipulation is no longer supported");
    }

    /** was net/minecraft/class_1890.method_37426(Lnet/minecraft/class_2960;I)Lnet/minecraft/class_2487; */
    public static net.minecraft.class_2487 method_37426(net.minecraft.class_1890 self, net.minecraft.class_2960 a0, int a1) {
        net.minecraft.class_2487 nbtCompound = new net.minecraft.class_2487();
        nbtCompound.method_10582("id", java.lang.String.valueOf(a0));
        nbtCompound.method_10548("lvl", (short) a1);
        return nbtCompound;
    }

    public void method_32888(net.minecraft.class_5712 a0, net.minecraft.class_243 a1, net.minecraft.class_5712.class_7397 a2) {
        ((net.minecraft.class_1936) (Object) this).method_32888(net.minecraft.class_6880.method_40223(a0), a1, a2);
    }

    public void method_33596(net.minecraft.class_1297 a0, net.minecraft.class_5712 a1, net.minecraft.class_2338 a2) {
        ((net.minecraft.class_1936) (Object) this).method_33596(a0, net.minecraft.class_6880.method_40223(a1), a2);
    }

    public void method_43275(net.minecraft.class_1297 a0, net.minecraft.class_5712 a1, net.minecraft.class_243 a2) {
        ((net.minecraft.class_1936) (Object) this).method_43275(a0, net.minecraft.class_6880.method_40223(a1), a2);
    }

    public void method_43276(net.minecraft.class_5712 a0, net.minecraft.class_2338 a1, net.minecraft.class_5712.class_7397 a2) {
        ((net.minecraft.class_1936) (Object) this).method_43276(net.minecraft.class_6880.method_40223(a0), a1, a2);
    }

    /** was net/minecraft/class_1973.field_24715Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24715() {
        return net.minecraft.class_1973.field_24715.codec();
    }

    /** was net/minecraft/class_1992.field_24717Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24717() {
        return net.minecraft.class_1992.field_24717.codec();
    }

    /** was net/minecraft/class_2169.field_24730Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24730() {
        return net.minecraft.class_2169.field_24730.codec();
    }

    /** was net/minecraft/class_2196$class_2197.field_9877Ljava/lang/String; */
    public static java.lang.String field_9877() {
        throw new UnsupportedOperationException("CenturyBridge: field_9877 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/class_2196$class_2197.method_35691()Ljava/lang/String; */
    public static java.lang.String method_35691(net.minecraft.class_2196.class_2197 self) {
        return self.comp_2653();
    }

    /** was net/minecraft/class_2196$class_2197.method_35692()[Lnet/minecraft/class_2196$class_2198; */
    public static net.minecraft.class_2196.class_2198[] method_35692(net.minecraft.class_2196.class_2197 self) {
        return self.comp_2654();
    }

    /** was net/minecraft/class_2196$class_2198.method_35693()Lnet/minecraft/class_2300; */
    public static net.minecraft.class_2300 method_35693(net.minecraft.class_2196.class_2198 self) {
        return self.comp_2657();
    }

    /** was net/minecraft/class_2196$class_2198.method_9343()I */
    public static int method_9343(net.minecraft.class_2196.class_2198 self) {
        return self.comp_2655();
    }

    /** was net/minecraft/class_2196$class_2198.method_9344()I */
    public static int method_9344(net.minecraft.class_2196.class_2198 self) {
        return self.comp_2655();
    }

    /** was net/minecraft/class_22$class_5637.field_27892I */
    public static int field_27892() {
        throw new UnsupportedOperationException("CenturyBridge: field_27892 is an instance field but the shim is static");
    }

    /** was net/minecraft/class_22$class_5637.field_27893I */
    public static int field_27893() {
        throw new UnsupportedOperationException("CenturyBridge: field_27893 cannot be represented as static int");
    }

    /** was net/minecraft/class_22$class_5637.field_27894I */
    public static int field_27894() {
        throw new UnsupportedOperationException("CenturyBridge: field_27894 is an instance field but the shim has no receiver");
    }

    /** was net/minecraft/class_22$class_5637.field_27895I */
    public static int field_27895() {
        return 4;
    }

    /** was net/minecraft/class_22$class_5637.field_27896[B */
    public static byte[] field_27896() {
        return new byte[]{(byte) -119, 80, 78, 71, 13, 10, 26, 10};
    }

    /** was net/minecraft/class_2284$1.field_10786Lnet/minecraft/class_2284; */
    public static net.minecraft.class_2284 field_10786() {
        throw new UnsupportedOperationException("CenturyBridge: field_10786 has been removed without replacement");
    }

    /** was net/minecraft/class_2284$2.field_10788Lnet/minecraft/class_2284; */
    public static net.minecraft.class_2284 field_10788() {
        throw new UnsupportedOperationException("CenturyBridge: field_10788 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/class_2370.method_46744(ILnet/minecraft/class_5321;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/class_6880$class_6883; */
    public static net.minecraft.class_6880.class_6883 method_46744(net.minecraft.class_2370 self, int a0, net.minecraft.class_5321 a1, java.lang.Object a2, com.mojang.serialization.Lifecycle a3) {
        throw new UnsupportedOperationException("CenturyBridge: Registering with an explicit raw ID is no longer supported");
    }

    public net.minecraft.class_6880.class_6883 method_10272(net.minecraft.class_5321 a0, java.lang.Object a1, com.mojang.serialization.Lifecycle a2) {
        return ((net.minecraft.class_2385) (Object) this).method_10272(a0, a1, new net.minecraft.class_9248(java.util.Optional.empty(), a2));
    }

    /** was net/minecraft/class_2547.method_40065()Z */
    public static boolean method_40065(net.minecraft.class_2547 self) {
        return self.method_48106();
    }

    /** was net/minecraft/class_2573.field_31297Ljava/lang/String; */
    public static java.lang.String field_31297() {
        return "Patterns";
    }

    /** was net/minecraft/class_2573.field_31298Ljava/lang/String; */
    public static java.lang.String field_31298() {
        return "Items";
    }

    /** was net/minecraft/class_2573.field_31299Ljava/lang/String; */
    public static java.lang.String field_31299() {
        return "Patterns";
    }

    /** was net/minecraft/class_2573.method_16842(Lnet/minecraft/class_2561;)V */
    public static void method_16842(net.minecraft.class_2573 self, net.minecraft.class_2561 a0) {
        throw new UnsupportedOperationException("CenturyBridge: BannerBlockEntity.setCustomName (method_16842) was removed in 1.20.5 in favor of the component system.");
    }

    /** was net/minecraft/class_2573.method_24280(Lnet/minecraft/class_1767;Lnet/minecraft/class_2499;)Ljava/util/List; */
    public static java.util.List method_24280(net.minecraft.class_2573 self, net.minecraft.class_1767 a0, net.minecraft.class_2499 a1) {
        throw new UnsupportedOperationException("CenturyBridge: Cannot parse banner patterns without registry context");
    }

    /** was net/minecraft/class_2596.method_11052(Lnet/minecraft/class_2540;)V */
    public static void method_11052(net.minecraft.class_2596 self, net.minecraft.class_2540 a0) {
        throw new UnsupportedOperationException("CenturyBridge: Packets no longer support self-serialization without a codec context in 1.20.5");
    }

    /** was net/minecraft/class_2596.method_52273()Lnet/minecraft/class_2539; */
    public static net.minecraft.class_2539 method_52273(net.minecraft.class_2596 self) {
        throw new UnsupportedOperationException("CenturyBridge: Packets no longer hold a reference to their NetworkState in 1.20.5");
    }

    /** was net/minecraft/class_2615.field_12025Lnet/minecraft/class_265; */
    public static net.minecraft.class_265 field_12025() {
        return net.minecraft.class_259.method_1077();
    }

    /** was net/minecraft/class_2621.field_12037Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_12037() {
        throw new UnsupportedOperationException("CenturyBridge: field_12037 is an instance field but the shim has no receiver parameter");
    }

    /** was net/minecraft/class_2636$1.field_27219Lnet/minecraft/class_2636; */
    public static net.minecraft.class_2636 field_27219() {
        throw new UnsupportedOperationException("CenturyBridge: field_27219 is the synthetic outer this$0 field of class_2636$1 and cannot be accessed statically without a receiver");
    }

    /** was net/minecraft/class_2818$class_5564.field_27227Lnet/minecraft/class_2818; */
    public static net.minecraft.class_2818 field_27227() {
        throw new UnsupportedOperationException("CenturyBridge: Cannot access instance field field_27227 statically");
    }

    /** was net/minecraft/class_2826$class_6869.field_36411Lnet/minecraft/class_2826; */
    public static net.minecraft.class_2826 field_36411() {
        throw new UnsupportedOperationException("CenturyBridge: field_36411 cannot be retrieved statically");
    }

    /** was net/minecraft/class_2891.field_24768Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24768() {
        return net.minecraft.class_2891.field_24768.codec();
    }

    /** was net/minecraft/class_2897.field_24769Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24769() {
        return net.minecraft.class_2897.field_24769.codec();
    }

    /** was net/minecraft/class_2940.method_12712()Lnet/minecraft/class_2941; */
    public static net.minecraft.class_2941 method_12712(net.minecraft.class_2940 self) {
        return self.comp_2328();
    }

    /** was net/minecraft/class_2940.method_12713()I */
    public static int method_12713(net.minecraft.class_2940 self) {
        return self.comp_2327();
    }

    /** was net/minecraft/class_2944$1.field_42840Lnet/minecraft/class_2944; */
    public static net.minecraft.class_2944 field_42840() {
        throw new UnsupportedOperationException("CenturyBridge: field_42840 is no longer available as the outer class is not captured by class_2944$1");
    }

    /** was net/minecraft/class_2956.field_37790Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37790() {
        return net.minecraft.class_2956.field_37790.codec();
    }

    /** was net/minecraft/class_29.method_261(Lnet/minecraft/class_1657;)Lnet/minecraft/class_2487; */
    public static net.minecraft.class_2487 method_261(net.minecraft.class_29 self, net.minecraft.class_1657 a0) {
        return (net.minecraft.class_2487) self.method_55789(a0).orElse(null);
    }

    /** was net/minecraft/class_29.method_263()[Ljava/lang/String; */
    public static java.lang.String[] method_263(net.minecraft.class_29 self) {
        throw new UnsupportedOperationException("CenturyBridge: PlayerDataStorage no longer exposes saved player IDs");
    }

    /** was net/minecraft/class_3003.field_24890Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24890() {
        return net.minecraft.class_3003.field_24890.codec();
    }

    /** was net/minecraft/class_3006.field_37791Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37791() {
        return net.minecraft.class_3006.field_37791.codec();
    }

    /** was net/minecraft/class_3021.field_37792Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37792() {
        return net.minecraft.class_3021.field_37792.codec();
    }

    /** was net/minecraft/class_3071.field_37793Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37793() {
        return net.minecraft.class_3071.field_37793.codec();
    }

    /** was net/minecraft/class_3076.field_37800Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37800() {
        return net.minecraft.class_3076.field_37800.codec();
    }

    /** was net/minecraft/class_3098.field_37801Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37801() {
        return net.minecraft.class_3098.field_37801.codec();
    }

    /** was net/minecraft/class_3108.field_37803Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37803() {
        return net.minecraft.class_3108.field_37803.codec();
    }

    /** was net/minecraft/class_3116.field_37806Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37806() {
        return net.minecraft.class_3116.field_37806.codec();
    }

    /** was net/minecraft/class_315.field_37208Z */
    public static boolean field_37208() {
        return false;
    }

    /** was net/minecraft/class_3170.field_37815Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37815() {
        return net.minecraft.class_3170.field_37815.codec();
    }

    /** was net/minecraft/class_3178$1.field_13826Lnet/minecraft/class_3178; */
    public static net.minecraft.class_3178 field_13826() {
        throw new UnsupportedOperationException("CenturyBridge: Instance field field_13826 cannot be accessed without a receiver");
    }

    /** was net/minecraft/class_3182$2.field_13842Lnet/minecraft/class_3182; */
    public static net.minecraft.class_3182 field_13842() {
        throw new UnsupportedOperationException("CenturyBridge: field_13842 is an instance field but the shim is static without the receiver parameter");
    }

    /** was net/minecraft/class_3188.field_37817Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37817() {
        return net.minecraft.class_3188.field_37817.codec();
    }

    /** was net/minecraft/class_3197.field_37818Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37818() {
        return net.minecraft.class_3197.field_37818.codec();
    }

    /** was net/minecraft/class_3223.field_37819Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37819() {
        return net.minecraft.class_3223.field_37819.codec();
    }

    /** was net/minecraft/class_3262.method_45178()Z */
    public static boolean method_45178(net.minecraft.class_3262 self) {
        return self instanceof net.minecraft.class_3268;
    }

    /** was net/minecraft/class_3275.field_24987Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24987() {
        return net.minecraft.class_3275.field_24987.codec();
    }

    /** was net/minecraft/class_3411.field_37807Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37807() {
        return net.minecraft.class_3411.field_37807.codec();
    }

    /** was net/minecraft/class_3415.method_14834(Lnet/minecraft/class_3485;Lnet/minecraft/class_2338;Lnet/minecraft/class_2470;Lnet/minecraft/class_6130;Lnet/minecraft/class_5819;Z)V */
    public static void method_14834(net.minecraft.class_3415 self, net.minecraft.class_3485 a0, net.minecraft.class_2338 a1, net.minecraft.class_2470 a2, net.minecraft.class_6130 a3, net.minecraft.class_5819 a4, boolean a5) {
        self.method_59864(a0, a1, a2, a3, a4, a5);
    }

    /** was net/minecraft/class_3417.field_14554Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14554() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14554.comp_349();
    }

    /** was net/minecraft/class_3417.field_14581Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14581() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14581.comp_349();
    }

    /** was net/minecraft/class_3417.field_14684Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14684() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14684.comp_349();
    }

    /** was net/minecraft/class_3417.field_14761Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14761() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14761.comp_349();
    }

    /** was net/minecraft/class_3417.field_14862Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14862() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14862.comp_349();
    }

    /** was net/minecraft/class_3417.field_14883Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14883() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14883.comp_349();
    }

    /** was net/minecraft/class_3417.field_14966Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14966() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14966.comp_349();
    }

    /** was net/minecraft/class_3417.field_15103Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_15103() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_15103.comp_349();
    }

    /** was net/minecraft/class_3417.field_15191Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_15191() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_15191.comp_349();
    }

    /** was net/minecraft/class_3417.field_21866Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_21866() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_21866.comp_349();
    }

    /** was net/minecraft/class_3488.field_25000Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25000() {
        return net.minecraft.class_3488.field_25000.codec();
    }

    /** was net/minecraft/class_3745.method_18811(Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;Lnet/minecraft/class_1676;F)V */
    public static void method_18811(net.minecraft.class_3745 self, net.minecraft.class_1309 a0, net.minecraft.class_1799 a1, net.minecraft.class_1676 a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: CrossbowItem.shoot (method_18811) was removed in 1.20.5");
    }

    /** was net/minecraft/class_3745.method_24652(Lnet/minecraft/class_1309;Lnet/minecraft/class_1309;Lnet/minecraft/class_1676;FF)V */
    public static void method_24652(net.minecraft.class_3745 self, net.minecraft.class_1309 a0, net.minecraft.class_1309 a1, net.minecraft.class_1676 a2, float a3, float a4) {
        self.method_24654(a1, a3);
    }

    /** was net/minecraft/class_3745.method_24653(Lnet/minecraft/class_1309;Lnet/minecraft/class_243;F)Lorg/joml/Vector3f; */
    public static org.joml.Vector3f method_24653(net.minecraft.class_3745 self, net.minecraft.class_1309 a0, net.minecraft.class_243 a1, float a2) {
        double dx = a1.field_1352 - a0.method_23317();
        double dy = a1.field_1351 - a0.method_23320();
        double dz = a1.field_1350 - a0.method_23321();
        double dh = Math.sqrt(dx * dx + dz * dz);
        float pitch = (float) (-(Math.atan2(dy, dh) * 180.0 / Math.PI));
        float yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        return new org.joml.Vector3f(pitch, yaw, 0.0f);
    }

    /** was net/minecraft/class_3754.field_24773Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24773() {
        return net.minecraft.class_3754.field_24773.codec();
    }

    /** was net/minecraft/class_3763$class_3764.field_16604Lnet/minecraft/class_3763; */
    public static net.minecraft.class_3763 field_16604() {
        throw new UnsupportedOperationException("CenturyBridge: field_16604 has been removed without replacement");
    }

    /** was net/minecraft/class_3763$class_4223.field_18882Lnet/minecraft/class_3763; */
    public static net.minecraft.class_3763 field_18882() {
        throw new UnsupportedOperationException("CenturyBridge: Outer class reference field_18882 was removed and cannot be accessed");
    }

    /** was net/minecraft/class_3763.method_20030(Lnet/minecraft/class_3763;)F */
    public static float method_20030(net.minecraft.class_3763 self, net.minecraft.class_3763 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_20030 was deleted with no direct replacement");
    }

    /** was net/minecraft/class_3776.field_24948Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24948() {
        return net.minecraft.class_3776.field_24948.codec();
    }

    /** was net/minecraft/class_3777.field_24947Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24947() {
        return net.minecraft.class_3777.field_24947.codec();
    }

    /** was net/minecraft/class_3782.field_24950Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24950() {
        return net.minecraft.class_3782.field_24950.codec();
    }

    /** was net/minecraft/class_3793.field_24998Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24998() {
        return net.minecraft.class_3793.field_24998.codec();
    }

    /** was net/minecraft/class_3794.field_25003Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25003() {
        return net.minecraft.class_3794.field_25003.codec();
    }

    /** was net/minecraft/class_3795.field_25002Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25002() {
        return net.minecraft.class_3795.field_25002.codec();
    }

    /** was net/minecraft/class_3798.field_25014Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25014() {
        return net.minecraft.class_3798.field_25014.codec();
    }

    /** was net/minecraft/class_3803$2.field_16777Lnet/minecraft/class_3803; */
    public static net.minecraft.class_3803 field_16777() {
        throw new UnsupportedOperationException("CenturyBridge: Synthetic outer field cannot be accessed without receiver");
    }

    /** was net/minecraft/class_3803$3.field_16778Lnet/minecraft/class_3803; */
    public static net.minecraft.class_3803 field_16778() {
        throw new UnsupportedOperationException("CenturyBridge: Instance field accessed without receiver");
    }

    /** was net/minecraft/class_3818.field_24994Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24994() {
        return net.minecraft.class_3818.field_24994.codec();
    }

    /** was net/minecraft/class_3819.field_24999Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24999() {
        return net.minecraft.class_3819.field_24999.codec();
    }

    /** was net/minecraft/class_3820.field_25001Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25001() {
        return net.minecraft.class_3820.field_25001.codec();
    }

    /** was net/minecraft/class_3822.field_25005Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25005() {
        return net.minecraft.class_3822.field_25005.codec();
    }

    /** was net/minecraft/class_3823.field_25010Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25010() {
        return net.minecraft.class_3823.field_25010.codec();
    }

    /** was net/minecraft/class_3824.field_25009Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25009() {
        return net.minecraft.class_3824.field_25009.codec();
    }

    /** was net/minecraft/class_3826.field_25011Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25011() {
        return net.minecraft.class_3826.field_25011.codec();
    }

    public net.minecraft.class_3827 method_16821(java.lang.String a0, com.mojang.serialization.Codec a1) {
        throw new UnsupportedOperationException("CenturyBridge: RecipeSerializer registration using raw Codec is no longer supported; use MapCodec instead");
    }

    /** was net/minecraft/class_3984.method_17997(Lcom/mojang/datafixers/schemas/Schema;)Lcom/mojang/datafixers/types/templates/TypeTemplate; */
    public static com.mojang.datafixers.types.templates.TypeTemplate method_17997(net.minecraft.class_3984 self, com.mojang.datafixers.schemas.Schema a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_17997 was removed in 1.20.5 with no direct replacement");
    }

    /** was net/minecraft/class_39.field_16216Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_16216() {
        return net.minecraft.class_39.field_16216.method_29177();
    }

    /** was net/minecraft/class_39.field_16593Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_16593() {
        return net.minecraft.class_39.field_16593.method_29177();
    }

    /** was net/minecraft/class_39.field_16748Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_16748() {
        return net.minecraft.class_39.field_16748.method_29177();
    }

    /** was net/minecraft/class_39.field_16749Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_16749() {
        return net.minecraft.class_39.field_16749.method_29177();
    }

    /** was net/minecraft/class_39.field_16750Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_16750() {
        return net.minecraft.class_39.field_16750.method_29177();
    }

    /** was net/minecraft/class_39.field_16751Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_16751() {
        return net.minecraft.class_39.field_16751.method_29177();
    }

    /** was net/minecraft/class_39.field_16752Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_16752() {
        return net.minecraft.class_39.field_16752.method_29177();
    }

    /** was net/minecraft/class_39.field_16753Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_16753() {
        return net.minecraft.class_39.field_16753.method_29177();
    }

    /** was net/minecraft/class_39.field_16754Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_16754() {
        return net.minecraft.class_39.field_16754.method_29177();
    }

    /** was net/minecraft/class_39.field_17009Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_17009() {
        return net.minecraft.class_39.field_17009.method_29177();
    }

    /** was net/minecraft/class_39.field_17010Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_17010() {
        return net.minecraft.class_39.field_17010.method_29177();
    }

    /** was net/minecraft/class_39.field_17011Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_17011() {
        return net.minecraft.class_39.field_17011.method_29177();
    }

    /** was net/minecraft/class_39.field_17012Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_17012() {
        return net.minecraft.class_39.field_17012.method_29177();
    }

    /** was net/minecraft/class_39.field_17107Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_17107() {
        return net.minecraft.class_39.field_17107.method_29177();
    }

    /** was net/minecraft/class_39.field_17108Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_17108() {
        return net.minecraft.class_39.field_17108.method_29177();
    }

    /** was net/minecraft/class_39.field_17109Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_17109() {
        return net.minecraft.class_39.field_17109.method_29177();
    }

    /** was net/minecraft/class_39.field_18007Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_18007() {
        return net.minecraft.class_39.field_18007.method_29177();
    }

    /** was net/minecraft/class_39.field_19062Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_19062() {
        return net.minecraft.class_39.field_19062.method_29177();
    }

    /** was net/minecraft/class_39.field_19063Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_19063() {
        return net.minecraft.class_39.field_19063.method_29177();
    }

    /** was net/minecraft/class_39.field_19064Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_19064() {
        return net.minecraft.class_39.field_19064.method_29177();
    }

    /** was net/minecraft/class_39.field_19065Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_19065() {
        return net.minecraft.class_39.field_19065.method_29177();
    }

    /** was net/minecraft/class_39.field_19066Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_19066() {
        return net.minecraft.class_39.field_19066.method_29177();
    }

    /** was net/minecraft/class_39.field_19067Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_19067() {
        return net.minecraft.class_39.field_19067.method_29177();
    }

    /** was net/minecraft/class_39.field_19068Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_19068() {
        return net.minecraft.class_39.field_19068.method_29177();
    }

    /** was net/minecraft/class_39.field_19069Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_19069() {
        return net.minecraft.class_39.field_19069.method_29177();
    }

    /** was net/minecraft/class_39.field_19070Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_19070() {
        return net.minecraft.class_39.field_19070.method_29177();
    }

    /** was net/minecraft/class_39.field_19071Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_19071() {
        return net.minecraft.class_39.field_19071.method_29177();
    }

    /** was net/minecraft/class_39.field_19072Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_19072() {
        return net.minecraft.class_39.field_19072.method_29177();
    }

    /** was net/minecraft/class_39.field_19073Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_19073() {
        return net.minecraft.class_39.field_19073.method_29177();
    }

    /** was net/minecraft/class_39.field_19074Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_19074() {
        return net.minecraft.class_39.field_19074.method_29177();
    }

    /** was net/minecraft/class_39.field_22402Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22402() {
        return net.minecraft.class_39.field_22402.method_29177();
    }

    /** was net/minecraft/class_39.field_24046Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_24046() {
        return net.minecraft.class_39.field_24046.method_29177();
    }

    /** was net/minecraft/class_39.field_24047Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_24047() {
        return net.minecraft.class_39.field_24047.method_29177();
    }

    /** was net/minecraft/class_39.field_24048Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_24048() {
        return net.minecraft.class_39.field_24048.method_29177();
    }

    /** was net/minecraft/class_39.field_24049Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_24049() {
        return net.minecraft.class_39.field_24049.method_29177();
    }

    /** was net/minecraft/class_39.field_24050Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_24050() {
        return net.minecraft.class_39.field_24050.method_29177();
    }

    /** was net/minecraft/class_39.field_251Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_251() {
        return net.minecraft.class_39.field_251.method_29177();
    }

    /** was net/minecraft/class_39.field_266Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_266() {
        return net.minecraft.class_39.field_266.method_29177();
    }

    /** was net/minecraft/class_39.field_274Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_274() {
        return net.minecraft.class_39.field_274.method_29177();
    }

    /** was net/minecraft/class_39.field_300Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_300() {
        return net.minecraft.class_39.field_300.method_29177();
    }

    /** was net/minecraft/class_39.field_353Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_353() {
        return net.minecraft.class_39.field_353.method_29177();
    }

    /** was net/minecraft/class_39.field_356Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_356() {
        return net.minecraft.class_39.field_356.method_29177();
    }

    /** was net/minecraft/class_39.field_38438Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_38438() {
        return net.minecraft.class_39.field_38438.method_29177();
    }

    /** was net/minecraft/class_39.field_38439Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_38439() {
        return net.minecraft.class_39.field_38439.method_29177();
    }

    /** was net/minecraft/class_39.field_397Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_397() {
        return net.minecraft.class_39.field_397.method_29177();
    }

    /** was net/minecraft/class_39.field_43353Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_43353() {
        return net.minecraft.class_39.field_43353.method_29177();
    }

    /** was net/minecraft/class_39.field_43354Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_43354() {
        return net.minecraft.class_39.field_43354.method_29177();
    }

    /** was net/minecraft/class_39.field_43356Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_43356() {
        return net.minecraft.class_39.field_43356.method_29177();
    }

    /** was net/minecraft/class_39.field_43357Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_43357() {
        return net.minecraft.class_39.field_43357.method_29177();
    }

    /** was net/minecraft/class_39.field_434Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_434() {
        return net.minecraft.class_39.field_434.method_29177();
    }

    /** was net/minecraft/class_39.field_44648Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_44648() {
        return net.minecraft.class_39.field_44648.method_29177();
    }

    /** was net/minecraft/class_39.field_44649Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_44649() {
        return net.minecraft.class_39.field_44649.method_29177();
    }

    /** was net/minecraft/class_39.field_44748Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_44748() {
        return net.minecraft.class_39.field_44748.method_29177();
    }

    /** was net/minecraft/class_39.field_472Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_472() {
        return net.minecraft.class_39.field_472.method_29177();
    }

    /** was net/minecraft/class_39.field_47415Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_47415() {
        return net.minecraft.class_39.field_47415.method_29177();
    }

    /** was net/minecraft/class_39.field_47416Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_47416() {
        return net.minecraft.class_39.field_47416.method_41185();
    }

    /** was net/minecraft/class_39.field_47417Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_47417() {
        return net.minecraft.class_39.field_47417.method_29177();
    }

    /** was net/minecraft/class_39.field_47418Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_47418() {
        return net.minecraft.class_39.field_47418.method_29177();
    }

    /** was net/minecraft/class_39.field_47419Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_47419() {
        return net.minecraft.class_39.field_47419.method_29177();
    }

    /** was net/minecraft/class_39.field_47420Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_47420() {
        return net.minecraft.class_39.field_47420.method_41185();
    }

    /** was net/minecraft/class_39.field_47421Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_47421() {
        return net.minecraft.class_39.field_47421.method_29177();
    }

    /** was net/minecraft/class_39.field_47422Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_47422() {
        return net.minecraft.class_39.field_47422.method_29177();
    }

    /** was net/minecraft/class_39.field_47423Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_47423() {
        return net.minecraft.class_39.field_47423.method_29177();
    }

    /** was net/minecraft/class_39.field_47424Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_47424() {
        return net.minecraft.class_39.field_47424.method_29177();
    }

    /** was net/minecraft/class_39.field_47425Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_47425() {
        return net.minecraft.class_39.field_47425.method_29177();
    }

    /** was net/minecraft/class_39.field_47426Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_47426() {
        return net.minecraft.class_39.field_47426.method_29177();
    }

    /** was net/minecraft/class_39.field_484Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_484() {
        return net.minecraft.class_39.field_484.method_29177();
    }

    /** was net/minecraft/class_39.field_615Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_615() {
        return net.minecraft.class_39.field_615.method_29177();
    }

    /** was net/minecraft/class_39.field_662Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_662() {
        return net.minecraft.class_39.field_662.method_29177();
    }

    /** was net/minecraft/class_39.field_665Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_665() {
        return net.minecraft.class_39.field_665.method_29177();
        // CB-CONFIDENCY: high -- field_665 changed from Identifier to RegistryKey; method_29177() on class_5321 returns the Identifier (class_2960), preserving the old value exactly.
    }

    /** was net/minecraft/class_39.field_683Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_683() {
        return net.minecraft.class_39.field_683.method_29177();
    }

    /** was net/minecraft/class_39.field_751Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_751() {
        return net.minecraft.class_39.field_751.method_29177();
    }

    /** was net/minecraft/class_39.field_795Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_795() {
        return net.minecraft.class_39.field_795.method_41185();
    }

    /** was net/minecraft/class_39.field_800Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_800() {
        return net.minecraft.class_39.field_800.method_29177();
    }

    /** was net/minecraft/class_39.field_803Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_803() {
        return net.minecraft.class_39.field_803.method_29177();
    }

    /** was net/minecraft/class_39.field_841Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_841() {
        return net.minecraft.class_39.field_841.method_29177();
    }

    /** was net/minecraft/class_39.field_842Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_842() {
        return net.minecraft.class_39.field_842.method_29177();
    }

    /** was net/minecraft/class_39.field_850Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_850() {
        return net.minecraft.class_39.field_850.method_41185();
    }

    /** was net/minecraft/class_39.field_854Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_854() {
        return net.minecraft.class_39.field_854.method_41185();
    }

    /** was net/minecraft/class_39.field_880Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_880() {
        return net.minecraft.class_39.field_880.method_29177();
    }

    /** was net/minecraft/class_39.field_885Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_885() {
        return net.minecraft.class_39.field_885.method_29177();
    }

    /** was net/minecraft/class_4019$class_4024.field_17973Lnet/minecraft/class_4019; */
    public static net.minecraft.class_4019 field_17973() {
        throw new UnsupportedOperationException("CenturyBridge: field_17973 (class_4019 constant on class_4019$class_4024) was deleted with no direct replacement and its value cannot be reconstructed from the new API surface");
        // CB-CONFIDENCY: high -- the field is explicitly marked deleted with no direct replacement, and the new API surface provides no equivalent constant or accessor to reconstruct the held class_4019 value
    }

    /** was net/minecraft/class_4019$class_4052.field_18103Lnet/minecraft/class_4019; */
    public static net.minecraft.class_4019 field_18103() {
        throw new UnsupportedOperationException("CenturyBridge: field_18103 (static class_4019 on class_4019$class_4052) was deleted in 1.20.5 with no replacement; the new class exposes no method returning class_4019");
    }

    /** was net/minecraft/class_4208.method_19442()Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 method_19442(net.minecraft.class_4208 self) {
        return self.comp_2207();
    }

    /** was net/minecraft/class_4208.method_19446()Lnet/minecraft/class_2338; */
    public static net.minecraft.class_2338 method_19446(net.minecraft.class_4208 self) {
        return self.comp_2208();
    }

    /** was net/minecraft/class_4381.field_19747I */
    public static int field_19747() {
        throw new UnsupportedOperationException("CenturyBridge: class_4381.field_19747 was a deleted static int constant with no direct replacement in 1.20.5; its value cannot be inferred from the new API surface");
        // CB-CONFIDENCE: medium -- the field is genuinely gone with no equivalent, but a tombstone is the honest choice since the original constant's value is not recoverable from the new API surface
    }

    /** was net/minecraft/class_4406.field_19970I */
    public static int field_19970() {
        throw new UnsupportedOperationException("CenturyBridge: class_4406.field_19970 was a mutable per-instance animation tick counter on EnchantmentScreen, removed in 1.20.5; no static constant equivalent exists");
        // CB-CONFIDENCE: high -- field_19970 was mutable instance state (tick counter incremented in aM_), not a constant; a static shim with no receiver cannot reproduce per-instance mutable state
    }

    /** was net/minecraft/class_4406.field_26498Lnet/minecraft/class_2561; */
    public static net.minecraft.class_2561 field_26498() {
        throw new UnsupportedOperationException("CenturyBridge: field_26498 was deleted in 1.20.5 with no direct replacement");
        // CB-CONFIDENCE: high -- the prompt explicitly states "deleted -- no direct replacement"; field_44908 is an instance field of the same type but cannot be confirmed as the semantic equivalent of the old static field
    }

    /** was net/minecraft/class_4406.field_26500Lnet/minecraft/class_2561; */
    public static net.minecraft.class_2561 field_26500() {
        throw new UnsupportedOperationException("CenturyBridge: field_26500 was deleted in 1.20.5 with no direct replacement");
        // CB-CONFIDENCE: high -- field explicitly deleted, no equivalent in new API surface; tombstone is the correct choice
    }

    /** was net/minecraft/class_4514.method_22152()Ljava/lang/String; */
    public static java.lang.String method_22152(net.minecraft.class_4514 self) {
        return self.comp_2209();
    }

    /** was net/minecraft/class_4514.method_22153(Lnet/minecraft/class_3218;)V */
    public static void method_22153(net.minecraft.class_4514 self, net.minecraft.class_3218 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_22153(ServerWorld) was deleted in 1.20.5 with no direct replacement; class_4514 is now a record (String, Collection, Consumer, Consumer) with no ServerWorld-interacting methods");
    }

    /** was net/minecraft/class_4514.method_22154()Ljava/util/Collection; */
    public static java.util.Collection method_22154(net.minecraft.class_4514 self) {
        return self.comp_2210();
    }

    public void method_33317(net.minecraft.class_4517 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_33317 now requires a class_4520 raid-context parameter whose constructor needs server world, collections, and nested types unavailable from class_4517 alone");
    }

    /** was net/minecraft/class_4645.field_24926Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24926() {
        return net.minecraft.class_4645.field_24926.codec();
    }

    /** was net/minecraft/class_4646.field_24927Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24927() {
        return net.minecraft.class_4646.field_24927.codec();
    }

    /** was net/minecraft/class_4649.field_24935Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24935() {
        return net.minecraft.class_4649.field_24935.codec();
    }

    /** was net/minecraft/class_4650.field_24936Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24936() {
        return net.minecraft.class_4650.field_24936.codec();
    }

    /** was net/minecraft/class_4655.field_24944Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24944() {
        return net.minecraft.class_4655.field_24944.codec();
    }

    /** was net/minecraft/class_4656.field_24945Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24945() {
        return net.minecraft.class_4656.field_24945.codec();
    }

    /** was net/minecraft/class_4660.field_24959Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24959() {
        return net.minecraft.class_4660.field_24959.codec();
    }

    /** was net/minecraft/class_4722.field_28253Ljava/util/Map; */
    public static java.util.Map field_28253() {
        throw new UnsupportedOperationException("CenturyBridge: field_28253 was a memoized render-layer cache map removed in 1.20.5; replaced by direct method calls (e.g. method_49341) with incompatible key types");
        // CB-CONFIDENCE: medium -- field was a Map on TexturedRenderLayers deleted with no replacement; the memoized-cache nature means it cannot be rebuilt as a constant, and the new API uses different key types (RegistryKey vs Identifier)
    }

    /** was net/minecraft/class_4766.field_24719Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24719() {
        return net.minecraft.class_4766.field_24719.codec();
    }

    /** was net/minecraft/class_4969$1.field_25406Lnet/minecraft/class_4969; */
    public static net.minecraft.class_4969 field_25406() {
        throw new UnsupportedOperationException("CenturyBridge: field_25406 was the synthetic outer instance reference (this$0) of class_4969$1, no longer mapped in 1.20.5; cannot reconstruct from a parameterless static shim without an instance");
    }

    /** was net/minecraft/class_4992.field_24995Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24995() {
        return net.minecraft.class_4992.field_24995.codec();
    }

    /** was net/minecraft/class_4993.field_25004Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25004() {
        return net.minecraft.class_4993.field_25004.codec();
    }

    /** was net/minecraft/class_5131.method_26842(Lnet/minecraft/class_1320;)Lnet/minecraft/class_1324; */
    public static net.minecraft.class_1324 method_26842(net.minecraft.class_5131 self, net.minecraft.class_1320 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_26842(ServerPlayerEntity) has no equivalent in 1.20.5; boss bar player management was reworked to use RegistryKey-based lookups (method_45329)");
    }

    /** was net/minecraft/class_5131.method_26854(Lcom/google/common/collect/Multimap;)V */
    public static void method_26854(net.minecraft.class_5131 self, com.google.common.collect.Multimap a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_26854(Multimap) was removed in 1.20.5; AttributeContainer no longer accepts a Multimap of modifiers -- the attribute modifier system was reworked to RegistryEntry-keyed queries with no bulk-apply equivalent");
    }

    /** was net/minecraft/class_5134.field_23716Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_23716() {
        return (net.minecraft.class_1320) net.minecraft.class_5134.field_23716.comp_349();
    }

    /** was net/minecraft/class_5134.field_23718Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_23718() {
        return (net.minecraft.class_1320) net.minecraft.class_5134.field_23718.comp_349();
    }

    /** was net/minecraft/class_5134.field_23719Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_23719() {
        return (net.minecraft.class_1320) net.minecraft.class_5134.field_23719.comp_349();
        // CB-CONFIDENCE: high -- field_23719 is now a RegistryEntry<EntityAttribute>; comp_349() unwraps to the value, which is the old EntityAttribute
    }

    /** was net/minecraft/class_5134.field_23720Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_23720() {
        return (net.minecraft.class_1320) net.minecraft.class_5134.field_23720.comp_349();
    }

    /** was net/minecraft/class_5134.field_23723Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_23723() {
        return (net.minecraft.class_1320) net.minecraft.class_5134.field_23723.comp_349();
    }

    /** was net/minecraft/class_5134.field_23724Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_23724() {
        return (net.minecraft.class_1320) net.minecraft.class_5134.field_23724.comp_349();
    }

    /** was net/minecraft/class_5134.field_23725Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_23725() {
        return (net.minecraft.class_1320) net.minecraft.class_5134.field_23725.comp_349();
    }

    /** was net/minecraft/class_5134.field_23727Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_23727() {
        return (net.minecraft.class_1320) net.minecraft.class_5134.field_23727.comp_349();
    }

    /** was net/minecraft/class_5134.field_23728Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_23728() {
        return (net.minecraft.class_1320) net.minecraft.class_5134.field_23728.comp_349();
    }

    /** was net/minecraft/class_5134.field_45124Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_45124() {
        return (net.minecraft.class_1320) net.minecraft.class_5134.field_45124.comp_349();
    }

    /** was net/minecraft/class_5139.field_24968Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24968() {
        return net.minecraft.class_5139.field_24968.codec();
    }

    /** was net/minecraft/class_5140.field_24971Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24971() {
        return net.minecraft.class_5140.field_24971.codec();
    }

    /** was net/minecraft/class_5183.field_37812Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37812() {
        return net.minecraft.class_5183.field_37812.codec();
    }

    /** was net/minecraft/class_5192.field_24996Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24996() {
        return net.minecraft.class_5192.field_24996.codec();
    }

    /** was net/minecraft/class_5193.field_24997Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24997() {
        return net.minecraft.class_5193.field_24997.codec();
    }

    /** was net/minecraft/class_5203.field_24924Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24924() {
        return net.minecraft.class_5203.field_24924.codec();
    }

    /** was net/minecraft/class_5204.field_24925Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24925() {
        return net.minecraft.class_5204.field_24925.codec();
    }

    /** was net/minecraft/class_5206.field_24929Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24929() {
        return net.minecraft.class_5206.field_24929.codec();
    }

    /** was net/minecraft/class_5207.field_24930Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24930() {
        return net.minecraft.class_5207.field_24930.codec();
    }

    /** was net/minecraft/class_5209.field_24933Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24933() {
        return net.minecraft.class_5209.field_24933.codec();
    }

    /** was net/minecraft/class_5211.field_24966Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24966() {
        return net.minecraft.class_5211.field_24966.codec();
    }

    /** was net/minecraft/class_5212.field_24967Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24967() {
        return net.minecraft.class_5212.field_24967.codec();
    }

    /** was net/minecraft/class_5214.field_24969Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24969() {
        return net.minecraft.class_5214.field_24969.codec();
    }

    /** was net/minecraft/class_5217.method_144()I */
    public static int method_144(net.minecraft.class_5217 self) {
        throw new UnsupportedOperationException("CenturyBridge: getSeaLevel (method_144) was removed from class_5217 in 1.20.5 with no direct replacement; sea level is no longer exposed on WorldProperties");
    }

    /** was net/minecraft/class_5269.method_27416(I)V */
    public static void method_27416(net.minecraft.class_5269 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_27416(int) has no 1.20.5 equivalent; the only remaining method method_187 requires a BlockPos and float that cannot be derived from the int argument");
    }

    /** was net/minecraft/class_5269.method_27417(I)V */
    public static void method_27417(net.minecraft.class_5269 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_27417(int) was removed in 1.20.5 with no direct replacement; remaining method_187(BlockPos, float) is semantically incompatible -- cannot derive BlockPos+float from a single int");
    }

    /** was net/minecraft/class_5269.method_27419(I)V */
    public static void method_27419(net.minecraft.class_5269 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_27419(int) was deleted with no direct replacement; the only remaining method_187 requires a BlockPos and float, neither of which can be derived from the single int argument, so the old contract cannot be faithfully expressed");
    }

    /** was net/minecraft/class_5289$1.field_24574[I */
    public static int[] field_24574() {
        throw new UnsupportedOperationException("CenturyBridge: field_24574 was deleted in 1.20.5 with no direct replacement; field_24575 is a distinct field (stable intermediary names), so the original constant value cannot be faithfully reconstructed");
    }

    /** was net/minecraft/class_5399.field_25618Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25618() {
        return net.minecraft.class_5399.field_25618.codec();
    }

    /** was net/minecraft/class_5450.field_35727Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35727() {
        return net.minecraft.class_5450.field_35727.codec();
    }

    /** was net/minecraft/class_5452.field_35716Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35716() {
        return net.minecraft.class_5452.field_35716.codec();
    }

    /** was net/minecraft/class_5455$class_7781.field_40586Lnet/minecraft/class_5455; */
    public static net.minecraft.class_5455 field_40586() {
        throw new UnsupportedOperationException("CenturyBridge: class_5455$class_7781.field_40586 (a default class_5455 instance) was removed in 1.20.5 with no direct replacement; the new class only exposes a constructor requiring an existing class_5455, so the old singleton cannot be faithfully reconstructed");
    }

    /** was net/minecraft/class_5537.field_30857I */
    public static int field_30857() {
        return 3;
    }

    /** was net/minecraft/class_5641.field_45834Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_45834() {
        return net.minecraft.class_5641.field_45835;
    }

    /** was net/minecraft/class_5707.field_28137Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_28137() {
        return net.minecraft.class_5707.field_28137.codec();
    }

    /** was net/minecraft/class_5707.field_28138Lnet/minecraft/class_2338; */
    public static net.minecraft.class_2338 field_28138() {
        return new net.minecraft.class_2338(0, 0, 0);
    }

    public boolean method_32943(net.minecraft.class_5712 a0, net.minecraft.class_243 a1, net.minecraft.class_5712.class_7397 a2, net.minecraft.class_5713.class_7721 a3) {
        return ((net.minecraft.class_5713) (Object) this).method_32943(net.minecraft.class_6880.method_40223(a0), a1, a2, a3);
    }

    public boolean method_32947(net.minecraft.class_3218 a0, net.minecraft.class_5712 a1, net.minecraft.class_5712.class_7397 a2, net.minecraft.class_243 a3) {
        return ((net.minecraft.class_5714)(Object)this).method_32947(a0, net.minecraft.class_6880.method_40223(a1), a2, a3);
    }

    /** was net/minecraft/class_5717.method_32960(Lnet/minecraft/class_2540;Lnet/minecraft/class_5716;)V */
    public static void method_32960(net.minecraft.class_5717 self, net.minecraft.class_2540 a0, net.minecraft.class_5716 a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_32960 wrote TextContent to PacketByteBuf in 1.20.1's binary format; 1.20.5 replaced this with MapCodec serialization (method_32957) using a different on-wire format");
    }

    /** was net/minecraft/class_5861.field_28998Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_28998() {
        return net.minecraft.class_5861.field_28998.codec();
    }

    /** was net/minecraft/class_5862.field_29004Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_29004() {
        return net.minecraft.class_5862.field_29004.codec();
    }

    /** was net/minecraft/class_5866.field_29016Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_29016() {
        return net.minecraft.class_5866.field_29016.codec();
    }

    /** was net/minecraft/class_5925.field_29265Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_29265() {
        return net.minecraft.class_5925.field_29265.codec();
    }

    /** was net/minecraft/class_5928.field_29297Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_29297() {
        return net.minecraft.class_5928.field_29297.codec();
    }

    /** was net/minecraft/class_5930.field_29306Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_29306() {
        return net.minecraft.class_5930.field_29306.codec();
    }

    /** was net/minecraft/class_5934.field_29323Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_29323() {
        return net.minecraft.class_5934.field_29323.codec();
    }

    /** was net/minecraft/class_6008$class_6010.method_34983()Ljava/lang/Object; */
    public static java.lang.Object method_34983(net.minecraft.class_6008.class_6010 self) {
        return self.comp_2542();
    }

    /** was net/minecraft/class_6016.field_29943Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_29943() {
        return net.minecraft.class_6016.field_29943.codec();
    }

    /** was net/minecraft/class_6019.field_29949Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_29949() {
        return net.minecraft.class_6019.field_29949.codec();
    }

    /** was net/minecraft/class_6088.field_47339I */
    public static int field_47339() {
        throw new UnsupportedOperationException("CenturyBridge: field_47339 was deleted in 1.20.5 with no direct replacement; its int value cannot be reconstructed from the new API surface");
    }

    /** was net/minecraft/class_6120.field_31531Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_31531() {
        return net.minecraft.class_6120.field_31531.codec();
    }

    /** was net/minecraft/class_6306$1.field_33163Lnet/minecraft/class_6306; */
    public static net.minecraft.class_6306 field_33163() {
        throw new UnsupportedOperationException("CenturyBridge: field_33163 (type class_6306) in class_6306$1 was deleted in 1.20.5 with no direct replacement; cannot reconstruct a class_6306 constant without constructor or factory information for the outer type");
    }

    /** was net/minecraft/class_6333.field_33443Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_33443() {
        return net.minecraft.class_6333.field_33443.codec();
    }

    /** was net/minecraft/class_6334.field_33446Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_33446() {
        return net.minecraft.class_6334.field_33446.codec();
    }

    /** was net/minecraft/class_6342.field_33522Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_33522() {
        return net.minecraft.class_6342.field_33522.codec();
    }

    /** was net/minecraft/class_6343.field_33527Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_33527() {
        return net.minecraft.class_6343.field_33527.codec();
    }

    /** was net/minecraft/class_6581.field_34713Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_34713() {
        return net.minecraft.class_6581.field_34713.codec();
    }

    /** was net/minecraft/class_6584.field_34721Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_34721() {
        return net.minecraft.class_6584.field_34721.codec();
    }

    /** was net/minecraft/class_6658.field_35075Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35075() {
        return net.minecraft.class_6658.field_35075.codec();
    }

    /** was net/minecraft/class_6681.field_35160Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35160() {
        return net.minecraft.class_6681.field_35160.codec();
    }

    /** was net/minecraft/class_6724$1.field_35297Lnet/minecraft/class_6724; */
    public static net.minecraft.class_6724 field_35297() {
        throw new UnsupportedOperationException("CenturyBridge: field_35297 was the synthetic this$0 outer instance reference of class_6724$1; it is an instance field and cannot be returned from a parameterless static helper");
    }

    /** was net/minecraft/class_6728.field_35352Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35352() {
        return net.minecraft.class_6728.field_35352.codec();
    }

    /** was net/minecraft/class_6732.field_35419Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35419() {
        return net.minecraft.class_6732.field_35419.codec();
    }

    /** was net/minecraft/class_6791.field_35712Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35712() {
        return net.minecraft.class_6791.field_35712.codec();
    }

    /** was net/minecraft/class_6792.field_35714Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35714() {
        return net.minecraft.class_6792.field_35714.codec();
    }

    /** was net/minecraft/class_6793.field_35718Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35718() {
        return net.minecraft.class_6793.field_35718.codec();
    }

    /** was net/minecraft/class_6794.field_35720Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35720() {
        return net.minecraft.class_6794.field_35720.codec();
    }

    /** was net/minecraft/class_6799.field_35752Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35752() {
        return net.minecraft.class_6799.field_35752.codec();
    }

    /** was net/minecraft/class_6872.field_36420Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_36420() {
        return net.minecraft.class_6872.field_36420.codec();
    }

    /** was net/minecraft/class_7106.field_37462Lnet/minecraft/class_7106; */
    public static net.minecraft.class_7106 field_37462() {
        return new net.minecraft.class_7106(net.minecraft.class_7106.field_37462.method_29177());
        // CB-CONFIDENCE: medium -- reconstructs the old singleton from the new RegistryKey's value identifier; faithful if equality is identifier-based (likely, given comp_707 + equals/hashCode), but not identity-identical to the original 1.20.1 instance
    }

    /** was net/minecraft/class_7106.field_37463Lnet/minecraft/class_7106; */
    public static net.minecraft.class_7106 field_37463() {
        return new net.minecraft.class_7106(net.minecraft.class_7106.field_37463.method_29177());
    }

    /** was net/minecraft/class_7106.field_37464Lnet/minecraft/class_7106; */
    public static net.minecraft.class_7106 field_37464() {
        return new net.minecraft.class_7106(net.minecraft.class_7106.field_37464.method_29177());
    }

    /** was net/minecraft/class_7138$1.field_40361Lnet/minecraft/class_7138; */
    public static net.minecraft.class_7138 field_40361() {
        throw new UnsupportedOperationException("CenturyBridge: field_40361 was an instance field holding the enclosing class_7138 reference; cannot reconstruct from a static context with no receiver");
    }

    /** was net/minecraft/class_7260$1.field_39306Lnet/minecraft/class_7260; */
    public static net.minecraft.class_7260 field_39306() {
        return null;
    }

    public net.minecraft.class_2960 method_42276() {
        return ((net.minecraft.class_7265) (Object) this).method_42276().method_29177();
    }

    public void method_42285(net.minecraft.class_2487 a0) {
        net.minecraft.class_1937 world = ((net.minecraft.class_7265)(Object)this).method_37908();
        ((net.minecraft.class_7265)(Object)this).method_42285(a0, (net.minecraft.class_7225.class_7874)world);
    }

    public void method_42288(net.minecraft.class_2487 a0) {
        ((net.minecraft.class_7265)(Object)this).method_42288(a0, (net.minecraft.class_7225.class_7874)((net.minecraft.class_7265)(Object)this).method_37908());
    }

    /** was net/minecraft/class_7294.method_42645(Lcom/mojang/datafixers/schemas/Schema;Ljava/util/Map;Ljava/lang/String;)V */
    public static void method_42645(net.minecraft.class_7294 self, com.mojang.datafixers.schemas.Schema a0, java.util.Map a1, java.lang.String a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_42645(Schema, Map, String) was a per-name type registration helper deleted in 1.20.5 with no direct replacement; new class_7294 only exposes bulk registerEntities(Schema) returning a new Map, with no per-name registration equivalent");
    }

    /** was net/minecraft/class_7389.field_38783Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_38783() {
        return net.minecraft.class_7389.field_38783.codec();
    }

    /** was net/minecraft/class_7390.field_38793Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_38793() {
        return net.minecraft.class_7390.field_38793.codec();
    }

    /** was net/minecraft/class_7450$class_7599.comp_914Lnet/minecraft/class_7469; */
    public static net.minecraft.class_7469 comp_914() {
        throw new UnsupportedOperationException("CenturyBridge: comp_914 was a static field on class_7450$class_7599, deleted in 1.20.5; new comp_914() is an instance method with no static equivalent");
    }

    /** was net/minecraft/class_757.field_3996[Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960[] field_3996() {
        throw new UnsupportedOperationException("CenturyBridge: field_3996 (SHADERS_LOCATIONS) was removed in 1.20.5 when the core shader system was reworked; the new API provides individual class_5944 shader accessors but no equivalent identifier array");
    }

    /** was net/minecraft/class_7689.field_40131Lnet/minecraft/class_1856; */
    public static net.minecraft.class_1856 field_40131() {
        throw new UnsupportedOperationException("CenturyBridge: field_40131 (Ingredient) was deleted in 1.20.5 with no direct replacement; cannot reconstruct the allay duplication ingredient as a static constant");
        // CB-CONFIDENCE: high -- the field is explicitly deleted with no direct replacement, and the new API surface contains no Ingredient field or factory to rebuild it from
    }

    /** was net/minecraft/class_7708.method_47573(Lnet/minecraft/class_1799;)I */
    public static int method_47573(net.minecraft.class_7708 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_7708.method_47573(ItemStack) was removed in 1.20.5 with no direct replacement; the remaining API (method_47572 returning Set) cannot query a specific item's cooldown value");
    }

    /** was net/minecraft/class_7898$class_7900$1.field_41004Lnet/minecraft/class_7898$class_7900; */
    public static net.minecraft.class_7898.class_7900 field_41004() {
        throw new UnsupportedOperationException("CenturyBridge: field_41004 was the synthetic this$0 enclosing instance reference in class_7898$class_7900$1; it is an instance field holding the enclosing class_7898$class_7900 and cannot be reconstructed as a static constant");
    }

    /** was net/minecraft/class_7898$class_7900$3.field_41011Lnet/minecraft/class_7898$class_7900; */
    public static net.minecraft.class_7898.class_7900 field_41011() {
        throw new UnsupportedOperationException("CenturyBridge: field_41011 was the synthetic this$0 reference to the enclosing class_7900 instance in anonymous class class_7898$class_7900$3; it is per-instance and cannot be reconstructed as a static constant");
    }

    /** was net/minecraft/class_7923.field_41165Lnet/minecraft/class_2378; */
    public static net.minecraft.class_2378 field_41165() {
        throw new UnsupportedOperationException("CenturyBridge: Registries.field_41165 was removed in 1.20.5 with no direct replacement");
    }

    /** was net/minecraft/class_8178.field_42841Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_42841() {
        return net.minecraft.class_8178.field_42841.codec();
    }

    /** was net/minecraft/class_8180.field_42848Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_42848() {
        return net.minecraft.class_8180.field_42848.codec();
    }

    public net.minecraft.class_2586 method_54080() {
        return ((net.minecraft.class_8181.class_9210) (Object) this).method_54080();
    }

    /** was net/minecraft/class_8243.field_43329Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_43329() {
        return net.minecraft.class_8243.field_43329.codec();
    }

    /** was net/minecraft/class_8245.field_43339Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_43339() {
        return net.minecraft.class_8245.field_43339.codec();
    }

    /** was net/minecraft/class_8246.field_43341Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_43341() {
        return net.minecraft.class_8246.field_43341.codec();
    }

    /** was net/minecraft/class_8247.field_43344Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_43344() {
        return net.minecraft.class_8247.field_43344.codec();
    }

    public void method_32969(net.minecraft.class_3218 a0, net.minecraft.class_2338 a1, net.minecraft.class_5712 a2, net.minecraft.class_1297 a3, net.minecraft.class_1297 a4, float a5) {
        ((net.minecraft.class_8514.class_5719) (Object) this).method_32969(a0, a1, net.minecraft.class_6880.method_40223(a2), a3, a4, a5);
    }

    /** was net/minecraft/class_8514.field_44638[Lnet/minecraft/class_5712; */
    public static net.minecraft.class_5712[] field_44638() {
        return net.minecraft.class_8514.field_44638.toArray(new net.minecraft.class_5712[0]);
    }

    /** was net/minecraft/class_8700.method_53011(Lio/netty/util/Attribute;Lnet/minecraft/class_2596;)V */
    public static void method_53011(net.minecraft.class_8700 self, io.netty.util.Attribute a0, net.minecraft.class_2596 a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_53011(Attribute, Packet) was deleted; new API methods require ChannelHandlerContext which cannot be derived from an io.netty.util.Attribute");
    }

    /** was net/minecraft/class_8710.method_53028(Lnet/minecraft/class_2540;)V */
    public static void method_53028(net.minecraft.class_8710 self, net.minecraft.class_2540 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_8710.method_53028(PacketByteBuf) was deleted in 1.20.5 with no direct replacement; the new class_8710 API exposes no PacketByteBuf serialization path");
    }

    public net.minecraft.class_2960 method_54869() {
        return ((net.minecraft.class_8934) (Object) this).method_54869().method_29177();
    }

    /** was net/minecraft/class_8949.method_55006()Lnet/minecraft/class_8949; */
    public static net.minecraft.class_8949 method_55006(net.minecraft.class_8949 self) {
        return self.method_55015();
    }

    /** was net/minecraft/class_8949.method_55011(Lnet/minecraft/class_243;)Z */
    public static boolean method_55011(net.minecraft.class_8949 self, net.minecraft.class_243 a0) {
        return self.method_55013(a0);
    }

    /** was net/minecraft/class_8949.method_55012(Lnet/minecraft/class_243;)Z */
    public static boolean method_55012(net.minecraft.class_8949 self, net.minecraft.class_243 a0) {
        return self.method_55013(a0);
    }

    /** was net/minecraft/class_8962.field_47356Lnet/minecraft/class_8962; */
    public static net.minecraft.class_8962 field_47356() {
        throw new UnsupportedOperationException("CenturyBridge: class_8962.field_47356 was deleted in 1.20.5; remaining constants field_47357/field_48860/field_48861 are not equivalent");
    }

    /** was net/minecraft/class_8966.field_47378Lnet/minecraft/class_6005; */
    public static net.minecraft.class_6005 field_47378() {
        throw new UnsupportedOperationException("CenturyBridge: field_47378 (class_6005) was deleted in 1.20.5 with no direct replacement; cannot reconstruct the original constant value without class_6005's API surface");
    }

    /** was net/minecraft/class_8.field_20622Lnet/minecraft/class_1950; */
    public static net.minecraft.class_1950 field_20622() {
        throw new UnsupportedOperationException("CenturyBridge: field_20622 (class_1950 HitResult) was deleted from class_8 with no direct replacement; it was a mutable instance field that cannot be reconstructed as a constant expression");
        // CB-CONFIDENCE: high -- the field was explicitly deleted with no replacement in the new API surface, and a HitResult is a mutable runtime object that cannot be expressed as a constant
    }

    /** was net/minecraft/class_9024.method_55467(Lnet/minecraft/class_2540;)Lnet/minecraft/class_9022; */
    public static net.minecraft.class_9022 method_55467(net.minecraft.class_9024 self, net.minecraft.class_2540 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_9024.method_55467(PacketByteBuf)->class_9022 was removed in 1.20.5 with no direct replacement; the new class_9024 API has no method accepting a PacketByteBuf, and the old instance-method semantics (using receiver state to drive deserialization) cannot be expressed via the static Codec/MapCodec fields");
    }

    /** was net/minecraft/class_1282.method_48790()Z */
    public static boolean method_48790(net.minecraft.class_1282 self) {
        return self.method_5526() == self.method_5529();
    }

    /** was net/minecraft/class_1297.field_5972I */
    public static int field_5972() {
        return 1024;
    }

    /** was net/minecraft/class_1297.field_5991Lnet/minecraft/class_2338; */
    public static net.minecraft.class_2338 field_5991() {
        return new net.minecraft.class_2338(0, 0, 0);
    }

    /** was net/minecraft/class_1297.method_20620(DDD)V */
    public static void method_20620(net.minecraft.class_1297 self, double a0, double a1, double a2) {
        self.method_18800(a0, a1, a2);
    }

    /** was net/minecraft/class_1297.method_30329(Lnet/minecraft/class_3218;)Lnet/minecraft/class_5454; */
    public static net.minecraft.class_5454 method_30329(net.minecraft.class_1297 self, net.minecraft.class_3218 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_30329 (getTeleportTarget(ServerWorld)) was removed in 1.21; TeleportTarget (class_5454) was reworked to Entity.TeleportTarget (class_1297$class_5799) via method_33570 with different semantics and no ServerWorld parameter");
    }

    /** was net/minecraft/class_1297.method_52208(ZLnet/minecraft/class_243;)V */
    public static void method_52208(net.minecraft.class_1297 self, boolean a0, net.minecraft.class_243 a1) {
        self.method_18799(a1);
        // Can't set velocityModified flag in new API
    }

    /** was net/minecraft/class_1297.method_5741()I */
    public static int method_5741(net.minecraft.class_1297 self) {
        return self.method_20802();
    }

    /** was net/minecraft/class_1304.method_20234(Lnet/minecraft/class_1304$class_1305;I)Lnet/minecraft/class_1304; */
    public static net.minecraft.class_1304 method_20234(net.minecraft.class_1304 self, net.minecraft.class_1304.class_1305 a0, int a1) {
        throw new UnsupportedOperationException("CenturyBridge: DyeColor.method_20234(DyeColorCode, int) was removed in 1.21 with no direct replacement; no remaining method accepts a DyeColorCode parameter");
    }

    /** was net/minecraft/class_1308.field_30086Ljava/lang/String; */
    public static java.lang.String field_30086() {
        return "Leash";
    }

    /** was net/minecraft/class_1308.method_18810(I)V */
    public static void method_18810(net.minecraft.class_1308 self, int a0) {
        self.method_5976((float) a0);
    }

    /** was net/minecraft/class_1309.method_27302()Z */
    public static boolean method_27302(net.minecraft.class_1309 self) {
        return self.method_29503();
    }

    /** was net/minecraft/class_1309.method_29502()V */
    public static void method_29502(net.minecraft.class_1309 self) {
        self.method_29505(null);
    }

    /** was net/minecraft/class_1492.field_30412I */
    public static int field_30412() {
        return 32767;
    }

    /** was net/minecraft/class_1530.method_6891()I */
    public static int method_6891(net.minecraft.class_1530 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_1530.method_6891() (getLifeTime) was removed in 1.21; the lifeTime field is no longer exposed on FireworkRocketEntity and no equivalent accessor exists in the new API surface");
    }

    /** was net/minecraft/class_155.field_29743Z */
    public static boolean field_29743() {
        throw new UnsupportedOperationException("CenturyBridge: class_155.field_29743 was a boolean flag deleted in 1.21 with no direct replacement; its original value cannot be inferred from the new API surface");
        // CB-CONFIDENCE: high -- the field is explicitly listed as deleted with no replacement, and returning a guessed boolean constant would risk silent behaviour corruption
    }

    /** was net/minecraft/class_155.field_29744Z */
    public static boolean field_29744() {
        throw new UnsupportedOperationException("CenturyBridge: class_155.field_29744 was removed in 1.21 with no direct replacement");
        // CB-CONFIDENCE: high -- the field is explicitly listed as deleted with no equivalent in the new API surface
    }

    /** was net/minecraft/class_155.field_43095Ljava/util/Set; */
    public static java.util.Set field_43095() {
        throw new UnsupportedOperationException("CenturyBridge: field_43095 was an instance Set<String> of enabled resource packs loaded from options.txt; removed in 1.21 with no static equivalent, use method_16673() on a GameOptions instance instead");
    }

    /** was net/minecraft/class_155.method_43250()V */
    public static void method_43250(net.minecraft.class_155 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_43250 was deleted in 1.21 with no direct replacement");
    }

    /** was net/minecraft/class_1657.field_49991Z */
    public static boolean field_49991() {
        return true;
        // CB-CONFIDENCE: low -- deleted static boolean constant with no direct replacement; value inferred as true from common feature-flag patterns but unverified
    }

    /** was net/minecraft/class_1657.method_26091(Lnet/minecraft/class_3218;Lnet/minecraft/class_2338;FZZ)Ljava/util/Optional; */
    public static java.util.Optional method_26091(net.minecraft.class_1657 self, net.minecraft.class_3218 a0, net.minecraft.class_2338 a1, float a2, boolean a3, boolean a4) {
        throw new UnsupportedOperationException("CenturyBridge: ServerPlayerEntity.trySleep(ServerWorld, BlockPos, float, boolean, boolean) (method_26091) was removed in 1.21; sleep handling was reworked and no direct equivalent exists on class_1657");
    }

    public net.minecraft.class_1799 method_8116(net.minecraft.class_1263 a0, net.minecraft.class_7225.class_7874 a1) {
        return ((net.minecraft.class_1860) (Object) this).method_8116((net.minecraft.class_9695) a0, a1);
    }

    /** was net/minecraft/class_1890.method_42304(Lnet/minecraft/class_1309;)F */
    public static float method_42304(net.minecraft.class_1890 self, net.minecraft.class_1309 a0) {
        return self.method_60123(a0.method_6047(), a0);
    }

    /** was net/minecraft/class_1890.method_8202(Lnet/minecraft/class_1799;)I */
    public static int method_8202(net.minecraft.class_1890 self, net.minecraft.class_1799 a0) {
        return 1;
    }

    /** was net/minecraft/class_1890.method_8217(Lnet/minecraft/class_1309;)F */
    public static float method_8217(net.minecraft.class_1890 self, net.minecraft.class_1309 a0) {
        return self.method_60123(a0.method_6047(), a0);
    }

    /** was net/minecraft/class_1890.method_8218(Lnet/minecraft/class_1799;Lnet/minecraft/class_1299;)F */
    public static float method_8218(net.minecraft.class_1890 self, net.minecraft.class_1799 a0, net.minecraft.class_1299 a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_8218(ItemStack, EntityType<?>) was replaced by method_60123(ItemStack, LivingEntity) which requires an entity instance, not an EntityType; cannot bridge without a LivingEntity");
    }

    /** was net/minecraft/class_1890.method_8221(Lnet/minecraft/class_1799;)Z */
    public static boolean method_8221(net.minecraft.class_1890 self, net.minecraft.class_1799 a0) {
        return self.method_57529(a0);
    }

    /** was net/minecraft/class_1893.field_23071Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_23071() {
        throw new UnsupportedOperationException("CenturyBridge: field_23071 changed from Enchantment (class_1887) to RegistryKey<Enchantment> (class_5321); resolving a registry key to an Enchantment instance requires runtime registry access unavailable in a static field context");
    }

    /** was net/minecraft/class_1893.field_50158Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_50158() {
        throw new UnsupportedOperationException("CenturyBridge: field_50158 is now a RegistryKey<Enchantment> (class_5321); the Enchantment instance (class_1887) is data-driven and only resolvable via the dynamic registry manager at runtime, not as a static constant");
    }

    /** was net/minecraft/class_1893.field_9095Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9095() {
        throw new UnsupportedOperationException("CenturyBridge: field_9095 changed from Enchantment (class_1887) to RegistryKey<Enchantment> (class_5321); enchantment instances require runtime registry lookup and are no longer available as static constants");
        // CB-CONFIDENCE: high -- the old field type (class_1887 Enchantment) is a runtime instance, the new field type (class_5321 RegistryKey) is only a lookup key; no static conversion exists without registry access
    }

    /** was net/minecraft/class_1893.field_9096Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9096() {
        throw new UnsupportedOperationException("CenturyBridge: field_9096 is now a RegistryKey (class_5321), not a Registry (class_1887); cannot reconstruct Registry from RegistryKey without runtime context");
    }

    /** was net/minecraft/class_1893.field_9097Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9097() {
        throw new UnsupportedOperationException("CenturyBridge: class_1887 (RecipeType) instances are no longer available as static fields; they require registry lookup via class_5321 (RegistryKey)");
    }

    /** was net/minecraft/class_1893.field_9099Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9099() {
        throw new UnsupportedOperationException("CenturyBridge: field_9099 changed from Registry (class_1887) to RegistryKey (class_5321); a static RegistryKey cannot yield a live Registry without a WrapperLookup");
    }

    /** was net/minecraft/class_1893.field_9101Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9101() {
        throw new UnsupportedOperationException("CenturyBridge: Enchantments.field_9101 changed from Enchantment (class_1887) to RegistryKey (class_5321) in 1.21; cannot resolve a RegistryKey to an Enchantment instance without dynamic registry access");
    }

    /** was net/minecraft/class_1893.field_9104Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9104() {
        throw new UnsupportedOperationException("CenturyBridge: Enchantments.field_9104 is now a RegistryKey<Enchantment>; resolving to Enchantment requires a dynamic registry lookup not available in a static context");
    }

    /** was net/minecraft/class_1893.field_9106Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9106() {
        throw new UnsupportedOperationException("CenturyBridge: Enchantments.field_9106 is now a RegistryKey<Enchantment> (class_5321); the Enchantment instance (class_1887) is no longer a static constant and must be resolved from the registry at runtime");
    }

    /** was net/minecraft/class_1893.field_9113Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9113() {
        throw new UnsupportedOperationException("CenturyBridge: class_1893.field_9113 changed from Enchantment (class_1887) to RegistryKey (class_5321); enchantment instances are no longer static constants and must be resolved from the registry at runtime");
    }

    /** was net/minecraft/class_1893.field_9115Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9115() {
        throw new UnsupportedOperationException("CenturyBridge: field_9115 changed from Enchantment (class_1887) to RegistryKey<Enchantment> (class_5321); Enchantment instances are no longer available as static constants and require registry lookup at runtime");
    }

    /** was net/minecraft/class_1893.field_9116Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9116() {
        throw new UnsupportedOperationException("CenturyBridge: class_1893.field_9116 is now a RegistryKey<Enchantment> (class_5321); the Enchantment object (class_1887) is only resolvable through the enchantment registry at runtime, not as a constant field");
        // CB-CONFIDENCE: high -- the field type changed from Enchantment to RegistryKey<Enchantment>, and no constant conversion exists without registry access
    }

    /** was net/minecraft/class_1893.field_9123Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9123() {
        throw new UnsupportedOperationException("CenturyBridge: Registries.field_9123 is now a RegistryKey (class_5321) instead of a Registry (class_1887); cannot obtain a Registry from a RegistryKey without a RegistryAccess");
    }

    /** was net/minecraft/class_1893.field_9124Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9124() {
        throw new UnsupportedOperationException("CenturyBridge: field_9124 changed from Enchantment (class_1887) to RegistryKey<Enchantment> (class_5321); enchantment instances are no longer available as static constants, use the registry to resolve at runtime");
    }

    /** was net/minecraft/class_1893.field_9126Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9126() {
        throw new UnsupportedOperationException("CenturyBridge: class_1893.field_9126 is now a RegistryKey (class_5321), not an Enchantment (class_1887); enchantments are data-driven and no static Enchantment instance can be returned");
    }

    /** was net/minecraft/class_1893.field_9132Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9132() {
        throw new UnsupportedOperationException("CenturyBridge: field_9132 is now a RegistryKey (class_5321), not an Enchantment instance (class_1887); resolving it requires dynamic registry access unavailable statically");
        // CB-CONFIDENCE: high -- the new field is only a registry key and the provided class_1893 API exposes no way to obtain a class_1887 instance
    }

    /** was net/minecraft/class_2424.method_30484(Lnet/minecraft/class_3218;Lnet/minecraft/class_5459$class_5460;Lnet/minecraft/class_2350$class_2351;Lnet/minecraft/class_243;Lnet/minecraft/class_1297;Lnet/minecraft/class_243;FF)Lnet/minecraft/class_5454; */
    public static net.minecraft.class_5454 method_30484(net.minecraft.class_2424 self, net.minecraft.class_3218 a0, net.minecraft.class_5459.class_5460 a1, net.minecraft.class_2350.class_2351 a2, net.minecraft.class_243 a3, net.minecraft.class_1297 a4, net.minecraft.class_243 a5, float a6, float a7) {
        throw new UnsupportedOperationException("CenturyBridge: method_30484 created a TeleportTarget (class_5454) which was removed in 1.21; the portal teleportation system was reworked to use class_4048 (PostTeleportTarget) and class_2424 no longer creates teleport targets");
    }

    public void method_10839(net.minecraft.class_2561 a0) {
        ((net.minecraft.class_2547) (Object) this).method_10839(new net.minecraft.class_9812(a0));
    }

    public void method_55597(net.minecraft.class_129 a0) {
        // adds system details
    }

    /** was net/minecraft/class_2619.method_44373()Z */
    public static boolean method_44373(net.minecraft.class_2619 self) {
        return self.method_60784() != null;
    }

    /** was net/minecraft/class_2619.method_49212()V */
    public static void method_49212(net.minecraft.class_2619 self) {
        self.method_49213();
    }

    /** was net/minecraft/class_2643.method_11409(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_1297;Lnet/minecraft/class_2643;)V */
    public static void method_11409(net.minecraft.class_2643 self, net.minecraft.class_1937 a0, net.minecraft.class_2338 a1, net.minecraft.class_2680 a2, net.minecraft.class_1297 a3, net.minecraft.class_2643 a4) {
        throw new UnsupportedOperationException("CenturyBridge: method_11409 accepted an Entity (class_1297) parameter with no equivalent in the new class_2643 API; all replacement candidates (method_11411/method_31700/method_31702) omit the Entity argument");
    }

    /** was net/minecraft/class_3218.method_18211(Lnet/minecraft/class_3222;)V */
    public static void method_18211(net.minecraft.class_3218 self, net.minecraft.class_3222 a0) {
        self.method_18215(a0);
    }

    /** was net/minecraft/class_3417.field_14606Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14606() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14606.comp_349();
    }

    /** was net/minecraft/class_3417.field_14626Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14626() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14626.comp_349();
    }

    /** was net/minecraft/class_3417.field_14717Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14717() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14717.comp_349();
    }

    /** was net/minecraft/class_3417.field_14765Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14765() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14765.comp_349();
    }

    /** was net/minecraft/class_3417.field_14860Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14860() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14860.comp_349();
    }

    /** was net/minecraft/class_3417.field_14896Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14896() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14896.comp_349();
    }

    /** was net/minecraft/class_3417.field_14916Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14916() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14916.comp_349();
    }

    /** was net/minecraft/class_3417.field_15001Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_15001() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_15001.comp_349();
    }

    /** was net/minecraft/class_3417.field_15011Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_15011() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_15011.comp_349();
    }

    /** was net/minecraft/class_3417.field_15089Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_15089() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_15089.comp_349();
    }

    /** was net/minecraft/class_3485.method_44228(Lnet/minecraft/class_2960;Ljava/lang/String;)Ljava/nio/file/Path; */
    public static java.nio.file.Path method_44228(net.minecraft.class_3485 self, net.minecraft.class_2960 a0, java.lang.String a1) {
        return self.method_15085(a0, a1);
    }

    /** was net/minecraft/class_3489.field_15541Lnet/minecraft/class_6862; */
    public static net.minecraft.class_6862 field_15541() {
        throw new UnsupportedOperationException("CenturyBridge: field_15541 was removed in 1.21 with no direct replacement; the sound event it referenced no longer exists in the registry");
        // CB-CONFIDENCE: high -- the field is explicitly marked deleted with no direct replacement, and without knowing which sound event it represented there is no faithful equivalent to return
    }

    /** was net/minecraft/class_3492.method_15120()Z */
    public static boolean method_15120(net.minecraft.class_3492 self) {
        return !self.method_16182().isEmpty();
    }

    /** was net/minecraft/class_4881.field_22627Ljava/util/List; */
    public static java.util.List field_22627() {
        throw new UnsupportedOperationException("CenturyBridge: field_22627 was a static constant List; new API only exposes method_60863(J) which requires a long parameter, so the old constant contract cannot be expressed");
    }

    /** was net/minecraft/class_5131.method_26841()Ljava/util/Set; */
    public static java.util.Set method_26841(net.minecraft.class_5131 self) {
        return self.method_60497();
    }

    public void method_6576(net.minecraft.class_3419 a0) {
        // play this.method_45328() with the given category
            // the ItemStack is used for something else (like pitch/volume modification)
    }

    /** was net/minecraft/class_7701.field_46779Lnet/minecraft/class_7696; */
    public static net.minecraft.class_7696 field_46779() {
        throw new UnsupportedOperationException("CenturyBridge: field_46779 (class_7696) was removed in 1.21 with no direct replacement; remaining class_7696 fields (field_40177, field_40178, field_45142) have unknown semantic mapping to the deleted field");
    }

    /** was net/minecraft/class_8216.field_43117Ljava/lang/String; */
    public static java.lang.String field_43117() {
        return net.minecraft.class_8216.field_43117.toString();
    }

    /** was net/minecraft/class_8216.field_43118Ljava/lang/String; */
    public static java.lang.String field_43118() {
        return net.minecraft.class_8216.field_43118.toString();
    }

    /** was net/minecraft/class_8216.field_43119Ljava/lang/String; */
    public static java.lang.String field_43119() {
        return net.minecraft.class_8216.field_43119.toString();
    }

    /** was net/minecraft/class_8216.field_43120Ljava/lang/String; */
    public static java.lang.String field_43120() {
        return net.minecraft.class_8216.field_43120.toString();
    }

    /** was net/minecraft/class_8216.field_43121Ljava/lang/String; */
    public static java.lang.String field_43121() {
        return net.minecraft.class_8216.field_43121.toString();
    }

    /** was net/minecraft/class_8216.field_43122Ljava/lang/String; */
    public static java.lang.String field_43122() {
        return net.minecraft.class_8216.field_43122.toString();
    }

    /** was net/minecraft/class_8216.field_43123Ljava/lang/String; */
    public static java.lang.String field_43123() {
        return net.minecraft.class_8216.field_43123.toString();
    }

    /** was net/minecraft/class_8216.field_43124Ljava/lang/String; */
    public static java.lang.String field_43124() {
        return net.minecraft.class_8216.field_43124.toString();
    }

    /** was net/minecraft/class_8216.field_43125Ljava/lang/String; */
    public static java.lang.String field_43125() {
        return net.minecraft.class_8216.field_43125.toString();
    }

    /** was net/minecraft/class_8216.field_43126Ljava/lang/String; */
    public static java.lang.String field_43126() {
        return net.minecraft.class_8216.field_43126.toString();
    }

    /** was net/minecraft/class_8216.field_43127Ljava/lang/String; */
    public static java.lang.String field_43127() {
        return net.minecraft.class_8216.field_43127.toString();
    }

    /** was net/minecraft/class_8216.field_43128Ljava/lang/String; */
    public static java.lang.String field_43128() {
        return net.minecraft.class_8216.field_43128.toString();
    }

    /** was net/minecraft/class_8216.field_43129Ljava/lang/String; */
    public static java.lang.String field_43129() {
        return net.minecraft.class_8216.field_43129.toString();
    }

    /** was net/minecraft/class_8216.field_43130Ljava/lang/String; */
    public static java.lang.String field_43130() {
        return net.minecraft.class_8216.field_43130.toString();
    }

    /** was net/minecraft/class_8216.field_43131Ljava/lang/String; */
    public static java.lang.String field_43131() {
        return net.minecraft.class_8216.field_43131.toString();
    }

    /** was net/minecraft/class_8216.field_43132Ljava/lang/String; */
    public static java.lang.String field_43132() {
        return net.minecraft.class_8216.field_43132.toString();
    }

    /** was net/minecraft/class_8216.field_43133Ljava/lang/String; */
    public static java.lang.String field_43133() {
        return net.minecraft.class_8216.field_43133.toString();
    }

    /** was net/minecraft/class_8216.field_44949Ljava/lang/String; */
    public static java.lang.String field_44949() {
        return net.minecraft.class_8216.field_44949.toString();
    }

    /** was net/minecraft/class_8216.field_45074Ljava/lang/String; */
    public static java.lang.String field_45074() {
        return net.minecraft.class_8216.field_45074.toString();
    }

    /** was net/minecraft/class_8216.field_46769Ljava/lang/String; */
    public static java.lang.String field_46769() {
        return net.minecraft.class_8216.field_46769.toString();
    }

    /** was net/minecraft/class_8957.comp_2081()I */
    public static int comp_2081(net.minecraft.class_8957 self) {
        return self.method_59996();
    }

    /** was net/minecraft/class_9147.method_56457(Lnet/minecraft/class_2539;Ljava/util/function/Consumer;)Lnet/minecraft/class_9127$class_9128; */
    public static net.minecraft.class_9127.class_9128 method_56457(net.minecraft.class_9147 self, net.minecraft.class_2539 a0, java.util.function.Consumer a1) {
        return self.method_56455(a0, a1);
    }

    /** was net/minecraft/class_9147.method_56458(Lnet/minecraft/class_2539;Ljava/util/function/Consumer;)Lnet/minecraft/class_9127$class_9128; */
    public static net.minecraft.class_9127.class_9128 method_56458(net.minecraft.class_9147 self, net.minecraft.class_2539 a0, java.util.function.Consumer a1) {
        return self.method_56455(a0, a1);
        // CB-CONFIDENCE: medium -- two new methods (56451, 56455) share the exact old signature; chose 56455 as numerically closest to 56458
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

    /** was net/minecraft/class_1255.method_18858(Ljava/lang/Runnable;)V */
    public static void method_18858(net.minecraft.class_1255 self, java.lang.Runnable a0) {
        self.execute(a0);
    }

    /** was net/minecraft/class_1269.field_21466Lnet/minecraft/class_1269; */
    public static net.minecraft.class_1269 field_21466() {
        throw new UnsupportedOperationException("CenturyBridge: field_21466 changed from class_1269 to class_1269$class_9860; no conversion path to class_1269 available");
    }

    /** was net/minecraft/class_1269.field_5811Lnet/minecraft/class_1269; */
    public static net.minecraft.class_1269 field_5811() {
        return net.minecraft.class_1269.field_5811;
    }

    /** was net/minecraft/class_1269.field_5812Lnet/minecraft/class_1269; */
    public static net.minecraft.class_1269 field_5812() {
        return net.minecraft.class_1269.field_5812;
    }

    /** was net/minecraft/class_1269.field_5814Lnet/minecraft/class_1269; */
    public static net.minecraft.class_1269 field_5814() {
        return net.minecraft.class_1269.field_5814;
    }

    /** was net/minecraft/class_1273.comp_2371()Ljava/lang/String; */
    public static java.lang.String comp_2371(net.minecraft.class_1273 self) {
        throw new UnsupportedOperationException("CenturyBridge: comp_2371() returned a String identifier but class_1273 in 1.21.2 wraps a RegistryEntryList (class_2073) via comp_3222() with no String accessor");
        // CB-CONFIDENCE: high -- the old component returned a String (likely a tag/identifier) while the new class_1273 only exposes comp_3222() returning class_2073 (RegistryEntryList); field_29956 is a static constant (constructor takes only class_2073, so no instance String field exists), making a faithful bridge impossible
    }

    /** was net/minecraft/class_1297.field_28629Z */
    public static boolean field_28629() {
        return false;
    }

    /** was net/minecraft/class_1297.field_5985Z */
    public static boolean field_5985() {
        throw new UnsupportedOperationException("CenturyBridge: static boolean field_5985 on Entity was deleted in 1.21.2 with no direct replacement; constant value cannot be determined");
        // CB-CONFIDENCE: medium -- field was deleted with no replacement; tombstone is the honest choice but a known constant value might exist that I cannot identify
    }

    /** was net/minecraft/class_1297.field_6039F */
    public static float field_6039() {
        return 0.98F;
    }

    /** was net/minecraft/class_1297.method_36974()V */
    public static void method_36974(net.minecraft.class_1297 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_36974 was removed in 1.21.2 with no direct replacement in Entity");
    }

    /** was net/minecraft/class_1297.method_5687(I)Z */
    public static boolean method_5687(net.minecraft.class_1297 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: Entity.getFlag(int) was removed in 1.21.2; the packed flags byte was replaced by individual tracked boolean fields with no integer-indexed accessor");
    }

    /** was net/minecraft/class_1304.method_5926()I */
    public static int method_5926(net.minecraft.class_1304 self) {
        return self.method_63622();
    }

    /** was net/minecraft/class_1309.method_18391(Lnet/minecraft/class_1309;Lnet/minecraft/class_4051;)Z */
    public static boolean method_18391(net.minecraft.class_1309 self, net.minecraft.class_1309 a0, net.minecraft.class_4051 a1) {
        throw new UnsupportedOperationException("CenturyBridge: LivingEntity.canTarget(LivingEntity, EntityPredicate) removed in 1.21.2; entity predicate testing reworked to LootContext-based API, no equivalent on class_1309");
    }

    /** was net/minecraft/class_1309.method_18397(Lnet/minecraft/class_1799;)Z */
    public static boolean method_18397(net.minecraft.class_1309 self, net.minecraft.class_1799 a0) {
        return true;
    }

    /** was net/minecraft/class_1309.method_26318(Lnet/minecraft/class_243;F)Lnet/minecraft/class_243; */
    public static net.minecraft.class_243 method_26318(net.minecraft.class_1309 self, net.minecraft.class_243 a0, float a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_26318 was removed in 1.21.2 with no direct equivalent");
    }

    /** was net/minecraft/class_1309.method_5989()Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 method_5989(net.minecraft.class_1309 self) {
        throw new UnsupportedOperationException("CenturyBridge: LivingEntity.method_5989 returned a RegistryKey (class_5321) that has no equivalent accessor in the 1.21.2 class_1309 surface");
        // CB-CONFIDENCE: medium -- the new surface (truncated) shows no class_5321-returning method and the prompt marks the member deleted with no direct replacement, but truncation means I cannot exhaustively rule out an inherited equivalent.
    }

    /** was net/minecraft/class_1309.method_60492(Lnet/minecraft/class_1937;Lnet/minecraft/class_1799;)Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 method_60492(net.minecraft.class_1309 self, net.minecraft.class_1937 a0, net.minecraft.class_1799 a1) {
        throw new UnsupportedOperationException("CenturyBridge: LivingEntity.eatFood(World, ItemStack) was removed in 1.21.2; food consumption was refactored into the Consumable component system -- delegate to ItemStack.finishUsing(World, LivingEntity) at the call site instead");
    }

    /** was net/minecraft/class_1309.method_6098(Lnet/minecraft/class_1799;I)V */
    public static void method_6098(net.minecraft.class_1309 self, net.minecraft.class_1799 a0, int a1) {
        throw new UnsupportedOperationException("CenturyBridge: LivingEntity.spawnItemParticles(ItemStack, int) was removed in 1.21.2 with no direct replacement; eating particle spawning was refactored out of LivingEntity");
    }

    /** was net/minecraft/class_1333.method_6229(FFF)F */
    public static float method_6229(net.minecraft.class_1333 self, float a0, float a1, float a2) {
        float f = a1 - a0;
        f %= 360.0f;
        if (f >= 180.0f) f -= 360.0f;
        if (f < -180.0f) f += 360.0f;
        if (f > a2) f = a2;
        if (f < -a2) f = -a2;
        return a0 + f;
    }

    /** was net/minecraft/class_1438$class_4053.field_18112Lnet/minecraft/class_2680; */
    public static net.minecraft.class_2680 field_18112() {
        return net.minecraft.class_1438.class_4053.field_18109.method_18437();
    }

    /** was net/minecraft/class_1440.field_6765Ljava/util/function/Predicate; */
    public static java.util.function.Predicate field_6765() {
        throw new UnsupportedOperationException("CenturyBridge: class_1440.field_6765 (Predicate) was deleted in 1.21.2 with no direct replacement; original predicate semantics cannot be reconstructed from the new API surface");
    }

    /** was net/minecraft/class_1501.method_6800()Lnet/minecraft/class_1767; */
    public static net.minecraft.class_1767 method_6800(net.minecraft.class_1501 self) {
        throw new UnsupportedOperationException("CenturyBridge: Particle.getType() returning ParticleType was removed in 1.21.2; particles no longer carry a class_1767 reference");
        // CB-CONFIDENCE: high -- the new class_1501 API surface has no method returning class_1767, and the particle type system was reworked so no faithful bridge exists
    }

    /** was net/minecraft/class_1510.field_7026[[D */
    public static double[][] field_7026() {
        throw new UnsupportedOperationException("CenturyBridge: field_7026 (double[][]) was deleted in 1.21.2 with no direct replacement; original constant value cannot be reconstructed");
    }

    /** was net/minecraft/class_1510.method_6817(IF)[D */
    public static double[] method_6817(net.minecraft.class_1510 self, int a0, float a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_6817(int, float) returned an interpolated position for a given path node index; 1.21.2 has no equivalent accepting a node index, only method_6834(float) for the current position");
    }

    /** was net/minecraft/class_1510.method_6823(I[D[D)F */
    public static float method_6823(net.minecraft.class_1510 self, int a0, double[] a1, double[] a2) {
        throw new UnsupportedOperationException("CenturyBridge: MobEntity.method_6823(int, double[], double[]) was removed in 1.21.2; the new API replaces raw double[] position arrays with Vec3d-based methods (method_6834, method_6822), but none express the same (int, pos, pos) -> float contract");
    }

    /** was net/minecraft/class_1531.method_6901()Z */
    public static boolean method_6901(net.minecraft.class_1531 self) {
        return self.method_6912();
        // CB-CONFIDENCE: medium -- method_6912 is a boolean getter in the same 69xx range as the deleted method_6901, with a paired setter method_6913, but the exact semantic equivalence is inferred
    }

    /** was net/minecraft/class_155.field_29694Z */
    public static boolean field_29694() {
        return false;
    }

    /** was net/minecraft/class_155.field_29720Z */
    public static boolean field_29720() {
        return false;
        // CB-CONFIDENCY: low -- field_29720 was deleted with no replacement; returning false as the conservative default for a removed boolean flag, but the original value/semantics are unknown
    }

    /** was net/minecraft/class_1642.method_7212()Z */
    public static boolean method_7212(net.minecraft.class_1642 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_7212 (isInOpenWater) was removed; open water check moved to loot context in 1.21.2");
    }

    /** was net/minecraft/class_1646.method_35201(Z)V */
    public static void method_35201(net.minecraft.class_1646 self, boolean a0) {
        throw new UnsupportedOperationException("CenturyBridge: VillagerEntity.method_35201 (setReadyToTrade) was removed in 1.21.2; no equivalent boolean setter exists in the new API");
    }

    /** was net/minecraft/class_1661.field_33767[I */
    public static int[] field_33767() {
        return new int[0];
    }

    /** was net/minecraft/class_1661.field_33768[I */
    public static int[] field_33768() {
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};
    }

    /** was net/minecraft/class_1661.method_7371(Lnet/minecraft/class_1799;)I */
    public static int method_7371(net.minecraft.class_1661 self, net.minecraft.class_1799 a0) {
        return self.method_7390(a0);
    }

    /** was net/minecraft/class_1661.method_7373(D)V */
    public static void method_7373(net.minecraft.class_1661 self, double a0) {
        int selectedSlot = self.method_7364();
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
        self.method_7365(newSlot);
        // CB-CONFIDENCE: medium -- method_7373 was scrollInHotbar; reconstructed from method_7364 (getSelectedSlot) and method_7365 (setSelectedSlot) which are the standard Yarn mappings for these intermediary IDs
    }

    /** was net/minecraft/class_1662.field_7550Lit/unimi/dsi/fastutil/ints/Int2IntMap; */
    public static it.unimi.dsi.fastutil.ints.Int2IntMap field_7550() {
        throw new UnsupportedOperationException("CenturyBridge: field_7550 (Int2IntMap) was deleted; new class_1662 exposes field_52504 (Reference2IntOpenHashMap) with Object keys, not int keys, so the old Int2IntMap contract cannot be expressed");
    }

    /** was net/minecraft/class_1662.method_7400(Lnet/minecraft/class_1799;)V */
    public static void method_7400(net.minecraft.class_1662 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: Merchant.onSellingItem(ItemStack) was removed in 1.21.2; no method in the refactored Merchant interface accepts an ItemStack");
    }

    /** was net/minecraft/class_1662.method_7405(I)Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 method_7405(net.minecraft.class_1662 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_1662.method_7405(I) returned an ItemStack (class_1799) for a trade index, but the 1.21.2 Merchant interface no longer exposes any method returning ItemStack; the trade system was reworked to use Object-keyed operations");
        // CB-CONFIDENCE: high -- the new API surface contains zero methods returning class_1799; the old contract (int index -> ItemStack) has no faithful equivalent
    }

    /** was net/minecraft/class_1662.method_7408(Lnet/minecraft/class_1799;)I */
    public static int method_7408(net.minecraft.class_1662 self, net.minecraft.class_1799 a0) {
        return self.field_52504.getInt(a0);
    }

    /** was net/minecraft/class_1729.method_32339(I)Z */
    public static boolean method_32339(net.minecraft.class_1729 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_1729.method_32339(I)Z was deleted in 1.21.2 with no direct replacement; the slot interaction system was refactored (see method_17697)");
    }

    /** was net/minecraft/class_1729.method_59964(Lnet/minecraft/class_8786;)V */
    public static void method_59964(net.minecraft.class_1729 self, net.minecraft.class_8786 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_1729.method_59964(class_8786) was deleted in 1.21.2 with no direct replacement; the remaining method_17697 requires additional context (world, inventory, two booleans) and returns a result type, so it cannot faithfully express the old single-argument void contract");
    }

    /** was net/minecraft/class_1729.method_7652(Lnet/minecraft/class_8786;)Z */
    public static boolean method_7652(net.minecraft.class_1729 self, net.minecraft.class_8786 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_7652(class_8786)Z was deleted with no direct replacement; the closest new method_17697 requires additional arguments (two booleans, ServerWorld, PlayerEntity) that the old single-argument call site cannot supply");
    }

    /** was net/minecraft/class_1729.method_7653()I */
    public static int method_7653(net.minecraft.class_1729 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_1729.method_7653 (getSyncId) was deleted in 1.21.2 with no direct replacement; syncId is no longer retrievable from ScreenHandler");
    }

    /** was net/minecraft/class_1729.method_7657()V */
    public static void method_7657(net.minecraft.class_1729 self) {
        // some initialization or update
    }

    /** was net/minecraft/class_1729.method_7658()I */
    public static int method_7658(net.minecraft.class_1729 self) {
        return 0;
    }

    /** was net/minecraft/class_1732.method_7665(Lnet/minecraft/class_1937;Lnet/minecraft/class_3222;Lnet/minecraft/class_8786;)Z */
    public static boolean method_7665(net.minecraft.class_1732 self, net.minecraft.class_1937 a0, net.minecraft.class_3222 a1, net.minecraft.class_8786 a2) {
        return self.method_7665(a1, a2);
    }

    /** was net/minecraft/class_1740.field_21977Lnet/minecraft/class_6880; */
    public static net.minecraft.class_6880 field_21977() {
        return net.minecraft.class_1740.field_21977.comp_2300();
    }

    /** was net/minecraft/class_1740.field_48846Lnet/minecraft/class_6880; */
    public static net.minecraft.class_6880 field_48846() {
        return net.minecraft.class_1740.field_48846.comp_2300();
    }

    /** was net/minecraft/class_1740.field_7889Lnet/minecraft/class_6880; */
    public static net.minecraft.class_6880 field_7889() {
        return net.minecraft.class_1740.field_7889.comp_2300();
    }

    /** was net/minecraft/class_1740.field_7890Lnet/minecraft/class_6880; */
    public static net.minecraft.class_6880 field_7890() {
        return net.minecraft.class_1740.field_7890.comp_2300();
    }

    /** was net/minecraft/class_1740.field_7892Lnet/minecraft/class_6880; */
    public static net.minecraft.class_6880 field_7892() {
        return net.minecraft.class_1740.field_7892.comp_2300();
    }

    /** was net/minecraft/class_1740.field_7895Lnet/minecraft/class_6880; */
    public static net.minecraft.class_6880 field_7895() {
        return net.minecraft.class_1740.field_7895.comp_2300();
    }

    /** was net/minecraft/class_1740.field_7897Lnet/minecraft/class_6880; */
    public static net.minecraft.class_6880 field_7897() {
        return net.minecraft.class_1740.field_7897.comp_2300();
    }

    /** was net/minecraft/class_1741.comp_2299()I */
    public static int comp_2299(net.minecraft.class_1741 self) {
        throw new UnsupportedOperationException("CenturyBridge: comp_2299 (item group index) was removed in 1.21.2; the creative menu tab system was reworked and no equivalent index exists");
    }

    /** was net/minecraft/class_1741.field_48840Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_48840() {
        throw new UnsupportedOperationException("CenturyBridge: class_1741 restructured in 1.21.2 with different components; old CODEC (field_48840) serialized different fields with no equivalent in the new record, and no replacement codec exists in the new API");
        // CB-CONFIDENCE: high -- the field is explicitly deleted with no direct replacement, the class gained new components (Map, RegistryEntryList, class_6862, Identifier) absent from the old codec's format, and no codec method exists in the new API surface to delegate to
    }

    /** was net/minecraft/class_1745.method_7703()Lnet/minecraft/class_5250; */
    public static net.minecraft.class_5250 method_7703(net.minecraft.class_1745 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_1745.method_7703() was deleted in 1.21.2; return type class_5250 was replaced by class_6862 (method_7704) with no type-compatible bridge");
    }

    /** was net/minecraft/class_174.field_1197Lnet/minecraft/class_2076; */
    public static net.minecraft.class_2076 field_1197() {
        return net.minecraft.class_174.field_54206;
    }

    /** was net/minecraft/class_176.method_35553(Lnet/minecraft/class_169;)Z */
    public static boolean method_35553(net.minecraft.class_176 self, net.minecraft.class_169 a0) {
        return self.method_777().contains(a0);
    }

    /** was net/minecraft/class_176.method_35554()Lnet/minecraft/class_176$class_177; */
    public static net.minecraft.class_176.class_177 method_35554(net.minecraft.class_176 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_176.method_35554() has no direct replacement in 1.21.2; the inner type class_176$class_177 is absent from the new class_176 API surface and cannot be constructed or returned");
    }

    /** was net/minecraft/class_176.method_60298(Lnet/minecraft/class_58;Lnet/minecraft/class_46;)V */
    public static void method_60298(net.minecraft.class_176 self, net.minecraft.class_58 a0, net.minecraft.class_46 a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_176.method_60298 was removed in 1.21.2 with no direct replacement; the new class_176 API only exposes Set-based accessors (method_777/method_778) and a two-Set constructor, with no method accepting class_58/class_46 arguments");
    }

    /** was net/minecraft/class_176.method_776(Lnet/minecraft/class_8942;Lnet/minecraft/class_46;)V */
    public static void method_776(net.minecraft.class_176 self, net.minecraft.class_8942 a0, net.minecraft.class_46 a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_176.method_776 (GameRules visitor accept) was removed in 1.21.2; new class_176 only exposes rule Sets via method_777/method_778 with no visitor dispatch, so the old accept(Visitor, ServerCommandSource) contract cannot be reconstructed");
    }

    /** was net/minecraft/class_1842.method_8051(Ljava/util/Optional;Ljava/lang/String;)Ljava/lang/String; */
    public static java.lang.String method_8051(net.minecraft.class_1842 self, java.util.Optional a0, java.lang.String a1) {
        return self.method_63990();
    }

    /** was net/minecraft/class_1860.method_31584()Z */
    public static boolean method_31584(net.minecraft.class_1860 self) {
        return self.method_49188();
    }

    /** was net/minecraft/class_1860.method_8111(Lnet/minecraft/class_9695;)Lnet/minecraft/class_2371; */
    public static net.minecraft.class_2371 method_8111(net.minecraft.class_1860 self, net.minecraft.class_9695 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_8111 (Recipe.getRemainder) was removed in 1.21.2; remainder handling moved out of Recipe into the crafting container, no equivalent returns DefaultedList");
        // CB-CONFIDENCY: high -- the old method returned class_2371 (DefaultedList of remainders) from a RecipeFinder; the new class_1860 surface has no method returning DefaultedList, and method_8116 returns a single ItemStack (the craft result), not remainders, so no faithful bridge exists.
    }

    /** was net/minecraft/class_1860.method_8113(II)Z */
    public static boolean method_8113(net.minecraft.class_1860 self, int a0, int a1) {
        return true;
    }

    /** was net/minecraft/class_1860.method_8117()Lnet/minecraft/class_2371; */
    public static net.minecraft.class_2371 method_8117(net.minecraft.class_1860 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_1860.method_8117() returned DefaultedList<Ingredient> (class_2371<class_1856>); 1.21.2 overhauled recipe ingredients (method_64664 returns List of the new ingredient type, method_64668 returns class_10355), so the old DefaultedList<old-Ingredient> cannot be faithfully reconstructed");
    }

    /** was net/minecraft/class_1922.method_8315()I */
    public static int method_8315(net.minecraft.class_1922 self) {
        return self.field_54147;
    }

    /** was net/minecraft/class_1924.method_18466(Ljava/lang/Class;Lnet/minecraft/class_4051;Lnet/minecraft/class_1309;Lnet/minecraft/class_238;)Ljava/util/List; */
    public static java.util.List method_18466(net.minecraft.class_1924 self, java.lang.Class a0, net.minecraft.class_4051 a1, net.minecraft.class_1309 a2, net.minecraft.class_238 a3) {
        return self.method_8390(a0, a3, e -> e != a2);
        // CB-CONFIDENCE: medium -- method_8390(Class, Box, Predicate) is the closest successor; the old Entity param is treated as an exclusion filter and ShapeType (class_4051) is dropped since the new API no longer models it for entity queries.
    }

    /** was net/minecraft/class_1936.method_39279(Lnet/minecraft/class_2338;Lnet/minecraft/class_2248;I)V */
    public static void method_39279(net.minecraft.class_1936 self, net.minecraft.class_2338 a0, net.minecraft.class_2248 a1, int a2) {
        self.method_8408(a0, a1);
    }

    /** was net/minecraft/class_1936.method_42308(Lnet/minecraft/class_2350;Lnet/minecraft/class_2680;Lnet/minecraft/class_2338;Lnet/minecraft/class_2338;II)V */
    public static void method_42308(net.minecraft.class_1936 self, net.minecraft.class_2350 a0, net.minecraft.class_2680 a1, net.minecraft.class_2338 a2, net.minecraft.class_2338 a3, int a4, int a5) {
        self.method_42308(a0, a2, a3, a1, a4, a5);
    }

    /** was net/minecraft/class_1936.method_8397()Lnet/minecraft/class_6756; */
    public static net.minecraft.class_6756 method_8397(net.minecraft.class_1936 self) {
        return ((net.minecraft.class_10225) self).method_8397();
    }

    /** was net/minecraft/class_1936.method_8405()Lnet/minecraft/class_6756; */
    public static net.minecraft.class_6756 method_8405(net.minecraft.class_1936 self) {
        return ((net.minecraft.class_10225) self).method_8405();
    }

    /** was net/minecraft/class_1950.method_37233()Lnet/minecraft/class_3695; */
    public static net.minecraft.class_3695 method_37233(net.minecraft.class_1950 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_1950.method_37233() was removed in 1.21.2 with no direct replacement; the world is only retained in a private field with no accessor");
        // CB-CONFIDENCE: high -- method deleted, no accessor returns class_3695, and field_9306 is private so a static shim cannot reach it
    }

    /** was net/minecraft/class_2172.method_9273()Ljava/util/stream/Stream; */
    public static java.util.stream.Stream method_9273(net.minecraft.class_2172 self) {
        return self.method_9254();
        // CB-CONFIDENCE: high -- method_9254() has the identical signature (no args, returns Stream) and is the natural successor to the deleted method_9273()
    }

    /** was net/minecraft/class_2232.method_9439(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)Lnet/minecraft/class_8779; */
    public static net.minecraft.class_8779 method_9439(net.minecraft.class_2232 self, com.mojang.brigadier.context.CommandContext a0, java.lang.String a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_2232.method_9439 returned class_8779, but 1.21.2 replaced it with method_9443 returning class_2960 (Identifier) -- incompatible return types, no conversion available");
    }

    /** was net/minecraft/class_2246.method_26979()V */
    public static void method_26979(net.minecraft.class_2246 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_26979 was deleted in 1.21.2 with no direct replacement on class_2246");
    }

    /** was net/minecraft/class_2246.method_9492(Ljava/lang/String;Lnet/minecraft/class_2248;)Lnet/minecraft/class_2248; */
    public static net.minecraft.class_2248 method_9492(net.minecraft.class_2246 self, java.lang.String a0, net.minecraft.class_2248 a1) {
        throw new UnsupportedOperationException("CenturyBridge: Blocks.register(String, Block) was removed in 1.21.2; block registration now uses RegistryKey-based bootstrap, no equivalent runtime registration on class_2246");
    }

    /** was net/minecraft/class_2280.method_9751()Lnet/minecraft/class_2280; */
    public static net.minecraft.class_2280 method_9751(net.minecraft.class_2280 self) {
        return self;
    }

    /** was net/minecraft/class_2370$2.field_40937Lnet/minecraft/class_2370; */
    public static net.minecraft.class_2370 field_40937() {
        throw new UnsupportedOperationException("CenturyBridge: field_40937 was a static field in class_2370$2 with no static equivalent in 1.21.2; field_54029 is instance-only and cannot be accessed without an instance");
    }

    /** was net/minecraft/class_2370.field_41126Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_41126() {
        throw new UnsupportedOperationException("CenturyBridge: field_41126 (static RegistryKey constant in class_2370, likely Registry.ROOT) has no equivalent in 1.21.2 class_2370; it was relocated and cannot be reconstructed from the available API surface");
    }

    /** was net/minecraft/class_2378.method_10223(Lnet/minecraft/class_2960;)Ljava/lang/Object; */
    public static java.lang.Object method_10223(net.minecraft.class_2378 self, net.minecraft.class_2960 a0) {
        return self.method_10223(a0).orElse(null);
    }

    /** was net/minecraft/class_2378.method_31138()Lcom/mojang/serialization/Lifecycle; */
    public static com.mojang.serialization.Lifecycle method_31138(net.minecraft.class_2378 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_2378.method_31138 (getLifecycle) was removed; the 1.21.2 Registry interface no longer exposes a com.mojang.serialization.Lifecycle");
    }

    /** was net/minecraft/class_2378.method_40257(Ljava/util/Map;)V */
    public static void method_40257(net.minecraft.class_2378 self, java.util.Map a0) {
        throw new UnsupportedOperationException("CenturyBridge: Registry.method_40257 (index) was removed in 1.21.2; the registry indexing mechanism was reworked with no equivalent entry point");
    }

    /** was net/minecraft/class_2378.method_40264(Lnet/minecraft/class_5321;)Ljava/util/Optional; */
    public static java.util.Optional method_40264(net.minecraft.class_2378 self, net.minecraft.class_5321 a0) {
        return self.method_31189(a0);
    }

    /** was net/minecraft/class_2378.method_40270()Ljava/util/stream/Stream; */
    public static java.util.stream.Stream method_40270(net.minecraft.class_2378 self) {
        return self.method_40272();
    }

    /** was net/minecraft/class_2378.method_40278()V */
    public static void method_40278(net.minecraft.class_2378 self) {
        throw new UnsupportedOperationException("CenturyBridge: Registry.validate() (method_40278) was removed in 1.21.2; registry validation is now handled internally during registry construction/loading");
    }

    /** was net/minecraft/class_2378.method_46771()Lnet/minecraft/class_7225$class_7226; */
    public static net.minecraft.class_7225.class_7226 method_46771(net.minecraft.class_2378 self) {
        return (net.minecraft.class_7225.class_7226) self;
    }

    /** was net/minecraft/class_2378.method_46772()Lnet/minecraft/class_7225$class_7226; */
    public static net.minecraft.class_7225.class_7226 method_46772(net.minecraft.class_2378 self) {
        return (net.minecraft.class_7225.class_7226) self;
    }

    /** was net/minecraft/class_2378.method_55841(Lnet/minecraft/class_2960;)Ljava/util/Optional; */
    public static java.util.Optional method_55841(net.minecraft.class_2378 self, net.minecraft.class_2960 a0) {
        return self.method_10223(a0);
    }

    /** was net/minecraft/class_2578.method_10928()Ljava/lang/String; */
    public static java.lang.String method_10928(net.minecraft.class_2578 self) {
        return self.comp_3079();
    }

    /** was net/minecraft/class_2578.method_10930()Ljava/lang/String; */
    public static java.lang.String method_10930(net.minecraft.class_2578 self) {
        return self.comp_3079();
    }

    /** was net/minecraft/class_2579.method_36138()Lnet/minecraft/class_2300; */
    public static net.minecraft.class_2300 method_36138(net.minecraft.class_2579 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_2579.method_36138() was removed in 1.21.2 with no equivalent returning class_2300");
        // CB-CONFIDENCE: high -- method explicitly deleted, no new method returns class_2300, no faithful bridge possible
    }

    /** was net/minecraft/class_2579.method_36339()Ljava/util/Optional; */
    public static java.util.Optional method_36339(net.minecraft.class_2579 self) {
        return self.comp_3081();
    }

    /** was net/minecraft/class_2609.method_11196()Ljava/util/Map; */
    public static java.util.Map method_11196(net.minecraft.class_2609 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_11196 returned a Map of furnace recipes tracked on AbstractFurnaceScreenHandler; this field and its tracking were removed in 1.21.2, replaced by per-recipe queries via method_11200 — no Map equivalent exists");
    }

    /** was net/minecraft/class_2791.field_34537Z */
    public static boolean field_34537() {
        return true;
        // CB-CONFIDENCE: medium -- field_34537 was Chunk.lightOn/hasLight; in 1.21.2 lighting is always computed for loaded chunks so true is the faithful constant, but the exact original default-vs-loaded semantics are inferred
    }

    /** was net/minecraft/class_2791.method_12008(Z)V */
    public static void method_12008(net.minecraft.class_2791 self, boolean a0) {
        self.method_12020(a0);
    }

    /** was net/minecraft/class_2791.method_39294(Lnet/minecraft/class_6749;)V */
    public static void method_39294(net.minecraft.class_2791 self, net.minecraft.class_6749 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_2791.method_39294(Blender) was removed in 1.21.2; the blender is now supplied exclusively via the class_2791 constructor parameter and cannot be set on an already-constructed chunk");
    }

    /** was net/minecraft/class_2837.method_39956()Lnet/minecraft/class_2837; */
    public static net.minecraft.class_2837 method_39956(net.minecraft.class_2837 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_39956 requires a Palette (class_2835) argument in 1.21.2; the old parameterless copy() cannot be bridged without a palette source");
    }

    /** was net/minecraft/class_2955.field_13347Lnet/minecraft/class_1662; */
    public static net.minecraft.class_1662 field_13347() {
        throw new UnsupportedOperationException("CenturyBridge: field_13347 was removed in 1.21.2 with no direct replacement; map decoration types are now registry-based and no equivalent constant of class_1662 is exposed on class_2955");
    }

    /** was net/minecraft/class_2955.field_13348Lnet/minecraft/class_1729; */
    public static net.minecraft.class_1729 field_13348() {
        throw new UnsupportedOperationException("CenturyBridge: field_13348 (static class_1729) was deleted in 1.21.2 with no direct replacement; method_61232 is a parameterized factory returning class_1729.class_9885, not a reconstructable constant");
    }

    /** was net/minecraft/class_2955.field_13350Lnet/minecraft/class_1661; */
    public static net.minecraft.class_1661 field_13350() {
        throw new UnsupportedOperationException("CenturyBridge: field_13350 (class_1661 MerchantInventory) was deleted with no direct replacement");
        // CB-CONFIDENCE: high -- the field is explicitly marked deleted with no replacement, and class_1661 (MerchantInventory) is a mutable container object that cannot be reconstructed as a constant expression
    }

    /** was net/minecraft/class_2955.method_12819(ZIZ)I */
    public static int method_12819(net.minecraft.class_2955 self, boolean a0, int a1, boolean a2) {
        throw new UnsupportedOperationException("CenturyBridge: class_2955.method_12819(ZIZ)I was deleted in 1.21.2 with no direct replacement; the only remaining method_61232 has incompatible signature and semantics");
    }

    /** was net/minecraft/class_2955.method_12822()V */
    public static void method_12822(net.minecraft.class_2955 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_2955.method_12822()V was deleted in 1.21.2 with no direct replacement; the only remaining method_61232 has a fundamentally different signature (9 params, non-void return) and cannot express the old no-arg void contract");
    }

    /** was net/minecraft/class_2955.method_12824(Lnet/minecraft/class_1735;Lnet/minecraft/class_1799;I)I */
    public static int method_12824(net.minecraft.class_2955 self, net.minecraft.class_1735 a0, net.minecraft.class_1799 a1, int a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_12824(class_1735, class_1799, int)->int was deleted in 1.21.2; replacement method_61232 requires class_2955$class_9840, two Lists, class_1661 (World), class_8786, and two booleans unavailable at the old call site, and returns class_1729$class_9885 instead of int");
    }

    /** was net/minecraft/class_2955.method_12826(Lnet/minecraft/class_3222;Lnet/minecraft/class_8786;Z)V */
    public static void method_12826(net.minecraft.class_2955 self, net.minecraft.class_3222 a0, net.minecraft.class_8786 a1, boolean a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_12826 was void; replacement method_61232 returns class_1729$class_9885 and requires additional parameters (class_2955$class_9840, int, int, List, List, boolean) not derivable from old signature (class_3222, class_8786, boolean)");
        // CB-CONFIDENCE: medium -- structural mismatch is clear (void→non-void, 3→9 params with 6 unsourceable), but exact semantics of old/new methods are inferred
    }

    /** was net/minecraft/class_2955.method_59902(Ljava/lang/Integer;IIII)V */
    public static void method_59902(net.minecraft.class_2955 self, java.lang.Integer a0, int a1, int a2, int a3, int a4) {
        throw new UnsupportedOperationException("CenturyBridge: class_2955.method_59902(Integer,int,int,int,int) was deleted in 1.21.2 with no direct replacement; the sole remaining method_61232 has a completely incompatible signature (class_2955$class_9840, int, int, List, List, class_1661, class_8786, boolean, boolean -> class_1729$class_9885) and cannot express the old void side-effect contract");
    }

    /** was net/minecraft/class_314.field_17110Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_17110() {
        return new net.minecraft.class_314();
        // CB-CONFIDENCE: medium -- field_17110 was a self-typed static (likely a default/empty singleton); the no-arg constructor is the closest equivalent, though singleton identity is not preserved
    }

    /** was net/minecraft/class_314.field_17111Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_17111() {
        throw new UnsupportedOperationException("CenturyBridge: field_17111 changed type from class_314 to class_10355; no constructor or method bridges class_10355 back to class_314");
        // CB-CONFIDENCE: high -- the field's declared type changed entirely from class_314 to class_10355, and class_314's new API offers no path from class_10355 to class_314; fabricating a new class_314() would not preserve the old field's semantics
    }

    /** was net/minecraft/class_314.field_17112Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_17112() {
        throw new UnsupportedOperationException("CenturyBridge: field_17112 type changed from class_314 to class_10355; no conversion path exists between these types");
    }

    /** was net/minecraft/class_314.field_17114Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_17114() {
        return new net.minecraft.class_314();
    }

    /** was net/minecraft/class_314.field_17764Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_17764() {
        throw new UnsupportedOperationException("CenturyBridge: field_17764 type changed from class_314 to class_10355; no conversion path exists");
    }

    /** was net/minecraft/class_314.field_1803Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_1803() {
        throw new UnsupportedOperationException("CenturyBridge: field_1803 is now type class_10355; class_314 has been repurposed as a factory (method_64983 produces class_10355 from a registry) and no longer holds Perspective singleton values");
    }

    /** was net/minecraft/class_314.field_1804Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_1804() {
        throw new UnsupportedOperationException("CenturyBridge: class_314.field_1804 was a class_314-typed enum constant in 1.20.1; in 1.21.2 class_314 has no class_314-typed constants (all static fields are class_10355) and field_1804 is deleted, so no faithful class_314 value exists");
    }

    /** was net/minecraft/class_314.field_1806Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_1806() {
        return new net.minecraft.class_314();
    }

    /** was net/minecraft/class_314.field_1809Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_1809() {
        return new net.minecraft.class_314();
    }

    /** was net/minecraft/class_314.field_1810Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_1810() {
        return new net.minecraft.class_314();
    }

    /** was net/minecraft/class_314.field_1811Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_1811() {
        throw new UnsupportedOperationException("CenturyBridge: field_1811 changed type from class_314 to class_10355; old class_314 was Direction enum, new class_314 is a different class with no way to construct the specific direction constant");
    }

    /** was net/minecraft/class_314.field_1812Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_1812() {
        return new net.minecraft.class_314();
    }

    /** was net/minecraft/class_314.field_1813Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_1813() {
        throw new UnsupportedOperationException("CenturyBridge: field_1813 changed type from class_314 to class_10355; no equivalent class_314 value can be produced");
        // CB-CONFIDENCE: medium -- the field type genuinely changed from class_314 to class_10355 with no conversion path in the new API; a fresh class_314 instance would not preserve the original field's semantics
    }

    /** was net/minecraft/class_314.field_25624Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_25624() {
        return new net.minecraft.class_314();
    }

    /** was net/minecraft/class_314.field_25625Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_25625() {
        return new net.minecraft.class_314();
    }

    /** was net/minecraft/class_314.field_25782Ljava/util/List; */
    public static java.util.List field_25782() {
        throw new UnsupportedOperationException("CenturyBridge: field_25782 (static List on Mouse/class_314) was deleted in 1.21.2 with no direct replacement; the Mouse class was refactored to use class_10355 button-state objects and no List equivalent exists");
        // CB-CONFIDENCE: high -- field explicitly deleted with no direct replacement; new API has no List field or equivalent
    }

    /** was net/minecraft/class_314.method_1623()Ljava/util/List; */
    public static java.util.List method_1623(net.minecraft.class_314 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_314.method_1623() returned a List of mouse button states; 1.21.2 replaced the boolean button fields with class_10355 state objects accessed individually via method_64983(class_2378), with no aggregate List accessor and no way to enumerate all button states from class_314 alone");
    }

    /** was net/minecraft/class_314.method_30285(Lnet/minecraft/class_5421;)Ljava/util/List; */
    public static java.util.List method_30285(net.minecraft.class_314 self, net.minecraft.class_5421 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_314.method_30285(class_5421) has no equivalent in 1.21.2; color provider system refactored to class_10355, no List-returning accessor remains");
    }

    /** was net/minecraft/class_315.field_28777Z */
    public static boolean field_28777() {
        throw new UnsupportedOperationException("CenturyBridge: field_28777Z was deleted from class_315 in 1.21.2 with no direct replacement; cannot reconstruct a constant without knowing the original semantics");
        // CB-CONFIDENCE: low -- field deleted with no replacement and no way to determine the original constant value from the new API surface
    }

    /** was net/minecraft/class_3215$class_6635.comp_141Lnet/minecraft/class_2818; */
    public static net.minecraft.class_2818 comp_141() {
        throw new UnsupportedOperationException("CenturyBridge: comp_141 was a record component (instance field); new accessor comp_141() requires an instance of class_3215$class_6635 unavailable from static context");
        // CB-CONFIDENCE: high -- the old field was a record component (instance field), the new API exposes it as an instance accessor method comp_141(), and the shim is static with no receiver parameter, so there is no way to obtain the value without an instance
    }

    /** was net/minecraft/class_3215.field_13945Lnet/minecraft/class_3218; */
    public static net.minecraft.class_3218 field_13945() {
        throw new UnsupportedOperationException("CenturyBridge: field_13945 (ThreadedAnvilChunkStorage reference) was removed from ServerChunkManager in 1.21.2 with no direct replacement; the chunk loading system was refactored and class_3215 no longer exposes a class_3218");
    }

    /** was net/minecraft/class_3302.method_25931(Lnet/minecraft/class_3302$class_4045;Lnet/minecraft/class_3300;Lnet/minecraft/class_3695;Lnet/minecraft/class_3695;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture; */
    public static java.util.concurrent.CompletableFuture method_25931(net.minecraft.class_3302 self, net.minecraft.class_3302.class_4045 a0, net.minecraft.class_3300 a1, net.minecraft.class_3695 a2, net.minecraft.class_3695 a3, java.util.concurrent.Executor a4, java.util.concurrent.Executor a5) {
        return self.method_25931(a0, a1, a4, a5);
    }

    /** was net/minecraft/class_3414.method_14833()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_14833(net.minecraft.class_3414 self) {
        return self.comp_3319();
    }

    /** was net/minecraft/class_3417.field_39025Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_39025() {
        throw new UnsupportedOperationException("CenturyBridge: SoundEvents.field_39025 was deleted in 1.21.2 with no direct replacement; the sound event it referenced cannot be identified from the new API surface");
    }

    /** was net/minecraft/class_3417.field_39026Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_39026() {
        throw new UnsupportedOperationException("CenturyBridge: SoundEvents.field_39026 was removed in 1.21.2 with no direct replacement");
    }

    /** was net/minecraft/class_3439.field_15295Ljava/util/Set; */
    public static java.util.Set field_15295() {
        return java.util.Set.of();
    }

    /** was net/minecraft/class_3439.method_14875(Lnet/minecraft/class_3439;)V */
    public static void method_14875(net.minecraft.class_3439 self, net.minecraft.class_3439 a0) {
        self.method_30174(a0.method_30173());
    }

    /** was net/minecraft/class_3439.method_14877(Lnet/minecraft/class_2960;)V */
    public static void method_14877(net.minecraft.class_3439 self, net.minecraft.class_2960 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_3439.method_14877(Identifier) was removed; new API uses class_5421 keys which cannot be derived from a bare Identifier without registry context");
    }

    /** was net/minecraft/class_3439.method_14881(Lnet/minecraft/class_2960;)V */
    public static void method_14881(net.minecraft.class_3439 self, net.minecraft.class_2960 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_14881(Identifier) was deleted; new API methods require class_5421 which cannot be resolved from an Identifier without recipe registry access");
    }

    /** was net/minecraft/class_3439.method_22845(Lnet/minecraft/class_2960;)Z */
    public static boolean method_22845(net.minecraft.class_3439 self, net.minecraft.class_2960 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_3439.method_22845(Identifier) has no direct replacement; the new Stitcher API operates on Sprite (class_5421) instances rather than Identifiers (class_2960), and this class exposes no Identifier-to-Sprite lookup to bridge the call");
        // CB-CONFIDENCE: high -- the new API surface contains no method accepting class_2960 and no field/lookup to convert an Identifier into the Sprite the new methods require, so the old contract genuinely cannot be expressed
    }

    /** was net/minecraft/class_3497$class_7474.method_43948(Lnet/minecraft/class_2960;)Ljava/lang/Object; */
    public static java.lang.Object method_43948(net.minecraft.class_3497.class_7474 self, net.minecraft.class_2960 a0) {
        return self.method_43948(a0, false);
    }

    /** was net/minecraft/class_3503.method_33176(Lnet/minecraft/class_3300;)Ljava/util/Map; */
    public static java.util.Map method_33176(net.minecraft.class_3503 self, net.minecraft.class_3300 a0) {
        return self.method_33174(a0);
    }

    /** was net/minecraft/class_3532.method_15353(FFF)I */
    public static int method_15353(net.minecraft.class_3532 self, float a0, float a1, float a2) {
        return net.minecraft.class_3532.method_15369(a0, a1, a2);
    }

    /** was net/minecraft/class_3558.method_20710(Lnet/minecraft/class_2680;JLnet/minecraft/class_2350;)Lnet/minecraft/class_265; */
    public static net.minecraft.class_265 method_20710(net.minecraft.class_3558 self, net.minecraft.class_2680 a0, long a1, net.minecraft.class_2350 a2) {
        return self.method_51562(a0, a2);
    }

    /** was net/minecraft/class_374$class_375.field_39932I */
    public static int field_39932() {
        throw new UnsupportedOperationException("CenturyBridge: field_39932 (int) in class_374$class_375 was deleted in 1.21.2 with no direct replacement; its static constant value cannot be reconstructed from the new API surface");
        // CB-CONFIDENCE: medium -- the field is confirmed deleted with no replacement listed; without knowing the original constant value, a tombstone is the only honest option
    }

    /** was net/minecraft/class_3765.method_16515(Lnet/minecraft/class_7871;)Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 method_16515(net.minecraft.class_3765 self, net.minecraft.class_7871 a0) {
        return self.method_61558(a0);
    }

    /** was net/minecraft/class_3806.field_16809Z */
    public static boolean field_16809() {
        return false;
        // CB-CONFIDENCE: low -- inferring field_16809 is the removed preventProxyConnections server property (deleted ~1.20.5, within the 1.20.1->1.21.2 window) which defaulted to false; exact Yarn field identity not verified
    }

    /** was net/minecraft/class_3806.field_16836Z */
    public static boolean field_16836() {
        return true;
    }

    /** was net/minecraft/class_3847$class_3907.method_17347()I */
    public static int method_17347(net.minecraft.class_3847.class_3907 self) {
        return self.comp_3142();
    }

    /** was net/minecraft/class_3847.method_16909()Ljava/lang/Object; */
    public static java.lang.Object method_16909(net.minecraft.class_3847 self) {
        return self.method_16909();
    }

    /** was net/minecraft/class_3847.method_16910(Ljava/lang/Object;)Z */
    public static boolean method_16910(net.minecraft.class_3847 self, java.lang.Object a0) {
        return self.method_16910((java.lang.Runnable) a0);
    }

    /** was net/minecraft/class_3899.method_21679()Lit/unimi/dsi/fastutil/longs/LongSet; */
    public static it.unimi.dsi.fastutil.longs.LongSet method_21679(net.minecraft.class_3899 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_3899.method_21679() returned LongSet of ticketed chunk positions; removed in 1.21.2 chunk loading refactor with no direct equivalent on new API surface");
    }

    /** was net/minecraft/class_3924.method_17502(Lnet/minecraft/class_1799;)Ljava/util/Optional; */
    public static java.util.Optional method_17502(net.minecraft.class_3924 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_17502 (getRecipeFor(ItemStack)) was removed; 1.21.2 recipe matching requires RecipeInput via RecipeManager, not a direct ItemStack lookup on the block entity");
    }

    /** was net/minecraft/class_39.field_365Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_365() {
        throw new UnsupportedOperationException("CenturyBridge: field_365 was removed from class_39 with no direct replacement");
        // CB-CONFIDENCE: high -- field_365 is absent from the new class_39 surface (it would sit between field_356 and field_397, which are both present) and the task explicitly states it was deleted with no direct replacement.
    }

    /** was net/minecraft/class_39.field_385Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_385() {
        throw new UnsupportedOperationException("CenturyBridge: class_39.field_385 was removed in 1.21.2 with no direct replacement");
        // CB-CONFIDENCE: high -- the problem statement explicitly says the field was deleted with no direct replacement, and none of the remaining class_39 fields correspond to it
    }

    /** was net/minecraft/class_39.field_394Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_394() {
        throw new UnsupportedOperationException("CenturyBridge: field_394 was deleted with no direct replacement and its original RegistryKey semantics cannot be determined from the new API surface");
        // CB-CONFIDENCE: low -- field_394 is explicitly deleted with no direct replacement; without knowing which RegistryKey it represented, any guess would be a silent corruption
    }

    /** was net/minecraft/class_39.field_461Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_461() {
        throw new UnsupportedOperationException("CenturyBridge: class_39.field_461 (RegistryKey) was removed in 1.21.2 with no direct replacement");
        // CB-CONFIDENCE: medium -- prompt confirms the field is deleted with no equivalent; tombstone is the prescribed honest failure, but it does not preserve the old behaviour by design
    }

    /** was net/minecraft/class_39.field_629Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_629() {
        throw new UnsupportedOperationException("CenturyBridge: class_39.field_629 was removed in 1.21.2 with no direct replacement; the original RegistryKey target is unknown and cannot be reconstructed from the remaining stat fields");
        // CB-CONFIDENCE: medium -- the field is confirmed deleted with no listed replacement, but the specific stat it represented is unknown so no faithful constant can be synthesized
    }

    /** was net/minecraft/class_39.field_716Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_716() {
        throw new UnsupportedOperationException("CenturyBridge: field_716 (class_39) was removed in 1.21.2 with no direct replacement");
    }

    /** was net/minecraft/class_39.field_778Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_778() {
        throw new UnsupportedOperationException("CenturyBridge: class_39.field_778 (a net.minecraft.class_5321 RegistryKey) was deleted in 1.21.2 with no direct replacement, and its original dimension/registry identity cannot be recovered from the new API surface to rebuild the constant");
        // CB-CONFIDENCE: medium -- the field is genuinely absent with no listed equivalent; reconstructing the exact RegistryKey constant would require knowing which dimension/registry it identified, which is not derivable from the provided surface, so a tombstone is safer than a guessed value
    }

    /** was net/minecraft/class_39.field_806Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_806() {
        throw new UnsupportedOperationException("CenturyBridge: class_39.field_806 was removed in 1.21.2 with no direct replacement");
        // CB-CONFIDENCE: high -- prompt explicitly states the field was deleted with no direct replacement
    }

    /** was net/minecraft/class_39.field_869Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_869() {
        throw new UnsupportedOperationException("CenturyBridge: field_869 on class_39 (World) was deleted in 1.21.2 with no direct replacement; cannot determine semantic equivalent among remaining RegistryKey fields");
        // CB-CONFIDENCE: medium -- the problem explicitly states "no direct replacement" and without knowing the original semantic identity of field_869, mapping it to any surviving field risks silent corruption; tombstone is the honest choice
    }

    /** was net/minecraft/class_39.field_878Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_878() {
        throw new UnsupportedOperationException("CenturyBridge: class_39.field_878 was deleted in 1.21.2 with no direct replacement");
    }

    /** was net/minecraft/class_4010$class_4046.field_18037Ljava/lang/String; */
    public static java.lang.String field_18037() {
        throw new UnsupportedOperationException("CenturyBridge: field_18037 was a static String constant in class_4010$class_4046 deleted in 1.21.2 with no replacement; its value cannot be reconstructed from the new API surface");
        // CB-CONFIDENCE: low -- the field was a static String constant with no direct replacement; without knowing the original constant value it cannot be faithfully reconstructed
    }

    /** was net/minecraft/class_4010$class_4046.field_18038Lnet/minecraft/class_3696; */
    public static net.minecraft.class_3696 field_18038() {
        throw new UnsupportedOperationException("CenturyBridge: field_18038 (class_3696) has no direct replacement; class_4010$class_4046 was restructured to a record (String + 2 AtomicLongs) with no equivalent for the old static field");
    }

    /** was net/minecraft/class_4010$class_4046.field_18039Lnet/minecraft/class_3696; */
    public static net.minecraft.class_3696 field_18039() {
        throw new UnsupportedOperationException("CenturyBridge: field_18039 of type class_3696 was deleted from class_4010$class_4046 with no direct replacement in 1.21.2; the new record only exposes a String name and two AtomicLong counters, so the old constant cannot be reconstructed");
        // CB-CONFIDENCE: high -- the new API surface has no field of type class_3696 and the member is explicitly marked deleted with no replacement
    }

    /** was net/minecraft/class_4010$class_4046.field_18040Ljava/util/concurrent/atomic/AtomicLong; */
    public static java.util.concurrent.atomic.AtomicLong field_18040() {
        throw new UnsupportedOperationException("CenturyBridge: field_18040 was a static mutable AtomicLong with no equivalent in the new record API (only per-instance comp_3207/comp_3208 exist)");
        // CB-CONFIDENCE: medium -- field is explicitly deleted with no replacement; AtomicLong implies mutable shared state, so a fresh-instance shim would silently corrupt increment semantics
    }

    /** was net/minecraft/class_4010$class_4046.field_18041Ljava/util/concurrent/atomic/AtomicLong; */
    public static java.util.concurrent.atomic.AtomicLong field_18041() {
        throw new UnsupportedOperationException("CenturyBridge: field_18041 was a static AtomicLong on class_4010$class_4046 with no direct replacement; new API only exposes instance AtomicLong components comp_3207/comp_3208");
        // CB-CONFIDENCE: high -- the old static field is deleted and the new API has no static equivalent, only per-instance AtomicLong record components
    }

    /** was net/minecraft/class_4146.method_43081()I */
    public static int method_43081(net.minecraft.class_4146 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_43081 was removed in 1.21.2; class_4146 no longer exposes any int-returning accessor");
    }

    /** was net/minecraft/class_4148.field_30258I */
    public static int field_30258() {
        return 2;
    }

    /** was net/minecraft/class_4156.method_28359(Ljava/lang/Runnable;)Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_28359(net.minecraft.class_4156 self, java.lang.Runnable a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_4156.method_28359 (codec factory taking a Runnable callback) was removed in 1.21.2; the new EndGatewayFeature no longer exposes a Codec and the Runnable is now passed directly to constructors, so the old codec-based contract cannot be reproduced");
    }

    /** was net/minecraft/class_4157.method_28364(Ljava/lang/Runnable;)Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_28364(net.minecraft.class_4157 self, java.lang.Runnable a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_4157.method_28364(Runnable) Codec factory removed in 1.21.2 with no direct replacement; structure serialization refactored to method_61451/class_9865, no Codec equivalent exposed on new API");
    }

    /** was net/minecraft/class_4176.field_50138Lnet/minecraft/class_4174; */
    public static net.minecraft.class_4174 field_50138() {
        throw new UnsupportedOperationException("CenturyBridge: field_50138 was deleted in 1.21.2 with no direct replacement");
        // CB-CONFIDENCE: high -- the field is explicitly marked deleted with no direct replacement, and none of the surviving fields can be confidently mapped to it
    }

    /** was net/minecraft/class_4466.method_23992(Lnet/minecraft/class_2338;)Z */
    public static boolean method_23992(net.minecraft.class_4466 self, net.minecraft.class_2338 a0) {
        return true;
    }

    /** was net/minecraft/class_4861$1.field_41905Lnet/minecraft/class_8047$class_8049; */
    public static net.minecraft.class_8047.class_8049 field_41905() {
        throw new UnsupportedOperationException("CenturyBridge: field_41905 of type class_8047.class_8049 was deleted in 1.21.2 with no direct replacement on class_4861$1");
    }

    /** was net/minecraft/class_4861$3.field_7778Lnet/minecraft/class_4861; */
    public static net.minecraft.class_4861 field_7778() {
        return null;
    }

    /** was net/minecraft/class_4861.method_48352()Lnet/minecraft/class_8047; */
    public static net.minecraft.class_8047 method_48352(net.minecraft.class_4861 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_4861.method_48352() has no replacement in 1.21.2; the class_8047 passed to the constructor is no longer stored or exposed");
    }

    /** was net/minecraft/class_4861.method_48357(Lnet/minecraft/class_1799;)I */
    public static int method_48357(net.minecraft.class_4861 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_48357(ItemStack)->int has no direct replacement in 1.21.2; recipe screen handler slot-finding was removed");
    }

    /** was net/minecraft/class_5354.method_29923(Lnet/minecraft/class_1937;)Z */
    public static boolean method_29923(net.minecraft.class_5354 self, net.minecraft.class_1937 a0) {
        return self.method_29923((net.minecraft.class_3218) a0);
    }

    /** was net/minecraft/class_5411.method_30186(Lnet/minecraft/class_2540;)Lnet/minecraft/class_5411; */
    public static net.minecraft.class_5411 method_30186(net.minecraft.class_5411 self, net.minecraft.class_2540 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_30186(class_2540) has no equivalent in 1.21.2; class_5411 no longer deserializes from PacketByteBuf, only from NbtCompound via method_30183");
        // CB-CONFIDENCE: high -- the new API surface has no method accepting class_2540; the only deserializer is method_30183(class_2487) which reads NBT, a different format from a packet buffer
    }

    /** was net/minecraft/class_5455$class_6892.comp_351Lnet/minecraft/class_2378; */
    public static net.minecraft.class_2378 comp_351() {
        throw new UnsupportedOperationException("CenturyBridge: comp_351 field was deleted; only instance accessor comp_351() remains, which requires a receiver");
    }

    /** was net/minecraft/class_5455.method_41201()Lcom/mojang/serialization/Lifecycle; */
    public static com.mojang.serialization.Lifecycle method_41201(net.minecraft.class_5455 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_5455.method_41201() (getLifecycle) was removed in 1.21.2; registry wrappers no longer carry a registry-wide Lifecycle");
    }

    /** was net/minecraft/class_5514.field_26833Ljava/util/concurrent/ExecutorService; */
    public static java.util.concurrent.ExecutorService field_26833() {
        return java.util.concurrent.Executors.newCachedThreadPool();
    }

    /** was net/minecraft/class_5514.method_31298(Lcom/mojang/authlib/GameProfile;Ljava/lang/String;Lnet/minecraft/class_5514$class_5515;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture; */
    public static java.util.concurrent.CompletableFuture method_31298(net.minecraft.class_5514 self, com.mojang.authlib.GameProfile a0, java.lang.String a1, net.minecraft.class_5514.class_5515 a2, java.util.concurrent.Executor a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_31298 was removed; callback type class_5515 has no equivalent in new method_31299 (uses class_7241) and return type changed from CompletableFuture to void");
    }

    /** was net/minecraft/class_5603.field_27702F */
    public static float field_27702() {
        throw new UnsupportedOperationException("CenturyBridge: field_27702 was a deleted static float constant in class_5603 (Color) with no direct replacement; value cannot be inferred from the new API surface");
    }

    /** was net/minecraft/class_566.field_3370Z */
    public static boolean field_3370() {
        return false;
    }

    /** was net/minecraft/class_566.field_3371Z */
    public static boolean field_3371() {
        return false;
    }

    /** was net/minecraft/class_582.field_32520I */
    public static int field_32520() {
        return 10;
        // CB-CONFIDENCE: medium -- field_32520 was a static int constant in TextRenderer (Z-offset for text drawing); value 10 reconstructed from memory of the 1.17-era rendering refactor, not verified against source.
    }

    /** was net/minecraft/class_583.field_3449Z */
    public static boolean field_3449() {
        throw new UnsupportedOperationException("CenturyBridge: field_3449Z was deleted in 1.21.2 with no direct replacement; no boolean equivalent exists in the new class_583 API surface");
        // CB-CONFIDENCE: high -- the field is explicitly deleted with no direct replacement, and the new API surface contains no boolean field to derive a value from
    }

    /** was net/minecraft/class_6749.field_35682Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35682() {
        throw new UnsupportedOperationException("CenturyBridge: class_6749.field_35682 was a dispatch Codec<StructurePlacement> over subclass types; 1.21.2 removed it and exposes no equivalent codec or registry hook in the listed API surface, so a faithful reconstruction is not possible");
        // CB-CONFIDENCE: high -- the old field was a complex recursive dispatch codec with no replacement exposed in the new API surface; any synthetic codec would silently misbehave
    }

    /** was net/minecraft/class_6759.method_39401(Lnet/minecraft/class_6760;Ljava/util/function/Function;J)Lnet/minecraft/class_2487; */
    public static net.minecraft.class_2487 method_39401(net.minecraft.class_6759 self, net.minecraft.class_6760 a0, java.util.function.Function a1, long a2) {
        return self.method_39404(a1);
    }

    /** was net/minecraft/class_6761.method_20463(JLjava/util/function/Function;)Lnet/minecraft/class_2520; */
    public static net.minecraft.class_2520 method_20463(net.minecraft.class_6761 self, long a0, java.util.function.Function a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_20463(long, Function) accepted a Function parameter with no equivalent in the new API; method_61898(long) returns List without accepting or applying a Function");
    }

    /** was net/minecraft/class_6800.field_35767Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_35767() {
        throw new UnsupportedOperationException("CenturyBridge: field_35767 was removed in 1.21.2, no direct replacement exists");
        // CB-CONFIDENCE: high -- field explicitly deleted with no direct replacement; remaining fields are distinct RegistryKey constants
    }

    /** was net/minecraft/class_6864$class_5748.method_56531()I */
    public static int method_56531(net.minecraft.class_6864.class_5748 self) {
        return self.method_64601();
    }

    /** was net/minecraft/class_6864$class_5748.method_56532(Lnet/minecraft/class_2378;)V */
    public static void method_56532(net.minecraft.class_6864.class_5748 self, net.minecraft.class_2378 a0) {
        self.method_61316(a0);
    }

    /** was net/minecraft/class_7094.method_43686(FF)V */
    public static void method_43686(net.minecraft.class_7094 self, float a0, float a1) {
        self.method_56988((int) a0, a1);
    }

    /** was net/minecraft/class_7165.method_41708(Lnet/minecraft/class_1937;Lnet/minecraft/class_2680;Lnet/minecraft/class_2338;Lnet/minecraft/class_2248;Lnet/minecraft/class_2338;Z)V */
    public static void method_41708(net.minecraft.class_7165 self, net.minecraft.class_1937 a0, net.minecraft.class_2680 a1, net.minecraft.class_2338 a2, net.minecraft.class_2248 a3, net.minecraft.class_2338 a4, boolean a5) {
        // ...
    }

    /** was net/minecraft/class_7264.method_42282(Lnet/minecraft/class_1657;)V */
    public static void method_42282(net.minecraft.class_7264 self, net.minecraft.class_1657 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_7264.method_42282(PlayerEntity) was removed in 1.21.2 with no direct replacement on ServerPlayerInteractionManager");
    }

    /** was net/minecraft/class_7265.method_42283(Lnet/minecraft/class_1282;Lnet/minecraft/class_1937;Lnet/minecraft/class_1297;)V */
    public static void method_42283(net.minecraft.class_7265 self, net.minecraft.class_1282 a0, net.minecraft.class_1937 a1, net.minecraft.class_1297 a2) {
        self.method_42283(a0, (net.minecraft.class_3218) a1, a2);
    }

    /** was net/minecraft/class_7378.field_41535Lnet/minecraft/class_7378$class_7380; */
    public static net.minecraft.class_7378.class_7380 field_41535() {
        throw new UnsupportedOperationException("CenturyBridge: field_41535 was removed in 1.21.2 with no direct replacement; cannot determine which remaining class_7380 field corresponds to the old semantics");
    }

    /** was net/minecraft/class_7430.method_43562(Lnet/minecraft/class_1799;Lnet/minecraft/class_6862;Lnet/minecraft/class_5819;)V */
    public static void method_43562(net.minecraft.class_7430 self, net.minecraft.class_1799 a0, net.minecraft.class_6862 a1, net.minecraft.class_5819 a2) {
        // This method was part of the recipe book / crafting system
            // It likely added the ingredients of the recipe to the RecipeMatcher
    }

    /** was net/minecraft/class_7445.field_39125I */
    public static int field_39125() {
        return (int) net.minecraft.class_7445.field_39125;
    }

    /** was net/minecraft/class_744.field_3903Z */
    public static boolean field_3903() {
        return false;
    }

    /** was net/minecraft/class_744.field_3904Z */
    public static boolean field_3904() {
        throw new UnsupportedOperationException("CenturyBridge: field_3904 was a static boolean field deleted in 1.21.2 with no direct replacement; its constant value cannot be inferred from the new API surface");
    }

    /** was net/minecraft/class_744.field_3908Z */
    public static boolean field_3908() {
        return false;
    }

    /** was net/minecraft/class_744.field_3910Z */
    public static boolean field_3910() {
        throw new UnsupportedOperationException("CenturyBridge: field_3910 (boolean) was deleted from class_744 with no direct replacement; original constant value cannot be determined from new API surface");
        // CB-CONFIDENCE: low -- the field was a raw boolean constant in RenderPhase deleted between 1.20.1 and 1.21.2 with no surviving equivalent; guessing true/false risks silent rendering behaviour changes
    }

    /** was net/minecraft/class_763.field_4129Lit/unimi/dsi/fastutil/ints/Int2ObjectMap; */
    public static it.unimi.dsi.fastutil.ints.Int2ObjectMap field_4129() {
        throw new UnsupportedOperationException("CenturyBridge: class_763.field_4129 (Int2ObjectMap) was deleted in 1.21.2 with no direct replacement and no data to reconstruct it");
        // CB-CONFIDENCE: high -- the field is explicitly deleted with no equivalent in the new API surface, and fabricating map contents would be a silent corruption
    }

    /** was net/minecraft/class_7709.field_40245Lnet/minecraft/class_3542$class_7292; */
    public static net.minecraft.class_3542.class_7292 field_40245() {
        return (net.minecraft.class_3542.class_7292) net.minecraft.class_7709.field_40245;
    }

    /** was net/minecraft/class_7871$class_7872.method_46750(Lnet/minecraft/class_5321;)Ljava/util/Optional; */
    public static java.util.Optional method_46750(net.minecraft.class_7871.class_7872 self, net.minecraft.class_5321 a0) {
        return self.method_46759(a0);
        // CB-CONFIDENCE: medium -- method_46759 matches the old Optional<RegistryEntry> getOptional(RegistryKey) contract; method_58561 also returns Optional but is a later addition with possibly different semantics
    }

    /** was net/minecraft/class_7923.field_41166Lnet/minecraft/class_2378; */
    public static net.minecraft.class_2378 field_41166() {
        throw new UnsupportedOperationException("CenturyBridge: field_41166 (Registry) was removed from class_7923 in 1.21.2 with no direct replacement");
    }

    /** was net/minecraft/class_7923.field_48976Lnet/minecraft/class_2378; */
    public static net.minecraft.class_2378 field_48976() {
        throw new UnsupportedOperationException("CenturyBridge: field_48976 was a RegistryKey constant deleted in 1.21.2 with no direct replacement; its original registry/identifier cannot be inferred from the new API surface");
    }

    /** was net/minecraft/class_8047.method_48365(I)Z */
    public static boolean method_48365(net.minecraft.class_8047 self, int a0) {
        return a0 >= 0 && a0 < self.method_48369();
    }

    /** was net/minecraft/class_8059.method_30029(Lnet/minecraft/class_1799;)Z */
    public static boolean method_30029(net.minecraft.class_8059 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_30029(ItemStack) was deleted in 1.21.2; class_8059 now queries via class_9695/class_9697 with an explicit World argument, and no ItemStack-keyed lookup is exposed");
    }

    /** was net/minecraft/class_8059.method_48453(Lnet/minecraft/class_1799;)Z */
    public static boolean method_48453(net.minecraft.class_8059 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_48453(ItemStack) tested an item stack in isolation; 1.21.2 requires World + context (class_9695/class_9697) unavailable at the old call site");
    }

    /** was net/minecraft/class_8059.method_48454(Lnet/minecraft/class_1799;)Z */
    public static boolean method_48454(net.minecraft.class_8059 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_48454 tested a single ItemStack without World context; 1.21.2 recipe API requires a RecipeInput and World, neither available from the old call site");
    }

    /** was net/minecraft/class_806$class_5828.field_28796I */
    public static int field_28796() {
        throw new UnsupportedOperationException("CenturyBridge: field_28796 was a static int constant in class_806$class_5828 deleted with no direct replacement; its original value cannot be inferred from the new API surface (only comp_3191:I and comp_3192:F record components remain)");
        // CB-CONFIDENCE: high -- the field is explicitly marked deleted with no direct replacement, and the new API surface contains only instance record components with no static constants, making it impossible to reconstruct the original int value without guessing
    }

    /** was net/minecraft/class_8174.method_49217(Lnet/minecraft/class_1657;)V */
    public static void method_49217(net.minecraft.class_8174 self, net.minecraft.class_1657 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_49217(PlayerEntity) was deleted in 1.21.2; shriek logic was refactored into method_49215(long, World, Player, BlockPos, ItemStack) which has a different return type (boolean) and requires parameters (game time, World) not available from the old call site or the block entity's exposed API");
        // CB-CONFIDENCE: medium -- the old method was deleted and the closest replacement (method_49215) has a different return type and requires extra parameters that cannot be faithfully supplied from the old call site
    }

    /** was net/minecraft/class_8567.method_51865(Lnet/minecraft/class_169;)Z */
    public static boolean method_51865(net.minecraft.class_8567 self, net.minecraft.class_169 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_8567.method_51865(DamageSource)Z was deleted in 1.21.2 with no direct replacement; new EndDragonFight API exposes no method accepting a class_169 (DamageSource) parameter, so the old contract cannot be expressed");
    }

    /** was net/minecraft/class_8567.method_51867(Lnet/minecraft/class_169;)Ljava/lang/Object; */
    public static java.lang.Object method_51867(net.minecraft.class_8567 self, net.minecraft.class_169 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_8567.method_51867(class_169) was removed in 1.21.2 with no direct replacement; no remaining method accepts a DamageSource");
    }

    /** was net/minecraft/class_8567.method_51868(Lnet/minecraft/class_169;)Ljava/lang/Object; */
    public static java.lang.Object method_51868(net.minecraft.class_8567 self, net.minecraft.class_169 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_51868(Scoreboard) was removed in 1.21.2 with no direct replacement; no remaining method on class_8567 accepts a Scoreboard or returns Object");
    }

    /** was net/minecraft/class_8567.method_51869(Lnet/minecraft/class_169;)Ljava/lang/Object; */
    public static java.lang.Object method_51869(net.minecraft.class_8567 self, net.minecraft.class_169 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_51869(class_169)->Object was removed in 1.21.2 with no direct replacement; method_51864 uses a different parameter type (class_2960) and a callback-based void pattern, so the old synchronous getter contract cannot be faithfully bridged");
        // CB-CONFIDENCE: high -- method is explicitly deleted with no direct replacement; new API surface has no method accepting class_169 or returning Object
    }

    /** was net/minecraft/class_8673.field_51520Z */
    public static boolean field_51520() {
        return false;
    }

    /** was net/minecraft/class_8679$class_6600.field_34819Ljava/util/LinkedHashSet; */
    public static java.util.LinkedHashSet field_34819() {
        throw new UnsupportedOperationException("CenturyBridge: field_34819 (LinkedHashSet) was removed from class_8679$class_6600 in 1.21.2 with no direct replacement; new fields (field_45627, field_45628, field_53929) have incompatible types and semantics");
    }

    /** was net/minecraft/class_8947.field_47260I */
    public static int field_47260() {
        throw new UnsupportedOperationException("CenturyBridge: field_47260 was a deleted static int constant from class_8947 with no direct replacement in 1.21.2; its value cannot be derived from the new API surface");
    }

    /** was net/minecraft/class_8949.method_55009()D */
    public static double method_55009(net.minecraft.class_8949 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_8949.method_55009()D was deleted in 1.21.2 with no direct replacement; the double value it returned is no longer exposed on class_8949");
    }

    /** was net/minecraft/class_8953.method_55038(Lnet/minecraft/class_8949;Lnet/minecraft/class_1309;)Z */
    public static boolean method_55038(net.minecraft.class_8953 self, net.minecraft.class_8949 a0, net.minecraft.class_1309 a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_55038(class_8949, class_1309)->boolean has no direct replacement; new API splits Raid and LivingEntity operations into separate method groups with no combined overload");
    }

    /** was net/minecraft/class_8998.field_47509Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_47509() {
        throw new UnsupportedOperationException("CenturyBridge: field_47509 (RegistryKey) was deleted in 1.21.2; remaining fields field_47507/field_47508 are different keys and cannot substitute");
    }

    /** was net/minecraft/class_9094.field_48073Lnet/minecraft/class_9145; */
    public static net.minecraft.class_9145 field_48073() {
        throw new UnsupportedOperationException("CenturyBridge: field_48073 was deleted in 1.21.2 with no direct replacement among the remaining class_9145 constants");
        // CB-CONFIDENCE: high -- the field is explicitly marked deleted with no direct replacement; returning a sibling constant would be a silent semantic mismatch
    }

    /** was net/minecraft/class_9135$13.field_51473Lnet/minecraft/class_9139; */
    public static net.minecraft.class_9139 field_51473() {
        throw new UnsupportedOperationException("CenturyBridge: field_51473 was a static class_9139 constant on class_9135$13, deleted in 1.21.2 with no static replacement; the codec is now passed via constructor and stored in instance field field_49697, so the original static constant cannot be reconstructed");
        // CB-CONFIDENCE: medium -- field_49697 matches the type but the constructor strongly implies it is an instance field, not a static one; the old static constant's value is unknowable from the new API surface alone
    }

    /** was net/minecraft/class_9135$16.field_48574Ljava/util/function/Function; */
    public static java.util.function.Function field_48574() {
        throw new UnsupportedOperationException("CenturyBridge: field_48574 was a Function in the old Either stream codec (class_9135$16) with no direct replacement in 1.21.2; the codec was restructured to use method_58008/method_58009 with Either directly, eliminating the need for this function field");
        // CB-CONFIDENCE: medium -- the field is explicitly marked deleted with no direct replacement; the Either codec was restructured in 1.21.2, but the original Function's exact semantics cannot be determined from the new API surface alone
    }

    /** was net/minecraft/class_9135$17.field_49690Lnet/minecraft/class_9139; */
    public static net.minecraft.class_9139 field_49690() {
        throw new UnsupportedOperationException("CenturyBridge: class_9135$17.field_49690 was deleted with no direct replacement; the static PacketCodec constant it held cannot be reconstructed from the remaining IntFunction/ToIntFunction instance fields");
    }

    /** was net/minecraft/class_9135$18.field_51475Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_51475() {
        throw new UnsupportedOperationException("CenturyBridge: field_51475 was a static RegistryKey on class_9135$18 with no static equivalent in the new API; the new class stores the value as instance field_53746");
        // CB-CONFIDENCE: medium -- the old static field has no direct static replacement; the new class refactored it into an instance field, so the original constant value cannot be reconstructed from the new API surface alone
    }

    /** was net/minecraft/class_1355.<init>(Ljava/util/function/Supplier;)V */
    public static net.minecraft.class_1355 cbNewclass_1355(java.util.function.Supplier a0) {
        return new net.minecraft.class_1355();
    }

    /** was net/minecraft/class_1400.<init>(Lnet/minecraft/class_1308;Ljava/lang/Class;IZZLjava/util/function/Predicate;)V */
    public static net.minecraft.class_1400 cbNewclass_1400(net.minecraft.class_1308 a0, java.lang.Class a1, int a2, boolean a3, boolean a4, java.util.function.Predicate a5) {
        return new net.minecraft.class_1400(a0, a1, a2, a3, a4, a5 == null ? null : (net.minecraft.class_4051.class_10254) (net.minecraft.class_1309 entity, net.minecraft.class_3218 world) -> a5.test(entity));
    }

    /** was net/minecraft/class_1400.<init>(Lnet/minecraft/class_1308;Ljava/lang/Class;ZLjava/util/function/Predicate;)V */
    public static net.minecraft.class_1400 cbNewclass_1400(net.minecraft.class_1308 a0, java.lang.Class a1, boolean a2, java.util.function.Predicate a3) {
        if (a3 == null) {
            return new net.minecraft.class_1400(a0, a1, a2);
        }
        return new net.minecraft.class_1400(a0, a1, a2, (net.minecraft.class_1309 entity, net.minecraft.class_3218 world) -> a3.test(entity));
    }

    /** was net/minecraft/class_1404.<init>(Lnet/minecraft/class_1321;Ljava/lang/Class;ZLjava/util/function/Predicate;)V */
    public static net.minecraft.class_1404 cbNewclass_1404(net.minecraft.class_1321 a0, java.lang.Class a1, boolean a2, java.util.function.Predicate a3) {
        return new net.minecraft.class_1404(a0, a1, a2, (net.minecraft.class_1309 entity, net.minecraft.class_3218 world) -> a3.test(entity));
    }

    /** was net/minecraft/class_1680.<init>(Lnet/minecraft/class_1937;Lnet/minecraft/class_1309;)V */
    public static net.minecraft.class_1680 cbNewclass_1680(net.minecraft.class_1937 a0, net.minecraft.class_1309 a1) {
        return new net.minecraft.class_1680(a0, a1, CbDefaults.THROWN_DEFAULT_ITEM);
    }

    /** was net/minecraft/class_1681.<init>(Lnet/minecraft/class_1937;DDD)V */
    public static net.minecraft.class_1681 cbNewclass_1681(net.minecraft.class_1937 a0, double a1, double a2, double a3) {
        return new net.minecraft.class_1681(a0, a1, a2, a3, CbDefaults.THROWN_DEFAULT_ITEM);
    }

    /** was net/minecraft/class_1686.<init>(Lnet/minecraft/class_1937;Lnet/minecraft/class_1309;)V */
    public static net.minecraft.class_1686 cbNewclass_1686(net.minecraft.class_1937 a0, net.minecraft.class_1309 a1) {
        return new net.minecraft.class_1686(a0, a1, CbDefaults.THROWN_DEFAULT_ITEM);
    }

    /** was net/minecraft/class_1758.<init>(Lnet/minecraft/class_1792$class_1793;Lnet/minecraft/class_1299;I)V */
    public static net.minecraft.class_1758 cbNewclass_1758(net.minecraft.class_1792.class_1793 a0, net.minecraft.class_1299 a1, int a2) {
        return new net.minecraft.class_1758(a1, a2, a0);
    }

    /** was net/minecraft/class_1822.<init>(Lnet/minecraft/class_1792$class_1793;Lnet/minecraft/class_2248;Lnet/minecraft/class_2248;)V */
    public static net.minecraft.class_1822 cbNewclass_1822(net.minecraft.class_1792.class_1793 a0, net.minecraft.class_2248 a1, net.minecraft.class_2248 a2) {
        return new net.minecraft.class_1822(a1, a2, a0);
    }

    /** was net/minecraft/class_1827.<init>(Lnet/minecraft/class_2248;Lnet/minecraft/class_2248;Lnet/minecraft/class_1792$class_1793;Lnet/minecraft/class_2350;)V */
    public static net.minecraft.class_1827 cbNewclass_1827(net.minecraft.class_2248 a0, net.minecraft.class_2248 a1, net.minecraft.class_1792.class_1793 a2, net.minecraft.class_2350 a3) {
        return new net.minecraft.class_1827(a0, a1, a3, a2);
    }

    /** was net/minecraft/class_1842.<init>([Lnet/minecraft/class_1293;)V */
    public static net.minecraft.class_1842 cbNewclass_1842(net.minecraft.class_1293[] a0) {
        return new net.minecraft.class_1842("", a0);
    }

    /** was net/minecraft/class_2424.<init>(Lnet/minecraft/class_1936;Lnet/minecraft/class_2338;Lnet/minecraft/class_2350$class_2351;)V */
    public static net.minecraft.class_2424 cbNewclass_2424(net.minecraft.class_1936 a0, net.minecraft.class_2338 a1, net.minecraft.class_2350.class_2351 a2) {
        return net.minecraft.class_2424.method_64314(a0, a1, a2);
    }

    /** was net/minecraft/class_2828$class_2829.<init>(DDDZ)V */
    public static net.minecraft.class_2828.class_2829 cbNewclass_2828class_2829(double a0, double a1, double a2, boolean a3) {
        return new net.minecraft.class_2828.class_2829(a0, a1, a2, a3, false);
        // CB-CONFIDENCY: medium -- old boolean maps to first new boolean; second boolean is new with false default to preserve old behavior
    }

    /** was net/minecraft/class_2828$class_2830.<init>(DDDFFZ)V */
    public static net.minecraft.class_2828.class_2830 cbNewclass_2828class_2830(double a0, double a1, double a2, float a3, float a4, boolean a5) {
        return new net.minecraft.class_2828.class_2830(a0, a1, a2, a3, a4, a5, false);
    }

    /** was net/minecraft/class_2828$class_2831.<init>(FFZ)V */
    public static net.minecraft.class_2828.class_2831 cbNewclass_2828class_2831(float a0, float a1, boolean a2) {
        return new net.minecraft.class_2828.class_2831(a0, a1, a2, false);
    }

    /** was net/minecraft/class_2828$class_5911.<init>(Z)V */
    public static net.minecraft.class_2828.class_5911 cbNewclass_2828class_5911(boolean a0) {
        return new net.minecraft.class_2828.class_5911(a0, false);
    }

    /** was net/minecraft/class_3441.<init>()V */
    public static net.minecraft.class_3441 cbNewclass_3441() {
        return new net.minecraft.class_3441((identifier, consumer) -> {});
    }

    /** was net/minecraft/class_3503.<init>(Ljava/util/function/Function;Ljava/lang/String;)V */
    public static net.minecraft.class_3503 cbNewclass_3503(java.util.function.Function a0, java.lang.String a1) {
        return new net.minecraft.class_3503((net.minecraft.class_2960 id, boolean b) -> (java.util.Optional) a0.apply(id), a1);
    }

    /** was net/minecraft/class_3760.<init>(Lnet/minecraft/class_3763;Ljava/lang/Class;IZZLjava/util/function/Predicate;)V */
    public static net.minecraft.class_3760 cbNewclass_3760(net.minecraft.class_3763 a0, java.lang.Class a1, int a2, boolean a3, boolean a4, java.util.function.Predicate a5) {
        return new net.minecraft.class_3760(a0, a1, a2, a3, a4, (entity, world) -> a5.test(entity));
    }

    /** was net/minecraft/class_3909.<init>(Lnet/minecraft/class_3763;Ljava/lang/Class;ZLjava/util/function/Predicate;)V */
    public static net.minecraft.class_3909 cbNewclass_3909(net.minecraft.class_3763 a0, java.lang.Class a1, boolean a2, java.util.function.Predicate a3) {
        return new net.minecraft.class_3909(a0, a1, a2, (net.minecraft.class_1309 entity, net.minecraft.class_3218 world) -> a3.test(entity));
        // CB-CONFIDENCE: medium -- the old Predicate (single-arg) is adapted to the new two-arg functional interface class_4051$class_10254 by ignoring the added ServerWorld parameter; inferred that the old predicate tested Entity (class_1309) which matches the new method_18303's first parameter
    }

    /** was net/minecraft/class_4010$class_4046.<init>(Ljava/lang/String;Lnet/minecraft/class_3696;Lnet/minecraft/class_3696;Ljava/util/concurrent/atomic/AtomicLong;Ljava/util/concurrent/atomic/AtomicLong;)V */
    public static net.minecraft.class_4010.class_4046 cbNewclass_4010class_4046(java.lang.String a0, net.minecraft.class_3696 a1, net.minecraft.class_3696 a2, java.util.concurrent.atomic.AtomicLong a3, java.util.concurrent.atomic.AtomicLong a4) {
        return new net.minecraft.class_4010.class_4046(a0, a3, a4);
    }

    /** was net/minecraft/class_4309.<init>(Lcom/google/gson/Gson;Ljava/lang/String;)V */
    public static net.minecraft.class_4309 cbNewclass_4309(com.google.gson.Gson a0, java.lang.String a1) {
        throw new UnsupportedOperationException("CenturyBridge: Cannot convert Gson to Codec; the old constructor accepted a Gson for JSON parsing but the new constructor requires a typed Codec with no Gson equivalent");
    }

    /** was net/minecraft/class_5485.<init>(Ljava/util/Map;Ljava/util/List;)V */
    public static net.minecraft.class_5485 cbNewclass_5485(java.util.Map a0, java.util.List a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_5485 old ctor took Map of weighted pool elements; new ctor requires class_6885 (RegistryEntryList) backed by registry class_6880 entries — cannot fabricate RegistryEntryList from a raw Map without a registry to wrap elements");
    }

    /** was net/minecraft/class_630$class_593.field_21618Lorg/joml/Vector3f; */
    public static org.joml.Vector3f field_21618() {
        return new org.joml.Vector3f();
        // CB-CONFIDENCY: low -- the field was deleted with no replacement; returning a zero vector is a best-guess default for a static Vector3f on a cuboid/origin-related class, but the exact original value and any shared-mutability semantics are unknown
    }

    /** was net/minecraft/class_630$class_618.field_3605Lorg/joml/Vector3f; */
    public static org.joml.Vector3f field_3605() {
        throw new UnsupportedOperationException("CenturyBridge: field_3605 (static Vector3f) was deleted in 1.21.2 with no direct replacement; constant value cannot be inferred from new API surface");
    }

    /** was net/minecraft/class_6582.<init>(Ljava/util/List;)V */
    public static net.minecraft.class_6582 cbNewclass_6582(java.util.List a0) {
        net.minecraft.class_6568.class_6569[] arr = new net.minecraft.class_6568.class_6569[a0.size()];
        for (int i = 0; i < a0.size(); i++) {
            arr[i] = (net.minecraft.class_6568.class_6569) a0.get(i);
        }
        return new net.minecraft.class_6582(arr);
    }

    /** was net/minecraft/class_6757.<init>(Ljava/util/function/LongPredicate;Ljava/util/function/Supplier;)V */
    public static net.minecraft.class_6757 cbNewclass_6757(java.util.function.LongPredicate a0, java.util.function.Supplier a1) {
        return new net.minecraft.class_6757(a0);
    }

    /** was net/minecraft/class_7264.<init>(Lnet/minecraft/class_1299;Lnet/minecraft/class_1937;)V */
    public static net.minecraft.class_7264 cbNewclass_7264(net.minecraft.class_1299 a0, net.minecraft.class_1937 a1) {
        return new net.minecraft.class_7264(a0, a1, () -> null);
    }

    /** was net/minecraft/class_7430.<init>(Lnet/minecraft/class_1792$class_1793;Lnet/minecraft/class_6862;)V */
    public static net.minecraft.class_7430 cbNewclass_7430(net.minecraft.class_1792.class_1793 a0, net.minecraft.class_6862 a1) {
        return new net.minecraft.class_7430(a1, a0);
    }

    /** was net/minecraft/class_7444.<init>(Lnet/minecraft/class_6880;IF)V */
    public static net.minecraft.class_7444 cbNewclass_7444(net.minecraft.class_6880 a0, int a1, float a2) {
        return new net.minecraft.class_7444(a0, (float) a1, a2, net.minecraft.class_2561.method_30163(""));
    }

    /** was net/minecraft/class_8786.<init>(Lnet/minecraft/class_2960;Lnet/minecraft/class_1860;)V */
    public static net.minecraft.class_8786 cbNewclass_8786(net.minecraft.class_2960 a0, net.minecraft.class_1860 a1) {
        return new net.minecraft.class_8786(net.minecraft.class_5321.method_29180(a0), a1);
    }

    /** was net/minecraft/class_8957.<init>(IILnet/minecraft/class_2371;Ljava/util/Optional;)V */
    public static net.minecraft.class_8957 cbNewclass_8957(int a0, int a1, net.minecraft.class_2371 a2, java.util.Optional a3) {
        return new net.minecraft.class_8957(a0, a1, a2, a3);
    }

    /** was net/minecraft/class_9135$2.field_51478I */
    public static int field_51478() {
        throw new UnsupportedOperationException("CenturyBridge: field_51478 was a deleted static int constant in class_9135$2 (Double packet codec) with no direct replacement and unknown value");
        // CB-CONFIDENCE: low -- the field is deleted with no replacement and its value cannot be determined from the new API surface alone
    }

    /** was net/minecraft/class_9139$11.field_48597Ljava/util/function/Function; */
    public static java.util.function.Function field_48597() {
        try {
            java.lang.reflect.Field f = Class.forName("net.minecraft.class_9139$13").getDeclaredField("field_48597");
            f.setAccessible(true);
            return (java.util.function.Function) f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /** was net/minecraft/class_9139$7.field_48586Ljava/lang/Object; */
    public static java.lang.Object field_48586() {
        return null;
    }

    /** was net/minecraft/class_9383$class_9385.<init>(Lnet/minecraft/class_5455$class_6890;)V */
    public static net.minecraft.class_9383.class_9385 cbNewclass_9383class_9385(net.minecraft.class_5455.class_6890 a0) {
        return new net.minecraft.class_9383.class_9385((net.minecraft.class_7225.class_7874) a0);
    }

    /** was net/minecraft/class_9383$class_9385.method_58289()Lnet/minecraft/class_5455$class_6890; */
    public static net.minecraft.class_5455.class_6890 method_58289(net.minecraft.class_9383.class_9385 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_58289 was removed in 1.21.2; the Registry.Loader (class_5455$class_6890) it returned has no equivalent in the refactored registry wrapper API");
    }

    /** was net/minecraft/class_9424$class_9425.method_58428(Lnet/minecraft/class_6862;F)Lnet/minecraft/class_9424$class_9425; */
    public static net.minecraft.class_9424.class_9425 method_58428(net.minecraft.class_9424.class_9425 self, net.minecraft.class_6862 a0, float a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_58428 accepted class_6862 but new API methods (method_58431/method_58434) require class_6885; no conversion path available in provided API surface");
    }

    /** was net/minecraft/class_9424$class_9425.method_58433(Lnet/minecraft/class_6862;F)Lnet/minecraft/class_9424$class_9425; */
    public static net.minecraft.class_9424.class_9425 method_58433(net.minecraft.class_9424.class_9425 self, net.minecraft.class_6862 a0, float a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_58433(class_6862, float) was removed; replacement methods method_58431/method_58434 take class_6885 instead of class_6862 with no conversion path");
        // CB-CONFIDENCE: high -- the old parameter type class_6862 was replaced by class_6885 in the new API; with no inheritance relationship or conversion available between the two types, and two ambiguous candidate replacements (method_58431 vs method_58434), there is no faithful bridge
    }

    /** was net/minecraft/class_9761.method_60470()I */
    public static int method_60470(net.minecraft.class_9761 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_9761.method_60470()I was deleted in 1.21.2 with no direct replacement; the remaining int-returning methods (method_14005, method_17208) are pre-existing and not semantically equivalent to the removed method");
        // CB-CONFIDENCE: medium -- the prompt confirms deletion with no direct replacement, but I cannot rule out that one of the surviving int methods (method_14005/method_17208) was the original semantic equivalent repurposed under an old id
    }

    /** was net/minecraft/class_9824.field_52259I */
    public static int field_52259() {
        throw new UnsupportedOperationException("CenturyBridge: field_52259 was a static int constant deleted with no direct replacement; its value cannot be inferred from the new class_9824 API surface");
    }

    /** was net/minecraft/class_9824.field_52260Lnet/minecraft/class_7654; */
    public static net.minecraft.class_7654 field_52260() {
        throw new UnsupportedOperationException("CenturyBridge: field_52260 of type class_7654 was deleted from class_9824 with no direct replacement and no remaining API path produces a class_7654 value");
        // CB-CONFIDENCE: high -- the field is explicitly deleted, no field or method in the new surface yields class_7654, so fabrication would be a silent corruption
    }

    /** was net/minecraft/class_1044.field_5203Z */
    public static boolean field_5203() {
        throw new UnsupportedOperationException("CenturyBridge: old mipmap field was per-instance state, so a receiverless static helper cannot preserve its value");
    }

    /** was net/minecraft/class_1269.field_33562Lnet/minecraft/class_1269; */
    public static net.minecraft.class_1269 field_33562() {
        throw new UnsupportedOperationException("CenturyBridge: CONSUME_PARTIAL has no 1.21.2 equivalent preserving its no-stat-increment semantics");
    }

    /** was net/minecraft/class_1269.field_51370Lnet/minecraft/class_1269; */
    public static net.minecraft.class_1269 field_51370() {
        return net.minecraft.class_1269.field_52422;
    }

    /** was net/minecraft/class_1269.method_23666()Z */
    public static boolean method_23666(net.minecraft.class_1269 self) {
        return self == net.minecraft.class_1269.field_5812 || self == net.minecraft.class_1269.field_52422;
    }

    /** was net/minecraft/class_1269.method_29236(Z)Lnet/minecraft/class_1269; */
    public static net.minecraft.class_1269 method_29236(net.minecraft.class_1269 self, boolean a0) {
        return a0 ? net.minecraft.class_1269.field_5812 : net.minecraft.class_1269.field_5811;
    }

    /** was net/minecraft/class_1273.<init>(Ljava/lang/String;)V */
    public static net.minecraft.class_1273 cbNewclass_1273(java.lang.String a0) {
        throw new UnsupportedOperationException("CenturyBridge: the legacy String representation cannot be converted to class_2073 using the surviving API surface");
    }

    /** was net/minecraft/class_1297.field_5973F */
    public static float field_5973() {
        throw new UnsupportedOperationException("CenturyBridge: old instance field field_5973 requires an entity receiver, but the static zero-argument shim has none");
    }

    /** was net/minecraft/class_1297.method_5679(Lnet/minecraft/class_1282;)Z */
    public static boolean method_5679(net.minecraft.class_1297 self, net.minecraft.class_1282 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new Entity API has no equivalent for source-specific invulnerability checks");
    }

    /** was net/minecraft/class_1297.method_5852()V */
    public static void method_5852(net.minecraft.class_1297 self) {
        throw new UnsupportedOperationException("CenturyBridge: the old block-collision callback scan has no equivalent in the new Entity API");
    }

    /** was net/minecraft/class_1308.method_6773(Lnet/minecraft/class_1799;)Z */
    public static boolean method_6773(net.minecraft.class_1308 self, net.minecraft.class_1799 a0) {
        return a0.method_7909() instanceof net.minecraft.class_1811 && self.method_25938((net.minecraft.class_1811) a0.method_7909());
    }

    /** was net/minecraft/class_1309.field_30063I */
    public static int field_30063() {
        return 4;
    }

    /** was net/minecraft/class_1309.method_18866(Lnet/minecraft/class_1937;Lnet/minecraft/class_1799;Lnet/minecraft/class_4174;)Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 method_18866(net.minecraft.class_1309 self, net.minecraft.class_1937 a0, net.minecraft.class_1799 a1, net.minecraft.class_4174 a2) {
        throw new UnsupportedOperationException("CenturyBridge: the removed item-use completion helper has no faithful equivalent in the new class_1309 API");
    }

    /** was net/minecraft/class_1481.field_6921Ljava/util/function/Predicate; */
    public static java.util.function.Predicate field_6921() {
        return entity -> net.minecraft.class_1481.field_6921.method_18303((net.minecraft.class_1309) entity, (net.minecraft.class_3218) ((net.minecraft.class_1309) entity).method_37908());
    }

    /** was net/minecraft/class_1510.field_7010I */
    public static int field_7010() {
        throw new UnsupportedOperationException("CenturyBridge: the old field is mutable per-instance state, but this static no-argument shim has no receiver from which to read it");
    }

    /** was net/minecraft/class_1528.method_6879(I)F */
    public static float method_6879(net.minecraft.class_1528 self, int a0) {
        return self.method_61488()[a0];
    }

    /** was net/minecraft/class_1528.method_6887(I)F */
    public static float method_6887(net.minecraft.class_1528 self, int a0) {
        return self.method_61487()[a0];
    }

    /** was net/minecraft/class_1541.field_46776Ljava/lang/String; */
    public static java.lang.String field_46776() {
        return net.minecraft.class_1541.field_46776;
    }

    /** was net/minecraft/class_1646.method_35200()Z */
    public static boolean method_35200(net.minecraft.class_1646 self) {
        throw new UnsupportedOperationException("CenturyBridge: the villager natural-spawn state queried by method_35200 was removed and is not exposed by the new API");
    }

    /** was net/minecraft/class_1662.method_20478(Lnet/minecraft/class_1799;I)V */
    public static void method_20478(net.minecraft.class_1662 self, net.minecraft.class_1799 a0, int a1) {
        self.method_61501(a0.method_7909(), Math.min(a1, a0.method_7947()));
    }

    /** was net/minecraft/class_1662.method_7403(Lnet/minecraft/class_8786;ILit/unimi/dsi/fastutil/ints/IntList;)I */
    public static int method_7403(net.minecraft.class_1662 self, net.minecraft.class_8786 a0, int a1, it.unimi.dsi.fastutil.ints.IntList a2) {
        throw new UnsupportedOperationException("CenturyBridge: the new generic RecipeMatcher requires raw ingredients and an item callback, so the old RecipeEntry and IntList contract cannot be preserved");
    }

    /** was net/minecraft/class_1662.method_7404(Lnet/minecraft/class_1799;)V */
    public static void method_7404(net.minecraft.class_1662 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new generic RecipeMatcher cannot reproduce the old ItemStack eligibility filtering");
    }

    /** was net/minecraft/class_1662.method_7407(Lnet/minecraft/class_8786;Lit/unimi/dsi/fastutil/ints/IntList;)I */
    public static int method_7407(net.minecraft.class_1662 self, net.minecraft.class_8786 a0, it.unimi.dsi.fastutil.ints.IntList a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new RecipeMatcher requires a raw ingredient list and item callback, which cannot be derived from the legacy RecipeEntry and IntList contract");
    }

    /** was net/minecraft/class_1662.method_7410(I)Z */
    public static boolean method_7410(net.minecraft.class_1662 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new object-keyed recipe matcher cannot resolve an old raw item ID without an ID-to-key conversion API");
    }

    /** was net/minecraft/class_1680.<init>(Lnet/minecraft/class_1937;DDD)V */
    public static net.minecraft.class_1680 cbNewclass_1680(net.minecraft.class_1937 a0, double a1, double a2, double a3) {
        return new net.minecraft.class_1680(a0, a1, a2, a3, CbDefaults.THROWN_DEFAULT_ITEM);
    }

    /** was net/minecraft/class_1683.<init>(Lnet/minecraft/class_1937;DDD)V */
    public static net.minecraft.class_1683 cbNewclass_1683(net.minecraft.class_1937 a0, double a1, double a2, double a3) {
        return new net.minecraft.class_1683(a0, a1, a2, a3, new net.minecraft.class_1799(net.minecraft.class_1802.field_8543));
    }

    /** was net/minecraft/class_1683.<init>(Lnet/minecraft/class_1937;Lnet/minecraft/class_1309;)V */
    public static net.minecraft.class_1683 cbNewclass_1683(net.minecraft.class_1937 a0, net.minecraft.class_1309 a1) {
        return new net.minecraft.class_1683(a0, a1, new net.minecraft.class_1799(net.minecraft.class_1802.field_8543));
    }

    /** was net/minecraft/class_1684.<init>(Lnet/minecraft/class_1937;Lnet/minecraft/class_1309;)V */
    public static net.minecraft.class_1684 cbNewclass_1684(net.minecraft.class_1937 a0, net.minecraft.class_1309 a1) {
        return new net.minecraft.class_1684(a0, a1, CbDefaults.THROWN_DEFAULT_ITEM);
    }

    /** was net/minecraft/class_1694.<init>(Lnet/minecraft/class_1937;DDD)V */
    public static net.minecraft.class_1694 cbNewclass_1694(net.minecraft.class_1937 a0, double a1, double a2, double a3) {
        throw new UnsupportedOperationException("CenturyBridge: the surviving constructor requires a class_1299 entity type, but no guaranteed class_1694 entity type constant or equivalent factory is available");
    }

    /** was net/minecraft/class_1695.<init>(Lnet/minecraft/class_1937;DDD)V */
    public static net.minecraft.class_1695 cbNewclass_1695(net.minecraft.class_1937 a0, double a1, double a2, double a3) {
        throw new UnsupportedOperationException("CenturyBridge: the surviving constructor requires a class_1299 entity type, but no equivalent type source or factory is available from the old arguments");
    }

    /** was net/minecraft/class_1696.<init>(Lnet/minecraft/class_1937;DDD)V */
    public static net.minecraft.class_1696 cbNewclass_1696(net.minecraft.class_1937 a0, double a1, double a2, double a3) {
        throw new UnsupportedOperationException("CenturyBridge: the surviving constructor requires a class_1299 entity type, but no equivalent class_1696 factory or entity-type constant is available in the provided API surface");
    }

    /** was net/minecraft/class_1696.field_7736D */
    public static double field_7736() {
        throw new UnsupportedOperationException("CenturyBridge: the old mutable per-entity pushZ value cannot be represented by a receiverless static field shim");
    }

    /** was net/minecraft/class_1696.field_7737D */
    public static double field_7737() {
        throw new UnsupportedOperationException("CenturyBridge: old field_7737 was per-instance pushX state, but this static field shim has no receiver to read the replacement vector state");
    }

    /** was net/minecraft/class_1697.<init>(Lnet/minecraft/class_1937;DDD)V */
    public static net.minecraft.class_1697 cbNewclass_1697(net.minecraft.class_1937 a0, double a1, double a2, double a3) {
        throw new UnsupportedOperationException("CenturyBridge: the surviving constructor requires a class_1299 entity type, but the provided API exposes no reliable way to obtain the required type from the old arguments");
    }

    /** was net/minecraft/class_1699.<init>(Lnet/minecraft/class_1937;DDD)V */
    public static net.minecraft.class_1699 cbNewclass_1699(net.minecraft.class_1937 a0, double a1, double a2, double a3) {
        throw new UnsupportedOperationException("CenturyBridge: the surviving constructor requires a spawner-minecart EntityType, but no corresponding type constant or factory is available in the provided API surface");
    }

    /** was net/minecraft/class_1700.<init>(Lnet/minecraft/class_1937;DDD)V */
    public static net.minecraft.class_1700 cbNewclass_1700(net.minecraft.class_1937 a0, double a1, double a2, double a3) {
        net.minecraft.class_1700 v = new net.minecraft.class_1700(net.minecraft.class_1299.field_6058, a0);
        v.method_23327(a1, a2, a3);
        return v;
    }

    /** was net/minecraft/class_1702.method_35217()I */
    public static int method_35217(net.minecraft.class_1702 self) {
        throw new UnsupportedOperationException("CenturyBridge: previous food level state was removed from HungerManager and the new API exposes no equivalent");
    }

    /** was net/minecraft/class_1720.<init>(Lnet/minecraft/class_3917;Lnet/minecraft/class_3956;Lnet/minecraft/class_5421;ILnet/minecraft/class_1661;Lnet/minecraft/class_1263;Lnet/minecraft/class_3913;)V */
    public static net.minecraft.class_1720 cbNewclass_1720(net.minecraft.class_3917 a0, net.minecraft.class_3956 a1, net.minecraft.class_5421 a2, int a3, net.minecraft.class_1661 a4, net.minecraft.class_1263 a5, net.minecraft.class_3913 a6) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires a class_5321 registry key that cannot be derived faithfully from the old arguments");
    }

    /** was net/minecraft/class_1720.<init>(Lnet/minecraft/class_3917;Lnet/minecraft/class_3956;Lnet/minecraft/class_5421;ILnet/minecraft/class_1661;)V */
    public static net.minecraft.class_1720 cbNewclass_1720(net.minecraft.class_3917 a0, net.minecraft.class_3956 a1, net.minecraft.class_5421 a2, int a3, net.minecraft.class_1661 a4) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires a class_5321 recipe property-set key that cannot be derived faithfully from the old constructor arguments");
    }

    /** was net/minecraft/class_1729.method_7655()I */
    public static int method_7655(net.minecraft.class_1729 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_1729 no longer exposes an equivalent for the deleted integer-returning method_7655 contract");
    }

    /** was net/minecraft/class_1729.method_7656()I */
    public static int method_7656(net.minecraft.class_1729 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_1729 no longer exposes an equivalent for the removed integer-returning method_7656");
    }

    /** was net/minecraft/class_1737.method_7683(Lnet/minecraft/class_1662;)V */
    public static void method_7683(net.minecraft.class_1737 self, net.minecraft.class_1662 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new RecipeFinder API cannot wrap or populate the caller-provided class_1662 RecipeMatcher because its matcher field is private and no accepting constructor or transfer method exists");
    }

    /** was net/minecraft/class_1740.<init>()V */
    public static net.minecraft.class_1740 cbNewclass_1740() {
        throw new UnsupportedOperationException("CenturyBridge: class_1740 has no surviving constructor or static factory in the provided API surface");
    }

    /** was net/minecraft/class_1740.field_7887Lnet/minecraft/class_6880; */
    public static net.minecraft.class_6880 field_7887() {
        return net.minecraft.class_1740.field_7887.comp_2300();
    }

    /** was net/minecraft/class_1740.method_56697(Lnet/minecraft/class_2378;)Lnet/minecraft/class_6880; */
    public static net.minecraft.class_6880 method_56697(net.minecraft.class_1740 self, net.minecraft.class_2378 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_1740 API exposes no instance-to-class_6880 lookup equivalent for the deleted method");
    }

    /** was net/minecraft/class_1741.<init>(Ljava/util/Map;ILnet/minecraft/class_6880;Ljava/util/function/Supplier;Ljava/util/List;FF)V */
    public static net.minecraft.class_1741 cbNewclass_1741(java.util.Map a0, int a1, net.minecraft.class_6880 a2, java.util.function.Supplier a3, java.util.List a4, float a5, float a6) {
        throw new UnsupportedOperationException("CenturyBridge: old Supplier and List arguments cannot be converted to the required class_6862 and class_2960 constructor arguments");
    }

    /** was net/minecraft/class_1741.comp_2302()Ljava/util/List; */
    public static java.util.List comp_2302(net.minecraft.class_1741 self) {
        throw new UnsupportedOperationException("CenturyBridge: the old list component was removed and the new API exposes no equivalent list or derivation source");
    }

    /** was net/minecraft/class_1796$class_1797.field_8027I */
    public static int field_8027() {
        throw new UnsupportedOperationException("CenturyBridge: field_8027 was a per-instance end tick and cannot be reconstructed by a zero-argument static shim");
    }

    /** was net/minecraft/class_1796$class_1797.field_8028I */
    public static int field_8028() {
        throw new UnsupportedOperationException("CenturyBridge: old field_8028 was per-instance startTick, but this static field shim has no receiver from which to call comp_3083()");
    }

    /** was net/minecraft/class_1860.method_17447()Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 method_17447(net.minecraft.class_1860 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new API cannot provide a context-free output; method_8116 requires both a recipe input and registry lookup");
    }

    /** was net/minecraft/class_1860.method_8110(Lnet/minecraft/class_7225$class_7874;)Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 method_8110(net.minecraft.class_1860 self, net.minecraft.class_7225.class_7874 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new method_8116 requires a class_9695 argument that the old contract does not provide");
    }

    /** was net/minecraft/class_1865.field_9030Lnet/minecraft/class_1865; */
    public static net.minecraft.class_1865 field_9030() {
        throw new UnsupportedOperationException("CenturyBridge: the suspicious stew recipe serializer was removed and the new API exposes no equivalent serializer");
    }

    /** was net/minecraft/class_1865.field_9041Lnet/minecraft/class_1865; */
    public static net.minecraft.class_1865 field_9041() {
        return net.minecraft.class_1865.field_54318;
    }

    /** was net/minecraft/class_1923$1.field_18681Lnet/minecraft/class_1923; */
    public static net.minecraft.class_1923 field_18681() {
        throw new UnsupportedOperationException("CenturyBridge: field_18681 moved to an instance field on class_1923$2, but this static shim has no receiver");
    }

    /** was net/minecraft/class_1923$1.field_18682I */
    public static int field_18682() {
        return 0;
    }

    /** was net/minecraft/class_1924.method_18461(Lnet/minecraft/class_4051;DDD)Lnet/minecraft/class_1657; */
    public static net.minecraft.class_1657 method_18461(net.minecraft.class_1924 self, net.minecraft.class_4051 a0, double a1, double a2, double a3) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_1924 API has no method that applies class_4051 target-selection semantics when finding the closest player");
    }

    /** was net/minecraft/class_1924.method_18463(Lnet/minecraft/class_4051;Lnet/minecraft/class_1309;DDD)Lnet/minecraft/class_1657; */
    public static net.minecraft.class_1657 method_18463(net.minecraft.class_1924 self, net.minecraft.class_4051 a0, net.minecraft.class_1309 a1, double a2, double a3, double a4) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_1924 API cannot apply the class_4051 target predicate required by the old closest-player contract");
    }

    /** was net/minecraft/class_1924.method_18468(Ljava/util/List;Lnet/minecraft/class_4051;Lnet/minecraft/class_1309;DDD)Lnet/minecraft/class_1309; */
    public static net.minecraft.class_1309 method_18468(net.minecraft.class_1924 self, java.util.List a0, net.minecraft.class_4051 a1, net.minecraft.class_1309 a2, double a3, double a4, double a5) {
        throw new UnsupportedOperationException("CenturyBridge: closest-entity selection moved off class_1924, and the new class_1924 API cannot express the old list-based TargetPredicate contract");
    }

    /** was net/minecraft/class_1928.<init>(Lcom/mojang/serialization/DynamicLike;)V */
    public static net.minecraft.class_1928 cbNewclass_1928(com.mojang.serialization.DynamicLike a0) {
        return new net.minecraft.class_1928(net.minecraft.class_7699.method_45397(), a0);
    }

    /** was net/minecraft/class_1928.<init>()V */
    public static net.minecraft.class_1928 cbNewclass_1928() {
        return new net.minecraft.class_1928(net.minecraft.class_7699.method_45397());
    }

    /** was net/minecraft/class_1936.method_39280(Lnet/minecraft/class_2338;Lnet/minecraft/class_2248;ILnet/minecraft/class_1953;)V */
    public static void method_39280(net.minecraft.class_1936 self, net.minecraft.class_2338 a0, net.minecraft.class_2248 a1, int a2, net.minecraft.class_1953 a3) {
        self.method_39284(a0, a1, a2, a3);
    }

    /** was net/minecraft/class_1936.method_39281(Lnet/minecraft/class_2338;Lnet/minecraft/class_3611;I)V */
    public static void method_39281(net.minecraft.class_1936 self, net.minecraft.class_2338 a0, net.minecraft.class_3611 a1, int a2) {
        throw new UnsupportedOperationException("CenturyBridge: the new API can construct an ordered fluid tick but exposes no equivalent operation on class_1936 that schedules it");
    }

    /** was net/minecraft/class_1936.method_39282(Lnet/minecraft/class_2338;Lnet/minecraft/class_3611;ILnet/minecraft/class_1953;)V */
    public static void method_39282(net.minecraft.class_1936 self, net.minecraft.class_2338 a0, net.minecraft.class_3611 a1, int a2, net.minecraft.class_1953 a3) {
        self.method_39284(a0, a1, a2, a3);
    }

    /** was net/minecraft/class_2232.method_9442(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)Lnet/minecraft/class_8786; */
    public static net.minecraft.class_8786 method_9442(net.minecraft.class_2232 self, com.mojang.brigadier.context.CommandContext a0, java.lang.String a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new IdentifierArgumentType API can only retrieve the identifier and cannot resolve it to an advancement entry");
    }

    /** was net/minecraft/class_2378.method_30517()Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 method_30517(net.minecraft.class_2378 self) {
        return self.method_46765();
    }

    /** was net/minecraft/class_2378.method_40260(Lnet/minecraft/class_6862;)Lnet/minecraft/class_6885$class_6888; */
    public static net.minecraft.class_6885.class_6888 method_40260(net.minecraft.class_2378 self, net.minecraft.class_6862 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new registry API cannot create or return a named entry list for an absent tag");
    }

    /** was net/minecraft/class_2378.method_40266(Lnet/minecraft/class_6862;)Ljava/util/Optional; */
    public static java.util.Optional method_40266(net.minecraft.class_2378 self, net.minecraft.class_6862 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new registry API no longer exposes the optional named entry list for a tag");
    }

    /** was net/minecraft/class_2378.method_40273()Ljava/util/stream/Stream; */
    public static java.util.stream.Stream method_40273(net.minecraft.class_2378 self) {
        return self.method_40272();
    }

    /** was net/minecraft/class_2378.method_46770()Lnet/minecraft/class_7876; */
    public static net.minecraft.class_7876 method_46770(net.minecraft.class_2378 self) {
        return self;
    }

    /** was net/minecraft/class_2578.<init>(Ljava/lang/String;Ljava/lang/String;)V */
    public static net.minecraft.class_2578 cbNewclass_2578(java.lang.String a0, java.lang.String a1) {
        return new net.minecraft.class_2578(com.mojang.datafixers.util.Either.right(a0), a1);
    }

    /** was net/minecraft/class_2579.<init>(Ljava/lang/String;Ljava/util/Optional;)V */
    public static net.minecraft.class_2579 cbNewclass_2579(java.lang.String a0, java.util.Optional a1) {
        throw new UnsupportedOperationException("CenturyBridge: cannot construct class_10104 because the old arguments provide no class_2300 value");
    }

    /** was net/minecraft/class_2579.field_33540Ljava/util/Optional; */
    public static java.util.Optional field_33540() {
        return java.util.Optional.empty();
    }

    /** was net/minecraft/class_2609.method_11195(Lnet/minecraft/class_1799;)Z */
    public static boolean method_11195(net.minecraft.class_2609 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: fuel validation now requires a net.minecraft.class_9895 fuel registry, which the old signature does not provide");
    }

    /** was net/minecraft/class_2791$class_6745.<init>(Lnet/minecraft/class_6761;Lnet/minecraft/class_6761;)V */
    public static net.minecraft.class_2791.class_6745 cbNewclass_2791class_6745(net.minecraft.class_6761 a0, net.minecraft.class_6761 a1) {
        throw new UnsupportedOperationException("CenturyBridge: converting SerializableTickScheduler instances to scheduled-tick lists requires the serialization time and type-name functions, which are not available to this constructor shim");
    }

    /** was net/minecraft/class_2852.<init>()V */
    public static net.minecraft.class_2852 cbNewclass_2852() {
        throw new UnsupportedOperationException("CenturyBridge: the removed no-argument constructor has no faithful equivalent because the surviving constructor requires chunk serialization state that cannot be derived without arguments");
    }

    /** was net/minecraft/class_2955.<init>(Lnet/minecraft/class_1729;)V */
    public static net.minecraft.class_2955 cbNewclass_2955(net.minecraft.class_1729 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires a handler, crafting mode, dimensions, and slot lists that cannot be derived from the old PlayerInventory argument");
    }

    /** was net/minecraft/class_314.field_17113Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_17113() {
        throw new UnsupportedOperationException("CenturyBridge: SMOKER_SEARCH was removed and the new API has no equivalent recipe-book category");
    }

    /** was net/minecraft/class_314.field_25779Ljava/util/List; */
    public static java.util.List field_25779() {
        return java.util.List.of(net.minecraft.class_314.field_17114);
    }

    /** was net/minecraft/class_314.field_25780Ljava/util/List; */
    public static java.util.List field_25780() {
        throw new UnsupportedOperationException("CenturyBridge: the removed recipe-book group list has no behaviorally equivalent representation in the new RecipeBookCategory API");
    }

    /** was net/minecraft/class_314.field_25781Ljava/util/List; */
    public static java.util.List field_25781() {
        throw new UnsupportedOperationException("CenturyBridge: the old furnace category list included field_1804, but the new API has no equivalent search category");
    }

    /** was net/minecraft/class_3417.field_15141Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_15141() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_15141.comp_349();
    }

    /** was net/minecraft/class_3417.field_20613Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_20613() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_20613.comp_349();
    }

    /** was net/minecraft/class_3417.field_20614Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_20614() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_20614.comp_349();
    }

    /** was net/minecraft/class_3417.field_20615Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_20615() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_20615.comp_349();
    }

    /** was net/minecraft/class_3417.field_50103Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_50103() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_50103.comp_349();
    }

    /** was net/minecraft/class_3439.field_15300Ljava/util/Set; */
    public static java.util.Set field_15300() {
        throw new UnsupportedOperationException("CenturyBridge: old field_15300 was a per-instance mutable recipe set, which cannot be reconstructed by a receiverless static field shim");
    }

    /** was net/minecraft/class_3439.method_14879(Lnet/minecraft/class_2960;)V */
    public static void method_14879(net.minecraft.class_3439 self, net.minecraft.class_2960 a0) {
        throw new UnsupportedOperationException("CenturyBridge: recipe state and Identifier-based removal were removed from RecipeBook in 1.21.2");
    }

    /** was net/minecraft/class_3439.method_14880(Lnet/minecraft/class_1729;)Z */
    public static boolean method_14880(net.minecraft.class_3439 self, net.minecraft.class_1729 a0) {
        return self.method_30176(a0.method_30264());
    }

    /** was net/minecraft/class_3503.field_15609Ljava/util/function/Function; */
    public static java.util.function.Function field_15609() {
        throw new UnsupportedOperationException("CenturyBridge: the old field is an instance-specific registry lookup function and cannot be reconstructed by a receiverless static field shim");
    }

    /** was net/minecraft/class_3751$class_4991.method_26401(Ljava/lang/String;)Ljava/util/Optional; */
    public static java.util.Optional method_26401(net.minecraft.class_3751.class_4991 self, java.lang.String a0) {
        for (net.minecraft.class_3751.class_4991 value : net.minecraft.class_3751.class_4991.values()) {
            if (value.method_15434().equals(a0)) {
                return java.util.Optional.of(value);
            }
        }
        return java.util.Optional.empty();
    }

    /** was net/minecraft/class_382$class_328.field_2003F */
    public static float field_2003() {
        return 0.01F;
    }

    /** was net/minecraft/class_382$class_328.field_2004F */
    public static float field_2004() {
        return 0.01F;
    }

    /** was net/minecraft/class_382$class_328.field_2005F */
    public static float field_2005() {
        return 0.01F;
    }

    /** was net/minecraft/class_382$class_328.field_2006F */
    public static float field_2006() {
        return 0.0F;
    }

    /** was net/minecraft/class_382$class_328.field_2007F */
    public static float field_2007() {
        return 0.01F;
    }

    /** was net/minecraft/class_382$class_328.field_2008F */
    public static float field_2008() {
        return 0.01F;
    }

    /** was net/minecraft/class_382$class_328.field_2009F */
    public static float field_2009() {
        throw new UnsupportedOperationException("CenturyBridge: old field_2009 was an instance-specific z-index, but the field shim has no receiver from which to obtain comp_3311()");
    }

    /** was net/minecraft/class_382$class_328.field_2010F */
    public static float field_2010() {
        return 0.01F;
    }

    /** was net/minecraft/class_382$class_328.field_20911F */
    public static float field_20911() {
        throw new UnsupportedOperationException("CenturyBridge: field_20911 is per-instance z-index state and cannot be recovered by a receiverless static helper");
    }

    /** was net/minecraft/class_3847$class_3848.method_17346()Ljava/lang/Runnable; */
    public static java.lang.Runnable method_17346(net.minecraft.class_3847.class_3848 self) {
        return self.method_16909();
    }

    /** was net/minecraft/class_3847$class_3907.field_17278I */
    public static int field_17278() {
        throw new UnsupportedOperationException("CenturyBridge: old field_17278 was per-instance priority state and cannot be reconstructed by a receiverless static helper");
    }

    /** was net/minecraft/class_3864.method_16992(Lnet/minecraft/class_5485$class_5495;)V */
    public static void method_16992(net.minecraft.class_3864 self, net.minecraft.class_5485.class_5495 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the seagrass-on-stone biome feature was removed and the new API exposes no equivalent operation");
    }

    /** was net/minecraft/class_39.field_224Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_224() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted registry key's identifier cannot be recovered from the provided truncated API surface");
    }

    /** was net/minecraft/class_39.field_814Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_814() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted field_814 registry key identity is not recoverable from the provided new API surface");
    }

    /** was net/minecraft/class_39.field_844Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_844() {
        return net.minecraft.class_5321.method_29179(net.minecraft.class_5321.method_29180(net.minecraft.class_2960.method_60656("loot_table")), net.minecraft.class_2960.method_60656("empty"));
    }

    /** was net/minecraft/class_4146.method_43082()I */
    public static int method_43082(net.minecraft.class_4146 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_4146 API exposes no equivalent integer-valued state or accessor");
    }

    /** was net/minecraft/class_4351.field_19601J */
    public static long field_19601() {
        throw new UnsupportedOperationException("CenturyBridge: old field_19601 was mutable per-instance upload progress state and cannot be represented by a receiverless static field shim");
    }

    /** was net/minecraft/class_4351.field_19602J */
    public static long field_19602() {
        throw new UnsupportedOperationException("CenturyBridge: old field_19602 was per-instance totalBytes, but the static shim has no receiver");
    }

    /** was net/minecraft/class_4466.method_21801(Lnet/minecraft/class_4466;)Lnet/minecraft/class_5819; */
    public static net.minecraft.class_5819 method_21801(net.minecraft.class_4466 self, net.minecraft.class_4466 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_4466 API provides no identifiable equivalent for method_21801 with preserved semantics");
    }

    /** was net/minecraft/class_4838.method_24719(Lnet/minecraft/class_1309;)Z */
    public static boolean method_24719(net.minecraft.class_4838 self, net.minecraft.class_1309 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the LivingEntity predicate method_24719 was removed and class_4838 exposes no faithful equivalent");
    }

    /** was net/minecraft/class_4861$2.field_22483Lnet/minecraft/class_4861; */
    public static net.minecraft.class_4861 field_22483() {
        throw new UnsupportedOperationException("CenturyBridge: deleted synthetic outer-instance reference cannot be recovered without a class_4861$2 receiver");
    }

    /** was net/minecraft/class_4861.<init>(Lnet/minecraft/class_3917;ILnet/minecraft/class_1661;Lnet/minecraft/class_3914;)V */
    public static net.minecraft.class_4861 cbNewclass_4861(net.minecraft.class_3917 a0, int a1, net.minecraft.class_1661 a2, net.minecraft.class_3914 a3) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires a class_8047 slot layout, but the old arguments do not provide one and the supplied API exposes no faithful default or factory");
    }

    /** was net/minecraft/class_486.field_2915I */
    public static int field_2915() {
        throw new UnsupportedOperationException("CenturyBridge: field_2915 was per-instance mutable tick state and cannot be reproduced by a receiverless static constant shim");
    }

    /** was net/minecraft/class_5147.method_6636(Lnet/minecraft/class_3419;)V */
    public static void method_6636(net.minecraft.class_5147 self, net.minecraft.class_3419 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new sheared contract requires a ServerWorld and ItemStack that the old receiver and SoundCategory arguments do not provide");
    }

    /** was net/minecraft/class_514.field_32413I */
    public static int field_32413() {
        return 30;
    }

    /** was net/minecraft/class_5217.method_146()Lnet/minecraft/class_1928; */
    public static net.minecraft.class_1928 method_146(net.minecraft.class_5217 self) {
        throw new UnsupportedOperationException("CenturyBridge: the replacement API on class_5219/class_5268 is unavailable from class_5217, so the old class_1928 contract cannot be expressed");
    }

    /** was net/minecraft/class_5411$class_5412.field_25737Z */
    public static boolean field_25737() {
        return false;
    }

    /** was net/minecraft/class_5411$class_5412.field_25738Z */
    public static boolean field_25738() {
        return true;
    }

    /** was net/minecraft/class_5455.method_33310(Lnet/minecraft/class_5321;)Ljava/util/Optional; */
    public static java.util.Optional method_33310(net.minecraft.class_5455 self, net.minecraft.class_5321 a0) {
        return self.method_46759(a0);
    }

    /** was net/minecraft/class_5514.field_26832Lnet/minecraft/class_5514$class_5515; */
    public static net.minecraft.class_5514.class_5515 field_26832() {
        throw new UnsupportedOperationException("CenturyBridge: deleted class_5515 constant has no equivalent in the new class_5514 API");
    }

    /** was net/minecraft/class_10025.field_53369Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 field_53369() {
        throw new UnsupportedOperationException("CenturyBridge: the new field type net.minecraft.class_10444 cannot be converted to the old net.minecraft.class_1799 contract");
    }

    /** was net/minecraft/class_10038.field_53433Lnet/minecraft/class_8113$class_8122$class_8226; */
    public static net.minecraft.class_8113.class_8122.class_8226 field_53433() {
        throw new UnsupportedOperationException("CenturyBridge: field_53433 was deleted and class_10038 exposes no equivalent class_8113.class_8122.class_8226 constant");
    }

    /** was net/minecraft/class_10039.field_53437Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 field_53437() {
        throw new UnsupportedOperationException("CenturyBridge: class_10039 no longer exposes an equivalent class_1799 constant");
    }

    /** was net/minecraft/class_10040.field_53439Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 field_53439() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted class_1799 constant has no equivalent in the new class_10040 API");
    }

    /** was net/minecraft/class_10042.field_53467Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 field_53467() {
        throw new UnsupportedOperationException("CenturyBridge: field_53467 changed from class_1799 to class_10444 with no API exposing an equivalent class_1799 value");
    }

    /** was net/minecraft/class_10042.field_53468Lnet/minecraft/class_1306; */
    public static net.minecraft.class_1306 field_53468() {
        return net.minecraft.class_1306.field_6183;
    }

    /** was net/minecraft/class_10042.field_53470Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 field_53470() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted class_1799 constant cannot be reconstructed from class_10042's new API surface");
    }

    /** was net/minecraft/class_10042.field_53472Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 field_53472() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted class_1799 constant has no equivalent in the new class_10042 API surface");
    }

    /** was net/minecraft/class_10072.field_53591Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 field_53591() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted class_1799 constant has no equivalent in the new class_10072 API surface");
    }

    /** was net/minecraft/class_10097.field_54194Ljava/lang/String; */
    public static java.lang.String field_54194() {
        return "item/";
    }

    /** was net/minecraft/class_10191.field_54134Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_54134() {
        return net.minecraft.class_10191.field_54134.method_29177();
    }

    /** was net/minecraft/class_10191.field_54135Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_54135() {
        return net.minecraft.class_10191.field_54135.method_29177();
    }

    /** was net/minecraft/class_10191.field_54136Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_54136() {
        return net.minecraft.class_10191.field_54136.method_29177();
    }

    /** was net/minecraft/class_10191.field_54137Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_54137() {
        return net.minecraft.class_10191.field_54137.method_29177();
    }

    /** was net/minecraft/class_10191.field_54138Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_54138() {
        return net.minecraft.class_10191.field_54138.method_29177();
    }

    /** was net/minecraft/class_10191.field_54139Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_54139() {
        return net.minecraft.class_10191.field_54139.method_29177();
    }

    /** was net/minecraft/class_10191.field_54140Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_54140() {
        return net.minecraft.class_10191.field_54140.method_29177();
    }

    /** was net/minecraft/class_10191.field_54141Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_54141() {
        return net.minecraft.class_10191.field_54141.method_29177();
    }

    /** was net/minecraft/class_10191.field_54142Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_54142() {
        return net.minecraft.class_10191.field_54142.method_29177();
    }

    /** was net/minecraft/class_10191.field_54144Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_54144() {
        return net.minecraft.class_10191.field_54144.method_29177();
    }

    /** was net/minecraft/class_10191.method_64013(Ljava/util/function/BiConsumer;)V */
    public static void method_64013(net.minecraft.class_10191 self, java.util.function.BiConsumer a0) {
        self.field_54143.forEach(a0);
    }

    /** was net/minecraft/class_10197$class_10199.comp_3201Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 comp_3201() {
        throw new UnsupportedOperationException("CenturyBridge: the old static class_2960 value cannot be reconstructed without an instance of class_10197$class_10199");
    }

    /** was net/minecraft/class_10319.field_54740Lnet/minecraft/class_2754; */
    public static net.minecraft.class_2754 field_54740() {
        return net.minecraft.class_10319.field_54739;
    }

    /** was net/minecraft/class_10319.method_64782(Lnet/minecraft/class_1937;)Z */
    public static boolean method_64782(net.minecraft.class_10319 self, net.minecraft.class_1937 a0) {
        return self.method_65147(a0);
    }

    /** was net/minecraft/class_10339.<init>(Lnet/minecraft/class_243;I)V */
    public static net.minecraft.class_10339 cbNewclass_10339(net.minecraft.class_243 a0, int a1) {
        return new net.minecraft.class_10339(a0, a1, 0);
    }

    /** was net/minecraft/class_1049.field_5224Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_5224() {
        throw new UnsupportedOperationException("CenturyBridge: old instance-specific texture location cannot be reconstructed by a parameterless static field shim");
    }

    /** was net/minecraft/class_1049.field_5225Lorg/slf4j/Logger; */
    public static org.slf4j.Logger field_5225() {
        return com.mojang.logging.LogUtils.getLogger();
    }

    /** was net/minecraft/class_1079.field_32974Ljava/lang/String; */
    public static java.lang.String field_32974() {
        return "animation";
    }

    /** was net/minecraft/class_1079.field_32975I */
    public static int field_32975() {
        return 1;
    }

    /** was net/minecraft/class_1079.field_32976I */
    public static int field_32976() {
        return -1;
    }

    /** was net/minecraft/class_1080.field_32973I */
    public static int field_32973() {
        return -1;
    }

    /** was net/minecraft/class_1093.field_5411Ljava/util/List; */
    public static java.util.List field_5411() {
        throw new UnsupportedOperationException("CenturyBridge: old field_5411 contained per-instance model quads and cannot be reconstructed by a receiverless static field shim");
    }

    /** was net/minecraft/class_1093.field_5415Z */
    public static boolean field_5415() {
        return true;
    }

    /** was net/minecraft/class_1297.method_5833()Z */
    public static boolean method_5833(net.minecraft.class_1297 self) {
        throw new UnsupportedOperationException("CenturyBridge: deleted Entity boolean contract has no equivalent in the provided 1.21.4 API surface");
    }

    /** was net/minecraft/class_1309.field_6232I */
    public static int field_6232() {
        return 0;
    }

    /** was net/minecraft/class_1407.method_35128()Z */
    public static boolean method_35128(net.minecraft.class_1407 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new BirdNavigation API no longer exposes whether open-door entry is enabled");
    }

    /** was net/minecraft/class_1407.method_35129()Z */
    public static boolean method_35129(net.minecraft.class_1407 self) {
        throw new UnsupportedOperationException("CenturyBridge: the canPathThroughDoors state is no longer exposed by the new BirdNavigation API");
    }

    /** was net/minecraft/class_1407.method_6331(Z)V */
    public static void method_6331(net.minecraft.class_1407 self, boolean a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_1407 API has no equivalent for method_6331(boolean)");
    }

    /** was net/minecraft/class_1409.method_35139(Z)V */
    public static void method_35139(net.minecraft.class_1409 self, boolean a0) {
        self.method_6363(a0);
    }

    /** was net/minecraft/class_1409.method_35140()Z */
    public static boolean method_35140(net.minecraft.class_1409 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new MobNavigation API exposes no equivalent getter for whether the navigation can pass through doors");
    }

    /** was net/minecraft/class_1409.method_6366()Z */
    public static boolean method_6366(net.minecraft.class_1409 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new API exposes no readable equivalent for the canEnterOpenDoors state");
    }

    /** was net/minecraft/class_1462$class_9869.field_52474Ljava/lang/String; */
    public static java.lang.String field_52474() {
        throw new UnsupportedOperationException("CenturyBridge: the old field value is instance-specific, but the zero-argument static shim has no Variant receiver");
    }

    /** was net/minecraft/class_1661.method_7374(Lnet/minecraft/class_1799;)V */
    public static void method_7374(net.minecraft.class_1661 self, net.minecraft.class_1799 a0) {
        self.method_65126(a0);
    }

    /** was net/minecraft/class_1741.<init>(ILjava/util/Map;ILnet/minecraft/class_6880;FFLnet/minecraft/class_6862;Lnet/minecraft/class_2960;)V */
    public static net.minecraft.class_1741 cbNewclass_1741(int a0, java.util.Map a1, int a2, net.minecraft.class_6880 a3, float a4, float a5, net.minecraft.class_6862 a6, net.minecraft.class_2960 a7) {
        return new net.minecraft.class_1741(a0, a1, a2, a3, a4, a5, a6, net.minecraft.class_5321.method_29179(net.minecraft.class_10191.field_55214, a7));
    }

    /** was net/minecraft/class_1759.method_43123(Lnet/minecraft/class_1937;)Lnet/minecraft/class_4208; */
    public static net.minecraft.class_4208 method_43123(net.minecraft.class_1759 self, net.minecraft.class_1937 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_1759 no longer exposes an API that can derive class_4208 from class_1937");
    }

    /** was net/minecraft/class_1826.<init>(Lnet/minecraft/class_1299;IILnet/minecraft/class_1792$class_1793;)V */
    public static net.minecraft.class_1826 cbNewclass_1826(net.minecraft.class_1299 a0, int a1, int a2, net.minecraft.class_1792.class_1793 a3) {
        return new net.minecraft.class_1826(a0, a3);
    }

    /** was net/minecraft/class_1926.method_43717()I */
    public static int method_43717(net.minecraft.class_1926 self) {
        return net.minecraft.class_1926.field_55218;
    }

    /** was net/minecraft/class_1926.method_8341()I */
    public static int method_8341(net.minecraft.class_1926 self) {
        return net.minecraft.class_1926.field_55217;
    }

    /** was net/minecraft/class_1926.method_8342()I */
    public static int method_8342(net.minecraft.class_1926 self) {
        return net.minecraft.class_1926.field_55215;
    }

    /** was net/minecraft/class_1926.method_8343()I */
    public static int method_8343(net.minecraft.class_1926 self) {
        return net.minecraft.class_1926.field_55216;
    }

    /** was net/minecraft/class_2464.field_11456Lnet/minecraft/class_2464; */
    public static net.minecraft.class_2464 field_11456() {
        throw new UnsupportedOperationException("CenturyBridge: ENTITYBLOCK_ANIMATED was removed and the new API has no render type with equivalent behavior");
    }

    /** was net/minecraft/class_2480.method_10526(Lnet/minecraft/class_2248;)Lnet/minecraft/class_1767; */
    public static net.minecraft.class_1767 method_10526(net.minecraft.class_2480 self, net.minecraft.class_2248 a0) {
        return self.method_10528();
    }

    /** was net/minecraft/class_2480.method_10527(Lnet/minecraft/class_1792;)Lnet/minecraft/class_1767; */
    public static net.minecraft.class_1767 method_10527(net.minecraft.class_2480 self, net.minecraft.class_1792 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API has no equivalent mapping from class_1792 items to class_1767 blocks");
    }

    /** was net/minecraft/class_2573.method_10913(Lnet/minecraft/class_1799;Lnet/minecraft/class_1767;)V */
    public static void method_10913(net.minecraft.class_2573 self, net.minecraft.class_1799 a0, net.minecraft.class_1767 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new API exposes no mutator equivalent for setting both the item stack and display context on an existing class_2573 instance");
    }

    /** was net/minecraft/class_2586.method_11011()Z */
    public static boolean method_11011(net.minecraft.class_2586 self) {
        throw new UnsupportedOperationException("CenturyBridge: the operator-only item-data permission contract was removed and cannot be recovered from the new BlockEntity API");
    }

    /** was net/minecraft/class_2596.method_11054(Lnet/minecraft/class_2547;)V */
    public static void method_11054(net.minecraft.class_2596 self, net.minecraft.class_2547 a0) {
        self.method_65081(a0);
    }

    /** was net/minecraft/class_2596.method_55846()Lnet/minecraft/class_9145; */
    public static net.minecraft.class_9145 method_55846(net.minecraft.class_2596 self) {
        return self.method_65080();
    }

    /** was net/minecraft/class_2609.field_11980I */
    public static int field_11980() {
        throw new UnsupportedOperationException("CenturyBridge: old field_11980 was per-instance furnace fuel time and cannot be reconstructed by a receiverless static helper");
    }

    /** was net/minecraft/class_2609.field_11981I */
    public static int field_11981() {
        throw new UnsupportedOperationException("CenturyBridge: field_11981 was mutable per-instance furnace burn time and cannot be reconstructed by a receiverless static field shim");
    }

    /** was net/minecraft/class_2609.field_11989I */
    public static int field_11989() {
        throw new UnsupportedOperationException("CenturyBridge: the old field was mutable per-instance furnace cooking progress and cannot be represented by a receiverless constant field shim");
    }

    /** was net/minecraft/class_2609.field_52634I */
    public static int field_52634() {
        return 20;
    }

    /** was net/minecraft/class_2741.field_54793Lnet/minecraft/class_2754; */
    public static net.minecraft.class_2754 field_54793() {
        throw new UnsupportedOperationException("CenturyBridge: deleted property has no behaviorally equivalent constant in the new API");
    }

    /** was net/minecraft/class_372.field_32225I */
    public static int field_32225() {
        return 28;
    }

    /** was net/minecraft/class_3888.field_32977Ljava/lang/String; */
    public static java.lang.String field_32977() {
        return "villager";
    }

    /** was net/minecraft/class_5537.<init>(Lnet/minecraft/class_2960;Lnet/minecraft/class_2960;Lnet/minecraft/class_1792$class_1793;)V */
    public static net.minecraft.class_5537 cbNewclass_5537(net.minecraft.class_2960 a0, net.minecraft.class_2960 a1, net.minecraft.class_1792.class_1793 a2) {
        return new net.minecraft.class_5537(a2);
    }

    /** was net/minecraft/class_5537.method_63677()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_63677(net.minecraft.class_5537 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_5537 no longer exposes the class_2960 identifier required by the old contract");
    }

    /** was net/minecraft/class_5537.method_63678()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_63678(net.minecraft.class_5537 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_5537 no longer exposes the class_2960 identifier required by the old contract");
    }

    /** was net/minecraft/class_5780.field_37709Lnet/minecraft/class_5778; */
    public static net.minecraft.class_5778 field_37709() {
        throw new UnsupportedOperationException("CenturyBridge: new field_37709 has type net.minecraft.class_10376, with no API to recover the old net.minecraft.class_5778 value");
    }

    /** was net/minecraft/class_7677.method_45252(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Lnet/minecraft/class_7677; */
    public static net.minecraft.class_7677 method_45252(net.minecraft.class_7677 self, java.lang.String a0, com.mojang.serialization.Codec a1) {
        return new net.minecraft.class_7677(a0, a1);
    }

    /** was net/minecraft/class_7701.field_54592Lnet/minecraft/class_7696; */
    public static net.minecraft.class_7696 field_54592() {
        return net.minecraft.class_7701.field_40177;
    }

    /** was net/minecraft/class_793.field_32792Ljava/lang/String; */
    public static java.lang.String field_32792() {
        return "particle";
    }

    /** was net/minecraft/class_793.field_4251Ljava/util/Map; */
    public static java.util.Map field_4251() {
        throw new UnsupportedOperationException("CenturyBridge: old field_4251 was per-instance texture state and cannot be reconstructed as a static Map");
    }

    /** was net/minecraft/class_793.field_4252Ljava/lang/String; */
    public static java.lang.String field_4252() {
        return "particle";
    }

    /** was net/minecraft/class_8054.<init>(Ljava/lang/String;Lnet/minecraft/class_6880;FLjava/util/Map;Lnet/minecraft/class_2561;)V */
    public static net.minecraft.class_8054 cbNewclass_8054(java.lang.String a0, net.minecraft.class_6880 a1, float a2, java.util.Map a3, net.minecraft.class_2561 a4) {
        return new net.minecraft.class_8054(a0, a1, a3, a4);
    }

    /** was net/minecraft/class_8054.comp_1210()F */
    public static float comp_1210(net.minecraft.class_8054 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_8054 API no longer exposes the float value returned by comp_1210");
    }

    /** was net/minecraft/class_8167.<init>(Lnet/minecraft/class_4970$class_2251;)V */
    public static net.minecraft.class_8167 cbNewclass_8167(net.minecraft.class_4970.class_2251 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires an int and class_2394 with no old-semantic defaults exposed");
    }

    /** was net/minecraft/class_8172.method_49196(Lnet/minecraft/class_1799;)V */
    public static void method_49196(net.minecraft.class_8172 self, net.minecraft.class_1799 a0) {
        self.method_54077(a0);
    }

    /** was net/minecraft/class_8172.method_52578()Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 method_52578(net.minecraft.class_8172 self) {
        return self.method_54079();
    }

    /** was net/minecraft/class_8197$class_5305.field_54706Lnet/minecraft/class_8197$class_5305; */
    public static net.minecraft.class_8197.class_5305 field_54706() {
        throw new UnsupportedOperationException("CenturyBridge: the removed overworld winter-drop preset has no behaviorally equivalent preset in the new API");
    }

    /** was net/minecraft/class_8198.method_64761(Lnet/minecraft/class_7891;)V */
    public static void method_64761(net.minecraft.class_8198 self, net.minecraft.class_7891 a0) {
        self.method_49522(a0);
    }

    /** was net/minecraft/class_823.field_32816Ljava/lang/String; */
    public static java.lang.String field_32816() {
        return "flag";
    }

    /** was net/minecraft/class_8966.method_55189(Lnet/minecraft/class_8963;Lnet/minecraft/class_5819;Lnet/minecraft/class_1299;)V */
    public static void method_55189(net.minecraft.class_8966 self, net.minecraft.class_8963 a0, net.minecraft.class_5819 a1, net.minecraft.class_1299 a2) {
        throw new UnsupportedOperationException("CenturyBridge: the new API exposes no equivalent that accepts an explicit entity type, so the old mutation contract cannot be preserved");
    }

    /** was net/minecraft/class_9280.<init>(I)V */
    public static net.minecraft.class_9280 cbNewclass_9280(int a0) {
        return new net.minecraft.class_9280(java.util.List.of((float) a0), java.util.List.of(), java.util.List.of(), java.util.List.of());
    }

    /** was net/minecraft/class_9280.comp_2382()I */
    public static int comp_2382(net.minecraft.class_9280 self) {
        throw new UnsupportedOperationException("CenturyBridge: the old integer component comp_2382 has no equivalent in the new four-list API");
    }

    /** was net/minecraft/class_9280.field_49307Lnet/minecraft/class_9280; */
    public static net.minecraft.class_9280 field_49307() {
        return net.minecraft.class_9280.field_55212;
    }

    /** was net/minecraft/class_9817.method_5932(ZZ)V */
    public static void method_5932(net.minecraft.class_9817 self, boolean a0, boolean a1) {
        self.method_5932();
    }

    /** was net/minecraft/class_9887.method_61681()Ljava/util/List; */
    public static java.util.List method_61681(net.minecraft.class_9887 self) {
        return self.method_64675();
    }

    /** was net/minecraft/class_9887.method_64676()Ljava/util/List; */
    public static java.util.List method_64676(net.minecraft.class_9887 self) {
        return self.method_64675();
    }

    /** was net/minecraft/class_128.method_568()Ljava/lang/String; */
    public static java.lang.String method_568(net.minecraft.class_128 self) {
        return self.method_60920(net.minecraft.class_9813.field_52181);
    }

    /** was net/minecraft/class_1295.method_35048()Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 method_35048(net.minecraft.class_1295 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new AreaEffectCloudEntity API exposes no getter for its private potion contents");
    }

    /** was net/minecraft/class_1295.method_5612(Lnet/minecraft/class_1842;)V */
    public static void method_5612(net.minecraft.class_1295 self, net.minecraft.class_1842 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API requires a class_1844 potion contents component backed by a registry entry, which cannot be faithfully constructed from the old raw class_1842 potion");
    }

    /** was net/minecraft/class_1297.field_5963Z */
    public static boolean field_5963() {
        return false;
    }

    /** was net/minecraft/class_1297.method_18379()V */
    public static void method_18379(net.minecraft.class_1297 self) {
        throw new UnsupportedOperationException("CenturyBridge: portal ticking was integrated into the new entity tick lifecycle and has no independently callable equivalent");
    }

    /** was net/minecraft/class_1297.method_20343(Lnet/minecraft/class_4050;)Lnet/minecraft/class_238; */
    public static net.minecraft.class_238 method_20343(net.minecraft.class_1297 self, net.minecraft.class_4050 a0) {
        return self.method_18377(a0).method_30757(self.method_19538());
    }

    /** was net/minecraft/class_1297.method_30330(Lnet/minecraft/class_3218;Lnet/minecraft/class_2338;ZLnet/minecraft/class_2784;)Ljava/util/Optional; */
    public static java.util.Optional method_30330(net.minecraft.class_1297 self, net.minecraft.class_3218 a0, net.minecraft.class_2338 a1, boolean a2, net.minecraft.class_2784 a3) {
        throw new UnsupportedOperationException("CenturyBridge: the 1.21.4 Entity API no longer exposes an equivalent portal-rectangle lookup using the destination position, creation flag, and world border");
    }

    /** was net/minecraft/class_1297.method_36601()Lnet/minecraft/class_2680; */
    public static net.minecraft.class_2680 method_36601(net.minecraft.class_1297 self) {
        return self.method_37908().method_8320(self.method_24515());
    }

    /** was net/minecraft/class_1297.method_49477(F)V */
    public static void method_49477(net.minecraft.class_1297 self, float a0) {
        throw new UnsupportedOperationException("CenturyBridge: the mutable entity step-height setter was removed and the new API provides no equivalent for arbitrary entities");
    }

    /** was net/minecraft/class_1297.method_51152()F */
    public static float method_51152(net.minecraft.class_1297 self) {
        return (float) self.method_56072().method_55678(net.minecraft.class_9064.field_47745, 0, 0.0F).method_10214();
    }

    /** was net/minecraft/class_1297.method_52536(Lnet/minecraft/class_1297;)F */
    public static float method_52536(net.minecraft.class_1297 self, net.minecraft.class_1297 a0) {
        throw new UnsupportedOperationException("CenturyBridge: entity-dependent float calculation was removed with no equivalent in the new API");
    }

    /** was net/minecraft/class_1297.method_5717(Lnet/minecraft/class_2338;)V */
    public static void method_5717(net.minecraft.class_1297 self, net.minecraft.class_2338 a0) {
        self.method_5814((double) a0.method_10263() + 0.5D, (double) a0.method_10264(), (double) a0.method_10260() + 0.5D);
    }

    /** was net/minecraft/class_1297.method_5723(Lnet/minecraft/class_1309;Lnet/minecraft/class_1297;)V */
    public static void method_5723(net.minecraft.class_1297 self, net.minecraft.class_1309 a0, net.minecraft.class_1297 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new enchantment damage-effects API requires damage context not present in the old signature");
    }

    /** was net/minecraft/class_1297.method_59657(Lnet/minecraft/class_243;)Ljava/util/Optional; */
    public static java.util.Optional method_59657(net.minecraft.class_1297 self, net.minecraft.class_243 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new Entity API has no equivalent for resolving the optional position from the supplied vector");
    }

    /** was net/minecraft/class_1308.method_58151()Z */
    public static boolean method_58151(net.minecraft.class_1308 self) {
        throw new UnsupportedOperationException("CenturyBridge: the deleted MobEntity predicate has no equivalent in the 1.21.4 API");
    }

    /** was net/minecraft/class_1308.method_5934()Z */
    public static boolean method_5934(net.minecraft.class_1308 self) {
        return self.method_60953();
    }

    /** was net/minecraft/class_1308.method_5954(Lnet/minecraft/class_1297;Z)V */
    public static void method_5954(net.minecraft.class_1308 self, net.minecraft.class_1297 a0, boolean a1) {
        self.method_60964(a0, a1);
    }

    /** was net/minecraft/class_1308.method_5995()V */
    public static void method_5995(net.minecraft.class_1308 self) {
        throw new UnsupportedOperationException("CenturyBridge: leash ticking moved to the Leashable framework and no equivalent callable operation exists on class_1308");
    }

    /** was net/minecraft/class_1308.method_6735()Z */
    public static boolean method_6735(net.minecraft.class_1308 self) {
        throw new UnsupportedOperationException("CenturyBridge: the 1.21.4 MobEntity API exposes no equivalent for the deleted method_6735 boolean contract");
    }

    /** was net/minecraft/class_1309.method_18394(Lnet/minecraft/class_4050;Lnet/minecraft/class_4048;)F */
    public static float method_18394(net.minecraft.class_1309 self, net.minecraft.class_4050 a0, net.minecraft.class_4048 a1) {
        return a1.comp_2187();
    }

    /** was net/minecraft/class_1309.method_25937()V */
    public static void method_25937(net.minecraft.class_1309 self) {
        throw new UnsupportedOperationException("CenturyBridge: the deleted soul speed particle and sound effect routine has no equivalent in the new LivingEntity API");
    }

    /** was net/minecraft/class_1309.method_26826(Lnet/minecraft/class_1320;)D */
    public static double method_26826(net.minecraft.class_1309 self, net.minecraft.class_1320 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new attribute API requires a registry entry and cannot faithfully resolve an arbitrary raw class_1320");
    }

    /** was net/minecraft/class_1309.method_27303()Z */
    public static boolean method_27303(net.minecraft.class_1309 self) {
        throw new UnsupportedOperationException("CenturyBridge: the soul-speed block predicate and its backing block tag were removed, so the old data-pack-sensitive contract cannot be preserved");
    }

    /** was net/minecraft/class_1309.method_29500(Lnet/minecraft/class_2680;)Z */
    public static boolean method_29500(net.minecraft.class_1309 self, net.minecraft.class_2680 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the removed soul-speed modifier state check has no faithful equivalent in the new API");
    }

    /** was net/minecraft/class_1309.method_29501()V */
    public static void method_29501(net.minecraft.class_1309 self) {
        throw new UnsupportedOperationException("CenturyBridge: soul speed boost removal is no longer exposed by LivingEntity in the new API");
    }

    /** was net/minecraft/class_1314.method_6142(F)V */
    public static void method_6142(net.minecraft.class_1314 self, float a0) {
        throw new UnsupportedOperationException("CenturyBridge: the replacement leash-distance API requires the leash holder entity, which the old signature does not provide");
    }

    /** was net/minecraft/class_131.field_25219Lnet/minecraft/class_5339; */
    public static net.minecraft.class_5339 field_25219() {
        return net.minecraft.class_131.field_51800;
    }

    /** was net/minecraft/class_1320.field_30097I */
    public static int field_30097() {
        return 64;
    }

    /** was net/minecraft/class_1355.method_19048()Ljava/util/stream/Stream; */
    public static java.util.stream.Stream method_19048(net.minecraft.class_1355 self) {
        return self.method_35115().stream();
    }

    /** was net/minecraft/class_14.method_29303(Lnet/minecraft/class_1308;III)Lnet/minecraft/class_7; */
    public static net.minecraft.class_7 method_29303(net.minecraft.class_14 self, net.minecraft.class_1308 a0, int a1, int a2, int a3) {
        return self.method_57625(a0, new net.minecraft.class_2338(a1, a2, a3));
    }

    /** was net/minecraft/class_14.method_61(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;Lnet/minecraft/class_7;)Lnet/minecraft/class_7; */
    public static net.minecraft.class_7 method_61(net.minecraft.class_14 self, net.minecraft.class_1922 a0, net.minecraft.class_2338 a1, net.minecraft.class_7 a2) {
        throw new UnsupportedOperationException("CenturyBridge: neighbor path-node classification now requires a PathContext, which cannot be faithfully constructed from the old BlockView-only arguments");
    }

    /** was net/minecraft/class_1530.field_7100Lnet/minecraft/class_2338; */
    public static net.minecraft.class_2338 field_7100() {
        return net.minecraft.class_2338.field_10980;
    }

    /** was net/minecraft/class_1530.method_6896()Lnet/minecraft/class_2338; */
    public static net.minecraft.class_2338 method_6896(net.minecraft.class_1530 self) {
        return self.method_59940();
    }

    /** was net/minecraft/class_1530.method_6897()I */
    public static int method_6897(net.minecraft.class_1530 self) {
        throw new UnsupportedOperationException("CenturyBridge: the removed item-frame rotation getter has no equivalent in the new class_1530 API");
    }

    /** was net/minecraft/class_1534.method_48928(Lnet/minecraft/class_2487;Lnet/minecraft/class_6880;)V */
    public static void method_48928(net.minecraft.class_1534 self, net.minecraft.class_2487 a0, net.minecraft.class_6880 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new API has no equivalent for restoring painting data from NBT with a fallback variant");
    }

    /** was net/minecraft/class_1534.method_48929(Lnet/minecraft/class_2487;)Ljava/util/Optional; */
    public static java.util.Optional method_48929(net.minecraft.class_1534 self, net.minecraft.class_2487 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new PaintingEntity API cannot decode and return a painting variant from NBT");
    }

    /** was net/minecraft/class_155.field_29727Z */
    public static boolean field_29727() {
        return false;
    }

    /** was net/minecraft/class_155.method_644(Ljava/lang/String;)Ljava/lang/String; */
    public static java.lang.String method_644(net.minecraft.class_155 self, java.lang.String a0) {
        java.lang.StringBuilder result = new java.lang.StringBuilder();
        for (char c : a0.toCharArray()) {
            if (c != '\u00A7' && c >= ' ' && c != '\u007F') {
                result.append(c);
            }
        }
        return result.toString();
    }

    /** was net/minecraft/class_1661.method_7375(Lnet/minecraft/class_1282;F[I)V */
    public static void method_7375(net.minecraft.class_1661 self, net.minecraft.class_1282 a0, float a1, int[] a2) {
        throw new UnsupportedOperationException("CenturyBridge: the new PlayerInventory API has no equivalent for applying source-aware durability damage to selected armor slots");
    }

    /** was net/minecraft/class_1685.method_31551()Z */
    public static boolean method_31551(net.minecraft.class_1685 self) {
        throw new UnsupportedOperationException("CenturyBridge: channeling is now data-driven through enchantment effects and class_1685 exposes no equivalent channeling query");
    }

    /** was net/minecraft/class_1741.method_7698()Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 method_7698(net.minecraft.class_1741 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_1741 no longer exposes the old class_3414 value or an equivalent derivation");
    }

    /** was net/minecraft/class_1747.method_38072(Lnet/minecraft/class_1799;)Lnet/minecraft/class_2487; */
    public static net.minecraft.class_2487 method_38072(net.minecraft.class_1747 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_1747 API exposes no equivalent for retrieving block entity NBT from an ItemStack");
    }

    /** was net/minecraft/class_1759.method_26365(Lnet/minecraft/class_1799;)Z */
    public static boolean method_26365(net.minecraft.class_1759 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new CompassItem API cannot distinguish lodestone-component presence from other causes of item glint");
    }

    /** was net/minecraft/class_1759.method_43124(Lnet/minecraft/class_2487;)Lnet/minecraft/class_4208; */
    public static net.minecraft.class_4208 method_43124(net.minecraft.class_1759 self, net.minecraft.class_2487 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_1759 no longer exposes NBT-to-class_4208 parsing or an equivalent API");
    }

    /** was net/minecraft/class_1764.method_7772(Lnet/minecraft/class_1799;Lnet/minecraft/class_1792;)Z */
    public static boolean method_7772(net.minecraft.class_1764 self, net.minecraft.class_1799 a0, net.minecraft.class_1792 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new CrossbowItem API exposes no equivalent for testing whether a loaded crossbow contains a specific projectile item");
    }

    /** was net/minecraft/class_1764.method_7782(Lnet/minecraft/class_1799;Z)V */
    public static void method_7782(net.minecraft.class_1764 self, net.minecraft.class_1799 a0, boolean a1) {
        throw new UnsupportedOperationException("CenturyBridge: the removed method_7782 stack mutation contract has no equivalent in the new class_1764 API");
    }

    /** was net/minecraft/class_1781.method_47368(Lnet/minecraft/class_1799;B)V */
    public static void method_47368(net.minecraft.class_1781 self, net.minecraft.class_1799 a0, byte a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_1781 API has no operation that can set the class_1799 flight duration byte");
    }

    /** was net/minecraft/class_1806.method_53837(Lnet/minecraft/class_1799;)Lnet/minecraft/class_2561; */
    public static net.minecraft.class_2561 method_53837(net.minecraft.class_1806 self, net.minecraft.class_1799 a0) {
        return net.minecraft.class_1806.method_53836(a0.method_57824(net.minecraft.class_9334.field_49646));
    }

    /** was net/minecraft/class_1806.method_7999(Lnet/minecraft/class_1799;)I */
    public static int method_7999(net.minecraft.class_1806 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the map ID is now stored in an item-stack data component, but the required component accessor and component type are not available in the provided API surface");
    }

    /** was net/minecraft/class_1806.method_8003(Lnet/minecraft/class_1799;)Ljava/lang/Integer; */
    public static java.lang.Integer method_8003(net.minecraft.class_1806 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_1806 API cannot derive the deleted Integer value from only a class_1799");
    }

    /** was net/minecraft/class_1811.method_57392(Lnet/minecraft/class_1799;Lnet/minecraft/class_1799;Z)Z */
    public static boolean method_57392(net.minecraft.class_1811 self, net.minecraft.class_1799 a0, net.minecraft.class_1799 a1, boolean a2) {
        throw new UnsupportedOperationException("CenturyBridge: the removed infinity-ammunition check has no equivalent on the new RangedWeaponItem API");
    }

    /** was net/minecraft/class_1819.method_8013(Lnet/minecraft/class_1799;)Lnet/minecraft/class_1767; */
    public static net.minecraft.class_1767 method_8013(net.minecraft.class_1819 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: shield base color lookup has no equivalent in the provided 1.21.4 API surface");
    }

    /** was net/minecraft/class_1840.method_8047(Lnet/minecraft/class_2487;)Z */
    public static boolean method_8047(net.minecraft.class_1840 self, net.minecraft.class_2487 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_1840 no longer exposes NBT deserialization or an equivalent operation");
    }

    /** was net/minecraft/class_1842.method_53211()Lnet/minecraft/class_6880$class_6883; */
    public static net.minecraft.class_6880.class_6883 method_53211(net.minecraft.class_1842 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_1842 no longer exposes the class_6880.class_6883 value returned by method_53211");
    }

    /** was net/minecraft/class_1842.method_8048(Ljava/lang/String;)Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 method_8048(net.minecraft.class_1842 self, java.lang.String a0) {
        throw new UnsupportedOperationException("CenturyBridge: the potion base name is immutable in the new API and cannot be set on the existing instance");
    }

    /** was net/minecraft/class_1843.method_17443(Lnet/minecraft/class_1799;)I */
    public static int method_17443(net.minecraft.class_1843 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_1843 API exposes no equivalent for deriving an int from class_1799");
    }

    /** was net/minecraft/class_1843.method_8052(Lnet/minecraft/class_1799;)I */
    public static int method_8052(net.minecraft.class_1843 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API cannot express the deleted item-stack-to-integer contract");
    }

    /** was net/minecraft/class_1843.method_8053(Lnet/minecraft/class_2487;)Z */
    public static boolean method_8053(net.minecraft.class_1843 self, net.minecraft.class_2487 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API cannot test class_2487 NBT applicability without an item stack and command context");
    }

    /** was net/minecraft/class_1847.field_42473Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_42473() {
        return net.minecraft.class_5321.method_29179(net.minecraft.class_5321.method_29180(net.minecraft.class_2960.method_60656("potion")), net.minecraft.class_2960.method_60656("empty"));
    }

    /** was net/minecraft/class_1847.field_8984Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8984() {
        throw new UnsupportedOperationException("CenturyBridge: the old EMPTY potion was removed and the new API exposes no equivalent Potion value");
    }

    /** was net/minecraft/class_1860.method_8115(Lnet/minecraft/class_1263;Lnet/minecraft/class_1937;)Z */
    public static boolean method_8115(net.minecraft.class_1860 self, net.minecraft.class_1263 a0, net.minecraft.class_1937 a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_1263 cannot be safely adapted to class_9695 without a guaranteed RecipeInput implementation");
    }

    /** was net/minecraft/class_1860.method_8116(Lnet/minecraft/class_1263;Lnet/minecraft/class_5455;)Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 method_8116(net.minecraft.class_1860 self, net.minecraft.class_1263 a0, net.minecraft.class_5455 a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_1263 cannot be faithfully adapted to the required class_9695 recipe input");
    }

    /** was net/minecraft/class_1865.method_53736()Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_53736(net.minecraft.class_1865 self) {
        return self.method_53736().codec();
    }

    /** was net/minecraft/class_1865.method_8122(Lnet/minecraft/class_2540;)Lnet/minecraft/class_1860; */
    public static net.minecraft.class_1860 method_8122(net.minecraft.class_1865 self, net.minecraft.class_2540 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_1865 no longer exposes packet decoding from class_2540, and the codec-based API cannot faithfully produce class_1860 from the old buffer");
    }

    /** was net/minecraft/class_1865.method_8124(Lnet/minecraft/class_2540;Lnet/minecraft/class_1860;)V */
    public static void method_8124(net.minecraft.class_1865 self, net.minecraft.class_2540 a0, net.minecraft.class_1860 a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_1865 no longer exposes packet serialization equivalent to method_8124");
    }

    /** was net/minecraft/class_1890.method_24365(Lnet/minecraft/class_1887;Lnet/minecraft/class_1309;Ljava/util/function/Predicate;)Ljava/util/Map$Entry; */
    public static java.util.Map.Entry method_24365(net.minecraft.class_1890 self, net.minecraft.class_1887 a0, net.minecraft.class_1309 a1, java.util.function.Predicate a2) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_1890 API no longer exposes an equivalent for selecting an equipment entry by enchantment, living entity, and predicate");
    }

    /** was net/minecraft/class_1890.method_25951(Lnet/minecraft/class_1309;)Z */
    public static boolean method_25951(net.minecraft.class_1890 self, net.minecraft.class_1309 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API has no boolean entity-only equivalent for the deleted member");
    }

    /** was net/minecraft/class_1890.method_37427(Lnet/minecraft/class_2487;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_37427(net.minecraft.class_1890 self, net.minecraft.class_2487 a0) {
        return net.minecraft.class_2960.method_12829(a0.method_10558("id"));
    }

    /** was net/minecraft/class_1890.method_49189(Lnet/minecraft/class_1799;)Z */
    public static boolean method_49189(net.minecraft.class_1890 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the data-driven enchantment API cannot identify Silk Touch from an ItemStack without registry access");
    }

    /** was net/minecraft/class_1890.method_58448(I)F */
    public static float method_58448(net.minecraft.class_1890 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_1890 API has no equivalent operation accepting only an integer and returning a float");
    }

    /** was net/minecraft/class_1890.method_58676(Lnet/minecraft/class_1297;F)F */
    public static float method_58676(net.minecraft.class_1890 self, net.minecraft.class_1297 a0, float a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new armor-effectiveness API requires a ServerWorld, weapon ItemStack, and DamageSource that the old contract does not provide");
    }

    /** was net/minecraft/class_1890.method_58677(Lnet/minecraft/class_1309;Lnet/minecraft/class_1297;Lnet/minecraft/class_9304;)V */
    public static void method_58677(net.minecraft.class_1890 self, net.minecraft.class_1309 a0, net.minecraft.class_1297 a1, net.minecraft.class_9304 a2) {
        throw new UnsupportedOperationException("CenturyBridge: the old explicit ItemEnchantmentsComponent attack hook cannot be expressed by the new API, which requires server-world, damage-source, and weapon context");
    }

    /** was net/minecraft/class_1890.method_8199(Lnet/minecraft/class_1309;)I */
    public static int method_8199(net.minecraft.class_1890 self, net.minecraft.class_1309 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the old entity-only integer contract has no equivalent in the new API surface, which requires additional world, item, or effect context");
    }

    /** was net/minecraft/class_1890.method_8200(Lnet/minecraft/class_1309;)Z */
    public static boolean method_8200(net.minecraft.class_1890 self, net.minecraft.class_1309 a0) {
        throw new UnsupportedOperationException("CenturyBridge: Aqua Affinity is data-driven in 1.21.4 and the provided API exposes no equivalent entity-level presence check");
    }

    /** was net/minecraft/class_1890.method_8205(Lnet/minecraft/class_1309;)I */
    public static int method_8205(net.minecraft.class_1890 self, net.minecraft.class_1309 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new data-driven enchantment API exposes no equivalent integer knockback-enchantment level query for a LivingEntity");
    }

    /** was net/minecraft/class_1890.method_8206(Lnet/minecraft/class_1799;)I */
    public static int method_8206(net.minecraft.class_1890 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: loyalty is now a registry-driven enchantment effect requiring world and user context, which the old ItemStack-only contract does not provide");
    }

    /** was net/minecraft/class_1890.method_8210(Lnet/minecraft/class_1309;Lnet/minecraft/class_1297;)V */
    public static void method_8210(net.minecraft.class_1890 self, net.minecraft.class_1309 a0, net.minecraft.class_1297 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new onTargetDamaged API requires a ServerWorld and DamageSource, which the old arguments do not provide");
    }

    /** was net/minecraft/class_1890.method_8211(Lnet/minecraft/class_1309;)I */
    public static int method_8211(net.minecraft.class_1890 self, net.minecraft.class_1309 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the old entity-only integer contract has no faithful equivalent in the new API, which requires additional context such as a server world, item stack, or effect entry");
    }

    /** was net/minecraft/class_1890.method_8213(Lnet/minecraft/class_1309;Lnet/minecraft/class_1297;)V */
    public static void method_8213(net.minecraft.class_1890 self, net.minecraft.class_1309 a0, net.minecraft.class_1297 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new enchantment post-attack API requires weapon, equipment-slot, world, position, block-state, and drop-consumer context that the old signature does not provide");
    }

    /** was net/minecraft/class_1890.method_8214(Ljava/util/Map;Lnet/minecraft/class_1799;)V */
    public static void method_8214(net.minecraft.class_1890 self, java.util.Map a0, net.minecraft.class_1799 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the old enchantment map uses direct enchantment values, but the new API requires registry entries that cannot be resolved without registry access");
    }

    /** was net/minecraft/class_1890.method_8215(Lnet/minecraft/class_1799;)I */
    public static int method_8215(net.minecraft.class_1890 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new enchantment API requires a registry entry, which cannot be recovered from the old enchantment instance");
    }

    /** was net/minecraft/class_1890.method_8216(Lnet/minecraft/class_1309;)Z */
    public static boolean method_8216(net.minecraft.class_1890 self, net.minecraft.class_1309 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API surface exposes no boolean Frost Walker check for a LivingEntity");
    }

    /** was net/minecraft/class_1890.method_8222(Lnet/minecraft/class_1799;)Ljava/util/Map; */
    public static java.util.Map method_8222(net.minecraft.class_1890 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: enchantments are represented by class_9304 in the new API, which cannot faithfully provide the old Map contract");
    }

    /** was net/minecraft/class_1890.method_8224(Lnet/minecraft/class_1799;)Z */
    public static boolean method_8224(net.minecraft.class_1890 self, net.minecraft.class_1799 a0) {
        return self.method_57529(a0);
    }

    /** was net/minecraft/class_1890.method_8226(Lnet/minecraft/class_1309;)I */
    public static int method_8226(net.minecraft.class_1890 self, net.minecraft.class_1309 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API has no equivalent for querying this enchantment's maximum level across a living entity's equipped items");
    }

    /** was net/minecraft/class_1890.method_8228(Lnet/minecraft/class_1799;)Z */
    public static boolean method_8228(net.minecraft.class_1890 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: channeling is data-driven in 1.21.4 and the new API surface provides no context-free equivalent for the old hasChanneling(ItemStack) contract");
    }

    /** was net/minecraft/class_1890.method_8232(Lnet/minecraft/class_1309;)I */
    public static int method_8232(net.minecraft.class_1890 self, net.minecraft.class_1309 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API requires a registry entry for this enchantment, which cannot be recovered from the enchantment instance");
    }

    /** was net/minecraft/class_1890.method_8234(Lnet/minecraft/class_1309;)I */
    public static int method_8234(net.minecraft.class_1890 self, net.minecraft.class_1309 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API requires a registry entry for the enchantment, which cannot be recovered faithfully from the old enchantment instance and living entity");
    }

    /** was net/minecraft/class_1893.field_38223Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_38223() {
        throw new UnsupportedOperationException("CenturyBridge: field_38223 is now a registry key and cannot be resolved to class_1887 without registry access");
    }

    /** was net/minecraft/class_1893.field_50157Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_50157() {
        throw new UnsupportedOperationException("CenturyBridge: field_50157 is now a registry key, and the new API surface provides no registry lookup to recover the class_1887 value");
    }

    /** was net/minecraft/class_1893.field_50159Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_50159() {
        throw new UnsupportedOperationException("CenturyBridge: field_50159 is now a registry key and cannot be converted to an enchantment instance without registry access");
    }

    /** was net/minecraft/class_1893.field_9098Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9098() {
        throw new UnsupportedOperationException("CenturyBridge: field_9098 is now a registry key, and the supplied API cannot resolve it to a class_1887 instance");
    }

    /** was net/minecraft/class_1893.field_9100Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9100() {
        throw new UnsupportedOperationException("CenturyBridge: field_9100 is now a registry key and the provided API cannot resolve it to a net.minecraft.class_1887 instance");
    }

    /** was net/minecraft/class_1893.field_9103Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9103() {
        throw new UnsupportedOperationException("CenturyBridge: field_9103 is now a registry key and the provided API cannot resolve it to a class_1887 value");
    }

    /** was net/minecraft/class_1893.field_9105Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9105() {
        throw new UnsupportedOperationException("CenturyBridge: field_9105 is now a registry key, and the new API surface provides no registry access to resolve it to class_1887");
    }

    /** was net/minecraft/class_1893.field_9107Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9107() {
        throw new UnsupportedOperationException("CenturyBridge: field_9107 is now a registry key and cannot be resolved to class_1887 without registry access");
    }

    /** was net/minecraft/class_1893.field_9108Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9108() {
        throw new UnsupportedOperationException("CenturyBridge: field_9108 is now a registry key and cannot be resolved to a class_1887 instance without registry access");
    }

    /** was net/minecraft/class_1893.field_9109Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9109() {
        throw new UnsupportedOperationException("CenturyBridge: field_9109 is now a registry key and no registry lookup context is available to recover the class_1887 value");
    }

    /** was net/minecraft/class_1893.field_9110Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9110() {
        throw new UnsupportedOperationException("CenturyBridge: field_9110 is now a registry key and cannot produce a class_1887 instance without registry access");
    }

    /** was net/minecraft/class_1893.field_9111Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9111() {
        throw new UnsupportedOperationException("CenturyBridge: field_9111 is now a registry key and no registry lookup context is available to resolve it to class_1887");
    }

    /** was net/minecraft/class_1893.field_9112Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9112() {
        throw new UnsupportedOperationException("CenturyBridge: field_9112 is now a registry key and cannot be resolved to a class_1887 instance without registry access");
    }

    /** was net/minecraft/class_1893.field_9114Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9114() {
        throw new UnsupportedOperationException("CenturyBridge: field_9114 is now a registry key and no registry lookup is available to recover the class_1887 value");
    }

    /** was net/minecraft/class_1893.field_9117Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9117() {
        throw new UnsupportedOperationException("CenturyBridge: field_9117 is now a registry key and no registry lookup context is available to recover the class_1887 value");
    }

    /** was net/minecraft/class_1893.field_9118Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9118() {
        throw new UnsupportedOperationException("CenturyBridge: field_9118 is now a registry key and cannot be resolved to class_1887 without registry access");
    }

    /** was net/minecraft/class_1893.field_9119Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9119() {
        throw new UnsupportedOperationException("CenturyBridge: the new field is a registry key and no registry lookup context is available to resolve it to class_1887");
    }

    /** was net/minecraft/class_1893.field_9120Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9120() {
        throw new UnsupportedOperationException("CenturyBridge: field_9120 is now a registry key and cannot be converted to an enchantment instance without registry access");
    }

    /** was net/minecraft/class_1893.field_9121Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9121() {
        throw new UnsupportedOperationException("CenturyBridge: field_9121 is now a registry key and the provided API surface cannot resolve it to a class_1887 instance");
    }

    /** was net/minecraft/class_1893.field_9122Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9122() {
        throw new UnsupportedOperationException("CenturyBridge: field_9122 is now a registry key and the provided API cannot resolve it to a class_1887 instance");
    }

    /** was net/minecraft/class_1893.field_9125Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9125() {
        throw new UnsupportedOperationException("CenturyBridge: field_9125 is now a registry key and cannot be resolved to a class_1887 instance without registry access");
    }

    /** was net/minecraft/class_1893.field_9127Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9127() {
        throw new UnsupportedOperationException("CenturyBridge: field_9127 is now a registry key and no registry lookup context is available to recover the class_1887 instance");
    }

    /** was net/minecraft/class_1893.field_9128Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9128() {
        throw new UnsupportedOperationException("CenturyBridge: field_9128 is now a registry key and cannot be resolved to a class_1887 without registry access");
    }

    /** was net/minecraft/class_1893.field_9129Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9129() {
        throw new UnsupportedOperationException("CenturyBridge: field_9129 is now a registry key and cannot be resolved to class_1887 without registry access");
    }

    /** was net/minecraft/class_1893.field_9130Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9130() {
        throw new UnsupportedOperationException("CenturyBridge: field_9130 is now a registry key and cannot be resolved to a class_1887 without registry access");
    }

    /** was net/minecraft/class_1893.field_9131Lnet/minecraft/class_1887; */
    public static net.minecraft.class_1887 field_9131() {
        throw new UnsupportedOperationException("CenturyBridge: field_9131 is now a registry key and no registry lookup is available to recover the class_1887 instance");
    }

    /** was net/minecraft/class_189.method_831()Ljava/lang/String; */
    public static java.lang.String method_831(net.minecraft.class_189 self) {
        return self.method_15434();
    }

    /** was net/minecraft/class_1936.method_32888(Lnet/minecraft/class_5712;Lnet/minecraft/class_243;Lnet/minecraft/class_5712$class_7397;)V */
    public static void method_32888(net.minecraft.class_1936 self, net.minecraft.class_5712 a0, net.minecraft.class_243 a1, net.minecraft.class_5712.class_7397 a2) {
        self.method_32888(net.minecraft.class_6880.method_40223(a0), a1, a2);
    }

    /** was net/minecraft/class_1936.method_33596(Lnet/minecraft/class_1297;Lnet/minecraft/class_5712;Lnet/minecraft/class_2338;)V */
    public static void method_33596(net.minecraft.class_1936 self, net.minecraft.class_1297 a0, net.minecraft.class_5712 a1, net.minecraft.class_2338 a2) {
        self.method_33596(a0, net.minecraft.class_6880.method_40223(a1), a2);
    }

    /** was net/minecraft/class_1936.method_43275(Lnet/minecraft/class_1297;Lnet/minecraft/class_5712;Lnet/minecraft/class_243;)V */
    public static void method_43275(net.minecraft.class_1936 self, net.minecraft.class_1297 a0, net.minecraft.class_5712 a1, net.minecraft.class_243 a2) {
        self.method_43275(a0, net.minecraft.class_6880.method_40223(a1), a2);
    }

    /** was net/minecraft/class_1936.method_43276(Lnet/minecraft/class_5712;Lnet/minecraft/class_2338;Lnet/minecraft/class_5712$class_7397;)V */
    public static void method_43276(net.minecraft.class_1936 self, net.minecraft.class_5712 a0, net.minecraft.class_2338 a1, net.minecraft.class_5712.class_7397 a2) {
        self.method_43276(net.minecraft.class_6880.method_40223(a0), a1, a2);
    }

    /** was net/minecraft/class_2232.method_23727(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)Lnet/minecraft/class_5341; */
    public static net.minecraft.class_5341 method_23727(net.minecraft.class_2232 self, com.mojang.brigadier.context.CommandContext a0, java.lang.String a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new API only retrieves an identifier and no longer resolves it to a net.minecraft.class_5341 predicate");
    }

    /** was net/minecraft/class_2232.method_32688(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)Lnet/minecraft/class_117; */
    public static net.minecraft.class_117 method_32688(net.minecraft.class_2232 self, com.mojang.brigadier.context.CommandContext a0, java.lang.String a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_2232 no longer exposes an API that resolves the parsed identifier to class_117");
    }

    /** was net/minecraft/class_2385.method_10272(Lnet/minecraft/class_5321;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/class_6880$class_6883; */
    public static net.minecraft.class_6880.class_6883 method_10272(net.minecraft.class_2385 self, net.minecraft.class_5321 a0, java.lang.Object a1, com.mojang.serialization.Lifecycle a2) {
        return self.method_10272(a0, a1, new net.minecraft.class_9248(java.util.Optional.empty(), a2));
    }

    /** was net/minecraft/class_2487.method_29143()Ljava/util/Map; */
    public static java.util.Map method_29143(net.minecraft.class_2487 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new API does not expose the compound tag's backing entry map required by the old method");
    }

    /** was net/minecraft/class_2509$class_5320.field_25131Lnet/minecraft/class_2509; */
    public static net.minecraft.class_2509 field_25131() {
        throw new UnsupportedOperationException("CenturyBridge: deleted enclosing class_2509 instance cannot be recovered without a receiver");
    }

    /** was net/minecraft/class_2512.method_10683(Lnet/minecraft/class_2487;)Lcom/mojang/authlib/GameProfile; */
    public static com.mojang.authlib.GameProfile method_10683(net.minecraft.class_2512 self, net.minecraft.class_2487 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_2512 API has no GameProfile deserialization equivalent");
    }

    /** was net/minecraft/class_2512.method_10684(Lnet/minecraft/class_2487;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/class_2487; */
    public static net.minecraft.class_2487 method_10684(net.minecraft.class_2512 self, net.minecraft.class_2487 a0, com.mojang.authlib.GameProfile a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_2512 API has no GameProfile serialization equivalent");
    }

    /** was net/minecraft/class_2547.method_10839(Lnet/minecraft/class_2561;)V */
    public static void method_10839(net.minecraft.class_2547 self, net.minecraft.class_2561 a0) {
        self.method_10839(new net.minecraft.class_9812(a0));
    }

    /** was net/minecraft/class_2547.method_55597(Lnet/minecraft/class_129;)V */
    public static void method_55597(net.minecraft.class_2547 self, net.minecraft.class_129 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API requires the originating crash report, which cannot be recovered from the old crash report section argument");
    }

    /** was net/minecraft/class_2558$class_2559.method_10846()Ljava/lang/String; */
    public static java.lang.String method_10846(net.minecraft.class_2558.class_2559 self) {
        return self.method_15434();
    }

    /** was net/minecraft/class_2558$class_2559.method_10848(Ljava/lang/String;)Lnet/minecraft/class_2558$class_2559; */
    public static net.minecraft.class_2558.class_2559 method_10848(net.minecraft.class_2558.class_2559 self, java.lang.String a0) {
        for (net.minecraft.class_2558.class_2559 value : net.minecraft.class_2558.class_2559.values()) {
            if (value.method_15434().equals(a0)) {
                return value;
            }
        }
        return null;
    }

    /** was net/minecraft/class_2573.method_10910(Lnet/minecraft/class_1799;)I */
    public static int method_10910(net.minecraft.class_2573 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new jukebox block entity API exposes no equivalent for deriving the old comparator output from an ItemStack");
    }

    /** was net/minecraft/class_2573.method_10911()Ljava/util/List; */
    public static java.util.List method_10911(net.minecraft.class_2573 self) {
        throw new UnsupportedOperationException("CenturyBridge: banner pattern list is stored in a new component type whose API is not available on class_2573");
    }

    /** was net/minecraft/class_2573.method_24281(Lnet/minecraft/class_1799;)Lnet/minecraft/class_2499; */
    public static net.minecraft.class_2499 method_24281(net.minecraft.class_2573 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: banner pattern data is component-backed in the new API and cannot be faithfully returned as the old NBT list");
    }

    /** was net/minecraft/class_2573.method_38992(Lnet/minecraft/class_1799;)V */
    public static void method_38992(net.minecraft.class_2573 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API exposes no public operation to apply an ItemStack's banner patterns and custom name, and the equivalent component reader is protected");
    }

    /** was net/minecraft/class_2615.field_12026Lnet/minecraft/class_265; */
    public static net.minecraft.class_265 field_12026() {
        return net.minecraft.class_259.method_1078(net.minecraft.class_2615.field_12026);
    }

    /** was net/minecraft/class_2643.method_30276(Lnet/minecraft/class_1297;)Z */
    public static boolean method_30276(net.minecraft.class_2643 self, net.minecraft.class_1297 a0) {
        throw new UnsupportedOperationException("CenturyBridge: entity-based portal eligibility check was removed and the new API surface exposes no faithful equivalent");
    }

    /** was net/minecraft/class_315$4.field_38301[I */
    public static int[] field_38301() {
        throw new UnsupportedOperationException("CenturyBridge: moved field net.minecraft.class_315.class_5.field_38301 is not present in the provided new API surface, so its int[] mapping cannot be reconstructed safely");
    }

    /** was net/minecraft/class_3218.method_18207(Lnet/minecraft/class_3222;)V */
    public static void method_18207(net.minecraft.class_3218 self, net.minecraft.class_3222 a0) {
        self.method_18770(a0, net.minecraft.class_1297.class_5529.field_26999);
    }

    /** was net/minecraft/class_3218.method_29200(Lnet/minecraft/class_3218;)V */
    public static void method_29200(net.minecraft.class_3218 self, net.minecraft.class_3218 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the deleted End spawn platform generation helper has no equivalent in the provided class_3218 API");
    }

    /** was net/minecraft/class_3230.field_19270Lnet/minecraft/class_3230; */
    public static net.minecraft.class_3230 field_19270() {
        return net.minecraft.class_3230.method_14291("light", net.minecraft.class_3230.field_14033.method_14292());
    }

    /** was net/minecraft/class_3298.method_45305()Z */
    public static boolean method_45305(net.minecraft.class_3298 self) {
        throw new UnsupportedOperationException("CenturyBridge: the new Resource API no longer exposes whether its resource pack is always stable");
    }

    /** was net/minecraft/class_3414.method_47961(Lnet/minecraft/class_2540;)Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 method_47961(net.minecraft.class_3414 self, net.minecraft.class_2540 a0) {
        return net.minecraft.class_3414.field_48278.decode(a0);
    }

    /** was net/minecraft/class_3417.field_14578Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14578() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14578.comp_349();
    }

    /** was net/minecraft/class_3417.field_14592Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14592() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14592.comp_349();
    }

    /** was net/minecraft/class_3417.field_14654Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14654() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14654.comp_349();
    }

    /** was net/minecraft/class_3417.field_14656Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14656() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14656.comp_349();
    }

    /** was net/minecraft/class_3417.field_14744Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14744() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14744.comp_349();
    }

    /** was net/minecraft/class_3417.field_14759Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14759() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14759.comp_349();
    }

    /** was net/minecraft/class_3417.field_14806Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14806() {
        return net.minecraft.class_3417.field_14806.comp_349();
    }

    /** was net/minecraft/class_3417.field_14829Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14829() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14829.comp_349();
    }

    /** was net/minecraft/class_3417.field_14838Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14838() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14838.comp_349();
    }

    /** was net/minecraft/class_3417.field_14944Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_14944() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_14944.comp_349();
    }

    /** was net/minecraft/class_3417.field_15039Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_15039() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_15039.comp_349();
    }

    /** was net/minecraft/class_3417.field_15059Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_15059() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_15059.comp_349();
    }

    /** was net/minecraft/class_3417.field_15152Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_15152() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_15152.comp_349();
    }

    /** was net/minecraft/class_3417.field_15169Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_15169() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_15169.comp_349();
    }

    /** was net/minecraft/class_3417.field_23060Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_23060() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_23060.comp_349();
    }

    /** was net/minecraft/class_3417.field_23968Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_23968() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_23968.comp_349();
    }

    /** was net/minecraft/class_3417.field_35343Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_35343() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_35343.comp_349();
    }

    /** was net/minecraft/class_3417.field_38924Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_38924() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_38924.comp_349();
    }

    /** was net/minecraft/class_3417.field_44700Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_44700() {
        return (net.minecraft.class_3414) net.minecraft.class_3417.field_44700.comp_349();
    }

    /** was net/minecraft/class_3417.field_47191Lnet/minecraft/class_3414; */
    public static net.minecraft.class_3414 field_47191() {
        return net.minecraft.class_3417.field_49049.comp_349();
    }

    /** was net/minecraft/class_3910$3.field_17299Lnet/minecraft/class_3910; */
    public static net.minecraft.class_3910 field_17299() {
        throw new UnsupportedOperationException("CenturyBridge: deleted anonymous-class outer-instance field has no constant net.minecraft.class_3910 equivalent");
    }

    /** was net/minecraft/class_3910$4.field_17300Lnet/minecraft/class_3910; */
    public static net.minecraft.class_3910 field_17300() {
        throw new UnsupportedOperationException("CenturyBridge: deleted synthetic outer-instance field cannot be reconstructed without a class_3910 receiver");
    }

    /** was net/minecraft/class_437.field_44669Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_44669() {
        return net.minecraft.class_2960.method_60655("minecraft", "textures/gui/options_background.png");
    }

    /** was net/minecraft/class_4406.field_19963I */
    public static int field_19963() {
        throw new UnsupportedOperationException("CenturyBridge: field_19963 was an instance-specific column position and cannot be reconstructed by a receiverless static helper");
    }

    /** was net/minecraft/class_4406.field_19964I */
    public static int field_19964() {
        return 150;
    }

    /** was net/minecraft/class_4406.field_26499Lnet/minecraft/class_2561; */
    public static net.minecraft.class_2561 field_26499() {
        return net.minecraft.class_2561.method_43471("mco.configure.world.invites.ops.tooltip");
    }

    /** was net/minecraft/class_4514.method_32237(Lnet/minecraft/class_3218;)V */
    public static void method_32237(net.minecraft.class_4514 self, net.minecraft.class_3218 a0) {
        if (self.comp_2212() != null) {
            self.comp_2212().accept(a0);
        }
    }

    /** was net/minecraft/class_4518.method_22190(Lnet/minecraft/class_4517;)V */
    public static void method_22190(net.minecraft.class_4518 self, net.minecraft.class_4517 a0) {
        throw new UnsupportedOperationException("CenturyBridge: onFailed now requires a TestRunContext, which cannot be derived from the old receiver and GameTestState");
    }

    /** was net/minecraft/class_4524$1.field_25305Lnet/minecraft/class_4524; */
    public static net.minecraft.class_4524 field_25305() {
        throw new UnsupportedOperationException("CenturyBridge: field_25305 was an enclosing-instance reference and cannot be reconstructed without a class_4524 instance");
    }

    /** was net/minecraft/class_4527.method_22280(Lnet/minecraft/class_3218;Lnet/minecraft/class_4524;)V */
    public static void method_22280(net.minecraft.class_4527 self, net.minecraft.class_3218 a0, net.minecraft.class_4524 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new API cannot express the deleted class_4524-based operation");
    }

    /** was net/minecraft/class_4657.field_24946Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24946() {
        return net.minecraft.class_4657.field_24946.codec();
    }

    /** was net/minecraft/class_4658.field_24957Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24957() {
        return net.minecraft.class_4658.field_24957.codec();
    }

    /** was net/minecraft/class_4659.field_24958Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24958() {
        return net.minecraft.class_4659.field_24958.codec();
    }

    /** was net/minecraft/class_4661.field_24960Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24960() {
        return net.minecraft.class_4661.field_24960.codec();
    }

    /** was net/minecraft/class_4664.field_24964Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24964() {
        return net.minecraft.class_4664.field_24964.codec();
    }

    /** was net/minecraft/class_4760.method_30081(Lnet/minecraft/class_3414;)V */
    public static void method_30081(net.minecraft.class_4760 self, net.minecraft.class_3414 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_4760 API has no sound-setting method equivalent to old method_30081");
    }

    /** was net/minecraft/class_4785.field_37804Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37804() {
        return net.minecraft.class_4785.field_37804.codec();
    }

    /** was net/minecraft/class_4836.method_30086(Lnet/minecraft/class_3414;)V */
    public static void method_30086(net.minecraft.class_4836 self, net.minecraft.class_3414 a0) {
        self.method_5783(a0, 1.0F, 1.0F);
    }

    /** was net/minecraft/class_4881.field_22626J */
    public static long field_22626() {
        throw new UnsupportedOperationException("CenturyBridge: deleted per-instance serverId cannot be recovered without a receiver");
    }

    /** was net/minecraft/class_4905.field_33042I */
    public static int field_33042() {
        return 13;
    }

    /** was net/minecraft/class_4994.field_25006Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25006() {
        return net.minecraft.class_4994.field_25006.codec();
    }

    /** was net/minecraft/class_5131.method_26847(Lcom/google/common/collect/Multimap;)V */
    public static void method_26847(net.minecraft.class_5131 self, com.google.common.collect.Multimap a0) {
        self.method_59932(a0);
    }

    /** was net/minecraft/class_5131.method_27305(Lnet/minecraft/class_1320;Ljava/util/UUID;)Z */
    public static boolean method_27305(net.minecraft.class_5131 self, net.minecraft.class_1320 a0, java.util.UUID a1) {
        throw new UnsupportedOperationException("CenturyBridge: UUID-based attribute modifier removal cannot be expressed by the new identifier-based API");
    }

    /** was net/minecraft/class_5131.method_27306(Lnet/minecraft/class_1320;)Z */
    public static boolean method_27306(net.minecraft.class_5131 self, net.minecraft.class_1320 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API requires a RegistryEntry for the attribute, but the old raw attribute cannot be converted faithfully using class_5131's API");
    }

    /** was net/minecraft/class_5131.method_27307(Lnet/minecraft/class_1320;Ljava/util/UUID;)D */
    public static double method_27307(net.minecraft.class_5131 self, net.minecraft.class_1320 a0, java.util.UUID a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new API requires a registry entry for the attribute, but the old raw class_1320 parameter cannot be converted faithfully");
    }

    /** was net/minecraft/class_5134.field_23717Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_23717() {
        return (net.minecraft.class_1320) net.minecraft.class_5134.field_23717.comp_349();
    }

    /** was net/minecraft/class_5134.field_23721Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_23721() {
        return (net.minecraft.class_1320) net.minecraft.class_5134.field_23721.comp_349();
    }

    /** was net/minecraft/class_5134.field_23722Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_23722() {
        return (net.minecraft.class_1320) net.minecraft.class_5134.field_23722.comp_349();
    }

    /** was net/minecraft/class_5134.field_23726Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_23726() {
        return (net.minecraft.class_1320) net.minecraft.class_5134.field_23726.comp_349();
    }

    /** was net/minecraft/class_5205.field_24928Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24928() {
        return net.minecraft.class_5205.field_24928.codec();
    }

    /** was net/minecraft/class_5210.field_24934Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24934() {
        return net.minecraft.class_5210.field_24934.codec();
    }

    /** was net/minecraft/class_5215.field_24970Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24970() {
        return net.minecraft.class_5215.field_24970.codec();
    }

    /** was net/minecraft/class_5217.method_166()I */
    public static int method_166(net.minecraft.class_5217 self) {
        return self.method_56126().method_10260();
    }

    /** was net/minecraft/class_5217.method_215()I */
    public static int method_215(net.minecraft.class_5217 self) {
        return self.method_56126().method_10263();
    }

    /** was net/minecraft/class_5269.method_30657(F)V */
    public static void method_30657(net.minecraft.class_5269 self, float a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new method_187 requires a net.minecraft.class_2338 position that the old method_30657 contract did not provide");
    }

    /** was net/minecraft/class_5418.field_41030F */
    public static float field_41030() {
        return 1.79F;
    }

    /** was net/minecraft/class_5707$class_5708.method_32929(Lnet/minecraft/class_2540;)Lnet/minecraft/class_5707; */
    public static net.minecraft.class_5707 method_32929(net.minecraft.class_5707.class_5708 self, net.minecraft.class_2540 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API exposes only codec metadata and cannot decode class_5707 from class_2540");
    }

    /** was net/minecraft/class_5707$class_5708.method_32930(Lnet/minecraft/class_2540;Lnet/minecraft/class_5707;)V */
    public static void method_32930(net.minecraft.class_5707.class_5708 self, net.minecraft.class_2540 a0, net.minecraft.class_5707 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new API exposes only codec metadata and cannot serialize this type to class_2540 with the old method_32930 contract");
    }

    /** was net/minecraft/class_5713.method_32943(Lnet/minecraft/class_5712;Lnet/minecraft/class_243;Lnet/minecraft/class_5712$class_7397;Lnet/minecraft/class_5713$class_7721;)Z */
    public static boolean method_32943(net.minecraft.class_5713 self, net.minecraft.class_5712 a0, net.minecraft.class_243 a1, net.minecraft.class_5712.class_7397 a2, net.minecraft.class_5713.class_7721 a3) {
        return self.method_32943(net.minecraft.class_6880.method_40223(a0), a1, a2, a3);
    }

    /** was net/minecraft/class_5714.method_32947(Lnet/minecraft/class_3218;Lnet/minecraft/class_5712;Lnet/minecraft/class_5712$class_7397;Lnet/minecraft/class_243;)Z */
    public static boolean method_32947(net.minecraft.class_5714 self, net.minecraft.class_3218 a0, net.minecraft.class_5712 a1, net.minecraft.class_5712.class_7397 a2, net.minecraft.class_243 a3) {
        return self.method_32947(a0, net.minecraft.class_6880.method_40223(a1), a2, a3);
    }

    /** was net/minecraft/class_5717.method_32957()Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_32957(net.minecraft.class_5717 self) {
        return self.method_32957().codec();
    }

    /** was net/minecraft/class_5717.method_32958(Lnet/minecraft/class_5716;Lnet/minecraft/class_2540;)V */
    public static void method_32958(net.minecraft.class_5717 self, net.minecraft.class_5716 a0, net.minecraft.class_2540 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the old packet serialization contract has no equivalent in the new codec-only API");
    }

    /** was net/minecraft/class_5717.method_32962(Lnet/minecraft/class_2540;)Lnet/minecraft/class_5716; */
    public static net.minecraft.class_5716 method_32962(net.minecraft.class_5717 self, net.minecraft.class_2540 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_5717 no longer exposes packet decoding from class_2540, and the available MapCodec cannot faithfully replace the old buffer-based contract");
    }

    /** was net/minecraft/class_5717.method_32963(Lnet/minecraft/class_2540;)Lnet/minecraft/class_5716; */
    public static net.minecraft.class_5716 method_32963(net.minecraft.class_5717 self, net.minecraft.class_2540 a0) {
        return (net.minecraft.class_5716) self.method_56123().decode(a0);
    }

    /** was net/minecraft/class_5864.method_33925(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Lnet/minecraft/class_5864; */
    public static net.minecraft.class_5864 method_33925(net.minecraft.class_5864 self, java.lang.String a0, com.mojang.serialization.Codec a1) {
        return net.minecraft.class_5864.method_33925(a0, (com.mojang.serialization.MapCodec) com.mojang.serialization.MapCodec.assumeMapUnsafe(a1));
    }

    /** was net/minecraft/class_5865.field_29012Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_29012() {
        return net.minecraft.class_5865.field_29012.codec();
    }

    /** was net/minecraft/class_5929.field_29301Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_29301() {
        return net.minecraft.class_5929.field_29301.codec();
    }

    /** was net/minecraft/class_6024.field_30011B */
    public static byte field_30011() {
        return (byte) 11;
    }

    /** was net/minecraft/class_6054.field_33494F */
    public static float field_33494() {
        return 1.5F;
    }

    /** was net/minecraft/class_6067.method_46399(Lnet/minecraft/class_2487;)V */
    public static void method_46399(net.minecraft.class_6067 self, net.minecraft.class_2487 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new method_46399 requires a class_7225.class_7874 registry lookup, but the old contract provides no equivalent lookup context");
    }

    /** was net/minecraft/class_6067.method_46400(Lnet/minecraft/class_2487;)V */
    public static void method_46400(net.minecraft.class_6067 self, net.minecraft.class_2487 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_46400 now requires a class_7225.class_7874 registry lookup provider, which the old signature does not supply");
    }

    /** was net/minecraft/class_6088.field_31148I */
    public static int field_31148() {
        return 2005;
    }

    /** was net/minecraft/class_6088.field_31161I */
    public static int field_31161() {
        return 1003;
    }

    /** was net/minecraft/class_6121.field_31537Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_31537() {
        return net.minecraft.class_6121.field_31537.codec();
    }

    /** was net/minecraft/class_6124.field_31544Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_31544() {
        return net.minecraft.class_6124.field_31544.codec();
    }

    /** was net/minecraft/class_6378.field_33772Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_33772() {
        return net.minecraft.class_6378.field_33772.codec();
    }

    /** was net/minecraft/class_6501$2.field_37413Lnet/minecraft/class_6501; */
    public static net.minecraft.class_6501 field_37413() {
        return net.minecraft.class_6501.field_37409;
    }

    /** was net/minecraft/class_6578.field_34702Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_34702() {
        return net.minecraft.class_6578.field_34702.codec();
    }

    /** was net/minecraft/class_6580.field_34711Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_34711() {
        return net.minecraft.class_6580.field_34711.codec();
    }

    /** was net/minecraft/class_6611.method_38657(Lnet/minecraft/class_2539;ILjava/net/SocketAddress;I)V */
    public static void method_38657(net.minecraft.class_6611 self, net.minecraft.class_2539 a0, int a1, java.net.SocketAddress a2, int a3) {
        throw new UnsupportedOperationException("CenturyBridge: the legacy int cannot be faithfully converted to the required class_9145 packet type and identifier");
    }

    /** was net/minecraft/class_6642.field_35035Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35035() {
        return net.minecraft.class_6642.field_35035.codec();
    }

    /** was net/minecraft/class_6783.field_35700Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35700() {
        return net.minecraft.class_6783.field_35700.codec();
    }

    /** was net/minecraft/class_6784.field_35702Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35702() {
        return net.minecraft.class_6784.field_35702.codec();
    }

    /** was net/minecraft/class_6795.field_35725Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35725() {
        return net.minecraft.class_6795.field_35725.codec();
    }

    /** was net/minecraft/class_6847.field_36284Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_36284() {
        return net.minecraft.class_6847.field_36284.codec();
    }

    /** was net/minecraft/class_6848.field_36288Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_36288() {
        return net.minecraft.class_6848.field_36288.codec();
    }

    /** was net/minecraft/class_6864$class_5748.method_40108()Z */
    public static boolean method_40108(net.minecraft.class_6864.class_5748 self) {
        return self.method_61315();
    }

    /** was net/minecraft/class_6871.field_36419Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_36419() {
        return net.minecraft.class_6871.field_36419.codec();
    }

    /** was net/minecraft/class_7102.field_37448Lnet/minecraft/class_1856; */
    public static net.minecraft.class_1856 field_37448() {
        return net.minecraft.class_1856.method_8101(net.minecraft.class_1802.field_8550);
    }

    /** was net/minecraft/class_7130$class_7131.field_37634Lnet/minecraft/class_7130; */
    public static net.minecraft.class_7130 field_37634() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted synthetic outer-instance field represented the specific class_7130 used to construct each class_7131, which a receiverless static helper cannot recover");
    }

    /** was net/minecraft/class_7265.method_42276()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_42276(net.minecraft.class_7265 self) {
        return self.method_42276().method_29177();
    }

    /** was net/minecraft/class_7265.method_42285(Lnet/minecraft/class_2487;)V */
    public static void method_42285(net.minecraft.class_7265 self, net.minecraft.class_2487 a0) {
        self.method_42285(a0, self.method_37908().method_30349());
    }

    /** was net/minecraft/class_7265.method_42288(Lnet/minecraft/class_2487;)V */
    public static void method_42288(net.minecraft.class_7265 self, net.minecraft.class_2487 a0) {
        self.method_42288(a0, self.method_37908().method_30349());
    }

    /** was net/minecraft/class_7368$class_8622$1.field_45060Lnet/minecraft/class_7368$class_8622; */
    public static net.minecraft.class_7368.class_8622 field_45060() {
        throw new UnsupportedOperationException("CenturyBridge: deleted field stored a specific enclosing class_8622 instance, which cannot be reconstructed by a receiverless static shim");
    }

    /** was net/minecraft/class_7386.field_38771Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_38771() {
        return net.minecraft.class_7386.field_38771.codec();
    }

    /** was net/minecraft/class_7450.method_43748(Ljava/lang/String;)Lnet/minecraft/class_7469; */
    public static net.minecraft.class_7469 method_43748(net.minecraft.class_7450 self, java.lang.String a0) {
        for (net.minecraft.class_7450.class_7599 entry : self.comp_912()) {
            if (entry.comp_913().equals(a0)) {
                return entry.comp_914();
            }
        }
        return null;
    }

    /** was net/minecraft/class_757.field_4010I */
    public static int field_4010() {
        return 0;
    }

    /** was net/minecraft/class_757.field_4023I */
    public static int field_4023() {
        return 25;
    }

    /** was net/minecraft/class_783.field_4224Ljava/lang/String; */
    public static java.lang.String field_4224() {
        throw new UnsupportedOperationException("CenturyBridge: the old instance textureId field requires a ModelElementFace receiver, but this static field shim has none");
    }

    /** was net/minecraft/class_783.field_4225Lnet/minecraft/class_2350; */
    public static net.minecraft.class_2350 field_4225() {
        throw new UnsupportedOperationException("CenturyBridge: old field_4225 was instance-specific, but this static no-argument shim has no receiver for comp_2867()");
    }

    /** was net/minecraft/class_783.field_4226I */
    public static int field_4226() {
        throw new UnsupportedOperationException("CenturyBridge: old instance tint index field requires a receiver, but the static field shim provides none");
    }

    /** was net/minecraft/class_7898$class_7900$2.field_41007Lnet/minecraft/class_7898$class_7900; */
    public static net.minecraft.class_7898.class_7900 field_41007() {
        throw new UnsupportedOperationException("CenturyBridge: deleted captured outer-instance field cannot be recovered without an instance of class_7898$class_7900$2");
    }

    /** was net/minecraft/class_7898$class_7900$4.field_41016Lnet/minecraft/class_7898$class_7900; */
    public static net.minecraft.class_7898.class_7900 field_41016() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted captured class_7900 instance is not exposed by the new API");
    }

    /** was net/minecraft/class_7898$class_7900$5.field_41022Lnet/minecraft/class_7898$class_7900; */
    public static net.minecraft.class_7898.class_7900 field_41022() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted class_7900-valued field has no equivalent in the new API surface");
    }

    /** was net/minecraft/class_7923.field_41176Lnet/minecraft/class_2378; */
    public static net.minecraft.class_2378 field_41176() {
        throw new UnsupportedOperationException("CenturyBridge: enchantments moved to a dynamic registry and no global registry instance is available without registry context");
    }

    /** was net/minecraft/class_7923.field_41179Lnet/minecraft/class_7922; */
    public static net.minecraft.class_7922 field_41179() {
        throw new UnsupportedOperationException("CenturyBridge: replacement field field_41179 has type net.minecraft.class_2378, which cannot be safely converted to the old net.minecraft.class_7922 contract");
    }

    /** was net/minecraft/class_7923.field_41182Lnet/minecraft/class_7922; */
    public static net.minecraft.class_7922 field_41182() {
        throw new UnsupportedOperationException("CenturyBridge: the built-in painting variant registry was removed and no equivalent net.minecraft.class_7922 registry exists");
    }

    /** was net/minecraft/class_8173.method_49205(Lnet/minecraft/class_5321;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_49205(net.minecraft.class_8173 self, net.minecraft.class_5321 a0) {
        return a0.method_29177();
    }

    /** was net/minecraft/class_8181.method_54080()Lnet/minecraft/class_2586; */
    public static net.minecraft.class_2586 method_54080(net.minecraft.class_8181 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_54080 moved to class_8181.class_9210 and class_8181 exposes no equivalent block entity accessor");
    }

    /** was net/minecraft/class_8514$class_5719.method_32969(Lnet/minecraft/class_3218;Lnet/minecraft/class_2338;Lnet/minecraft/class_5712;Lnet/minecraft/class_1297;Lnet/minecraft/class_1297;F)V */
    public static void method_32969(net.minecraft.class_8514.class_5719 self, net.minecraft.class_3218 a0, net.minecraft.class_2338 a1, net.minecraft.class_5712 a2, net.minecraft.class_1297 a3, net.minecraft.class_1297 a4, float a5) {
        self.method_32969(a0, a1, net.minecraft.class_6880.method_40223(a2), a3, a4, a5);
    }

    /** was net/minecraft/class_8514$class_5719.method_32970(Lnet/minecraft/class_3218;Lnet/minecraft/class_2338;Lnet/minecraft/class_5712;Lnet/minecraft/class_5712$class_7397;)Z */
    public static boolean method_32970(net.minecraft.class_8514.class_5719 self, net.minecraft.class_3218 a0, net.minecraft.class_2338 a1, net.minecraft.class_5712 a2, net.minecraft.class_5712.class_7397 a3) {
        return self.method_32970(a0, a1, net.minecraft.class_6880.method_40223(a2), a3);
    }

    /** was net/minecraft/class_8514$class_5719.method_42324(Lnet/minecraft/class_5712;Lnet/minecraft/class_5712$class_7397;)Z */
    public static boolean method_42324(net.minecraft.class_8514.class_5719 self, net.minecraft.class_5712 a0, net.minecraft.class_5712.class_7397 a1) {
        return self.method_42324(net.minecraft.class_6880.method_40223(a0), a1);
    }

    /** was net/minecraft/class_8514.method_51385(Lnet/minecraft/class_5712;)I */
    public static int method_51385(net.minecraft.class_8514 self, net.minecraft.class_5712 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the old GameEvent value cannot be converted faithfully to the RegistryKey required by the new API");
    }

    /** was net/minecraft/class_8514.method_51386(I)Lnet/minecraft/class_5712; */
    public static net.minecraft.class_5712 method_51386(net.minecraft.class_8514 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_51386 now returns class_5321, and the provided API exposes no faithful conversion to the former class_5712 return type");
    }

    /** was net/minecraft/class_8710.comp_1678()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 comp_1678(net.minecraft.class_8710 self) {
        throw new UnsupportedOperationException("CenturyBridge: comp_1678 moved to class_8711, and class_8710 exposes no equivalent accessor or conversion to class_8711");
    }

    /** was net/minecraft/class_8779.method_53635(Lnet/minecraft/class_2540;)V */
    public static void method_53635(net.minecraft.class_8779 self, net.minecraft.class_2540 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_8779 no longer exposes packet serialization, and the provided API has no equivalent writer");
    }

    /** was net/minecraft/class_8779.method_53636(Lnet/minecraft/class_2540;)Lnet/minecraft/class_8779; */
    public static net.minecraft.class_8779 method_53636(net.minecraft.class_8779 self, net.minecraft.class_2540 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the removed buffer deserialization method cannot be expressed using only the new value constructor and accessors");
    }

    /** was net/minecraft/class_8887$1.field_46819Lnet/minecraft/class_8887; */
    public static net.minecraft.class_8887 field_46819() {
        throw new UnsupportedOperationException("CenturyBridge: deleted field held a class_8887 instance, but the new API exposes no equivalent instance or static constant");
    }

    /** was net/minecraft/class_8889.method_54502()Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_54502(net.minecraft.class_8889 self) {
        return self.method_54502().codec();
    }

    /** was net/minecraft/class_8901.field_46903I */
    public static int field_46903() {
        return 2;
    }

    /** was net/minecraft/class_8934.method_54869()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_54869(net.minecraft.class_8934 self) {
        return self.method_54869().method_29177();
    }

    /** was net/minecraft/class_8957.comp_2082()I */
    public static int comp_2082(net.minecraft.class_8957 self) {
        return self.method_59996();
    }

    /** was net/minecraft/class_8957.method_55087(Lnet/minecraft/class_2540;)V */
    public static void method_55087(net.minecraft.class_8957 self, net.minecraft.class_2540 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_8957 no longer exposes packet serialization to class_2540, and the new codec API cannot faithfully reproduce the old buffer-writing contract");
    }

    /** was net/minecraft/class_8957.method_55090(Lnet/minecraft/class_2540;)Lnet/minecraft/class_8957; */
    public static net.minecraft.class_8957 method_55090(net.minecraft.class_8957 self, net.minecraft.class_2540 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API provides no packet-buffer decoding equivalent for class_8957");
    }

    /** was net/minecraft/class_8966.method_55191(Lnet/minecraft/class_8965;)V */
    public static void method_55191(net.minecraft.class_8966 self, net.minecraft.class_8965 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the old mutating operation for class_8965 has no faithful equivalent in the new API");
    }

    /** was net/minecraft/class_8.method_43568(Lnet/minecraft/class_9;)Lnet/minecraft/class_4459; */
    public static net.minecraft.class_4459 method_43568(net.minecraft.class_8 self, net.minecraft.class_9 a0) {
        return new net.minecraft.class_4459(a0);
    }

    /** was net/minecraft/class_9023.method_55461(Lnet/minecraft/class_2540;Lnet/minecraft/class_9022;)V */
    public static void method_55461(net.minecraft.class_9023 self, net.minecraft.class_2540 a0, net.minecraft.class_9022 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new API exposes only codec and type metadata and cannot express the deleted packet serialization contract");
    }

    /** was net/minecraft/class_9023.method_55462(Lnet/minecraft/class_2540;)Lnet/minecraft/class_9022; */
    public static net.minecraft.class_9022 method_55462(net.minecraft.class_9023 self, net.minecraft.class_2540 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new packet codec requires a RegistryByteBuf, but the old contract accepts an arbitrary PacketByteBuf");
    }

    /** was net/minecraft/class_9024.method_55468(Lnet/minecraft/class_2540;Lnet/minecraft/class_9022;)V */
    public static void method_55468(net.minecraft.class_9024 self, net.minecraft.class_2540 a0, net.minecraft.class_9022 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_9024 API exposes codec-based serialization only and cannot reproduce the deleted packet-buffer write contract");
    }

    /** was net/minecraft/class_9069.method_56997()Z */
    public static boolean method_56997(net.minecraft.class_9069 self) {
        throw new UnsupportedOperationException("CenturyBridge: the removed shouldUnrollAndFlee predicate has no equivalent in the new ArmadilloEntity API");
    }

    /** was net/minecraft/class_927.field_32940I */
    public static int field_32940() {
        return 24;
    }

    /** was net/minecraft/class_9304.field_50014I */
    public static int field_50014() {
        return 255;
    }

    /** was net/minecraft/class_1521.method_6855()V */
    public static void method_6855(net.minecraft.class_1521 self) {
        throw new UnsupportedOperationException("CenturyBridge: the old receiver does not expose the ServerWorld required by the new method_6855 contract");
    }

    /** was net/minecraft/class_1681.<init>(Lnet/minecraft/class_1937;Lnet/minecraft/class_1309;)V */
    public static net.minecraft.class_1681 cbNewclass_1681(net.minecraft.class_1937 a0, net.minecraft.class_1309 a1) {
        return new net.minecraft.class_1681(a0, a1, CbDefaults.THROWN_DEFAULT_ITEM);
    }

    /** was net/minecraft/class_1682.<init>(Lnet/minecraft/class_1299;Lnet/minecraft/class_1309;Lnet/minecraft/class_1937;)V */
    public static net.minecraft.class_1682 cbNewclass_1682(net.minecraft.class_1299 a0, net.minecraft.class_1309 a1, net.minecraft.class_1937 a2) {
        throw new UnsupportedOperationException("CenturyBridge: surviving constructors cannot preserve the old living-entity owner association");
    }

    /** was net/minecraft/class_1701.<init>(Lnet/minecraft/class_1937;DDD)V */
    public static net.minecraft.class_1701 cbNewclass_1701(net.minecraft.class_1937 a0, double a1, double a2, double a3) {
        net.minecraft.class_1701 entity = new net.minecraft.class_1701(net.minecraft.class_1299.field_6053, a0);
        entity.method_23327(a1, a2, a3);
        return entity;
    }

    /** was net/minecraft/class_1702.method_35219()F */
    public static float method_35219(net.minecraft.class_1702 self) {
        throw new UnsupportedOperationException("CenturyBridge: exhaustion is private and the 1.21.4 API exposes no getter");
    }

    /** was net/minecraft/class_1923$1.field_18680Lnet/minecraft/class_1923; */
    public static net.minecraft.class_1923 field_18680() {
        return new net.minecraft.class_1923(0, 0);
    }

    /** was net/minecraft/class_1923$1.field_18683I */
    public static int field_18683() {
        throw new UnsupportedOperationException("CenturyBridge: field_18683 is instance state moved to inaccessible anonymous class class_1923$2 and cannot be reconstructed without a receiver");
    }

    /** was net/minecraft/class_3899.<init>(Ljava/lang/String;I)V */
    public static net.minecraft.class_3899 cbNewclass_3899(java.lang.String a0, int a1) {
        return new net.minecraft.class_3899(a0);
    }

    /** was net/minecraft/class_39.field_489Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_489() {
        throw new UnsupportedOperationException("CenturyBridge: deleted registry key field has no identifiable equivalent in the new API surface");
    }

    /** was net/minecraft/class_39.field_607Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_607() {
        throw new UnsupportedOperationException("CenturyBridge: field_607 was removed and the provided 1.21.4 API surface identifies no equivalent registry key");
    }

    /** was net/minecraft/class_5603.field_27703F */
    public static float field_27703() {
        throw new UnsupportedOperationException("CenturyBridge: old field_27703 was an instance pivotY value and cannot be reconstructed by a receiverless static helper");
    }

    /** was net/minecraft/class_5603.field_27704F */
    public static float field_27704() {
        throw new UnsupportedOperationException("CenturyBridge: old field_27704 was an instance-specific pivotZ value, but this static no-argument shim has no receiver");
    }

    /** was net/minecraft/class_5603.field_27705F */
    public static float field_27705() {
        throw new UnsupportedOperationException("CenturyBridge: field_27705 was an instance-specific pitch value, but this static shim has no ModelTransform receiver");
    }

    /** was net/minecraft/class_5603.field_27706F */
    public static float field_27706() {
        throw new UnsupportedOperationException("CenturyBridge: old field_27706 is instance-dependent, but the static field shim has no ModelTransform receiver");
    }

    /** was net/minecraft/class_5603.field_27707F */
    public static float field_27707() {
        throw new UnsupportedOperationException("CenturyBridge: old field_27707 was the per-instance roll value and cannot be recovered by a receiverless static helper");
    }

    /** was net/minecraft/class_582.field_3434I */
    public static int field_3434() {
        throw new UnsupportedOperationException("CenturyBridge: old field_3434 was per-instance mutable animation state and cannot be expressed by a receiverless static constant shim");
    }

    /** was net/minecraft/class_583.field_3447F */
    public static float field_3447() {
        throw new UnsupportedOperationException("CenturyBridge: old field_3447 was mutable per-instance model state, but this static field shim has no receiver and the new API stores that state in the render state");
    }

    /** was net/minecraft/class_583.field_3448Z */
    public static boolean field_3448() {
        return false;
    }

    /** was net/minecraft/class_5944.field_32778Ljava/lang/String; */
    public static java.lang.String field_32778() {
        return "shaders";
    }

    /** was net/minecraft/class_5949.field_29552Lnet/minecraft/class_5949; */
    public static net.minecraft.class_5949 field_29552() {
        return net.minecraft.class_5949.field_54068;
    }

    /** was net/minecraft/class_630$class_618.field_3603F */
    public static float field_3603() {
        throw new UnsupportedOperationException("CenturyBridge: old field_3603 was an instance-specific float value, but the static field shim has no receiver from which to read comp_3188()");
    }

    /** was net/minecraft/class_630$class_618.field_3604F */
    public static float field_3604() {
        throw new UnsupportedOperationException("CenturyBridge: field_3604 was an instance-specific vertex U coordinate and cannot be reconstructed without a receiver");
    }

    /** was net/minecraft/class_6350$class_6351.field_33576I */
    public static int field_33576() {
        throw new UnsupportedOperationException("CenturyBridge: old field_33576 was an instance-specific fluid level, but the static field shim has no receiver from which to call comp_2967()");
    }

    /** was net/minecraft/class_6350$class_6351.field_33577Lnet/minecraft/class_2680; */
    public static net.minecraft.class_2680 field_33577() {
        throw new UnsupportedOperationException("CenturyBridge: old field_33577 was per-instance fluid state and cannot be reconstructed by a receiverless static helper");
    }

    /** was net/minecraft/class_6670.<init>(Lnet/minecraft/class_1309;Ljava/util/List;)V */
    public static net.minecraft.class_6670 cbNewclass_6670(net.minecraft.class_1309 a0, java.util.List a1) {
        if (!(a0.method_37908() instanceof net.minecraft.class_3218)) {
            throw new UnsupportedOperationException("CenturyBridge: class_6670 requires the entity to be in a server world");
        }
        return new net.minecraft.class_6670((net.minecraft.class_3218) a0.method_37908(), a0, a1);
    }

    /** was net/minecraft/class_6798.field_35751Lnet/minecraft/class_6798; */
    public static net.minecraft.class_6798 field_35751() {
        throw new UnsupportedOperationException("CenturyBridge: the CARVING_MASK placement modifier type was removed and the new API has no behaviorally equivalent replacement");
    }

    /** was net/minecraft/class_7094.method_43687()J */
    public static long method_43687(net.minecraft.class_7094 self) {
        return self.method_61400(1.0F);
    }

    /** was net/minecraft/class_7165.method_41704(Lnet/minecraft/class_2338;Lnet/minecraft/class_2248;Lnet/minecraft/class_2338;)V */
    public static void method_41704(net.minecraft.class_7165 self, net.minecraft.class_2338 a0, net.minecraft.class_2248 a1, net.minecraft.class_2338 a2) {
        throw new UnsupportedOperationException("CenturyBridge: the old BlockPos argument cannot be converted to the required class_9904 placement context");
    }

    /** was net/minecraft/class_7237$class_7660.<init>(Lnet/minecraft/class_3300;Lnet/minecraft/class_7712;Lnet/minecraft/class_5455$class_6890;Lnet/minecraft/class_5455$class_6890;)V */
    public static net.minecraft.class_7237.class_7660 cbNewclass_7237class_7660(net.minecraft.class_3300 a0, net.minecraft.class_7712 a1, net.minecraft.class_5455.class_6890 a2, net.minecraft.class_5455.class_6890 a3) {
        throw new UnsupportedOperationException("CenturyBridge: cannot convert class_5455.class_6890 to the required class_7225.class_7874 registry lookup");
    }

    /** was net/minecraft/class_7264.<init>(Lnet/minecraft/class_1937;DDD)V */
    public static net.minecraft.class_7264 cbNewclass_7264(net.minecraft.class_1937 a0, double a1, double a2, double a3) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires an EntityType and item supplier, but the provided API surface exposes no faithful defaults for the old constructor");
    }

    /** was net/minecraft/class_7445.method_43713(Lnet/minecraft/class_2378;)Lnet/minecraft/class_7444; */
    public static net.minecraft.class_7444 method_43713(net.minecraft.class_7445 self, net.minecraft.class_2378 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new method_43713 accepts class_7891, returns void, and cannot preserve the old class_2378-to-class_7444 contract");
    }

    /** was net/minecraft/class_744.field_3906Z */
    public static boolean field_3906() {
        return false;
    }

    /** was net/minecraft/class_7701.field_40178Lnet/minecraft/class_7696; */
    public static net.minecraft.class_7696 field_40178() {
        throw new UnsupportedOperationException("CenturyBridge: deleted field_40178 has no identified semantic replacement among the remaining class_7696 constants");
    }

    /** was net/minecraft/class_7871$class_7872.method_58561(Lnet/minecraft/class_5321;Lnet/minecraft/class_5321;)Ljava/util/Optional; */
    public static java.util.Optional method_58561(net.minecraft.class_7871.class_7872 self, net.minecraft.class_5321 a0, net.minecraft.class_5321 a1) {
        return self.method_58561(a1);
    }

    /** was net/minecraft/class_8047.method_48371()Ljava/util/List; */
    public static java.util.List method_48371(net.minecraft.class_8047 self) {
        return self.method_48368();
    }

    /** was net/minecraft/class_806$class_5828.field_28797F */
    public static float field_28797() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted instance-specific threshold value cannot be reconstructed as a receiverless constant");
    }

    /** was net/minecraft/class_8791.<init>(Ljava/lang/String;ILnet/minecraft/class_1659;ZILnet/minecraft/class_1306;ZZ)V */
    public static net.minecraft.class_8791 cbNewclass_8791(java.lang.String a0, int a1, net.minecraft.class_1659 a2, boolean a3, int a4, net.minecraft.class_1306 a5, boolean a6, boolean a7) {
        return new net.minecraft.class_8791(a0, a1, a2, a3, a4, a5, a6, a7, net.minecraft.class_4066.field_18197);
    }

    /** was net/minecraft/class_9135$11.field_49682I */
    public static int field_49682() {
        return 5;
    }

    /** was net/minecraft/class_9135$11.field_51471Lnet/minecraft/class_9139; */
    public static net.minecraft.class_9139 field_51471() {
        throw new UnsupportedOperationException("CenturyBridge: the replacement codec is implemented by an anonymous inner class that cannot be named or instantiated from Java source, and no accessible equivalent is exposed");
    }

    /** was net/minecraft/class_9135$13.field_51472Lnet/minecraft/class_9139; */
    public static net.minecraft.class_9139 field_51472() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted field is now constructor-supplied instance state in field_49697 and cannot be reconstructed by a parameterless static helper");
    }

    /** was net/minecraft/class_9135$16.field_48575Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_48575() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted registry key constant cannot be reconstructed from the new codec API");
    }

    /** was net/minecraft/class_9135$17.field_49688Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_49688() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted registry-key constant has no equivalent in the new API");
    }

    /** was net/minecraft/class_9135$21.field_48563[I */
    public static int[] field_48563() {
        throw new UnsupportedOperationException("CenturyBridge: field_48563 moved to class_9135$24, but its value is not available from the provided new API surface");
    }

    /** was net/minecraft/class_9135$4.field_48988I */
    public static int field_48988() {
        return 2097152;
    }

    /** was net/minecraft/class_9135$6.field_49696Lnet/minecraft/class_9139; */
    public static net.minecraft.class_9139 field_49696() {
        throw new UnsupportedOperationException("CenturyBridge: field_49696 moved to class_9135$7, but the provided new API surface only describes class_9135$6 and exposes no class_9139 equivalent");
    }

    /** was net/minecraft/class_9139$10.field_48592Ljava/util/function/Function; */
    public static java.util.function.Function field_48592() {
        return java.util.function.Function.identity();
    }

    /** was net/minecraft/class_9139$10.field_48593Ljava/util/function/Function; */
    public static java.util.function.Function field_48593() {
        throw new UnsupportedOperationException("CenturyBridge: the replacement field in net.minecraft.class_9139$12 is not exposed by the provided new API surface");
    }

    /** was net/minecraft/class_9139$11.field_48596Ljava/util/function/Function; */
    public static java.util.function.Function field_48596() {
        throw new UnsupportedOperationException("CenturyBridge: the replacement field moved to class_9139$13, but its value or construction API is not provided");
    }

    /** was net/minecraft/class_9139$12.field_48600Ljava/util/function/BiFunction; */
    public static java.util.function.BiFunction field_48600() {
        throw new UnsupportedOperationException("CenturyBridge: replacement field moved to class_9139$14, but its API surface and field semantics are unavailable");
    }

    /** was net/minecraft/class_9139$13.field_48606Lcom/mojang/datafixers/util/Function3; */
    public static com.mojang.datafixers.util.Function3 field_48606() {
        throw new UnsupportedOperationException("CenturyBridge: replacement field moved to class_9139$15, but its value or API surface was not provided");
    }

    /** was net/minecraft/class_9139$13.field_48607Ljava/util/function/Function; */
    public static java.util.function.Function field_48607() {
        throw new UnsupportedOperationException("CenturyBridge: replacement field is on anonymous class net.minecraft.class_9139$15, which cannot be referenced from Java source");
    }

    /** was net/minecraft/class_9139$6.field_48584Lnet/minecraft/class_9141; */
    public static net.minecraft.class_9141 field_48584() {
        throw new UnsupportedOperationException("CenturyBridge: replacement field is owned by anonymous class net.minecraft.class_9139$8, but its NEW API surface and field identifier were not provided");
    }

    /** was net/minecraft/class_9139$6.field_48585Lnet/minecraft/class_9143; */
    public static net.minecraft.class_9143 field_48585() {
        throw new UnsupportedOperationException("CenturyBridge: field_48585 moved to inaccessible anonymous class net.minecraft.class_9139$8 and no equivalent constant expression is exposed");
    }

    /** was net/minecraft/class_9139$8.field_48589Lnet/minecraft/class_9139; */
    public static net.minecraft.class_9139 field_48589() {
        throw new UnsupportedOperationException("CenturyBridge: replacement field is owned by anonymous class net.minecraft.class_9139$10 and is not accessible from Java source");
    }

    /** was net/minecraft/class_9334.field_50076Lnet/minecraft/class_9331; */
    public static net.minecraft.class_9331 field_50076() {
        throw new UnsupportedOperationException("CenturyBridge: the removed fire-resistant unit component has no type-compatible equivalent in the new API");
    }

    /** was net/minecraft/class_998.field_32946Ljava/lang/String; */
    public static java.lang.String field_32946() {
        return "box";
    }

    /** was net/minecraft/class_1309.method_26825(Lnet/minecraft/class_1320;)D */
    public static double method_26825(net.minecraft.class_1309 self, net.minecraft.class_1320 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API cannot query an attribute value from a raw class_1320 instance");
    }

    /** was net/minecraft/class_1309.method_6051()Lnet/minecraft/class_5819; */
    public static net.minecraft.class_5819 method_6051(net.minecraft.class_1309 self) {
        throw new UnsupportedOperationException("CenturyBridge: per-entity random generator is no longer exposed by the new API");
    }

    /** was net/minecraft/class_1702.method_35218(F)V */
    public static void method_35218(net.minecraft.class_1702 self, float a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API exposes no exhaustion setter or getter, so the old absolute exhaustion assignment cannot be preserved");
    }

    /** was net/minecraft/class_1826.method_8016(I)I */
    public static int method_8016(net.minecraft.class_1826 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API no longer exposes the spawn egg tint colors required by method_8016");
    }

    /** was net/minecraft/class_2248.method_9574(Lnet/minecraft/class_4538;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;)Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 method_9574(net.minecraft.class_2248 self, net.minecraft.class_4538 a0, net.minecraft.class_2338 a1, net.minecraft.class_2680 a2) {
        return new net.minecraft.class_1799(self);
    }

    /** was net/minecraft/class_2321.field_10932Lcom/mojang/brigadier/suggestion/SuggestionProvider; */
    public static com.mojang.brigadier.suggestion.SuggestionProvider field_10932() {
        throw new UnsupportedOperationException("CenturyBridge: the ALL_RECIPES suggestion provider was removed and the new API exposes no equivalent");
    }

    /** was net/minecraft/class_259.method_16344(Lnet/minecraft/class_265;Lnet/minecraft/class_2350;)Lnet/minecraft/class_265; */
    public static net.minecraft.class_265 method_16344(net.minecraft.class_259 self, net.minecraft.class_265 a0, net.minecraft.class_2350 a1) {
        return a0.method_20538(a1);
    }

    /** was net/minecraft/class_315$4.field_37883[I */
    public static int[] field_37883() {
        throw new UnsupportedOperationException("CenturyBridge: the int[] contents of moved field net.minecraft.class_315$5.field_37883 are not present in the provided new API surface");
    }

    /** was net/minecraft/class_3414.method_47958(Lnet/minecraft/class_2540;)V */
    public static void method_47958(net.minecraft.class_3414 self, net.minecraft.class_2540 a0) {
        throw new UnsupportedOperationException("CenturyBridge: legacy buffer serialization was replaced by packet codecs, but no callable codec encode operation is available in the supplied API surface");
    }

    /** was net/minecraft/class_3492.method_35476(Z)Lnet/minecraft/class_3492; */
    public static net.minecraft.class_3492 method_35476(net.minecraft.class_3492 self, boolean a0) {
        return self.method_61020(a0 ? net.minecraft.class_9822.field_52238 : net.minecraft.class_9822.field_52237);
    }

    /** was net/minecraft/class_3816.method_28885(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Lnet/minecraft/class_3816; */
    public static net.minecraft.class_3816 method_28885(net.minecraft.class_3816 self, java.lang.String a0, com.mojang.serialization.Codec a1) {
        throw new UnsupportedOperationException("CenturyBridge: arbitrary Codec cannot be converted to MapCodec while preserving its serialized shape");
    }

    /** was net/minecraft/class_3828.method_16822(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Lnet/minecraft/class_3828; */
    public static net.minecraft.class_3828 method_16822(net.minecraft.class_3828 self, java.lang.String a0, com.mojang.serialization.Codec a1) {
        return net.minecraft.class_3828.method_16822(a0, com.mojang.serialization.MapCodec.assumeMapUnsafe(a1));
    }

    /** was net/minecraft/class_39.field_285Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_285() {
        throw new UnsupportedOperationException("CenturyBridge: the deleted registry key has no surviving equivalent or recoverable identifier in the new API");
    }

    /** was net/minecraft/class_5146.method_6576(Lnet/minecraft/class_3419;)V */
    public static void method_6576(net.minecraft.class_5146 self, net.minecraft.class_3419 a0) {
        self.method_6576(new net.minecraft.class_1799(net.minecraft.class_1802.field_8118), a0);
    }

    /** was net/minecraft/class_5780.<init>(Lnet/minecraft/class_5778;IZZZFLnet/minecraft/class_6885;)V */
    public static net.minecraft.class_5780 cbNewclass_5780(net.minecraft.class_5778 a0, int a1, boolean a2, boolean a3, boolean a4, float a5, net.minecraft.class_6885 a6) {
        throw new UnsupportedOperationException("CenturyBridge: class_5778 cannot be converted to class_10376 using the surviving API surface");
    }

    /** was net/minecraft/class_6811.field_35975Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_35975() {
        throw new UnsupportedOperationException("CenturyBridge: SEAGRASS_SIMPLE placed feature was removed with no faithful replacement");
    }

    /** was net/minecraft/class_8173.field_42787Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_42787() {
        return net.minecraft.class_8173.field_51897;
    }

    /** was net/minecraft/class_8934.method_54867(Lnet/minecraft/class_2960;J)V */
    public static void method_54867(net.minecraft.class_8934 self, net.minecraft.class_2960 a0, long a1) {
        throw new UnsupportedOperationException("CenturyBridge: cannot construct the required loot-table registry key from an identifier because the canonical registry key is not exposed by the provided new API surface");
    }

    /** was net/minecraft/class_8952.method_55027(Lnet/minecraft/class_8949;Lnet/minecraft/class_243;)Z */
    public static boolean method_55027(net.minecraft.class_8952 self, net.minecraft.class_8949 a0, net.minecraft.class_243 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_8952 API has no boolean operation accepting class_8949 and class_243, and converting the vector for method_55028 would require unavailable semantics");
    }

    /** was net/minecraft/class_9135$5.field_48565Ljava/util/function/Supplier; */
    public static java.util.function.Supplier field_48565() {
        throw new UnsupportedOperationException("CenturyBridge: the replacement field moved to class_9135$6, but its API surface was not provided");
    }

    /** was net/minecraft/class_9139$12.field_48599Lnet/minecraft/class_9139; */
    public static net.minecraft.class_9139 field_48599() {
        throw new UnsupportedOperationException("CenturyBridge: replacement field is on inaccessible anonymous class net.minecraft.class_9139$14 and the provided API exposes no equivalent constant");
    }

    /** was net/minecraft/class_9139$12.field_48601Ljava/util/function/Function; */
    public static java.util.function.Function field_48601() {
        throw new UnsupportedOperationException("CenturyBridge: replacement field is hosted on anonymous class net.minecraft.class_9139$14 and is not expressible as a Java source reference");
    }

    /** was net/minecraft/class_1093.field_21864Z */
    public static boolean field_21864() {
        return false;
    }

    /** was net/minecraft/class_1093.field_5413Z */
    public static boolean field_5413() {
        return false;
    }

    /** was net/minecraft/class_1093.field_5414Ljava/util/Map; */
    public static java.util.Map field_5414() {
        throw new UnsupportedOperationException("CenturyBridge: field_5414 is per-instance face-quad state and cannot be reconstructed by a receiverless static field shim");
    }

    /** was net/minecraft/class_1248.method_5292(Lcom/mojang/datafixers/schemas/Schema;Ljava/util/Map;Ljava/lang/String;)V */
    public static void method_5292(net.minecraft.class_1248 self, com.mojang.datafixers.schemas.Schema a0, java.util.Map a1, java.lang.String a2) {
        throw new UnsupportedOperationException("CenturyBridge: the removed instance helper mutated a caller-supplied entity map, while the new API only exposes registerEntities(Schema) returning its own map");
    }

    /** was net/minecraft/class_1308.method_5933()Lnet/minecraft/class_1297; */
    public static net.minecraft.class_1297 method_5933(net.minecraft.class_1308 self) {
        return self.method_60952();
    }

    /** was net/minecraft/class_1481.field_28406Lnet/minecraft/class_1856; */
    public static net.minecraft.class_1856 field_28406() {
        return net.minecraft.class_1856.method_8101(net.minecraft.class_1802.field_8217);
    }

    /** was net/minecraft/class_2615.method_11262()Lnet/minecraft/class_265; */
    public static net.minecraft.class_265 method_11262(net.minecraft.class_2615 self) {
        return net.minecraft.class_259.method_1078(self.method_11262());
    }

    /** was net/minecraft/class_3568.method_51559(Lnet/minecraft/class_4076;)Z */
    public static boolean method_51559(net.minecraft.class_3568 self, net.minecraft.class_4076 a0) {
        return self.method_62874(a0.method_18694());
    }

    /** was net/minecraft/class_7450$class_7599.comp_913Ljava/lang/String; */
    public static java.lang.String comp_913() {
        throw new UnsupportedOperationException("CenturyBridge: old instance field value requires a receiver, but the static field shim has no receiver parameter");
    }

    /** was net/minecraft/class_7677.method_45251(Ljava/lang/Object;)Lcom/google/gson/JsonObject; */
    public static com.google.gson.JsonObject method_45251(net.minecraft.class_7677 self, java.lang.Object a0) {
        return ((com.google.gson.JsonElement) self.comp_3437().encodeStart(com.mojang.serialization.JsonOps.INSTANCE, a0).getOrThrow()).getAsJsonObject();
    }

    /** was net/minecraft/class_793.field_4247Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_4247() {
        throw new UnsupportedOperationException("CenturyBridge: old static field contract cannot be reconstructed because field_4247 is an instance-specific parent identifier in the new API");
    }

    /** was net/minecraft/class_9139$10.field_48594Lnet/minecraft/class_9139; */
    public static net.minecraft.class_9139 field_48594() {
        throw new UnsupportedOperationException("CenturyBridge: the moved field is an instance-captured PacketCodec and cannot be reconstructed without its enclosing codec instance");
    }

    /** was net/minecraft/class_9139$12.field_48602Ljava/util/function/Function; */
    public static java.util.function.Function field_48602() {
        throw new UnsupportedOperationException("CenturyBridge: replacement field moved to inaccessible anonymous class net.minecraft.class_9139$14 and no equivalent function contract is exposed");
    }

    /** was net/minecraft/class_9139$13.field_48604Lnet/minecraft/class_9139; */
    public static net.minecraft.class_9139 field_48604() {
        throw new UnsupportedOperationException("CenturyBridge: field_48604 moved to inaccessible anonymous inner class net.minecraft.class_9139$15 and no equivalent is exposed by the provided new API");
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
            cbOps = net.minecraft.class_6903.method_46632(JsonOps.INSTANCE,
                net.minecraft.class_5455.method_40302(net.minecraft.class_7923.field_41167));
        }
        return cbOps;
    }

    /**
     * 1.20.1 ItemStack.hasCustomName(): the component wall made the name a
     * CUSTOM_NAME data component, so "has a name" is "carries the component".
     * (First attempt guessed setCustomName from the method NAME -- the 1.20.1
     * stub says ()Z. Semantics come from descriptors, not from naming vibes.)
     */
    public static boolean method_7938(net.minecraft.class_1799 self) {
        return self.method_57826(net.minecraft.class_9334.field_49631);
    }

    /**
     * Builtin registry lookup for component-wall call sites that suddenly need
     * one (BlockEntity NBT family). The world's own manager would be the exact
     * choice; the builtin wrapper resolves everything vanilla serialization
     * reaches for in these paths.
     */
    private static net.minecraft.class_7225.class_7874 cbLookupCache;

    public static net.minecraft.class_7225.class_7874 cbLookup() {
        // cached like cbOps above: this backs every rewritten writeNbt/readNbt
        // call site, and the wrapper is over the builtin (static) registries,
        // which never reload -- rebuilding the whole wrapper graph per chunk
        // save was pure waste
        if (cbLookupCache == null) {
            cbLookupCache = net.minecraft.class_5455.method_40302(net.minecraft.class_7923.field_41167);
        }
        return cbLookupCache;
    }

    private Statics() {
    }

    // ---- Identifier constructors: gone since 1.21 in favour of static factories ----
    /** ctorRedirects: 1.20.1 new Identifier("ns:path") */
    public static net.minecraft.class_2960 cbNewIdentifier(String id) {
        return net.minecraft.class_2960.method_60654(id);
    }

    /** ctorRedirects: 1.20.1 new Identifier(namespace, path) */
    public static net.minecraft.class_2960 cbNewIdentifier(String namespace, String path) {
        return net.minecraft.class_2960.method_60655(namespace, path);
    }

    /** staticRedirects: 1.20.1 Pack.readMetaAndCreate -- id/title/required/pos/source
     *  folded into PackLocationInfo + PackSelectionConfig at 1.20.5. */
    public static net.minecraft.class_3288 cbReadMetaAndCreate(String id, net.minecraft.class_2561 title,
            boolean required, net.minecraft.class_3288.class_7680 resources, net.minecraft.class_3264 type,
            net.minecraft.class_3288.class_3289 position, net.minecraft.class_5352 source) {
        return net.minecraft.class_3288.method_45275(
            new net.minecraft.class_9224(id, title, source, java.util.Optional.empty()),
            cbWrapSupplier(resources), type, new net.minecraft.class_9225(required, position, false));
    }

    /**
     * A 1.20.1 ResourcesSupplier lambda implements the old SAM open(String),
     * not the PackLocationInfo pair the runtime calls: AbstractMethodError at
     * Pack.readPackMetadata. Detect that shape and adapt it. The interface
     * cannot be bridged by mixin (redeclaring its abstract pair would count as
     * an overwrite), so the wrap happens where the legacy call enters.
     */
    private static net.minecraft.class_3288.class_7680 cbWrapSupplier(net.minecraft.class_3288.class_7680 res) {
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
        return new net.minecraft.class_3288.class_7680() {
            @Override
            public net.minecraft.class_3262 method_52424(net.minecraft.class_9224 info) {
                try {
                    return (net.minecraft.class_3262) open.invoke(res, info.comp_2329());
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("centurybridge: legacy ResourcesSupplier failed", e);
                }
            }

            @Override
            public net.minecraft.class_3262 method_52425(net.minecraft.class_9224 info,
                    net.minecraft.class_3288.class_7679 meta) {
                return method_52424(info);
            }
        };
    }

    /** ctorRedirects: 1.20.1 new PathPackResources(name, path, alwaysStable) */
    public static net.minecraft.class_3259 cbNewPathPackResources(String name, java.nio.file.Path path,
            boolean alwaysStable) {
        return new net.minecraft.class_3259(cbPackLocation(name), path);
    }

    /** ctorRedirects: 1.20.1 new ZipResourcePack(name, file, alwaysStable) */
    public static net.minecraft.class_3258 cbNewFilePackResources(String name, java.io.File file,
            boolean alwaysStable) {
        return (net.minecraft.class_3258) new net.minecraft.class_3258.class_8615(file)
            .method_52424(cbPackLocation(name));
    }

    private static net.minecraft.class_9224 cbPackLocation(String name) {
        return new net.minecraft.class_9224(name, net.minecraft.class_2561.method_43470(name),
            net.minecraft.class_5352.field_25347, java.util.Optional.empty());
    }

    // Entity.setOnFireFor int-seconds -> float-seconds (1.21 widened the parameter).
    /** was net/minecraft/class_1297.method_5639(I)V */
    public static void method_5639(net.minecraft.class_1297 self, int i) { self.method_5639((float) i); }
    /** was net/minecraft/class_1309.method_5639(I)V */
    public static void method_5639(net.minecraft.class_1309 self, int i) { self.method_5639((float) i); }
    /** was net/minecraft/class_1542.method_5639(I)V */
    public static void method_5639(net.minecraft.class_1542 self, int i) { self.method_5639((float) i); }
    /** was net/minecraft/class_1588.method_5639(I)V */
    public static void method_5639(net.minecraft.class_1588 self, int i) { self.method_5639((float) i); }
    /** was net/minecraft/class_1657.method_5639(I)V */
    public static void method_5639(net.minecraft.class_1657 self, int i) { self.method_5639((float) i); }
    /** was net/minecraft/class_1676.method_5639(I)V */
    public static void method_5639(net.minecraft.class_1676 self, int i) { self.method_5639((float) i); }

    /** was net/minecraft/class_5251.method_27719(Ljava/lang/String;)Lnet/minecraft/class_5251; -- TextColor.parseColor
     *  returns DataResult since 1.20.3; the 1.20.1 contract was "TextColor or null". */
    public static net.minecraft.class_5251 method_27719(String s) {
        return net.minecraft.class_5251.method_27719(s).result().orElse(null);
    }

    /** was net/minecraft/class_1299$class_1300.method_5905(Ljava/lang/String;)Lnet/minecraft/class_1299; -- EntityType.Builder.build(String)
     *  takes a RegistryKey since 1.21.2; the 1.20.1 string was the entity's registry id. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_1299 method_5905(net.minecraft.class_1299.class_1300 self, String id) {
        return self.method_5905(net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41266, net.minecraft.class_2960.method_60654(id)));
    }

    /** was net/minecraft/class_1317.method_20637(EntityType, SpawnRestriction.Location, Heightmap.Type, SpawnPredicate)V
     *  -- SpawnRestriction.register; the Location enum became the SpawnLocation interface in 1.20.5 (facade class_1317_class_1319). */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void method_20637(net.minecraft.class_1299 type, top.dext.centurybridge.facade.v1_21_4.class_1317_class_1319 location,
            net.minecraft.class_2902.class_2903 heightmap, net.minecraft.class_1317.class_4306 predicate) {
        // private since 1.20.5: through the invoker mixin
        top.dext.centurybridge.shims.v1_21_4.Class1317Accessor.cbRegister(type, location.cbVanilla(), heightmap, predicate);
    }

    /** was net/minecraft/class_1686.<init>(Lnet/minecraft/class_1937;DDD)V -- 1.21.2 ctors take the stack; the sentinel becomes getDefaultItem() (ThrownItemEntityBridge) */
    public static net.minecraft.class_1686 cbNewclass_1686(net.minecraft.class_1937 a0, double a1, double a2, double a3) {
        return new net.minecraft.class_1686(a0, a1, a2, a3, CbDefaults.THROWN_DEFAULT_ITEM);
    }

    /** ThrownItemEntity super(...) reshapes push this: the 1.20.1 no-stack ctors meant "the default item" */
    public static net.minecraft.class_1799 cbThrownDefaultItem() {
        return CbDefaults.THROWN_DEFAULT_ITEM;
    }

    /** was net/minecraft/class_2591.<init>(Lnet/minecraft/class_2591$class_5559;Ljava/util/Set;Lcom/mojang/datafixers/types/Type;)V -- the DFU type argument is gone (fapi's AW opens the private ctor at runtime) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_2591 cbNewclass_2591(net.minecraft.class_2591.class_5559 factory, java.util.Set<net.minecraft.class_2248> blocks, com.mojang.datafixers.types.Type<?> type) {
        return new net.minecraft.class_2591(factory, blocks);
    }

    // method_7843 (Item.onCraft, player param dropped @1.20.2): the converter emits the site's own owner
    public static void method_7843(net.minecraft.class_1747 self, net.minecraft.class_1799 stack, net.minecraft.class_1937 world, net.minecraft.class_1657 player) {
        method_7843((net.minecraft.class_1792) self, stack, world, player);
    }

    public static void method_7843(net.minecraft.class_1811 self, net.minecraft.class_1799 stack, net.minecraft.class_1937 world, net.minecraft.class_1657 player) {
        method_7843((net.minecraft.class_1792) self, stack, world, player);
    }

    public static void method_7843(net.minecraft.class_1826 self, net.minecraft.class_1799 stack, net.minecraft.class_1937 world, net.minecraft.class_1657 player) {
        method_7843((net.minecraft.class_1792) self, stack, world, player);
    }

    public static void method_7843(net.minecraft.class_1738 self, net.minecraft.class_1799 stack, net.minecraft.class_1937 world, net.minecraft.class_1657 player) {
        method_7843((net.minecraft.class_1792) self, stack, world, player);
    }

    /** was net/minecraft/class_5328.method_29282(World, PlayerEntity, Hand)TypedActionResult -- ItemUsage.consumeHeldItem now answers a bare ActionResult */
    public static top.dext.centurybridge.facade.v1_21_4.class_1271<net.minecraft.class_1799> method_29282(net.minecraft.class_1937 world, net.minecraft.class_1657 player, net.minecraft.class_1268 hand) {
        return top.dext.centurybridge.facade.v1_21_4.class_1271.cbFrom(net.minecraft.class_5328.method_29282(world, player, hand), player.method_5998(hand));
    }

    // ---- dead-cover replacements (the wiring claimed a mixin restored these shapes; none ever existed) ----

    /** registries for a block entity: its world's, or the vanilla lookup before it is placed */
    public static net.minecraft.class_7225.class_7874 cbRegistries(net.minecraft.class_2586 be) {
        net.minecraft.class_1937 w = be.method_10997();
        return w != null ? w.method_30349() : net.minecraft.class_7887.method_46817();
    }

    /** was Entity.damage(DamageSource, float): 1.21.2 split it into hurtServer(ServerWorld, ..); client-side damage was always a no-op */
    public static boolean method_5643(net.minecraft.class_1297 self, net.minecraft.class_1282 source, float amount) {
        return self.method_37908() instanceof net.minecraft.class_3218 sw && self.method_64397(sw, source, amount);
    }

    /** was NbtHelper.fromBlockPos(BlockPos)NbtCompound: the 1.20.1 {X,Y,Z} compound (1.21.2 writes an int array) */
    public static net.minecraft.class_2487 method_10692(net.minecraft.class_2338 pos) {
        net.minecraft.class_2487 c = new net.minecraft.class_2487();
        c.method_10569("X", pos.method_10263());
        c.method_10569("Y", pos.method_10264());
        c.method_10569("Z", pos.method_10260());
        return c;
    }

    // StatusEffect / EntityAttribute became Holders (1.20.5 / 1.21)
    public static boolean method_6059(net.minecraft.class_1309 self, net.minecraft.class_1291 effect) {
        return self.method_6059(net.minecraft.class_7923.field_41174.method_47983(effect));
    }

    public static net.minecraft.class_1293 method_6112(net.minecraft.class_1309 self, net.minecraft.class_1291 effect) {
        return self.method_6112(net.minecraft.class_7923.field_41174.method_47983(effect));
    }

    public static boolean method_6016(net.minecraft.class_1309 self, net.minecraft.class_1291 effect) {
        return self.method_6016(net.minecraft.class_7923.field_41174.method_47983(effect));
    }

    public static net.minecraft.class_1324 method_5996(net.minecraft.class_1309 self, net.minecraft.class_1320 attribute) {
        return self.method_5996(net.minecraft.class_7923.field_41190.method_47983(attribute));
    }

    public static double method_26852(net.minecraft.class_5131 self, net.minecraft.class_1320 attribute) {
        return self.method_26852(net.minecraft.class_7923.field_41190.method_47983(attribute));
    }

    public static boolean method_27310(net.minecraft.class_5132 self, net.minecraft.class_1320 attribute) {
        return self.method_27310(net.minecraft.class_7923.field_41190.method_47983(attribute));
    }

    /** was LivingEntity.sendEquipmentBreakStatus(EquipmentSlot): 1.21.2 also wants the item */
    public static void method_20235(net.minecraft.class_1309 self, net.minecraft.class_1304 slot) {
        self.method_20235(self.method_6118(slot).method_7909(), slot);
    }

    // BlockEntity NBT views gained the registry lookup (1.20.5)
    public static net.minecraft.class_2487 method_38242(net.minecraft.class_2586 self) {
        return self.method_38242(cbRegistries(self));
    }

    public static net.minecraft.class_2487 method_38243(net.minecraft.class_2586 self) {
        return self.method_38243(cbRegistries(self));
    }

    public static net.minecraft.class_2586 method_11005(net.minecraft.class_2338 pos, net.minecraft.class_2680 state, net.minecraft.class_2487 nbt) {
        return net.minecraft.class_2586.method_11005(pos, state, nbt, net.minecraft.class_7887.method_46817());
    }

    // ItemCooldownManager keys by stack (cooldown group) since 1.21.2
    public static boolean method_7904(net.minecraft.class_1796 self, net.minecraft.class_1792 item) {
        return self.method_7904(new net.minecraft.class_1799(item));
    }

    public static float method_7905(net.minecraft.class_1796 self, net.minecraft.class_1792 item, float tickDelta) {
        return self.method_7905(new net.minecraft.class_1799(item), tickDelta);
    }

    /** was EntityAttributeModifier.getName(): modifiers are keyed by Identifier since 1.21 */
    public static String method_6185(net.minecraft.class_1322 self) {
        return self.comp_2447().toString();
    }

    /** was Block.onDestroyedByExplosion(World, ..): ServerWorld since 1.21.2 (client explosions never reached it) */
    public static void method_9586(net.minecraft.class_2248 self, net.minecraft.class_1937 world, net.minecraft.class_2338 pos, net.minecraft.class_1927 explosion) {
        if (world instanceof net.minecraft.class_3218 sw) {
            self.method_9586(sw, pos, explosion);
        }
    }

    /** was Entity.canModifyAt(World, BlockPos): ServerWorld since 1.21.2; the client never enforces it */
    public static boolean method_36971(net.minecraft.class_1297 self, net.minecraft.class_1937 world, net.minecraft.class_2338 pos) {
        return !(world instanceof net.minecraft.class_3218 sw) || self.method_36971(sw, pos);
    }

    /** was Entity.dropStack(ItemStack): ServerWorld-explicit since 1.21.2 (client side dropped nothing and answered null) */
    public static net.minecraft.class_1542 method_5775(net.minecraft.class_1297 self, net.minecraft.class_1799 stack) {
        return self.method_37908() instanceof net.minecraft.class_3218 sw ? self.method_5775(sw, stack) : null;
    }

    public static net.minecraft.class_1542 method_5706(net.minecraft.class_1297 self, net.minecraft.class_1935 item) {
        return self.method_37908() instanceof net.minecraft.class_3218 sw ? self.method_5706(sw, item) : null;
    }

    // PersistentStateManager: (reader, factory, id) folded into PersistentState.Type in 1.20.5, reader gained the registry lookup
    public static <T extends net.minecraft.class_18> T method_17924(net.minecraft.class_26 self, java.util.function.Function<net.minecraft.class_2487, T> reader,
            java.util.function.Supplier<T> factory, String id) {
        return self.method_17924(new net.minecraft.class_18.class_8645<>(factory, (nbt, lookup) -> reader.apply(nbt), null), id);
    }

    public static <T extends net.minecraft.class_18> T method_20786(net.minecraft.class_26 self, java.util.function.Function<net.minecraft.class_2487, T> reader, String id) {
        return self.method_20786(new net.minecraft.class_18.class_8645<>(null, (nbt, lookup) -> reader.apply(nbt), null), id);
    }

    /** was SimpleInventory.toNbtList(): the registry lookup is the vanilla one (the inventory has no world) */
    public static net.minecraft.class_2499 method_7660(net.minecraft.class_1277 self) {
        return self.method_7660(net.minecraft.class_7887.method_46817());
    }

    /** was ItemUsage.spawnItemContents(ItemEntity, Stream) -> Iterable since 1.21 */
    public static void method_33263(net.minecraft.class_1542 entity, java.util.stream.Stream<net.minecraft.class_1799> contents) {
        net.minecraft.class_5328.method_33263(entity, contents.toList());
    }

    /** was CampfireBlockEntity.addItem(Entity, ItemStack, int): 1.21.2 takes (ServerWorld, LivingEntity, ItemStack), cook time comes from the recipe */
    public static boolean method_17503(net.minecraft.class_3924 self, net.minecraft.class_1297 user, net.minecraft.class_1799 stack, int cookTime) {
        return self.method_10997() instanceof net.minecraft.class_3218 sw && user instanceof net.minecraft.class_1309 living && self.method_17503(sw, living, stack);
    }

    /** was AbstractFurnaceBlockEntity.tick(World, ..): ServerWorld since 1.21.2 (the client never ticked it) */
    public static void method_31651(net.minecraft.class_1937 world, net.minecraft.class_2338 pos, net.minecraft.class_2680 state, net.minecraft.class_2609 be) {
        if (world instanceof net.minecraft.class_3218 sw) {
            net.minecraft.class_2609.method_31651(sw, pos, state, be);
        }
    }

    /** was StructureAccessor.getStructureContaining(BlockPos, TagKey): a predicate over structure holders since 1.21.2 */
    public static net.minecraft.class_3449 method_41413(net.minecraft.class_5138 self, net.minecraft.class_2338 pos, net.minecraft.class_6862<net.minecraft.class_3195> tag) {
        return self.method_41413(pos, holder -> holder.method_40220(tag));
    }

    /** was MobEntity.initialize(.., NbtCompound): the NBT argument left in 1.20.5. Call sites only -- a 1.20.1 override stays an orphan (vanilla calls the 4-arg shape) */
    public static net.minecraft.class_1315 method_5943(net.minecraft.class_1308 self, net.minecraft.class_5425 world, net.minecraft.class_1266 difficulty,
            net.minecraft.class_3730 reason, net.minecraft.class_1315 data, net.minecraft.class_2487 nbt) {
        return self.method_5943(world, difficulty, reason, data);
    }

    /** was net/minecraft/class_1254.method_5359(Lcom/mojang/serialization/Dynamic;Ljava/util/Map;Ljava/lang/String;)Ljava/lang/Object; */
    public static java.lang.Object method_5359(com.mojang.serialization.Dynamic a0, java.util.Map a1, java.lang.String a2) {
        throw new UnsupportedOperationException("CenturyBridge: the old String argument cannot be faithfully converted to the replacement Map contract");
    }

    /** was net/minecraft/class_1280.method_5496(FLnet/minecraft/class_1282;FF)F */
    public static float method_5496(float a0, net.minecraft.class_1282 a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: the new overload requires a class_1309 instance that the old contract does not provide");
    }

    /** was net/minecraft/class_14.method_23476(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338$class_2339;)Lnet/minecraft/class_7; */
    public static net.minecraft.class_7 method_23476(net.minecraft.class_1922 a0, net.minecraft.class_2338.class_2339 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the new method requires a class_9316 path context containing a class_1941 world and class_1308 entity, which cannot be recovered from the old class_1922 argument");
    }

    /** was net/minecraft/class_1606.method_33346(FLnet/minecraft/class_2350;F)Lnet/minecraft/class_238;
     *  ShulkerEntity.calculateBoundingBox(scale, direction, extraLength): 1.21.2 added the position the
     *  box is offset to; the old box is relative, i.e. at the origin */
    public static net.minecraft.class_238 method_33346(float a0, net.minecraft.class_2350 a1, float a2) {
        return net.minecraft.class_1606.method_33346(a0, a1, a2, net.minecraft.class_243.field_1353);
    }

    /** was net/minecraft/class_1606.method_33347(FLnet/minecraft/class_2350;FF)Lnet/minecraft/class_238; */
    public static net.minecraft.class_238 method_33347(float a0, net.minecraft.class_2350 a1, float a2, float a3) {
        return net.minecraft.class_1606.method_33347(a0, a1, a2, a3, new net.minecraft.class_243(0.0D, 0.0D, 0.0D));
    }

    /** was net/minecraft/class_1675.method_18813(Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;F)Lnet/minecraft/class_1665; */
    public static net.minecraft.class_1665 method_18813(net.minecraft.class_1309 a0, net.minecraft.class_1799 a1, float a2) {
        return net.minecraft.class_1675.method_18813(a0, a1, a2, net.minecraft.class_1799.field_8037);
    }

    /** was net/minecraft/class_1704.method_53173(Lnet/minecraft/class_1291;)I */
    public static int method_53173(net.minecraft.class_1291 a0) {
        return net.minecraft.class_1704.method_53173(net.minecraft.class_6880.method_40223(a0));
    }

    /** was net/minecraft/class_1714.method_17399(Lnet/minecraft/class_1703;Lnet/minecraft/class_1937;Lnet/minecraft/class_1657;Lnet/minecraft/class_8566;Lnet/minecraft/class_1731;)V */
    public static void method_17399(net.minecraft.class_1703 a0, net.minecraft.class_1937 a1, net.minecraft.class_1657 a2, net.minecraft.class_8566 a3, net.minecraft.class_1731 a4) {
        throw new UnsupportedOperationException("CenturyBridge: the new method requires a ServerWorld and the resolved RecipeEntry, which the old signature does not provide");
    }

    /** was net/minecraft/class_1764.method_7775(Lnet/minecraft/class_1799;)I */
    public static int method_7775(net.minecraft.class_1799 a0) {
        return net.minecraft.class_1764.method_7775(a0, null);
    }

    /** was net/minecraft/class_1764.method_7777(Lnet/minecraft/class_1937;Lnet/minecraft/class_1309;Lnet/minecraft/class_1268;Lnet/minecraft/class_1799;FF)V
     *  CrossbowItem.shootAll(world, shooter, hand, crossbow, speed, divergence): an instance method of the
     *  crossbow (with an optional target) since 1.20.5 -- the stack's own crossbow item shoots, as the old
     *  static did for the stack it was given */
    public static void method_7777(net.minecraft.class_1937 a0, net.minecraft.class_1309 a1, net.minecraft.class_1268 a2, net.minecraft.class_1799 a3, float a4, float a5) {
        net.minecraft.class_1764 crossbow = a3.method_7909() instanceof net.minecraft.class_1764 c
            ? c : (net.minecraft.class_1764) net.minecraft.class_1802.field_8399;
        crossbow.method_7777(a0, a1, a2, a3, a4, a5, null);
    }

    /** was net/minecraft/class_1802.method_51348(Lnet/minecraft/class_5321;Lnet/minecraft/class_1792;)Lnet/minecraft/class_1792; */
    public static net.minecraft.class_1792 method_51348(net.minecraft.class_5321 a0, net.minecraft.class_1792 a1) {
        return net.minecraft.class_1802.method_51348(a0, settings -> a1, new net.minecraft.class_1792.class_1793());
    }

    /** was net/minecraft/class_1806.method_7997(Ljava/lang/Integer;Lnet/minecraft/class_1937;)Lnet/minecraft/class_22; */
    public static net.minecraft.class_22 method_7997(java.lang.Integer a0, net.minecraft.class_1937 a1) {
        return net.minecraft.class_1806.method_7997(a0 == null ? null : new net.minecraft.class_9209(a0), a1);
    }

    /** was net/minecraft/class_1890.method_8201(Ljava/util/Collection;Lnet/minecraft/class_1887;)Z */
    public static boolean method_8201(java.util.Collection a0, net.minecraft.class_1887 a1) {
        throw new UnsupportedOperationException("CenturyBridge: the old enchantment instance cannot be faithfully converted to the required registry entry without registry access");
    }

    /** was net/minecraft/class_1932.method_32350(Lnet/minecraft/class_2487;)Lnet/minecraft/class_1932; */
    public static net.minecraft.class_1932 method_32350(net.minecraft.class_2487 a0) {
        return net.minecraft.class_1932.method_32350(a0, null);
    }

    /** was net/minecraft/class_2178.method_9281()Lnet/minecraft/class_2178; */
    public static net.minecraft.class_2178 method_9281() {
        throw new UnsupportedOperationException("CenturyBridge: TextArgumentType.text() requires CommandRegistryAccess");
    }

    /** was net/minecraft/class_2293.method_9804(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)Ljava/util/function/Predicate; */
    public static java.util.function.Predicate method_9804(com.mojang.brigadier.context.CommandContext a0, java.lang.String a1) {
        return net.minecraft.class_2293.method_9804(a0, a1);
    }

    /** was net/minecraft/class_2507.method_10630(Lnet/minecraft/class_2487;Ljava/io/File;)V */
    public static void method_10630(net.minecraft.class_2487 a0, java.io.File a1) throws Exception {
        net.minecraft.class_2507.method_10630(a0, a1.toPath());
    }

    /** was net/minecraft/class_2507.method_10633(Ljava/io/File;)Lnet/minecraft/class_2487; */
    public static net.minecraft.class_2487 method_10633(java.io.File a0) throws Exception {
        return net.minecraft.class_2507.method_10633(a0.toPath());
    }

    /** was net/minecraft/class_2507.method_30614(Lnet/minecraft/class_2487;Ljava/io/File;)V */
    public static void method_30614(net.minecraft.class_2487 a0, java.io.File a1) throws Exception {
        net.minecraft.class_2507.method_30614(a0, a1.toPath());
    }

    /** was net/minecraft/class_2512.method_10691(Lnet/minecraft/class_2487;)Lnet/minecraft/class_2338; */
    public static net.minecraft.class_2338 method_10691(net.minecraft.class_2487 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API requires a keyed block-position value and cannot decode the old root compound contract");
    }

    /** was net/minecraft/class_2754.method_11847(Ljava/lang/String;Ljava/lang/Class;Ljava/util/Collection;)Lnet/minecraft/class_2754; */
    public static net.minecraft.class_2754 method_11847(java.lang.String a0, java.lang.Class a1, java.util.Collection a2) {
        java.util.List list = a2 instanceof java.util.List ? (java.util.List) a2 : new java.util.ArrayList(a2);
        return net.minecraft.class_2754.method_11847(a0, a1, list);
    }

    /** was net/minecraft/class_2788.method_17816(Lnet/minecraft/class_2540;Lnet/minecraft/class_1860;)V */
    public static void method_17816(net.minecraft.class_2540 a0, net.minecraft.class_1860 a1) {
        throw new UnsupportedOperationException("CenturyBridge: Recipe no longer contains its ID");
    }

    /** was net/minecraft/class_3002.method_12966(Lnet/minecraft/class_2487;Lnet/minecraft/class_2960;)Lnet/minecraft/class_3002; */
    public static net.minecraft.class_3002 method_12966(net.minecraft.class_2487 a0, net.minecraft.class_2960 a1) {
        return net.minecraft.class_3002.method_12966(a0, a1, net.minecraft.class_7225.class_7874.method_46761(java.util.stream.Stream.empty()));
    }

    /** was net/minecraft/class_3019.method_13053(Lcom/mojang/brigadier/CommandDispatcher;)V */
    public static void method_13053(com.mojang.brigadier.CommandDispatcher a0) {
        net.minecraft.class_3019.method_13053(a0, null);
    }

    /** was net/minecraft/class_31.method_29029(Lcom/mojang/serialization/Dynamic;Lcom/mojang/datafixers/DataFixer;ILnet/minecraft/class_2487;Lnet/minecraft/class_1940;Lnet/minecraft/class_5315;Lnet/minecraft/class_31$class_7729;Lnet/minecraft/class_5285;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/class_31; */
    public static net.minecraft.class_31 method_29029(com.mojang.serialization.Dynamic a0, com.mojang.datafixers.DataFixer a1, int a2, net.minecraft.class_2487 a3, net.minecraft.class_1940 a4, net.minecraft.class_5315 a5, net.minecraft.class_31.class_7729 a6, net.minecraft.class_5285 a7, com.mojang.serialization.Lifecycle a8) {
        return net.minecraft.class_31.method_29029(a0, a4, a6, a7, a8);
    }

    /** was net/minecraft/class_3115.method_13595(Lcom/mojang/brigadier/CommandDispatcher;)V */
    public static void method_13595(com.mojang.brigadier.CommandDispatcher a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new overload requires a class_7157 registry access instance, which cannot be derived from the old arguments or target instance");
    }

    /** was net/minecraft/class_3142.method_13736(Lcom/mojang/brigadier/CommandDispatcher;)V */
    public static void method_13736(com.mojang.brigadier.CommandDispatcher a0) {
        net.minecraft.class_3142.method_13736(a0, null);
    }

    /** was net/minecraft/class_3146.method_13776(Lcom/mojang/brigadier/CommandDispatcher;)V */
    public static void method_13776(com.mojang.brigadier.CommandDispatcher a0) {
        net.minecraft.class_3146.method_13776(a0, (net.minecraft.class_7157) null);
    }

    /** was net/minecraft/class_3151.method_13804(Lcom/mojang/brigadier/CommandDispatcher;)V */
    public static void method_13804(com.mojang.brigadier.CommandDispatcher a0) {
        net.minecraft.class_3151.method_13804(a0, null);
    }

    /** was net/minecraft/class_32.method_54523(Lcom/mojang/serialization/Dynamic;Lnet/minecraft/class_7712;Lnet/minecraft/class_2378;Lnet/minecraft/class_5455$class_6890;)Lnet/minecraft/class_8895; */
    public static net.minecraft.class_8895 method_54523(com.mojang.serialization.Dynamic a0, net.minecraft.class_7712 a1, net.minecraft.class_2378 a2, net.minecraft.class_5455.class_6890 a3) {
        return net.minecraft.class_32.method_54523(a0, a1, a2, (net.minecraft.class_7225.class_7874)a3);
        // CB-CONFIDENCE: medium -- cast assumes class_5455.class_6890 still implements class_7225.class_7874 in 1.21.2
    }

    /** was net/minecraft/class_3281.method_14436(ILnet/minecraft/class_3264;)Lnet/minecraft/class_3281; */
    public static net.minecraft.class_3281 method_14436(int a0, net.minecraft.class_3264 a1) {
        int max = net.minecraft.class_3281.values().length - 1;
        int id = a0;
        if (a1 != null && "WRAP".equals(a1.name())) {
            id = a0 % (max + 1);
            if (id < 0) {
                id += (max + 1);
            }
        }
        return net.minecraft.class_3281.method_14436(new net.minecraft.class_6497(Integer.valueOf(0), Integer.valueOf(max)), id);
    }

    /** was net/minecraft/class_3440.method_32358(Lnet/minecraft/class_2487;)Lnet/minecraft/class_3440; */
    public static net.minecraft.class_3440 method_32358(net.minecraft.class_2487 a0) {
        throw new UnsupportedOperationException("CenturyBridge: StructureStart deserialization requires a RegistryWrapper.WrapperLookup in 1.20.5");
    }

    /** was net/minecraft/class_3485.method_15085(Ljava/nio/file/Path;Lnet/minecraft/class_2960;Ljava/lang/String;)Ljava/nio/file/Path; */
    public static java.nio.file.Path method_15085(java.nio.file.Path a0, net.minecraft.class_2960 a1, java.lang.String a2) {
        throw new UnsupportedOperationException("CenturyBridge: the new method always uses the manager's configured generated path and cannot honor the old caller-supplied Path");
    }

    /** was net/minecraft/class_3518.method_34916(Lcom/google/gson/JsonObject;Ljava/lang/String;Lnet/minecraft/class_1792;)Lnet/minecraft/class_1792; */
    public static net.minecraft.class_1792 method_34916(com.google.gson.JsonObject a0, java.lang.String a1, net.minecraft.class_1792 a2) {
        net.minecraft.class_6880 var3 = net.minecraft.class_6880.method_40223(a2);
        return (net.minecraft.class_1792) net.minecraft.class_3518.method_34916(a0, a1, var3).comp_349();
    }

    /** was net/minecraft/class_3558.method_20049(Lnet/minecraft/class_1922;Lnet/minecraft/class_2680;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_2338;Lnet/minecraft/class_2350;I)I */
    public static int method_20049(net.minecraft.class_1922 a0, net.minecraft.class_2680 a1, net.minecraft.class_2338 a2, net.minecraft.class_2680 a3, net.minecraft.class_2338 a4, net.minecraft.class_2350 a5, int a6) {
        return net.minecraft.class_3558.method_20049(a1, a3, a5, a6);
    }

    /** was net/minecraft/class_3558.method_51562(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_2350;)Lnet/minecraft/class_265; */
    public static net.minecraft.class_265 method_51562(net.minecraft.class_1922 a0, net.minecraft.class_2338 a1, net.minecraft.class_2680 a2, net.minecraft.class_2350 a3) {
        return net.minecraft.class_3558.method_51562(a2, a3);
    }

    /** was net/minecraft/class_3715.method_17472(Lnet/minecraft/class_1297;Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_1799;)Z */
    public static boolean method_17472(net.minecraft.class_1297 a0, net.minecraft.class_1937 a1, net.minecraft.class_2338 a2, net.minecraft.class_2680 a3, net.minecraft.class_1799 a4) {
        if (a0 instanceof net.minecraft.class_1309) {
            return net.minecraft.class_3715.method_17472((net.minecraft.class_1309) a0, a1, a2, a3, a4);
        }
        return false;
    }

    /** was net/minecraft/class_3748.method_16546(Lnet/minecraft/class_3499$class_3501;Lnet/minecraft/class_3499$class_3501;)Z */
    public static boolean method_16546(net.minecraft.class_3499.class_3501 a0, net.minecraft.class_3499.class_3501 a1) {
        return net.minecraft.class_3748.method_16546(net.minecraft.class_3499.class_10326.method_64824(a0), net.minecraft.class_3499.class_10326.method_64824(a1));
    }

    /** was net/minecraft/class_3924.method_31666(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_3924;)V */
    public static void method_31666(net.minecraft.class_1937 a0, net.minecraft.class_2338 a1, net.minecraft.class_2680 a2, net.minecraft.class_3924 a3) {
        throw new UnsupportedOperationException("CenturyBridge: the new method requires a class_1863.class_7266 recipe matcher that cannot be derived from the old arguments");
    }

    /** was net/minecraft/class_3978.method_32360(Lnet/minecraft/class_2487;)Lnet/minecraft/class_3978; */
    public static net.minecraft.class_3978 method_32360(net.minecraft.class_2487 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_32360(CompoundTag) requires a RegistryWrapper.WrapperLookup (class_7225.class_7874) in 1.20.5+; the old single-argument signature relied on global registry access that no longer exists");
    }

    /** was net/minecraft/class_4096.method_46883(Ljava/util/function/Predicate;Lnet/minecraft/class_4140;Lnet/minecraft/class_4140;ZLjava/util/Optional;)Lnet/minecraft/class_7893; */
    public static net.minecraft.class_7893 method_46883(java.util.function.Predicate a0, net.minecraft.class_4140 a1, net.minecraft.class_4140 a2, boolean a3, java.util.Optional a4) {
        return net.minecraft.class_4096.method_46883(a0, a1, a2, a3, a4, (a, b) -> true);
    }

    /** was net/minecraft/class_4148.method_37456(Lnet/minecraft/class_1309;Lnet/minecraft/class_1309;)Z */
    public static boolean method_37456(net.minecraft.class_1309 a0, net.minecraft.class_1309 a1) {
        return net.minecraft.class_4148.method_37456((net.minecraft.class_3218) a0.method_37908(), a0, a1);
    }

    /** was net/minecraft/class_4209.method_33139(Lnet/minecraft/class_1937;Lnet/minecraft/class_5712;Lnet/minecraft/class_243;)V */
    public static void method_33139(net.minecraft.class_1937 a0, net.minecraft.class_5712 a1, net.minecraft.class_243 a2) {
        net.minecraft.class_4209.method_33139(a0, net.minecraft.class_6880.method_40223(a1), a2);
    }

    /** was net/minecraft/class_4215.method_19548(Lnet/minecraft/class_1309;Lnet/minecraft/class_1309;F)V */
    public static void method_19548(net.minecraft.class_1309 a0, net.minecraft.class_1309 a1, float a2) {
        net.minecraft.class_4215.method_19548(a0, a1, a2, 0);
    }

    /** was net/minecraft/class_4296.method_39398(Lnet/minecraft/class_2499;Ljava/util/function/Function;Lnet/minecraft/class_1923;)Lnet/minecraft/class_4296; */
    public static net.minecraft.class_4296 method_39398(net.minecraft.class_2499 a0, java.util.function.Function a1, net.minecraft.class_1923 a2) {
        throw new UnsupportedOperationException("CenturyBridge: class_4296.method_39398(PacketByteBuf, Function, ChunkPos) deserialized chunk section data from a network buffer using a registry lookup function; 1.21.2 moved that deserialization out of class_4296 entirely and method_39398 now only accepts a pre-deserialized List with no buffer-reading equivalent available on this class");
        // CB-CONFIDENCE: high -- the old method performed PacketByteBuf deserialization with a registry Function and ChunkPos; the new API surface for class_4296 contains no buffer-reading method, only a List setter, so no faithful bridge exists
    }

    /** was net/minecraft/class_4309.method_51148(Lnet/minecraft/class_3300;Ljava/lang/String;Lcom/google/gson/Gson;Ljava/util/Map;)V */
    public static void method_51148(net.minecraft.class_3300 a0, java.lang.String a1, com.google.gson.Gson a2, java.util.Map a3) {
        throw new UnsupportedOperationException("CenturyBridge: the old Gson-based contract cannot supply the DynamicOps and Codec required by the new API");
    }

    /** was net/minecraft/class_4519.method_22193(Ljava/lang/String;)Ljava/util/Collection; */
    public static java.util.Collection method_22193(java.lang.String a0) {
        return net.minecraft.class_4519.method_22193(a0).collect(java.util.stream.Collectors.toList());
    }

    /** was net/minecraft/class_4519.method_29405()Ljava/util/Collection; */
    public static java.util.Collection method_29405() {
        return net.minecraft.class_4519.method_29405().collect(java.util.stream.Collectors.toList());
    }

    /** was net/minecraft/class_4525.method_22246(Lnet/minecraft/class_3341;ILnet/minecraft/class_3218;)V */
    public static void method_22246(net.minecraft.class_3341 a0, int a1, net.minecraft.class_3218 a2) {
        net.minecraft.class_4525.method_22246(a0, a2);
    }

    /** was net/minecraft/class_4525.method_22255(Lnet/minecraft/class_2338;ILnet/minecraft/class_3218;)Lnet/minecraft/class_2338; */
    public static net.minecraft.class_2338 method_22255(net.minecraft.class_2338 a0, int a1, net.minecraft.class_3218 a2) {
        return net.minecraft.class_4525.method_22255(a0, a1, a2).orElse(null);
    }

    /** was net/minecraft/class_4525.method_22258(Lnet/minecraft/class_2338;ILnet/minecraft/class_3218;)Ljava/util/Collection; */
    public static java.util.Collection method_22258(net.minecraft.class_2338 a0, int a1, net.minecraft.class_3218 a2) {
        return net.minecraft.class_4525.method_22258(a0, a1, a2).collect(java.util.stream.Collectors.toList());
    }

    /** was net/minecraft/class_4609.method_23221(Lnet/minecraft/class_4590;Lnet/minecraft/class_2350;Ljava/util/function/Supplier;)Lnet/minecraft/class_4590; */
    public static net.minecraft.class_4590 method_23221(net.minecraft.class_4590 a0, net.minecraft.class_2350 a1, java.util.function.Supplier a2) {
        return net.minecraft.class_4609.method_23221(a0, a1);
    }

    /** was net/minecraft/class_4824.method_47120(Ljava/util/function/Predicate;Ljava/util/function/Function;)Lnet/minecraft/class_7893; */
    public static net.minecraft.class_7893 method_47120(java.util.function.Predicate a0, java.util.function.Function a1) {
        return net.minecraft.class_4824.method_47120((world, value) -> a0.test(value), (world, value) -> (java.util.Optional) a1.apply(value));
    }

    /** was net/minecraft/class_4828.method_47138(Ljava/util/function/Predicate;)Lnet/minecraft/class_7893; */
    public static net.minecraft.class_7893 method_47138(java.util.function.Predicate a0) {
        return net.minecraft.class_4828.method_47138((net.minecraft.class_3218 world, net.minecraft.class_1309 entity) -> a0.test(entity));
    }

    /** was net/minecraft/class_4828.method_47139(Ljava/util/function/Predicate;Ljava/util/function/BiConsumer;Z)Lnet/minecraft/class_7893; */
    public static net.minecraft.class_7893 method_47139(java.util.function.Predicate a0, java.util.function.BiConsumer a1, boolean a2) {
        net.minecraft.class_4828.class_10252 cbPredicate = (net.minecraft.class_3218 world, net.minecraft.class_1309 entity) -> a0.test(entity);
        net.minecraft.class_4828.class_10253 cbConsumer = (net.minecraft.class_3218 world, java.lang.Object obj, net.minecraft.class_1309 entity) -> a1.accept(obj, entity);
        return net.minecraft.class_4828.method_47139(cbPredicate, cbConsumer, a2);
    }

    /** was net/minecraft/class_5317.method_41598(Lnet/minecraft/class_5455;)Lnet/minecraft/class_7723; */
    public static net.minecraft.class_7723 method_41598(net.minecraft.class_5455 a0) {
        return net.minecraft.class_5317.method_41598((net.minecraft.class_7225.class_7874)a0);
    }

    /** was net/minecraft/class_5317.method_41599(Lnet/minecraft/class_5455;)Lnet/minecraft/class_5363; */
    public static net.minecraft.class_5363 method_41599(net.minecraft.class_5455 a0) {
        return net.minecraft.class_5317.method_41599((net.minecraft.class_7225.class_7874) a0);
    }

    /** was net/minecraft/class_5514.method_33805(Ljava/lang/String;)Lnet/minecraft/class_5514; */
    public static net.minecraft.class_5514 method_33805(java.lang.String a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_33805 return type changed from class_5514 to class_9847; class_9847 does not extend class_5514, cannot cast");
        // CB-CONFIDENCE: medium -- class_5514 references class_9847$class_5515 in its own method signature, strongly suggesting no inheritance relationship; return type change is fundamental
    }

    /** was net/minecraft/class_5545.method_31632(Lnet/minecraft/class_2248;)Lnet/minecraft/class_2680; */
    public static net.minecraft.class_2680 method_31632(net.minecraft.class_2248 a0) {
        return net.minecraft.class_5545.method_31632((net.minecraft.class_5544) a0);
    }

    /** was net/minecraft/class_5762.method_35175(Lnet/minecraft/class_5762;Lnet/minecraft/class_1309;)V
     *  AxolotlEntity's kill-assist buff: the old shape read the axolotl's own world; 1.21.2 takes it as a
     *  ServerWorld, and the buff only ever applied on the logical server */
    public static void method_35175(net.minecraft.class_5762 a0, net.minecraft.class_1309 a1) {
        if (a0.method_37908() instanceof net.minecraft.class_3218 world) {
            net.minecraft.class_5762.method_35175(world, a0, a1);
        }
    }

    /** was net/minecraft/class_5768.method_33241()Lnet/minecraft/class_1856; */
    public static net.minecraft.class_1856 method_33241() {
        return (net.minecraft.class_1856) net.minecraft.class_5768.method_33241();
    }

    /** was net/minecraft/class_6054.method_35181()Lnet/minecraft/class_1856; */
    public static net.minecraft.class_1856 method_35181() {
        java.util.function.Predicate predicate = net.minecraft.class_6054.method_35181();
        return (net.minecraft.class_1856) predicate;
    }

    /** was net/minecraft/class_6329.method_36192(Lcom/mojang/brigadier/CommandDispatcher;)V */
    public static void method_36192(com.mojang.brigadier.CommandDispatcher a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_36192(CommandDispatcher) requires a CommandBuildContext (class_7157) in 1.20.5+; the old single-arg overload has no source for the registry/feature context needed to construct one");
        // CB-CONFIDENCE: high -- the new API mandates a CommandBuildContext that cannot be derived from a CommandDispatcher alone
    }

    /** was net/minecraft/class_6759.method_39406(Lnet/minecraft/class_2499;Ljava/util/function/Function;Lnet/minecraft/class_1923;Ljava/util/function/Consumer;)V */
    public static void method_39406(net.minecraft.class_2499 a0, java.util.function.Function a1, net.minecraft.class_1923 a2, java.util.function.Consumer a3) {
        java.util.List list = net.minecraft.class_6759.method_39406(a0, a1, a2);
        for (Object element : list) {
            a3.accept(element);
        }
    }

    /** was net/minecraft/class_7107.method_41366()Lnet/minecraft/class_1856; */
    public static net.minecraft.class_1856 method_41366() {
        return (net.minecraft.class_1856) net.minecraft.class_7107.method_41366();
    }

    /** was net/minecraft/class_7244.method_42122(Lnet/minecraft/class_1299;Lnet/minecraft/class_3730;Lnet/minecraft/class_3218;Lnet/minecraft/class_2338;IIILnet/minecraft/class_7244$class_7502;)Ljava/util/Optional; */
    public static java.util.Optional method_42122(net.minecraft.class_1299 a0, net.minecraft.class_3730 a1, net.minecraft.class_3218 a2, net.minecraft.class_2338 a3, int a4, int a5, int a6, net.minecraft.class_7244.class_7502 a7) {
        return net.minecraft.class_7244.method_42122(a0, a1, a2, a3, a4, a5, a6, a7, false);
    }

    /** was net/minecraft/class_7408.method_43406(Lnet/minecraft/class_2378;)Lnet/minecraft/class_1535; */
    public static net.minecraft.class_1535 method_43406(net.minecraft.class_2378 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API requires a class_7891 registry context and no longer returns class_1535; class_2378 cannot be faithfully adapted using the available API");
    }

    /** was net/minecraft/class_7446.method_43721(Lnet/minecraft/class_2378;)Lnet/minecraft/class_2582; */
    public static net.minecraft.class_2582 method_43721(net.minecraft.class_2378 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new API only registers entries into class_7891 and cannot return the old class_2582 result from class_2378");
    }

    /** was net/minecraft/class_7509.method_44207(Lnet/minecraft/class_2378;)Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_44207(net.minecraft.class_2378 a0) {
        return net.minecraft.class_7509.method_44207(a0).codec();
    }

    /** was net/minecraft/class_7511.method_44219(Lnet/minecraft/class_2378;)Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_44219(net.minecraft.class_2378 a0) {
        return net.minecraft.class_7511.method_44219(a0).codec();
    }

    /** was net/minecraft/class_7691.method_45368()Lnet/minecraft/class_1856; */
    public static net.minecraft.class_1856 method_45368() {
        throw new UnsupportedOperationException("CenturyBridge: class_7691.method_45368() now returns Predicate instead of Ingredient; 1.20.5 Ingredient is data-driven and cannot wrap an arbitrary predicate");
    }

    /** was net/minecraft/class_7723.method_45519(Lnet/minecraft/class_2378;Lnet/minecraft/class_6880;Lnet/minecraft/class_2794;)Lnet/minecraft/class_2378; */
    public static net.minecraft.class_2378 method_45519(net.minecraft.class_2378 a0, net.minecraft.class_6880 a1, net.minecraft.class_2794 a2) {
        throw new UnsupportedOperationException("CenturyBridge: the new API returns a Map and cannot reconstruct the old Registry result while preserving registry metadata and lifecycle semantics");
    }

    /** was net/minecraft/class_7723.method_45520(Lnet/minecraft/class_2378;Lnet/minecraft/class_2378;Lnet/minecraft/class_2794;)Lnet/minecraft/class_2378; */
    public static net.minecraft.class_2378 method_45520(net.minecraft.class_2378 a0, net.minecraft.class_2378 a1, net.minecraft.class_2794 a2) {
        throw new UnsupportedOperationException("CenturyBridge: the new method_45520 requires a class_7225 and a registry Map, so the old pair of class_2378 arguments cannot be faithfully adapted");
    }

    /** was net/minecraft/class_8005.method_48031(Lnet/minecraft/class_161;Ljava/util/function/Predicate;Lnet/minecraft/class_8005$class_8006;)V */
    public static void method_48031(net.minecraft.class_161 a0, java.util.function.Predicate a1, net.minecraft.class_8005.class_8006 a2) {
        throw new UnsupportedOperationException("CenturyBridge: RecipeResultCollection now requires RecipeEntry");
    }

    /** was net/minecraft/class_8054.method_48438(Ljava/lang/String;Lnet/minecraft/class_1792;FLnet/minecraft/class_2561;Ljava/util/Map;)Lnet/minecraft/class_8054; */
    public static net.minecraft.class_8054 method_48438(java.lang.String a0, net.minecraft.class_1792 a1, float a2, net.minecraft.class_2561 a3, java.util.Map a4) {
        return net.minecraft.class_8054.method_48438(a0, a1, a3, a4);
    }

    /** was net/minecraft/class_8055.method_48440(Lnet/minecraft/class_5455;Lnet/minecraft/class_1799;)Ljava/util/Optional; */
    public static java.util.Optional method_48440(net.minecraft.class_5455 a0, net.minecraft.class_1799 a1) {
        return net.minecraft.class_8055.method_48440(a0, a1);
    }

    /** was net/minecraft/class_8057.method_48448(Lnet/minecraft/class_5455;Lnet/minecraft/class_1799;)Ljava/util/Optional; */
    public static java.util.Optional method_48448(net.minecraft.class_5455 a0, net.minecraft.class_1799 a1) {
        return net.minecraft.class_8057.method_48448((net.minecraft.class_7225.class_7874) a0, a1);
    }

    /** was net/minecraft/class_8173.method_49207(Lnet/minecraft/class_2378;)Ljava/lang/String; */
    public static java.lang.String method_49207(net.minecraft.class_2378 a0) {
        return net.minecraft.class_8173.method_49207(a0).comp_2801().toString();
    }

    /** was net/minecraft/class_8216.method_49719(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String; */
    public static java.lang.String method_49719(java.lang.String a0, java.lang.String a1) {
        return net.minecraft.class_8216.method_49719(a0, java.util.UUID.fromString(a1));
    }

    /** was net/minecraft/class_8216.method_49720(Ljava/lang/String;Ljava/lang/String;Z)Ljava/lang/String; */
    public static java.lang.String method_49720(java.lang.String a0, java.lang.String a1, boolean a2) {
        return net.minecraft.class_8216.method_49720(a0, java.util.UUID.fromString(a1), a2);
    }

    /** was net/minecraft/class_8795.method_53853(Lnet/minecraft/class_7784;)Lnet/minecraft/class_2438; */
    public static net.minecraft.class_2438 method_53853(net.minecraft.class_7784 a0) {
        return net.minecraft.class_8795.method_53853(a0, new java.util.concurrent.CompletableFuture<>());
    }

    /** was net/minecraft/class_8886.method_54475(Lnet/minecraft/class_1937;Lnet/minecraft/class_9694;)Ljava/util/Optional; */
    public static java.util.Optional method_54475(net.minecraft.class_1937 a0, net.minecraft.class_9694 a1) {
        return net.minecraft.class_8886.method_54475((net.minecraft.class_3218) a0, a1);
    }

    /** was net/minecraft/class_8890.method_54509(Lnet/minecraft/class_2378;)Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_54509(net.minecraft.class_2378 a0) {
        return net.minecraft.class_8890.method_54509(a0).codec();
    }

    /** was net/minecraft/class_8950.method_55016(Lnet/minecraft/class_4095;)Lnet/minecraft/class_4095; */
    public static net.minecraft.class_4095 method_55016(net.minecraft.class_4095 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new method requires a class_8949 context that the old signature does not provide");
    }

    /** was net/minecraft/class_9010.method_55377(Lcom/mojang/brigadier/StringReader;Lcom/mojang/serialization/Codec;)Ljava/lang/Object; */
    public static java.lang.Object method_55377(com.mojang.brigadier.StringReader a0, com.mojang.serialization.Codec a1) {
        return net.minecraft.class_9010.method_55377(net.minecraft.class_7225.class_7874.method_46761(java.util.stream.Stream.empty()), a0, a1);
    }

    /** was net/minecraft/class_9019.method_55446()Lnet/minecraft/class_9019; */
    public static net.minecraft.class_9019 method_55446() {
        throw new UnsupportedOperationException("CenturyBridge: method_55446() requires a class_7157 (registry lookup) which cannot be constructed without command context in 1.20.5");
    }

    /** was net/minecraft/class_9362.method_58659(Lnet/minecraft/class_1657;)Z */
    public static boolean method_58659(net.minecraft.class_1657 a0) {
        return net.minecraft.class_9362.method_58659(a0);
    }

    /** was net/minecraft/class_9383.method_58284(Lnet/minecraft/class_7780;Lnet/minecraft/class_3300;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture; */
    public static java.util.concurrent.CompletableFuture method_58284(net.minecraft.class_7780 a0, net.minecraft.class_3300 a1, java.util.concurrent.Executor a2) {
        return net.minecraft.class_9383.method_58284(a0, java.util.Collections.emptyList(), a1, a2);
    }

    /** was net/minecraft/class_9424$class_9425.method_58427(Lnet/minecraft/class_6862;)Lnet/minecraft/class_9424$class_9425; */
    public static net.minecraft.class_9424.class_9425 method_58427(net.minecraft.class_6862 a0) {
        throw new UnsupportedOperationException("CenturyBridge: converting a tag key to a registry entry list requires a registry entry owner, which is not available");
    }

    /** was net/minecraft/class_9424$class_9425.method_58431(Ljava/util/List;F)Lnet/minecraft/class_9424$class_9425; */
    public static net.minecraft.class_9424.class_9425 method_58431(java.util.List a0, float a1) {
        return net.minecraft.class_9424.class_9425.method_58431(net.minecraft.class_6885.method_40242(a0), a1);
    }

}
