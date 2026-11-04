package cb.smx1dn.mixin;

import cb.smx1dn.Probe;
import net.minecraft.class_777;
import net.minecraft.class_796;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** native control: 1.21.11 FaceBakery.bakeQuad(interner, ..) HEAD, counted on the render thread (model baking runs elsewhere) */
@Mixin(class_796.class)
public class NativeBakeQuadMixin {
    @Inject(method = "method_3468", at = @At("HEAD"))
    private static void cbSmx1dn$bake(CallbackInfoReturnable<class_777> cir) {
        Probe.bumpRender("bakeQuad");
    }
}
