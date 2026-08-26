package top.dext.centurybridge.rt.v1_20_2;

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

    /** 1.20.1 ItemPredicate.fromJson: return became Optional in 1.20.2; old callers expect a
     *  match-anything predicate for absent json (the ANY constant is gone -> empty builder). L3 x6. */
    public static net.minecraft.class_2073 method_8969(JsonElement json) {
        return net.minecraft.class_2073.method_8969(json)
            .orElseGet(() -> net.minecraft.class_2073.class_2074.method_8973().method_8976());
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

    /** 1.20.1 LocationPredicate.ANY, removed with the Optional-ization; empty builder = match-all. */
    public static final net.minecraft.class_2090 field_9685 =
        net.minecraft.class_2090.class_2091.method_22484().method_9023();

    /** 1.20.1 LocationPredicate.fromJson (same Optional-ization pattern as ItemPredicate). */
    public static net.minecraft.class_2090 method_9021(JsonElement json) {
        return net.minecraft.class_2090.method_9021(json).orElse(field_9685);
    }

    /** Optional-ized fromJson batch (1.20.2 predicate rework); invalid json throws like the old code did. */
    public static net.minecraft.class_2019 method_8839(JsonElement json) {
        return net.minecraft.class_2019.method_8839(json)
            .orElseThrow(() -> new JsonSyntaxException("Invalid damage predicate: " + json));
    }

    public static net.minecraft.class_2022 method_8846(JsonElement json) {
        return net.minecraft.class_2022.method_8846(json)
            .orElseThrow(() -> new JsonSyntaxException("Invalid damage source predicate: " + json));
    }

    public static net.minecraft.class_2025 method_8857(JsonElement json) {
        return net.minecraft.class_2025.method_8857(json)
            .orElseThrow(() -> new JsonSyntaxException("Invalid distance predicate: " + json));
    }

    public static net.minecraft.class_2048 method_8913(JsonElement json) {
        return net.minecraft.class_2048.method_8913(json)
            .orElseThrow(() -> new JsonSyntaxException("Invalid entity predicate: " + json));
    }

    public static net.minecraft.class_5258 method_51705(com.google.gson.JsonObject json, String key,
            net.minecraft.class_5257 deserializer) {
        return net.minecraft.class_2048.method_51705(json, key, deserializer)
            .orElse(field_24388);
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
    public static void method_7662(net.minecraft.class_1731 self, net.minecraft.class_1860<?> recipe) {
        if (recipe == null) {
            ((net.minecraft.class_1732) self).method_7662(null);
            return;
        }
        net.minecraft.class_2960 id = Trackers.RECIPE_IDS.get(recipe);
        ((net.minecraft.class_1732) self).method_7662(
            id == null ? null : new net.minecraft.class_8786<>(id, recipe));
    }

    // ==== GENERATED BRIDGES (assemble.py) ====
    /** was net/minecraft/class_161.<init>(Lnet/minecraft/class_2960;Lnet/minecraft/class_161;Lnet/minecraft/class_185;Lnet/minecraft/class_170;Ljava/util/Map;[[Ljava/lang/String;Z)V */
    public static net.minecraft.class_161 cbNewclass_161(net.minecraft.class_2960 a0, net.minecraft.class_161 a1, net.minecraft.class_185 a2, net.minecraft.class_170 a3, java.util.Map a4, java.lang.String[][] a5, boolean a6) {
        throw new UnsupportedOperationException("CenturyBridge: the new advancement API requires the parent's identifier, but the old constructor supplies only a parent advancement whose identifier is no longer stored");
    }

    /** was net/minecraft/class_175.<init>(Lnet/minecraft/class_184;)V */
    public static net.minecraft.class_175 cbNewclass_175(net.minecraft.class_184 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires a net.minecraft.class_179 value that the old constructor argument cannot provide");
    }

    /** was net/minecraft/class_1867.<init>(Lnet/minecraft/class_2960;Ljava/lang/String;Lnet/minecraft/class_7710;Lnet/minecraft/class_1799;Lnet/minecraft/class_2371;)V */
    public static net.minecraft.class_1867 cbNewclass_1867(net.minecraft.class_2960 a0, java.lang.String a1, net.minecraft.class_7710 a2, net.minecraft.class_1799 a3, net.minecraft.class_2371 a4) {
        return new net.minecraft.class_1867(a1, a2, a3, a4);
    }

    /** was net/minecraft/class_1869.<init>(Lnet/minecraft/class_2960;Ljava/lang/String;Lnet/minecraft/class_7710;IILnet/minecraft/class_2371;Lnet/minecraft/class_1799;)V */
    public static net.minecraft.class_1869 cbNewclass_1869(net.minecraft.class_2960 a0, java.lang.String a1, net.minecraft.class_7710 a2, int a3, int a4, net.minecraft.class_2371 a5, net.minecraft.class_1799 a6) {
        return new net.minecraft.class_1869(a1, a2, a3, a4, a5, a6);
    }

    /** was net/minecraft/class_196$class_198.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_5258;Lnet/minecraft/class_5258;Lnet/minecraft/class_5258;)V */
    public static net.minecraft.class_196.class_198 cbNewclass_196class_198(net.minecraft.class_5258 a0, net.minecraft.class_5258 a1, net.minecraft.class_5258 a2, net.minecraft.class_5258 a3) {
        return new net.minecraft.class_196.class_198(java.util.Optional.ofNullable(a0), java.util.Optional.ofNullable(a1), java.util.Optional.ofNullable(a2), java.util.Optional.ofNullable(a3));
    }

    /** was net/minecraft/class_1996$class_1998.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_1842;)V */
    public static net.minecraft.class_1996.class_1998 cbNewclass_1996class_1998(net.minecraft.class_5258 a0, net.minecraft.class_1842 a1) {
        return new net.minecraft.class_1996.class_1998(java.util.Optional.ofNullable(a0), a1);
    }

    /** was net/minecraft/class_1999$class_2001.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_5321;Lnet/minecraft/class_5321;)V */
    public static net.minecraft.class_1999.class_2001 cbNewclass_1999class_2001(net.minecraft.class_5258 a0, net.minecraft.class_5321 a1, net.minecraft.class_5321 a2) {
        return new net.minecraft.class_1999.class_2001(java.util.Optional.ofNullable(a0), a1, a2);
    }

    /** was net/minecraft/class_2002$class_2004.<init>(Lnet/minecraft/class_5258;[Lnet/minecraft/class_5258;)V */
    public static net.minecraft.class_2002.class_2004 cbNewclass_2002class_2004(net.minecraft.class_5258 a0, net.minecraft.class_5258[] a1) {
        return new net.minecraft.class_2002.class_2004(java.util.Optional.of(a0), java.util.Arrays.asList(a1));
    }

    /** was net/minecraft/class_2006$class_2008.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2096$class_2100;)V */
    public static net.minecraft.class_2006.class_2008 cbNewclass_2006class_2008(net.minecraft.class_5258 a0, net.minecraft.class_2096.class_2100 a1) {
        return new net.minecraft.class_2006.class_2008(java.util.Optional.ofNullable(a0), a1);
    }

    /** was net/minecraft/class_2014$class_2016.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_5258;Lnet/minecraft/class_5258;)V */
    public static net.minecraft.class_2014.class_2016 cbNewclass_2014class_2016(net.minecraft.class_5258 a0, net.minecraft.class_5258 a1, net.minecraft.class_5258 a2) {
        return new net.minecraft.class_2014.class_2016(java.util.Optional.ofNullable(a0), java.util.Optional.ofNullable(a1), java.util.Optional.ofNullable(a2));
    }

    /** was net/minecraft/class_2027$class_2029.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2102;Lnet/minecraft/class_5258;)V */
    public static net.minecraft.class_2027.class_2029 cbNewclass_2027class_2029(net.minecraft.class_5258 a0, net.minecraft.class_2102 a1, net.minecraft.class_5258 a2) {
        return new net.minecraft.class_2027.class_2029(java.util.Optional.ofNullable(a0), java.util.Optional.ofNullable(a1), java.util.Optional.ofNullable(a2));
    }

    /** was net/minecraft/class_2030$class_2032.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2073;Lnet/minecraft/class_2096$class_2100;)V */
    public static net.minecraft.class_2030.class_2032 cbNewclass_2030class_2032(net.minecraft.class_5258 a0, net.minecraft.class_2073 a1, net.minecraft.class_2096.class_2100 a2) {
        return new net.minecraft.class_2030.class_2032(java.util.Optional.ofNullable(a0), java.util.Optional.ofNullable(a1), a2);
    }

    /** was net/minecraft/class_2037$class_2039.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2248;Lnet/minecraft/class_4559;)V */
    public static net.minecraft.class_2037.class_2039 cbNewclass_2037class_2039(net.minecraft.class_5258 a0, net.minecraft.class_2248 a1, net.minecraft.class_4559 a2) {
        return new net.minecraft.class_2037.class_2039(java.util.Optional.ofNullable(a0), a1, java.util.Optional.ofNullable(a2));
    }

    /** was net/minecraft/class_2044$class_2046.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2019;)V */
    public static net.minecraft.class_2044.class_2046 cbNewclass_2044class_2046(net.minecraft.class_5258 a0, net.minecraft.class_2019 a1) {
        return new net.minecraft.class_2044.class_2046(java.util.Optional.ofNullable(a0), java.util.Optional.ofNullable(a1));
    }

    /** was net/minecraft/class_2058$class_2060.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2073;Lnet/minecraft/class_5258;Lnet/minecraft/class_2073;)V */
    public static net.minecraft.class_2058.class_2060 cbNewclass_2058class_2060(net.minecraft.class_5258 a0, net.minecraft.class_2073 a1, net.minecraft.class_5258 a2, net.minecraft.class_2073 a3) {
        return new net.minecraft.class_2058.class_2060(java.util.Optional.ofNullable(a0), java.util.Optional.ofNullable(a1), java.util.Optional.ofNullable(a2), java.util.Optional.ofNullable(a3));
    }

    /** was net/minecraft/class_2069$class_2071.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2073;Lnet/minecraft/class_2096$class_2100;Lnet/minecraft/class_2096$class_2100;)V */
    public static net.minecraft.class_2069.class_2071 cbNewclass_2069class_2071(net.minecraft.class_5258 a0, net.minecraft.class_2073 a1, net.minecraft.class_2096.class_2100 a2, net.minecraft.class_2096.class_2100 a3) {
        return new net.minecraft.class_2069.class_2071(java.util.Optional.ofNullable(a0), java.util.Optional.ofNullable(a1), a2, a3);
    }

    /** was net/minecraft/class_2073.<init>()V */
    public static net.minecraft.class_2073 cbNewclass_2073() {
        throw new UnsupportedOperationException("CenturyBridge: the old no-argument constructor has no faithful equivalent because the new constructor requires two net.minecraft.class_2096.class_2100 values and no valid defaults or factories are exposed");
    }

    /** was net/minecraft/class_2073.<init>(Lnet/minecraft/class_6862;Ljava/util/Set;Lnet/minecraft/class_2096$class_2100;Lnet/minecraft/class_2096$class_2100;[Lnet/minecraft/class_2035;[Lnet/minecraft/class_2035;Lnet/minecraft/class_1842;Lnet/minecraft/class_2105;)V */
    public static net.minecraft.class_2073 cbNewclass_2073(net.minecraft.class_6862 a0, java.util.Set a1, net.minecraft.class_2096.class_2100 a2, net.minecraft.class_2096.class_2100 a3, net.minecraft.class_2035[] a4, net.minecraft.class_2035[] a5, net.minecraft.class_1842 a6, net.minecraft.class_2105 a7) {
        throw new UnsupportedOperationException("CenturyBridge: old raw item and potion values cannot be converted to the new registry-entry-backed fields without registry access");
    }

    /** was net/minecraft/class_2073.field_9644Ljava/util/Set; */
    public static java.util.Set field_9644() {
        throw new UnsupportedOperationException("CenturyBridge: field_9644 was instance-specific item predicate state, but this static shim has no receiver from which to read comp_1784");
    }

    /** was net/minecraft/class_2076$class_2078.<init>(Lnet/minecraft/class_5258;[Lnet/minecraft/class_5258;Lnet/minecraft/class_2096$class_2100;)V */
    public static net.minecraft.class_2076.class_2078 cbNewclass_2076class_2078(net.minecraft.class_5258 a0, net.minecraft.class_5258[] a1, net.minecraft.class_2096.class_2100 a2) {
        return new net.minecraft.class_2076.class_2078(java.util.Optional.ofNullable(a0), java.util.Arrays.asList(a1), a2);
    }

    /** was net/minecraft/class_2080$class_2083.<init>(Lnet/minecraft/class_2960;Lnet/minecraft/class_5258;Lnet/minecraft/class_5258;Lnet/minecraft/class_2022;)V */
    public static net.minecraft.class_2080.class_2083 cbNewclass_2080class_2083(net.minecraft.class_2960 a0, net.minecraft.class_5258 a1, net.minecraft.class_5258 a2, net.minecraft.class_2022 a3) {
        return new net.minecraft.class_2080.class_2083(java.util.Optional.ofNullable(a1), java.util.Optional.ofNullable(a2), java.util.Optional.ofNullable(a3));
    }

    /** was net/minecraft/class_2080.<init>(Lnet/minecraft/class_2960;)V */
    public static net.minecraft.class_2080 cbNewclass_2080(net.minecraft.class_2960 a0) {
        return new net.minecraft.class_2080();
    }

    /** was net/minecraft/class_2085$class_2087.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2025;Lnet/minecraft/class_2096$class_2100;)V */
    public static net.minecraft.class_2085.class_2087 cbNewclass_2085class_2087(net.minecraft.class_5258 a0, net.minecraft.class_2025 a1, net.minecraft.class_2096.class_2100 a2) {
        return new net.minecraft.class_2085.class_2087(java.util.Optional.ofNullable(a0), java.util.Optional.ofNullable(a1), a2);
    }

    /** was net/minecraft/class_2108$class_2110.<init>(Lnet/minecraft/class_2960;Lnet/minecraft/class_5258;Lnet/minecraft/class_2090;Lnet/minecraft/class_2025;)V */
    public static net.minecraft.class_2108.class_2110 cbNewclass_2108class_2110(net.minecraft.class_2960 a0, net.minecraft.class_5258 a1, net.minecraft.class_2090 a2, net.minecraft.class_2025 a3) {
        return new net.minecraft.class_2108.class_2110(java.util.Optional.ofNullable(a1), java.util.Optional.ofNullable(a2), java.util.Optional.ofNullable(a3));
    }

    /** was net/minecraft/class_2115$class_2117.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2019;Lnet/minecraft/class_5258;)V */
    public static net.minecraft.class_2115.class_2117 cbNewclass_2115class_2117(net.minecraft.class_5258 a0, net.minecraft.class_2019 a1, net.minecraft.class_5258 a2) {
        return new net.minecraft.class_2115.class_2117(java.util.Optional.ofNullable(a0), java.util.Optional.ofNullable(a1), java.util.Optional.ofNullable(a2));
    }

    /** was net/minecraft/class_2119$class_2121.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2960;)V */
    public static net.minecraft.class_2119.class_2121 cbNewclass_2119class_2121(net.minecraft.class_5258 a0, net.minecraft.class_2960 a1) {
        return new net.minecraft.class_2119.class_2121(java.util.Optional.of(a0), a1);
    }

    /** was net/minecraft/class_2123$class_2125.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2073;)V */
    public static net.minecraft.class_2123.class_2125 cbNewclass_2123class_2125(net.minecraft.class_5258 a0, net.minecraft.class_2073 a1) {
        return new net.minecraft.class_2123.class_2125(java.util.Optional.ofNullable(a0), java.util.Optional.ofNullable(a1));
    }

    /** was net/minecraft/class_2128$class_2130.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_5258;)V */
    public static net.minecraft.class_2128.class_2130 cbNewclass_2128class_2130(net.minecraft.class_5258 a0, net.minecraft.class_5258 a1) {
        return new net.minecraft.class_2128.class_2130(java.util.Optional.ofNullable(a0), java.util.Optional.ofNullable(a1));
    }

    /** was net/minecraft/class_2131$class_2133.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_5258;)V */
    public static net.minecraft.class_2131.class_2133 cbNewclass_2131class_2133(net.minecraft.class_5258 a0, net.minecraft.class_5258 a1) {
        return new net.minecraft.class_2131.class_2133(java.util.Optional.ofNullable(a0), java.util.Optional.ofNullable(a1));
    }

    /** was net/minecraft/class_2135$class_2137.<init>(Lnet/minecraft/class_2960;Lnet/minecraft/class_5258;)V */
    public static net.minecraft.class_2135.class_2137 cbNewclass_2135class_2137(net.minecraft.class_2960 a0, net.minecraft.class_5258 a1) {
        return new net.minecraft.class_2135.class_2137(java.util.Optional.ofNullable(a1));
    }

    /** was net/minecraft/class_2135.<init>(Lnet/minecraft/class_2960;)V */
    public static net.minecraft.class_2135 cbNewclass_2135(net.minecraft.class_2960 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the resource identifier constructor argument has no equivalent in the surviving no-argument constructor or API");
    }

    /** was net/minecraft/class_2140$class_2142.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_5258;Lnet/minecraft/class_2073;)V */
    public static net.minecraft.class_2140.class_2142 cbNewclass_2140class_2142(net.minecraft.class_5258 a0, net.minecraft.class_5258 a1, net.minecraft.class_2073 a2) {
        return new net.minecraft.class_2140.class_2142(java.util.Optional.ofNullable(a0), java.util.Optional.ofNullable(a1), java.util.Optional.ofNullable(a2));
    }

    /** was net/minecraft/class_2143$class_2145.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2096$class_2099;)V */
    public static net.minecraft.class_2143.class_2145 cbNewclass_2143class_2145(net.minecraft.class_5258 a0, net.minecraft.class_2096.class_2099 a1) {
        return new net.minecraft.class_2143.class_2145(java.util.Optional.ofNullable(a0), a1);
    }

    /** was net/minecraft/class_2148$class_2150.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2073;)V */
    public static net.minecraft.class_2148.class_2150 cbNewclass_2148class_2150(net.minecraft.class_5258 a0, net.minecraft.class_2073 a1) {
        return new net.minecraft.class_2148.class_2150(java.util.Optional.ofNullable(a0), java.util.Optional.ofNullable(a1));
    }

    /** was net/minecraft/class_223.field_1298Lnet/minecraft/class_2073; */
    public static net.minecraft.class_2073 field_1298() {
        throw new UnsupportedOperationException("CenturyBridge: the old field was instance-specific, but the zero-argument static shim has no receiver from which to obtain the new Optional-backed predicate");
    }

    /** was net/minecraft/class_2319.<init>(Ljava/util/function/Supplier;)V */
    public static net.minecraft.class_2319 cbNewclass_2319(java.util.function.Supplier a0) {
        return net.minecraft.class_2319.method_41999(a0);
    }

    /** was net/minecraft/class_2505.<init>(J)V */
    public static net.minecraft.class_2505 cbNewclass_2505(long a0) {
        return new net.minecraft.class_2505(a0, 0);
    }

    /** was net/minecraft/class_2588.<init>(Ljava/lang/String;[Ljava/lang/Object;)V */
    public static net.minecraft.class_2588 cbNewclass_2588(java.lang.String a0, java.lang.Object[] a1) {
        return new net.minecraft.class_2588(a0, a0, a1);
    }

    /** was net/minecraft/class_2588.<init>(Ljava/lang/String;)V */
    public static net.minecraft.class_2588 cbNewclass_2588(java.lang.String a0) {
        return new net.minecraft.class_2588(a0, a0, new Object[0]);
    }

    /** was net/minecraft/class_2724.<init>(Lnet/minecraft/class_5321;Lnet/minecraft/class_5321;JLnet/minecraft/class_1934;Lnet/minecraft/class_1934;ZZBLjava/util/Optional;I)V */
    public static net.minecraft.class_2724 cbNewclass_2724(net.minecraft.class_5321 a0, net.minecraft.class_5321 a1, long a2, net.minecraft.class_1934 a3, net.minecraft.class_1934 a4, boolean a5, boolean a6, byte a7, java.util.Optional a8, int a9) {
        throw new UnsupportedOperationException("CenturyBridge: the old constructor arguments cannot be converted to the required class_8589 payload using the provided API surface");
    }

    /** was net/minecraft/class_2889.<init>(Ljava/lang/String;ILnet/minecraft/class_2539;)V */
    public static net.minecraft.class_2889 cbNewclass_2889(java.lang.String a0, int a1, net.minecraft.class_2539 a2) {
        throw new UnsupportedOperationException("CenturyBridge: the old NetworkState argument cannot be converted to the new ConnectionIntent type using the available API");
    }


    /** was net/minecraft/class_2915.<init>(Ljava/lang/String;Ljava/util/Optional;)V */
    public static net.minecraft.class_2915 cbNewclass_2915(java.lang.String a0, java.util.Optional a1) {
        return new net.minecraft.class_2915(a0, (java.util.UUID) a1.orElse(null));
    }

    /** was net/minecraft/class_3222.<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/class_3218;Lcom/mojang/authlib/GameProfile;)V */
    public static net.minecraft.class_3222 cbNewclass_3222(net.minecraft.server.MinecraftServer a0, net.minecraft.class_3218 a1, com.mojang.authlib.GameProfile a2) {
        return new net.minecraft.class_3222(a0, a1, a2, net.minecraft.class_8791.method_53821());
    }

    /** was net/minecraft/class_3244.<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/class_2535;Lnet/minecraft/class_3222;)V */
    public static net.minecraft.class_3244 cbNewclass_3244(net.minecraft.server.MinecraftServer a0, net.minecraft.class_2535 a1, net.minecraft.class_3222 a2) {
        throw new UnsupportedOperationException("CenturyBridge: the surviving constructor requires a class_8792 argument that cannot be derived faithfully from the old constructor arguments");
    }

    /** was net/minecraft/class_3255.<init>(Ljava/io/File;)V */
    public static net.minecraft.class_3255 cbNewclass_3255(java.io.File a0) {
        throw new UnsupportedOperationException("CenturyBridge: the abstract class has no instantiable factory, and the File argument cannot supply the required alwaysStable constructor value");
    }

    /** was net/minecraft/class_3258.<init>(Ljava/lang/String;Ljava/io/File;Z)V */
    public static net.minecraft.class_3258 cbNewclass_3258(java.lang.String a0, java.io.File a1, boolean a2) {
        throw new UnsupportedOperationException("CenturyBridge: the surviving constructor requires the inaccessible private class_3258.class_8616 wrapper, and no public factory accepts the old File argument");
    }

    /** was net/minecraft/class_3258.<init>(Ljava/io/File;)V */
    public static net.minecraft.class_3258 cbNewclass_3258(java.io.File a0) {
        throw new UnsupportedOperationException("CenturyBridge: the surviving constructor requires an inaccessible class_3258.class_8616 wrapper, and no equivalent static factory exists");
    }

    /** was net/minecraft/class_3259.<init>(Ljava/io/File;)V */
    public static net.minecraft.class_3259 cbNewclass_3259(java.io.File a0) {
        return new net.minecraft.class_3259(a0.getName(), a0.toPath(), false);
    }

    /** was net/minecraft/class_3272.<init>(Lnet/minecraft/class_2561;I)V */
    public static net.minecraft.class_3272 cbNewclass_3272(net.minecraft.class_2561 a0, int a1) {
        return new net.minecraft.class_3272(a0, a1, java.util.Optional.empty());
    }

    /** was net/minecraft/class_3279.<init>(Ljava/nio/file/Path;Lnet/minecraft/class_3264;Lnet/minecraft/class_5352;)V */
    public static net.minecraft.class_3279 cbNewclass_3279(java.nio.file.Path a0, net.minecraft.class_3264 a1, net.minecraft.class_5352 a2) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructor requires a class_8580 argument that cannot be derived from the old constructor arguments");
    }

    /** was net/minecraft/class_3283.<init>(Lnet/minecraft/class_3264;[Lnet/minecraft/class_3285;)V */
    public static net.minecraft.class_3283 cbNewclass_3283(net.minecraft.class_3264 a0, net.minecraft.class_3285[] a1) {
        return new net.minecraft.class_3283(a1);
    }

    /** was net/minecraft/class_3286.<init>()V */
    public static net.minecraft.class_3286 cbNewclass_3286() {
        throw new UnsupportedOperationException("CenturyBridge: the surviving constructor requires a class_8580 instance, but the old no-argument constructor provides no equivalent value");
    }

    /** was net/minecraft/class_3288.<init>(Ljava/lang/String;Lnet/minecraft/class_2561;ZLjava/util/function/Supplier;Lnet/minecraft/class_3272;Lnet/minecraft/class_3264;Lnet/minecraft/class_3288$class_3289;Lnet/minecraft/class_5352;)V */
    public static net.minecraft.class_3288 cbNewclass_3288(java.lang.String a0, net.minecraft.class_2561 a1, boolean a2, java.util.function.Supplier a3, net.minecraft.class_3272 a4, net.minecraft.class_3264 a5, net.minecraft.class_3288.class_3289 a6, net.minecraft.class_5352 a7) {
        return net.minecraft.class_3288.method_45275(a0, a1, a2, new net.minecraft.class_3288.class_7680() {
            public net.minecraft.class_3262 method_52424(java.lang.String name) {
                return (net.minecraft.class_3262) a3.get();
            }
        
            public net.minecraft.class_3262 method_52425(java.lang.String name, net.minecraft.class_3288.class_7679 metadata) {
                return (net.minecraft.class_3262) a3.get();
            }
        }, a5, a6, a7);
    }

    /** was net/minecraft/class_329.field_44654Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_44654() {
        return new net.minecraft.class_2960("textures/gui/icons.png");
    }

    /** was net/minecraft/class_332.<init>()V */
    public static net.minecraft.class_332 cbNewclass_332() {
        throw new UnsupportedOperationException("CenturyBridge: the new constructors require a Minecraft client and vertex consumer provider, but the old zero-argument constructor supplies neither");
    }

    /** was net/minecraft/class_339.field_22757Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22757() {
        return new net.minecraft.class_2960("textures/gui/widgets.png");
    }

    /** was net/minecraft/class_344.<init>(IIIIIIILnet/minecraft/class_2960;IILnet/minecraft/class_4185$class_4241;)V */
    public static net.minecraft.class_344 cbNewclass_344(int a0, int a1, int a2, int a3, int a4, int a5, int a6, net.minecraft.class_2960 a7, int a8, int a9, net.minecraft.class_4185.class_4241 a10) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_8666 sprite-based constructor cannot represent the old texture atlas UV coordinates and dimensions");
    }

    /** was net/minecraft/class_344.<init>(IIIIIIILnet/minecraft/class_2960;Lnet/minecraft/class_4185$class_4241;)V */
    public static net.minecraft.class_344 cbNewclass_344(int a0, int a1, int a2, int a3, int a4, int a5, int a6, net.minecraft.class_2960 a7, net.minecraft.class_4185.class_4241 a8) {
        throw new UnsupportedOperationException("CenturyBridge: the old texture-atlas UV and hover-offset contract cannot be represented by the new class_8666 sprite-based constructor");
    }

    /** was net/minecraft/class_344.<init>(IIIIIIILnet/minecraft/class_2960;IILnet/minecraft/class_4185$class_4241;Lnet/minecraft/class_2561;)V */
    public static net.minecraft.class_344 cbNewclass_344(int a0, int a1, int a2, int a3, int a4, int a5, int a6, net.minecraft.class_2960 a7, int a8, int a9, net.minecraft.class_4185.class_4241 a10, net.minecraft.class_2561 a11) {
        throw new UnsupportedOperationException("CenturyBridge: the new class_8666 sprite API cannot represent the old texture atlas UV coordinates and dimensions");
    }

    /** was net/minecraft/class_344.<init>(IIIIIILnet/minecraft/class_2960;Lnet/minecraft/class_4185$class_4241;)V */
    public static net.minecraft.class_344 cbNewclass_344(int a0, int a1, int a2, int a3, int a4, int a5, net.minecraft.class_2960 a6, net.minecraft.class_4185.class_4241 a7) {
        throw new UnsupportedOperationException("CenturyBridge: the old texture coordinates and identifier cannot be converted to the required class_8666 without its construction API");
    }

    /** was net/minecraft/class_3975.<init>(Lnet/minecraft/class_2960;Ljava/lang/String;Lnet/minecraft/class_1856;Lnet/minecraft/class_1799;)V */
    public static net.minecraft.class_3975 cbNewclass_3975(net.minecraft.class_2960 a0, java.lang.String a1, net.minecraft.class_1856 a2, net.minecraft.class_1799 a3) {
        return new net.minecraft.class_3975(a1, a2, a3);
    }


    /** was net/minecraft/class_4708$class_4709.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2248;Lnet/minecraft/class_2073;Lnet/minecraft/class_2096$class_2100;)V */
    public static net.minecraft.class_4708.class_4709 cbNewclass_4708class_4709(net.minecraft.class_5258 a0, net.minecraft.class_2248 a1, net.minecraft.class_2073 a2, net.minecraft.class_2096.class_2100 a3) {
        return new net.minecraft.class_4708.class_4709(java.util.Optional.ofNullable(a0), a1, java.util.Optional.ofNullable(a2), a3);
    }

    /** was net/minecraft/class_4713$class_4714.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2248;Lnet/minecraft/class_4559;)V */
    public static net.minecraft.class_4713.class_4714 cbNewclass_4713class_4714(net.minecraft.class_5258 a0, net.minecraft.class_2248 a1, net.minecraft.class_4559 a2) {
        return new net.minecraft.class_4713.class_4714(java.util.Optional.ofNullable(a0), a1, java.util.Optional.ofNullable(a2));
    }

    /** was net/minecraft/class_4851$class_4852.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2096$class_2100;Lnet/minecraft/class_5258;)V */
    public static net.minecraft.class_4851.class_4852 cbNewclass_4851class_4852(net.minecraft.class_5258 a0, net.minecraft.class_2096.class_2100 a1, net.minecraft.class_5258 a2) {
        return new net.minecraft.class_4851.class_4852(java.util.Optional.ofNullable(a0), a1, java.util.Optional.ofNullable(a2));
    }


    /** was net/minecraft/class_5279$class_5280.<init>(Lnet/minecraft/class_2960;Lnet/minecraft/class_5258;Lnet/minecraft/class_2073;Lnet/minecraft/class_5258;)V */
    public static net.minecraft.class_5279.class_5280 cbNewclass_5279class_5280(net.minecraft.class_2960 a0, net.minecraft.class_5258 a1, net.minecraft.class_2073 a2, net.minecraft.class_5258 a3) {
        return new net.minecraft.class_5279.class_5280(java.util.Optional.ofNullable(a1), java.util.Optional.ofNullable(a2), java.util.Optional.ofNullable(a3));
    }

    /** was net/minecraft/class_5409$class_5410.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2073;Lnet/minecraft/class_5258;)V */
    public static net.minecraft.class_5409.class_5410 cbNewclass_5409class_5410(net.minecraft.class_5258 a0, net.minecraft.class_2073 a1, net.minecraft.class_5258 a2) {
        return new net.minecraft.class_5409.class_5410(java.util.Optional.ofNullable(a0), java.util.Optional.ofNullable(a1), java.util.Optional.ofNullable(a2));
    }



    /** was net/minecraft/class_6405$class_6406.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_5258;Lnet/minecraft/class_5258;)V */
    public static net.minecraft.class_6405.class_6406 cbNewclass_6405class_6406(net.minecraft.class_5258 a0, net.minecraft.class_5258 a1, net.minecraft.class_5258 a2) {
        return new net.minecraft.class_6405.class_6406(java.util.Optional.ofNullable(a0), java.util.Optional.ofNullable(a1), java.util.Optional.ofNullable(a2));
    }

    /** was net/minecraft/class_6407$class_6408.<init>(Lnet/minecraft/class_5258;)V */
    public static net.minecraft.class_6407.class_6408 cbNewclass_6407class_6408(net.minecraft.class_5258 a0) {
        return new net.minecraft.class_6407.class_6408(java.util.Optional.ofNullable(a0));
    }

    /** was net/minecraft/class_6409$class_6410.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2073;)V */
    public static net.minecraft.class_6409.class_6410 cbNewclass_6409class_6410(net.minecraft.class_5258 a0, net.minecraft.class_2073 a1) {
        return new net.minecraft.class_6409.class_6410(java.util.Optional.of(a0), java.util.Optional.of(a1));
    }

    /** was net/minecraft/class_7193.<init>(Lnet/minecraft/class_5285;Lcom/mojang/serialization/Lifecycle;Lnet/minecraft/class_5455$class_6890;Lnet/minecraft/class_5350;)V */
    public static net.minecraft.class_7193 cbNewclass_7193(net.minecraft.class_5285 a0, com.mojang.serialization.Lifecycle a1, net.minecraft.class_5455.class_6890 a2, net.minecraft.class_5350 a3) {
        throw new UnsupportedOperationException("CenturyBridge: the new constructors require class_7723, class_7780, and class_7712 values that cannot be derived from the old Lifecycle and class_5455.class_6890 arguments");
    }

    /** was net/minecraft/class_8060.<init>(Lnet/minecraft/class_2960;Lnet/minecraft/class_1856;Lnet/minecraft/class_1856;Lnet/minecraft/class_1856;Lnet/minecraft/class_1799;)V */
    public static net.minecraft.class_8060 cbNewclass_8060(net.minecraft.class_2960 a0, net.minecraft.class_1856 a1, net.minecraft.class_1856 a2, net.minecraft.class_1856 a3, net.minecraft.class_1799 a4) {
        return new net.minecraft.class_8060(a1, a2, a3, a4);
    }

    /** was net/minecraft/class_8208.<init>(IILnet/minecraft/class_2960;)V */
    public static net.minecraft.class_8208 cbNewclass_8208(int a0, int a1, net.minecraft.class_2960 a2) {
        return net.minecraft.class_8208.method_52720(a0, a1, a2);
    }

    /** was net/minecraft/class_8208.<init>(IIIILnet/minecraft/class_2960;)V */
    public static net.minecraft.class_8208 cbNewclass_8208(int a0, int a1, int a2, int a3, net.minecraft.class_2960 a4) {
        net.minecraft.class_8208 widget = net.minecraft.class_8208.method_52720(a2, a3, a4);
        widget.method_46421(a0);
        widget.method_46419(a1);
        return widget;
    }

    /** was net/minecraft/class_8508$class_8509.<init>(Lnet/minecraft/class_5258;Lnet/minecraft/class_2960;Ljava/util/List;)V */
    public static net.minecraft.class_8508.class_8509 cbNewclass_8508class_8509(net.minecraft.class_5258 a0, net.minecraft.class_2960 a1, java.util.List a2) {
        return new net.minecraft.class_8508.class_8509(java.util.Optional.ofNullable(a0), a1, a2);
    }

    // ==== END GENERATED ====

    private Statics() {
    }

    /**
     * superRedirects: a 1.20.1 override's super.render(context, mx, my, delta).
     * 1.20.1 Screen.render drew the widget list and nothing else; since 1.20.2
     * it draws the background first, over everything the mod just drew. Render
     * exactly what the old contract promised.
     */
    public static void cbRenderWidgets(net.minecraft.class_437 self, net.minecraft.class_332 context, int mouseX, int mouseY, float delta) {
        for (net.minecraft.class_4068 d : ((top.dext.centurybridge.shims.v1_20_2.ScreenDrawablesAccessor) self).cbDrawables()) {
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
}
