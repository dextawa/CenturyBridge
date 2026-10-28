package cb.s9rn;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.class_11677;
import net.minecraft.class_11909;
import net.minecraft.class_11910;
import net.minecraft.class_310;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_437;
import net.minecraft.class_5599;
import net.minecraft.class_5601;
import net.minecraft.class_5602;
import net.minecraft.class_630;
import org.joml.Vector3f;

/**
 * The native 1.21.9 driver of the s9 route checks (see ../build.sh). Route A (selection list): with the climbed list on
 * screen, the native entriesCanBeSelected() modifier still runs in its renderItem (A.native), the climbed
 * isSelectedItem override is asked for every row (A.converted) and the native renderSelection head sees exactly the
 * row it answered true for (A.box). Route B (widget click): vanilla's Screen.mouseClicked reaches both buttons; the
 * native AbstractWidget.mouseClicked head runs for each (B.native), the climbed 1.21.8 mouseClicked mixin runs for
 * each (B.converted) and its answer holds: "keep" pressed once, "blocked" never (B.answer). Armor layers (C): the
 * climbed mod bakes 1.21.8's inner / outer armor layers (cb.s9r.ArmorCheck); each part must hold exactly the vertices
 * of the 1.21.9 slot layer that carries it at that deformation (outer: head = helmet, body and arms = chestplate, legs
 * = boots; inner: body and legs = leggings), and the inner head and arms must be there and smaller than the outer.
 */
public class Driver implements ClientModInitializer {
    public static final AtomicInteger NATIVE_SELECTABLE = new AtomicInteger();
    public static final Set<Integer> BOXED = ConcurrentHashMap.newKeySet();
    public static final Map<String, Integer> NATIVE_CLICKS = new ConcurrentHashMap<>();
    static final Map<String, Boolean> OK = new LinkedHashMap<>();

    public static boolean isTestList(Object list) {
        return list.getClass().getName().equals("cb.s9r.TestList");
    }

    static void check(String name, boolean pass, String detail) {
        OK.put(name, pass);
        System.out.println("CB-S9R " + (pass ? "ok " : "FAIL ") + name + " " + detail);
    }

    @Override
    public void onInitializeClient() {
        Thread t = new Thread(Driver::drive, "cb-s9rn");
        t.setDaemon(true);
        t.start();
    }

    static int counter(Class<?> c, String field) throws ReflectiveOperationException {
        return ((AtomicInteger) c.getField(field).get(null)).get();
    }

    @SuppressWarnings("unchecked")
    static Map<String, Set<Vector3f>> parts(class_5599 models, class_5601 layer) throws ReflectiveOperationException {
        return (Map<String, Set<Vector3f>>) Class.forName("cb.s9r.ArmorCheck").getMethod("parts", class_630.class)
            .invoke(null, models.method_32072(layer));
    }

    static float width(Set<Vector3f> v) {
        float lo = Float.MAX_VALUE, hi = -Float.MAX_VALUE;
        for (Vector3f p : v) {
            lo = Math.min(lo, p.x);
            hi = Math.max(hi, p.x);
        }
        return hi - lo;
    }

    @SuppressWarnings("unchecked")
    static void checkArmor(class_5599 models) throws ReflectiveOperationException {
        Map<String, Map<String, Set<Vector3f>>[]> baked =
            (Map<String, Map<String, Set<Vector3f>>[]>) Class.forName("cb.s9r.ArmorCheck").getMethod("bake").invoke(null);
        Map<String, class_11677<class_5601>> sets = new LinkedHashMap<>();
        sets.put("player", class_5602.field_61701);
        sets.put("zombie_baby", class_5602.field_61713);
        sets.put("armor_stand", class_5602.field_61719);
        for (Map.Entry<String, class_11677<class_5601>> e : sets.entrySet()) {
            Map<String, Set<Vector3f>> inner = baked.get(e.getKey())[0], outer = baked.get(e.getKey())[1];
            class_11677<class_5601> s = e.getValue();
            Map<String, Set<Vector3f>> helmet = parts(models, s.comp_4542()), chest = parts(models, s.comp_4543()),
                legs = parts(models, s.comp_4544()), boots = parts(models, s.comp_4545());
            boolean outerOk = !outer.get("head").isEmpty() && outer.get("head").equals(helmet.get("head"))
                && !outer.get("body").isEmpty() && outer.get("body").equals(chest.get("body"))
                && outer.get("right_arm").equals(chest.get("right_arm")) && outer.get("left_arm").equals(chest.get("left_arm"))
                && !outer.get("right_leg").isEmpty() && outer.get("right_leg").equals(boots.get("right_leg"))
                && outer.get("left_leg").equals(boots.get("left_leg"));
            check("C.outer." + e.getKey(), outerOk, "outer_armor parts = helmet head / chestplate body+arms / boots legs; vertex counts "
                + counts(outer));
            boolean innerOk = !inner.get("body").isEmpty() && inner.get("body").equals(legs.get("body"))
                && !inner.get("right_leg").isEmpty() && inner.get("right_leg").equals(legs.get("right_leg"))
                && inner.get("left_leg").equals(legs.get("left_leg"))
                && !inner.get("head").isEmpty() && !inner.get("right_arm").isEmpty()
                && width(inner.get("head")) < width(outer.get("head")) && width(inner.get("right_arm")) < width(outer.get("right_arm"));
            check("C.inner." + e.getKey(), innerOk, "inner_armor body+legs = leggings, head/arms present and narrower than outer ("
                + width(inner.get("head")) + " < " + width(outer.get("head")) + "); vertex counts " + counts(inner));
        }
    }

    static String counts(Map<String, Set<Vector3f>> parts) {
        StringBuilder b = new StringBuilder();
        parts.forEach((k, v) -> b.append(k).append('=').append(v.size()).append(' '));
        return b.toString().trim();
    }

    static void drive() {
        class_310 mc = class_310.method_1551();
        try {
            long until = System.currentTimeMillis() + 240_000;
            while (mc.field_1755 == null && System.currentTimeMillis() < until) {
                Thread.sleep(500);
            }
            Thread.sleep(3000);
            Class<?> counters = Class.forName("cb.s9r.Counters");
            class_437 screen = (class_437) Class.forName("cb.s9r.LegacyScreen").getMethod("create").invoke(null);
            CompletableFuture.runAsync(() -> mc.method_1507(screen), mc).get(20, TimeUnit.SECONDS);
            Thread.sleep(3000);

            // route A
            int nativeSel = NATIVE_SELECTABLE.get(), asked = counter(counters, "IS_SELECTED_CALLS");
            Object rows = counters.getField("ASKED").get(null);
            check("A.native", nativeSel > 0, "native entriesCanBeSelected() modifier ran " + nativeSel + "x in the climbed list's renderItem");
            check("A.converted", asked > 0 && ((Set<?>) rows).size() == 4, "the 1.21.8 isSelectedItem override was asked " + asked + "x, rows " + rows);
            check("A.box", BOXED.equals(Set.of(2)), "native renderSelection head saw rows " + BOXED + " (the override answers true for row 2 only)");

            // route B: click both buttons through vanilla's Screen.mouseClicked
            int mixin0 = counter(counters, "CLICK_MIXIN_CALLS");
            List<String> clicked = CompletableFuture.supplyAsync(() -> {
                List<String> out = new ArrayList<>();
                for (class_364 child : new ArrayList<>(screen.method_25396())) {
                    if (child instanceof class_339 w) {
                        String label = w.method_25369().getString();
                        if (label.equals("keep") || label.equals("blocked")) {
                            double x = w.method_46426() + w.method_25368() / 2.0;
                            double y = w.method_46427() + w.method_25364() / 2.0;
                            screen.method_25402(new class_11909(x, y, new class_11910(0, 0)), false);
                            out.add(label);
                        }
                    }
                }
                return out;
            }, mc).get(20, TimeUnit.SECONDS);
            int mixinCalls = counter(counters, "CLICK_MIXIN_CALLS") - mixin0;
            check("B.native", NATIVE_CLICKS.getOrDefault("keep", 0) == 1 && NATIVE_CLICKS.getOrDefault("blocked", 0) == 1,
                "clicked " + clicked + "; native AbstractWidget.mouseClicked(event) head ran " + NATIVE_CLICKS);
            check("B.converted", mixinCalls == 2, "the 1.21.8 mouseClicked(x, y, button) mixin ran " + mixinCalls + "x for the 2 clicks");
            int keep = counter(counters, "KEEP_PRESSED"), blocked = counter(counters, "BLOCKED_PRESSED");
            check("B.answer", keep == 1 && blocked == 0,
                "keep pressed " + keep + "x, blocked pressed " + blocked + "x (the 1.21.8 mixin answers false for blocked)");

            // armor layers
            checkArmor(mc.method_31974());
        } catch (Throwable t) {
            check("driver", false, String.valueOf(t));
            t.printStackTrace();
        }
        int ok = 0;
        for (boolean b : OK.values()) {
            ok += b ? 1 : 0;
        }
        System.out.println("CB-S9R " + ok + "/" + OK.size());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
        mc.execute(mc::method_1592);
    }
}
