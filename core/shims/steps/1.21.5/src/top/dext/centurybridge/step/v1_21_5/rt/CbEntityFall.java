package top.dext.centurybridge.step.v1_21_5.rt;

import net.minecraft.class_1297;

/**
 * 1.21.4's fall distance on 1.21.5 (entity area): Entity.fallDistance became a double, and with it
 * Entity.causeFallDamage(fallDistance, multiplier, source) and LivingEntity.calculateFallDamage(fallDistance,
 * multiplier) take it as a double. A 1.21.4 read gets the value as the float 1.21.4 kept (a write stores it
 * exactly); a 1.21.4 call passes its float on widened (exact); a 1.21.4 override is asked with vanilla's distance
 * narrowed to the float 1.21.4 computed it in (overrideHooks wrap).
 */
public final class CbEntityFall {

    private CbEntityFall() {
    }

    /** Entity.fallDistance, read */
    public static float field_6017(class_1297 entity) {
        return (float) entity.field_6017;
    }

    /** Entity.fallDistance, written */
    public static void field_6017(class_1297 entity, float value) {
        entity.field_6017 = value;
    }

    /** a 1.21.4 call's float distance, widened */
    public static double toDouble(float distance) {
        return distance;
    }

    /** vanilla's double distance for a 1.21.4 override */
    public static float toFloat(double distance) {
        return (float) distance;
    }
}
