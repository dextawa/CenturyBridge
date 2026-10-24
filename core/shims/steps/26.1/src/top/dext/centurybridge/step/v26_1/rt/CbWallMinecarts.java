package top.dext.centurybridge.step.v26_1.rt;

import com.google.common.collect.MapMaker;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A minecart's displayed block. 1.21.11's MinecartRenderState carried the block state (displayBlockState, field_53487)
 * and its renderer's submitMinecartContents(state, blockState, pose, collector, light) submitted it as a block; 26.1's
 * carries the block's resolved model (displayBlockModel) and submitMinecartContents(state, model, ...) submits that.
 * The block state a renderer extracted is kept beside the render state (CbWallMinecartContentsMixin); a 1.21.11 write
 * of the field resolves the model again, as the next frame of 1.21.11 rendered whatever the field held.
 */
@Environment(EnvType.CLIENT)
public final class CbWallMinecarts {

    private static final Map<MinecartRenderState, BlockState> DISPLAY = new MapMaker().weakKeys().makeMap();

    private CbWallMinecarts() {
    }

    /** the block state AbstractMinecartRenderer.extractRenderState resolved the state's model from */
    public static void extracted(MinecartRenderState state, BlockState block) {
        DISPLAY.put(state, block);
    }

    /** MinecartRenderState.displayBlockState read by 1.21.11 code (1.21.11's initial value: air) */
    public static BlockState displayBlockState(MinecartRenderState state) {
        BlockState block = DISPLAY.get(state);
        return block != null ? block : Blocks.AIR.defaultBlockState();
    }

    /** MinecartRenderState.displayBlockState written by 1.21.11 code: the model shown follows it */
    public static void setDisplayBlockState(MinecartRenderState state, BlockState block) {
        DISPLAY.put(state, block);
        new BlockModelResolver(Minecraft.getInstance().getModelManager())
            .update(state.displayBlockModel, block, AbstractMinecartRenderer.BLOCK_DISPLAY_CONTEXT);
    }

    /** 1.21.11 AbstractMinecartRenderer.submitMinecartContents body: the block, no overlay, the state's outline */
    public static void renderContents(MinecartRenderState state, BlockState block, PoseStack pose, SubmitNodeCollector collector, int light) {
        CbFapiSubmitBlocks.submitBlock(collector, pose, block, light, OverlayTexture.NO_OVERLAY, state.outlineColor);
    }

    /** 1.21.11 TntMinecartRenderer.submitWhiteSolidBlock(blockState, pose, collector, light, white, outline) */
    public static void submitWhiteSolidBlock(BlockState block, PoseStack pose, SubmitNodeCollector collector, int light, boolean white, int outline) {
        int overlay = white ? OverlayTexture.pack(OverlayTexture.u(1.0F), 10) : OverlayTexture.NO_OVERLAY;
        CbFapiSubmitBlocks.submitBlock(collector, pose, block, light, overlay, outline);
    }
}
