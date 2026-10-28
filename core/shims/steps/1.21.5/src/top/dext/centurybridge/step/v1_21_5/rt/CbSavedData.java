package top.dext.centurybridge.step.v1_21_5.rt;

import com.google.common.collect.MapMaker;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.class_18;
import net.minecraft.class_22;
import net.minecraft.class_2487;
import net.minecraft.class_2509;
import net.minecraft.class_2512;
import net.minecraft.class_2520;
import net.minecraft.class_26;
import net.minecraft.class_273;
import net.minecraft.class_2995;
import net.minecraft.class_3218;
import net.minecraft.class_3440;
import net.minecraft.class_3767;
import net.minecraft.class_3978;
import net.minecraft.class_4284;
import net.minecraft.class_7225;
import net.minecraft.class_8565;
import net.minecraft.class_9209;
import net.minecraft.class_10741;
import top.dext.centurybridge.step.v1_21_5.facade.SavedData;

/**
 * 1.21.4's DimensionDataStorage calls on 1.21.5's.
 *
 * 1.21.4 (javap class_26): computeIfAbsent(factory, name) / get(factory, name) / set(name, data), the cache keyed by
 * the file name; a load decodes the file's "data" compound with the factory's deserializer and the storage's
 * registries, a save writes data.save(registries) ({"data": save(new CompoundTag(), registries), DataVersion}).
 * 1.21.5 (javap class_26, class_10741): the same calls take a SavedDataType(id, constructor, codec, DataFixTypes), the
 * cache keyed by the type (a record: equal types share an entry); a load parses the file's "data" with the type's
 * codec through RegistryOps of the storage's registries, a save encodes the data into "data" (and the DataVersion).
 *
 * A converted mod's data (facade SavedData) gets one SavedDataType per storage and file name, whose constructor and
 * codec use the factory last given for that name: 1.21.5's cache then holds one entry per file as 1.21.4's did, the
 * codec decodes with the factory's deserializer and encodes with the data's save(registries). The registries are the
 * storage's own (class_26.field_48926, opened by the layer access widener), which 1.21.4's storage handed both. Vanilla's own factories (ForcedChunks, Raids, MapIndex, ... factory()) answer a Factory
 * standing for vanilla's 1.21.5 SavedDataType of the file name.
 */
public final class CbSavedData {
    private CbSavedData() {
    }

    /** one per storage and file name: the 1.21.5 type of a converted mod's data file */
    private static final class Legacy {
        volatile SavedData.Factory<?> factory;
        final class_10741<class_18> type;
        /** the storage's own registries: what 1.21.4's storage handed the deserializer and save */
        private final class_7225.class_7874 registries;

        Legacy(class_26 storage, String name, class_4284 fixType) {
            this.registries = storage.field_48926;
            this.type = new class_10741<>(name, this::create, this::codec, fixType);
        }

        private class_18 create(class_18.class_10740 ctx) {
            SavedData.Factory<?> f = this.factory;
            if (f == null) {
                throw new IllegalStateException("saved data " + this.type.comp_3641() + " was set without a factory and has none to construct it");
            }
            return f.comp_1590().get();
        }

        private Codec<class_18> codec(class_18.class_10740 ctx) {
            class_7225.class_7874 registries = this.registries;
            Legacy self = this;
            return new Codec<>() {
                @Override
                public <T> DataResult<Pair<class_18, T>> decode(DynamicOps<T> ops, T input) {
                    SavedData.Factory<?> f = self.factory;
                    if (f == null) {
                        return DataResult.error(() -> "no factory for saved data " + self.type.comp_3641());
                    }
                    class_2520 nbt = ops.convertTo(class_2509.field_11560, input);
                    if (!(nbt instanceof class_2487 tag)) {
                        return DataResult.error(() -> "saved data " + self.type.comp_3641() + " is not a compound: " + nbt);
                    }
                    return DataResult.success(Pair.of(f.comp_1591().apply(tag, registries), ops.empty()));
                }

                @Override
                public <T> DataResult<T> encode(class_18 data, DynamicOps<T> ops, T prefix) {
                    if (!(data instanceof SavedData legacy)) {
                        return DataResult.error(() -> "saved data " + self.type.comp_3641() + " is not 1.21.4 saved data: " + data);
                    }
                    // 1.21.4 wrote data.save(registries) and the storage read its "data" back on load
                    class_2520 inner = legacy.method_17919(registries).method_10580("data");
                    return DataResult.success(class_2509.field_11560.method_29146(ops, inner == null ? new class_2487() : inner));
                }
            };
        }
    }

    private static final Map<class_26, Map<String, Legacy>> LEGACY = new MapMaker().weakKeys().makeMap();

    /** the Factory objects vanilla's factory() methods answer: file name -> vanilla's 1.21.5 type */
    private static final Map<SavedData.Factory<?>, Function<String, class_10741<?>>> VANILLA = new MapMaker().weakKeys().makeMap();

    private static Legacy legacy(class_26 storage, String name, class_4284 fixType) {
        return LEGACY.computeIfAbsent(storage, s -> new ConcurrentHashMap<>()).computeIfAbsent(name, n -> new Legacy(storage, n, fixType));
    }

    /** the SavedData.Context of a storage a converted mod builds itself (1.21.4's constructor had none): no level, seed 0 */
    public static class_18.class_10740 detachedContext() {
        return new class_18.class_10740(null, 0L);
    }

    @SuppressWarnings("unchecked")
    private static <T extends class_18> class_10741<T> typeFor(class_26 storage, SavedData.Factory<T> factory, String name) {
        Function<String, class_10741<?>> vanilla = VANILLA.get(factory);
        if (vanilla != null) {
            return (class_10741<T>) vanilla.apply(name);
        }
        Legacy l = legacy(storage, name, factory.comp_1592());
        l.factory = factory;
        return (class_10741<T>) (class_10741<?>) l.type;
    }

    /** DimensionDataStorage.computeIfAbsent(factory, name) */
    public static <T extends class_18> T computeIfAbsent(class_26 storage, SavedData.Factory<T> factory, String name) {
        return storage.method_17924(typeFor(storage, factory, name));
    }

    /** DimensionDataStorage.get(factory, name) */
    public static <T extends class_18> T get(class_26 storage, SavedData.Factory<T> factory, String name) {
        return storage.method_20786(typeFor(storage, factory, name));
    }

    /** DimensionDataStorage.set(name, data) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void set(class_26 storage, String name, class_18 data) {
        class_10741 type = vanillaTypeOf(name, data);
        if (type == null) {
            Map<String, Legacy> byName = LEGACY.computeIfAbsent(storage, s -> new ConcurrentHashMap<>());
            Legacy l = byName.get(name);
            // set before any computeIfAbsent of the name: 1.21.4 cached it by name; the type is completed by the
            // first factory given for the name (Legacy.factory), and its DataFixTypes is unknown until then -- the
            // cached entry is what later calls answer, as 1.21.4's cache was
            type = (l != null ? l : legacy(storage, name, null)).type;
        }
        storage.method_123(type, data);
    }

    /**
     * SavedData.save(tag, registries) on any saved data: a converted mod's (facade SavedData) runs its own save; vanilla's
     * 1.21.5 data has no save of its own, its type's codec writes it (what 1.21.5's storage puts under "data"), merged
     * into tag as 1.21.4's save wrote its fields into tag. The type's codec is the same for every file of a class
     * (StructureFeatureIndexSavedData / MapItemSavedData types differ in id only), so the class picks it. Scoreboard
     * data's codec reads the scoreboard from the storage level, which a bare save has none of: refused (a loss).
     */
    public static class_2487 method_75(class_18 data, class_2487 tag, class_7225.class_7874 registries) {
        if (data instanceof SavedData legacy) {
            return legacy.method_75(tag, registries);
        }
        class_10741<?> type = data instanceof class_3440 ? STRUCTURE_INDEX_SAMPLE : data instanceof class_22 ? MAPS_SAMPLE
            : data instanceof class_3767 ? class_3767.field_56443 : vanillaTypeOf("", data);
        if (type == null || data instanceof class_273) {
            throw new UnsupportedOperationException("1.21.5 writes " + data.getClass().getName() + " through its storage type only");
        }
        @SuppressWarnings("unchecked")
        Codec<class_18> codec = (Codec<class_18>) type.comp_3643().apply(type == class_8565.field_56420
            ? new class_18.class_10740(null, 0L) : null);
        class_2520 out = codec.encodeStart(registries.method_57093(class_2509.field_11560), data).getOrThrow(IllegalStateException::new);
        if (out instanceof class_2487 compound) {
            tag.method_10543(compound);
        }
        return tag;
    }

    /** SavedData.save(registries): {"data": save(new CompoundTag(), registries), "DataVersion": current}, as 1.21.4's */
    public static class_2487 method_17919(class_18 data, class_7225.class_7874 registries) {
        if (data instanceof SavedData legacy) {
            return legacy.method_17919(registries);
        }
        class_2487 tag = new class_2487();
        tag.method_10566("data", method_75(data, new class_2487(), registries));
        class_2512.method_48310(tag);
        return tag;
    }

    private static final class_10741<class_3440> STRUCTURE_INDEX_SAMPLE = class_3440.method_67412("cb_structure_index");
    private static final class_10741<class_22> MAPS_SAMPLE = class_22.method_67425(new class_9209(0));

    /** vanilla's 1.21.5 type for vanilla saved data stored under a 1.21.4 file name, null for any other data */
    private static class_10741<?> vanillaTypeOf(String name, class_18 data) {
        if (data instanceof class_273) {
            return class_2995.field_56414;
        }
        if (data instanceof class_8565) {
            return class_8565.field_56420;
        }
        if (data instanceof class_3767) {
            return raids(name);
        }
        if (data instanceof class_3440) {
            return class_3440.method_67412(name);
        }
        if (data instanceof class_3978) {
            return class_3978.field_56475;
        }
        if (data instanceof class_22) {
            return mapType(name);
        }
        return null;
    }

    private static class_10741<class_3767> raids(String name) {
        return class_3767.field_56444.comp_3641().equals(name) ? class_3767.field_56444 : class_3767.field_56443;
    }

    private static class_10741<class_22> mapType(String name) {
        // 1.21.4 MapId.key() is "map_" + id; 1.21.5's type for a map takes its MapId (class_22.method_67425)
        if (!name.startsWith("map_")) {
            throw new IllegalArgumentException("not a map data file name: " + name);
        }
        return class_22.method_67425(new class_9209(Integer.parseInt(name.substring(4))));
    }

    /**
     * A Factory standing for vanilla's 1.21.5 type of a file name (DimensionDataStorage calls take that type). Its own
     * constructor / deserializer are 1.21.5's for the type (built with ctx, the Context the type reads; vanilla's
     * Supplier-form types ignore it), so a mod calling them directly gets what 1.21.4's factory built.
     */
    @SuppressWarnings("unchecked")
    private static <T extends class_18> SavedData.Factory<T> vanilla(Function<String, class_10741<?>> types, class_10741<T> sample,
                                                                   class_18.class_10740 ctx) {
        SavedData.Factory<T> f = new SavedData.Factory<>(
            () -> sample.comp_3642().apply(ctx),
            (tag, registries) -> sample.comp_3643().apply(ctx).parse(registries.method_57093(class_2509.field_11560), tag)
                .getOrThrow(IllegalStateException::new),
            sample.comp_3644());
        VANILLA.put(f, types);
        return f;
    }

    private static final SavedData.Factory<class_3767> RAIDS = vanilla(CbSavedData::raids, class_3767.field_56443, null);
    private static final SavedData.Factory<class_3440> STRUCTURE_INDEX = vanilla(class_3440::method_67412, class_3440.method_67412("cb_structure_index"), null);
    private static final SavedData.Factory<class_3978> MAP_INDEX = vanilla(n -> class_3978.field_56475, class_3978.field_56475, null);
    private static final SavedData.Factory<class_22> MAPS = vanilla(CbSavedData::mapType, class_22.method_67425(new class_9209(0)), null);

    /**
     * ServerScoreboard.dataFactory(): 1.21.4 built the scoreboard's data (createData) and loaded it into the
     * scoreboard. 1.21.5's type reads the scoreboard from the storage level; the Factory's own constructor is the
     * scoreboard's createData, its deserializer goes through the scoreboard type of a storage only (a direct call is
     * refused: recorded as a loss).
     */
    public static SavedData.Factory<class_273> method_52297(class_2995 scoreboard) {
        SavedData.Factory<class_273> f = new SavedData.Factory<>(scoreboard::method_32705,
            (tag, registries) -> {
                throw new UnsupportedOperationException("1.21.5 loads scoreboard data through the storage (DimensionDataStorage), not a factory");
            },
            class_2995.field_56414.comp_3644());
        VANILLA.put(f, n -> class_2995.field_56414);
        return f;
    }

    /** RandomSequences.factory(seed): 1.21.5's type builds them from the Context's world seed, here the seed vanilla passed */
    public static SavedData.Factory<class_8565> method_52511(long seed) {
        return vanilla(n -> class_8565.field_56420, class_8565.field_56420, new class_18.class_10740(null, seed));
    }

    /** Raids.factory(level) */
    public static SavedData.Factory<class_3767> method_52559(class_3218 level) {
        return RAIDS;
    }

    /** StructureFeatureIndexSavedData.factory() */
    public static SavedData.Factory<class_3440> method_52601() {
        return STRUCTURE_INDEX;
    }

    /** MapIndex.factory() */
    public static SavedData.Factory<class_3978> method_52610() {
        return MAP_INDEX;
    }

    /** MapItemSavedData.factory() */
    public static SavedData.Factory<class_22> method_52611() {
        return MAPS;
    }
}
