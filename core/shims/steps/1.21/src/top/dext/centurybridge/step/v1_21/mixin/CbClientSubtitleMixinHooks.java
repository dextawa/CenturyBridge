package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbClientGui;
import top.dext.centurybridge.step.v1_21.rt.CbPrioritySplit;

/**
 * The injectors into 1.21's own SubtitleOverlay.Subtitle methods that go with {@link CbClientSubtitleMixin} (its
 * restored 1.20.6 members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.6 injectors can anchor inside
 * them): the location and time kept at construction and refresh; for a 1.20.6 subclass, getClosest and isStillActive
 * answer from its own getters.
 */
@Mixin(targets = "net.minecraft.class_359$class_360")
public abstract class CbClientSubtitleMixinHooks {

    @Unique
    private double cb$displayMs = 3000.0;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$made(net.minecraft.class_2561 text, float range, net.minecraft.class_243 location, CallbackInfo ci) {
        ((CbPrioritySplit.Subtitle) (Object) this).cb$v1_21$played(location, net.minecraft.class_156.method_658());
    }

    @Inject(method = "method_1958", at = @At("HEAD"))
    private void cb$refreshed(net.minecraft.class_243 location, CallbackInfo ci) {
        ((CbPrioritySplit.Subtitle) (Object) this).cb$v1_21$played(location, net.minecraft.class_156.method_658());
    }

    @Unique
    private boolean cb$legacySubclass() {
        // the class literal, not its name string: it follows Subtitle as the layer climbs (the wall renames it)
        return (Object) this.getClass() != net.minecraft.class_359.class_360.class;
    }

    @Inject(method = "method_60572", at = @At("HEAD"), cancellable = true)
    private void cb$closest(net.minecraft.class_243 listener, CallbackInfoReturnable<Object> cir) {
        if (cb$legacySubclass()) {
            CbPrioritySplit.Subtitle subtitle = (CbPrioritySplit.Subtitle) (Object) this;
            cir.setReturnValue(CbClientGui.cbPlayedAt(subtitle.cb$v1_21$location(), subtitle.cb$v1_21$time()));
        }
    }

    @Inject(method = "method_60570", at = @At("HEAD"))
    private void cb$purge(double displayMs, CallbackInfo ci) {
        this.cb$displayMs = displayMs;
    }

    @Inject(method = "method_60574", at = @At("HEAD"), cancellable = true)
    private void cb$active(CallbackInfoReturnable<Boolean> cir) {
        if (cb$legacySubclass()) {
            cir.setReturnValue((double) ((CbPrioritySplit.Subtitle) (Object) this).cb$v1_21$time() + this.cb$displayMs
                > (double) net.minecraft.class_156.method_658());
        }
    }
}
