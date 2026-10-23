package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_10225;
import net.minecraft.class_1936;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_4538;
import net.minecraft.class_4864;
import net.minecraft.class_4865;
import net.minecraft.class_5819;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelGrowingPlants;

/**
 * GrowingPlantBodyBlock.updateShape: 1.21.1 asked its head block getStateForPlacement(level) with the updating level,
 * 1.21.2 asks it with the random it is handed. A 1.21.1 head block's override is asked with the level, which 1.21.2
 * hands updateShape as a LevelReader -- every caller's level is the LevelAccessor 1.21.1 passed (a Level, a
 * WorldGenRegion); a reader that is no LevelAccessor has no level of 1.21.1's kind to give, and gets 1.21.2's
 * answer (CbLevelGrowingPlants).
 */
@Mixin(class_4864.class)
public abstract class CbLevelGrowingPlantBodyMixin {

    @Redirect(method = "method_9559", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4865;method_24948(Lnet/minecraft/class_5819;)Lnet/minecraft/class_2680;"))
    private class_2680 cb$headState(class_4865 head, class_5819 random, class_2680 state, class_4538 level, class_10225 ticks,
                                    class_2338 pos, class_2350 direction, class_2338 neighborPos, class_2680 neighborState,
                                    class_5819 rnd) {
        if (head instanceof CbLevelGrowingPlants.LegacyPlacementState legacy && level instanceof class_1936 accessor) {
            return legacy.method_24948(accessor);
        }
        return head.method_24948(random);
    }
}
