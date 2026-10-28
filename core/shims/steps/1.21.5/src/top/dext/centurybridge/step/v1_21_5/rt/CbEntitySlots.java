package top.dext.centurybridge.step.v1_21_5.rt;

import java.util.AbstractList;
import java.util.List;
import net.minecraft.class_1304;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1799;

/**
 * 1.21.4's LivingEntity slot iterables on 1.21.5, which keeps an entity's items in EntityEquipment and has no such
 * members (javap 1.21.5 class_1309: getItemBySlot / setItemSlot over field_56535). 1.21.4's lists were the entity's
 * own storage (Mob.armorItems / handItems, Player inventory.armor, ArmorStand.armorItems), indexed FEET, LEGS, CHEST,
 * HEAD and MAINHAND, OFFHAND; here they are views of the same slots through getItemBySlot / setItemSlot, so a
 * read sees the current item and a set changes the entity's slot (an entity class overriding those two is asked).
 */
public final class CbEntitySlots {
    private static final class_1304[] ARMOR = {class_1304.field_6166, class_1304.field_6172, class_1304.field_6174, class_1304.field_6169};
    private static final class_1304[] HANDS = {class_1304.field_6173, class_1304.field_6171};
    private static final class_1304[] BODY = {class_1304.field_48824};

    private CbEntitySlots() {
    }

    /** getArmorSlots(): FEET, LEGS, CHEST, HEAD */
    public static List<class_1799> armor(class_1309 e) {
        return new View(e, ARMOR);
    }

    /** getHandSlots(): MAINHAND, OFFHAND (1.21.4's LivingEntity answered none; every vanilla subclass answered both) */
    public static List<class_1799> hands(class_1309 e) {
        return new View(e, HANDS);
    }

    /**
     * getArmorAndBodyArmorSlots(): the armor; for a Mob the armor and its BODY slot (1.21.4: LivingEntity answered
     * getArmorSlots(), Mob concatenated its bodyArmorItem)
     */
    public static Iterable<class_1799> armorAndBody(class_1309 e, Iterable<class_1799> armor) {
        return e instanceof class_1308 ? com.google.common.collect.Iterables.concat(armor, new View(e, BODY)) : armor;
    }

    private static final class View extends AbstractList<class_1799> {
        private final class_1309 entity;
        private final class_1304[] slots;

        View(class_1309 entity, class_1304[] slots) {
            this.entity = entity;
            this.slots = slots;
        }

        @Override
        public class_1799 get(int i) {
            return this.entity.method_6118(this.slots[i]);
        }

        @Override
        public class_1799 set(int i, class_1799 stack) {
            class_1799 old = this.entity.method_6118(this.slots[i]);
            this.entity.method_5673(this.slots[i], stack);
            return old;
        }

        @Override
        public int size() {
            return this.slots.length;
        }
    }
}
