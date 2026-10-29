package top.dext.centurybridge.step.v26_2.mixin;

import java.util.function.Function;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v26_2.facade.TagAppender;
import top.dext.centurybridge.step.v26_2.rt.CbS26TagProviders;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * 26.1.2's tag(TagKey) / tag(TagKey, boolean) answering its TagAppender&lt;E, T&gt; (facade/TagAppender), restored on
 * TagsProvider. 26.1.2 declared them on the three providers 26.2 deleted, with these bodies (javap):
 * <ul>
 * <li>KeyTagProvider.tag(TagKey): TagAppender.forBuilder(getOrCreateRawBuilder(tag));</li>
 * <li>KeyTagProvider.tag(TagKey, boolean): the same over getOrCreateRawBuilder(tag).setReplace(replace);</li>
 * <li>IntrinsicHolderTagsProvider.tag(TagKey): forBuilder(...).map(keyExtractor);</li>
 * <li>HolderTagProvider.tag(TagKey): forBuilder(...).map(Holder.Reference::key).</li>
 * </ul>
 * One provider kind per class, so one body: the conversion of a provider that converted is asked through
 * CbS26TagProviders.Keyed (the facades of the deleted classes, and the 26.2 classes 26.1.2 derived from them:
 * CbS26TagsProviderKeyedMixin); a provider of keys hands back the builder's appender itself (not a mapped one: 26.1.2's
 * KeyTagProvider did not map). Here on TagsProvider, a 26.1.2 mod's call reaches them through any subclass -- its own
 * provider over a facade, or a 26.2 class such as BiomeTagsProvider that 26.1.2 derived from KeyTagProvider -- and its
 * override overrides them. 26.2's own tag(TagKey) / tag(TagKey, boolean) answer 26.2's TagAppender (another descriptor)
 * and stay as they are; nothing in 26.2 calls these. Priority below every mod's.
 */
@Mixin(value = TagsProvider.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26TagsProviderRestoredMixin<T> {

    @Shadow
    protected abstract TagBuilder getOrCreateRawBuilder(TagKey<T> tag);

    /** tag(TagKey): KeyTagProvider's, IntrinsicHolderTagsProvider's, HolderTagProvider's */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected TagAppender tag(TagKey<T> tag) {
        TagBuilder builder = this.getOrCreateRawBuilder(tag);
        TagAppender<ResourceKey<T>, T> appender = TagAppender.forBuilder(builder);
        Function elementKey = CbS26TagProviders.elementKey(this);
        return elementKey == null ? appender : appender.map(elementKey);
    }

    /** tag(TagKey, boolean): KeyTagProvider's */
    protected TagAppender<ResourceKey<T>, T> tag(TagKey<T> tag, boolean replace) {
        TagBuilder builder = this.getOrCreateRawBuilder(tag).setReplace(replace);
        return TagAppender.forBuilder(builder);
    }
}
