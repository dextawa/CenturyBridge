package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;
import top.dext.centurybridge.step.v1_20_5.rt.Guard;

/**
 * 1.20.4 Player declared getStandingEyeHeight(Pose, EntityDimensions) public (its eye height per pose);
 * 1.20.5 keeps those heights in the player's dimensions per pose, which is what the cover answers.
 * <p>
 * Player.attack evaluates the weapon's damage bonus with the attacked entity known
 * (CbRestMobType.cbDamageBonus; CbRestPlayerMixinHooks, default priority), so a target whose class declares a
 * 1.20.4 MobType gets its bonus.
 * <p>
 * 1.20.4 Player.disableShield(becauseOfAxe): the shield is disabled by chance -- a quarter, plus a
 * twentieth per level of block efficiency, plus three quarters when an axe did it -- which 1.20.5's
 * disableShield() always does (the attacker decides now); the cover rolls 1.20.4's chance and calls it
 * with the Guard held (a mod's 1.20.4 override is reconnected by this step's adapter hook).
 */
@Mixin(value = net.minecraft.class_1657.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestPlayerMixin {

    /** 1.20.4 Player.getStandingEyeHeight(Pose, EntityDimensions) (public) */
    public float method_18394(net.minecraft.class_4050 pose, net.minecraft.class_4048 dimensions) {
        return dimensions.comp_2187();
    }

    @org.spongepowered.asm.mixin.Shadow
    public abstract void method_7284();

    /** 1.20.4 Player.disableShield(becauseOfAxe) */
    public void method_7284(boolean becauseOfAxe) {
        float chance = 0.25F + net.minecraft.class_1890.method_8234((net.minecraft.class_1309) (Object) this) * 0.05F;
        if (becauseOfAxe) {
            chance += 0.75F;
        }
        if (((CbRestEntityInvoker) this).cbRest$random().method_43057() < chance) {
            boolean entered = Guard.cbEnter(this);
            try {
                this.method_7284();
            } finally {
                if (entered) {
                    Guard.cbExit(this);
                }
            }
        }
    }
}
