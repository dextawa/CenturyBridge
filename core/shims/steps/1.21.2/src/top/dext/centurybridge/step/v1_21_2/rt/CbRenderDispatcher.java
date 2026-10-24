package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's EntityRenderDispatcher.render(entity, x, y, z, yRot, partialTick, poseStack, buffers, light) over 1.21.2's
 * render(entity, x, y, z, partialTick, poseStack, buffers, light).
 *
 * <p>1.21.2 dropped the yaw: each renderer's extractRenderState reads the entity's own rotation. A mod dispatcher's
 * 1.21.1 override answers 1.21.2's calls (overrideHooks, bridge {@link #render}) with the yaw 1.21.1's world renderer
 * passed (LevelRenderer.renderEntity: Mth.lerp(partialTick, yRotO, getYRot())); a mod's own call keeps the yaw it passes
 * ({@link #method_3954}); its super call ({@link #superRender}) draws through 1.21.2's render, as 1.21.1's vanilla
 * render drew without reading a yaw its renderers did not use (1.21.1's living renderers took the body rotation from the
 * entity).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderDispatcher {

    /** EntityRenderDispatcher's 1.21.1 render, as a mod subclass overrides it */
    public interface Legacy {
        void method_3954(net.minecraft.class_1297 entity, double x, double y, double z, float yRot, float partialTick,
                         net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light);
    }

    private CbRenderDispatcher() {
    }

    /** the synthesized 1.21.2 render of a mod dispatcher overriding 1.21.1's (overrideHooks bridge) */
    public static void render(Legacy dispatcher, net.minecraft.class_1297 entity, double x, double y, double z, float partialTick,
                              net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light) {
        float yRot = net.minecraft.class_3532.method_16439(partialTick, entity.field_5982, entity.method_36454());
        dispatcher.method_3954(entity, x, y, z, yRot, partialTick, poseStack, buffers, light);
    }

    /** a mod's dispatcher.render(entity, x, y, z, yRot, ...) (instanceRedirects): its override with that yaw, else 1.21.2's render */
    public static void method_3954(net.minecraft.class_898 dispatcher, net.minecraft.class_1297 entity, double x, double y, double z,
                                   float yRot, float partialTick, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                                   int light) {
        if (dispatcher instanceof Legacy legacy) {
            legacy.method_3954(entity, x, y, z, yRot, partialTick, poseStack, buffers, light);
        } else {
            dispatcher.method_62424(entity, x, y, z, partialTick, poseStack, buffers, light);
        }
    }

    /**
     * a mod dispatcher's super.render(entity, x, y, z, yRot, ...) (superRedirects): 1.21.2's vanilla render. Held under
     * the hook's guard, so the synthesized render of the mod class falls through to vanilla's rather than entering the
     * mod's override again.
     */
    public static void superRender(net.minecraft.class_898 dispatcher, net.minecraft.class_1297 entity, double x, double y, double z,
                                   float yRot, float partialTick, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                                   int light) {
        boolean entered = Guard.cbEnter(dispatcher);
        try {
            dispatcher.method_62424(entity, x, y, z, partialTick, poseStack, buffers, light);
        } finally {
            if (entered) {
                Guard.cbExit(dispatcher);
            }
        }
    }
}
