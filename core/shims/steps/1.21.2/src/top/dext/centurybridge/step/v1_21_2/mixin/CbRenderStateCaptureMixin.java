package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderStateEntity;

/**
 * EntityRenderer.extractRenderState(entity, state, partialTick): every subclass's extraction ends in this one
 * (each override calls super first, 1.21.2's own and a mod's alike), so its HEAD sees every extraction of every
 * state, with the entity being extracted now (CbRenderStateEntity).
 */
@Mixin(net.minecraft.class_897.class)
public abstract class CbRenderStateCaptureMixin {

    @Inject(method = "method_62354(Lnet/minecraft/class_1297;Lnet/minecraft/class_10017;F)V", at = @At("HEAD"))
    private void cb$capture(net.minecraft.class_1297 entity, net.minecraft.class_10017 state, float partialTick, CallbackInfo ci) {
        ((CbRenderStateEntity) state).cb$capture(entity, partialTick);
    }
}
