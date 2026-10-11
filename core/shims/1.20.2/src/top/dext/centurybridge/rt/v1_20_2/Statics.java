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

    /** 1.20.1 LootContextPredicate.EMPTY field, removed 1.20.2; field access redirected here.
     *  1.20.2 rejects a predicate with zero conditions (IllegalArgumentException in <init> -- which
     *  killed this whole class's <clinit> and with it every bridge routed through Statics, caught by
     *  the real-client fixture). A RandomChance(1.0) condition is the faithful match-all. */
    public static final net.minecraft.class_5258 field_24388 =
        net.minecraft.class_5258.method_27973(new net.minecraft.class_219(1.0f));

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

    /** was net/minecraft/class_332.<init>()V -- a real DrawContext over the client's entity vertex consumers */
    public static net.minecraft.class_332 cbNewclass_332() {
        net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
        return new net.minecraft.class_332(mc, mc.method_22940().method_23000());
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

    // =====================================================================
    // 1.20.2 kill-list closure batch (hand-written). Intermediary names;
    // bodies target the surviving 1.20.2 API.
    // =====================================================================

    // ---- Player skins: 1.20.2 folded the per-texture getters into the SkinTextures record
    // (class_8685: texture, textureUrl, cape, elytra, model, secure).
    /** was net/minecraft/class_742.method_3117()Lnet/minecraft/class_2960; getSkinTexture */
    public static net.minecraft.class_2960 method_3117(net.minecraft.class_742 self) { return self.method_52814().comp_1626(); }
    /** was net/minecraft/class_746.method_3117()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_3117(net.minecraft.class_746 self) { return self.method_52814().comp_1626(); }
    /** was net/minecraft/class_742.method_3121()Ljava/lang/String; getModel */
    public static String method_3121(net.minecraft.class_742 self) { return self.method_52814().comp_1629().method_52856(); }
    /** was net/minecraft/class_746.method_3121()Ljava/lang/String; */
    public static String method_3121(net.minecraft.class_746 self) { return self.method_52814().comp_1629().method_52856(); }
    /** was net/minecraft/class_742.method_3119()Lnet/minecraft/class_2960; getCapeTexture */
    public static net.minecraft.class_2960 method_3119(net.minecraft.class_742 self) { return self.method_52814().comp_1627(); }
    /** was net/minecraft/class_742.method_3122()Lnet/minecraft/class_2960; getElytraTexture */
    public static net.minecraft.class_2960 method_3122(net.minecraft.class_742 self) { return self.method_52814().comp_1628(); }
    /** was net/minecraft/class_742.method_3125()Z isCapeLoaded */
    public static boolean method_3125(net.minecraft.class_742 self) { return self.method_52814().comp_1627() != null; }
    /** was net/minecraft/class_742.method_3126()Z isElytraLoaded */
    public static boolean method_3126(net.minecraft.class_742 self) { return self.method_52814().comp_1628() != null; }
    /** was net/minecraft/class_742.method_3127()Z isSkinLoaded */
    public static boolean method_3127(net.minecraft.class_742 self) { return self.method_52814().comp_1626() != null; }
    /** was net/minecraft/class_640.method_2968()Lnet/minecraft/class_2960; PlayerListEntry.getSkinTexture */
    public static net.minecraft.class_2960 method_2968(net.minecraft.class_640 self) { return self.method_52810().comp_1626(); }
    /** was net/minecraft/class_640.method_2957()Lnet/minecraft/class_2960; getElytraTexture */
    public static net.minecraft.class_2960 method_2957(net.minecraft.class_640 self) { return self.method_52810().comp_1628(); }
    /** was net/minecraft/class_640.method_2979()Lnet/minecraft/class_2960; getCapeTexture */
    public static net.minecraft.class_2960 method_2979(net.minecraft.class_640 self) { return self.method_52810().comp_1627(); }
    /** was net/minecraft/class_640.method_2977()Ljava/lang/String; getModel */
    public static String method_2977(net.minecraft.class_640 self) { return self.method_52810().comp_1629().method_52856(); }
    /** was net/minecraft/class_1068.method_4647(Ljava/util/UUID;)Ljava/lang/String; DefaultSkinHelper.getModel */
    public static String method_4647(java.util.UUID uuid) { return net.minecraft.class_1068.method_4648(uuid).comp_1629().method_52856(); }
    /** was net/minecraft/class_1071.method_4654(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map; PlayerSkinProvider.getTextures.
     *  Rebuilt from the synchronous SkinTextures: SKIN carries the real URL + model metadata; CAPE/ELYTRA
     *  entries are present when the texture is, with the registered texture id standing in for a URL. */
    public static java.util.Map<com.mojang.authlib.minecraft.MinecraftProfileTexture.Type, com.mojang.authlib.minecraft.MinecraftProfileTexture> method_4654(net.minecraft.class_1071 self, com.mojang.authlib.GameProfile profile) {
        net.minecraft.class_8685 t = self.method_52862(profile);
        java.util.Map<com.mojang.authlib.minecraft.MinecraftProfileTexture.Type, com.mojang.authlib.minecraft.MinecraftProfileTexture> m =
            new java.util.EnumMap<>(com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.class);
        if (t.comp_1911() != null) {
            m.put(com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN,
                new com.mojang.authlib.minecraft.MinecraftProfileTexture(t.comp_1911(), java.util.Map.of("model", t.comp_1629().method_52856())));
        }
        if (t.comp_1627() != null) {
            m.put(com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.CAPE,
                new com.mojang.authlib.minecraft.MinecraftProfileTexture(t.comp_1627().toString(), null));
        }
        if (t.comp_1628() != null) {
            m.put(com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.ELYTRA,
                new com.mojang.authlib.minecraft.MinecraftProfileTexture(t.comp_1628().toString(), null));
        }
        return m;
    }
    /** was net/minecraft/class_320.method_1673()Ljava/lang/String; Session.getUuid (1.20.2 stores a UUID) */
    public static String method_1673(net.minecraft.class_320 self) { java.util.UUID u = self.method_44717(); return u == null ? "" : u.toString(); }
    /** was net/minecraft/class_320.method_1677()Lcom/mojang/authlib/GameProfile; Session.getProfile */
    public static com.mojang.authlib.GameProfile method_1677(net.minecraft.class_320 self) {
        return new com.mojang.authlib.GameProfile(self.method_44717(), self.method_1676());
    }

    // ---- ClientWorld.addEntity(int id, Entity) -> addEntity(Entity): the id is already on the entity.
    /** was net/minecraft/class_638.method_2942(ILnet/minecraft/class_1297;)V */
    public static void method_2942(net.minecraft.class_638 self, int id, net.minecraft.class_1297 e) { self.method_53875(e); }

    // ---- Advancements: 1.20.2 wraps Advancement in AdvancementEntry(id, advancement) and the
    // client tree in PlacedAdvancement.
    /** was net/minecraft/class_161$class_162.method_695(Lnet/minecraft/class_2960;)Lnet/minecraft/class_161; Builder.build(id) */
    public static net.minecraft.class_161 method_695(net.minecraft.class_161.class_162 self, net.minecraft.class_2960 id) {
        return self.method_695(id).comp_1920();
    }
    /** was net/minecraft/class_161$class_162.method_710()Ljava/util/Map; Builder.getCriteria (accessor on the ImmutableMap builder) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static java.util.Map method_710(net.minecraft.class_161.class_162 self) {
        return ((top.dext.centurybridge.shims.v1_20_2.CbAdvancementBuilderCriteria) (Object) self).cbCriteria().build();
    }
    /** was net/minecraft/class_632.method_2864(Lnet/minecraft/class_161;Z)V selectTab(Advancement, boolean): locate its entry in the tree */
    public static void method_2864(net.minecraft.class_632 self, net.minecraft.class_161 adv, boolean b) {
        for (net.minecraft.class_8781 p : self.method_53814().method_53693()) {
            if (p.method_53647() == adv) { self.method_2864(p.method_53649(), b); return; }
        }
        self.method_2864((net.minecraft.class_8779) null, b);
    }
    /** was net/minecraft/class_163.method_712()Ljava/util/Collection; AdvancementManager.getAdvancements */
    public static java.util.Collection<net.minecraft.class_161> method_712(net.minecraft.class_163 self) {
        java.util.List<net.minecraft.class_161> out = new java.util.ArrayList<>();
        for (net.minecraft.class_8781 p : self.method_53693()) out.add(p.method_53647());
        return out;
    }
    /** was net/minecraft/class_455.method_2320()I AdvancementObtainedStatus.getSpriteIndex: OBTAINED=0, UNOBTAINED=1 (== ordinal) */
    public static int method_2320(net.minecraft.class_455 self) { return self.ordinal(); }

    // ---- Registry-derived names that lost their getters.
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
        return top.dext.centurybridge.shims.v1_20_2.CbLootContextTypesAccessor.cbRegistry().inverse().get(set);
    }

    // ---- Entity.getMountedHeightOffset removed (1.20.2 moved to passenger attachment points);
    // the 1.20.1 default was height * 0.75 (horses used the same formula).
    /** was net/minecraft/class_1297.method_5621()D */
    public static double method_5621(net.minecraft.class_1297 self) { return self.method_17682() * 0.75; }
    /** was net/minecraft/class_1496.method_5621()D */
    public static double method_5621(net.minecraft.class_1496 self) { return self.method_17682() * 0.75; }
    /** was net/minecraft/class_1498.method_5621()D */
    public static double method_5621(net.minecraft.class_1498 self) { return self.method_17682() * 0.75; }

    // ---- Predicates / criteria: builders became Optional-typed, factories return AdvancementCriterion.
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

    // ---- Recipes: json read via the serializer codec; ghost recipe via the client recipe manager.
    /** was net/minecraft/class_1867$class_1868.method_8142(Lnet/minecraft/class_2960;Lcom/google/gson/JsonObject;)Lnet/minecraft/class_1867; */
    public static net.minecraft.class_1867 method_8142(net.minecraft.class_1867.class_1868 self, net.minecraft.class_2960 id, com.google.gson.JsonObject json) {
        return self.method_53736().parse(JsonOps.INSTANCE, json).result()
            .orElseThrow(() -> new JsonSyntaxException("CenturyBridge: invalid shapeless recipe " + id));
    }
    /** was net/minecraft/class_505.method_2565(Lnet/minecraft/class_1860;)V RecipeBookWidget.showGhostRecipe(Recipe): find its RecipeEntry */
    public static void method_2565(net.minecraft.class_505 self, net.minecraft.class_1860<?> recipe) {
        net.minecraft.class_638 world = net.minecraft.class_310.method_1551().field_1687;
        if (world == null) return;
        for (net.minecraft.class_8786<?> e : world.method_8433().method_8126()) {
            if (e.comp_1933() == recipe) { self.method_2565(e); return; }
        }
    }

    // ---- DrawContext nine-slice / repeating textures. 1.20.2 replaced them with sprite-atlas gui
    // textures (drawGuiTexture + Scaling metadata) that take a sprite id, not a texture path, so the
    // faithful bridge is the 1.20.1 algorithm on top of the surviving 7-arg drawTexture (method_25302).
    /** was net/minecraft/class_332.method_48586(Lnet/minecraft/class_2960;IIIIIIIII)V drawNineSlicedTexture(outer) */
    public static void method_48586(net.minecraft.class_332 self, net.minecraft.class_2960 tex, int x, int y, int w, int h, int outer, int cw, int ch, int u, int v) {
        method_48587(self, tex, x, y, w, h, outer, outer, outer, outer, cw, ch, u, v);
    }
    /** was net/minecraft/class_332.method_49697(Lnet/minecraft/class_2960;IIIIIIIIII)V drawNineSlicedTexture(sideW, sideH) */
    public static void method_49697(net.minecraft.class_332 self, net.minecraft.class_2960 tex, int x, int y, int w, int h, int sw, int sh, int cw, int ch, int u, int v) {
        method_48587(self, tex, x, y, w, h, sw, sh, sw, sh, cw, ch, u, v);
    }
    /** was net/minecraft/class_332.method_48587(Lnet/minecraft/class_2960;IIIIIIIIIIII)V drawNineSlicedTexture(left, top, right, bottom) */
    public static void method_48587(net.minecraft.class_332 self, net.minecraft.class_2960 tex, int x, int y, int w, int h,
                                    int left, int top, int right, int bottom, int cw, int ch, int u, int v) {
        left = Math.min(left, w / 2);
        right = Math.min(right, w / 2);
        top = Math.min(top, h / 2);
        bottom = Math.min(bottom, h / 2);
        if (w == cw && h == ch) { self.method_25302(tex, x, y, u, v, w, h); return; }
        if (h == ch) {
            self.method_25302(tex, x, y, u, v, left, h);
            method_48585(self, tex, x + left, y, w - right - left, h, u + left, v, cw - right - left, ch);
            self.method_25302(tex, x + w - right, y, u + cw - right, v, right, h);
            return;
        }
        if (w == cw) {
            self.method_25302(tex, x, y, u, v, w, top);
            method_48585(self, tex, x, y + top, w, h - bottom - top, u, v + top, cw, ch - bottom - top);
            self.method_25302(tex, x, y + h - bottom, u, v + ch - bottom, w, bottom);
            return;
        }
        self.method_25302(tex, x, y, u, v, left, top);
        method_48585(self, tex, x + left, y, w - right - left, top, u + left, v, cw - right - left, top);
        self.method_25302(tex, x + w - right, y, u + cw - right, v, right, top);
        self.method_25302(tex, x, y + h - bottom, u, v + ch - bottom, left, bottom);
        method_48585(self, tex, x + left, y + h - bottom, w - right - left, bottom, u + left, v + ch - bottom, cw - right - left, bottom);
        self.method_25302(tex, x + w - right, y + h - bottom, u + cw - right, v + ch - bottom, right, bottom);
        method_48585(self, tex, x, y + top, left, h - bottom - top, u, v + top, left, ch - bottom - top);
        method_48585(self, tex, x + left, y + top, w - right - left, h - bottom - top, u + left, v + top, cw - right - left, ch - bottom - top);
        method_48585(self, tex, x + w - right, y + top, right, h - bottom - top, u + cw - right, v + top, right, ch - bottom - top);
    }
    /** was net/minecraft/class_332.method_48585(Lnet/minecraft/class_2960;IIIIIIII)V drawRepeatingTexture */
    public static void method_48585(net.minecraft.class_332 self, net.minecraft.class_2960 tex, int x, int y, int w, int h, int u, int v, int tw, int th) {
        int i = x;
        for (int j : cbDivide(w, tw)) {
            int k = (tw - j) / 2;
            int l = y;
            for (int m : cbDivide(h, th)) {
                int n = (th - m) / 2;
                self.method_25302(tex, i, l, u + k, v + n, j, m);
                l += m;
            }
            i += j;
        }
    }
    /** 1.20.1 Divider(total, ceilDiv(total, piece)): even split, remainder spread one pixel at a time. */
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

    // ---- super(...) argument adapters for 1.20.2 constructor reshapes (engine superCtorAdapts:
    // a static converter runs on the stack tail right before INVOKESPECIAL <init>).
    /** SpecialCraftingRecipe(Identifier, category) -> (category): the id moved to RecipeEntry. */
    public static net.minecraft.class_7710 cbDropRecipeId(net.minecraft.class_2960 id, net.minecraft.class_7710 category) { return category; }
    /** AbstractCriterionConditions(Identifier, LootContextPredicate) -> (Optional<LootContextPredicate>) */
    public static java.util.Optional<net.minecraft.class_5258> cbCriterionPlayer(net.minecraft.class_2960 id, net.minecraft.class_5258 p) { return java.util.Optional.ofNullable(p); }
    /** ServerPlayerEntity(server, world, profile) gained SyncedClientOptions */
    public static net.minecraft.class_8791 cbDefaultClientOptions() { return net.minecraft.class_8791.method_53821(); }
    /** ButtonWidget(x, y, w, h, text, action) gained a NarrationSupplier; the vanilla default narrates the supplied message */
    public static net.minecraft.class_4185.class_7841 cbDefaultNarration() { return s -> s.get(); }

    /** was net/minecraft/class_4185.<init>(IIIILnet/minecraft/class_2561;Lnet/minecraft/class_4185$class_4241;)V -- the ctor is
     *  protected since 1.19.3; Button.builder is the public path */
    public static net.minecraft.class_4185 cbNewclass_4185(int x, int y, int w, int h, net.minecraft.class_2561 text, net.minecraft.class_4185.class_4241 action) {
        return net.minecraft.class_4185.method_46430(text, action).method_46434(x, y, w, h).method_46431();
    }

    // ---- static-signature migrations: old return type recovered by unwrapping
    //      the 1.20.2 wrapper record (wiring-check closure cycle 64) ----

    /** 1.20.1 SynchronizeRecipesS2CPacket.readRecipe(buf) returned the Recipe directly;
     *  1.20.2 made it private and returns a RecipeEntry (class_8786). The @Invoker on
     *  GenClass2788Bridge reaches the private reader; comp_1933() is the wrapped recipe. */
    public static net.minecraft.class_1860<?> method_17817(net.minecraft.class_2540 buf) {
        net.minecraft.class_8786<?> entry =
            top.dext.centurybridge.shims.v1_20_2.GenClass2788Bridge.cbReadRecipeEntry(buf);
        return entry == null ? null : entry.comp_1933();
    }

    /** 1.20.1 EntityPredicate.Builder static builder returned the Builder directly;
     *  1.20.2 wraps it in a class_175 record. comp_1924() is the wrapped Builder. */
    public static net.minecraft.class_2080.class_2083 method_8997(net.minecraft.class_2048.class_2049 arg) {
        net.minecraft.class_175<net.minecraft.class_2080.class_2083> w = net.minecraft.class_2080.class_2083.method_8997(arg);
        return w == null ? null : w.comp_1924();
    }

    // ---- 1.20.1 -> 1.20.2 dead constructors (wiring-check closure cycle 62) ----

    /** was ServerboundCustomPayloadPacket(Identifier, PacketByteBuf): 1.20.2 packets
     *  carry a CustomPayload object. CbRawPayload wraps the old id + already-encoded
     *  bytes verbatim, byte-for-byte what the 1.20.1 packet put on the wire. */
    public static net.minecraft.class_2817 cbNewclass_2817(net.minecraft.class_2960 id, net.minecraft.class_2540 data) {
        return new net.minecraft.class_2817(new CbRawPayload(id, data));
    }

    /** LootConditionType(JsonSerializer) -> Codec: a legacy serializer is a codec in
     *  disguise (CbLootJson bridges toJson/fromJson both ways). */
    public static net.minecraft.class_5342 cbNewclass_5342(top.dext.centurybridge.facade.v1_20_2.class_5335 serializer) {
        return new net.minecraft.class_5342(CbLootJson.codec(serializer));
    }

    /** LootFunctionType(JsonSerializer) -> Codec */
    public static net.minecraft.class_5339 cbNewclass_5339(top.dext.centurybridge.facade.v1_20_2.class_5335 serializer) {
        return new net.minecraft.class_5339(CbLootJson.codec(serializer));
    }

    /** LootPoolEntryType(JsonSerializer) -> Codec */
    public static net.minecraft.class_5338 cbNewclass_5338(top.dext.centurybridge.facade.v1_20_2.class_5335 serializer) {
        return new net.minecraft.class_5338(CbLootJson.codec(serializer));
    }

    /** AdvancementRewards-area LootContextPredicate wrapper: 1.20.2's class_195 is
     *  abstract -- no faithful standalone construction. */
    public static net.minecraft.class_195 cbNewclass_195(net.minecraft.class_2960 id, net.minecraft.class_5258 pred) {
        throw new UnsupportedOperationException("CenturyBridge: class_195 became abstract in 1.20.2; the old concrete (Identifier, LootContextPredicate) ctor has no faithful standalone equivalent");
    }

    /** CraftingRecipe-category ctor: 1.20.2's class_1852 is abstract with new
     *  abstract methods a legacy call cannot satisfy. */
    public static net.minecraft.class_1852 cbNewclass_1852(net.minecraft.class_2960 id, net.minecraft.class_7710 category) {
        throw new UnsupportedOperationException("CenturyBridge: SpecialCraftingRecipe is abstract in 1.20.2 and its id moved to the registry key; the old (Identifier, category) ctor cannot be reconstructed here");
    }

    /** GameEvent(String name, int range) -> 1.20.2 takes range only (the registry
     *  holds the name). */
    public static net.minecraft.class_5712 cbNewclass_5712(String name, int range) {
        return new net.minecraft.class_5712(range);
    }

    /** AbstractCookingRecipe: 1.20.2's class_1874 is abstract (CookingRecipeSerializer
     *  builds concrete subtypes); no faithful standalone construction. */
    public static net.minecraft.class_1874 cbNewclass_1874(net.minecraft.class_3956 type, net.minecraft.class_2960 id,
            String group, net.minecraft.class_7709 category, net.minecraft.class_1856 ingredient,
            net.minecraft.class_1799 result, float xp, int cookTime) {
        throw new UnsupportedOperationException("CenturyBridge: AbstractCookingRecipe is abstract in 1.20.2 and its id moved to the registry key; the old concrete ctor has no faithful equivalent");
    }

    // ---- unbridgeable dead ctors: tombstone stubs (mod loads; lazy-fails only if
    //      a mod actually constructs these vanilla-internal screens/packets) ----

    /** ServerboundCustomQueryAnswerPacket(int, PacketByteBuf): 1.20.2 takes a typed
     *  QueryAnswerPayload (class_8594) with no lossless path from raw bytes. */
    public static net.minecraft.class_2913 cbNewclass_2913(int id, net.minecraft.class_2540 data) {
        throw new UnsupportedOperationException("CenturyBridge: ServerboundCustomQueryAnswerPacket became a typed QueryAnswerPayload in 1.20.2; raw PacketByteBuf has no faithful conversion");
    }

    /** SimpleOptionsScreen / CreditsScreen ctors are unbridgeable (deleted/package-private
     *  arg types); their ctorRedirect entries were removed and ledgered instead. */

    /** EnchantmentScreen-family ctor gained two extra Identifier args in 1.20.2. */
    public static net.minecraft.class_489 cbNewclass_489(net.minecraft.class_1720 handler, net.minecraft.class_517 a, net.minecraft.class_1661 inv, net.minecraft.class_2561 title, net.minecraft.class_2960 tex) {
        throw new UnsupportedOperationException("CenturyBridge: this screen's 1.20.2 ctor requires two additional texture Identifiers not present in the 1.20.1 call");
    }

    /** ClientPlayNetworkHandler ctor collapsed its args into a single class_8675
     *  ClientConnectionState in 1.20.2. */
    public static net.minecraft.class_634 cbNewclass_634(net.minecraft.class_310 mc, net.minecraft.class_437 screen, net.minecraft.class_2535 conn, net.minecraft.class_642 server, com.mojang.authlib.GameProfile profile, net.minecraft.class_7975 cookie) {
        throw new UnsupportedOperationException("CenturyBridge: ClientPlayNetworkHandler's 1.20.2 ctor takes a ClientConnectionState (class_8675) that bundles state not reconstructable from the old separate args");
    }

    /** was net/minecraft/class_1068.method_4648(Ljava/util/UUID;)Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_4648(java.util.UUID a0) {
        return net.minecraft.class_1068.method_4648(a0).comp_1626();
    }

    /** was net/minecraft/class_2069$class_2071.method_8967(Lnet/minecraft/class_5258;Lnet/minecraft/class_2073;Lnet/minecraft/class_2096$class_2100;)Lnet/minecraft/class_2069$class_2071; */
    public static net.minecraft.class_2069.class_2071 method_8967(net.minecraft.class_5258 a0, net.minecraft.class_2073 a1, net.minecraft.class_2096.class_2100 a2) {
        throw new UnsupportedOperationException("CenturyBridge: the new factory returns a criterion wrapper rather than Conditions, and the provided API exposes no faithful way to unwrap it or supply the old factory's default delta range");
    }

    /** was net/minecraft/class_2102.method_9064(Lcom/google/gson/JsonElement;)Lnet/minecraft/class_2102; */
    public static net.minecraft.class_2102 method_9064(com.google.gson.JsonElement a0) {
        return net.minecraft.class_2102.method_9064(a0).orElseGet(() -> new net.minecraft.class_2102(java.util.Map.of()));
    }

    /** was net/minecraft/class_2119.method_27847(Lnet/minecraft/class_2960;)Lnet/minecraft/class_2119$class_2121; */
    public static net.minecraft.class_2119.class_2121 method_27847(net.minecraft.class_2960 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new method_27847 returns net.minecraft.class_175, and the provided API exposes no guaranteed conversion to net.minecraft.class_2119.class_2121");
    }

    /** was net/minecraft/class_2135$class_2137.method_43137(Lnet/minecraft/class_2090;)Lnet/minecraft/class_2135$class_2137; */
    public static net.minecraft.class_2135.class_2137 method_43137(net.minecraft.class_2090 a0) {
        throw new UnsupportedOperationException("CenturyBridge: the new method_43137 accepts class_2090$class_2091 and returns class_175, so it cannot preserve the old class_2090-to-class_2137 contract");
    }

    /** was net/minecraft/class_2135$class_2137.method_49195()Lnet/minecraft/class_2135$class_2137; */
    public static net.minecraft.class_2135.class_2137 method_49195() {
        throw new UnsupportedOperationException("CenturyBridge: the new API has only the same zero-argument method returning net.minecraft.class_175, so the old fluent return contract cannot be preserved");
    }

    /** was net/minecraft/class_2535.method_10753(Ljava/net/InetSocketAddress;Z)Lnet/minecraft/class_2535; */
    public static net.minecraft.class_2535 method_10753(java.net.InetSocketAddress a0, boolean a1) {
        return net.minecraft.class_2535.method_10753(a0, a1, null);
    }

    /** was net/minecraft/class_4722.method_48480()Lnet/minecraft/class_1921; */
    public static net.minecraft.class_1921 method_48480() {
        return net.minecraft.class_4722.method_48480(false);
    }

    /**
     * 1.20.1 InventoryScreen.drawEntity(ctx, x, y, size, mouseX, mouseY, entity): x/y is
     * where the feet go, mouseX/mouseY are already relative to the entity (the caller
     * subtracts the cursor). 1.20.2 replaced it with a scissor-box form that takes
     * ABSOLUTE cursor coordinates, so feeding the old arguments into it drew nothing (a
     * zero size) or looked the wrong way. The 1.20.1 body is ported verbatim instead:
     * the same head/body rotation, applied to the entity around the old renderer
     * {@link #method_48472}, then restored.
     */
    public static void method_2486(net.minecraft.class_332 context,
            int x, int y, int size, float mouseX, float mouseY,
            net.minecraft.class_1309 entity) {
        float yaw = (float) Math.atan(mouseX / 40.0F);
        float pitch = (float) Math.atan(mouseY / 40.0F);
        org.joml.Quaternionf rotation = new org.joml.Quaternionf().rotateZ((float) Math.PI);
        org.joml.Quaternionf camera = new org.joml.Quaternionf().rotateX(pitch * 20.0F * ((float) Math.PI / 180.0F));
        rotation.mul(camera);
        float bodyYaw = entity.field_6283;
        float entityYaw = entity.method_36454();
        float entityPitch = entity.method_36455();
        float prevHeadYaw = entity.field_6259;
        float headYaw = entity.field_6241;
        entity.field_6283 = 180.0F + yaw * 20.0F;
        entity.method_36456(180.0F + yaw * 40.0F);
        entity.method_36457(-pitch * 20.0F);
        entity.field_6241 = entity.method_36454();
        entity.field_6259 = entity.method_36454();
        method_48472(context, x, y, size, rotation, camera, entity);
        entity.field_6283 = bodyYaw;
        entity.method_36456(entityYaw);
        entity.method_36457(entityPitch);
        entity.field_6259 = prevHeadYaw;
        entity.field_6241 = headYaw;
    }

    /** was net/minecraft/class_490.method_48472(Lnet/minecraft/class_332;IIILorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/class_1309;)V */
    public static void method_48472(net.minecraft.class_332 a0, int a1, int a2, int a3, org.joml.Quaternionf a4, org.joml.Quaternionf a5, net.minecraft.class_1309 a6) {
        net.minecraft.class_490.method_48472(a0, (float) a1, (float) a2, a3, new org.joml.Vector3f(), a4, a5, a6);
    }
}
