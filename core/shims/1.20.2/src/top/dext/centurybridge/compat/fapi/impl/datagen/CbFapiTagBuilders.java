package top.dext.centurybridge.compat.fapi.impl.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import top.dext.centurybridge.shims.v1_20_2.CbFapiProvidedTagBuilderAccess;
import top.dext.centurybridge.shims.v1_20_2.CbFapiTagEntryAccess;

/**
 * Tag removal in data generation, as 1.20.1's Fabric API had it: FabricTagBuilder.remove / removeAll /
 * removeTag (gone from 1.20.2's FabricTagBuilder; calls are redirected here) record removals on the
 * tag's TagBuilder, and the tag provider writes them as the tag file's "fabric:remove" list
 * (CbFapiTagProviderRemoval).
 *
 * Removing an element (rather than its registry key) resolves the key through the tag provider's
 * reverseLookup, as 1.20.1's remove(T) did -- it is protected, overridden by the item / block / fluid /
 * entity type providers, and the builder only knows its provider as the inner-class outer instance,
 * so both are reached reflectively, on first use.
 */
public final class CbFapiTagBuilders {

    private CbFapiTagBuilders() {
    }

    @SuppressWarnings("rawtypes")
    public static FabricTagProvider.FabricTagBuilder remove(FabricTagProvider.FabricTagBuilder builder, Object element) {
        return remove(builder, ReverseLookup.of(builder, element));
    }

    @SuppressWarnings("rawtypes")
    public static FabricTagProvider.FabricTagBuilder remove(FabricTagProvider.FabricTagBuilder builder, Object[] elements) {
        Stream.of(elements).map(element -> ReverseLookup.of(builder, element)).forEach(key -> remove(builder, key));
        return builder;
    }

    @SuppressWarnings("rawtypes")
    public static FabricTagProvider.FabricTagBuilder remove(FabricTagProvider.FabricTagBuilder builder, net.minecraft.class_5321<?> registryKey) {
        removals(builder).cb$removeElement(registryKey.method_29177());
        return builder;
    }

    @SuppressWarnings("rawtypes")
    public static FabricTagProvider.FabricTagBuilder remove(FabricTagProvider.FabricTagBuilder builder, net.minecraft.class_5321<?>[] registryKeys) {
        for (net.minecraft.class_5321<?> registryKey : registryKeys) {
            remove(builder, registryKey);
        }
        return builder;
    }

    @SuppressWarnings("rawtypes")
    public static FabricTagProvider.FabricTagBuilder removeAll(FabricTagProvider.FabricTagBuilder builder, Collection<?> registryKeys) {
        registryKeys.forEach(registryKey -> remove(builder, (net.minecraft.class_5321<?>) registryKey));
        return builder;
    }

    @SuppressWarnings("rawtypes")
    public static FabricTagProvider.FabricTagBuilder removeAll(FabricTagProvider.FabricTagBuilder builder, Stream<?> registryKeys) {
        registryKeys.forEach(registryKey -> remove(builder, (net.minecraft.class_5321<?>) registryKey));
        return builder;
    }

    @SuppressWarnings("rawtypes")
    public static FabricTagProvider.FabricTagBuilder removeTag(FabricTagProvider.FabricTagBuilder builder, net.minecraft.class_6862<?> tag) {
        removals(builder).cb$removeTag(tag.comp_327());
        return builder;
    }

    /** the "fabric:remove" list of a generated tag file: its builder's removals, tags marked with #, as 1.20.1 wrote them */
    public static void writeRemovals(JsonElement json, net.minecraft.class_3495 tagBuilder) {
        if (!((Object) tagBuilder instanceof CbFapiTagBuilderRemovals removals)) {
            return;
        }
        JsonArray array = new JsonArray();
        for (net.minecraft.class_3497 entry : removals.cb$getRemove()) {
            CbFapiTagEntryAccess access = (CbFapiTagEntryAccess) entry;
            array.add((access.cb$tag() ? "#" : "") + access.cb$id().toString());
        }
        if (!array.isEmpty()) {
            json.getAsJsonObject().add("fabric:remove", array);
        }
    }

    @SuppressWarnings("rawtypes")
    private static CbFapiTagBuilderRemovals removals(FabricTagProvider.FabricTagBuilder builder) {
        return (CbFapiTagBuilderRemovals) (Object) ((CbFapiProvidedTagBuilderAccess) (Object) builder).cb$builder();
    }

    /** FabricTagProvider.reverseLookup of the builder's provider */
    private static final class ReverseLookup {

        private static final Field PROVIDER;
        private static final Method REVERSE_LOOKUP;

        static {
            try {
                PROVIDER = FabricTagProvider.FabricTagBuilder.class.getDeclaredField("this$0");
                PROVIDER.setAccessible(true);
                REVERSE_LOOKUP = FabricTagProvider.class.getDeclaredMethod("reverseLookup", Object.class);
                REVERSE_LOOKUP.setAccessible(true);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("CenturyBridge: Fabric API's FabricTagBuilder no longer reaches its provider's reverseLookup", e);
            }
        }

        @SuppressWarnings("rawtypes")
        static net.minecraft.class_5321<?> of(FabricTagProvider.FabricTagBuilder builder, Object element) {
            try {
                return (net.minecraft.class_5321<?>) REVERSE_LOOKUP.invoke(PROVIDER.get(builder), element);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException r) {
                    throw r;
                }
                if (cause instanceof Error err) {
                    throw err;
                }
                throw new IllegalStateException(cause);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
