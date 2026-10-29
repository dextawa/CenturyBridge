package top.dext.centurybridge.step.v26_2.facade;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import top.dext.centurybridge.step.v26_2.rt.CbS26TagProviders;

/**
 * 26.1.2's net.minecraft.data.tags.HolderTagProvider (javap), which 26.2 deleted (PotionTagsProvider extends TagsProvider
 * now). A 26.1.2 mod's references are renamed here (classRenames, s26g-tags.json). The constructor is 26.1.2's;
 * tag(TagKey), answering the 26.1.2 TagAppender of holder references (facade/TagAppender), is the one
 * CbS26TagsProviderRestoredMixin restores on TagsProvider with 26.1.2's body,
 * TagAppender.forBuilder(getOrCreateRawBuilder(tag)).map(Holder.Reference::key), the conversion taken through
 * {@link CbS26TagProviders.Keyed}.
 */
public abstract class HolderTagProvider<T> extends TagsProvider<T> implements CbS26TagProviders.Keyed {

    protected HolderTagProvider(PackOutput output, ResourceKey<? extends Registry<T>> registryKey,
            CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, registryKey, lookupProvider);
    }

    @Override
    public Function<?, ?> cb$v26_2$elementKey() {
        return (Function<Holder.Reference<T>, ResourceKey<T>>) Holder.Reference::key;
    }
}
