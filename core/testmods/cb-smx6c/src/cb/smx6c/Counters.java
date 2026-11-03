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

    /** RebuildTaskMoveMixin (smixin18): 1.20.6 RebuildTask compile / handleBlockEntity HEAD, doTask's compile call, and a
     *  compile HEAD handler reading this (negative: stays 0 on 1.21) */
    public static final AtomicInteger COMPILE = new AtomicInteger();
    public static final AtomicInteger COMPILE_THIS = new AtomicInteger();
    public static final AtomicInteger BLOCK_ENTITY = new AtomicInteger();
    public static final AtomicInteger COMPILE_CALL = new AtomicInteger();

    private Counters() {
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
