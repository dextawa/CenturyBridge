package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestMobs;

/**
 * 1.20.4 called AbstractArrow.deflect() when an arrow hit an entity that deflects arrows; 1.20.5
 * deflects any projectile through the target's ProjectileDeflection (hitTargetOrDeflectSelf). For an
 * arrow whose class overrides 1.20.4's deflect() (CbRestMobs.LegacyDeflect, added by this step's adapter
 * hook) that deflection is the mod's method: 1.20.5's deflect runs as it does -- server side, then the
 * owner and onDeflection -- with the mod's deflect() in place of the ProjectileDeflection's. Every other
 * projectile deflects as 1.20.5 has it.
 */
@Mixin(net.minecraft.class_1676.class)
public abstract class CbRestProjectileMixin {

    @Shadow
    protected abstract void method_59525(net.minecraft.class_1297 target, boolean fromAttack);

    @Redirect(method = "method_59860", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1676;method_59859(Lnet/minecraft/class_9109;Lnet/minecraft/class_1297;Lnet/minecraft/class_1297;Z)V"))
    private void cbRest$legacyDeflect(net.minecraft.class_1676 self, net.minecraft.class_9109 deflection, net.minecraft.class_1297 target,
                                      net.minecraft.class_1297 owner, boolean fromAttack) {
        if (self instanceof CbRestMobs.LegacyDeflect legacy) {
            if (!self.method_37908().field_9236) {
                legacy.method_55052();
                self.method_7432(owner);
                this.method_59525(target, fromAttack);
            }
            return;
        }
        self.method_59859(deflection, target, owner, fromAttack);
    }
}
