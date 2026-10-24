package top.dext.centurybridge.step.v1_20_5.mixin;

import net.minecraft.class_1295;
import net.minecraft.class_1844;
import net.minecraft.class_2394;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinParticles;

/**
 * AreaEffectCloud.setParticle of a 1.20.4 entity effect particle type: 1.20.4's cloud tinted an entity effect with
 * its color (the potion's, 0 without one, as its getColor answered); 1.20.5's cloud keeps an entity_effect option, so
 * it is given one in that color at the marker's alpha. (An ambient entity effect cloud's particles took 1.20.4's
 * per-particle random speeds as their color; one option holds one color, the cloud's.)
 */
@Mixin(class_1295.class)
public abstract class CbFinParticleCloudMixin {

    @Shadow
    private class_1844 field_49209;

    @ModifyVariable(method = "method_5608", at = @At("HEAD"), argsOnly = true)
    private class_2394 cb$legacyEffect(class_2394 options) {
        if (options instanceof CbFinParticles.LegacyEffect effect) {
            int color = this.field_49209.equals(class_1844.field_49274) ? 0 : this.field_49209.method_8064() & 0xFFFFFF;
            return CbFinParticles.colored(effect, (color >> 16 & 0xFF) / 255.0, (color >> 8 & 0xFF) / 255.0, (color & 0xFF) / 255.0);
        }
        return options;
    }
}
