package cb.skspawnn.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.server.MinecraftServer.class)
public abstract class ServerDriverMixin {
    @Inject(method = "method_3735()V", at = @At("TAIL"), remap = false)
    private void cbskspawnn$run(CallbackInfo ci) {
        cb.skspawnn.Driver.run();
    }
}
