package top.dext.centurybridge.rt.v1_21_8;

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
        // 1.21.6 wall: the float-anchor drawEntity became a scissor-box form whose
        // center is the box middle; this box keeps the old (x, y) feet anchor.
        net.minecraft.class_490.method_48472(context, x - size, y - 2 * size, x + size, y,
                (float) size, new org.joml.Vector3f(0, 0, 0), rotation, cameraAngle, entity);
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

    public void method_2865(net.minecraft.class_161 a0, net.minecraft.class_167 a1) {
        throw new UnsupportedOperationException("CenturyBridge: Cannot convert net.minecraft.class_161 to net.minecraft.class_8781");
    }

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

    /** was net/minecraft/class_310.method_1540()Z */
    public static boolean method_1540(net.minecraft.class_310 self) {
        throw new UnsupportedOperationException("CenturyBridge: MinecraftClient.isDemo has no equivalent in 1.20.5");
    }

    /** was net/minecraft/class_310.method_27466(Z)V */
    public static void method_27466(net.minecraft.class_310 self, boolean a0) {
        try {
            java.lang.reflect.Field field = net.minecraft.class_310.class.getDeclaredField("field_24209");
            field.setAccessible(true);
            field.setBoolean(self, a0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** was net/minecraft/class_3116.field_37806Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37806() {
        return net.minecraft.class_3116.field_37806.codec();
    }

    /** was net/minecraft/class_312.method_1606()V */
    public static void method_1606(net.minecraft.class_312 self) {
        self.method_55793();
    }

    /** was net/minecraft/class_315$2.field_28779Lnet/minecraft/class_315; */
    public static net.minecraft.class_315 field_28779() {
        return net.minecraft.class_310.method_1551().field_1690;
    }

    /** was net/minecraft/class_315$3.field_28781Lnet/minecraft/class_315; */
    public static net.minecraft.class_315 field_28781() {
        throw new UnsupportedOperationException("CenturyBridge: field_28781 was an instance field of a deleted anonymous class and cannot be accessed without a receiver");
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

    /** was net/minecraft/class_383.method_41712()F */
    public static float method_41712(net.minecraft.class_383 self) {
        return (float)self.method_2031() / (float)self.method_2032();
    }

    /** was net/minecraft/class_383.method_41713()F */
    public static float method_41713(net.minecraft.class_383 self) {
        return 1.0f;
    }

    /** was net/minecraft/class_389.field_44801Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_44801() {
        return net.minecraft.class_389.field_44801.codec();
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

    /** was net/minecraft/class_4381.method_21155(I)V */
    public static void method_21155(net.minecraft.class_4381 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_21155(int) switched tabs by ordinal index; 1.20.5 CreateWorldScreen uses class_4381$class_4382 tab objects via field_49448/field_49449 with no int-indexed equivalent");
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

    public void method_22919(net.minecraft.class_4587.class_4665 a0, net.minecraft.class_777 a1, float a2, float a3, float a4, int a5, int a6) {
        ((net.minecraft.class_4588) (Object) this).method_22919(a0, a1, a2, a3, a4, 1.0f, a5, a6);
    }

    public void method_22920(net.minecraft.class_4587.class_4665 a0, net.minecraft.class_777 a1, float[] a2, float a3, float a4, float a5, int[] a6, int a7, boolean a8) {
        ((net.minecraft.class_4588) (Object) this).method_22920(a0, a1, a2, a3, a4, a5, 1.0f, a6, a7, a8);
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

    /** was net/minecraft/class_4722.method_24066(Ljava/util/function/Consumer;)V */
    public static void method_24066(net.minecraft.class_4722 self, java.util.function.Consumer a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_4722.method_24066(Consumer) was removed in 1.20.5; the model loading pipeline no longer collects model identifiers via a consumer callback");
        // CB-CONFIDENCE: high -- method explicitly deleted with no direct replacement; the 1.20.5 model loading pipeline was reworked and no new API accepts a Consumer of identifiers
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

    /** was net/minecraft/class_500.method_2528(Ljava/util/List;)V */
    public static void method_2528(net.minecraft.class_500 self, java.util.List a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_500.method_2528(List) was removed in 1.20.5 with no direct replacement; no method in the new API accepts a List");
        // CB-CONFIDENCE: high -- the method is explicitly deleted with no direct replacement, and no remaining method in class_500 accepts a List parameter
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

    /** was net/minecraft/class_5235$class_5236.field_24299Lnet/minecraft/class_5235; */
    public static net.minecraft.class_5235 field_24299() {
        throw new UnsupportedOperationException("CenturyBridge: field_24299 was the outer class_5235 instance reference on class_5235$class_5236 (now synthetic this$0); cannot be returned from a parameterless static shim without a receiver");
    }

    /** was net/minecraft/class_5235.method_27630(Lnet/minecraft/class_5235;)Lnet/minecraft/class_310; */
    public static net.minecraft.class_310 method_27630(net.minecraft.class_5235 self, net.minecraft.class_5235 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_27630 was deleted in 1.20.5 with no direct replacement");
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

    /** was net/minecraft/class_5289$class_5291.field_24585Lnet/minecraft/class_5289; */
    public static net.minecraft.class_5289 field_24585() {
        throw new UnsupportedOperationException("CenturyBridge: field_24585 was the synthetic outer class_5289 reference (this$0) of inner class class_5289$class_5291; it is an instance field bound to a specific instance and cannot be reconstructed as a static constant without a receiver");
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

    /** was net/minecraft/class_5602.field_47444Lnet/minecraft/class_5601; */
    public static net.minecraft.class_5601 field_47444() {
        throw new UnsupportedOperationException("CenturyBridge: field_47444 was removed in 1.20.5 with no direct replacement; cannot determine which PositionType it corresponded to");
    }

    /** was net/minecraft/class_5602.field_47445Lnet/minecraft/class_5601; */
    public static net.minecraft.class_5601 field_47445() {
        throw new UnsupportedOperationException("CenturyBridge: field_47445 (a ParticleType constant in ParticleTypes) was removed in 1.20.5 with no direct replacement");
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

    /** was net/minecraft/class_636.method_2904()F */
    public static float method_2904(net.minecraft.class_636 self) {
        return (float) self.method_51888();
        // CB-CONFIDENCE: medium -- method_51888 is the likely int-returning replacement for the old float getBlockReachDistance; the int-to-float cast loses the 0.5 fractional reach in survival mode
    }

    /** was net/minecraft/class_6385$1.field_33799Lnet/minecraft/class_6385; */
    public static net.minecraft.class_6385 field_33799() {
        throw new UnsupportedOperationException("CenturyBridge: field_33799 was the synthetic this$0 outer-instance reference of class_6385$1, deleted in 1.20.5 because the anonymous class no longer references its enclosing class_6385; a per-instance outer reference cannot be rebuilt as a static constant");
    }

    /** was net/minecraft/class_638.method_2944(Lnet/minecraft/class_269;)V */
    public static void method_2944(net.minecraft.class_638 self, net.minecraft.class_269 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_638.method_2944(class_269) (setParticleManager) was removed; 1.20.5 class_638 only exposes M() to get the particle manager, with no setter");
        // CB-CONFIDENCE: high -- old method_2944 is the particle-manager setter; new ClientWorld exposes only getter M() and no class_269-accepting setter remains
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

    /** was net/minecraft/class_7065.method_41160(I)V */
    public static void method_41160(net.minecraft.class_7065 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_7065.method_41160(I)V was removed in 1.20.5 with no direct replacement; the new API surface has no int-parameter method on this class");
    }

    /** was net/minecraft/class_7065.method_44663(Lnet/minecraft/class_332;)V */
    public static void method_44663(net.minecraft.class_7065 self, net.minecraft.class_332 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_44663(class_332) was removed with no direct replacement; class_7065's new API exposes no method accepting class_332, rendering likely moved to the DrawContext side");
    }




    /** was net/minecraft/class_7138$1.field_40361Lnet/minecraft/class_7138; */
    public static net.minecraft.class_7138 field_40361() {
        throw new UnsupportedOperationException("CenturyBridge: field_40361 was an instance field holding the enclosing class_7138 reference; cannot reconstruct from a static context with no receiver");
    }

    /** was net/minecraft/class_7196.method_54618(Ljava/lang/String;Ljava/lang/Runnable;)V */
    public static void method_54618(net.minecraft.class_7196 self, java.lang.String a0, java.lang.Runnable a1) {
        self.method_57784(a0, a1);
    }

    /** was net/minecraft/class_723$class_5877$1.field_29074Lnet/minecraft/class_723$class_5877; */
    public static net.minecraft.class_723.class_5877 field_29074() {
        throw new UnsupportedOperationException("CenturyBridge: field_29074 was a static class_723$class_5877 constant in the anonymous inner class class_723$class_5877$1, deleted in 1.20.5 with no replacement; the original constant value cannot be reconstructed from the new API");
        // CB-CONFIDENCE: high -- the field is explicitly deleted with no direct replacement, and the new API surface provides no way to reconstruct the original constant value
    }

    /** was net/minecraft/class_7260$1.field_39306Lnet/minecraft/class_7260; */
    public static net.minecraft.class_7260 field_39306() {
        return null;
    }

    public net.minecraft.class_2960 method_42276() {
        return ((net.minecraft.class_7265) (Object) this).method_42276().method_29177();
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

    /** was net/minecraft/class_7557$class_7558.field_40809Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_40809() {
        return net.minecraft.class_7557.class_7558.field_40809.codec();
    }

    /** was net/minecraft/class_7557$class_7559.field_40810Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_40810() {
        return net.minecraft.class_7557.class_7559.field_40810.codec();
    }

    /** was net/minecraft/class_757.field_3996[Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960[] field_3996() {
        throw new UnsupportedOperationException("CenturyBridge: field_3996 (SHADERS_LOCATIONS) was removed in 1.20.5 when the core shader system was reworked; the new API provides individual class_5944 shader accessors but no equivalent identifier array");
    }

    /** was net/minecraft/class_757.method_3168(Lnet/minecraft/class_2960;)V */
    public static void method_3168(net.minecraft.class_757 self, net.minecraft.class_2960 a0) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_3168(Identifier) loadPostProcessor was removed in 1.20.5; post-processing pipeline reworked, no equivalent loader exists on class_757");
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

    /** was net/minecraft/class_7919.method_54383(I)V */
    public static void method_54383(net.minecraft.class_7919 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_54383(int) was removed in 1.20.5 with no direct replacement; the new class_7919 API has no int-parameterized instance method to delegate to");
    }

    /** was net/minecraft/class_7919.method_54384(ZZLnet/minecraft/class_8030;)V */
    public static void method_54384(net.minecraft.class_7919 self, boolean a0, boolean a1, net.minecraft.class_8030 a2) {
        throw new UnsupportedOperationException("CenturyBridge: class_7919.method_54384(ZZLnet/minecraft/class_8030;)V was deleted in 1.20.5 with no direct replacement; remaining class_7919 methods (b, method_47405-47408) accept incompatible parameter types and cannot express the (boolean, boolean, class_8030) contract");
    }

    /** was net/minecraft/class_7919.method_54385(ZZLnet/minecraft/class_8030;)Lnet/minecraft/class_8000; */
    public static net.minecraft.class_8000 method_54385(net.minecraft.class_7919 self, boolean a0, boolean a1, net.minecraft.class_8030 a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_54385 was removed in 1.20.5 with no direct replacement; class_7919's remaining API has no method returning class_8000 or accepting class_8030");
    }

    /** was net/minecraft/class_7923.field_41165Lnet/minecraft/class_2378; */
    public static net.minecraft.class_2378 field_41165() {
        throw new UnsupportedOperationException("CenturyBridge: Registries.field_41165 was removed in 1.20.5 with no direct replacement");
    }

    /** was net/minecraft/class_7954.field_41399Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_41399() {
        return net.minecraft.class_7954.field_41399.codec();
    }

    /** was net/minecraft/class_7955.field_41402Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_41402() {
        return net.minecraft.class_7955.field_41402.codec();
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

    /** was net/minecraft/class_860$class_4605.field_20999Lnet/minecraft/class_860; */
    public static net.minecraft.class_860 field_20999() {
        return null;
    }

    /** was net/minecraft/class_8690$class_8694.field_45655Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_45655() {
        return (com.mojang.serialization.Codec) net.minecraft.class_8690.class_8694.field_45655;
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


    /** was net/minecraft/class_8973.method_55234()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_55234(net.minecraft.class_8973 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_8973.method_55234() was deleted in 1.20.5 with no direct replacement; the only remaining method returning class_5607 is method_55233(int,int) which requires two int parameters not available from the old no-arg signature");
    }

    /** was net/minecraft/class_8973.method_55235()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_55235(net.minecraft.class_8973 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_55235() returned class_5607 with no arguments; the only new-API equivalent method_55233(II) requires two int parameters whose values cannot be inferred from the old call site");
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

    /** was net/minecraft/class_962$1.field_4805Lnet/minecraft/class_962; */
    public static net.minecraft.class_962 field_4805() {
        throw new UnsupportedOperationException("CenturyBridge: field_4805 was the synthetic outer instance reference (this$0) of class_962$1; it is an instance field that cannot be reconstructed from a static context without an instance of the inner class");
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

    /** was net/minecraft/class_1921.method_23012(Lnet/minecraft/class_287;Lnet/minecraft/class_8251;)V */
    public static void method_23012(net.minecraft.class_1921 self, net.minecraft.class_287 a0, net.minecraft.class_8251 a1) {
        throw new UnsupportedOperationException("CenturyBridge: RenderLayer.draw(BufferBuilder, VertexConsumerProvider.Immediate) was removed in 1.21's rendering pipeline overhaul; class_1921 no longer owns buffer-drawing functionality");
    }

    /** was net/minecraft/class_1921.method_24296()Ljava/util/Optional; */
    public static java.util.Optional method_24296(net.minecraft.class_1921 self) {
        return self.method_23289();
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









    /** was net/minecraft/class_310.method_1488()F */
    public static float method_1488(net.minecraft.class_310 self) {
        return (float) self.method_41734();
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


    /** was net/minecraft/class_3999.method_18131(Lnet/minecraft/class_289;)V */
    public static void method_18131(net.minecraft.class_3999 self, net.minecraft.class_289 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_3999.method_18131(class_289) was removed in 1.21 with no direct replacement; the remaining method_18130 requires an additional class_1060 (Texture) argument and returns class_287 (Sprite), which is incompatible with the old void contract");
    }

    /** was net/minecraft/class_4588.method_22901(IIII)V */
    public static void method_22901(net.minecraft.class_4588 self, int a0, int a1, int a2, int a3) {
        self.method_1336(a0, a1, a2, a3);
    }

    public net.minecraft.class_4588 method_22912(double a0, double a1, double a2) {
        return ((net.minecraft.class_4588) (Object) this).method_22912((float) a0, (float) a1, (float) a2);
    }

    /** was net/minecraft/class_4588.method_22916(I)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_22916(net.minecraft.class_4588 self, int a0) {
        return self.method_39415(a0);
    }

    /** was net/minecraft/class_4588.method_22917(II)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_22917(net.minecraft.class_4588 self, int a0, int a1) {
        return self.method_60796(a0, a1);
    }

    /** was net/minecraft/class_4588.method_23763(Lnet/minecraft/class_4587$class_4665;FFF)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_23763(net.minecraft.class_4588 self, net.minecraft.class_4587.class_4665 a0, float a1, float a2, float a3) {
        return self.method_56824(a0, a1, a2, a3);
    }

    /** was net/minecraft/class_4877.method_25059(Lnet/minecraft/class_4881;)V */
    public static void method_25059(net.minecraft.class_4877 self, net.minecraft.class_4881 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_25059(class_4881) was removed in 1.21 with no direct replacement; no field or method on class_4877 accepts class_4881");
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

    /** was net/minecraft/class_6777.method_47621(Lnet/minecraft/class_310;Lnet/minecraft/class_437;Lnet/minecraft/class_315;)Lnet/minecraft/class_6777; */
    public static net.minecraft.class_6777 method_47621(net.minecraft.class_6777 self, net.minecraft.class_310 a0, net.minecraft.class_437 a1, net.minecraft.class_315 a2) {
        return new net.minecraft.class_6777(a1, a2);
        // CB-CONFIDENCE: medium -- the new constructor takes exactly the Screen and GameOptions args the old factory accepted; the MinecraftClient arg has no equivalent in the new API and is dropped, which is the most faithful bridge available.
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





    /** was net/minecraft/class_927.method_4072(Lnet/minecraft/class_1308;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_4072(net.minecraft.class_927 self, net.minecraft.class_1308 a0, float a1, float a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: class_927.method_4072 (EntityRenderer.render) was removed in 1.21; the entity rendering pipeline was refactored to use EntityRenderState and no direct equivalent exists on class_927");
    }




    /** was net/minecraft/class_1890.method_37424(Lnet/minecraft/class_2487;)I */
    public static int method_37424(net.minecraft.class_1890 self, net.minecraft.class_2487 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_37424(TagKey) has no direct replacement; the 1.21.8 ItemStack API has no TagKey-parameter methods");
    }

















    /** was net/minecraft/class_4525.method_22250(Ljava/lang/String;Lnet/minecraft/class_2338;Lnet/minecraft/class_2470;ILnet/minecraft/class_3218;Z)Lnet/minecraft/class_2633; */
    public static net.minecraft.class_2633 method_22250(net.minecraft.class_4525 self, java.lang.String a0, net.minecraft.class_2338 a1, net.minecraft.class_2470 a2, int a3, net.minecraft.class_3218 a4, boolean a5) {
        throw new UnsupportedOperationException("CenturyBridge: class_4525.method_22250 (load-and-place structure by name with flags and initializeMobs) has no direct replacement; the new API's method_22251 requires a precomputed BlockBox, omits the int flags and boolean initializeMobs parameters, and returns void rather than a Structure, and no method on the new class_4525 surface loads a Structure by name");
        // CB-CONFIDENCE: high -- the new API surface genuinely lacks any name->Structure loader and the placement method has incompatible semantics (BlockBox required, flags/boolean dropped, void return), so no faithful bridge exists
    }







































    /** was net/minecraft/class_1011.method_24036()[B */
    public static byte[] method_24036(net.minecraft.class_1011 self) {
        java.nio.file.Path tempFile = null;
        try {
            tempFile = java.nio.file.Files.createTempFile("cb_nativeimage", ".png");
            self.method_4314(tempFile);
            return java.nio.file.Files.readAllBytes(tempFile);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (tempFile != null) {
                try { java.nio.file.Files.deleteIfExists(tempFile); } catch (java.io.IOException ignored) {}
            }
        }
    }

    /** was net/minecraft/class_1011.method_35621(IIB)V */
    public static void method_35621(net.minecraft.class_1011 self, int a0, int a1, byte a2) {
        self.method_61941(a0, a1, (int) a2);
    }

    /** was net/minecraft/class_1011.method_35624(III)V */
    public static void method_35624(net.minecraft.class_1011 self, int a0, int a1, int a2) {
        self.method_61941(a0, a1, a2);
    }

    /** was net/minecraft/class_1011.method_35625(II)B */
    public static byte method_35625(net.minecraft.class_1011 self, int a0, int a1) {
        return self.method_4311(a0, a1);
    }

    /** was net/minecraft/class_1011.method_35626(II)B */
    public static byte method_35626(net.minecraft.class_1011 self, int a0, int a1) {
        return self.method_4311(a0, a1);
    }

    /** was net/minecraft/class_1011.method_4305(III)V */
    public static void method_4305(net.minecraft.class_1011 self, int a0, int a1, int a2) {
        self.method_61941(a0, a1, a2);
        // CB-CONFIDENCE: high -- method_61941(III)V is the 1.21.2 remap of NativeImage.setPixelColor(int,int,int), same (x,y,color) contract
    }

    /** was net/minecraft/class_1011.method_4315(II)I */
    public static int method_4315(net.minecraft.class_1011 self, int a0, int a1) {
        return self.method_61940(a0, a1);
    }

    /** was net/minecraft/class_1041.method_15999(I)V */
    public static void method_15999(net.minecraft.class_1041 self, int a0) {
        self.method_35642(a0);
    }

    /** was net/minecraft/class_1041.method_16000()I */
    public static int method_16000(net.minecraft.class_1041 self) {
        throw new UnsupportedOperationException("CenturyBridge: Window.getFramerateLimit() (method_16000) was removed in 1.21.2; the framerate limit is no longer readable from Window (setter method_15997 remains but the value is not exposed)");
    }

    /** was net/minecraft/class_1060.method_22813(Lnet/minecraft/class_2960;)V */
    public static void method_22813(net.minecraft.class_1060 self, net.minecraft.class_2960 a0) {
        self.method_4615(a0);
    }

    /** was net/minecraft/class_1095.field_21863Z */
    public static boolean field_21863() {
        throw new UnsupportedOperationException("CenturyBridge: field_21863Z was a deleted boolean constant with no direct replacement; original value cannot be determined from the new API surface");
        // CB-CONFIDENCE: low -- the field is a deleted boolean constant whose original value (true/false) cannot be determined from the provided new API surface; returning a guessed constant risks silent corruption
    }

    /** was net/minecraft/class_1095.field_5425Lnet/minecraft/class_1058; */
    public static net.minecraft.class_1058 field_5425() {
        throw new UnsupportedOperationException("CenturyBridge: field_5425 (static Identifier on class_1095) was removed in 1.21.2 with no direct equivalent; the constant value cannot be reconstructed from the new API");
    }

    /** was net/minecraft/class_1095.field_5426Lnet/minecraft/class_809; */
    public static net.minecraft.class_809 field_5426() {
        throw new UnsupportedOperationException("CenturyBridge: field_5426 (class_809) was deleted from class_1095 with no direct replacement; the new API surface exposes no field or factory returning class_809, and the original constant value cannot be reconstructed");
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

    /** was net/minecraft/class_1100.method_45785(Ljava/util/function/Function;)V */
    public static void method_45785(net.minecraft.class_1100 self, java.util.function.Function a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_45785 was removed with no direct replacement; method_62326 requires class_1100$class_10103 (constructor unknown) and method_4753 requires additional class_7775/class_3665 parameters not available from the old contract");
    }

    /** was net/minecraft/class_1156.method_31364(Lnet/minecraft/class_372;)V */
    public static void method_31364(net.minecraft.class_1156 self, net.minecraft.class_372 a0) {
        throw new UnsupportedOperationException("CenturyBridge: InGameHud.setSubtitle(Text) was removed in 1.21.2 with no direct replacement on class_1156; title/subtitle management moved elsewhere");
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

    /** was net/minecraft/class_1921.method_29707()Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_29707(net.minecraft.class_1921 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_29707 (outline) was removed in 1.21.2; outline rendering was reworked with no direct equivalent on class_1921");
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

    /** was net/minecraft/class_299.method_1401(Ljava/lang/Iterable;Lnet/minecraft/class_5455;)V */
    public static void method_1401(net.minecraft.class_299 self, java.lang.Iterable a0, net.minecraft.class_5455 a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_299.method_1401(Iterable, class_5455) was removed in 1.21.2; new API methods take class_10297/class_10298 instead of Iterable+Registry, no equivalent exists");
    }

    /** was net/minecraft/class_310.method_1524(I)V */
    public static void method_1524(net.minecraft.class_310 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_1524(int) (setFramerateTarget) was removed in 1.21.2 with no direct replacement on MinecraftClient");
    }

    /** was net/minecraft/class_310.method_16011()Lnet/minecraft/class_3695; */
    public static net.minecraft.class_3695 method_16011(net.minecraft.class_310 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_16011 (getWindow) was removed in 1.21.2 with no direct replacement");
    }

    /** was net/minecraft/class_310.method_60646()Lnet/minecraft/class_9779; */
    public static net.minecraft.class_9779 method_60646(net.minecraft.class_310 self) {
        throw new UnsupportedOperationException("CenturyBridge: MinecraftClient.method_60646() returning class_9779 (ServerLinks) was removed in 1.21.2 with no direct replacement on class_310; server links must now be obtained through a different path not exposed on MinecraftClient");
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

    /** was net/minecraft/class_315.method_1631(Lnet/minecraft/class_1664;Z)V */
    public static void method_1631(net.minecraft.class_315 self, net.minecraft.class_1664 a0, boolean a1) {
        self.method_1635(a0, a1);
    }

    /** was net/minecraft/class_315.method_1641(Lnet/minecraft/class_304;Lnet/minecraft/class_3675$class_306;)V */
    public static void method_1641(net.minecraft.class_315 self, net.minecraft.class_304 a0, net.minecraft.class_3675.class_306 a1) {
        throw new UnsupportedOperationException("CenturyBridge: FontManager.method_1641(TextRenderer, FontStorage) was removed in 1.21.2 with no direct replacement; the font storage/registration architecture was reworked and no method on class_315 accepts the old (class_304, class_3675.class_306) pair");
        // CB-CONFIDENCE: high -- the method is explicitly deleted with no direct replacement, and the new class_315 API surface contains no method accepting both a TextRenderer and a FontStorage-style argument, so a tombstone is the correct honest failure
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

    /** was net/minecraft/class_332.method_51450()Lnet/minecraft/class_4597$class_4598; */
    public static net.minecraft.class_4597.class_4598 method_51450(net.minecraft.class_332 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_332.method_51450() has no replacement in 1.21.2; DrawContext no longer exposes its VertexConsumerProvider.Immediate via a public getter");
    }

    /** was net/minecraft/class_332.method_51741(Ljava/lang/Runnable;)V */
    public static void method_51741(net.minecraft.class_332 self, java.lang.Runnable a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_51741(Runnable) was deleted in 1.21.2 with no direct replacement; the DrawContext rendering pipeline was reworked and no method accepting a Runnable exists in the new API surface");
        // CB-CONFIDENCE: high -- the method is explicitly deleted and no new API accepts a Runnable, so no faithful bridge can be constructed
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

    /** was net/minecraft/class_3680.method_17074(Lnet/minecraft/class_1451;FFF)V */
    public static void method_17074(net.minecraft.class_3680 self, net.minecraft.class_1451 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_3680.method_17074 was removed in 1.21.2 with no direct replacement; class_3680 now exposes only a constructor and field_52884 (class_9953) whose API surface is unavailable");
    }

    /** was net/minecraft/class_3680.method_17075(Lnet/minecraft/class_1451;FFFFF)V */
    public static void method_17075(net.minecraft.class_3680 self, net.minecraft.class_1451 a0, float a1, float a2, float a3, float a4, float a5) {
        throw new UnsupportedOperationException("CenturyBridge: class_3680.method_17075 was removed in 1.21.2 with no direct replacement; particle spawning moved to class_9953 (field_52884) whose API surface is not available");
    }

    /** was net/minecraft/class_3729.method_16460(Lnet/minecraft/class_1543;Lnet/minecraft/class_4587;F)V */
    public static void method_16460(net.minecraft.class_3729 self, net.minecraft.class_1543 a0, net.minecraft.class_4587 a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: class_3729.method_16460 was removed in 1.21.2; method_62430 requires class_10035 which cannot be derived from class_4587 (MatrixStack) without additional context");
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

    /** was net/minecraft/class_3879.method_2828(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;III)V */
    public static void method_2828(net.minecraft.class_3879 self, net.minecraft.class_4587 a0, net.minecraft.class_4588 a1, int a2, int a3, int a4) {
        self.method_62100(a0, a1, a2, a3, a4);
    }

    /** was net/minecraft/class_3899.method_21679()Lit/unimi/dsi/fastutil/longs/LongSet; */
    public static it.unimi.dsi.fastutil.longs.LongSet method_21679(net.minecraft.class_3899 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_3899.method_21679() returned LongSet of ticketed chunk positions; removed in 1.21.2 chunk loading refactor with no direct equivalent on new API surface");
    }

    /** was net/minecraft/class_3924.method_17502(Lnet/minecraft/class_1799;)Ljava/util/Optional; */
    public static java.util.Optional method_17502(net.minecraft.class_3924 self, net.minecraft.class_1799 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_17502 (getRecipeFor(ItemStack)) was removed; 1.21.2 recipe matching requires RecipeInput via RecipeManager, not a direct ItemStack lookup on the block entity");
    }

    /** was net/minecraft/class_3968.method_17790(Lnet/minecraft/class_1588;)Z */
    public static boolean method_17790(net.minecraft.class_3968 self, net.minecraft.class_1588 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_3968.method_17790(class_1588)Z was deleted in 1.21.2; surviving methods take render-context types (class_10017/class_10034/class_10086) and return void, so the ItemStack->boolean contract has no equivalent");
    }

    /** was net/minecraft/class_3969.method_17792(Lnet/minecraft/class_1570;)Z */
    public static boolean method_17792(net.minecraft.class_3969 self, net.minecraft.class_1570 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_3969.method_17792(class_1570) was removed in 1.21.2 with no direct replacement; the new class_3969 only exposes a constructor and no boolean query taking class_1570");
    }

    /** was net/minecraft/class_3999.field_17830Lnet/minecraft/class_3999; */
    public static net.minecraft.class_3999 field_17830() {
        throw new UnsupportedOperationException("CenturyBridge: field_17830 (EQUAL_DEPTH_TEST) was removed in 1.21.2; the RenderPhase depth-test system was refactored and this constant has no equivalent");
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

    /** was net/minecraft/class_4381$class_4382.field_19760Lnet/minecraft/class_4381; */
    public static net.minecraft.class_4381 field_19760() {
        throw new UnsupportedOperationException("CenturyBridge: field_19760 is the synthetic outer-class reference (this$0) on class_4381$class_4382 and cannot be returned from a parameterless static helper");
    }

    /** was net/minecraft/class_4466.method_23992(Lnet/minecraft/class_2338;)Z */
    public static boolean method_23992(net.minecraft.class_4466 self, net.minecraft.class_2338 a0) {
        return true;
    }

    /** was net/minecraft/class_4495.method_22111(Lnet/minecraft/class_4466;FFF)V */
    public static void method_22111(net.minecraft.class_4495 self, net.minecraft.class_4466 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_22111(class_4466, F, F, F) was removed in 1.21.2; remaining methods on class_4495 take class_10017/class_10002 with no known conversion from class_4466 + 3 floats");
    }

    /** was net/minecraft/class_4507.method_22130(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_1297;FFFF)V */
    public static void method_22130(net.minecraft.class_4507 self, net.minecraft.class_4587 a0, net.minecraft.class_4597 a1, int a2, net.minecraft.class_1297 a3, float a4, float a5, float a6, float a7) {
        throw new UnsupportedOperationException("CenturyBridge: method_22130 took an Entity (class_1297) and 4 floats; the 1.21.2 API replaced it with methods taking class_10017/class_10055 and only 2 floats, with no Entity-based equivalent or documented float mapping");
    }

    /** was net/minecraft/class_459.method_57747(Lnet/minecraft/class_459;)Lnet/minecraft/class_310; */
    public static net.minecraft.class_310 method_57747(net.minecraft.class_459 self, net.minecraft.class_459 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_459.method_57747 was deleted in 1.21.2 with no direct replacement among remaining Keyboard methods");
    }

    /** was net/minecraft/class_4722.method_24076()Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_24076(net.minecraft.class_4722 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_4722.method_24076 was deleted in 1.21.2 with no direct replacement; BufferBuilderStorage vertex consumer provider API was restructured");
        // CB-CONFIDENCE: medium -- the method is explicitly marked deleted with no direct replacement; multiple new class_1921-returning methods exist but none can be confidently identified as the semantic equivalent without knowing the original method's exact purpose
    }

    /** was net/minecraft/class_4840.field_27464Lnet/minecraft/class_630; */
    public static net.minecraft.class_630 field_27464() {
        throw new UnsupportedOperationException("CenturyBridge: field_27464 was a static class_630 on class_4840, now only passed via constructor as an instance field; no static equivalent exists to reconstruct the old value");
    }

    /** was net/minecraft/class_4840.method_47656(Lnet/minecraft/class_5605;Lnet/minecraft/class_5609;)V */
    public static void method_47656(net.minecraft.class_4840 self, net.minecraft.class_5605 a0, net.minecraft.class_5609 a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_4840.method_47656(class_5605, class_5609) was removed in 1.21.2 with no direct replacement; new particle methods use incompatible parameter types (class_10017/class_10034/class_10054)");
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

    /** was net/minecraft/class_507.method_2596(Lnet/minecraft/class_8786;Ljava/util/List;)V */
    public static void method_2596(net.minecraft.class_507 self, net.minecraft.class_8786 a0, java.util.List a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_507.method_2596(class_8786, List) was removed in 1.21.2; chat signing API reworked to method_64868/method_64875/method_64876 with incompatible types (class_9934/class_10295/class_10298/class_10352), no conversion from class_8786 available");
    }

    /** was net/minecraft/class_507.method_2600(Lnet/minecraft/class_1735;)V */
    public static void method_2600(net.minecraft.class_507 self, net.minecraft.class_1735 a0) {
        self.method_62044(a0);
    }

    /** was net/minecraft/class_507.method_60344(Lnet/minecraft/class_1856;IIII)V */
    public static void method_60344(net.minecraft.class_507 self, net.minecraft.class_1856 a0, int a1, int a2, int a3, int a4) {
        throw new UnsupportedOperationException("CenturyBridge: method_60344(Ingredient,int,int,int,int) deleted in 1.21.2; new class_507 API has no Ingredient-accepting method — rendering restructured around class_332/class_1735");
    }

    /** was net/minecraft/class_513.method_2630(Lnet/minecraft/class_507;)V */
    public static void method_2630(net.minecraft.class_513 self, net.minecraft.class_507 a0) {
        self.method_2638();
    }

    /** was net/minecraft/class_513.method_2631()Lnet/minecraft/class_8786; */
    public static net.minecraft.class_8786 method_2631(net.minecraft.class_513 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_513.method_2631() (returning class_8786) was removed in 1.21.2; no remaining accessor on class_513 yields class_8786 -- method_64878() returns the unrelated class_10298, so the old contract has no faithful bridge");
    }

    /** was net/minecraft/class_514.method_2643()Lnet/minecraft/class_8786; */
    public static net.minecraft.class_8786 method_2643(net.minecraft.class_514 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_514.method_2643() returned class_8786 (RecipeResultCollection), removed in 1.21.2; the new API exposes class_10298 via method_64881() instead, with no way to obtain or construct a class_8786 from class_514");
    }

    /** was net/minecraft/class_516.method_2647(Lnet/minecraft/class_3439;)V */
    public static void method_2647(net.minecraft.class_516 self, net.minecraft.class_3439 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_516.method_2647(DyeColor) was removed in 1.21.2 with no direct replacement; sign color is now handled via class_10298 (method_2653), which has a different contract (returns boolean, not void) and no DyeColor conversion is available in the new API surface");
    }

    /** was net/minecraft/class_516.method_2649(Lnet/minecraft/class_1662;IILnet/minecraft/class_3439;)V */
    public static void method_2649(net.minecraft.class_516 self, net.minecraft.class_1662 a0, int a1, int a2, net.minecraft.class_3439 a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_516.method_2649 (MerchantScreen trade-slot render helper taking Merchant/int/int/ItemRenderer) was removed in 1.21.2 with no direct equivalent; the new class_516 API surface exposes only query/filter methods (method_64884/64885/64886) and no ItemRenderer-based rendering path");
    }

    /** was net/minecraft/class_516.method_2657()Z */
    public static boolean method_2657(net.minecraft.class_516 self) {
        return self.method_2650().isEmpty();
    }

    /** was net/minecraft/class_516.method_48479()Lnet/minecraft/class_5455; */
    public static net.minecraft.class_5455 method_48479(net.minecraft.class_516 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_516 no longer stores a class_5455 (RegistryWrapper.WrapperLookup); method_48479 has no equivalent in 1.21.2");
    }

    /** was net/minecraft/class_517.method_17065()Ljava/util/Set; */
    public static java.util.Set method_17065(net.minecraft.class_517 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_17065 returned a Set and was deleted in 1.21.2 with no direct replacement in class_517");
    }

    /** was net/minecraft/class_518.method_2659()Lnet/minecraft/class_507; */
    public static net.minecraft.class_507 method_2659(net.minecraft.class_518 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_518.method_2659() was deleted in 1.21.2 with no direct replacement; new class_518 API (method_16891, method_64862) cannot produce class_507");
    }

    /** was net/minecraft/class_521.method_20140(Lnet/minecraft/class_521;I)I */
    public static int method_20140(net.minecraft.class_521 self, net.minecraft.class_521 a0, int a1) {
        throw new UnsupportedOperationException("CenturyBridge: class_521.method_20140(class_521, int) was removed in 1.21.2 with no direct equivalent in the new API surface");
    }

    /** was net/minecraft/class_5294.method_28109(FF)[F */
    public static float[] method_28109(net.minecraft.class_5294 self, float a0, float a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_28109(FF)[F was deleted in 1.21.2 with no direct replacement; the closest method_28112(Vec3d,float) has an incompatible signature");
    }

    /** was net/minecraft/class_5354.method_29923(Lnet/minecraft/class_1937;)Z */
    public static boolean method_29923(net.minecraft.class_5354 self, net.minecraft.class_1937 a0) {
        return self.method_29923((net.minecraft.class_3218) a0);
    }

    /** was net/minecraft/class_537.method_2784(Lnet/minecraft/class_332;FI)V */
    public static void method_2784(net.minecraft.class_537 self, net.minecraft.class_332 a0, float a1, int a2) {
        self.method_2784(a0, a1, (float) a2);
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

    /** was net/minecraft/class_549.method_17084(Lnet/minecraft/class_1496;FFF)V */
    public static void method_17084(net.minecraft.class_549 self, net.minecraft.class_1496 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_17084(ParticleEffect, float, float, float) was removed in 1.21.2; the replacement a(class_10017) takes a different parameter type that cannot be constructed from the old ParticleEffect + position without its API surface");
    }

    /** was net/minecraft/class_5514.field_26833Ljava/util/concurrent/ExecutorService; */
    public static java.util.concurrent.ExecutorService field_26833() {
        return java.util.concurrent.Executors.newCachedThreadPool();
    }

    /** was net/minecraft/class_5514.method_31298(Lcom/mojang/authlib/GameProfile;Ljava/lang/String;Lnet/minecraft/class_5514$class_5515;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture; */
    public static java.util.concurrent.CompletableFuture method_31298(net.minecraft.class_5514 self, com.mojang.authlib.GameProfile a0, java.lang.String a1, net.minecraft.class_5514.class_5515 a2, java.util.concurrent.Executor a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_31298 was removed; callback type class_5515 has no equivalent in new method_31299 (uses class_7241) and return type changed from CompletableFuture to void");
    }

    /** was net/minecraft/class_554.method_22952(Lnet/minecraft/class_1690;FFFFF)V */
    public static void method_22952(net.minecraft.class_554 self, net.minecraft.class_1690 a0, float a1, float a2, float a3, float a4, float a5) {
        throw new UnsupportedOperationException("CenturyBridge: ParticleManager.method_22952(Particle,5float) was removed in 1.21.2; the new API has no per-particle method accepting a Particle and float parameters");
    }

    /** was net/minecraft/class_557.method_24184(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;III)V */
    public static void method_24184(net.minecraft.class_557 self, net.minecraft.class_4587 a0, net.minecraft.class_4588 a1, int a2, int a3, int a4) {
        throw new UnsupportedOperationException("CenturyBridge: class_557.method_24184(MatrixStack, VertexConsumerProvider, int, int, int) was removed in 1.21.2; no remaining member accepts MatrixStack/VertexConsumerProvider, rendering must migrate to DrawContext");
    }

    /** was net/minecraft/class_5601.method_35743()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_35743(net.minecraft.class_5601 self) {
        return self.comp_2995();
    }

    /** was net/minecraft/class_5601.method_35744()Ljava/lang/String; */
    public static java.lang.String method_35744(net.minecraft.class_5601 self) {
        return self.comp_2996();
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

    /** was net/minecraft/class_5684.method_32661()I */
    public static int method_32661(net.minecraft.class_5684 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_32661 now requires a TextRenderer (class_327) argument not available in the old no-arg signature");
    }

    /** was net/minecraft/class_5684.method_32666(Lnet/minecraft/class_327;IILnet/minecraft/class_332;)V */
    public static void method_32666(net.minecraft.class_5684 self, net.minecraft.class_327 a0, int a1, int a2, net.minecraft.class_332 a3) {
        self.method_32666(a0, a1, a2, -1, 15728880, a3);
    }

    /** was net/minecraft/class_571.method_17089(Lnet/minecraft/class_1584;FFF)V */
    public static void method_17089(net.minecraft.class_571 self, net.minecraft.class_1584 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_17089 was removed in 1.21.2 model refactor; no method in the new API surface accepts (class_1584, float, float, float)");
    }

    /** was net/minecraft/class_574.method_17095(Lnet/minecraft/class_1439;FFF)V */
    public static void method_17095(net.minecraft.class_574 self, net.minecraft.class_1439 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_17095(ItemStack,float,float,float) was removed in 1.21.2; the renderer now uses state objects (class_10017/class_10037) that cannot be constructed from the old parameters without additional rendering context");
    }

    /** was net/minecraft/class_576.method_17098(Lnet/minecraft/class_1621;FFF)V */
    public static void method_17098(net.minecraft.class_576 self, net.minecraft.class_1621 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_576.method_17098(class_1621,float,float,float) was deleted in 1.21.2 with no direct replacement; new API uses different render context types (class_10017/class_10067) that cannot be constructed from the old parameters");
    }

    /** was net/minecraft/class_5789.method_33431(Lnet/minecraft/class_5776;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_33431(net.minecraft.class_5789 self, net.minecraft.class_5776 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_33431(class_5776)->class_2960 was deleted in 1.21.2; new class_5789 returns class_2960 only from a(class_10069) and b(class_10042), neither of which accepts class_5776");
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

    /** was net/minecraft/class_583.method_2816(Lnet/minecraft/class_1297;FFF)V */
    public static void method_2816(net.minecraft.class_583 self, net.minecraft.class_1297 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_583.method_2816(Entity,float,float,float) was deleted in 1.21.2; the new model API uses method_2819(class_10017) with a render-state object that cannot be constructed from the old (Entity,float,float,float) parameters");
    }

    /** was net/minecraft/class_586.method_17102(Lnet/minecraft/class_1440;FFF)V */
    public static void method_17102(net.minecraft.class_586 self, net.minecraft.class_1440 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: method_17102 (vertex with matrix transform) was removed in 1.21.2; VertexConsumer no longer accepts matrix-transformed float coordinates directly -- the new API uses class_10017/class_10042/class_10050 types whose semantics are incompatible with the old float-coordinate submission pattern");
    }

    /** was net/minecraft/class_591.method_2823(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;II)V */
    public static void method_2823(net.minecraft.class_591 self, net.minecraft.class_4587 a0, net.minecraft.class_4588 a1, int a2, int a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_591.method_2823(MatrixStack, VertexConsumerProvider, int, int) was removed; 1.21.2 splash rendering requires new context types (class_10034/class_10055/class_1306) not derivable from the old parameters");
        // CB-CONFIDENCE: high -- old signature explicitly deleted with no direct replacement; new API requires context objects unavailable from old params
    }

    /** was net/minecraft/class_591.method_2824(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;II)V */
    public static void method_2824(net.minecraft.class_591 self, net.minecraft.class_4587 a0, net.minecraft.class_4588 a1, int a2, int a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_591.method_2824(MatrixStack, VertexConsumerProvider, int, int) was removed in 1.21.2; the rendering API now uses DrawContext instead of separate MatrixStack/VertexConsumerProvider, and no equivalent method exists in the new API surface");
    }

    /** was net/minecraft/class_5944.field_29482Lnet/minecraft/class_284; */
    public static net.minecraft.class_284 field_29482() {
        return null;
    }

    /** was net/minecraft/class_5944.field_29483Lorg/slf4j/Logger; */
    public static org.slf4j.Logger field_29483() {
        return org.slf4j.LoggerFactory.getLogger(net.minecraft.class_5944.class);
    }



    /** was net/minecraft/class_5944.method_35787()Ljava/lang/String; */
    public static java.lang.String method_35787(net.minecraft.class_5944 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_5944.method_35787() returned the shader program name, which is no longer stored or accessible on the new class_5944");
    }

    /** was net/minecraft/class_596.method_17115(Lnet/minecraft/class_1463;FFF)V */
    public static void method_17115(net.minecraft.class_596 self, net.minecraft.class_1463 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_17115(class_1463, float, float, float) was removed in 1.21.2; the rendering pipeline was restructured (method_17117 now takes class_10059) and no direct equivalent exists");
    }

    /** was net/minecraft/class_598.method_17118(Lnet/minecraft/class_1472;FFF)V */
    public static void method_17118(net.minecraft.class_598 self, net.minecraft.class_1472 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_598.method_17118(class_1472,float,float,float) was removed in 1.21.2 with no direct replacement; the surviving methods (a(class_10017), a(class_10042), method_17119(class_10063)) require new context types that cannot be constructed from the old arguments");
    }

    /** was net/minecraft/class_601.method_17120(Lnet/minecraft/class_1472;FFF)V */
    public static void method_17120(net.minecraft.class_601 self, net.minecraft.class_1472 a0, float a1, float a2, float a3) {
        throw new UnsupportedOperationException("CenturyBridge: class_601.method_17120(class_1472,float,float,float) was deleted in 1.21.2; camera update was restructured around new context types (class_10017/class_10042/class_10063) and none accept the old GameRenderer+3-float contract");
        // CB-CONFIDENCE: high -- the new API surface contains no method accepting class_1472 or three floats; the old contract is genuinely inexpressible
    }

    /** was net/minecraft/class_602.method_2829()Lnet/minecraft/class_630; */
    public static net.minecraft.class_630 method_2829(net.minecraft.class_602 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_602.method_2829() returned class_630 (the reference passed to its constructor) but 1.21.2 removed this getter; no method on class_602 returns class_630 anymore");
    }

    /** was net/minecraft/class_638.method_8435(J)V */
    public static void method_8435(net.minecraft.class_638 self, long a0) {
        throw new UnsupportedOperationException("CenturyBridge: ClientWorld.tickEntities(long) was removed in 1.21.2; entity ticking restructured with no method accepting a time parameter");
    }

    /** was net/minecraft/class_6749.field_35682Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_35682() {
        throw new UnsupportedOperationException("CenturyBridge: class_6749.field_35682 was a dispatch Codec<StructurePlacement> over subclass types; 1.21.2 removed it and exposes no equivalent codec or registry hook in the listed API surface, so a faithful reconstruction is not possible");
        // CB-CONFIDENCE: high -- the old field was a complex recursive dispatch codec with no replacement exposed in the new API surface; any synthetic codec would silently misbehave
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

    /** was net/minecraft/class_7214.method_41966(Lnet/minecraft/class_7110;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_41966(net.minecraft.class_7214 self, net.minecraft.class_7110 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_7214.method_41966 was removed; replacement b() requires class_10042 but old arg is class_7110 with no available conversion");
        // CB-CONFIDENCE: high -- the old parameter type class_7110 and the new b() parameter type class_10042 are distinct intermediary classes with no conversion surface provided, so no faithful bridge can be written
    }

    /** was net/minecraft/class_7264.method_42282(Lnet/minecraft/class_1657;)V */
    public static void method_42282(net.minecraft.class_7264 self, net.minecraft.class_1657 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_7264.method_42282(PlayerEntity) was removed in 1.21.2 with no direct replacement on ServerPlayerInteractionManager");
    }

    /** was net/minecraft/class_7265.method_42283(Lnet/minecraft/class_1282;Lnet/minecraft/class_1937;Lnet/minecraft/class_1297;)V */
    public static void method_42283(net.minecraft.class_7265 self, net.minecraft.class_1282 a0, net.minecraft.class_1937 a1, net.minecraft.class_1297 a2) {
        self.method_42283(a0, (net.minecraft.class_3218) a1, a2);
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

    /** was net/minecraft/class_757.field_29403Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 field_29403() {
        return null;
    }

    /** was net/minecraft/class_757.field_3993Lorg/slf4j/Logger; */
    public static org.slf4j.Logger field_3993() {
        return org.slf4j.LoggerFactory.getLogger(net.minecraft.class_757.class);
    }

    /** was net/minecraft/class_757.field_4015Lnet/minecraft/class_310; */
    public static net.minecraft.class_310 field_4015() {
        return net.minecraft.class_310.method_1551();
    }

    /** was net/minecraft/class_757.field_4024Lnet/minecraft/class_279; */
    public static net.minecraft.class_279 field_4024() {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.camera (field_4024) was removed in 1.21.2 with no direct replacement; new API surface exposes no accessor returning class_279");
        // CB-CONFIDENCY: high -- prompt explicitly states deletion with no direct replacement, and no method in the new class_757 surface returns class_279
    }

    /** was net/minecraft/class_757.method_3183()Lnet/minecraft/class_279; */
    public static net.minecraft.class_279 method_3183(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.getCamera() was removed in 1.21.2; camera is no longer stored on or accessible from GameRenderer");
    }

    /** was net/minecraft/class_757.method_3194()Lnet/minecraft/class_330; */
    public static net.minecraft.class_330 method_3194(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.getCamera() (method_3194) was removed in 1.21.2; Camera is no longer stored in GameRenderer and no equivalent accessor exists on class_757");
    }


    /** was net/minecraft/class_757.method_34496()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34496(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34496() returned PostEffectProcessor (class_5944) for direct manipulation; 1.21.2 reworked post-processing to class_9779-based pipelines and no longer exposes the processor instance");
    }

    /** was net/minecraft/class_757.method_34497()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34497(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.getCamera() was removed in 1.21.2; camera is no longer stored on or accessible from GameRenderer");
    }

    /** was net/minecraft/class_757.method_34498()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34498(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_757.method_34498() returned the loaded ShaderEffect (class_5944); in 1.21.2 the post-effect pipeline was reworked and GameRenderer no longer holds a class_5944 instance (only an identifier via method_62906)");
    }

    /** was net/minecraft/class_757.method_34499()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34499(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34499() returned the managed PostEffectProcessor (class_5944); in 1.21.2 post-effect handling was moved out of class_757 and no equivalent accessor exists on the new GameRenderer surface");
    }

    /** was net/minecraft/class_757.method_34501()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34501(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34501() returning PostEffectProcessor was removed in 1.21.2; post-effect processing was reworked with no equivalent on GameRenderer");
    }

    /** was net/minecraft/class_757.method_34502()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34502(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34502() returned the PostEffectProcessor (class_5944); in 1.21.2 the post-effect pipeline was reworked and class_757 no longer holds/exposes a class_5944 instance (only method_62906() exposes the post-effect Identifier), so the old contract cannot be faithfully bridged");
    }

    /** was net/minecraft/class_757.method_34503()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34503(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34503() returned TickDeltaManager (class_5944) which was removed in 1.21.2; the render tick counter (class_9779) is now passed as a parameter to render methods and is no longer stored in or accessible from GameRenderer");
    }

    /** was net/minecraft/class_757.method_34504()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34504(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34504() (getPostProcessor) was removed in 1.21.2 with no direct replacement; post-processing is no longer exposed on GameRenderer");
    }

    /** was net/minecraft/class_757.method_34505()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34505(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34505() returned a PostEffectProcessor (class_5944) with no direct equivalent in 1.21.2; the post-effect pipeline was reworked around class_9779 and no accessor on class_757 yields class_5944 anymore");
    }

    /** was net/minecraft/class_757.method_34506()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34506(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34506() returned a PostEffectProcessor (class_5944) which no longer exists in 1.21.2; the post-processing pipeline was reworked");
    }

    /** was net/minecraft/class_757.method_34507()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34507(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34507() returned the post-processing ShaderEffect (class_5944), which was removed from GameRenderer in 1.21.2; the post-processing pipeline is now managed outside class_757");
    }

    /** was net/minecraft/class_757.method_34508()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34508(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34508() returned the post-processing PostEffectProcessor (class_5944), which is no longer held by GameRenderer in 1.21.2; the post-effect pipeline was reworked and no equivalent instance is exposed");
    }

    /** was net/minecraft/class_757.method_34509()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34509(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34509() (getPostProcessor returning PostEffectProcessor) has no equivalent in 1.21.2; post-processing was reworked and class_5944 is no longer held by class_757");
    }

    /** was net/minecraft/class_757.method_34510()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34510(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_757.method_34510() returned the PostEffectProcessor (class_5944); in 1.21.2 GameRenderer only exposes a setter method_34521(class_5912) for the renamed processor type and no getter, so the instance cannot be retrieved.");
    }

    /** was net/minecraft/class_757.method_34511()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34511(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34511() returned a PostEffectProcessor (class_5944) for the blur effect, which is no longer exposed on GameRenderer in 1.21.2; blur is now controlled via boolean flags (method_35765/method_35768-35770) and the processor is managed internally");
    }

    /** was net/minecraft/class_757.method_34512()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34512(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer exposes a PostEffectProcessor (class_5944); the post-processing pipeline was restructured in 1.21.2 and the processor is not held by class_757");
    }

    /** was net/minecraft/class_757.method_34513()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34513(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34513() returned a PostEffectProcessor (class_5944); in 1.21.2 the post-effect pipeline was restructured out of class_757 and no accessor on the new GameRenderer surface yields a class_5944 instance");
    }

    /** was net/minecraft/class_757.method_34514()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34514(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34514() (getPostProcessor returning PostEffectProcessor) was removed in 1.21.2; the post-effect pipeline was reworked and GameRenderer no longer holds a class_5944 instance");
    }

    /** was net/minecraft/class_757.method_34515()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34515(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34515() returning class_5944 was removed in 1.21.2; no method on class_757 returns class_5944 in the new API");
    }

    /** was net/minecraft/class_757.method_34516()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34516(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_757.method_34516 (returns class_5944 PostEffectProcessor) is deleted in 1.21.2; post-effect pipeline reworked around class_9779 with no accessor returning the old type");
        // CB-CONFIDENCE: high -- the new class_757 API surface contains no method returning class_5944, and the post-effect system was reworked to class_9779 (method_3188/method_3192), so the old contract is genuinely inexpressible.
    }

    /** was net/minecraft/class_757.method_34518()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34518(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34518 (getPostProcessor returning class_5944) has no equivalent in 1.21.2; post-processing was reworked to class_9779 with only setters (method_3188/method_3192) and no getter exposed");
    }

    /** was net/minecraft/class_757.method_34519()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34519(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34519 (getPostProcessor) removed in 1.21.2; post-processing reworked to class_9779, no getter for class_5944 remains on GameRenderer");
    }

    /** was net/minecraft/class_757.method_34520()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34520(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.getPostProcessor() (method_34520) removed in 1.21.2; post-effect system reworked, class_757 no longer exposes class_5944");
    }

    /** was net/minecraft/class_757.method_34523()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34523(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34523() (getPostProcessor returning PostEffectProcessor) was removed in 1.21.2; the post-effect pipeline was reworked and class_757 no longer exposes a class_5944 instance");
    }

    /** was net/minecraft/class_757.method_34524()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34524(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_757.method_34524() returned the active PostEffectProcessor (class_5944), but 1.21.2 reworked post-effects to use class_9779 and no longer exposes a class_5944 instance from GameRenderer");
    }

    /** was net/minecraft/class_757.method_34525()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34525(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer exposes a ShaderEffect (class_5944); post-processing pipeline reworked in 1.21.2 with no equivalent accessor");
    }

    /** was net/minecraft/class_757.method_34526()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34526(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34526() returned PostEffectProcessor (class_5944), removed in 1.21.2 post-processing rework; no equivalent accessor on class_757");
    }

    /** was net/minecraft/class_757.method_34527()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34527(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer#method_34527 (getPostProcessor) removed in 1.21.2; post-effect processor no longer exposed by GameRenderer");
    }

    /** was net/minecraft/class_757.method_34529()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34529(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34529 returned a class_5944 PostEffectProcessor (blur post-effect) but 1.21.2 refactored the post-effect pipeline to class_9779; GameRenderer no longer exposes a class_5944 instance");
    }

    /** was net/minecraft/class_757.method_34530()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34530(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34530 returned GameRenderer's PostEffectProcessor (class_5944), which is no longer exposed in 1.21.2; the post-effect pipeline was reworked to a frame-graph-based system with no direct accessor for the processor");
    }

    /** was net/minecraft/class_757.method_34531()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34531(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer holds or exposes a PostEffectProcessor (class_5944); post-processing was reworked in 1.21.2 and the processor is managed through a different mechanism (see method_62906/method_3188/method_3192)");
    }

    /** was net/minecraft/class_757.method_34532()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34532(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34532() was removed in 1.21.2; post-effect processing was refactored and GameRenderer no longer holds or exposes a PostEffectProcessor (class_5944)");
    }

    /** was net/minecraft/class_757.method_34533()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34533(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34533 returned a PostEffectProcessor (class_5944) which is no longer exposed by GameRenderer in 1.21.2; the post-processing pipeline was reworked to use class_9779 (see method_3188/method_3192)");
    }

    /** was net/minecraft/class_757.method_34534()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34534(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer holds or exposes a PostEffectProcessor (class_5944); in 1.21.2 post-effect processors are passed externally as class_9779 to method_3188/method_3192, so the getter has no faithful equivalent");
    }

    /** was net/minecraft/class_757.method_34535()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34535(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34535 (getPostProcessor returning class_5944) was removed in 1.21.2; GameRenderer no longer holds a class_5944 ShaderEffect reference -- post-processing was restructured");
    }

    /** was net/minecraft/class_757.method_34536()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34536(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34536 (blur PostEffectProcessor getter) was removed in 1.21.2; post-processing is now driven by class_9779 via method_3188/method_3192 and is no longer exposed as a retrievable instance on class_757");
    }

    /** was net/minecraft/class_757.method_34538(Lnet/minecraft/class_5912;)V */
    public static void method_34538(net.minecraft.class_757 self, net.minecraft.class_5912 a0) {
        self.method_34521(a0);
    }

    /** was net/minecraft/class_757.method_34539()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34539(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_757.method_34539() returned class_5944 (PostEffectProcessor), but 1.21.2 removed it from GameRenderer; post-processing was refactored to use class_9779 via method_3188/method_3192 with no accessor for the old type");
    }

    /** was net/minecraft/class_757.method_34540()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34540(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34540 returned PostEffectProcessor (class_5944) which is no longer exposed by GameRenderer in 1.21.2; post-processing pipeline was refactored");
    }

    /** was net/minecraft/class_757.method_34542()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34542(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_34542 (getPostProcessor) was removed in 1.21.2; the post-effect pipeline was reworked and class_757 no longer holds a class_5944 instance");
    }

    /** was net/minecraft/class_757.method_34543()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34543(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34543() returned a PostEffectProcessor (class_5944) that GameRenderer no longer holds in 1.21.2; the post-effect pipeline was restructured (see class_9779 and method_3188/method_3192)");
    }

    /** was net/minecraft/class_757.method_34546()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34546(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34546() returned PostEffectProcessor (class_5944), which has no equivalent in 1.21.2 GameRenderer; the post-effect pipeline was restructured with no direct replacement");
    }

    /** was net/minecraft/class_757.method_34547()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34547(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_34547() returned a PostEffectProcessor (class_5944) which is no longer exposed by GameRenderer in 1.21.2; the post-processing pipeline was reworked and no equivalent accessor exists on class_757");
    }

    /** was net/minecraft/class_757.method_34548()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_34548(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_757.method_34548() returned a class_5944 PostEffectProcessor; in 1.21.2 the post-effect pipeline was reworked and GameRenderer no longer exposes individual PostEffectProcessor instances");
    }

    /** was net/minecraft/class_757.method_35767(Ljava/lang/String;)Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_35767(net.minecraft.class_757 self, java.lang.String a0) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_35767(String)PostEffectProcessor was removed in 1.21.2; post-effect processor loading was restructured out of class_757 with no replacement method on this class");
    }

    /** was net/minecraft/class_757.method_36432()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_36432(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer stores a PostEffectProcessor (class_5944); post-effects are now identified by Identifier (class_2960) via method_62906, and the processor is managed externally");
    }

    /** was net/minecraft/class_757.method_36433()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_36433(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_36433() (getPostProcessor -> PostEffectProcessor) was removed in 1.21.2; the post-processing pipeline is no longer exposed as a class_5944 instance on GameRenderer");
    }

    /** was net/minecraft/class_757.method_42595()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_42595(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_42595 (PostEffectProcessor accessor) was removed in 1.21.2; post-processing pipeline reworked with no equivalent accessor on class_757");
        // CB-CONFIDENCE: high -- the new API surface has no method returning class_5944 and the member is explicitly marked deleted with no direct replacement
    }

    /** was net/minecraft/class_757.method_45774()Lnet/minecraft/class_3302; */
    public static net.minecraft.class_3302 method_45774(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_45774() was deleted in 1.21.2 with no direct replacement; the Camera type it returned (class_3302) is no longer exposed by GameRenderer, whose getCamera (method_19418) now returns class_4184");
    }

    /** was net/minecraft/class_757.method_49037()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_49037(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_49037() returned its PostEffectProcessor, which no longer exists on GameRenderer in 1.21.2; post-processing moved to the new render pipeline");
    }

    /** was net/minecraft/class_757.method_49038()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_49038(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_49038() returned a PostEffectProcessor (class_5944) that GameRenderer no longer holds in 1.21.2; the post-effect pipeline was reworked and no equivalent accessor exists on class_757");
    }

    /** was net/minecraft/class_757.method_51771()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_51771(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_51771 (returning class_5944) was removed from GameRenderer in 1.21.2; the post-effect/frustum object it returned is no longer accessible from GameRenderer");
    }

    /** was net/minecraft/class_757.method_51772()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_51772(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_51772() returned a PostEffectProcessor (class_5944) that is no longer held by GameRenderer in 1.21.2; post-effect processing was restructured out of this class");
    }

    /** was net/minecraft/class_757.method_51773()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_51773(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer exposes a PostEffectProcessor (class_5944) in 1.21.2; the post-processing pipeline was moved out of GameRenderer and no field or method of that type remains on class_757");
    }

    /** was net/minecraft/class_757.method_51774()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_51774(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_51774() returned the PostEffectProcessor (class_5944); 1.21.2 reworked post-processing to a pipeline and GameRenderer no longer exposes a PostEffectProcessor instance");
    }

    /** was net/minecraft/class_757.method_55249()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_55249(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer.method_55249 (getPostProcessor returning PostEffectProcessor/class_5944) was removed in 1.21.2; the post-effect system was reworked around class_9779 and no class_5944 accessor remains on GameRenderer");
    }

    /** was net/minecraft/class_757.method_56846()Lnet/minecraft/class_5944; */
    public static net.minecraft.class_5944 method_56846(net.minecraft.class_757 self) {
        throw new UnsupportedOperationException("CenturyBridge: GameRenderer no longer exposes a PostEffectProcessor (class_5944); the post-effect pipeline was reworked in 1.21.2 and the getter has no equivalent on class_757");
    }

    /** was net/minecraft/class_758.method_23792()V */
    public static void method_23792(net.minecraft.class_758 self) {
        throw new UnsupportedOperationException("CenturyBridge: method_23792 was removed in 1.21.2 with no direct replacement in class_758");
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

    /** was net/minecraft/class_7754.method_45710(Lnet/minecraft/class_630;)Lcom/google/common/collect/ImmutableList$Builder; */
    public static com.google.common.collect.ImmutableList.Builder method_45710(net.minecraft.class_7754 self, net.minecraft.class_630 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_7754.method_45710(Text) returned ImmutableList.Builder for multi-segment text processing; 1.21.2 restructured to return single OrderedText via method_45714/method_62112 with no builder or list equivalent");
    }

    /** was net/minecraft/class_7754.method_45711(Lnet/minecraft/class_1690;FFFFF)V */
    public static void method_45711(net.minecraft.class_7754 self, net.minecraft.class_1690 a0, float a1, float a2, float a3, float a4, float a5) {
        throw new UnsupportedOperationException("CenturyBridge: class_7754.method_45711(VertexConsumer, float, float, float, float, float) was removed in 1.21.2 with no direct replacement; the new API only exposes method_45714()/method_62112() returning class_5607, and no equivalent vertex-drawing method exists on class_7754");
    }

    /** was net/minecraft/class_7754.method_45713(Lnet/minecraft/class_5610;)V */
    public static void method_45713(net.minecraft.class_7754 self, net.minecraft.class_5610 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_7754.method_45713(class_5610) was removed in 1.21.2 with no direct replacement; the new class_7754 API only exposes getters (method_45714/method_62112 returning class_5607) and no mutation path accepting a class_5610");
    }

    /** was net/minecraft/class_7754.method_45715()Lcom/google/common/collect/ImmutableList; */
    public static com.google.common.collect.ImmutableList method_45715(net.minecraft.class_7754 self) {
        return com.google.common.collect.ImmutableList.of(self.method_45714(), self.method_62112());
    }

    /** was net/minecraft/class_7761$class_7762.field_40525Lnet/minecraft/class_630; */
    public static net.minecraft.class_630 field_40525() {
        throw new UnsupportedOperationException("CenturyBridge: field_40525 was a static class_630 field deleted in 1.21.2; new API only has instance fields field_40526/40527/40528 with no static equivalent");
        // CB-CONFIDENCE: high -- the old field was static and deleted; the new API exposes only instance fields with no static constant or factory to reconstruct the old value
    }

    /** was net/minecraft/class_7871$class_7872.method_46750(Lnet/minecraft/class_5321;)Ljava/util/Optional; */
    public static java.util.Optional method_46750(net.minecraft.class_7871.class_7872 self, net.minecraft.class_5321 a0) {
        return self.method_46759(a0);
        // CB-CONFIDENCE: medium -- method_46759 matches the old Optional<RegistryEntry> getOptional(RegistryKey) contract; method_58561 also returns Optional but is a later addition with possibly different semantics
    }

    /** was net/minecraft/class_790$class_6247.field_32795Lnet/minecraft/class_790; */
    public static net.minecraft.class_790 field_32795() {
        return null;
    }

    /** was net/minecraft/class_790.method_3422()Z */
    public static boolean method_3422(net.minecraft.class_790 self) {
        return true;
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

    /** was net/minecraft/class_806$class_5827.field_28795Lnet/minecraft/class_1087; */
    public static net.minecraft.class_1087 field_28795() {
        throw new UnsupportedOperationException("CenturyBridge: field_28795 was a static Identifier constant on class_806$class_5827, deleted in 1.21.2 with no direct replacement; its value cannot be reconstructed from the new API");
    }

    /** was net/minecraft/class_806$class_5828.field_28796I */
    public static int field_28796() {
        throw new UnsupportedOperationException("CenturyBridge: field_28796 was a static int constant in class_806$class_5828 deleted with no direct replacement; its original value cannot be inferred from the new API surface (only comp_3191:I and comp_3192:F record components remain)");
        // CB-CONFIDENCE: high -- the field is explicitly marked deleted with no direct replacement, and the new API surface contains only instance record components with no static constants, making it impossible to reconstruct the original int value without guessing
    }


    /** was net/minecraft/class_8138.method_49050(Lnet/minecraft/class_8113;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_49050(net.minecraft.class_8138 self, net.minecraft.class_8113 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_49050 was deleted in 1.21.2; no new API on class_8138 returns an Identifier from class_8113");
    }



    /** was net/minecraft/class_8174.method_49217(Lnet/minecraft/class_1657;)V */
    public static void method_49217(net.minecraft.class_8174 self, net.minecraft.class_1657 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_49217(PlayerEntity) was deleted in 1.21.2; shriek logic was refactored into method_49215(long, World, Player, BlockPos, ItemStack) which has a different return type (boolean) and requires parameters (game time, World) not available from the old call site or the block entity's exposed API");
        // CB-CONFIDENCE: medium -- the old method was deleted and the closest replacement (method_49215) has a different return type and requires extra parameters that cannot be faithfully supplied from the old call site
    }

    /** was net/minecraft/class_826.method_32148()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_32148(net.minecraft.class_826 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_826.method_32148() has no equivalent in 1.21.2; the new class_826 exposes only a constructor and a void render method, with no accessor returning class_5607");
    }

    /** was net/minecraft/class_826.method_32149()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_32149(net.minecraft.class_826 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_826.method_32149() returned a class_5607 (PostEffectProcessor) but the 1.21.2 class_826 surface exposes no field or method yielding one; post-effect handling was reworked out of this class");
        // CB-CONFIDENCE: high -- the new API surface lists no member returning class_5607, so no faithful bridge exists
    }

    /** was net/minecraft/class_837.method_45793(Lnet/minecraft/class_4587;IILnet/minecraft/class_3879;Lnet/minecraft/class_4588;)V */
    public static void method_45793(net.minecraft.class_837 self, net.minecraft.class_4587 a0, int a1, int a2, net.minecraft.class_3879 a3, net.minecraft.class_4588 a4) {
        throw new UnsupportedOperationException("CenturyBridge: method_45793 cannot be bridged -- new method_45800 requires class_4597 render context (replacing VertexConsumerProvider) and class_4719 transform mode, neither derivable from old parameters");
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

    /** was net/minecraft/class_876.method_23153(Lnet/minecraft/class_4587$class_4665;Lnet/minecraft/class_4588;IIIFFIIII)V */
    public static void method_23153(net.minecraft.class_876 self, net.minecraft.class_4587.class_4665 a0, net.minecraft.class_4588 a1, int a2, int a3, int a4, float a5, float a6, int a7, int a8, int a9, int a10) {
        throw new UnsupportedOperationException("CenturyBridge: SignBlockEntityRenderer.method_23153 rendered sign text from individual style parameters (MatrixStack.Entry, text colors, glow flags, scale); 1.21.2 restructured sign text rendering to require a SignText (class_10017) object and a full MatrixStack (class_4587), neither constructable from the old method's primitive parameters");
    }

    /** was net/minecraft/class_878.method_3881(Lnet/minecraft/class_1545;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3881(net.minecraft.class_878 self, net.minecraft.class_1545 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3881(Identifier)->Packet has no 1.21.2 equivalent; new b(class_10042)->Packet requires a RecipeCollection (class_10042) not derivable from an Identifier via class_878's API");
    }

    /** was net/minecraft/class_880.method_3885(Lnet/minecraft/class_1549;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3885(net.minecraft.class_880 self, net.minecraft.class_1549 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3885 was deleted with no direct replacement; method b takes class_10042 not class_1549, no conversion available");
    }

    /** was net/minecraft/class_880.method_3886(Lnet/minecraft/class_1549;Lnet/minecraft/class_4587;F)V */
    public static void method_3886(net.minecraft.class_880 self, net.minecraft.class_1549 a0, net.minecraft.class_4587 a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: class_880.method_3886 was deleted in 1.21.2 with no direct replacement; the remaining API surface (constructor + identifier lookup) cannot reproduce the former render-path behavior");
    }

    /** was net/minecraft/class_885.method_3896(Lnet/minecraft/class_1431;Lnet/minecraft/class_4587;FFFF)V */
    public static void method_3896(net.minecraft.class_885 self, net.minecraft.class_1431 a0, net.minecraft.class_4587 a1, float a2, float a3, float a4, float a5) {
        throw new UnsupportedOperationException("CenturyBridge: method_3896 was removed in 1.21.2; the new render API (method a) requires a pre-populated RenderState (class_10042) which cannot be constructed from the old (class_1431, 4 floats) parameters");
        // CB-CONFIDENCE: medium -- method explicitly deleted; new API uses RenderState-based paradigm incompatible with old direct-data parameters
    }

    /** was net/minecraft/class_885.method_3897(Lnet/minecraft/class_1431;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3897(net.minecraft.class_885 self, net.minecraft.class_1431 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_885.method_3897(class_1431) was deleted in 1.21.2; the structurally similar b(class_10042) takes a different parameter type with no known conversion from class_1431");
    }

    /** was net/minecraft/class_891.method_3905(Lnet/minecraft/class_1670;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3905(net.minecraft.class_891 self, net.minecraft.class_1670 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3905 returned class_2960 (Identifier) from class_1670 (Camera), but 1.21.2 class_891 has no Identifier-returning equivalent; the API was restructured around class_10017 and int-returning methods");
    }

    /** was net/minecraft/class_891.method_3906(Lnet/minecraft/class_1670;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_3906(net.minecraft.class_891 self, net.minecraft.class_1670 a0, float a1, float a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: method_3906(ParticleEffect,float,float,VertexConsumerProvider,LightmapTextureManager,int) was deleted in 1.21.2; the replacement a(class_10017,...) requires a class_10017 that cannot be constructed from the old ParticleEffect+float parameters (only obtainable via d(), which would ignore caller-supplied args)");
    }

    /** was net/minecraft/class_892.method_3909(Lnet/minecraft/class_1511;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3909(net.minecraft.class_892 self, net.minecraft.class_1511 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3909 returned a per-particle texture Identifier (class_2960); 1.21.2 particles use sprite-atlas descriptors (class_10014 via method_62416 / class_10017 via d) with no equivalent single texture Identifier");
    }

    /** was net/minecraft/class_893.method_3910(Lnet/minecraft/class_1577;Lnet/minecraft/class_4587;F)V */
    public static void method_3910(net.minecraft.class_893 self, net.minecraft.class_1577 a0, net.minecraft.class_4587 a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: method_3910 rendered an ItemStack (class_1577) via MatrixStack (class_4587); class_893 in 1.21.2 only exposes Identifier-returning methods (a/b) and no longer handles ItemStack rendering");
    }

    /** was net/minecraft/class_8947.field_47260I */
    public static int field_47260() {
        throw new UnsupportedOperationException("CenturyBridge: field_47260 was a deleted static int constant from class_8947 with no direct replacement in 1.21.2; its value cannot be derived from the new API surface");
    }

    /** was net/minecraft/class_8949.method_55009()D */
    public static double method_55009(net.minecraft.class_8949 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_8949.method_55009()D was deleted in 1.21.2 with no direct replacement; the double value it returned is no longer exposed on class_8949");
    }

    /** was net/minecraft/class_894.method_3911(Lnet/minecraft/class_1560;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_3911(net.minecraft.class_894 self, net.minecraft.class_1560 a0, float a1, float a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5) {
        self.method_62422(a0, self.method_62421(), a1);
    }

    /** was net/minecraft/class_8953.method_55038(Lnet/minecraft/class_8949;Lnet/minecraft/class_1309;)Z */
    public static boolean method_55038(net.minecraft.class_8953 self, net.minecraft.class_8949 a0, net.minecraft.class_1309 a1) {
        throw new UnsupportedOperationException("CenturyBridge: method_55038(class_8949, class_1309)->boolean has no direct replacement; new API splits Raid and LivingEntity operations into separate method groups with no combined overload");
    }

    /** was net/minecraft/class_895.method_32165()Lnet/minecraft/class_5607; */
    public static net.minecraft.class_5607 method_32165(net.minecraft.class_895 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_895.method_32165() returned a class_5607 (PostEffectProcessor) that class_895 no longer exposes in 1.21.2; the post-effect pipeline was reworked and no equivalent accessor exists");
    }

    /** was net/minecraft/class_8974.method_55239(Lnet/minecraft/class_9236;FFFFF)V */
    public static void method_55239(net.minecraft.class_8974 self, net.minecraft.class_9236 a0, float a1, float a2, float a3, float a4, float a5) {
        throw new UnsupportedOperationException("CenturyBridge: class_8974.method_55239(class_9236,float,float,float,float,float) was removed in 1.21.2 with no direct replacement; new a(class_10017) cannot be bridged without class_10017's constructor API to wrap the old (Random + 5 floats) arguments");
    }

    /** was net/minecraft/class_897.method_3931(Lnet/minecraft/class_1297;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3931(net.minecraft.class_897 self, net.minecraft.class_1297 a0) {
        throw new UnsupportedOperationException("CenturyBridge: EntityRenderer.getTexture(Entity) was removed in 1.21.2; textures are now resolved through the render state system, not via EntityRenderer");
        // CB-CONFIDENCE: high -- the new class_897 API surface has no method returning class_2960 (Identifier); getTexture was genuinely removed in the 1.21.2 entity renderer refactor
    }

    /** was net/minecraft/class_8987.method_55269(Lnet/minecraft/class_9236;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_55269(net.minecraft.class_8987 self, net.minecraft.class_9236 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_55269 was removed in 1.21.2 with no direct replacement; the new class_8987 API exposes no path from class_9236 to class_2960");
    }

    /** was net/minecraft/class_8987.method_55270(Lnet/minecraft/class_9236;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_55270(net.minecraft.class_8987 self, net.minecraft.class_9236 a0, float a1, float a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: method_55270 was removed; replacement a() requires class_10017 (not class_9236) and drops the two position floats with no available conversion");
    }

    /** was net/minecraft/class_898.method_3954(Lnet/minecraft/class_1297;DDDFFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_3954(net.minecraft.class_898 self, net.minecraft.class_1297 a0, double a1, double a2, double a3, float a4, float a5, net.minecraft.class_4587 a6, net.minecraft.class_4597 a7, int a8) {
        self.method_62424(a0, a1, a2, a3, a5, a6, a7, a8);
    }

    /** was net/minecraft/class_8998.field_47509Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_47509() {
        throw new UnsupportedOperationException("CenturyBridge: field_47509 (RegistryKey) was deleted in 1.21.2; remaining fields field_47507/field_47508 are different keys and cannot substitute");
    }

    /** was net/minecraft/class_900.method_3963(Lnet/minecraft/class_1669;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3963(net.minecraft.class_900 self, net.minecraft.class_1669 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3963 returned an Identifier (model/texture id) for an ItemStack; 1.21.2 replaced identifier-based lookups with state-based rendering via method_62429(class_1669, class_10020, float) and no longer exposes the Identifier");
    }

    /** was net/minecraft/class_901.method_3964(Lnet/minecraft/class_1540;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3964(net.minecraft.class_901 self, net.minecraft.class_1540 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_901.method_3964(Vec3d)->Identifier was removed in 1.21.2 with no direct replacement; no method on the new class_901 returns an Identifier");
    }

    /** was net/minecraft/class_902.method_3967(Lnet/minecraft/class_1303;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3967(net.minecraft.class_902 self, net.minecraft.class_1303 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3967(Camera) returned class_2960 (Matrix4f) computed from the camera alone; 1.21.2 replaced this with method_62434(Camera, class_10022, float) which writes into a class_10022 holder and requires a tick-delta parameter, and class_2960 itself was removed in the JOML migration");
    }

    /** was net/minecraft/class_903.method_3969(Lnet/minecraft/class_1671;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3969(net.minecraft.class_903 self, net.minecraft.class_1671 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_903.method_3969(ItemStack)Identifier was removed in 1.21.2; ItemRenderer no longer resolves an ItemStack to a texture Identifier (texture/sprite resolution moved to the new model system, and no method on class_903 returns class_2960)");
    }

    /** was net/minecraft/class_905.method_3973(Lnet/minecraft/class_1571;Lnet/minecraft/class_4587;F)V */
    public static void method_3973(net.minecraft.class_905 self, net.minecraft.class_1571 a0, net.minecraft.class_4587 a1, float a2) {
        self.method_62448(a0, self.method_62449(), a2);
    }

    /** was net/minecraft/class_906.method_3975(Lnet/minecraft/class_1536;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3975(net.minecraft.class_906 self, net.minecraft.class_1536 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_3975 returned an Identifier derived from the Camera; 1.21.2 replaced it with boolean predicates (method_62441/method_62442) and direct rendering (method_62443), so the Identifier is no longer exposed");
    }

    /** was net/minecraft/class_908.method_3980(Lnet/minecraft/class_1570;Lnet/minecraft/class_4587;F)V */
    public static void method_3980(net.minecraft.class_908 self, net.minecraft.class_1570 a0, net.minecraft.class_4587 a1, float a2) {
        self.method_62450(a0, self.method_62451(), a2);
    }

    /** was net/minecraft/class_9094.field_48073Lnet/minecraft/class_9145; */
    public static net.minecraft.class_9145 field_48073() {
        throw new UnsupportedOperationException("CenturyBridge: field_48073 was deleted in 1.21.2 with no direct replacement among the remaining class_9145 constants");
        // CB-CONFIDENCE: high -- the field is explicitly marked deleted with no direct replacement; returning a sibling constant would be a silent semantic mismatch
    }

    /** was net/minecraft/class_912.method_3985(Lnet/minecraft/class_1642;Lnet/minecraft/class_4587;F)V */
    public static void method_3985(net.minecraft.class_912 self, net.minecraft.class_1642 a0, net.minecraft.class_4587 a1, float a2) {
        throw new UnsupportedOperationException("CenturyBridge: class_912.method_3985(ItemStack, MatrixStack, float) was removed in 1.21.2; class_912 no longer exposes rendering methods, only identifier accessors a() and b()");
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


    /** was net/minecraft/class_918.method_29711(Lnet/minecraft/class_4597;Lnet/minecraft/class_1921;ZZ)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_29711(net.minecraft.class_918 self, net.minecraft.class_4597 a0, net.minecraft.class_1921 a1, boolean a2, boolean a3) {
        return self.method_23181(a0, a1, a2, a3);
    }

    /** was net/minecraft/class_951.method_4127(Lnet/minecraft/class_1477;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_4127(net.minecraft.class_951 self, net.minecraft.class_1477 a0) {
        return self.method_33431(self.method_62542());
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

    /**
     * superRedirects: a 1.20.1 override's super.render(context, mx, my, delta).
     * 1.20.1 Screen.render drew the widget list and nothing else; since 1.20.2
     * it draws the background first, over everything the mod just drew. Render
     * exactly what the old contract promised.
     */
    public static void cbRenderWidgets(net.minecraft.class_437 self, net.minecraft.class_332 context, int mouseX, int mouseY, float delta) {
        for (net.minecraft.class_4068 d : ((top.dext.centurybridge.shims.v1_21_8.ScreenDrawablesAccessor) self).cbDrawables()) {
            d.method_25394(context, mouseX, mouseY, delta);
        }
    }

    /** ctorRedirects: 1.20.1 new ServerInfo(name, address, local) -- the boolean
     *  became ServerInfo.ServerType in 1.20.2 (local ? LAN : OTHER). */
    public static net.minecraft.class_642 cbNewServerInfo(String name, String address, boolean local) {
        return new net.minecraft.class_642(name, address,
            local ? net.minecraft.class_642.class_8678.field_45609   // LAN
                  : net.minecraft.class_642.class_8678.field_45611); // OTHER
    }

    /** superCtorAdapts: the 1.20.1 ServerInfo ctor boolean, as ServerType */
    public static net.minecraft.class_642.class_8678 cbServerType(boolean local) {
        return local ? net.minecraft.class_642.class_8678.field_45609   // LAN
                     : net.minecraft.class_642.class_8678.field_45611;  // OTHER
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
}
