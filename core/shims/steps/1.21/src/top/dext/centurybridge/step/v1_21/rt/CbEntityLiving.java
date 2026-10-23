package top.dext.centurybridge.step.v1_21.rt;

import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1498;
import net.minecraft.class_1501;
import net.minecraft.class_1657;
import net.minecraft.class_174;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_1282;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_3468;
import net.minecraft.class_3763;
import net.minecraft.class_4174;
import net.minecraft.class_5151;
import net.minecraft.class_9334;
import top.dext.centurybridge.step.v1_21.mixin.CbEntityLivingInvoker;

/**
 * LivingEntity, Mob and Player: the 1.20.6 members 1.21 reshaped, given back with 1.20.6's behaviour on 1.21's
 * API. Wired from s6world.json (instanceRedirects, staticRedirects, frameDefaults, overrideHooks, superRedirects,
 * adapterHooks).
 * <p>
 * Where 1.21 added a parameter a 1.20.6 caller cannot supply, the call lands here and the value is what 1.20.6
 * meant: the entity's own server level for the server-side methods (they ran on the server only, as 1.21's do),
 * no attacker where 1.20.6 had none. Where a 1.20.6 subclass of a mod overrides the old shape, the converter gives
 * it the {@code Legacy*} interface of that shape (adapterHooks), so a call answers through the override as the
 * virtual call did in 1.20.6.
 */
public final class CbEntityLiving {

    // ------------------------------------------------------------------------------------------------ Entity
    /**
     * 1.20.6 Entity.igniteForSeconds(int): igniteForTicks(seconds * 20) (final). 1.21's takes float seconds and
     * floors seconds * 20F, the same number until the float product loses integer precision.
     */
    public static void igniteForSeconds(class_1297 entity, int seconds) {
        entity.method_56073(seconds * 20);
    }

    /** the level of an entity whose 1.20.6 method ran on the server only: 1.21 takes it as a ServerLevel */
    public static class_3218 serverLevel(class_1297 entity) {
        return (class_3218) entity.method_37908();
    }

    // ------------------------------------------------------------------------------------------------ equipment
    /**
     * 1.20.6 LivingEntity.getEquipmentSlotForItem(stack), static: the slot an Equipable item names, else the
     * main hand. 1.21's is an instance method that also asks whether the entity can use that slot.
     */
    public static class_1304 getEquipmentSlotForItem(class_1799 stack) {
        class_5151 equipable = class_5151.method_48957(stack);
        return equipable != null ? equipable.method_7685() : class_1304.field_6173;
    }

    /** the 1.20.6 broadcastBreakEvent(slot) a 1.20.6 subclass of a mod overrides */
    public interface LegacyBreakEvent {
        void method_20235(class_1304 slot);
    }

    /**
     * 1.20.6 LivingEntity.broadcastBreakEvent(slot): the equipment-break entity event, nothing else. 1.21's
     * onEquippedItemBroken(item, slot) is that event too, but on a ServerPlayer it also counts the ITEM_BROKEN
     * statistic, which 1.20.6 counted in ItemStack.hurtAndBreak, not here.
     */
    public static void broadcastBreakEvent(class_1309 entity, class_1304 slot) {
        if (entity instanceof LegacyBreakEvent legacy) {
            legacy.method_20235(slot);
            return;
        }
        entity.method_37908().method_8421(entity, CbEntityLivingInvoker.cb$entityEventForEquipmentBreak(slot));
    }

    /** onEquippedItemBroken's item for a 1.20.6 override entered directly: what the slot holds */
    public static class_1792 brokenItem(class_1309 entity, class_1304 slot) {
        return entity.method_6118(slot).method_7909();
    }

    // ------------------------------------------------------------------------------------------------ eat
    /**
     * 1.20.6 LivingEntity.eat(level, stack). With food, 1.21's eat(level, stack, food) -- asked virtually, so a
     * 1.20.6 override answers through its hook. Without food 1.20.6's LivingEntity did nothing and its Player
     * still ran the rest of its body: the food data found nothing to eat, the ITEM_USED statistic, the burp and
     * the consume trigger.
     */
    public static class_1799 eat(class_1309 entity, class_1937 level, class_1799 stack) {
        class_4174 food = foodOf(entity, level, stack);
        if (food != null) {
            return entity.method_18866(level, stack, food);
        }
        if (entity instanceof class_1657 player) {
            player.method_7259(class_3468.field_15372.method_14956(stack.method_7909()));
            level.method_43128(null, player.method_23317(), player.method_23318(), player.method_23321(),
                class_3417.field_19149, class_3419.field_15248, 0.5F, level.field_9229.method_43057() * 0.1F + 0.9F);
            if (player instanceof class_3222 serverPlayer) {
                class_174.field_1198.method_8821(serverPlayer, stack);
            }
        }
        return stack;
    }

    /** the food 1.20.6's eat read off the stack (1.21 passes it in) */
    public static class_4174 foodOf(class_1309 entity, class_1937 level, class_1799 stack) {
        return stack.method_57824(class_9334.field_50075);
    }

    // ------------------------------------------------------------------------------------------------ death
    /** 1.20.6 dropAllDeathLoot(source), protected: 1.21's on the entity's server level */
    public static void dropAllDeathLoot(class_1309 entity, class_1282 source) {
        ((CbEntityLivingInvoker) entity).cb$dropAllDeathLoot(serverLevel(entity), source);
    }

    /**
     * 1.20.6 dropExperience(), protected: 1.21's with no attacker. 1.21 passes the killer so its enchantment
     * effects can change the experience; 1.20.6 had none, and with no attacker 1.21 changes nothing either.
     */
    public static void dropExperience(class_1309 entity) {
        ((CbEntityLivingInvoker) entity).cb$dropExperience(null);
    }

    /** 1.20.6 onChangedBlock(pos), protected: 1.21's on the entity's server level (it ran on the server only) */
    public static void onChangedBlock(class_1309 entity, class_2338 pos) {
        ((CbEntityLivingInvoker) entity).cb$onChangedBlock(serverLevel(entity), pos);
    }

    // ------------------------------------------------------------------------------------------------ raids
    /** 1.20.6 Raider.applyRaidBuffs(wave, unused): 1.21's on the raider's server level */
    public static void applyRaidBuffs(class_3763 raider, int wave, boolean unused) {
        raider.method_16484(serverLevel(raider), wave, unused);
    }

    // ------------------------------------------------------------------------------------------------ riptide
    /**
     * The damage a spin attack started through 1.20.6's startAutoSpinAttack(ticks) carries: none of its own.
     * 1.20.6's Player.attack read ATTACK_DAMAGE during a spin as at any other time; 1.21 records a damage with
     * the spin (the trident's 8) and attacks with it. CbEntitySpinAttackMixin answers the attribute again for a
     * spin started with this value.
     */
    public static final float LEGACY_SPIN_DAMAGE = Float.NaN;

    /** 1.20.6 Player.startAutoSpinAttack(ticks): no damage of its own, no item (the main hand, as in 1.20.6) */
    public static void startAutoSpinAttack(class_1657 player, int ticks) {
        player.method_40126(ticks, LEGACY_SPIN_DAMAGE, null);
    }

    public static float legacySpinDamage() {
        return LEGACY_SPIN_DAMAGE;
    }

    // ------------------------------------------------------------------------------------------------ body armor
    /** the 1.20.6 canWearBodyArmor() a 1.20.6 subclass of a mod overrides */
    public interface LegacyBodyArmor {
        boolean method_6735();
    }

    /**
     * 1.20.6 Mob.canWearBodyArmor(): false on Mob, true on Horse and Llama -- the horse inventory's armor slot,
     * the dispenser's horse armor and the horse screen asked it, all on an AbstractHorse. 1.21 asks
     * canUseSlot(BODY) in those places instead, which Horse and Llama answer true, and also the wolf, which
     * 1.20.6 answered false. So a 1.20.6 subclass of a mod answers itself; any other horse answers 1.21's
     * canUseSlot(BODY) (exactly Horse, Llama and their subclasses among vanilla horses); anything else false.
     */
    public static boolean canWearBodyArmor(class_1308 mob) {
        if (mob instanceof LegacyBodyArmor legacy) {
            return legacy.method_6735();
        }
        return mob instanceof net.minecraft.class_1496 && mob.method_56991(class_1304.field_48824);
    }

    /**
     * 1.21 asks canUseSlot(slot) of a 1.20.6 subclass that overrides canWearBodyArmor(): for the body slot that
     * is the question 1.20.6 asked canWearBodyArmor() (see above); any other slot is super's answer (the guard
     * sends the repeated call down to it).
     */
    public static boolean canUseSlot(LegacyBodyArmor self, class_1304 slot) {
        if (slot == class_1304.field_48824) {
            return self.method_6735();
        }
        return ((class_1308) self).method_56991(slot);
    }

    /** a 1.20.6 super.canWearBodyArmor() landing on Mob's: false */
    public static boolean superCanWearBodyArmor(class_1308 mob) {
        return false;
    }

    /** a 1.20.6 super.canWearBodyArmor() landing on Horse's: true */
    public static boolean superCanWearBodyArmor(class_1498 horse) {
        return true;
    }

    /** a 1.20.6 super.canWearBodyArmor() landing on Llama's: true */
    public static boolean superCanWearBodyArmor(class_1501 llama) {
        return true;
    }

    private CbEntityLiving() {
    }
}
