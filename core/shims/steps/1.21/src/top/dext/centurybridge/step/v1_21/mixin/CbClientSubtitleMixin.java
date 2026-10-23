package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbClientGui;

/**
 * SubtitleOverlay.Subtitle with 1.20.6's getLocation() / getTime() (the last refresh, as 1.20.6 kept it); for a
 * 1.20.6 subclass, 1.21's overlay calls (getClosest, purgeOldInstances, isStillActive) answer from the subclass's
 * own getters, the way 1.20.6's overlay read them (arrow from getLocation(), dropped 3 s times the display time
 * after getTime()). 1.21 has no Subtitle subclass of its own, so a subclass is a 1.20.6 one.
 */
@Mixin(targets = "net.minecraft.class_359$class_360")
public abstract class CbClientSubtitleMixin {

    @Unique
    private net.minecraft.class_243 cb$location;

    @Unique
    private long cb$time;

    @Unique
    private double cb$displayMs = 3000.0;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$made(net.minecraft.class_2561 text, float range, net.minecraft.class_243 location, CallbackInfo ci) {
        this.cb$location = location;
        this.cb$time = net.minecraft.class_156.method_658();
    }

    @Inject(method = "method_1958", at = @At("HEAD"))
    private void cb$refreshed(net.minecraft.class_243 location, CallbackInfo ci) {
        this.cb$location = location;
        this.cb$time = net.minecraft.class_156.method_658();
    }

    /** getLocation() of 1.20.6 */
    public net.minecraft.class_243 method_1959() {
        return this.cb$location;
    }

    /** getTime() of 1.20.6 */
    public long method_1961() {
        return this.cb$time;
    }

    @Unique
    private boolean cb$legacySubclass() {
        return !this.getClass().getName().equals("net.minecraft.class_359$class_360");
    }

    @Inject(method = "method_60572", at = @At("HEAD"), cancellable = true)
    private void cb$closest(net.minecraft.class_243 listener, CallbackInfoReturnable<Object> cir) {
        if (cb$legacySubclass()) {
            cir.setReturnValue(CbClientGui.cbPlayedAt(this.method_1959(), this.method_1961()));
        }
    }

    @Inject(method = "method_60570", at = @At("HEAD"))
    private void cb$purge(double displayMs, CallbackInfo ci) {
        this.cb$displayMs = displayMs;
    }

    @Inject(method = "method_60574", at = @At("HEAD"), cancellable = true)
    private void cb$active(CallbackInfoReturnable<Boolean> cir) {
        if (cb$legacySubclass()) {
            cir.setReturnValue((double) this.method_1961() + this.cb$displayMs > (double) net.minecraft.class_156.method_658());
        }
    }
}
