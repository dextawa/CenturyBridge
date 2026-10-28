package top.dext.centurybridge.step.v26_2.mixin;

import java.util.function.Function;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_2.facade.TagAppender;
import top.dext.centurybridge.step.v26_2.rt.CbS26TagProviders;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * Fabric API 0.155.3+26.1.2's FabricTagsProvider.builder(TagKey) and FabricIntrinsicHolderTagsProvider.valueLookupBuilder
 * (TagKey), answering the 26.1.2 TagAppender (facade/TagAppender), restored on FabricTagsProvider with 26.1.2's bodies
 * (javap of fabric-data-generation-api-v1 24.3.4): TagAppender.forBuilder(getOrCreateRawBuilder(tag)), mapped by the
 * provider's valueToKey for valueLookupBuilder. 26.2's builder answers 26.2's TagAppender (BlockItemTagAppender for blocks
 * and items) and valueLookupBuilder is gone with FabricIntrinsicHolderTagsProvider; a 26.1.2 mod's calls name these
 * descriptors. The conversion is asked through CbS26TagProviders.Keyed (facade/fapi/FabricIntrinsicHolderTagsProvider,
 * and Fabric API's five providers 26.1.2 derived from it: CbS26TagsProviderKeyedMixin). The mixin extends TagsProvider
 * for its protected getOrCreateRawBuilder (a shadow does not climb the hierarchy). Priority below every mod's.
 */
@Mixin(value = FabricTagsProvider.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26FapiTagsProviderRestoredMixin<T> extends TagsProvider<T> {

    private CbS26FapiTagsProviderRestoredMixin(PackOutput output) {
        super(output, null, null);
    }

    /** builder(TagKey) */
    protected TagAppender<ResourceKey<T>, T> builder(TagKey<T> tag) {
        TagBuilder builder = this.getOrCreateRawBuilder(tag);
        return TagAppender.forBuilder(builder);
    }

    /**
     * FabricIntrinsicHolderTagsProvider.valueLookupBuilder(TagKey). 26.1.2 declared it there only, so a call on any
     * other provider could not have linked: it fails as the link would have (NoSuchMethodError).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected TagAppender valueLookupBuilder(TagKey<T> tag) {
        Function elementKey = CbS26TagProviders.elementKey(this);
        if (elementKey == null) {
            throw new NoSuchMethodError("valueLookupBuilder on " + this.getClass().getName());
        }
        TagBuilder builder = this.getOrCreateRawBuilder(tag);
        return TagAppender.forBuilder(builder).map(elementKey);
    }
}
