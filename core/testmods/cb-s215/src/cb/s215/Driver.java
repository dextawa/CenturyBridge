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

    /** Block.fallOn as 1.21.5 declares it (fall distance double), looked up at run time: this mod is compiled against 1.21.4 */
    static java.lang.reflect.Method class_2248_fallOnW() throws NoSuchMethodException {
        return net.minecraft.class_2248.class.getMethod("method_9554", net.minecraft.class_1937.class, net.minecraft.class_2680.class,
            class_2338.class, net.minecraft.class_1297.class, double.class);
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

        run("shape-static", () -> {
            // GrindstoneBlock.FLOOR_NORTH_* (field_16338): 1.21.4 built it from boxes in <clinit>; 1.21.5 has no such field
            net.minecraft.class_238 b = net.minecraft.class_3713.field_16338.method_1107();
            check("shape-static", Math.abs(b.field_1323 - 2 / 16.0) < 1e-9 && b.field_1322 == 0 && Math.abs(b.field_1321 - 2 / 16.0) < 1e-9
                && Math.abs(b.field_1320 - 14 / 16.0) < 1e-9 && b.field_1325 == 1 && Math.abs(b.field_1324 - 14 / 16.0) < 1e-9, "bounds=" + b);
        });

        run("fall-on", () -> {
            FallBlock block = Init.fall;
            net.minecraft.class_1303 orb = new net.minecraft.class_1303(level, 0, 80, 0, 3);
            class_2338 pos = new class_2338(0, 79, 0);
            // the mod's own 1.21.4 call (float) ...
            block.method_9554(level, block.method_9564(), pos, orb, 1.5F);
            float viaMod = FallBlock.got;
            // ... and 1.21.5's vanilla call shape (double), as Entity.checkFallDamage makes it
            java.lang.reflect.Method w = class_2248_fallOnW();
            w.invoke(block, level, block.method_9564(), pos, orb, 2.5D);
            check("fall-on", viaMod == 1.5F && FallBlock.got == 2.5F && FallBlock.calls == 2, "viaMod=" + viaMod + " viaVanilla=" + FallBlock.got
                + " calls=" + FallBlock.calls);
        });

        run("orb-packet", () -> {
            net.minecraft.class_1303 orb = new net.minecraft.class_1303(level, 1.25, 70.5, -3.75, 11);
            net.minecraft.class_3231 se = new net.minecraft.class_3231(level, orb, 0, false, p -> {
            });
            net.minecraft.class_2606 p = new net.minecraft.class_2606(orb, se);
            net.minecraft.class_2540 buf = new net.minecraft.class_2540(io.netty.buffer.Unpooled.buffer());
            net.minecraft.class_2606.field_47897.encode(buf, p);
            net.minecraft.class_2606 back = net.minecraft.class_2606.field_47897.decode(buf);
            check("orb-packet", p.method_11183() == orb.method_5628() && p.method_11185() == 1.25 && p.method_11181() == 70.5
                && p.method_11180() == -3.75 && p.method_11184() == 11 && back.method_11183() == p.method_11183()
                && back.method_11185() == 1.25 && back.method_11184() == 11 && p.method_65080() == net.minecraft.class_9094.field_48149,
                "id=" + p.method_11183() + " pos=" + p.method_11185() + "," + p.method_11181() + "," + p.method_11180() + " value=" + p.method_11184()
                + " type=" + p.method_65080());
        });

        run("slot-iterables", () -> {
            net.minecraft.class_1642 zombie = new net.minecraft.class_1642(level);
            zombie.method_5673(net.minecraft.class_1304.field_6169, new net.minecraft.class_1799(net.minecraft.class_1802.field_8743));
            zombie.method_5673(net.minecraft.class_1304.field_6173, new net.minecraft.class_1799(net.minecraft.class_1802.field_8371));
            List<net.minecraft.class_1799> armor = new ArrayList<>();
            zombie.method_5661().forEach(armor::add);
            List<net.minecraft.class_1799> hands = new ArrayList<>();
            zombie.method_5877().forEach(hands::add);
            int all = 0;
            for (net.minecraft.class_1799 st : zombie.method_56675()) {
                all++;
            }
            // a write through the 1.21.4 list reaches the entity's slot (Mob.armorItems was its storage)
            ((List<net.minecraft.class_1799>) zombie.method_5661()).set(0, new net.minecraft.class_1799(net.minecraft.class_1802.field_8660));
            check("slot-iterables", armor.size() == 4 && armor.get(3).method_31574(net.minecraft.class_1802.field_8743)
                && hands.size() == 2 && hands.get(0).method_31574(net.minecraft.class_1802.field_8371) && all == 7
                && zombie.method_6118(net.minecraft.class_1304.field_6166).method_31574(net.minecraft.class_1802.field_8660),
                "armor=" + armor + " hands=" + hands + " all=" + all);
        });

        run("rotations", () -> {
            net.minecraft.class_2379 r = new net.minecraft.class_2379(370.0F, -10.0F, 190.0F);
            net.minecraft.class_2499 tag = r.method_10255();
            net.minecraft.class_2379 back = new net.minecraft.class_2379(tag);
            check("rotations", r.method_10256() == 10.0F && r.method_10257() == -10.0F && r.method_35847() == -170.0F
                && back.method_10256() == 10.0F && back.method_10258() == 190.0F && tag.size() == 3 && back.equals(r),
                "x=" + r.method_10256() + " wrappedZ=" + r.method_35847() + " tag=" + tag);
        });

        run("lerp", () -> {
            net.minecraft.class_1642 z = new net.minecraft.class_1642(level);
            z.method_5814(0, 70, 0);
            z.method_5759(5, 70, 0, 90.0F, 0.0F, 3);
            double target = z.method_53830();
            float yRot = z.method_53831();
            z.method_64610();
            double after = z.method_53830();
            net.minecraft.class_1542 item = new net.minecraft.class_1542(level, 0, 70, 0, new net.minecraft.class_1799(net.minecraft.class_1802.field_8371));
            item.method_5759(2, 71, 3, 0.0F, 0.0F, 3);
            check("lerp", target == 5 && yRot == 90.0F && after == z.method_23317() && item.method_23317() == 2 && item.method_23318() == 71,
                "target=" + target + " yRot=" + yRot + " afterCancel=" + after + " item=" + item.method_23317() + "," + item.method_23318());
        });

        run("container-click", () -> {
            it.unimi.dsi.fastutil.ints.Int2ObjectMap<net.minecraft.class_1799> changed = new it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap<>();
            changed.put(5, new net.minecraft.class_1799(net.minecraft.class_1802.field_8743, 1));
            net.minecraft.class_2813 p = new net.minecraft.class_2813(3, 17, 300, 1, net.minecraft.class_1713.field_7790,
                new net.minecraft.class_1799(net.minecraft.class_1802.field_8371, 2), changed);
            net.minecraft.class_1799 carried = p.method_12190();
            net.minecraft.class_1799 slot5 = p.method_34678().get(5);
            check("container-click", p.method_12194() == 3 && p.method_37440() == 17 && p.method_12192() == 300 && p.method_12193() == 1
                && p.method_12195() == net.minecraft.class_1713.field_7790 && carried.method_31574(net.minecraft.class_1802.field_8371)
                && carried.method_7947() == 2 && slot5.method_31574(net.minecraft.class_1802.field_8743),
                "slot=" + p.method_12192() + " carried=" + carried + " changed=" + p.method_34678());
        });

        run("region-ticket", () -> {
            net.minecraft.class_1923 cp = new net.minecraft.class_1923(40, 40);
            // 1.21.5 keeps tickets in the cache's TicketStorage (read by reflection: this mod is compiled against 1.21.4)
            java.lang.reflect.Field tf = level.method_14178().getClass().getDeclaredField("field_55594");
            tf.setAccessible(true);
            Object storage = tf.get(level.method_14178());
            java.lang.reflect.Method tickets = storage.getClass().getMethod("method_66352", long.class);
            java.util.function.IntSupplier forced = () -> {
                try {
                    int n = 0;
                    for (Object t : (List<?>) tickets.invoke(storage, cp.method_8324())) {
                        if (t.getClass().getMethod("method_14281").invoke(t) == net.minecraft.class_3230.field_14031) {
                            n++;
                        }
                    }
                    return n;
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            };
            int before = forced.getAsInt();
            level.method_14178().method_17297(net.minecraft.class_3230.field_14031, cp, 1, cp);
            int added = forced.getAsInt();
            level.method_14178().method_17300(net.minecraft.class_3230.field_14031, cp, 1, cp);
            int removed = forced.getAsInt();
            check("region-ticket", added == before + 1 && removed == before, "before=" + before + " added=" + added + " removed=" + removed);
        });

        run("shape-sample", () -> {
            List<String> lines = ShapeSample.lines();
            for (String line : lines) {
                System.out.println("CB-S215 shape " + line);
            }
            check("shape-sample", lines.size() == 20, "n=" + lines.size());
        });

        run("villager-data", () -> {
            net.minecraft.class_3850 d = new net.minecraft.class_3850(net.minecraft.class_3854.field_17073, net.minecraft.class_3852.field_17056, 3);
            net.minecraft.class_3850 d2 = d.method_16921(net.minecraft.class_3852.field_17051).method_16922(net.minecraft.class_3854.field_17071);
            String name = net.minecraft.class_3852.field_17056.comp_818();
            check("villager-data", d.method_16924() == net.minecraft.class_3852.field_17056 && d.method_16919() == net.minecraft.class_3854.field_17073
                && d.method_16925() == 3 && d2.method_16924() == net.minecraft.class_3852.field_17051 && d2.method_16919() == net.minecraft.class_3854.field_17071
                && d2.method_16925() == 3 && "farmer".equals(name),
                "profession=" + d.method_16924() + " type=" + d.method_16919() + " level=" + d.method_16925() + " name=" + name);
        });

        run("raid", () -> {
            class_2338 center = new class_2338(30, 70, 30);
            net.minecraft.class_3765 raid = new net.minecraft.class_3765(77, level, center);
            net.minecraft.class_2487 saved = raid.method_16502(new net.minecraft.class_2487());
            net.minecraft.class_3765 back = new net.minecraft.class_3765(level, saved);
            check("raid", raid.method_16494() == 77 && raid.method_16831() == level && saved.method_10550("Id") == 77
                && saved.method_10550("CX") == 30 && saved.method_10550("CZ") == 30 && back.method_16494() == 77 && back.method_16831() == level
                && back.method_16495().equals(center) && "ongoing".equals(saved.method_10558("Status")),
                "id=" + raid.method_16494() + " saved=" + saved + " back=" + back.method_16494() + "@" + back.method_16495());
        });

        run("villager-rest", () -> {
            int pkg = net.minecraft.class_4129.method_19020(net.minecraft.class_3852.field_17056, 0.5F).size();
            net.minecraft.class_1646 v = new net.minecraft.class_1646(net.minecraft.class_1299.field_6077, level, net.minecraft.class_3854.field_17071);
            net.minecraft.class_3854 t0 = v.method_47882();
            v.method_47883(net.minecraft.class_3854.field_17075);
            java.util.UUID who = java.util.UUID.randomUUID();
            net.minecraft.class_2487 g = new net.minecraft.class_2487();
            g.method_10539("Target", net.minecraft.class_4844.method_26275(who));
            g.method_10582("Type", "minor_positive");
            g.method_10569("Value", 7);
            net.minecraft.class_2499 gl = new net.minecraft.class_2499();
            gl.add(g);
            v.method_21650(gl);
            int rep = v.method_21651().method_19073(who, x -> true);
            check("villager-rest", pkg > 0 && t0 == net.minecraft.class_3854.field_17071 && v.method_7231().method_16919() == net.minecraft.class_3854.field_17075
                && rep == 7, "corePackage=" + pkg + " type0=" + t0 + " type1=" + v.method_7231().method_16919() + " reputation=" + rep);
        });

        run("raids", () -> {
            net.minecraft.class_3767 fresh = new net.minecraft.class_3767(level);
            fresh.method_16539();
            net.minecraft.class_2487 r = new net.minecraft.class_2487();
            r.method_10569("Id", 3);
            r.method_10569("CX", 5);
            r.method_10569("CY", 64);
            r.method_10569("CZ", 6);
            r.method_10582("Status", "ongoing");
            net.minecraft.class_2499 rl = new net.minecraft.class_2499();
            rl.add(r);
            net.minecraft.class_2487 file = new net.minecraft.class_2487();
            file.method_10569("NextAvailableID", 4);
            file.method_10569("Tick", 11);
            file.method_10566("Raids", rl);
            net.minecraft.class_3767 loaded = net.minecraft.class_3767.method_77(level, file);
            net.minecraft.class_3765 raid = loaded.method_16541(3);
            String fileId = net.minecraft.class_3767.method_16533(level.method_40134());
            check("raids", raid != null && raid.method_16494() == 3 && raid.method_16831() == level && raid.method_16495().equals(new class_2338(5, 64, 6))
                && "raids".equals(fileId) && !net.minecraft.class_3767.method_16838(null, raid),
                "raid=" + raid + " id=" + (raid == null ? -1 : raid.method_16494()) + " fileId=" + fileId);
        });

        run("pink-petals", () -> {
            net.minecraft.class_2248 petals = net.minecraft.class_2246.field_42750;
            net.minecraft.class_2680 s = petals.method_9564().method_11657(net.minecraft.class_8169.field_42765, 3);
            check("pink-petals", petals instanceof net.minecraft.class_8169 && s.method_11654(net.minecraft.class_8169.field_42765) == 3
                && net.minecraft.class_8169.field_46409 != null && s.method_28498(net.minecraft.class_8169.field_42764),
                petals.getClass().getName() + " amount=" + s.method_11654(net.minecraft.class_8169.field_42765));
        });

        run("gametest-assert", () -> {
            net.minecraft.class_4512 e = new net.minecraft.class_4512("cb boom");
            net.minecraft.class_4513 p = new net.minecraft.class_4513("cb pos", new class_2338(1, 2, 3), new class_2338(0, 0, 0), 9L);
            check("gametest-assert", e.getMessage().contains("cb boom") && p.method_22151().equals(new class_2338(1, 2, 3)),
                e.getMessage() + " / " + p.getMessage());
        });

        run("level-sounds", () -> {
            // 1.21.5's Entity shapes, called the way 1.21.5 vanilla calls them (this mod is compiled against 1.21.4)
            java.lang.reflect.Field uf = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            uf.setAccessible(true);
            SoundLevel sl = (SoundLevel) ((sun.misc.Unsafe) uf.get(null)).allocateInstance(SoundLevel.class);
            java.lang.reflect.Field seen = SoundLevel.class.getField("seen");
            seen.setAccessible(true);
            seen.set(sl, new ArrayList<String>());
            net.minecraft.class_1297 pig = class_1299.field_6093.method_5883(level, net.minecraft.class_3730.field_16462);
            net.minecraft.class_3222 player = new net.minecraft.class_3222(server, level,
                new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "cbs215"), net.minecraft.class_8791.method_53821());
            Class<?> e = net.minecraft.class_1297.class;
            class_6880<net.minecraft.class_3414> holder = class_6880.method_40223(net.minecraft.class_3417.field_14627);
            var seeded = class_3218.class.getMethod("method_8465", e, double.class, double.class, double.class, class_6880.class,
                net.minecraft.class_3419.class, float.class, float.class, long.class);
            seeded.invoke(sl, pig, 1.0, 2.0, 3.0, holder, net.minecraft.class_3419.field_15254, 1.0F, 1.0F, 7L);
            seeded.invoke(sl, player, 4.0, 2.0, 3.0, holder, net.minecraft.class_3419.field_15254, 1.0F, 1.0F, 8L);
            class_3218.class.getMethod("method_8444", e, int.class, class_2338.class, int.class).invoke(sl, pig, 1000, class_2338.field_10980, 5);
            var interact = class_3218.class.getMethod("method_8505", e, class_2338.class);
            boolean asPlayer = (Boolean) interact.invoke(sl, player, class_2338.field_10980);
            boolean asPig = (Boolean) interact.invoke(sl, pig, class_2338.field_10980);
            class_3218.class.getMethod("method_8396", e, class_2338.class, net.minecraft.class_3414.class, net.minecraft.class_3419.class,
                float.class, float.class).invoke(sl, pig, new class_2338(10, 64, 0), net.minecraft.class_3417.field_14627,
                net.minecraft.class_3419.field_15254, 1.0F, 1.0F);
            // 1.21.4 call sites (reshaped / renamed) on the real overworld: no player online, so nothing is sent
            level.method_8449(null, pig, holder, net.minecraft.class_3419.field_15254, 1.0F, 1.0F, 3L);
            level.method_43129(player, pig, net.minecraft.class_3417.field_14627, net.minecraft.class_3419.field_15254, 1.0F, 1.0F);
            level.method_45445(pig, class_2338.field_10980, net.minecraft.class_3417.field_14627, net.minecraft.class_3419.field_15254, 1.0F, 1.0F);
            String got = String.join(",", sl.seen);
            check("level-sounds", got.equals("seeded:null:1:7,seeded:player:4:8,event:null:1000:5,interact:player,entity-overload,sound:null:10.5")
                && asPlayer && asPig, got + " asPlayer=" + asPlayer + " asPig=" + asPig);
        });

        DONE = true;
        System.out.println("CB-S215 summary ok=" + ok + " fail=" + fail);
    }
}
