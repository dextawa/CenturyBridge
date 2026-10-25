package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_11683;
import net.minecraft.class_11954;
import net.minecraft.class_243;
import net.minecraft.class_2586;
import net.minecraft.class_824;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_9.rt.CbBlockEntityRenders;

/**
 * BlockEntityRenderDispatcher.tryExtractRenderState: stamps the extracted state with the block entity, partial tick
 * and camera position 1.21.8's render took (CbBlockEntityRenders).
 */
@Mixin(class_824.class)
public abstract class CbBlockEntityDispatchMixin {

    @Shadow
    private class_243 field_63069;

    @Inject(method = "method_74348(Lnet/minecraft/class_2586;FLnet/minecraft/class_11683$class_11792;)Lnet/minecraft/class_11954;",
        at = @At("RETURN"))
    private void cb$stampLegacy(class_2586 blockEntity, float partialTick, class_11683.class_11792 crumbling,
                                CallbackInfoReturnable<class_11954> cir) {
        class_11954 state = cir.getReturnValue();
        if (state != null) {
            ((CbBlockEntityRenders.Info) state).cb$legacy(blockEntity, partialTick, this.field_63069);
        }
    }
}
