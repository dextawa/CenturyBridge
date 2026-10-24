package top.dext.centurybridge.step.v26_1.compat.fapi.renderer.v1.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.state.BlockState;
import top.dext.centurybridge.step.v26_1.rt.CbFapiRender;

/**
 * 1.21.11's net.fabricmc.fabric.api.renderer.v1.render.FabricBlockModelRenderer (injected into ModelBlockRenderer),
 * which 26.1's Fabric API removed. The bodies are 1.21.11's: both render through the renderer (rt/CbFapiRender,
 * 1.21.11's Indigo on 26.1's AltModelBlockRenderer and QuadEmitter).
 */
@Environment(EnvType.CLIENT)
public interface FabricBlockModelRenderer {
    default void render(BlockAndLightGetter blockView, BlockStateModel model, BlockState state, BlockPos pos, PoseStack matrices,
                        BlockVertexConsumerProvider vertexConsumers, boolean cull, long seed, int overlay) {
        CbFapiRender.renderBlockImpl((ModelBlockRenderer) (Object) this, blockView, model, state, pos, matrices, vertexConsumers, cull, seed,
                overlay);
    }

    static void render(PoseStack.Pose matrices, BlockVertexConsumerProvider vertexConsumers, BlockStateModel model, float red, float green,
                       float blue, int light, int overlay, BlockAndLightGetter blockView, BlockPos pos, BlockState state) {
        CbFapiRender.renderModel(matrices, vertexConsumers, model, red, green, blue, light, overlay, blockView, pos, state);
    }
}
