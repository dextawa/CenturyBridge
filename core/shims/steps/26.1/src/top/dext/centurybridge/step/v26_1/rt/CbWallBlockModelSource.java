package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The block state a 26.1 BlockModelRenderState shows. 1.21.11's entity render states carried the BlockState itself
 * (EndermanRenderState.carriedBlock, TntRenderState.blockState, CopperGolemRenderState.blockOnAntenna,
 * SnowGolemRenderState.hasPumpkin, BlockDisplayEntityRenderState.blockRenderState) and the renderer drew that block
 * when it submitted; 26.1's carry a BlockModelRenderState that the renderer's BlockModelResolver.update(model,
 * blockState, context) fills at extract (BlockModelRenderState.clear() when there is no block; javap of the five
 * extractRenderState bodies). update(..) first clears the model, then fills it.
 * <p>
 * mixin/CbWallBlockModelResolverMixin records, at update's return, the block state, the resolver and the display
 * context on the model; mixin/CbWallBlockModelSourceMixin forgets the block state when the model is cleared. So a
 * 1.21.11 read answers the block the model was last resolved from (null once cleared), and a 1.21.11 write resolves
 * the model again from the written block, with the resolver and context it was last resolved with (the renderer's
 * own), as 1.21.11's next submit drew whatever the field held.
 */
@Environment(EnvType.CLIENT)
public final class CbWallBlockModelSource {

    private static ModelManager models;
    private static BlockModelResolver resolver;

    private CbWallBlockModelSource() {
    }

    /** implemented by BlockModelRenderState (mixin/CbWallBlockModelSourceMixin) */
    @Environment(EnvType.CLIENT)
    public interface Source {
        BlockState cb$source();

        BlockModelResolver cb$resolver();

        BlockDisplayContext cb$context();

        void cb$resolved(BlockState state, BlockModelResolver resolver, BlockDisplayContext context);
    }

    /** the block state the model was last resolved from, or null when it was cleared since (or never resolved) */
    public static BlockState source(BlockModelRenderState model) {
        return ((Source) model).cb$source();
    }

    /**
     * Show {@code block} in {@code model} (clear it when null): through the resolver and context the model was last
     * resolved with, else a resolver over the client's model manager (26.1's resolvers hold only that) and the
     * renderer's {@code context}.
     */
    public static void show(BlockModelRenderState model, BlockState block, BlockDisplayContext context) {
        if (block == null) {
            model.clear();
            return;
        }
        Source source = (Source) model;
        BlockModelResolver by = source.cb$resolver() != null ? source.cb$resolver() : resolver();
        BlockDisplayContext in = source.cb$context() != null ? source.cb$context() : context;
        by.update(model, block, in);
    }

    private static synchronized BlockModelResolver resolver() {
        ModelManager current = Minecraft.getInstance().getModelManager();
        if (resolver == null || models != current) {
            models = current;
            resolver = new BlockModelResolver(current);
        }
        return resolver;
    }
}
