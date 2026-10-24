package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_4146;
import net.minecraft.class_7252;
import net.minecraft.class_8947;

/**
 * 1.21.1's NearestLivingEntitySensor radii and sensor constants on 1.21.2.
 *
 * <p>1.21.1's doTick searched the box inflated by radiusXZ() / radiusY() (16 / 16; BreezeAttackEntitySensor and
 * WardenEntitySensor 24 / 24), protected and overridable; 1.21.2's inflates by the mob's FOLLOW_RANGE attribute and
 * has no such methods. A call answers the sensor class's own override, else 1.21.1's value of the vanilla class; a
 * sensor whose class overrides either radius searches 1.21.1's box (CbEntitySensorMixin). A sensor whose class
 * overrides neither keeps 1.21.2's vanilla search, like the vanilla sensor it extends.
 */
public final class CbEntitySensors {

    public interface LegacyXZ {
        int method_43081();
    }

    public interface LegacyY {
        int method_43082();
    }

    public static int radiusXZ(class_4146 sensor) {
        return sensor instanceof LegacyXZ legacy ? legacy.method_43081() : vanilla(sensor);
    }

    public static int radiusY(class_4146 sensor) {
        return sensor instanceof LegacyY legacy ? legacy.method_43082() : vanilla(sensor);
    }

    /** 1.21.1's radius of the vanilla class (both radii are equal in each) */
    public static int vanilla(class_4146 sensor) {
        return sensor instanceof class_8947 || sensor instanceof class_7252 ? 24 : 16;
    }

    public static int superRadius(class_4146 sensor) {
        return vanilla(sensor);
    }

    public static int superRadius(class_8947 sensor) {
        return 24;
    }

    public static int superRadius(class_7252 sensor) {
        return 24;
    }

    /** from the mixin on doTick: whether the sensor's class overrides a 1.21.1 radius */
    public static boolean ownRadii(class_4146 sensor) {
        return sensor instanceof LegacyXZ || sensor instanceof LegacyY;
    }

    /** BreezeAttackEntitySensor.BREEZE_SENSOR_RADIUS, Sensor.TARGETING_RANGE, TemptingSensor.TEMPTATION_RANGE */
    public static int field_47260() {
        return 24;
    }

    public static int field_30258() {
        return 16;
    }

    public static int field_30262() {
        return 10;
    }

    private CbEntitySensors() {
    }
}
