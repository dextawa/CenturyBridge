package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_238;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestBoundingBox;

/** Entity.makeBoundingBox(pos) hands the call to a 1.21.3 subclass's makeBoundingBox() override (CbRestBoundingBox). */
@Mixin(net.minecraft.class_1297.class)
public abstract class CbRestBoundingBoxEntityMixin {

    @Inject(method = "method_65341", at = @At("HEAD"), cancellable = true)
    private void cb$legacyBox(class_243 pos, CallbackInfoReturnable<class_238> cir) {
        class_238 box = CbRestBoundingBox.legacyBox(this, pos);
        if (box != null) {
            cir.setReturnValue(box);
        }
    }
}
