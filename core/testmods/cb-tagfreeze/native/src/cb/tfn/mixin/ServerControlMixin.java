package cb.tfn.mixin;

import cb.tfn.Control;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** runs the control once the world is loaded (MinecraftServer.loadLevel) */
@Mixin(net.minecraft.server.MinecraftServer.class)
public abstract class ServerControlMixin {
    @Inject(method = "method_3735()V", at = @At("TAIL"), remap = false)
    private void cbtfn$run(CallbackInfo ci) {
        Control.run();
    }
}
