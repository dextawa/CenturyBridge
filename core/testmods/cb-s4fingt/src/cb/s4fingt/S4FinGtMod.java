package cb.s4fingt;

import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.class_1657;
import net.minecraft.class_2338;
import net.minecraft.class_2370;
import net.minecraft.class_2378;
import net.minecraft.class_2470;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_4514;
import net.minecraft.class_4516;
import net.minecraft.class_4517;
import net.minecraft.class_4518;
import net.minecraft.class_4519;
import net.minecraft.class_4520;
import net.minecraft.class_4521;
import net.minecraft.class_4525;
import net.minecraft.class_4529;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.server.MinecraftServer;

/**
 * A 1.20.4-era server mod (intermediary names) for the s4fin game test and registry bridges of step 1.20.4 -> 1.20.5.
 * Prints "CBS4GT OK|FAIL ..." per check and "CBS4GT DONE n/m" at the end:
 * <ul>
 * <li>MappedRegistry subclasses overriding register only, registerMapping only, and both: what each override sees
 * (and in which order) for a direct register(key, value, lifecycle), for vanilla's Registry.register (1.20.5's
 * register(key, value, info) path) and for a direct registerMapping;</li>
 * <li>TestFunction's 10-argument constructor, groupTestsIntoBatches, a mod-built GameTestBatch (its functions and
 * before/after functions), GameTestInfo's rerun flag, GameTestHelper's mock players, the registry's last failed
 * tests, new GameTestRunner();</li>
 * <li>a whole run through GameTestRunner.runTestBatches with a mod batch subclass and a mod listener: the test
 * passes, the batch's before and after functions run, the listener hears the structure loaded and the pass, the
 * structure block is where getStructureBlockPos says and findStructureBlocks finds it, clearAllTests clears it.</li>
 * </ul>
 */
public class S4FinGtMod implements ModInitializer {

    static final List<String> FAILS = new ArrayList<>();
    static int checks;

    static void check(boolean ok, String what) {
        checks++;
        if (!ok) {
            FAILS.add(what);
        }
        System.out.println("CBS4GT " + (ok ? "OK " : "FAIL ") + what);
    }

    static void guard(String what, Runnable r) {
        try {
            r.run();
        } catch (Throwable t) {
            t.printStackTrace();
            check(false, what + " threw " + t);
        }
    }

    // ---- registries ----

    static final List<String> LOG = new ArrayList<>();

    static class Both extends class_2370<String> {
        Both(class_5321<? extends class_2378<String>> key) {
            super(key, Lifecycle.stable());
        }

        @Override
        public class_6880.class_6883<String> method_10272(class_5321<String> key, String value, Lifecycle lifecycle) {
            LOG.add("R:" + key.method_29177().method_12832());
            return super.method_10272(key, value, lifecycle);
        }

        @Override
        public class_6880.class_6883<String> method_46744(int id, class_5321<String> key, String value, Lifecycle lifecycle) {
            LOG.add("M" + id + ":" + key.method_29177().method_12832());
            return super.method_46744(id, key, value, lifecycle);
        }
    }

    static class RegisterOnly extends class_2370<String> {
        RegisterOnly(class_5321<? extends class_2378<String>> key) {
            super(key, Lifecycle.stable());
        }

        @Override
        public class_6880.class_6883<String> method_10272(class_5321<String> key, String value, Lifecycle lifecycle) {
            LOG.add("R:" + key.method_29177().method_12832());
            return super.method_10272(key, value, lifecycle);
        }
    }

    static class MappingOnly extends class_2370<String> {
        MappingOnly(class_5321<? extends class_2378<String>> key) {
            super(key, Lifecycle.stable());
        }

        @Override
        public class_6880.class_6883<String> method_46744(int id, class_5321<String> key, String value, Lifecycle lifecycle) {
            LOG.add("M" + id + ":" + key.method_29177().method_12832());
            return super.method_46744(id, key, value, lifecycle);
        }
    }

    static <R extends class_2370<String>> void registries(String name, R registry, String direct, String vanilla, String mapping) {
        class_5321<class_2378<String>> rk = (class_5321<class_2378<String>>) (Object) registry.method_30517();
        LOG.clear();
        registry.method_10272(class_5321.method_29179(rk, new class_2960("cb", "a")), "va", Lifecycle.stable());
        check(String.join(",", LOG).equals(direct), name + ": direct register sees [" + String.join(",", LOG) + "], expected [" + direct + "]");
        LOG.clear();
        class_2378.method_10230(registry, new class_2960("cb", "b"), "vb");
        check(String.join(",", LOG).equals(vanilla), name + ": Registry.register sees [" + String.join(",", LOG) + "], expected [" + vanilla + "]");
        LOG.clear();
        registry.method_46744(2, class_5321.method_29179(rk, new class_2960("cb", "c")), "vc", Lifecycle.stable());
        check(String.join(",", LOG).equals(mapping), name + ": direct registerMapping sees [" + String.join(",", LOG) + "], expected [" + mapping + "]");
        check("va".equals(registry.method_10223(new class_2960("cb", "a"))) && "vb".equals(registry.method_10223(new class_2960("cb", "b")))
            && "vc".equals(registry.method_10223(new class_2960("cb", "c"))), name + ": all three registered");
        check(registry.method_10206("va") == 0 && registry.method_10206("vb") == 1 && registry.method_10206("vc") == 2,
            name + ": ids 0, 1, 2 (" + registry.method_10206("va") + "," + registry.method_10206("vb") + "," + registry.method_10206("vc") + ")");
    }

    // ---- DFU 6 API (1.20.4's library) ----

    static void dfu() {
        com.mojang.serialization.DataResult<Integer> ok = com.mojang.serialization.DataResult.success(7);
        com.mojang.serialization.DataResult<Integer> bad = com.mojang.serialization.DataResult.error(() -> "nope", 3);
        check(ok.result().get() == 7 && ok.get().left().get() == 7, "DataResult.result()/get() of a success");
        check(bad.get().right().get().message().equals("nope") && bad.error().get().message().equals("nope") && bad.resultOrPartial(e -> { }).get() == 3,
            "DataResult.get()/error() of an error: PartialResult message and partial value");
        List<String> errs = new ArrayList<>();
        check(bad.getOrThrow(true, errs::add) == 3 && errs.equals(List.of("nope")), "getOrThrow(allowPartial, onError) answers the partial after onError");
        boolean threw = false;
        try {
            bad.getOrThrow(false, e -> { });
        } catch (RuntimeException e) {
            threw = "nope".equals(e.getMessage());
        }
        check(threw, "getOrThrow(false, ..) throws with the message");
        com.mojang.serialization.Codec<Integer> rec = com.mojang.serialization.codecs.RecordCodecBuilder.create(i -> i.group(
            com.mojang.serialization.Codec.optionalField("n", com.mojang.serialization.Codec.INT).forGetter(x -> java.util.Optional.of(x))
        ).apply(i, o -> o.orElse(-1)));
        com.google.gson.JsonObject j = new com.google.gson.JsonObject();
        j.addProperty("n", "not a number");
        check(rec.parse(com.mojang.serialization.JsonOps.INSTANCE, j).result().orElse(0) == -1, "Codec.optionalField is lenient (unparsable reads as absent)");
        com.mojang.serialization.codecs.ListCodec<Integer> list = new com.mojang.serialization.codecs.ListCodec<>(com.mojang.serialization.Codec.INT);
        check(list.encodeStart(com.mojang.serialization.JsonOps.INSTANCE, List.of(1, 2, 3)).result().get().toString().equals("[1,2,3]"), "new ListCodec(codec) encodes");
        com.mojang.serialization.MapCodec<Integer> kd = new com.mojang.serialization.codecs.KeyDispatchCodec<String, Integer>("type", com.mojang.serialization.Codec.STRING,
            v -> com.mojang.serialization.DataResult.success("int"), k -> com.mojang.serialization.DataResult.success(com.mojang.serialization.Codec.INT));
        com.google.gson.JsonElement enc = kd.codec().encodeStart(com.mojang.serialization.JsonOps.INSTANCE, 5).result().orElse(null);
        check(enc != null && enc.equals(com.google.gson.JsonParser.parseString("{\"type\":\"int\",\"value\":5}")), "KeyDispatchCodec(.., codec): a plain codec's value under \"value\" (" + enc + ")");
        check(kd.codec().parse(com.mojang.serialization.JsonOps.INSTANCE, enc).result().orElse(0) == 5, "KeyDispatchCodec decodes it back");
    }

    // ---- entity effect particles ----

    static void particles(class_3218 level) {
        net.minecraft.class_1295 cloud = new net.minecraft.class_1295(level, 0.5, 100, 0.5);
        cloud.method_5608(net.minecraft.class_2398.field_11226);
        net.minecraft.class_2960 type = net.minecraft.class_7923.field_41180.method_10221(cloud.method_5600().method_10295());
        check(type != null && type.toString().equals("minecraft:entity_effect"), "cloud with ENTITY_EFFECT holds a registered entity_effect particle (" + type + ")");
        cloud.method_5608(net.minecraft.class_2398.field_11225);
        type = net.minecraft.class_7923.field_41180.method_10221(cloud.method_5600().method_10295());
        check(type != null && type.toString().equals("minecraft:entity_effect"), "cloud with AMBIENT_ENTITY_EFFECT holds a registered entity_effect particle (" + type + ")");
        int a = level.method_14199(net.minecraft.class_2398.field_11226, 0.5, 100, 0.5, 0, 1.0, 0.0, 0.0, 1.0);
        int b = level.method_14199(net.minecraft.class_2398.field_11225, 0.5, 100, 0.5, 5, 0.2, 0.2, 0.2, 0.5);
        check(a == 0 && b == 0, "sendParticles of both entity effects (no players: reached " + a + "/" + b + ")");
    }

    // ---- game tests ----

    static boolean before, after, subBefore, passedFlag;
    static final List<String> HEARD = new ArrayList<>();

    /** a 1.20.4 mod batch overriding runBeforeBatchFunction */
    static class MyBatch extends class_4514 {
        MyBatch(String name, Collection<class_4529> functions, Consumer<class_3218> before, Consumer<class_3218> after) {
            super(name, functions, before, after);
        }

        @Override
        public void method_22153(class_3218 level) {
            subBefore = true;
            super.method_22153(level);
        }
    }

    static class MyListener implements class_4518 {
        @Override
        public void method_22188(class_4517 info) {
            HEARD.add("loaded");
        }

        @Override
        public void method_33317(class_4517 info) {
            HEARD.add("passed");
        }

        @Override
        public void method_22190(class_4517 info) {
            HEARD.add("failed");
        }
    }

    static Collection<class_4517> running;
    static int waited;
    static final class_2338 CORNER = new class_2338(8, 120, 8);

    static void gametests(class_3218 level) {
        class_4529 a = new class_4529("cbbatch", "cb_one", "cb_s4fingt:stone", class_2470.field_11467, 100, 0L, true, 2, 3, h -> h.method_36036());
        check(a.method_32259() == 2 && a.method_32258() == 3, "TestFunction requiredSuccesses 2 / maxAttempts 3 (" + a.method_32259() + "/" + a.method_32258() + ")");
        check("cbbatch".equals(a.method_22301()) && "cb_one".equals(a.method_22296()) && "cb_s4fingt:stone".equals(a.method_22298()),
            "TestFunction batch/test/structure names");
        check(a.method_22300() && a.method_22299() == 100, "TestFunction required, maxTicks 100");
        class_4529 b = new class_4529("cbbatch", "cb_two", "cb_s4fingt:stone", class_2470.field_11467, 100, 0L, true, h -> h.method_36036());
        class_4529 c = new class_4529("other", "cb_three", "cb_s4fingt:stone", 100, 0L, true, h -> h.method_36036());
        Collection<class_4514> grouped = class_4520.method_22209(List.of(a, b, c));
        List<String> names = new ArrayList<>();
        for (class_4514 g : grouped) {
            names.add(g.method_22152() + "=" + g.method_22154().size());
        }
        check(String.join(",", names).equals("cbbatch:1=2,other:1=1"), "groupTestsIntoBatches [" + String.join(",", names) + "]");
        class_4514 first = grouped.iterator().next();
        check(first.method_22154().contains(a) && first.method_22154().contains(b), "grouped batch's getTestFunctions holds the functions");

        class_4514 own = new class_4514("own", List.of(a), l -> before = true, l -> after = true);
        own.method_22153(level);
        own.method_32237(level);
        check(before && after, "mod batch runBefore/runAfterBatchFunction run its functions");
        check(own.method_22154().size() == 1 && own.method_22154().iterator().next() == a, "mod batch getTestFunctions is its function");
        before = after = false;

        class_4517 info = new class_4517(a, class_2470.field_11467, level);
        check(!info.method_54145(), "new GameTestInfo is not rerun-until-failed");
        info.method_54146(true);
        check(info.method_54145(), "setRerunUntilFailed(true) reads back");
        info.method_54146(false);
        check(!info.method_54145(), "setRerunUntilFailed(false) reads back");
        check(info.method_29403() == a, "GameTestInfo's test function");

        class_4516 helper = new class_4516(info);
        class_1657 survival = helper.method_46228();
        check(!survival.method_7337() && !survival.method_7325(), "makeMockSurvivalPlayer: neither creative nor spectator");
        class_1657 mock = helper.method_36021();
        check(mock.method_7337() && !mock.method_7325(), "makeMockPlayer: creative, not spectator (1.20.4's)");

        check(class_4519.method_29405() != null, "getLastFailedTests answers a collection");
        check(class_4519.method_22193("cb.none.Nothing").isEmpty(), "getTestFunctionsForClassName of an unknown class is empty");
        check(new class_4520() != null, "new GameTestRunner()");

        class_4514 mine = new MyBatch("mine", List.of(a), l -> before = true, l -> after = true);
        running = class_4520.method_22210(List.of(mine), CORNER, class_2470.field_11467, level, class_4521.field_20574, 8);
        check(running.size() == 1 && running.iterator().next().method_29403() == a, "runTestBatches answers the batch's one info");
        running.iterator().next().method_22167(new MyListener());
        check(subBefore && before, "batch subclass's runBeforeBatchFunction ran (and its super the function)");
    }

    static void tick(MinecraftServer server) {
        if (running == null) {
            return;
        }
        // vanilla ticks GameTestTicker only when SharedConstants.IS_RUNNING_IN_IDE (1.20.4 and 1.20.5 alike); a mod
        // hosting its own tests ticks it itself
        class_4521.field_20574.method_22228();
        class_4517 info = running.iterator().next();
        if (waited % 100 == 1) {
            System.out.println("CBS4GT-DIAG tick " + waited + " started=" + info.method_22179() + " done=" + info.method_22180()
                + " entityTicking=" + server.method_30002().method_37118(CORNER));
        }
        if (!info.method_22180() && ++waited < 400) { // isDone
            return;
        }
        class_3218 level = server.method_30002();
        guard("run", () -> {
            check(info.method_22180(), "test done (" + waited + " ticks)");
            check(info.method_22177(), "test passed (error " + info.method_22182() + ")");
            check(after, "batch's after function ran when the batch was done");
            check(HEARD.contains("passed"), "mod listener heard the pass " + HEARD);
            class_2338 pos = info.method_54843();
            check(pos != null, "getStructureBlockPos " + pos);
            check(info.method_36061() != null, "getStructureSize " + info.method_36061());
            Collection<class_2338> found = class_4525.method_22258(CORNER, 16, level);
            check(!found.isEmpty(), "findStructureBlocks finds the structure block " + found);
            class_4520.method_22216(level, CORNER.method_10074(), class_4521.field_20574, 16); // the structure block sits a block below the corner
            check(class_4525.method_22258(CORNER, 16, level).isEmpty(), "clearAllTests cleared the structure");
        });
        running = null;
        System.out.println("CBS4GT DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " FAILS " + FAILS));
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            guard("both", () -> registries("both", new Both(class_5321.method_29180(new class_2960("cb", "both"))), "R:a,M0:a", "R:b,M1:b", "M2:c"));
            guard("register only", () -> registries("register only", new RegisterOnly(class_5321.method_29180(new class_2960("cb", "ronly"))), "R:a", "R:b", ""));
            guard("mapping only", () -> registries("mapping only", new MappingOnly(class_5321.method_29180(new class_2960("cb", "monly"))), "M0:a", "M1:b", "M2:c"));
            guard("particles", () -> particles(server.method_30002()));
            guard("dfu", S4FinGtMod::dfu);
            guard("gametests", () -> gametests(server.method_30002()));
            if (running == null) {
                System.out.println("CBS4GT DONE " + (checks - FAILS.size()) + "/" + checks + (FAILS.isEmpty() ? "" : " FAILS " + FAILS));
            }
        });
        ServerTickEvents.END_SERVER_TICK.register(S4FinGtMod::tick);
    }
}
