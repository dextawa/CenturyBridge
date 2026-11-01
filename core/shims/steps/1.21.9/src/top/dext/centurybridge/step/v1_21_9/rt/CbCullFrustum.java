package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_243;
import net.minecraft.class_4604;
import net.minecraft.class_761;
import org.joml.Matrix4f;

/**
 * 1.21.8's LevelRenderer.prepareCullFrustum(cameraPos, frustumMatrix, projection): it built the frame's culling
 * frustum into a field that the rest of the frame culled with. 1.21.9 builds the frustum inside renderLevel and keeps
 * it local. The restored call builds the frustum 1.21.8's body built and puts it in a per-frame slot; the layer's mixin
 * on 1.21.9's prepareCullFrustum (CbCullFrustumMixin) has the frame cull with it when an outside call set it before the
 * frame built its own. The slot is cleared when renderLevel returns, so a frame no outside call touched is 1.21.9's.
 */
@Environment(EnvType.CLIENT)
public final class CbCullFrustum {

    private CbCullFrustum() {
    }

    private static class_4604 slot;

    /** LevelRenderer.prepareCullFrustum(cameraPos, frustumMatrix, projection), 1.21.8's body into the slot */
    public static void method_32133(class_761 renderer, class_243 cameraPos, Matrix4f frustumMatrix, Matrix4f projection) {
        class_4604 frustum = new class_4604(frustumMatrix, projection);
        frustum.method_23088(cameraPos.method_10216(), cameraPos.method_10214(), cameraPos.method_10215());
        slot = frustum;
    }

    /** the frustum an outside call set for this frame, or null */
    public static class_4604 cbSlot() {
        return slot;
    }

    /** the frustum the current (or last) frame culls with, kept at prepareCullFrustum's return (CbCullFrustumMixin) */
    private static class_4604 frame;

    public static void cbFrame(class_4604 f) {
        frame = f;
    }

    /** LevelRenderer.cullingFrustum (field_27740) read: 1.21.8 held the frame's culling frustum there */
    public static class_4604 field_27740(class_761 renderer) {
        return slot != null ? slot : frame;
    }

    /** LevelRenderer.cullingFrustum write: the frame culls with it, as 1.21.8's renderLevel read the field */
    public static void field_27740(class_761 renderer, class_4604 f) {
        slot = f;
        frame = f;
    }

    /** renderLevel's end: the next frame starts unset */
    public static void cbEndFrame() {
        slot = null;
    }
}
