package cb.smx6n.mixin;

import cb.smx6n.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.21's EntityRenderer.addVertexPair (method_61050): one call per leash vertex pair 1.21 draws */
@Mixin(net.minecraft.class_897.class)
public abstract class LeashVertexCounter {
    @Inject(method = "method_61050", at = @At("HEAD"))
    private static void cb$leashVertex(CallbackInfo ci) {
        Driver.LEASH.incrementAndGet();
    }
}
