package top.dext.centurybridge.step.v1_21_5.rt;

import net.minecraft.class_2487;
import net.minecraft.class_2940;
import net.minecraft.class_2945;
import net.minecraft.class_4980;
import top.dext.centurybridge.step.v1_21_5.facade.ItemBasedSteering;

/**
 * 1.21.4's ItemBasedSteering constructor and saddle members on 1.21.5 (facade/ItemBasedSteering: a 1.21.5
 * ItemBasedSteering keeping the saddle in the mod's own synched accessor). A converted mod's new ItemBasedSteering(data,
 * boost, saddle) builds the facade; its calls of 1.21.4's saddle members reach the facade's (a converted subclass's
 * overrides by virtual dispatch). 1.21.5's own steering objects -- Pig's and Strider's, private, reachable only through a
 * mod's accessor -- keep no saddle accessor any more (the saddle is their SADDLE slot): those calls fail with the reason.
 */
public final class CbSteering {
    private CbSteering() {
    }

    /** new ItemBasedSteering(entityData, boostTime, hasSaddle) */
    public static class_4980 create(class_2945 entityData, class_2940<Integer> boostTime, class_2940<Boolean> hasSaddle) {
        return new ItemBasedSteering(entityData, boostTime, hasSaddle);
    }

    private static ItemBasedSteering legacy(class_4980 steering) {
        if (steering instanceof ItemBasedSteering f) {
            return f;
        }
        throw new UnsupportedOperationException("a 1.21.5 vanilla ItemBasedSteering keeps no saddle (1.21.5 moved it to the "
            + "entity's SADDLE slot); only a steering a 1.21.4 mod built has 1.21.4's saddle members");
    }

    /** addAdditionalSaveData(tag) */
    public static void addAdditionalSaveData(class_4980 steering, class_2487 tag) {
        legacy(steering).method_26309(tag);
    }

    /** readAdditionalSaveData(tag) */
    public static void readAdditionalSaveData(class_4980 steering, class_2487 tag) {
        legacy(steering).method_26312(tag);
    }

    /** setSaddle(saddled) */
    public static void setSaddle(class_4980 steering, boolean saddled) {
        legacy(steering).method_26310(saddled);
    }

    /** hasSaddle() */
    public static boolean hasSaddle(class_4980 steering) {
        return legacy(steering).method_26311();
    }
}
