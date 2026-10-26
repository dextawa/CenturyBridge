package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbRestMobs;
import top.dext.centurybridge.step.v1_20_5.rt.CbRoutes;

/**
 * 1.20.4 called AbstractArrow.deflect() when an arrow (or trident) hit an entity that deflects arrows (the
 * deflects_arrows tag: the breeze); 1.20.5 deflects any projectile through the target's ProjectileDeflection
 * (hitTargetOrDeflectSelf; the breeze answers REVERSE for an arrow). Before 1.20.5's Projectile.deflect call there,
 * the deflection is 1.20.4's deflect() for
 * - an arrow whose class overrides 1.20.4's deflect() (CbRestMobs.LegacyDeflect, added by this step's adapter hook):
 *   the mod's method;
 * - any arrow when a converted mod mixes the restored AbstractArrow.deflect() (route, CbMixedMembers).
 * Then, as 1.20.5's deflect, server side only, the owner is set again and onDeflection runs, and
 * hitTargetOrDeflectSelf answers the deflection. Every other deflection runs 1.20.5's own call untouched (an inject,
 * not a redirect: native 1.20.5 mixins on that call and on Projectile.deflect still apply).
 */
@Mixin(net.minecraft.class_1676.class)
public abstract class CbRestProjectileMixin {

    @Shadow
    protected abstract void method_59525(net.minecraft.class_1297 target, boolean fromAttack);

    @Inject(method = "method_59860", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1676;method_59859(Lnet/minecraft/class_9109;Lnet/minecraft/class_1297;Lnet/minecraft/class_1297;Z)V"),
        cancellable = true)
    private void cbRest$legacyDeflect(net.minecraft.class_239 hit, CallbackInfoReturnable<net.minecraft.class_9109> cir) {
        net.minecraft.class_1676 self = (net.minecraft.class_1676) (Object) this;
        Runnable deflect;
        if (self instanceof CbRestMobs.LegacyDeflect legacy) {
            deflect = legacy::method_55052;
        } else if (self instanceof net.minecraft.class_1665 && CbRoutes.route(CbRoutes.ARROW_DEFLECT, self)) {
            deflect = ((CbRoutes.ArrowDeflect) self)::method_55052;
        } else {
            return;
        }
        // the call is reached only for an entity hit whose deflection is not NONE (method_59860); the target's
        // deflection is asked again for the answer rather than captured (local capture does not climb past 1.21)
        net.minecraft.class_1297 target = ((net.minecraft.class_3966) hit).method_17782();
        if (!self.method_37908().field_9236) {
            deflect.run();
            self.method_7432(self.method_24921());
            this.method_59525(target, false);
        }
        cir.setReturnValue(target.method_56071(self));
    }
}
