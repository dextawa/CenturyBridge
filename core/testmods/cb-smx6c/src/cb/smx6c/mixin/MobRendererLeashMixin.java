package cb.smx6c.mixin;

import cb.smx6c.Counters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.20.6's MobRenderer.addVertexPair (method_23187, private static: one leash segment's two vertices), which 1.21 moved,
 * same body, up into EntityRenderer (method_61050; step wiring s121.json mixinMoves). HEAD counts each call.
 */
@Mixin(net.minecraft.class_927.class)
public abstract class MobRendererLeashMixin {

    @Inject(method = "method_23187", at = @At("HEAD"))
    private static void cb$leashVertex(CallbackInfo ci) {
        Counters.LEASH.incrementAndGet();
    }
}
