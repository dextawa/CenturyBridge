package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelBase;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelFields;

/**
 * 1.21.1's AgeableListModel (net/minecraft/class_4592, classRenames), gone in 1.21.2 (a baby is a separate model
 * there): a model drawing its head parts and body parts, a young entity's scaled. 1.21.1's fields and bodies, on
 * 1.21.2's EntityModel with an empty root (CbRenderModelBase: this model draws itself).
 */
@Environment(EnvType.CLIENT)
public abstract class AgeableListModel<E extends net.minecraft.class_1297> extends net.minecraft.class_583<net.minecraft.class_10017> {

    /** scaleHead */
    public final boolean field_20915;

    /** babyYHeadOffset */
    public final float field_20916;

    /** babyZHeadOffset */
    public final float field_20917;

    /** babyHeadScale */
    public final float field_20918;

    /** babyBodyScale */
    public final float field_20919;

    /** bodyYOffset */
    public final float field_20920;

    public AgeableListModel(boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset) {
        this(scaleHead, babyYHeadOffset, babyZHeadOffset, 2.0F, 2.0F, 24.0F);
    }

    public AgeableListModel(boolean scaleHead, float babyYHeadOffset, float babyZHeadOffset, float babyHeadScale, float babyBodyScale,
                               float bodyYOffset) {
        this(net.minecraft.class_1921::method_23578, scaleHead, babyYHeadOffset, babyZHeadOffset, babyHeadScale, babyBodyScale, bodyYOffset);
    }

    public AgeableListModel(Function<net.minecraft.class_2960, net.minecraft.class_1921> renderType, boolean scaleHead, float babyYHeadOffset,
                               float babyZHeadOffset, float babyHeadScale, float babyBodyScale, float bodyYOffset) {
        super(CbRenderModelBase.emptyRoot(), renderType);
        this.field_20915 = scaleHead;
        this.field_20916 = babyYHeadOffset;
        this.field_20917 = babyZHeadOffset;
        this.field_20918 = babyHeadScale;
        this.field_20919 = babyBodyScale;
        this.field_20920 = bodyYOffset;
    }

    public AgeableListModel() {
        this(false, 5.0F, 2.0F);
    }

    /** renderToBuffer: young -> head parts (scaled 1.5 / babyHeadScale if scaleHead) and body parts (1 / babyBodyScale), offset */
    public void method_2828(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, int light, int overlay, int color) {
        if (((CbRenderModelFields.Pose) this).cb$young()) {
            pose.method_22903();
            if (this.field_20915) {
                float s = 1.5F / this.field_20918;
                pose.method_22905(s, s, s);
            }
            pose.method_46416(0.0F, this.field_20916 / 16.0F, this.field_20917 / 16.0F);
            this.method_22946().forEach(part -> part.method_22699(pose, consumer, light, overlay, color));
            pose.method_22909();
            pose.method_22903();
            float s = 1.0F / this.field_20919;
            pose.method_22905(s, s, s);
            pose.method_46416(0.0F, this.field_20920 / 16.0F, 0.0F);
            this.method_22948().forEach(part -> part.method_22699(pose, consumer, light, overlay, color));
            pose.method_22909();
        } else {
            this.method_22946().forEach(part -> part.method_22699(pose, consumer, light, overlay, color));
            this.method_22948().forEach(part -> part.method_22699(pose, consumer, light, overlay, color));
        }
    }

    /** headParts() */
    public abstract Iterable<net.minecraft.class_630> method_22946();

    /** bodyParts() */
    public abstract Iterable<net.minecraft.class_630> method_22948();
}
