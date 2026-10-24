package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's ChestBoatModel (net/minecraft/class_7752, classRenames): 1.21.2's BoatModel baked from a chest boat layer
 * (its createPartsBuilder adds the chest parts, CbRenderBoatModelMixin); createBodyModel is 1.21.2's chest boat layer.
 */
@Environment(EnvType.CLIENT)
public class ChestBoatModel extends net.minecraft.class_554 {

    public ChestBoatModel(net.minecraft.class_630 root) {
        super(root);
    }

    /** createPartsBuilder(root): the parts with the chest ones */
    public com.google.common.collect.ImmutableList.Builder<net.minecraft.class_630> method_45702(net.minecraft.class_630 root) {
        return top.dext.centurybridge.step.v1_21_2.rt.CbRenderBoats.boatParts(root);
    }

    /** createBodyModel() */
    public static net.minecraft.class_5607 method_45708() {
        return net.minecraft.class_554.method_62066();
    }
}
