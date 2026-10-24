package top.dext.centurybridge.step.v1_21_2.rt;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.class_2960;
import net.minecraft.class_3495;
import net.minecraft.class_3497;
import net.minecraft.class_5321;
import net.minecraft.class_6862;

/**
 * Fabric API 0.116's tag removal in data generation on 0.106 (dev-time datagen; fabric-data-generation-api-v1).
 * FabricTagBuilder.remove / removeAll / removeTag (gone in 0.106) record the element or tag on the appender's
 * TagBuilder (CbItemTagBuilderMixin), and the tags provider writes them into the tag file's "fabric:remove" list
 * (CbItemTagProviderMixin; the file's codec and the runtime removal are fabric-tag-api-v1's, CbRestFapiTags), as
 * 0.116's TagBuilderMixin / TagProviderMixin did. remove(element) names the element by the provider's
 * reverseLookup, as 0.116's did.
 */
public final class CbItemDatagenTags {
    private CbItemDatagenTags() {
    }

    /** a TagBuilder's "fabric:remove" entries (CbItemTagBuilderMixin; 0.116's TagBuilderHooks) */
    public interface Removals {
        void cb$removeElement(class_2960 id);

        void cb$removeTag(class_2960 id);

        List<class_3497> cb$getRemove();
    }

    /** a TagAppender's TagBuilder (CbItemTagAppenderMixin) */
    public interface Appender {
        class_3495 cb$builder();
    }

    private static Removals removals(Object appender) {
        return (Removals) (Object) ((Appender) appender).cb$builder();
    }

    /** FabricTagBuilder.remove(key) */
    public static <T> FabricTagProvider<T>.FabricTagBuilder remove(FabricTagProvider<T>.FabricTagBuilder b, class_5321<T> key) {
        removals(b).cb$removeElement(key.method_29177());
        return b;
    }

    /** FabricTagBuilder.remove(keys...) */
    public static <T> FabricTagProvider<T>.FabricTagBuilder remove(FabricTagProvider<T>.FabricTagBuilder b, class_5321<T>[] keys) {
        for (class_5321<T> key : keys) {
            remove(b, key);
        }
        return b;
    }

    /** FabricTagBuilder.remove(element): the provider's reverseLookup(element) */
    public static <T> FabricTagProvider<T>.FabricTagBuilder remove(FabricTagProvider<T>.FabricTagBuilder b, Object element) {
        return remove(b, CbItemDatagenTags.<T>reverseLookup(b, element));
    }

    /** FabricTagBuilder.remove(elements...) */
    public static <T> FabricTagProvider<T>.FabricTagBuilder remove(FabricTagProvider<T>.FabricTagBuilder b, Object[] elements) {
        for (Object element : elements) {
            remove(b, element);
        }
        return b;
    }

    /** FabricTagBuilder.removeAll(keys) */
    public static <T> FabricTagProvider<T>.FabricTagBuilder removeAll(FabricTagProvider<T>.FabricTagBuilder b, Collection<class_5321<T>> keys) {
        keys.forEach(key -> remove(b, key));
        return b;
    }

    /** FabricTagBuilder.removeAll(keys) */
    public static <T> FabricTagProvider<T>.FabricTagBuilder removeAll(FabricTagProvider<T>.FabricTagBuilder b, Stream<class_5321<T>> keys) {
        keys.forEach(key -> remove(b, key));
        return b;
    }

    /** FabricTagBuilder.removeTag(tag) */
    public static <T> FabricTagProvider<T>.FabricTagBuilder removeTag(FabricTagProvider<T>.FabricTagBuilder b, class_6862<T> tag) {
        removals(b).cb$removeTag(tag.comp_327());
        return b;
    }

    /** the entries a TagBuilder removes, none for one never given any */
    public static List<class_3497> removeOf(class_3495 builder) {
        return ((Object) builder) instanceof Removals r ? r.cb$getRemove() : Collections.emptyList();
    }

    /** a new, empty removal list (CbItemTagBuilderMixin) */
    public static List<class_3497> newRemovals() {
        return new ArrayList<>();
    }

    private static volatile Field outer;
    private static volatile Method reverseLookup;

    @SuppressWarnings("unchecked")
    private static <T> class_5321<T> reverseLookup(FabricTagProvider<T>.FabricTagBuilder b, Object element) {
        try {
            if (reverseLookup == null) {
                // the builder's provider (FabricTagBuilder is its inner class) and its protected reverseLookup,
                // overridden per registry (ItemTagProvider: the item's built-in holder)
                Field f = FabricTagProvider.FabricTagBuilder.class.getDeclaredField("this$0");
                f.setAccessible(true);
                Method m = FabricTagProvider.class.getDeclaredMethod("reverseLookup", Object.class);
                m.setAccessible(true);
                outer = f;
                reverseLookup = m;
            }
            return (class_5321<T>) reverseLookup.invoke(outer.get(b), element);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable c = e.getCause();
            if (c instanceof RuntimeException re) {
                throw re;
            }
            if (c instanceof Error er) {
                throw er;
            }
            throw new IllegalStateException(c);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("FabricTagBuilder.remove(element): FabricTagProvider.reverseLookup", e);
        }
    }
}
