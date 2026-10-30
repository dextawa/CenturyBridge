package cb.s9rn;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import net.minecraft.class_1058;
import net.minecraft.class_10725;
import net.minecraft.class_11697;
import net.minecraft.class_20;
import net.minecraft.class_21;
import net.minecraft.class_2960;
import net.minecraft.class_8689;
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
 * WardenModel (G): the climbed 1.21.8 mod builds a WardenModel; its constructor RETURN mixin must see the four part
 * lists 1.21.8 filled in the constructor, and the getters must answer those same lists. DebugRenderer (H): the
 * climbed mod's Minecraft.debugRenderer is LevelRenderer's; its switchRenderChunkborder / toggleRenderOctree flip
 * 1.21.9's debug entries and answer them; clear() returns.
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

    static String name(Object sprite) {
        return sprite instanceof class_1058 s ? String.valueOf(s.method_45851().method_45816()) : String.valueOf(sprite);
    }

    @SuppressWarnings("unchecked")
    static void checkAtlases(class_310 mc) throws Exception {
        Class<?> c = Class.forName("cb.s9r.AtlasCheck");
        Map<String, Object> h = CompletableFuture.supplyAsync(() -> {
            try {
                return (Map<String, Object>) c.getMethod("holders").invoke(null);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }, mc).get(20, TimeUnit.SECONDS);
        class_11697 atlases = mc.method_72703();
        class_1058 back = atlases.method_73025(class_10725.field_56388).method_4608(class_2960.method_60656("back"));
        check("D.paintings", h.get("paintings.back") == back && name(back).equals("minecraft:back"),
            "getPaintingTextures().getBackSprite() = " + name(h.get("paintings.back")) + ", AtlasManager paintings back = " + name(back));
        class_2960 player = new class_20(class_21.field_91, (byte) 0, (byte) 0, (byte) 0, Optional.empty()).method_58451();
        class_1058 map = atlases.method_73025(class_10725.field_56386).method_4608(player);
        check("D.map", h.get("map.player") == map && name(map).equals(player.toString()),
            "getMapDecorationTextures().get(player) = " + name(h.get("map.player")) + ", AtlasManager = " + name(map));
        class_1058 heart = atlases.method_73025(class_10725.field_56385).method_4608(class_2960.method_60656("hud/heart/full"));
        check("D.gui", h.get("gui.heart") == heart && name(heart).equals("minecraft:hud/heart/full"),
            "getGuiSprites().getSprite(hud/heart/full) = " + name(h.get("gui.heart")) + ", AtlasManager = " + name(heart));
        class_1058 button = (class_1058) h.get("gui.button");
        Object scaling = button.method_45851().method_73021(class_8689.field_45647).orElse(class_8689.field_45645).comp_1636();
        check("D.scaling", scaling.equals(h.get("gui.button.scaling")) && !scaling.equals(class_8689.field_45645.comp_1636()),
            "getGuiSprites().getSpriteScaling(widget/button) = " + h.get("gui.button.scaling") + ", 1.21.9's = " + scaling);
        class_1058 stone = atlases.method_73025(class_10725.field_56382).method_4608(class_2960.method_60656("block/stone"));
        check("D.blocks", h.get("blocks.stone") == stone && name(stone).equals("minecraft:block/stone"),
            "getTextureAtlas(blocks).apply(block/stone) = " + name(h.get("blocks.stone")) + ", AtlasManager = " + name(stone));
        check("D.same", Boolean.TRUE.equals(h.get("same.holder")), "getPaintingTextures() answers the same holder each time: " + h.get("same.holder"));
        check("D.models", h.get("models.blocks") == atlases.method_73025(class_10725.field_56382),
            "getModelManager().getAtlas(textures/atlas/blocks.png) is AtlasManager's blocks atlas: " + (h.get("models.blocks") == atlases.method_73025(class_10725.field_56382)));
        Object ownSet = c.getMethod("ownAtlasSet").invoke(null);
        check("D.atlasSet", name(ownSet).equals("minecraft:block/stone"),
            "a 1.21.8 mod's own AtlasSet stitched and uploaded the 1.21.8 way has block/stone in its atlas and result: " + name(ownSet));
        Object own = c.getMethod("ownAtlas").invoke(null);
        check("D.own", name(own).equals("minecraft:block/stone"),
            "a 1.21.8 mod's own TextureAtlasHolder reloaded the 1.21.8 way has block/stone: " + name(own));
    }

    static Object call(class_310 mc, String method) throws Exception {
        Class<?> c = Class.forName("cb.s9r.DebugCheck");
        return CompletableFuture.supplyAsync(() -> {
            try {
                return c.getMethod(method).invoke(null);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }, mc).get(20, TimeUnit.SECONDS);
    }

    static void checkDebugRenderer(class_310 mc) throws Exception {
        int id = (Integer) call(mc, "rendererId");
        check("H.debugRenderer", id == System.identityHashCode(mc.field_1769.field_63067),
            "a 1.21.8 Minecraft.debugRenderer read answers LevelRenderer's DebugRenderer");
        String[][] toggles = {{"switchChunkBorder", "chunk borders"}, {"toggleOctree", "octree"}};
        net.minecraft.class_2960[] entries = {net.minecraft.class_11631.field_61582, net.minecraft.class_11631.field_61555};
        for (int i = 0; i < 2; i++) {
            boolean before = mc.field_61504.method_72772(entries[i]);
            boolean r1 = (Boolean) call(mc, toggles[i][0]);
            boolean s1 = mc.field_61504.method_72772(entries[i]);
            boolean r2 = (Boolean) call(mc, toggles[i][0]);
            boolean s2 = mc.field_61504.method_72772(entries[i]);
            check("H." + toggles[i][0], r1 == s1 && s1 != before && r2 == s2 && s2 == before,
                "1.21.8 " + toggles[i][0] + "() flips 1.21.9's " + toggles[i][1] + " debug entry and answers it: before " + before
                    + ", answers " + r1 + "/" + r2 + ", entry " + s1 + "/" + s2);
        }
        call(mc, "clear");
        check("H.clear", true, "1.21.8 DebugRenderer.clear() returns (title screen: no connection, nothing to drop)");
    }

    static void checkLoadScreen(class_310 mc) throws Exception {
        Class<?> c = Class.forName("cb.s9r.LoadScreenCheck");
        for (String reason : new String[]{"NETHER_PORTAL", "END_PORTAL", "OTHER"}) {
            Object screen = CompletableFuture.supplyAsync(() -> {
                try {
                    return c.getMethod("open", String.class).invoke(null, reason);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }, mc).get(20, TimeUnit.SECONDS);
            Thread.sleep(1500);
            boolean shown = mc.field_1755 == screen;
            boolean escape = (Boolean) c.getMethod("closesOnEscape", class_437.class).invoke(null, screen);
            ((java.util.concurrent.atomic.AtomicBoolean) c.getField("RECEIVED").get(null)).set(true);
            Thread.sleep(1500);
            boolean closed = mc.field_1755 != screen;
            check("E." + reason, shown && !escape && closed && screen.getClass().getSimpleName().equals("ReceivingLevelScreen"),
                "a 1.21.8 ReceivingLevelScreen (" + screen.getClass().getName() + ") shows and renders for " + reason + " (" + shown
                    + "), ignores Escape (" + !escape + "), closes once levelReceived answers true (" + closed + ")");
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
            // a screen and no loading overlay: the first resource reload (models, atlases) has finished
            while ((mc.field_1755 == null || mc.method_18506() != null) && System.currentTimeMillis() < until) {
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

            // Realms RowButton (the climbed list still on screen)
            String row = CompletableFuture.supplyAsync(() -> {
                try {
                    return (String) Class.forName("cb.s9r.RowCheck").getMethod("press", Object.class).invoke(null, screen);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }, mc).get(20, TimeUnit.SECONDS);
            check("F.rowButton", row.equals("1 true 25 12"),
                "a 1.21.8 RowButton pressed over row 1 of the climbed list: clicked index, row selected, right, bottom = " + row + " (want 1 true 25 12)");

            // armor layers
            checkArmor(mc.method_31974());

            // atlas holders
            checkAtlases(mc);

            // ReceivingLevelScreen
            checkLoadScreen(mc);

            // DebugRenderer toggles / clear, through 1.21.8's Minecraft.debugRenderer
            checkDebugRenderer(mc);

            // WardenModel emissive part lists
            String warden = CompletableFuture.supplyAsync(() -> {
                try {
                    return (String) Class.forName("cb.s9r.WardenCheck").getMethod("bake").invoke(null);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }, mc).get(20, TimeUnit.SECONDS);
            check("G.warden", warden.equals("lists at init: true | getters same as the constructor's lists: true | sizes 2,1,5,6"),
                "a 1.21.8 WardenModel: the 1.21.8 mod's constructor RETURN mixin saw the part lists, the getters answer them: " + warden);
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
