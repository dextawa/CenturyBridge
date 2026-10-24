package top.dext.centurybridge.step.v26_1.facade;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.SavedDataStorage;
import top.dext.centurybridge.step.v26_1.rt.CbWallRegionStorage;

/**
 * 1.21.11's LegacyStructureDataHandler (net/minecraft/class_3360): the LegacyTagFixer a dimension's chunk storage
 * ran on chunk tags older than data version 1493, moving pre-1.13 structure data (the {@code <legacy name>.dat}
 * feature files and {@code <legacy name>_index.dat} indices of the dimension's data folder) into each chunk's
 * Structures Starts/References. 26.1 dropped it: its LegacyStructureFileFix does that conversion once, when the
 * world is upgraded. 1.21.11's members and bodies on 26.1's API (the files are read from the storage's data
 * folder by 1.21.11's names, rt/CbWallRegionStorage.readTagFromDisk; the indices are
 * StructureFeatureIndexSavedData facades); a 1.21.11 region storage built with one runs it as 1.21.11 did.
 */
public class LegacyStructureDataHandler implements LegacyTagFixer {

    /** 1.21.11 LAST_MONOLYTH_STRUCTURE_DATA_VERSION */
    public static final int field_63962 = 1493;

    public static final Map<String, String> field_14435 = Util.make(Maps.newHashMap(), map -> {
        map.put("Village", "Village");
        map.put("Mineshaft", "Mineshaft");
        map.put("Mansion", "Mansion");
        map.put("Igloo", "Temple");
        map.put("Desert_Pyramid", "Temple");
        map.put("Jungle_Pyramid", "Temple");
        map.put("Swamp_Hut", "Temple");
        map.put("Stronghold", "Stronghold");
        map.put("Monument", "Monument");
        map.put("Fortress", "Fortress");
        map.put("EndCity", "EndCity");
    });
    public static final Map<String, String> field_14436 = Util.make(Maps.newHashMap(), map -> {
        map.put("Iglu", "Igloo");
        map.put("TeDP", "Desert_Pyramid");
        map.put("TeJP", "Jungle_Pyramid");
        map.put("TeSH", "Swamp_Hut");
    });
    public static final Set<String> field_37194 = Set.of(
        "pillager_outpost", "mineshaft", "mansion", "jungle_pyramid", "desert_pyramid", "igloo", "ruined_portal",
        "shipwreck", "swamp_hut", "stronghold", "monument", "ocean_ruin", "fortress", "endcity", "buried_treasure",
        "village", "nether_fossil", "bastion_remnant");

    public final boolean field_14434;
    public final Map<String, Long2ObjectMap<CompoundTag>> field_14432 = Maps.newHashMap();
    public final Map<String, StructureFeatureIndexSavedData> field_14433 = Maps.newHashMap();
    public final SavedDataStorage field_64684;
    public final List<String> field_17658;
    public final List<String> field_17659;
    public final DataFixer field_63963;
    public boolean field_64685;

    public LegacyStructureDataHandler(SavedDataStorage field_64684, List<String> field_17658, List<String> field_17659, DataFixer field_63963) {
        this.field_64684 = field_64684;
        this.field_17658 = field_17658;
        this.field_17659 = field_17659;
        this.field_63963 = field_63963;
        // 1.21.11 computes this before any data is read (field_14432 is still empty here), as it does below
        boolean field_14434 = false;
        for (String key : this.field_17659) {
            field_14434 |= this.field_14432.get(key) != null;
        }
        this.field_14434 = field_14434;
    }

    @Override
    public void markChunkDone(ChunkPos pos) {
        long chunk = pos.pack();
        for (String key : this.field_17658) {
            StructureFeatureIndexSavedData index = this.field_14433.get(key);
            if (index != null && index.method_14894(chunk)) {
                index.method_14895(chunk);
            }
        }
    }

    @Override
    public int targetDataVersion() {
        return 1493;
    }

    @Override
    public CompoundTag applyFix(CompoundTag tag) {
        if (!this.field_64685 && this.field_64684 != null) {
            this.method_14734(this.field_64684);
        }
        int version = NbtUtils.getDataVersion(tag);
        if (version < 1493) {
            tag = DataFixTypes.CHUNK.update(this.field_63963, tag, version, 1493);
            if (tag.getCompound("Level").flatMap(level -> level.getBoolean("hasLegacyStructureData")).orElse(false)) {
                tag = this.method_14735(tag);
            }
        }
        return tag;
    }

    public CompoundTag method_14735(CompoundTag tag) {
        CompoundTag level = tag.getCompoundOrEmpty("Level");
        ChunkPos pos = new ChunkPos(level.getIntOr("xPos", 0), level.getIntOr("zPos", 0));
        if (this.method_14737(pos.x(), pos.z())) {
            tag = this.method_14741(tag, pos);
        }
        CompoundTag structures = level.getCompoundOrEmpty("Structures");
        CompoundTag references = structures.getCompoundOrEmpty("References");
        for (String key : this.field_17659) {
            boolean oldKey = field_37194.contains(key.toLowerCase(Locale.ROOT));
            if (!references.getLongArray(key).isPresent() && oldKey) {
                int range = 8;
                LongList starts = new LongArrayList();
                for (int x = pos.x() - range; x <= pos.x() + range; x++) {
                    for (int z = pos.z() - range; z <= pos.z() + range; z++) {
                        if (this.method_14738(x, z, key)) {
                            starts.add(ChunkPos.pack(x, z));
                        }
                    }
                }
                references.putLongArray(key, starts.toLongArray());
            }
        }
        structures.put("References", references);
        level.put("Structures", structures);
        tag.put("Level", level);
        return tag;
    }

    public boolean method_14738(int x, int z, String key) {
        return this.field_14434
            && this.field_14432.get(key) != null
            && this.field_14433.get(field_14435.get(key)).method_14897(ChunkPos.pack(x, z));
    }

    public boolean method_14737(int x, int z) {
        if (!this.field_14434) {
            return false;
        }
        for (String key : this.field_17659) {
            if (this.field_14432.get(key) != null && this.field_14433.get(field_14435.get(key)).method_14894(ChunkPos.pack(x, z))) {
                return true;
            }
        }
        return false;
    }

    public CompoundTag method_14741(CompoundTag tag, ChunkPos pos) {
        CompoundTag level = tag.getCompoundOrEmpty("Level");
        CompoundTag structures = level.getCompoundOrEmpty("Structures");
        CompoundTag starts = structures.getCompoundOrEmpty("Starts");
        for (String key : this.field_17659) {
            Long2ObjectMap<CompoundTag> data = this.field_14432.get(key);
            if (data == null) {
                continue;
            }
            long chunk = pos.pack();
            if (!this.field_14433.get(field_14435.get(key)).method_14894(chunk)) {
                continue;
            }
            CompoundTag start = data.get(chunk);
            if (start != null) {
                starts.put(key, start);
            }
        }
        structures.put("Starts", starts);
        level.put("Structures", structures);
        tag.put("Level", level);
        return tag;
    }

    public synchronized void method_14734(SavedDataStorage storage) {
        if (this.field_64685) {
            return;
        }
        for (String legacyKey : this.field_17658) {
            CompoundTag features = new CompoundTag();
            try {
                features = CbWallRegionStorage.readTagFromDisk(storage, legacyKey, DataFixTypes.SAVED_DATA_STRUCTURE_FEATURE_INDICES, 1493)
                    .getCompoundOrEmpty("data").getCompoundOrEmpty("Features");
                if (features.isEmpty()) {
                    continue;
                }
            } catch (IOException ignored) {
                // 1.21.11 goes on with no features when the file cannot be read
            }
            features.forEach(this::cacheFeature);
            String indexName = legacyKey + "_index";
            StructureFeatureIndexSavedData stored = storage.computeIfAbsent(StructureFeatureIndexSavedData.method_67412(indexName));
            if (stored.method_14898().isEmpty()) {
                StructureFeatureIndexSavedData index = new StructureFeatureIndexSavedData();
                this.field_14433.put(legacyKey, index);
                features.forEach((key, feature) -> {
                    if (feature instanceof CompoundTag compound) {
                        index.method_14896(ChunkPos.pack(compound.getIntOr("ChunkX", 0), compound.getIntOr("ChunkZ", 0)));
                    }
                });
            } else {
                this.field_14433.put(legacyKey, stored);
            }
        }
        this.field_64685 = true;
    }

    private void cacheFeature(String key, Tag tag) {
        if (!(tag instanceof CompoundTag feature)) {
            return;
        }
        long chunk = ChunkPos.pack(feature.getIntOr("ChunkX", 0), feature.getIntOr("ChunkZ", 0));
        ListTag children = feature.getListOrEmpty("Children");
        if (!children.isEmpty()) {
            Optional<String> childId = children.getCompound(0).flatMap(child -> child.getString("id"));
            childId.map(field_14436::get).ifPresent(id -> feature.putString("id", id));
        }
        feature.getString("id").ifPresent(id -> this.field_14432.computeIfAbsent(id, k -> new Long2ObjectOpenHashMap<>()).put(chunk, feature));
    }

    /**
     * 1.21.11 getLegacyTagFixer(dimension, storage, fixer): the overworld's handler over its six legacy files, the
     * nether's over Fortress, the end's over EndCity, LegacyTagFixer.EMPTY for any other dimension
     */
    public static Supplier<LegacyTagFixer> method_14745(ResourceKey<Level> dimension, Supplier<SavedDataStorage> storage, DataFixer fixer) {
        if (dimension == Level.OVERWORLD) {
            return () -> new LegacyStructureDataHandler(storage.get(),
                ImmutableList.of("Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"),
                ImmutableList.of("Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"),
                fixer);
        } else if (dimension == Level.NETHER) {
            List<String> keys = ImmutableList.of("Fortress");
            return () -> new LegacyStructureDataHandler(storage.get(), keys, keys, fixer);
        } else if (dimension == Level.END) {
            List<String> keys = ImmutableList.of("EndCity");
            return () -> new LegacyStructureDataHandler(storage.get(), keys, keys, fixer);
        } else {
            return LegacyTagFixer.field_63959;
        }
    }
}
