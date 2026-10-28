package top.dext.centurybridge.step.v1_21_6.facade;

import java.util.List;
import net.minecraft.class_2960;
import net.minecraft.class_3495;
import net.minecraft.class_5321;
import net.minecraft.class_6862;

/**
 * 1.21.5's TagsProvider.TagAppender (net/minecraft/class_2474$class_5124), which 1.21.6 removed. Its datagen tag API is
 * now the interface TagAppender (class_11389: forBuilder / map, KeyTagProvider.tag), with other names and return types.
 * A 1.21.5 mod's references are renamed here (classRenames, s6h-tags.json). The bodies are 1.21.5's (javap): each call
 * adds one entry to the provider's raw TagBuilder (class_3495, unchanged in 1.21.6), which 1.21.6's TagsProvider.run
 * writes exactly as 1.21.5's did.
 * <p>Differences from 1.21.5's declaration, for the climbed callers: the constructor is public (1.21.5: protected; the
 * layer's restored TagsProvider.tag builds it from another package). The builder field is public, as Fabric API's access
 * widener made it for FabricTagBuilder. The add methods are not final, since Fabric API's access widener made them
 * extendable and its FabricTagBuilder overrides them (facade/fapi/FabricTagBuilder).
 */
public class TagAppender<T> {

    /** builder */
    public final class_3495 field_23960;

    public TagAppender(class_3495 builder) {
        this.field_23960 = builder;
    }

    /** add(ResourceKey) */
    public TagAppender<T> method_46835(class_5321<T> key) {
        this.field_23960.method_26784(key.method_29177());
        return this;
    }

    /** add(ResourceKey...) */
    @SuppressWarnings("unchecked")
    public TagAppender<T> method_40565(class_5321<T>... keys) {
        for (class_5321<T> key : keys) {
            this.field_23960.method_26784(key.method_29177());
        }
        return this;
    }

    /** addAll(List) */
    public final TagAppender<T> method_55842(List<class_5321<T>> keys) {
        for (class_5321<T> key : keys) {
            this.field_23960.method_26784(key.method_29177());
        }
        return this;
    }

    /** addOptional(ResourceLocation) */
    public TagAppender<T> method_35922(class_2960 id) {
        this.field_23960.method_34891(id);
        return this;
    }

    /** addTag(TagKey) */
    public TagAppender<T> method_26792(class_6862<T> tag) {
        this.field_23960.method_26787(tag.comp_327());
        return this;
    }

    /** addOptionalTag(ResourceLocation) */
    public TagAppender<T> method_35923(class_2960 id) {
        this.field_23960.method_34892(id);
        return this;
    }
}
