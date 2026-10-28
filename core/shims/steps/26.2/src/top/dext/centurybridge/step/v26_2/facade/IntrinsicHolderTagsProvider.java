package top.dext.centurybridge.step.v26_2.facade;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import top.dext.centurybridge.step.v26_2.rt.CbS26TagProviders;

/**
 * 26.1.2's net.minecraft.data.tags.IntrinsicHolderTagsProvider (javap), which 26.2 deleted: its subclasses extend
 * TagsProvider directly and add keys only. A 26.1.2 mod's references (and every climbed layer facade extending it: the
 * 1.21.6 layer's ItemTagsProvider, the 1.21.4 layer's WinterDrop*TagsProvider, the 1.21 layer's class_8907 / class_8997)
 * are renamed here (classRenames, s26g-tags.json). The constructors and the key extractor are 26.1.2's. tag(TagKey),
 * answering the 26.1.2 TagAppender (facade/TagAppender) of elements, is the one CbS26TagsProviderRestoredMixin restores
 * on TagsProvider: javac cannot declare it here beside 26.2's inherited tag(TagKey) of another return type, and with it
 * on TagsProvider a mod's call reaches it the same through every subclass. Its body is 26.1.2's
 * (TagAppender.forBuilder(getOrCreateRawBuilder(tag)).map(keyExtractor)), the extractor taken through
 * {@link CbS26TagProviders.Keyed}.
 * <p>The key extractor keeps 26.1.2's private access and name: the 1.21.6 layer's CbIntrinsicTagsRestoredMixin, climbed
 * here and retargeted onto this class, shadows it.
 */
public abstract class IntrinsicHolderTagsProvider<T> extends TagsProvider<T> implements CbS26TagProviders.Keyed {
    private final Function<T, ResourceKey<T>> keyExtractor;

    public IntrinsicHolderTagsProvider(PackOutput output, ResourceKey<? extends Registry<T>> registryKey,
            CompletableFuture<HolderLookup.Provider> lookupProvider, Function<T, ResourceKey<T>> keyExtractor) {
        super(output, registryKey, lookupProvider);
        this.keyExtractor = keyExtractor;
    }

    public IntrinsicHolderTagsProvider(PackOutput output, ResourceKey<? extends Registry<T>> registryKey,
            CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<T>> parentProvider,
            Function<T, ResourceKey<T>> keyExtractor) {
        super(output, registryKey, lookupProvider, parentProvider);
        this.keyExtractor = keyExtractor;
    }

    @Override
    public Function<?, ?> cb$v26_2$elementKey() {
        return this.keyExtractor;
    }
}
