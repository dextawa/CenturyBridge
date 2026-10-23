package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityMisc;

/**
 * Entity fields 1.21.1 had and 1.21.2 dropped, kept as 1.21.1 kept them, and the final isAlliedTo handing the call to
 * a mod class's own 1.21.1 override (CbEntityMisc):
 * <ul>
 * <li>walkDist (field_5973) grew by the movement's horizontal distance x 0.6 where 1.21.1's move() did it, beside
 *     moveDist -- 1.21.2's applyMovementEmissionAndPlaySound, the same block, the same movement; walkDistO
 *     (field_6039) took walkDist at the start of baseTick;</li>
 * <li>noCulling (field_5985): the entity's own switch, read by the renderer (CbEntityCullingMixin).</li>
 * </ul>
 */
@Mixin(class_1297.class)
public abstract class CbEntityMiscMixin implements CbEntityMisc.Fields {

    public float field_5973;

    public float field_6039;

    public boolean field_5985;

    @Override
    public boolean cb$noCulling() {
        return this.field_5985;
    }

    @Inject(method = "method_61407", at = @At("HEAD"))
    private void cb$walkDist(class_1297.class_5799 emission, class_243 movement, net.minecraft.class_2338 pos,
                             net.minecraft.class_2680 state, CallbackInfo ci) {
        this.field_5973 += (float) movement.method_37267() * 0.6F;
    }

    @Inject(method = "method_5670", at = @At("HEAD"))
    private void cb$walkDistO(CallbackInfo ci) {
        this.field_6039 = this.field_5973;
    }

    @Inject(method = "method_5722", at = @At("HEAD"), cancellable = true)
    private void cb$isAlliedTo(class_1297 other, CallbackInfoReturnable<Boolean> cir) {
        Boolean r = CbEntityMisc.allied((class_1297) (Object) this, other);
        if (r != null) {
            cir.setReturnValue(r);
        }
    }
}
