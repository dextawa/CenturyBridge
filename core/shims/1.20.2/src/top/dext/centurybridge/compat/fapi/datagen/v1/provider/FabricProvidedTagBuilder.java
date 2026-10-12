package top.dext.centurybridge.compat.fapi.datagen.v1.provider;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Fabric API data generation's FabricProvidedTagBuilder as 0.92 had it for 1.20.1 (gone from 1.20.2's
 * Fabric API): replacing a tag, forcing a tag reference, and removing elements and tags. In 1.20.1 only
 * FabricTagProvider.FabricTagBuilder implemented it -- no mixin implemented it on vanilla's
 * ProvidedTagBuilder -- so these defaults are what any other implementor had. On 1.20.2 Fabric API's
 * FabricTagBuilder cannot implement this layer's interface; an interface call on one is served by
 * CbFapiTagBuilders (interfaceCallRedirects), a cast to this interface is not.
 */
public interface FabricProvidedTagBuilder {

    default FabricProvidedTagBuilder setReplace(boolean replace) {
        throw new AssertionError("Implemented via mixin");
    }

    default <T> FabricProvidedTagBuilder forceAddTag(net.minecraft.class_6862<T> tag) {
        throw new AssertionError("Implemented via mixin");
    }

    default <T> FabricProvidedTagBuilder remove(net.minecraft.class_5321<T> registryKey) {
        throw new AssertionError("Implemented via mixin");
    }

    @SuppressWarnings("unchecked")
    default <T> FabricProvidedTagBuilder remove(net.minecraft.class_5321<T>... registryKeys) {
        throw new AssertionError("Implemented via mixin");
    }

    default <T> FabricProvidedTagBuilder removeAll(Collection<T> registryKeys) {
        throw new AssertionError("Implemented via mixin");
    }

    default <T> FabricProvidedTagBuilder removeAll(Stream<T> registryKeys) {
        throw new AssertionError("Implemented via mixin");
    }

    default <T> FabricProvidedTagBuilder removeTag(net.minecraft.class_6862<T> tag) {
        throw new AssertionError("Implemented via mixin");
    }
}
