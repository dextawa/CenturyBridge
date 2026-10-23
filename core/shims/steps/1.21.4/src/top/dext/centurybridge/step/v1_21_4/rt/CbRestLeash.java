package top.dext.centurybridge.step.v1_21_4.rt;

import com.mojang.datafixers.util.Either;
import net.minecraft.class_1297;
import net.minecraft.class_1308;
import net.minecraft.class_1802;
import net.minecraft.class_2487;
import net.minecraft.class_2512;
import net.minecraft.class_2740;
import net.minecraft.class_3218;
import net.minecraft.class_9817;
import top.dext.centurybridge.step.v1_21_4.mixin.CbRestLeashDataInvoker;

/**
 * 1.21.3's Leashable.readLeashData(tag) -> LeashData and dropLeash(sendPacket, dropItem), which 1.21.4
 * changed to readLeashData(tag) (reads and applies) and dropLeash() / removeLeash() (sendPacket true,
 * dropItem true / false).
 * <ul>
 * <li>{@link #method_60967}: 1.21.3's body, parsing the tag only (the caller set the data itself);</li>
 * <li>{@link #method_5932}: 1.21.3's body (Leashable's static dropLeash(entity, sendPacket, dropItem), which
 *     1.21.4 keeps private with Mob's 1.21.3 clearRestriction moved into its onLeashRemoved hook), over
 *     1.21.4's public members: the same steps in the same order;</li>
 * <li>a 1.21.3 override of readLeashData answers 1.21.4's readLeashData(tag) with 1.21.4's apply step over its
 *     answer; one of dropLeash(ZZ) answers dropLeash() and removeLeash() with their flags.</li>
 * </ul>
 */
public final class CbRestLeash {

    private CbRestLeash() {
    }

    /** 1.21.3 Leashable.readLeashData(tag): the leash data the tag holds, or null */
    public static class_9817.class_9818 method_60967(class_9817 self, class_2487 tag) {
        if (tag.method_10573("leash", 10)) {
            return CbRestLeashDataInvoker.cb$create(Either.left(tag.method_10562("leash").method_25926("UUID")));
        }
        if (tag.method_10573("leash", 11)) {
            return class_2512.method_10691(tag, "leash").map(pos -> CbRestLeashDataInvoker.cb$create(Either.right(pos))).orElse(null);
        }
        return null;
    }

    public static class_9817.class_9818 method_60967(class_1308 self, class_2487 tag) {
        return method_60967((class_9817) self, tag);
    }

    /** 1.21.3 Leashable.dropLeash(sendPacket, dropItem) */
    public static void method_5932(class_9817 self, boolean sendPacket, boolean dropItem) {
        class_1297 entity = (class_1297) self;
        class_9817.class_9818 data = self.method_60955();
        if (data != null && data.field_52217 != null) {
            self.method_60960(null);
            self.method_65895();
            if (entity.method_37908() instanceof class_3218 level) {
                if (dropItem) {
                    entity.method_5706(level, class_1802.field_8719);
                }
                if (sendPacket) {
                    level.method_14178().method_18754(entity, new class_2740(entity, null));
                }
            }
        }
    }

    public static void method_5932(class_1308 self, boolean sendPacket, boolean dropItem) {
        method_5932((class_9817) self, sendPacket, dropItem);
    }

    /** a 1.21.3 override of readLeashData(tag) -> LeashData */
    public interface LegacyRead {
        class_9817.class_9818 method_60967(class_2487 tag);
    }

    /** 1.21.4's readLeashData(tag) of a class overriding 1.21.3's: its answer applied as 1.21.4's default applies its own */
    public static void readLeashData(LegacyRead self, class_2487 tag) {
        class_9817 leashable = (class_9817) self;
        class_9817.class_9818 data = self.method_60967(tag);
        if (leashable.method_60955() != null && data == null) {
            leashable.method_65894();
        }
        leashable.method_60960(data);
    }

    /** a 1.21.3 override of dropLeash(sendPacket, dropItem) */
    public interface LegacyDrop {
        void method_5932(boolean sendPacket, boolean dropItem);
    }

    /** 1.21.4's dropLeash(): 1.21.3's dropLeash(true, true) */
    public static void dropLeash(LegacyDrop self) {
        self.method_5932(true, true);
    }

    /** 1.21.4's removeLeash(): 1.21.3's dropLeash(true, false) */
    public static void removeLeash(LegacyDrop self) {
        self.method_5932(true, false);
    }
}
