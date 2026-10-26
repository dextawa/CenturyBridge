package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_10583;
import net.minecraft.class_1297;
import net.minecraft.class_1676;
import net.minecraft.class_9109;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_9.rt.CbDeflection;
import top.dext.centurybridge.step.v1_21_9.rt.CbMixedMembers;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * 1.21.8's Projectile.onDeflection(Entity deflector, boolean fromAttack), which 1.21.9 reshaped to
 * onDeflection(boolean): restored with the body 1.21.9 runs -- 1.21.9's onDeflection(fromAttack), so
 * AbstractHurtingProjectile's acceleration change (its 1.21.8 override's body, now in its onDeflection(boolean)) and
 * native 1.21.9 mixins and overrides of onDeflection(boolean) still run. 1.21.8's Projectile body was empty and no
 * vanilla override read the deflector.
 * <p>Route: deflect (1.21.9's only caller, server side, after setting the owner) asks the restored member with its own
 * deflector, instead of calling onDeflection(fromAttack) itself, only for a projectile whose class a converted mod wrote
 * with a 1.21.8 override ({@link CbDeflection.LegacyOnDeflection}) or when a converted mod mixes the restored member;
 * otherwise deflect runs untouched (an injection before the call, not a redirect). deflect answers true after the call
 * either way.
 * <p>Known difference: a RETURN injector on the restored Projectile member, for an AbstractHurtingProjectile, fires after
 * the acceleration change (1.21.8's subclass override changed it after its super call returned).
 */
@Mixin(value = class_1676.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbProjectileDeflectionMixin {

    @Shadow
    protected abstract void method_59525(boolean fromAttack);

    /** Projectile.onDeflection(deflector, fromAttack), 1.21.8's shape */
    @CbVanillaReads("CbProjectileDeflectionMixin: deflect asks it in place of onDeflection(fromAttack) when it is overridden or mixed")
    protected void method_59525(class_1297 deflector, boolean fromAttack) {
        this.method_59525(fromAttack);
    }

    @Inject(method = "method_59859(Lnet/minecraft/class_9109;Lnet/minecraft/class_1297;Lnet/minecraft/class_10583;Z)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1676;method_59525(Z)V"), cancellable = true)
    private void cb$legacyOnDeflection(class_9109 deflection, class_1297 deflector, class_10583<class_1297> owner, boolean fromAttack,
                                       CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof CbDeflection.LegacyOnDeflection || CbMixedMembers.mixed(CbDeflection.ON_DEFLECTION)) {
            this.method_59525(deflector, fromAttack);
            cir.setReturnValue(true);
        }
    }
}
