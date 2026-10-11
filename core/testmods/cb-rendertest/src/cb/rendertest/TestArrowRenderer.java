package cb.rendertest;

/** A plain EntityRenderer that draws a white-ish textured quad with raw vertex calls. */
@SuppressWarnings({"rawtypes", "unchecked"})
public class TestArrowRenderer extends net.minecraft.class_897 {

    static final net.minecraft.class_2960 TEXTURE = new net.minecraft.class_2960("minecraft", "textures/block/gold_block.png");

    public TestArrowRenderer(net.minecraft.class_5617.class_5618 ctx) {
        super(ctx);
        RenderTestMod.once("arrow renderer constructed");
    }

    @Override
    public net.minecraft.class_2960 method_3931(net.minecraft.class_1297 entity) {
        return TEXTURE;
    }

    @Override
    public void method_3936(net.minecraft.class_1297 entity, float yaw, float tickDelta,
                            net.minecraft.class_4587 matrices, net.minecraft.class_4597 buffers, int light) {
        RenderTestMod.once("arrow render(light=" + light + ")");
        net.minecraft.class_4588 vc = buffers.getBuffer(net.minecraft.class_1921.method_23578(TEXTURE));
        net.minecraft.class_4587.class_4665 last = matrices.method_23760();
        org.joml.Matrix4f pos = last.method_23761();
        org.joml.Matrix3f nrm = last.method_23762();
        quad(vc, pos, nrm, light, -0.75F, 0.0F, 0.75F, 1.5F);
        super.method_3936(entity, yaw, tickDelta, matrices, buffers, light);
    }

    private static void quad(net.minecraft.class_4588 vc, org.joml.Matrix4f pos, org.joml.Matrix3f nrm, int light,
                             float x0, float y0, float x1, float y1) {
        vertex(vc, pos, nrm, light, x0, y0, 0.0F, 1.0F);
        vertex(vc, pos, nrm, light, x1, y0, 1.0F, 1.0F);
        vertex(vc, pos, nrm, light, x1, y1, 1.0F, 0.0F);
        vertex(vc, pos, nrm, light, x0, y1, 0.0F, 0.0F);
    }

    private static void vertex(net.minecraft.class_4588 vc, org.joml.Matrix4f pos, org.joml.Matrix3f nrm, int light,
                               float x, float y, float u, float v) {
        vc.method_22918(pos, x, y, 0.0F).method_1336(255, 255, 255, 255).method_22913(u, v)
            .method_22922(net.minecraft.class_4608.field_21444).method_22916(light).method_23763(nrm, 0.0F, 0.0F, 1.0F).method_1344();
    }
}
