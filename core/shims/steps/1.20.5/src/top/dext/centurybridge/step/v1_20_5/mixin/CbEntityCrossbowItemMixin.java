package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.rt.CbEntityCrossbow;

/**
 * CrossbowItem.shootProjectile: a 1.20.4 crossbow mob that declares shootCrossbowProjectile(target, crossbow,
 * projectile, angle) aims the projectile itself, as 1.20.4's CrossbowItem had it do; everything after the aim
 * (the shot sound) stays 1.20.5's, as 1.20.4's item played its own after the mob's (rt/CbEntityCrossbow).
 */
@Mixin(net.minecraft.class_1764.class)
public abstract class CbEntityCrossbowItemMixin {

    @Redirect(method = "method_7763", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1676;method_7485(DDDFF)V"))
    private void cbS4$aim(net.minecraft.class_1676 projectile, double x, double y, double z, float velocity, float inaccuracy,
                          net.minecraft.class_1309 shooter, net.minecraft.class_1676 shot, int index, float shotVelocity,
                          float shotInaccuracy, float angle, net.minecraft.class_1309 target) {
        if (shooter instanceof CbEntityCrossbow.Shooter mob) {
            mob.method_18811(target, CbEntityCrossbow.weapon(), projectile, angle);
        } else {
            projectile.method_7485(x, y, z, velocity, inaccuracy);
        }
    }
}
