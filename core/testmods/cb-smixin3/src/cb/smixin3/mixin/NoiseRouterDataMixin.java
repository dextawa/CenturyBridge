package cb.smixin3.mixin;

import cb.smixin3.Driver;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * 26.1.2: NoiseRouterData.peaksAndValleys scales by 3.0F; this doubles the scale. 26.2's peaksAndValleys calls
 * TerrainProvider.peaksAndValleys (another class, also called by TerrainProvider's own spline code), which holds the
 * constant: the converter moves this into a guarded mixin on TerrainProvider (guarded descend, stage 2).
 */
@Mixin(NoiseRouterData.class)
public abstract class NoiseRouterDataMixin {
    @ModifyConstant(method = "peaksAndValleys(F)F", remap = false, constant = @Constant(floatValue = 3.0F))
    private static float cbsmixin3$scale(float v) {
        Driver.scaleHits++;
        return v * 2.0F;
    }
}
