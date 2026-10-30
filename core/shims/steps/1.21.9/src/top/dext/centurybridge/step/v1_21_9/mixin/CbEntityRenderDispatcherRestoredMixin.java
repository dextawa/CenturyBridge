package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_10017;
import net.minecraft.class_1297;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_898;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_9.rt.CbSubmit;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * EntityRenderDispatcher.distanceToSqr(x, y, z), which 1.21.9 dropped: 1.21.8's body (the squared distance from the
 * dispatcher's camera position; 1.21.9 keeps the camera field and still reads distanceToSqr(entity) from it). 1.21.9
 * never calls it.
 * <p>render(entity, x, y, z, partialTick, poseStack, buffers, light), which 1.21.9 replaced by extract-then-submit:
 * 1.21.8 extracted the entity's render state (renderer.createRenderState(entity, partialTick)) and rendered it with
 * the given light at x/y/z plus the renderer's offset; here the state is 1.21.9's extractEntity (method_72977) with the
 * given light, submitted by 1.21.9's submit (method_72976, the same offset) to the collector behind the buffers
 * (rt/CbSubmit: the capture's queue, else drawn into the buffers at once). 1.21.9 never calls it.
 */
@Mixin(value = class_898.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityRenderDispatcherRestoredMixin {

    @Shadow
    public class_4184 field_4686;

    /** EntityRenderDispatcher.distanceToSqr(x, y, z) */
    public double method_3959(double x, double y, double z) {
        return this.field_4686.method_19326().method_1028(x, y, z);
    }

    /** EntityRenderDispatcher.render(entity, x, y, z, partialTick, poseStack, buffers, light) */
    public <E extends class_1297> void method_62424(E entity, double x, double y, double z, float partialTick, class_4587 poseStack,
                                                     class_4597 buffers, int light) {
        class_898 self = (class_898) (Object) this;
        class_10017 state = self.method_72977(entity, partialTick);
        state.field_61820 = light;
        self.method_72976(state, CbSubmit.camera(buffers), x, y, z, poseStack, CbSubmit.collector(buffers));
    }
}
