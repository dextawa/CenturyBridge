package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_4.rt.CbItemRender;

/**
 * ItemRenderer's 1.21.3 model-based methods, given back on 1.21.4 (see {@link CbItemRender}). 1.21.3's renderStatic
 * was getModel then render, both virtual: a 1.21.3 ItemRenderer subclass overriding either is drawn through them again.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_918.class)
public abstract class CbItemRendererMixin {

    private net.minecraft.class_918 cb$self() {
        return (net.minecraft.class_918) (Object) this;
    }

    /** was render(ItemStack, ItemDisplayContext, boolean, PoseStack, MultiBufferSource, int, int, BakedModel) */
    public void method_23179(net.minecraft.class_1799 stack, net.minecraft.class_811 context, boolean leftHand, net.minecraft.class_4587 pose,
            net.minecraft.class_4597 buffers, int light, int overlay, net.minecraft.class_1087 model) {
        CbItemRender.render(cb$self(), stack, context, leftHand, pose, buffers, light, overlay, model);
    }

    /** was render(.., BakedModel, Level, LivingEntity, int) */
    public void method_62477(net.minecraft.class_1799 stack, net.minecraft.class_811 context, boolean leftHand, net.minecraft.class_4587 pose,
            net.minecraft.class_4597 buffers, int light, int overlay, net.minecraft.class_1087 model, net.minecraft.class_1937 level,
            net.minecraft.class_1309 entity, int seed) {
        CbItemRender.render(cb$self(), stack, context, leftHand, pose, buffers, light, overlay, model, level, entity, seed);
    }

    /** was getModel(ItemStack, Level, LivingEntity, int) */
    public net.minecraft.class_1087 method_4019(net.minecraft.class_1799 stack, net.minecraft.class_1937 level, net.minecraft.class_1309 entity, int seed) {
        return CbItemRender.getModel(cb$self(), stack, level, entity, seed);
    }

    /** was resolveItemModel(ItemStack, LivingEntity, ItemDisplayContext) */
    public net.minecraft.class_1087 method_62475(net.minecraft.class_1799 stack, net.minecraft.class_1309 entity, net.minecraft.class_811 context) {
        return CbItemRender.resolveItemModel(cb$self(), stack, entity, context);
    }

    /**
     * was onResourceManagerReload (method_14491): 1.21.3 cleared the item model shaper's cache of baked models;
     * 1.21.4's renderer resolves through the model manager on every draw and keeps no cache, so there is nothing to clear
     */
    public void method_14491(net.minecraft.class_3300 resourceManager) {
    }

    /** 1.21.3 renderStatic(entity, ..): getModel, then render -- for a subclass that overrides them */
    @Inject(method = "method_23177", at = @At("HEAD"), cancellable = true)
    private void cb$legacyRenderStatic(net.minecraft.class_1309 entity, net.minecraft.class_1799 stack, net.minecraft.class_811 context, boolean leftHand,
            net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, net.minecraft.class_1937 level, int light, int overlay, int seed, CallbackInfo ci) {
        if ((Object) this.getClass() == net.minecraft.class_918.class) {
            return;
        }
        if (!stack.method_7960()) {
            net.minecraft.class_1087 model = this.method_4019(stack, level, entity, seed);
            this.method_23179(stack, context, leftHand, pose, buffers, light, overlay, model);
        }
        ci.cancel();
    }
}
