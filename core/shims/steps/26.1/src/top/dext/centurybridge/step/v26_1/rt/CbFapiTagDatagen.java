package top.dext.centurybridge.step.v26_1.rt;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagAppender;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import top.dext.centurybridge.step.v26_1.mixin.CbFapiTagAppenderAccess;

/**
 * 1.21.11's FabricProvidedTagBuilder.remove / removeAll / removeTag (26.1's FabricTagAppender has none: 26.1's
 * Fabric API dropped tag removal), written to the tag file as "fabric:remove" (CbFapiTagsProviderMixin,
 * CbFapiTagFileMixin, the codec of rt/CbFapiTagRemovals).
 *
 * 1.21.11 implemented them on vanilla's builder-backed appender (TagAppender$1, its TagAppenderMixin): each
 * removal is recorded on the TagBuilder, as a required entry, and the appender returned. Every other appender
 * -- vanilla's mapped one (TagAppender$2) included -- had only the interface's default, which threw
 * AssertionError("Implemented via mixin"); so do these bodies. A call reaches the receiver's own override
 * first (CbFapiOverrides); a mod's super call runs the interface default.
 */
public final class CbFapiTagDatagen {
    private CbFapiTagDatagen() {
    }

    private static final ThreadLocal<TagBuilder> WRITING = new ThreadLocal<>();

    /** TagsProvider is about to make the TagFile of this builder */
    public static void writing(TagBuilder builder) {
        WRITING.set(builder);
    }

    /** the removals of the builder whose TagFile is being made, once; null for any other TagFile */
    public static List<TagEntry> takeRemovals() {
        TagBuilder builder = WRITING.get();
        if (builder == null) {
            return null;
        }
        WRITING.remove();
        List<TagEntry> removals = ((CbFapiTagBuilderHooks) builder).cb$remove();
        return removals.isEmpty() ? null : removals;
    }

    private static final ClassValue<MethodHandle[]> OVERRIDES = new ClassValue<>() {
        @Override
        protected MethodHandle[] computeValue(Class<?> type) {
            return new MethodHandle[] {
                CbFapiOverrides.find(type, "remove", MethodType.methodType(TagAppender.class, FabricTagAppender.class, ResourceKey.class)),
                CbFapiOverrides.find(type, "remove", MethodType.methodType(TagAppender.class, FabricTagAppender.class, ResourceKey[].class)),
                CbFapiOverrides.find(type, "removeAll", MethodType.methodType(TagAppender.class, FabricTagAppender.class, Collection.class)),
                CbFapiOverrides.find(type, "removeAll", MethodType.methodType(TagAppender.class, FabricTagAppender.class, Stream.class)),
                CbFapiOverrides.find(type, "removeTag", MethodType.methodType(TagAppender.class, FabricTagAppender.class, TagKey.class)),
            };
        }
    };

    @SuppressWarnings("rawtypes")
    public static TagAppender remove(FabricTagAppender appender, ResourceKey key) {
        MethodHandle h = OVERRIDES.get(appender.getClass())[0];
        if (h != null) {
            try {
                return (TagAppender) h.invokeExact(appender, key);
            } catch (Throwable t) {
                throw CbFapiOverrides.rethrow(t);
            }
        }
        builder(appender).cb$removeElement(key.identifier());
        return (TagAppender) appender;
    }

    @SuppressWarnings("rawtypes")
    public static TagAppender remove(FabricTagAppender appender, ResourceKey[] keys) {
        MethodHandle h = OVERRIDES.get(appender.getClass())[1];
        if (h != null) {
            try {
                return (TagAppender) h.invokeExact(appender, keys);
            } catch (Throwable t) {
                throw CbFapiOverrides.rethrow(t);
            }
        }
        builder(appender); // 1.21.11: removeAll(Arrays.stream(keys)), virtually
        return removeAll(appender, Arrays.stream(keys));
    }

    @SuppressWarnings("rawtypes")
    public static TagAppender removeAll(FabricTagAppender appender, Collection keys) {
        MethodHandle h = OVERRIDES.get(appender.getClass())[2];
        if (h != null) {
            try {
                return (TagAppender) h.invokeExact(appender, keys);
            } catch (Throwable t) {
                throw CbFapiOverrides.rethrow(t);
            }
        }
        CbFapiTagBuilderHooks builder = builder(appender);
        for (Object key : keys) {
            builder.cb$removeElement(((ResourceKey<?>) key).identifier());
        }
        return (TagAppender) appender;
    }

    @SuppressWarnings("rawtypes")
    public static TagAppender removeAll(FabricTagAppender appender, Stream keys) {
        MethodHandle h = OVERRIDES.get(appender.getClass())[3];
        if (h != null) {
            try {
                return (TagAppender) h.invokeExact(appender, keys);
            } catch (Throwable t) {
                throw CbFapiOverrides.rethrow(t);
            }
        }
        CbFapiTagBuilderHooks builder = builder(appender);
        keys.forEach(key -> builder.cb$removeElement(((ResourceKey<?>) key).identifier()));
        return (TagAppender) appender;
    }

    @SuppressWarnings("rawtypes")
    public static TagAppender removeTag(FabricTagAppender appender, TagKey tag) {
        MethodHandle h = OVERRIDES.get(appender.getClass())[4];
        if (h != null) {
            try {
                return (TagAppender) h.invokeExact(appender, tag);
            } catch (Throwable t) {
                throw CbFapiOverrides.rethrow(t);
            }
        }
        builder(appender).cb$removeTag(tag.location());
        return (TagAppender) appender;
    }

    // a mod's super call: 1.21.11's interface default

    @SuppressWarnings("rawtypes")
    public static TagAppender superRemove(FabricTagAppender appender, ResourceKey key) {
        throw new AssertionError("Implemented via mixin");
    }

    @SuppressWarnings("rawtypes")
    public static TagAppender superRemove(FabricTagAppender appender, ResourceKey[] keys) {
        throw new AssertionError("Implemented via mixin");
    }

    @SuppressWarnings("rawtypes")
    public static TagAppender superRemoveAll(FabricTagAppender appender, Collection keys) {
        throw new AssertionError("Implemented via mixin");
    }

    @SuppressWarnings("rawtypes")
    public static TagAppender superRemoveAll(FabricTagAppender appender, Stream keys) {
        throw new AssertionError("Implemented via mixin");
    }

    @SuppressWarnings("rawtypes")
    public static TagAppender superRemoveTag(FabricTagAppender appender, TagKey tag) {
        throw new AssertionError("Implemented via mixin");
    }

    /** the builder of vanilla's builder-backed appender; any other appender had only the throwing default */
    private static CbFapiTagBuilderHooks builder(FabricTagAppender<?, ?> appender) {
        if (appender instanceof CbFapiTagAppenderAccess access) {
            return (CbFapiTagBuilderHooks) access.cb$builder();
        }
        throw new AssertionError("Implemented via mixin");
    }
}
