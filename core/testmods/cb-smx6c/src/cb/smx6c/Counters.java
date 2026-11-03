package cb.smx6c;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** What the 1.20.6 mixins saw; read by the native 1.21 driver (cb-smx6n) through reflection. */
public final class Counters {
    /** FaceBakeryMixin.cb$countNoId: bakeQuad HEAD calls of a handler that does not read the id */
    /** DragonRaysMixin: 1.20.6 death-ray helper calls (centre, corners A / B / C) and impossible arguments */
    public static final AtomicInteger RAY_CENTRE = new AtomicInteger();
    public static final AtomicInteger RAY_A = new AtomicInteger();
    public static final AtomicInteger RAY_B = new AtomicInteger();
    public static final AtomicInteger RAY_C = new AtomicInteger();
    public static final AtomicInteger RAY_BAD = new AtomicInteger();
    public static final AtomicInteger CALLS_NO_ID = new AtomicInteger();
    public static final AtomicInteger CALLS = new AtomicInteger();
    public static final AtomicInteger NULLS = new AtomicInteger();
    public static final AtomicInteger REPLACED = new AtomicInteger();
    public static final Map<String, Integer> LOCATIONS = new ConcurrentHashMap<>();
    /** ModelBakeryMoveMixin / MobRendererLeashMixin: calls of the members 1.21 moved (s121.json mixinMoves) */
    public static final AtomicInteger PREDICATE = new AtomicInteger();
    public static final AtomicInteger VALUE = new AtomicInteger();
    public static final AtomicInteger GROUP = new AtomicInteger();
    public static final AtomicInteger LEASH = new AtomicInteger();
    /** the same mixins with bare-name selectors ("method_4725"), as most mods write them */
    public static final AtomicInteger PREDICATE_BARE = new AtomicInteger();
    public static final AtomicInteger VALUE_BARE = new AtomicInteger();
    public static final AtomicInteger GROUP_BARE = new AtomicInteger();
    public static final AtomicInteger LEASH_BARE = new AtomicInteger();

    /** BufferBuilderLifecycleMixin: 1.20.6 begin / end / endOrDiscardIfEmpty calls */
    public static final AtomicInteger BEGIN = new AtomicInteger();
    public static final AtomicInteger END = new AtomicInteger();
    public static final AtomicInteger END_OR_DISCARD = new AtomicInteger();

    private Counters() {
    }

    /**
     * 1.20.6 RebuildTask.compile(x, y, z, pack) and handleBlockEntity(results, blockEntity), private, reached through this
     * mod's access widener (damage-aw 1.20.6__1.21, aw-call): a task over a fresh RenderSection at the section holding
     * the chest at (cx, cy, cz), its region made as 1.20.6's createCompileTask made it, compiled from the camera position.
     * Render thread. Answers {layers, chest block entities in blockEntities, visibilitySet set, sort state (-1 none, 1 a
     * QUADS state whose vertex count is four per point, 0 another), handleBlockEntity added the chest (1/0), the
     * translucent layer present (1/0)}. Every rendered buffer is released and the pack closed: the caller owns them.
     */
    public static int[] compileProbe(int cx, int cy, int cz) {
        net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
        net.minecraft.class_846 dispatcher = mc.field_1769.method_34810();
        int ox = net.minecraft.class_4076.method_18675(cx) * 16, oy = net.minecraft.class_4076.method_18675(cy) * 16,
            oz = net.minecraft.class_4076.method_18675(cz) * 16;
        net.minecraft.class_846.class_851 section = dispatcher.new class_851(0, ox, oy, oz);
        net.minecraft.class_2338 origin = new net.minecraft.class_2338(ox, oy, oz);
        net.minecraft.class_853 region = new net.minecraft.class_6850().method_39969(mc.field_1687, origin.method_10069(-1, -1, -1),
            origin.method_10069(16, 16, 16), 1);
        net.minecraft.class_846.class_851.class_4578 task = section.new class_4578(0.0, region, false);
        net.minecraft.class_243 eye = mc.field_1724.method_33571();
        net.minecraft.class_750 pack = new net.minecraft.class_750();
        try {
            net.minecraft.class_846.class_851.class_4578.class_7435 res = task.method_22785((float) eye.field_1352, (float) eye.field_1351,
                (float) eye.field_1350, pack);
            int chests = 0;
            for (net.minecraft.class_2586 be : res.field_39080) {
                chests += be instanceof net.minecraft.class_2595 ? 1 : 0;
            }
            int sort = -1;
            net.minecraft.class_287.class_5594 st = res.field_39083;
            if (st != null) {
                sort = st.field_27358 == net.minecraft.class_293.class_5596.field_27382 && st.field_27360.length > 0
                    && st.field_27359 == st.field_27360.length * 4 ? 1 : 0;
            }
            int translucent = res.field_39081.containsKey(net.minecraft.class_1921.method_23583()) ? 1 : 0;
            int layers = res.field_39081.size();
            for (net.minecraft.class_287.class_7433 b : res.field_39081.values()) {
                b.method_43585();
            }
            net.minecraft.class_846.class_851.class_4578.class_7435 fresh = new net.minecraft.class_846.class_851.class_4578.class_7435();
            net.minecraft.class_2586 chest = mc.field_1687.method_8321(new net.minecraft.class_2338(cx, cy, cz));
            int handled = 0;
            if (chest != null) {
                task.method_23087(fresh, chest);
                handled = fresh.field_39080.contains(chest) ? 1 : 0;
            }
            return new int[] {layers, chests, res.field_39082 != null ? 1 : 0, sort, handled, translucent};
        } finally {
            pack.close();
            section.method_3659();
        }
    }

    /** the location bakeFaceProbe hands BlockModel.bakeFace; FaceBakeryMixin.cb$count records it in LOCATIONS */
    public static final String PROBE_LOCATION = "cb_smx6c:block/aw_probe";

    /**
     * 1.20.6 BlockModel.bakeFace(element, face, sprite, direction, state, location), reached through this mod's access
     * widener as a mod would (damage-aw aw-call-static). Called on the client thread by the native driver (reflection);
     * answers whether a quad came back. 1.21: the layer's CbRenderModelBake.method_3447 bakes it on the restored
     * bakeQuad(.., location), so the location reaches FaceBakeryMixin as on 1.20.6.
     */
    public static boolean bakeFaceProbe() {
        net.minecraft.class_1058 sprite = net.minecraft.class_310.method_1551().method_1549(net.minecraft.class_1059.field_5275)
            .apply(net.minecraft.class_1047.method_4539());
        net.minecraft.class_783 face = new net.minecraft.class_783(net.minecraft.class_2350.field_11036, -1, "#all",
            new net.minecraft.class_787(new float[] {0, 0, 16, 16}, 0));
        net.minecraft.class_785 element = new net.minecraft.class_785(new org.joml.Vector3f(0, 0, 0), new org.joml.Vector3f(16, 16, 16),
            java.util.Map.of(net.minecraft.class_2350.field_11036, face), null, true);
        net.minecraft.class_777 quad = net.minecraft.class_793.method_3447(element, face, sprite, net.minecraft.class_2350.field_11036,
            net.minecraft.class_1086.field_5350, new net.minecraft.class_2960(PROBE_LOCATION));
        return quad != null;
    }
}
