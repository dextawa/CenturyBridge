package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * BeaconRenderer.renderPart: the beam geometry 1.20.6's renderBeaconBeam drew with its own colors (CbRenderLevel).
 */
@Mixin(net.minecraft.class_822.class)
public interface CbRenderBeaconAccess {

    @Invoker("method_22741")
    static void cb$renderPart(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, int color, int minY, int maxY, float x1, float z1, float x2, float z2, float x3, float z3, float x4, float z4, float minU, float maxU, float minV, float maxV) {
        throw new AssertionError();
    }
}
