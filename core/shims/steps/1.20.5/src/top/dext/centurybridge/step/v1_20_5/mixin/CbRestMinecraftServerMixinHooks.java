package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplit;

/**
 * The injector into 1.20.5's own MinecraftServer.logTickMethodTime that goes with {@link CbRestMinecraftServerMixin}
 * (its restored 1.20.4 members stay there, at {@code CbVanillaReads.PRIORITY}): 1.20.4's logTickTime hook called with
 * the tick's duration.
 */
@Mixin(net.minecraft.server.MinecraftServer.class)
public abstract class CbRestMinecraftServerMixinHooks {

    @Inject(method = "method_56602", at = @At("HEAD"))
    private void cbRest$logTickTime(long tickStart, CallbackInfo ci) {
        ((CbPrioritySplit.ServerTickTime) (Object) this).cb$v1_20_5$logTickTime(net.minecraft.class_156.method_648() - tickStart);
    }
}
