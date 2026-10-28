package top.dext.centurybridge.step.v26_2.rt;

import java.util.function.Function;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.Holder;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.PotionTagsProvider;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

/**
 * 26.1.2's tag providers that turned an element into its key before it reached the tag's raw builder: vanilla's
 * IntrinsicHolderTagsProvider (a key extractor given to its constructor) and HolderTagProvider (a holder reference's key),
 * and Fabric API's FabricTagsProvider.FabricIntrinsicHolderTagsProvider (its valueToKey). 26.2 deleted the three and its
 * providers take keys only. The providers restored with 26.1.2's tag / valueLookupBuilder
 * (CbS26TagsProviderRestoredMixin, CbS26FapiTagsProviderRestoredMixin) ask the provider for its conversion here:
 * the layer's facades of the deleted classes, and the 26.2 classes that 26.1.2 derived from them
 * (CbS26TagsProviderKeyedMixin), answer {@link Keyed}; any other provider is one of keys (26.1.2's KeyTagProvider and
 * FabricTagsProvider.builder), and its appender is the raw builder's own.
 */
public final class CbS26TagProviders {
    private CbS26TagProviders() {
    }

    /** a provider whose 26.1.2 appender converted each element to its key first */
    public interface Keyed {
        Function<?, ?> cb$v26_2$elementKey();
    }

    /** the conversion of a provider, null for one of keys */
    public static Function<?, ?> elementKey(Object provider) {
        return provider instanceof Keyed keyed ? keyed.cb$v26_2$elementKey() : null;
    }

    /**
     * The conversion 26.1.2 gave each 26.2 class that it derived from IntrinsicHolderTagsProvider / HolderTagProvider /
     * FabricIntrinsicHolderTagsProvider (javap of 26.1.2's constructors and their lambda$new$0): the element's built-in
     * registry holder's key, or the holder reference's key for PotionTagsProvider (HolderTagProvider's
     * Holder.Reference::key).
     */
    public static Function<?, ?> builtInKey(Object provider) {
        if (provider instanceof EntityTypeTagsProvider || provider instanceof FabricTagsProvider.EntityTypeTagsProvider) {
            return (Function<EntityType<?>, ResourceKey<EntityType<?>>>) type -> type.builtInRegistryHolder().key();
        }
        if (provider instanceof FluidTagsProvider || provider instanceof FabricTagsProvider.FluidTagsProvider) {
            return (Function<Fluid, ResourceKey<Fluid>>) fluid -> fluid.builtInRegistryHolder().key();
        }
        if (provider instanceof VanillaBlockTagsProvider || provider instanceof FabricTagsProvider.BlockTagsProvider) {
            return (Function<Block, ResourceKey<Block>>) block -> block.builtInRegistryHolder().key();
        }
        if (provider instanceof VanillaItemTagsProvider || provider instanceof FabricTagsProvider.ItemTagsProvider) {
            return (Function<Item, ResourceKey<Item>>) item -> item.builtInRegistryHolder().key();
        }
        if (provider instanceof FabricTagsProvider.BlockEntityTypeTagsProvider) {
            return (Function<BlockEntityType<?>, ResourceKey<BlockEntityType<?>>>) type -> type.builtInRegistryHolder().key();
        }
        if (provider instanceof PotionTagsProvider) {
            return (Function<Holder.Reference<?>, ResourceKey<?>>) Holder.Reference::key;
        }
        throw new IllegalStateException("not a 26.1.2 intrinsic provider: " + provider.getClass().getName());
    }
}
