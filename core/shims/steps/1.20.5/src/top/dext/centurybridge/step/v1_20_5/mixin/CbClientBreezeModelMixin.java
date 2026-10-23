package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * BreezeModel.windTop() / windMiddle() / windBottom(): 1.20.4's getters of the three wind rings, which 1.20.5
 * still keeps as fields (wind_top, wind_mid, wind_bottom) without getters. The same parts.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_8973.class)
public abstract class CbClientBreezeModelMixin {

    @Shadow
    @Final
    private net.minecraft.class_630 field_47437;

    @Shadow
    @Final
    private net.minecraft.class_630 field_47438;

    @Shadow
    @Final
    private net.minecraft.class_630 field_47439;

    /** 1.20.4 BreezeModel.windTop() */
    public net.minecraft.class_630 method_55236() {
        return this.field_47437;
    }

    /** 1.20.4 BreezeModel.windMiddle() */
    public net.minecraft.class_630 method_55237() {
        return this.field_47438;
    }

    /** 1.20.4 BreezeModel.windBottom() */
    public net.minecraft.class_630 method_55238() {
        return this.field_47439;
    }
}
