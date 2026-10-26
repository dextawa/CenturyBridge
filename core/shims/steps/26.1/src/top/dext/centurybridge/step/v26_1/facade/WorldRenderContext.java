package top.dext.centurybridge.step.v26_1.facade;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import top.dext.centurybridge.step.v26_1.rt.CbWorldRenderHome;

/**
 * Fabric API 0.136's (1.21.8) WorldRenderContext and its BlockOutlineContext: the 26.1 home of the 1.21.9 layer's copy
 * (see WorldRenderEvents), with the accessors that copy has once converted to 26.1 names. Answered by
 * rt/CbWorldRenderHomeContext with what 26.1's renderLevel was handed or built at the matching point of the frame.
 */
@Environment(EnvType.CLIENT)
public interface WorldRenderContext {

    /** the context of the frame the renderer is drawing; IllegalStateException outside a frame, as 0.136 */
    static WorldRenderContext getInstance(LevelRenderer worldRenderer) {
        return CbWorldRenderHome.cbContext(worldRenderer);
    }

    LevelRenderer worldRenderer();

    DeltaTracker tickCounter();

    boolean blockOutlines();

    Camera camera();

    GameRenderer gameRenderer();

    Matrix4f positionMatrix();

    Matrix4f projectionMatrix();

    ClientLevel world();

    boolean advancedTranslucency();

    MultiBufferSource consumers();

    Frustum frustum();

    PoseStack matrixStack();

    boolean translucentBlockOutline();

    @Environment(EnvType.CLIENT)
    interface BlockOutlineContext {
        Entity entity();

        double cameraX();

        double cameraY();

        double cameraZ();

        BlockPos blockPos();

        BlockState blockState();

        VertexConsumer vertexConsumer();
    }
}
