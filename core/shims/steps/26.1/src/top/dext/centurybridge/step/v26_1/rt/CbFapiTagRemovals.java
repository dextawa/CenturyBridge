package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagLoader;
import top.dext.centurybridge.step.v26_1.compat.fapi.tag.v1.FabricTagFile;
import top.dext.centurybridge.step.v26_1.mixin.CbFapiTagEntryAccess;

/**
 * 1.21.11 Fabric API's tag removal ("fabric:remove" in a tag file), which 26.1's Fabric API dropped, on
 * 26.1's TagLoader (the same load / build / tryBuildTag as 1.21.11's). 1.21.11's TagRemovalInternals, member
 * for member, with the mixin side in mixin/CbFapiTagLoaderMixin:
 * <ul>
 *   <li>load: each file's remove entries are recorded for its tag (as optional entries of the same id), with
 *       the order of the packs that contributed to the tag;</li>
 *   <li>build: before a tag is ordered by its dependencies, its removals are merged into its entries pack by
 *       pack (a pack's additions, then its removals), so a later pack can add back what an earlier removed;</li>
 *   <li>tryBuildTag: a removal entry takes its elements out of the tag being built instead of adding them.</li>
 * </ul>
 * State is per thread and per build, as in 1.21.11.
 */
public final class CbFapiTagRemovals {
    private CbFapiTagRemovals() {
    }

    private static final ThreadLocal<Identifier> TAG_ID = new ThreadLocal<>();
    private static final ThreadLocal<Map<Identifier, List<String>>> TAG_SOURCE_ORDER = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<Identifier, List<TagLoader.EntryWithSource>>> REMOVE_ENTRIES = ThreadLocal.withInitial(HashMap::new);
    /** the TagEntry instances that are removals, per tag (1.21.11 tested list membership; TagEntry equals by identity) */
    private static final ThreadLocal<Map<Identifier, Set<TagEntry>>> REMOVE_IDENTITIES = ThreadLocal.withInitial(HashMap::new);
    /** the element set tryBuildTag is filling, so a removal can take elements out of it */
    private static final ThreadLocal<java.util.SequencedSet<?>> BUILDING = new ThreadLocal<>();

    public static void setTagId(Identifier id) {
        TAG_ID.set(id);
    }

    public static void clearTagId() {
        TAG_ID.remove();
    }

    /** TagFile.CODEC with the optional "fabric:remove" entry list (empty when absent, omitted when empty) */
    public static Codec<TagFile> modifyTagFileCodec(Codec<TagFile> codec) {
        return RecordCodecBuilder.create(instance -> instance.group(
            MapCodec.assumeMapUnsafe(codec).forGetter(Function.identity()),
            TagEntry.CODEC.listOf().lenientOptionalFieldOf("fabric:remove", Collections.emptyList())
                .forGetter(file -> ((FabricTagFile) (Object) file).remove())
        ).apply(instance, (file, remove) -> {
            ((CbFapiTagFileHooks) (Object) file).cb$setRemove(remove);
            return file;
        }));
    }

    /** a tag file's resource id (data/ns/tags/dir/.../name.json, dir possibly nested once) -> the tag's id */
    public static Identifier normalizeTagResourceId(Identifier id) {
        String path = id.getPath();
        if (path.startsWith("tags/")) {
            path = path.substring("tags/".length());
        }
        int first = path.indexOf('/');
        if (first < 0) {
            return id.withPath(path.replace(".json", ""));
        }
        int second = path.indexOf('/', first + 1);
        int start = second >= 0 ? second + 1 : first + 1;
        String name = path.substring(start);
        if (name.endsWith(".json")) {
            name = name.substring(0, name.length() - ".json".length());
        }
        return id.withPath(name);
    }

    /** the file TagLoader.load is reading (its resource location) and the pack of the copy being parsed */
    private static final ThreadLocal<Identifier> LOADING_FILE = new ThreadLocal<>();
    private static final ThreadLocal<String> LOADING_PACK = new ThreadLocal<>();

    public static void loadingFile(Identifier location) {
        LOADING_FILE.set(location);
    }

    public static String loadingPack(String source) {
        LOADING_PACK.set(source);
        return source;
    }

    /** TagLoader.load, as a parsed file's entries are added: its removals and its pack */
    public static void loadRemoveEntries(TagFile file) {
        loadRemoveEntries(LOADING_FILE.get(), file, LOADING_PACK.get());
    }

    public static void loadRemoveEntries(Identifier fileId, TagFile file, String source) {
        Identifier tagId = normalizeTagResourceId(fileId);
        for (TagEntry entry : ((FabricTagFile) (Object) file).remove()) {
            addRemoveEntry(tagId, new TagLoader.EntryWithSource(entry, source));
        }
        addTagSource(tagId, source);
    }

    public static void addTagSource(Identifier id, String source) {
        TAG_SOURCE_ORDER.get().computeIfAbsent(id, k -> new ArrayList<>()).add(source);
    }

    public static void addRemoveEntry(Identifier id, TagLoader.EntryWithSource entry) {
        CbFapiTagEntryAccess access = (CbFapiTagEntryAccess) entry.entry();
        TagEntry optional = access.cb$tag() ? TagEntry.optionalTag(access.cb$id()) : TagEntry.optionalElement(access.cb$id());
        REMOVE_ENTRIES.get().computeIfAbsent(id, k -> new ArrayList<>()).add(new TagLoader.EntryWithSource(optional, entry.source()));
        REMOVE_IDENTITIES.get().computeIfAbsent(id, k -> Collections.newSetFromMap(new IdentityHashMap<>())).add(optional);
    }

    public static boolean isEntryRemove(TagEntry entry) {
        Identifier id = TAG_ID.get();
        if (id == null) {
            return false;
        }
        Set<TagEntry> removes = REMOVE_IDENTITIES.get().get(id);
        return removes != null && removes.contains(entry);
    }

    /** a tag's entries, pack by pack in load order: the pack's additions, then its removals */
    public static void mergeAddedAndRemovedEntries(Identifier id, List<TagLoader.EntryWithSource> entries) {
        if (!REMOVE_ENTRIES.get().containsKey(id)) {
            return;
        }
        List<TagLoader.EntryWithSource> merged = new ArrayList<>();
        List<TagLoader.EntryWithSource> removes = REMOVE_ENTRIES.get().getOrDefault(id, Collections.emptyList());
        for (String source : TAG_SOURCE_ORDER.get().getOrDefault(id, Collections.emptyList())) {
            merged.addAll(java.util.stream.Stream.concat(
                entries.stream().filter(e -> e.source().equals(source)),
                removes.stream().filter(e -> e.source().equals(source))).toList());
        }
        entries.clear();
        entries.addAll(merged);
    }

    public static void removeTagRemovalReferences() {
        TAG_SOURCE_ORDER.remove();
        REMOVE_ENTRIES.remove();
        REMOVE_IDENTITIES.remove();
    }

    /** build's dependency-sorter feed: removals merged into each tag's entries first */
    public static BiConsumer<Identifier, List<TagLoader.EntryWithSource>> mergingRemovals(BiConsumer<Identifier, List<TagLoader.EntryWithSource>> consumer) {
        return (id, entries) -> {
            mergeAddedAndRemovedEntries(id, entries);
            consumer.accept(id, entries);
        };
    }

    /** build's per-tag step, run with the tag's id known to tryBuildTag */
    public static <V> BiConsumer<Identifier, V> scopedTagId(BiConsumer<Identifier, V> consumer) {
        return (id, value) -> {
            setTagId(id);
            try {
                consumer.accept(id, value);
            } finally {
                clearTagId();
            }
        };
    }

    /** tryBuildTag's element set, as it is created */
    public static <S extends java.util.SequencedSet<?>> S building(S set) {
        BUILDING.set(set);
        return set;
    }

    /** tryBuildTag's TagEntry.build: a removal entry takes its elements out of the set being built */
    @SuppressWarnings("unchecked")
    public static <T> boolean build(TagEntry entry, TagEntry.Lookup<T> lookup, java.util.function.Consumer<T> consumer) {
        if (isEntryRemove(entry)) {
            java.util.SequencedSet<T> set = (java.util.SequencedSet<T>) BUILDING.get();
            entry.build(lookup, set::remove);
            return true;
        }
        return entry.build(lookup, consumer);
    }
}
