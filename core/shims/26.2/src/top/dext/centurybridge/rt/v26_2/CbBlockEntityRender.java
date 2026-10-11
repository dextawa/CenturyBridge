package top.dext.centurybridge.rt.v26_2;

import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * 1.20.1 block entity renderers over 26.x's extract/submit pipeline.
 *
 * 1.20.1: render(blockEntity, tickDelta, matrices, buffers, light, overlay), the dispatcher
 * passing no overlay (OverlayTexture.DEFAULT_UV). 26.x: createRenderState() (abstract),
 * extractRenderState (default: position, type, light, crumbling), submit(state, poseStack,
 * collector, camera) (abstract). Every state the dispatcher builds remembers its block entity
 * (CbRenderStateBlockEntity), so the old method gets what it got: the block entity, the
 * partial tick, the pose stack vanilla positioned the block with, a recording buffer source
 * (submitted afterwards), the light vanilla computed.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbBlockEntityRender {

    /** renderer class -> whether a vanilla superclass implements createRenderState (then it wins: its submit casts its own state) */
    private static final Map<Class<?>, Boolean> VANILLA_CREATES = new ConcurrentHashMap<>();

    public static BlockEntityRenderState createRenderState(CbLegacyBlockEntityRenderer self) {
        if (VANILLA_CREATES.computeIfAbsent(self.getClass(), CbBlockEntityRender::vanillaCreates)) {
            return ((BlockEntityRenderer<?, ?>) self).createRenderState(); // second entry: super's
        }
        return new BlockEntityRenderState();
    }

    private static boolean vanillaCreates(Class<?> renderer) {
        for (Class<?> c = renderer.getSuperclass(); c != null; c = c.getSuperclass()) {
            if (!c.getName().startsWith("net.minecraft.")) {
                continue;
            }
            try {
                Method m = c.getMethod("createRenderState");
                return !Modifier.isAbstract(m.getModifiers()) && !m.getDeclaringClass().isInterface();
            } catch (NoSuchMethodException e) {
                return false;
            }
        }
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void submit(CbLegacyBlockEntityRenderer self, BlockEntityRenderState state, PoseStack poseStack,
                              SubmitNodeCollector collector, CameraRenderState camera) {
        BlockEntity blockEntity = ((CbRenderStateBlockEntity) state).centurybridge$blockEntity();
        if (blockEntity == null) {
            ((BlockEntityRenderer) self).submit(state, poseStack, collector, camera); // super's, if any
            return;
        }
        float tickDelta = ((CbRenderStateBlockEntity) state).centurybridge$partialTick();
        CbRecordingBuffers buffers = new CbRecordingBuffers();
        try {
            self.submit(blockEntity, tickDelta, poseStack, buffers, state.lightCoords, OverlayTexture.NO_OVERLAY);
        } finally {
            buffers.submitTo(collector);
        }
    }

    private CbBlockEntityRender() {
    }
}
