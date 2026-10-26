package top.dext.centurybridge.step.v1_21_5.rt;

import java.util.AbstractList;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_2371;

/**
 * 1.21.4's Inventory.armor and Inventory.offhand (class_1661 field_7548 / field_7544, NonNullList&lt;ItemStack&gt; of 4
 * and 1) on 1.21.5, which moved both into the player's EntityEquipment (javap W class_1661: the two lists and
 * compartments are gone; the slot table field_56551 maps inventory index 36 + armor index to FEET / LEGS / CHEST / HEAD
 * and 40 to OFFHAND, and getItem / setItem (method_5438 / method_5447) read and write the equipment through it).
 * 1.21.4 kept the same layout: the armor list was inventory indices 36..39 (FEET first), the offhand list index 40.
 * <p>
 * Each getter answers a live view: get / set / size go to the inventory's own getItem / setItem at those indices, so a
 * read sees the worn stack and a write equips it, as a write into 1.21.4's list did. A view is a NonNullList, so a null
 * write is refused as 1.21.4's list refused it; add / remove are unsupported (1.21.4's fixed-size lists threw too).
 * Mods' reads of the fields (instanceFieldGetters) and a climbed layer's @Shadow of them (MixinBridge) reach these.
 */
public final class CbEntityInventory {

    private CbEntityInventory() {
    }

    /** was Inventory.armor (field_7548) */
    public static class_2371<class_1799> armor(class_1661 inventory) {
        return new View(new Slots(inventory, 36, 4));
    }

    /** was Inventory.offhand (field_7544) */
    public static class_2371<class_1799> offhand(class_1661 inventory) {
        return new View(new Slots(inventory, 40, 1));
    }

    /** the inventory's slots from {@code first}, {@code size} of them */
    private static final class Slots extends AbstractList<class_1799> {
        private final class_1661 inventory;
        private final int first;
        private final int size;

        Slots(class_1661 inventory, int first, int size) {
            this.inventory = inventory;
            this.first = first;
            this.size = size;
        }

        @Override
        public class_1799 get(int index) {
            return this.inventory.method_5438(this.first + java.util.Objects.checkIndex(index, this.size));
        }

        @Override
        public class_1799 set(int index, class_1799 stack) {
            int slot = this.first + java.util.Objects.checkIndex(index, this.size);
            class_1799 old = this.inventory.method_5438(slot);
            this.inventory.method_5447(slot, stack);
            return old;
        }

        @Override
        public int size() {
            return this.size;
        }
    }

    /** NonNullList over the slots (its constructor is protected) */
    private static final class View extends class_2371<class_1799> {
        View(Slots slots) {
            super(slots, class_1799.field_8037);
        }
    }
}
