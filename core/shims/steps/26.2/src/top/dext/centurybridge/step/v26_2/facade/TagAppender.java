package top.dext.centurybridge.step.v26_2.facade;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.fabric.impl.datagen.TagBuilderHooks;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import top.dext.centurybridge.step.v26_2.facade.fapi.FabricTagAppender;

/**
 * 26.1.2's net.minecraft.data.tags.TagAppender&lt;E, T&gt; (javap of 26.1.2): an appender of elements E into a tag of T's
 * registry. 26.2 kept the name for another interface, TagAppender&lt;T&gt;, whose elements are always ResourceKey&lt;T&gt;
 * (add / addOptional take a ResourceKey, map() is gone), so every 26.1.2 add(Object) / addOptional(Object) / map is a
 * NoSuchMethodError on 26.2. A 26.1.2 mod's references are renamed here (classRenames, s26g-tags.json); the providers
 * that answered it (26.1.2's KeyTagProvider / IntrinsicHolderTagsProvider / HolderTagProvider.tag, Fabric API's
 * FabricTagsProvider.builder / valueLookupBuilder) are restored answering this type (mixin/CbS26TagsProviderRestoredMixin,
 * CbS26FapiTagsProviderRestoredMixin, the provider facades).
 * <p>The bodies are 26.1.2's: {@link #forBuilder} is TagAppender$1 (each call one entry of the tag's raw TagBuilder,
 * which 26.2's TagsProvider.run writes as 26.1.2's did), {@link #map} is TagAppender$2 (the element converted, then
 * handed to the original appender).
 * <p>26.1.2's Fabric API made this interface a FabricTagAppender (TagAppenderMixin, inject-interface) and implemented its
 * members on both appenders (TagAppenderMixin$TagAppender1Mixin / TagAppender2Mixin, fabric-data-generation-api-v1
 * 24.3.4): setReplace / forceAddTag / remove* on the raw builder's TagBuilderHooks (26.2's Fabric API still applies them
 * to every TagBuilder and its TagsProviderMixin writes the removals), the mapped appender delegating with the element
 * converted. Those bodies are here too, and so is Fabric API's one fix of vanilla: 26.1.2's TagAppender$2.addOptional
 * called the original's add (a required entry); Fabric API's TagAppender2Mixin.fixAddOptional wraps that call into
 * addOptional, which is what every 26.1.2 Fabric game ran.
 */
public interface TagAppender<E, T> extends FabricTagAppender<E, T> {

    TagAppender<E, T> add(E element);

    @SuppressWarnings("unchecked")
    default TagAppender<E, T> add(E... elements) {
        return this.addAll(Arrays.stream(elements));
    }

    default TagAppender<E, T> addAll(Collection<E> elements) {
        elements.forEach(this::add);
        return this;
    }

    default TagAppender<E, T> addAll(Stream<E> elements) {
        elements.forEach(this::add);
        return this;
    }

    TagAppender<E, T> addOptional(E element);

    TagAppender<E, T> addTag(TagKey<T> tag);

    TagAppender<E, T> addOptionalTag(TagKey<T> tag);

    static <T> TagAppender<ResourceKey<T>, T> forBuilder(TagBuilder builder) {
        return new TagAppender<ResourceKey<T>, T>() {
            @Override
            public TagAppender<ResourceKey<T>, T> add(ResourceKey<T> key) {
                builder.addElement(key.identifier());
                return this;
            }

            @Override
            public TagAppender<ResourceKey<T>, T> addOptional(ResourceKey<T> key) {
                builder.addOptionalElement(key.identifier());
                return this;
            }

            @Override
            public TagAppender<ResourceKey<T>, T> addTag(TagKey<T> tag) {
                builder.addTag(tag.location());
                return this;
            }

            @Override
            public TagAppender<ResourceKey<T>, T> addOptionalTag(TagKey<T> tag) {
                builder.addOptionalTag(tag.location());
                return this;
            }

            // Fabric API's TagAppender1Mixin

            @Override
            public TagAppender<ResourceKey<T>, T> setReplace(boolean replace) {
                ((TagBuilderHooks) builder).fabric_setReplace(replace);
                return this;
            }

            @Override
            public TagAppender<ResourceKey<T>, T> forceAddTag(TagKey<T> tag) {
                ((TagBuilderHooks) builder).fabric_forceAddTag(tag.location());
                return this;
            }

            @Override
            public TagAppender<ResourceKey<T>, T> remove(ResourceKey<T> key) {
                ((TagBuilderHooks) builder).fabric_removeElement(key.identifier());
                return this;
            }

            @Override
            @SafeVarargs
            public final TagAppender<ResourceKey<T>, T> remove(ResourceKey<T>... keys) {
                return this.removeAll(Arrays.stream(keys));
            }

            @Override
            public TagAppender<ResourceKey<T>, T> removeAll(Collection<ResourceKey<T>> keys) {
                keys.forEach(key -> ((TagBuilderHooks) builder).fabric_removeElement(key.identifier()));
                return this;
            }

            @Override
            public TagAppender<ResourceKey<T>, T> removeAll(Stream<ResourceKey<T>> keys) {
                keys.forEach(key -> ((TagBuilderHooks) builder).fabric_removeElement(key.identifier()));
                return this;
            }

            @Override
            public TagAppender<ResourceKey<T>, T> removeTag(TagKey<T> tag) {
                ((TagBuilderHooks) builder).fabric_removeTag(tag.location());
                return this;
            }
        };
    }

    default <U> TagAppender<U, T> map(Function<U, E> converter) {
        TagAppender<E, T> original = this;
        Objects.requireNonNull(converter);
        return new TagAppender<U, T>() {
            @Override
            public TagAppender<U, T> add(U element) {
                original.add(converter.apply(element));
                return this;
            }

            /** Fabric API's fixAddOptional: the original's addOptional, not 26.1.2 vanilla's add */
            @Override
            public TagAppender<U, T> addOptional(U element) {
                original.addOptional(converter.apply(element));
                return this;
            }

            @Override
            public TagAppender<U, T> addTag(TagKey<T> tag) {
                original.addTag(tag);
                return this;
            }

            @Override
            public TagAppender<U, T> addOptionalTag(TagKey<T> tag) {
                original.addOptionalTag(tag);
                return this;
            }

            // Fabric API's TagAppender2Mixin

            @Override
            public TagAppender<U, T> setReplace(boolean replace) {
                original.setReplace(replace);
                return this;
            }

            @Override
            public TagAppender<U, T> forceAddTag(TagKey<T> tag) {
                original.forceAddTag(tag);
                return this;
            }

            @Override
            public TagAppender<U, T> remove(U element) {
                original.remove(converter.apply(element));
                return this;
            }

            @Override
            @SafeVarargs
            public final TagAppender<U, T> remove(U... elements) {
                return this.removeAll(Arrays.stream(elements));
            }

            @Override
            public TagAppender<U, T> removeAll(Collection<U> elements) {
                elements.forEach(element -> original.remove(converter.apply(element)));
                return this;
            }

            @Override
            public TagAppender<U, T> removeAll(Stream<U> elements) {
                elements.forEach(element -> original.remove(converter.apply(element)));
                return this;
            }

            @Override
            public TagAppender<U, T> removeTag(TagKey<T> tag) {
                original.removeTag(tag);
                return this;
            }
        };
    }
}
