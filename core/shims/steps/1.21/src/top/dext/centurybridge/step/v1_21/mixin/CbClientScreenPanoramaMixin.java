package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.dext.centurybridge.step.v1_21.rt.CbClientGui;

/**
 * 1.20.6's Screen spun the panorama by advancePanoramaTime() (real time since the screen last asked, kept per
 * screen); 1.21's renderPanorama spins it by the partial tick. A 1.20.6 screen class (CbClientGui.CbLegacyScreen)
 * spins it by its own advancePanoramaTime, 1.20.6's body unless it overrides it.
 */
@Mixin(net.minecraft.class_437.class)
public abstract class CbClientScreenPanoramaMixin implements CbClientGui.CbPanoramaTime {

    @Unique
    private long cb$lastPanoramaTime = net.minecraft.class_156.method_658();

    @Override
    public long cb$lastPanoramaTime() {
        return this.cb$lastPanoramaTime;
    }

    @Override
    public void cb$setLastPanoramaTime(long time) {
        this.cb$lastPanoramaTime = time;
    }

    @ModifyArg(method = "method_57728", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_766;method_3317(Lnet/minecraft/class_332;IIFF)V"), index = 4)
    private float cb$panoramaSpin(float partialTick) {
        return (Object) this instanceof CbClientGui.CbLegacyScreen legacy ? legacy.method_59870() : partialTick;
    }
}
