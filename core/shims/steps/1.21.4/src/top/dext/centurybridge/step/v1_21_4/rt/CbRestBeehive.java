package top.dext.centurybridge.step.v1_21_4.rt;

import net.minecraft.class_1297;
import net.minecraft.class_4482;

/** 1.21.3's BeehiveBlockEntity.addOccupant(entity): 1.21.3's body, CbRestBeehiveMixin. */
public final class CbRestBeehive {

    private CbRestBeehive() {
    }

    public interface Legacy {
        void cb$addOccupant(class_1297 entity);
    }

    public static void method_21848(class_4482 hive, class_1297 entity) {
        ((Legacy) hive).cb$addOccupant(entity);
    }
}
