package top.dext.centurybridge.step.v1_21_11.rt;

import com.google.common.collect.MapMaker;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12247;
import net.minecraft.class_1921;
import top.dext.centurybridge.step.v1_21_11.facade.CompositeState;
import top.dext.centurybridge.step.v1_21_11.facade.RenderStateShard;

/**
 * 1.21.10's RenderType as a RenderStateShard (javap -c -p both code jars).
 *
 * 1.21.10: RenderType extends RenderStateShard; RenderType(name, bufferSize, affectsCrumbling, sortOnUpload,
 * setupState, clearState) (public, the abstract class's only constructor) passed (name, setup, clear) to
 * RenderStateShard and kept bufferSize / affectsCrumbling / sortOnUpload in fields its getters answered;
 * setupRenderState / clearRenderState ran the runnables; CompositeRenderType's runnables ran each of its composite
 * state's shards (texture, lightmap, overlay, layering, output, texturing, line) in that order, setup and clear alike.
 * 1.21.11: RenderType is a concrete class built from (name, RenderSetup) by a private constructor and is no shard;
 * its getters read the RenderSetup (buffer size, crumbling, sorting, outline property, pipeline).
 *
 * A 1.21.10 subclass's super(...) becomes RenderType(name, setup) with a RenderSetup carrying 1.21.10's three values
 * and RenderSetup's defaults otherwise (outline property NONE: 1.21.10's base isOutline() answered false and
 * outline() empty, as 1.21.11's do for NONE); the pipeline is null, which 1.21.11 reads only in the getters a 1.21.10
 * subclass overrides (getRenderPipeline / format / mode / draw were abstract in 1.21.10). Its runnables are recorded
 * per instance; setupRenderState / clearRenderState / getName called through RenderType reach the subclass's own
 * overrides (rt/CbLegacyRenderTypes.Shard, added to every 1.21.10 subclass), else 1.21.10's base bodies. A render
 * type built by 1.21.10's RenderType.create (rt/CbRenderTypeCreate) runs its composite state's shards as 1.21.10's
 * CompositeRenderType did; a 1.21.11 render type the RenderSetup pieces 1.21.11 still has, as the facade's vanilla
 * shards run them (layering on the model view stack, texturing on the restored texture matrix).
 */
@Environment(EnvType.CLIENT)
public final class CbLegacyRenderTypes {
    private CbLegacyRenderTypes() {
    }

    /** 1.21.10 RenderStateShard's overridable surface on a 1.21.10 RenderType subclass (its own overrides win) */
    public interface Shard {
        default void method_23516() {
            setupBase((class_1921) (Object) this);
        }

        default void method_23518() {
            clearBase((class_1921) (Object) this);
        }

        default String method_68484() {
            return ((class_1921) (Object) this).field_64011;
        }
    }

    private record Runnables(Runnable setup, Runnable clear) {
    }

    private static final Map<class_1921, Runnables> LEGACY = new MapMaker().weakKeys().makeMap();
    private static final Map<class_1921, CompositeState> COMPOSITE = new MapMaker().weakKeys().makeMap();
    private static final Map<class_12247, List<RenderStateShard>> VANILLA = new MapMaker().weakKeys().makeMap();
    /** the bufferSize a 1.21.10 constructor / create call gave the types built here (1.21.10 kept it in a field) */
    private static final Map<class_1921, Integer> SIZE = new MapMaker().weakKeys().makeMap();

    /** the RenderSetup 1.21.10's RenderType(name, bufferSize, affectsCrumbling, sortOnUpload, ..) stands for */
    public static class_12247 setup(int bufferSize, boolean affectsCrumbling, boolean sortOnUpload) {
        class_12247.class_12248 b = class_12247.method_75927(null);
        if (affectsCrumbling) {
            b.method_75936();
        }
        if (sortOnUpload) {
            b.method_75937();
        }
        b.method_75929(bufferSize);
        return b.method_75938();
    }

    /** after a 1.21.10 subclass's super(...): its runnables */
    public static void built(class_1921 type, String name, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload,
                             Runnable setupState, Runnable clearState) {
        LEGACY.put(type, new Runnables(setupState, clearState));
        SIZE.put(type, bufferSize);
    }

    /** a render type rt/CbRenderTypeCreate built from 1.21.10's composite state */
    static void composite(class_1921 type, CompositeState state, int bufferSize) {
        COMPOSITE.put(type, state);
        SIZE.put(type, bufferSize);
    }

    /** RenderType.setupRenderState(), called through RenderType */
    public static void method_23516(class_1921 type) {
        if (type instanceof Shard own) {
            own.method_23516();
        } else {
            setupBase(type);
        }
    }

    /** RenderType.clearRenderState(), called through RenderType */
    public static void method_23518(class_1921 type) {
        if (type instanceof Shard own) {
            own.method_23518();
        } else {
            clearBase(type);
        }
    }

    /** RenderType.getName(), called through RenderType */
    public static String method_68484(class_1921 type) {
        return type instanceof Shard own ? own.method_68484() : type.field_64011;
    }

    /** a 1.21.10 subclass's super.setupRenderState(): 1.21.10's body */
    public static void superSetup(class_1921 type) {
        setupBase(type);
    }

    /** a 1.21.10 subclass's super.clearRenderState(): 1.21.10's body */
    public static void superClear(class_1921 type) {
        clearBase(type);
    }

    /** a 1.21.10 subclass's super.getName(): 1.21.10's body */
    public static String superName(class_1921 type) {
        return type.field_64011;
    }

    static void setupBase(class_1921 type) {
        Runnables r = LEGACY.get(type);
        if (r != null) {
            r.setup.run();
            return;
        }
        for (RenderStateShard s : shards(type)) {
            s.method_23516();
        }
    }

    static void clearBase(class_1921 type) {
        Runnables r = LEGACY.get(type);
        if (r != null) {
            r.clear.run();
            return;
        }
        for (RenderStateShard s : shards(type)) {
            s.method_23518();
        }
    }

    private static List<? extends RenderStateShard> shards(class_1921 type) {
        CompositeState state = COMPOSITE.get(type);
        if (state != null) {
            return state.field_21422;
        }
        class_12247 setup = type.field_64013;
        return VANILLA.computeIfAbsent(setup, s -> List.of(
            new RenderStateShard.LayeringStateShard(s.field_63996),
            new RenderStateShard.TexturingStateShard(s.field_63988)));
    }

    /**
     * RenderType.bufferSize (field read, a mod's AW): the size a 1.21.10 call built the type with, kept here; for any
     * other type 1.21.11's public RenderType.bufferSize() (javap: setup.bufferSize), which 26.1 .. 26.1.2 keep
     * (26.2 dropped the size from RenderSetup and RenderType)
     */
    public static int field_20806(class_1921 type) {
        Integer own = SIZE.get(type);
        return own != null ? own : type.method_22722();
    }

    /** RenderType.affectsCrumbling (field read, a mod's AW) */
    public static boolean field_20975(class_1921 type) {
        return type.field_64013.field_63993;
    }

    /** RenderType.sortOnUpload (field read, a mod's AW) */
    public static boolean field_21402(class_1921 type) {
        return type.field_64013.field_63994;
    }
}
