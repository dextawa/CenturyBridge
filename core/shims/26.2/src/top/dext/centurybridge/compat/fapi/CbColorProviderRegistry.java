package top.dext.centurybridge.compat.fapi;

import java.util.IdentityHashMap;
import java.util.Map;

import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import top.dext.centurybridge.facade.v26_2.class_322;
import top.dext.centurybridge.facade.v26_2.class_326;

/**
 * fabric-rendering-v1's ColorProviderRegistry as 1.20.x mods call it (19 corpus
 * mods): {@code BLOCK.register(provider, blocks...)} / {@code ITEM.register(...)}.
 * 26.x: block tints are BlockTintSource lists per block (BlockColorRegistry
 * takes a BlockTintsFactory that fills one colour per tint index), so each old
 * per-index callback is asked for indices 0..7 -- every 1.20.1 model tintindex
 * in the corpus is below that. Item tints have had no runtime registry since
 * 1.21.4 (item model definition JSON); ITEM keeps the providers, and the
 * centurybridge:legacy_color tint the converter writes into a mod's item model
 * definitions answers from them (CbLegacyItemModels). Stays an interface: the
 * corpus calls it through INVOKEINTERFACE.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbColorProviderRegistry<T, Provider> {

    CbColorProviderRegistry<ItemLike, class_326> ITEM = new Items();
    CbColorProviderRegistry<Block, class_322> BLOCK = new Blocks();

    /** erased exactly as 1.20.1: register(Object, Object[]) */
    @SuppressWarnings("unchecked")
    void register(Provider provider, T... objects);

    Provider get(T object);

    final class Blocks implements CbColorProviderRegistry<Block, class_322> {
        private final Map<Block, class_322> providers = new IdentityHashMap<>();

        @Override
        public void register(class_322 provider, Block... blocks) {
            synchronized (providers) {
                for (Block b : blocks) {
                    providers.put(b, provider);
                }
            }
            BlockColorRegistry.register((state, level, pos, out) -> {
                for (int i = 0; i < 8; i++) {
                    out.add(provider.getColor(state, level, pos, i));
                }
            }, blocks);
        }

        @Override
        public class_322 get(Block block) {
            synchronized (providers) {
                return providers.get(block);
            }
        }
    }

    final class Items implements CbColorProviderRegistry<ItemLike, class_326> {
        private final Map<Item, class_326> providers = new IdentityHashMap<>();

        @Override
        public void register(class_326 provider, ItemLike... items) {
            synchronized (providers) {
                for (ItemLike i : items) {
                    providers.put(i.asItem(), provider);
                }
            }
        }

        @Override
        public class_326 get(ItemLike item) {
            synchronized (providers) {
                return providers.get(item.asItem());
            }
        }
    }
}
