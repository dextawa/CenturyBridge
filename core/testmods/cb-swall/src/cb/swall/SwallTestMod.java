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

        ServerLifecycleEvents.SERVER_STARTED.register(SwallTestMod::onStarted);
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
            interactions(server);
            levelData(server);
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
}
