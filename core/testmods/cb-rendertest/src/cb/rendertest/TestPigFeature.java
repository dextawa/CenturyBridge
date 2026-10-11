package cb.rendertest;

/**
 * A FeatureRenderer on the test pig: a raw diamond-textured quad above the entity, and the
 * pig's own model drawn again slightly enlarged with FeatureRenderer.renderModel (the
 * armour-layer pattern), so both the feature hook and the static helper are exercised.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class TestPigFeature extends net.minecraft.class_3887 {

    static final net.minecraft.class_2960 QUAD = new net.minecraft.class_2960("minecraft", "textures/block/diamond_block.png");
    static final net.minecraft.class_2960 SHELL = new net.minecraft.class_2960("minecraft", "textures/block/glass.png");

    public TestPigFeature(net.minecraft.class_3883 context) {
        super(context);
    }

    @Override
    public void method_4199(net.minecraft.class_4587 matrices, net.minecraft.class_4597 buffers, int light,
                            net.minecraft.class_1297 entity, float limbAngle, float limbDistance, float tickDelta,
                            float animationProgress, float headYaw, float headPitch) {
        RenderTestMod.once("pig feature render(entity=" + entity.getClass().getSimpleName() + ", age=" + (int) animationProgress + ")");
        net.minecraft.class_4588 vc = buffers.getBuffer(net.minecraft.class_1921.method_23578(QUAD));
        net.minecraft.class_4587.class_4665 last = matrices.method_23760();
        org.joml.Matrix4f pos = last.method_23761();
        org.joml.Matrix3f nrm = last.method_23762();
        float y = -0.6F; // model space is upside down: above the body
        quad(vc, pos, nrm, light, -0.4F, y - 0.8F, 0.4F, y);
        matrices.method_22903();
        matrices.method_22905(1.1F, 1.1F, 1.1F);
        method_23199(this.method_17165(), SHELL, matrices, buffers, light, (net.minecraft.class_1309) entity, 1.0F, 1.0F, 1.0F);
        matrices.method_22909();
    }

    private static void quad(net.minecraft.class_4588 vc, org.joml.Matrix4f pos, org.joml.Matrix3f nrm, int light,
                             float x0, float y0, float x1, float y1) {
        vertex(vc, pos, nrm, light, x0, y0, 0.0F, 0.0F);
        vertex(vc, pos, nrm, light, x1, y0, 1.0F, 0.0F);
        vertex(vc, pos, nrm, light, x1, y1, 1.0F, 1.0F);
        vertex(vc, pos, nrm, light, x0, y1, 0.0F, 1.0F);
    }

    private static void vertex(net.minecraft.class_4588 vc, org.joml.Matrix4f pos, org.joml.Matrix3f nrm, int light,
                               float x, float y, float u, float v) {
        vc.method_22918(pos, x, y, 0.0F).method_1336(255, 255, 255, 255).method_22913(u, v)
            .method_22922(net.minecraft.class_4608.field_21444).method_22916(light).method_23763(nrm, 0.0F, 0.0F, 1.0F).method_1344();
    }
}
