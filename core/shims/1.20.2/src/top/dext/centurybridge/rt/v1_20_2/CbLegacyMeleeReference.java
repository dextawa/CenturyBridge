package top.dext.centurybridge.rt.v1_20_2;

/**
 * 1.20.1 LivingEntity.getMeleeAttackReferencePosition (protected, method_47921), answered again by
 * every living entity through a mixin. A converted subclass may override it with a protected
 * method, which an interface call cannot select (IllegalAccessError), so callers go through
 * {@link #cb$meleeReferencePosition}, a public trampoline that calls it virtually.
 */
public interface CbLegacyMeleeReference {

    /** 1.20.1 getMeleeAttackReferencePosition() */
    net.minecraft.class_243 method_47921();

    /** method_47921() called virtually from inside LivingEntity */
    net.minecraft.class_243 cb$meleeReferencePosition();
}
