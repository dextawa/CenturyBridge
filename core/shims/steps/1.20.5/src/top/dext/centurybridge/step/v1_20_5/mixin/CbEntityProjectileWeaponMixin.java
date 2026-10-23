package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbEntityCrossbow;

/** ProjectileWeaponItem.shoot: the weapon of the shot, for a 1.20.4 crossbow mob that aims its projectiles (CbEntityCrossbowItemMixin) */
@Mixin(net.minecraft.class_1811.class)
public abstract class CbEntityProjectileWeaponMixin {

    @Inject(method = "method_57393", at = @At("HEAD"))
    private void cbS4$shooting(net.minecraft.class_1937 level, net.minecraft.class_1309 shooter, net.minecraft.class_1268 hand,
                               net.minecraft.class_1799 weapon, java.util.List<net.minecraft.class_1799> projectiles, float velocity,
                               float inaccuracy, boolean crit, net.minecraft.class_1309 target, CallbackInfo ci) {
        CbEntityCrossbow.shooting(weapon);
    }

    @Inject(method = "method_57393", at = @At("RETURN"))
    private void cbS4$shot(net.minecraft.class_1937 level, net.minecraft.class_1309 shooter, net.minecraft.class_1268 hand,
                           net.minecraft.class_1799 weapon, java.util.List<net.minecraft.class_1799> projectiles, float velocity,
                           float inaccuracy, boolean crit, net.minecraft.class_1309 target, CallbackInfo ci) {
        CbEntityCrossbow.shot();
    }
}
