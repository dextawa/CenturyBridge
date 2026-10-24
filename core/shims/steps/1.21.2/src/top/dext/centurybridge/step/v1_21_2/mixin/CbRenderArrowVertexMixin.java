package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.21.1 ArrowRenderer.vertex(pose, consumer, x, y, z, u, v, normalX, normalZ, normalY, light), gone in 1.21.2 (the
 * arrow is an ArrowModel drawn whole), put back with 1.21.1's body: one white, unoverlaid vertex at (x, y, z).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_876.class)
public abstract class CbRenderArrowVertexMixin {

    /** 1.21.1 ArrowRenderer.vertex */
    public void method_23153(net.minecraft.class_4587.class_4665 pose, net.minecraft.class_4588 consumer, int x, int y, int z, float u, float v,
                             int normalX, int normalZ, int normalY, int light) {
        consumer.method_56824(pose, (float) x, (float) y, (float) z)
            .method_39415(-1)
            .method_22913(u, v)
            .method_22922(net.minecraft.class_4608.field_21444)
            .method_60803(light)
            .method_60831(pose, (float) normalX, (float) normalY, (float) normalZ);
    }
}
