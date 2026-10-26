package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.rt.CbPrioritySplitClient;

/**
 * The injector into 1.21.4's own ItemRenderer.renderStatic that goes with {@link CbItemRendererMixin} (its restored
 * 1.21.3 members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.21.3 injectors can anchor inside them):
 * a 1.21.3 ItemRenderer subclass renders through getModel then render, as 1.21.3's renderStatic did.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_918.class)
public abstract class CbItemRendererMixinHooks {

    /** 1.21.3 renderStatic(entity, ..): getModel, then render -- for a subclass that overrides them */
    @Inject(method = "method_23177", at = @At("HEAD"), cancellable = true)
    private void cb$legacyRenderStatic(net.minecraft.class_1309 entity, net.minecraft.class_1799 stack, net.minecraft.class_811 context, boolean leftHand,
            net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, net.minecraft.class_1937 level, int light, int overlay, int seed, CallbackInfo ci) {
        if ((Object) this.getClass() == net.minecraft.class_918.class) {
            return;
        }
        ((CbPrioritySplitClient.ItemRenderStatic) (Object) this).cb$v1_21_4$renderStatic(entity, stack, context, leftHand, pose, buffers, level, light, overlay, seed);
        ci.cancel();
    }
}
