package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_4184;
import net.minecraft.class_898;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * EntityRenderDispatcher.distanceToSqr(x, y, z), which 1.21.9 dropped: 1.21.8's body (the squared distance from the
 * dispatcher's camera position; 1.21.9 keeps the camera field and still reads distanceToSqr(entity) from it). 1.21.9
 * never calls it.
 */
@Mixin(value = class_898.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityRenderDispatcherRestoredMixin {

    @Shadow
    public class_4184 field_4686;

    /** EntityRenderDispatcher.distanceToSqr(x, y, z) */
    public double method_3959(double x, double y, double z) {
        return this.field_4686.method_19326().method_1028(x, y, z);
    }
}
