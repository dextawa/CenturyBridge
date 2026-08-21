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
     * 1.20.1 EntityRenderer.drawEntity(ctx,x,y,size,mouseX,mouseY,entity)
     * gained 3 extra int params (overlayLight,blockLight,yaw) at 1.20.2.
     * 13 client call sites.
     */
    public static void method_2486(net.minecraft.class_332 context,
            int x, int y, int size, float mouseX, float mouseY,
            net.minecraft.class_1309 entity) {
        net.minecraft.class_490.method_2486(context, x, y, size, 0, 0, mouseX, mouseY, 0.0f, entity);
    }

    /**
     * 1.20.1 EntityRenderer.drawEntity(ctx,x,y,size,rot,cam,entity)
     * gained a float+Vector3f pivot at 1.20.2. 4 client call sites.
     */
    public static void method_48472(net.minecraft.class_332 context,
            int x, int y, int size,
            org.joml.Quaternionf rotation, org.joml.Quaternionf cameraAngle,
            net.minecraft.class_1309 entity) {
        net.minecraft.class_490.method_48472(context, (float) x, (float) y, size,
                new org.joml.Vector3f(0, 0, 0), rotation, cameraAngle, entity);
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


    /** was net/minecraft/class_1145.method_4884(Lnet/minecraft/class_1113;Lnet/minecraft/class_1146;)V */
    public void method_4884(net.minecraft.class_1113 a0, net.minecraft.class_1146 a1) {
        ((net.minecraft.class_1145) (Object) this).method_4884(a0, a1, 1.0f);
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

    /** was net/minecraft/class_163$class_164.method_721(Lnet/minecraft/class_161;)V */
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

    /** was net/minecraft/class_1732.method_7662(Lnet/minecraft/class_1860;)V */
    public void method_7662(net.minecraft.class_1860 a0) {
        net.minecraft.class_8786 current = ((net.minecraft.class_1732)(Object)this).method_7663();
        if (current == null) {
            throw new UnsupportedOperationException("CenturyBridge: cannot derive RecipeEntry id from null current recipe");
        }
        ((net.minecraft.class_1732)(Object)this).method_7662(new net.minecraft.class_8786(current.comp_1932(), a0));
    }

    /** was net/minecraft/class_1732.method_7663()Lnet/minecraft/class_1860; */
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

    /** was net/minecraft/class_1921.method_23585()Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_23585(net.minecraft.class_1921 self) {
        return net.minecraft.class_1921.method_23584(net.minecraft.class_2960.method_60654("textures/misc/enchanted_glint_item.png"));
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

    /** was net/minecraft/class_2602.method_11126(Lnet/minecraft/class_2790;)V */
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


    /** was net/minecraft/class_2792.method_12069(Lnet/minecraft/class_2803;)V */
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

    /** was net/minecraft/class_2921.method_12666(Lnet/minecraft/class_2923;)V */
    public void method_12666(net.minecraft.class_2923 a0) {
        ((net.minecraft.class_8763) (Object) this).method_12666(a0);
    }

    /** was net/minecraft/class_2933.method_12697(Lnet/minecraft/class_2935;)V */
    public void method_12697(net.minecraft.class_2935 a0) {
        throw new UnsupportedOperationException("CenturyBridge: tasks are no longer managed by BuiltChunk");
    }

    /** was net/minecraft/class_2952.method_12816(IIILnet/minecraft/class_1860;Ljava/util/Iterator;I)V */
    public void method_12816(int a0, int a1, int a2, net.minecraft.class_1860 a3, java.util.Iterator a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: class_8786 (RecipeEntry) requires a class_2960 (Identifier) that is not available from the old class_1860-only method_12816 signature; Recipe.getId() was removed in 1.20.2");
    }

    /** was net/minecraft/class_2994.method_3788()I */
    public int method_3788() {
        return ((net.minecraft.class_8599) (Object) this).method_3788();
    }

    /** was net/minecraft/class_2994.method_3802()I */
    public int method_3802() {
        return ((net.minecraft.class_8599) (Object) this).method_3802();
    }

    /** was net/minecraft/class_2994.method_3827()Ljava/lang/String; */
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

    /** was net/minecraft/class_310.method_1519()V */
    public static void method_1519(net.minecraft.class_310 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_1519 (handleInputEvents) was removed and its functionality was merged into client tick");
    }

    /** was net/minecraft/class_310.method_1537(Z)V */
    public static void method_1537(net.minecraft.class_310 self, boolean a0) {
        throw new UnsupportedOperationException("CenturyBridge: setConnectedToRealms is no longer supported as this state is now automatically derived from the network handler.");
    }

    /** was net/minecraft/class_310.method_1589()Z */
    public static boolean method_1589(net.minecraft.class_310 self) {
        return true;
    }

    /** was net/minecraft/class_310.method_44649()Z */
    public static boolean method_44649(net.minecraft.class_310 self) {
        return self.method_47595();
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

    /** was net/minecraft/class_332.method_48585(Lnet/minecraft/class_2960;IIIIIIII)V */
    public static void method_48585(net.minecraft.class_332 self, net.minecraft.class_2960 a0, int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8) {
        self.method_52708(net.minecraft.class_1921::method_62277, a0, a1, a2, a3, a4, a5, a6, a7, a8);
    }

    /** was net/minecraft/class_332.method_48586(Lnet/minecraft/class_2960;IIIIIIIII)V */
    public static void method_48586(net.minecraft.class_332 self, net.minecraft.class_2960 a0, int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9) {
        self.method_52708(net.minecraft.class_1921::method_62277, a0, a1, a2, a3, a4, a5, a6, a8, a9);
    }

    /** was net/minecraft/class_332.method_48587(Lnet/minecraft/class_2960;IIIIIIIIIIII)V */
    public static void method_48587(net.minecraft.class_332 self, net.minecraft.class_2960 a0, int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9, int a10, int a11, int a12) {
        int i = Math.min(a5, a3 / 2);
        int j = Math.min(a5, a3 / 2);
        int k = Math.min(a6, a4 / 2);
        int l = Math.min(a6, a4 / 2);
        if (a3 == a5 && a4 == a6) {
            self.method_25290(net.minecraft.class_1921::method_62277, a0, a1, a2, (float)a9, (float)a10, a3, a4, a11, a12);
        } else if (a4 == a6) {
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1, a2, (float)a9, (float)a10, i, a4, i, a4, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1 + i, a2, (float)(a9 + a5), (float)a10, a3 - i - j, a4, a7, a8, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1 + a3 - j, a2, (float)(a9 + a5 + a7), (float)a10, j, a4, j, a4, a11, a12, -1);
        } else if (a3 == a5) {
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1, a2, (float)a9, (float)a10, a3, k, a3, k, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1, a2 + k, (float)a9, (float)(a10 + a6), a3, a4 - k - l, a7, a8, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1, a2 + a4 - l, (float)a9, (float)(a10 + a6 + a8), a3, l, a3, l, a11, a12, -1);
        } else {
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1, a2, (float)a9, (float)a10, i, k, i, k, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1 + i, a2, (float)(a9 + a5), (float)a10, a3 - i - j, k, a7, k, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1 + a3 - j, a2, (float)(a9 + a5 + a7), (float)a10, j, k, j, k, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1, a2 + k, (float)a9, (float)(a10 + a6), i, a4 - k - l, i, a8, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1 + i, a2 + k, (float)(a9 + a5), (float)(a10 + a6), a3 - i - j, a4 - k - l, a7, a8, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1 + a3 - j, a2 + k, (float)(a9 + a5 + a7), (float)(a10 + a6), j, a4 - k - l, j, a8, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1, a2 + a4 - l, (float)a9, (float)(a10 + a6 + a8), i, l, i, l, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1 + i, a2 + a4 - l, (float)(a9 + a5), (float)(a10 + a6 + a8), a3 - i - j, l, a7, l, a11, a12, -1);
            self.method_25293(net.minecraft.class_1921::method_62277, a0, a1 + a3 - j, a2 + a4 - l, (float)(a9 + a5 + a7), (float)(a10 + a6 + a8), j, l, j, l, a11, a12, -1);
        }
    }


    /** was net/minecraft/class_339.field_22757Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22757() {
        return net.minecraft.class_2960.method_60654("textures/gui/widgets.png");
    }

    /** was net/minecraft/class_339.field_42117Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_42117() {
        return net.minecraft.class_2960.method_60654("textures/gui/widgets.png");
    }

    /** was net/minecraft/class_339.method_47937()Lnet/minecraft/class_8000; */
    public static net.minecraft.class_8000 method_47937(net.minecraft.class_339 self) {
        try {
            for (java.lang.reflect.Field field : net.minecraft.class_339.class.getDeclaredFields()) {
                if (field.getType() == net.minecraft.class_8000.class) {
                    field.setAccessible(true);
                    return (net.minecraft.class_8000) field.get(self);
                }
            }
        } catch (Exception e) {
        }
        return null;
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

    /** was net/minecraft/class_364.method_25401(DDD)Z */
    public boolean method_25401(double a0, double a1, double a2) {
        return ((net.minecraft.class_364) (Object) this).method_25401(a0, a1, 0.0, a2);
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

    /** was net/minecraft/class_4227.method_19671(Lnet/minecraft/class_243;)V */
    public static void method_19671(net.minecraft.class_4227 self, net.minecraft.class_243 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_19671 is no longer supported on FogData");
    }

    /** was net/minecraft/class_4227.method_19672(Lorg/joml/Vector3f;Lorg/joml/Vector3f;)V */
    public static void method_19672(net.minecraft.class_4227 self, org.joml.Vector3f a0, org.joml.Vector3f a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_19672 was deleted and cannot be reconstructed from the new Camera API");
    }

    /** was net/minecraft/class_4227.method_27268()Lnet/minecraft/class_243; */
    public static net.minecraft.class_243 method_27268(net.minecraft.class_4227 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_27268 was removed from class_4227 with no replacement");
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

    /** was net/minecraft/class_4341.field_19576Lnet/minecraft/class_4341$class_4343; */
    public static net.minecraft.class_4341.class_4343 field_19576() {
        return net.minecraft.class_4341.field_45232;
    }

    /** was net/minecraft/class_4341.method_21001()V */
    public static void method_21001(net.minecraft.class_4341 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_4341.method_21001() was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/class_4341.method_21008()V */
    public static void method_21008(net.minecraft.class_4341 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_21008 has no replacement");
    }

    /** was net/minecraft/class_4341.method_21012()V */
    public static void method_21012(net.minecraft.class_4341 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_4341.method_21012 was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/class_4341.method_21021()Ljava/lang/Boolean; */
    public static java.lang.Boolean method_21021(net.minecraft.class_4341 self) {
        return java.lang.Boolean.FALSE;
    }

    /** was net/minecraft/class_4341.method_21024()Ljava/lang/Boolean; */
    public static java.lang.Boolean method_21024(net.minecraft.class_4341 self) {
        throw new UnsupportedOperationException("CenturyBridge: RealmsClient.trialAvailable (method_21024) was removed in 1.20.2");
    }

    /** was net/minecraft/class_4346.method_35685(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/class_4346; */
    public static net.minecraft.class_4346 method_35685(net.minecraft.class_4346 self, java.lang.String a0, java.lang.String a1) {
        self.method_21042(a0, a1);
        return self;
    }

    /** was net/minecraft/class_4358.field_19638Lnet/minecraft/class_4398; */
    public static net.minecraft.class_4398 field_19638() {
        throw new UnsupportedOperationException("CenturyBridge: class_4358.field_19638 (class_4398) was removed in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/class_4358.method_21066(Lnet/minecraft/class_4398;)V */
    public static void method_21066(net.minecraft.class_4358 self, net.minecraft.class_4398 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_21066 (addLabel) is no longer supported as RealmsLabel has been removed in 1.20.2");
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

    /** was net/minecraft/class_4406.method_25191(I)I */
    public static int method_25191(net.minecraft.class_4406 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_25191 was removed from class_4406 in 1.20.3 with no direct replacement");
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

    /** was net/minecraft/class_4419.method_21440()V */
    public static void method_21440(net.minecraft.class_4419 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_21440 was removed from ConnectScreen in 1.20.2 with no direct equivalent");
    }

    /** was net/minecraft/class_4419.method_25228(I)I */
    public static int method_25228(net.minecraft.class_4419 self, int a0) {
        return 40 + a0 * 13;
    }

    /** was net/minecraft/class_447.method_19399()Lnet/minecraft/class_4280; */
    public static net.minecraft.class_4280 method_19399(net.minecraft.class_447 self) {
        throw new UnsupportedOperationException("CenturyBridge: State getter was removed and state is no longer accessible");
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

    /** was net/minecraft/class_455.method_2320()I */
    public static int method_2320(net.minecraft.class_455 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_455.method_2320() (Difficulty.getId()) was removed in 1.20.2; difficulties are now registry-backed and identified by ResourceLocation via method_52753(), with no stable int id equivalent");
    }

    /** was net/minecraft/class_457.field_2716Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_2716() {
        return net.minecraft.class_2960.method_60654("textures/gui/options_background.png");
    }






    /** was net/minecraft/class_4667.method_45626(Lnet/minecraft/class_332;Lnet/minecraft/class_353;IIF)V */
    public static void method_45626(net.minecraft.class_4667 self, net.minecraft.class_332 a0, net.minecraft.class_353 a1, int a2, int a3, float a4) {
        throw new UnsupportedOperationException("CenturyBridge: method_45626 was deleted in 1.20.3 and has no direct replacement");
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

    /** was net/minecraft/class_632$class_633.method_2865(Lnet/minecraft/class_161;Lnet/minecraft/class_167;)V */
    public void method_2865(net.minecraft.class_161 a0, net.minecraft.class_167 a1) {
        throw new UnsupportedOperationException("CenturyBridge: Cannot convert net.minecraft.class_161 to net.minecraft.class_8781");
    }

    /** was net/minecraft/class_632$class_633.method_2866(Lnet/minecraft/class_161;)V */
    public void method_2866(net.minecraft.class_161 a0) {
        ((net.minecraft.class_632.class_633) (Object) this).method_2866(new net.minecraft.class_8779(net.minecraft.class_2960.method_60655("centurybridge", "dummy"), a0));
    }

    /** was net/minecraft/class_632.method_2863()Lnet/minecraft/class_163; */
    public static net.minecraft.class_163 method_2863(net.minecraft.class_632 self) {
        return self.method_53814();
    }

    /** was net/minecraft/class_640.method_2967()Z */
    public static boolean method_2967(net.minecraft.class_640 self) {
        return self.method_45742();
    }


    /** was net/minecraft/class_640.method_2969()V */
    public static void method_2969(net.minecraft.class_640 self) {
        self.method_52810();
    }

    /** was net/minecraft/class_640.method_2977()Ljava/lang/String; */
    public static java.lang.String method_2977(net.minecraft.class_640 self) {
        return self.method_2966().getName();
    }

    /** was net/minecraft/class_640.method_35757()Z */
    public static boolean method_35757(net.minecraft.class_640 self) {
        return self.method_45742();
    }



    /** was net/minecraft/class_7196.method_41890(Lnet/minecraft/class_32$class_5143;Z)Lnet/minecraft/class_6904; */
    public static net.minecraft.class_6904 method_41890(net.minecraft.class_7196 self, net.minecraft.class_32.class_5143 a0, boolean a1) {
        throw new UnsupportedOperationException("CenturyBridge: synchronous world loading from session is no longer supported");
    }

    /** was net/minecraft/class_7196.method_41894(Lnet/minecraft/class_437;Ljava/lang/String;)V */
    public static void method_41894(net.minecraft.class_7196 self, net.minecraft.class_437 a0, java.lang.String a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_7196.method_41894(Screen,String) created a world from the CreateWorldScreen instance's internal state (data packs, level storage, generator options); 1.20.3 replaced it with method_41895(String,class_1940,class_5285,Function,class_437) which requires those as explicit arguments, and the shim has no access to the receiver's fields to supply them");
    }

    /** was net/minecraft/class_7298.method_45342()Z */
    public static boolean method_45342(net.minecraft.class_7298 self) {
        return self.method_44359();
    }


    /** was net/minecraft/class_742.method_3117()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3117(net.minecraft.class_742 self) {
        return self.method_52814().comp_1626();
    }


    /** was net/minecraft/class_742.method_3120(Lnet/minecraft/class_2960;Ljava/lang/String;)V */
    public static void method_3120(net.minecraft.class_742 self, net.minecraft.class_2960 a0, java.lang.String a1) {
        throw new UnsupportedOperationException("CenturyBridge: registerSkinTexture is not supported in 1.20.2 as skin loading is managed by PlayerSkinProvider");
    }

    /** was net/minecraft/class_742.method_3121()Ljava/lang/String; */
    public static java.lang.String method_3121(net.minecraft.class_742 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_3121 (getSkinType) was removed; use method_52814 (getSkinTextures) and extract the model from class_8685 instead");
    }



    /** was net/minecraft/class_742.method_3126()Z */
    public static boolean method_3126(net.minecraft.class_742 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_742.method_3126()Z was deleted in 1.20.2 with no direct replacement");
    }

    /** was net/minecraft/class_7529.method_44406()V */
    public static void method_44406(net.minecraft.class_7529 self) {
        return;
    }

    /** was net/minecraft/class_7578.field_39684Lnet/minecraft/class_7581$class_7586; */
    public static net.minecraft.class_7581.class_7586 field_39684() {
        throw new UnsupportedOperationException("CenturyBridge: field_39684 was removed in 1.20.2 with no direct replacement; the registered class_7581$class_7586 instance no longer exists");
    }

    /** was net/minecraft/class_757.method_34500()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34500(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: getPositionColorLightmapShader was removed");
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

    /** was net/minecraft/class_7766.method_45829(Lnet/minecraft/class_2960;Lnet/minecraft/class_3298;)Lnet/minecraft/class_7764; */
    public static net.minecraft.class_7764 method_45829(net.minecraft.class_7766 self, net.minecraft.class_2960 a0, net.minecraft.class_3298 a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_45829 (SpriteLoader.load(Identifier, Resource)) was removed in 1.20.2; the new API is async (CompletableFuture-based) and takes a ResourceManager or List instead of a single Resource, with no synchronous single-resource equivalent");
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

    /** was net/minecraft/class_8087.method_48613()V */
    public static void method_48613(net.minecraft.class_8087 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_8087.method_48613 was removed in 1.20.2 with no direct equivalent");
    }

    /** was net/minecraft/class_8088.method_48617()V */
    public static void method_48617(net.minecraft.class_8088 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_8088.method_48617 was removed in 1.20.2 with no direct replacement");
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

    /** was net/minecraft/class_8132.method_49001()Lnet/minecraft/class_7847; */
    public static net.minecraft.class_7847 method_49001(net.minecraft.class_8132 self) {
        throw new UnsupportedOperationException("CenturyBridge: HeaderAndFooterLayoutWidget no longer exposes its default positioner");
    }

    /** was net/minecraft/class_8132.method_49002()Lnet/minecraft/class_7847; */
    public static net.minecraft.class_7847 method_49002(net.minecraft.class_8132 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_8132.method_49002() was deleted in 1.20.2 with no direct replacement returning class_7847");
    }

    /** was net/minecraft/class_8132.method_49003()Lnet/minecraft/class_7847; */
    public static net.minecraft.class_7847 method_49003(net.minecraft.class_8132 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_49003() was removed in 1.20.2 with no direct replacement; the new class_8132 API does not produce class_7847 instances");
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

    /** was net/minecraft/class_8209.method_49607()I */
    public static int method_49607(net.minecraft.class_8209 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_49607 returned a creative tab index (int), but 1.20.2 replaced tab indices with ItemGroup references (class_8087); use method_49609() instead");
    }

    /** was net/minecraft/class_8216.field_43134Ljava/lang/String; */
    public static java.lang.String field_43134() {
        return "players";
    }

    /** was net/minecraft/class_8490.method_51203()Lcom/google/gson/Gson; */
    public static com.google.gson.Gson method_51203(net.minecraft.class_8490 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_8490.method_51203() returned a Gson instance that was removed in 1.20.2; the new API surface has no Gson field or method to delegate to");
    }

    /** was net/minecraft/class_879.method_3882(Lnet/minecraft/class_1420;Lnet/minecraft/class_4587;FFF)V */
    public static void method_3882(net.minecraft.class_879 self, net.minecraft.class_1420 a0, net.minecraft.class_4587 a1, float a2, float a3, float a4) {
        throw new UnsupportedOperationException("CenturyBridge: method_3882(class_1420, class_4587, float, float, float) was deleted in 1.20.3 with no direct replacement; remaining method_3883(class_1420) returns class_2960 and drops the MatrixStack/position parameters, so the old render-style contract cannot be expressed");
    }

    /** was net/minecraft/class_879.method_3884(Lnet/minecraft/class_1420;Lnet/minecraft/class_4587;F)V */
    public static void method_3884(net.minecraft.class_879 self, net.minecraft.class_1420 a0, net.minecraft.class_4587 a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_3884 is unsupported because modern rendering requires a VertexConsumerProvider and light map coordinates which are missing from this signature.");
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
}
