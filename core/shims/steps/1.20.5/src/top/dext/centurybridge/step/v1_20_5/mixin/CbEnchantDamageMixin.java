package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.Optional;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.facade.class_1310;
import top.dext.centurybridge.step.v1_20_5.rt.CbEnchant;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestMobType;

/**
 * DamageEnchantment by 1.20.4's MobType where it decided differently from 1.20.5's entity type tags:
 * a converted DamageEnchantment answers by its own 1.20.4 type (rt/CbEnchant); 1.20.5's smite, bane of
 * arthropods and impaling, hitting an entity whose class declares its MobType (a 1.20.4 mod's mob,
 * rt/CbRestMobType), pay their bonus -- and bane slows -- by that declaration, as 1.20.4's did.
 */
@Mixin(net.minecraft.class_1882.class)
public abstract class CbEnchantDamageMixin implements CbEnchant.DamageTargets {

    /** targets */
    @Shadow
    @Final
    private Optional<net.minecraft.class_6862<net.minecraft.class_1299<?>>> field_48369;

    @Override
    public Optional<net.minecraft.class_6862<net.minecraft.class_1299<?>>> cbS4$targets() {
        return field_48369;
    }

    /** getDamageBonus(level, entityType) */
    @Inject(method = "method_8196", at = @At("HEAD"), cancellable = true)
    private void cbS4$bonus(int level, net.minecraft.class_1299<?> type, CallbackInfoReturnable<Float> cir) {
        net.minecraft.class_1882 self = (net.minecraft.class_1882) (Object) this;
        if (CbEnchant.legacy(self) != null) {
            cir.setReturnValue(CbEnchant.damageBonus1204(CbEnchant.damageType(self), level, CbEnchant.mobType(type)));
            return;
        }
        class_1310 declared = type == null ? null : CbRestMobType.targetDeclared(type);
        if (declared != null) {
            Float bonus = CbEnchant.declaredBonus(self, level, declared);
            if (bonus != null) {
                cir.setReturnValue(bonus);
            }
        }
    }

    /** doPostAttack(attacker, target, level) */
    @Inject(method = "method_8189", at = @At("HEAD"), cancellable = true)
    private void cbS4$postAttack(net.minecraft.class_1309 attacker, net.minecraft.class_1297 target, int level, CallbackInfo ci) {
        if (CbEnchant.postAttack1204((net.minecraft.class_1882) (Object) this, attacker, target, level)) {
            ci.cancel();
        }
    }
}
