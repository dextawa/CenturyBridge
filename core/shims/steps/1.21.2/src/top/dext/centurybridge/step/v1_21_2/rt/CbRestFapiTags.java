/*
 * Ported from Fabric API 0.116 (fabric-tag-api-v1, TagRemovalInternals), Copyright (c) 2016-2019 FabricMC,
 * licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0).
 */
package top.dext.centurybridge.step.v1_21_2.rt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.class_2960;
import net.minecraft.class_3497;
import net.minecraft.class_3503;
import net.minecraft.class_7475;
import top.dext.centurybridge.step.v1_21_2.facade.fapi.FabricTagFile;
import top.dext.centurybridge.step.v1_21_2.mixin.CbRestTagEntryAccessor;

/**
 * fabric-tag-api-v1 of 1.21.1's Fabric API (0.116), which 1.21.2's Fabric API (0.106.1) does not ship: a tag
 * file's "fabric:remove" list takes entries out of the tag built from the files of lower packs. 0.116's
 * TagRemovalInternals on 1.21.2's names (TagFile, TagEntry, TagLoader.EntryWithSource -- the same classes
 * and members, checked against 1.21.2's bytecode); its hooks are CbRestFapiTagFileMixin (the codec) and
 * CbRestFapiTagLoaderMixin (loading and building).
 */
public final class CbRestFapiTags {

    private CbRestFapiTags() {
    }

    private static final ThreadLocal<class_2960> TAG_ID_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Map<class_2960, List<String>>> TAG_SOURCE_ORDER = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<class_2960, List<class_3503.class_5145>>> REMOVE_ENTRIES = ThreadLocal.withInitial(HashMap::new);

    /** set on a TagFile as its codec reads "fabric:remove" (CbRestFapiTagFileMixin) */
    public interface TagFileHooks {
        void cb$setRemove(List<class_3497> remove);
    }

    public static void setTagId(class_2960 tagId) {
        TAG_ID_THREAD_LOCAL.set(tagId);
    }

    public static void clearTagId() {
        TAG_ID_THREAD_LOCAL.remove();
    }

    public static Codec<class_7475> modifyTagFileCodec(Codec<class_7475> originalCodec) {
        return RecordCodecBuilder.create(i -> i.group(
            MapCodec.assumeMapUnsafe(originalCodec).forGetter(Function.identity()),
            class_3497.field_39265.listOf().lenientOptionalFieldOf("fabric:remove", Collections.emptyList())
                .forGetter(file -> ((FabricTagFile) (Object) file).remove())
        ).apply(i, (tagFile, remove) -> {
            ((TagFileHooks) (Object) tagFile).cb$setRemove(remove);
            return tagFile;
        }));
    }

    public static class_2960 normalizeTagResourceId(class_2960 resourceId) {
        String path = resourceId.method_12832();
        if (path.startsWith("tags/")) {
            path = path.substring("tags/".length());
        }
        int firstSeparator = path.indexOf('/');
        if (firstSeparator < 0) {
            return resourceId.method_45136(path.replace(".json", ""));
        }
        int secondSeparator = path.indexOf('/', firstSeparator + 1);
        int tagStart = secondSeparator >= 0 ? secondSeparator + 1 : firstSeparator + 1;
        String normalizedPath = path.substring(tagStart);
        if (normalizedPath.endsWith(".json")) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - ".json".length());
        }
        return resourceId.method_45136(normalizedPath);
    }

    public static void addTagSource(class_2960 tagId, String source) {
        TAG_SOURCE_ORDER.get().computeIfAbsent(tagId, k -> new ArrayList<>()).add(source);
    }

    public static void addRemoveEntry(class_2960 tagId, class_3503.class_5145 entry) {
        CbRestTagEntryAccessor accessor = (CbRestTagEntryAccessor) entry.comp_324();
        class_3497 optional = accessor.cb$tag() ? class_3497.method_43947(accessor.cb$id()) : class_3497.method_43942(accessor.cb$id());
        REMOVE_ENTRIES.get().computeIfAbsent(tagId, k -> new ArrayList<>()).add(new class_3503.class_5145(optional, entry.comp_325()));
    }

    public static boolean isEntryRemove(class_3503.class_5145 entry) {
        class_2960 tagId = TAG_ID_THREAD_LOCAL.get();
        if (tagId == null) {
            return false;
        }
        return REMOVE_ENTRIES.get().getOrDefault(tagId, Collections.emptyList()).contains(entry);
    }

    public static void mergeAddedAndRemovedEntries(class_2960 tagId, List<class_3503.class_5145> entries) {
        if (!REMOVE_ENTRIES.get().containsKey(tagId)) {
            return;
        }
        List<class_3503.class_5145> newEntries = new ArrayList<>();
        for (String sourceId : TAG_SOURCE_ORDER.get().getOrDefault(tagId, Collections.emptyList())) {
            newEntries.addAll(Stream.concat(
                // 'values' key should be added before 'fabric:remove' key.
                entries.stream().filter(entry -> entry.comp_325().equals(sourceId)),
                REMOVE_ENTRIES.get().getOrDefault(tagId, Collections.emptyList()).stream().filter(entry -> entry.comp_325().equals(sourceId))
            ).toList());
        }
        entries.clear();
        entries.addAll(newEntries);
    }

    public static void removeTagRemovalReferences() {
        TAG_SOURCE_ORDER.remove();
        REMOVE_ENTRIES.remove();
    }
}
