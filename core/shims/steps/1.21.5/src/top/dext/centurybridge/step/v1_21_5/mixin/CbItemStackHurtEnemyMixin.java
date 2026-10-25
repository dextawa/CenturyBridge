package top.dext.centurybridge.step.v1_21_5.mixin;

import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_3468;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_5.rt.CbItemWeapon;

/**
 * ItemStack.hurtEnemy(target, attacker) asks 1.21.4's Item.hurtEnemy (restored, CbItemHurtEnemyRestoredMixin) for an
 * item that answers it otherwise than 1.21.5's path (CbItemWeapon.legacy): 1.21.4's body of ItemStack.hurtEnemy --
 * the item's answer decides the item-used stat and, through the return value, whether the attack goes on to
 * postHurtEnemy (Player.attack, LivingEntity.doHurtTarget). Every other item takes 1.21.5's body unchanged.
 */
@Mixin(class_1799.class)
public abstract class CbItemStackHurtEnemyMixin {

    @Inject(method = "method_7979(Lnet/minecraft/class_1309;Lnet/minecraft/class_1309;)Z", at = @At("HEAD"), cancellable = true)
    private void cb$legacyHurtEnemy(class_1309 target, class_1309 attacker, CallbackInfoReturnable<Boolean> cir) {
        class_1799 self = (class_1799) (Object) this;
        class_1792 item = self.method_7909();
        if (!CbItemWeapon.legacy(item)) {
            return;
        }
        boolean used = ((CbItemWeapon.ItemHurtEnemy) item).method_7873(self, target, attacker);
        if (used && attacker instanceof class_1657 player) {
            player.method_7259(class_3468.field_15372.method_14956(item));
        }
        cir.setReturnValue(used);
    }
}
