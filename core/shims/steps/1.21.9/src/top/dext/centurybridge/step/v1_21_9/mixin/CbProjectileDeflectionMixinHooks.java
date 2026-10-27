package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_10583;
import net.minecraft.class_1297;
import net.minecraft.class_1676;
import net.minecraft.class_9109;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_9.rt.CbDeflection;
import top.dext.centurybridge.step.v1_21_9.rt.CbMixedMembers;
import top.dext.centurybridge.step.v1_21_9.rt.CbPrioritySplit;

/**
 * The injector into 1.21.9's own Projectile.deflect that goes with {@link CbProjectileDeflectionMixin} (its restored
 * 1.21.8 onDeflection(deflector, fromAttack) stays there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.8
 * injectors can anchor inside it): before deflect's call of onDeflection(fromAttack), the restored member is asked with
 * deflect's own deflector instead (CbPrioritySplit.ProjectileOnDeflection, virtually), only for a projectile whose class
 * a converted mod wrote with a 1.21.8 override ({@link CbDeflection.LegacyOnDeflection}) or when a converted mod mixes the
 * restored member; deflect then answers true, as it does after the call. Otherwise deflect runs untouched.
 */
@Mixin(class_1676.class)
public abstract class CbProjectileDeflectionMixinHooks {

    @Inject(method = "method_59859(Lnet/minecraft/class_9109;Lnet/minecraft/class_1297;Lnet/minecraft/class_10583;Z)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1676;method_59525(Z)V"), cancellable = true)
    private void cb$legacyOnDeflection(class_9109 deflection, class_1297 deflector, class_10583<class_1297> owner, boolean fromAttack,
                                       CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof CbDeflection.LegacyOnDeflection || CbMixedMembers.mixed(CbDeflection.ON_DEFLECTION)) {
            ((CbPrioritySplit.ProjectileOnDeflection) (Object) this).cb$v1_21_9$onDeflection(deflector, fromAttack);
            cir.setReturnValue(true);
        }
    }
}
