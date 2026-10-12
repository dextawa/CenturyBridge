package top.dext.centurybridge.step.v1_21.rt;

import top.dext.centurybridge.step.v1_21.facade.class_1887;

/**
 * Where 1.21's game asks enchantments for their effects, the 1.20.6 enchantments of mods answer through
 * their objects (CbEnchantments' records carry no 1.21 effect components; these hooks hand the
 * question to the object instead, at the points that correspond to 1.20.6's own calls):
 * <ul>
 * <li>damage protection (1.20.6 LivingEntity.getDamageAfterMagicAbsorb summed every enchantment's
 *     getDamageProtection over the armor slots) -- added to 1.21's
 *     EnchantmentHelper.getDamageProtection, over the same armor slots;</li>
 * <li>damage bonus (1.20.6 added getDamageBonus of the weapon's enchantments in Player.attack against
 *     the attacked entity, Mob.doHurtTarget, and ThrownTrident against a living target) -- added to
 *     1.21's EnchantmentHelper.modifyDamage at those calls, not at 1.21's newer ones (the sweep
 *     targets of a player's attack, an arrow's hit, which 1.20.6 never gave a bonus);</li>
 * <li>doPostHurt / doPostAttack (1.20.6 doPostHurtEffects / doPostDamageEffects after a hit, every
 *     slot of the victim and of the attacker, a player's main hand once more) -- run after 1.21's
 *     EnchantmentHelper.doPostAttackEffectsWithItemSource, which every 1.21 hit runs, except for the
 *     sweep targets 1.20.6 left alone;</li>
 * <li>doPostItemStackHurt (1.20.6 ItemStack.hurtEnemy, the stack's enchantments as they were before
 *     the hit, when the item accepted the hit) -- run at the end of 1.21's ItemStack.hurtEnemy.</li>
 * </ul>
 * 1.21's own enchantments keep 1.21's data-driven effects; only CenturyBridge's records hand back.
 */
public final class CbEnchEffects {

    private CbEnchEffects() {
    }

    private static final ThreadLocal<int[]> SWEEP = ThreadLocal.withInitial(() -> new int[1]);

    /** a player's attack is hurting its sweep targets (CbEnchPlayerMixin) */
    public static void enterSweep() {
        SWEEP.get()[0]++;
    }

    public static void exitSweep() {
        SWEEP.get()[0]--;
    }

    private static boolean inSweep() {
        return SWEEP.get()[0] > 0;
    }

    private interface Visitor {
        void accept(class_1887 enchantment, int level);
    }

    /** the 1.20.6 enchantments of CenturyBridge's records on a stack (mods' enchantments), with their levels */
    private static void forEachLegacy(net.minecraft.class_9304 enchantments, Visitor visitor) {
        if (enchantments == null || enchantments.method_57543()) {
            return;
        }
        for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<net.minecraft.class_6880<net.minecraft.class_1887>> e : enchantments.method_57539()) {
            class_1887 legacy = CbEnchantments.legacyOf(e.getKey().comp_349());
            if (legacy != null) {
                visitor.accept(legacy, e.getIntValue());
            }
        }
    }

    private static void forEachLegacy(net.minecraft.class_1799 stack, Visitor visitor) {
        if (stack != null && !stack.method_7960()) {
            forEachLegacy(stack.method_58657(), visitor);
        }
    }

    private static void forEachLegacy(Iterable<net.minecraft.class_1799> stacks, Visitor visitor) {
        for (net.minecraft.class_1799 stack : stacks) {
            forEachLegacy(stack, visitor);
        }
    }

    /** 1.20.6's protection sum of mods' enchantments over the entity's armor slots */
    public static float protection(net.minecraft.class_1309 entity, net.minecraft.class_1282 source) {
        int[] sum = {0};
        forEachLegacy(entity.method_5661(), (e, level) -> sum[0] += e.method_8181(level, source));
        return (float) sum[0];
    }

    /** 1.20.6's damage bonus of mods' enchantments on the weapon, where 1.20.6 asked for one */
    public static float damageBonus(net.minecraft.class_1799 weapon, net.minecraft.class_1297 target, net.minecraft.class_1282 source) {
        if (inSweep() || target == null) {
            return 0.0F;
        }
        net.minecraft.class_1297 direct = source.method_5526();
        if (direct instanceof net.minecraft.class_1685) {
            if (!(target instanceof net.minecraft.class_1309)) {
                return 0.0F;
            }
        } else if (direct instanceof net.minecraft.class_1665) {
            return 0.0F;
        }
        float[] sum = {0.0F};
        net.minecraft.class_1299<?> type = target.method_5864();
        forEachLegacy(weapon, (e, level) -> sum[0] += e.method_8196(level, type));
        return sum[0];
    }

    /** 1.20.6's doPostHurtEffects and doPostDamageEffects of mods' enchantments after a hit */
    public static void postAttack(net.minecraft.class_1297 target, net.minecraft.class_1282 source) {
        if (inSweep() || !(source.method_5529() instanceof net.minecraft.class_1309 attacker)) {
            return;
        }
        if (target instanceof net.minecraft.class_1309 victim) {
            Visitor hurt = (e, level) -> e.method_8178(victim, attacker, level);
            forEachLegacy(victim.method_56675(), hurt);
            if (attacker instanceof net.minecraft.class_1657) {
                forEachLegacy(victim.method_6047(), hurt);
            }
        }
        Visitor damage = (e, level) -> e.method_8189(attacker, target, level);
        forEachLegacy(attacker.method_56675(), damage);
        if (attacker instanceof net.minecraft.class_1657) {
            forEachLegacy(attacker.method_6047(), damage);
        }
    }

    private static final ThreadLocal<java.util.ArrayDeque<net.minecraft.class_9304>> BEFORE = ThreadLocal.withInitial(java.util.ArrayDeque::new);

    /** ItemStack.hurtEnemy is entered: the stack's enchantments before the hit (CbEnchItemStackMixin) */
    public static void pushBefore(net.minecraft.class_9304 enchantments) {
        BEFORE.get().push(enchantments);
    }

    /** ItemStack.hurtEnemy returns: the enchantments its entry recorded */
    public static net.minecraft.class_9304 popBefore() {
        return BEFORE.get().pop();
    }

    /** 1.20.6's doPostItemStackHurtEffects of mods' enchantments, with the enchantments the stack had before the hit */
    public static void postItemStackHurt(net.minecraft.class_9304 enchantmentsBefore, net.minecraft.class_1309 attacker, net.minecraft.class_1297 target) {
        forEachLegacy(enchantmentsBefore, (e, level) -> e.method_58675(attacker, target, level));
    }
}
