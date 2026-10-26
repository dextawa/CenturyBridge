package cb.smx4c;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.class_310;
import net.minecraft.class_318;
import net.minecraft.class_526;

/**
 * smx4's 1.21.3-side driver (see ../build.sh): once a world is joined, opens the test screen, calls the 1.21.3 scroll
 * members, asks for real input (wheel, scroll bar drag, clicks on the buttons and the edit box), judges, then raises
 * Counters.done for the native driver, which stops the client.
 */
public class Driver implements ClientModInitializer {
    static final Map<String, Boolean> OK = new LinkedHashMap<>();

    static void check(String name, boolean pass, String detail) {
        OK.put(name, pass);
        System.out.println("CB-SMX4 " + (pass ? "ok " : "FAIL ") + name + " " + detail);
    }

    @Override
    public void onInitializeClient() {
        Thread t = new Thread(Driver::drive, "cb-smx4c");
        t.setDaemon(true);
        t.start();
    }

    static <T> T on(class_310 mc, Supplier<T> s) throws Exception {
        return CompletableFuture.supplyAsync(s, mc).get(20, TimeUnit.SECONDS);
    }

    static int px(class_310 mc, double gui) throws Exception {
        return on(mc, () -> (int) Math.round(gui * mc.method_22683().method_4495()));
    }

    static void drive() {
        class_310 mc = class_310.method_1551();
        try {
            long until = System.currentTimeMillis() + 240_000;
            while ((mc.field_1724 == null || mc.field_1687 == null) && System.currentTimeMillis() < until) {
                Thread.sleep(500);
            }
            if (mc.field_1724 == null) {
                check("join", false, "no world joined within 240 s");
                return;
            }
            Thread.sleep(6000);
            TestScreen s = on(mc, () -> {
                TestScreen ts = new TestScreen();
                mc.method_1507(ts);
                return ts;
            });
            Thread.sleep(1500);

            // 6a: the scroll members meet the overrides
            String probe = on(mc, () -> s.list.probe());
            String[] p = probe.split(" ");
            int width = Integer.parseInt(p[4]);
            check("6a.maxScroll", p[0].equals("40"), "getMaxScroll() " + p[0]);
            check("6a.clampAsksOverride", Double.parseDouble(p[1]) == 40.0, "setScrollAmount(1000) -> getScrollAmount() " + p[1]);
            check("6a.scrollbarPosition", Integer.parseInt(p[3]) == width - 10, "getScrollbarPosition() " + p[3] + ", width " + width
                + ", scrollbarVisible " + p[2]);
            check("6a.scrollAmountOverrideCalled", Counters.SCROLL_AMOUNT_OVERRIDE.get() > 0, "override calls " + Counters.SCROLL_AMOUNT_OVERRIDE.get());

            // 6b: wheel, then drag the scroll bar (at width - 10) far down: never past 40
            on(mc, () -> {
                s.list.scrollTo(0);
                return null;
            });
            int lx = px(mc, 100), ly = px(mc, 90);
            StringBuilder wheel = new StringBuilder("move " + lx + " " + ly);
            for (int i = 0; i < 15; i++) {
                wheel.append("; click 5; sleep 80");
            }
            System.out.println("CB-SMX4 input ready: " + wheel);
            Thread.sleep(6000);
            double afterWheel = on(mc, () -> s.list.amount());
            check("6b.wheel", afterWheel > 0 && afterWheel <= 40, "scroll amount after 15 wheel notches " + afterWheel);
            on(mc, () -> {
                s.list.scrollTo(0);
                return null;
            });
            int bx = px(mc, width - 7), by0 = px(mc, 36), by1 = px(mc, 340);
            System.out.println("CB-SMX4 input ready: move " + bx + " " + by0 + "; down 1; drag " + bx + " " + by1 + " 12; up 1");
            Thread.sleep(6000);
            double afterDrag = on(mc, () -> s.list.amount());
            check("6b.drag", afterDrag > 0 && afterDrag <= 40, "scroll amount after dragging the bar down " + afterDrag);
            on(mc, () -> {
                class_318.method_22690(mc.field_1697, "cb-smx4-screen.png", mc.method_1522(), c -> { });
                return null;
            });

            // 6 (text area): innerPadding 10 asked through 1.21.4's names
            String ans = s.area.answers;
            check("6.textArea", ans.startsWith("10 20 "), "innerPadding totalInnerPadding withinContentAreaTopBottom(in) (above): " + ans
                + " (screenshot cb-smx4-screen.png: yellow text 10 px in)");

            // 7: clicks on A (clicked() false), B, the edit box
            int ax = px(mc, 250), ay = px(mc, 140), cx = px(mc, 330), ex = px(mc, 450);
            System.out.println("CB-SMX4 input ready: move " + ax + " " + ay + "; click 1; sleep 300; move " + cx + " " + ay
                + "; click 1; sleep 300; move " + ex + " " + ay + "; click 1");
            Thread.sleep(6000);
            boolean focused = on(mc, () -> s.edit.method_25370());
            check("7c.blockedButton", s.pressedA.get() == 0, "button A presses " + s.pressedA.get());
            check("7a.otherButton", s.pressedB.get() == 1, "button B presses " + s.pressedB.get());
            check("7a.editBoxFocus", focused, "edit box focused after its click " + focused);

            // 6c: the 1.21.3 getMaxScroll injection on vanilla's reads, also for a vanilla list
            Integer testCalls = Counters.MAX_CALLS.get(TestList.class.getName());
            check("6c.testList", testCalls != null && testCalls > 1 && Integer.valueOf(40).equals(Counters.MAX_VALUE.get(TestList.class.getName())),
                "calls " + testCalls + ", last value " + Counters.MAX_VALUE.get(TestList.class.getName()));
            on(mc, () -> {
                mc.method_1507(new class_526(null));
                return null;
            });
            Thread.sleep(3000);
            check("6c.vanillaList", Counters.MAX_CALLS.containsKey("net.minecraft.class_528"),
                "calls by list class " + Counters.MAX_CALLS + ", last values " + Counters.MAX_VALUE);
            on(mc, () -> {
                mc.method_1507(null);
                return null;
            });
        } catch (Throwable t) {
            System.out.println("CB-SMX4 FAIL driver " + t);
            t.printStackTrace();
        } finally {
            int ok = 0;
            for (boolean b : OK.values()) {
                ok += b ? 1 : 0;
            }
            System.out.println("CB-SMX4 climbed summary ok=" + ok + " fail=" + (OK.size() - ok));
            Counters.done = true;
        }
    }
}
