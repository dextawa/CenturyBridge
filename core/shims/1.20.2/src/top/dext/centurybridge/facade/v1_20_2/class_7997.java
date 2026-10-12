package top.dext.centurybridge.facade.v1_20_2;

/**
 * 1.20.1 RiderShieldingMount (class_7997), gone in 1.20.2: a mount whose riders sit out of melee
 * reach by getRiderShieldingHeight() (1.20.1's LivingEntity.getMeleeAttackReferencePosition
 * raised a rider's reference point by it). 1.20.2's camel, its only vanilla implementor,
 * implements this again (mixin, 1.20.1's 0.5); 1.20.2 itself lifts a rider's hitbox to its seat
 * instead.
 */
public interface class_7997 {

    /** getRiderShieldingHeight() */
    double method_47923();
}
