package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import top.dext.centurybridge.step.v1_20_5.facade.LootDataId;
import top.dext.centurybridge.step.v1_20_5.facade.LootDataManager;
import top.dext.centurybridge.step.v1_20_5.facade.LootDataResolver;

/**
 * 1.20.4's loot data API on 1.20.5's. 1.20.4 kept predicates, item modifiers and loot tables in a
 * LootDataManager (a reload listener of its own) and handed code a LootDataResolver to look them up by
 * LootDataId(type, location); 1.20.5 loads them as the server's reloadable registries and hands a
 * registry lookup (HolderGetter.Provider) naming each element by its ResourceKey. The three 1.20.4
 * types are facades (classRenames); this class turns them into 1.20.5's and back:
 * <ul>
 * <li>{@link #manager}: a 1.20.4 manager over any lookup provider holding the loot registries (the
 *     server's: {@link #server}); {@link #resolver}: a 1.20.4 resolver over a 1.20.5 lookup;</li>
 * <li>{@link #lookup}: the reverse -- a resolver made from a 1.20.5 lookup gives that lookup back, a
 *     1.20.4 resolver of a mod's own is asked element by element;</li>
 * <li>{@link #key} / {@link #id}: a LootDataId and the registry key naming the same element.</li>
 * </ul>
 * A manager answers the empty loot table's id with LootTable.EMPTY, as every 1.20.4 manager did, unless
 * the registries hold a table of that name (which the running game then uses too).
 */
public final class CbLootData {

    /** what a 1.20.4 manager answers from */
    public interface View {
        /** the element, or null */
        Object element(net.minecraft.class_8490<?> type, net.minecraft.class_2960 location);

        /** the ids of one type's elements */
        Collection<net.minecraft.class_2960> keys(net.minecraft.class_8490<?> type);

        /** the same data as a 1.20.5 lookup */
        net.minecraft.class_7871.class_7872 lookup();
    }

    /** a view with no elements: a 1.20.4 manager before its first reload */
    public static final View EMPTY = new View() {
        @Override
        public Object element(net.minecraft.class_8490<?> type, net.minecraft.class_2960 location) {
            return emptyTable(type, location);
        }

        @Override
        public Collection<net.minecraft.class_2960> keys(net.minecraft.class_8490<?> type) {
            return List.of();
        }

        @Override
        public net.minecraft.class_7871.class_7872 lookup() {
            return new LegacyLookup(new LootDataManager(this));
        }
    };

    /** the empty loot table, for its id (null for any other) */
    private static Object emptyTable(net.minecraft.class_8490<?> type, net.minecraft.class_2960 location) {
        return type == net.minecraft.class_8490.field_44498 && net.minecraft.class_39.field_844.method_29177().equals(location)
            ? net.minecraft.class_52.field_948 : null;
    }

    /** a view over a 1.20.5 lookup provider holding the loot registries */
    private static final class LookupView implements View {
        private final net.minecraft.class_7225.class_7874 provider;

        LookupView(net.minecraft.class_7225.class_7874 provider) {
            this.provider = provider;
        }

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public Object element(net.minecraft.class_8490<?> type, net.minecraft.class_2960 location) {
            net.minecraft.class_5321 registry = type.comp_2519();
            Optional lookup = provider.method_46759(registry);
            Object value = lookup.flatMap(l -> ((net.minecraft.class_7225.class_7226) l).method_46746(net.minecraft.class_5321.method_29179(registry, location)))
                .map(h -> ((net.minecraft.class_6880) h).comp_349()).orElse(null);
            return value != null ? value : emptyTable(type, location);
        }

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public Collection<net.minecraft.class_2960> keys(net.minecraft.class_8490<?> type) {
            Optional lookup = provider.method_46759((net.minecraft.class_5321) type.comp_2519());
            return (Collection<net.minecraft.class_2960>) lookup.map(l -> ((java.util.stream.Stream<net.minecraft.class_5321>) ((net.minecraft.class_7225.class_7226) l).method_46754())
                .map(net.minecraft.class_5321::method_29177).toList()).orElse(List.of());
        }

        @Override
        public net.minecraft.class_7871.class_7872 lookup() {
            return provider.method_46758();
        }
    }

    // ---------------------------------------------------------------- 1.20.5 -> 1.20.4

    /** a 1.20.4 LootDataManager answering from a 1.20.5 lookup provider that holds the loot registries */
    public static LootDataManager manager(net.minecraft.class_7225.class_7874 provider) {
        return new LootDataManager(new LookupView(provider));
    }

    /** the server's loot data as a 1.20.4 LootDataManager (1.20.4's server.getLootData()) */
    public static LootDataManager server(net.minecraft.server.MinecraftServer server) {
        return manager(server.method_58576().method_58289());
    }

    /** a 1.20.4 resolver over a 1.20.5 lookup (what a ValidationContext or LootContext holds) */
    public static LootDataResolver resolver(net.minecraft.class_7871.class_7872 lookup) {
        if (lookup == null) {
            return null;
        }
        if (lookup instanceof LegacyLookup l) {
            return l.resolver;
        }
        return new Resolver(lookup);
    }

    /** a 1.20.4 resolver over a 1.20.5 lookup */
    public static final class Resolver implements LootDataResolver {
        private final net.minecraft.class_7871.class_7872 lookup;

        Resolver(net.minecraft.class_7871.class_7872 lookup) {
            this.lookup = lookup;
        }

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public <T> T getElement(LootDataId<T> id) {
            net.minecraft.class_5321 registry = id.comp_1474.comp_2519();
            Optional getter = lookup.method_46750(registry);
            Object value = getter.flatMap(g -> ((net.minecraft.class_7871) g).method_46746(net.minecraft.class_5321.method_29179(registry, id.comp_1475)))
                .map(h -> ((net.minecraft.class_6880) h).comp_349()).orElse(null);
            return (T) (value != null ? value : emptyTable(id.comp_1474, id.comp_1475));
        }
    }

    // ---------------------------------------------------------------- 1.20.4 -> 1.20.5

    /**
     * A 1.20.4 resolver as 1.20.5's lookup: one made from a 1.20.5 lookup gives that lookup back; any
     * other (a mod's own) is asked element by element, through holders bound to what it answers.
     */
    public static net.minecraft.class_7871.class_7872 lookup(LootDataResolver resolver) {
        if (resolver == null) {
            return null;
        }
        if (resolver instanceof Resolver r) {
            return r.lookup;
        }
        if (resolver instanceof LootDataManager m) {
            return m.cbView().lookup();
        }
        return new LegacyLookup(resolver);
    }

    /** a 1.20.4 resolver asked by 1.20.5 code */
    static final class LegacyLookup implements net.minecraft.class_7871.class_7872 {
        final LootDataResolver resolver;

        LegacyLookup(LootDataResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        @SuppressWarnings({"rawtypes", "unchecked"})
        public Optional method_46750(net.minecraft.class_5321 registry) {
            net.minecraft.class_8490<?> type = typeOfRegistry(registry);
            return type == null ? Optional.empty() : Optional.of(new LegacyGetter(resolver, type));
        }
    }

    /** one loot data type of a 1.20.4 resolver, as 1.20.5's HolderGetter (1.20.4 loot data had no tags) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static final class LegacyGetter implements net.minecraft.class_7871, net.minecraft.class_7876 {
        private final LootDataResolver resolver;
        private final net.minecraft.class_8490 type;

        LegacyGetter(LootDataResolver resolver, net.minecraft.class_8490 type) {
            this.resolver = resolver;
            this.type = type;
        }

        @Override
        public Optional method_46746(net.minecraft.class_5321 key) {
            Object value = resolver.getElement(new LootDataId(type, key.method_29177()));
            return value == null ? Optional.empty() : Optional.of(new Ref(this, key, value));
        }

        @Override
        public Optional method_46733(net.minecraft.class_6862 tag) {
            return Optional.empty();
        }
    }

    /** a holder bound to the element a 1.20.4 resolver answered, under its key */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static final class Ref extends net.minecraft.class_6880.class_6883 {
        Ref(net.minecraft.class_7876 owner, net.minecraft.class_5321 key, Object value) {
            super(net.minecraft.class_6880.class_6883.class_6884.field_36454, owner, key, value);
        }
    }

    // ---------------------------------------------------------------- ids and keys

    /** the loot data type whose registry this is, or null */
    @SuppressWarnings("rawtypes")
    static net.minecraft.class_8490<?> typeOfRegistry(net.minecraft.class_5321 registry) {
        for (net.minecraft.class_8490<?> type : net.minecraft.class_8490.method_51215().toList()) {
            if (type.comp_2519().equals(registry)) {
                return type;
            }
        }
        return null;
    }

    /** a LootDataId as the registry key of the same element */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_5321 key(LootDataId<?> id) {
        return id == null ? null : net.minecraft.class_5321.method_29179(id.comp_1474.comp_2519(), id.comp_1475);
    }

    /** the LootDataId of an element of that type with that key */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static LootDataId<?> id(net.minecraft.class_8490 type, net.minecraft.class_5321 key) {
        return key == null ? null : new LootDataId(type, key.method_29177());
    }

    /** the LootDataId of a loot data element's registry key (its type read off the key's registry) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static LootDataId<?> idOfKey(net.minecraft.class_5321 key) {
        if (key == null) {
            return null;
        }
        net.minecraft.class_8490<?> type = typeOfRegistry(key.method_58273());
        if (type == null) {
            throw new IllegalArgumentException("not a loot data element's key: " + key);
        }
        return new LootDataId(type, key.method_29177());
    }

    // ---------------------------------------------------------------- LootDataType

    /** type.directory(): 1.20.5 keeps it as a record component (the same directories) */
    public static String directory(net.minecraft.class_8490<?> type) {
        return type.comp_2521();
    }

    /**
     * The ops 1.20.4's type.deserialize(id, json) parsed with: 1.20.4's loot codecs read plain JSON, 1.20.5's
     * read registry entries and tags through registry ops -- the running world's registries.
     */
    public static com.mojang.serialization.DynamicOps<com.google.gson.JsonElement> jsonOps() {
        return CbRegistries.current().method_57093(com.mojang.serialization.JsonOps.INSTANCE);
    }

    /** a 1.20.4 Validator.run(context, id, element) (lambda adapter) */
    @FunctionalInterface
    public interface LegacyValidator {
        void run(net.minecraft.class_58 context, LootDataId<?> id, Object element);
    }

    /** a 1.20.4 validator as 1.20.5's, which names the element by its key */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_8490.class_8491 validator(LegacyValidator legacy) {
        return (context, key, element) -> legacy.run(context, idOfKey(key), element);
    }

    // ---------------------------------------------------------------- loading

    /**
     * What a 1.20.4 manager's reload makes of resources: 1.20.5 loads loot data as the reloadable
     * registries layered over the others -- the running server's when there is one, else the static
     * built-in layer alone (data naming a worldgen registry entry then fails to parse, with its error).
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static CompletableFuture<View> load(net.minecraft.class_3300 resources, Executor background) {
        net.minecraft.server.MinecraftServer server = CbRegistries.server();
        net.minecraft.class_7780 layers = server != null ? server.method_46221() : net.minecraft.class_7659.method_45139();
        return ((CompletableFuture<net.minecraft.class_7780>) net.minecraft.class_9383.method_58284(layers, resources, background))
            .thenApply(l -> new LookupView(l.method_45926()));
    }

    private CbLootData() {
    }
}
