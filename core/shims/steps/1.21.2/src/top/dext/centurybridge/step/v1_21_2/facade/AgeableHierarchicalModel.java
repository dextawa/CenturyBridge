package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's AgeableHierarchicalModel (net/minecraft/class_8184, classRenames), gone in 1.21.2: a HierarchicalModel
 * that draws a young entity scaled. 1.21.1's fields and bodies.
 */
@Environment(EnvType.CLIENT)
public abstract class AgeableHierarchicalModel<E extends net.minecraft.class_1297> extends HierarchicalModel<E> {

    /** youngScaleFactor */
    public final float field_42875;

    /** bodyYOffset */
    public final float field_42876;

    public AgeableHierarchicalModel(float youngScaleFactor, float bodyYOffset) {
        this(youngScaleFactor, bodyYOffset, net.minecraft.class_1921::method_23578);
    }

    public AgeableHierarchicalModel(float youngScaleFactor, float bodyYOffset, Function<net.minecraft.class_2960, net.minecraft.class_1921> renderType) {
        super(renderType);
        this.field_42876 = bodyYOffset;
        this.field_42875 = youngScaleFactor;
    }

    /** renderToBuffer: young -> scaled by youngScaleFactor and lowered by bodyYOffset / 16 */
    @Override
    public void method_2828(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, int light, int overlay, int color) {
        if (((top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelFields.Pose) this).cb$young()) {
            pose.method_22903();
            pose.method_22905(this.field_42875, this.field_42875, this.field_42875);
            pose.method_46416(0.0F, this.field_42876 / 16.0F, 0.0F);
            this.method_32008().method_22699(pose, consumer, light, overlay, color);
            pose.method_22909();
        } else {
            this.method_32008().method_22699(pose, consumer, light, overlay, color);
        }
    }
}
