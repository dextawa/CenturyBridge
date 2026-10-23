package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_1282;
import net.minecraft.class_1309;
import net.minecraft.class_1665;
import net.minecraft.class_2487;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbEntityArrows;

/**
 * AbstractArrow and what a 1.20.6 caller set on it (CbEntityArrows): a knockback it set is the hit's knockback,
 * as 1.20.6's hit read its knockback field (1.21 asks the weapon's enchantments); a crossbow flag it set is what
 * shotFromCrossbow() answers (1.21 asks whether the weapon is a crossbow), and the save keeps it under 1.20.6's
 * key "ShotFromCrossbow". An arrow nobody set these on is 1.21's alone.
 */
@Mixin(class_1665.class)
public abstract class CbEntityArrowMixin implements CbEntityArrows.ArrowState {

    @Unique
    private Integer cb$knockback;

    @Unique
    private Boolean cb$crossbow;

    @Override
    public Integer cb$knockback() {
        return this.cb$knockback;
    }

    @Override
    public void cb$setKnockback(Integer knockback) {
        this.cb$knockback = knockback;
    }

    @Override
    public Boolean cb$crossbow() {
        return this.cb$crossbow;
    }

    @Override
    public void cb$setCrossbow(Boolean crossbow) {
        this.cb$crossbow = crossbow;
    }

    @Inject(method = "method_59957(Lnet/minecraft/class_1309;Lnet/minecraft/class_1282;)V", at = @At("HEAD"), cancellable = true)
    private void cb$legacyKnockback(class_1309 target, class_1282 source, CallbackInfo ci) {
        if (this.cb$knockback != null) {
            CbEntityArrows.doKnockback((class_1665) (Object) this, target, this.cb$knockback);
            ci.cancel();
        }
    }

    @Inject(method = "method_7456()Z", at = @At("HEAD"), cancellable = true)
    private void cb$legacyCrossbow(CallbackInfoReturnable<Boolean> cir) {
        if (this.cb$crossbow != null) {
            cir.setReturnValue(this.cb$crossbow);
        }
    }

    @Inject(method = "method_5652(Lnet/minecraft/class_2487;)V", at = @At("TAIL"))
    private void cb$saveCrossbow(class_2487 tag, CallbackInfo ci) {
        if (this.cb$crossbow != null) {
            tag.method_10556("ShotFromCrossbow", this.cb$crossbow);
        }
    }

    @Inject(method = "method_5749(Lnet/minecraft/class_2487;)V", at = @At("TAIL"))
    private void cb$readCrossbow(class_2487 tag, CallbackInfo ci) {
        if (tag.method_10545("ShotFromCrossbow")) {
            this.cb$crossbow = tag.method_10577("ShotFromCrossbow");
        }
    }
}
