package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1304;
import net.minecraft.class_1451;
import net.minecraft.class_1477;
import net.minecraft.class_1493;
import net.minecraft.class_1501;
import net.minecraft.class_1531;
import net.minecraft.class_1551;
import net.minecraft.class_1642;
import net.minecraft.class_1646;
import net.minecraft.class_1661;
import net.minecraft.class_1665;
import net.minecraft.class_1767;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2248;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_5815;
import net.minecraft.class_8949;
import net.minecraft.class_9334;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntityArrowAccess;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntitySquidAccess;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntityVillagerAccess;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntityWolfAccess;

/**
 * Small members of 1.21.1 entity classes that 1.21.2 removed, answered with 1.21.1's bodies on 1.21.2's state (the
 * fields they read live on under the same intermediary names unless said otherwise):
 * Wolf.isWet / getBodyRollAngle / hasArmor, Llama.getSwag, Cat.getTextureId, ArmorStand.isNoBasePlate (1.21.2's
 * showBasePlate, negated), Squid.setMovementVector (1.21.2 keeps the vector as one Vec3), Villager.isChasing /
 * setChasing, Zombie.supportsBreakDoorGoal (Zombie true, Drowned false), Inventory.findSlotMatchingUnusedItem /
 * swapPaint / ALL_ARMOR_SLOTS / HELMET_SLOT_ONLY, AbstractArrow.shotFromCrossbow / inGround (1.21.2's protected
 * isInGround / setInGround), EquipmentSlot.getFilterFlag (1.21.2's getFilterBit(0), the same constant),
 * Breeze.getSnoutYPosition. A call answers the class's own 1.21.1 override where the class could override it.
 */
public final class CbEntityTail {

    /**
     * the adapter of a mod LookControl's renamed 1.21.1 rotateTowards: 1.21.2's Control.rotateTowards is a public
     * default, so the renamed override must be public (the adapter makes it so)
     */
    public interface RotateTowards { float method_63642(float from, float to, float maxDelta); }

    public interface LegacyWet { boolean method_6711(); }

    public interface LegacyRoll { float method_6715(float partialTick, float offset); }

    public interface LegacyWolfArmor { boolean method_55710(); }

    public interface LegacySwag { class_1767 method_6800(); }

    public interface LegacyCatTexture { class_2960 method_16092(); }

    public interface LegacyBasePlate { boolean method_6901(); }

    public interface LegacyMovementVector { void method_6670(float x, float y, float z); }

    public interface LegacyChasing { boolean method_35200(); }

    public interface LegacySetChasing { void method_35201(boolean chasing); }

    public interface LegacyBreakDoor { boolean method_7212(); }

    public interface LegacyUnusedSlot { int method_7371(class_1799 stack); }

    public interface LegacySwapPaint { void method_7373(double direction); }

    public interface LegacyCrossbow { boolean method_7456(); }

    public interface LegacySnout { double method_55009(); }

    // ------------------------------------------------------------------------------------------ Wolf

    public static boolean isWet(class_1493 wolf) {
        return wolf instanceof LegacyWet l ? l.method_6711() : superIsWet(wolf);
    }

    public static boolean superIsWet(class_1493 wolf) {
        return ((CbEntityWolfAccess) wolf).cb$isWet();
    }

    public static float getBodyRollAngle(class_1493 wolf, float partialTick, float offset) {
        return wolf instanceof LegacyRoll l ? l.method_6715(partialTick, offset) : superGetBodyRollAngle(wolf, partialTick, offset);
    }

    /** (lerp(partialTick, shakeAnimO, shakeAnim) + offset) / 1.8 clamped to [0, 1]: sin(f pi) sin(11 f pi) 0.15 pi */
    public static float superGetBodyRollAngle(class_1493 wolf, float partialTick, float offset) {
        CbEntityWolfAccess w = (CbEntityWolfAccess) wolf;
        float f = (class_3532.method_16439(partialTick, w.cb$shakeAnimO(), w.cb$shakeAnim()) + offset) / 1.8F;
        if (f < 0.0F) {
            f = 0.0F;
        } else if (f > 1.0F) {
            f = 1.0F;
        }
        return class_3532.method_15374(f * (float) Math.PI) * class_3532.method_15374(f * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
    }

    public static boolean hasArmor(class_1493 wolf) {
        return wolf instanceof LegacyWolfArmor l ? l.method_55710() : superHasArmor(wolf);
    }

    public static boolean superHasArmor(class_1493 wolf) {
        return wolf.method_56676().method_31574(class_1802.field_47831);
    }

    // ------------------------------------------------------------------------------------------ Llama, Cat, ArmorStand, Squid

    public static class_1767 getSwag(class_1501 llama) {
        return llama instanceof LegacySwag l ? l.method_6800() : superGetSwag(llama);
    }

    /** the colour of the wool carpet in the body slot, else null */
    public static class_1767 superGetSwag(class_1501 llama) {
        class_2248 block = class_2248.method_9503(llama.method_6118(class_1304.field_48824).method_7909());
        return block instanceof class_5815 carpet ? carpet.method_33635() : null;
    }

    public static class_2960 getTextureId(class_1451 cat) {
        return cat instanceof LegacyCatTexture l ? l.method_16092() : superGetTextureId(cat);
    }

    public static class_2960 superGetTextureId(class_1451 cat) {
        return cat.method_47843().comp_349().comp_706();
    }

    public static boolean isNoBasePlate(class_1531 stand) {
        return stand instanceof LegacyBasePlate l ? l.method_6901() : !stand.method_61489();
    }

    public static boolean superIsNoBasePlate(class_1531 stand) {
        return !stand.method_61489();
    }

    public static void setMovementVector(class_1477 squid, float x, float y, float z) {
        if (squid instanceof LegacyMovementVector l) {
            l.method_6670(x, y, z);
            return;
        }
        superSetMovementVector(squid, x, y, z);
    }

    public static void superSetMovementVector(class_1477 squid, float x, float y, float z) {
        ((CbEntitySquidAccess) squid).cb$setMovementVector(new class_243(x, y, z));
    }

    // ------------------------------------------------------------------------------------------ Villager, Zombie

    public static boolean isChasing(class_1646 villager) {
        return villager instanceof LegacyChasing l ? l.method_35200() : ((CbEntityVillagerAccess) villager).cb$chasing();
    }

    public static boolean superIsChasing(class_1646 villager) {
        return ((CbEntityVillagerAccess) villager).cb$chasing();
    }

    public static void setChasing(class_1646 villager, boolean chasing) {
        if (villager instanceof LegacySetChasing l) {
            l.method_35201(chasing);
            return;
        }
        ((CbEntityVillagerAccess) villager).cb$setChasing(chasing);
    }

    public static void superSetChasing(class_1646 villager, boolean chasing) {
        ((CbEntityVillagerAccess) villager).cb$setChasing(chasing);
    }

    public static boolean supportsBreakDoorGoal(class_1642 zombie) {
        return zombie instanceof LegacyBreakDoor l ? l.method_7212() : superSupportsBreakDoorGoal(zombie);
    }

    public static boolean superSupportsBreakDoorGoal(class_1642 zombie) {
        return !(zombie instanceof class_1551);
    }

    public static boolean superSupportsBreakDoorGoal(class_1551 drowned) {
        return false;
    }

    // ------------------------------------------------------------------------------------------ Inventory

    private static final int[] ALL_ARMOR_SLOTS = {0, 1, 2, 3};
    private static final int[] HELMET_SLOT_ONLY = {3};

    public static int[] field_33767() {
        return ALL_ARMOR_SLOTS;
    }

    public static int[] field_33768() {
        return HELMET_SLOT_ONLY;
    }

    public static int findSlotMatchingUnusedItem(class_1661 inventory, class_1799 stack) {
        return inventory instanceof LegacyUnusedSlot l ? l.method_7371(stack) : superFindSlotMatchingUnusedItem(inventory, stack);
    }

    /** the first main slot holding the same item and components, undamaged, unenchanted and unnamed; else -1 */
    public static int superFindSlotMatchingUnusedItem(class_1661 inventory, class_1799 stack) {
        for (int i = 0; i < inventory.field_7547.size(); i++) {
            class_1799 s = inventory.field_7547.get(i);
            if (!s.method_7960() && class_1799.method_31577(stack, s) && !s.method_7986() && !s.method_7942()
                    && !s.method_57826(class_9334.field_49631)) {
                return i;
            }
        }
        return -1;
    }

    public static void swapPaint(class_1661 inventory, double direction) {
        if (inventory instanceof LegacySwapPaint l) {
            l.method_7373(direction);
            return;
        }
        superSwapPaint(inventory, direction);
    }

    /** selected -= signum(direction), wrapping in 0..8 */
    public static void superSwapPaint(class_1661 inventory, double direction) {
        int step = (int) Math.signum(direction);
        inventory.field_7545 -= step;
        while (inventory.field_7545 < 0) {
            inventory.field_7545 += 9;
        }
        while (inventory.field_7545 >= 9) {
            inventory.field_7545 -= 9;
        }
    }

    // ------------------------------------------------------------------------------------------ AbstractArrow, slots, Breeze

    public static boolean shotFromCrossbow(class_1665 arrow) {
        return arrow instanceof LegacyCrossbow l ? l.method_7456() : superShotFromCrossbow(arrow);
    }

    public static boolean superShotFromCrossbow(class_1665 arrow) {
        class_1799 weapon = arrow.method_59958();
        return weapon != null && weapon.method_31574(class_1802.field_8399);
    }

    public static boolean getInGround(class_1665 arrow) {
        return ((CbEntityArrowAccess) arrow).cb$isInGround();
    }

    public static void setInGround(class_1665 arrow, boolean inGround) {
        ((CbEntityArrowAccess) arrow).cb$setInGround(inGround);
    }

    public static int getFilterFlag(class_1304 slot) {
        return slot.method_63620(0);
    }

    public static double getSnoutYPosition(class_8949 breeze) {
        return breeze instanceof LegacySnout l ? l.method_55009() : breeze.method_23320() - 0.4;
    }

    public static double superGetSnoutYPosition(class_8949 breeze) {
        return breeze.method_23320() - 0.4;
    }

    private CbEntityTail() {
    }
}
