package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbEnchantments;

/** Minecraft.disconnect(Screen, boolean): the client's enchantment registry is no world's any more (CbEnchantments) */
@Mixin(net.minecraft.class_310.class)
public abstract class CbEnchClientMixin {

    @Inject(method = "method_18096", at = @At("TAIL"))
    private void cbEnch$disconnected(net.minecraft.class_437 next, boolean keepResourcePacks, CallbackInfo ci) {
        CbEnchantments.onClientDisconnected();
    }
}
