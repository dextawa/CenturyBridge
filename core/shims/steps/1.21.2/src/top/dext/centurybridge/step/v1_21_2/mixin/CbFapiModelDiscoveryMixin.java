package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbFapiModels;

/** the ModelDiscovery of this thread, for 1.21.1 Fabric API's getOrLoadModel (CbFapiModels) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10097.class)
public abstract class CbFapiModelDiscoveryMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$current(CallbackInfo ci) {
        CbFapiModels.discovery((net.minecraft.class_10097) (Object) this);
    }
}
