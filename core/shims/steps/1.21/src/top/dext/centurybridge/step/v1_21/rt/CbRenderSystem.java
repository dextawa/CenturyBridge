package top.dext.centurybridge.step.v1_21.rt;

import java.util.function.IntSupplier;
import java.util.function.Supplier;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21.mixin.CbRenderSystemAccess;

/**
 * 1.20.6's RenderSystem members 1.21 dropped, with 1.20.6's bodies. 1.20.6 already answered isOnGameThread() and
 * isInInitPhase() with true -- so its game-thread and init-phase asserts never threw -- and kept a game thread
 * only initGameThread set and nothing read; 1.21 removed them. The buffer / vertex array binds took suppliers of
 * the id; setupOverlayColor bound the overlay texture to unit 1; _setShaderLights wrote the two light directions
 * with no thread check.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderSystem {

    private CbRenderSystem() {
    }

    private static Thread gameThread;

    /** initGameThread(assumeSingleThreaded) of 1.20.6 */
    public static synchronized void initGameThread(boolean assumeSingleThreaded) {
        Thread renderThread = CbRenderSystemAccess.cb$renderThread();
        boolean onRenderThread = renderThread == Thread.currentThread();
        if (gameThread != null || renderThread == null || onRenderThread != assumeSingleThreaded) {
            throw new IllegalStateException("Could not initialize tick thread");
        }
        gameThread = Thread.currentThread();
    }

    /** isOnGameThread() of 1.20.6 */
    public static boolean isOnGameThread() {
        return true;
    }

    /** isInInitPhase() of 1.20.6 */
    public static boolean isInInitPhase() {
        return true;
    }

    /** assertInInitPhase() of 1.20.6 */
    public static void assertInInitPhase() {
        if (!isInInitPhase()) {
            throw cbThreadException();
        }
    }

    /** assertOnGameThread() of 1.20.6 */
    public static void assertOnGameThread() {
        if (!isOnGameThread()) {
            throw cbThreadException();
        }
    }

    /** assertOnGameThreadOrInit() of 1.20.6 (1.20.6 read its isInInit flag, then isOnGameThread) */
    public static void assertOnGameThreadOrInit() {
        if (!isOnGameThread()) {
            throw cbThreadException();
        }
    }

    /** constructThreadException() of 1.20.6 */
    private static IllegalStateException cbThreadException() {
        return new IllegalStateException("Rendersystem called from wrong thread");
    }

    /** _setShaderLights(first, second) of 1.20.6 */
    public static void _setShaderLights(org.joml.Vector3f first, org.joml.Vector3f second) {
        org.joml.Vector3f[] lights = CbRenderSystemAccess.cb$shaderLightDirections();
        lights[0] = first;
        lights[1] = second;
    }

    /** glBindBuffer(target, buffer) of 1.20.6 */
    public static void glBindBuffer(int target, IntSupplier buffer) {
        GlStateManager._glBindBuffer(target, buffer.getAsInt());
    }

    /** glBindVertexArray(array) of 1.20.6 */
    public static void glBindVertexArray(Supplier<Integer> array) {
        GlStateManager._glBindVertexArray(array.get());
    }

    /** setupOverlayColor(texture, size) of 1.20.6: the texture on unit 1 */
    public static void setupOverlayColor(IntSupplier texture, int size) {
        RenderSystem.assertOnRenderThread();
        int id = texture.getAsInt();
        RenderSystem.setShaderTexture(1, id);
    }
}
