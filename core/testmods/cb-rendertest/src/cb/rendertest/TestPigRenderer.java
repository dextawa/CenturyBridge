package cb.rendertest;

/** A MobEntityRenderer (raw, as a 1.20.1 mod's erasure sees it) drawing pigs with TestPigModel. */
@SuppressWarnings({"rawtypes", "unchecked"})
public class TestPigRenderer extends net.minecraft.class_927 {

    static final net.minecraft.class_2960 TEXTURE = new net.minecraft.class_2960("minecraft", "textures/block/oak_planks.png");

    public TestPigRenderer(net.minecraft.class_5617.class_5618 ctx) {
        super(ctx, new TestPigModel(TestPigModel.layer().method_32109()), 0.5F);
        this.method_4046(new TestPigFeature(this));
        RenderTestMod.once("pig renderer constructed");
    }

    @Override
    public net.minecraft.class_2960 method_3931(net.minecraft.class_1297 entity) {
        RenderTestMod.once("pig getTexture(entity=" + entity.getClass().getSimpleName() + ")");
        return TEXTURE;
    }

    @Override
    public void method_4072(net.minecraft.class_1308 entity, float yaw, float tickDelta,
                            net.minecraft.class_4587 matrices, net.minecraft.class_4597 buffers, int light) {
        RenderTestMod.once("pig render(yaw, tickDelta, light=" + light + ")");
        super.method_4072(entity, yaw, tickDelta, matrices, buffers, light);
    }
}
