package top.dext.centurybridge.step.v1_21_5.rt;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2371;
import net.minecraft.class_3218;

/**
 * 1.21.4's Item.inventoryTick(stack, level, entity, int slot, boolean selected) is inventoryTick(stack, ServerLevel,
 * entity, EquipmentSlot) since 1.21.5, which ItemStack.inventoryTick calls only on the server. 1.21.4 called it from
 * Inventory.tick on both sides, for each stack of the player's three compartments: the index within its compartment
 * (items 0..35, armor FEET 0 .. HEAD 3, offhand 0) and whether that index is the selected hotbar slot (compared in
 * every compartment alike). It never ticked other entities' items. 1.21.5 ticks the player's items list from
 * Inventory.tick (MAINHAND for the selected slot, null otherwise) and every living entity's equipment from
 * EntityEquipment.tick (the slot it is in).
 * <ul>
 * <li>A 1.21.4 override gets the new shape (overrideHooks, bridge {@link #inventoryTick}): for a player's stack the
 *     old body is asked with 1.21.4's index and flag ({@link #locate}); for any other entity's nothing runs, as on
 *     1.21.4. On the client, where 1.21.5 asks no item, CbItemStackTickMixin asks the old body the same way.</li>
 * <li>Its super.inventoryTick(..) (superBridge {@link #superInventoryTick}) asks super's 1.21.5 shape on the server
 *     with the slot vanilla passed (else the one 1.21.4's flag stands for); on the client super had nothing to run.</li>
 * <li>A 1.21.4 call of the old shape reaches the member restored on Item, which asks the 1.21.5 shape the same way.</li>
 * </ul>
 */
public final class CbItemTick {

    private CbItemTick() {
    }

    /** the 1.21.4 shape a converted Item subclass overrides (adapter of the override hook) */
    public interface LegacyInventoryTick {
        void method_7888(class_1799 stack, class_1937 level, class_1297 entity, int slot, boolean selected);
    }

    /** super's 1.21.5 shape, reached from such a subclass's super call */
    public interface SuperInventoryTick {
        void cb$super$method_7888(class_1799 stack, class_3218 level, class_1297 entity, class_1304 slot);
    }

    /** 1.21.4's (index, selected) of a player's stack, or null when 1.21.4 did not tick it */
    public static int[] locate(class_1657 player, class_1799 stack, class_1304 slot) {
        class_1661 inv = player.method_31548();
        int selected = inv.method_67532();
        if (slot == null || slot == class_1304.field_6173) {
            class_2371<class_1799> items = inv.method_67533();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i) == stack) {
                    return new int[] {i, i == selected ? 1 : 0};
                }
            }
            return null;
        }
        if (slot == class_1304.field_6171) {
            return new int[] {0, selected == 0 ? 1 : 0};
        }
        if (slot.method_5925() == class_1304.class_1305.field_6178) {
            int i = slot.method_5927();
            return new int[] {i, i == selected ? 1 : 0};
        }
        return null;
    }

    /** 1.21.5's inventoryTick of a 1.21.4 override */
    public static void inventoryTick(LegacyInventoryTick self, class_1799 stack, class_3218 level, class_1297 entity, class_1304 slot) {
        if (!(entity instanceof class_1657 player)) {
            return;
        }
        int[] at = locate(player, stack, slot);
        if (at == null) {
            return;
        }
        Guard.cbPush(self, new Object[] {slot});
        try {
            self.method_7888(stack, level, entity, at[0], at[1] != 0);
        } finally {
            Guard.cbPop();
        }
    }

    /** a 1.21.4 override's super.inventoryTick(stack, level, entity, slot, selected) */
    public static void superInventoryTick(SuperInventoryTick self, class_1799 stack, class_1937 level, class_1297 entity, int slot,
                                          boolean selected) {
        if (level instanceof class_3218 server) {
            class_1304 at = (class_1304) Guard.cbArgOrL(self, 0, slotOf(selected));
            self.cb$super$method_7888(stack, server, entity, at);
        }
    }

    /** the restored Item.inventoryTick: the item's 1.21.5 shape on the server */
    public static void restored(class_1792 item, class_1799 stack, class_1937 level, class_1297 entity, int slot, boolean selected) {
        if (level instanceof class_3218 server) {
            item.method_7888(stack, server, entity, (class_1304) Guard.cbArgOrL(item, 0, slotOf(selected)));
        }
    }

    /** the EquipmentSlot 1.21.5's Inventory.tick passes for 1.21.4's selected flag */
    public static class_1304 slotOf(boolean selected) {
        return selected ? class_1304.field_6173 : null;
    }

    private static final ClassValue<Boolean> OVERRIDES = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> c) {
            for (Class<?> k = c; k != null && k != class_1792.class; k = k.getSuperclass()) {
                if (LegacyInventoryTick.class.isAssignableFrom(k)) {
                    return true;
                }
                for (Method m : k.getDeclaredMethods()) {
                    if (m.getName().equals("method_7888") && m.getParameterCount() == 5 && m.getParameterTypes()[3] == int.class
                            && !Modifier.isStatic(m.getModifiers())) {
                        return true;
                    }
                }
            }
            return false;
        }
    };

    /** the client side 1.21.5 leaves out: a 1.21.4 override of a player's stack is asked as on 1.21.4 */
    public static void clientTick(class_1799 stack, class_1937 level, class_1297 entity, class_1304 slot) {
        if (level instanceof class_3218 || !(entity instanceof class_1657 player) || stack.method_7960()) {
            return;
        }
        class_1792 item = stack.method_7909();
        if (!OVERRIDES.get(item.getClass())) {
            return;
        }
        int[] at = locate(player, stack, slot);
        if (at != null) {
            ((LegacyInventoryTick) item).method_7888(stack, level, entity, at[0], at[1] != 0);
        }
    }
}
