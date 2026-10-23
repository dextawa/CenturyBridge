package top.dext.centurybridge.step.v1_21.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.6's SheetedDecalTextureGenerator, for a 1.20.6 subclass of it (superRenames): a 1.20.6 consumer
 * (CbRenderLegacyConsumer) that keeps the vertex it is given -- position, overlay coordinates, light, normal; color
 * and UV it ignores -- and on endVertex() writes it to the delegate whole, white, its UV the decal coordinate of the
 * position seen from the pose (camera and normal inverted at construction, as 1.20.6's), then starts afresh (no
 * overlay offset but v = 10, full-bright light, normal up). 1.21's decal writes on as it goes and computes the UV at
 * setNormal; a 1.20.6 subclass gets 1.20.6's, and 1.21's calls on it (addVertex, setColor, setUv1, setUv2, setNormal,
 * the whole vertex) reach the 1.20.6 methods (the subclass's own if it overrides them). Its default color is
 * 1.20.6's DefaultedVertexConsumer's: kept, unused.
 */
@Environment(EnvType.CLIENT)
public class CbRenderLegacyDecal extends net.minecraft.class_4583 implements CbRenderLegacyConsumer {

    private final net.minecraft.class_4588 cbDelegate;

    private final org.joml.Matrix4f cbCameraInversePose;

    private final org.joml.Matrix3f cbNormalInversePose;

    private final float cbTextureScale;

    private float cbX;

    private float cbY;

    private float cbZ;

    private int cbOverlayU;

    private int cbOverlayV;

    private int cbLightCoords;

    private float cbNx;

    private float cbNy;

    private float cbNz;

    /** DefaultedVertexConsumer's default color, which the decal kept and never used */
    public boolean cbDefaultColorSet;

    public CbRenderLegacyDecal(net.minecraft.class_4588 delegate, net.minecraft.class_4587.class_4665 pose, float textureScale) {
        super(delegate, pose, textureScale);
        this.cbDelegate = delegate;
        this.cbCameraInversePose = new org.joml.Matrix4f(pose.method_23761()).invert();
        this.cbNormalInversePose = new org.joml.Matrix3f(pose.method_23762()).invert();
        this.cbTextureScale = textureScale;
        this.cbResetState();
    }

    private void cbResetState() {
        this.cbX = 0.0F;
        this.cbY = 0.0F;
        this.cbZ = 0.0F;
        this.cbOverlayU = 0;
        this.cbOverlayV = 10;
        this.cbLightCoords = 15728880;
        this.cbNx = 0.0F;
        this.cbNy = 1.0F;
        this.cbNz = 0.0F;
    }

    // ------------------------------------------------------------------ 1.21's calls to the 1.20.6 methods

    @Override
    public net.minecraft.class_4588 method_22912(float x, float y, float z) {
        return CbRenderLegacyConsumer.super.method_22912(x, y, z);
    }

    @Override
    public net.minecraft.class_4588 method_60796(int u, int v) {
        return CbRenderLegacyConsumer.super.method_60796(u, v);
    }

    @Override
    public void method_23919(float x, float y, float z, int color, float u, float v, int overlay, int light, float nx, float ny,
                             float nz) {
        CbRenderLegacyConsumer.super.method_23919(x, y, z, color, u, v, overlay, light, nx, ny, nz);
    }

    // ------------------------------------------------------------------ 1.20.6's decal

    /** vertex(x, y, z): kept */
    @Override
    public net.minecraft.class_4588 cbLegacyVertex(double x, double y, double z) {
        this.cbX = (float) x;
        this.cbY = (float) y;
        this.cbZ = (float) z;
        return this;
    }

    /** color(..): ignored (the decal is white) */
    @Override
    public net.minecraft.class_4588 method_1336(int r, int g, int b, int a) {
        return this;
    }

    /** uv(..): ignored (the decal computes its own) */
    @Override
    public net.minecraft.class_4588 method_22913(float u, float v) {
        return this;
    }

    /** overlayCoords(u, v): kept */
    @Override
    public net.minecraft.class_4588 method_22917(int u, int v) {
        this.cbOverlayU = u;
        this.cbOverlayV = v;
        return this;
    }

    /** uv2(u, v): kept, packed */
    @Override
    public net.minecraft.class_4588 method_22921(int u, int v) {
        this.cbLightCoords = u | v << 16;
        return this;
    }

    /** normal(x, y, z): kept */
    @Override
    public net.minecraft.class_4588 method_22914(float x, float y, float z) {
        this.cbNx = x;
        this.cbNy = y;
        this.cbNz = z;
        return this;
    }

    /** endVertex() of 1.20.6: the kept vertex, white, at its decal UV, written to the delegate; then afresh */
    @Override
    public void cbLegacyEndVertex() {
        org.joml.Vector3f n = this.cbNormalInversePose.transform(new org.joml.Vector3f(this.cbNx, this.cbNy, this.cbNz));
        net.minecraft.class_2350 facing = net.minecraft.class_2350.method_10147(n.x(), n.y(), n.z());
        org.joml.Vector4f p = this.cbCameraInversePose.transform(new org.joml.Vector4f(this.cbX, this.cbY, this.cbZ, 1.0F));
        p.rotateY((float) Math.PI);
        p.rotateX((float) (-Math.PI / 2));
        p.rotate(facing.method_23224());
        float u = -p.x() * this.cbTextureScale;
        float v = -p.y() * this.cbTextureScale;
        net.minecraft.class_4588 c = CbRenderVertex.cbVertex(this.cbDelegate, this.cbX, this.cbY, this.cbZ);
        c = c.method_22915(1.0F, 1.0F, 1.0F, 1.0F);
        c = c.method_22913(u, v);
        c = CbRenderVertex.cbOverlayCoords(c, this.cbOverlayU, this.cbOverlayV);
        c = CbRenderVertex.cbUv2(c, this.cbLightCoords);
        c = c.method_22914(this.cbNx, this.cbNy, this.cbNz);
        CbRenderVertex.cbEndVertex(c);
        this.cbResetState();
    }

    /** defaultColor(..) of 1.20.6's DefaultedVertexConsumer: kept, unused by the decal */
    @Override
    public void method_22901(int r, int g, int b, int a) {
        this.cbDefaultColorSet = true;
    }

    /** unsetDefaultColor() */
    @Override
    public void method_35666() {
        this.cbDefaultColorSet = false;
    }
}
