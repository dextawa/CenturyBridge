package cb.smx6.mixin;

import cb.smx6.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A 1.20.6 mod's mixins on Mob's leash save state: a cancellable HEAD on the private restoreLeashFromSave (method_5940),
 * which keeps the saved leash unrestored while Driver.blockLeashRestore is set, an INVOKE-anchored injector on its
 * setLeashedTo call (method_5954), counting restores, and the private leashHolder / delayedLeashInfo fields read and
 * written through @Shadow (LeashSaveAccess).
 */
@Mixin(net.minecraft.class_1308.class)
public abstract class LeashSaveMixin implements cb.smx6.LeashSaveAccess {

    @Shadow
    private net.minecraft.class_1297 field_6202;

    @Shadow
    private com.mojang.datafixers.util.Either<java.util.UUID, net.minecraft.class_2338> field_6192;

    @Inject(method = "method_5940", at = @At("HEAD"), cancellable = true)
    private void cbsmx6$restoreAsked(CallbackInfo ci) {
        Driver.leashRestoreAsked++;
        if (Driver.blockLeashRestore) {
            ci.cancel();
        }
    }

    @Inject(method = "method_5940", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1308;method_5954(Lnet/minecraft/class_1297;Z)V"))
    private void cbsmx6$restored(CallbackInfo ci) {
        Driver.leashRestored++;
    }

    @Override
    public net.minecraft.class_1297 cbsmx6$shadowHolder() {
        return this.field_6202;
    }

    @Override
    public com.mojang.datafixers.util.Either<java.util.UUID, net.minecraft.class_2338> cbsmx6$shadowDelayed() {
        return this.field_6192;
    }

    @Override
    public void cbsmx6$shadowSetDelayed(com.mojang.datafixers.util.Either<java.util.UUID, net.minecraft.class_2338> info) {
        this.field_6192 = info;
    }
}
