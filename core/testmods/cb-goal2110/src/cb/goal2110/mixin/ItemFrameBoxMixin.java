package cb.goal2110.mixin;

import cb.goal2110.Driver;
import net.minecraft.class_1533;
import net.minecraft.class_238;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** The one -0.46875 site in 1.21.9's ItemFrame.method_59943. */
@Mixin(class_1533.class)
public abstract class ItemFrameBoxMixin {
    @Inject(
        method = "method_59943(Lnet/minecraft/class_2338;Lnet/minecraft/class_2350;)Lnet/minecraft/class_238;",
        at = @At(value = "CONSTANT", args = {"doubleValue=-0.46875"}),
        remap = false
    )
    private void cbgoal2110$hit(CallbackInfoReturnable<class_238> cir) {
        Driver.hits++;
    }
}
