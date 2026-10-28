package top.dext.centurybridge.step.v26_2.facade;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;

/**
 * 26.1.2's net.minecraft.data.tags.KeyTagProvider (javap), which 26.2 deleted: its subclasses extend TagsProvider, whose
 * own tag(TagKey) / tag(TagKey, boolean) now answer 26.2's TagAppender. A 26.1.2 mod's references are renamed here
 * (classRenames, s26g-tags.json). The constructor is 26.1.2's; tag(TagKey) and tag(TagKey, boolean), answering the 26.1.2
 * TagAppender of keys (facade/TagAppender), are the ones CbS26TagsProviderRestoredMixin restores on TagsProvider with
 * 26.1.2's bodies (javac cannot declare them here beside 26.2's inherited ones of another return type).
 */
public abstract class KeyTagProvider<T> extends TagsProvider<T> {

    protected KeyTagProvider(PackOutput output, ResourceKey<? extends Registry<T>> registryKey,
            CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, registryKey, lookupProvider);
    }
}
