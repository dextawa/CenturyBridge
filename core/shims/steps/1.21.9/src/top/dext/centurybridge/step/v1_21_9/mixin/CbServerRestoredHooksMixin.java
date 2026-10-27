package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_9.rt.CbPrioritySplit;

/**
 * The injector split off CbServerRestoredMixin (default priority, so it keeps 1.21.9's order among mods' injectors):
 * once 1.21.8 code set the flight flag on a non-dedicated server, isFlightAllowed answers it.
 */
@Mixin(MinecraftServer.class)
public abstract class CbServerRestoredHooksMixin {

    @Inject(method = "method_3718()Z", at = @At("HEAD"), cancellable = true)
    private void cb$flight(CallbackInfoReturnable<Boolean> cir) {
        Boolean flight = ((CbPrioritySplit.ServerFlight) (Object) this).cb$v1_21_9$flightAllowed();
        if (flight != null) {
            cir.setReturnValue(flight);
        }
    }
}
