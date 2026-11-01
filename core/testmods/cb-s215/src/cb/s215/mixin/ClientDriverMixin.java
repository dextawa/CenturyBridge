package cb.s215.mixin;

import cb.s215.ClientModels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** runs the client cases on the first client tick after the first resource load (Minecraft.tick) */
@Mixin(net.minecraft.class_310.class)
public abstract class ClientDriverMixin {
    @Inject(method = "method_1574()V", at = @At("HEAD"), remap = false)
    private void cbs215$client(CallbackInfo ci) {
        ClientModels.runOnce((net.minecraft.class_310) (Object) this);
    }
}
