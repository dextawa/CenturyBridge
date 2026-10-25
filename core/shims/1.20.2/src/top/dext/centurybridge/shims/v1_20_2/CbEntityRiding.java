package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1297;
import net.minecraft.class_4048;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyRiding;
import top.dext.centurybridge.rt.v1_20_2.CbRiding;

/**
 * 1.20.1's riding offsets back on Entity (s1misc riding bridge): getPassengersRidingOffset and
 * getMyRidingOffset answer 1.20.1's formula of the vanilla class the entity is (CbRiding), so a
 * call, a converted subclass's override and its super call all behave as in 1.20.1. 1.20.2 calls
 * neither; a converted override reaches 1.20.2's riding through the override hooks
 * (CbRiding.vehicleAttachment / passengerOffset).
 */
@Mixin(value = class_1297.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbEntityRiding implements CbLegacyRiding {

    @Shadow
    protected abstract Vector3f method_52533(class_1297 passenger, class_4048 dimensions, float scale);

    @Override
    public double method_5621() {
        return CbRiding.vanillaVehicleOffset((class_1297) (Object) this);
    }

    @Override
    public double method_5678() {
        return CbRiding.vanillaPassengerOffset((class_1297) (Object) this);
    }

    @Override
    public Vector3f cb$passengerAttachment(class_1297 passenger, class_4048 dimensions, float scale) {
        return this.method_52533(passenger, dimensions, scale);
    }
}
