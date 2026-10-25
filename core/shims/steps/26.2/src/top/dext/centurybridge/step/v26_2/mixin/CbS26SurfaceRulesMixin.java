package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_2.rt.CbS26SurfaceRules;

/**
 * 26.1.2's key-tested biome conditions in 26.2's surface rules (rt/CbS26SurfaceRules): isBiome given the layer's
 * key getter answers 26.1.2's condition; any other getter goes on to 26.2's.
 */
@Mixin(SurfaceRules.class)
public abstract class CbS26SurfaceRulesMixin {

    @Inject(method = "isBiome(Lnet/minecraft/core/HolderGetter;[Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/levelgen/SurfaceRules$ConditionSource;",
            at = @At("HEAD"), cancellable = true)
    private static void cbS26$keys(HolderGetter<Biome> getter, ResourceKey<Biome>[] biomes,
                                   CallbackInfoReturnable<SurfaceRules.ConditionSource> cir) {
        SurfaceRules.ConditionSource keys = CbS26SurfaceRules.keyCondition(getter, biomes);
        if (keys != null) {
            cir.setReturnValue(keys);
        }
    }
}
