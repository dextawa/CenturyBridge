package cb.smx6n.mixin;

import cb.smx6n.Driver;
import net.minecraft.class_793;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.21's BlockModel.bakeFace(element, face, sprite, direction, state): one call per element face 1.21 bakes. */
@Mixin(class_793.class)
public abstract class BakeFaceCounter {
    @Inject(method = "method_3447", at = @At("HEAD"))
    private static void cb$face(CallbackInfoReturnable<?> cir) {
        Driver.FACES.incrementAndGet();
    }
}
