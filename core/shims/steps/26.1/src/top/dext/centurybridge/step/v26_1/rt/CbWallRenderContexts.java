package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import top.dext.centurybridge.step.v26_1.facade.BlockRenderDispatcher;
import top.dext.centurybridge.step.v26_1.facade.ItemRenderer;

/**
 * Renderer provider contexts: 1.21.11's carried the BlockRenderDispatcher (and the block entity one the
 * ItemRenderer), 26.1's a BlockModelResolver over the model manager (and no item renderer). A 1.21.11 mod asking a
 * context for them gets the game's own (the facades Minecraft's getBlockRenderer / the item renderer answer);
 * a context a 1.21.11 constructor builds resolves block models over the model manager the given dispatcher's
 * models come from.
 */
@Environment(EnvType.CLIENT)
public final class CbWallRenderContexts {

    private CbWallRenderContexts() {
    }

    /** the BlockModelResolver standing for a 1.21.11 BlockRenderDispatcher argument */
    public static BlockModelResolver blockModelResolver(BlockRenderDispatcher dispatcher) {
        return new BlockModelResolver(dispatcher != null ? CbWallGameRenderer.modelManager(dispatcher)
            : Minecraft.getInstance().getModelManager());
    }

    /** EntityRendererProvider.Context.getBlockRenderDispatcher() */
    public static BlockRenderDispatcher getBlockRenderDispatcher(EntityRendererProvider.Context context) {
        return CbWallBlockRender.blockRenderer(Minecraft.getInstance());
    }

    /** BlockEntityRendererProvider.Context.blockRenderDispatcher() */
    public static BlockRenderDispatcher blockRenderDispatcher(BlockEntityRendererProvider.Context context) {
        return CbWallBlockRender.blockRenderer(Minecraft.getInstance());
    }

    /** BlockEntityRendererProvider.Context.itemRenderer() */
    public static ItemRenderer itemRenderer(BlockEntityRendererProvider.Context context) {
        return ItemRenderer.CB$INSTANCE;
    }
}
