package top.dext.centurybridge.step.v1_20_3.mixin;

import java.util.Set;
import net.minecraft.class_1293;
import net.minecraft.class_1667;
import net.minecraft.class_1799;
import net.minecraft.class_1842;
import net.minecraft.class_2487;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldArrowAccess;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldArrows;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldLegacyArrow;

/**
 * Arrow.getPickupItem the 1.20.2 way for the arrows 1.20.2 code built: those a 1.20.2 constructor call made
 * (marked by CbWorldArrows.newArrow) and every Arrow subclass converted from 1.20.2 (CbWorldLegacyArrow). The
 * mark is saved with the arrow, so a reloaded arrow is still picked up as 1.20.2 picked it up.
 */
@Mixin(class_1667.class)
public abstract class CbWorldArrowMixin implements CbWorldArrowAccess {

    @Shadow
    private class_1842 field_7597;

    @Shadow
    @Final
    private Set<class_1293> field_7598;

    @Shadow
    private boolean field_7596;

    @Shadow
    public abstract int method_7460();

    @Unique
    private boolean cb$legacyPickup;

    @Override
    public void cb$markLegacyPickup() {
        this.cb$legacyPickup = true;
    }

    @Inject(method = "method_7445", at = @At("HEAD"), cancellable = true)
    private void cb$legacyPickupItem(CallbackInfoReturnable<class_1799> cir) {
        if (this.cb$legacyPickup || ((Object) this) instanceof CbWorldLegacyArrow) {
            cir.setReturnValue(CbWorldArrows.legacyPickupItem(this.field_7597, this.field_7598, this.field_7596, this.method_7460()));
        }
    }

    @Inject(method = "method_5652", at = @At("RETURN"))
    private void cb$saveLegacyPickup(class_2487 tag, CallbackInfo ci) {
        if (this.cb$legacyPickup) {
            tag.method_10556(CbWorldArrows.LEGACY_PICKUP_TAG, true);
        }
    }

    @Inject(method = "method_5749", at = @At("RETURN"))
    private void cb$loadLegacyPickup(class_2487 tag, CallbackInfo ci) {
        if (tag.method_10577(CbWorldArrows.LEGACY_PICKUP_TAG)) {
            this.cb$legacyPickup = true;
        }
    }
}
