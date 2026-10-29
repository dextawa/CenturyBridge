package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4604;
import net.minecraft.class_863;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10 DebugRenderer.render(pose, frustum, buffers, x, y, z, translucent): LevelRenderer called it twice a frame,
 * the opaque renderer list then the translucent one, each drawing into the buffers. 1.21.11 replaced it with
 * emitGizmos(frustum, x, y, z, partialTick), called once a frame, which has every renderer emit gizmos the level
 * renderer draws itself (javap -c both). The opaque call emits the frame's gizmos with the partial tick LevelRenderer
 * passes (the delta tracker's, frozen tick not ignored); the translucent call has nothing left to do (1.21.11's one
 * emission covers both lists). Pose and buffers are no longer drawn into. 1.21.11 never calls it.
 */
@Mixin(value = class_863.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbDebugRendererRestoredMixin {

    /** DebugRenderer.render(pose, frustum, buffers, x, y, z, translucent) */
    public void method_23099(class_4587 pose, class_4604 frustum, class_4597.class_4598 buffers, double x, double y, double z,
        boolean translucent) {
        if (!translucent) {
            ((class_863) (Object) this).method_23099(frustum, x, y, z, class_310.method_1551().method_61966().method_60637(false));
        }
    }
}
