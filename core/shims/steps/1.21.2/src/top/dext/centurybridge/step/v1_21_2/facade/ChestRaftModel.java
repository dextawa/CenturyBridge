package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's ChestRaftModel (net/minecraft/class_7753, classRenames): 1.21.2's RaftModel baked from a chest raft layer
 * (its createPartsBuilder adds the chest parts, CbRenderRaftModelMixin); createBodyModel is 1.21.2's chest raft layer.
 */
@Environment(EnvType.CLIENT)
public class ChestRaftModel extends net.minecraft.class_7754 {

    public ChestRaftModel(net.minecraft.class_630 root) {
        super(root);
    }

    /** createPartsBuilder(root): the parts with the chest ones */
    public com.google.common.collect.ImmutableList.Builder<net.minecraft.class_630> method_45710(net.minecraft.class_630 root) {
        return top.dext.centurybridge.step.v1_21_2.rt.CbRenderBoats.raftParts(root);
    }

    /** createBodyModel() */
    public static net.minecraft.class_5607 method_45709() {
        return net.minecraft.class_7754.method_62112();
    }
}
