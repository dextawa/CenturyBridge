package top.dext.centurybridge.step.v1_21_6.mixin;

import net.minecraft.class_243;
import net.minecraft.class_846;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * 1.21.5's SectionRenderDispatcher.getCameraPosition() (method_19420), restored: 1.21.5 answered field_18766, which the
 * constructor set to Vec3.ZERO and setCameraPosition (method_19419) to its argument -- its only writers (javap). 1.21.6
 * keeps that field and setter and reads the field directly where 1.21.5's RenderSection and its compile task called the
 * getter. The answer is kept in a field of the layer's own, set at setCameraPosition's return (so a mod cancelling the
 * setter leaves it unchanged, as the field was): 26.1 makes SectionRenderDispatcher.cameraPosition an AtomicReference and
 * a shadow of field_18766 would not climb (climb_mixin_check). Priority below every mod's.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
@Mixin(value = class_846.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbSectionDispatcherRestoredMixin {

    @Unique
    private class_243 cb$cameraPosition = class_243.field_1353;

    @Inject(method = "method_19419(Lnet/minecraft/class_243;)V", at = @At("RETURN"))
    private void cb$recordCameraPosition(class_243 position, CallbackInfo ci) {
        this.cb$cameraPosition = position;
    }

    public class_243 method_19420() {
        return this.cb$cameraPosition;
    }
}
