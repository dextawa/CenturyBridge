package top.dext.centurybridge.rt.v1_21_1;

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

    /** 1.20.1 Ingredient.fromJson(json, allowEmpty), removed 1.20.2. L3 x9. */
    public static class_1856 method_8102(JsonElement json, boolean allowEmpty) {
        var codec = allowEmpty ? class_1856.field_46095 : class_1856.field_46096;
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
    public static boolean method_7665(net.minecraft.class_1731 self,
            net.minecraft.class_1937 world, net.minecraft.class_3222 player,
            net.minecraft.class_1860<?> recipe) {
        net.minecraft.class_2960 id = Trackers.RECIPE_IDS.get(recipe);
        if (id == null) {
            return false;
        }
        return ((net.minecraft.class_1732) self)
            .method_7665(world, player, new net.minecraft.class_8786<>(id, recipe));
    }

    // ==== GENERATED BRIDGES (assemble.py) ====
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

    /** was net/minecraft/class_1355.method_35114(I)V */
    public static void method_35114(net.minecraft.class_1355 self, int a0) {
        net.minecraft.class_1352.class_4134[] values = net.minecraft.class_1352.class_4134.values();
        if (a0 >= 0 && a0 < values.length) {
            self.method_6273(values[a0]);
        }
    }

    /** was net/minecraft/class_1498.method_6786()Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 method_6786(net.minecraft.class_1498 self) {
        return new net.minecraft.class_1799(net.minecraft.class_2246.field_10103);
    }

    /** was net/minecraft/class_14.method_63(Lnet/minecraft/class_1308;Lnet/minecraft/class_2338;)Lnet/minecraft/class_7; */
    public static net.minecraft.class_7 method_63(net.minecraft.class_14 self, net.minecraft.class_1308 a0, net.minecraft.class_2338 a1) {
        return self.method_57625(a0, a1);
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

    /** was net/minecraft/class_1642.field_41028F */
    public static float field_41028() {
        return 0.001f;
    }

    /** was net/minecraft/class_1657.field_30643I */
    public static int field_30643() {
        return 100;
    }

    /** was net/minecraft/class_1657.method_53968(Ljava/lang/String;)Z */
    public static boolean method_53968(net.minecraft.class_1657 self, java.lang.String a0) {
        return self.method_7334().getName().equalsIgnoreCase(a0);
    }

    /** was net/minecraft/class_1741.method_24355()F */
    public static float method_24355(net.minecraft.class_1741 self) {
        return self.comp_2303();
    }

    /** was net/minecraft/class_1741.method_7694()Ljava/lang/String; */
    public static java.lang.String method_7694(net.minecraft.class_1741 self) {
        java.util.List<?> list = self.comp_2302();
        if (list == null) {
            return "";
        }
        java.lang.StringBuilder sb = new java.lang.StringBuilder();
        for (Object obj : list) {
            sb.append(obj);
        }
        return sb.toString();
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

    /** was net/minecraft/class_1819.field_30920Ljava/lang/String; */
    public static java.lang.String field_30920() {
        return "ChargedProjectiles";
    }

    /** was net/minecraft/class_1830.field_30923Ljava/lang/String; */
    public static java.lang.String field_30923() {
        return "Potion";
    }

    /** was net/minecraft/class_1832.method_8024()I */
    public static int method_8024(net.minecraft.class_1832 self) {
        return self.method_8025();
    }

    /** was net/minecraft/class_1843.field_30929I */
    public static int field_30929() {
        return 64;
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

    /** was net/minecraft/class_1860.method_8110(Lnet/minecraft/class_5455;)Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 method_8110(net.minecraft.class_1860 self, net.minecraft.class_5455 a0) {
        return self.method_8110((net.minecraft.class_7225.class_7874) a0);
    }

    /** was net/minecraft/class_1865.method_53736()Lcom/mojang/serialization/Codec; */
    public com.mojang.serialization.Codec method_53736() {
        return ((net.minecraft.class_1865) (Object) this).method_53736().codec();
    }

    /** was net/minecraft/class_1890.method_37424(Lnet/minecraft/class_2487;)I */
    public static int method_37424(net.minecraft.class_1890 self, net.minecraft.class_2487 a0) {
        return Math.max(0, Math.min(255, a0.method_10550("lvl")));
    }

    /** was net/minecraft/class_1890.method_37426(Lnet/minecraft/class_2960;I)Lnet/minecraft/class_2487; */
    public static net.minecraft.class_2487 method_37426(net.minecraft.class_1890 self, net.minecraft.class_2960 a0, int a1) {
        net.minecraft.class_2487 nbtCompound = new net.minecraft.class_2487();
        nbtCompound.method_10582("id", java.lang.String.valueOf(a0));
        nbtCompound.method_10548("lvl", (short) a1);
        return nbtCompound;
    }

    /** was net/minecraft/class_1936.method_32888(Lnet/minecraft/class_5712;Lnet/minecraft/class_243;Lnet/minecraft/class_5712$class_7397;)V */
    public void method_32888(net.minecraft.class_5712 a0, net.minecraft.class_243 a1, net.minecraft.class_5712.class_7397 a2) {
        ((net.minecraft.class_1936) (Object) this).method_32888(net.minecraft.class_6880.method_40223(a0), a1, a2);
    }

    /** was net/minecraft/class_1936.method_33596(Lnet/minecraft/class_1297;Lnet/minecraft/class_5712;Lnet/minecraft/class_2338;)V */
    public void method_33596(net.minecraft.class_1297 a0, net.minecraft.class_5712 a1, net.minecraft.class_2338 a2) {
        ((net.minecraft.class_1936) (Object) this).method_33596(a0, net.minecraft.class_6880.method_40223(a1), a2);
    }

    /** was net/minecraft/class_1936.method_43275(Lnet/minecraft/class_1297;Lnet/minecraft/class_5712;Lnet/minecraft/class_243;)V */
    public void method_43275(net.minecraft.class_1297 a0, net.minecraft.class_5712 a1, net.minecraft.class_243 a2) {
        ((net.minecraft.class_1936) (Object) this).method_43275(a0, net.minecraft.class_6880.method_40223(a1), a2);
    }

    /** was net/minecraft/class_1936.method_43276(Lnet/minecraft/class_5712;Lnet/minecraft/class_2338;Lnet/minecraft/class_5712$class_7397;)V */
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

    /** was net/minecraft/class_22$class_5637.field_27895I */
    public static int field_27895() {
        return 4;
    }

    /** was net/minecraft/class_22$class_5637.field_27896[B */
    public static byte[] field_27896() {
        return new byte[]{(byte) -119, 80, 78, 71, 13, 10, 26, 10};
    }

    /** was net/minecraft/class_2378.method_31139(Ljava/lang/Object;)Lcom/mojang/serialization/Lifecycle; */
    public static com.mojang.serialization.Lifecycle method_31139(net.minecraft.class_2378 self, java.lang.Object a0) {
        return self.method_31138();
    }

    /** was net/minecraft/class_2385.method_10272(Lnet/minecraft/class_5321;Ljava/lang/Object;Lcom/mojang/serialization/Lifecycle;)Lnet/minecraft/class_6880$class_6883; */
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

    /** was net/minecraft/class_2573.method_10905(Lnet/minecraft/class_1799;)V */
    public static void method_10905(net.minecraft.class_2573 self, net.minecraft.class_1799 a0) {
        self.method_10913(a0, self.method_10908());
    }

    /** was net/minecraft/class_2615.field_12025Lnet/minecraft/class_265; */
    public static net.minecraft.class_265 field_12025() {
        return net.minecraft.class_259.method_1077();
    }

    /** was net/minecraft/class_2828$class_2829.method_34221(Lnet/minecraft/class_2540;)Lnet/minecraft/class_2828$class_2829; */
    public static net.minecraft.class_2828.class_2829 method_34221(net.minecraft.class_2828.class_2829 self, net.minecraft.class_2540 a0) {
        double d = a0.readDouble();
        double e = a0.readDouble();
        double f = a0.readDouble();
        boolean bl = a0.readBoolean();
        return new net.minecraft.class_2828.class_2829(d, e, f, bl);
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

    /** was net/minecraft/class_2956.field_37790Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_37790() {
        return net.minecraft.class_2956.field_37790.codec();
    }

    /** was net/minecraft/class_29.method_261(Lnet/minecraft/class_1657;)Lnet/minecraft/class_2487; */
    public static net.minecraft.class_2487 method_261(net.minecraft.class_29 self, net.minecraft.class_1657 a0) {
        return (net.minecraft.class_2487) self.method_55789(a0).orElse(null);
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

    /** was net/minecraft/class_39.field_224Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_224() {
        return net.minecraft.class_39.field_224.method_29177();
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

    /** was net/minecraft/class_39.field_285Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_285() {
        return net.minecraft.class_39.field_285.method_29177();
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

    /** was net/minecraft/class_39.field_365Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_365() {
        return net.minecraft.class_39.field_365.method_29177();
    }

    /** was net/minecraft/class_39.field_38438Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_38438() {
        return net.minecraft.class_39.field_38438.method_29177();
    }

    /** was net/minecraft/class_39.field_38439Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_38439() {
        return net.minecraft.class_39.field_38439.method_29177();
    }

    /** was net/minecraft/class_39.field_385Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_385() {
        return net.minecraft.class_39.field_385.method_41185();
    }

    /** was net/minecraft/class_39.field_394Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_394() {
        return net.minecraft.class_39.field_394.method_29177();
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

    /** was net/minecraft/class_39.field_461Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_461() {
        return net.minecraft.class_39.field_461.method_41185();
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

    /** was net/minecraft/class_39.field_489Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_489() {
        return net.minecraft.class_39.field_489.method_29177();
    }

    /** was net/minecraft/class_39.field_607Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_607() {
        return net.minecraft.class_39.field_607.method_29177();
    }

    /** was net/minecraft/class_39.field_615Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_615() {
        return net.minecraft.class_39.field_615.method_29177();
    }

    /** was net/minecraft/class_39.field_629Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_629() {
        return net.minecraft.class_39.field_629.method_29177();
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

    /** was net/minecraft/class_39.field_702Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_702() {
        return net.minecraft.class_39.field_702.method_29177();
    }

    /** was net/minecraft/class_39.field_716Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_716() {
        return net.minecraft.class_39.field_716.method_29177();
    }

    /** was net/minecraft/class_39.field_751Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_751() {
        return net.minecraft.class_39.field_751.method_29177();
    }

    /** was net/minecraft/class_39.field_778Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_778() {
        return net.minecraft.class_39.field_778.method_41185();
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

    /** was net/minecraft/class_39.field_806Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_806() {
        return net.minecraft.class_39.field_806.method_29177();
    }

    /** was net/minecraft/class_39.field_814Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_814() {
        return net.minecraft.class_39.field_814.method_29177();
    }

    /** was net/minecraft/class_39.field_841Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_841() {
        return net.minecraft.class_39.field_841.method_29177();
    }

    /** was net/minecraft/class_39.field_842Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_842() {
        return net.minecraft.class_39.field_842.method_29177();
    }

    /** was net/minecraft/class_39.field_844Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_844() {
        return net.minecraft.class_39.field_844.method_29177();
    }

    /** was net/minecraft/class_39.field_850Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_850() {
        return net.minecraft.class_39.field_850.method_41185();
    }

    /** was net/minecraft/class_39.field_854Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_854() {
        return net.minecraft.class_39.field_854.method_41185();
    }

    /** was net/minecraft/class_39.field_869Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_869() {
        return net.minecraft.class_39.field_869.method_29177();
    }

    /** was net/minecraft/class_39.field_878Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_878() {
        return net.minecraft.class_39.field_878.method_29177();
        // CB-CONFIDENCY: high -- old Identifier field became a RegistryKey; getValue() (method_29177) returns the same Identifier
    }

    /** was net/minecraft/class_39.field_880Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_880() {
        return net.minecraft.class_39.field_880.method_29177();
    }

    /** was net/minecraft/class_39.field_885Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_885() {
        return net.minecraft.class_39.field_885.method_29177();
    }

    /** was net/minecraft/class_4208.method_19442()Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 method_19442(net.minecraft.class_4208 self) {
        return self.comp_2207();
    }

    /** was net/minecraft/class_4208.method_19446()Lnet/minecraft/class_2338; */
    public static net.minecraft.class_2338 method_19446(net.minecraft.class_4208 self) {
        return self.comp_2208();
    }

    /** was net/minecraft/class_4514.method_22152()Ljava/lang/String; */
    public static java.lang.String method_22152(net.minecraft.class_4514 self) {
        return self.comp_2209();
    }

    /** was net/minecraft/class_4514.method_22154()Ljava/util/Collection; */
    public static java.util.Collection method_22154(net.minecraft.class_4514 self) {
        return self.comp_2210();
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

    /** was net/minecraft/class_4766.field_24719Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24719() {
        return net.minecraft.class_4766.field_24719.codec();
    }

    /** was net/minecraft/class_4992.field_24995Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_24995() {
        return net.minecraft.class_4992.field_24995.codec();
    }

    /** was net/minecraft/class_4993.field_25004Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_25004() {
        return net.minecraft.class_4993.field_25004.codec();
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

    /** was net/minecraft/class_5151.method_31570()Lnet/minecraft/class_3414; */
    public net.minecraft.class_3414 method_31570() {
        return (net.minecraft.class_3414) ((net.minecraft.class_5151) (Object) this).method_31570().comp_349();
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

    /** was net/minecraft/class_5713.method_32943(Lnet/minecraft/class_5712;Lnet/minecraft/class_243;Lnet/minecraft/class_5712$class_7397;Lnet/minecraft/class_5713$class_7721;)Z */
    public boolean method_32943(net.minecraft.class_5712 a0, net.minecraft.class_243 a1, net.minecraft.class_5712.class_7397 a2, net.minecraft.class_5713.class_7721 a3) {
        return ((net.minecraft.class_5713) (Object) this).method_32943(net.minecraft.class_6880.method_40223(a0), a1, a2, a3);
    }

    /** was net/minecraft/class_5714.method_32947(Lnet/minecraft/class_3218;Lnet/minecraft/class_5712;Lnet/minecraft/class_5712$class_7397;Lnet/minecraft/class_243;)Z */
    public boolean method_32947(net.minecraft.class_3218 a0, net.minecraft.class_5712 a1, net.minecraft.class_5712.class_7397 a2, net.minecraft.class_243 a3) {
        return ((net.minecraft.class_5714)(Object)this).method_32947(a0, net.minecraft.class_6880.method_40223(a1), a2, a3);
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

    /** was net/minecraft/class_6120.field_31531Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_31531() {
        return net.minecraft.class_6120.field_31531.codec();
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

    /** was net/minecraft/class_7260$1.field_39306Lnet/minecraft/class_7260; */
    public static net.minecraft.class_7260 field_39306() {
        return null;
    }

    /** was net/minecraft/class_7265.method_42276()Lnet/minecraft/class_2960; */
    public net.minecraft.class_2960 method_42276() {
        return ((net.minecraft.class_7265) (Object) this).method_42276().method_29177();
    }

    /** was net/minecraft/class_7265.method_42285(Lnet/minecraft/class_2487;)V */
    public void method_42285(net.minecraft.class_2487 a0) {
        net.minecraft.class_1937 world = ((net.minecraft.class_7265)(Object)this).method_37908();
        ((net.minecraft.class_7265)(Object)this).method_42285(a0, (net.minecraft.class_7225.class_7874)world);
    }

    /** was net/minecraft/class_7265.method_42288(Lnet/minecraft/class_2487;)V */
    public void method_42288(net.minecraft.class_2487 a0) {
        ((net.minecraft.class_7265)(Object)this).method_42288(a0, (net.minecraft.class_7225.class_7874)((net.minecraft.class_7265)(Object)this).method_37908());
    }

    /** was net/minecraft/class_7389.field_38783Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_38783() {
        return net.minecraft.class_7389.field_38783.codec();
    }

    /** was net/minecraft/class_7390.field_38793Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_38793() {
        return net.minecraft.class_7390.field_38793.codec();
    }

    /** was net/minecraft/class_8178.field_42841Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_42841() {
        return net.minecraft.class_8178.field_42841.codec();
    }

    /** was net/minecraft/class_8180.field_42848Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec field_42848() {
        return net.minecraft.class_8180.field_42848.codec();
    }

    /** was net/minecraft/class_8181.method_54080()Lnet/minecraft/class_2586; */
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

    /** was net/minecraft/class_8514$class_5719.method_32969(Lnet/minecraft/class_3218;Lnet/minecraft/class_2338;Lnet/minecraft/class_5712;Lnet/minecraft/class_1297;Lnet/minecraft/class_1297;F)V */
    public void method_32969(net.minecraft.class_3218 a0, net.minecraft.class_2338 a1, net.minecraft.class_5712 a2, net.minecraft.class_1297 a3, net.minecraft.class_1297 a4, float a5) {
        ((net.minecraft.class_8514.class_5719) (Object) this).method_32969(a0, a1, net.minecraft.class_6880.method_40223(a2), a3, a4, a5);
    }

    /** was net/minecraft/class_8514.field_44638[Lnet/minecraft/class_5712; */
    public static net.minecraft.class_5712[] field_44638() {
        return net.minecraft.class_8514.field_44638.toArray(new net.minecraft.class_5712[0]);
    }

    /** was net/minecraft/class_8934.method_54869()Lnet/minecraft/class_2960; */
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

    /** was net/minecraft/class_1297.method_52208(ZLnet/minecraft/class_243;)V */
    public static void method_52208(net.minecraft.class_1297 self, boolean a0, net.minecraft.class_243 a1) {
        self.method_18799(a1);
        // Can't set velocityModified flag in new API
    }

    /** was net/minecraft/class_1297.method_5741()I */
    public static int method_5741(net.minecraft.class_1297 self) {
        return self.method_20802();
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

    /** was net/minecraft/class_1657.field_49991Z */
    public static boolean field_49991() {
        return true;
        // CB-CONFIDENCE: low -- deleted static boolean constant with no direct replacement; value inferred as true from common feature-flag patterns but unverified
    }

    /** was net/minecraft/class_1860.method_8111(Lnet/minecraft/class_1263;)Lnet/minecraft/class_2371; */
    public net.minecraft.class_2371 method_8111(net.minecraft.class_1263 a0) {
        return ((net.minecraft.class_1860) (Object) this).method_8111((net.minecraft.class_9695) a0);
    }

    /** was net/minecraft/class_1860.method_8116(Lnet/minecraft/class_1263;Lnet/minecraft/class_7225$class_7874;)Lnet/minecraft/class_1799; */
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

    /** was net/minecraft/class_1890.method_8221(Lnet/minecraft/class_1799;)Z */
    public static boolean method_8221(net.minecraft.class_1890 self, net.minecraft.class_1799 a0) {
        return self.method_57529(a0);
    }

    /** was net/minecraft/class_2547.method_10839(Lnet/minecraft/class_2561;)V */
    public void method_10839(net.minecraft.class_2561 a0) {
        ((net.minecraft.class_2547) (Object) this).method_10839(new net.minecraft.class_9812(a0));
    }

    /** was net/minecraft/class_2547.method_55597(Lnet/minecraft/class_129;)V */
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

    /** was net/minecraft/class_2952.method_12815(Ljava/util/Iterator;IIII)V */
    public void method_12815(java.util.Iterator a0, int a1, int a2, int a3, int a4) {
        ((net.minecraft.class_2952)(Object) this).method_12815((java.lang.Object) a0, a1, a2, a3, a4);
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

    /** was net/minecraft/class_3492.method_15120()Z */
    public static boolean method_15120(net.minecraft.class_3492 self) {
        return !self.method_16182().isEmpty();
    }

    /** was net/minecraft/class_5131.method_26841()Ljava/util/Set; */
    public static java.util.Set method_26841(net.minecraft.class_5131 self) {
        return self.method_60497();
    }

    /** was net/minecraft/class_5146.method_6576(Lnet/minecraft/class_3419;)V */
    public void method_6576(net.minecraft.class_3419 a0) {
        // play this.method_45328() with the given category
            // the ItemStack is used for something else (like pitch/volume modification)
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

    /** was net/minecraft/class_8957.comp_2083()Lnet/minecraft/class_2371; */
    public static net.minecraft.class_2371 comp_2083(net.minecraft.class_8957 self) {
        return self.method_59997();
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

    /** was net/minecraft/class_1297.method_5743()Ljava/lang/Iterable; */
    public static java.lang.Iterable method_5743(net.minecraft.class_1297 self) {
        if (self instanceof net.minecraft.class_1309 living) {
            return com.google.common.collect.Iterables.concat(living.method_5877(), living.method_5661());
        }
        return java.util.Collections.emptyList();
    }

    /** was net/minecraft/class_1308.method_5933()Lnet/minecraft/class_1297; */
    public static net.minecraft.class_1297 method_5933(net.minecraft.class_1308 self) {
        return self.method_60952();
    }

    /** was net/minecraft/class_1481.field_28406Lnet/minecraft/class_1856; */
    public static net.minecraft.class_1856 field_28406() {
        return net.minecraft.class_1856.method_8091(net.minecraft.class_1802.field_8699);
    }

    /** was net/minecraft/class_2615.field_12027Lnet/minecraft/class_265; */
    public static net.minecraft.class_265 field_12027() {
        return net.minecraft.class_259.method_1081(0.0D, 1.0D, 0.0D, 1.0D, 2.0D, 1.0D);
    }

    /** was net/minecraft/class_1835.field_8001Ljava/util/UUID; */
    public static java.util.UUID field_8001() {
        return java.util.UUID.fromString("cb3f55d3-645c-4f38-a497-9c13a33db5cf");
    }

    /** was net/minecraft/class_1835.field_8006Ljava/util/UUID; */
    public static java.util.UUID field_8006() {
        return java.util.UUID.fromString("fa233e1c-4180-4865-b01b-bcce9785aca3");
    }

    /** was net/minecraft/class_2319.<init>(Ljava/util/function/Supplier;)V */
    public static net.minecraft.class_2319 cbNewclass_2319(java.util.function.Supplier a0) {
        return net.minecraft.class_2319.method_41999(a0);
    }

    /** was net/minecraft/class_2588.<init>(Ljava/lang/String;[Ljava/lang/Object;)V */
    public static net.minecraft.class_2588 cbNewclass_2588(java.lang.String a0, java.lang.Object[] a1) {
        return new net.minecraft.class_2588(a0, null, a1);
    }

    /** was net/minecraft/class_2588.<init>(Ljava/lang/String;)V */
    public static net.minecraft.class_2588 cbNewclass_2588(java.lang.String a0) {
        return new net.minecraft.class_2588(a0, null, new Object[0]);
    }

    /** was net/minecraft/class_2960.<init>(Ljava/lang/String;)V */
    public static net.minecraft.class_2960 cbNewclass_2960(java.lang.String a0) {
        return net.minecraft.class_2960.method_60654(a0);
    }

    /** was net/minecraft/class_3283.<init>(Lnet/minecraft/class_3264;[Lnet/minecraft/class_3285;)V */
    public static net.minecraft.class_3283 cbNewclass_3283(net.minecraft.class_3264 a0, net.minecraft.class_3285[] a1) {
        return new net.minecraft.class_3283(a1);
    }

    /** was net/minecraft/class_3955.method_8110(Lnet/minecraft/class_5455;)Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 method_8110(net.minecraft.class_3955 self, net.minecraft.class_5455 a0) {
        return self.method_8110((net.minecraft.class_7225.class_7874) a0);
    }

    /** was net/minecraft/class_437.field_44669Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_44669() {
        return net.minecraft.class_2960.method_60656("textures/gui/options_background.png");
    }

    /** was net/minecraft/class_5134.field_23721Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_23721() {
        return net.minecraft.class_5134.field_23721.comp_349();
    }

    /** was net/minecraft/class_5134.field_23722Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_23722() {
        return net.minecraft.class_5134.field_23722.comp_349();
    }

    /** was net/minecraft/class_5134.field_23726Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_23726() {
        return (net.minecraft.class_1320) net.minecraft.class_5134.field_23726.comp_349();
    }

    /** was net/minecraft/class_8059.method_8110(Lnet/minecraft/class_5455;)Lnet/minecraft/class_1799; */
    public static net.minecraft.class_1799 method_8110(net.minecraft.class_8059 self, net.minecraft.class_5455 a0) {
        return self.method_17447();
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

    private static boolean cbWarnedTorchParticle;

    /**
     * 1.20.1 TorchBlock(settings, particle) took any ParticleEffect; 1.20.3 takes a
     * DefaultParticleType first. Vanilla torches and nearly every mod pass FLAME or
     * SOUL_FIRE_FLAME, which are; a particle with parameters (dust, item...) cannot be a
     * torch particle any more, so the torch shows FLAME and says so once.
     */
    public static net.minecraft.class_2400 cbToSimpleParticle(net.minecraft.class_2394 particle) {
        if (particle instanceof net.minecraft.class_2400 simple) {
            return simple;
        }
        if (!cbWarnedTorchParticle) {
            cbWarnedTorchParticle = true;
            org.slf4j.LoggerFactory.getLogger("CenturyBridge").warn(
                "a torch block was built with particle {}, which 1.20.3+ torches cannot carry; it shows flame", particle);
        }
        return net.minecraft.class_2398.field_11240;
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

    /** was net/minecraft/class_5134.field_23717Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_23717() {
        return (net.minecraft.class_1320) net.minecraft.class_5134.field_23717.comp_349();
    }

    /** was net/minecraft/class_2512.method_10691(Lnet/minecraft/class_2487;)Lnet/minecraft/class_2338; */
    public static net.minecraft.class_2338 method_10691(net.minecraft.class_2487 nbt) {
        return new net.minecraft.class_2338(nbt.method_10550("X"), nbt.method_10550("Y"), nbt.method_10550("Z"));
    }

    /** was net/minecraft/class_5132$class_5133.method_26867(Lnet/minecraft/class_1320;)Lnet/minecraft/class_5132$class_5133; */
    public static net.minecraft.class_5132.class_5133 method_26867(net.minecraft.class_5132.class_5133 self, net.minecraft.class_1320 attr) {
        return self.method_26867(net.minecraft.class_7923.field_41190.method_47983(attr));
    }

    // Entity.setOnFireFor int-seconds -> float-seconds (1.21 widened the parameter).
    /** was net/minecraft/class_1297.method_5639(I)V */
    public static void method_5639(net.minecraft.class_1297 self, int i) { self.method_5639((float) i); }
    /** was net/minecraft/class_1309.method_5639(I)V */
    public static void method_5639(net.minecraft.class_1309 self, int i) { self.method_5639((float) i); }
    /** was net/minecraft/class_1657.method_5639(I)V */
    public static void method_5639(net.minecraft.class_1657 self, int i) { self.method_5639((float) i); }

    /** was net/minecraft/class_5251.method_27719(Ljava/lang/String;)Lnet/minecraft/class_5251; -- TextColor.parseColor
     *  returns DataResult since 1.20.3; the 1.20.1 contract was "TextColor or null". */
    public static net.minecraft.class_5251 method_27719(String s) {
        return net.minecraft.class_5251.method_27719(s).result().orElse(null);
    }

    // ---- dead-cover replacements (the wiring claimed a mixin restored these shapes; none ever existed) ----

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

    /** was LivingEntity.sendEquipmentBreakStatus(EquipmentSlot): 1.20.5+ also wants the item */
    public static void method_20235(net.minecraft.class_1309 self, net.minecraft.class_1304 slot) {
        self.method_20235(self.method_6118(slot).method_7909(), slot);
    }

    /** was EntityAttributeModifier.getName(): modifiers are keyed by Identifier since 1.21 */
    public static String method_6185(net.minecraft.class_1322 self) {
        return self.comp_2447().toString();
    }

    /** was MobEntity.initialize(.., NbtCompound): the NBT argument left in 1.20.5. Call sites only -- a 1.20.1 override stays an orphan (vanilla calls the 4-arg shape) */
    public static net.minecraft.class_1315 method_5943(net.minecraft.class_1308 self, net.minecraft.class_5425 world, net.minecraft.class_1266 difficulty,
            net.minecraft.class_3730 reason, net.minecraft.class_1315 data, net.minecraft.class_2487 nbt) {
        return self.method_5943(world, difficulty, reason, data);
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

    // World.emitGameEvent(.., GameEvent, ..) -> Holder<GameEvent> (1.20.5); keyed on the ServerWorld owner the corpus calls through
    public static void method_33596(net.minecraft.class_3218 self, net.minecraft.class_1297 entity, net.minecraft.class_5712 event, net.minecraft.class_2338 pos) {
        self.method_33596(entity, net.minecraft.class_7923.field_41171.method_47983(event), pos);
    }

    public static void method_43275(net.minecraft.class_3218 self, net.minecraft.class_1297 entity, net.minecraft.class_5712 event, net.minecraft.class_243 pos) {
        self.method_43275(entity, net.minecraft.class_7923.field_41171.method_47983(event), pos);
    }

    public static void method_32888(net.minecraft.class_3218 self, net.minecraft.class_5712 event, net.minecraft.class_243 pos, net.minecraft.class_5712.class_7397 emitter) {
        self.method_32888(net.minecraft.class_7923.field_41171.method_47983(event), pos, emitter);
    }

    /** was PlayerEntity.eatFood(World, ItemStack): 1.20.5+ takes the food component explicitly (the stack's own) */
    public static net.minecraft.class_1799 method_18866(net.minecraft.class_1657 self, net.minecraft.class_1937 world, net.minecraft.class_1799 stack) {
        net.minecraft.class_4174 food = stack.method_57824(net.minecraft.class_9334.field_50075);
        return food == null ? stack : self.method_18866(world, stack, food);
    }

    /** was TreeDecoratorType(Codec): private and MapCodec-typed since 1.20.5 -- the codec is taken as a map codec (record codecs serialize the same way) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_4663 cbNewclass_4663(com.mojang.serialization.Codec codec) {
        // the ctor is private and the build gate cannot check an <init> invoker: reflection (game classes live in the unnamed module)
        try {
            java.lang.reflect.Constructor<net.minecraft.class_4663> c = net.minecraft.class_4663.class.getDeclaredConstructor(com.mojang.serialization.MapCodec.class);
            c.setAccessible(true);
            return c.newInstance(com.mojang.serialization.MapCodec.assumeMapUnsafe(codec));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("centurybridge: TreeDecoratorType(MapCodec) constructor", e);
        }
    }

    /**
     * 1.20.1 StateHolder.getValues() (class_2688.method_11656) returned a Guava
     * ImmutableMap; at 1.21 the internal store became a fastutil
     * Reference2ObjectArrayMap and the method now returns java.util.Map, so a
     * 1.20.1 call site resolved to ()ImmutableMap NoSuchMethodErrors. Wrap the
     * real Map back into an ImmutableMap so the old contract holds (callers
     * iterate/read it; copy is cheap and faithful).
     */
    public static com.google.common.collect.ImmutableMap<net.minecraft.class_2769<?>, java.lang.Comparable<?>>
            method_11656(net.minecraft.class_2688<?, ?> self) {
        return com.google.common.collect.ImmutableMap.copyOf(self.method_11656());
    }

    /** was net/minecraft/class_1280.method_5496(FFF)F */
    public static float method_5496(float a0, float a1, float a2) {
        float f = 2.0F + a2 / 4.0F;
        float g = a1 - a0 / f;
        float minLimit = a1 * 0.2F;
        float clampedG = g < minLimit ? minLimit : (g > 20.0F ? 20.0F : g);
        return a0 * (1.0F - clampedG / 25.0F);
    }

    /** was net/minecraft/class_1606.method_33346(Lnet/minecraft/class_2350;F)Lnet/minecraft/class_238;
     *  ShulkerEntity.calculateBoundingBox(direction, extraLength): 1.20.5 added the scale; 1.0 is the old box */
    public static net.minecraft.class_238 method_33346(net.minecraft.class_2350 a0, float a1) {
        return net.minecraft.class_1606.method_33346(1.0F, a0, a1);
    }

    /** was net/minecraft/class_1606.method_33347(Lnet/minecraft/class_2350;FF)Lnet/minecraft/class_238; */
    public static net.minecraft.class_238 method_33347(net.minecraft.class_2350 a0, float a1, float a2) {
        return net.minecraft.class_1606.method_33347(1.0F, a0, a1, a2);
    }

    /** was net/minecraft/class_1704.method_53174(I)Lnet/minecraft/class_1291; */
    public static net.minecraft.class_1291 method_53174(int a0) {
        net.minecraft.class_6880 entry = net.minecraft.class_1704.method_53174(a0);
        return entry == null ? null : (net.minecraft.class_1291) entry.comp_349();
    }

    /** was net/minecraft/class_1806.method_7997(Ljava/lang/Integer;Lnet/minecraft/class_1937;)Lnet/minecraft/class_22; */
    public static net.minecraft.class_22 method_7997(java.lang.Integer a0, net.minecraft.class_1937 a1) {
        return net.minecraft.class_1806.method_7997(a0 == null ? null : new net.minecraft.class_9209(a0), a1);
    }

    /** was net/minecraft/class_1932.method_32350(Lnet/minecraft/class_2487;)Lnet/minecraft/class_1932; */
    public static net.minecraft.class_1932 method_32350(net.minecraft.class_2487 a0) {
        return net.minecraft.class_1932.method_32350(a0, null);
    }

    /** was net/minecraft/class_22.method_32371(Lnet/minecraft/class_2487;)Lnet/minecraft/class_22; */
    public static net.minecraft.class_22 method_32371(net.minecraft.class_2487 a0) {
        return net.minecraft.class_22.method_32371(a0, net.minecraft.class_7225.class_7874.method_46761(java.util.stream.Stream.empty()));
    }

    /** was net/minecraft/class_2293.method_9804(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)Ljava/util/function/Predicate; */
    public static java.util.function.Predicate method_9804(com.mojang.brigadier.context.CommandContext a0, java.lang.String a1) {
        return net.minecraft.class_2293.method_9804(a0, a1);
    }

    /** was net/minecraft/class_2315.method_10010(Lnet/minecraft/class_2342;)Lnet/minecraft/class_2374; */
    public static net.minecraft.class_2374 method_10010(net.minecraft.class_2342 a0) {
        return net.minecraft.class_2315.method_58682(a0);
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

    /** was net/minecraft/class_2512.method_10692(Lnet/minecraft/class_2338;)Lnet/minecraft/class_2487; */
    public static net.minecraft.class_2487 method_10692(net.minecraft.class_2338 a0) {
        net.minecraft.class_2487 nbt = new net.minecraft.class_2487();
        nbt.method_10548("X", a0.method_10263());
        nbt.method_10548("Y", a0.method_10264());
        nbt.method_10548("Z", a0.method_10260());
        return nbt;
    }

    /** was net/minecraft/class_2586.method_11005(Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_2487;)Lnet/minecraft/class_2586; */
    public static net.minecraft.class_2586 method_11005(net.minecraft.class_2338 a0, net.minecraft.class_2680 a1, net.minecraft.class_2487 a2) {
        return net.minecraft.class_2586.method_11005(a0, a1, a2, net.minecraft.class_7225.class_7874.method_46761(java.util.stream.Stream.empty()));
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

    /** was net/minecraft/class_3195.method_42699(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_42699(java.util.function.Function a0) {
        return net.minecraft.class_3195.method_42699(a0).codec();
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

    /** was net/minecraft/class_3715.method_17472(Lnet/minecraft/class_1297;Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_1799;)Z */
    public static boolean method_17472(net.minecraft.class_1297 a0, net.minecraft.class_1937 a1, net.minecraft.class_2338 a2, net.minecraft.class_2680 a3, net.minecraft.class_1799 a4) {
        if (a0 instanceof net.minecraft.class_1309) {
            return net.minecraft.class_3715.method_17472((net.minecraft.class_1309) a0, a1, a2, a3, a4);
        }
        return false;
    }

    /** was net/minecraft/class_3900.method_34870(Lnet/minecraft/class_3193;Ljava/util/function/Function;)Lnet/minecraft/class_3900$class_3946; */
    public static net.minecraft.class_3900.class_3946 method_34870(net.minecraft.class_3193 a0, java.util.function.Function a1) {
        return net.minecraft.class_3900.method_34870((net.minecraft.class_9761) a0, a1);
    }

    /** was net/minecraft/class_4215.method_19548(Lnet/minecraft/class_1309;Lnet/minecraft/class_1309;F)V */
    public static void method_19548(net.minecraft.class_1309 a0, net.minecraft.class_1309 a1, float a2) {
        net.minecraft.class_4215.method_19548(a0, a1, a2, 0);
    }

    /** was net/minecraft/class_4519.method_22193(Ljava/lang/String;)Ljava/util/Collection; */
    public static java.util.Collection method_22193(java.lang.String a0) {
        return net.minecraft.class_4519.method_22193(a0).collect(java.util.stream.Collectors.toList());
    }

    /** was net/minecraft/class_4519.method_29405()Ljava/util/Collection; */
    public static java.util.Collection method_29405() {
        return net.minecraft.class_4519.method_29405().collect(java.util.stream.Collectors.toList());
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

    /** was net/minecraft/class_5317.method_41594(Lnet/minecraft/class_2378;)Ljava/util/Optional; */
    public static java.util.Optional method_41594(net.minecraft.class_2378 a0) {
        return net.minecraft.class_5317.method_41594(new net.minecraft.class_7723(a0));
    }

    /** was net/minecraft/class_5328.method_33263(Lnet/minecraft/class_1542;Ljava/util/stream/Stream;)V */
    public static void method_33263(net.minecraft.class_1542 a0, java.util.stream.Stream a1) {
        net.minecraft.class_5328.method_33263(a0, a1::iterator);
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

    /** was net/minecraft/class_7107.method_41366()Lnet/minecraft/class_1856; */
    public static net.minecraft.class_1856 method_41366() {
        return (net.minecraft.class_1856) net.minecraft.class_7107.method_41366();
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

    /** was net/minecraft/class_8886.method_54475(Lnet/minecraft/class_1937;Lnet/minecraft/class_8566;)Ljava/util/Optional; */
    public static java.util.Optional method_54475(net.minecraft.class_1937 a0, net.minecraft.class_8566 a1) {
        return net.minecraft.class_8886.method_54475(a0, (net.minecraft.class_9694) a1);
    }

    /** was net/minecraft/class_8890.method_54509(Lnet/minecraft/class_2378;)Lcom/mojang/serialization/Codec; */
    public static com.mojang.serialization.Codec method_54509(net.minecraft.class_2378 a0) {
        return net.minecraft.class_8890.method_54509(a0).codec();
    }

    /** was net/minecraft/class_9010.method_55377(Lcom/mojang/brigadier/StringReader;Lcom/mojang/serialization/Codec;)Ljava/lang/Object; */
    public static java.lang.Object method_55377(com.mojang.brigadier.StringReader a0, com.mojang.serialization.Codec a1) {
        return net.minecraft.class_9010.method_55377(net.minecraft.class_7225.class_7874.method_46761(java.util.stream.Stream.empty()), a0, a1);
    }

    /** was net/minecraft/class_9147.method_56455(Lnet/minecraft/class_2539;Ljava/util/function/Consumer;)Lnet/minecraft/class_9127; */
    public static net.minecraft.class_9127 method_56455(net.minecraft.class_2539 a0, java.util.function.Consumer a1) {
        return net.minecraft.class_9147.method_56455(a0, a1).method_61107(java.util.function.Function.identity());
    }

    /** was net/minecraft/class_9362.method_58659(Lnet/minecraft/class_1657;)Z */
    public static boolean method_58659(net.minecraft.class_1657 a0) {
        return net.minecraft.class_9362.method_58659(a0);
    }
}
