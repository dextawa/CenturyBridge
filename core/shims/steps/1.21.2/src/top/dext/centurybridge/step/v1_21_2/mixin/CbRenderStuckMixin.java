package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderStuck;

/** a 1.21.1 StuckInBodyLayer subclass draws with 1.21.1's body and its own renderStuckItem (see CbRenderStuck) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_4507.class)
public abstract class CbRenderStuckMixin {

    @Inject(method = "method_22132(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;ILnet/minecraft/class_10055;FF)V", at = @At("HEAD"), cancellable = true)
    private void cb$legacy(net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light, net.minecraft.class_10055 state,
                           float yRot, float xRot, CallbackInfo ci) {
        if (this instanceof CbRenderStuck.Legacy
                && CbRenderStuck.render((net.minecraft.class_4507<?>) (Object) this, poseStack, buffers, light, state)) {
            ci.cancel();
        }
    }
}
