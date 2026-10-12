package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1922;
import net.minecraft.class_2279;
import net.minecraft.class_2283;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldChorusPlantAccess;

/**
 * 1.20.2's ChorusFlowerBlock grew its plant through plant.getStateForPlacement(level, pos), a virtual call a
 * plant subclass could answer; 1.20.3 calls ChorusPlantBlock.getStateWithConnections with the plant's default
 * state. The growth asks the plant again (CbWorldChorusPlantMixin's 1.20.2 method): the same state for a
 * vanilla plant, the subclass's own for a 1.20.2 one.
 */
@Mixin(class_2279.class)
public abstract class CbWorldChorusFlowerMixin {

    @Redirect(method = "method_9514", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_2283;method_9759(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;Lnet/minecraft/class_2680;)Lnet/minecraft/class_2680;"))
    private class_2680 cb$askPlant(class_1922 level, class_2338 pos, class_2680 state) {
        if (state.method_26204() instanceof class_2283 plant && state == plant.method_9564()) {
            return ((CbWorldChorusPlantAccess) plant).cb$getStateForPlacement(level, pos);
        }
        return class_2283.method_9759(level, pos, state);
    }
}
