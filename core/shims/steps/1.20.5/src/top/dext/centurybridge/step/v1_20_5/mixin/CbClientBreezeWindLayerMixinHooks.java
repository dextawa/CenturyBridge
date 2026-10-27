package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbClientModels;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplitClient;

/**
 * The injector into 1.20.5's own BreezeWindLayer.render that goes with {@link CbClientBreezeWindLayerMixin} (its
 * restored 1.20.4 members stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.4 injectors can anchor inside
 * them): a layer built with 1.20.4's constructor draws its own model in 1.20.4's three passes.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_8989.class)
public abstract class CbClientBreezeWindLayerMixinHooks {

    @SuppressWarnings("unchecked")
    @Inject(method = "method_55275", at = @At("HEAD"), cancellable = true)
    private void cb$legacyRender(net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, int light, net.minecraft.class_8949 breeze,
                                 float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw,
                                 float headPitch, CallbackInfo ci) {
        net.minecraft.class_8973<net.minecraft.class_8949> model = ((CbClientModels.LegacyBreezeLayer) (Object) this).cb$legacyModel();
        if (model == null) {
            return;
        }
        ci.cancel();
        float t = (float) breeze.field_6012 + partialTick;
        model.method_2816(breeze, limbSwing, limbSwingAmount, partialTick);
        ((net.minecraft.class_8973<net.minecraft.class_8949>) ((net.minecraft.class_3887<?, ?>) (Object) this).method_17165()).method_17081(model);
        net.minecraft.class_4588 buffer = buffers.getBuffer(net.minecraft.class_1921.method_55251(
            ((CbPrioritySplitClient.BreezeWindTexture) (Object) this).cb$v1_20_5$textureLocation(breeze), t * 0.02F % 1.0F, 0.0F));
        model.method_55232(breeze, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        // the model's windBottom / windMiddle / windTop parts (the fields BreezeModel's constructor took from these children)
        net.minecraft.class_630 root = model.method_32008();
        net.minecraft.class_630 bottom = root.method_32086("wind_body").method_32086("wind_bottom");
        net.minecraft.class_630 mid = bottom.method_32086("wind_mid");
        net.minecraft.class_630 top = mid.method_32086("wind_top");
        cb$pass(root, top, mid, bottom, pose, buffer, light, true, true, false);
        cb$pass(root, top, mid, bottom, pose, buffer, light, true, false, true);
        cb$pass(root, top, mid, bottom, pose, buffer, light, false, true, true);
    }

    /** one of 1.20.4's passes: the rings' skipDraw set as given, then the whole model */
    @Unique
    private static void cb$pass(net.minecraft.class_630 root, net.minecraft.class_630 top, net.minecraft.class_630 mid, net.minecraft.class_630 bottom,
                                net.minecraft.class_4587 pose, net.minecraft.class_4588 buffer, int light,
                                boolean skipTop, boolean skipMid, boolean skipBottom) {
        top.field_38456 = skipTop;
        mid.field_38456 = skipMid;
        bottom.field_38456 = skipBottom;
        root.method_22699(pose, buffer, light, net.minecraft.class_4608.field_21444, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
