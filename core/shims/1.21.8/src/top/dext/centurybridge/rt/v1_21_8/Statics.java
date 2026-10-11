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

    public void method_721(net.minecraft.class_161 a0) {
        ((net.minecraft.class_163.class_164) (Object) this).method_721(new net.minecraft.class_8781((net.minecraft.class_8779) (Object) a0, null));
    }

    /** was net/minecraft/class_163.method_712()Ljava/util/Collection; */
    public static java.util.Collection method_712(net.minecraft.class_163 self) {
        return self.method_53693();
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

    public void method_11126(net.minecraft.class_2790 a0) {
        ((net.minecraft.class_8705) (Object) this).method_11126(a0);
    }

    /** was net/minecraft/class_2602.method_36895(Lnet/minecraft/class_6373;)V */
    public static void method_36895(net.minecraft.class_2602 self, net.minecraft.class_6373 a0) {
        a0.method_36949(self);
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

    public void method_12069(net.minecraft.class_2803 a0) {
        ((net.minecraft.class_8706) (Object) this).method_12069(a0);
    }

    public void method_12666(net.minecraft.class_2923 a0) {
        ((net.minecraft.class_8763) (Object) this).method_12666(a0);
    }

    public int method_3788() {
        return ((net.minecraft.class_8599) (Object) this).method_3788();
    }

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
        return net.minecraft.class_2960.method_60654("textures/gui/widgets.png");
    }

    /** was net/minecraft/class_339.field_42117Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_42117() {
        return net.minecraft.class_2960.method_60654("textures/gui/widgets.png");
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

    /** was net/minecraft/class_3898.field_18243I */
    public static int field_18243() {
        return 34;
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

    /** was net/minecraft/class_4381.field_22687Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22687() {
        return net.minecraft.class_2960.method_60655("realms", "textures/gui/realms/slot_frame.png");
    }

    /** was net/minecraft/class_4406.field_22704Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22704() {
        return net.minecraft.class_2960.method_60654("textures/gui/recipe_book.png");
    }

    /** was net/minecraft/class_4406.field_22706Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_22706() {
        return net.minecraft.class_2960.method_60654("textures/gui/container/creative_inventory/creative_inventory.png");
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

    /** was net/minecraft/class_521.field_19125Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_19125() {
        return net.minecraft.class_2960.method_60654("textures/gui/recipe_book.png");
    }

    /** was net/minecraft/class_5289.field_24566Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_24566() {
        return net.minecraft.class_2960.method_60654("textures/gui/social_interactions/black_icon.png");
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
        return net.minecraft.class_2960.method_60654("textures/gui/container/bundle.png");
    }

    /** was net/minecraft/class_5754.field_30116D */
    public static double field_30116() {
        return 8.0;
    }

    /** was net/minecraft/class_7298.method_45342()Z */
    public static boolean method_45342(net.minecraft.class_7298 self) {
        return self.method_44359();
    }

    /** was net/minecraft/class_7591$class_7592.field_39765I */
    public static int field_39765() {
        return 0;
    }

    /** was net/minecraft/class_7591.field_39761Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_39761() {
        return net.minecraft.class_2960.method_60654("textures/gui/chat_status.png");
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
        return net.minecraft.class_2960.method_60654("root");
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

    /** was net/minecraft/class_2615.field_12025Lnet/minecraft/class_265; */
    public static net.minecraft.class_265 field_12025() {
        return net.minecraft.class_259.method_1077();
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

    /** was net/minecraft/class_4208.method_19442()Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 method_19442(net.minecraft.class_4208 self) {
        return self.comp_2207();
    }

    /** was net/minecraft/class_4208.method_19446()Lnet/minecraft/class_2338; */
    public static net.minecraft.class_2338 method_19446(net.minecraft.class_4208 self) {
        return self.comp_2208();
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

    public boolean method_32943(net.minecraft.class_5712 a0, net.minecraft.class_243 a1, net.minecraft.class_5712.class_7397 a2, net.minecraft.class_5713.class_7721 a3) {
        return ((net.minecraft.class_5713) (Object) this).method_32943(net.minecraft.class_6880.method_40223(a0), a1, a2, a3);
    }

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

    /** was net/minecraft/class_7260$1.field_39306Lnet/minecraft/class_7260; */
    public static net.minecraft.class_7260 field_39306() {
        return null;
    }

    public net.minecraft.class_2960 method_42276() {
        return ((net.minecraft.class_7265) (Object) this).method_42276().method_29177();
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

    /** was net/minecraft/class_1492.field_30412I */
    public static int field_30412() {
        return 32767;
    }

    /** was net/minecraft/class_1657.field_49991Z */
    public static boolean field_49991() {
        return true;
        // CB-CONFIDENCE: low -- deleted static boolean constant with no direct replacement; value inferred as true from common feature-flag patterns but unverified
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

    /** was net/minecraft/class_1890.method_8221(Lnet/minecraft/class_1799;)Z */
    public static boolean method_8221(net.minecraft.class_1890 self, net.minecraft.class_1799 a0) {
        return self.method_57529(a0);
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

    /** was net/minecraft/class_1255.method_18858(Ljava/lang/Runnable;)V */
    public static void method_18858(net.minecraft.class_1255 self, java.lang.Runnable a0) {
        self.execute(a0);
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

    /** was net/minecraft/class_1297.field_28629Z */
    public static boolean field_28629() {
        return false;
    }

    /** was net/minecraft/class_1297.field_6039F */
    public static float field_6039() {
        return 0.98F;
    }

    /** was net/minecraft/class_1304.method_5926()I */
    public static int method_5926(net.minecraft.class_1304 self) {
        return self.method_63622();
    }

    /** was net/minecraft/class_1309.method_18397(Lnet/minecraft/class_1799;)Z */
    public static boolean method_18397(net.minecraft.class_1309 self, net.minecraft.class_1799 a0) {
        return true;
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

    /** was net/minecraft/class_1662.method_7408(Lnet/minecraft/class_1799;)I */
    public static int method_7408(net.minecraft.class_1662 self, net.minecraft.class_1799 a0) {
        return self.field_52504.getInt(a0);
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

    /** was net/minecraft/class_174.field_1197Lnet/minecraft/class_2076; */
    public static net.minecraft.class_2076 field_1197() {
        return net.minecraft.class_174.field_54206;
    }

    /** was net/minecraft/class_176.method_35553(Lnet/minecraft/class_169;)Z */
    public static boolean method_35553(net.minecraft.class_176 self, net.minecraft.class_169 a0) {
        return self.method_777().contains(a0);
    }

    /** was net/minecraft/class_1842.method_8051(Ljava/util/Optional;Ljava/lang/String;)Ljava/lang/String; */
    public static java.lang.String method_8051(net.minecraft.class_1842 self, java.util.Optional a0, java.lang.String a1) {
        return self.method_63990();
    }

    /** was net/minecraft/class_1860.method_31584()Z */
    public static boolean method_31584(net.minecraft.class_1860 self) {
        return self.method_49188();
    }

    /** was net/minecraft/class_1860.method_8113(II)Z */
    public static boolean method_8113(net.minecraft.class_1860 self, int a0, int a1) {
        return true;
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

    /** was net/minecraft/class_2172.method_9273()Ljava/util/stream/Stream; */
    public static java.util.stream.Stream method_9273(net.minecraft.class_2172 self) {
        return self.method_9254();
        // CB-CONFIDENCE: high -- method_9254() has the identical signature (no args, returns Stream) and is the natural successor to the deleted method_9273()
    }

    /** was net/minecraft/class_2280.method_9751()Lnet/minecraft/class_2280; */
    public static net.minecraft.class_2280 method_9751(net.minecraft.class_2280 self) {
        return self;
    }

    /** was net/minecraft/class_2378.method_10223(Lnet/minecraft/class_2960;)Ljava/lang/Object; */
    public static java.lang.Object method_10223(net.minecraft.class_2378 self, net.minecraft.class_2960 a0) {
        return self.method_10223(a0).orElse(null);
    }

    /** was net/minecraft/class_2378.method_40264(Lnet/minecraft/class_5321;)Ljava/util/Optional; */
    public static java.util.Optional method_40264(net.minecraft.class_2378 self, net.minecraft.class_5321 a0) {
        return self.method_31189(a0);
    }

    /** was net/minecraft/class_2378.method_40270()Ljava/util/stream/Stream; */
    public static java.util.stream.Stream method_40270(net.minecraft.class_2378 self) {
        return self.method_40272();
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

    /** was net/minecraft/class_2579.method_36339()Ljava/util/Optional; */
    public static java.util.Optional method_36339(net.minecraft.class_2579 self) {
        return self.comp_3081();
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

    /** was net/minecraft/class_314.field_17110Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_17110() {
        return new net.minecraft.class_314();
        // CB-CONFIDENCE: medium -- field_17110 was a self-typed static (likely a default/empty singleton); the no-arg constructor is the closest equivalent, though singleton identity is not preserved
    }

    /** was net/minecraft/class_314.field_17114Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_17114() {
        return new net.minecraft.class_314();
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

    /** was net/minecraft/class_314.field_1812Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_1812() {
        return new net.minecraft.class_314();
    }

    /** was net/minecraft/class_314.field_25624Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_25624() {
        return new net.minecraft.class_314();
    }

    /** was net/minecraft/class_314.field_25625Lnet/minecraft/class_314; */
    public static net.minecraft.class_314 field_25625() {
        return new net.minecraft.class_314();
    }

    /** was net/minecraft/class_3302.method_25931(Lnet/minecraft/class_3302$class_4045;Lnet/minecraft/class_3300;Lnet/minecraft/class_3695;Lnet/minecraft/class_3695;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture; */
    public static java.util.concurrent.CompletableFuture method_25931(net.minecraft.class_3302 self, net.minecraft.class_3302.class_4045 a0, net.minecraft.class_3300 a1, net.minecraft.class_3695 a2, net.minecraft.class_3695 a3, java.util.concurrent.Executor a4, java.util.concurrent.Executor a5) {
        return self.method_25931(a0, a1, a4, a5);
    }

    /** was net/minecraft/class_3414.method_14833()Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 method_14833(net.minecraft.class_3414 self) {
        return self.comp_3319();
    }

    /** was net/minecraft/class_3439.field_15295Ljava/util/Set; */
    public static java.util.Set field_15295() {
        return java.util.Set.of();
    }

    /** was net/minecraft/class_3439.method_14875(Lnet/minecraft/class_3439;)V */
    public static void method_14875(net.minecraft.class_3439 self, net.minecraft.class_3439 a0) {
        self.method_30174(a0.method_30173());
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

    /** was net/minecraft/class_4148.field_30258I */
    public static int field_30258() {
        return 2;
    }

    /** was net/minecraft/class_4466.method_23992(Lnet/minecraft/class_2338;)Z */
    public static boolean method_23992(net.minecraft.class_4466 self, net.minecraft.class_2338 a0) {
        return true;
    }

    /** was net/minecraft/class_4861$3.field_7778Lnet/minecraft/class_4861; */
    public static net.minecraft.class_4861 field_7778() {
        return null;
    }

    /** was net/minecraft/class_5354.method_29923(Lnet/minecraft/class_1937;)Z */
    public static boolean method_29923(net.minecraft.class_5354 self, net.minecraft.class_1937 a0) {
        return self.method_29923((net.minecraft.class_3218) a0);
    }

    /** was net/minecraft/class_5514.field_26833Ljava/util/concurrent/ExecutorService; */
    public static java.util.concurrent.ExecutorService field_26833() {
        return java.util.concurrent.Executors.newCachedThreadPool();
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

    /** was net/minecraft/class_744.field_3908Z */
    public static boolean field_3908() {
        return false;
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

    /** was net/minecraft/class_8047.method_48365(I)Z */
    public static boolean method_48365(net.minecraft.class_8047 self, int a0) {
        return a0 >= 0 && a0 < self.method_48369();
    }

    /** was net/minecraft/class_8673.field_51520Z */
    public static boolean field_51520() {
        return false;
    }

    /** was net/minecraft/class_1400.<init>(Lnet/minecraft/class_1308;Ljava/lang/Class;IZZLjava/util/function/Predicate;)V */
    public static net.minecraft.class_1400 cbNewclass_1400(net.minecraft.class_1308 a0, java.lang.Class a1, int a2, boolean a3, boolean a4, java.util.function.Predicate a5) {
        return new net.minecraft.class_1400(a0, a1, a2, a3, a4, a5 == null ? null : (target, world) -> a5.test(target));
    }

    /** was net/minecraft/class_1657.field_5973F */
    public static float field_5973() {
        return 1.62F;
    }

    /** was net/minecraft/class_1696.field_7737D */
    public static double field_7737() {
        return 0.0D;
    }

    /** was net/minecraft/class_1822.<init>(Lnet/minecraft/class_1792$class_1793;Lnet/minecraft/class_2248;Lnet/minecraft/class_2248;)V */
    public static net.minecraft.class_1822 cbNewclass_1822(net.minecraft.class_1792.class_1793 a0, net.minecraft.class_2248 a1, net.minecraft.class_2248 a2) {
        return new net.minecraft.class_1822(a1, a2, a0);
    }

    /** was net/minecraft/class_1826.<init>(Lnet/minecraft/class_1299;IILnet/minecraft/class_1792$class_1793;)V */
    public static net.minecraft.class_1826 cbNewclass_1826(net.minecraft.class_1299 a0, int a1, int a2, net.minecraft.class_1792.class_1793 a3) {
        return new net.minecraft.class_1826(a0, a3);
    }

    /** was net/minecraft/class_1827.<init>(Lnet/minecraft/class_2248;Lnet/minecraft/class_2248;Lnet/minecraft/class_1792$class_1793;Lnet/minecraft/class_2350;)V */
    public static net.minecraft.class_1827 cbNewclass_1827(net.minecraft.class_2248 a0, net.minecraft.class_2248 a1, net.minecraft.class_1792.class_1793 a2, net.minecraft.class_2350 a3) {
        return new net.minecraft.class_1827(a0, a1, a3, a2);
    }

    /** was net/minecraft/class_1842.<init>([Lnet/minecraft/class_1293;)V */
    public static net.minecraft.class_1842 cbNewclass_1842(net.minecraft.class_1293[] a0) {
        return new net.minecraft.class_1842("", a0);
    }

    /** was net/minecraft/class_1847.field_8984Lnet/minecraft/class_1842; */
    public static net.minecraft.class_1842 field_8984() {
        return new net.minecraft.class_1842("empty");
    }

    /** was net/minecraft/class_1936.method_33596(Lnet/minecraft/class_1297;Lnet/minecraft/class_5712;Lnet/minecraft/class_2338;)V */
    public static void method_33596(net.minecraft.class_1936 self, net.minecraft.class_1297 a0, net.minecraft.class_5712 a1, net.minecraft.class_2338 a2) {
        self.method_33596(a0, net.minecraft.class_6880.method_40223(a1), a2);
    }

    /** was net/minecraft/class_1936.method_8396(Lnet/minecraft/class_1657;Lnet/minecraft/class_2338;Lnet/minecraft/class_3414;Lnet/minecraft/class_3419;FF)V */
    public static void method_8396(net.minecraft.class_1936 self, net.minecraft.class_1657 a0, net.minecraft.class_2338 a1, net.minecraft.class_3414 a2, net.minecraft.class_3419 a3, float a4, float a5) {
        self.method_8396(a0, a1, a2, a3, a4, a5);
    }

    /** was net/minecraft/class_2479.<init>(Ljava/util/List;)V */
    public static net.minecraft.class_2479 cbNewclass_2479(java.util.List a0) {
        byte[] bytes = new byte[a0.size()];
        for (int i = 0; i < a0.size(); i++) {
            bytes[i] = ((java.lang.Byte) a0.get(i)).byteValue();
        }
        return new net.minecraft.class_2479(bytes);
    }

    /** was net/minecraft/class_2544.field_22156Lnet/minecraft/class_2754; */
    public static net.minecraft.class_2754 field_22156() {
        return net.minecraft.class_2544.field_55815;
    }

    /** was net/minecraft/class_2544.field_22157Lnet/minecraft/class_2754; */
    public static net.minecraft.class_2754 field_22157() {
        return net.minecraft.class_2544.field_55816;
    }

    /** was net/minecraft/class_2544.field_22158Lnet/minecraft/class_2754; */
    public static net.minecraft.class_2754 field_22158() {
        return net.minecraft.class_2544.field_55815;
    }

    /** was net/minecraft/class_2544.field_22159Lnet/minecraft/class_2754; */
    public static net.minecraft.class_2754 field_22159() {
        return net.minecraft.class_2544.field_55818;
    }

    /** was net/minecraft/class_2588.<init>(Ljava/lang/String;[Ljava/lang/Object;)V */
    public static net.minecraft.class_2588 cbNewclass_2588(java.lang.String a0, java.lang.Object[] a1) {
        return new net.minecraft.class_2588(a0, null, a1);
    }

    /** was net/minecraft/class_2588.<init>(Ljava/lang/String;)V */
    public static net.minecraft.class_2588 cbNewclass_2588(java.lang.String a0) {
        return new net.minecraft.class_2588(a0, a0, new Object[0]);
    }

    /** was net/minecraft/class_3852.field_17061Lnet/minecraft/class_3852; */
    public static net.minecraft.class_3852 field_17061() {
        return net.minecraft.class_7923.field_41195.method_29107(net.minecraft.class_3852.field_17061);
    }

    /** was net/minecraft/class_39.field_844Lnet/minecraft/class_2960; */
    public static net.minecraft.class_2960 field_844() {
        return net.minecraft.class_2960.method_60656("empty");
    }

    /** was net/minecraft/class_4512.<init>(Ljava/lang/String;)V */
    public static net.minecraft.class_4512 cbNewclass_4512(java.lang.String a0) {
        return new net.minecraft.class_4512(net.minecraft.class_2561.method_30163(a0), 0);
    }

    /** was net/minecraft/class_4590.<init>(Lorg/joml/Matrix4f;)V */
    public static net.minecraft.class_4590 cbNewclass_4590(org.joml.Matrix4f a0) {
        return new net.minecraft.class_4590((org.joml.Matrix4fc) a0);
    }

    /** was net/minecraft/class_5134.field_23721Lnet/minecraft/class_1320; */
    public static net.minecraft.class_1320 field_23721() {
        return net.minecraft.class_5134.field_23721.comp_349();
    }

    /** was net/minecraft/class_5483$class_1964.<init>(Lnet/minecraft/class_1299;III)V */
    public static net.minecraft.class_5483.class_1964 cbNewclass_5483class_1964(net.minecraft.class_1299 a0, int a1, int a2, int a3) {
        return new net.minecraft.class_5483.class_1964(a0, a1, a2);
    }

    /** was net/minecraft/class_7375.field_38710Lnet/minecraft/class_5321; */
    public static net.minecraft.class_5321 field_38710() {
        return net.minecraft.class_10686.field_56260;
    }

    /** was net/minecraft/class_7922.method_10223(Lnet/minecraft/class_2960;)Ljava/lang/Object; */
    public static java.lang.Object method_10223(net.minecraft.class_7922 self, net.minecraft.class_2960 a0) {
        return self.method_63535(a0);
    }

    /** was net/minecraft/class_804.field_4285Lorg/joml/Vector3f; */
    public static org.joml.Vector3f field_4285() {
        return new org.joml.Vector3f();
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

    /**
     * 1.20.1 AbstractBlock.Settings.dropsLike(source): the block drops what source drops --
     * the body copied source's loot table id. 1.21.2 keeps the id as an optional key on
     * the settings (overrideLootTable); the same copy, taken at the same moment.
     */
    public static net.minecraft.class_4970.class_2251 method_16228(net.minecraft.class_4970.class_2251 self,
            net.minecraft.class_2248 source) {
        return self.method_63502(source.method_26162());
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

    public static net.minecraft.class_2487 method_38242(net.minecraft.class_2586 self) {
        return self.method_38242(cbRegistries(self));
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

    /** was CampfireBlockEntity.addItem(Entity, ItemStack, int): 1.21.2 takes (ServerWorld, LivingEntity, ItemStack), cook time comes from the recipe */
    public static boolean method_17503(net.minecraft.class_3924 self, net.minecraft.class_1297 user, net.minecraft.class_1799 stack, int cookTime) {
        return self.method_10997() instanceof net.minecraft.class_3218 sw && user instanceof net.minecraft.class_1309 living && self.method_17503(sw, living, stack);
    }

    /** was net/minecraft/class_1280.method_5496(FFF)F */
    public static float method_5496(float a0, float a1, float a2) {
        float f = 2.0F + a2 / 4.0F;
        float g = a1 - a0 / f;
        float minLimit = a1 * 0.2F;
        float clampedG = g < minLimit ? minLimit : (g > 20.0F ? 20.0F : g);
        return a0 * (1.0F - clampedG / 25.0F);
    }

    /** was net/minecraft/class_14.method_59(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338$class_2339;Lnet/minecraft/class_7;)Lnet/minecraft/class_7; */
    public static net.minecraft.class_7 method_59(net.minecraft.class_1922 a0, net.minecraft.class_2338.class_2339 a1, net.minecraft.class_7 a2) {
        return net.minecraft.class_14.method_59(new net.minecraft.class_9316((net.minecraft.class_1941) a0, null), a1.method_10263(), a1.method_10264(), a1.method_10260(), a2);
    }

    /** was net/minecraft/class_1675.method_18077(Lnet/minecraft/class_1937;Lnet/minecraft/class_1297;Lnet/minecraft/class_243;Lnet/minecraft/class_243;Lnet/minecraft/class_238;Ljava/util/function/Predicate;)Lnet/minecraft/class_3966; */
    public static net.minecraft.class_3966 method_18077(net.minecraft.class_1937 a0, net.minecraft.class_1297 a1, net.minecraft.class_243 a2, net.minecraft.class_243 a3, net.minecraft.class_238 a4, java.util.function.Predicate a5) {
        return net.minecraft.class_1675.method_37226(a0, a1, a2, a3, a4, a5, net.minecraft.class_1675.field_46654);
    }

    /** was net/minecraft/class_1704.method_53174(I)Lnet/minecraft/class_1291; */
    public static net.minecraft.class_1291 method_53174(int a0) {
        net.minecraft.class_6880 entry = net.minecraft.class_1704.method_53174(a0);
        return entry == null ? null : (net.minecraft.class_1291) entry.comp_349();
    }

    /** was net/minecraft/class_1714.method_17399(Lnet/minecraft/class_1703;Lnet/minecraft/class_1937;Lnet/minecraft/class_1657;Lnet/minecraft/class_8566;Lnet/minecraft/class_1731;Lnet/minecraft/class_8786;)V
     *  CraftingScreenHandler.updateResult(handler, world, player, input, result, recipe): the old body ran
     *  only off the client (`!world.isClient`); 1.21.2 made that the parameter type */
    public static void method_17399(net.minecraft.class_1703 a0, net.minecraft.class_1937 a1, net.minecraft.class_1657 a2, net.minecraft.class_8566 a3, net.minecraft.class_1731 a4, net.minecraft.class_8786 a5) {
        if (a1 instanceof net.minecraft.class_3218 world) {
            top.dext.centurybridge.shims.v1_21_8.CraftingScreenHandlerInvoker.cb$updateResult(a0, world, a2, a3, a4, a5);
        }
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

    /** was net/minecraft/class_1802.method_7990(Lnet/minecraft/class_2960;Lnet/minecraft/class_1792;)Lnet/minecraft/class_1792; */
    public static net.minecraft.class_1792 method_7990(net.minecraft.class_2960 a0, net.minecraft.class_1792 a1) {
        return net.minecraft.class_1802.method_7990(a0.toString());
    }

    /** was net/minecraft/class_1806.method_7997(Ljava/lang/Integer;Lnet/minecraft/class_1937;)Lnet/minecraft/class_22; */
    public static net.minecraft.class_22 method_7997(java.lang.Integer a0, net.minecraft.class_1937 a1) {
        return net.minecraft.class_1806.method_7997(a0 == null ? null : new net.minecraft.class_9209(a0), a1);
    }

    /** was net/minecraft/class_2248.method_9607(Lnet/minecraft/class_2680;Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;Lnet/minecraft/class_2350;Lnet/minecraft/class_2338;)Z */
    public static boolean method_9607(net.minecraft.class_2680 a0, net.minecraft.class_1922 a1, net.minecraft.class_2338 a2, net.minecraft.class_2350 a3, net.minecraft.class_2338 a4) {
        return net.minecraft.class_2248.method_9607(a0, a1.method_8320(a4), a3);
    }

    /** was net/minecraft/class_2293.method_9804(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)Ljava/util/function/Predicate; */
    public static java.util.function.Predicate method_9804(com.mojang.brigadier.context.CommandContext a0, java.lang.String a1) {
        return net.minecraft.class_2293.method_9804(a0, a1);
    }

    /** was net/minecraft/class_2315.method_10010(Lnet/minecraft/class_2342;)Lnet/minecraft/class_2374; */
    public static net.minecraft.class_2374 method_10010(net.minecraft.class_2342 a0) {
        return net.minecraft.class_2315.method_58682(a0);
    }

    /** was net/minecraft/class_2507.method_30614(Lnet/minecraft/class_2487;Ljava/io/File;)V */
    public static void method_30614(net.minecraft.class_2487 a0, java.io.File a1) throws Exception {
        net.minecraft.class_2507.method_30614(a0, a1.toPath());
    }

    /** was net/minecraft/class_2609.method_31651(Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;Lnet/minecraft/class_2609;)V */
    public static void method_31651(net.minecraft.class_1937 a0, net.minecraft.class_2338 a1, net.minecraft.class_2680 a2, net.minecraft.class_2609 a3) {
        net.minecraft.class_2609.method_31651((net.minecraft.class_3218) a0, a1, a2, a3);
    }

    /** was net/minecraft/class_2754.method_11847(Ljava/lang/String;Ljava/lang/Class;Ljava/util/Collection;)Lnet/minecraft/class_2754; */
    public static net.minecraft.class_2754 method_11847(java.lang.String a0, java.lang.Class a1, java.util.Collection a2) {
        java.util.List list = a2 instanceof java.util.List ? (java.util.List) a2 : new java.util.ArrayList(a2);
        return net.minecraft.class_2754.method_11847(a0, a1, list);
    }

    /** was net/minecraft/class_2852.method_12395(Lnet/minecraft/class_3218;Lnet/minecraft/class_4153;Lnet/minecraft/class_9240;Lnet/minecraft/class_1923;Lnet/minecraft/class_2487;)Lnet/minecraft/class_2839;
     *  ChunkSerializer.deserialize(world, poi, key, pos, nbt): 1.21.2 split it into SerializedChunk.fromNbt
     *  (parse) and convert (build the chunk); null when the nbt is not a chunk, as fromNbt reports it */
    public static net.minecraft.class_2839 method_12395(net.minecraft.class_3218 a0, net.minecraft.class_4153 a1, net.minecraft.class_9240 a2, net.minecraft.class_1923 a3, net.minecraft.class_2487 a4) {
        net.minecraft.class_2852 data = net.minecraft.class_2852.method_61794(a0, a0.method_30349(), a4);
        return data == null ? null : data.method_12395(a0, a1, a2, a3);
    }

    /** was net/minecraft/class_2852.method_12410(Lnet/minecraft/class_3218;Lnet/minecraft/class_2791;)Lnet/minecraft/class_2487; */
    public static net.minecraft.class_2487 method_12410(net.minecraft.class_3218 a0, net.minecraft.class_2791 a1) {
        return net.minecraft.class_2852.method_61793(a0, a1).method_12410();
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

    /** was net/minecraft/class_4148.method_30954(Lnet/minecraft/class_1309;Lnet/minecraft/class_1309;)Z */
    public static boolean method_30954(net.minecraft.class_1309 a0, net.minecraft.class_1309 a1) {
        return net.minecraft.class_4148.method_30954((net.minecraft.class_3218) a0.method_37908(), a0, a1);
    }

    /** was net/minecraft/class_4215.method_19548(Lnet/minecraft/class_1309;Lnet/minecraft/class_1309;F)V */
    public static void method_19548(net.minecraft.class_1309 a0, net.minecraft.class_1309 a1, float a2) {
        net.minecraft.class_4215.method_19548(a0, a1, a2, 0);
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

    /** was net/minecraft/class_4838.method_24733(Lnet/minecraft/class_1657;Z)V */
    public static void method_24733(net.minecraft.class_1657 a0, boolean a1) {
        net.minecraft.class_4838.method_24733((net.minecraft.class_3218) a0.method_37908(), a0, a1);
    }

    /** was net/minecraft/class_5317.method_41594(Lnet/minecraft/class_2378;)Ljava/util/Optional; */
    public static java.util.Optional method_41594(net.minecraft.class_2378 a0) {
        return net.minecraft.class_5317.method_41594(new net.minecraft.class_7723(a0));
    }

    /** was net/minecraft/class_5317.method_41598(Lnet/minecraft/class_5455;)Lnet/minecraft/class_7723; */
    public static net.minecraft.class_7723 method_41598(net.minecraft.class_5455 a0) {
        return net.minecraft.class_5317.method_41598((net.minecraft.class_7225.class_7874)a0);
    }

    /** was net/minecraft/class_5317.method_41599(Lnet/minecraft/class_5455;)Lnet/minecraft/class_5363; */
    public static net.minecraft.class_5363 method_41599(net.minecraft.class_5455 a0) {
        return net.minecraft.class_5317.method_41599((net.minecraft.class_7225.class_7874) a0);
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

    /** was net/minecraft/class_7723.method_45520(Lnet/minecraft/class_2378;Ljava/util/Map;Lnet/minecraft/class_2794;)Ljava/util/Map; */
    public static java.util.Map method_45520(net.minecraft.class_2378 a0, java.util.Map a1, net.minecraft.class_2794 a2) {
        return net.minecraft.class_7723.method_45520((net.minecraft.class_7225) a0, a1, a2);
    }

    /** was net/minecraft/class_8055.method_48440(Lnet/minecraft/class_5455;Lnet/minecraft/class_1799;)Ljava/util/Optional; */
    public static java.util.Optional method_48440(net.minecraft.class_5455 a0, net.minecraft.class_1799 a1) {
        return net.minecraft.class_8055.method_48440(a0, a1);
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

    /** was net/minecraft/class_9362.method_58659(Lnet/minecraft/class_1657;)Z */
    public static boolean method_58659(net.minecraft.class_1657 a0) {
        return net.minecraft.class_9362.method_58659(a0);
    }
}
