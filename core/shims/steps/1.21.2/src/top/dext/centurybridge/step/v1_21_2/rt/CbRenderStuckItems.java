package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's renderStuckItem(poseStack, buffers, light, entity, x, y, z, partialTick) of ArrowLayer and BeeStingerLayer,
 * for a 1.21.1 mod that calls it.
 *
 * <p>1.21.2 made StuckInBodyLayer.renderStuckItem private with another shape (a fixed model, no entity), so neither a
 * subclass's super.renderStuckItem(..) (superRedirects: {@link #superArrow}, {@link #superStinger}) nor its own
 * this.renderStuckItem(..) on a layer that does not override it (instanceRedirects: the method_22130 overloads) links.
 * Both get 1.21.1's bodies: ArrowLayer drew a throwaway Arrow entity turned toward (x, y, z) through the entity render
 * dispatcher; BeeStingerLayer drew four crossed 9x2 quads of the stinger texture.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderStuckItems {

    private static final net.minecraft.class_2960 BEE_STINGER = net.minecraft.class_2960.method_60656("textures/entity/bee/bee_stinger.png");

    private CbRenderStuckItems() {
    }

    /** 1.21.1 ArrowLayer.renderStuckItem's body */
    public static void superArrow(net.minecraft.class_973<?> layer, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                                  int light, net.minecraft.class_1297 entity, float x, float y, float z, float partialTick) {
        float horizontal = net.minecraft.class_3532.method_15355(x * x + z * z);
        net.minecraft.class_1667 arrow = new net.minecraft.class_1667(entity.method_37908(), entity.method_23317(), entity.method_23318(),
            entity.method_23321(), net.minecraft.class_1799.field_8037, null);
        arrow.method_36456((float) (Math.atan2(x, z) * 57.2957763671875));
        arrow.method_36457((float) (Math.atan2(y, horizontal) * 57.2957763671875));
        arrow.field_5982 = arrow.method_36454();
        arrow.field_6004 = arrow.method_36455();
        net.minecraft.class_310.method_1551().method_1561()
            .method_62424(arrow, 0.0, 0.0, 0.0, partialTick, poseStack, buffers, light);
    }

    /** 1.21.1 BeeStingerLayer.renderStuckItem's body */
    public static void superStinger(net.minecraft.class_4506<?> layer, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                                    int light, net.minecraft.class_1297 entity, float x, float y, float z, float partialTick) {
        float horizontal = net.minecraft.class_3532.method_15355(x * x + z * z);
        float yRot = (float) (Math.atan2(x, z) * 57.2957763671875);
        float xRot = (float) (Math.atan2(y, horizontal) * 57.2957763671875);
        poseStack.method_46416(0.0F, 0.0F, 0.0F);
        poseStack.method_22907(net.minecraft.class_7833.field_40716.rotationDegrees(yRot - 90.0F));
        poseStack.method_22907(net.minecraft.class_7833.field_40718.rotationDegrees(xRot));
        poseStack.method_22907(net.minecraft.class_7833.field_40714.rotationDegrees(45.0F));
        poseStack.method_22905(0.03125F, 0.03125F, 0.03125F);
        poseStack.method_46416(2.5F, 0.0F, 0.0F);
        net.minecraft.class_4588 consumer = buffers.getBuffer(net.minecraft.class_1921.method_23578(BEE_STINGER));
        for (int i = 0; i < 4; i++) {
            poseStack.method_22907(net.minecraft.class_7833.field_40714.rotationDegrees(90.0F));
            net.minecraft.class_4587.class_4665 pose = poseStack.method_23760();
            stingerVertex(consumer, pose, -4.5F, -1, 0.0F, 0.0F, light);
            stingerVertex(consumer, pose, 4.5F, -1, 0.125F, 0.0F, light);
            stingerVertex(consumer, pose, 4.5F, 1, 0.125F, 0.0625F, light);
            stingerVertex(consumer, pose, -4.5F, 1, 0.0F, 0.0625F, light);
        }
    }

    private static void stingerVertex(net.minecraft.class_4588 consumer, net.minecraft.class_4587.class_4665 pose, float x, int y, float u,
                                      float v, int light) {
        consumer.method_56824(pose, x, (float) y, 0.0F)
            .method_39415(-1)
            .method_22913(u, v)
            .method_22922(net.minecraft.class_4608.field_21444)
            .method_60803(light)
            .method_60831(pose, 0.0F, 1.0F, 0.0F);
    }

    /** this.renderStuckItem(..) on a StuckInBodyLayer: a 1.21.1 subclass's own override (adapterHooks), else 1.21.1's vanilla body */
    public static void method_22130(net.minecraft.class_4507<?> layer, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                                    int light, net.minecraft.class_1297 entity, float x, float y, float z, float partialTick) {
        if (layer instanceof CbRenderStuck.Legacy legacy) {
            legacy.method_22130(poseStack, buffers, light, entity, x, y, z, partialTick);
        } else if (layer instanceof net.minecraft.class_973<?> arrow) {
            superArrow(arrow, poseStack, buffers, light, entity, x, y, z, partialTick);
        } else if (layer instanceof net.minecraft.class_4506<?> stinger) {
            superStinger(stinger, poseStack, buffers, light, entity, x, y, z, partialTick);
        } else {
            // 1.21.1's StuckInBodyLayer was abstract in renderStuckItem: every 1.21.1 layer is one of the three above
            throw new IllegalStateException("no 1.21.1 renderStuckItem on " + layer.getClass().getName());
        }
    }

    public static void method_22130(net.minecraft.class_973<?> layer, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                                    int light, net.minecraft.class_1297 entity, float x, float y, float z, float partialTick) {
        method_22130((net.minecraft.class_4507<?>) layer, poseStack, buffers, light, entity, x, y, z, partialTick);
    }

    public static void method_22130(net.minecraft.class_4506<?> layer, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                                    int light, net.minecraft.class_1297 entity, float x, float y, float z, float partialTick) {
        method_22130((net.minecraft.class_4507<?>) layer, poseStack, buffers, light, entity, x, y, z, partialTick);
    }
}
