package top.dext.centurybridge.step.v26_1.compat.fapi.client.rendering.v1;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.level.block.Block;
import top.dext.centurybridge.step.v26_1.rt.CbFapiSpecialBlocks;

/**
 * 1.21.11's net.fabricmc.fabric.api.client.rendering.v1.SpecialBlockRendererRegistry, which 26.1's Fabric API removed
 * (26.1 renders a block's special renderer through its built-in block model). rt/CbFapiSpecialBlocks puts each
 * registration into 26.1's BuiltInBlockModels the way 26.1 registers its own special blocks.
 */
@Environment(EnvType.CLIENT)
public final class SpecialBlockRendererRegistry {
    private SpecialBlockRendererRegistry() {
    }

    public static void register(Block block, SpecialModelRenderer.Unbaked<?> unbakedRenderer) {
        CbFapiSpecialBlocks.register(block, unbakedRenderer);
    }
}
