package top.dext.centurybridge.step.v1_21_4.rt;

import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3222;
import top.dext.centurybridge.step.v1_21_4.mixin.CbRestEntityInvoker;

/**
 * 1.21.3 player / inventory members gone or reshaped in 1.21.4, with 1.21.3's bodies over 1.21.4's members.
 * <ul>
 * <li>ServerPlayer.doCheckFallDamage(dx, dy, dz, onGround): 1.21.3 checked the supporting block, then ran the
 *     extra fall particles and Player.checkFallDamage; 1.21.4 moved the particles into ServerPlayer's
 *     checkFallDamage override, so the same steps are checkSupportingBlock and then checkFallDamage.</li>
 * <li>Inventory.setPickedItem(stack): select the hotbar slot holding it, else add it and pick it
 *     (1.21.4's addAndPickItem, 1.21.3's own branch), else pick its slot.</li>
 * <li>Inventory.findSlotMatchingCraftingIngredient(item): 1.21.4 also compares components against a reference
 *     stack; an empty reference compares nothing, 1.21.3's test ({@link #noReference}).</li>
 * </ul>
 * A 1.21.3 subclass's override stays reachable through the interfaces below (adapterHooks); a mod's super
 * call runs 1.21.3's body.
 */
public final class CbRestPlayer {

    private CbRestPlayer() {
    }

    public interface FallDamage { void method_14207(double dx, double dy, double dz, boolean onGround); }
    public interface PickedItem { void method_7374(class_1799 stack); }

    public static void method_14207(class_3222 player, double dx, double dy, double dz, boolean onGround) {
        if ((Object) player instanceof FallDamage a) {
            a.method_14207(dx, dy, dz, onGround);
        } else {
            superDoCheckFallDamage(player, dx, dy, dz, onGround);
        }
    }

    public static void superDoCheckFallDamage(class_3222 player, double dx, double dy, double dz, boolean onGround) {
        if (player.method_33724()) {
            return;
        }
        CbRestEntityInvoker e = (CbRestEntityInvoker) player;
        e.cb$checkSupportingBlock(onGround, new class_243(dx, dy, dz));
        class_2338 pos = player.method_43260();
        class_2680 state = player.method_37908().method_8320(pos);
        e.cb$checkFallDamage(dy, onGround, state, pos);
    }

    public static void method_7374(class_1661 inv, class_1799 stack) {
        if ((Object) inv instanceof PickedItem a) {
            a.method_7374(stack);
        } else {
            superSetPickedItem(inv, stack);
        }
    }

    public static void superSetPickedItem(class_1661 inv, class_1799 stack) {
        int slot = inv.method_7395(stack);
        if (class_1661.method_7380(slot)) {
            inv.field_7545 = slot;
        } else if (slot == -1) {
            inv.method_65126(stack);
        } else {
            inv.method_7365(slot);
        }
    }

    /** the reference stack of 1.21.3's findSlotMatchingCraftingIngredient: none (empty), no component test */
    public static class_1799 noReference() {
        return class_1799.field_8037;
    }

}
