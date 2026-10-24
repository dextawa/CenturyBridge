package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's StuckInBodyLayer for a 1.21.1 subclass (a mod's stuck-item layer).
 *
 * <p>1.21.1: the layer drew numStuck(entity) items, each at a random cube of a random part of the player model (seeded
 * by the entity id), handing the subclass's renderStuckItem(poseStack, buffers, light, entity, x, y, z, partialTick) the
 * direction it sticks out in. 1.21.2's layer draws one fixed model per stuck item itself (renderStuckItem is private,
 * with no entity), its constructor taking that model, a texture and a placement. A 1.21.1 subclass is built with an
 * empty model (never drawn) and, being under the adapter (adapterHooks: its own renderStuckItem implements
 * {@link Legacy}), its layer draws 1.21.1's body: the same random choices and offsets, its renderStuckItem called with the
 * entity the state was extracted from (CbRenderStuckMixin).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderStuck {

    /** a 1.21.1 StuckInBodyLayer subclass's renderStuckItem */
    public interface Legacy {
        void method_22130(net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light, net.minecraft.class_1297 entity,
                          float x, float y, float z, float partialTick);
    }

    private CbRenderStuck() {
    }

    /** the stuck model a 1.21.1 subclass hands 1.21.2's constructor: an empty one, never drawn (its own renderStuckItem draws) */
    public static net.minecraft.class_3879 emptyModel() {
        return new net.minecraft.class_3879.class_9948(CbRenderModelBase.emptyRoot(), net.minecraft.class_1921::method_23578);
    }

    public static net.minecraft.class_2960 noTexture() {
        return net.minecraft.class_1047.method_4539();
    }

    public static net.minecraft.class_4507.class_9995 placement() {
        return net.minecraft.class_4507.class_9995.field_53232;
    }

    /** 1.21.1 StuckInBodyLayer.render's body for a 1.21.1 subclass, on the entity of the state; false when there is none */
    public static boolean render(net.minecraft.class_4507<?> layer, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light,
                                 net.minecraft.class_10055 state) {
        if (!(CbRenderStateEntity.entityOf(state) instanceof net.minecraft.class_1309 entity)) {
            return false;
        }
        int count = ((CbRenderStateGen.W4507) layer).cb$w$method_22134(state);
        net.minecraft.class_5819 random = net.minecraft.class_5819.method_43049(entity.method_5628());
        float partialTick = CbRenderStateEntity.partialTickOf(state);
        for (int i = 0; i < count; i++) {
            poseStack.method_22903();
            net.minecraft.class_630 part = ((net.minecraft.class_591) layer.method_17165()).method_22697(random);
            net.minecraft.class_630.class_628 cube = part.method_22700(random);
            part.method_22703(poseStack);
            float fx = random.method_43057();
            float fy = random.method_43057();
            float fz = random.method_43057();
            poseStack.method_46416(net.minecraft.class_3532.method_16439(fx, cube.field_3645, cube.field_3648) / 16.0F,
                net.minecraft.class_3532.method_16439(fy, cube.field_3644, cube.field_3647) / 16.0F,
                net.minecraft.class_3532.method_16439(fz, cube.field_3643, cube.field_3646) / 16.0F);
            ((Legacy) layer).method_22130(poseStack, buffers, light, entity, -1.0F * (fx * 2.0F - 1.0F), -1.0F * (fy * 2.0F - 1.0F),
                -1.0F * (fz * 2.0F - 1.0F), partialTick);
            poseStack.method_22909();
        }
        return true;
    }
}
