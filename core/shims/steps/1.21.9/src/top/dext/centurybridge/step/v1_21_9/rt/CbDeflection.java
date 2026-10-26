package top.dext.centurybridge.step.v1_21_9.rt;

import net.minecraft.class_10583;
import net.minecraft.class_1297;
import net.minecraft.class_9109;

/**
 * Projectile deflection, 1.21.8 -> 1.21.9 (javap of both code jars):
 * <ul>
 * <li>deflect(deflection, deflector, owner, fromAttack): the owner is the owner's EntityReference on 1.21.9 (1.21.8 passed
 *     getOwner(), 1.21.9 passes the owner field; setOwner(Entity) is setOwner(EntityReference.of(entity)) on both sides
 *     of the change). A 1.21.8 call hands its entity over as {@link #ownerRef}; a 1.21.8 override gets the entity the
 *     reference stands for in the projectile's level ({@link #cbDeflect}), which is what 1.21.9's getOwner() answers.</li>
 * <li>onDeflection(deflector, fromAttack) lost the deflector (no 1.21.8 vanilla body read it); the layer restores the
 *     1.21.8 member on Projectile (CbProjectileDeflectionMixin), forwarding to 1.21.9's onDeflection(fromAttack), and
 *     deflect asks the restored member with its own deflector when a converted mod overrides it ({@link LegacyOnDeflection},
 *     added by the step's adapter hook) or mixes it (CbMixedMembers).</li>
 * </ul>
 */
public final class CbDeflection {

    /** the restored Projectile.onDeflection(Entity, boolean), as the layer declares it */
    public static final String ON_DEFLECTION = "net/minecraft/class_1676.method_59525(Lnet/minecraft/class_1297;Z)V";

    private CbDeflection() {
    }

    /** a 1.21.8 deflect call's owner entity as 1.21.9's argument (null stays null, as EntityReference.of answers) */
    public static class_10583<class_1297> ownerRef(class_1297 owner) {
        return class_10583.method_73299(owner);
    }

    /**
     * a projectile class of a converted mod that declares 1.21.8's onDeflection(Entity, boolean) (the adapter hook adds
     * it and makes that method public); the route tests for it and calls the restored member, which it overrides
     */
    public interface LegacyOnDeflection {
        void method_59525(class_1297 deflector, boolean fromAttack);
    }

    /** a projectile class of a converted mod that declares 1.21.8's deflect(.., Entity owner, ..) */
    public interface LegacyDeflect {
        boolean method_59859(class_9109 deflection, class_1297 deflector, class_1297 owner, boolean fromAttack);
    }

    /** 1.21.9's deflect, answered by a 1.21.8 override: the owner reference resolved in the projectile's level */
    public static boolean cbDeflect(LegacyDeflect self, class_9109 deflection, class_1297 deflector, class_10583<class_1297> owner,
                                    boolean fromAttack) {
        class_1297 entity = owner == null ? null : class_10583.method_73297(owner, ((class_1297) self).method_73183());
        return self.method_59859(deflection, deflector, entity, fromAttack);
    }
}
