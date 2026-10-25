package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_1295;
import net.minecraft.class_2394;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.dext.centurybridge.step.v1_21_9.rt.CbParticleOptions;

/**
 * AreaEffectCloud.setCustomParticle(options): the cloud saves and syncs its particle through the options' codecs; a
 * 1.21.8 type that 1.21.9 gave options of their own (CbParticleOptions.Legacy, e.g. 1.21.8's dragon breath cloud) is
 * stored as those options. Any other options pass unchanged.
 */
@Mixin(class_1295.class)
public abstract class CbAreaEffectCloudMixin {

    @ModifyVariable(method = "method_5608(Lnet/minecraft/class_2394;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private class_2394 cb$legacyOptions(class_2394 options) {
        return CbParticleOptions.current(options);
    }
}
