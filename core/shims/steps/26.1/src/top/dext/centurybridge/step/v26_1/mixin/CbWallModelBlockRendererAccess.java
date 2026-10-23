package top.dext.centurybridge.step.v26_1.mixin;

import java.util.List;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * ModelBlockRenderer's per-part tesselation (1.21.11's tesselateWithAO / tesselateWithoutAO were public and
 * took the parts; 26.1 keeps them as private tesselateAmbientOcclusion / tesselateFlat behind
 * tesselateBlock(model)), its tint cache reset and its colours, for CbWallBlockRender.
 */
@Mixin(ModelBlockRenderer.class)
public interface CbWallModelBlockRendererAccess {

    @Invoker("tesselateAmbientOcclusion")
    void cb$tesselateAmbientOcclusion(BlockQuadOutput output, float x, float y, float z, List<BlockStateModelPart> parts,
                                      BlockAndTintGetter level, BlockState state, BlockPos pos);

    @Invoker("tesselateFlat")
    void cb$tesselateFlat(BlockQuadOutput output, float x, float y, float z, List<BlockStateModelPart> parts,
                          BlockAndTintGetter level, BlockState state, BlockPos pos);

    @Invoker("resetTintCache")
    void cb$resetTintCache();

    @Accessor("blockColors")
    BlockColors cb$blockColors();

    @Accessor("cull")
    boolean cb$cull();
}
