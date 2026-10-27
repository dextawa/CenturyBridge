package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1676;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_9.rt.CbPrioritySplit;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * 1.21.8's Projectile.onDeflection(Entity deflector, boolean fromAttack), which 1.21.9 reshaped to
 * onDeflection(boolean): restored with the body 1.21.9 runs -- 1.21.9's onDeflection(fromAttack), so
 * AbstractHurtingProjectile's acceleration change (its 1.21.8 override's body, now in its onDeflection(boolean)) and
 * native 1.21.9 mixins and overrides of onDeflection(boolean) still run. 1.21.8's Projectile body was empty and no
 * vanilla override read the deflector.
 * <p>Route (CbProjectileDeflectionMixinHooks, at the default priority): deflect (1.21.9's only caller, server side, after setting the owner) asks the restored member with its own
 * deflector, instead of calling onDeflection(fromAttack) itself, only for a projectile whose class a converted mod wrote
 * with a 1.21.8 override ({@link CbDeflection.LegacyOnDeflection}) or when a converted mod mixes the restored member;
 * otherwise deflect runs untouched (an injection before the call, not a redirect). deflect answers true after the call
 * either way.
 * <p>Known difference: a RETURN injector on the restored Projectile member, for an AbstractHurtingProjectile, fires after
 * the acceleration change (1.21.8's subclass override changed it after its super call returned).
 */
@Mixin(value = class_1676.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbProjectileDeflectionMixin implements CbPrioritySplit.ProjectileOnDeflection {

    @Shadow
    protected abstract void method_59525(boolean fromAttack);

    /** Projectile.onDeflection(deflector, fromAttack), 1.21.8's shape */
    @CbVanillaReads("CbProjectileDeflectionMixinHooks: deflect asks it in place of onDeflection(fromAttack) when it is overridden or mixed")
    protected void method_59525(class_1297 deflector, boolean fromAttack) {
        this.method_59525(fromAttack);
    }

    @Override
    public void cb$v1_21_9$onDeflection(class_1297 deflector, boolean fromAttack) {
        this.method_59525(deflector, fromAttack);
    }
}
