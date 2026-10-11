package cb.rendertest;

/** A BlockEntityRenderer for chests: an emerald-textured quad standing on top of the chest. */
@SuppressWarnings({"rawtypes", "unchecked"})
public class TestChestRenderer implements net.minecraft.class_827 {

    static final net.minecraft.class_2960 TEXTURE = new net.minecraft.class_2960("minecraft", "textures/block/emerald_block.png");

    public TestChestRenderer(net.minecraft.class_5614.class_5615 ctx) {
        RenderTestMod.once("chest renderer constructed");
    }

    @Override
    public void method_3569(net.minecraft.class_2586 blockEntity, float tickDelta, net.minecraft.class_4587 matrices,
                            net.minecraft.class_4597 buffers, int light, int overlay) {
        RenderTestMod.once("chest render(blockEntity=" + blockEntity.getClass().getSimpleName() + ", light=" + light + ")");
        net.minecraft.class_4588 vc = buffers.getBuffer(net.minecraft.class_1921.method_23578(TEXTURE));
        net.minecraft.class_4587.class_4665 last = matrices.method_23760();
        org.joml.Matrix4f pos = last.method_23761();
        org.joml.Matrix3f nrm = last.method_23762();
        vertex(vc, pos, nrm, light, overlay, 0.0F, 1.0F, 0.0F, 1.0F);
        vertex(vc, pos, nrm, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        vertex(vc, pos, nrm, light, overlay, 1.0F, 2.0F, 1.0F, 0.0F);
        vertex(vc, pos, nrm, light, overlay, 0.0F, 2.0F, 0.0F, 0.0F);
    }

    private static void vertex(net.minecraft.class_4588 vc, org.joml.Matrix4f pos, org.joml.Matrix3f nrm, int light, int overlay,
                               float x, float y, float u, float v) {
        vc.method_22918(pos, x, y, 0.5F).method_1336(255, 255, 255, 255).method_22913(u, v)
            .method_22922(overlay).method_22916(light).method_23763(nrm, 0.0F, 0.0F, 1.0F).method_1344();
    }
}
