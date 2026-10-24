package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.compat.fapi.client.event.lifecycle.v1.ClientWorldEvents;

/** 1.21.1 Fabric API's AFTER_CLIENT_WORLD_CHANGE, fired as 1.21.1's MinecraftClientMixin fired it (setWorld TAIL, world != null) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_310.class)
public abstract class CbFapiClientWorldMixin {

    @Inject(method = "method_18097", at = @At("TAIL"))
    private void cb$afterClientWorldChange(net.minecraft.class_638 world, CallbackInfo ci) {
        if (world != null) {
            ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.invoker().afterWorldChange((net.minecraft.class_310) (Object) this, world);
        }
    }
}
