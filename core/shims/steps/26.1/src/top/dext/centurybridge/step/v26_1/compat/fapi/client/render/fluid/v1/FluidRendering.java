package top.dext.centurybridge.step.v26_1.compat.fapi.client.render.fluid.v1;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import top.dext.centurybridge.step.v26_1.rt.CbFapiFluids;

/**
 * 1.21.11's net.fabricmc.fabric.api.client.render.fluid.v1.FluidRendering, whose render and DefaultRenderer
 * 26.1 reshaped around FluidRenderer and its Output: 1.21.11's shape over the {@link FluidRenderHandler} of
 * 1.21.11, the bodies 1.21.11's FluidRenderingImpl's (rt/CbFapiFluids).
 */
@Environment(EnvType.CLIENT)
public final class FluidRendering {
    private FluidRendering() {
    }

    public static void render(FluidRenderHandler handler, BlockAndLightGetter world, BlockPos pos, VertexConsumer vertexConsumer,
                              BlockState blockState, FluidState fluidState, DefaultRenderer defaultRenderer) {
        CbFapiFluids.render(handler, world, pos, vertexConsumer, blockState, fluidState, defaultRenderer);
    }

    public interface DefaultRenderer {
        default void render(FluidRenderHandler handler, BlockAndLightGetter world, BlockPos pos, VertexConsumer vertexConsumer,
                            BlockState blockState, FluidState fluidState) {
            CbFapiFluids.renderVanillaDefault(handler, world, pos, vertexConsumer, blockState, fluidState);
        }
    }
}
