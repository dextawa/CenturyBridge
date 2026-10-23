package top.dext.centurybridge.step.v1_21.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.6's frame timing on 1.21. 1.20.6 handed renderers a float partial tick (Gui.render, LayeredDraw,
 * GameRenderer.render / renderLevel, LevelRenderer.renderLevel) and Minecraft answered getFrameTime() /
 * getDeltaFrameTime() off its Timer; 1.21 hands them a DeltaTracker (the game's DeltaTracker.Timer), which
 * keeps the same numbers: its deltaTicks is 1.20.6's tickDelta, its deltaTickResidual 1.20.6's
 * partialTick, saved and restored around a pause exactly as 1.20.6 saved partialTick into
 * pausePartialTick, and getGameTimeDeltaPartialTick(true) is 1.20.6's "pause ? pausePartialTick :
 * timer.partialTick" -- the float 1.20.6's GameRenderer passed down the frame.
 */
@Environment(EnvType.CLIENT)
public final class CbClientTime {

    private CbClientTime() {
    }

    private static net.minecraft.class_9779 timer() {
        return net.minecraft.class_310.method_1551().method_60646();
    }

    // ------------------------------------------------------------------ Minecraft.getFrameTime / getDeltaFrameTime

    /** Minecraft.getFrameTime() of 1.20.6: the timer's partialTick, as kept (not the paused copy) */
    public static float cbFrameTime(net.minecraft.class_310 mc) {
        net.minecraft.class_9779 t = mc.method_60646();
        if (t instanceof top.dext.centurybridge.step.v1_21.mixin.CbClientDeltaTimerAccess a) {
            return a.cb$deltaTickResidual();
        }
        return t.method_60637(true);
    }

    /** Minecraft.getDeltaFrameTime() of 1.20.6: the timer's tickDelta, 1.21's game-time delta ticks */
    public static float cbDeltaFrameTime(net.minecraft.class_310 mc) {
        return mc.method_60646().method_60636();
    }

    /**
     * A 1.20.6 Minecraft subclass's getFrameTime / getDeltaFrameTime were what 1.20.6's renderers read;
     * 1.21's read the subclass's getTimer(). The subclass's getTimer answers with its own two values
     * (1.21's pause and freeze rules applied to them as 1.21 applies them to its own residual).
     */
    public interface CbLegacyFrameTime {
        default float method_1488() {
            return cbFrameTime((net.minecraft.class_310) this);
        }

        default float method_1534() {
            return cbDeltaFrameTime((net.minecraft.class_310) this);
        }
    }

    public static net.minecraft.class_9779 cbLegacyTimer(CbLegacyFrameTime mc) {
        net.minecraft.class_9779 own = ((top.dext.centurybridge.step.v1_21.mixin.CbClientMinecraftTimerAccess) mc).cb$timer();
        return new net.minecraft.class_9779() {
            @Override
            public float method_60636() {
                return mc.method_1534();
            }

            @Override
            public float method_60637(boolean runsNormally) {
                float vanilla = own.method_60637(runsNormally);
                float raw = own instanceof top.dext.centurybridge.step.v1_21.mixin.CbClientDeltaTimerAccess a
                    ? a.cb$deltaTickResidual() : vanilla;
                float legacy = mc.method_1488();
                // the subclass's own partial tick where it answers one, else 1.21's pause/freeze-aware value
                return legacy == raw ? vanilla : legacy;
            }

            @Override
            public float method_60638() {
                return own.method_60638();
            }
        };
    }

    // ------------------------------------------------------------------ float partial ticks <-> DeltaTracker

    /**
     * The DeltaTracker 1.21 passes, as the float 1.20.6 passed in its place: the partial tick
     * getGameTimeDeltaPartialTick(true) answers (1.20.6's pause-aware partial tick; 1.21 applies a tick
     * freeze inside its renderers as 1.20.6's did).
     */
    public static float cbPartialTick(net.minecraft.class_9779 tracker) {
        return tracker.method_60637(true);
    }

    /**
     * A 1.20.6 caller's partial tick, as the DeltaTracker 1.21 takes: the game's own when the value is its
     * partial tick (a 1.20.6 method handing on what it was given), else one answering that partial tick and
     * the game's own deltas (1.20.6's renderers read those off Minecraft, not off an argument).
     */
    public static net.minecraft.class_9779 cbTracker(float partialTick) {
        net.minecraft.class_9779 game = timer();
        if (game.method_60637(true) == partialTick) {
            return game;
        }
        return new LegacyPartialTick(partialTick, game);
    }

    private static final class LegacyPartialTick implements net.minecraft.class_9779 {
        private final float partialTick;
        private final net.minecraft.class_9779 game;

        LegacyPartialTick(float partialTick, net.minecraft.class_9779 game) {
            this.partialTick = partialTick;
            this.game = game;
        }

        @Override
        public float method_60636() {
            return game.method_60636();
        }

        /** a frozen game is a whole tick for game-time animation, as 1.21's own timer answers it */
        @Override
        public float method_60637(boolean runsNormally) {
            if (!runsNormally && game instanceof top.dext.centurybridge.step.v1_21.mixin.CbClientDeltaTimerAccess a && a.cb$frozen()) {
                return 1.0F;
            }
            return partialTick;
        }

        @Override
        public float method_60638() {
            return game.method_60638();
        }
    }

    // ------------------------------------------------------------------ the frame's start time

    private static long frameNanos = net.minecraft.class_156.method_648();

    /** Minecraft.runTick's start (mixin/CbClientMinecraftFrameMixin): 1.20.6 took Util.getNanos() there */
    public static void cbFrameStarted() {
        frameNanos = net.minecraft.class_156.method_648();
    }

    /** the nanoTime 1.20.6's runTick handed GameRenderer.render, renderLevel and LevelRenderer.renderLevel */
    public static long cbFrameNanos() {
        return frameNanos;
    }

    // ------------------------------------------------------------------ renderers' override bridges

    /** GameRenderer.render(partialTick, nanoTime, renderLevel) of 1.20.6, as a subclass declares it */
    public interface CbLegacyGameRender {
        void method_3192(float partialTick, long nanoTime, boolean renderLevel);
    }

    public static void cbGameRender(CbLegacyGameRender renderer, net.minecraft.class_9779 tracker, boolean renderLevel) {
        renderer.method_3192(cbPartialTick(tracker), cbFrameNanos(), renderLevel);
    }

    /** GameRenderer.renderLevel(partialTick, nanoTime) of 1.20.6 */
    public interface CbLegacyGameRenderLevel {
        void method_3188(float partialTick, long nanoTime);
    }

    public static void cbGameRenderLevel(CbLegacyGameRenderLevel renderer, net.minecraft.class_9779 tracker) {
        renderer.method_3188(cbPartialTick(tracker), cbFrameNanos());
    }

    /** LevelRenderer.renderLevel(partialTick, nanoTime, outline, camera, gameRenderer, lightTexture, pose, projection) of 1.20.6 */
    public interface CbLegacyLevelRender {
        void method_22710(float partialTick, long nanoTime, boolean renderBlockOutline, net.minecraft.class_4184 camera,
                          net.minecraft.class_757 gameRenderer, net.minecraft.class_765 lightTexture,
                          org.joml.Matrix4f pose, org.joml.Matrix4f projection);
    }

    public static void cbLevelRender(CbLegacyLevelRender renderer, net.minecraft.class_9779 tracker, boolean renderBlockOutline,
                                     net.minecraft.class_4184 camera, net.minecraft.class_757 gameRenderer,
                                     net.minecraft.class_765 lightTexture, org.joml.Matrix4f pose, org.joml.Matrix4f projection) {
        renderer.method_22710(cbPartialTick(tracker), cbFrameNanos(), renderBlockOutline, camera, gameRenderer, lightTexture,
            pose, projection);
    }

    // ------------------------------------------------------------------ LayeredDraw.Layer lambdas

    /** a 1.20.6 LayeredDraw.Layer lambda: render(GuiGraphics, float partialTick) */
    @FunctionalInterface
    public interface CbLegacyLayer {
        void render(net.minecraft.class_332 graphics, float partialTick);
    }

    public static net.minecraft.class_9080.class_9081 cbLayer(CbLegacyLayer layer) {
        return (graphics, tracker) -> layer.render(graphics, cbPartialTick(tracker));
    }
}
