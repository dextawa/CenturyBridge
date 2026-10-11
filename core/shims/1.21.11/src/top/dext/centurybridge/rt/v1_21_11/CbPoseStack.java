package top.dext.centurybridge.rt.v1_21_11;

/**
 * The PoseStack a 1.20.1 mod gets from DrawContext.getMatrices() past 1.21.6:
 * every transform is applied to the real stack (last() stays meaningful) and
 * mirrored onto the context's 2D Matrix3x2fStack, which is what positions the
 * retained-mode GUI. Lives in the mixin package only so the bridge can name it
 * without an import; it is a plain class, not a mixin.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbPoseStack extends net.minecraft.class_4587 {
    private final org.joml.Matrix3x2fStack mirror;

    public CbPoseStack(org.joml.Matrix3x2fStack mirror) {
        this.mirror = mirror;
    }

    @Override
    public void method_22903() {
        super.method_22903();
        mirror.pushMatrix();
    }

    @Override
    public void method_22909() {
        super.method_22909();
        mirror.popMatrix();
    }

    @Override
    public void method_46416(float x, float y, float z) {
        super.method_46416(x, y, z);
        mirror.translate(x, y);
    }

    @Override
    public void method_22904(double x, double y, double z) {
        method_46416((float) x, (float) y, (float) z);
    }

    @Override
    public void method_22905(float x, float y, float z) {
        super.method_22905(x, y, z);
        mirror.scale(x, y);
    }

    @Override
    public void method_22907(org.joml.Quaternionfc q) {
        super.method_22907(q);
        mirror.rotate(new org.joml.Quaternionf(q).getEulerAnglesZYX(new org.joml.Vector3f()).z);
    }
}
