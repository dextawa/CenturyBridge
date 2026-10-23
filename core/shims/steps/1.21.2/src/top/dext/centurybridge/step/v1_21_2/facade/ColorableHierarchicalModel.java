package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's ColorableHierarchicalModel (net/minecraft/class_4594, classRenames), gone in 1.21.2: a HierarchicalModel
 * tinted by a colour of its own, multiplied into the one it is drawn with. 1.21.1's fields and bodies.
 */
@Environment(EnvType.CLIENT)
public abstract class ColorableHierarchicalModel<E extends net.minecraft.class_1297> extends HierarchicalModel<E> {

    /** color (-1: untinted) */
    public int field_52152;

    public ColorableHierarchicalModel() {
        super();
        this.field_52152 = -1;
    }

    /** setColor(color) */
    public void method_22956(int color) {
        this.field_52152 = color;
    }

    /** renderToBuffer: with the colour multiplied by this model's (FastColor.ARGB32.multiply) */
    @Override
    public void method_2828(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, int light, int overlay, int color) {
        super.method_2828(pose, consumer, light, overlay, FastColor.ARGB32.method_27763(color, this.field_52152));
    }
}
