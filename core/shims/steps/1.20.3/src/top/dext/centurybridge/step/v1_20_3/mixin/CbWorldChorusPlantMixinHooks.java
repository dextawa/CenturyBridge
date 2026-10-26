package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1922;
import net.minecraft.class_2283;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldChorusPlantAccess;

/**
 * The injector into 1.20.3's own ChorusPlantBlock.getStateForPlacement(context) that goes with
 * {@link CbWorldChorusPlantMixin} (its restored 1.20.2 member stays there, at {@code CbVanillaReads.PRIORITY}): the
 * plant's placement asks 1.20.2's getStateForPlacement(level, pos), as 1.20.2's did.
 */
@Mixin(class_2283.class)
public abstract class CbWorldChorusPlantMixinHooks {

    @Redirect(method = "method_9605", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_2283;method_9759(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;)Lnet/minecraft/class_2680;"))
    private class_2680 cb$askPlant(class_1922 level, class_2338 pos, class_2680 state) {
        return state == ((class_2283) (Object) this).method_9564() ? ((CbWorldChorusPlantAccess) (Object) this).cb$getStateForPlacement(level, pos)
            : class_2283.method_9759(level, pos, state);
    }
}
