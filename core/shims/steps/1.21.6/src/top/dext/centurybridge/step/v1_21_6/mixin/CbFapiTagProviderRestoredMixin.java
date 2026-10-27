package top.dext.centurybridge.step.v1_21_6.mixin;

import java.util.Optional;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.class_2378;
import net.minecraft.class_2474;
import net.minecraft.class_5321;
import net.minecraft.class_6862;
import net.minecraft.class_7784;
import net.minecraft.class_7923;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_6.facade.TagAppender;
import top.dext.centurybridge.step.v1_21_6.facade.fapi.FabricTagBuilder;
import top.dext.centurybridge.step.v1_21_6.rt.CbFapiTags;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * Fabric API 0.128.2+1.21.5's FabricTagProvider members that 0.128.2+1.21.6 dropped, restored on FabricTagProvider with
 * 1.21.5's bodies (javap of fabric-data-generation-api-v1 22.5.2):
 * <ul>
 * <li>reverseLookup(T): the element's key in its built-in registry (BuiltInRegistries.REGISTRY by the provider's
 *     registry key), else UnsupportedOperationException "Adding objects is not supported by &lt;class&gt;". 1.21.5's
 *     subclasses override it with their holder's key (CbFapi*TagReverseLookupMixin). A 1.21.5 mod's own override answers
 *     through virtual dispatch.</li>
 * <li>getOrCreateTagBuilder(TagKey): a FabricTagBuilder (facade/fapi) over TagsProvider.tag's appender. 1.21.5 called
 *     super.tag(tag), whose body (restored by CbTagsProviderRestoredMixin) is a TagAppender over getOrCreateRawBuilder(tag).
 *     A mixin cannot call the superclass's mixin-added member non-virtually, so that body is inlined. A converted mod's
 *     mixin on TagsProvider.tag therefore does not see Fabric providers' builders being made.</li>
 * <li>tag(TagKey), its override of TagsProvider.tag: getOrCreateTagBuilder.</li>
 * </ul>
 * The mixin extends TagsProvider for its protected members (registry key, raw builders). Priority below every mod's.
 */
@Mixin(value = FabricTagProvider.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbFapiTagProviderRestoredMixin<T> extends class_2474<T> implements CbFapiTags.Provider {

    private CbFapiTagProviderRestoredMixin(class_7784 output) {
        super(output, null, null);
    }

    /** reverseLookup(T) */
    @SuppressWarnings("unchecked")
    protected class_5321<T> reverseLookup(T element) {
        class_2378<T> registry = (class_2378<T>) class_7923.field_41167.method_29107((class_5321) this.field_40957);
        if (registry != null) {
            Optional<class_5321<T>> key = registry.method_29113(element);
            if (key.isPresent()) {
                return key.get();
            }
        }
        throw new UnsupportedOperationException("Adding objects is not supported by " + this.getClass());
    }

    /** getOrCreateTagBuilder(TagKey) */
    protected FabricTagBuilder<T> getOrCreateTagBuilder(class_6862<T> tag) {
        return new FabricTagBuilder<>(this, new TagAppender<>(this.method_27169(tag)));
    }

    /** tag(TagKey), FabricTagProvider's override of TagsProvider.tag */
    protected TagAppender<T> method_10512(class_6862<T> tag) {
        return this.getOrCreateTagBuilder(tag);
    }

    @Override
    @SuppressWarnings("unchecked")
    public class_5321<?> cb$v1_21_6$reverseLookup(Object element) {
        return this.reverseLookup((T) element);
    }
}
