package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21.rt.CbDragonRays;
import top.dext.centurybridge.step.v1_21.rt.CbRenderVertex;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * 1.20.6's EnderDragonRenderer death-ray vertex helpers, restored with 1.20.6's bodies (javap -c -p): vertex(matrix, x, y,
 * z) (cb$v1_21$vertex), color(r, g, b, a), endVertex() (CbRenderVertex.cbEndVertex, the 1.21 layer's endVertex). 1.21 inlined them into
 * method_61158 (CbDragonRays). Route: CbRenderDragonRaysRouteMixin, when a converted mod mixes one of them; handed over
 * through CbDragonRays from the static initializer this mixin merges into EnderDragonRenderer (the helpers stay private).
 */
@Mixin(value = net.minecraft.class_895.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderDragonRaysRestoredMixin {

    /** the hand-over to CbRenderDragonRaysRouteMixin, run by EnderDragonRenderer's static init (merged), not an injector */
    @Unique
    private static final boolean cb$v1_21$published = CbDragonRays.publish(CbRenderDragonRaysRestoredMixin::method_23156,
        CbRenderDragonRaysRestoredMixin::method_23157, CbRenderDragonRaysRestoredMixin::method_23158,
        CbRenderDragonRaysRestoredMixin::method_23159);

    /** 1.20.6's HALF_SQRT_3 (field_21007), (float) (Math.sqrt(3.0) / 2.0) */
    @Unique
    private static final float cb$v1_21$halfSqrt3 = (float) (Math.sqrt(3.0) / 2.0);

    /**
     * 1.20.6's vertex(matrix, x, y, z), as 1.21's default VertexConsumer.addVertex(Matrix4f, x, y, z) runs it (javap): the
     * position transformed by the matrix, then addVertex(x, y, z). Spelled out because 26.1 changed that default to take
     * a Matrix4fc, which the climbed layer would reach only through a tombstone.
     */
    @Unique
    private static net.minecraft.class_4588 cb$v1_21$vertex(net.minecraft.class_4588 consumer, org.joml.Matrix4f matrix, float x, float y, float z) {
        org.joml.Vector3f v = matrix.transformPosition(x, y, z, new org.joml.Vector3f());
        return consumer.method_22912(v.x(), v.y(), v.z());
    }

    /** vertex01: the ray's centre */
    @CbVanillaReads("CbRenderDragonRaysRouteMixin: 1.21's method_61158 draws each ray centre through it when a converted mod mixes a ray "
        + "helper (CbMixedMembers); body 1.20.6's")
    private static void method_23157(net.minecraft.class_4588 consumer, org.joml.Matrix4f matrix, int alpha) {
        cb$v1_21$vertex(consumer, matrix, 0.0F, 0.0F, 0.0F).method_1336(255, 255, 255, alpha);
        CbRenderVertex.cbEndVertex(consumer);
    }

    /** vertex2: corner A */
    @CbVanillaReads("CbRenderDragonRaysRouteMixin: 1.21's method_61158 draws each ray's corner A through it when a converted mod mixes a "
        + "ray helper (CbMixedMembers); body 1.20.6's")
    private static void method_23156(net.minecraft.class_4588 consumer, org.joml.Matrix4f matrix, float h, float w) {
        cb$v1_21$vertex(consumer, matrix, -cb$v1_21$halfSqrt3 * w, h, -0.5F * w).method_1336(255, 0, 255, 0);
        CbRenderVertex.cbEndVertex(consumer);
    }

    /** vertex3: corner B */
    @CbVanillaReads("CbRenderDragonRaysRouteMixin: 1.21's method_61158 draws each ray's corner B through it when a converted mod mixes a "
        + "ray helper (CbMixedMembers); body 1.20.6's")
    private static void method_23158(net.minecraft.class_4588 consumer, org.joml.Matrix4f matrix, float h, float w) {
        cb$v1_21$vertex(consumer, matrix, cb$v1_21$halfSqrt3 * w, h, -0.5F * w).method_1336(255, 0, 255, 0);
        CbRenderVertex.cbEndVertex(consumer);
    }

    /** vertex4: corner C */
    @CbVanillaReads("CbRenderDragonRaysRouteMixin: 1.21's method_61158 draws each ray's corner C through it when a converted mod mixes a "
        + "ray helper (CbMixedMembers); body 1.20.6's")
    private static void method_23159(net.minecraft.class_4588 consumer, org.joml.Matrix4f matrix, float h, float w) {
        cb$v1_21$vertex(consumer, matrix, 0.0F, h, 1.0F * w).method_1336(255, 0, 255, 0);
        CbRenderVertex.cbEndVertex(consumer);
    }
}
