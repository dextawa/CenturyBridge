package cb.smx6.mixin;

import cb.smx6.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A 1.20.6 mod's mixins on BadOmenMobEffect's private raid start (method_58613(ServerPlayer, ServerLevel)Z, 1.20.6's path
 * for worlds without update_1_21): a cancellable HEAD that keeps Bad Omen (no raid) while Driver.blockRaid is set, and an
 * INVOKE-anchored injector on its Raids.createOrExtendRaid call (class_3767.method_16540), counting raids it starts.
 */
@Mixin(targets = "net.minecraft.class_8631")
public abstract class BadOmenMixin {

    @Inject(method = "method_58613", at = @At("HEAD"), cancellable = true)
    private void cbsmx6$raidAsked(net.minecraft.class_3222 player, net.minecraft.class_3218 level, CallbackInfoReturnable<Boolean> cir) {
        Driver.badOmenAsked++;
        if (Driver.blockRaid) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "method_58613", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_3767;method_16540(Lnet/minecraft/class_3222;Lnet/minecraft/class_2338;)Lnet/minecraft/class_3765;"))
    private void cbsmx6$raidStarted(net.minecraft.class_3222 player, net.minecraft.class_3218 level, CallbackInfoReturnable<Boolean> cir) {
        Driver.badOmenRaids++;
    }
}
