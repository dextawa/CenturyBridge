package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbFapiTrades;

/** 1.21.11's trade tables take the server's trade data once its registries and tags are loaded (rt/CbFapiTrades.expand) */
@Mixin(MinecraftServer.class)
public abstract class CbFapiTradeServerMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$expandTrades(CallbackInfo ci) {
        CbFapiTrades.serverLoaded((MinecraftServer) (Object) this);
    }
}
