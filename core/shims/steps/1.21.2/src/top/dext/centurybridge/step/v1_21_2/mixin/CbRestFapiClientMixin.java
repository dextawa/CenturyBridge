package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.facade.fapi.ClientWorldEvents;

/** ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE, where 1.21.1's Fabric API fired it (end of updateLevelInEngines, level not null) */
@Environment(EnvType.CLIENT)
@Mixin(class_310.class)
public abstract class CbRestFapiClientMixin {

    @Inject(method = "method_18097", at = @At("TAIL"))
    private void cb$afterWorldChange(class_638 world, CallbackInfo ci) {
        if (world != null) {
            ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.invoker().afterWorldChange((class_310) (Object) this, world);
        }
    }
}
