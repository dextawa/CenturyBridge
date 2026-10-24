package top.dext.centurybridge.step.v26_1.compat.fapi.client.rendering.v1;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.Block;
import top.dext.centurybridge.step.v26_1.facade.BlockColor;
import top.dext.centurybridge.step.v26_1.rt.CbFapiBlockColors;

/**
 * 1.21.11's net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry, which 26.1's Fabric API replaced with
 * BlockColorRegistry.register(List&lt;BlockTintSource&gt;, Block...). BLOCK registers a 1.21.11 BlockColor as the tint
 * source list rt/CbFapiBlockColors converts it to, and answers a block's colors as a BlockColor.
 */
@Environment(EnvType.CLIENT)
public interface ColorProviderRegistry<T, Provider> {
    ColorProviderRegistry<Block, BlockColor> BLOCK = new ColorProviderRegistry<>() {
        @Override
        public void register(BlockColor provider, Block... objects) {
            CbFapiBlockColors.registerProvider(provider, objects);
        }

        @Override
        public BlockColor get(Block object) {
            return CbFapiBlockColors.getProvider(object);
        }
    };

    @SuppressWarnings("unchecked")
    void register(Provider provider, T... objects);

    Provider get(T object);
}
