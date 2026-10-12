package top.dext.centurybridge.step.v1_21.rt;

import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1890;
import net.minecraft.class_1937;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_5819;

/**
 * Using, firing and wearing out items: the 1.20.6 shapes 1.21 gave an entity, a weapon or a level.
 *
 * <ul>
 * <li>getUseDuration(stack) became getUseDuration(stack, entity) (Item, ItemStack, CrossbowItem's static
 *     getChargeDuration): 1.20.6's answer depended on the stack alone, and of 1.21's implementations only
 *     the crossbow reads the entity -- for the random its charge-time enchantment effects are evaluated
 *     with. A 1.20.6 call passes no entity ({@link #cbNoEntity}); with none, the crossbow's effects get a
 *     random of their own (CbItemCrossbowChargeMixin), the answer 1.21's quick charge gives every holder.</li>
 * <li>ArrowItem.createArrow(level, ammo, shooter) gained the weapon the arrow is fired from (1.21's
 *     projectile enchantments read it): a 1.20.6 call fires from no weapon ({@link #cbNoWeapon}), which
 *     1.21's arrow takes as an arrow no weapon's enchantments touch -- 1.20.6's arrow.</li>
 * <li>Item.getAttackDamageBonus(player, damage) became (target, damage, source): 1.21 asks it only from
 *     Player.attack, the attacker being the source's entity.</li>
 * <li>ItemStack.hurtAndBreak(amount, random, player, onBreak) became (amount, level, player, onBreak(item)),
 *     and 1.21's also shrinks the stack when it breaks.</li>
 * </ul>
 */
public final class CbItemUse {

    private CbItemUse() {
    }

    // ---- getUseDuration, getChargeDuration ----

    /** the entity a 1.20.6 getUseDuration / getChargeDuration call passes: none (callReshapes) */
    public static class_1309 cbNoEntity() {
        return null;
    }

    /**
     * The random a crossbow's charge-time effects are evaluated with (CbItemCrossbowChargeMixin): the
     * holder's, else -- a 1.20.6 caller named none -- a fresh one.
     */
    public static class_5819 chargeRandom(class_1309 holder) {
        return holder != null ? holder.method_59922() : class_5819.method_43047();
    }

    // ---- createArrow ----

    /** the weapon a 1.20.6 createArrow call fires from: none (callReshapes; 1.21's arrow takes null as no weapon) */
    public static class_1799 cbNoWeapon() {
        return null;
    }

    // ---- getAttackDamageBonus ----

    /** the 1.20.6 Item.getAttackDamageBonus(Player, float) a mod's item overrides (adapter of the bridge hook) */
    public interface LegacyAttackDamageBonus {
        float method_58403(class_1657 attacker, float damage);
    }

    /** the accessor a mod item that super-calls getAttackDamageBonus gets: super's 1.21 getAttackDamageBonus */
    public interface AttackDamageBonusSuper {
        float cb$super$method_58403(class_1297 target, float damage, class_1282 source);
    }

    /**
     * 1.21 asks a 1.20.6 item for its attack damage bonus (Player.attack: the source's entity is the
     * attacking player): the mod's answer for that player. Any other attacker -- 1.20.6 never asked for
     * one -- gets no bonus. The target and the source stay at hand for the mod's super call
     * ({@link #superAttackDamageBonus}).
     */
    public static float attackDamageBonus(LegacyAttackDamageBonus item, class_1297 target, float damage, class_1282 source) {
        if (!(source.method_5526() instanceof class_1657 attacker)) {
            return 0.0F;
        }
        Guard.cbPush(item, new Object[] {target, damage, source});
        try {
            return item.method_58403(attacker, damage);
        } finally {
            Guard.cbPop();
        }
    }

    /**
     * A mod item's super.getAttackDamageBonus(player, damage): super's 1.21 answer for the target and source
     * of the attack being answered; outside one (the mod's own call), for no target and the player's
     * attack.
     */
    public static float superAttackDamageBonus(AttackDamageBonusSuper self, class_1657 attacker, float damage) {
        Object target = Guard.cbArgOrL(self, 0, null);
        Object source = Guard.cbArgOrL(self, 2, null);
        return self.cb$super$method_58403((class_1297) target, damage,
            source != null ? (class_1282) source : attacker.method_48923().method_48802(attacker));
    }

    /**
     * Item.getAttackDamageBonus(player, damage) called by a 1.20.6 mod (instanceRedirect): a 1.20.6 item's
     * own answer; any other item's 1.21 answer for the player's attack on no target known (Item's is 0;
     * the mace's fall bonus reads the attacker from the source).
     */
    public static float method_58403(class_1792 item, class_1657 attacker, float damage) {
        if (item instanceof LegacyAttackDamageBonus legacy) {
            return legacy.method_58403(attacker, damage);
        }
        return item.method_58403(null, damage, attacker.method_48923().method_48802(attacker));
    }

    /** MaceItem.getAttackDamageBonus(player, damage) called by a 1.20.6 mod: as for any item */
    public static float method_58403(net.minecraft.class_9362 item, class_1657 attacker, float damage) {
        return method_58403((class_1792) item, attacker, damage);
    }

    // ---- ProjectileWeaponItem ----

    /**
     * ProjectileWeaponItem.hasInfiniteArrows(weapon, ammo, creative) (gone in 1.21, whose infinity is the
     * ammo-use enchantment effect): 1.20.6's body -- creative, or a plain arrow from a weapon with infinity.
     */
    public static boolean method_57392(class_1799 weapon, class_1799 ammo, boolean creative) {
        return creative || ammo.method_31574(class_1802.field_8107)
            && CbEnchantmentHelper.method_8225(CbEnchVanilla.field_9125(), weapon) > 0;
    }

    /**
     * The level a 1.20.6 ProjectileWeaponItem.shoot call passes, as the ServerLevel 1.21's takes: 1.21 shoots
     * on the server only, and a client level has no 1.21 shoot to stand for.
     */
    public static class_3218 cbServerLevel(class_1937 level) {
        if (level instanceof class_3218 server) {
            return server;
        }
        throw new IllegalArgumentException("ProjectileWeaponItem.shoot on a client level: 1.21 shoots projectiles on the server only");
    }

    // ---- hurtAndBreak ----

    /**
     * ItemStack.hurtAndBreak(amount, random, player, onBreak) of 1.20.6 (instanceRedirect): 1.20.6's body,
     * its unbreaking step the world's (1.21's data-driven durability effects, evaluated in the level of the
     * player, else of the level whose random this is, else of the server's overworld; with no server
     * here, 1.20.6's own unbreaking formula with the random given). Like 1.20.6's, and unlike 1.21's, it
     * neither skips a creative player nor shrinks the stack: onBreak runs when the damage reaches the
     * maximum, and the caller does what it did then.
     */
    public static void method_7956(class_1799 stack, int amount, class_5819 random, class_3222 player, Runnable onBreak) {
        if (!stack.method_7963()) {
            return;
        }
        if (amount > 0) {
            class_3218 level = player != null ? player.method_51469() : CbItemServers.levelFor(random);
            if (level != null) {
                amount = class_1890.method_60117(level, stack, amount);
            } else {
                int unbreaking = CbEnchantmentHelper.method_8225(CbEnchVanilla.field_9119(), stack);
                int ignored = 0;
                for (int i = 0; unbreaking > 0 && i < amount; i++) {
                    if (top.dext.centurybridge.step.v1_21.facade.class_1885.method_8176(stack, unbreaking, random)) {
                        ignored++;
                    }
                }
                amount -= ignored;
            }
            if (amount <= 0) {
                return;
            }
        }
        if (player != null && amount != 0) {
            net.minecraft.class_174.field_1185.method_8960(player, stack, stack.method_7919() + amount);
        }
        int damage = stack.method_7919() + amount;
        stack.method_7974(damage);
        if (damage >= stack.method_7936()) {
            onBreak.run();
        }
    }
}
