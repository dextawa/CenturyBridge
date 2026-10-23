package top.dext.centurybridge.step.v26_1.compat.fapi.client.rendering.v1.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * The four 1.21.11 WorldRenderEvents events 26.1's LevelRenderEvents no longer has (the rest are renames):
 * BEFORE_ENTITIES, AFTER_ENTITIES, BEFORE_DEBUG_RENDER, BEFORE_TRANSLUCENT, with 1.21.11's listener types and
 * invokers. The fields are reached through fieldRedirects; the listener types are the nested interfaces here.
 *
 * Fired by mixin/CbFapiLevelRendererMixin at the points of 26.1's main pass where 1.21.11's LevelRendererMixin
 * fired them, with the frame's render context: 26.1's Fabric API hands it to START_MAIN listeners at the
 * start of each main pass (the same LevelRenderContext object every frame), and this class takes it there.
 * <ul>
 *   <li>BEFORE_ENTITIES: before the entities are submitted (1.21.11: the "submitEntities" profiler section;
 *       26.1: "submitFeatures");</li>
 *   <li>AFTER_ENTITIES: once the entities are drawn (26.1: after the solid features, at the first pose-stack
 *       check);</li>
 *   <li>BEFORE_DEBUG_RENDER: before the debug geometry is drawn (26.1 draws it as gizmos, at
 *       finalizeGizmoCollection; it collects them during extraction, before this frame's context exists);</li>
 *   <li>BEFORE_TRANSLUCENT: before translucent terrain (1.21.11's "translucent" section, 26.1's
 *       "translucentTerrain").</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbWorldRenderEvents {
    private CbWorldRenderEvents() {
    }

    private static volatile LevelRenderContext frame;

    static {
        LevelRenderEvents.START_MAIN.register(context -> {
            if (context instanceof LevelRenderContext full) {
                frame = full;
            }
        });
    }

    public static final Event<BeforeEntities> BEFORE_ENTITIES = EventFactory.createArrayBacked(BeforeEntities.class, listeners -> context -> {
        for (BeforeEntities listener : listeners) {
            listener.beforeEntities(context);
        }
    });

    public static final Event<AfterEntities> AFTER_ENTITIES = EventFactory.createArrayBacked(AfterEntities.class, listeners -> context -> {
        for (AfterEntities listener : listeners) {
            listener.afterEntities(context);
        }
    });

    public static final Event<DebugRender> BEFORE_DEBUG_RENDER = EventFactory.createArrayBacked(DebugRender.class, listeners -> context -> {
        for (DebugRender listener : listeners) {
            listener.beforeDebugRender(context);
        }
    });

    public static final Event<BeforeTranslucent> BEFORE_TRANSLUCENT = EventFactory.createArrayBacked(BeforeTranslucent.class, listeners -> context -> {
        for (BeforeTranslucent listener : listeners) {
            listener.beforeTranslucent(context);
        }
    });

    /** this frame's render context, or null before the first main pass this class saw */
    public static LevelRenderContext frame() {
        return frame;
    }

    @FunctionalInterface
    public interface BeforeEntities {
        void beforeEntities(LevelRenderContext context);
    }

    @FunctionalInterface
    public interface AfterEntities {
        void afterEntities(LevelRenderContext context);
    }

    @FunctionalInterface
    public interface DebugRender {
        void beforeDebugRender(LevelRenderContext context);
    }

    @FunctionalInterface
    public interface BeforeTranslucent {
        void beforeTranslucent(LevelRenderContext context);
    }
}
