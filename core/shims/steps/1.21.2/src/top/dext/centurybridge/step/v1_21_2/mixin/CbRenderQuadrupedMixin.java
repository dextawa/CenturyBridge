package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelFields;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderQuadruped;

/** 1.21.1 QuadrupedModel's baby values, headParts() and bodyParts() on 1.21.2's QuadrupedModel (see CbRenderQuadruped) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_597.class)
public abstract class CbRenderQuadrupedMixin implements CbRenderQuadruped.Legacy {

    @Shadow
    @Final
    protected net.minecraft.class_630 field_3535;

    @Shadow
    @Final
    protected net.minecraft.class_630 field_3538;

    @Shadow
    @Final
    protected net.minecraft.class_630 field_27476;

    @Shadow
    @Final
    protected net.minecraft.class_630 field_27477;

    @Shadow
    @Final
    protected net.minecraft.class_630 field_27478;

    @Shadow
    @Final
    protected net.minecraft.class_630 field_27479;

    /** scaleHead, babyYHeadOffset, babyZHeadOffset, babyHeadScale, babyBodyScale, bodyYOffset; null: not built by 1.21.1's constructor */
    @Unique
    private float[] cb$baby;

    /** 1.21.1 QuadrupedModel.headParts() */
    protected Iterable<net.minecraft.class_630> method_22946() {
        return com.google.common.collect.ImmutableList.of(this.field_3535);
    }

    /** 1.21.1 QuadrupedModel.bodyParts() */
    protected Iterable<net.minecraft.class_630> method_22948() {
        return com.google.common.collect.ImmutableList.of(this.field_3538, this.field_27476, this.field_27477, this.field_27478, this.field_27479);
    }

    @Override
    public void cb$baby(boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset, float babyHeadScale, float babyBodyScale, float bodyYOffset) {
        this.cb$baby = new float[] {scaleHead ? 1.0F : 0.0F, babyYHeadOffset, babyZHeadOffset, babyHeadScale, babyBodyScale, bodyYOffset};
    }

    @Override
    public boolean cb$drawLegacy(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, int light, int overlay, int color) {
        float[] b = this.cb$baby;
        if (b == null) {
            return false;
        }
        CbRenderQuadruped.draw(((CbRenderModelFields.Pose) this).cb$young(), b[0] != 0.0F, b[1], b[2], b[3], b[4], b[5], this.method_22946(),
            this.method_22948(), pose, consumer, light, overlay, color);
        return true;
    }
}
