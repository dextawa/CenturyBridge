package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbEnchEffects;

/**
 * ItemStack.hurtEnemy: 1.20.6 read the stack's enchantments before the item took the hit (it may break)
 * and, when the item accepted the hit, ran their doPostItemStackHurt; 1.21 runs none. For 1.20.6 mods'
 * enchantments the same happens here (CbEnchEffects.postItemStackHurt).
 */
@Mixin(net.minecraft.class_1799.class)
public abstract class CbEnchItemStackMixin {

    @Inject(method = "method_7979", at = @At("HEAD"))
    private void cbEnch$before(net.minecraft.class_1309 target, net.minecraft.class_1657 attacker, CallbackInfoReturnable<Boolean> cir) {
        CbEnchEffects.pushBefore(((net.minecraft.class_1799) (Object) this).method_58657());
    }

    @Inject(method = "method_7979", at = @At("RETURN"))
    private void cbEnch$after(net.minecraft.class_1309 target, net.minecraft.class_1657 attacker, CallbackInfoReturnable<Boolean> cir) {
        net.minecraft.class_9304 before = CbEnchEffects.popBefore();
        if (cir.getReturnValueZ()) {
            CbEnchEffects.postItemStackHurt(before, attacker, target);
        }
    }
}
