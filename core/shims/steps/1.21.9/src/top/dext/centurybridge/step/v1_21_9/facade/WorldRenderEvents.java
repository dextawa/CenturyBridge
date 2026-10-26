package top.dext.centurybridge.step.v1_21_9.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.class_239;
import top.dext.centurybridge.step.v1_21_9.rt.CbWorldRender;

/**
 * Fabric API 0.136's (1.21.8) WorldRenderEvents, which Fabric API for 1.21.9 deleted: the same ten events with the
 * same listener interfaces, invokers and empty invokers as 0.136's class. Converted mods reach it through the step's
 * classRenames. 1.21.9's LevelRenderer fires them through the layer's CbWorldRenderEventsMixin at the points 0.136's
 * WorldRendererMixin fired them; the mixin acts only once this class has initialized (a mod touched the API), so a
 * game no converted mod asked for these events renders exactly 1.21.9's frame.
 */
@Environment(EnvType.CLIENT)
public final class WorldRenderEvents {

    private WorldRenderEvents() {
    }

    private static final Start EMPTY_START = context -> { };
    private static final AfterSetup EMPTY_AFTER_SETUP = context -> { };
    private static final BeforeEntities EMPTY_BEFORE_ENTITIES = context -> { };
    private static final AfterEntities EMPTY_AFTER_ENTITIES = context -> { };
    private static final BeforeBlockOutline EMPTY_BEFORE_BLOCK_OUTLINE = (context, hitResult) -> true;
    private static final BlockOutline EMPTY_BLOCK_OUTLINE = (worldRenderContext, blockOutlineContext) -> true;
    private static final DebugRender EMPTY_DEBUG_RENDER = context -> { };
    private static final AfterTranslucent EMPTY_AFTER_TRANSLUCENT = context -> { };
    private static final Last EMPTY_LAST = context -> { };
    private static final End EMPTY_END = context -> { };

    public static final Event<Start> START = EventFactory.createArrayBacked(Start.class, EMPTY_START, callbacks -> context -> {
        for (Start callback : callbacks) {
            callback.onStart(context);
        }
    });

    public static final Event<AfterSetup> AFTER_SETUP = EventFactory.createArrayBacked(AfterSetup.class, EMPTY_AFTER_SETUP, callbacks -> context -> {
        for (AfterSetup callback : callbacks) {
            callback.afterSetup(context);
        }
    });

    public static final Event<BeforeEntities> BEFORE_ENTITIES = EventFactory.createArrayBacked(BeforeEntities.class, EMPTY_BEFORE_ENTITIES, callbacks -> context -> {
        for (BeforeEntities callback : callbacks) {
            callback.beforeEntities(context);
        }
    });

    public static final Event<AfterEntities> AFTER_ENTITIES = EventFactory.createArrayBacked(AfterEntities.class, EMPTY_AFTER_ENTITIES, callbacks -> context -> {
        for (AfterEntities callback : callbacks) {
            callback.afterEntities(context);
        }
    });

    /** every listener is asked; the outline renders only when none answered false (0.136's invoker) */
    public static final Event<BeforeBlockOutline> BEFORE_BLOCK_OUTLINE = EventFactory.createArrayBacked(BeforeBlockOutline.class, EMPTY_BEFORE_BLOCK_OUTLINE, callbacks -> (context, hitResult) -> {
        boolean shouldRender = true;
        for (BeforeBlockOutline callback : callbacks) {
            if (!callback.beforeBlockOutline(context, hitResult)) {
                shouldRender = false;
            }
        }
        return shouldRender;
    });

    /** every listener is asked; vanilla's outline draws only when none answered false (0.136's invoker) */
    public static final Event<BlockOutline> BLOCK_OUTLINE = EventFactory.createArrayBacked(BlockOutline.class, EMPTY_BLOCK_OUTLINE, callbacks -> (worldRenderContext, blockOutlineContext) -> {
        boolean shouldRender = true;
        for (BlockOutline callback : callbacks) {
            if (!callback.onBlockOutline(worldRenderContext, blockOutlineContext)) {
                shouldRender = false;
            }
        }
        return shouldRender;
    });

    public static final Event<DebugRender> BEFORE_DEBUG_RENDER = EventFactory.createArrayBacked(DebugRender.class, EMPTY_DEBUG_RENDER, callbacks -> context -> {
        for (DebugRender callback : callbacks) {
            callback.beforeDebugRender(context);
        }
    });

    public static final Event<AfterTranslucent> AFTER_TRANSLUCENT = EventFactory.createArrayBacked(AfterTranslucent.class, EMPTY_AFTER_TRANSLUCENT, callbacks -> context -> {
        for (AfterTranslucent callback : callbacks) {
            callback.afterTranslucent(context);
        }
    });

    public static final Event<Last> LAST = EventFactory.createArrayBacked(Last.class, EMPTY_LAST, callbacks -> context -> {
        for (Last callback : callbacks) {
            callback.onLast(context);
        }
    });

    public static final Event<End> END = EventFactory.createArrayBacked(End.class, EMPTY_END, callbacks -> context -> {
        for (End callback : callbacks) {
            callback.onEnd(context);
        }
    });

    static {
        CbWorldRender.cbActivate();
    }

    /**
     * Whether a listener is registered on AFTER_TRANSLUCENT: an array-backed event answers its empty invoker exactly
     * while it has no listener. The layer adds 0.136's afterTranslucent frame pass only then, so the frame graph of a
     * frame nobody listens to is 1.21.9's.
     */
    public static boolean cbAfterTranslucentListened() {
        return AFTER_TRANSLUCENT.invoker() != EMPTY_AFTER_TRANSLUCENT;
    }

    @FunctionalInterface
    public interface Start {
        void onStart(WorldRenderContext context);
    }

    @FunctionalInterface
    public interface AfterSetup {
        void afterSetup(WorldRenderContext context);
    }

    @FunctionalInterface
    public interface BeforeEntities {
        void beforeEntities(WorldRenderContext context);
    }

    @FunctionalInterface
    public interface AfterEntities {
        void afterEntities(WorldRenderContext context);
    }

    @FunctionalInterface
    public interface BeforeBlockOutline {
        boolean beforeBlockOutline(WorldRenderContext context, class_239 hitResult);
    }

    @FunctionalInterface
    public interface BlockOutline {
        boolean onBlockOutline(WorldRenderContext worldRenderContext, WorldRenderContext.BlockOutlineContext blockOutlineContext);
    }

    @FunctionalInterface
    public interface DebugRender {
        void beforeDebugRender(WorldRenderContext context);
    }

    @FunctionalInterface
    public interface AfterTranslucent {
        void afterTranslucent(WorldRenderContext context);
    }

    @FunctionalInterface
    public interface Last {
        void onLast(WorldRenderContext context);
    }

    @FunctionalInterface
    public interface End {
        void onEnd(WorldRenderContext context);
    }
}
