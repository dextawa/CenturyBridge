package top.dext.centurybridge.compat.fapi.impl.datagen;

import java.util.Collection;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import top.dext.centurybridge.compat.fapi.datagen.v1.provider.FabricProvidedTagBuilder;

/**
 * Interface calls on FabricProvidedTagBuilder (interfaceCallRedirects). In 1.20.1 Fabric API's
 * FabricTagBuilder implemented it; 1.20.2's FabricTagBuilder cannot implement this layer's interface,
 * so a FabricTagBuilder reaching such a call is served as 1.20.1's served it -- its replace / forced
 * tag / removal, answering the builder itself -- and any other receiver gets the interface method
 * (its own implementation, or the default). Super calls of an implementor are not redirected.
 * instanceof FabricProvidedTagBuilder holds for a FabricTagBuilder as in 1.20.1 (instanceOfRedirects).
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CbFapiProvidedTagBuilders {

    private CbFapiProvidedTagBuilders() {
    }

    public static boolean cbInstanceOf(Object o) {
        return o instanceof FabricProvidedTagBuilder || o instanceof FabricTagProvider.FabricTagBuilder;
    }

    public static FabricProvidedTagBuilder setReplace(FabricProvidedTagBuilder builder, boolean replace) {
        if ((Object) builder instanceof FabricTagProvider.FabricTagBuilder fabric) {
            fabric.setReplace(replace);
            return builder;
        }
        return builder.setReplace(replace);
    }

    public static FabricProvidedTagBuilder forceAddTag(FabricProvidedTagBuilder builder, net.minecraft.class_6862 tag) {
        if ((Object) builder instanceof FabricTagProvider.FabricTagBuilder fabric) {
            fabric.forceAddTag(tag);
            return builder;
        }
        return builder.forceAddTag(tag);
    }

    public static FabricProvidedTagBuilder remove(FabricProvidedTagBuilder builder, net.minecraft.class_5321 registryKey) {
        if ((Object) builder instanceof FabricTagProvider.FabricTagBuilder fabric) {
            CbFapiTagBuilders.remove(fabric, (net.minecraft.class_5321<?>) registryKey);
            return builder;
        }
        return builder.remove(registryKey);
    }

    public static FabricProvidedTagBuilder remove(FabricProvidedTagBuilder builder, net.minecraft.class_5321[] registryKeys) {
        if ((Object) builder instanceof FabricTagProvider.FabricTagBuilder fabric) {
            CbFapiTagBuilders.remove(fabric, (net.minecraft.class_5321<?>[]) registryKeys);
            return builder;
        }
        return builder.remove(registryKeys);
    }

    public static FabricProvidedTagBuilder removeAll(FabricProvidedTagBuilder builder, Collection registryKeys) {
        if ((Object) builder instanceof FabricTagProvider.FabricTagBuilder fabric) {
            CbFapiTagBuilders.removeAll(fabric, (Collection<?>) registryKeys);
            return builder;
        }
        return builder.removeAll(registryKeys);
    }

    public static FabricProvidedTagBuilder removeAll(FabricProvidedTagBuilder builder, Stream registryKeys) {
        if ((Object) builder instanceof FabricTagProvider.FabricTagBuilder fabric) {
            CbFapiTagBuilders.removeAll(fabric, (Stream<?>) registryKeys);
            return builder;
        }
        return builder.removeAll(registryKeys);
    }

    public static FabricProvidedTagBuilder removeTag(FabricProvidedTagBuilder builder, net.minecraft.class_6862 tag) {
        if ((Object) builder instanceof FabricTagProvider.FabricTagBuilder fabric) {
            CbFapiTagBuilders.removeTag(fabric, (net.minecraft.class_6862<?>) tag);
            return builder;
        }
        return builder.removeTag(tag);
    }
}
