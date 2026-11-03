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
    /** DragonRayCounter: 1.21 death-ray vertices (addVertex in EnderDragonRenderer.method_61158) */
    public static final AtomicInteger RAY_VERTS = new AtomicInteger();
    /** what section (h)'s bakeFace probe added to QUADS and to cb-smx6c's CALLS / CALLS_NO_ID (baked outside 1.21's bakeFace) */
    static int PROBE_QUADS, PROBE_CALLS, PROBE_NO_ID;
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

            // (h) 1.20.6 BlockModel.bakeFace through cb-smx6c's access widener (damage-aw): a quad, and its location seen by
            // the 1.20.6 bakeQuad mixin (the layer bakes it on the restored bakeQuad(.., location))
            try {
                Class<?> cc = Class.forName("cb.smx6c.Counters");
                String loc = (String) field(cc, "PROBE_LOCATION");
                @SuppressWarnings("unchecked")
                Map<String, Integer> locs = (Map<String, Integer>) field(cc, "LOCATIONS");
                int before = locs.getOrDefault(loc, 0);
                int q0 = QUADS.get(), c0 = ((AtomicInteger) field(cc, "CALLS")).get(), n0 = ((AtomicInteger) field(cc, "CALLS_NO_ID")).get();
                boolean quad = on(mc, () -> {
                    try {
                        return (Boolean) cc.getMethod("bakeFaceProbe").invoke(null);
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e.getCause() != null ? e.getCause() : e);
                    }
                });
                int seen = locs.getOrDefault(loc, 0) - before;
                // the probe bakes its quad outside 1.21's bakeFace: kept out of the once-per-bakeFace checks (b) / (e)
                PROBE_QUADS = QUADS.get() - q0;
                PROBE_CALLS = ((AtomicInteger) field(cc, "CALLS")).get() - c0;
                PROBE_NO_ID = ((AtomicInteger) field(cc, "CALLS_NO_ID")).get() - n0;
                check("h.awBakeFace", quad && seen == 1, "1.20.6 BlockModel.bakeFace via access widener: quad " + quad
                    + ", its location seen by the 1.20.6 bakeQuad mixin " + seen + " time(s)");
            } catch (ClassNotFoundException e) {
                log("h: cb-smx6c not loaded");
            } catch (Exception e) {
                Throwable c = e.getCause() != null ? e.getCause() : e;
                check("h.awBakeFace", false, "bakeFace probe failed: " + c);
            }
            // (i) 1.20.6 RebuildTask.compile / handleBlockEntity through cb-smx6c's access widener (damage-aw, s121 C20): a
            // chest and a stained-glass block 2 north of the player, the section compiled by the restored compile on 1.21's
            // SectionCompiler. Native controls: 1.21's SectionCompiler injectors (SectionBuildCounter) and the layer's
            // CbRenderSectionCompileRouteMixin (which answers cb-smx6c's endOrDiscardIfEmpty mixin) fire for its builds
            try {
                Class<?> cc = Class.forName("cb.smx6c.Counters");
                class_1132 srv = mc.method_1576();
                UUID pid = mc.field_1724.method_5667();
                int[] at = new int[3];
                CompletableFuture.runAsync(() -> {
                    class_3222 p = srv.method_3760().method_14602(pid);
                    class_3218 lvl = (class_3218) p.method_37908();
                    at[0] = (int) Math.floor(p.method_23317());
                    at[1] = (int) Math.floor(p.method_23318()) + 1;
                    at[2] = (int) Math.floor(p.method_23321()) - 2;
                    lvl.method_8501(new class_2338(at[0], at[1], at[2]), class_2246.field_10034.method_9564());
                    lvl.method_8501(new class_2338(at[0] + 1, at[1], at[2]), class_2246.field_10087.method_9564());
                }, srv).get(20, TimeUnit.SECONDS);
                Thread.sleep(3000);
                AtomicInteger eod = (AtomicInteger) field(cc, "END_OR_DISCARD");
                int[] r = on(mc, () -> {
                    int sb = SECTION_BUILDS.get(), e0 = eod.get();
                    int[] out;
                    try {
                        out = (int[]) cc.getMethod("compileProbe", int.class, int.class, int.class).invoke(null, at[0], at[1], at[2]);
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e.getCause() != null ? e.getCause() : e);
                    }
                    int[] all = Arrays.copyOf(out, out.length + 2);
                    all[out.length] = SECTION_BUILDS.get() - sb;
                    all[out.length + 1] = eod.get() - e0;
                    return all;
                });
                log("compile probe: layers " + r[0] + ", chests " + r[1] + ", visibility " + r[2] + ", sort " + r[3] + ", handled " + r[4]
                    + ", translucent " + r[5] + ", native section builds " + r[6] + ", 1.20.6 endOrDiscardIfEmpty " + r[7]);
                check("i.awCompile", r[0] > 0 && r[1] == 1 && r[2] == 1, "1.20.6 RebuildTask.compile via access widener: layers " + r[0]
                    + ", the chest in blockEntities " + r[1] + ", visibilitySet " + r[2]);
                check("i.awSortState", r[5] == 1 && r[3] == 1, "translucent layer " + r[5] + ", 1.20.6 SortState QUADS with 4 vertices per point "
                    + r[3]);
                check("i.awHandleBlockEntity", r[4] == 1, "1.20.6 RebuildTask.handleBlockEntity added the chest " + r[4]);
                check("i.nativeSectionBuild", r[6] == r[0], "native SectionCompiler build() injector during the probe " + r[6]
                    + " = layers " + r[0]);
                check("i.layerRoute", r[7] == r[0], "the layer's section-compile route (1.20.6 endOrDiscardIfEmpty mixin) during the probe "
                    + r[7] + " = layers " + r[0]);
            } catch (ClassNotFoundException e) {
                log("i: cb-smx6c not loaded");
            } catch (Exception e) {
                Throwable c = e.getCause() != null ? e.getCause() : e;
                check("i.awCompile", false, "compile probe failed: " + c);
            }
            // (a) the dump: one hash per baked model (compare the layered run with the bare 1.21 run). Climbed past 1.21.4
            // the accessor's field (ModelManager.field_5408) is gone; that failure is this section's own, recorded, and the
            // later sections still run
            try {
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
            } catch (Exception e) {
                Throwable c = e.getCause() != null ? e.getCause() : e;
                check("a.dump", false, "baked-model dump not taken: " + c);
            }

            // (e) native 1.21 injectors in the bake path, with or without the layer's bakeQuad route: once per face
            check("e.nativeQuadOnce", FACES.get() > 0 && QUADS.get() - PROBE_QUADS == FACES.get(),
                "native bakeQuad HEAD " + QUADS.get() + " (" + PROBE_QUADS + " from the h probe), bakeFace calls " + FACES.get());
            check("e.nativeWrap", FACE_WRAPS.get() == FACES.get(),
                "native @WrapOperation of bakeFace's bakeQuad call " + FACE_WRAPS.get() + ", bakeFace calls " + FACES.get());

            // (b) / (c): the climbed 1.20.6 mixin, when loaded
            Class<?> counters = null;
            try {
                counters = Class.forName("cb.smx6c.Counters");
            } catch (ClassNotFoundException absent) {
                log("cb-smx6c not loaded: checks b/c not run (dump run)");
            }
            class_1132 server = mc.method_1576();
            UUID id = mc.field_1724.method_5667();
            // (b) .. (e): their failures (members gone past 1.21, e.g. BakedModel.getQuads at 1.21.5) are their own,
            // recorded, and (f) still runs
            try {
                if (counters != null) {
                    int calls = ((AtomicInteger) field(counters, "CALLS")).get() - PROBE_CALLS;
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
                    int noId = ((AtomicInteger) field(counters, "CALLS_NO_ID")).get() - PROBE_NO_ID;
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
            } catch (Exception e) {
                Throwable c = e.getCause() != null ? e.getCause() : e;
                check("bcde.ran", false, "sections b..e stopped: " + c);
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
            // (g) EnderDragonRenderer death rays: a dragon (no AI) above and south of the player, the player turned to it,
            // its client copy held dying (dragonDeathTime 60) for three seconds; rays are drawn while dragonDeathTime > 0
            int[] dragonId = new int[1];
            CompletableFuture.runAsync(() -> {
                class_3222 p = server.method_3760().method_14602(id);
                class_3218 lvl = (class_3218) p.method_37908();
                net.minecraft.class_1510 dragon = net.minecraft.class_1299.field_6116.method_5883(lvl);
                dragon.method_5808(p.method_23317(), p.method_23318() + 8.0, p.method_23321() + 16.0, 0.0F, 0.0F);
                dragon.method_5977(true);
                lvl.method_8649(dragon);
                dragonId[0] = dragon.method_5628();
                p.method_14251(lvl, p.method_23317(), p.method_23318(), p.method_23321(), 0.0F, -20.0F);
            }, server).get(20, TimeUnit.SECONDS);
            int vertsBefore = RAY_VERTS.get();
            long hold = System.currentTimeMillis() + 3000;
            boolean seen = false;
            while (System.currentTimeMillis() < hold) {
                seen |= on(mc, () -> {
                    net.minecraft.class_1297 e = mc.field_1687 == null ? null : mc.field_1687.method_8469(dragonId[0]);
                    if (e instanceof net.minecraft.class_1510 d) {
                        d.field_7031 = 60;
                        return true;
                    }
                    return false;
                });
                Thread.sleep(50);
            }
            Thread.sleep(500);
            AtomicInteger z0 = new AtomicInteger();
            AtomicInteger rc = counters == null ? z0 : (AtomicInteger) field(counters, "RAY_CENTRE");
            AtomicInteger ra = counters == null ? z0 : (AtomicInteger) field(counters, "RAY_A");
            AtomicInteger rb = counters == null ? z0 : (AtomicInteger) field(counters, "RAY_B");
            AtomicInteger rcc = counters == null ? z0 : (AtomicInteger) field(counters, "RAY_C");
            AtomicInteger bad = counters == null ? z0 : (AtomicInteger) field(counters, "RAY_BAD");
            int[] r = on(mc, () -> new int[] {RAY_VERTS.get(), rc.get(), ra.get(), rb.get(), rcc.get(), bad.get()});
            log("dragon rays: client dragon seen " + seen + ", 1.21 ray vertices " + r[0] + " (" + (r[0] - vertsBefore)
                + " while dying); 1.20.6 helpers centre " + r[1] + ", A " + r[2] + ", B " + r[3] + ", C " + r[4] + ", bad arguments " + r[5]);
            check("g.nativeRayVerts", seen && r[0] > 0 && r[0] % 9 == 0, "native @WrapOperation of the ray addVertex " + r[0]
                + " (nine per ray)");
            if (counters != null) {
                check("g.centre", r[1] * 3 == r[0], "1.20.6 vertex01 calls " + r[1] + " = a third of 1.21's ray vertices " + r[0]);
                // per ray 1.20.6 drew centre A B, centre B C, centre C A: the centre three times, each corner twice
                check("g.corners", r[1] > 0 && 3 * r[2] == 2 * r[1] && r[3] == r[2] && r[4] == r[2], "1.20.6 vertex2 / 3 / 4 calls " + r[2]
                    + " / " + r[3] + " / " + r[4] + ", each two thirds of the centre's " + r[1]);
                check("g.args", r[5] == 0, "helper calls with an alpha, height or width 1.20.6 could not pass: " + r[5]);
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
