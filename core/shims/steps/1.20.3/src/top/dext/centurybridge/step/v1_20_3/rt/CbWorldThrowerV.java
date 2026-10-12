package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.UUID;

/**
 * Marks an ItemEntity class converted from 1.20.2 that overrides 1.20.2's setThrower(UUID) (its own method
 * implements it): a 1.20.2 call of setThrower(UUID) reaches the override through this, as 1.20.2's virtual call
 * did (CbWorldEntities.setThrower).
 */
public interface CbWorldThrowerV {
    void method_6981(UUID thrower);
}
