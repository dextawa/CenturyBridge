package cb.smx6n;

import cb.smx6n.mixin.ModelManagerAccess;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.class_1087;
import net.minecraft.class_1132;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_318;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_5819;
import net.minecraft.class_777;

/**
 * smx6's native 1.21 driver (see ../build.sh): once a world is joined, dumps every baked model's quads as one hash per
 * model, judges what the climbed 1.20.6 FaceBakery mixin (cb-smx6c, when loaded) recorded against 1.21's bakeFace
 * count, checks the stone model's quads, walls stone beside cobblestone in front of the player, screenshots, stops.
 */
public class Driver implements ClientModInitializer {
    public static final AtomicInteger FACES = new AtomicInteger();
    /** BakeQuadCounter / BakeFaceWrapCounter: 1.21 bakeQuad HEAD, bakeFace's bakeQuad call wrapped (MixinExtras) */
    public static final AtomicInteger QUADS = new AtomicInteger();
    public static final AtomicInteger FACE_WRAPS = new AtomicInteger();
    /** MovedHomeCounter / LeashVertexCounter: calls of 1.21's homes of the members 1.21 moved */
    public static final AtomicInteger PREDICATE = new AtomicInteger();
    public static final AtomicInteger VALUE = new AtomicInteger();
    public static final AtomicInteger GROUP = new AtomicInteger();
    public static final AtomicInteger LEASH = new AtomicInteger();
    /** BufferBuildCounter / SectionBuildCounter: 1.21 builders made, build() HEAD / RETURN, section-compile builds */
    public static final AtomicInteger BUILDERS = new AtomicInteger();
    public static final AtomicInteger BUILD_HEAD = new AtomicInteger();
    public static final AtomicInteger BUILD_RETURN = new AtomicInteger();
    public static final AtomicInteger SECTION_BUILDS = new AtomicInteger();
    static final Map<String, Boolean> OK = new LinkedHashMap<>();

    static void check(String name, boolean pass, String detail) {
        OK.put(name, pass);
        log((pass ? "ok " : "FAIL ") + name + " " + detail);
    }

    /** a case waiting on a known engine gap: logged "ok" or "known", not counted in the N/T tally */
    static void known(String name, boolean pass, String detail) {
        log((pass ? "ok " : "known ") + name + " " + detail);
    }

    static void log(String s) {
        System.out.println("CB-SMX6 " + s);
    }

    @Override
    public void onInitializeClient() {
        Thread t = new Thread(Driver::drive, "cb-smx6n");
        t.setDaemon(true);
        t.start();
    }

    static <T> T on(Executor ex, Supplier<T> s) throws Exception {
        return CompletableFuture.supplyAsync(s, ex).get(60, TimeUnit.SECONDS);
    }

    /** a hash of one quad: vertices, direction, tint index, shade, sprite (name and atlas coordinates) */
    static int quadHash(class_777 q) {
        return Arrays.hashCode(new int[] {Arrays.hashCode(q.method_3357()), q.method_3358() == null ? -1 : q.method_3358().ordinal(),
            q.method_3359(), q.method_24874() ? 1 : 0, String.valueOf(q.method_35788()).hashCode()});
    }

    static int modelHash(class_1087 m, class_2680 state) {
        int h = 17;
        List<class_2350> dirs = new ArrayList<>(Arrays.asList(class_2350.values()));
        dirs.add(null);
        for (class_2350 d : dirs) {
            List<class_777> qs = m.method_4707(state, d, class_5819.method_43049(42L));
            h = h * 31 + qs.size();
            for (class_777 q : qs) {
                h = h * 31 + quadHash(q);
            }
        }
        return h;
    }

    static boolean allZero(class_1087 m, class_2680 state) {
        boolean any = false;
        for (class_2350 d : class_2350.values()) {
            for (class_777 q : m.method_4707(state, d, class_5819.method_43049(42L))) {
                any = true;
                for (int v : q.method_3357()) {
                    if (v != 0) {
                        return false;
                    }
                }
            }
        }
        return any;
    }

    static void drive() {
        class_310 mc = class_310.method_1551();
        try {
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

            // (a) the dump: one hash per baked model (compare the layered run with the bare 1.21 run)
            Map<String, Integer> dump = on(mc, () -> {
                Map<String, Integer> out = new TreeMap<>();
                Map<?, ?> reg = ((ModelManagerAccess) mc.method_1554()).cb$bakedRegistry();
                for (Map.Entry<?, ?> e : reg.entrySet()) {
                    out.put(String.valueOf(e.getKey()), modelHash((class_1087) e.getValue(), null));
                }
                return out;
            });
            for (Map.Entry<String, Integer> e : dump.entrySet()) {
                log("dump " + e.getKey() + " " + Integer.toHexString(e.getValue()));
            }
            log("dump-total models=" + dump.size() + " hash=" + Integer.toHexString(dump.hashCode()) + " faces=" + FACES.get());

            // (e) native 1.21 injectors in the bake path, with or without the layer's bakeQuad route: once per face
            check("e.nativeQuadOnce", FACES.get() > 0 && QUADS.get() == FACES.get(),
                "native bakeQuad HEAD " + QUADS.get() + ", bakeFace calls " + FACES.get());
            check("e.nativeWrap", FACE_WRAPS.get() == FACES.get(),
                "native @WrapOperation of bakeFace's bakeQuad call " + FACE_WRAPS.get() + ", bakeFace calls " + FACES.get());

            // (b) / (c): the climbed 1.20.6 mixin, when loaded
            Class<?> counters = null;
            try {
                counters = Class.forName("cb.smx6c.Counters");
            } catch (ClassNotFoundException absent) {
                log("cb-smx6c not loaded: checks b/c not run (dump run)");
            }
            if (counters != null) {
                int calls = ((AtomicInteger) field(counters, "CALLS")).get();
                int nulls = ((AtomicInteger) field(counters, "NULLS")).get();
                int replaced = ((AtomicInteger) field(counters, "REPLACED")).get();
                @SuppressWarnings("unchecked")
                Map<String, Integer> locs = (Map<String, Integer>) field(counters, "LOCATIONS");
                List<String> sample = new ArrayList<>(new TreeMap<>(locs).keySet());
                check("b.calls", calls > 0, "bakeQuad(.., location) calls " + calls);
                check("b.childStone", locs.containsKey("minecraft:block/stone"),
                    "minecraft:block/stone seen " + locs.get("minecraft:block/stone") + " times; " + locs.size() + " locations, first "
                        + sample.subList(0, Math.min(8, sample.size())));
                check("b.noNull", nulls == 0, "calls with a null location " + nulls);
                check("b.countEqualsFaces", calls == FACES.get(), "bakeQuad calls " + calls + ", 1.21 bakeFace calls " + FACES.get());
                int noId = ((AtomicInteger) field(counters, "CALLS_NO_ID")).get();
                check("b.noIdOnce", noId == FACES.get(), "bakeQuad HEAD calls of a handler not reading the id " + noId
                    + " (on 1.21's bakeQuad through mixinTargets), 1.21 bakeFace calls " + FACES.get());
                boolean stoneZero = on(mc, () -> allZero(mc.method_1554().method_4743().method_3335(class_2246.field_10340.method_9564()),
                    class_2246.field_10340.method_9564()));
                boolean graniteDrawn = on(mc, () -> !allZero(mc.method_1554().method_4743().method_3335(class_2246.field_10474.method_9564()),
                    class_2246.field_10474.method_9564()));
                check("c.stoneQuadsZero", stoneZero && replaced > 0, "stone model quads all-zero " + stoneZero + ", quads replaced " + replaced);
                check("c.othersUnchanged", graniteDrawn, "granite model has non-zero vertices " + graniteDrawn);
                // (d) s121.json mixinMoves: ModelBakery helpers -> BlockStateModelLoader, counted at both ends
                for (String n : new String[] {"PREDICATE", "VALUE", "GROUP"}) {
                    int v = ((AtomicInteger) field(counters, n)).get();
                    int w = ((AtomicInteger) Driver.class.getField(n).get(null)).get();
                    check("d.moved." + n, v > 0 && v == w, "1.20.6 mixin calls " + v + ", 1.21 BlockStateModelLoader calls " + w);
                    int b = ((AtomicInteger) field(counters, n + "_BARE")).get();
                    known("d.movedBare." + n, b > 0 && b == w, "bare-name selector: 1.20.6 mixin calls " + b + ", 1.21 calls " + w
                        + " (mixinMoves needs the selector's descriptor: cases_smixin3/s121.md C23)");
                }
            }

            // the screenshot: stone (left) beside cobblestone (right), 3 blocks north of the player, looking north
            class_1132 server = mc.method_1576();
            UUID id = mc.field_1724.method_5667();
            CompletableFuture.runAsync(() -> {
                class_3222 p = server.method_3760().method_14602(id);
                class_3218 lvl = (class_3218) p.method_37908();
                int x = (int) Math.floor(p.method_23317()), y = (int) Math.floor(p.method_23318()), z = (int) Math.floor(p.method_23321());
                for (int dx = -3; dx <= 3; dx++) {
                    for (int dy = 0; dy <= 3; dy++) {
                        class_2248 b = dx < 0 ? class_2246.field_10340 : dx == 0 ? class_2246.field_10201 : class_2246.field_10445;
                        lvl.method_8501(new class_2338(x + dx, y + dy, z - 3), b.method_9564());
                        for (int dz = -2; dz <= -1; dz++) {
                            lvl.method_8501(new class_2338(x + dx, y + dy, z + dz), class_2246.field_10124.method_9564());
                        }
                    }
                }
                p.method_14251(lvl, x + 0.5, y, z + 0.5, 180.0F, 0.0F);
                // (e) a pig two blocks in front of the player, leashed to it: 1.21 draws the leash through
                // EntityRenderer.addVertexPair, 1.20.6 through MobRenderer.addVertexPair
                net.minecraft.class_1452 pig = net.minecraft.class_1299.field_6093.method_5883(lvl);
                pig.method_5808(x + 0.5, y, z - 2 + 0.5, 0.0F, 0.0F);
                pig.method_5977(true);
                lvl.method_8649(pig);
                pig.method_60964(p, true);
            }, server).get(20, TimeUnit.SECONDS);
            Thread.sleep(4000);
            CompletableFuture.runAsync(() -> class_318.method_22690(mc.field_1697, "cb-smx6-wall.png", mc.method_1522(), c -> { }), mc)
                .get(20, TimeUnit.SECONDS);
            log("screenshot cb-smx6-wall.png: left stone (invisible with the mixin), middle diamond block, right cobblestone, a leashed pig");
            if (counters != null) {
                int v = ((AtomicInteger) field(counters, "LEASH")).get();
                int w = LEASH.get();
                check("e.moved.LEASH", v > 0 && v == w,
                    "1.20.6 MobRenderer.addVertexPair mixin calls " + v + ", 1.21 EntityRenderer.addVertexPair calls " + w);
                int b = ((AtomicInteger) field(counters, "LEASH_BARE")).get();
                known("e.movedBare.LEASH", b > 0 && b == w, "bare-name selector: 1.20.6 mixin calls " + b + ", 1.21 calls " + w
                    + " (C23)");
            } else {
                log("leash vertex pairs drawn by 1.21: " + LEASH.get());
            }
            // (f) BufferBuilder lifecycle routes: one snapshot on the render thread (every count moves on it, except
            // section compiles and their builders on worker threads, which the snapshot reads as they stand: up to 32 in flight)
            AtomicInteger zero = new AtomicInteger();
            AtomicInteger vBegin = counters == null ? zero : (AtomicInteger) field(counters, "BEGIN");
            AtomicInteger vEnd = counters == null ? zero : (AtomicInteger) field(counters, "END");
            AtomicInteger vEod = counters == null ? zero : (AtomicInteger) field(counters, "END_OR_DISCARD");
            int[] snap = on(mc, () -> new int[] {BUILDERS.get(), BUILD_HEAD.get(), BUILD_RETURN.get(), SECTION_BUILDS.get(),
                vBegin.get(), vEnd.get(), vEod.get()});
            log("buffers: made " + snap[0] + ", build HEAD " + snap[1] + ", RETURN " + snap[2] + ", section builds " + snap[3]
                + "; 1.20.6 begin " + snap[4] + ", end " + snap[5] + ", endOrDiscardIfEmpty " + snap[6]);
            check("f.nativeOnce", snap[1] > 0 && snap[1] == snap[2], "native build() HEAD " + snap[1] + " = RETURN " + snap[2]
                + " (a native injector runs once per batch with the routes on)");
            if (counters != null) {
                check("f.begin", snap[4] > 0 && Math.abs(snap[4] - snap[0]) <= 32, "1.20.6 begin " + snap[4] + ", 1.21 builders made " + snap[0]);
                check("f.endOrDiscard", snap[6] > 0 && Math.abs(snap[6] - snap[3]) <= 32,
                    "1.20.6 endOrDiscardIfEmpty " + snap[6] + ", 1.21 section-compile builds " + snap[3]);
                check("f.end", snap[5] > 0 && Math.abs(snap[5] + snap[3] - snap[2]) <= 32,
                    "1.20.6 end " + snap[5] + " + section builds " + snap[3] + " vs 1.21 build() RETURN " + snap[2]);
            }
        } catch (Throwable t) {
            log("FAIL driver " + t);
            t.printStackTrace();
        }
        finish(mc);
    }

    static Object field(Class<?> c, String name) throws ReflectiveOperationException {
        Field f = c.getField(name);
        return f.get(null);
    }

    static void finish(class_310 mc) {
        int ok = 0;
        for (boolean b : OK.values()) {
            ok += b ? 1 : 0;
        }
        log("summary ok=" + ok + " fail=" + (OK.size() - ok));
        log(ok + "/" + OK.size());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
        mc.execute(mc::method_1592);
    }
}
