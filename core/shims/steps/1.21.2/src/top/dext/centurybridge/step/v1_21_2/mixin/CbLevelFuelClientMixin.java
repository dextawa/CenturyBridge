package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelBlockEntities;

/** the client level, whose FuelValues 1.21.1's static furnace fuel API answers from off the server thread */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_638.class)
public abstract class CbLevelFuelClientMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$clientLevel(CallbackInfo ci) {
        CbLevelBlockEntities.clientLevel((net.minecraft.class_1937) (Object) this);
    }
}
