package top.dext.centurybridge.step.v1_21.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.6's VertexConsumer, as a 1.21 one. A 1.20.6 class implementing VertexConsumer implements this instead
 * (superRenames); 1.20.6's vertex(double..), endVertex(), vertex(matrix, ..) and vertex(pose, ..) that it declares
 * are renamed to the bodies below (cbLegacyVertex, cbLegacyEndVertex, cbLegacyVertexMatrix, cbLegacyVertexPose:
 * overrideRenames), so every call -- 1.20.6's and 1.21's -- passes through the entries here first. Its other
 * 1.20.6 methods keep their names: overlayCoords(int, int), uv2(int), normal(pose, ..), the whole vertex of
 * fourteen arguments, defaultColor / unsetDefaultColor; color, uv, uv2(int, int), normal(float..) are 1.21's of
 * the same name and shape.
 *
 * 1.20.6's contract had the caller end each vertex (endVertex()); 1.21's callers never do -- a vertex ends where
 * the next begins. A vertex 1.21 code begins on a 1.20.6 consumer (addVertex, in any of its shapes) is marked
 * open once begun, and ended -- the consumer's own endVertex() -- when the next one begins on it, when a mesh is
 * built (the vertices are complete by then), or when 1.20.6 code begins a vertex or a whole vertex on it; a
 * 1.20.6 endVertex() on it ends it there instead (CbRenderVertex). A vertex 1.20.6 code begins is its own to
 * end, as it always was. 1.21's whole vertex and putBulkData run 1.20.6's own bodies, which end each vertex
 * themselves. Inside a 1.20.6 body the class's own calls go to the renamed bodies or through the entries, which
 * see no open vertex of 1.21's then: the one 1.21 began before has been ended, the new one is marked after.
 */
@Environment(EnvType.CLIENT)
public interface CbRenderLegacyConsumer extends net.minecraft.class_4588 {

    // ------------------------------------------------------------------ 1.20.6's abstract shape

    /** vertex(x, y, z): the class's own, renamed */
    net.minecraft.class_4588 cbLegacyVertex(double x, double y, double z);

    /** overlayCoords(u, v) */
    net.minecraft.class_4588 method_22917(int u, int v);

    /** endVertex(): the class's own, renamed */
    void cbLegacyEndVertex();

    /** defaultColor(r, g, b, a) */
    void method_22901(int r, int g, int b, int a);

    /** unsetDefaultColor() */
    void method_35666();

    // ------------------------------------------------------------------ 1.20.6's entries

    /** vertex(x, y, z): a vertex 1.21 code left open ends before 1.20.6 code begins one */
    default net.minecraft.class_4588 method_22912(double x, double y, double z) {
        CbRenderVertex.cbEndPendingVertex(this);
        return this.cbLegacyVertex(x, y, z);
    }

    /** endVertex(): whoever began the vertex, it ends here once */
    default void method_1344() {
        CbRenderVertex.cbVertexEnded(this);
        this.cbLegacyEndVertex();
    }

    // ------------------------------------------------------------------ 1.20.6's default bodies

    /** vertex(matrix, x, y, z) of 1.20.6: the transformed position, then vertex(..) */
    default net.minecraft.class_4588 cbLegacyVertexMatrix(org.joml.Matrix4f matrix, float x, float y, float z) {
        org.joml.Vector3f p = matrix.transformPosition(x, y, z, new org.joml.Vector3f());
        return this.cbLegacyVertex(p.x(), p.y(), p.z());
    }

    /** vertex(pose, x, y, z) of 1.20.6: vertex(pose.pose(), x, y, z) */
    default net.minecraft.class_4588 cbLegacyVertexPose(net.minecraft.class_4587.class_4665 pose, float x, float y, float z) {
        return this.cbLegacyVertexMatrix(pose.method_23761(), x, y, z);
    }

    /** uv2(packed) of 1.20.6 */
    default net.minecraft.class_4588 method_22916(int packed) {
        return this.method_22921(packed & 0xFFFF, packed >> 16 & 0xFFFF);
    }

    /** normal(pose, x, y, z) of 1.20.6: the normal through the pose's normal matrix */
    default net.minecraft.class_4588 method_23763(net.minecraft.class_4587.class_4665 pose, float x, float y, float z) {
        org.joml.Vector3f n = pose.method_56820(x, y, z, new org.joml.Vector3f());
        return this.method_22914(n.x(), n.y(), n.z());
    }

    /** vertex(x, y, z, r, g, b, a, u, v, overlay, light, nx, ny, nz) of 1.20.6: every element, then endVertex() */
    default void method_23919(float x, float y, float z, float r, float g, float b, float a, float u, float v, int overlay,
                              int light, float nx, float ny, float nz) {
        CbRenderVertex.cbEndPendingVertex(this);
        this.cbLegacyVertex(x, y, z);
        this.method_22915(r, g, b, a);
        this.method_22913(u, v);
        this.method_22922(overlay);
        this.method_22916(light);
        this.method_22914(nx, ny, nz);
        this.cbLegacyEndVertex();
    }

    // ------------------------------------------------------------------ 1.21's calls, answered by 1.20.6's

    /**
     * addVertex(x, y, z): a vertex 1.21 code begins -- the one it began before ends first; this one is marked
     * open once its position is in (a 1.20.6 body may itself call vertex(..) on the way)
     */
    @Override
    default net.minecraft.class_4588 method_22912(float x, float y, float z) {
        CbRenderVertex.cbEndPendingVertex(this);
        net.minecraft.class_4588 next = this.cbLegacyVertex(x, y, z);
        CbRenderVertex.cbMarkPending(this);
        return next;
    }

    /** addVertex(matrix, x, y, z): 1.20.6's vertex(matrix, ..), the class's own if it has one; marked open as addVertex */
    @Override
    default net.minecraft.class_4588 method_22918(org.joml.Matrix4f matrix, float x, float y, float z) {
        CbRenderVertex.cbEndPendingVertex(this);
        net.minecraft.class_4588 next = this.cbLegacyVertexMatrix(matrix, x, y, z);
        CbRenderVertex.cbMarkPending(this);
        return next;
    }

    /** addVertex(pose, x, y, z): 1.20.6's vertex(pose, ..), the class's own if it has one; marked open as addVertex */
    @Override
    default net.minecraft.class_4588 method_56824(net.minecraft.class_4587.class_4665 pose, float x, float y, float z) {
        CbRenderVertex.cbEndPendingVertex(this);
        net.minecraft.class_4588 next = this.cbLegacyVertexPose(pose, x, y, z);
        CbRenderVertex.cbMarkPending(this);
        return next;
    }

    /** setUv1(u, v) is 1.20.6's overlayCoords(u, v) */
    @Override
    default net.minecraft.class_4588 method_60796(int u, int v) {
        return this.method_22917(u, v);
    }

    /** setLight(packed) is 1.20.6's uv2(packed) */
    @Override
    default net.minecraft.class_4588 method_60803(int packed) {
        return this.method_22916(packed);
    }

    /** setNormal(pose, x, y, z) is 1.20.6's normal(pose, x, y, z) */
    @Override
    default net.minecraft.class_4588 method_60831(net.minecraft.class_4587.class_4665 pose, float x, float y, float z) {
        return this.method_23763(pose, x, y, z);
    }

    /** 1.21's whole vertex: 1.20.6's, the packed color's channels as 1.20.6's floats */
    @Override
    default void method_23919(float x, float y, float z, int color, float u, float v, int overlay, int light,
                              float nx, float ny, float nz) {
        CbRenderVertex.cbEndPendingVertex(this);
        this.method_23919(x, y, z, CbClientModels.cbRed(color), CbClientModels.cbGreen(color), CbClientModels.cbBlue(color),
            CbClientModels.cbAlpha(color), u, v, overlay, light, nx, ny, nz);
    }

    /** putBulkData(pose, quad, r, g, b, a, light, overlay) of 1.20.6: full brightness, one light */
    @Override
    default void method_22919(net.minecraft.class_4587.class_4665 pose, net.minecraft.class_777 quad, float r, float g, float b,
                              float a, int light, int overlay) {
        this.method_22920(pose, quad, new float[] {1.0F, 1.0F, 1.0F, 1.0F}, r, g, b, a, new int[] {light, light, light, light},
            overlay, false);
    }

    /**
     * putBulkData(pose, quad, brightness, r, g, b, a, lights, overlay, readExistingColor) of 1.20.6: each of the
     * quad's vertices as one whole 1.20.6 vertex, its color the float product 1.20.6 computed.
     */
    @Override
    default void method_22920(net.minecraft.class_4587.class_4665 pose, net.minecraft.class_777 quad, float[] brightness, float r,
                              float g, float b, float a, int[] lights, int overlay, boolean readExistingColor) {
        CbRenderVertex.cbEndPendingVertex(this);
        CbRenderVertex.cbLegacyBulk(this, pose, quad, brightness, r, g, b, a, lights, overlay, readExistingColor);
    }
}
