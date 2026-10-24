package cb.s7gear.mixin;

import java.util.Arrays;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1856;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * a 1.21.1 mod mixin on Ingredient.getItems(): an ingredient that takes sticks also takes diamonds
 * (GearTestMod.restoredMixins)
 */
@Mixin(class_1856.class)
public abstract class IngredientItemsMixin {

    @Inject(method = "method_8105()[Lnet/minecraft/class_1799;", at = @At("RETURN"), cancellable = true)
    private void cbs7gear$diamondForStick(CallbackInfoReturnable<class_1799[]> cir) {
        class_1799[] stacks = cir.getReturnValue();
        boolean stick = false;
        for (class_1799 s : stacks) {
            stick |= s.method_31574(class_1802.field_8600);
        }
        if (stick) {
            class_1799[] more = Arrays.copyOf(stacks, stacks.length + 1);
            more[stacks.length] = new class_1799(class_1802.field_8477);
            cir.setReturnValue(more);
        }
    }
}
