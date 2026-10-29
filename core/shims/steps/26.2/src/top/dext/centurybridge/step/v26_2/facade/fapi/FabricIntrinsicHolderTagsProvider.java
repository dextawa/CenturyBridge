package top.dext.centurybridge.step.v26_2.facade.fapi;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import top.dext.centurybridge.step.v26_2.rt.CbS26TagProviders;

/**
 * Fabric API 0.155.3+26.1.2's FabricTagsProvider.FabricIntrinsicHolderTagsProvider (fabric-data-generation-api-v1 24.3.4,
 * javap), which 26.2's Fabric API deleted: its Block / Item / Fluid / EntityType / BlockEntityType providers extend
 * FabricTagsProvider directly. A 26.1.2 mod's references are renamed here (classRenames, s26g-tags.json). The constructor
 * and valueToKey are 26.1.2's; valueLookupBuilder(TagKey), answering the 26.1.2 TagAppender of elements
 * (facade/TagAppender), is the one CbS26FapiTagsProviderRestoredMixin restores on FabricTagsProvider with 26.1.2's body,
 * the conversion taken through {@link CbS26TagProviders.Keyed}.
 */
public abstract class FabricIntrinsicHolderTagsProvider<T> extends FabricTagsProvider<T> implements CbS26TagProviders.Keyed {
    private final Function<T, ResourceKey<T>> valueToKey;

    protected FabricIntrinsicHolderTagsProvider(FabricPackOutput output, ResourceKey<? extends Registry<T>> registryKey,
            CompletableFuture<HolderLookup.Provider> registriesFuture, Function<T, ResourceKey<T>> valueToKey) {
        super(output, registryKey, registriesFuture);
        this.valueToKey = valueToKey;
    }

    @Override
    public Function<?, ?> cb$v26_2$elementKey() {
        return this.valueToKey;
    }
}
