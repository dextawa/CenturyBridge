package top.dext.centurybridge.step.v1_21_6.facade.fapi;

import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_6862;
import top.dext.centurybridge.step.v1_21_6.facade.TagAppender;
import top.dext.centurybridge.step.v1_21_6.rt.CbFapiTags;

/**
 * Fabric API 0.128.2+1.21.5's FabricTagProvider.FabricTagBuilder (an inner class of FabricTagProvider, extending vanilla's
 * TagsProvider.TagAppender), which Fabric API 0.128.2+1.21.6 removed along with that class: its tag builders are vanilla's
 * TagAppender interface (class_11389) with FabricProvidedTagBuilder injected. A 1.21.5 mod's references are renamed here
 * (classRenames, s6h-fapitags.json). It extends the layer's TagAppender facade, as 1.21.5's extended vanilla's class.
 * The bodies are 0.128.2+1.21.5's (javap of fabric-data-generation-api-v1 22.5.2), with two changes:
 * <ul>
 * <li>add(T) asks the provider's restored reverseLookup (CbFapiTagProviderRestoredMixin) through {@link CbFapiTags.Provider};</li>
 * <li>forceAddTag hands the tag to 1.21.6's TagBuilder.fabric_forceAddTag. 1.21.5 added a ForcedTagEntry wrapping
 *     TagEntry.tag(id), which is (id, tag, required). 1.21.6's ForcedTagEntry(id) is the same three values, and the
 *     same entry is written.</li>
 * </ul>
 * setReplace sets the flag on the raw builder (Fabric API's TagBuilderMixin, in both releases).
 */
public final class FabricTagBuilder<T> extends TagAppender<T> {

    public final TagAppender<T> parent;

    private final CbFapiTags.Provider provider;

    public FabricTagBuilder(CbFapiTags.Provider provider, TagAppender<T> parent) {
        super(parent.field_23960);
        this.provider = provider;
        this.parent = parent;
    }

    public FabricTagBuilder<T> setReplace(boolean replace) {
        ((net.fabricmc.fabric.impl.datagen.FabricTagBuilder) this.field_23960).fabric_setReplace(replace);
        return this;
    }

    @SuppressWarnings("unchecked")
    public FabricTagBuilder<T> add(T element) {
        return this.add((class_5321<T>) this.provider.cb$v1_21_6$reverseLookup(element));
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final FabricTagBuilder<T> add(T... elements) {
        for (T element : elements) {
            this.add((class_5321<T>) this.provider.cb$v1_21_6$reverseLookup(element));
        }
        return this;
    }

    public FabricTagBuilder<T> add(class_5321<T> key) {
        this.parent.method_46835(key);
        return this;
    }

    public FabricTagBuilder<T> add(class_2960 id) {
        this.field_23960.method_26784(id);
        return this;
    }

    public FabricTagBuilder<T> addOptional(class_2960 id) {
        this.parent.method_35922(id);
        return this;
    }

    public FabricTagBuilder<T> addOptional(class_5321<? extends T> key) {
        return this.addOptional(key.method_29177());
    }

    public FabricTagBuilder<T> addTag(class_6862<T> tag) {
        this.field_23960.method_26787(tag.comp_327());
        return this;
    }

    @SafeVarargs
    public final FabricTagBuilder<T> addTags(class_6862<T>... tags) {
        for (class_6862<T> tag : tags) {
            this.addTag(tag);
        }
        return this;
    }

    public FabricTagBuilder<T> addOptionalTag(class_2960 id) {
        this.parent.method_35923(id);
        return this;
    }

    public FabricTagBuilder<T> addOptionalTag(class_6862<T> tag) {
        return this.addOptionalTag(tag.comp_327());
    }

    @SafeVarargs
    public final FabricTagBuilder<T> addOptionalTags(class_6862<T>... tags) {
        for (class_6862<T> tag : tags) {
            this.addOptionalTag(tag);
        }
        return this;
    }

    public FabricTagBuilder<T> forceAddTag(class_6862<T> tag) {
        ((net.fabricmc.fabric.impl.datagen.FabricTagBuilder) this.field_23960).fabric_forceAddTag(tag.comp_327());
        return this;
    }

    @SafeVarargs
    public final FabricTagBuilder<T> forceAddTags(class_6862<T>... tags) {
        for (class_6862<T> tag : tags) {
            this.forceAddTag(tag);
        }
        return this;
    }

    public FabricTagBuilder<T> add(class_2960... ids) {
        for (class_2960 id : ids) {
            this.add(id);
        }
        return this;
    }

    @SafeVarargs
    public final FabricTagBuilder<T> add(class_5321<T>... keys) {
        for (class_5321<T> key : keys) {
            this.add(key);
        }
        return this;
    }

    /** addOptionalTag(ResourceLocation) as vanilla's TagAppender method */
    @Override
    public TagAppender<T> method_35923(class_2960 id) {
        return this.addOptionalTag(id);
    }

    /** addTag(TagKey) as vanilla's TagAppender method */
    @Override
    public TagAppender<T> method_26792(class_6862<T> tag) {
        return this.addTag(tag);
    }

    /** addOptional(ResourceLocation) as vanilla's TagAppender method */
    @Override
    public TagAppender<T> method_35922(class_2960 id) {
        return this.addOptional(id);
    }

    /** add(ResourceKey...) as vanilla's TagAppender method */
    @Override
    @SuppressWarnings("unchecked")
    public TagAppender<T> method_40565(class_5321<T>... keys) {
        return this.add(keys);
    }

    /** add(ResourceKey) as vanilla's TagAppender method */
    @Override
    public TagAppender<T> method_46835(class_5321<T> key) {
        return this.add(key);
    }
}
