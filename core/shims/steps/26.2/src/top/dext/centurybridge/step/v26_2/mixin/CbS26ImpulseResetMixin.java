package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26Entity;

/**
 * 26.1.2's LivingEntity.resetCurrentImpulseContext cleared currentExplosionCause with the impulse context; 26.2
 * keeps the field on ServerPlayer only, so the value a 26.1.2 mod keeps on another living entity (rt/CbS26Entity)
 * is cleared here, at the same point.
 */
@Mixin(LivingEntity.class)
public abstract class CbS26ImpulseResetMixin {

    @Inject(method = "resetCurrentImpulseContext", at = @At("TAIL"))
    private void cb$clearExplosionCause(CallbackInfo ci) {
        CbS26Entity.clearExplosionCause((LivingEntity) (Object) this);
    }
}
