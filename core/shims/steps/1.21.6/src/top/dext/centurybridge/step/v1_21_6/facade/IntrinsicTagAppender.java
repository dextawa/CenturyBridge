package top.dext.centurybridge.step.v1_21_6.facade;

import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.class_3495;
import net.minecraft.class_5321;
import net.minecraft.class_6862;

/**
 * 1.21.5's IntrinsicHolderTagsProvider.IntrinsicTagAppender (net/minecraft/class_7889$class_7890), which 1.21.6 removed:
 * its IntrinsicHolderTagsProvider.tag answers the interface TagAppender&lt;T, T&gt; (class_11389) instead. A 1.21.5 mod's
 * references are renamed here (classRenames, s6h-tags.json). The bodies are 1.21.5's (javap): a value is added as the key
 * the provider's key extractor gives it, into the same raw TagBuilder. The constructor is public (1.21.5: package-private;
 * the layer's restored IntrinsicHolderTagsProvider.tag builds it from another package), and the key extractor field is
 * public (1.21.5: private; access wideners never reach a CenturyBridge class).
 */
public class IntrinsicTagAppender<T> extends TagAppender<T> {

    /** keyExtractor */
    public final Function<T, class_5321<T>> field_40955;

    public IntrinsicTagAppender(class_3495 builder, Function<T, class_5321<T>> keyExtractor) {
        super(builder);
        this.field_40955 = keyExtractor;
    }

    /** addTag(TagKey), 1.21.5's covariant override */
    public IntrinsicTagAppender<T> method_46828(class_6862<T> tag) {
        super.method_26792(tag);
        return this;
    }

    /** add(T) */
    public final IntrinsicTagAppender<T> method_46829(T value) {
        this.method_46835(this.field_40955.apply(value));
        return this;
    }

    /** add(T...) */
    @SafeVarargs
    public final IntrinsicTagAppender<T> method_46830(T... values) {
        Stream.of(values).map(this.field_40955).forEach(this::method_46835);
        return this;
    }

    /** addTag(TagKey): 1.21.5's bridge to the covariant override */
    @Override
    public TagAppender<T> method_26792(class_6862<T> tag) {
        return this.method_46828(tag);
    }
}
