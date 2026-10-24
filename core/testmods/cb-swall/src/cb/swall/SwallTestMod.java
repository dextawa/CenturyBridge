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
    private static int passed;

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
        EARLY_ATTACKS = new net.minecraft.class_10707(0.25F, 1.0F, List.of(), net.minecraft.class_10707.class_10709.field_56308,
            Optional.of(net.minecraft.class_8103.field_56242), Optional.empty(), Optional.empty());
        ServerLifecycleEvents.SERVER_STARTED.register(SwallTestMod::onStarted);
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
            worldgenStatics(server);
            interactPacket(server);
            timerQueue();
            extendFinal();
        } catch (Throwable t) {
            t.printStackTrace();
            check("server.exception", false, t);
        }
        System.out.println("CBTEST SUMMARY " + passed + "/" + (passed + FAILS.size()) + " passed" + (FAILS.isEmpty() ? "" : " fails=" + FAILS));
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
}
