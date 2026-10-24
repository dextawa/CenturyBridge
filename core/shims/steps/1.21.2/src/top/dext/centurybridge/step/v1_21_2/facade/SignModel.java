package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's SignRenderer.SignModel (net/minecraft/class_837$class_4702, classRenames): a sign model over a root with the
 * board and the stick (1.21.1 hid the stick for a wall sign). 1.21.2 has a plain Model per standing / wall sign layer;
 * the 1.21.1 layer is 1.21.2's standing one (the same "sign" and "stick" boxes: compared in both jars), so this model is
 * baked from it (rt/CbRenderSigns). Its parts are 1.21.1's public fields; it draws its root as 1.21.1's did.
 */
@Environment(EnvType.CLIENT)
public final class SignModel extends net.minecraft.class_3879 {

    /** root */
    public final net.minecraft.class_630 field_27756;
    /** stick */
    public final net.minecraft.class_630 field_21531;

    public SignModel(net.minecraft.class_630 root) {
        super(root, net.minecraft.class_1921::method_23578);
        this.field_27756 = root;
        this.field_21531 = root.method_32086("stick");
    }

    /** renderToBuffer(poseStack, buffer, light, overlay, color): the root */
    public void method_2828(net.minecraft.class_4587 poseStack, net.minecraft.class_4588 buffer, int light, int overlay, int color) {
        this.field_27756.method_22699(poseStack, buffer, light, overlay, color);
    }
}
