package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.class_12199;
import net.minecraft.class_1959;
import net.minecraft.class_4763;
import net.minecraft.class_5483;
import net.minecraft.class_5485;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_11.rt.CbBiomeEffectsLegacy;

/**
 * Biome(climate, attributes, specialEffects, generation, spawns) (class_1959.<init>, javap): records which biome a
 * BiomeSpecialEffects belongs to, so 1.21.10's effects getters answer from that biome's environment attributes
 * (rt/CbBiomeEffectsLegacy). Reads nothing, changes nothing; default priority.
 */
@Mixin(class_1959.class)
public abstract class CbBiomeEffectsOwnerMixinHooks {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cb$v1_21_11$owner(class_1959.class_5482 climate, class_12199 attributes, class_4763 effects,
                                   class_5485 generation, class_5483 spawns, CallbackInfo ci) {
        CbBiomeEffectsLegacy.owner(effects, (class_1959) (Object) this);
    }
}
