package top.dext.centurybridge.compat.fapi;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Function;

import net.fabricmc.fabric.api.client.rendering.v1.level.AbstractLevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * fabric-rendering-v1's WorldRenderEvents as 1.20.x mods register them (15
 * corpus mods), over 26.2's LevelRenderEvents (extraction / submit pipeline).
 * Hook mapping, in the order the 1.20.1 frame fired them:
 * <pre>
 *   START, AFTER_SETUP        &lt;- START_MAIN            (terrain context)
 *   BEFORE_ENTITIES           &lt;- AFTER_OPAQUE_TERRAIN   (terrain context)
 *   AFTER_ENTITIES            &lt;- AFTER_SOLID_FEATURES   (entities are "features" now)
 *   BEFORE_BLOCK_OUTLINE,
 *   BLOCK_OUTLINE             &lt;- BEFORE_BLOCK_OUTLINE   (boolean: false skips the vanilla outline)
 *   BEFORE_DEBUG_RENDER       &lt;- BEFORE_GIZMOS
 *   AFTER_TRANSLUCENT         &lt;- AFTER_TRANSLUCENT_TERRAIN
 *   LAST, END                 &lt;- END_MAIN
 * </pre>
 * Every listener runs under a catch: a 1.20.1 listener that draws through
 * consumers() (immediate-mode, gone) throws NoSuchMethodError on 26.2, and an
 * exception in a render hook would take the client down. Such a listener is
 * logged once and dropped; the rest of the frame and the other mods carry on.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbWorldRenderEvents {

    public static final Event<CbWorldRenderStart> START = event(CbWorldRenderStart.class, ls -> ctx -> each(ls, l -> l.onStart(ctx)));
    public static final Event<CbWorldRenderAfterSetup> AFTER_SETUP = event(CbWorldRenderAfterSetup.class, ls -> ctx -> each(ls, l -> l.afterSetup(ctx)));
    public static final Event<CbWorldRenderBeforeEntities> BEFORE_ENTITIES = event(CbWorldRenderBeforeEntities.class, ls -> ctx -> each(ls, l -> l.beforeEntities(ctx)));
    public static final Event<CbWorldRenderAfterEntities> AFTER_ENTITIES = event(CbWorldRenderAfterEntities.class, ls -> ctx -> each(ls, l -> l.afterEntities(ctx)));
    public static final Event<CbWorldRenderBeforeBlockOutline> BEFORE_BLOCK_OUTLINE = event(CbWorldRenderBeforeBlockOutline.class,
        ls -> (ctx, hit) -> {
            boolean[] go = {true};
            each(ls, l -> {
                if (!l.beforeBlockOutline(ctx, hit)) {
                    go[0] = false;
                }
            });
            return go[0];
        });
    public static final Event<CbWorldRenderBlockOutline> BLOCK_OUTLINE = event(CbWorldRenderBlockOutline.class,
        ls -> (ctx, outline) -> {
            boolean[] go = {true};
            each(ls, l -> {
                if (!l.onBlockOutline(ctx, outline)) {
                    go[0] = false;
                }
            });
            return go[0];
        });
    public static final Event<CbWorldRenderDebugRender> BEFORE_DEBUG_RENDER = event(CbWorldRenderDebugRender.class, ls -> ctx -> each(ls, l -> l.beforeDebugRender(ctx)));
    public static final Event<CbWorldRenderAfterTranslucent> AFTER_TRANSLUCENT = event(CbWorldRenderAfterTranslucent.class, ls -> ctx -> each(ls, l -> l.afterTranslucent(ctx)));
    public static final Event<CbWorldRenderLast> LAST = event(CbWorldRenderLast.class, ls -> ctx -> each(ls, l -> l.onLast(ctx)));
    public static final Event<CbWorldRenderEnd> END = event(CbWorldRenderEnd.class, ls -> ctx -> each(ls, l -> l.onEnd(ctx)));

    private static final Set<Object> DEAD = Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap<>()));

    static {
        LevelRenderEvents.START_MAIN.register(ctx -> {
            CbWorldRenderContext c = wrap(ctx);
            START.invoker().onStart(c);
            AFTER_SETUP.invoker().afterSetup(c);
        });
        LevelRenderEvents.AFTER_OPAQUE_TERRAIN.register(ctx -> BEFORE_ENTITIES.invoker().beforeEntities(wrap(ctx)));
        LevelRenderEvents.AFTER_SOLID_FEATURES.register(ctx -> AFTER_ENTITIES.invoker().afterEntities(wrap(ctx)));
        LevelRenderEvents.BEFORE_BLOCK_OUTLINE.register((ctx, state) -> {
            CbWorldRenderContext c = wrap(ctx);
            Minecraft mc = Minecraft.getInstance();
            HitResult hit = mc.hitResult;
            boolean go = BEFORE_BLOCK_OUTLINE.invoker().beforeBlockOutline(c, hit);
            if (go && state != null) {
                Camera cam = mc.gameRenderer.mainCamera();
                Vec3 p = cam.position();
                ClientLevel level = mc.level;
                BlockPos pos = state.pos();
                BlockState bs = level == null ? null : level.getBlockState(pos);
                go = BLOCK_OUTLINE.invoker().onBlockOutline(c, new CbBlockOutlineContext.Impl(cam.entity(), p.x, p.y, p.z, pos, bs));
            }
            return go;
        });
        LevelRenderEvents.BEFORE_GIZMOS.register(ctx -> BEFORE_DEBUG_RENDER.invoker().beforeDebugRender(wrap(ctx)));
        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(ctx -> AFTER_TRANSLUCENT.invoker().afterTranslucent(wrap(ctx)));
        LevelRenderEvents.END_MAIN.register(ctx -> {
            CbWorldRenderContext c = wrap(ctx);
            LAST.invoker().onLast(c);
            END.invoker().onEnd(c);
        });
    }

    private static CbWorldRenderContext wrap(AbstractLevelRenderContext ctx) {
        return new CbWorldRenderContext.Impl(ctx);
    }

    private static <T> Event<T> event(Class<T> type, Function<T[], T> invokerFactory) {
        return EventFactory.createArrayBacked(type, invokerFactory);
    }

    interface Call<T> {
        void run(T listener);
    }

    /** run every listener; one that throws is logged and never called again (the frame must survive) */
    static <T> void each(T[] listeners, Call<T> call) {
        for (T l : listeners) {
            if (DEAD.contains(l)) {
                continue;
            }
            try {
                call.run(l);
            } catch (Throwable t) {
                DEAD.add(l);
                System.err.println("[CenturyBridge] WorldRenderEvents listener " + l.getClass().getName()
                    + " failed on 26.2 and is disabled: " + t);
            }
        }
    }

    private CbWorldRenderEvents() {
    }
}
