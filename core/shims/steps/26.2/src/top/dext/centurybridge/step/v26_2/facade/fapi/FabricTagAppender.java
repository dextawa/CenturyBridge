package top.dext.centurybridge.step.v26_2.facade.fapi;

import java.util.Collection;
import java.util.stream.Stream;
import net.minecraft.tags.TagKey;
import top.dext.centurybridge.step.v26_2.facade.TagAppender;

/**
 * Fabric API 0.155.3+26.1.2's FabricTagAppender&lt;E, T&gt; (fabric-data-generation-api-v1 24.3.4, javap), the interface its
 * TagAppenderMixin injected into vanilla's TagAppender. 26.2's Fabric API kept the name for FabricTagAppender&lt;T&gt;, whose
 * remove takes a ResourceKey and whose members answer 26.2's TagAppender, so a 26.1.2 mod's calls no longer link. Its
 * references are renamed here (classRenames, s26g-tags.json), with 26.1.2's TagAppender's (facade/TagAppender, which
 * extends this as the injection made vanilla's do). The defaults are 26.1.2's: AssertionError("Implemented via mixin");
 * both appenders of facade/TagAppender implement every member, as Fabric API's mixins did.
 */
public interface FabricTagAppender<E, T> {

    default TagAppender<E, T> setReplace(boolean replace) {
        throw new AssertionError("Implemented via mixin");
    }

    default TagAppender<E, T> forceAddTag(TagKey<T> tag) {
        throw new AssertionError("Implemented via mixin");
    }

    default TagAppender<E, T> remove(E element) {
        throw new AssertionError("Implemented via mixin");
    }

    @SuppressWarnings("unchecked")
    default TagAppender<E, T> remove(E... elements) {
        throw new AssertionError("Implemented via mixin");
    }

    default TagAppender<E, T> removeAll(Collection<E> elements) {
        throw new AssertionError("Implemented via mixin");
    }

    default TagAppender<E, T> removeAll(Stream<E> elements) {
        throw new AssertionError("Implemented via mixin");
    }

    default TagAppender<E, T> removeTag(TagKey<T> tag) {
        throw new AssertionError("Implemented via mixin");
    }
}
