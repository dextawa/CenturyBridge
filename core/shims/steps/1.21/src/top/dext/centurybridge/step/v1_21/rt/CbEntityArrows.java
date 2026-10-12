package top.dext.centurybridge.step.v1_21.rt;

import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1547;
import net.minecraft.class_1665;
import net.minecraft.class_1675;
import net.minecraft.class_1744;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_243;
import net.minecraft.class_5134;
import top.dext.centurybridge.step.v1_21.mixin.CbEntitySkeletonInvoker;

/**
 * Arrows: what 1.20.6 set on an arrow after making it and 1.21 derives from the weapon that fired it -- the
 * knockback (Punch), the crossbow flag, the mob shooter's enchantment effects -- and the constructors that gained
 * the weapon. Wired from s6world.json; the arrow's own 1.20.6 state lives in CbEntityArrowMixin.
 * <p>
 * A 1.20.6 caller's arrow has no weapon: 1.20.6's constructors took none, and 1.21's take null for "none" (no
 * piercing, no spawn effects from a weapon) -- exactly the arrow 1.20.6 made, before its shooter set the rest.
 */
public final class CbEntityArrows {

    /** CbEntityArrowMixin's state: what a 1.20.6 caller set, null where it set nothing */
    public interface ArrowState {
        Integer cb$knockback();

        void cb$setKnockback(Integer knockback);

        Boolean cb$crossbow();

        void cb$setCrossbow(Boolean crossbow);
    }

    public interface LegacyGetKnockback {
        int method_35207();
    }

    public interface LegacySetKnockback {
        void method_7449(int knockback);
    }

    public interface LegacyCrossbow {
        void method_7442(boolean shotFromCrossbow);
    }

    public interface LegacyEnchantmentEffects {
        void method_7435(class_1309 shooter, float power);
    }

    /** the weapon of an arrow 1.20.6 made: none */
    public static class_1799 noWeapon() {
        return null;
    }

    /** the position of 1.20.6's (type, level, stack) constructor, which set none: the entity's initial origin */
    public static double origin() {
        return 0.0;
    }

    // ------------------------------------------------------------------------------------------------ knockback
    public static int getKnockback(class_1665 arrow) {
        if (arrow instanceof LegacyGetKnockback legacy) {
            return legacy.method_35207();
        }
        return superGetKnockback(arrow);
    }

    /**
     * 1.20.6 AbstractArrow.getKnockback(): the knockback field -- what a 1.20.6 caller set, else what 1.20.6's
     * shooters set from the firing bow's Punch level, which 1.21 finds on the arrow's weapon.
     */
    public static int superGetKnockback(class_1665 arrow) {
        Integer set = ((ArrowState) arrow).cb$knockback();
        if (set != null) {
            return set;
        }
        class_1799 weapon = arrow.method_59958();
        return weapon != null ? CbEnchantmentHelper.method_8225(CbEnchVanilla.field_9116(), weapon) : 0;
    }

    public static void setKnockback(class_1665 arrow, int knockback) {
        if (arrow instanceof LegacySetKnockback legacy) {
            legacy.method_7449(knockback);
        } else {
            superSetKnockback(arrow, knockback);
        }
    }

    /** 1.20.6 AbstractArrow.setKnockback(k): the hit pushes with k (CbEntityArrowMixin, doKnockback) */
    public static void superSetKnockback(class_1665 arrow, int knockback) {
        ((ArrowState) arrow).cb$setKnockback(knockback);
    }

    /**
     * 1.20.6's knockback of an arrow hit, for an arrow whose knockback a 1.20.6 caller set: the horizontal motion's
     * direction, times knockback x 0.6 x (1 - the target's knockback resistance), pushed with 0.1 upwards.
     */
    public static void doKnockback(class_1665 arrow, class_1309 target, int knockback) {
        if (knockback <= 0) {
            return;
        }
        double resist = Math.max(0.0, 1.0 - target.method_45325(class_5134.field_23718));
        class_243 push = arrow.method_18798().method_18805(1.0, 0.0, 1.0).method_1029().method_1021(knockback * 0.6 * resist);
        if (push.method_1027() > 0.0) {
            target.method_5762(push.field_1352, 0.1, push.field_1350);
        }
    }

    // ------------------------------------------------------------------------------------------------ crossbow
    public static void setShotFromCrossbow(class_1665 arrow, boolean crossbow) {
        if (arrow instanceof LegacyCrossbow legacy) {
            legacy.method_7442(crossbow);
        } else {
            superSetShotFromCrossbow(arrow, crossbow);
        }
    }

    /** 1.20.6 AbstractArrow.setShotFromCrossbow(b): shotFromCrossbow() answers b (CbEntityArrowMixin) */
    public static void superSetShotFromCrossbow(class_1665 arrow, boolean crossbow) {
        ((ArrowState) arrow).cb$setCrossbow(crossbow);
    }

    // ------------------------------------------------------------------------------------------------ mob shooters
    public static void setEnchantmentEffectsFromEntity(class_1665 arrow, class_1309 shooter, float power) {
        if (arrow instanceof LegacyEnchantmentEffects legacy) {
            legacy.method_7435(shooter, power);
        } else {
            superSetEnchantmentEffectsFromEntity(arrow, shooter, power);
        }
    }

    /**
     * 1.20.6 AbstractArrow.setEnchantmentEffectsFromEntity(shooter, power): base damage power x 2 plus the
     * difficulty's triangle draw; the shooter's Power adds level x 0.5 + 0.5, its Punch is the knockback, its Flame
     * sets the arrow alight for 100 seconds (1.20.6's EnchantmentHelper.getEnchantmentLevel, CbEnchantmentHelper).
     */
    public static void superSetEnchantmentEffectsFromEntity(class_1665 arrow, class_1309 shooter, float power) {
        int powerLevel = CbEnchantmentHelper.method_8203(CbEnchVanilla.field_9103(), shooter);
        int punchLevel = CbEnchantmentHelper.method_8203(CbEnchVanilla.field_9116(), shooter);
        arrow.method_7438(power * 2.0F + arrow.method_59922().method_43385(arrow.method_37908().method_8407().method_5461() * 0.11, 0.57425));
        if (powerLevel > 0) {
            arrow.method_7438(arrow.method_7448() + powerLevel * 0.5 + 0.5);
        }
        if (punchLevel > 0) {
            setKnockback(arrow, punchLevel);
        }
        if (CbEnchantmentHelper.method_8203(CbEnchVanilla.field_9126(), shooter) > 0) {
            arrow.method_56073(100 * 20);
        }
    }

    /**
     * 1.20.6 ProjectileUtil.getMobArrow(shooter, ammo, power): the ammo's arrow item (an arrow when the ammo is no
     * arrow item) makes the arrow -- with no weapon, as 1.20.6's createArrow took none -- and the shooter's
     * enchantment effects are set on it.
     */
    public static class_1665 getMobArrow(class_1309 shooter, class_1799 ammo, float power) {
        class_1744 item = (class_1744) (ammo.method_7909() instanceof class_1744 arrowItem ? arrowItem : class_1802.field_8107);
        class_1665 arrow = item.method_7702(shooter.method_37908(), ammo, shooter, null);
        setEnchantmentEffectsFromEntity(arrow, shooter, power);
        return arrow;
    }

    /** CbEntityMobArrowMixin: 1.21's mob arrow, for a 1.20.6 arrow class that overrides setEnchantmentEffectsFromEntity */
    public static void mobArrowMade(class_1665 arrow, class_1309 shooter, float power) {
        if (arrow instanceof LegacyEnchantmentEffects legacy) {
            legacy.method_7435(shooter, power);
        }
    }

    // ------------------------------------------------------------------------------------------------ skeletons
    /** the bow a skeleton shoots with, the weapon 1.21's performRangedAttack hands getArrow */
    public static class_1799 weaponOf(class_1297 skeleton) {
        class_1547 s = (class_1547) skeleton;
        return s.method_5998(class_1675.method_18812(s, class_1802.field_8102));
    }

    /** 1.20.6 skeleton.getArrow(ammo, power), protected: 1.21's with the bow the skeleton holds */
    public static class_1665 getArrow(class_1547 skeleton, class_1799 ammo, float power) {
        return ((CbEntitySkeletonInvoker) skeleton).cb$getArrow(ammo, power, weaponOf(skeleton));
    }

    private CbEntityArrows() {
    }
}
