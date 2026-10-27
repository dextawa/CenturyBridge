package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_20_5.rt.CbClientModels;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * BreezeEyesLayer as 1.20.4 had it. A layer built with 1.20.4's constructor (parent, modelSet, texture; the
 * reshape hands the last two to rt/CbClientModels.cbBreezeEyesBuilt) draws its own model, baked from
 * ModelLayers.BREEZE_EYES, with its own texture -- 1.20.4's render body (CbClientBreezeEyesLayerMixinHooks, default
 * priority). 1.20.5's layers (built with 1.20.5's constructor) draw 1.20.5's way. getTextureLocation(breeze) is
 * 1.20.4's protected getter.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_8988.class, priority = CbVanillaReads.PRIORITY)
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
}
