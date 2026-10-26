package top.dext.centurybridge.step.v26_1.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.phys.HitResult;
import top.dext.centurybridge.step.v26_1.rt.CbWorldRenderHome;

/**
 * Fabric API 0.136's (1.21.8) WorldRenderEvents, deleted by Fabric API for 1.21.9 and restored by the 1.21.9 layer
 * (top.dext.centurybridge.step.v1_21_9.facade.WorldRenderEvents). That copy fires from 1.21.9's renderLevel, whose
 * start died at 1.21.11 -> 26.1 (renderLevel's head, the main pass moved into a lambda), so no event of it fires on
 * 26.1. This is its 26.1 home: the same ten events, listener interfaces, invokers and empty invokers as 0.136's class;
 * the wall step's classRenames move converted mods (and the climbing 1.21.9 layer) from the 1.21.9 copy to this one,
 * and its retiredClasses take the 1.21.9 copy and its firing mixin out. 26.1's LevelRenderer fires them through
 * mixin/CbWorldRenderHomeMixin at the points of 26.1's frame that match 0.136's; nothing there runs before this class
 * initialized (a converted mod touched the API), so a game no converted mod asked for these events renders exactly
 * 26.1's frame.
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
        CbWorldRenderHome.cbActivate();
    }

    /**
     * Whether a listener is registered on AFTER_TRANSLUCENT: an array-backed event answers its empty invoker exactly
     * while it has no listener. 0.136's afterTranslucent frame pass is added only then, so the frame graph of a frame
     * nobody listens to is 26.1's.
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
        boolean beforeBlockOutline(WorldRenderContext context, HitResult hitResult);
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
