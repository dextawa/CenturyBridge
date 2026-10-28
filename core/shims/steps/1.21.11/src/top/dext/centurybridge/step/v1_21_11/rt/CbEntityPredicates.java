package top.dext.centurybridge.step.v1_21_11.rt;

import java.util.Optional;

/**
 * 1.21.11's EntityFlagsPredicate (class_2040, a record) appended two components to 1.21.10's seven: isInWater
 * (comp_4946) and isFallFlying (comp_4947) (javap of both: the first seven in the same order, isOnGround .. isBaby).
 * 1.21.10's predicate tested neither; an empty Optional is "no requirement" for 1.21.11's matches() as for every
 * other flag, so a 1.21.10 construction passes empty for both.
 */
public final class CbEntityPredicates {
    private CbEntityPredicates() {
    }

    /** a flag 1.21.10's predicate did not have: no requirement */
    public static Optional<Boolean> noFlag() {
        return Optional.empty();
    }
}
