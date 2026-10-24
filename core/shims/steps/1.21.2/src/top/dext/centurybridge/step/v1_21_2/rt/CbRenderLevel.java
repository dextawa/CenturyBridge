package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.mixin.CbRenderLevelClientAccess;
import top.dext.centurybridge.step.v1_21_2.mixin.CbRenderLevelEventsInvoker;
import top.dext.centurybridge.step.v1_21_2.mixin.CbRenderLevelRendererAccess;

/**
 * 1.21.1 LevelRenderer members 1.21.2 moved (instanceRedirects / fieldGetters).
 *
 * Level events and jukebox songs: 1.21.2's LevelEventHandler (class_9959), which the ClientLevel owns and 1.21.2's
 * ClientLevel.levelEvent hands them to; the bodies are 1.21.1's, moved (call sequences match: levelEvent =
 * method_62194, globalLevelEvent = method_62187, playJukeboxSong = method_62193, stopJukeboxSongAndNotifyNearby =
 * method_62195). A 1.21.1 LevelRenderer acted on its own level; here that level's handler does.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderLevel {

    private static final net.minecraft.class_2960 CLOUDS_LOCATION = net.minecraft.class_2960.method_60656("textures/environment/clouds.png");

    private CbRenderLevel() {
    }

    private static net.minecraft.class_9959 events(net.minecraft.class_761 renderer) {
        return ((CbRenderLevelClientAccess) ((CbRenderLevelRendererAccess) renderer).cb$level()).cb$levelEvents();
    }

    /** 1.21.1 LevelRenderer.levelEvent(type, pos, data) */
    public static void method_8567(net.minecraft.class_761 renderer, int type, net.minecraft.class_2338 pos, int data) {
        events(renderer).method_62194(type, pos, data);
    }

    /** 1.21.1 LevelRenderer.globalLevelEvent(type, pos, data) */
    public static void method_8564(net.minecraft.class_761 renderer, int type, net.minecraft.class_2338 pos, int data) {
        events(renderer).method_62187(type, pos, data);
    }

    /** 1.21.1 LevelRenderer.playJukeboxSong(song, pos) */
    public static void method_60891(net.minecraft.class_761 renderer, net.minecraft.class_6880<net.minecraft.class_9793> song,
                                    net.minecraft.class_2338 pos) {
        ((CbRenderLevelEventsInvoker) events(renderer)).cb$playJukeboxSong(song, pos);
    }

    /** 1.21.1 LevelRenderer.stopJukeboxSongAndNotifyNearby(pos) */
    public static void method_60889(net.minecraft.class_761 renderer, net.minecraft.class_2338 pos) {
        ((CbRenderLevelEventsInvoker) events(renderer)).cb$stopJukeboxSongAndNotifyNearby(pos);
    }

    /**
     * 1.21.1 LevelRenderer.tickRain(camera): 1.21.2's method_62209(camera) does it -- the weather renderer's rain
     * particles from the level, the camera, the renderer's tick count and the particle option, 1.21.1's inputs
     */
    public static void method_22713(net.minecraft.class_761 renderer, net.minecraft.class_4184 camera) {
        renderer.method_62209(camera);
    }

    /** 1.21.1 LevelRenderer.DIRECTIONS (fieldGetters): Direction.values() */
    public static net.minecraft.class_2350[] field_4095() {
        return net.minecraft.class_2350.values();
    }

    /** 1.21.1 LevelRenderer.CLOUDS_LOCATION (fieldGetters) */
    public static net.minecraft.class_2960 field_4108() {
        return CLOUDS_LOCATION;
    }

    /**
     * 1.21.1 renderLevel(deltaTracker, renderBlockOutline, camera, gameRenderer, lightTexture, frustumMatrix,
     * projectionMatrix): 1.21.2's takes the graphics resource allocator its frame graph draws with first -- the game
     * renderer's resource pool, which 1.21.2's GameRenderer hands it (CbRenderFrames.ResourcePool)
     */
    public static void method_22710(net.minecraft.class_761 renderer, net.minecraft.class_9779 deltaTracker, boolean renderBlockOutline,
                                    net.minecraft.class_4184 camera, net.minecraft.class_757 gameRenderer, net.minecraft.class_765 lightTexture,
                                    org.joml.Matrix4f frustumMatrix, org.joml.Matrix4f projectionMatrix) {
        renderer.method_22710(((CbRenderFrames.ResourcePool) gameRenderer).cb$resourcePool(), deltaTracker, renderBlockOutline, camera, gameRenderer,
            lightTexture, frustumMatrix, projectionMatrix);
    }
}
