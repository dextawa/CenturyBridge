package cb.s215n.mixin;

import cb.s215n.NativeClientModels;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** runs the native client control on the first client tick after the first resource load (Minecraft.tick) */
@Mixin(net.minecraft.class_310.class)
public abstract class ClientDriverMixin {
    @Inject(method = "method_1574()V", at = @At("HEAD"), remap = false)
    private void cbs215n$client(CallbackInfo ci) {
        NativeClientModels.runOnce((net.minecraft.class_310) (Object) this);
    }
}
