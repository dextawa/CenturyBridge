package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1282;
import net.minecraft.class_1927;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldExplosionAccess;

/**
 * The injector into 1.20.3's own Explosion.explode that goes with {@link CbWorldExplosionMixin} (its restored 1.20.2
 * member stays there, at {@code CbVanillaReads.PRIORITY}): explode() asks 1.20.2's getDamageSource(), as 1.20.2's did.
 */
@Mixin(class_1927.class)
public abstract class CbWorldExplosionMixinHooks {

    @Redirect(method = "method_8348", at = @At(value = "FIELD", target = "Lnet/minecraft/class_1927;field_9193:Lnet/minecraft/class_1282;"))
    private class_1282 cb$askDamageSource(class_1927 self) {
        return ((CbWorldExplosionAccess) self).cb$getDamageSource();
    }
}
