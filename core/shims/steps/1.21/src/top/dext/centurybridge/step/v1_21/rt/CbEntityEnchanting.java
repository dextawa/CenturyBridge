package top.dext.centurybridge.step.v1_21.rt;

import net.minecraft.class_1266;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1528;
import net.minecraft.class_1538;
import net.minecraft.class_1548;
import net.minecraft.class_1560;
import net.minecraft.class_1604;
import net.minecraft.class_1613;
import net.minecraft.class_1639;
import net.minecraft.class_1642;
import net.minecraft.class_1657;
import net.minecraft.class_1685;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1890;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_4836;
import net.minecraft.class_5425;
import net.minecraft.class_5819;
import net.minecraft.class_9304;
import net.minecraft.class_9334;
import top.dext.centurybridge.step.v1_21.mixin.CbEntityLivingInvoker;
import top.dext.centurybridge.step.v1_21.mixin.CbEntityMobInvoker;

/**
 * The entity side of 1.20.6's enchantment code that 1.21 turned into enchantment data: the looting level handed
 * to dropCustomDeathLoot, the spawn equipment enchanting of Mob, doEnchantDamageEffects, the trident's
 * channeling. 1.20.6 callers get 1.20.6's bodies, over s6item's 1.20.6 enchantment objects (CbEnchVanilla,
 * CbEnchantmentHelper); a 1.20.6 subclass's overrides answer through the Legacy* interfaces, and 1.21 reaches
 * them through bridge hooks (s6world.json) where 1.21 asks the new shape. Wired from s6world.json.
 */
public final class CbEntityEnchanting {

    // ------------------------------------------------------------------------------------------------ death loot
    /** the 1.20.6 dropCustomDeathLoot(source, looting, recentlyHit) a 1.20.6 subclass of a mod overrides */
    public interface LegacyDeathLoot {
        void method_6099(class_1282 source, int looting, boolean recentlyHit);
    }

    /**
     * The looting level 1.20.6's dropAllDeathLoot handed dropCustomDeathLoot: the killer's mob looting when a
     * player killed, else 0.
     */
    public static int looting(class_1282 source) {
        return source.method_5529() instanceof class_1657 player ? CbEnchantmentHelper.method_8226(player) : 0;
    }

    /**
     * 1.20.6 entity.dropCustomDeathLoot(source, looting, recentlyHit), protected: a 1.20.6 override with the
     * looting asked for, else 1.21's on the entity's server level -- which works the looting out of the killer's
     * weapon itself (1.21's equipment drop chance through its enchantment effects), as 1.20.6's vanilla bodies did
     * from the level they were handed.
     */
    public static void dropCustomDeathLoot(class_1309 entity, class_1282 source, int looting, boolean recentlyHit) {
        if (entity instanceof LegacyDeathLoot legacy) {
            legacy.method_6099(source, looting, recentlyHit);
            return;
        }
        ((CbEntityLivingInvoker) entity).cb$dropCustomDeathLoot(CbEntityLiving.serverLevel(entity), source, recentlyHit);
    }

    /** 1.21's dropCustomDeathLoot(level, source, recentlyHit) on a 1.20.6 override: the looting 1.20.6 passed */
    public static void dropCustomDeathLoot(LegacyDeathLoot self, class_3218 level, class_1282 source, boolean recentlyHit) {
        self.method_6099(source, looting(source), recentlyHit);
    }

    /**
     * A 1.20.6 super.dropCustomDeathLoot(source, looting, recentlyHit): the vanilla body above the subclass, which
     * in 1.21 is the new shape -- reached by calling it again with the guard held, so the subclass's synthesized
     * new shape falls through to super's. 1.21's body works the looting out itself.
     */
    private static void superDrop(class_1309 entity, class_1282 source, boolean recentlyHit) {
        Guard.cbEnter(entity);
        try {
            ((CbEntityLivingInvoker) entity).cb$dropCustomDeathLoot(CbEntityLiving.serverLevel(entity), source, recentlyHit);
        } finally {
            Guard.cbExit(entity);
        }
    }

    // one per class that declared dropCustomDeathLoot in 1.20.6 (gen_s6world.py checks the list)
    public static void superDropCustomDeathLoot(class_1309 entity, class_1282 source, int looting, boolean hit) {
        superDrop(entity, source, hit);
    }

    public static void superDropCustomDeathLoot(class_1308 entity, class_1282 source, int looting, boolean hit) {
        superDrop(entity, source, hit);
    }

    public static void superDropCustomDeathLoot(class_1528 entity, class_1282 source, int looting, boolean hit) {
        superDrop(entity, source, hit);
    }

    public static void superDropCustomDeathLoot(class_1548 entity, class_1282 source, int looting, boolean hit) {
        superDrop(entity, source, hit);
    }

    public static void superDropCustomDeathLoot(class_1560 entity, class_1282 source, int looting, boolean hit) {
        superDrop(entity, source, hit);
    }

    public static void superDropCustomDeathLoot(class_1613 entity, class_1282 source, int looting, boolean hit) {
        superDrop(entity, source, hit);
    }

    public static void superDropCustomDeathLoot(class_1639 entity, class_1282 source, int looting, boolean hit) {
        superDrop(entity, source, hit);
    }

    public static void superDropCustomDeathLoot(class_1642 entity, class_1282 source, int looting, boolean hit) {
        superDrop(entity, source, hit);
    }

    public static void superDropCustomDeathLoot(class_4836 entity, class_1282 source, int looting, boolean hit) {
        superDrop(entity, source, hit);
    }

    // ------------------------------------------------------------------------------------------------ spawn equipment
    /** the 1.20.6 enchantSpawnedWeapon(random, chanceMultiplier) a 1.20.6 subclass of a mod overrides */
    public interface LegacySpawnWeapon {
        void method_30759(class_5819 random, float chanceMultiplier);
    }

    /** the 1.20.6 enchantSpawnedArmor(random, chanceMultiplier, slot) a 1.20.6 subclass of a mod overrides */
    public interface LegacySpawnArmor {
        void method_30758(class_5819 random, float chanceMultiplier, class_1304 slot);
    }

    /** a mob's server level as 1.21's spawn equipment methods take it */
    public static class_5425 serverLevelAccessor(class_1297 entity) {
        return (class_5425) entity.method_37908();
    }

    /** 1.20.6 populateDefaultEquipmentEnchantments(random, difficulty), protected: 1.21's on the mob's server level */
    public static void populateDefaultEquipmentEnchantments(class_1308 mob, class_5819 random, class_1266 difficulty) {
        ((CbEntityMobInvoker) mob).cb$populateDefaultEquipmentEnchantments(serverLevelAccessor(mob), random, difficulty);
    }

    /** 1.20.6 mob.enchantSpawnedWeapon(random, chanceMultiplier), protected */
    public static void enchantSpawnedWeapon(class_1308 mob, class_5819 random, float chanceMultiplier) {
        if (mob instanceof LegacySpawnWeapon legacy) {
            legacy.method_30759(random, chanceMultiplier);
        } else if (mob instanceof class_1604 pillager) {
            superEnchantSpawnedWeapon(pillager, random, chanceMultiplier);
        } else {
            superEnchantSpawnedWeapon(mob, random, chanceMultiplier);
        }
    }

    /** 1.21's enchantSpawnedWeapon(level, random, difficulty) on a 1.20.6 override: 1.20.6 passed the special multiplier */
    public static void enchantSpawnedWeapon(LegacySpawnWeapon self, class_5425 level, class_5819 random, class_1266 difficulty) {
        self.method_30759(random, difficulty.method_5458());
    }

    /**
     * 1.20.6 Mob.enchantSpawnedWeapon: with a main-hand item, a chance of 0.25 x the multiplier to enchant it at
     * level 5 + multiplier x random(18), no treasure (1.20.6's enchantItem, CbEnchantmentHelper).
     */
    public static void superEnchantSpawnedWeapon(class_1308 mob, class_5819 random, float chanceMultiplier) {
        class_1799 stack = mob.method_6047();
        if (!stack.method_7960() && random.method_43057() < 0.25F * chanceMultiplier) {
            mob.method_5673(class_1304.field_6173, CbEnchantmentHelper.method_8233(mob.method_37908().method_45162(), random,
                stack, (int) (5.0F + chanceMultiplier * random.method_43048(18)), false));
        }
    }

    /** 1.20.6 Pillager.enchantSpawnedWeapon: Mob's, then one in 300 a crossbow gets Piercing I */
    public static void superEnchantSpawnedWeapon(class_1604 pillager, class_5819 random, float chanceMultiplier) {
        superEnchantSpawnedWeapon((class_1308) pillager, random, chanceMultiplier);
        if (random.method_43048(300) == 0) {
            class_1799 stack = pillager.method_6047();
            if (stack.method_31574(class_1802.field_8399)) {
                stack.method_7978(CbEnchantments.cbHolder(CbEnchVanilla.field_9132()), 1);
                pillager.method_5673(class_1304.field_6173, stack);
            }
        }
    }

    /** 1.20.6 mob.enchantSpawnedArmor(random, chanceMultiplier, slot), protected */
    public static void enchantSpawnedArmor(class_1308 mob, class_5819 random, float chanceMultiplier, class_1304 slot) {
        if (mob instanceof LegacySpawnArmor legacy) {
            legacy.method_30758(random, chanceMultiplier, slot);
        } else {
            superEnchantSpawnedArmor(mob, random, chanceMultiplier, slot);
        }
    }

    /** 1.21's enchantSpawnedArmor(level, random, slot, difficulty) on a 1.20.6 override */
    public static void enchantSpawnedArmor(LegacySpawnArmor self, class_5425 level, class_5819 random, class_1304 slot,
                                           class_1266 difficulty) {
        self.method_30758(random, difficulty.method_5458(), slot);
    }

    /** 1.20.6 Mob.enchantSpawnedArmor: a chance of 0.5 x the multiplier to enchant the slot's item, as for the weapon */
    public static void superEnchantSpawnedArmor(class_1308 mob, class_5819 random, float chanceMultiplier, class_1304 slot) {
        class_1799 stack = mob.method_6118(slot);
        if (!stack.method_7960() && random.method_43057() < 0.5F * chanceMultiplier) {
            mob.method_5673(slot, CbEnchantmentHelper.method_8233(mob.method_37908().method_45162(), random, stack,
                (int) (5.0F + chanceMultiplier * random.method_43048(18)), false));
        }
    }

    // ------------------------------------------------------------------------------------------------ post-attack effects
    /** the 1.20.6 doEnchantDamageEffects(attacker, target) a 1.20.6 subclass of a mod overrides */
    public interface LegacyEnchantDamageEffects {
        void method_5723(class_1309 attacker, class_1297 target);
    }

    /** 1.20.6 entity.doEnchantDamageEffects(attacker, target): the entity's own override, else 1.20.6's body */
    public static void doEnchantDamageEffects(class_1297 entity, class_1309 attacker, class_1297 target) {
        if (entity instanceof LegacyEnchantDamageEffects legacy) {
            legacy.method_5723(attacker, target);
        } else {
            legacyDamageEffects(attacker, target);
        }
    }

    /**
     * 1.20.6 Entity.doEnchantDamageEffects: the target's post-hurt effects (thorns and the like) when it lives, then
     * the attacker's post-damage effects (bane of arthropods' slowness and the like) -- 1.20.6's
     * EnchantmentHelper.doPostHurtEffects / doPostDamageEffects (CbEnchantmentHelper).
     */
    private static void legacyDamageEffects(class_1309 attacker, class_1297 target) {
        if (target instanceof class_1309 living) {
            CbEnchantmentHelper.method_8210(living, attacker);
        }
        CbEnchantmentHelper.method_8213(attacker, target);
    }

    /** one 1.21 doPostAttackEffects call handed to a 1.20.6 override: what vanilla passed */
    private static final class PostAttack {
        final class_1297 direct;
        final class_3218 level;
        final class_1297 target;
        final class_1282 source;
        /** set for the one call the override's super makes, which 1.21's effects then run for */
        boolean pass;

        PostAttack(class_1297 direct, class_3218 level, class_1297 target, class_1282 source) {
            this.direct = direct;
            this.level = level;
            this.target = target;
            this.source = source;
        }
    }

    /** the post-attack calls in progress on this thread, innermost first (an effect's hit can attack again) */
    private static final ThreadLocal<java.util.ArrayDeque<PostAttack>> POST_ATTACKS =
        ThreadLocal.withInitial(java.util.ArrayDeque::new);

    /**
     * CbEntityPostAttackMixin, at the head of 1.21's EnchantmentHelper.doPostAttackEffects(level, target, source):
     * 1.21 calls it after a hit where 1.20.6 called this.doEnchantDamageEffects(attacker, target) on the hitting
     * entity (Mob.doHurtTarget, the fireballs, the wither skull...). When that entity -- the source's direct entity
     * -- is a 1.20.6 class overriding doEnchantDamageEffects, and the source names a living attacker, the override
     * answers instead (true: 1.21's call is cancelled). Its super call then runs 1.21's own effects for this very
     * call (superDoEnchantDamageEffects).
     */
    public static boolean legacyPostAttack(class_3218 level, class_1297 target, class_1282 source) {
        java.util.ArrayDeque<PostAttack> calls = POST_ATTACKS.get();
        PostAttack top = calls.peek();
        if (top != null && top.pass) {
            top.pass = false;
            return false;
        }
        if (!(source.method_5526() instanceof LegacyEnchantDamageEffects legacy) || !(source.method_5529() instanceof class_1309 attacker)) {
            return false;
        }
        calls.push(new PostAttack(source.method_5526(), level, target, source));
        try {
            legacy.method_5723(attacker, target);
        } finally {
            calls.pop();
        }
        return true;
    }

    /**
     * A 1.20.6 super.doEnchantDamageEffects(attacker, target). Inside the call vanilla made (the frame above, for
     * this entity, attacker and target): 1.21's doPostAttackEffects with what vanilla passed -- 1.21 keeps these
     * effects in the enchantments' post-attack data (fire aspect's ignition, thorns, bane of arthropods,
     * channeling, wind burst), and s6item's hook in it runs the 1.20.6 enchantments of mods. Outside one (the
     * subclass's own call): 1.20.6's body.
     */
    public static void superDoEnchantDamageEffects(class_1297 entity, class_1309 attacker, class_1297 target) {
        PostAttack call = POST_ATTACKS.get().peek();
        if (call != null && call.direct == entity && call.target == target && call.source.method_5529() == attacker) {
            call.pass = true;
            try {
                class_1890.method_60107(call.level, target, call.source);
            } finally {
                call.pass = false;
            }
            return;
        }
        legacyDamageEffects(attacker, target);
    }

    // ------------------------------------------------------------------------------------------------ channeling
    /**
     * the 1.20.6 isChanneling(): CbEntityTridentMixin restores it on every ThrownTrident (a mod's 1.20.6 mixin on it
     * lands there) and a 1.20.6 subclass of a mod overrides it
     */
    public interface LegacyChanneling {
        boolean method_31551();
    }

    /**
     * the per-trident state of CbEntityTridentMixin's hit: the enchantments it took Channeling off for the call, and
     * isChanneling()'s answer for this hit (asked once, as 1.20.6 asked it once)
     */
    public interface TridentHit {
        class_9304 cb$heldEnchantments();

        void cb$holdEnchantments(class_9304 enchantments);

        Boolean cb$channelingAnswer();

        void cb$channelingAnswer(Boolean answer);
    }

    /** 1.20.6 trident.isChanneling() as a mod calls it: the restored member, or the trident's own override */
    public static boolean isChanneling(class_1685 trident) {
        if (trident instanceof LegacyChanneling legacy) {
            return legacy.method_31551();
        }
        return superIsChanneling(trident);
    }

    /** 1.20.6 ThrownTrident.isChanneling()'s body: EnchantmentHelper.hasChanneling(the thrown stack) */
    public static boolean superIsChanneling(class_1685 trident) {
        return CbEnchantmentHelper.method_8228(trident.method_54759());
    }

    /** whether the trident's weapon carries Channeling, 1.21's own condition for its strike */
    private static boolean enchanted(class_1685 trident) {
        class_1799 weapon = trident.method_59958();
        return weapon != null && class_1890.method_8225(CbEnchantments.cbHolder(CbEnchVanilla.field_9117()), weapon) > 0;
    }

    /** isChanneling()'s answer for the current hit: asked through the trident (mods' mixins run) the first time only */
    private static boolean answer(class_1685 trident, TridentHit hit) {
        if (hit.cb$channelingAnswer() == null) {
            hit.cb$channelingAnswer(((LegacyChanneling) trident).method_31551());
        }
        return hit.cb$channelingAnswer();
    }

    /** Channeling off the weapon for 1.21's effect call (afterWeaponEffects puts it back) */
    private static void holdChanneling(class_1685 trident, TridentHit hit) {
        class_1799 weapon = trident.method_59958();
        hit.cb$holdEnchantments(weapon.method_57824(class_9334.field_49633));
        class_1890.method_57531(weapon, mutable -> mutable.method_57547(CbEnchantments.cbHolder(CbEnchVanilla.field_9117()), 0));
    }

    /**
     * Before 1.21 runs the post-attack effects of a trident's entity hit: 1.20.6 struck lightning when, in a
     * thunderstorm on a server level, isChanneling() said so; 1.21 strikes when the weapon carries Channeling (its
     * channeling.json post_attack effect, which also requires the thunderstorm). So isChanneling() is asked here
     * under 1.20.6's condition, and where it says no while the weapon has Channeling, Channeling is taken off the
     * weapon for the call; where it says yes without the enchantment, 1.20.6's own strike follows (afterEntityHit).
     * With nothing changing isChanneling() its answer is the enchantment's presence: 1.21's call runs untouched.
     */
    public static void beforeWeaponEffects(class_1685 trident) {
        if (!(trident instanceof TridentHit hit) || !(trident instanceof LegacyChanneling)
                || !(trident.method_37908() instanceof class_3218 level) || !level.method_8546()) {
            return;
        }
        if (!answer(trident, hit) && enchanted(trident)) {
            holdChanneling(trident, hit);
        }
    }

    /**
     * Before 1.21 runs a trident's hit-block effects: 1.20.6 asked isChanneling() from LightningRodBlock.onProjectileHit
     * only for a lightning rod in a thunderstorm (channeling.json's hit_block effect has the same requirements), so
     * it is asked under that condition and handled as beforeWeaponEffects does.
     */
    public static void beforeBlockEffects(class_1685 trident, class_3218 level, class_2338 pos) {
        if (!(trident instanceof TridentHit hit) || !(trident instanceof LegacyChanneling) || !level.method_8546()
                || !(level.method_8320(pos).method_26204() instanceof net.minecraft.class_5554)) {
            return;
        }
        if (!answer(trident, hit) && enchanted(trident)) {
            holdChanneling(trident, hit);
        }
    }

    /** after the weapon effects: Channeling back on the weapon if it was taken off */
    public static void afterWeaponEffects(class_1685 trident) {
        if (trident instanceof TridentHit hit && hit.cb$heldEnchantments() != null) {
            trident.method_59958().method_57379(class_9334.field_49633, hit.cb$heldEnchantments());
            hit.cb$holdEnchantments(null);
        }
    }

    /**
     * 1.20.6 ThrownTrident.onHitEntity's strike where 1.21 makes none: in a thunderstorm on a server level, when
     * isChanneling() (asked once per hit, here if the post-attack effects did not run) says yes and the weapon has no
     * Channeling, a lightning bolt on the target where the sky is open, caused by the throwing player, and the
     * thunder sound at volume 5.
     */
    public static void afterEntityHit(class_1685 trident, class_1297 target) {
        if (!(trident instanceof TridentHit hit) || !(trident instanceof LegacyChanneling)) {
            return;
        }
        try {
            if (trident.method_37908() instanceof class_3218 level && level.method_8546()
                    && answer(trident, hit) && !enchanted(trident)) {
                class_2338 pos = target.method_24515();
                if (level.method_8311(pos)) {
                    strike(level, trident, class_243.method_24955(pos));
                    trident.method_5783(class_3417.field_14896.comp_349(), 5.0F, 1.0F);
                }
            }
        } finally {
            hit.cb$channelingAnswer(null);
        }
    }

    /**
     * 1.20.6 LightningRodBlock.onProjectileHit's strike where 1.21 makes none: when isChanneling() said yes for a
     * lightning rod hit in a thunderstorm (asked by beforeBlockEffects) and the weapon has no Channeling, under open
     * sky a strike above the rod, with the thunder sound at volume 5 in the weather source.
     */
    public static void afterBlockHit(class_1685 trident, class_3218 level, class_2338 pos) {
        if (!(trident instanceof TridentHit hit)) {
            return;
        }
        try {
            if (Boolean.TRUE.equals(hit.cb$channelingAnswer()) && !enchanted(trident) && level.method_8311(pos)) {
                strike(level, trident, class_243.method_24955(pos.method_10084()));
                level.method_8396(null, pos, class_3417.field_14896.comp_349(), class_3419.field_15252, 5.0F, 1.0F);
            }
        } finally {
            hit.cb$channelingAnswer(null);
        }
    }

    private static void strike(class_3218 level, class_1685 trident, class_243 at) {
        class_1538 bolt = (class_1538) net.minecraft.class_1299.field_6112.method_5883(level);
        if (bolt != null) {
            bolt.method_29495(at);
            bolt.method_6961(trident.method_24921() instanceof class_3222 player ? player : null);
            level.method_8649(bolt);
        }
    }

    private CbEntityEnchanting() {
    }
}
