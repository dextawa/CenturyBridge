package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelBlockEntities;

/** the running server, whose FuelValues 1.21.1's static furnace fuel API answers from (CbLevelBlockEntities) */
@Mixin(net.minecraft.server.MinecraftServer.class)
public abstract class CbLevelFuelServerMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$server(CallbackInfo ci) {
        CbLevelBlockEntities.server((net.minecraft.server.MinecraftServer) (Object) this);
    }
}
