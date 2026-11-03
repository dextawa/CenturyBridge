package cb.tf.mixin;

import cb.tf.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** runs the driver once the world is loaded (MinecraftServer.loadLevel) */
@Mixin(net.minecraft.server.MinecraftServer.class)
public abstract class ServerDriverMixin {
    @Inject(method = "method_3735()V", at = @At("TAIL"), remap = false)
    private void cbtf$run(CallbackInfo ci) {
        Driver.run();
    }
}
