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
 * method_62195). A 1.21.1 LevelRenderer acted on its own level; here that level's handler does. The 1.21.1 methods
 * are declared on LevelRenderer (CbRenderLevelMixin), and 1.21.2's handler hands its calls to a mod subclass's
 * overrides (CbRenderLevelEventsMixin, {@link #routes}). tickRain is 1.21.2's method_62209 under its 1.21.1 name
 * (methodRenames / overrideRenames).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderLevel {

    private static final net.minecraft.class_2960 CLOUDS_LOCATION = net.minecraft.class_2960.method_60656("textures/environment/clouds.png");

    private CbRenderLevel() {
    }

    private static net.minecraft.class_9959 events(net.minecraft.class_761 renderer) {
        return ((CbRenderLevelClientAccess) ((CbRenderLevelRendererAccess) renderer).cb$level()).cb$levelEvents();
    }

    /** LevelRenderer's 1.21.1 level-event members (CbRenderLevelMixin declares them; a mod subclass may override them) */
    public interface LegacyEvents {
        void method_8567(int type, net.minecraft.class_2338 pos, int data);

        void method_8564(int type, net.minecraft.class_2338 pos, int data);

        void method_60891(net.minecraft.class_6880<net.minecraft.class_9793> song, net.minecraft.class_2338 pos);

        void method_60889(net.minecraft.class_2338 pos);
    }

    public static final int LEVEL_EVENT = 1;
    public static final int GLOBAL_LEVEL_EVENT = 2;
    public static final int PLAY_SONG = 4;
    public static final int STOP_SONG = 8;

    /** the routed members whose 1.21.1 method is running on this thread (its super call reaches 1.21.2's handler, which then runs as is) */
    private static final ThreadLocal<int[]> ROUTING = ThreadLocal.withInitial(() -> new int[1]);

    /** the 1.21.1 level-event bodies: the level's handler, as 1.21.1's LevelRenderer acted on its own level */
    public static net.minecraft.class_9959 events(net.minecraft.class_638 level) {
        return ((CbRenderLevelClientAccess) level).cb$levelEvents();
    }

    public static void levelEvent(net.minecraft.class_638 level, int type, net.minecraft.class_2338 pos, int data) {
        events(level).method_62194(type, pos, data);
    }

    public static void globalLevelEvent(net.minecraft.class_638 level, int type, net.minecraft.class_2338 pos, int data) {
        events(level).method_62187(type, pos, data);
    }

    public static void playJukeboxSong(net.minecraft.class_638 level, net.minecraft.class_6880<net.minecraft.class_9793> song, net.minecraft.class_2338 pos) {
        ((CbRenderLevelEventsInvoker) events(level)).cb$playJukeboxSong(song, pos);
    }

    public static void stopJukeboxSong(net.minecraft.class_638 level, net.minecraft.class_2338 pos) {
        ((CbRenderLevelEventsInvoker) events(level)).cb$stopJukeboxSongAndNotifyNearby(pos);
    }

    /**
     * Whether 1.21.2's handler hands this member to the renderer's 1.21.1 method (CbRenderLevelEventsMixin): 1.21.1's
     * ClientLevel / LevelRenderer.levelEvent called the renderer's levelEvent / globalLevelEvent / playJukeboxSong /
     * stopJukeboxSongAndNotifyNearby, so a mod LevelRenderer subclass's override ran there; 1.21.2 calls its handler. Only
     * for a subclass (vanilla's own renderer runs the handler as is) and not while that member's 1.21.1 method is already
     * running on this thread (its super call then reaches the handler, which runs vanilla's body).
     */
    public static boolean routes(net.minecraft.class_761 renderer, int member) {
        return renderer != null && renderer.getClass() != net.minecraft.class_761.class && (ROUTING.get()[0] & member) == 0;
    }

    /** run a mod renderer's 1.21.1 member with its routing held (see {@link #routes}) */
    public static void route(int member, Runnable call) {
        int[] held = ROUTING.get();
        int before = held[0];
        held[0] = before | member;
        try {
            call.run();
        } finally {
            held[0] = before;
        }
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

    /*
     * 1.21.1's private Fabulous render-target fields of LevelRenderer (read by mods' accessors). 1.21.2 keeps these
     * targets in the frame's LevelTargetBundle; its public getters (the same getTranslucentTarget / getItemEntityTarget /
     * ... 1.21.1 had, which returned exactly these fields) answer them, so the fields read as the getters do.
     */

    public static net.minecraft.class_276 field_25274(net.minecraft.class_761 renderer) {
        return renderer.method_29360();
    }

    public static net.minecraft.class_276 field_25275(net.minecraft.class_761 renderer) {
        return renderer.method_29361();
    }

    public static net.minecraft.class_276 field_25276(net.minecraft.class_761 renderer) {
        return renderer.method_29362();
    }

    public static net.minecraft.class_276 field_25277(net.minecraft.class_761 renderer) {
        return renderer.method_29363();
    }

    public static net.minecraft.class_276 field_25278(net.minecraft.class_761 renderer) {
        return renderer.method_29364();
    }
}
