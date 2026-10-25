package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1309;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyEffectInstanceOverride;

/**
 * The injectors into 1.20.2's own methods that go with {@link CbEffectInstanceLegacy} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(class_1293.class)
public abstract class CbEffectInstanceLegacyHooks {

    @Redirect(method = "method_5585", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1291;method_5572(Lnet/minecraft/class_1309;I)V"))
    private void cb$applyEffect(class_1291 effect, class_1309 entity, int amplifier) {
        if ((Object) this instanceof CbLegacyEffectInstanceOverride legacy) {
            legacy.method_5589(entity);
        } else {
            effect.method_5572(entity, amplifier);
        }
    }
}
