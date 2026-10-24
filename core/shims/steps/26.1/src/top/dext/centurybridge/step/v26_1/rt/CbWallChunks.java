package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.world.level.Level;
import top.dext.centurybridge.step.v26_1.facade.BlockRenderDispatcher;

/**
 * Chunk section rendering members 26.1 reshaped.
 * <ul>
 * <li>ChunkSectionLayer.TRIPWIRE / ChunkSectionLayerGroup.TRIPWIRE: 26.1 has three layers (solid, cutout,
 *     translucent) and picks a block's layer from its textures' transparency; 1.21.11's tripwire layer was drawn
 *     blended, in a group of its own after the translucent one. Its 26.1 counterpart is the translucent layer and
 *     group.</li>
 * <li>SectionCompiler: 1.21.11's took the block and block entity render dispatchers and read the ambient occlusion and
 *     cutout leaves options while compiling; 26.1's takes the block / fluid model sets, block colours and the options'
 *     values, as 26.1's LevelRenderer builds it (the dispatcher's model manager, the game's colours).</li>
 * <li>SectionRenderDispatcher: 1.21.11's built its compiler itself and setLevel(level) kept it; 26.1's takes one.</li>
 * <li>RenderRegionCache.createRegion(level, node): 26.1's takes the client level (section rendering only runs on one).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbWallChunks {

    private CbWallChunks() {
    }

    /** ChunkSectionLayer.TRIPWIRE read by 1.21.11 code */
    public static ChunkSectionLayer tripwireLayer() {
        return ChunkSectionLayer.TRANSLUCENT;
    }

    /** ChunkSectionLayerGroup.TRIPWIRE read by 1.21.11 code */
    public static ChunkSectionLayerGroup tripwireGroup() {
        return ChunkSectionLayerGroup.TRANSLUCENT;
    }

    public static boolean ambientOcclusion() {
        return Minecraft.getInstance().options.ambientOcclusion().get();
    }

    public static boolean cutoutLeaves() {
        return Minecraft.getInstance().options.cutoutLeaves().get();
    }

    public static BlockStateModelSet blockStateModels(BlockRenderDispatcher dispatcher) {
        return CbWallGameRenderer.modelManager(dispatcher).getBlockStateModelSet();
    }

    public static FluidStateModelSet fluidStateModels(BlockRenderDispatcher dispatcher) {
        return CbWallGameRenderer.modelManager(dispatcher).getFluidStateModelSet();
    }

    public static BlockColors blockColors() {
        return Minecraft.getInstance().getBlockColors();
    }

    /** the compiler 1.21.11's SectionRenderDispatcher built from the two dispatchers */
    public static SectionCompiler sectionCompiler(BlockRenderDispatcher blocks, BlockEntityRenderDispatcher blockEntities) {
        return new SectionCompiler(ambientOcclusion(), cutoutLeaves(), blockStateModels(blocks), fluidStateModels(blocks), blockColors(), blockEntities);
    }

    /** 1.21.11 SectionRenderDispatcher.setLevel(level): the dispatcher keeps its compiler */
    public static void setLevel(SectionRenderDispatcher dispatcher, ClientLevel level) {
        dispatcher.setLevel(level, dispatcher.sectionCompiler);
    }

    /** the level a 1.21.11 RenderRegionCache.createRegion was given, as the client level 26.1's takes */
    public static ClientLevel clientLevel(Level level) {
        if (level instanceof ClientLevel client) {
            return client;
        }
        throw new IllegalArgumentException("26.1 builds render regions of a client level only, not " + level);
    }
}
