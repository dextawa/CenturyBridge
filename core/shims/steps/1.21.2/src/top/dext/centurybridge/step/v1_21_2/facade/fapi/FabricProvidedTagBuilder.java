package top.dext.centurybridge.step.v1_21_2.facade.fapi;

import net.minecraft.class_5321;
import net.minecraft.class_6862;

/**
 * Fabric API 0.116's FabricProvidedTagBuilder (classRenames; dev-time datagen), gone in 0.106. 0.116 injected it into
 * TagsProvider.TagAppender and implemented it on its FabricTagBuilder; CbItemTagAppenderMixin implements it on 1.21.2's
 * TagAppender the same way (a FabricTagBuilder answers, any other appender fails as 0.116's did).
 */
public interface FabricProvidedTagBuilder<T> {

    default FabricProvidedTagBuilder<T> setReplace(boolean replace) {
        throw new AssertionError("Implemented via mixin");
    }

    default FabricProvidedTagBuilder<T> forceAddTag(class_6862<T> tag) {
        throw new AssertionError("Implemented via mixin");
    }

    default FabricProvidedTagBuilder<T> remove(class_5321<T> key) {
        throw new AssertionError("Implemented via mixin");
    }

    @SuppressWarnings("unchecked")
    default FabricProvidedTagBuilder<T> remove(class_5321<T>... keys) {
        throw new AssertionError("Implemented via mixin");
    }

    default FabricProvidedTagBuilder<T> removeAll(java.util.Collection<class_5321<T>> keys) {
        throw new AssertionError("Implemented via mixin");
    }

    default FabricProvidedTagBuilder<T> removeAll(java.util.stream.Stream<class_5321<T>> keys) {
        throw new AssertionError("Implemented via mixin");
    }

    default FabricProvidedTagBuilder<T> removeTag(class_6862<T> tag) {
        throw new AssertionError("Implemented via mixin");
    }
}
