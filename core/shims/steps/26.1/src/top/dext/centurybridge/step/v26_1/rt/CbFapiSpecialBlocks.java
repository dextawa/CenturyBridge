package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.SpecialBlockModelWrapper;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import top.dext.centurybridge.step.v26_1.mixin.CbFapiBuiltInBlockModelsAccess;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 1.21.11's SpecialBlockRendererRegistry (SpecialBlockRendererRegistryImpl: the blocks Fabric API added to vanilla's
 * static block -> special renderer mapping, rendered with the block when it is drawn outside the world) on 26.1,
 * where that role is the block's built-in model: each registered block's states take, at every
 * BuiltInBlockModels.createBlockModels (each model reload), the special renderer combined with the block's own
 * model exactly as 26.1 builds its own special blocks (Builder.put with a SpecialModelFactory:
 * combineSpecialAndBlockModels of a SpecialBlockModelWrapper without transformation). A later registration of the
 * same block replaces the earlier one, as 1.21.11's map put did.
 */
@Environment(EnvType.CLIENT)
public final class CbFapiSpecialBlocks {
    private static final Map<Block, SpecialModelRenderer.Unbaked<?>> BLOCKS = new LinkedHashMap<>();

    private CbFapiSpecialBlocks() {
    }

    /** 1.21.11 SpecialBlockRendererRegistry.register */
    public static synchronized void register(Block block, SpecialModelRenderer.Unbaked<?> unbakedRenderer) {
        BLOCKS.put(Objects.requireNonNull(block, "block"), Objects.requireNonNull(unbakedRenderer, "unbakedRenderer"));
    }

    /** BuiltInBlockModels.createBlockModels' answer with the 1.21.11 registrations added */
    public static synchronized Map<BlockState, BlockModel.Unbaked> withRegistered(Map<BlockState, BlockModel.Unbaked> models, BlockColors colors) {
        if (BLOCKS.isEmpty()) {
            return models;
        }
        Map<BlockState, BlockModel.Unbaked> out = new HashMap<>(models);
        BLOCKS.forEach((block, renderer) -> {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                out.put(state, CbFapiBuiltInBlockModelsAccess.cb$combineSpecialAndBlockModels(
                        new SpecialBlockModelWrapper.Unbaked(renderer, java.util.Optional.empty()), colors, state));
            }
        });
        return Map.copyOf(out);
    }
}
