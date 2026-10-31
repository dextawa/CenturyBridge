package cb.swall;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.class_1928;
import net.minecraft.class_2509;
import net.minecraft.class_2520;
import net.minecraft.class_4095;
import net.minecraft.class_4140;
import net.minecraft.class_4831;
import net.minecraft.server.MinecraftServer;

/**
 * Real-server check of swall's 1.21.11 -> 26.1 server-side bridges (written against 1.21.11, converted one
 * step): ExpirableValue (own values, a subclass's inherited calls, the codec), Brain (getMemories as a live
 * view of the slots, serializeStart / Brain.codec round trip, copyWithoutBehaviors sharing the memories) and
 * WorldData.getGameRules. Prints "CBTEST PASS|FAIL name" lines and a summary.
 */
public class SwallTestMod implements ModInitializer {

    private static final List<String> FAILS = new ArrayList<>();
    /** calls of the restored 1.21.11 furnace burn (mixin/FurnaceBurnMixin) */
    public static final java.util.concurrent.atomic.AtomicInteger BURN_CALLS = new java.util.concurrent.atomic.AtomicInteger();
    /** calls of ItemStack.CODEC's components getter lambda (mixin/ItemStackCodecLambdaMixin) */
    public static final java.util.concurrent.atomic.AtomicInteger CODEC_PATCH_CALLS = new java.util.concurrent.atomic.AtomicInteger();
    public static final java.util.concurrent.atomic.AtomicInteger FROG_ATTACK_CALLS = new java.util.concurrent.atomic.AtomicInteger();
    public static final java.util.concurrent.atomic.AtomicInteger FANCY_LIMB_CALLS = new java.util.concurrent.atomic.AtomicInteger();
    public static final java.util.concurrent.atomic.AtomicInteger LOOT_MINE_CALLS = new java.util.concurrent.atomic.AtomicInteger();
    private static int passed;
    private static int villagerMemoriesBefore = -1;
    private static Throwable villagerWriteFailure;

    private static void check(String name, boolean ok, Object detail) {
        if (ok) {
            passed++;
            System.out.println("CBTEST PASS " + name);
        } else {
            FAILS.add(name);
            System.out.println("CBTEST FAIL " + name + " : " + detail);
        }
    }

    /** a 1.21.11 subclass: its calls of inherited ExpirableValue members name this class */
    /** 1.21.11: an item reading its own components in its constructor (the stack size and the item name it was built with) */
    static class LegacyComponentsItem extends net.minecraft.class_1792 {
        final Object stackSizeSeen;
        final String nameSeen;

        LegacyComponentsItem(net.minecraft.class_1792.class_1793 properties) {
            super(properties);
            net.minecraft.class_9323 components = this.method_57347();
            this.stackSizeSeen = components.method_58694(net.minecraft.class_9334.field_50071);
            net.minecraft.class_2561 name = components.method_58694(net.minecraft.class_9334.field_50239);
            this.nameSeen = name != null && name.method_10851() instanceof net.minecraft.class_2588 t ? t.method_11022() : String.valueOf(name);
        }
    }

    static class Sub extends class_4831<String> {
        Sub() {
            super("sub", 5L);
        }

        long tickTwice() {
            method_24913();
            method_24913();
            return method_35127();
        }

        @Override
        public boolean method_24634() {
            return super.method_24634() || method_35127() < 2L;
        }
    }

    @Override
    public void onInitialize() {
        // 1.21.11 Villager's private MEMORY_TYPES replaced with one more memory before any brain is built
        // (VillagerMemoryAccessor; rt/brainlists): read back on a live villager's brain once the server runs
        try {
            com.google.common.collect.ImmutableList<class_4140<?>> vm = cb.swall.mixin.VillagerMemoryAccessor.cbswall$memories();
            villagerMemoriesBefore = vm.size();
            cb.swall.mixin.VillagerMemoryAccessor.cbswall$setMemories(com.google.common.collect.ImmutableList.<class_4140<?>>builder()
                .addAll(vm).add(class_4140.field_30246).build());
        } catch (Throwable t) {
            villagerWriteFailure = t;
        }
        // 1.21.11 Item.components read in the item's own constructor (CbWallItemComponentsMixin): 1.21.11 built them there
        try {
            net.minecraft.class_5321<net.minecraft.class_1792> key = net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41197,
                net.minecraft.class_2960.method_60655("cbswall", "legacy_components"));
            LegacyComponentsItem item = new LegacyComponentsItem(new net.minecraft.class_1792.class_1793().method_7889(16).method_63686(key));
            net.minecraft.class_2378.method_39197(net.minecraft.class_7923.field_41178, key, item);
            String seen = item.stackSizeSeen + "/" + item.nameSeen;
            check("item.componentsInConstructor", "16/item.cbswall.legacy_components".equals(seen), seen);
            // 1.21.11 made stacks of an item before the registries loaded (26.1's ItemStack reads the item's holder)
            int max = new net.minecraft.class_1799(item).method_7914();
            check("item.stackBeforeBinding", max == 16, max);
        } catch (Throwable t) {
            check("item.componentsInConstructor", false, t);
        }
        // blocks are built while the block registry still makes intrusive holders (before it freezes)
        try {
            legacyStem = net.minecraft.class_2378.method_10230(net.minecraft.class_7923.field_41175,
                net.minecraft.class_2960.method_60655("cbswall", "legacy_stem"), new LegacyStem());
            legacyRoots = net.minecraft.class_2378.method_10230(net.minecraft.class_7923.field_41175,
                net.minecraft.class_2960.method_60655("cbswall", "legacy_roots"), new LegacyRoots());
        } catch (Throwable t) {
            legacyBlocksFailure = t;
        }
        // ---- ExpirableValue, no server needed
        class_4831<String> ev = class_4831.method_24636("x", 3L);
        ev.method_24913();
        check("expirable.tick", ev.method_35127() == 2L && !ev.method_24634() && ev.method_24914(), ev);
        class_4831<String> forever = class_4831.method_28355("y");
        forever.method_24913();
        check("expirable.never", !forever.method_24914() && "y".equals(forever.method_24637()), forever);
        Sub sub = new Sub();
        check("expirable.subclass", sub.tickTwice() == 3L && !sub.method_24634(), sub);
        Codec<class_4831<String>> codec = class_4831.method_28353(Codec.STRING);
        com.google.gson.JsonElement json = codec.encodeStart(JsonOps.INSTANCE, class_4831.method_24636("z", 7L)).getOrThrow();
        class_4831<String> back = codec.parse(JsonOps.INSTANCE, json).getOrThrow();
        check("expirable.codec", "z".equals(back.method_24637()) && back.method_35127() == 7L
                && json.toString().contains("\"ttl\":7"), json);

        net.minecraft.class_2378.method_10230(net.minecraft.class_7923.field_41189,
            net.minecraft.class_2960.method_60655("cbswall", "test_recipe"), TEST_SERIALIZER);
        TestRecipe recipe = new TestRecipe(net.minecraft.class_7710.field_40250);
        check("customRecipe.category", recipe.method_45441() == net.minecraft.class_7710.field_40250, recipe.method_45441());
        com.google.gson.JsonElement recipeJson = TEST_SERIALIZER.method_53736().codec().encodeStart(JsonOps.INSTANCE, recipe).getOrThrow();
        TestRecipe decoded = TEST_SERIALIZER.method_53736().codec().parse(JsonOps.INSTANCE, recipeJson).getOrThrow();
        check("customRecipe.serializerCodec", recipeJson.toString().contains("\"category\":\"equipment\"")
            && decoded.method_45441() == net.minecraft.class_7710.field_40250, recipeJson);
        check("customRecipe.getSerializer", recipe.method_8119() == TEST_SERIALIZER, recipe.method_8119());

        lootRegistry();
        STEP_TYPE = net.minecraft.class_6018.method_35014("cbswall_step", StepInt.CODEC);
        HALF_TYPE = net.minecraft.class_2378.method_10230(net.minecraft.class_7923.field_41139,
            net.minecraft.class_2960.method_60655("cbswall", "half"), () -> HalfFloat.CODEC);
        EARLY_RESISTANT = new net.minecraft.class_10215(net.minecraft.class_8103.field_42246);
        EARLY_ATTACKS = new net.minecraft.class_10707(0.25F, 1.0F, List.of(), net.minecraft.class_10707.class_10709.field_56308,
            Optional.of(net.minecraft.class_8103.field_56242), Optional.empty(), Optional.empty());
        // before any registries are up: the clock is looked up on first use (rt/CbWallClocks.Lazy)
        try {
            EARLY_TIMELINE = net.minecraft.class_12329.method_76478().method_76491(24000).method_76490();
            EARLY_TIME_CHECK = new net.minecraft.class_4571(Optional.of(24000L), net.minecraft.class_42.method_32387(5));
        } catch (Throwable t) {
            earlyClockFailure = t;
        }
        ServerLifecycleEvents.SERVER_STARTED.register(SwallTestMod::onStarted);
    }

    /** 1.21.11 LevelSettings (rt/CbWallLevelSettings, CbWallLevelSettingsMixin) and LevelSummary (super-ctor reshape) */
    private static void levelSettings() {
        try {
            net.minecraft.class_1928 rules = new net.minecraft.class_1928(net.minecraft.class_7701.field_40182);
            net.minecraft.class_1940 s = new net.minecraft.class_1940("cbswall", net.minecraft.class_1934.field_9220, true,
                net.minecraft.class_1267.field_5807, true, rules, net.minecraft.class_7712.field_40260);
            boolean own = "cbswall".equals(s.method_27339()) && s.method_8574() == net.minecraft.class_1934.field_9220 && s.method_8583()
                && s.method_27340() == net.minecraft.class_1267.field_5807 && s.method_8582() && s.method_27341() == rules;
            net.minecraft.class_1940 easy = s.method_28381(net.minecraft.class_1267.field_5805);
            boolean wither = easy.method_27340() == net.minecraft.class_1267.field_5805 && easy.method_8583() && easy.method_27341() == rules;
            net.minecraft.class_1940 copy = s.method_28385();
            boolean copied = copy.method_27341() != null && copy.method_27341() != rules && copy.method_8583();
            check("levelSettings.v", own && wither && copied, own + " " + wither + " " + copied);
            net.minecraft.class_34 summary = new net.minecraft.class_34(s, null, "cbswall-id", false, false, false, null) { };
            check("levelSummary.subclass", summary.method_35505() == s && "cbswall-id".equals(summary.method_248()) && summary.method_257(), summary);
        } catch (Throwable e) {
            check("levelSettings.v", false, e);
        }
    }

    /** 1.21.11 new VillagerProfession(6 args) (rt/CbWallProfessions): its own values, no trade sets */
    private static void professions() {
        try {
            net.minecraft.class_2561 name = net.minecraft.class_2561.method_43470("cbswall_prof");
            net.minecraft.class_3852 p = new net.minecraft.class_3852(name, h -> false, h -> true,
                com.google.common.collect.ImmutableSet.of(net.minecraft.class_1802.field_8687), com.google.common.collect.ImmutableSet.of(), null);
            check("villagerProfession.ctor", p.comp_818() == name && !p.comp_819().test(null) && p.comp_820().test(null)
                && p.comp_821().contains(net.minecraft.class_1802.field_8687) && p.comp_822().isEmpty() && p.comp_823() == null, p);
        } catch (Throwable e) {
            check("villagerProfession.ctor", false, e);
        }
    }

    static net.minecraft.class_12329 EARLY_TIMELINE;

    /** a 1.21.11 entity subclass reading and writing the protected Entity.fluidHeight (rt/CbWallEntityFluids) */
    static class FluidStand extends net.minecraft.class_1531 {
        FluidStand(net.minecraft.class_1937 level) {
            super(net.minecraft.class_1299.field_6131, level);
        }

        double height(net.minecraft.class_6862<net.minecraft.class_3611> tag) {
            return this.field_5964.getDouble(tag);
        }

        void put(net.minecraft.class_6862<net.minecraft.class_3611> tag, double h) {
            this.field_5964.put(tag, h);
        }

        void replace(it.unimi.dsi.fastutil.objects.Object2DoubleMap<net.minecraft.class_6862<net.minecraft.class_3611>> heights) {
            this.field_5964 = heights;
        }
    }

    /** 1.21.11 Entity.fluidHeight: a view of getFluidHeight; a mod's own tag written there is answered by getFluidHeight */
    private static void fluidHeights(MinecraftServer server) {
        try {
            net.minecraft.class_3218 level = server.method_30002();
            FluidStand stand = new FluidStand(level);
            net.minecraft.class_6862<net.minecraft.class_3611> water = net.minecraft.class_3486.field_15517;
            net.minecraft.class_6862<net.minecraft.class_3611> own = net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_41270,
                net.minecraft.class_2960.method_60655("cbswall", "goo"));
            boolean dry = stand.height(water) == stand.method_5861(water) && stand.height(own) == 0.0;
            stand.put(own, 0.75);
            boolean put = stand.height(own) == 0.75 && stand.method_5861(own) == 0.75;
            it.unimi.dsi.fastutil.objects.Object2DoubleMap<net.minecraft.class_6862<net.minecraft.class_3611>> m =
                new it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap<>();
            m.put(own, 0.5);
            stand.replace(m);
            boolean replaced = stand.method_5861(own) == 0.5;
            check("entity.fluidHeight", dry && put && replaced, dry + " " + put + " " + replaced);
        } catch (Throwable e) {
            check("entity.fluidHeight", false, e);
        }
    }
    static net.minecraft.class_4571 EARLY_TIME_CHECK;
    static Throwable earlyClockFailure;

    /** 1.21.11 Timeline (builder, getTotalTicks / getCurrentTicks on a level) and TimeCheck shapes (rt/CbWallClocks) */
    private static void clocks(MinecraftServer server) {
        try {
            net.minecraft.class_3218 level = server.method_30002();
            net.minecraft.class_12329 t = net.minecraft.class_12329.method_76478().method_76491(24000).method_76490();
            long day = level.method_8532();
            long total = t.method_76485(level), current = t.method_76481(level);
            check("clock.timelineTicks", total == day && current == day % 24000, total + " " + current + " " + day);
            check("clock.timelineBuiltEarly", earlyClockFailure == null && EARLY_TIMELINE.method_76485(level) == day
                && EARLY_TIMELINE.method_76481(level) == day % 24000, earlyClockFailure);
            net.minecraft.class_42 five = net.minecraft.class_42.method_32387(5);
            net.minecraft.class_4571 made = new net.minecraft.class_4571(Optional.of(24000L), five);
            net.minecraft.class_4571 built = net.minecraft.class_4571.method_35559(five).method_35560(24000L).method_35561();
            net.minecraft.class_4571 sub = new net.minecraft.class_4571.class_6164(five) { }.method_35560(24000L).method_35561();
            check("clock.timeCheck", made.equals(built) && made.equals(sub) && made.comp_1885().get() == 24000L
                && made.comp_1886() == five, made + " " + built + " " + sub);
            check("clock.timeCheckBuiltEarly", EARLY_TIME_CHECK != null && EARLY_TIME_CHECK.comp_1885().get() == 24000L
                && EARLY_TIME_CHECK.equals(new net.minecraft.class_4571(Optional.of(24000L), EARLY_TIME_CHECK.comp_1886())), EARLY_TIME_CHECK);
        } catch (Throwable e) {
            check("clock.timelineTicks", false, e);
        }
    }

    /** a 1.21.11 loot function and its type, registered into 26.1's registry of codecs */
    static final class TestFn implements net.minecraft.class_117 {
        @Override
        public net.minecraft.class_5339<TestFn> method_29321() {
            return TEST_FN_TYPE;
        }

        @Override
        public net.minecraft.class_1799 apply(net.minecraft.class_1799 stack, net.minecraft.class_47 context) {
            return stack;
        }
    }

    static net.minecraft.class_10707 EARLY_ATTACKS;
    /** 1.21.11 new DamageResistant(TagKey) while the item registry fills, before any registries are loaded */
    static net.minecraft.class_10215 EARLY_RESISTANT;
    static net.minecraft.class_6018<StepInt> STEP_TYPE;
    static net.minecraft.class_5864<HalfFloat> HALF_TYPE;

    /** a 1.21.11 int provider: always its value, bounds value..value */
    static final class StepInt extends net.minecraft.class_6017 {
        static final com.mojang.serialization.MapCodec<StepInt> CODEC = Codec.INT.fieldOf("step").xmap(StepInt::new, p -> p.value);
        final int value;

        StepInt(int value) {
            this.value = value;
        }

        @Override
        public int method_35008(net.minecraft.class_5819 random) {
            return value;
        }

        @Override
        public int method_35009() {
            return value;
        }

        @Override
        public int method_35011() {
            return value;
        }

        @Override
        public net.minecraft.class_6018<?> method_35012() {
            return STEP_TYPE;
        }
    }

    /** a 1.21.11 float provider registered through a lambda type */
    static final class HalfFloat extends net.minecraft.class_5863 {
        static final com.mojang.serialization.MapCodec<HalfFloat> CODEC = com.mojang.serialization.MapCodec.unit(HalfFloat::new);

        @Override
        public float method_33920(net.minecraft.class_5819 random) {
            return 0.5F;
        }

        @Override
        public float method_33915() {
            return 0.5F;
        }

        @Override
        public float method_33921() {
            return 0.5F;
        }

        @Override
        public net.minecraft.class_5864<?> method_33923() {
            return HALF_TYPE;
        }
    }

    /** a 1.21.11 subclass of ClampedInt (an open class there) */
    static final class LoggedClamp extends net.minecraft.class_6334 {
        LoggedClamp() {
            super(net.minecraft.class_6019.method_35017(0, 100), 10, 20);
        }

        @Override
        public net.minecraft.class_6018<?> method_35012() {
            return super.method_35012();
        }
    }
    static final com.mojang.serialization.MapCodec<TestFn> TEST_FN_CODEC = com.mojang.serialization.MapCodec.unit(TestFn::new);
    static net.minecraft.class_5339<TestFn> TEST_FN_TYPE;

    /** BuiltInRegistries.LOOT_FUNCTION_TYPE read back by 1.21.11 code: values are LootItemFunctionType again */
    private static void lootRegistry() {
        net.minecraft.class_2960 id = net.minecraft.class_2960.method_60655("cbswall", "test_fn");
        net.minecraft.class_2378<net.minecraft.class_5339<?>> reg = net.minecraft.class_7923.field_41134;
        TEST_FN_TYPE = net.minecraft.class_2378.method_10230(reg, id, new net.minecraft.class_5339<>(TEST_FN_CODEC));
        net.minecraft.class_5339<?> read = reg.method_63535(id);
        check("lootRegistry.getValue", read == TEST_FN_TYPE && read.comp_1858() == TEST_FN_CODEC, read);
        check("lootRegistry.getKey", id.equals(reg.method_10221(TEST_FN_TYPE)), reg.method_10221(TEST_FN_TYPE));
        check("lootRegistry.lambda", reg.method_10220().filter(t -> t == TEST_FN_TYPE).count() == 1, "");
        check("lootRegistry.methodRef", reg.method_10220().map(net.minecraft.class_5339::comp_1858).anyMatch(c -> c == TEST_FN_CODEC), "");
        int n = 0;
        for (net.minecraft.class_5339<?> t : reg) {
            n++;
        }
        Object o = reg.method_63535(net.minecraft.class_2960.method_60656("set_count"));
        check("lootRegistry.iterateAndInstanceof", n > 30 && o instanceof net.minecraft.class_5339<?> sc
            && sc.comp_1858() == net.minecraft.class_141.field_45845 && new TestFn().method_29321() == TEST_FN_TYPE, n + " " + o);
    }

    /** data components whose value type 26.1 changed, set and read by 1.21.11 code */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void componentValues(MinecraftServer server) {
        net.minecraft.class_2378<net.minecraft.class_8110> damageTypes = server.method_30611().method_30530(net.minecraft.class_7924.field_42534);
        net.minecraft.class_6880<net.minecraft.class_8110> generic = damageTypes.method_46747(net.minecraft.class_8111.field_42348);
        net.minecraft.class_1799 stack = new net.minecraft.class_1799(net.minecraft.class_1802.field_8600);
        stack.method_57379(net.minecraft.class_9334.field_63636, new net.minecraft.class_9791<>(generic));
        net.minecraft.class_9791<net.minecraft.class_8110> back = stack.method_58694(net.minecraft.class_9334.field_63636);
        check("componentValue.eitherHolder", back != null && back.comp_3636().left().get().method_40230().get().equals(net.minecraft.class_8111.field_42348), back);
        stack.method_57379(net.minecraft.class_9334.field_63636, new net.minecraft.class_9791<>(net.minecraft.class_8111.field_42348));
        Object raw = stack.method_58694(net.minecraft.class_9334.field_63636);
        check("componentValue.eitherKeyResolvedAndInstanceof", raw instanceof net.minecraft.class_9791<?> e
            && e.comp_3636().left().isPresent() && e.comp_3636().left().get().method_40230().get().equals(net.minecraft.class_8111.field_42348), raw);

        net.minecraft.class_6862<net.minecraft.class_2582> flower = net.minecraft.class_1799.class.cast(
            new net.minecraft.class_1799(net.minecraft.class_1802.field_8498)).method_58694(net.minecraft.class_9334.field_56398);
        check("componentValue.bannerTagVanilla", net.minecraft.class_7440.field_39098.equals(flower), flower);
        stack.method_57379(net.minecraft.class_9334.field_56398, net.minecraft.class_7440.field_39098);
        Optional<net.minecraft.class_2960> loc = Optional.ofNullable(stack.method_58694(net.minecraft.class_9334.field_56398))
            .map((net.minecraft.class_6862<net.minecraft.class_2582> t) -> t.comp_327());
        check("componentValue.bannerTagSetLambda", loc.isPresent() && loc.get().equals(net.minecraft.class_7440.field_39098.comp_327()), loc);

        net.minecraft.class_10711 trim = new net.minecraft.class_1799(net.minecraft.class_1802.field_8620).method_58694(net.minecraft.class_9334.field_56397);
        check("componentValue.providesTrimMaterial", trim != null && trim.comp_3599().comp_3636().left().get().method_40230().get()
            .equals(net.minecraft.class_8055.field_42005), trim);

        net.minecraft.class_10707 attacks = new net.minecraft.class_10707(0.25F, 1.0F, List.of(), net.minecraft.class_10707.class_10709.field_56308,
            Optional.of(net.minecraft.class_8103.field_56242), Optional.empty(), Optional.empty());
        net.minecraft.class_10707 shield = new net.minecraft.class_1799(net.minecraft.class_1802.field_8255).method_58694(net.minecraft.class_9334.field_56396);
        check("componentValue.blocksAttacksTag", attacks.comp_3637().get().equals(net.minecraft.class_8103.field_56242)
            && shield.comp_3637().get().equals(net.minecraft.class_8103.field_56242), attacks.comp_3637() + " " + shield.comp_3637());
        net.minecraft.class_1799 early = new net.minecraft.class_1799(net.minecraft.class_1802.field_8600);
        early.method_57379(net.minecraft.class_9334.field_56396, EARLY_ATTACKS);
        net.minecraft.class_10707 earlyBack = early.method_58694(net.minecraft.class_9334.field_56396);
        com.google.gson.JsonElement earlyJson = net.minecraft.class_1799.field_24671.encodeStart(
            server.method_30611().method_57093(com.mojang.serialization.JsonOps.INSTANCE), early).getOrThrow();
        check("componentValue.blocksAttacksEarlyTag", earlyBack.comp_3637().get().equals(net.minecraft.class_8103.field_56242)
            && earlyJson.toString().contains("#minecraft:bypasses_shield"), earlyJson);

        // DamageResistant(TagKey): built before and after the registries load, both resist fire and not generic
        // damage, both read back the tag, both encode as the tag
        net.minecraft.class_8109 sources = server.method_30002().method_48963();
        net.minecraft.class_10215 late = new net.minecraft.class_10215(net.minecraft.class_8103.field_42246);
        for (Object[] c : new Object[][] {{"early", EARLY_RESISTANT}, {"late", late}}) {
            net.minecraft.class_10215 r = (net.minecraft.class_10215) c[1];
            boolean fire = r.method_64198(sources.method_48794());
            boolean notFire = r.method_64198(sources.method_48830());
            String json = String.valueOf(net.minecraft.class_10215.field_54229.encodeStart(
                server.method_30611().method_57093(com.mojang.serialization.JsonOps.INSTANCE), r).result().orElse(null));
            check("componentValue.damageResistantCtor." + c[0], fire && !notFire
                && net.minecraft.class_8103.field_42246.equals(r.comp_3210()) && json.contains("#minecraft:is_fire"),
                fire + " " + notFire + " " + r.comp_3210() + " " + json);
        }
        net.minecraft.class_1799 resist = new net.minecraft.class_1799(net.minecraft.class_1802.field_8600);
        resist.method_57379(net.minecraft.class_9334.field_54273, EARLY_RESISTANT);
        net.minecraft.class_10215 resistBack = resist.method_58694(net.minecraft.class_9334.field_54273);
        check("componentValue.damageResistantOnStack", resistBack != null
            && resistBack.method_64198(sources.method_48794()) && !resistBack.method_64198(sources.method_48830()), resistBack);

        // JukeboxPlayable(EitherHolder): by key (resolved in the registries in use) and by holder; reads back the song
        net.minecraft.class_6880<net.minecraft.class_9793> cat = server.method_30611().method_30530(net.minecraft.class_7924.field_52176)
            .method_46747(net.minecraft.class_9796.field_52038);
        net.minecraft.class_9792 byKey = new net.minecraft.class_9792(new net.minecraft.class_9791<>(net.minecraft.class_9796.field_52038));
        net.minecraft.class_9792 byHolder = new net.minecraft.class_9792(new net.minecraft.class_9791<>(cat));
        for (Object[] c : new Object[][] {{"key", byKey}, {"holder", byHolder}}) {
            net.minecraft.class_9791<net.minecraft.class_9793> e = ((net.minecraft.class_9792) c[1]).comp_2833();
            boolean ok = e != null && e.comp_3636().left().isPresent()
                && e.comp_3636().left().get().method_40230().get().equals(net.minecraft.class_9796.field_52038);
            check("componentValue.jukeboxPlayableCtor." + c[0], ok, e);
        }
        net.minecraft.class_1799 disc = new net.minecraft.class_1799(net.minecraft.class_1802.field_8600);
        disc.method_57379(net.minecraft.class_9334.field_52175, byKey);
        net.minecraft.class_9792 discBack = disc.method_58694(net.minecraft.class_9334.field_52175);
        check("componentValue.jukeboxPlayableOnStack", discBack != null && discBack.comp_2833().comp_3636().left()
            .map(h -> h.method_40230().get().equals(net.minecraft.class_9796.field_52038)).orElse(false), discBack);
    }

    /** 1.21.11's SelectorPattern, 26.1's CompilableString of an entity selector */
    private static void selectors() {
        net.minecraft.class_10104 pat = net.minecraft.class_10104.method_62667("@a[limit=1]").getOrThrow();
        check("selector.parse", "@a[limit=1]".equals(pat.comp_3067()) && pat.comp_3068() != null
            && net.minecraft.class_10104.method_62667("@q").error().isPresent(), pat);
        net.minecraft.class_5250 comp = net.minecraft.class_2561.method_43467(pat, Optional.empty());
        check("selector.component", comp.method_10851() instanceof net.minecraft.class_2579 sc && sc.comp_3080().comp_3067().equals("@a[limit=1]"), comp);
        net.minecraft.class_2578 score = new net.minecraft.class_2578(com.mojang.datafixers.util.Either.left(pat), "obj");
        net.minecraft.class_10104 back = net.minecraft.class_10104.field_53684.parse(JsonOps.INSTANCE, new com.google.gson.JsonPrimitive("@p")).getOrThrow();
        check("selector.scoreAndCodec", score.comp_3078().left().get().comp_3067().equals("@a[limit=1]") && "@p".equals(back.comp_3067())
            && new net.minecraft.class_10104("@s", back.comp_3068()).comp_3067().equals("@s"), back);
    }

    /** bundle / container contents and dyes: 1.21.11 stacks and dye items over 26.1 templates and colors */
    private static void itemContainers(MinecraftServer server) {
        net.minecraft.class_9276 bundle = new net.minecraft.class_9276(List.of(new net.minecraft.class_1799(net.minecraft.class_1802.field_8600, 3),
            net.minecraft.class_1799.field_8037));
        int count = 0;
        for (net.minecraft.class_1799 s : bundle.method_57421()) {
            count += s.method_7947();
        }
        check("container.bundleCtor", count == 3, count);
        net.minecraft.class_9288 contents = net.minecraft.class_9288.method_57493(List.of(new net.minecraft.class_1799(net.minecraft.class_1802.field_8620, 2)));
        int iron = 0;
        for (net.minecraft.class_1799 s : contents.method_59714()) {
            iron += s.method_31574(net.minecraft.class_1802.field_8620) ? s.method_7947() : 0;
        }
        check("container.nonEmptyItems", iron == 2, iron);
        net.minecraft.class_1799 leather = new net.minecraft.class_1799(net.minecraft.class_1802.field_8267);
        net.minecraft.class_1799 dyed = net.minecraft.class_9282.method_57471(leather, List.of((net.minecraft.class_1769) net.minecraft.class_1802.field_8264));
        net.minecraft.class_9282 color = dyed.method_58694(net.minecraft.class_9334.field_49644);
        check("dye.applyDyes", color != null && (color.comp_2384() & 0xFFFFFF) == (net.minecraft.class_1767.field_7964.method_7787() & 0xFFFFFF), color);
    }

    /** IntProvider / FloatProvider: 1.21.11 abstract classes with types, 26.1 interfaces with codecs */
    private static void valueProviders(MinecraftServer server) {
        net.minecraft.class_5819 random = net.minecraft.class_5819.method_43049(1L);
        StepInt step = new StepInt(7);
        net.minecraft.class_6017 asProvider = step;
        check("provider.subclass", asProvider.method_35008(random) == 7 && asProvider.method_35009() == 7 && asProvider.method_35012() == STEP_TYPE, step);
        com.google.gson.JsonElement json = net.minecraft.class_6017.field_29946.encodeStart(JsonOps.INSTANCE, step).getOrThrow();
        net.minecraft.class_6017 back = net.minecraft.class_6017.field_29946.parse(JsonOps.INSTANCE, json).getOrThrow();
        check("provider.codecRoundTrip", json.toString().contains("cbswall_step") && back instanceof StepInt s && s.value == 7, json);
        net.minecraft.class_6019 uniform = net.minecraft.class_6019.method_35017(1, 4);
        check("provider.vanillaType", uniform.method_35012() == net.minecraft.class_6018.field_29948
            && ((net.minecraft.class_6017) uniform).method_35012() == net.minecraft.class_6018.field_29948, uniform.method_35012());
        net.minecraft.class_6018<?> read = net.minecraft.class_7923.field_41140.method_63535(net.minecraft.class_2960.method_60656("cbswall_step"));
        check("provider.registryReadBack", read == STEP_TYPE && read.codec() == StepInt.CODEC, read);
        check("provider.rangedCodec", net.minecraft.class_6017.method_35004(0, 5).parse(JsonOps.INSTANCE, new com.google.gson.JsonPrimitive(9)).error().isPresent()
            && net.minecraft.class_6017.method_35004(0, 10).parse(JsonOps.INSTANCE, new com.google.gson.JsonPrimitive(9)).result().isPresent(), "");
        HalfFloat half = new HalfFloat();
        com.google.gson.JsonElement fjson = net.minecraft.class_5863.field_29007.encodeStart(JsonOps.INSTANCE, half).getOrThrow();
        check("provider.float", half.method_33923() == HALF_TYPE && ((net.minecraft.class_5863) half).method_33920(random) == 0.5F
            && fjson.toString().contains("cbswall:half") && net.minecraft.class_5862.method_33908(2F).method_33923() == net.minecraft.class_5864.field_29008, fjson);
        LoggedClamp clamp = new LoggedClamp();
        com.google.gson.JsonElement cjson = net.minecraft.class_6017.field_29946.encodeStart(JsonOps.INSTANCE, clamp).getOrThrow();
        int sample = clamp.method_35008(random);
        check("provider.clampedSubclass", clamp.method_35012() == net.minecraft.class_6018.field_33453 && sample >= 10 && sample <= 20
            && cjson.toString().contains("clamped"), cjson);
    }

    /** a 1.21.11 placement modifier: the origin and the block above it */
    static final class Twice extends net.minecraft.class_6797 {
        @Override
        public java.util.stream.Stream<net.minecraft.class_2338> method_14452(net.minecraft.class_5444 context, net.minecraft.class_5819 random,
                net.minecraft.class_2338 pos) {
            return java.util.stream.Stream.of(pos, pos.method_10084());
        }

        @Override
        public net.minecraft.class_6798<?> method_39615() {
            return net.minecraft.class_6798.field_35749;
        }
    }

    /** a 1.21.11 repeating placement: three times */
    static final class Thrice extends net.minecraft.class_5857 {
        @Override
        protected int method_14452(net.minecraft.class_5819 random, net.minecraft.class_2338 pos) {
            return 3;
        }

        @Override
        public net.minecraft.class_6798<?> method_39615() {
            return net.minecraft.class_6798.field_35749;
        }
    }

    /** method_14452 names both getPositions and RepeatingPlacement.count in 1.21.11 */
    private static void placements() {
        net.minecraft.class_5819 random = net.minecraft.class_5819.method_43049(2L);
        net.minecraft.class_2338 origin = new net.minecraft.class_2338(0, 64, 0);
        net.minecraft.class_6797 twice = new Twice();
        Thrice thrice = new Thrice();
        long a = twice.method_14452(null, random, origin).count();
        long b = thrice.method_14452(null, random, origin).count();
        long c = net.minecraft.class_5450.method_39639().method_14452(null, random, origin).count();
        check("placement.reusedId", a == 2 && b == 3 && c == 1 && thrice.method_14452(random, origin) == 3, a + " " + b + " " + c);
    }

    /** features 26.1 removed: registered under 1.21.11's ids and placing as 1.21.11 did */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void removedFeatures(MinecraftServer server) {
        net.minecraft.class_2378<net.minecraft.class_3031<?>> features = net.minecraft.class_7923.field_41144;
        check("feature.ids", "minecraft:random_patch".equals(String.valueOf(features.method_10221(net.minecraft.class_3031.field_21220)))
            && "minecraft:flower".equals(String.valueOf(features.method_10221(net.minecraft.class_3031.field_21219)))
            && "minecraft:ice_spike".equals(String.valueOf(features.method_10221(net.minecraft.class_3031.field_13562)))
            && "minecraft:forest_rock".equals(String.valueOf(features.method_10221(net.minecraft.class_3031.field_13584))), "");
        net.minecraft.class_3218 level = server.method_30002();
        net.minecraft.class_2794 generator = level.method_14178().method_12129();
        net.minecraft.class_5819 random = net.minecraft.class_5819.method_43049(3L);
        net.minecraft.class_2338 snow = new net.minecraft.class_2338(8, 250, 8);
        level.method_8501(snow, net.minecraft.class_2246.field_10491.method_9564());
        boolean spike = new net.minecraft.class_2975(net.minecraft.class_3031.field_13562, net.minecraft.class_3111.field_24894)
            .method_12862(level, generator, random, snow.method_10084());
        check("feature.iceSpike", spike && level.method_8320(snow).method_27852(net.minecraft.class_2246.field_10225), level.method_8320(snow));
        net.minecraft.class_2338 stone = new net.minecraft.class_2338(40, 250, 40);
        level.method_8501(stone, net.minecraft.class_2246.field_10340.method_9564());
        boolean rock = new net.minecraft.class_2975(net.minecraft.class_3031.field_13584,
            new net.minecraft.class_2963(net.minecraft.class_2246.field_9989.method_9564())).method_12862(level, generator, random, stone.method_10086(3));
        check("feature.forestRock", rock && level.method_8320(stone.method_10084()).method_27852(net.minecraft.class_2246.field_9989), level.method_8320(stone.method_10084()));
        com.google.gson.JsonElement cfg = net.minecraft.class_4638.field_24902.encodeStart(JsonOps.INSTANCE,
            new net.minecraft.class_4638(10, 2, 1, server.method_30611().method_30530(net.minecraft.class_7924.field_41245).method_10220()
                .findFirst().map(p -> server.method_30611().method_30530(net.minecraft.class_7924.field_41245).method_47983(p)).get())).getOrThrow();
        check("feature.randomPatchConfig", cfg.toString().contains("\"tries\":10") && net.minecraft.class_4638.field_24902.parse(JsonOps.INSTANCE, cfg)
            .getOrThrow().comp_149() == 10, cfg);
    }

    /** tree dirt, rule-based providers and disks: 1.21.11 shapes over 26.1's */
    private static void worldgenConfigs() {
        net.minecraft.class_4643.class_4644 builder = new net.minecraft.class_4643.class_4644(
            net.minecraft.class_4651.method_38432(net.minecraft.class_2246.field_10431), new net.minecraft.class_5140(4, 2, 0),
            net.minecraft.class_4651.method_38432(net.minecraft.class_2246.field_10503),
            new net.minecraft.class_4646(net.minecraft.class_6016.method_34998(2), net.minecraft.class_6016.method_34998(0), 3),
            Optional.empty(), new net.minecraft.class_5204(1, 0, 1));
        net.minecraft.class_4643 plain = builder.method_23445();
        net.minecraft.class_4651 stone = net.minecraft.class_4651.method_38432(net.minecraft.class_2246.field_10340);
        net.minecraft.class_4643 forced = builder.method_34346(stone).method_34347().method_23445();
        check("worldgen.treeDirt", !plain.field_29281 && plain.field_29279 instanceof net.minecraft.class_4656
            && forced.field_29281 && forced.field_29279 == stone, plain.field_29279 + " " + forced.field_29279);
        net.minecraft.class_7400 rules = net.minecraft.class_7400.method_43312(net.minecraft.class_2246.field_10340);
        net.minecraft.class_6577 disk = new net.minecraft.class_6577(rules, net.minecraft.class_6646.method_39011(),
            net.minecraft.class_6016.method_34998(2), 1);
        check("worldgen.ruleBasedDisk", rules.comp_725() != null && rules.comp_726().isEmpty() && disk.comp_715() == rules
            && net.minecraft.class_7400.field_38870 != null, disk);
    }

    /** tag-content drift (1.21.11's dirt) and the motion packet codec over a FriendlyByteBuf */
    private static void driftPilots() {
        check("tagDrift.dirt", net.minecraft.class_2246.field_10219.method_9564().method_26164(net.minecraft.class_3481.field_29822)
            && net.minecraft.class_2246.field_10566.method_9564().method_26164(net.minecraft.class_3481.field_29822)
            && !net.minecraft.class_2246.field_10340.method_9564().method_26164(net.minecraft.class_3481.field_29822), "");
        check("tagDrift.itemDirt", new net.minecraft.class_1799(net.minecraft.class_1802.field_8270).method_31573(net.minecraft.class_3489.field_36269)
            && !new net.minecraft.class_1799(net.minecraft.class_1802.field_20391).method_31573(net.minecraft.class_3489.field_36269), "");
        // block tags 26.1 renamed (same members, same vanilla readers): a 1.21.11 read is the tag vanilla 26.1 acts on
        check("tagRename.members", is(net.minecraft.class_2246.field_10460, net.minecraft.class_3481.field_35443)
            && is(net.minecraft.class_2246.field_10362, net.minecraft.class_3481.field_35443)
            && is(net.minecraft.class_2246.field_10460, net.minecraft.class_3481.field_33641)
            && is(net.minecraft.class_2246.field_10295, net.minecraft.class_3481.field_39104)
            && is(net.minecraft.class_2246.field_21211, net.minecraft.class_3481.field_39105)
            && is(net.minecraft.class_2246.field_10102, net.minecraft.class_3481.field_56529)
            && is(net.minecraft.class_2246.field_10362, net.minecraft.class_3481.field_56529)
            && is(net.minecraft.class_2246.field_10255, net.minecraft.class_3481.field_15497)
            && is(net.minecraft.class_2246.field_10402, net.minecraft.class_3481.field_25739)
            && !is(net.minecraft.class_2246.field_10340, net.minecraft.class_3481.field_35443), "");
        check("tagRename.ids", "supports_big_dripleaf".equals(net.minecraft.class_3481.field_35443.comp_327().method_12832())
            && "cannot_support_snow_layer".equals(net.minecraft.class_3481.field_39104.comp_327().method_12832())
            && "support_override_snow_layer".equals(net.minecraft.class_3481.field_39105.comp_327().method_12832())
            && "supports_dry_vegetation".equals(net.minecraft.class_3481.field_56529.comp_327().method_12832())
            && "substrate_overworld".equals(net.minecraft.class_3481.field_29822.comp_327().method_12832()),
            net.minecraft.class_3481.field_35443.comp_327() + " " + net.minecraft.class_3481.field_29822.comp_327());
        net.minecraft.class_2540 buf = new net.minecraft.class_2540(io.netty.buffer.Unpooled.buffer());
        net.minecraft.class_2743.field_47981.encode(buf, new net.minecraft.class_2743(42, new net.minecraft.class_243(0.5, 0.25, -1.0)));
        net.minecraft.class_2743 back = net.minecraft.class_2743.field_47981.decode(buf);
        net.minecraft.class_9139<net.minecraft.class_2540, Integer> ids = net.minecraft.class_2743.field_47981.method_56432(net.minecraft.class_2743::method_11818,
            id -> new net.minecraft.class_2743(id, net.minecraft.class_243.field_1353));
        net.minecraft.class_2540 buf2 = new net.minecraft.class_2540(io.netty.buffer.Unpooled.buffer());
        ids.encode(buf2, 7);
        check("streamCodec.motion", back.method_11818() == 42 && ids.decode(buf2) == 7, back.method_11818());
    }

    /** a 1.21.11 subclass of a recipe class 26.1 removed: vanilla's matches / assemble reach its overrides */
    static class StickDye extends net.minecraft.class_1849 {
        StickDye() {
            super(net.minecraft.class_7710.field_40251);
        }

        @Override
        public boolean method_17701(net.minecraft.class_9694 input, net.minecraft.class_1937 level) {
            return input.method_59984(0).method_31574(net.minecraft.class_1802.field_8600) || super.method_17701(input, level);
        }

        @Override
        public net.minecraft.class_1799 method_17700(net.minecraft.class_9694 input, net.minecraft.class_7225.class_7874 registries) {
            return input.method_59984(0).method_31574(net.minecraft.class_1802.field_8600)
                ? new net.minecraft.class_1799(net.minecraft.class_1802.field_8428) : super.method_17700(input, registries);
        }
    }

    /** ArmorDye / MapCloning / TippedArrow, removed in 26.1, given back with 1.21.11's bodies and serializers */
    private static void removedRecipes(MinecraftServer server) {
        StickDye stickDye = new StickDye();
        net.minecraft.class_9694 stickGrid = net.minecraft.class_9694.method_59986(1, 1, List.of(new net.minecraft.class_1799(net.minecraft.class_1802.field_8600)));
        @SuppressWarnings({"rawtypes", "unchecked"})
        net.minecraft.class_1860<net.minecraft.class_9694> asRecipe = (net.minecraft.class_1860) stickDye;
        check("recipe.removedClassSubclass", asRecipe.method_8115(stickGrid, server.method_30002())
            && asRecipe.method_8116(stickGrid, server.method_30611()).method_31574(net.minecraft.class_1802.field_8428), "");
        net.minecraft.class_7225.class_7874 registries = server.method_30611();
        net.minecraft.class_1799 e = net.minecraft.class_1799.field_8037;
        net.minecraft.class_1849 dye = new net.minecraft.class_1849(net.minecraft.class_7710.field_40251);
        net.minecraft.class_9694 dyeGrid = net.minecraft.class_9694.method_59986(2, 1, List.of(
            new net.minecraft.class_1799(net.minecraft.class_1802.field_8267), new net.minecraft.class_1799(net.minecraft.class_1802.field_8264)));
        net.minecraft.class_1799 dyed = dye.method_17700(dyeGrid, registries);
        check("recipe.armorDye", dye.method_17701(dyeGrid, server.method_30002()) && dyed.method_57826(net.minecraft.class_9334.field_49644)
            && dye.method_8119() == net.minecraft.class_1865.field_9028
            && "minecraft:crafting_special_armordye".equals(String.valueOf(net.minecraft.class_7923.field_41189.method_10221(
                net.minecraft.class_7923.field_41189.method_63535(net.minecraft.class_2960.method_60656("crafting_special_armordye"))))), dyed);
        java.util.List<net.minecraft.class_1799> grid = new java.util.ArrayList<>();
        for (int i = 0; i < 9; i++) {
            grid.add(new net.minecraft.class_1799(i == 4 ? net.minecraft.class_1802.field_8150 : net.minecraft.class_1802.field_8107));
        }
        net.minecraft.class_1876 tipped = new net.minecraft.class_1876(net.minecraft.class_7710.field_40251);
        net.minecraft.class_9694 arrows = net.minecraft.class_9694.method_59986(3, 3, grid);
        net.minecraft.class_1799 out = tipped.method_17740(arrows, registries);
        check("recipe.tippedArrow", tipped.method_17741(arrows, server.method_30002()) && out.method_31574(net.minecraft.class_1802.field_8087)
            && out.method_7947() == 8 && tipped.method_8119() == net.minecraft.class_1865.field_9037, out);
        net.minecraft.class_1855 maps = new net.minecraft.class_1855(net.minecraft.class_7710.field_40251);
        check("recipe.mapCloning", !maps.method_17715(net.minecraft.class_9694.method_59986(2, 1, List.of(
            new net.minecraft.class_1799(net.minecraft.class_1802.field_8895), e)), server.method_30002())
            && maps.method_8119() == net.minecraft.class_1865.field_9044, "");
    }

    /** 1.21.11's special recipe constructors (category only) over 26.1's parameterized recipes */
    private static void specialRecipeCtors(MinecraftServer server) {
        net.minecraft.class_1851 rocket = new net.minecraft.class_1851(net.minecraft.class_7710.field_40250);
        net.minecraft.class_9694 grid = net.minecraft.class_9694.method_59986(2, 1, List.of(
            new net.minecraft.class_1799(net.minecraft.class_1802.field_8407), new net.minecraft.class_1799(net.minecraft.class_1802.field_8054)));
        net.minecraft.class_1799 rockets = rocket.method_17708(grid, server.method_30611());
        net.minecraft.class_4317 repair = new net.minecraft.class_4317(net.minecraft.class_7710.field_40251);
        check("recipe.specialCtors", rocket.method_8115(grid, server.method_30002()) && rockets.method_31574(net.minecraft.class_1802.field_8639)
            && rockets.method_7947() == 3 && rocket.method_45441() == net.minecraft.class_7710.field_40250
            && repair.method_45441() == net.minecraft.class_7710.field_40251
            && new net.minecraft.class_1848(net.minecraft.class_7710.field_40251) != null, rockets);
    }

    /** 1.21.11 SimpleRegionStorage with a legacy tag fixer, over 26.1's storage */
    private static void legacyRegionStorage(MinecraftServer server) throws Exception {
        int[] done = new int[1];
        net.minecraft.class_12242 fixer = new net.minecraft.class_12242() {
            @Override
            public net.minecraft.class_2487 applyFix(net.minecraft.class_2487 tag) {
                tag.method_10556("cbFixed", true);
                return tag;
            }

            @Override
            public void markChunkDone(net.minecraft.class_1923 pos) {
                done[0]++;
            }
        };
        java.nio.file.Path dir = java.nio.file.Files.createTempDirectory("cbswall-region");
        int current = net.minecraft.class_155.method_16673().comp_4026().comp_4038();
        try (net.minecraft.class_3977 storage = new net.minecraft.class_3977(new net.minecraft.class_9240("cbtest", net.minecraft.class_1937.field_25179, "chunk"),
                dir, server.method_3855(), false, net.minecraft.class_4284.field_19214, () -> fixer)) {
            net.minecraft.class_2487 tag = new net.minecraft.class_2487();
            net.minecraft.class_2487 out = storage.method_17907(tag, current - 1, new net.minecraft.class_2487());
            check("regionStorage.legacyFixer", out.method_68566("cbFixed", false) && out.method_68083("DataVersion", 0) == current, out);
        }
    }

    private static boolean is(net.minecraft.class_2248 block, net.minecraft.class_6862<net.minecraft.class_2248> tag) {
        return block.method_9564().method_26164(tag);
    }

    /** 1.21.11 records 26.1 rebuilt around templates, compilable strings, two assets, split world data */
    private static void recordViews(MinecraftServer server) {
        net.minecraft.class_1799 bowl = new net.minecraft.class_1799(net.minecraft.class_1802.field_8428, 3);
        net.minecraft.class_10131 remainder = new net.minecraft.class_10131(bowl);
        net.minecraft.class_2568.class_10612 show = new net.minecraft.class_2568.class_10612(new net.minecraft.class_1799(net.minecraft.class_1802.field_8279));
        check("records.itemTemplates", remainder.comp_3093().method_31574(net.minecraft.class_1802.field_8428) && remainder.comp_3093().method_7947() == 3
            && show.comp_3509().method_31574(net.minecraft.class_1802.field_8279), remainder.comp_3093());
        net.minecraft.class_2575 block = new net.minecraft.class_2575("1 2 3");
        net.minecraft.class_2575 badBlock = new net.minecraft.class_2575("not a position");
        net.minecraft.class_2576 entity = new net.minecraft.class_2576("@p");
        net.minecraft.class_2168 source = server.method_3739();
        boolean emptyData;
        try {
            emptyData = badBlock.method_10916(source).findAny().isEmpty();
        } catch (Exception e) {
            emptyData = false;
        }
        check("records.dataSources", "1 2 3".equals(block.comp_733()) && block.comp_734() != null && badBlock.comp_734() == null
            && "@p".equals(entity.comp_735()) && entity.comp_736() != null && emptyData, block);
        net.minecraft.class_12079.class_10726 tex = new net.minecraft.class_12079.class_10726(net.minecraft.class_2960.method_60655("cbswall", "cat"));
        net.minecraft.class_7375 cat = new net.minecraft.class_7375(tex, net.minecraft.class_10702.field_56285);
        net.minecraft.class_9346.class_10688 wolfInfo = new net.minecraft.class_9346.class_10688(tex, tex, tex);
        net.minecraft.class_9346 wolf = new net.minecraft.class_9346(wolfInfo, net.minecraft.class_10702.field_56285);
        check("records.variants", cat.comp_3560() == tex && wolf.comp_3564() == wolfInfo, cat);
        @SuppressWarnings({"rawtypes", "unchecked"}) net.minecraft.class_7655.class_7657 required = new net.minecraft.class_7655.class_7657(net.minecraft.class_7924.field_41236,
            net.minecraft.class_1959.field_24677, true);
        @SuppressWarnings({"rawtypes", "unchecked"}) net.minecraft.class_7655.class_7657 optional = new net.minecraft.class_7655.class_7657(net.minecraft.class_7924.field_41236,
            net.minecraft.class_1959.field_24677, false);
        java.util.UUID a = net.minecraft.class_3532.method_15394();
        check("records.registryDataAndUuid", required.comp_2664() && !optional.comp_2664() && a.version() == 4
            && !a.equals(net.minecraft.class_3532.method_15394()), a);
    }

    /**
     * 1.21.11 world loading with a WorldData: WorldStem / LevelDataAndDimensions built from one (26.1 takes the data
     * with its generation settings split off), and level.dat through a LevelStorageAccess of a scratch world while
     * the server's own access is open too (getDataTag, getSummary, LevelStorageSource.getLevelDataAndDimensions)
     */
    private static void worldLoading(MinecraftServer server) {
        net.minecraft.class_5219 data = server.method_27728();
        net.minecraft.class_7780<net.minecraft.class_7659> registries = server.method_46221();
        long seed = data.method_28057().method_28028();
        try {
            net.minecraft.class_6904 stem = new net.minecraft.class_6904(null, null, registries, data);
            check("worldLoad.stem", stem.comp_359() == data && stem.comp_358() == registries
                && stem.comp_359().method_28057().method_28028() == seed, stem);
        } catch (Throwable t) {
            check("worldLoad.stem", false, t);
        }
        net.minecraft.class_2378<net.minecraft.class_5363> stems = registries.method_45926().method_30530(net.minecraft.class_7924.field_41224);
        try {
            net.minecraft.class_7723.class_7725 complete = new net.minecraft.class_7723.class_7725(stems, net.minecraft.class_31.class_7729.field_40373);
            net.minecraft.class_8895 both = new net.minecraft.class_8895(data, complete);
            check("worldLoad.levelDataAndDimensions", both.comp_2008() == data && both.comp_2009() == complete
                && both.comp_2008().method_28057().method_28028() == seed, both);
        } catch (Throwable t) {
            check("worldLoad.levelDataAndDimensions", false, t);
        }
        java.nio.file.Path dir = null;
        try {
            dir = java.nio.file.Files.createTempDirectory("cbswall-worlds");
            net.minecraft.class_32 source = net.minecraft.class_32.method_26999(dir);
            try (net.minecraft.class_32.class_5143 access = source.method_27002("scratch")) {
                access.method_27425(registries.method_45926(), data);
                com.mojang.serialization.Dynamic<?> tag = access.method_54545();
                String name = tag.get("LevelName").asString("");
                net.minecraft.class_34 summary = access.method_29584(tag);
                net.minecraft.class_8895 loaded = net.minecraft.class_32.method_54523(tag, data.method_29589(), stems, registries.method_45926());
                check("worldLoad.levelDat", name.equals(data.method_150()) && summary.method_252().equals(data.method_150())
                    && "scratch".equals(summary.method_248()) && loaded.comp_2008().method_150().equals(data.method_150())
                    && loaded.comp_2008().method_28057().method_28028() == seed,
                    name + " / " + summary.method_252() + " / " + loaded.comp_2008().method_150() + " / " + loaded.comp_2008().method_28057().method_28028());
            }
        } catch (Throwable t) {
            check("worldLoad.levelDat", false, t);
        }
    }

    /** 1.21.11 noise, mushroom configuration and NBT statics */
    private static void worldgenStatics(MinecraftServer server) {
        net.minecraft.class_3537 noise = net.minecraft.class_3537.method_39126(net.minecraft.class_5819.method_43049(42L), -3,
            it.unimi.dsi.fastutil.doubles.DoubleList.of(1.0, 1.0, 1.0));
        double six = noise.method_16453(0.3, 0.7, 1.1, 0.0, 0.0, false);
        double three = noise.method_15416(0.3, 0.7, 1.1);
        double fixed = noise.method_16453(0.3, 0.7, 1.1, 0.0, 0.0, true);
        double fixedOtherY = noise.method_16453(0.3, 99.7, 1.1, 0.0, 0.0, true);
        check("noise.getValue6", six == three && fixed == fixedOtherY && Double.isFinite(fixed), six + " " + three + " " + fixed + " " + fixedOtherY);
        net.minecraft.class_4635 mushroom = new net.minecraft.class_4635(net.minecraft.class_4651.method_38432(net.minecraft.class_2246.field_10340),
            net.minecraft.class_4651.method_38432(net.minecraft.class_2246.field_10340), 2);
        check("mushroom.config", mushroom.field_21232 == 2 && mushroom.field_21230 != null, mushroom);
        com.mojang.serialization.Dynamic<net.minecraft.class_2520> dyn = new com.mojang.serialization.Dynamic<>(net.minecraft.class_2509.field_11560, new net.minecraft.class_2487());
        int current = net.minecraft.class_155.method_16673().comp_4026().comp_4038();
        check("nbt.addCurrentDataVersion", net.minecraft.class_2512.method_73082(dyn).get("DataVersion").asInt(0) == current, "");
    }

    /** a 1.21.11 subclass of the structure index */
    static class CountingIndex extends net.minecraft.class_3440 {
        int starts;

        @Override
        public boolean method_14897(long chunk) {
            starts++;
            return super.method_14897(chunk);
        }
    }

    /** 1.21.11 string-named saved data, StructureFeatureIndexSavedData and LegacyStructureDataHandler */
    private static void legacyStructureData(MinecraftServer server) throws Exception {
        java.nio.file.Path dir = java.nio.file.Files.createTempDirectory("cbswall-data");
        net.minecraft.class_10741<net.minecraft.class_3440> type = net.minecraft.class_3440.method_67412("cbswall_index");
        net.minecraft.class_26 storage = new net.minecraft.class_26(dir, server.method_3855(), server.method_30611());
        net.minecraft.class_3440 index = storage.method_17924(type);
        index.method_14896(5L);
        index.method_14896(6L);
        index.method_14895(6L);
        storage.method_125();
        net.minecraft.class_26 reopened = new net.minecraft.class_26(dir, server.method_3855(), server.method_30611());
        net.minecraft.class_3440 back = reopened.method_17924(net.minecraft.class_3440.method_67412("cbswall_index"));
        check("savedData.stringIdFile", java.nio.file.Files.exists(dir.resolve("cbswall_index.dat"))
            && "cbswall_index".equals(type.comp_3641()) && type.equals(net.minecraft.class_3440.method_67412("cbswall_index")), type.comp_3641());
        check("structureIndex.roundTrip", back.method_14897(5L) && back.method_14897(6L) && back.method_14894(5L) && !back.method_14894(6L)
            && back.method_14898().size() == 2, back.method_14898());
        CountingIndex counting = new CountingIndex();
        counting.method_14896(7L);
        check("structureIndex.subclass", counting.method_14897(7L) && counting.starts == 1 && counting.method_14894(7L), counting.starts);

        // a pre-1.13 nether: Fortress.dat in the data folder, a 1.13-era chunk flagged as holding legacy structure data
        net.minecraft.class_2487 feature = new net.minecraft.class_2487();
        feature.method_10569("ChunkX", 0);
        feature.method_10569("ChunkZ", 0);
        feature.method_10582("id", "Fortress");
        net.minecraft.class_2487 features = new net.minecraft.class_2487();
        features.method_10566("[0,0]", feature);
        net.minecraft.class_2487 data = new net.minecraft.class_2487();
        data.method_10566("Features", features);
        net.minecraft.class_2487 file = new net.minecraft.class_2487();
        file.method_10566("data", data);
        file.method_10569("DataVersion", 1493);
        net.minecraft.class_2507.method_30614(file, dir.resolve("Fortress.dat"));
        java.util.function.Supplier<net.minecraft.class_12242> nether = net.minecraft.class_3360.method_14745(net.minecraft.class_1937.field_25180, () -> reopened, server.method_3855());
        net.minecraft.class_12242 fixer = nether.get();
        net.minecraft.class_2487 level = new net.minecraft.class_2487();
        level.method_10569("xPos", 0);
        level.method_10569("zPos", 0);
        level.method_10556("hasLegacyStructureData", true);
        net.minecraft.class_2487 chunk = new net.minecraft.class_2487();
        chunk.method_10566("Level", level);
        chunk.method_10569("DataVersion", 1492);
        // DataFixTypes.update leaves DataVersion to the storage (1.21.11 and 26.1 alike); References gets the fortress key
        net.minecraft.class_2487 fixed = fixer.applyFix(chunk);
        java.util.Optional<long[]> refs = fixed.method_68568("Level").method_68568("Structures").method_68568("References").method_10565("Fortress");
        check("legacyStructure.netherFixer", fixer instanceof net.minecraft.class_3360 && fixer.targetDataVersion() == 1493
            && refs.isPresent() && refs.get().length == 0, fixed);
        fixer.markChunkDone(new net.minecraft.class_1923(0, 0));
        net.minecraft.class_2487 plain = new net.minecraft.class_2487();
        net.minecraft.class_12242 none = net.minecraft.class_3360.method_14745(
            net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41223, net.minecraft.class_2960.method_60655("cbswall", "dim")),
            () -> reopened, server.method_3855()).get();
        check("legacyStructure.otherDimension", !(none instanceof net.minecraft.class_3360) && none.applyFix(plain) == plain
            && none.targetDataVersion() == -1, none);
    }

    /** 1.21.11's one interact packet for three actions, over 26.1's interact and attack packets */
    private static void interactPacket(MinecraftServer server) {
        net.minecraft.class_3218 level = server.method_30002();
        net.minecraft.class_1531 stand = new net.minecraft.class_1531(level, 0.5, 100, 0.5);
        level.method_8649(stand);
        StringBuilder log = new StringBuilder();
        net.minecraft.class_2824.class_5908 handler = new net.minecraft.class_2824.class_5908() {
            @Override
            public void method_34219(net.minecraft.class_1268 hand) {
                log.append("interact;");
            }

            @Override
            public void method_34220(net.minecraft.class_1268 hand, net.minecraft.class_243 location) {
                log.append("at;");
            }

            @Override
            public void method_34218() {
                log.append("attack;");
            }
        };
        net.minecraft.class_2824 attack = net.minecraft.class_2824.method_34206(stand, true);
        net.minecraft.class_2824 at = net.minecraft.class_2824.method_34208(stand, true, net.minecraft.class_1268.field_5808, new net.minecraft.class_243(0, 1, 0));
        attack.method_34209(handler);
        at.method_34209(handler);
        Object asObject = at;
        check("interactPacket.split", attack.method_12248(level) == stand && at.method_12248(level) == stand && at.method_30007()
            && !attack.method_30007() && "attack;at;interact;".contentEquals(log) && asObject instanceof net.minecraft.class_2824, log);
        stand.method_31472();
    }

    /** a 1.21.11 ChunkPos subclass (26.1's ChunkPos is a final record) */
    static final class TaggedChunk extends net.minecraft.class_1923 {
        final String tag;

        TaggedChunk(net.minecraft.class_2338 pos, String tag) {
            super(pos);
            this.tag = tag;
        }

        TaggedChunk(long packed, String tag) {
            super(packed);
            this.tag = tag;
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o) && (!(o instanceof TaggedChunk t) || t.tag.equals(tag));
        }
    }

    /** a 1.21.11 ItemInput subclass (26.1's ItemInput is a final record) */
    static final class NamedInput extends net.minecraft.class_2290 {
        NamedInput() {
            super(net.minecraft.class_1802.field_8477.method_40131(), net.minecraft.class_9326.field_49588);
        }

        @Override
        public String toString() {
            return "named:" + method_9785();
        }
    }

    /** subclasses of classes 26.1 made final records */
    private static void extendFinal() throws Exception {
        TaggedChunk byBlock = new TaggedChunk(new net.minecraft.class_2338(33, 70, -17), "a");
        long packed = ((long) -7 << 32) | (5L & 0xFFFFFFFFL);
        TaggedChunk byLong = new TaggedChunk(packed, "b");
        check("extendFinal.chunkPos", byBlock.field_9181 == 2 && byBlock.field_9180 == -2 && byLong.field_9181 == 5
            && byLong.field_9180 == -7 && byBlock.equals(new net.minecraft.class_1923(2, -2))
            && !byBlock.equals(new TaggedChunk(new net.minecraft.class_2338(33, 0, -17), "c")),
            byBlock.field_9181 + "," + byBlock.field_9180 + " " + byLong.field_9181 + "," + byLong.field_9180);
        NamedInput input = new NamedInput();
        net.minecraft.class_1799 stack = input.method_9781(3, false);
        check("extendFinal.itemInput", input.toString().startsWith("named:") && stack.method_7947() == 3
            && stack.method_31574(net.minecraft.class_1802.field_8477), input + " " + stack);
    }

    /** 1.21.11's TimerQueue subclass writing itself through store() */
    /**
     * A 1.21.11 class implementing Fabric API's ModResourcePack (extending PackResources): its PackResources methods
     * (method_14406 getNamespaces, ...) must carry 26.1's names past the wall, reached through the vanilla interface
     * and through the Fabric API one. (The climbed layers' copy of this case, CbModResourcePacks$NoOverlay, kept its
     * tokens until the wall rename read Fabric API supertypes: JarRemapper.Table.targetLibraries.)
     */
    static final class TestModPack implements net.fabricmc.fabric.api.resource.ModResourcePack {
        @Override
        public net.fabricmc.loader.api.metadata.ModMetadata getFabricModMetadata() {
            return net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer("cb_swall").orElseThrow().getMetadata();
        }

        @Override
        public net.fabricmc.fabric.api.resource.ModResourcePack createOverlay(String overlay) {
            return this;
        }

        @Override
        public net.minecraft.class_7367<java.io.InputStream> method_14410(String... path) {
            return null;
        }

        @Override
        public net.minecraft.class_7367<java.io.InputStream> method_14405(net.minecraft.class_3264 type, net.minecraft.class_2960 id) {
            return null;
        }

        @Override
        public void method_14408(net.minecraft.class_3264 type, String ns, String prefix, net.minecraft.class_3262.class_7664 out) {
        }

        @Override
        public java.util.Set<String> method_14406(net.minecraft.class_3264 type) {
            return java.util.Set.of("cb_swall_pack");
        }

        @Override
        public <T> T method_14407(net.minecraft.class_7677<T> section) {
            return null;
        }

        @Override
        public net.minecraft.class_9224 method_56926() {
            return null;
        }

        @Override
        public void close() {
        }
    }

    private static void modResourcePack() {
        net.minecraft.class_3262 viaVanilla = new TestModPack();
        net.fabricmc.fabric.api.resource.ModResourcePack viaFabric = new TestModPack();
        java.util.Set<String> a = viaVanilla.method_14406(net.minecraft.class_3264.field_14190);
        java.util.Set<String> b = viaFabric.method_14406(net.minecraft.class_3264.field_14190);
        check("modResourcePack.namespaces", a.contains("cb_swall_pack") && b.contains("cb_swall_pack"), a + " / " + b);
        check("modResourcePack.overlay", viaFabric.createOverlay("x") == viaFabric, "");
    }

    static final class StoringQueue extends net.minecraft.class_236<MinecraftServer> {
        int stores;

        StoringQueue() {
            super(net.minecraft.class_233.field_1306);
        }

        @Override
        public net.minecraft.class_2499 method_982() {
            stores++;
            return super.method_982();
        }
    }

    /** 1.21.11 TimerQueue: built with its callbacks, written with store(), read back from saved events */
    private static void timerQueue() {
        net.minecraft.class_236<MinecraftServer> queue = new net.minecraft.class_236<>(net.minecraft.class_233.field_1306);
        queue.method_985("cb_later", 200L, new net.minecraft.class_231(net.minecraft.class_2960.method_60655("cb", "later")));
        queue.method_985("cb_sooner", 100L, new net.minecraft.class_231(net.minecraft.class_2960.method_60655("cb", "sooner")));
        net.minecraft.class_2499 stored = queue.method_982();
        net.minecraft.class_2487 first = stored.isEmpty() ? null : (net.minecraft.class_2487) stored.get(0);
        check("timerQueue.store", stored.size() == 2 && first != null && "cb_sooner".equals(first.method_68564("Name", ""))
            && first.method_68080("TriggerTime", 0L) == 100L && first.method_10545("Callback"), stored);
        net.minecraft.class_236<MinecraftServer> loaded = new net.minecraft.class_236<>(net.minecraft.class_233.field_1306,
            stored.stream().map(t -> new com.mojang.serialization.Dynamic<>(class_2509.field_11560, t)));
        check("timerQueue.load", loaded.method_22592().equals(java.util.Set.of("cb_later", "cb_sooner")), loaded.method_22592());
        StoringQueue sub = new StoringQueue();
        sub.method_985("cb_sub", 5L, new net.minecraft.class_231(net.minecraft.class_2960.method_60655("cb", "sub")));
        net.minecraft.class_2499 subStored = sub.method_982();
        check("timerQueue.subclassStore", subStored.size() == 1 && sub.stores == 1, subStored + " stores=" + sub.stores);
        // 26.1 writes the queue through pack(): the subclass's store() is what it writes (read reflectively, as the
        // name exists only on 26.1)
        Object packed = null;
        try {
            java.lang.reflect.Method pack = sub.getClass().getMethod("pack");
            packed = pack.invoke(sub);
        } catch (ReflectiveOperationException e) {
            packed = e;
        }
        check("timerQueue.packThroughStore", sub.stores == 2 && String.valueOf(packed).contains("cb_sub"), packed + " stores=" + sub.stores);
    }

    /** generic drift at the wall read on a running server */
    private static void generic(MinecraftServer server) {
        Optional<net.minecraft.class_5321<com.mojang.serialization.MapCodec<? extends net.minecraft.class_2794>>> key =
            server.method_30002().method_14178().method_12129().method_39301();
        check("generic.chunkGeneratorKey", key.isPresent() && key.get().method_29177().equals(net.minecraft.class_2960.method_60656("noise")), key);
        net.minecraft.class_2378<net.minecraft.class_6796> placed = server.method_30611().method_30530(net.minecraft.class_7924.field_41245);
        long features = placed.method_10220().flatMap(net.minecraft.class_6796::method_39643).map(f -> f.comp_332()).count();
        check("generic.placedFeatures", features > 100, features);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void onStarted(MinecraftServer server) {
        try {
            List<class_4140<?>> memories = List.of(class_4140.field_22334);
            // 1.21.11's saved brain: {"memories": {...}} (its codec requires the key, as 26.1's does)
            net.minecraft.class_2487 empty = new net.minecraft.class_2487();
            empty.method_10566("memories", new net.minecraft.class_2487());
            class_4095 brain = class_4095.method_28313(memories, List.of()).parse(class_2509.field_11560, empty).getOrThrow();
            brain.method_24525(class_4140.field_22334, Boolean.TRUE, 10L);
            Map<class_4140<?>, Optional<? extends class_4831<?>>> map = brain.method_35058();
            class_4831<?> mem = map.get(class_4140.field_22334).orElseThrow();
            check("brain.getMemories", mem.method_35127() == 10L && Boolean.TRUE.equals(mem.method_24637())
                    && map.containsKey(class_4140.field_22334), map);
            mem.method_24913();
            check("brain.memoryTicksSlot", brain.method_36978(class_4140.field_22334) == 9L, brain.method_36978(class_4140.field_22334));

            DataResult<class_2520> saved = brain.method_28310(class_2509.field_11560);
            class_2520 tag = saved.getOrThrow();
            class_4095 loaded = class_4095.method_28313(memories, List.of()).parse(class_2509.field_11560, tag).getOrThrow();
            check("brain.serializeRoundTrip", tag.toString().contains("memories") && loaded.method_18896(class_4140.field_22334)
                    && loaded.method_36978(class_4140.field_22334) == 9L, tag);

            class_4095 copy = brain.method_18911();
            check("brain.copyWithoutBehaviors", copy.method_18896(class_4140.field_22334)
                    && copy.method_36978(class_4140.field_22334) == 9L, copy);

            class_1928 rules = server.method_27728().method_146();
            check("worldData.getGameRules", rules == server.method_30002().method_64395(), rules);

            recipes(server);
            items();
            cauldrons();
            goats(server);
            misc();
            components(server);
            interactions(server);
            levelData(server);
            generic(server);
            componentValues(server);
            selectors();
            itemContainers(server);
            valueProviders(server);
            placements();
            removedFeatures(server);
            worldgenConfigs();
            driftPilots();
            removedRecipes(server);
            specialRecipeCtors(server);
            legacyRegionStorage(server);
            legacyStructureData(server);
            recordViews(server);
            worldLoading(server);
            worldgenStatics(server);
            interactPacket(server);
            timerQueue();
            extendFinal();
            worldCalls(server);
            legacyStates();
            trades(server);
            brains(server);
            furnaceBurn(server);
            emptyTintGetter();
            itemStackMembers(server);
            attackTypes(server);
            particleItems();
            books(server);
            itemMembers();
            serverStatics(server);
            componentMembers();
            displayIcons();
            blockStatics(server);
            legacyConstructors(server);
            timeCommand(server);
            swall12Batch();
            frogAttack(server);
            fancyLimb(server);
            lootMine(server);
            clocks(server);
            fluidHeights(server);
            check("server.throwIfFatalException", MinecraftServer.method_60584(), "");
            professions();
            levelSettings();
            modResourcePack();
        } catch (Throwable t) {
            t.printStackTrace();
            check("server.exception", false, t);
        }
        System.out.println("CBTEST SUMMARY " + passed + "/" + (passed + FAILS.size()) + " passed" + (FAILS.isEmpty() ? "" : " fails=" + FAILS));
    }

    /** a 1.21.11 listing: an emerald for one of the item, or nothing */
    static net.minecraft.class_3853.class_1652 sell(net.minecraft.class_1792 item) {
        return (level, trader, random) -> item == null ? null
            : new net.minecraft.class_1914(new net.minecraft.class_9306(net.minecraft.class_1802.field_8687), new net.minecraft.class_1799(item), 12, 2, 0.05F);
    }

    /** a 1.21.11 villager subclass: logs vanilla's calls of addOffersFromItemListings, reaches updateTrades and the method itself */
    static class TestVillager extends net.minecraft.class_1646 {
        final List<String> log = new ArrayList<>();

        TestVillager(net.minecraft.class_3218 level) {
            super(net.minecraft.class_1299.field_6077, level);
        }

        @Override
        protected void method_19170(net.minecraft.class_3218 level, net.minecraft.class_1916 offers, net.minecraft.class_3853.class_1652[] listings, int count) {
            log.add(listings.length + "x" + count);
            super.method_19170(level, offers, listings, count);
        }

        void update(net.minecraft.class_3218 level) {
            method_7237(level);
        }

        void addDirect(net.minecraft.class_3218 level, net.minecraft.class_3853.class_1652 listing) {
            method_19170(level, method_8264(), new net.minecraft.class_3853.class_1652[] {listing}, 1);
        }
    }

    /** a 1.21.11 wandering trader subclass logging vanilla's calls of addOffersFromItemListings */
    static class TestTrader extends net.minecraft.class_3989 {
        final List<String> log = new ArrayList<>();

        TestTrader(net.minecraft.class_3218 level) {
            super(net.minecraft.class_1299.field_17713, level);
        }

        @Override
        protected void method_19170(net.minecraft.class_3218 level, net.minecraft.class_1916 offers, net.minecraft.class_3853.class_1652[] listings, int count) {
            log.add(listings.length + "x" + count);
            super.method_19170(level, offers, listings, count);
        }
    }

    private static boolean sells(net.minecraft.class_1916 offers, net.minecraft.class_1792 item) {
        for (net.minecraft.class_1914 o : offers) {
            if (o.method_8250().method_31574(item)) {
                return true;
            }
        }
        return false;
    }

    /** 1.21.11's trade tables and AbstractVillager.addOffersFromItemListings over 26.1's data-driven trades */
    private static void trades(MinecraftServer server) {
        net.minecraft.class_3218 level = server.method_30002();
        it.unimi.dsi.fastutil.ints.Int2ObjectMap<net.minecraft.class_3853.class_1652[]> farmer = net.minecraft.class_3853.field_17067.get(net.minecraft.class_3852.field_17056);
        farmer.put(1, new net.minecraft.class_3853.class_1652[] {sell(net.minecraft.class_1802.field_8477), sell(net.minecraft.class_1802.field_8695), sell(net.minecraft.class_1802.field_8620)});
        farmer.put(2, new net.minecraft.class_3853.class_1652[] {sell(net.minecraft.class_1802.field_8600)});
        farmer.put(3, new net.minecraft.class_3853.class_1652[] {sell(null), sell(net.minecraft.class_1802.field_8279)});
        TestVillager v = new TestVillager(level);
        v.method_7195(v.method_7231().method_66688(server.method_30611(), net.minecraft.class_3852.field_17056).method_16920(1));
        net.minecraft.class_1916 offers = v.method_8264();
        java.util.Set<net.minecraft.class_1792> got = new java.util.HashSet<>();
        for (net.minecraft.class_1914 o : offers) {
            got.add(o.method_8250().method_7909());
        }
        check("trades.villagerLevel1", offers.size() == 2 && got.size() == 2
            && java.util.Set.of(net.minecraft.class_1802.field_8477, net.minecraft.class_1802.field_8695, net.minecraft.class_1802.field_8620).containsAll(got)
            && v.log.equals(List.of("3x2")), offers.size() + " " + got + " " + v.log);
        v.method_7195(v.method_7231().method_16920(2));
        v.update(level);
        check("trades.villagerLevel2", offers.size() == 3 && offers.get(2).method_8250().method_31574(net.minecraft.class_1802.field_8600)
            && v.log.equals(List.of("3x2", "1x2")), offers.size() + " " + v.log);
        v.method_7195(v.method_7231().method_16920(3));
        v.update(level);
        check("trades.nullListingNotCounted", offers.size() == 4 && offers.get(3).method_8250().method_31574(net.minecraft.class_1802.field_8279), offers.size());
        v.addDirect(level, sell(net.minecraft.class_1802.field_8137));
        check("trades.addOffersFromItemListings", offers.size() == 5 && offers.get(4).method_8250().method_31574(net.minecraft.class_1802.field_8137), offers.size());

        net.minecraft.class_3853.field_17724.add(org.apache.commons.lang3.tuple.Pair.of(new net.minecraft.class_3853.class_1652[] {sell(net.minecraft.class_1802.field_8137)}, 1));
        TestTrader t = new TestTrader(level);
        net.minecraft.class_1916 wares = t.method_8264();
        check("trades.wanderingPool", sells(wares, net.minecraft.class_1802.field_8137) && t.log.size() == net.minecraft.class_3853.field_17724.size()
            && t.log.get(t.log.size() - 1).equals("1x1"), wares.size() + " " + t.log);

        net.minecraft.class_3990 spawner = new net.minecraft.class_3990(server.method_27728().method_27859());
        net.minecraft.class_3990 derived = new net.minecraft.class_3990((net.minecraft.class_5268) server.method_3847(net.minecraft.class_1937.field_25180).method_8401()) {
        };
        check("trades.spawnerCtors", spawner != null && derived != null, "");
    }

    /** a 1.21.11 armor stand class declaring interactAt and interact */
    static class TestStand extends net.minecraft.class_1531 {
        String log = "";
        net.minecraft.class_1269 atAnswer = net.minecraft.class_1269.field_5811;

        TestStand(net.minecraft.class_1937 level) {
            super(net.minecraft.class_1299.field_6131, level);
        }

        @Override
        public net.minecraft.class_1269 method_5664(net.minecraft.class_1657 player, net.minecraft.class_243 location, net.minecraft.class_1268 hand) {
            log += "at;";
            return atAnswer;
        }

        @Override
        public net.minecraft.class_1269 method_5688(net.minecraft.class_1657 player, net.minecraft.class_1268 hand) {
            log += "interact;";
            return super.method_5688(player, hand);
        }
    }

    private static void interactions(MinecraftServer server) throws Exception {
        net.minecraft.class_3218 level = server.method_30002();
        net.minecraft.class_1657 player = net.fabricmc.fabric.api.entity.FakePlayer.get(level);
        net.minecraft.class_1268 hand = net.minecraft.class_1268.field_5808;
        net.minecraft.class_243 at = new net.minecraft.class_243(0.0, 1.0, 0.0);
        // 26.1's own interaction (what its server runs for the interact packet), reached by name at runtime
        java.lang.reflect.Method interactOn26 = net.minecraft.class_1657.class.getMethod("interactOn",
            net.minecraft.class_1297.class, net.minecraft.class_1268.class, net.minecraft.class_243.class);

        TestStand stand = new TestStand(level);
        interactOn26.invoke(player, stand, hand, at);
        check("interact.atThenInteract", "at;interact;".equals(stand.log), stand.log);

        stand.log = "";
        stand.atAnswer = net.minecraft.class_1269.field_5812;
        Object r = interactOn26.invoke(player, stand, hand, at);
        check("interact.atConsumes", "at;".equals(stand.log) && ((net.minecraft.class_1269) r).method_23665(), stand.log + " " + r);

        stand.log = "";
        stand.atAnswer = net.minecraft.class_1269.field_5811;
        player.method_7287(stand, hand);
        check("interact.oldInteractOnSecondStepOnly", "interact;".equals(stand.log), stand.log);

        // 1.21.11's ArmorStand.interactAt: a held helmet goes onto a plain armor stand
        player.method_6122(hand, new net.minecraft.class_1799(net.minecraft.class_1802.field_8743));
        net.minecraft.class_1531 plain = new net.minecraft.class_1531(net.minecraft.class_1299.field_6131, level);
        net.minecraft.class_1269 placed = plain.method_5664(player, at, hand);
        check("interact.armorStandInteractAt", placed.method_23665()
            && plain.method_6118(net.minecraft.class_1304.field_6169).method_31574(net.minecraft.class_1802.field_8743), placed);

        // a 1.21.11 armor stand class whose interactAt does not place: 1.21.11's interact was LivingEntity's (no placing)
        player.method_6122(hand, new net.minecraft.class_1799(net.minecraft.class_1802.field_8743));
        TestStand keeps = new TestStand(level);
        interactOn26.invoke(player, keeps, hand, at);
        check("interact.oldStandNoPlacing", "at;interact;".equals(keeps.log)
            && keeps.method_6118(net.minecraft.class_1304.field_6169).method_7960(), keeps.log);
    }

    private static void levelData(MinecraftServer server) {
        net.minecraft.class_5219 data = server.method_27728();
        net.minecraft.class_3218 end = server.method_3847(net.minecraft.class_1937.field_25181);
        net.minecraft.class_2881.class_8576 record = new net.minecraft.class_2881.class_8576(false, true, true, false,
            Optional.empty(), Optional.empty(), Optional.of(List.of(3, 1, 2)));
        net.minecraft.class_2881 fight = new net.minecraft.class_2881(end, 42L, record);
        net.minecraft.class_2881.class_8576 back = fight.method_12530();
        check("dragon.constructSave", !back.comp_1540() && back.comp_1541() && back.comp_1542() && !back.comp_1543()
            && back.comp_1546().orElseThrow().equals(List.of(3, 1, 2)), back);
        com.google.gson.JsonElement json = net.minecraft.class_2881.class_8576.field_44945.encodeStart(JsonOps.INSTANCE, record).getOrThrow();
        check("dragon.codec", json.toString().contains("\"DragonKilled\":true") && json.toString().contains("\"Gateways\":[3,1,2]"), json);
        net.minecraft.class_2881.class_8576 live = data.method_29036();
        check("dragon.worldDataLive", live.comp_1546().isPresent() && live.comp_1546().get().size() == 20, live);
        data.method_29037(record);
        check("dragon.worldDataSet", data.method_29036() == record, data.method_29036());

        net.minecraft.class_5268 primary = data.method_27859();
        net.minecraft.class_5268 nether = (net.minecraft.class_5268) server.method_3847(net.minecraft.class_1937.field_25180).method_8401();
        java.util.UUID id = java.util.UUID.randomUUID();
        primary.method_18040(id);
        nether.method_18040(java.util.UUID.randomUUID());
        check("trader.id", id.equals(primary.method_35506()) && nether.method_35506() == null, primary.method_35506() + " " + nether.method_35506());

        check("border.legacyEmpty", primary.method_27422().isEmpty(), primary.method_27422());
        primary.method_27415(Optional.of(net.minecraft.class_2784.class_5200.field_62538));
        check("border.legacySet", primary.method_27422().isPresent() && nether.method_27422().isPresent(), nether.method_27422());

        net.minecraft.class_2960 bossId = net.minecraft.class_2960.method_60656("cbtest");
        net.minecraft.class_3002 boss = server.method_3837().method_12970(bossId, net.minecraft.class_2561.method_43470("cb"));
        java.util.UUID offline = java.util.UUID.randomUUID();
        boss.method_12964(offline);
        check("bossEvents.createTextId", bossId.equals(boss.method_12959()), boss.method_12959());
        net.minecraft.class_2487 events = data.method_228();
        check("bossEvents.worldDataTag", events.method_10545("minecraft:cbtest"), events);
        net.minecraft.class_2487 saved = server.method_3837().method_12974(server.method_30611());
        check("bossEvents.saveOffline", saved.method_10545("minecraft:cbtest") && saved.toString().contains("Players"), saved);
        net.minecraft.class_2487 again = new net.minecraft.class_2487();
        again.method_10566("minecraft:cbtest2", saved.method_10580("minecraft:cbtest").method_10707());
        server.method_3837().method_12972(again, server.method_30611());
        net.minecraft.class_3002 loaded = server.method_3837().method_12971(net.minecraft.class_2960.method_60656("cbtest2"));
        check("bossEvents.load", loaded != null && loaded.method_12959().equals(net.minecraft.class_2960.method_60656("cbtest2")), loaded);
    }

    /** a 1.21.11 special recipe: CustomRecipe with its category, a CustomRecipe.Serializer */
    static class TestRecipe extends net.minecraft.class_1852 {
        TestRecipe(net.minecraft.class_7710 category) {
            super(category);
        }

        @Override
        public boolean method_8115(net.minecraft.class_9694 input, net.minecraft.class_1937 level) {
            return false;
        }

        @Override
        public net.minecraft.class_1799 method_8116(net.minecraft.class_9694 input, net.minecraft.class_7225.class_7874 registries) {
            return net.minecraft.class_1799.field_8037;
        }

        @Override
        public net.minecraft.class_1865<TestRecipe> method_8119() {
            return TEST_SERIALIZER;
        }
    }

    static final net.minecraft.class_1852.class_1866<TestRecipe> TEST_SERIALIZER = new net.minecraft.class_1852.class_1866<>(TestRecipe::new);

    /** a 1.21.11 shapeless recipe subclass overriding group(), which 26.1 made final */
    static class TestShapeless extends net.minecraft.class_1867 {
        TestShapeless() {
            super("g", net.minecraft.class_7710.field_40251, new net.minecraft.class_1799(net.minecraft.class_1802.field_20395),
                List.of(net.minecraft.class_1856.method_8091(net.minecraft.class_1802.field_8831)));
        }

        @Override
        public String method_8112() {
            return super.method_8112() + "!";
        }
    }

    private static void recipes(MinecraftServer server) {
        net.minecraft.class_3975 cutting = new net.minecraft.class_3975("grp", net.minecraft.class_1856.method_8091(net.minecraft.class_1802.field_20391),
            new net.minecraft.class_1799(net.minecraft.class_1802.field_20395));
        net.minecraft.class_1799 out = cutting.method_59998(new net.minecraft.class_9696(new net.minecraft.class_1799(net.minecraft.class_1802.field_20391)),
            server.method_30611());
        check("recipe.stonecutterGroupAssemble", "grp".equals(cutting.method_8112()) && cutting.method_49188()
            && out.method_7909() == net.minecraft.class_1802.field_20395, cutting.method_8112() + " " + out);
        TestShapeless shapeless = new TestShapeless();
        check("recipe.finalGroupOverride", "g!".equals(shapeless.method_8112()) && shapeless.method_45441() == net.minecraft.class_7710.field_40251,
            shapeless.method_8112());
    }

    private static void items() {
        net.minecraft.class_1769 red = net.minecraft.class_1769.method_7803(net.minecraft.class_1767.field_7964);
        check("dye.byColorAndColor", red == net.minecraft.class_1802.field_8264 && red.method_7802() == net.minecraft.class_1767.field_7964, red);
        net.minecraft.class_1826 pigEgg = net.minecraft.class_1826.method_8019(net.minecraft.class_1299.field_6093);
        net.minecraft.class_1799 eggStack = new net.minecraft.class_1799(net.minecraft.class_1802.field_8493);
        boolean listed = false;
        for (net.minecraft.class_1826 egg : net.minecraft.class_1826.method_8017()) {
            listed |= egg == pigEgg;
        }
        check("spawnEgg.byIdTypeEggs", pigEgg == net.minecraft.class_1802.field_8493 && listed
            && pigEgg.method_8015(eggStack) == net.minecraft.class_1299.field_6093 && pigEgg.method_8018(eggStack, net.minecraft.class_1299.field_6093), pigEgg);
    }

    private static void cauldrons() {
        net.minecraft.class_5620 stick = (state, level, pos, player, hand, stack) -> net.minecraft.class_1269.field_5812;
        java.util.Map<net.minecraft.class_1792, net.minecraft.class_5620> water = net.minecraft.class_5620.field_27776.comp_1982();
        water.put(net.minecraft.class_1802.field_8600, stick);
        check("cauldron.putGet", water.get(net.minecraft.class_1802.field_8600) == stick, water.get(net.minecraft.class_1802.field_8600));
        check("cauldron.vanillaEntries", net.minecraft.class_5620.field_27775.comp_1982().containsKey(net.minecraft.class_1802.field_8705)
            && water.containsKey(net.minecraft.class_1802.field_8539) && "water".equals(net.minecraft.class_5620.field_27776.comp_1981()), water.size());
    }

    /** a 1.21.11 goat class asking its brain provider (protected) */
    static class TestGoat extends net.minecraft.class_6053 {
        TestGoat(net.minecraft.class_1937 level) {
            super(net.minecraft.class_1299.field_30052, level);
        }

        Object provider() {
            return method_28306();
        }
    }

    private static void goats(MinecraftServer server) {
        TestGoat goat = new TestGoat(server.method_30002());
        goat.method_43542();
        boolean removed = !goat.method_43538() && !goat.method_43539();
        goat.method_43541();
        check("goat.hornsAndBrainProvider", removed && goat.method_43538() && goat.method_43539() && goat.provider() != null, removed);
    }

    private static void misc() {
        net.minecraft.class_1923 chunk = new net.minecraft.class_1923(new net.minecraft.class_2338(17, 0, 33));
        net.minecraft.class_1923 unpacked = new net.minecraft.class_1923(chunk.method_8324());
        check("chunkPos.ctors", chunk.field_9181 == 1 && chunk.field_9180 == 2 && unpacked.field_9181 == 1 && unpacked.field_9180 == 2, chunk);
        check("entityType.isTag", net.minecraft.class_1299.field_6137.method_20210(net.minecraft.class_3483.field_15507)
            && !net.minecraft.class_1299.field_6093.method_20210(net.minecraft.class_3483.field_15507), "");
        net.minecraft.class_1277 container = new net.minecraft.class_1277(1);
        int[] changes = {0};
        container.method_5489(c -> changes[0]++);
        container.method_5447(0, new net.minecraft.class_1799(net.minecraft.class_1802.field_8600));
        check("simpleContainer.listener", changes[0] == 1, changes[0]);
    }

    private static void components(MinecraftServer server) throws Exception {
        net.minecraft.class_2168 source = server.method_3739();
        net.minecraft.class_5250 resolved = net.minecraft.class_2561.method_43471("cb.key").method_10851().method_10890(source, null, 0);
        check("component.resolveTranslatable", "cb.key".equals(resolved.getString()), resolved.getString());
        net.minecraft.class_4616 storage = new net.minecraft.class_4616(net.minecraft.class_2960.method_60655("cbswall", "none"));
        net.minecraft.class_2574 good = new net.minecraft.class_2574("a.b", false, Optional.empty(), storage);
        net.minecraft.class_2574 bad = new net.minecraft.class_2574("[[[", false, Optional.empty(), storage);
        net.minecraft.class_5250 updated = net.minecraft.class_2564.method_10881(source, net.minecraft.class_2561.method_43471("cb.up"), null, 0);
        check("component.updateForEntity", "cb.up".equals(updated.getString()), updated.getString());
        check("statics.colors", net.minecraft.class_2480.method_10525(net.minecraft.class_1767.field_7964) == net.minecraft.class_2246.field_10068
            && net.minecraft.class_2480.method_10525(null) == net.minecraft.class_2246.field_10603
            && net.minecraft.class_2215.method_9398(net.minecraft.class_1767.field_7964) == net.minecraft.class_2246.field_10406, "");
        check("component.nbtContents", "a.b".equals(good.method_10920()) && "[[[".equals(bad.method_10920())
            && bad.method_10890(source, null, 0).getString().isEmpty(), good.method_10920());
    }

    /** 1.21.11 world calls 26.1 reshaped (CbWallWorldCalls); each check on its own so one failure keeps the rest */
    private static void worldCalls(MinecraftServer server) {
        net.minecraft.class_3218 level = server.method_30002();
        try {
            java.util.List<net.minecraft.class_1799> drops = net.minecraft.class_2248.method_9609(net.minecraft.class_2246.field_10340.method_9564(),
                level, new net.minecraft.class_2338(0, 100, 0), null, null, new net.minecraft.class_1799(net.minecraft.class_1802.field_8377));
            check("world.getDrops", drops.size() == 1 && drops.get(0).method_31574(net.minecraft.class_1802.field_20412), drops);
        } catch (Throwable t) {
            check("world.getDrops", false, t);
        }
        try {
            net.minecraft.class_2338 pos = new net.minecraft.class_2338(3, 250, 3);
            level.method_8501(pos.method_10074(), net.minecraft.class_2246.field_10114.method_9564());
            level.method_8501(pos, net.minecraft.class_2246.field_10382.method_9564());
            net.minecraft.class_2258.method_34267(level, pos, level.method_8320(pos), level.method_8320(pos.method_10074()));
            net.minecraft.class_2680 now = level.method_8320(pos);
            check("world.bubbleColumn", now.method_27852(net.minecraft.class_2246.field_10422), now);
        } catch (Throwable t) {
            check("world.bubbleColumn", false, t);
        }
        try {
            net.minecraft.class_12210<Float> lerp = net.minecraft.class_12210.method_75706();
            net.minecraft.class_12210<Float> step = net.minecraft.class_12210.method_75707(0.5F);
            net.minecraft.class_12192<Float> type = net.minecraft.class_12192.method_75634(com.mojang.serialization.Codec.FLOAT, java.util.Map.of(), lerp, step);
            check("world.attributeType", type.comp_5211() == lerp && type.comp_5131() == lerp && type.comp_5132() == step && type.comp_5128() == com.mojang.serialization.Codec.FLOAT, type);
        } catch (Throwable t) {
            check("world.attributeType", false, t);
        }
        try {
            net.minecraft.class_2761 on = new net.minecraft.class_2761(5L, 1234L, true);
            net.minecraft.class_2761 off = new net.minecraft.class_2761(6L, 99L, false);
            check("world.setTimePacket", on.comp_3219() == 5L && on.comp_3220() == 1234L && on.comp_3221()
                && off.comp_3220() == 99L && !off.comp_3221(), on);
        } catch (Throwable t) {
            check("world.setTimePacket", false, t);
        }
        try {
            net.minecraft.class_10215 resist = new net.minecraft.class_1799(net.minecraft.class_1802.field_22020).method_58694(net.minecraft.class_9334.field_54273);
            check("world.damageResistantTag", resist != null && resist.comp_3210() == net.minecraft.class_8103.field_42246, resist);
        } catch (Throwable t) {
            check("world.damageResistantTag", false, t);
        }
        try {
            @SuppressWarnings({"rawtypes", "unchecked"}) net.minecraft.class_7655.class_7657 banners = new net.minecraft.class_7655.class_7657(net.minecraft.class_7924.field_41252,
                net.minecraft.class_2582.field_49757, false);
            @SuppressWarnings("unchecked") net.minecraft.class_5455.class_6890 loaded = net.minecraft.class_7655.method_56515(server.method_34864(), java.util.List.of(), java.util.List.of(banners));
            long n = loaded.method_30530(net.minecraft.class_7924.field_41252).method_10220().count();
            check("world.registryLoadSync", n > 10, n);
        } catch (Throwable t) {
            check("world.registryLoadSync", false, t);
        }
    }

    /** a 1.21.11 BlockState subclass built through 1.21.11's StateHolder constructor, factory and codec (CbWallStates) */
    static final class LegacyState extends net.minecraft.class_2680 {
        LegacyState(net.minecraft.class_2248 block, it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap<net.minecraft.class_2769<?>, Comparable<?>> values,
                    com.mojang.serialization.MapCodec<net.minecraft.class_2680> codec) {
            super(block, values, codec);
        }

        static com.mojang.serialization.Codec<net.minecraft.class_2680> legacyCodec() {
            return method_28494(net.minecraft.class_7923.field_41175.method_39673(), net.minecraft.class_2248::method_9564);
        }

        com.mojang.serialization.MapCodec<net.minecraft.class_2680> codecField() {
            return field_24740;
        }
    }

    static final class LegacyFactoryClass implements net.minecraft.class_2689.class_2691<net.minecraft.class_2248, net.minecraft.class_2680> {
        int calls;

        @Override
        public net.minecraft.class_2680 create(net.minecraft.class_2248 block, it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap<net.minecraft.class_2769<?>, Comparable<?>> values,
                                               com.mojang.serialization.MapCodec<net.minecraft.class_2680> codec) {
            calls++;
            return new LegacyState(block, values, codec);
        }
    }

    /**
     * 1.21.11's <Mob>Ai.makeBrain statics (rt/CbWallAiBrains): with no mod mixin, the route over the mob's own brain
     * (on) and over a copy without behaviours (redo) leaves each of the 18 brains as 26.1 built it (off)
     */
    private static void brains(MinecraftServer server) {
        for (net.minecraft.CbSwallBrainProbe.Result r : net.minecraft.CbSwallBrainProbe.all(server.method_30002())) {
            boolean readable = !r.off().startsWith("unreadable") && !r.off().startsWith("behaviours []");
            check("brain.makeBrain." + r.mob() + ".on", readable && r.on().equals(r.off()), r.off() + " <> " + r.on());
            check("brain.makeBrain." + r.mob() + ".redo", readable && r.redo().equals(r.off()), r.off() + " <> " + r.redo());
            System.out.println("CBTEST BRAIN " + r.mob() + " " + r.off());
        }
    }

    /**
     * 1.21.11's AbstractFurnaceBlockEntity.burn, restored in the layer (mixin/CbWallFurnaceBurnMixin) with vanilla's
     * serverTick routed through it: a furnace driven by serverTick (class_2609.method_31651) still smelts, marks the
     * recipe used once per smelt, and turns a bucket in the fuel slot into a water bucket when a wet sponge dries
     */
    private static void furnaceBurn(MinecraftServer server) {
        net.minecraft.class_3218 level = server.method_30002();
        net.minecraft.class_2680 state = net.minecraft.class_2246.field_10181.method_9564();
        try {
            net.minecraft.class_2338 pos = new net.minecraft.class_2338(0, 100, 0);
            net.minecraft.class_3866 be = new net.minecraft.class_3866(pos, state);
            be.method_31662(level);
            be.method_5447(0, new net.minecraft.class_1799(net.minecraft.class_1802.field_33400, 2));
            be.method_5447(1, new net.minecraft.class_1799(net.minecraft.class_1802.field_8713, 1));
            for (int i = 0; i < 450; i++) {
                net.minecraft.class_2609.method_31651(level, pos, state, be);
            }
            net.minecraft.class_1799 out = be.method_5438(2);
            int used = be.method_27354(level, new net.minecraft.class_243(0, 100, 0)).size();
            check("furnace.burn.mixedRoute", BURN_CALLS.get() >= 2, BURN_CALLS.get());
            check("furnace.burn.smelt", out.method_7909() == net.minecraft.class_1802.field_8620 && out.method_7947() == 2
                && be.method_5438(0).method_7960() && used == 1, out + " input " + be.method_5438(0) + " recipes " + used);
        } catch (Throwable t) {
            check("furnace.burn.smelt", false, t);
        }
        try {
            net.minecraft.class_2338 pos = new net.minecraft.class_2338(2, 100, 0);
            net.minecraft.class_3866 be = new net.minecraft.class_3866(pos, state);
            be.method_31662(level);
            be.method_5447(0, new net.minecraft.class_1799(net.minecraft.class_1802.field_8554, 1));
            be.method_5447(1, new net.minecraft.class_1799(net.minecraft.class_1802.field_8713, 1));
            net.minecraft.class_2609.method_31651(level, pos, state, be);
            boolean lit = be.method_5438(1).method_7960();
            be.method_5447(1, new net.minecraft.class_1799(net.minecraft.class_1802.field_8550, 1));
            for (int i = 0; i < 250; i++) {
                net.minecraft.class_2609.method_31651(level, pos, state, be);
            }
            net.minecraft.class_1799 out = be.method_5438(2);
            check("furnace.burn.sponge", lit && out.method_7909() == net.minecraft.class_1802.field_8535
                && be.method_5438(1).method_7909() == net.minecraft.class_1802.field_8705 && be.method_5438(0).method_7960(),
                out + " fuel " + be.method_5438(1) + " input " + be.method_5438(0));
        } catch (Throwable t) {
            check("furnace.burn.sponge", false, t);
        }
    }

    /** 1.21.11's EmptyBlockAndTintGetter (facade/EmptyBlockAndTintGetter): its constant and every answer as 1.21.11 */
    private static void emptyTintGetter() {
        try {
            net.minecraft.class_9891 e = net.minecraft.class_9891.field_52611;
            net.minecraft.class_2338 pos = new net.minecraft.class_2338(1, 2, 3);
            net.minecraft.class_1920 g = e;
            check("emptyTint.answers", e.method_8320(pos).method_26215() && e.method_8321(pos) == null
                && e.method_8316(pos).method_15769() && e.method_31605() == 0 && e.method_31607() == 0
                && e.method_22336() == net.minecraft.class_3568.field_52658
                && g.method_24852(net.minecraft.class_2350.field_11036, true) == 1.0F
                && g.method_23752(pos, (biome, x, z) -> 7) == -1
                && net.minecraft.class_9891.values().length == 1 && net.minecraft.class_9891.valueOf("INSTANCE") == e
                && e.ordinal() == 0, e);
        } catch (Throwable t) {
            check("emptyTint.answers", false, t);
        }
    }

    /**
     * 1.21.11 ItemStack members 26.1 dropped: SINGLE_ITEM_CODEC / SIMPLE_ITEM_CODEC and getTags (rt/CbWallItemStacks),
     * the entity representation an item frame and an item entity set (CbWallItemStackRepresentationMixin and the
     * frame / item entity mixins)
     */
    private static void itemStackMembers(MinecraftServer server) {
        try {
            net.minecraft.class_1799 sword = new net.minecraft.class_1799(net.minecraft.class_1802.field_8802, 3);
            com.google.gson.JsonElement single = net.minecraft.class_1799.field_49747.encodeStart(JsonOps.INSTANCE, sword).getOrThrow();
            net.minecraft.class_1799 back = net.minecraft.class_1799.field_49747.parse(JsonOps.INSTANCE, single).getOrThrow();
            com.google.gson.JsonElement simple = net.minecraft.class_1799.field_49267.encodeStart(JsonOps.INSTANCE, sword).getOrThrow();
            net.minecraft.class_1799 simpleBack = net.minecraft.class_1799.field_49267.parse(JsonOps.INSTANCE, simple).getOrThrow();
            check("itemStack.codecs", single.isJsonObject() && single.getAsJsonObject().has("id") && !single.getAsJsonObject().has("count")
                && back.method_7909() == net.minecraft.class_1802.field_8802 && back.method_7947() == 1
                && simple.isJsonPrimitive() && simpleBack.method_7909() == net.minecraft.class_1802.field_8802 && simpleBack.method_7947() == 1,
                single + " / " + simple);
        } catch (Throwable t) {
            check("itemStack.codecs", false, t);
        }
        try {
            net.minecraft.class_1799 sword = new net.minecraft.class_1799(net.minecraft.class_1802.field_8802);
            check("itemStack.getTags", sword.method_40133().anyMatch(t -> t.comp_327().method_12832().equals("swords")),
                java.util.Arrays.toString(sword.method_40133().toArray()));
        } catch (Throwable t) {
            check("itemStack.getTags", false, t);
        }
        try {
            net.minecraft.class_3218 level = server.method_30002();
            net.minecraft.class_1533 frame = new net.minecraft.class_1533(level, new net.minecraft.class_2338(0, 100, 0), net.minecraft.class_2350.field_11043);
            net.minecraft.class_1799 map = new net.minecraft.class_1799(net.minecraft.class_1802.field_8895);
            boolean before = map.method_7961();
            frame.method_6935(map);
            net.minecraft.class_1799 held = frame.method_6940();
            net.minecraft.class_1542 dropped = new net.minecraft.class_1542(level, 0, 100, 0, new net.minecraft.class_1799(net.minecraft.class_1802.field_8802));
            check("itemStack.representation", !before && held.method_7961() && held.method_7945() == frame
                && held.method_27319() == frame && dropped.method_6983().method_27319() == dropped
                && !dropped.method_6983().method_7961() && net.minecraft.class_1799.field_8037.method_27319() == null,
                held.method_27319() + " / " + dropped.method_6983().method_27319());
        } catch (Throwable t) {
            check("itemStack.representation", false, t);
        }
    }

    /** 1.21.11 canAttackType (CbWall*AttackTypeMixin): LivingEntity / Mob / IronGolem / Breeze / Phantom answers */
    private static void attackTypes(MinecraftServer server) {
        try {
            net.minecraft.class_3218 level = server.method_30002();
            net.minecraft.class_3730 cmd = net.minecraft.class_3730.field_16462;
            net.minecraft.class_1308 zombie = (net.minecraft.class_1308) net.minecraft.class_1299.field_6051.method_5883(level, cmd);
            net.minecraft.class_1439 golem = (net.minecraft.class_1439) net.minecraft.class_1299.field_6147.method_5883(level, cmd);
            golem.method_6499(true);
            net.minecraft.class_1308 breeze = (net.minecraft.class_1308) net.minecraft.class_1299.field_47244.method_5883(level, cmd);
            net.minecraft.class_1308 phantom = (net.minecraft.class_1308) net.minecraft.class_1299.field_6078.method_5883(level, cmd);
            net.minecraft.class_1299<?> ghast = net.minecraft.class_1299.field_6107, player = net.minecraft.class_1299.field_6097,
                creeper = net.minecraft.class_1299.field_6046, z = net.minecraft.class_1299.field_6051;
            String got = "zombie " + zombie.method_5973(ghast) + zombie.method_5973(player)
                + " golem " + golem.method_5973(player) + golem.method_5973(creeper) + golem.method_5973(z) + golem.method_5973(ghast)
                + " breeze " + breeze.method_5973(player) + breeze.method_5973(z)
                + " phantom " + phantom.method_5973(ghast);
            check("attackType.answers", got.equals("zombie falsetrue golem falsefalsetruefalse breeze truefalse phantom true"), got);
        } catch (Throwable t) {
            check("attackType.answers", false, t);
        }
    }

    /** 1.21.11 ItemParticleOption(type, stack) / getItem() (rt/CbWallParticleItems, CbWallItemParticleStackMixin) */
    private static void particleItems() {
        try {
            net.minecraft.class_1799 stack = new net.minecraft.class_1799(net.minecraft.class_1802.field_8477, 3);
            net.minecraft.class_2392 option = new net.minecraft.class_2392(net.minecraft.class_2398.field_11218, stack);
            check("particleItem.sameStack", option.method_10289() == stack && option.method_10289() == stack, option.method_10289());
            boolean refused;
            try {
                new net.minecraft.class_2392(net.minecraft.class_2398.field_11218, net.minecraft.class_1799.field_8037);
                refused = false;
            } catch (IllegalArgumentException e) {
                refused = true;
            }
            check("particleItem.emptyRefused", refused, "accepted");
            net.minecraft.class_2392 sub = new FiveParticle(stack);
            net.minecraft.class_1799 subItem = sub.method_10289();
            check("particleItem.subclass", subItem.method_7947() == 5 && subItem.method_7909() == net.minecraft.class_1802.field_8477
                && ((FiveParticle) sub).base() == stack, subItem);
        } catch (Throwable t) {
            check("particleItem.sameStack", false, t);
        }
    }

    /** 1.21.11 WrittenBookContent tryCraftCopy / resolve / resolveForItem / MAX_CRAFTABLE_GENERATION (rt/CbWallBooks) */
    private static void books(MinecraftServer server) {
        try {
            List<net.minecraft.class_9262<net.minecraft.class_2561>> pages = List.of(
                net.minecraft.class_9262.method_57137(net.minecraft.class_2561.method_43470("page")));
            net.minecraft.class_9302 book = new net.minecraft.class_9302(net.minecraft.class_9262.method_57137("t"), "cb", 0, pages, false);
            net.minecraft.class_9302 copy = book.method_57519();
            net.minecraft.class_9302 copy2 = copy.method_57519();
            check("book.tryCraftCopy", copy.comp_2421() == 1 && copy2.comp_2421() == 2 && copy2.method_57519() == null
                && net.minecraft.class_9302.field_49380 == 2, copy2);
            net.minecraft.class_2168 source = server.method_3739();
            net.minecraft.class_9302 resolved = book.method_57521(source, null);
            check("book.resolve", resolved != null && resolved.comp_2423()
                && resolved.method_57525(false).get(0).getString().equals("page"), resolved);
            net.minecraft.class_1799 item = new net.minecraft.class_1799(net.minecraft.class_1802.field_8360);
            item.method_57379(net.minecraft.class_9334.field_49606, book);
            boolean changed = net.minecraft.class_9302.method_66337(item, source, null);
            check("book.resolveForItem", changed && item.method_58694(net.minecraft.class_9334.field_49606).comp_2423(), item);
        } catch (Throwable t) {
            check("book.resolve", false, t);
        }
    }

    /** a 1.21.11 ItemParticleOption subclass: super(type, stack), getItem() override calling super */
    private static final class FiveParticle extends net.minecraft.class_2392 {
        FiveParticle(net.minecraft.class_1799 stack) {
            super(net.minecraft.class_2398.field_11218, stack);
        }

        @Override
        public net.minecraft.class_1799 method_10289() {
            return super.method_10289().method_46651(5);
        }

        net.minecraft.class_1799 base() {
            return super.method_10289();
        }
    }

    /** 1.21.11 Allay / Piglin SENSOR_TYPES / MEMORY_TYPES read by a subclass (rt/brainlists, generated) */
    private abstract static class AllayLists extends net.minecraft.class_7298 {
        AllayLists() {
            super(null, null);
        }

        static List<?> sensors() {
            return field_38398;
        }

        static List<?> memories() {
            return field_38403;
        }
    }

    private abstract static class PiglinLists extends net.minecraft.class_4836 {
        PiglinLists() {
            super(null, null);
        }

        static List<?> sensors() {
            return field_22376;
        }

        static List<?> memories() {
            return field_22381;
        }
    }

    /** whether a live brain registers a memory (1.21.11 Brain.checkMemory(type, REGISTERED)) */
    private static boolean registered(net.minecraft.class_1309 mob, class_4140<?> memory) {
        return mob.method_18868().method_18876(memory, net.minecraft.class_4141.field_18458);
    }

    /**
     * rt/brainlists + CbWallBrainExtrasMixin: what 1.21.11 mods put in a mob's MEMORY_TYPES (PiglinMemoryMixin's
     * @ModifyArg in Piglin.<clinit>, onInitialize's write of Villager's private list) reaches the brains 26.1 builds,
     * added to 26.1's own (a hoglin's brain, unchanged by any mod, stays 26.1's: no added memory, its own ATTACK_TARGET; a piglin keeps
     * its own ADMIRING_ITEM / ANGRY_AT)
     */
    private static void brainListAppends(net.minecraft.class_3218 level) {
        net.minecraft.class_3730 cmd = net.minecraft.class_3730.field_16462;
        check("brainLists.piglinAppend", PiglinLists.memories().contains(class_4140.field_38394), PiglinLists.memories().size());
        net.minecraft.class_1309 piglin = (net.minecraft.class_1309) net.minecraft.class_1299.field_22281.method_5883(level, cmd);
        java.util.UUID liked = java.util.UUID.randomUUID();
        boolean piglinHas = registered(piglin, class_4140.field_38394);
        if (piglinHas) {
            piglin.method_18868().method_18878(class_4140.field_38394, liked);
        }
        check("brainLists.piglinLive", piglinHas && piglin.method_18868().method_18904(class_4140.field_38394).equals(Optional.of(liked))
            && registered(piglin, class_4140.field_22334) && registered(piglin, class_4140.field_22333), piglinHas);
        check("brainLists.villagerWrite", villagerWriteFailure == null
            && cb.swall.mixin.VillagerMemoryAccessor.cbswall$memories().size() == villagerMemoriesBefore + 1
            && cb.swall.mixin.VillagerMemoryAccessor.cbswall$memories().contains(class_4140.field_30246),
            villagerWriteFailure != null ? villagerWriteFailure : villagerMemoriesBefore);
        net.minecraft.class_1309 villager = (net.minecraft.class_1309) net.minecraft.class_1299.field_6077.method_5883(level, cmd);
        check("brainLists.villagerLive", registered(villager, class_4140.field_30246), "");
        net.minecraft.class_1309 hoglin = (net.minecraft.class_1309) net.minecraft.class_1299.field_21973.method_5883(level, cmd);
        check("brainLists.unchangedMob", !registered(hoglin, class_4140.field_38394) && !registered(hoglin, class_4140.field_30246)
            && registered(hoglin, class_4140.field_22355), "");
    }

    /** a 1.21.11 Zombie subclass calling handleAttributes(difficulty) (callReshapes: CONVERSION, no health reset) */
    private static final class AttrZombie extends net.minecraft.class_1642 {
        AttrZombie(net.minecraft.class_1937 level) {
            super(level);
        }

        void attributes(float difficulty) {
            method_7205(difficulty);
        }
    }

    /** rt/CbWallServerStatics, rt/brainlists, CbWallLivingEntityAttackRangeMixin, Zombie.handleAttributes */
    private static void serverStatics(MinecraftServer server) {
        try {
            int packed = net.minecraft.class_8104.method_68888(300, 7);
            check("brightness.pack", packed == (300 << 4 | 7 << 20) && net.minecraft.class_8104.method_68887(packed) == 300
                && net.minecraft.class_8104.method_68889(packed) == 7, packed);
            net.minecraft.class_3610 water = net.minecraft.class_3612.field_15910.method_15785();
            check("fluidState.holder", water.method_40180().method_40220(net.minecraft.class_3486.field_15517)
                && water.method_40181().anyMatch(t -> t == net.minecraft.class_3486.field_15517), water);
            check("piglinAi.isZombified", net.minecraft.class_4838.method_29534(net.minecraft.class_1299.field_6050)
                && net.minecraft.class_4838.method_29534(net.minecraft.class_1299.field_23696)
                && !net.minecraft.class_4838.method_29534(net.minecraft.class_1299.field_6051), "");
            net.minecraft.class_3218 level = server.method_30002();
            check("raids.type", net.minecraft.class_3767.method_67380(level.method_40134()) == net.minecraft.class_3767.field_56443
                && net.minecraft.class_3767.field_56444 == net.minecraft.class_3767.field_56443, "");
            check("brainLists.allay", AllayLists.sensors().size() == 4 && AllayLists.memories().size() == 12
                && PiglinLists.sensors().size() > 0, AllayLists.sensors());
            brainListAppends(level);
            net.minecraft.class_3730 cmd = net.minecraft.class_3730.field_16462;
            net.minecraft.class_7260 warden = (net.minecraft.class_7260) net.minecraft.class_1299.field_38095.method_5883(level, cmd);
            net.minecraft.class_7261.method_42228(warden);
            check("wardenAi.updateActivity", true, "");
            AttrZombie zombie = new AttrZombie(level);
            zombie.method_6033(5.0F);
            zombie.attributes(1.0F);
            check("zombie.handleAttributes", zombie.method_6032() == 5.0F, zombie.method_6032());
            net.minecraft.class_12392 range = zombie.method_76693();
            check("livingEntity.entityAttackRange", range != null && range.equals(net.minecraft.class_12392.method_76734(zombie)), range);
            net.minecraft.class_1542 itemEntity = new net.minecraft.class_1542(level, 0, 100, 0,
                new net.minecraft.class_1799(net.minecraft.class_1802.field_8477));
            net.minecraft.class_5328.method_33263(itemEntity, List.of(new net.minecraft.class_1799(net.minecraft.class_1802.field_8477)));
            check("itemUtils.onContainerDestroyed", true, "");
            net.minecraft.class_1657 player = net.fabricmc.fabric.api.entity.FakePlayer.get(level);
            check("player.isWithinAttackRange", player.method_76729(player.method_5829().method_1014(1.0), 0.0), player);
            net.minecraft.class_243 impact = new net.minecraft.class_243(1, 2, 3);
            player.field_49989 = impact;
            player.method_60984(true);
            check("player.setIgnoreFallDamage", player.field_49989 == impact, player.field_49989);
            player.method_60984(false);
            try {
                // isIgnoringFallDamageFromCurrentImpulse: 26.1 declares it on LivingEntity (cases_smixin3/swall.md, inherited-tombstone)
                player.method_60984(true);
                check("player.ignoreFallDamage", player.method_61165(), "false");
                player.method_60984(false);
            } catch (Throwable t) {
                check("player.ignoreFallDamage", false, t);
            }
        } catch (Throwable t) {
            check("serverStatics", false, t);
            t.printStackTrace();
        }
    }

    /** rt/CbWallComponentMembers: 1.21.11 component members over 26.1's */
    private static void componentMembers() {
        try {
            net.minecraft.class_1799 diamond = new net.minecraft.class_1799(net.minecraft.class_1802.field_8477, 2);
            net.minecraft.class_9288 contents = net.minecraft.class_9288.method_57493(
                List.of(net.minecraft.class_1799.field_8037, diamond));
            long all = contents.method_57489().count();
            long nonEmpty = contents.method_59712().count();
            int iter = 0;
            for (net.minecraft.class_1799 st : contents.method_59715()) {
                iter += st.method_7947();
            }
            check("containerContents.streams", all == 2 && nonEmpty == 1 && iter == 2, all + "/" + nonEmpty + "/" + iter);
            net.minecraft.class_10591 result = new net.minecraft.class_10591(net.minecraft.class_1802.field_8805);
            net.minecraft.class_1799 named = new net.minecraft.class_1799(net.minecraft.class_1802.field_8477);
            named.method_57379(net.minecraft.class_9334.field_49631, net.minecraft.class_2561.method_43470("n"));
            net.minecraft.class_1799 out = result.method_66340(named);
            check("transmute.apply", out.method_7909() == net.minecraft.class_1802.field_8805
                && out.method_58694(net.minecraft.class_9334.field_49631) != null && !result.method_67389(named)
                && result.method_67389(new net.minecraft.class_1799(net.minecraft.class_1802.field_8805))
                && result.method_66338() != null, out);
            net.minecraft.class_9278 charged = net.minecraft.class_9278.method_57439(diamond);
            check("chargedProjectiles.of", !charged.method_57442() && charged.method_57437().get(0).method_7947() == 2, charged);
            net.minecraft.class_9326 patch = net.minecraft.class_9326.method_57841()
                .method_57854(net.minecraft.class_9334.field_49631, net.minecraft.class_2561.method_43470("p")).method_57852();
            check("componentPatch.get", patch.method_57845(net.minecraft.class_9334.field_49631).isPresent()
                && patch.method_57845(net.minecraft.class_9334.field_54196) == null, patch);
            net.minecraft.class_10192 eq = new net.minecraft.class_1799(net.minecraft.class_1802.field_8805)
                .method_58694(net.minecraft.class_9334.field_54196);
            check("equippable.canBeEquippedBy", eq.method_64015(net.minecraft.class_1299.field_6051), eq);
            net.minecraft.class_2050 pred = new net.minecraft.class_2050(net.minecraft.class_6885.method_40246(
                net.minecraft.class_1299.field_6051.method_40124()));
            check("entityTypePredicate.matches", pred.method_8925(net.minecraft.class_1299.field_6051)
                && !pred.method_8925(net.minecraft.class_1299.field_6097), pred);
            check("difficulty.getKey", "easy".equals(net.minecraft.class_1267.field_5805.method_5460()), "");
            boolean disc = net.minecraft.class_9793.method_60753(null, new net.minecraft.class_1799(net.minecraft.class_1802.field_8144)).isPresent()
                && net.minecraft.class_9793.method_60753(null, diamond).isEmpty();
            check("jukeboxSong.fromStack", disc, "");
            net.minecraft.class_10710 horn = new net.minecraft.class_10710(net.minecraft.class_7445.field_39126);
            net.minecraft.class_10711 trim = new net.minecraft.class_10711(net.minecraft.class_8055.field_42009);
            check("component.keyCtors", horn.method_67211(null).get().method_40225(net.minecraft.class_7445.field_39126)
                && trim.method_67212(null).get().method_40225(net.minecraft.class_8055.field_42009), horn);
        } catch (Throwable t) {
            check("componentMembers", false, t);
            t.printStackTrace();
        }
    }

    /** a 1.21.11 DisplayInfo subclass: super(icon, ...), getIcon() override calling super */
    private static final class TwoIcon extends net.minecraft.class_185 {
        TwoIcon(net.minecraft.class_1799 icon) {
            super(icon, net.minecraft.class_2561.method_43470("t"), net.minecraft.class_2561.method_43470("d"), Optional.empty(),
                net.minecraft.class_189.field_1254, true, true, false);
        }

        @Override
        public net.minecraft.class_1799 method_821() {
            return super.method_821().method_46651(2);
        }

        net.minecraft.class_1799 base() {
            return super.method_821();
        }
    }

    /** 1.21.11 DisplayInfo(icon, ...) / getIcon() / Advancement.Builder.display(icon, ...) (rt/CbWallDisplayIcons) */
    private static void displayIcons() {
        try {
            net.minecraft.class_1799 icon = new net.minecraft.class_1799(net.minecraft.class_1802.field_8477);
            net.minecraft.class_185 info = new net.minecraft.class_185(icon, net.minecraft.class_2561.method_43470("t"),
                net.minecraft.class_2561.method_43470("d"), Optional.empty(), net.minecraft.class_189.field_1254, true, true, false);
            check("displayInfo.icon", info.method_821() == icon && info.method_821() == icon, info.method_821());
            TwoIcon sub = new TwoIcon(icon);
            net.minecraft.class_1799 subIcon = sub.method_821();
            check("displayInfo.subclass", subIcon.method_7947() == 2 && sub.base() == icon, subIcon);
            net.minecraft.class_161.class_162 builder = net.minecraft.class_161.class_162.method_707().method_20416(icon,
                net.minecraft.class_2561.method_43470("t"), net.minecraft.class_2561.method_43470("d"), null,
                net.minecraft.class_189.field_1254, true, true, false);
            check("advancementBuilder.display", builder != null, "");
        } catch (Throwable t) {
            check("displayInfo.icon", false, t);
            t.printStackTrace();
        }
    }

    /** a 1.21.11 BigDripleafBlock subclass reaching the protected static canPlaceAt(level, pos, state) */
    private abstract static class DripleafAccess extends net.minecraft.class_5801 {
        DripleafAccess() {
            super(null);
        }

        static boolean canPlace(net.minecraft.class_5539 level, net.minecraft.class_2338 pos, net.minecraft.class_2680 state) {
            return method_33725(level, pos, state);
        }
    }

    /** CbWallServerStatics block/menu statics, CbWallEnchantCodecs */
    private static void blockStatics(MinecraftServer server) {
        try {
            net.minecraft.class_3218 level = server.method_30002();
            java.util.Set<net.minecraft.class_1735> slots = new java.util.HashSet<>();
            slots.add(null);
            slots.add(new net.minecraft.class_1735(null, 0, 0, 0));
            net.minecraft.class_1799 carried = new net.minecraft.class_1799(net.minecraft.class_1802.field_8477, 9);
            check("menu.quickCraftPlaceCount", net.minecraft.class_1703.method_7617(slots, 0, carried) == 4
                && net.minecraft.class_1703.method_7617(slots, 1, carried) == 1
                && net.minecraft.class_1703.method_7617(slots, 2, carried) == 64, "");
            net.minecraft.class_2338 pos = new net.minecraft.class_2338(0, 64, 0);
            check("bigDripleaf.canPlaceAt", DripleafAccess.canPlace(level, pos, net.minecraft.class_2246.field_10124.method_9564())
                && !DripleafAccess.canPlace(level, pos, net.minecraft.class_2246.field_10340.method_9564())
                && !DripleafAccess.canPlace(level, new net.minecraft.class_2338(0, 4000, 0), net.minecraft.class_2246.field_10124.method_9564()), "");
            net.minecraft.class_2258.method_34268(level, new net.minecraft.class_2338(0, 300, 0), net.minecraft.class_2246.field_10124.method_9564());
            check("bubbleColumn.updateColumn", true, "");
            com.mojang.serialization.Codec<net.minecraft.class_5341> cond = net.minecraft.class_9698.method_60007(net.minecraft.class_173.field_51802);
            com.google.gson.JsonObject ok = com.google.gson.JsonParser.parseString("{\"condition\":\"minecraft:random_chance\",\"chance\":0.5}").getAsJsonObject();
            check("enchantCodec.condition", cond.parse(JsonOps.INSTANCE, ok).result().isPresent(), cond.parse(JsonOps.INSTANCE, ok));
            check("enchantCodec.effects", net.minecraft.class_9698.method_60004(Codec.INT, net.minecraft.class_173.field_51802) != null
                && net.minecraft.class_9710.method_60203(Codec.INT, net.minecraft.class_173.field_51802) != null
                && net.minecraft.class_9710.method_60208(Codec.INT, net.minecraft.class_173.field_51802) != null, "");
        } catch (Throwable t) {
            check("blockStatics", false, t);
            t.printStackTrace();
        }
    }

    private static net.minecraft.class_4970.class_2251 testProps(String name) {
        return net.minecraft.class_4970.class_2251.method_9637().method_63500(net.minecraft.class_5321.method_29179(
            net.minecraft.class_7924.field_41254, net.minecraft.class_2960.method_60655("cbswall", name)));
    }

    private static LegacyStem legacyStem;
    private static LegacyRoots legacyRoots;
    private static Throwable legacyBlocksFailure;

    /** a 1.21.11 StemBlock subclass: super(fruit, attachedStem, seed, properties) */
    private static final class LegacyStem extends net.minecraft.class_2513 {
        LegacyStem() {
            super(net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41254, net.minecraft.class_2960.method_60655("minecraft", "pumpkin")),
                net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41254, net.minecraft.class_2960.method_60655("minecraft", "attached_pumpkin_stem")),
                net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41197, net.minecraft.class_2960.method_60655("minecraft", "pumpkin_seeds")),
                testProps("legacy_stem"));
        }

        boolean placeOn(net.minecraft.class_2248 block) {
            return method_9695(block.method_9564(), null, net.minecraft.class_2338.field_10980);
        }
    }

    /** a 1.21.11 RootsBlock subclass: super(properties) */
    private static final class LegacyRoots extends net.minecraft.class_4774 {
        LegacyRoots() {
            super(testProps("legacy_roots"));
        }

        boolean placeOn(net.minecraft.class_2248 block) {
            return method_9695(block.method_9564(), null, net.minecraft.class_2338.field_10980);
        }
    }

    /** super-ctor reshapes and factories of 1.21.11 constructors 26.1 reshaped (CbWallBlockSupports, CbWallGiveItems, ...) */
    private static void legacyConstructors(MinecraftServer server) {
        try {
            if (legacyBlocksFailure != null) {
                throw legacyBlocksFailure;
            }
            LegacyStem stem = legacyStem;
            check("stemBlock.support", stem.placeOn(net.minecraft.class_2246.field_10362) && !stem.placeOn(net.minecraft.class_2246.field_10219), "");
            LegacyRoots roots = legacyRoots;
            check("rootsBlock.support", roots.placeOn(net.minecraft.class_2246.field_22120) && roots.placeOn(net.minecraft.class_2246.field_10219)
                && !roots.placeOn(net.minecraft.class_2246.field_10102), "");
            Object give = new net.minecraft.class_7296<>(e -> Optional.empty(), 1.0F, 10);
            Object spear = new net.minecraft.class_12359(1.0, 1.0, 4.0F, 9.0F);
            net.minecraft.class_1642 zombie = new net.minecraft.class_1642(server.method_30002());
            Object follow = new net.minecraft.class_1346(zombie);
            check("aiConstructors", give != null && spear != null && follow != null, "");
        } catch (Throwable t) {
            check("legacyConstructors", false, t);
            t.printStackTrace();
        }
    }

    /** 1.21.11 TimeCommand.setTime / addTime (CbWallServerStatics: the overworld clock) */
    private static void timeCommand(MinecraftServer server) {
        try {
            net.minecraft.class_2168 source = server.method_3739();
            long before = server.method_30002().method_8532();
            int set = net.minecraft.class_3149.method_13784(source, 1000);
            long afterSet = server.method_30002().method_8532();
            int added = net.minecraft.class_3149.method_13788(source, 500);
            long afterAdd = server.method_30002().method_8532();
            net.minecraft.class_3149.method_13784(source, (int) (before % 24000L));
            check("timeCommand.setAdd", set == 1000 && afterSet == 1000L && added == 1500 && afterAdd == 1500L,
                set + "/" + afterSet + "/" + added + "/" + afterAdd);
        } catch (Throwable t) {
            check("timeCommand.setAdd", false, t);
            t.printStackTrace();
        }
    }

    /** 1.21.11 Item.getCraftingRemainder() / getName() (rt/CbWallItemMembers) */
    private static void itemMembers() {
        try {
            net.minecraft.class_1799 rem = net.minecraft.class_1802.field_8103.method_7858();
            net.minecraft.class_1799 none = net.minecraft.class_1802.field_8477.method_7858();
            check("item.craftingRemainder", rem.method_7909() == net.minecraft.class_1802.field_8550 && none.method_7960(), rem);
            String name = net.minecraft.class_1802.field_8477.method_63680().getString();
            check("item.name", name.equals("Diamond"), name);
        } catch (Throwable t) {
            check("item.craftingRemainder", false, t);
        }
    }

    private static void legacyStates() {
        try {
            net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> def = new net.minecraft.class_2689.class_2690<net.minecraft.class_2248, net.minecraft.class_2680>(
                net.minecraft.class_2246.field_10524).method_11667(net.minecraft.class_2453.field_11413).method_11668(net.minecraft.class_2248::method_9564, LegacyState::new);
            java.util.List<net.minecraft.class_2680> states = def.method_11662();
            net.minecraft.class_2680 lit = states.stream().filter(st -> st.method_11654(net.minecraft.class_2453.field_11413)).findFirst().orElse(null);
            check("states.lambdaFactory", states.size() == 2 && states.stream().allMatch(st -> st instanceof LegacyState) && lit != null
                && lit.method_11656().size() == 1 && Boolean.TRUE.equals(lit.method_11656().get(net.minecraft.class_2453.field_11413)), states);
        } catch (Throwable t) {
            check("states.lambdaFactory", false, t);
        }
        try {
            LegacyFactoryClass factory = new LegacyFactoryClass();
            net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> def = new net.minecraft.class_2689.class_2690<net.minecraft.class_2248, net.minecraft.class_2680>(
                net.minecraft.class_2246.field_10524).method_11667(net.minecraft.class_2453.field_11413).method_11668(net.minecraft.class_2248::method_9564, factory);
            check("states.factoryClass", factory.calls == 2 && def.method_11662().stream().allMatch(st -> st instanceof LegacyState), factory.calls);
        } catch (Throwable t) {
            check("states.factoryClass", false, t);
        }
        try {
            net.minecraft.class_2680 lamp = net.minecraft.class_2246.field_10524.method_9564().method_11657(net.minecraft.class_2453.field_11413, true);
            com.mojang.serialization.Codec<net.minecraft.class_2680> codec = LegacyState.legacyCodec();
            com.google.gson.JsonElement json = codec.encodeStart(JsonOps.INSTANCE, lamp).getOrThrow();
            net.minecraft.class_2680 back = codec.parse(JsonOps.INSTANCE, json).getOrThrow();
            LegacyState own = new LegacyState(net.minecraft.class_2246.field_10524,
                new it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap<>(new net.minecraft.class_2769<?>[] {net.minecraft.class_2453.field_11413}, new Comparable<?>[] {Boolean.TRUE}), null);
            check("states.codecAndField", json.toString().contains("\"lit\":\"true\"") && back == lamp && own.codecField() != null
                && own.method_11654(net.minecraft.class_2453.field_11413), json);
        } catch (Throwable t) {
            check("states.codecAndField", false, t);
        }
    }

    /** 1.21.11 BlockableEventLoop(name) subclass: super(name) gets propagatesCrashes = false (rt/CbWallEventLoops) */
    static class LegacyLoop extends net.minecraft.class_1255<Runnable> {
        private final Thread other = new Thread(() -> { });

        LegacyLoop() {
            super("cbtest-loop");
        }

        @Override
        public Runnable method_16211(Runnable r) {
            return r;
        }

        @Override
        protected boolean method_18856(Runnable r) {
            return true;
        }

        @Override
        protected Thread method_3777() {
            return this.other;
        }
    }

    /** swall12 batch: event loop super(name), BuiltInMetadata, data/recipes builders */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void swall12Batch() {
        try {
            LegacyLoop loop = new LegacyLoop();
            java.util.concurrent.atomic.AtomicBoolean ran = new java.util.concurrent.atomic.AtomicBoolean();
            loop.execute(() -> ran.set(true));
            loop.method_18857(ran::get);
            check("swall12.eventLoopSuperName", ran.get() && "cbtest-loop".equals(loop.method_16898()), loop.method_16898());
        } catch (Throwable t) {
            check("swall12.eventLoopSuperName", false, t);
        }
        try {
            net.minecraft.class_7677 type = net.minecraft.class_3272.field_61155;
            net.minecraft.class_7662 empty = net.minecraft.class_7662.method_45172();
            net.minecraft.class_7662 one = net.minecraft.class_7662.method_45174(type, "cb-value");
            check("swall12.builtInMetadata", empty.method_45173(type) == null && "cb-value".equals(one.method_45173(type)),
                one.method_45173(type));
        } catch (Throwable t) {
            check("swall12.builtInMetadata", false, t);
        }
        try {
            net.minecraft.class_7800 category = net.minecraft.class_7800.values()[0];
            net.minecraft.class_1856 stone = net.minecraft.class_1856.method_8091(net.minecraft.class_1802.field_20391);
            net.minecraft.class_3981 cutting = net.minecraft.class_3981.method_17968(stone, category, net.minecraft.class_1802.field_20395);
            net.minecraft.class_2454 smelting = net.minecraft.class_2454.method_17802(stone, category, net.minecraft.class_1802.field_20395, 0.1F, 200);
            net.minecraft.class_5797 asInterface = smelting;
            net.minecraft.class_2960 id = net.minecraft.class_5797.method_36442(net.minecraft.class_1802.field_20391);
            check("swall12.recipeBuilders", cutting.method_36441() == net.minecraft.class_1802.field_20395
                && smelting.method_36441() == net.minecraft.class_1802.field_20395 && asInterface.method_36441() == net.minecraft.class_1802.field_20395
                && "minecraft:stone".equals(id.toString()), id);
            net.minecraft.class_2456 special = new net.minecraft.class_2456(cat -> null);
            check("swall12.specialRecipeBuilder", special != null, "");
            // BundleWeightMixin (a 1.21.11 mixin on getWeight): stone weighs 1/2, so a bundle takes 2 of 64
            net.minecraft.class_9276.class_9277 bundle = new net.minecraft.class_9276.class_9277(net.minecraft.class_9276.field_49289);
            int took = bundle.method_57432(new net.minecraft.class_1799(net.minecraft.class_1802.field_20391, 64));
            int dirt = new net.minecraft.class_9276.class_9277(net.minecraft.class_9276.field_49289)
                .method_57432(new net.minecraft.class_1799(net.minecraft.class_1802.field_20395, 64));
            check("swall12b.bundleWeightMixin", took == 2 && dirt > 2, took + " " + dirt);
        } catch (Throwable t) {
            check("swall12.recipeBuilders", false, t);
        }
        try {
            // ItemStackCodecLambdaMixin: encoding a stack runs the codec's components getter lambda
            int before = CODEC_PATCH_CALLS.get();
            net.minecraft.class_1799 named = new net.minecraft.class_1799(net.minecraft.class_1802.field_20391, 3);
            com.google.gson.JsonElement json = net.minecraft.class_1799.field_24671.encodeStart(JsonOps.INSTANCE, named).getOrThrow();
            check("swall12b.codecLambdaMixin", CODEC_PATCH_CALLS.get() > before && json.toString().contains("stone"),
                (CODEC_PATCH_CALLS.get() - before) + " " + json);
        } catch (Throwable t) {
            check("swall12b.codecLambdaMixin", false, t);
        }
    }

    /**
     * FrogAttackMixin (a 1.21.11 mixin on the private FrogAi.canAttack(frog)): a frog's brain ticking with no attack target
     * runs its StartAttacking behaviour, whose gate is canAttack, so the mixin counts
     */
    private static void frogAttack(MinecraftServer server) {
        try {
            net.minecraft.class_3218 level = server.method_30002();
            net.minecraft.class_7102 frog = net.minecraft.class_1299.field_37419.method_5883(level, net.minecraft.class_3730.field_16471);
            frog.method_5814(0.5, 100, 0.5);
            int before = FROG_ATTACK_CALLS.get();
            for (int i = 0; i < 5; i++) {
                frog.method_18868().method_19542(level, frog);
            }
            check("swall12b.frogAttackMixin", FROG_ATTACK_CALLS.get() > before, FROG_ATTACK_CALLS.get() - before);
        } catch (Throwable t) {
            check("swall12b.frogAttackMixin", false, t);
        }
    }

    /**
     * FancyLimbMixin (a 1.21.11 mixin on the private FancyTrunkPlacer.makeLimb(reader, ..)): a fancy oak placed on grass
     * checks and lays its limbs through makeLimb, so the mixin counts
     */
    private static void fancyLimb(MinecraftServer server) {
        try {
            net.minecraft.class_3218 level = server.method_30002();
            net.minecraft.class_2338 ground = new net.minecraft.class_2338(72, 200, 72);
            level.method_8501(ground, net.minecraft.class_2246.field_10219.method_9564());
            net.minecraft.class_2975<?, ?> fancy = server.method_30611().method_30530(net.minecraft.class_7924.field_41239)
                .method_63535(net.minecraft.class_2960.method_60656("fancy_oak"));
            int before = FANCY_LIMB_CALLS.get();
            boolean placed = fancy.method_12862(level, level.method_14178().method_12129(), net.minecraft.class_5819.method_43049(5L), ground.method_10084());
            check("swall12b.fancyLimbMixin", placed && FANCY_LIMB_CALLS.get() > before, placed + " " + (FANCY_LIMB_CALLS.get() - before));
        } catch (Throwable t) {
            check("swall12b.fancyLimbMixin", false, t);
        }
    }

    /** LootMineMixin (a 1.21.11 mixin on the private LootCommand.dropBlockLoot(.., ItemStack, ..)): "/loot spawn .. mine" runs it */
    private static void lootMine(MinecraftServer server) {
        try {
            server.method_30002().method_8501(new net.minecraft.class_2338(88, 200, 88), net.minecraft.class_2246.field_10340.method_9564());
            int before = LOOT_MINE_CALLS.get();
            server.method_3734().method_44252(server.method_3739().method_9217(), "loot spawn 88 202 88 mine 88 200 88");
            check("swall12b.lootMineMixin", LOOT_MINE_CALLS.get() > before, LOOT_MINE_CALLS.get() - before);
        } catch (Throwable t) {
            check("swall12b.lootMineMixin", false, t);
        }
    }
}
