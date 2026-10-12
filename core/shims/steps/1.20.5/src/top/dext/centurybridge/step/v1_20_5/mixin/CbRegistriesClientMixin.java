package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbRegistries;
import top.dext.centurybridge.step.v1_20_5.rt.CbRegistriesClient;

/** Tells CbRegistries how the client answers: its thread, and its world's registries (none outside a world). */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_310.class)
public abstract class CbRegistriesClientMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cb$registries(CallbackInfo ci) {
        CbRegistries.client(new CbRegistriesClient((net.minecraft.class_310) (Object) this));
    }
}
