package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_238;
import net.minecraft.class_4146;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntitySensors;

/** NearestLivingEntitySensor.doTick: a sensor class overriding 1.21.1's radii searches 1.21.1's box (CbEntitySensors). */
@Mixin(class_4146.class)
public abstract class CbEntitySensorMixin {

    @Redirect(method = "method_19101", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_238;method_1009(DDD)Lnet/minecraft/class_238;"))
    private class_238 cb$radii(class_238 box, double x, double y, double z) {
        class_4146 self = (class_4146) (Object) this;
        if (CbEntitySensors.ownRadii(self)) {
            double xz = CbEntitySensors.radiusXZ(self);
            return box.method_1009(xz, (double) CbEntitySensors.radiusY(self), xz);
        }
        return box.method_1009(x, y, z);
    }
}
