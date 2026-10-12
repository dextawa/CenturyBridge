package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_5819;
import net.minecraft.class_7113;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbEntityAnimals;

/**
 * FrogspawnBlock.getRandomTadpolePositionOffset: 1.20.6 placed hatching tadpoles from Tadpole.HITBOX_WIDTH as it
 * stood, 1.21 from the constant. Once a 1.20.6 mod has written the width, the offset is 1.20.6's on that width;
 * until then 1.21's own body runs (the same numbers).
 */
@Mixin(class_7113.class)
public abstract class CbEntityFrogspawnMixin {

    @Inject(method = "method_44015(Lnet/minecraft/class_5819;)D", at = @At("HEAD"), cancellable = true)
    private void cb$tadpoleOffset(class_5819 random, CallbackInfoReturnable<Double> cir) {
        if (CbEntityAnimals.tadpoleHitboxWidthWritten()) {
            cir.setReturnValue(CbEntityAnimals.tadpolePositionOffset(random));
        }
    }
}
