package top.dext.centurybridge.compat.fapi.impl.tag;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import top.dext.centurybridge.compat.fapi.tag.v1.FabricTagFile;
import top.dext.centurybridge.shims.v1_20_2.CbFapiTagEntryAccess;

/**
 * Tag removal as 1.20.1's Fabric API tag API v1 implemented it (TagRemovalInternals and the
 * TagFileMixin / TagGroupLoaderMixin bodies): a tag file may list entries under "fabric:remove";
 * while a tag group loads, each file's removals are recorded per tag with the data pack they came
 * from, merged into the tag's entry list after that pack's own entries (packs in load order), and
 * when the tag resolves a removal entry takes its values out of the values collected so far.
 *
 * The tag id a file's removals are recorded under is computed from the file's path exactly as
 * 1.20.1 computed it (normalizeTagResourceId), path segments included as 1.20.1 included them.
 * 1.20.1's mixin read the file id, the parsed tag file and the pack name out of the loader's locals;
 * this layer's mixin is handed them by the calls that produce them (CbFapiTagGroupLoaderRemoval) and
 * keeps them here per thread for the file being read.
 */
public final class CbFapiTagRemoval {

    private static final ThreadLocal<net.minecraft.class_2960> TAG_ID_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Map<net.minecraft.class_2960, List<String>>> TAG_SOURCE_ORDER = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<net.minecraft.class_2960, List<net.minecraft.class_3503.class_5145>>> REMOVE_ENTRIES =
        ThreadLocal.withInitial(HashMap::new);
    /** the values a tag resolves to so far (1.20.1's TagGroupLoaderMixin fabric$currentValues) */
    private static final ThreadLocal<LinkedHashSet<Object>> CURRENT_VALUES = ThreadLocal.withInitial(LinkedHashSet::new);

    /** the tag file id, data pack and resolving entry the loader's current call is about */
    private static final ThreadLocal<net.minecraft.class_2960> LOADING_FILE = new ThreadLocal<>();
    private static final ThreadLocal<String> LOADING_SOURCE = new ThreadLocal<>();
    private static final ThreadLocal<net.minecraft.class_3503.class_5145> RESOLVING = new ThreadLocal<>();

    private CbFapiTagRemoval() {
    }

    // ---------------------------------------------------------------- the tag file

    /** 1.20.1's TagFile codec: vanilla's "values" and "replace", plus the optional "fabric:remove" list */
    public static Codec<net.minecraft.class_7475> tagFileCodec() {
        return RecordCodecBuilder.create(instance -> instance.group(
            net.minecraft.class_3497.field_39265.listOf().fieldOf("values").forGetter(net.minecraft.class_7475::comp_811),
            Codec.BOOL.optionalFieldOf("replace", false).forGetter(net.minecraft.class_7475::comp_812),
            net.minecraft.class_3497.field_39265.listOf().optionalFieldOf("fabric:remove", Collections.emptyList())
                .forGetter(file -> ((FabricTagFile) (Object) file).remove())
        ).apply(instance, (values, replace, remove) -> {
            net.minecraft.class_7475 file = new net.minecraft.class_7475(values, replace);
            ((CbFapiTagFileHooks) (Object) file).cb$setRemove(remove);
            return file;
        }));
    }

    // ---------------------------------------------------------------- loading

    public static void loadingFile(net.minecraft.class_2960 fileId) {
        LOADING_FILE.set(fileId);
    }

    public static void loadingSource(String source) {
        LOADING_SOURCE.set(source);
    }

    /** 1.20.1's loadRemoveEntries: the removals of the tag file just parsed, and its pack in the tag's source order */
    public static void loadRemoveEntries(net.minecraft.class_7475 tagFile) {
        net.minecraft.class_2960 tagId = normalizeTagResourceId(LOADING_FILE.get());
        String source = LOADING_SOURCE.get();
        for (net.minecraft.class_3497 entry : ((FabricTagFile) (Object) tagFile).remove()) {
            addRemoveEntry(tagId, new net.minecraft.class_3503.class_5145(entry, source));
        }
        addTagSource(tagId, source);
    }

    /** 1.20.1's normalizeTagResourceId */
    public static net.minecraft.class_2960 normalizeTagResourceId(net.minecraft.class_2960 id) {
        String path = id.method_12832();
        if (path.startsWith("tags/")) {
            path = path.substring("tags/".length());
        }
        int firstSlash = path.indexOf('/');
        if (firstSlash < 0) {
            return id.method_45136(path.replace(".json", ""));
        }
        int secondSlash = path.indexOf('/', firstSlash + 1);
        int start = secondSlash >= 0 ? secondSlash + 1 : firstSlash + 1;
        String name = path.substring(start);
        if (name.endsWith(".json")) {
            name = name.substring(0, name.length() - ".json".length());
        }
        return id.method_45136(name);
    }

    public static void addTagSource(net.minecraft.class_2960 tagId, String source) {
        TAG_SOURCE_ORDER.get().computeIfAbsent(tagId, k -> new ArrayList<>()).add(source);
    }

    /** a removal entry, made optional (a missing element or tag is no error when removing it), as 1.20.1 recorded it */
    public static void addRemoveEntry(net.minecraft.class_2960 tagId, net.minecraft.class_3503.class_5145 trackedEntry) {
        Map<net.minecraft.class_2960, List<net.minecraft.class_3503.class_5145>> removes = REMOVE_ENTRIES.get();
        removes.computeIfAbsent(tagId, k -> new ArrayList<>());
        CbFapiTagEntryAccess entry = (CbFapiTagEntryAccess) trackedEntry.comp_324();
        net.minecraft.class_3497 optional = entry.cb$tag()
            ? net.minecraft.class_3497.method_43947(entry.cb$id())
            : net.minecraft.class_3497.method_43942(entry.cb$id());
        removes.get(tagId).add(new net.minecraft.class_3503.class_5145(optional, trackedEntry.comp_325()));
    }

    // ---------------------------------------------------------------- building the group

    public static void setTagId(net.minecraft.class_2960 tagId) {
        TAG_ID_THREAD_LOCAL.set(tagId);
    }

    public static void clearTagId() {
        TAG_ID_THREAD_LOCAL.remove();
    }

    /** the tag's entries regrouped by data pack in load order: each pack's added entries, then its removals */
    public static void mergeAddedAndRemovedEntries(net.minecraft.class_2960 tagId, List<net.minecraft.class_3503.class_5145> entries) {
        Map<net.minecraft.class_2960, List<net.minecraft.class_3503.class_5145>> removes = REMOVE_ENTRIES.get();
        if (!removes.containsKey(tagId)) {
            return;
        }
        List<net.minecraft.class_3503.class_5145> merged = new ArrayList<>();
        for (String source : TAG_SOURCE_ORDER.get().getOrDefault(tagId, Collections.emptyList())) {
            merged.addAll(Stream.concat(
                entries.stream().filter(e -> e.comp_325().equals(source)),
                removes.getOrDefault(tagId, Collections.emptyList()).stream().filter(e -> e.comp_325().equals(source))
            ).toList());
        }
        entries.clear();
        entries.addAll(merged);
    }

    public static void removeTagRemovalReferences() {
        TAG_SOURCE_ORDER.remove();
        REMOVE_ENTRIES.remove();
    }

    // ---------------------------------------------------------------- resolving a tag

    public static void beginResolve() {
        CURRENT_VALUES.get().clear();
    }

    public static void resolving(net.minecraft.class_3503.class_5145 trackedEntry) {
        RESOLVING.set(trackedEntry);
    }

    /** a removal entry takes its values out of the tag (and never fails it); any other entry adds its values */
    public static <T> boolean resolve(net.minecraft.class_3497 entry, net.minecraft.class_3497.class_7474<T> valueGetter) {
        LinkedHashSet<Object> values = CURRENT_VALUES.get();
        if (isEntryRemove(RESOLVING.get())) {
            entry.method_26790(valueGetter, values::remove);
            return true;
        }
        return entry.method_26790(valueGetter, values::add);
    }

    /** the tag's values are the ones collected, in order, when it resolved */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Either<Collection<net.minecraft.class_3503.class_5145>, Collection<?>> endResolve(
            Either<Collection<net.minecraft.class_3503.class_5145>, Collection<?>> result) {
        LinkedHashSet<Object> values = CURRENT_VALUES.get();
        Either<Collection<net.minecraft.class_3503.class_5145>, Collection<?>> out = result;
        if (result != null && result.right().isPresent()) {
            out = (Either) Either.right(ImmutableSet.copyOf(values));
        }
        values.clear();
        RESOLVING.remove();
        return out;
    }

    private static boolean isEntryRemove(net.minecraft.class_3503.class_5145 trackedEntry) {
        net.minecraft.class_2960 tagId = TAG_ID_THREAD_LOCAL.get();
        if (tagId == null || trackedEntry == null) {
            return false;
        }
        return REMOVE_ENTRIES.get().getOrDefault(tagId, Collections.emptyList()).contains(trackedEntry);
    }
}
