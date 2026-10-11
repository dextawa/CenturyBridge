package top.dext.centurybridge.facade.v26_2;

/**
 * The PoseStack a 1.20.x mod gets from DrawContext.getMatrices(): every
 * transform is applied to the real stack (so last() stays meaningful for code
 * that reads it back) and mirrored onto the extractor's 2D Matrix3x2fStack,
 * which is what actually positions retained-mode GUI elements in 26.2.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbPoseStack extends com.mojang.blaze3d.vertex.PoseStack {

    private final org.joml.Matrix3x2fStack mirror;

    CbPoseStack(org.joml.Matrix3x2fStack mirror) {
        this.mirror = mirror;
    }

    @Override
    public void pushPose() {
        super.pushPose();
        mirror.pushMatrix();
    }

    @Override
    public void popPose() {
        super.popPose();
        mirror.popMatrix();
    }

    @Override
    public void translate(float x, float y, float z) {
        super.translate(x, y, z);
        mirror.translate(x, y);
    }

    @Override
    public void translate(double x, double y, double z) {
        translate((float) x, (float) y, (float) z);
    }

    @Override
    public void scale(float x, float y, float z) {
        super.scale(x, y, z);
        mirror.scale(x, y);
    }

    @Override
    public void mulPose(org.joml.Quaternionfc q) {
        super.mulPose(q);
        org.joml.Vector3f e = new org.joml.Quaternionf(q).getEulerAnglesZYX(new org.joml.Vector3f());
        mirror.rotate(e.z);
    }
}
