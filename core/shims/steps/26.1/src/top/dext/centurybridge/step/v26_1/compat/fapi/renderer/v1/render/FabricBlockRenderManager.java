package top.dext.centurybridge.step.v26_1.compat.fapi.renderer.v1.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.state.BlockState;
import top.dext.centurybridge.step.v26_1.facade.BlockRenderDispatcher;
import top.dext.centurybridge.step.v26_1.rt.CbFapiRender;

/**
 * 1.21.11's net.fabricmc.fabric.api.renderer.v1.render.FabricBlockRenderManager (injected into BlockRenderDispatcher,
 * the layer's facade on 26.1), which 26.1's Fabric API removed. The body is 1.21.11's: the renderer's
 * renderBlockAsEntity (rt/CbFapiRender).
 */
@Environment(EnvType.CLIENT)
public interface FabricBlockRenderManager {
    default void renderBlockAsEntity(BlockState state, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay,
                                     BlockAndLightGetter blockView, BlockPos pos) {
        CbFapiRender.renderBlockAsEntityImpl((BlockRenderDispatcher) this, state, matrices, vertexConsumers, light, overlay, blockView, pos);
    }
}
