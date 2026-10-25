package cb.s5c;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.class_1113;
import net.minecraft.class_1132;
import net.minecraft.class_1145;
import net.minecraft.class_1146;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1304;
import net.minecraft.class_1308;
import net.minecraft.class_1321;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1836;
import net.minecraft.class_1934;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_318;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3730;
import net.minecraft.class_490;

/**
 * s5's client checks on a real client (see build.sh). A driver thread waits for the joined world, then runs each
 * phase on the client or integrated-server thread and judges what the mod's overrides recorded.
 */
public class S5cClient implements ClientModInitializer {
    static final Map<String, Boolean> OK = new LinkedHashMap<>();
    static final List<String> SOUNDS = new ArrayList<>();

    static void declare(String... names) {
        for (String n : names) {
            OK.put(n, null);
        }
    }

    static void check(String name, boolean pass, String detail) {
        if (OK.get(name) != null) {
            return;
        }
        OK.put(name, pass);
        log((pass ? "ok " : "FAIL ") + name + " " + detail);
    }

    static void log(String s) {
        System.out.println("CB-S5C " + s);
    }

    @Override
    public void onInitializeClient() {
        declare("join", "tick.hotbarSelected", "tick.hotbarSelectedServer", "tick.hotbarNotSelected", "tick.armorNotSelected",
            "tick.armorSelected", "tick.offhandNotSelected", "tick.offhandSelected", "tick.villagerNever",
            "tooltip.modLine", "tooltip.vanillaSword", "tooltip.advanced", "tooltip.hoverShot",
            "sound.modSword", "sound.ironSword", "sound.wolfArmor");
        Thread t = new Thread(S5cClient::drive, "cb-s5c");
        t.setDaemon(true);
        t.start();
    }

    static <T> T on(Executor ex, Supplier<T> s) throws Exception {
        return CompletableFuture.supplyAsync(s, ex).get(20, TimeUnit.SECONDS);
    }

    static void run(Executor ex, Runnable r) throws Exception {
        CompletableFuture.runAsync(r, ex).get(20, TimeUnit.SECONDS);
    }

    static List<S5cItems.Tick> window(long ms) throws InterruptedException {
        synchronized (S5cItems.TICKS) {
            S5cItems.TICKS.clear();
        }
        Thread.sleep(ms);
        synchronized (S5cItems.TICKS) {
            return new ArrayList<>(S5cItems.TICKS);
        }
    }

    static String summary(List<S5cItems.Tick> ticks) {
        Map<String, Integer> n = new LinkedHashMap<>();
        for (S5cItems.Tick t : ticks) {
            n.merge(t.toString(), 1, Integer::sum);
        }
        return n.toString();
    }

    static boolean has(List<S5cItems.Tick> ticks, boolean client, int slot, boolean selected) {
        for (S5cItems.Tick t : ticks) {
            if (t.client() == client && t.slot() == slot && t.selected() == selected && t.player()) {
                return true;
            }
        }
        return false;
    }

    /** only (slot, selected) on the client side for the player */
    static boolean onlyClient(List<S5cItems.Tick> ticks, int slot, boolean selected) {
        boolean any = false;
        for (S5cItems.Tick t : ticks) {
            if (t.client()) {
                if (t.slot() != slot || t.selected() != selected) {
                    return false;
                }
                any = true;
            }
        }
        return any;
    }

    static void drive() {
        try {
            class_310 mc = class_310.method_1551();
            long until = System.currentTimeMillis() + 240_000;
            while ((mc.field_1724 == null || mc.field_1687 == null || mc.method_1576() == null) && System.currentTimeMillis() < until) {
                Thread.sleep(500);
            }
            if (mc.field_1724 == null || mc.method_1576() == null) {
                check("join", false, "no world joined within 240 s");
                finish(mc);
                return;
            }
            Thread.sleep(4000);
            check("join", true, "player in world");
            class_1132 server = mc.method_1576();
            UUID id = mc.field_1724.method_5667();
            Supplier<class_3222> sp = () -> server.method_3760().method_14602(id);
            class_1792 sword = S5cItems.SWORD;

            // ---- 1. client-side inventoryTick
            run(server, () -> {
                class_3222 p = sp.get();
                p.method_7336(class_1934.field_9215);
                for (int i = 0; i < 9; i++) {
                    p.method_31548().method_5447(i, class_1799.field_8037);
                }
                p.method_31548().method_5447(3, new class_1799(sword));
            });
            run(mc, () -> mc.field_1724.method_31548().field_7545 = 3);
            Thread.sleep(1500);
            List<S5cItems.Tick> w = window(2000);
            check("tick.hotbarSelected", onlyClient(w, 3, true), "selected slot 3: " + summary(w));
            check("tick.hotbarSelectedServer", has(w, false, 3, true), "server side: " + summary(w));
            run(mc, () -> mc.field_1724.method_31548().field_7545 = 5);
            Thread.sleep(1000);
            w = window(2000);
            check("tick.hotbarNotSelected", onlyClient(w, 3, false), "selected slot 5: " + summary(w));

            // armor (HEAD, 1.21.4 index 3) and offhand (index 0); selected = index equals the selected hotbar slot
            run(server, () -> {
                class_3222 p = sp.get();
                p.method_31548().method_5447(3, class_1799.field_8037);
                p.method_5673(class_1304.field_6169, new class_1799(sword));
            });
            Thread.sleep(1500);
            w = window(2000);
            check("tick.armorNotSelected", onlyClient(w, 3, false), "head, selected 5: " + summary(w));
            run(mc, () -> mc.field_1724.method_31548().field_7545 = 3);
            Thread.sleep(1000);
            w = window(2000);
            check("tick.armorSelected", onlyClient(w, 3, true), "head, selected 3: " + summary(w));
            run(server, () -> {
                class_3222 p = sp.get();
                p.method_5673(class_1304.field_6169, class_1799.field_8037);
                p.method_5673(class_1304.field_6171, new class_1799(sword));
            });
            run(mc, () -> mc.field_1724.method_31548().field_7545 = 5);
            Thread.sleep(1500);
            w = window(2000);
            check("tick.offhandNotSelected", onlyClient(w, 0, false), "offhand, selected 5: " + summary(w));
            run(mc, () -> mc.field_1724.method_31548().field_7545 = 0);
            Thread.sleep(1000);
            w = window(2000);
            check("tick.offhandSelected", onlyClient(w, 0, true), "offhand, selected 0: " + summary(w));

            // a villager holding the sword: 1.21.4 ticked only the player's inventory
            run(server, () -> {
                class_3222 p = sp.get();
                p.method_5673(class_1304.field_6171, class_1799.field_8037);
                class_3218 lvl = (class_3218) p.method_37908();
                class_1297 v = class_1299.field_6077.method_5883(lvl, class_3730.field_16462);
                class_243 at = p.method_19538();
                v.method_5814(at.field_1352 + 2, at.field_1351, at.field_1350);
                ((class_1308) v).method_5977(true);
                ((class_1308) v).method_5673(class_1304.field_6173, new class_1799(sword));
                lvl.method_8649(v);
            });
            w = window(3000);
            boolean nonPlayer = false;
            for (S5cItems.Tick t : w) {
                nonPlayer |= !t.player();
            }
            check("tick.villagerNever", w.isEmpty() || !nonPlayer, "ticks with only the villager holding it: " + summary(w));

            // ---- 2. tooltips
            List<String> mod = on(mc, () -> lines(mc, new class_1799(sword), class_1836.field_41070));
            int idx = mod.indexOf("cb-s5 tooltip");
            int count = 0, mods = -1;
            for (int i = 0; i < mod.size(); i++) {
                if (mod.get(i).equals("cb-s5 tooltip")) {
                    count++;
                }
                if (mods < 0 && mod.get(i).contains("When in Main Hand")) {
                    mods = i;
                }
            }
            check("tooltip.modLine", count == 1 && idx >= 1 && (mods < 0 || idx < mods), "lines " + mod);
            List<String> van = on(mc, () -> lines(mc, new class_1799(class_1802.field_8802), class_1836.field_41070));
            boolean vanOk = !van.isEmpty() && van.get(0).contains("Diamond Sword") && !van.contains("cb-s5 tooltip");
            for (String l : van) {
                vanOk &= van.indexOf(l) == van.lastIndexOf(l) || l.isEmpty();
            }
            check("tooltip.vanillaSword", vanOk, "lines " + van);
            List<String> adv = on(mc, () -> lines(mc, new class_1799(sword), class_1836.field_41071));
            int ai = adv.indexOf("cb-s5 tooltip");
            int dur = -1, idl = -1;
            for (int i = 0; i < adv.size(); i++) {
                if (adv.get(i).startsWith("Durability")) {
                    dur = i;
                }
                if (adv.get(i).equals("cb_s5c:sword")) {
                    idl = i;
                }
            }
            check("tooltip.advanced", ai >= 1 && dur > ai && idl > ai && adv.indexOf("cb-s5 tooltip") == adv.lastIndexOf("cb-s5 tooltip"),
                "lines " + adv);

            // hover screenshot: the sword in hotbar slot 3 under the pointer in the inventory screen
            run(server, () -> sp.get().method_31548().method_5447(3, new class_1799(sword)));
            Thread.sleep(800);
            run(mc, () -> mc.method_1507(new class_490(mc.field_1724)));
            Thread.sleep(1500);
            int[] px = on(mc, () -> {
                double s = mc.method_22683().method_4495();
                int left = (mc.field_1755.field_22789 - 176) / 2, top = (mc.field_1755.field_22790 - 166) / 2;
                return new int[] {(int) ((left + 8 + 3 * 18 + 8) * s), (int) ((top + 142 + 8) * s)};
            });
            log("input ready: move " + px[0] + " " + px[1] + "; sleep 400; move " + (px[0] + 2) + " " + (px[1] + 1));
            Thread.sleep(4000);
            run(mc, () -> class_318.method_22690(mc.field_1697, "cb-s5c-tooltip.png", mc.method_1522(), c -> { }));
            check("tooltip.hoverShot", true, "screenshot cb-s5c-tooltip.png (judge by eye)");
            run(mc, () -> mc.method_1507(null));
            Thread.sleep(800);

            // ---- 3. break sounds, heard by the client's sound manager
            class_1145 listener = new class_1145() {
                @Override
                public void method_4884(class_1113 sound, class_1146 events, float range) {
                    synchronized (SOUNDS) {
                        SOUNDS.add(String.valueOf(sound.method_4775()));
                    }
                }
            };
            run(mc, () -> mc.method_1483().method_4878(listener));
            check("sound.modSword", breakAndHear(mc, server, sp, sword, "minecraft:block.anvil.land", "minecraft:entity.item.break"),
                "heard " + last() + ", getBreakingSound asked " + S5cItems.breakingSoundAsked);
            check("sound.ironSword", breakAndHear(mc, server, sp, class_1802.field_8371, "minecraft:entity.item.break", "minecraft:block.anvil.land"),
                "heard " + last());
            synchronized (SOUNDS) {
                SOUNDS.clear();
            }
            run(server, () -> {
                class_3222 p = sp.get();
                class_3218 lvl = (class_3218) p.method_37908();
                class_1297 e = class_1299.field_6055.method_5883(lvl, class_3730.field_16462);
                class_243 at = p.method_19538();
                e.method_5814(at.field_1352 - 1.5, at.field_1351, at.field_1350);
                class_1321 wolf = (class_1321) e;
                wolf.method_5977(true);
                wolf.method_6170(p);
                wolf.method_56678(new class_1799(class_1802.field_47831));
                lvl.method_8649(e);
            });
            Thread.sleep(1500);
            run(server, () -> {
                class_3222 p = sp.get();
                class_3218 lvl = (class_3218) p.method_37908();
                for (class_1297 e : lvl.method_27909()) {
                    if (e.method_5864() == class_1299.field_6055) {
                        class_1799 armor = ((class_1308) e).method_6118(class_1304.field_48824);
                        armor.method_7970(armor.method_7936() + 1, (class_1308) e, class_1304.field_48824);
                    }
                }
            });
            Thread.sleep(2000);
            boolean wolfHeard;
            synchronized (SOUNDS) {
                wolfHeard = SOUNDS.contains("minecraft:item.wolf_armor.break");
            }
            check("sound.wolfArmor", wolfHeard, "heard " + last());
            run(mc, () -> mc.method_1483().method_4866(listener));
        } catch (Throwable t) {
            log("FAIL driver " + t);
            t.printStackTrace();
        }
        finish(class_310.method_1551());
    }

    static String last() {
        synchronized (SOUNDS) {
            return SOUNDS.toString();
        }
    }

    static boolean breakAndHear(class_310 mc, class_1132 server, Supplier<class_3222> sp, class_1792 item, String want, String not)
        throws Exception {
        synchronized (SOUNDS) {
            SOUNDS.clear();
        }
        run(server, () -> sp.get().method_31548().method_5447(3, new class_1799(item)));
        run(mc, () -> mc.field_1724.method_31548().field_7545 = 3);
        Thread.sleep(1500);
        run(server, () -> {
            class_3222 p = sp.get();
            class_1799 st = p.method_6047();
            st.method_7970(st.method_7936() + 1, p, class_1304.field_6173);
        });
        Thread.sleep(2000);
        synchronized (SOUNDS) {
            return SOUNDS.contains(want) && !SOUNDS.contains(not);
        }
    }

    static List<String> lines(class_310 mc, class_1799 stack, class_1836 flag) {
        List<String> out = new ArrayList<>();
        for (class_2561 c : stack.method_7950(class_1792.class_9635.method_59528(mc.field_1687), mc.field_1724, flag)) {
            out.add(c.getString());
        }
        return out;
    }

    static void finish(class_310 mc) {
        int ok = 0, fail = 0;
        for (Map.Entry<String, Boolean> e : OK.entrySet()) {
            if (e.getValue() == null) {
                log("FAIL " + e.getKey() + " never ran");
                fail++;
            } else if (e.getValue()) {
                ok++;
            } else {
                fail++;
            }
        }
        log("summary ok=" + ok + " fail=" + fail);
        log(ok + "/" + (ok + fail));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
        mc.execute(mc::method_1592);
    }
}
