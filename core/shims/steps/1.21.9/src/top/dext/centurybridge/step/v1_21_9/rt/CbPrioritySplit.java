package top.dext.centurybridge.step.v1_21_9.rt;

import net.minecraft.class_1297;

/**
 * What a restoring layer mixin (at {@link CbVanillaReads#PRIORITY}) shares with the injectors into 1.21.9's own
 * methods that were split off it into a default-priority {@code <name>Hooks} mixin: the restored members both use,
 * called virtually through the restorer, so a mod's mixin on them, or a subclass's override, still answers; and the
 * restorer's own state (its {@code @Unique} fields), reached through it.
 */
public final class CbPrioritySplit {
    private CbPrioritySplit() {
    }

    /** CbProjectileDeflectionMixin: 1.21.8's Projectile.onDeflection(deflector, fromAttack), called virtually */
    public interface ProjectileOnDeflection {
        void cb$v1_21_9$onDeflection(class_1297 deflector, boolean fromAttack);
    }

    /** CbServerRestoredMixin: the flight flag a 1.21.8 setFlightAllowed set on a non-dedicated server (null: never set) */
    public interface ServerFlight {
        Boolean cb$v1_21_9$flightAllowed();
    }
}
