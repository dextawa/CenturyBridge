package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbClientModels;

/**
 * BreezeEyesLayer as 1.20.4 had it. A layer built with 1.20.4's constructor (parent, modelSet, texture; the
 * reshape hands the last two to rt/CbClientModels.cbBreezeEyesBuilt) draws its own model, baked from
 * ModelLayers.BREEZE_EYES, with its own texture -- 1.20.4's render body. 1.20.5's layers (built with 1.20.5's
 * constructor) draw 1.20.5's way. getTextureLocation(breeze) is 1.20.4's protected getter.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_8988.class)
public abstract class CbClientBreezeEyesLayerMixin implements CbClientModels.LegacyBreezeLayer {

    @Unique
    private net.minecraft.class_8973<net.minecraft.class_8949> cb$model;

    @Unique
    private net.minecraft.class_2960 cb$texture;

    @Override
    public net.minecraft.class_8973<net.minecraft.class_8949> cb$legacyModel() {
        return this.cb$model;
    }

    @Override
    public net.minecraft.class_2960 cb$legacyTexture() {
        return this.cb$texture;
    }

    @Override
    public void cb$setLegacy(net.minecraft.class_8973<net.minecraft.class_8949> model, net.minecraft.class_2960 texture) {
        this.cb$model = model;
        this.cb$texture = texture;
    }

    /** 1.20.4 BreezeEyesLayer.getTextureLocation(breeze): the texture it was built with (1.20.5's layer: breeze_eyes.png, the one it draws) */
    protected net.minecraft.class_2960 method_55271(net.minecraft.class_8949 breeze) {
        return this.cb$texture != null ? this.cb$texture : new net.minecraft.class_2960("textures/entity/breeze/breeze_eyes.png");
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "method_55272", at = @At("HEAD"), cancellable = true)
    private void cb$legacyRender(net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, int light, net.minecraft.class_8949 breeze,
                                 float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw,
                                 float headPitch, CallbackInfo ci) {
        net.minecraft.class_8973<net.minecraft.class_8949> model = this.cb$model;
        if (model == null) {
            return;
        }
        ci.cancel();
        model.method_2816(breeze, limbSwing, limbSwingAmount, partialTick);
        ((net.minecraft.class_8973<net.minecraft.class_8949>) ((net.minecraft.class_3887<?, ?>) (Object) this).method_17165()).method_17081(model);
        net.minecraft.class_4588 buffer = buffers.getBuffer(net.minecraft.class_1921.method_55252(this.cb$texture));
        model.method_55232(breeze, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.method_32008().method_22699(pose, buffer, light, net.minecraft.class_4608.field_21444, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
