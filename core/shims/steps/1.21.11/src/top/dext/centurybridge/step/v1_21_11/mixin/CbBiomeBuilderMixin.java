package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.class_12197;
import net.minecraft.class_1959;
import net.minecraft.class_4763;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_11.rt.CbBiomeEffects;

/**
 * Biome.Builder.specialEffects(effects): for effects a 1.21.10 builder built, the settings 1.21.11 keeps as biome
 * environment attributes are set on this builder (setAttribute), as 1.21.11's own biomes set them
 * (rt/CbBiomeEffects). Effects 1.21.11 code builds carry none: nothing changes for them.
 */
@Mixin(class_1959.class_1960.class)
public abstract class CbBiomeBuilderMixin {
    @Shadow
    public abstract <V> class_1959.class_1960 method_75737(class_12197<V> attribute, V value);

    @Inject(method = "method_24379", at = @At("HEAD"))
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void cb$legacyEffects(class_4763 effects, CallbackInfoReturnable<class_1959.class_1960> cir) {
        for (Object[] a : CbBiomeEffects.attributes(effects)) {
            this.method_75737((class_12197) a[0], a[1]);
        }
    }
}
