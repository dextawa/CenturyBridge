package top.dext.centurybridge.step.v26_1.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import top.dext.centurybridge.step.v26_1.rt.CbFapiBlockLayers;

/**
 * 1.21.11's ItemBlockRenderTypes (net/minecraft/class_4696) on 26.1, which removed it (each quad carries its own
 * layer). The answers are rt/CbFapiBlockLayers': the block table Fabric API's BlockRenderLayerMap fills, the layer
 * a block's 26.1 model renders in otherwise, the fluid's model layer, the cutout-leaves flag. Member names are
 * 1.21.11's intermediary names.
 */
@Environment(EnvType.CLIENT)
public class ItemBlockRenderTypes {

    public ItemBlockRenderTypes() {
    }

    /** getChunkRenderType */
    public static ChunkSectionLayer method_23679(BlockState state) {
        return CbFapiBlockLayers.getChunkRenderType(state);
    }

    /** getMovingBlockRenderType */
    public static RenderType method_29359(BlockState state) {
        return CbFapiBlockLayers.getMovingBlockRenderType(state);
    }

    /** getRenderType */
    public static RenderType method_23683(BlockState state) {
        return CbFapiBlockLayers.getRenderType(state);
    }

    /** getRenderLayer */
    public static ChunkSectionLayer method_23680(FluidState state) {
        return CbFapiBlockLayers.getRenderLayer(state);
    }

    /** setCutoutLeaves */
    public static void method_75412(boolean cutoutLeaves) {
        CbFapiBlockLayers.setCutoutLeaves(cutoutLeaves);
    }
}
