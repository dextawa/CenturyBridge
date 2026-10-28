package cb.s215n.mixin;

import cb.s215n.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** runs the native driver once the world is loaded (1.21.5 MinecraftServer.loadLevel) */
@Mixin(net.minecraft.server.MinecraftServer.class)
public abstract class ServerDriverMixin {
    @Inject(method = "method_3735()V", at = @At("TAIL"), remap = false)
    private void cbs215n$run(CallbackInfo ci) {
        Driver.run((net.minecraft.server.MinecraftServer) (Object) this);
    }
}
