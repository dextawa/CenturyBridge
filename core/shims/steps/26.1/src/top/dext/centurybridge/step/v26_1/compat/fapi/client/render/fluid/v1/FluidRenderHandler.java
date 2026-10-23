package top.dext.centurybridge.step.v26_1.compat.fapi.client.render.fluid.v1;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import top.dext.centurybridge.step.v26_1.rt.CbFapiFluids;

/**
 * 1.21.11's net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler. 26.1's Fabric API kept the name
 * for a different interface (one renderFluid taking the FluidRenderer and its Output; sprites and tint moved to
 * the fluid's FluidModel), so a 1.21.11 mod's handler is this one: per-position sprites and colour, and a
 * renderFluid writing to the one consumer of the fluid's layer. rt/CbFapiFluids puts it into 26.1's fluid
 * rendering (the fluid's model, the tesselation and the reload), as 1.21.11's FluidRenderHandlerRegistryImpl
 * and LiquidBlockRendererMixin did. The default bodies are 1.21.11's.
 */
@Environment(EnvType.CLIENT)
public interface FluidRenderHandler {
    TextureAtlasSprite[] getFluidSprites(BlockAndLightGetter view, BlockPos pos, FluidState state);

    default int getFluidColor(BlockAndLightGetter view, BlockPos pos, FluidState state) {
        return -1;
    }

    default void renderFluid(BlockPos pos, BlockAndLightGetter world, VertexConsumer vertexConsumer, BlockState blockState,
                             FluidState fluidState) {
        CbFapiFluids.renderDefault(this, world, pos, vertexConsumer, blockState, fluidState);
    }

    default void reloadTextures(TextureAtlas textureAtlas) {
    }
}
