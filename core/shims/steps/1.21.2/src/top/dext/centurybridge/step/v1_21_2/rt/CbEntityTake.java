package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1433;
import net.minecraft.class_1440;
import net.minecraft.class_1531;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_4019;
import net.minecraft.class_7298;
import top.dext.centurybridge.step.v1_21_2.mixin.CbEntityArmorStandInvoker;

/**
 * 1.21.1's LivingEntity.canTakeItem(stack) on 1.21.2, which removed it.
 *
 * <p>1.21.1 asked it in one place: a dispenser equipping a stack picks the entities that are alive, not spectators
 * and canTakeItem(stack) (EntitySelector.MobCanWearArmorEntitySelector with NO_SPECTATORS). 1.21.2 asks the final
 * canEquipWithDispenser(stack), built on the stack's equippable component. A mod class's own 1.21.1 override answers
 * there (CbEntityTakeMixin) as 1.21.1 asked it; a call answers the override, else 1.21.1's body of the vanilla class
 * (LivingEntity false; Mob: the slot is empty and it can pick up loot; Player: the slot is empty; Dolphin, Fox, Panda:
 * the slot is empty and it is the main hand, then Mob's; Allay false; ArmorStand: the slot is empty and not
 * disabled).
 */
public final class CbEntityTake {

    /** a mod class's own 1.21.1 canTakeItem (adapter hook) */
    public interface Legacy {
        boolean method_18397(class_1799 stack);
    }

    /** 1.21.1 canTakeItem(stack), called */
    public static boolean canTakeItem(class_1309 entity, class_1799 stack) {
        if (entity instanceof Legacy legacy) {
            return legacy.method_18397(stack);
        }
        return vanilla(entity, stack);
    }

    /** 1.21.1's body of the vanilla class an entity is (a mod class's super call lands on it) */
    public static boolean vanilla(class_1309 entity, class_1799 stack) {
        if (entity instanceof class_7298) {
            return false;
        }
        if (entity instanceof class_1531 stand) {
            class_1304 slot = stand.method_32326(stack);
            return stand.method_6118(slot).method_7960() && !((CbEntityArmorStandInvoker) stand).cb$isDisabled(slot);
        }
        if (entity instanceof class_1657 player) {
            return player.method_6118(player.method_32326(stack)).method_7960();
        }
        if (entity instanceof class_1433 || entity instanceof class_4019 || entity instanceof class_1440) {
            class_1304 slot = entity.method_32326(stack);
            if (!entity.method_6118(slot).method_7960()) {
                return false;
            }
            return slot == class_1304.field_6173 && mob((class_1308) entity, stack);
        }
        if (entity instanceof class_1308 mob) {
            return mob(mob, stack);
        }
        return false;
    }

    private static boolean mob(class_1308 mob, class_1799 stack) {
        return mob.method_6118(mob.method_32326(stack)).method_7960() && mob.method_5936();
    }

    /** a mod class's super.canTakeItem(stack), typed as each vanilla class that declared it */
    public static boolean superCanTakeItem(class_1309 entity, class_1799 stack) {
        return vanilla(entity, stack);
    }

    public static boolean superCanTakeItem(class_1308 entity, class_1799 stack) {
        return vanilla(entity, stack);
    }

    public static boolean superCanTakeItem(class_1657 entity, class_1799 stack) {
        return vanilla(entity, stack);
    }

    public static boolean superCanTakeItem(class_1433 entity, class_1799 stack) {
        return vanilla(entity, stack);
    }

    public static boolean superCanTakeItem(class_4019 entity, class_1799 stack) {
        return vanilla(entity, stack);
    }

    public static boolean superCanTakeItem(class_1440 entity, class_1799 stack) {
        return vanilla(entity, stack);
    }

    public static boolean superCanTakeItem(class_7298 entity, class_1799 stack) {
        return vanilla(entity, stack);
    }

    public static boolean superCanTakeItem(class_1531 entity, class_1799 stack) {
        return vanilla(entity, stack);
    }

    /** 1.21.1's dispenser selector test (MobCanWearArmorEntitySelector): alive, a LivingEntity, canTakeItem */
    public static boolean selectorTest(class_1297 entity, class_1799 stack) {
        if (!entity.method_5805() || !(entity instanceof class_1309 living)) {
            return false;
        }
        return canTakeItem(living, stack);
    }

    /** from the mixin on 1.21.2's final canEquipWithDispenser: a mod class's own 1.21.1 override, or null */
    public static Boolean dispenserEquip(class_1309 entity, class_1799 stack) {
        if (!(entity instanceof Legacy legacy)) {
            return null;
        }
        return entity.method_5805() && !entity.method_7325() && legacy.method_18397(stack);
    }

    // ------------------------------------------------------------------------------------------ body armor

    /** a mod Mob class's own 1.21.1 isBodyArmorItem (adapter hook) */
    public interface LegacyBodyArmor {
        boolean method_6773(class_1799 stack);
    }

    /**
     * 1.21.1 Mob.isBodyArmorItem(stack), called: the mob class's own override, else 1.21.1's body of the vanilla class
     * (Mob false; Llama: a wool carpet; Horse: equestrian animal armor -- on 1.21.2 the armor's equippable component
     * names the body slot and the horse, which isEquippableInSlot(stack, BODY) answers).
     */
    public static boolean isBodyArmorItem(class_1308 mob, class_1799 stack) {
        if (mob instanceof LegacyBodyArmor legacy) {
            return legacy.method_6773(stack);
        }
        return superIsBodyArmorItem(mob, stack);
    }

    public static boolean superIsBodyArmorItem(class_1308 mob, class_1799 stack) {
        if (mob instanceof net.minecraft.class_1501) {
            return stack.method_31573(net.minecraft.class_3489.field_15542);
        }
        if (mob instanceof net.minecraft.class_1498 horse) {
            return horse.method_63623(stack, class_1304.field_48824);
        }
        return false;
    }

    public static boolean superIsBodyArmorItem(net.minecraft.class_1501 mob, class_1799 stack) {
        return superIsBodyArmorItem((class_1308) mob, stack);
    }

    public static boolean superIsBodyArmorItem(net.minecraft.class_1498 mob, class_1799 stack) {
        return superIsBodyArmorItem((class_1308) mob, stack);
    }

    /** from the mixin on isEquippableInSlot: the body slot answered by a mod class's own 1.21.1 isBodyArmorItem */
    public static Boolean bodySlot(class_1309 entity, class_1799 stack, class_1304 slot) {
        if (slot == class_1304.field_48824 && entity instanceof LegacyBodyArmor legacy) {
            return legacy.method_6773(stack);
        }
        return null;
    }

    private CbEntityTake() {
    }
}
