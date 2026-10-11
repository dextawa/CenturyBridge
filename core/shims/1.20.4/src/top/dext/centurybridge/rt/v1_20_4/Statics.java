package top.dext.centurybridge.rt.v1_20_4;

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
        return class_1856.field_46095.parse(JsonOps.INSTANCE, json)
            .result()
            .orElseThrow(() -> new JsonSyntaxException("Invalid ingredient: " + json));
    }

    /** 1.20.1 ShapedRecipe.outputFromJson (item/count/nbt legacy format), removed 1.20.2. L3 x11. */
    public static net.minecraft.class_1799 method_35228(com.google.gson.JsonObject json) {
        String itemId = net.minecraft.class_3518.method_15265(json, "item");
        net.minecraft.class_1792 item = net.minecraft.class_7923.field_41178
            .method_17966(new net.minecraft.class_2960(itemId))
            .orElseThrow(() -> new JsonSyntaxException("Unknown item '" + itemId + "'"));
        net.minecraft.class_1799 stack = new net.minecraft.class_1799(
            item, net.minecraft.class_3518.method_15282(json, "count", 1));
        if (json.has("nbt")) {
            try {
                stack.method_7980(net.minecraft.class_2522.method_10718(
                    net.minecraft.class_3518.method_15265(json, "nbt")));
            } catch (Exception e) {
                throw new JsonSyntaxException("Invalid nbt on output stack", e);
            }
        }
        return stack;
    }

    /** 1.20.1 FluidDrainable.tryDrainFluid gained a leading @Nullable player param in 1.20.2.
     *  Interface methods cannot be re-added via Mixin; call sites are redirected here. L2 x19. */
    public static net.minecraft.class_1799 method_9700(net.minecraft.class_2263 self,
            net.minecraft.class_1936 world, net.minecraft.class_2338 pos, net.minecraft.class_2680 state) {
        return self.method_9700(null, world, pos, state);
    }

    /** 1.20.1 Ingredient.fromJson(json, allowEmpty), removed 1.20.2. L3 x9. */
    public static class_1856 method_8102(JsonElement json, boolean allowEmpty) {
        var codec = allowEmpty ? class_1856.field_46095 : class_1856.field_46096;
        return codec.parse(JsonOps.INSTANCE, json)
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

    /** 1.20.1 RecipeSerializer.read(id, json): parsing moved to codec(); id unused. Interface -> call-site redirect. L3 x7. */
    public static <T extends net.minecraft.class_1860<?>> T method_8121(
            net.minecraft.class_1865<T> self, net.minecraft.class_2960 id, com.google.gson.JsonObject json) {
        return self.method_53736().parse(JsonOps.INSTANCE, json)
            .result()
            .orElseThrow(() -> new JsonSyntaxException("Invalid recipe " + id + ": " + json));
    }

    /** 1.20.1 RecipeSerializer.read(id, buf): id parameter dropped. Interface -> call-site redirect. L2 x6. */
    public static <T extends net.minecraft.class_1860<?>> T method_8122(
            net.minecraft.class_1865<T> self, net.minecraft.class_2960 id, net.minecraft.class_2540 buf) {
        return self.method_8122(buf);
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

    /** 1.20.1 ShapedRecipe.Serializer read pair (id parameter dropped / json moved to codec). */
    public static net.minecraft.class_1869 method_8163(net.minecraft.class_1869.class_1870 self,
            net.minecraft.class_2960 id, net.minecraft.class_2540 buf) {
        return self.method_8163(buf);
    }

    public static net.minecraft.class_1869 method_8164(net.minecraft.class_1869.class_1870 self,
            net.minecraft.class_2960 id, com.google.gson.JsonObject json) {
        return ((net.minecraft.class_1865<net.minecraft.class_1869>) self).method_53736()
            .parse(JsonOps.INSTANCE, json)
            .result()
            .orElseThrow(() -> new JsonSyntaxException("Invalid shaped recipe " + id));
    }

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
    public static void method_7662(net.minecraft.class_1732 self, net.minecraft.class_1860<?> recipe) {
        if (recipe == null) {
            self.method_7662(null);
            return;
        }
        net.minecraft.class_2960 id = Trackers.RECIPE_IDS.get(recipe);
        self.method_7662(
            id == null ? null : new net.minecraft.class_8786<>(id, recipe));
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
        return net.minecraft.class_2073.field_45754.parse(JsonOps.INSTANCE, json)
            .result()
            .orElseThrow(() -> new JsonSyntaxException("Invalid item predicate: " + json));
    }

    /** 1.20.1 ItemPredicate.toJson, reimplemented on the codec's encode side. */
    public static JsonElement method_8971(net.minecraft.class_2073 self) {
        return net.minecraft.class_2073.field_45754.encodeStart(JsonOps.INSTANCE, self)
            .result()
            .orElse(com.google.gson.JsonNull.INSTANCE);
    }

    /**
     * 1.20.1 RecipeBookServer.contains(Recipe): the parameter became a
     * RecipeEntry at 1.20.2. The tracker remembers which id each recipe was
     * registered under, so the old shape still answers correctly instead of
     * being written off as "moved into the RecipeEntry system".
     */
    public static boolean method_14878(net.minecraft.class_3439 self,
            net.minecraft.class_1860<?> recipe) {
        net.minecraft.class_2960 id = Trackers.RECIPE_IDS.get(recipe);
        if (id == null) {
            return false;   // never registered, so certainly not unlocked
        }
        return self.method_14878(new net.minecraft.class_8786<>(id, recipe));
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
    public static boolean method_7665(net.minecraft.class_1732 self,
            net.minecraft.class_1937 world, net.minecraft.class_3222 player,
            net.minecraft.class_1860<?> recipe) {
        net.minecraft.class_2960 id = Trackers.RECIPE_IDS.get(recipe);
        if (id == null) {
            return false;
        }
        return self.method_7665(world, player, new net.minecraft.class_8786<>(id, recipe));
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

    /** was net/minecraft/class_1293.method_5589(Lnet/minecraft/class_1309;)V */
    public static void method_5589(net.minecraft.class_1293 self, net.minecraft.class_1309 a0) {
        self.method_52523(a0);
    }

    /** was net/minecraft/class_1297.method_5621()D */
    public static double method_5621(net.minecraft.class_1297 self) {
        return (double) self.method_17682();
    }

    /** was net/minecraft/class_1308.method_47922(Lnet/minecraft/class_1309;)D */
    public static double method_47922(net.minecraft.class_1308 self, net.minecraft.class_1309 a0) {
        return (double) (self.method_17681() * 2.0F * self.method_17681() * 2.0F + a0.method_17681());
    }

    /** was net/minecraft/class_1309.field_6242D */
    public static double field_6242() {
        return 1.3D;
    }

    /** was net/minecraft/class_1324.method_6202(Lnet/minecraft/class_1322;)V */
    public static void method_6202(net.minecraft.class_1324 self, net.minecraft.class_1322 a0) {
        self.method_26835(a0);
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

    /** was net/minecraft/class_1690.method_7553(I)V */
    public static void method_7553(net.minecraft.class_1690 self, int a0) {
        self.method_7556(a0);
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

    /** was net/minecraft/class_174.method_766()Ljava/lang/Iterable; */
    public static java.lang.Iterable method_766(net.minecraft.class_174 self) {
        return java.util.Collections.emptyList();
    }

    /** was net/minecraft/class_1830.field_30925Ljava/lang/String; */
    public static java.lang.String field_30925() {
        return "Items";
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
        return new net.minecraft.class_2960("enchanted_item");
    }

    /** was net/minecraft/class_2054.field_9612Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_9612() {
        return new net.minecraft.class_2960("enchantment_used_on_item");
    }

    /** was net/minecraft/class_2062.field_9624Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_9624() {
        return new net.minecraft.class_2960("recipe_unlocked");
    }

    /** was net/minecraft/class_2066.field_9625Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_9625() {
        return new net.minecraft.class_2960("minecraft", "brewed_potion");
    }

    /** was net/minecraft/class_2152.method_9175()Ljava/lang/Float; */
    public static java.lang.Float method_9175(net.minecraft.class_2152 self) {
        return self.comp_1841();
    }

    /** was net/minecraft/class_2152.method_9177()Ljava/lang/Float; */
    public static java.lang.Float method_9177(net.minecraft.class_2152 self) {
        return self.comp_1840();
    }

    /** was net/minecraft/class_2385.method_10273(ILnet/minecraft/class_5321;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/class_6880; */
    public static net.minecraft.class_6880 method_10273(net.minecraft.class_2385 self, int a0, net.minecraft.class_5321 a1, java.lang.Object a2, com.mojang.serialization.Lifecycle a3) {
        return self.method_10272(a1, a2, a3);
    }

    /** was net/minecraft/class_2539.method_10781(Lnet/minecraft/class_2598;Lnet/minecraft/class_2596;)I */
    public static int method_10781(net.minecraft.class_2539 self, net.minecraft.class_2598 a0, net.minecraft.class_2596 a1) {
        for (java.util.Map.Entry<Integer, ?> entry : self.method_39498(a0).entrySet()) {
            if (a1.getClass().equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("Unknown packet class: " + a1.getClass());
    }

    /** was net/minecraft/class_2539.method_10782(I)Lnet/minecraft/class_2539; */
    public static net.minecraft.class_2539 method_10782(net.minecraft.class_2539 self, int a0) {
        net.minecraft.class_2539[] values = net.minecraft.class_2539.values();
        for (net.minecraft.class_2539 value : values) {
            if (value.field_41866 == a0) {
                return value;
            }
        }
        int wrapped = java.lang.Math.floorMod(a0, values.length);
        for (net.minecraft.class_2539 value : values) {
            if (value.field_41866 == wrapped) {
                return value;
            }
        }
        return values[0];
    }

    /** was net/minecraft/class_2539.method_10786(Lnet/minecraft/class_2596;)Lnet/minecraft/class_2539; */
    public static net.minecraft.class_2539 method_10786(net.minecraft.class_2539 self, net.minecraft.class_2596 a0) {
        if (a0 == null) {
            return null;
        }
        for (net.minecraft.class_2539 state : net.minecraft.class_2539.values()) {
            for (net.minecraft.class_2598 side : net.minecraft.class_2598.values()) {
                it.unimi.dsi.fastutil.ints.Int2ObjectMap<?> map = state.method_39498(side);
                if (map != null && map.containsValue(a0.getClass())) {
                    return state;
                }
            }
        }
        return null;
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

    /** was net/minecraft/class_2602.method_11126(Lnet/minecraft/class_2790;)V */
    public void method_11126(net.minecraft.class_2790 a0) {
        ((net.minecraft.class_8705) (Object) this).method_11126(a0);
    }

    /** was net/minecraft/class_2602.method_36895(Lnet/minecraft/class_6373;)V */
    public static void method_36895(net.minecraft.class_2602 self, net.minecraft.class_6373 a0) {
        a0.method_11054(self);
    }

    /** was net/minecraft/class_2621.field_31352Ljava/lang/String; */
    public static java.lang.String field_31352() {
        return "Fuel";
    }

    /** was net/minecraft/class_2625.method_49844(Lnet/minecraft/class_1657;)Lnet/minecraft/class_8242; */
    public static net.minecraft.class_8242 method_49844(net.minecraft.class_2625 self, net.minecraft.class_1657 a0) {
        return self.method_49843(self.method_49834(a0));
    }

    /** was net/minecraft/class_2633.method_11368(Lnet/minecraft/class_3218;Z)Z */
    public static boolean method_11368(net.minecraft.class_2633 self, net.minecraft.class_3218 a0, boolean a1) {
        return self.method_54874(a0);
    }

    /** was net/minecraft/class_2633.method_21865()Ljava/lang/String; */
    public static java.lang.String method_21865(net.minecraft.class_2633 self) {
        return self.field_31366;
    }

    /** was net/minecraft/class_2792.method_12069(Lnet/minecraft/class_2803;)V */
    public void method_12069(net.minecraft.class_2803 a0) {
        ((net.minecraft.class_8706) (Object) this).method_12069(a0);
    }

    /** was net/minecraft/class_2921.method_12666(Lnet/minecraft/class_2923;)V */
    public void method_12666(net.minecraft.class_2923 a0) {
        ((net.minecraft.class_8763) (Object) this).method_12666(a0);
    }

    /** was net/minecraft/class_2994.method_3788()I */
    public int method_3788() {
        return ((net.minecraft.class_8599) (Object) this).method_3788();
    }

    /** was net/minecraft/class_2994.method_3802()I */
    public int method_3802() {
        return ((net.minecraft.class_8599) (Object) this).method_3802();
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

    /** was net/minecraft/class_315.field_1866Z */
    public static boolean field_1866() {
        return false;
    }

    /** was net/minecraft/class_315.field_1880Z */
    public static boolean field_1880() {
        return true;
    }

    /** was net/minecraft/class_3210.method_14081(JJLnet/minecraft/class_3222;)V */
    public static void method_14081(net.minecraft.class_3210 self, long a0, long a1, net.minecraft.class_3222 a2) {
        self.method_14087(a2);
    }

    /** was net/minecraft/class_3248.method_40085(Ljava/lang/String;)Z */
    public static boolean method_40085(net.minecraft.class_3248 self, java.lang.String a0) {
        return a0.chars().filter(c -> c <= 32 || c >= 127).findFirst().isEmpty();
    }

    /** was net/minecraft/class_339.field_22757Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22757() {
        return new net.minecraft.class_2960("textures/gui/widgets.png");
    }

    /** was net/minecraft/class_339.field_42117Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_42117() {
        return new net.minecraft.class_2960("textures/gui/widgets.png");
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

    /** was net/minecraft/class_361.field_2191I */
    public static int field_2191() {
        return 147;
    }

    /** was net/minecraft/class_361.field_2193Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_2193() {
        return new net.minecraft.class_2960("textures/gui/recipe_book.png");
    }

    /** was net/minecraft/class_365.field_2199Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_2199() {
        return new net.minecraft.class_2960("minecraft", "textures/gui/options_background.png");
    }

    /** was net/minecraft/class_368.field_2207Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_2207() {
        return new net.minecraft.class_2960("textures/gui/toasts.png");
    }

    /** was net/minecraft/class_3898.field_18243I */
    public static int field_18243() {
        return 34;
    }

    /** was net/minecraft/class_413$class_4192.field_44667Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_44667() {
        return new net.minecraft.class_2960("textures/gui/language_button.png");
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

    /** was net/minecraft/class_4381.field_22687Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22687() {
        return new net.minecraft.class_2960("realms", "textures/gui/realms/slot_frame.png");
    }

    /** was net/minecraft/class_4406.field_22704Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22704() {
        return new net.minecraft.class_2960("textures/gui/recipe_book.png");
    }

    /** was net/minecraft/class_4406.field_22706Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22706() {
        return new net.minecraft.class_2960("textures/gui/container/creative_inventory/creative_inventory.png");
    }

    /** was net/minecraft/class_4419.field_20072I */
    public static int field_20072() {
        return 32;
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
        return new net.minecraft.class_2960("textures/misc/unknown_server.png");
    }

    /** was net/minecraft/class_4419.field_22720Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22720() {
        return new net.minecraft.class_2960("textures/gui/container/command_block.png");
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

    /** was net/minecraft/class_4558$class_8788.method_27790()Ljava/util/Optional; */
    public static java.util.Optional method_27790(net.minecraft.class_4558.class_8788 self) {
        return self.comp_2029();
    }

    /** was net/minecraft/class_457.field_2716Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_2716() {
        return new net.minecraft.class_2960("textures/gui/options_background.png");
    }

    /** was net/minecraft/class_4871.field_22583Ljava/lang/String; */
    public static java.lang.String field_22583() {
        return "uuid";
    }

    /** was net/minecraft/class_4895.field_42047I */
    public static int field_42047() {
        return 8;
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

    /** was net/minecraft/class_4965.method_26094(Lcom/google/gson/JsonObject;)Lnet/minecraft/class_4965; */
    public static net.minecraft.class_4965 method_26094(net.minecraft.class_4965 self, com.google.gson.JsonObject a0) {
        return net.minecraft.class_4965.field_45750.codec().parse(com.mojang.serialization.JsonOps.INSTANCE, a0).getOrThrow(false, (error) -> {
            throw new com.google.gson.JsonSyntaxException(error);
        });
    }

    /** was net/minecraft/class_521.field_19125Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_19125() {
        return new net.minecraft.class_2960("textures/gui/recipe_book.png");
    }

    /** was net/minecraft/class_5289.field_24566Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_24566() {
        return new net.minecraft.class_2960("textures/gui/social_interactions/black_icon.png");
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

    /** was net/minecraft/class_5682.field_28359Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_28359() {
        return new net.minecraft.class_2960("textures/gui/container/bundle.png");
    }

    /** was net/minecraft/class_5754.field_30116D */
    public static double field_30116() {
        return 8.0;
    }

    /** was net/minecraft/class_6611.method_38656(IILjava/net/SocketAddress;I)V */
    public void method_38656(int a0, int a1, java.net.SocketAddress a2, int a3) {
        net.minecraft.class_6611 self = (net.minecraft.class_6611) (Object) this;
        for (net.minecraft.class_2539 state : net.minecraft.class_2539.values()) {
            if (state.field_41866 == a0) {
                self.method_38656(state, a1, a2, a3);
                return;
            }
        }
        throw new UnsupportedOperationException("CenturyBridge: no class_2539 with field_41866=" + a0);
    }

    /** was net/minecraft/class_6611.method_38657(IILjava/net/SocketAddress;I)V */
    public void method_38657(int a0, int a1, java.net.SocketAddress a2, int a3) {
        ((net.minecraft.class_6611) (Object) this).method_38657(net.minecraft.class_2539.values()[a0], a1, a2, a3);
    }

    /** was net/minecraft/class_7298.method_45342()Z */
    public static boolean method_45342(net.minecraft.class_7298 self) {
        return self.method_44359();
    }

    /** was net/minecraft/class_7383.method_43158(Lcom/google/gson/JsonObject;)Lnet/minecraft/class_7383; */
    public static net.minecraft.class_7383 method_43158(net.minecraft.class_7383 self, com.google.gson.JsonObject a0) {
        return net.minecraft.class_7383.field_45776.codec().parse(com.mojang.serialization.JsonOps.INSTANCE, a0).getOrThrow(false, s -> {
            throw new com.google.gson.JsonParseException(s);
        });
    }

    /** was net/minecraft/class_7591$class_7592.field_39765I */
    public static int field_39765() {
        return 0;
    }

    /** was net/minecraft/class_7591.field_39761Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_39761() {
        return new net.minecraft.class_2960("textures/gui/chat_status.png");
    }

    /** was net/minecraft/class_7610$class_7611.field_40848Lnet/minecraft/class_7610$class_7611; */
    public static net.minecraft.class_7610.class_7611 field_40848() {
        return net.minecraft.class_7610.class_7611.unsigned(new java.util.UUID(0L, 0L), () -> false);
    }

    /** was net/minecraft/class_7689.method_45348()Z */
    public static boolean method_45348(net.minecraft.class_7689 self) {
        return self.method_48573() && !self.method_45350();
    }

    /** was net/minecraft/class_7923.field_41154Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_41154() {
        return new net.minecraft.class_2960("root");
    }

    /** was net/minecraft/class_8113.field_43149Ljava/lang/String; */
    public static java.lang.String field_43149() {
        return "sniffing_state";
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

    // ==== END GENERATED ====

    private Statics() {
    }

    /** was net/minecraft/class_2588.<init>(Ljava/lang/String;[Ljava/lang/Object;)V -- 1.20.3 added a fallback string */
    public static net.minecraft.class_2588 cbNewclass_2588(String key, Object[] args) { return new net.minecraft.class_2588(key, key, args); }
    /** was net/minecraft/class_2588.<init>(Ljava/lang/String;)V */
    public static net.minecraft.class_2588 cbNewclass_2588(String key) { return new net.minecraft.class_2588(key, key, new Object[0]); }

    // NbtIo took java.nio.file.Path instead of File in 1.20.3.
    /** was net/minecraft/class_2507.method_30614(Lnet/minecraft/class_2487;Ljava/io/File;)V writeCompressed */
    public static void method_30614(net.minecraft.class_2487 nbt, java.io.File f) throws java.io.IOException { net.minecraft.class_2507.method_30614(nbt, f.toPath()); }
    /** was net/minecraft/class_2507.method_10630(Lnet/minecraft/class_2487;Ljava/io/File;)V write */
    public static void method_10630(net.minecraft.class_2487 nbt, java.io.File f) throws java.io.IOException { net.minecraft.class_2507.method_10630(nbt, f.toPath()); }
    /** was net/minecraft/class_2507.method_10633(Ljava/io/File;)Lnet/minecraft/class_2487; read */
    public static net.minecraft.class_2487 method_10633(java.io.File f) throws java.io.IOException { return net.minecraft.class_2507.method_10633(f.toPath()); }

    /** was net/minecraft/class_161$class_162.method_695(Lnet/minecraft/class_2960;)Lnet/minecraft/class_161; Builder.build(id) */
    public static net.minecraft.class_161 method_695(net.minecraft.class_161.class_162 self, net.minecraft.class_2960 id) {
        return self.method_695(id).comp_1920();
    }
    /** was net/minecraft/class_161$class_162.method_710()Ljava/util/Map; Builder.getCriteria (accessor on the ImmutableMap builder) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static java.util.Map method_710(net.minecraft.class_161.class_162 self) {
        return ((top.dext.centurybridge.shims.v1_20_4.CbAdvancementBuilderCriteria) (Object) self).cbCriteria().build();
    }
    /** was net/minecraft/class_3448.method_14957()Ljava/lang/String; StatType.getTranslationKey */
    @SuppressWarnings("rawtypes")
    public static String method_14957(net.minecraft.class_3448 self) {
        return net.minecraft.class_156.method_646("stat_type", net.minecraft.class_7923.field_41193.method_10221(self));
    }
    /** was net/minecraft/class_5712.method_32938()Ljava/lang/String; GameEvent.getId (the registry path) */
    public static String method_32938(net.minecraft.class_5712 self) {
        net.minecraft.class_2960 id = net.minecraft.class_7923.field_41171.method_10221(self);
        return id == null ? "unknown" : id.method_12832();
    }
    /** was net/minecraft/class_173.method_762(Lnet/minecraft/class_176;)Lnet/minecraft/class_2960; LootContextTypes.getId (registry map via accessor) */
    public static net.minecraft.class_2960 method_762(net.minecraft.class_176 set) {
        return top.dext.centurybridge.shims.v1_20_4.CbLootContextTypesAccessor.cbRegistry().inverse().get(set);
    }
    /** was net/minecraft/class_1496.method_5621()D */
    public static double method_5621(net.minecraft.class_1496 self) { return self.method_17682() * 0.75; }
    /** was net/minecraft/class_1498.method_5621()D */
    public static double method_5621(net.minecraft.class_1498 self) { return self.method_17682() * 0.75; }
    /** was net/minecraft/class_2090.method_9017(Lnet/minecraft/class_5321;)Lnet/minecraft/class_2090; LocationPredicate.feature(structure) */
    public static net.minecraft.class_2090 method_9017(net.minecraft.class_5321<net.minecraft.class_3195> key) {
        return net.minecraft.class_2090.class_2091.method_53183(key).method_9023();
    }
    /** was net/minecraft/class_2080$class_2083.method_35248(Lnet/minecraft/class_2048;)Lnet/minecraft/class_2080$class_2083; */
    public static net.minecraft.class_2080.class_2083 method_35248(net.minecraft.class_2048 p) {
        return net.minecraft.class_2080.class_2083.method_35248(java.util.Optional.ofNullable(p)).comp_1924();
    }
    /** was net/minecraft/class_5409$class_5410.method_30099(Lnet/minecraft/class_5258;Lnet/minecraft/class_2073$class_2074;Lnet/minecraft/class_5258;)Lnet/minecraft/class_5409$class_5410; */
    public static net.minecraft.class_5409.class_5410 method_30099(net.minecraft.class_5258 a, net.minecraft.class_2073.class_2074 b, net.minecraft.class_5258 c) {
        return net.minecraft.class_5409.class_5410.method_30099(java.util.Optional.ofNullable(a), b, java.util.Optional.ofNullable(c)).comp_1924();
    }
    /** was net/minecraft/class_2724.method_11779()Lnet/minecraft/class_5321; PlayerRespawnS2CPacket.getDimension */
    public static net.minecraft.class_5321<net.minecraft.class_1937> method_11779(net.minecraft.class_2724 self) { return self.comp_1728().comp_1554(); }
    /** was net/minecraft/class_3898.method_39975(IIIII)Z ChunkMap.isChunkInRange (1.20.1 algorithm) */
    public static boolean method_39975(int x1, int z1, int x2, int z2, int distance) {
        int i = Math.max(0, Math.abs(x1 - x2) - 1);
        int j = Math.max(0, Math.abs(z1 - z2) - 1);
        long l = Math.max(0, Math.max(i, j) - 1);
        long m = Math.min(i, j);
        long n = m * m + l * l;
        int k = distance * distance;
        return n < (long) k;
    }
    /** was net/minecraft/class_1867$class_1868.method_8142(Lnet/minecraft/class_2960;Lcom/google/gson/JsonObject;)Lnet/minecraft/class_1867; */
    public static net.minecraft.class_1867 method_8142(net.minecraft.class_1867.class_1868 self, net.minecraft.class_2960 id, com.google.gson.JsonObject json) {
        return self.method_53736().parse(JsonOps.INSTANCE, json).result()
            .orElseThrow(() -> new JsonSyntaxException("CenturyBridge: invalid shapeless recipe " + id));
    }
    private static int[] cbDivide(int total, int piece) {
        if (total <= 0 || piece <= 0) return new int[0];
        int count = net.minecraft.class_3532.method_38788(total, piece);
        int q = total / count, mod = total % count, rem = 0;
        int[] out = new int[count];
        for (int idx = 0; idx < count; idx++) {
            int p = q;
            rem += mod;
            if (rem >= count) { rem -= count; p++; }
            out[idx] = p;
        }
        return out;
    }
    /** SpecialCraftingRecipe(Identifier, category) -> (category): the id moved to RecipeEntry. */
    public static net.minecraft.class_7710 cbDropRecipeId(net.minecraft.class_2960 id, net.minecraft.class_7710 category) { return category; }
    /** AbstractCriterionConditions(Identifier, LootContextPredicate) -> (Optional<LootContextPredicate>) */
    public static java.util.Optional<net.minecraft.class_5258> cbCriterionPlayer(net.minecraft.class_2960 id, net.minecraft.class_5258 p) { return java.util.Optional.ofNullable(p); }
    /** ServerPlayerEntity(server, world, profile) gained SyncedClientOptions */
    public static net.minecraft.class_8791 cbDefaultClientOptions() { return net.minecraft.class_8791.method_53821(); }

    /** was net/minecraft/class_5251.method_27719(Ljava/lang/String;)Lnet/minecraft/class_5251; -- TextColor.parseColor
     *  returns DataResult since 1.20.3; the 1.20.1 contract was "TextColor or null". */
    public static net.minecraft.class_5251 method_27719(String s) {
        return net.minecraft.class_5251.method_27719(s).result().orElse(null);
    }

    /** was net/minecraft/class_31.method_29029(Lcom/mojang/serialization/Dynamic;Lcom/mojang/datafixers/DataFixer;ILnet/minecraft/class_2487;Lnet/minecraft/class_1940;Lnet/minecraft/class_5315;Lnet/minecraft/class_31$class_7729;Lnet/minecraft/class_5285;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/class_31; */
    public static net.minecraft.class_31 method_29029(com.mojang.serialization.Dynamic a0, com.mojang.datafixers.DataFixer a1, int a2, net.minecraft.class_2487 a3, net.minecraft.class_1940 a4, net.minecraft.class_5315 a5, net.minecraft.class_31.class_7729 a6, net.minecraft.class_5285 a7, com.mojang.serialization.Lifecycle a8) {
        return net.minecraft.class_31.method_29029(a0, a4, a6, a7, a8);
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

    /** was net/minecraft/class_3518.method_34916(Lcom/google/gson/JsonObject;Ljava/lang/String;Lnet/minecraft/class_1792;)Lnet/minecraft/class_1792; */
    public static net.minecraft.class_1792 method_34916(com.google.gson.JsonObject a0, java.lang.String a1, net.minecraft.class_1792 a2) {
        net.minecraft.class_6880 var3 = net.minecraft.class_6880.method_40223(a2);
        return (net.minecraft.class_1792) net.minecraft.class_3518.method_34916(a0, a1, var3).comp_349();
    }

    /** was net/minecraft/class_4525.method_22246(Lnet/minecraft/class_3341;ILnet/minecraft/class_3218;)V */
    public static void method_22246(net.minecraft.class_3341 a0, int a1, net.minecraft.class_3218 a2) {
        net.minecraft.class_4525.method_22246(a0, a2);
    }

    /** was net/minecraft/class_8216.method_49719(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String; */
    public static java.lang.String method_49719(java.lang.String a0, java.lang.String a1) {
        return net.minecraft.class_8216.method_49719(a0, java.util.UUID.fromString(a1));
    }

    /** was net/minecraft/class_8216.method_49720(Ljava/lang/String;Ljava/lang/String;Z)Ljava/lang/String; */
    public static java.lang.String method_49720(java.lang.String a0, java.lang.String a1, boolean a2) {
        return net.minecraft.class_8216.method_49720(a0, java.util.UUID.fromString(a1), a2);
    }

}
