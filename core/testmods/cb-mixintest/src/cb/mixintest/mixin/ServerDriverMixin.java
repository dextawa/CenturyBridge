package cb.mixintest.mixin;

import cb.mixintest.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** runs the test driver once the world is loaded (MinecraftServer.loadLevel, unchanged 1.20.1 - 1.20.3) */
@Mixin(net.minecraft.server.MinecraftServer.class)
public abstract class ServerDriverMixin {
    @Inject(method = "loadLevel", at = @At("TAIL"))
    private void cbtest$run(CallbackInfo ci) {
        Driver.run((net.minecraft.server.MinecraftServer) (Object) this);
    }
}
