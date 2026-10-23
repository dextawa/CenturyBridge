package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_10179;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1304;
import net.minecraft.class_1308;
import net.minecraft.class_1799;
import net.minecraft.class_3730;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntityMobInvoker;

/**
 * 1.21.1's Mob.convertTo(type, keepEquipment) on 1.21.2, whose convertTo takes ConversionParams, a spawn reason and
 * an after-conversion callback.
 *
 * <p>A call answers the mob class's own 1.21.1 override, else 1.21.1's body on 1.21.2's API ({@link #superConvertTo}).
 * A mod class's 1.21.1 override answers 1.21.2's convertTo too ({@link #convertBridge}): keepEquipment from the
 * params, then vanilla's after-conversion step on the result -- what 1.21.1's vanilla callers did themselves after
 * convertTo returned.
 */
public final class CbEntityConvert {

    public interface Legacy {
        <T extends class_1308> T method_29243(class_1299<T> type, boolean keepEquipment);
    }

    public static <T extends class_1308> T convertTo(class_1308 mob, class_1299<T> type, boolean keepEquipment) {
        if (mob instanceof Legacy legacy) {
            return legacy.method_29243(type, keepEquipment);
        }
        return superConvertTo(mob, type, keepEquipment);
    }

    /** 1.21.1 Mob.convertTo */
    public static <T extends class_1308> T superConvertTo(class_1308 mob, class_1299<T> type, boolean keepEquipment) {
        if (mob.method_31481()) {
            return null;
        }
        T converted = type.method_5883(mob.method_37908(), CbEntityFrames.createReason());
        if (converted == null) {
            return null;
        }
        converted.method_5719(mob);
        converted.method_7217(mob.method_6109());
        converted.method_5977(mob.method_5987());
        if (mob.method_16914()) {
            converted.method_5665(mob.method_5797());
            converted.method_5880(mob.method_5807());
        }
        if (mob.method_5947()) {
            converted.method_5971();
        }
        converted.method_5684(mob.method_5655());
        if (keepEquipment) {
            converted.method_5952(mob.method_5936());
            for (class_1304 slot : class_1304.values()) {
                class_1799 stack = mob.method_6118(slot);
                if (!stack.method_7960()) {
                    converted.method_5673(slot, stack.method_51164());
                    converted.method_5946(slot, ((CbEntityMobInvoker) mob).cb$getEquipmentDropChance(slot));
                }
            }
        }
        mob.method_37908().method_8649(converted);
        if (mob.method_5765()) {
            class_1297 vehicle = mob.method_5854();
            mob.method_5848();
            converted.method_5873(vehicle, true);
        }
        mob.method_31472();
        return converted;
    }

    /** 1.21.2's convertTo of a mod class overriding 1.21.1's */
    public static <T extends class_1308> T convertBridge(Legacy self, class_1299<T> type, class_10179 params, class_3730 reason,
                                                       class_10179.class_10180<T> after) {
        T converted = self.method_29243(type, params.comp_3145());
        if (converted != null) {
            after.finalizeConversion(converted);
        }
        return converted;
    }

    private CbEntityConvert() {
    }
}
