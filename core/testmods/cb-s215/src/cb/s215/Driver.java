package cb.s215;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_1299;
import net.minecraft.class_1311;
import net.minecraft.class_1889;
import net.minecraft.class_1893;
import net.minecraft.class_1959;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_3218;
import net.minecraft.class_5483;
import net.minecraft.class_5819;
import net.minecraft.class_6005;
import net.minecraft.class_6007;
import net.minecraft.class_6008;
import net.minecraft.class_6011;
import net.minecraft.class_6012;
import net.minecraft.class_6880;
import net.minecraft.class_7924;
import net.minecraft.server.MinecraftServer;

/**
 * cb-s215: a 1.21.4-era mod (intermediary names, no refmap), converted 1.21.4 -> 1.21.5 by the step's wiring. Runs once
 * the world is loaded and checks 1.21.4's outcomes on 1.21.5 through the step layer. Logs "CB-S215 ok|FAIL <case>
 * <detail>" and "CB-S215 summary ok=N fail=M". cb-s215n (native/, a 1.21.5 mod, not converted) reads what this mod
 * built through 1.21.5's own API (GEN, LIST) by reflection.
 */
public final class Driver {
    /** this mod's ChunkGenerator (getMobsAt override), read by cb-s215n through 1.21.5's ChunkGenerator.getMobsAt */
    public static volatile Object GEN;
    /** a WeightedRandomList this mod created from 1.21.4 entries, read by cb-s215n as a 1.21.5 WeightedList */
    public static volatile Object LIST;
    /** this mod's SavedData and the MapIndex vanilla's factory() got from the overworld storage, read by cb-s215n */
    public static volatile Object SAVED;
    public static volatile Object MAP_INDEX;
    public static volatile boolean DONE;
    private static int ok;
    private static int fail;

    private Driver() {
    }

    static void check(String c, boolean cond, String detail) {
        if (cond) {
            ok++;
        } else {
            fail++;
        }
        System.out.println("CB-S215 " + (cond ? "ok " : "FAIL ") + c + " " + detail);
    }

    interface Body {
        void run() throws Exception;
    }

    static void run(String c, Body r) {
        try {
            r.run();
        } catch (Throwable t) {
            fail++;
            System.out.println("CB-S215 FAIL " + c + " threw " + t);
            t.printStackTrace(System.out);
        }
    }

    /** a mod's own intrusive entry (1.21.4 WeightedEntry.IntrusiveBase subclass) */
    public static final class Prize extends class_6008.class_6009 {
        public final String name;

        public Prize(String name, int weight) {
            super(weight);
            this.name = name;
        }
    }

    public static synchronized void runOnce(MinecraftServer server) {
        if (DONE) {
            return;
        }
        class_5819 random = class_5819.method_43049(215L);
        class_3218 level = server.method_30002();

        run("weight", () -> {
            class_6007 w = class_6007.method_34977(5);
            check("weight", w.method_34976() == 5 && w.equals(class_6007.method_34977(5)) && "5".equals(w.toString())
                && class_6007.method_34977(1) == class_6007.method_34977(1), "asInt=" + w.method_34976() + " str=" + w);
        });

        run("simple-list", () -> {
            class_6005<String> l = class_6005.<String>method_34971().method_34975("a", 3).method_34975("b", 7).method_34974();
            List<class_6008.class_6010<String>> entries = l.method_34994();
            StringBuilder sb = new StringBuilder();
            int total = 0;
            for (class_6008.class_6010<String> e : entries) {
                sb.append(e.comp_2542()).append('=').append(e.comp_2543().method_34976()).append('/').append(e.method_34979().method_34976()).append(' ');
                total += e.comp_2543().method_34976();
            }
            Optional<String> v = l.method_34973(random);
            Optional<class_6008.class_6010<String>> w = l.method_34992(random);
            check("simple-list", entries.size() == 2 && total == 10 && v.isPresent() && ("a".equals(v.get()) || "b".equals(v.get()))
                && w.isPresent() && w.get() instanceof class_6008.class_6010 && ("a".equals(w.get().comp_2542()) || "b".equals(w.get().comp_2542())),
                sb + "value=" + v.orElse(null) + " entry=" + w.map(x -> x.comp_2542() + ":" + x.comp_2543()).orElse(null));
            class_6005<String> one = class_6005.method_38061("x");
            check("simple-single-empty", one.method_34994().size() == 1 && one.method_34994().get(0).comp_2543().method_34976() == 1
                && class_6005.<String>method_38062().method_34993(), "single=" + one.method_34994());
        });

        run("simple-codec", () -> {
            Codec<class_6005<String>> codec = class_6005.method_39521(Codec.STRING);
            class_6005<String> l = class_6005.<String>method_34971().method_34975("p", 4).method_34974();
            JsonElement json = codec.encodeStart(JsonOps.INSTANCE, l).getOrThrow();
            JsonObject first = json.getAsJsonArray().get(0).getAsJsonObject();
            class_6005<String> back = codec.parse(JsonOps.INSTANCE, json).getOrThrow();
            check("simple-codec", "p".equals(first.get("data").getAsString()) && first.get("weight").getAsInt() == 4
                && back.method_34994().get(0).comp_2543().method_34976() == 4, "json=" + json);
        });

        run("wrap", () -> {
            class_6008.class_6010<String> w = class_6008.method_34980("w", 9);
            class_6008.class_6010<String> n = new class_6008.class_6010<>("n", class_6007.method_34977(6));
            class_6008 asEntry = w;
            check("wrap", w.comp_2543().method_34976() == 9 && n.comp_2543().method_34976() == 6 && asEntry.method_34979().method_34976() == 9,
                "wrap=" + w + " new=" + n);
        });

        run("intrusive-list", () -> {
            Prize gold = new Prize("gold", 2);
            Prize coal = new Prize("coal", 18);
            class_6012<Prize> l = class_6012.method_34989(gold, coal);
            List<Prize> unwrapped = l.method_34994();
            Optional<Prize> pick = l.method_34992(random);
            int total = class_6011.method_34984(unwrapped);
            Optional<Prize> pick2 = class_6011.method_34986(random, unwrapped);
            check("intrusive-list", unwrapped.size() == 2 && unwrapped.get(0) == gold && unwrapped.get(1) == coal && total == 20
                && pick.isPresent() && pick.get() instanceof Prize && pick2.isPresent(),
                "unwrap=" + unwrapped.get(0).name + "," + unwrapped.get(1).name + " total=" + total + " pick=" + pick.map(p -> p.name).orElse(null));
            class_6012<Prize> fromList = class_6012.method_34988(List.of(coal));
            check("intrusive-create-list", fromList.method_34994().get(0) == coal && fromList.method_34992(random).get() == coal, "");
        });

        run("spawner-data", () -> {
            class_5483.class_1964 zombie = new class_5483.class_1964(class_1299.field_6051, 42, 1, 3);
            class_5483.class_1964 pig = new class_5483.class_1964(class_1299.field_6093, class_6007.method_34977(8), 2, 4);
            check("spawner-data", zombie.field_9389 == class_1299.field_6051 && zombie.field_9388 == 1 && zombie.field_9387 == 3
                && zombie.method_34979().method_34976() == 42 && pig.method_34979().method_34976() == 8,
                "zombie w=" + zombie.method_34979() + " pig w=" + pig.method_34979());
            class_6012<class_5483.class_1964> l = class_6012.method_34989(zombie, pig);
            LIST = l;
            List<class_5483.class_1964> u = l.method_34994();
            int total = class_6011.method_34984(u);
            class_5483.class_1964 got = l.method_34992(random).orElseThrow();
            check("spawner-list", u.size() == 2 && u.get(0) == zombie && total == 50 && (got == zombie || got == pig),
                "total=" + total + " pick=" + got.field_9389);
            class_5483 settings = new class_5483.class_5496().method_31011(class_1311.field_6302, zombie).method_31011(class_1311.field_6302, pig).method_31007();
            List<class_5483.class_1964> mobs = settings.method_31004(class_1311.field_6302).method_34994();
            int sum = 0;
            for (class_5483.class_1964 d : mobs) {
                sum += d.method_34979().method_34976();
            }
            check("spawner-builder", mobs.size() == 2 && sum == 50, "weights sum=" + sum);
        });

        run("spawner-codec", () -> {
            JsonObject o = new JsonObject();
            o.addProperty("type", "minecraft:zombie");
            o.addProperty("weight", 42);
            o.addProperty("minCount", 1);
            o.addProperty("maxCount", 3);
            JsonArray a = new JsonArray();
            a.add(o);
            class_5483.class_1964 d = class_5483.class_1964.field_24681.parse(JsonOps.INSTANCE, o).getOrThrow();
            class_6012<class_5483.class_1964> l = class_6012.method_34991(class_5483.class_1964.field_24681).parse(JsonOps.INSTANCE, a).getOrThrow();
            JsonElement re = class_5483.class_1964.field_24681.encodeStart(JsonOps.INSTANCE, d).getOrThrow();
            check("spawner-codec", d.method_34979().method_34976() == 42 && d.field_9387 == 3 && l.method_34994().get(0).method_34979().method_34976() == 42
                && re.getAsJsonObject().get("weight").getAsInt() == 42, "re=" + re);
        });

        run("vanilla-spawns", () -> {
            class_6880<class_1959> plains = server.method_30611().method_30530(class_7924.field_41236)
                .method_46747(net.minecraft.class_1972.field_9451);
            List<class_5483.class_1964> creatures = plains.comp_349().method_30966().method_31004(class_1311.field_6294).method_34994();
            int sheep = -1;
            StringBuilder sb = new StringBuilder();
            for (class_5483.class_1964 d : creatures) {
                sb.append(class_1299.method_5890(d.field_9389).method_12832()).append('=').append(d.method_34979().method_34976()).append(' ');
                if (d.field_9389 == class_1299.field_6115) {
                    sheep = d.method_34979().method_34976();
                }
            }
            check("vanilla-spawns", sheep == 12, sb.toString());
        });

        run("enchantment-instance", () -> {
            class_6880<net.minecraft.class_1887> sharp = server.method_30611().method_30530(class_7924.field_41265).method_46747(class_1893.field_9118);
            class_1889 inst = new class_1889(sharp, 3);
            List<class_1889> list = new ArrayList<>();
            list.add(inst);
            check("enchantment-instance", inst.field_9094 == 3 && inst.field_9093 == sharp
                && inst.method_34979().method_34976() == sharp.comp_349().method_58445() && class_6011.method_34984(list) == sharp.comp_349().method_58445(),
                "weight=" + inst.method_34979() + " level=" + inst.field_9094);
        });

        run("mobs-at", () -> {
            Gen gen = new Gen(level.method_14178().method_12129().method_12098());
            GEN = gen;
            class_2338 pos = new class_2338(0, 70, 0);
            List<class_5483.class_1964> mobs = gen.method_12113(level.method_23753(pos), level.method_27056(), class_1311.field_6302, pos).method_34994();
            class_5483.class_1964 last = mobs.get(mobs.size() - 1);
            check("mobs-at", last.field_9389 == class_1299.field_6099 && last.method_34979().method_34976() == 77 && Gen.vanillaSeen > 0,
                "n=" + mobs.size() + " vanilla=" + Gen.vanillaSeen + " last=" + class_1299.method_5890(last.field_9389) + ":" + last.method_34979());
        });

        run("saved-data", () -> {
            net.minecraft.class_26 storage = level.method_17983();
            net.minecraft.class_18.class_8645<Counter> factory = new net.minecraft.class_18.class_8645<>(Counter::new, Counter::load,
                net.minecraft.class_4284.field_45077);
            java.nio.file.Path dataDir = server.method_27050(net.minecraft.class_5218.field_24188).resolve("data");
            // a 1.21.4-format file written before the first load: {"data": {"count": 7}, "DataVersion": current}
            class_2487 file = new class_2487();
            class_2487 data = new class_2487();
            data.method_10569("count", 7);
            file.method_10566("data", data);
            net.minecraft.class_2512.method_48310(file);
            java.nio.file.Files.createDirectories(dataDir);
            net.minecraft.class_2507.method_30614(file, dataDir.resolve("cb_s215_load.dat"));
            Counter loaded = storage.method_17924(factory, "cb_s215_load");
            Counter again = storage.method_20786(factory, "cb_s215_load");
            Counter fresh = storage.method_17924(factory, "cb_s215_fresh");
            fresh.count = 41;
            fresh.method_80();
            storage.method_125();
            class_2487 written = net.minecraft.class_2507.method_30613(dataDir.resolve("cb_s215_fresh.dat"), net.minecraft.class_2505.method_53898());
            class_2487 viaSave = fresh.method_17919(server.method_30611());
            SAVED = fresh;
            MAP_INDEX = storage.method_17924(net.minecraft.class_3978.method_52610(), "idcounts");
            check("saved-data", loaded.count == 7 && again == loaded && written.method_10562("data").method_10550("count") == 41
                && viaSave.method_10562("data").method_10550("count") == 41 && !fresh.method_79(),
                "loaded=" + loaded.count + " written=" + written + " mapIndex=" + MAP_INDEX);
        });

        run("variant-predicates", () -> {
            net.minecraft.class_1451 cat = (net.minecraft.class_1451) class_1299.field_16281.method_5883(level, net.minecraft.class_3730.field_16462);
            class_6880<net.minecraft.class_7375> own = cat.method_47843();
            net.minecraft.class_7376 same = net.minecraft.class_7378.method_58153(own);
            net.minecraft.class_243 at = new net.minecraft.class_243(0, 70, 0);
            boolean matches = same.method_22497(cat, level, at);
            boolean otherFails = !net.minecraft.class_7378.method_58154(server.method_30611().method_30530(class_7924.field_41272)
                .method_46747(net.minecraft.class_7106.field_37462)).method_22497(cat, level, at);
            boolean registered = net.minecraft.class_7923.field_49911.method_10223(net.minecraft.class_2960.method_60656("axolotl")).isPresent()
                && net.minecraft.class_7923.field_49911.method_10223(net.minecraft.class_2960.method_60656("cat")).isPresent();
            check("variant-predicates", matches && otherFails && registered && net.minecraft.class_7378.field_41534 != null,
                "catMatchesOwn=" + matches + " frogPredicateRejectsCat=" + otherFails + " registered=" + registered);
        });

        run("smithing-transform", () -> {
            net.minecraft.class_1856 iron = net.minecraft.class_1856.method_8091(net.minecraft.class_1802.field_8620);
            net.minecraft.class_8060 r = new net.minecraft.class_8060(Optional.empty(), Optional.of(iron), Optional.empty(),
                new net.minecraft.class_1799(net.minecraft.class_1802.field_8477, 2));
            Optional<net.minecraft.class_1856> base = r.method_64723();
            Optional<net.minecraft.class_1856> viaIface = ((net.minecraft.class_8059) r).method_64723();
            net.minecraft.class_1799 out = r.method_60000(new net.minecraft.class_9697(net.minecraft.class_1799.field_8037,
                new net.minecraft.class_1799(net.minecraft.class_1802.field_8620), net.minecraft.class_1799.field_8037),
                server.method_30611());
            check("smithing-transform", base.isPresent() && base.get() == iron && viaIface.isPresent()
                && out.method_31574(net.minecraft.class_1802.field_8477) && out.method_7947() == 2,
                "base=" + base + " out=" + out);
        });

        DONE = true;
        System.out.println("CB-S215 summary ok=" + ok + " fail=" + fail);
    }
}
