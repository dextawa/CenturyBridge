package cb.smx6n.mixin;

import cb.smx6n.Driver;
import net.minecraft.class_796;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.21's FaceBakery.bakeQuad(from, to, face, sprite, direction, state, rotation, shade): HEAD, one per face baked. */
@Mixin(class_796.class)
public abstract class BakeQuadCounter {
    @Inject(method = "method_3468(Lorg/joml/Vector3f;Lorg/joml/Vector3f;Lnet/minecraft/class_783;Lnet/minecraft/class_1058;"
        + "Lnet/minecraft/class_2350;Lnet/minecraft/class_3665;Lnet/minecraft/class_789;Z)Lnet/minecraft/class_777;", at = @At("HEAD"))
    private void cb$quad(CallbackInfoReturnable<?> cir) {
        Driver.QUADS.incrementAndGet();
    }
}
