package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbEnchEffects;

/** 1.21's EnchantmentHelper effect entry points, where 1.20.6 mods' enchantments add their objects' answers (CbEnchEffects) */
@Mixin(net.minecraft.class_1890.class)
public abstract class CbEnchHelperMixin {

    /** getDamageProtection(ServerLevel, LivingEntity, DamageSource) */
    @Inject(method = "method_8219", at = @At("RETURN"), cancellable = true)
    private static void cbEnch$protection(net.minecraft.class_3218 level, net.minecraft.class_1309 entity, net.minecraft.class_1282 source,
            CallbackInfoReturnable<Float> cir) {
        float legacy = CbEnchEffects.protection(entity, source);
        if (legacy != 0.0F) {
            cir.setReturnValue(cir.getReturnValueF() + legacy);
        }
    }

    /** modifyDamage(ServerLevel, ItemStack, Entity, DamageSource, float) */
    @Inject(method = "method_60120", at = @At("RETURN"), cancellable = true)
    private static void cbEnch$damage(net.minecraft.class_3218 level, net.minecraft.class_1799 weapon, net.minecraft.class_1297 target,
            net.minecraft.class_1282 source, float base, CallbackInfoReturnable<Float> cir) {
        float legacy = CbEnchEffects.damageBonus(weapon, target, source);
        if (legacy != 0.0F) {
            cir.setReturnValue(cir.getReturnValueF() + legacy);
        }
    }

    /** doPostAttackEffectsWithItemSource(ServerLevel, Entity, DamageSource, ItemStack): every 1.21 hit's post-attack effects */
    @Inject(method = "method_60619", at = @At("TAIL"))
    private static void cbEnch$postAttack(net.minecraft.class_3218 level, net.minecraft.class_1297 target, net.minecraft.class_1282 source,
            net.minecraft.class_1799 weapon, CallbackInfo ci) {
        CbEnchEffects.postAttack(target, source);
    }
}
