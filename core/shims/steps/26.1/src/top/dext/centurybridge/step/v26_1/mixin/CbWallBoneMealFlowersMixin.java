package top.dext.centurybridge.step.v26_1.mixin;

import java.util.List;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbWallFeatures;

/** a biome's 1.21.11 FLOWER features grow from bone meal too, as 1.21.11's getFlowerFeatures had them (rt/CbWallFeatures) */
@Mixin(BiomeGenerationSettings.class)
public abstract class CbWallBoneMealFlowersMixin {

    @Shadow
    public abstract List<HolderSet<PlacedFeature>> features();

    @Inject(method = "getBoneMealFeatures", at = @At("RETURN"), cancellable = true)
    private void cb$withLegacyFlowers(CallbackInfoReturnable<List<ConfiguredFeature<?, ?>>> cir) {
        cir.setReturnValue(CbWallFeatures.withFlowers(cir.getReturnValue(), this.features()));
    }
}
