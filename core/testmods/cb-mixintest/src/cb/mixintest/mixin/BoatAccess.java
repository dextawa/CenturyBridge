package cb.mixintest.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Boat's hurt time, moved up to VehicleEntity and renamed in 1.20.3 (step 2 methodRenames) */
@Mixin(net.minecraft.class_1690.class)
public interface BoatAccess {
    @Invoker("setHurtTime")
    void cbtest$setHurtTime(int t);

    /** no value: Mixin inflects "getHurtTime" from the name */
    @Invoker
    int invokeGetHurtTime();
}
