package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_20_5.rt.CbClientModels;
import top.dext.centurybridge.step.v1_20_5.rt.CbPrioritySplitClient;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * BreezeWindLayer as 1.20.4 had it. A layer built with 1.20.4's constructor (parent, modelSet, texture;
 * rt/CbClientModels.cbBreezeWindBuilt) draws its own model, baked from ModelLayers.BREEZE_WIND, with the
 * texture getTextureLocation(breeze) answers, scrolling by (tickCount + partialTick) * 0.02 -- 1.20.4's three
 * passes, each skipping one ring (CbClientBreezeWindLayerMixinHooks, default priority). 1.20.5's layers draw
 * 1.20.5's way.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_8989.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbClientBreezeWindLayerMixin implements CbClientModels.LegacyBreezeLayer, CbPrioritySplitClient.BreezeWindTexture {

    @Shadow
    @Final
    private static net.minecraft.class_2960 field_47484;

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

    /** 1.20.4 BreezeWindLayer.getTextureLocation(breeze): the texture it was built with (1.20.5's layer: its breeze_wind.png) */
    protected net.minecraft.class_2960 method_55274(net.minecraft.class_8949 breeze) {
        return this.cb$texture != null ? this.cb$texture : field_47484;
    }

    /** 1.20.4's getTextureLocation called virtually, for 1.20.4's render body (CbClientBreezeWindLayerMixinHooks) */
    @Override
    public net.minecraft.class_2960 cb$v1_20_5$textureLocation(net.minecraft.class_8949 breeze) {
        return this.method_55274(breeze);
    }
}
