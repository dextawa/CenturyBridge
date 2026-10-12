package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbLegacyRegistries;

/** Minecraft.disconnect(Screen, boolean): the client's world registries are no world's any more (CbLegacyRegistries) */
@Mixin(net.minecraft.class_310.class)
public abstract class CbLegacyRegistryClientMixin {

    @Inject(method = "method_18096", at = @At("TAIL"))
    private void cbLegacy$disconnected(net.minecraft.class_437 next, boolean keepResourcePacks, CallbackInfo ci) {
        CbLegacyRegistries.onClientDisconnected();
    }
}
