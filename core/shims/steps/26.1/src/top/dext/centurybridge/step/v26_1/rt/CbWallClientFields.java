package top.dext.centurybridge.step.v26_1.rt;

import java.util.Objects;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;

/**
 * 1.21.11 client fields (opened by access wideners) 26.1 moved (javap of both):
 * <ul>
 * <li>GameRenderer.fovModifier / oldFovModifier (class_757.field_4019 / field_3999): 26.1's Camera.tickFov is 1.21.11's
 *     GameRenderer.tickFov instruction for instruction over Camera's fovModifier / oldFovModifier; the renderer's
 *     camera is getMainCamera().</li>
 * <li>GameRenderer.lastActiveTime (field_3998): the same idle-time stamp is Minecraft.lastActiveTime (set from
 *     Util.getMillis when the window has focus or a mouse button is down, compared with 500 ms the same way).</li>
 * <li>GameRenderer.renderDistance (field_4025): 1.21.11 set it every frame to options.getEffectiveRenderDistance() * 16;
 *     reads go through the restored getRenderDistance(), writes are kept until the next frame's reset
 *     (mixin/CbWallGameRendererRenderDistanceHooks).</li>
 * <li>LevelRenderer.captureFrustum / capturedFrustum (class_761.field_62945 / field_4056): 26.1 keeps both on Camera
 *     (captureFrustum() sets the flag, the frustum is taken in prepareCullFrustum as 1.21.11's renderer did); the
 *     client's main camera.</li>
 * <li>EditBox / MultiLineEditBox CURSOR_INSERT_WIDTH 1, CURSOR_APPEND_CHARACTER "_", CURSOR_BLINK_INTERVAL_MS 300:
 *     26.1's TextCursorUtils constants (same values).</li>
 * <li>EditBox.filter (class_342.field_2104): the filter the layer restores (CbWallEditBoxFilter); a box no mod gave one
 *     answers 1.21.11's default, Objects::nonNull.</li>
 * <li>OptionsScreen.difficultyButton / lockButton (class_429.field_2500 / field_2503): 26.1 keeps both in the screen's
 *     DifficultyButtons record (difficultyButtons, made in init as 1.21.11 made the two fields; null outside a world as
 *     1.21.11's were). Read only: the record is immutable and its refresh reads its own buttons.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbWallClientFields {
    private CbWallClientFields() {
    }

    private static final Predicate<String> DEFAULT_FILTER = Objects::nonNull;

    public static float fovModifier(GameRenderer r) {
        return r.getMainCamera().fovModifier;
    }

    public static void setFovModifier(GameRenderer r, float v) {
        r.getMainCamera().fovModifier = v;
    }

    public static float oldFovModifier(GameRenderer r) {
        return r.getMainCamera().oldFovModifier;
    }

    public static void setOldFovModifier(GameRenderer r, float v) {
        r.getMainCamera().oldFovModifier = v;
    }

    public static long lastActiveTime(GameRenderer r) {
        return Minecraft.getInstance().lastActiveTime;
    }

    public static void setLastActiveTime(GameRenderer r, long v) {
        Minecraft.getInstance().lastActiveTime = v;
    }

    public static float renderDistance(GameRenderer r) {
        return ((CbWallDepthFar) (Object) r).getRenderDistance();
    }

    public static void setRenderDistance(GameRenderer r, float v) {
        ((CbWallRenderDistance) (Object) r).cb$writeRenderDistance(v);
    }

    private static Camera camera() {
        return Minecraft.getInstance().gameRenderer.getMainCamera();
    }

    public static boolean captureFrustum(LevelRenderer r) {
        return camera().captureFrustum;
    }

    public static void setCaptureFrustum(LevelRenderer r, boolean v) {
        camera().captureFrustum = v;
    }

    public static Frustum capturedFrustum(LevelRenderer r) {
        return camera().capturedFrustum;
    }

    public static void setCapturedFrustum(LevelRenderer r, Frustum v) {
        camera().capturedFrustum = v;
    }

    public static int cursorInsertWidth() {
        return 1;
    }

    public static String cursorAppendCharacter() {
        return "_";
    }

    public static int cursorBlinkIntervalMs() {
        return 300;
    }

    /**
     * Minecraft.cameraEntity (class_310.field_1719): 26.1's getCameraEntity() / setCameraEntity() keep it as the main
     * camera's entity (Camera.entity); a raw field write in 1.21.11 skipped setCameraEntity's post-effect check, as
     * Camera.setEntity does.
     */
    public static net.minecraft.world.entity.Entity cameraEntity(Minecraft mc) {
        return camera().entity();
    }

    public static void setCameraEntity(Minecraft mc, net.minecraft.world.entity.Entity e) {
        camera().setEntity(e);
    }

    /**
     * Minecraft.windowActive (class_310.field_1695): 1.21.11 set it from the window focus callback; 26.1's
     * isWindowActive() asks the window's focus directly. Read only (a write would not reach the window).
     */
    public static boolean windowActive(Minecraft mc) {
        return mc.isWindowActive();
    }

    @SuppressWarnings("unchecked")
    public static Predicate filter(EditBox box) {
        Predicate<String> f = ((CbWallEditBoxFilter) box).cb$filter();
        return f != null ? f : DEFAULT_FILTER;
    }

    @SuppressWarnings("unchecked")
    public static void setFilter(EditBox box, Predicate v) {
        ((CbWallEditBoxFilter) box).cb$setFilter(v);
    }

    @SuppressWarnings("rawtypes")
    public static CycleButton optionsDifficultyButton(OptionsScreen s) {
        return s.difficultyButtons == null ? null : s.difficultyButtons.difficultyButton();
    }

    public static LockIconButton optionsLockButton(OptionsScreen s) {
        return s.difficultyButtons == null ? null : s.difficultyButtons.lockButton();
    }
}
