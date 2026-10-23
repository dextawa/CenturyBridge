package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1750;
import net.minecraft.class_2680;
import net.minecraft.class_4863;
import net.minecraft.class_5819;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelGrowingPlants;

/**
 * GrowingPlantBlock.getStateForPlacement(context): 1.21.1 asked getStateForPlacement(level) of the placing level,
 * 1.21.2 asks it with that level's random; a 1.21.1 override is asked with the level (CbLevelGrowingPlants).
 */
@Mixin(class_4863.class)
public abstract class CbLevelGrowingPlantMixin {

    @Redirect(method = "method_9605", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4863;method_24948(Lnet/minecraft/class_5819;)Lnet/minecraft/class_2680;"))
    private class_2680 cb$placementState(class_4863 self, class_5819 random, class_1750 context) {
        if (self instanceof CbLevelGrowingPlants.LegacyPlacementState legacy) {
            return legacy.method_24948(context.method_8045());
        }
        return self.method_24948(random);
    }
}
