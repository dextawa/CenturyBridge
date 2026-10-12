package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Members of the item area whose erased descriptor survived 1.20.5 while the type arguments changed
 * (core/segments/damage-generic): the JVM links them, the first element 1.20.4 code reads is the
 * wrong type. Every converter here is adaptive -- a value already in the wanted form passes through,
 * because erasure cannot tell who handed it over -- and each pair undoes the other, so a value that
 * crosses twice comes back as the very object it was. Collections are converted as live views over
 * the original (vanilla keeps the view, the 1.20.4 code keeps its own collection): reads and the
 * writes vanilla makes go through to the one collection both sides hold.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class CbItemGenerics {

    // ---------------------------------------------------------------- codecs 1.20.5 keeps as registry files

    /**
     * LootItemFunctions.CODEC: 1.20.4's was the inline codec of a function (the dispatch on "function",
     * or a list as a sequence); 1.20.5's CODEC reads a registry reference or one, and the 1.20.4 one is
     * ROOT_CODEC, built the same way.
     */
    public static com.mojang.serialization.Codec itemFunctionCodec() {
        return net.minecraft.class_131.field_50023;
    }

    /** LootItemConditions.CODEC: 1.20.4's inline condition codec is 1.20.5's DIRECT_CODEC */
    public static com.mojang.serialization.Codec conditionCodec() {
        return net.minecraft.class_217.field_50031;
    }

    /** LootTable.CODEC: 1.20.4's codec of a table itself is 1.20.5's DIRECT_CODEC */
    public static com.mojang.serialization.Codec lootTableCodec() {
        return net.minecraft.class_52.field_50021;
    }

    /** Instrument.CODEC: 1.20.4's record codec of an instrument is 1.20.5's DIRECT_CODEC */
    public static com.mojang.serialization.Codec instrumentCodec() {
        return net.minecraft.class_7444.field_49260;
    }

    // ---------------------------------------------------------------- loot table ids (location <-> key)

    private static Object toLootKey(Object o) {
        return o instanceof net.minecraft.class_2960 rl ? CbItemHolders.lootTableKey(rl) : o;
    }

    private static Object toLocation(Object o) {
        return o instanceof net.minecraft.class_5321 k ? k.method_29177() : o;
    }

    private static boolean all(Iterable<?> it, Class<?> type) {
        for (Object o : it) {
            if (o != null && !type.isInstance(o)) {
                return false;
            }
        }
        return true;
    }

    /** a 1.20.4 list of loot table locations where 1.20.5 keeps loot table keys (AdvancementRewards) */
    public static List lootTableKeys(List list) {
        if (list == null || list instanceof LocationList) {
            return list == null ? null : ((LocationList) list).keys;
        }
        return all(list, net.minecraft.class_5321.class) ? list : new KeyList(list);
    }

    /** a 1.20.5 list of loot table keys where 1.20.4 code reads locations (AdvancementRewards.loot()) */
    public static List lootTableLocations(List list) {
        if (list == null || list instanceof KeyList) {
            return list == null ? null : ((KeyList) list).locations;
        }
        return all(list, net.minecraft.class_2960.class) ? list : new LocationList(list);
    }

    /** the keys a list of 1.20.4 locations stands for, read through */
    static final class KeyList extends AbstractList<Object> {
        final List locations;

        KeyList(List locations) {
            this.locations = locations;
        }

        @Override
        public Object get(int i) {
            return toLootKey(locations.get(i));
        }

        @Override
        public int size() {
            return locations.size();
        }
    }

    /** the locations a list of 1.20.5 keys names, read through */
    static final class LocationList extends AbstractList<Object> {
        final List keys;

        LocationList(List keys) {
            this.keys = keys;
        }

        @Override
        public Object get(int i) {
            return toLocation(keys.get(i));
        }

        @Override
        public int size() {
            return keys.size();
        }
    }

    /** BuiltInLootTables.all(): 1.20.5's set of keys as 1.20.4's set of locations, read through */
    public static Set lootTableLocationSet(Set set) {
        if (set == null || all(set, net.minecraft.class_2960.class)) {
            return set;
        }
        return new AbstractSet<Object>() {
            @Override
            public Iterator<Object> iterator() {
                Iterator it = set.iterator();
                return new Iterator<Object>() {
                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public Object next() {
                        return toLocation(it.next());
                    }
                };
            }

            @Override
            public boolean contains(Object o) {
                return set.contains(toLootKey(o));
            }

            @Override
            public int size() {
                return set.size();
            }
        };
    }

    /**
     * A 1.20.4 BiConsumer of (location, table builder) where 1.20.5 hands (key, builder)
     * (VanillaChestLoot.spawnerLootTables): the consumer gets the location the key names.
     */
    public static BiConsumer lootKeyConsumer(BiConsumer consumer) {
        if (consumer == null) {
            return null;
        }
        if (consumer instanceof LocationConsumer back) {
            return back.keys;
        }
        return new KeyConsumer(consumer);
    }

    /** the reverse of {@link #lootKeyConsumer}: a 1.20.5 (key, builder) consumer handed to 1.20.4 code */
    public static BiConsumer lootLocationConsumer(BiConsumer consumer) {
        if (consumer == null) {
            return null;
        }
        if (consumer instanceof KeyConsumer back) {
            return back.locations;
        }
        return new LocationConsumer(consumer);
    }

    static final class KeyConsumer implements BiConsumer<Object, Object> {
        final BiConsumer locations;

        KeyConsumer(BiConsumer locations) {
            this.locations = locations;
        }

        @Override
        public void accept(Object key, Object value) {
            locations.accept(toLocation(key), value);
        }
    }

    static final class LocationConsumer implements BiConsumer<Object, Object> {
        final BiConsumer keys;

        LocationConsumer(BiConsumer keys) {
            this.keys = keys;
        }

        @Override
        public void accept(Object location, Object value) {
            keys.accept(toLootKey(location), value);
        }
    }

    /**
     * BlockLootSubProvider's map: 1.20.4 passed (and kept, in the field "map") a map from loot table
     * location to builder, which 1.20.5 keys by loot table key. The map vanilla holds is a live view
     * of the 1.20.4 map: its puts, removes and reads go to the 1.20.4 map under the location.
     */
    public static Map lootTableKeyMap(Map map) {
        if (map == null) {
            return null;
        }
        if (map instanceof LocationMap back) {
            return back.keys;
        }
        return all(map.keySet(), net.minecraft.class_5321.class) && !map.isEmpty() ? map : new KeyMap(map);
    }

    /** the reverse of {@link #lootTableKeyMap}: the field read back as 1.20.4's location map */
    public static Map lootTableLocationMap(Map map) {
        if (map == null) {
            return null;
        }
        if (map instanceof KeyMap back) {
            return back.locations;
        }
        return new LocationMap(map);
    }

    /** BlockLootSubProvider.map (protected final) read through 1.20.4's type (instanceFieldGetters) */
    public static Map blockLootMap(net.minecraft.class_7788 provider) {
        return lootTableLocationMap(
            ((top.dext.centurybridge.step.v1_20_5.mixin.CbItemBlockLootAccessor) provider).cb$map());
    }

    /** a map keyed by one form of the id, seen keyed by the other; every operation goes to the base map */
    abstract static class IdMap extends AbstractMap<Object, Object> {
        final Map base;

        IdMap(Map base) {
            this.base = base;
        }

        abstract Object in(Object key);   // this view's key -> the base map's

        abstract Object out(Object key);  // the base map's key -> this view's

        @Override
        public Object get(Object key) {
            return base.get(in(key));
        }

        @Override
        public boolean containsKey(Object key) {
            return base.containsKey(in(key));
        }

        @Override
        public Object put(Object key, Object value) {
            return base.put(in(key), value);
        }

        @Override
        public Object remove(Object key) {
            return base.remove(in(key));
        }

        @Override
        public int size() {
            return base.size();
        }

        @Override
        public void clear() {
            base.clear();
        }

        @Override
        public Set<Entry<Object, Object>> entrySet() {
            return new AbstractSet<Entry<Object, Object>>() {
                @Override
                public Iterator<Entry<Object, Object>> iterator() {
                    Iterator<Entry> it = base.entrySet().iterator();
                    return new Iterator<Entry<Object, Object>>() {
                        @Override
                        public boolean hasNext() {
                            return it.hasNext();
                        }

                        @Override
                        public Entry<Object, Object> next() {
                            Entry e = it.next();
                            return new Entry<Object, Object>() {
                                @Override
                                public Object getKey() {
                                    return out(e.getKey());
                                }

                                @Override
                                public Object getValue() {
                                    return e.getValue();
                                }

                                @Override
                                public Object setValue(Object v) {
                                    return e.setValue(v);
                                }
                            };
                        }

                        @Override
                        public void remove() {
                            it.remove();
                        }
                    };
                }

                @Override
                public int size() {
                    return base.size();
                }
            };
        }
    }

    /** a 1.20.4 location map seen keyed by loot table keys */
    static final class KeyMap extends IdMap {
        final Map locations;

        KeyMap(Map locations) {
            super(locations);
            this.locations = locations;
        }

        @Override
        Object in(Object key) {
            return toLocation(key);
        }

        @Override
        Object out(Object key) {
            return toLootKey(key);
        }
    }

    /** a 1.20.5 key map seen keyed by locations */
    static final class LocationMap extends IdMap {
        final Map keys;

        LocationMap(Map keys) {
            super(keys);
            this.keys = keys;
        }

        @Override
        Object in(Object key) {
            return toLootKey(key);
        }

        @Override
        Object out(Object key) {
            return toLocation(key);
        }
    }

    // ---------------------------------------------------------------- recipes

    /**
     * RecipeManager.getRecipeFor(type, container, level, last): 1.20.4 answered the recipe with its id,
     * Optional&lt;Pair&lt;ResourceLocation, RecipeHolder&gt;&gt;, 1.20.5 answers Optional&lt;RecipeHolder&gt;;
     * the id is the holder's.
     */
    public static Optional recipeWithId(Optional result) {
        if (result == null || result.isEmpty()) {
            return result;
        }
        Object v = result.get();
        return v instanceof net.minecraft.class_8786 h ? Optional.of(Pair.of(h.comp_1932(), h)) : result;
    }

    /** the reverse of {@link #recipeWithId}: a 1.20.4 override's pair answered to 1.20.5 as its holder */
    public static Optional recipeOfPair(Optional result) {
        if (result == null || result.isEmpty()) {
            return result;
        }
        Object v = result.get();
        return v instanceof Pair p ? Optional.ofNullable(p.getSecond()) : result;
    }

    // ---------------------------------------------------------------- holders in collections

    /** EnchantmentHelper.isEnchantmentCompatible: 1.20.4's enchantments as 1.20.5's holders, read through */
    public static Collection enchantmentHolders(Collection enchantments) {
        if (enchantments == null || all(enchantments, net.minecraft.class_6880.class)) {
            return enchantments;
        }
        return com.google.common.collect.Collections2.transform(enchantments,
            e -> e instanceof net.minecraft.class_1887 en ? CbHolders.enchantment(en) : e);
    }

    /**
     * MobEffectsPredicate.matches: 1.20.4's map of active effects keyed by effect, seen keyed by the
     * effects' holders as 1.20.5 looks them up.
     */
    public static Map effectHolderMap(Map effects) {
        if (effects == null || effects.isEmpty() || all(effects.keySet(), net.minecraft.class_6880.class)) {
            return effects;
        }
        return new IdMap(effects) {
            @Override
            Object in(Object key) {
                return key instanceof net.minecraft.class_6880 h ? h.comp_349() : key;
            }

            @Override
            Object out(Object key) {
                return key instanceof net.minecraft.class_1291 e ? CbHolders.mobEffect(e) : key;
            }
        };
    }

    // ---------------------------------------------------------------- location predicates

    /** LocationPredicate: 1.20.4's optional biome key as 1.20.5's optional biome set (see {@link KeySet}) */
    public static Optional biomeSet(Optional biome) {
        return keySet(biome, net.minecraft.class_7924.field_41236);
    }

    /** LocationPredicate: 1.20.4's optional structure key as 1.20.5's optional structure set */
    public static Optional structureSet(Optional structure) {
        return keySet(structure, net.minecraft.class_7924.field_41246);
    }

    private static Optional keySet(Optional o, net.minecraft.class_5321 registry) {
        if (o == null || o.isEmpty() || !(o.get() instanceof net.minecraft.class_5321 key)) {
            return o;
        }
        return Optional.of(new KeySet(registry, key));
    }

    /**
     * The one-entry set a 1.20.4 key stands for. 1.20.4's LocationPredicate matched a biome or structure
     * by its key; this set answers contains(holder) by the holder's key the same way, whichever registry
     * instance the holder comes from. Its contents (stream, get, unwrap -- what the codec encodes) are the
     * entry of that key in the running world's registries (CbRegistries); with no world loaded the
     * datapack registries are not there and the set has no element to encode.
     */
    public static final class KeySet implements net.minecraft.class_6885<Object> {
        final net.minecraft.class_5321 registry;
        final net.minecraft.class_5321 key;

        KeySet(net.minecraft.class_5321 registry, net.minecraft.class_5321 key) {
            this.registry = registry;
            this.key = key;
        }

        /** the key 1.20.4 code handed over */
        public net.minecraft.class_5321 key() {
            return key;
        }

        private Optional<net.minecraft.class_6880<Object>> resolve() {
            Optional<net.minecraft.class_2378> r = CbRegistries.currentAccess().method_33310(registry);
            return r.isEmpty() ? Optional.empty() : (Optional) r.get().method_40264(key);
        }

        @Override
        public Stream<net.minecraft.class_6880<Object>> method_40239() {
            return resolve().stream();
        }

        @Override
        public int method_40247() {
            return resolve().isPresent() ? 1 : 0;
        }

        @Override
        public Either<net.minecraft.class_6862<Object>, List<net.minecraft.class_6880<Object>>> method_40248() {
            return Either.right(resolve().map(List::of).orElse(List.of()));
        }

        @Override
        public Optional<net.minecraft.class_6880<Object>> method_40243(net.minecraft.class_5819 random) {
            return resolve();
        }

        @Override
        public net.minecraft.class_6880<Object> method_40240(int index) {
            return resolve().filter(h -> index == 0).orElseThrow(() -> new IndexOutOfBoundsException(index));
        }

        @Override
        public boolean method_40241(net.minecraft.class_6880<Object> holder) {
            return holder != null && holder.method_40225(key);
        }

        @Override
        public boolean method_46768(net.minecraft.class_7876<Object> owner) {
            return resolve().map(h -> h.method_46745(owner)).orElse(false);
        }

        @Override
        public Optional<net.minecraft.class_6862<Object>> method_45925() {
            return Optional.empty();
        }

        @Override
        public Iterator<net.minecraft.class_6880<Object>> iterator() {
            return method_40239().iterator();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof KeySet k && k.key.equals(key);
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public String toString() {
            return "KeySet[" + key + "]";
        }
    }

    private CbItemGenerics() {
    }
}
