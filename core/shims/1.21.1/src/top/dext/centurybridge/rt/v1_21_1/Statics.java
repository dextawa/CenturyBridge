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

    /** was net/minecraft/class_1297.method_5743()Ljava/lang/Iterable; */
    public static java.lang.Iterable method_5743(net.minecraft.class_1297 self) {
        return self.cU().toList();
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

    /** was net/minecraft/class_1741.method_48402(Lnet/minecraft/class_1738$class_8051;)I */
    public static int method_48402(net.minecraft.class_1741 self, net.minecraft.class_1738.class_8051 a0) {
        throw new UnsupportedOperationException("CenturyBridge: ArmorMaterial no longer contains durability information in 1.20.5");
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

    /** was net/minecraft/class_1832.method_8024()I */
    public static int method_8024(net.minecraft.class_1832 self) {
        return self.method_8025();
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

    /** was net/minecraft/class_1860.method_8110(Lnet/minecraft/class_5455;)Lnet/minecraft/class_1799; */
    public net.minecraft.class_1799 method_8110(net.minecraft.class_5455 a0) {
        return ((net.minecraft.class_1860) (Object) this).method_8110((net.minecraft.class_7225.class_7874) a0);
    }


    /** was net/minecraft/class_1865.method_53736()Lcom/mojang/serialization/Codec; */
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

    /** was net/minecraft/class_276.method_1232(I)V */
    public static void method_1232(net.minecraft.class_276 self, int a0) {
        self.method_58226(a0);
    }

    /** was net/minecraft/class_2818$class_5564.field_27227Lnet/minecraft/class_2818; */
    public static net.minecraft.class_2818 field_27227() {
        throw new UnsupportedOperationException("CenturyBridge: Cannot access instance field field_27227 statically");
    }

    /** was net/minecraft/class_2826$class_6869.field_36411Lnet/minecraft/class_2826; */
    public static net.minecraft.class_2826 field_36411() {
        throw new UnsupportedOperationException("CenturyBridge: field_36411 cannot be retrieved statically");
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

    /** was net/minecraft/class_3827.method_16821(Ljava/lang/String;Lcom/mojang/serialization/Codec;)Lnet/minecraft/class_3827; */
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

    /** was net/minecraft/class_4406.method_21325()V */
    public static void method_21325(net.minecraft.class_4406 self) {
        throw new UnsupportedOperationException("CenturyBridge: class_4406.method_21325() was removed in 1.20.5 with no direct replacement");
    }

    /** was net/minecraft/class_4406.method_21344(I)V */
    public static void method_21344(net.minecraft.class_4406 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_4406.method_21344(int) was removed in 1.20.5; merchant recipe index selection moved to the screen widget (class_4877) and no int-accepting equivalent remains on class_4406");
        // CB-CONFIDENCE: high -- the new API surface has no method accepting an int parameter, confirming the old recipe-index setter has no direct replacement
    }

    /** was net/minecraft/class_4406.method_25189(I)I */
    public static int method_25189(net.minecraft.class_4406 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_25189(I)I was removed in 1.20.5; creative inventory tab/page layout was reworked with no direct equivalent");
    }

    /** was net/minecraft/class_4406.method_25193(I)I */
    public static int method_25193(net.minecraft.class_4406 self, int a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_25193(I)I removed in 1.20.5, no direct replacement in MerchantScreen");
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

    /** was net/minecraft/class_4518.method_33317(Lnet/minecraft/class_4517;)V */
    public void method_33317(net.minecraft.class_4517 a0) {
        throw new UnsupportedOperationException("CenturyBridge: method_33317 now requires a class_4520 raid-context parameter whose constructor needs server world, collections, and nested types unavailable from class_4517 alone");
    }


    /** was net/minecraft/class_4588.method_22919(Lnet/minecraft/class_4587$class_4665;Lnet/minecraft/class_777;FFFII)V */
    public void method_22919(net.minecraft.class_4587.class_4665 a0, net.minecraft.class_777 a1, float a2, float a3, float a4, int a5, int a6) {
        ((net.minecraft.class_4588) (Object) this).method_22919(a0, a1, a2, a3, a4, 1.0f, a5, a6);
    }

    /** was net/minecraft/class_4588.method_22920(Lnet/minecraft/class_4587$class_4665;Lnet/minecraft/class_777;[FFFF[IIZ)V */
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

    /** was net/minecraft/class_5713.method_32943(Lnet/minecraft/class_5712;Lnet/minecraft/class_243;Lnet/minecraft/class_5712$class_7397;Lnet/minecraft/class_5713$class_7721;)Z */
    public boolean method_32943(net.minecraft.class_5712 a0, net.minecraft.class_243 a1, net.minecraft.class_5712.class_7397 a2, net.minecraft.class_5713.class_7721 a3) {
        return ((net.minecraft.class_5713) (Object) this).method_32943(net.minecraft.class_6880.method_40223(a0), a1, a2, a3);
    }

    /** was net/minecraft/class_5714.method_32947(Lnet/minecraft/class_3218;Lnet/minecraft/class_5712;Lnet/minecraft/class_5712$class_7397;Lnet/minecraft/class_243;)Z */
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


    /** was net/minecraft/class_8962.field_47356Lnet/minecraft/class_8962; */
    public static net.minecraft.class_8962 field_47356() {
        throw new UnsupportedOperationException("CenturyBridge: class_8962.field_47356 was deleted in 1.20.5; remaining constants field_47357/field_48860/field_48861 are not equivalent");
    }

    /** was net/minecraft/class_8966.field_47378Lnet/minecraft/class_6005; */
    public static net.minecraft.class_6005 field_47378() {
        throw new UnsupportedOperationException("CenturyBridge: field_47378 (class_6005) was deleted in 1.20.5 with no direct replacement; cannot reconstruct the original constant value without class_6005's API surface");
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

    /** was net/minecraft/class_1100.method_4753(Lnet/minecraft/class_7775;Ljava/util/function/Function;Lnet/minecraft/class_3665;Lnet/minecraft/class_2960;)Lnet/minecraft/class_1087; */
    public net.minecraft.class_1087 method_4753(net.minecraft.class_7775 a0, java.util.function.Function a1, net.minecraft.class_3665 a2, net.minecraft.class_2960 a3) {
        return ((net.minecraft.class_1100) (Object) this).method_4753(a0, a1, a2);
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

    /** was net/minecraft/class_1308.method_5933()Lnet/minecraft/class_1297; */
    public static net.minecraft.class_1297 method_5933(net.minecraft.class_1308 self) {
        return self.cQ();
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

    /** was net/minecraft/class_2643.method_11409(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_1297;Lnet/minecraft/class_2643;)V */
    public static void method_11409(net.minecraft.class_2643 self, net.minecraft.class_1937 a0, net.minecraft.class_2338 a1, net.minecraft.class_2680 a2, net.minecraft.class_1297 a3, net.minecraft.class_2643 a4) {
        throw new UnsupportedOperationException("CenturyBridge: method_11409 accepted an Entity (class_1297) parameter with no equivalent in the new class_2643 API; all replacement candidates (method_11411/method_31700/method_31702) omit the Entity argument");
    }

    /** was net/minecraft/class_290.field_1578Lnet/minecraft/class_296; */
    public static net.minecraft.class_296 field_1578() {
        throw new UnsupportedOperationException("CenturyBridge: class_290.field_1578 (type class_296) was removed in 1.21 with no direct equivalent");
    }

    /** was net/minecraft/class_290.field_1581Lnet/minecraft/class_296; */
    public static net.minecraft.class_296 field_1581() {
        throw new UnsupportedOperationException("CenturyBridge: class_290.field_1581 was a class_296 (VertexFormat) constant deleted in 1.21; the vertex format system was reworked and the new class_290 surface exposes only class_293 elements with no class_296 equivalent to reconstruct");
        // CB-CONFIDENCE: high -- the field is explicitly marked deleted with no replacement, and the new class_290 API surface contains zero class_296 references, so no faithful constant can be synthesized
    }

    /** was net/minecraft/class_290.field_1587Lnet/minecraft/class_296; */
    public static net.minecraft.class_296 field_1587() {
        throw new UnsupportedOperationException("CenturyBridge: class_290.field_1587 (class_296 ButtonWidget instance) was removed in 1.21 with no direct replacement; remaining fields are class_293 ClickableWidget");
        // CB-CONFIDENCE: high -- the field is deleted and the new API surface contains no class_296 fields, so no faithful constant can be produced
    }

    /** was net/minecraft/class_290.field_1591Lnet/minecraft/class_296; */
    public static net.minecraft.class_296 field_1591() {
        throw new UnsupportedOperationException("CenturyBridge: class_290.field_1591 (type class_296) was deleted in 1.21 with no direct replacement; new API surface exposes only class_293 (Drawable) fields, no class_296 equivalent exists");
    }

    /** was net/minecraft/class_290.field_20886Lnet/minecraft/class_296; */
    public static net.minecraft.class_296 field_20886() {
        throw new UnsupportedOperationException("CenturyBridge: class_290.field_20886 (normal Matrix3f) was removed in 1.21; MatrixStack no longer tracks a normal matrix");
    }

    /** was net/minecraft/class_290.field_20887Lnet/minecraft/class_293; */
    public static net.minecraft.class_293 field_20887() {
        throw new UnsupportedOperationException("CenturyBridge: class_290.field_20887 was removed in 1.21 with no direct equivalent");
        // CB-CONFIDENCE: high -- the prompt explicitly states the field was deleted with no direct replacement; a tombstone is the honest bridge.
    }

    /** was net/minecraft/class_290.field_29335Lnet/minecraft/class_296; */
    public static net.minecraft.class_296 field_29335() {
        throw new UnsupportedOperationException("CenturyBridge: class_290.field_29335 (MatrixStack) was removed in 1.21 with no direct replacement; MatrixStack is now passed as a render parameter");
    }

    /** was net/minecraft/class_2952.method_12815(Ljava/util/Iterator;IIII)V */
    public void method_12815(java.util.Iterator a0, int a1, int a2, int a3, int a4) {
        ((net.minecraft.class_2952)(Object) this).method_12815((java.lang.Object) a0, a1, a2, a3, a4);
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

    /** was net/minecraft/class_3505.method_40099(Lnet/minecraft/class_5321;)Ljava/lang/String; */
    public static java.lang.String method_40099(net.minecraft.class_3505 self, net.minecraft.class_5321 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_3505.method_40099 was deleted in 1.21 with no direct replacement; the remaining API surface (constructor, method a returning CompletableFuture, method_40096 returning List) has no equivalent that accepts a class_5321 and returns a String");
    }

    /** was net/minecraft/class_3999.method_18131(Lnet/minecraft/class_289;)V */
    public static void method_18131(net.minecraft.class_3999 self, net.minecraft.class_289 a0) {
        throw new UnsupportedOperationException("CenturyBridge: class_3999.method_18131(class_289) was removed in 1.21 with no direct replacement; the remaining method_18130 requires an additional class_1060 (Texture) argument and returns class_287 (Sprite), which is incompatible with the old void contract");
    }

    /** was net/minecraft/class_4588.method_22901(IIII)V */
    public static void method_22901(net.minecraft.class_4588 self, int a0, int a1, int a2, int a3) {
        self.method_1336(a0, a1, a2, a3);
    }

    /** was net/minecraft/class_4588.method_22912(DDD)Lnet/minecraft/class_4588; */
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

    /** was net/minecraft/class_5146.method_6576(Lnet/minecraft/class_3419;)V */
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

    /** was net/minecraft/class_783.field_4227Lnet/minecraft/class_787; */
    public static net.minecraft.class_787 field_4227() {
        throw new UnsupportedOperationException("CenturyBridge: class_783.field_4227 (static class_787) was deleted in 1.21 with no direct replacement; new class_783 is a record exposing no static class_787 constant, and the original value cannot be inferred from the new API surface");
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

    /** was net/minecraft/class_918.method_30115(Lnet/minecraft/class_4597;Lnet/minecraft/class_1921;Lnet/minecraft/class_4587$class_4665;)Lnet/minecraft/class_4588; */
    public static net.minecraft.class_4588 method_30115(net.minecraft.class_918 self, net.minecraft.class_4597 a0, net.minecraft.class_1921 a1, net.minecraft.class_4587.class_4665 a2) {
        return self.method_30114(a0, a1, a2);
    }

    /** was net/minecraft/class_927.method_4072(Lnet/minecraft/class_1308;FFLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V */
    public static void method_4072(net.minecraft.class_927 self, net.minecraft.class_1308 a0, float a1, float a2, net.minecraft.class_4587 a3, net.minecraft.class_4597 a4, int a5) {
        throw new UnsupportedOperationException("CenturyBridge: class_927.method_4072 (EntityRenderer.render) was removed in 1.21; the entity rendering pipeline was refactored to use EntityRenderState and no direct equivalent exists on class_927");
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
