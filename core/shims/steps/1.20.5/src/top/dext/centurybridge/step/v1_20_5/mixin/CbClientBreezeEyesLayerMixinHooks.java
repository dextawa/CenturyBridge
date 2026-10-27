package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbClientModels;

/**
 * The injector into 1.20.5's own BreezeEyesLayer.render that goes with {@link CbClientBreezeEyesLayerMixin} (its
 * restored 1.20.4 members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.4 injectors can anchor inside
 * them): a layer built with 1.20.4's constructor draws its own model with its own texture, 1.20.4's render body.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_8988.class)
public abstract class CbClientBreezeEyesLayerMixinHooks {

    @SuppressWarnings("unchecked")
    @Inject(method = "method_55272", at = @At("HEAD"), cancellable = true)
    private void cb$legacyRender(net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, int light, net.minecraft.class_8949 breeze,
                                 float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw,
                                 float headPitch, CallbackInfo ci) {
        CbClientModels.LegacyBreezeLayer legacy = (CbClientModels.LegacyBreezeLayer) (Object) this;
        net.minecraft.class_8973<net.minecraft.class_8949> model = legacy.cb$legacyModel();
        if (model == null) {
            return;
        }
        ci.cancel();
        model.method_2816(breeze, limbSwing, limbSwingAmount, partialTick);
        ((net.minecraft.class_8973<net.minecraft.class_8949>) ((net.minecraft.class_3887<?, ?>) (Object) this).method_17165()).method_17081(model);
        net.minecraft.class_4588 buffer = buffers.getBuffer(net.minecraft.class_1921.method_55252(legacy.cb$legacyTexture()));
        model.method_55232(breeze, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.method_32008().method_22699(pose, buffer, light, net.minecraft.class_4608.field_21444, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
