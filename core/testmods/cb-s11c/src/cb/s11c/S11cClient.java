package cb.s11c;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.class_11908;
import net.minecraft.class_1132;
import net.minecraft.class_1928;
import net.minecraft.class_2350;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_318;
import net.minecraft.class_339;
import net.minecraft.class_342;
import net.minecraft.class_350;
import net.minecraft.class_4069;
import net.minecraft.class_4185;
import net.minecraft.class_5235;
import net.minecraft.class_5481;
import net.minecraft.class_5676;

/**
 * s11's edit-game-rules checks (D:/tmp/main/client_checks/s11.md, part 1-5) as a 1.21.10-era mod (see build.sh): once a
 * world is joined the driver opens EditGameRulesScreen on a copy of the world's rules, reads every row (category header,
 * row class, label, widget), edits the mod rows through their widgets, presses Done, applies the result to the world and
 * reads the rules back through 1.21.10's API. Prints "CB-S11C ok|FAIL <check> <detail>", "CB-S11C N/T", then stops.
 */
public class S11cClient implements ClientModInitializer {
    static final Map<String, Boolean> OK = new LinkedHashMap<>();
    static final class_11908 ENTER = new class_11908(257, 0, 0);

    record Row(String category, Object entry, String label) {
    }

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
        System.out.println("CB-S11C " + s);
    }

    @Override
    public void onInitializeClient() {
        declare("join", "rules.initial", "screen.opened", "vanilla.rows", "bool.row", "int.row", "int.invalid200", "int.valid42",
            "double.row", "double.invalid", "double.valid", "enum.row", "enum.cycle", "custom.category", "sub.rows",
            "sub.writeThrough", "done.applied", "world.reads", "world.command", "screen.shot");
        Thread t = new Thread(S11cClient::drive, "cb-s11c");
        t.setDaemon(true);
        t.start();
    }

    static <T> T on(Executor ex, Supplier<T> s) throws Exception {
        return CompletableFuture.supplyAsync(s, ex).get(20, TimeUnit.SECONDS);
    }

    static void run(Executor ex, Runnable r) throws Exception {
        CompletableFuture.runAsync(r, ex).get(20, TimeUnit.SECONDS);
    }

    static String text(class_5481 seq) {
        StringBuilder sb = new StringBuilder();
        seq.accept((i, style, cp) -> {
            sb.appendCodePoint(cp);
            return true;
        });
        return sb.toString();
    }

    /** the first field of the given type declared on the class or a superclass, made accessible */
    static Object fieldOf(Object o, Class<?> type, java.util.function.Predicate<Object> accept) throws Exception {
        for (Class<?> c = o.getClass(); c != null; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                if (type.isAssignableFrom(f.getType()) && !java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                    f.setAccessible(true);
                    Object v = f.get(o);
                    if (v != null && accept.test(v)) {
                        return v;
                    }
                }
            }
        }
        return null;
    }

    static String label(Object entry) throws Exception {
        Object lines = fieldOf(entry, List.class, v -> !((List<?>) v).isEmpty() && ((List<?>) v).get(0) instanceof class_5481);
        if (lines != null) {
            StringBuilder sb = new StringBuilder();
            for (Object l : (List<?>) lines) {
                sb.append(text((class_5481) l));
            }
            return sb.toString();
        }
        Object c = fieldOf(entry, class_2561.class, v -> true);
        return c == null ? "?" : ((class_2561) c).getString();
    }

    static <W> W widget(Object entry, Class<W> type) {
        for (Object w : ((class_4069) entry).method_25396()) {
            if (type.isInstance(w)) {
                return type.cast(w);
            }
        }
        return null;
    }

    static Row find(List<Row> rows, String label) {
        for (Row r : rows) {
            if (r.label().equals(label)) {
                return r;
            }
        }
        return null;
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

            String init = on(server, () -> {
                class_1928 r = server.method_3767();
                return r.method_8355(S11cRules.BOOL) + " " + r.method_8356(S11cRules.INT) + " " + r.method_20746(S11cRules.DOUBLE).get() + " "
                    + r.method_20746(S11cRules.ENUM).get() + " " + r.method_8355(S11cRules.CUSTOM);
            });
            check("rules.initial", init.equals("true 7 0.5 " + class_2350.field_11043 + " false"), "bool int double enum custom = " + init);

            // ---- open the screen on a copy of the world's rules
            class_1928 copy = on(server, () -> server.method_3767().method_27325(server.method_27728().method_45560()));
            AtomicReference<Optional<class_1928>> result = new AtomicReference<>();
            AtomicInteger results = new AtomicInteger();
            class_5235 screen = on(mc, () -> new class_5235(copy, o -> {
                result.set(o);
                results.incrementAndGet();
            }));
            run(mc, () -> mc.method_1507(screen));
            Thread.sleep(2500);
            check("screen.opened", mc.field_1755 == screen, "current screen " + mc.field_1755);

            class_350<?> list = (class_350<?>) fieldOf(screen, class_350.class, v -> true);
            class_4185 done = (class_4185) fieldOf(screen, class_4185.class, v -> true);
            List<Row> rows = on(mc, () -> {
                List<Row> out = new ArrayList<>();
                String cat = "";
                try {
                    for (Object e : list.method_25396()) {
                        if (e instanceof class_5235.class_5237) {
                            cat = label(e);
                        } else {
                            out.add(new Row(cat, e, label(e)));
                        }
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                return out;
            });
            Set<String> ours = Set.of("CB Bool Rule", "CB Int Rule", "CB Double Rule", "CB Enum Rule", "CB Custom Rule", "CB Sub Bool",
                "CB Sub Int", "CB Sub Bool Row", "CB Sub Int Row");
            int vanilla = 0;
            StringBuilder vanillaHash = new StringBuilder();
            for (Row r : rows) {
                log("row " + r.category() + " | " + r.entry().getClass().getName() + " | " + r.label());
                if (!ours.contains(r.label())) {
                    vanilla++;
                    vanillaHash.append(r.category()).append('/').append(r.label()).append(';');
                }
            }
            int expected = expectedVanilla(server);
            check("vanilla.rows", vanilla == expected, vanilla + " vanilla rows, " + expected + " vanilla rules in the running game, hash "
                + Integer.toHexString(vanillaHash.toString().hashCode()));

            String player = class_2561.method_43471("gamerule.category.player").getString();
            String mobs = class_2561.method_43471("gamerule.category.mobs").getString();
            String misc = class_2561.method_43471("gamerule.category.misc").getString();

            Row b = find(rows, "CB Bool Rule");
            class_5676<?> bc = b == null ? null : widget(b.entry(), class_5676.class);
            check("bool.row", b != null && b.category().equals(player) && b.entry() instanceof class_5235.class_5236 && bc != null
                && Boolean.TRUE.equals(bc.method_32603()), b == null ? "no row" : "under '" + b.category() + "', " + b.entry().getClass().getName()
                + ", checkbox " + (bc == null ? "none" : bc.method_32603()));

            Row in = find(rows, "CB Int Rule");
            class_342 ie = in == null ? null : widget(in.entry(), class_342.class);
            check("int.row", in != null && in.category().equals(mobs) && in.entry() instanceof class_5235.class_5239 && ie != null
                && "7".equals(ie.method_1882()), in == null ? "no row" : "under '" + in.category() + "', " + in.entry().getClass().getName()
                + ", box " + (ie == null ? "none" : ie.method_1882()));
            if (ie != null) {
                run(mc, () -> ie.method_1852("200"));
                Thread.sleep(300);
                boolean a200 = done.field_22763;
                check("int.invalid200", !a200, "Done active after 200: " + a200);
                run(mc, () -> ie.method_1852("42"));
                Thread.sleep(300);
                check("int.valid42", done.field_22763, "Done active after 42: " + done.field_22763);
            }

            Row d = find(rows, "CB Double Rule");
            class_342 de = d == null ? null : widget(d.entry(), class_342.class);
            check("double.row", d != null && d.category().equals(misc) && de != null && "0.5".equals(de.method_1882()),
                d == null ? "no row" : "under '" + d.category() + "', " + d.entry().getClass().getName() + ", box " + (de == null ? "none" : de.method_1882()));
            if (de != null) {
                run(mc, () -> de.method_1852("1.5"));
                Thread.sleep(300);
                boolean a15 = done.field_22763;
                check("double.invalid", !a15, "Done active after 1.5: " + a15);
                run(mc, () -> de.method_1852("0.25"));
                Thread.sleep(300);
                check("double.valid", done.field_22763, "Done active after 0.25: " + done.field_22763);
            }

            // Fabric's enum row is a plain Button that cycles the screen's rule copy (EnumRuleWidget.buttonWidget)
            Row en = find(rows, "CB Enum Rule");
            class_4185 ec = en == null ? null : widget(en.entry(), class_4185.class);
            Supplier<String> enumNow = () -> String.valueOf(copy.method_20746(S11cRules.ENUM).get());
            String enumStart = on(mc, enumNow);
            check("enum.row", en != null && en.category().equals(misc) && ec != null && enumStart.equals(String.valueOf(class_2350.field_11043)),
                en == null ? "no row" : "under '" + en.category() + "', " + en.entry().getClass().getName() + ", button "
                    + (ec == null ? "none" : ec.method_25369().getString()) + ", rule " + enumStart);
            if (ec != null) {
                List<String> seen = on(mc, () -> {
                    List<String> s = new ArrayList<>();
                    for (int i = 0; i < 6; i++) {
                        ec.method_25306(ENTER);
                        s.add(enumNow.get());
                    }
                    for (int i = 0; i < 6 && !enumNow.get().equals(String.valueOf(class_2350.field_11034)); i++) {
                        ec.method_25306(ENTER);
                    }
                    return s;
                });
                String now = on(mc, enumNow);
                check("enum.cycle", new HashSet<>(seen).size() == 6 && now.equals(String.valueOf(class_2350.field_11034)),
                    "six presses gave " + seen + ", now " + now + " (button '" + ec.method_25369().getString() + "')");
            }

            Row cu = find(rows, "CB Custom Rule");
            check("custom.category", cu != null && cu.category().equals("CB Custom Category"), cu == null ? "no row" : "under '" + cu.category() + "'");

            Row sb = find(rows, "CB Sub Bool Row"), si = find(rows, "CB Sub Int Row");
            check("sub.rows", sb != null && si != null && sb.entry() instanceof SubRows.SubBool && si.entry() instanceof SubRows.SubInt,
                "subclass rows: " + (sb == null ? "no bool row" : sb.entry().getClass().getName()) + ", " + (si == null ? "no int row" : si.entry().getClass().getName()));
            if (sb != null && si != null) {
                class_5676<?> sbc = widget(sb.entry(), class_5676.class);
                class_342 sie = widget(si.entry(), class_342.class);
                String w = on(mc, () -> {
                    if (sbc != null) {
                        sbc.method_25306(ENTER);
                    }
                    if (sie != null) {
                        sie.method_1852("9");
                    }
                    return copy.method_8355(S11cRules.SUB_BOOL) + " " + copy.method_8356(S11cRules.SUB_INT);
                });
                check("sub.writeThrough", w.equals("true 9"), "after toggling and typing 9, the screen's rules read " + w
                    + " (checkbox " + (sbc == null ? "none" : "ok") + ", box " + (sie == null ? "none" : "ok") + ")");
            }

            // ---- screenshots, then the mod's own boolean off and Done
            run(mc, () -> class_318.method_22690(mc.field_1697, "cb-s11c-rules-top.png", mc.method_1522(), 1, c -> { }));
            Thread.sleep(800);
            run(mc, () -> list.method_44382(1e9));
            Thread.sleep(800);
            run(mc, () -> class_318.method_22690(mc.field_1697, "cb-s11c-rules-end.png", mc.method_1522(), 1, c -> { }));
            Thread.sleep(800);
            check("screen.shot", true, "screenshots cb-s11c-rules-top.png / cb-s11c-rules-end.png (judge by eye)");
            if (bc != null) {
                run(mc, () -> bc.method_25306(ENTER));
            }
            run(mc, () -> done.method_25306(ENTER));
            Thread.sleep(1000);
            Optional<class_1928> res = result.get();
            check("done.applied", results.get() == 1 && res != null && res.isPresent(), "consumer called " + results.get() + "x with " + res);
            if (res != null && res.isPresent()) {
                String reads = on(server, () -> {
                    class_1928 r = server.method_3767();
                    r.method_27322(res.get(), server);
                    return r.method_8355(S11cRules.BOOL) + " " + r.method_8356(S11cRules.INT) + " " + r.method_20746(S11cRules.DOUBLE).get() + " "
                        + r.method_20746(S11cRules.ENUM).get() + " " + r.method_8355(S11cRules.SUB_BOOL) + " " + r.method_8356(S11cRules.SUB_INT);
                });
                check("world.reads", reads.equals("false 42 0.25 " + class_2350.field_11034 + " true 9"),
                    "bool int double enum subBool subInt = " + reads);
                String cmd = on(server, () -> {
                    try {
                        server.method_3734().method_44252(server.method_3739(), "gamerule cbBoolRule");
                        server.method_3734().method_44252(server.method_3739(), "gamerule cbIntRule");
                        return "ran";
                    } catch (Throwable e) {
                        return e.toString();
                    }
                });
                check("world.command", cmd.equals("ran"), "/gamerule cbBoolRule, /gamerule cbIntRule: " + cmd
                    + " (the answers are the 'Gamerule ... is currently set to' lines in the log)");
            }
            run(mc, () -> mc.method_1507(null));
        } catch (Throwable t) {
            log("FAIL driver " + t);
            t.printStackTrace();
        }
        finish(class_310.method_1551());
    }

    /**
     * the vanilla rules the running game has: 1.21.11's game rule registry (BuiltInRegistries.GAME_RULE, read reflectively
     * because a 1.21.10 mod has no such API), else 1.21.10's GameRules visitor
     */
    static int expectedVanilla(class_1132 server) {
        try {
            Object reg = Class.forName("net.minecraft.class_7923").getField("field_64251").get(null);
            int n = 0;
            for (Object id : (Set<?>) reg.getClass().getMethod("method_10235").invoke(reg)) {
                if (id.toString().startsWith("minecraft:")) {
                    n++;
                }
            }
            log("vanilla rules from BuiltInRegistries.GAME_RULE: " + n);
            return n;
        } catch (Throwable e) {
            AtomicInteger n = new AtomicInteger();
            server.method_3767().method_20744(new class_1928.class_4311() {
                @Override
                public <T extends class_1928.class_4315<T>> void method_20762(class_1928.class_4313<T> key, class_1928.class_4314<T> type) {
                    if (!key.method_20771().startsWith("cb")) {
                        n.incrementAndGet();
                    }
                }
            });
            log("vanilla rules from the GameRules visitor: " + n.get() + " (no GAME_RULE registry: " + e + ")");
            return n.get();
        }
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
