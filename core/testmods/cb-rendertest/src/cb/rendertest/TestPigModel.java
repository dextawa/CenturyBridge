package cb.rendertest;

/**
 * An EntityModel with one cube: setAngles spins it with the entity's age, render draws the
 * cube itself. super() gets no root, as 1.20.1 models never passed one.
 */
@SuppressWarnings("rawtypes")
public class TestPigModel extends net.minecraft.class_583 {

    private final net.minecraft.class_630 body;

    public TestPigModel(net.minecraft.class_630 root) {
        super();
        this.body = root.method_32086("body");
    }

    static net.minecraft.class_5607 layer() {
        net.minecraft.class_5609 mesh = new net.minecraft.class_5609();
        mesh.method_32111().method_32117("body",
            net.minecraft.class_5606.method_32108().method_32101(28, 8).method_32097(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F),
            net.minecraft.class_5603.method_32090(0.0F, 11.0F, 2.0F));
        return net.minecraft.class_5607.method_32110(mesh, 64, 32);
    }

    @Override
    public void method_2819(net.minecraft.class_1297 entity, float limbAngle, float limbDistance,
                            float animationProgress, float headYaw, float headPitch) {
        RenderTestMod.once("pig model setAngles(entity=" + (entity == null ? "null" : entity.getClass().getSimpleName())
            + ", age=" + (int) animationProgress + ")");
        this.body.field_3675 = animationProgress * 0.05F;
    }

    @Override
    public void method_2828(net.minecraft.class_4587 matrices, net.minecraft.class_4588 vertices, int light, int overlay,
                            float red, float green, float blue, float alpha) {
        RenderTestMod.once("pig model render");
        this.body.method_22699(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
