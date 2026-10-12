package top.dext.centurybridge.step.v1_21.rt;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Builtin registries 1.21 turned into world registries: in 1.20.6 enchantments and painting variants were
 * objects registered in static registries (BuiltInRegistries.ENCHANTMENT, .PAINTING_VARIANT); in 1.21 they
 * are records each world loads from its datapacks into a dynamic registry of the same registry key
 * (minecraft:enchantment, minecraft:painting_variant), the server's sent to its clients.
 *
 * <p><b>The stand-in.</b> A 1.20.6 mod reads the static registry as a {@link Legacy} (a fieldGetter of the
 * BuiltInRegistries field): a registry of 1.20.6 objects (facades of the dead classes) under the same key,
 * which the mod registers into where it did in 1.20.6. Every object stands for the world entry of its key.
 * <ul>
 * <li>registration window: 1.20.6 froze its builtin registries after start-up and refused later
 *     registrations; the stand-in refuses a mod's registration once a world has loaded the registry (that
 *     world's entries are made then and never again), with 1.20.6's message;</li>
 * <li>a world loads the registry from its datapacks (a server): before it freezes, every object a mod
 *     registered gets the 1.21 entry its stand-in builds for it ({@link Legacy#recordFor}) unless the
 *     datapacks define that key themselves; the client receives those entries from the server;</li>
 * <li>a world entry no registered object stands for (a datapack's) gets the object its stand-in makes for
 *     it ({@link Legacy#newFacade}) on first sight, registered under its key;</li>
 * <li>raw ids: 1.20.6 synchronised the builtin registry's ids from server to client; the stand-in
 *     answers getId / byId with the world registry's, which 1.21 synchronises the same way;</li>
 * <li>tags: 1.20.6 bound the datapacks' tags of the builtin registry; the stand-in binds the world's tags
 *     over its objects' holders when the world binds them (a stand-in may add its own entries to the
 *     world's tags first: {@link Legacy#augmentTags}).</li>
 * </ul>
 *
 * <p><b>The worlds.</b> A world registry belongs to the server (a dedicated server's, the integrated one's)
 * or to the client (built from what the server sent). {@link #current} answers the client's on the
 * client's main thread, the server's anywhere else (its own thread, the world generation workers), the
 * client's when there is no server here. Every world registry is recorded as it loads
 * (CbLegacyRegistryLoaderMixin on RegistryDataLoader.Loader) and dropped when its world goes (the
 * integrated server stops: CbServers.onStopped; the client disconnects: CbLegacyRegistryClientMixin).
 */
public final class CbLegacyRegistries {

    private CbLegacyRegistries() {
    }

    /** a registry key's world registries */
    private static final class Worlds {
        volatile net.minecraft.class_2378<?> server;
        volatile net.minecraft.class_2378<?> client;
        volatile Thread clientThread;
        /** a world has loaded this registry: 1.20.6's builtin registry would be frozen by now */
        volatile boolean loaded;
    }

    private static final Map<net.minecraft.class_5321<?>, Worlds> WORLDS = new ConcurrentHashMap<>();
    private static final Map<net.minecraft.class_5321<?>, Legacy<?, ?>> STANDINS = new ConcurrentHashMap<>();

    static {
        // the integrated server's registries are no world's once it stops (CbServers)
        CbServers.onStopped(server -> onServerStopped());
    }

    private static Worlds worlds(net.minecraft.class_5321<?> key) {
        return WORLDS.computeIfAbsent(key, k -> new Worlds());
    }

    /** the world registry of that key this thread works for (see the class comment), or null */
    @SuppressWarnings("unchecked")
    public static <W> net.minecraft.class_2378<W> current(net.minecraft.class_5321<? extends net.minecraft.class_2378<W>> key) {
        Worlds w = WORLDS.get(key);
        if (w == null) {
            return null;
        }
        net.minecraft.class_2378<?> c = w.client;
        if (c != null && Thread.currentThread() == w.clientThread) {
            return (net.minecraft.class_2378<W>) c;
        }
        net.minecraft.class_2378<?> s = w.server;
        return (net.minecraft.class_2378<W>) (s != null ? s : c);
    }

    /** {@link #current}, or an IllegalStateException naming what needed it */
    public static <W> net.minecraft.class_2378<W> requireWorld(net.minecraft.class_5321<? extends net.minecraft.class_2378<W>> key, String what) {
        net.minecraft.class_2378<W> w = current(key);
        if (w == null) {
            throw new IllegalStateException("no world's " + key.method_29177() + " registry is loaded: " + what + " exists only in a world's"
                + " registries, once a world (or a server's registry data) has loaded");
        }
        return w;
    }

    /** whether a world has loaded the registry of that key */
    public static boolean loaded(net.minecraft.class_5321<?> key) {
        Worlds w = WORLDS.get(key);
        return w != null && w.loaded;
    }

    /**
     * CenturyBridge's stand-in for a builtin registry of 1.20.6 objects (V) whose entries 1.21 keeps in a
     * world registry of 1.21 values (W) under the same key. A subclass says how a mod's object becomes a
     * world entry and what object a world entry gets when none was registered for it.
     */
    public abstract static class Legacy<V, W> extends net.minecraft.class_2370<V> {
        private final net.minecraft.class_5321<net.minecraft.class_2378<W>> worldKey;
        /** CenturyBridge's own registrations (a world entry's new object) bypass the window */
        private final ThreadLocal<Boolean> cbAdding = ThreadLocal.withInitial(() -> Boolean.FALSE);

        @SuppressWarnings({"unchecked", "rawtypes"})
        protected Legacy(net.minecraft.class_5321<? extends net.minecraft.class_2378<V>> key, boolean intrusiveHolders) {
            super(key, com.mojang.serialization.Lifecycle.stable(), intrusiveHolders);
            this.worldKey = (net.minecraft.class_5321) key;
            if (STANDINS.putIfAbsent(key, this) != null) {
                throw new IllegalStateException("a second stand-in for " + key.method_29177());
            }
        }

        /**
         * The world entry a 1.20.6 object registered under {@code key} gets when a world loads the
         * registry from its datapacks, or null when it gets none (an object standing for one of vanilla's
         * own entries, which the world's data defines). {@code lookup} reaches the registries the world
         * has loaded so far.
         */
        protected abstract W recordFor(V value, net.minecraft.class_5321<V> key, net.minecraft.class_6903.class_7863 lookup);

        /** the object a world entry gets when none was registered for its key (a datapack's), or null when none is made */
        protected abstract V newFacade(net.minecraft.class_5321<W> key);

        /** the tags a world registry binds, with entries of the stand-in's added (default: as they are) */
        @SuppressWarnings("rawtypes")
        protected Map augmentTags(net.minecraft.class_2378<W> world, Map tags) {
            return tags;
        }

        /** the key of the world registry (the stand-in's own) */
        public net.minecraft.class_5321<net.minecraft.class_2378<W>> worldKey() {
            return worldKey;
        }

        /** register: refused once a world has loaded the registry, as 1.20.6's frozen registry refused it */
        @Override
        public net.minecraft.class_6880.class_6883<V> method_10272(net.minecraft.class_5321<V> key, V value, net.minecraft.class_9248 info) {
            if (loaded(worldKey) && !cbAdding.get()) {
                throw new IllegalStateException("Registry is already frozen (trying to add key " + key + ")");
            }
            return super.method_10272(key, value, info);
        }

        /** CenturyBridge's own registration of an object (a vanilla object at bootstrap, a world entry's new object), outside the window */
        public net.minecraft.class_6880.class_6883<V> cbRegister(net.minecraft.class_5321<V> key, V value) {
            cbAdding.set(Boolean.TRUE);
            try {
                return super.method_10272(key, value, net.minecraft.class_9248.field_49136);
            } finally {
                cbAdding.set(Boolean.FALSE);
            }
        }

        /** getId: the world registry's id of the same key, as 1.20.6's synchronised registry answered */
        @Override
        public int method_10206(V value) {
            net.minecraft.class_2378<W> w = current(worldKey);
            if (w == null) {
                return super.method_10206(value);
            }
            Optional<net.minecraft.class_5321<V>> key = this.method_29113(value);
            if (key.isEmpty()) {
                return -1;
            }
            W entry = w.method_29107(worldKey(key.get()));
            return entry == null ? -1 : w.method_10206(entry);
        }

        /** byId: the object of the world registry's entry with that id */
        @Override
        public V method_10200(int id) {
            net.minecraft.class_2378<W> w = current(worldKey);
            if (w == null) {
                return super.method_10200(id);
            }
            W entry = w.method_10200(id);
            if (entry == null) {
                return null;
            }
            return w.method_29113(entry).map(this::facadeFor).orElse(null);
        }

        /** getHolder(int): byId's holder */
        @Override
        public Optional<net.minecraft.class_6880.class_6883<V>> method_40265(int id) {
            V v = method_10200(id);
            return v == null ? Optional.empty() : this.method_29113(v).flatMap(this::method_40264);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public net.minecraft.class_5321<W> worldKey(net.minecraft.class_5321<V> key) {
            return (net.minecraft.class_5321) key;
        }

        /** the world key of a registered object; IllegalStateException when it was never registered */
        public net.minecraft.class_5321<W> worldKeyOf(V value) {
            return worldKey(this.method_29113(value).orElseThrow(() -> new IllegalStateException("a 1.20.6 object that was never registered in "
                + worldKey.method_29177() + " (" + value.getClass().getName() + ") stands for no 1.21 entry")));
        }

        /** the current world's entry of a registered object's key, or null (no world, unregistered, not in the world) */
        public W worldValue(V value) {
            net.minecraft.class_2378<W> w = current(worldKey);
            if (w == null) {
                return null;
            }
            Optional<net.minecraft.class_5321<V>> key = this.method_29113(value);
            return key.isEmpty() ? null : w.method_29107(worldKey(key.get()));
        }

        /** the current world's holder of a registered object's key (IllegalStateException without one) */
        public net.minecraft.class_6880<W> worldHolder(V value) {
            net.minecraft.class_2378<W> w = requireWorld(worldKey, "a 1.21 holder of " + worldKey.method_29177());
            net.minecraft.class_5321<W> key = worldKeyOf(value);
            return w.method_40264(key).orElseThrow(() -> new IllegalStateException(key.method_29177() + " is not in the world's "
                + worldKey.method_29177() + " registry"));
        }

        /**
         * The object of a world entry's key: the registered one, else one made now ({@link #newFacade}) and
         * registered under the key; null when the stand-in makes none.
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        public V facadeFor(net.minecraft.class_5321<W> key) {
            net.minecraft.class_5321<V> lk = (net.minecraft.class_5321) key;
            V v = this.method_29107(lk);
            if (v != null) {
                return v;
            }
            synchronized (this) {
                v = this.method_29107(lk);
                if (v != null) {
                    return v;
                }
                v = newFacade(key);
                if (v != null) {
                    cbRegister(lk, v);
                }
                return v;
            }
        }

        /** a server world has loaded the registry from its datapacks (before it freezes): the mods' objects get their entries */
        @SuppressWarnings({"unchecked", "rawtypes"})
        void inject(net.minecraft.class_2385<W> registry, net.minecraft.class_6903.class_7863 lookup) {
            List<Map.Entry<net.minecraft.class_5321<V>, V>> entries;
            synchronized (this) {
                entries = new ArrayList<>(this.method_29722());
            }
            for (Map.Entry<net.minecraft.class_5321<V>, V> e : entries) {
                W record = recordFor(e.getValue(), e.getKey(), lookup);
                if (record == null) {
                    continue;
                }
                net.minecraft.class_5321<W> key = worldKey(e.getKey());
                if (registry.method_35842(key)) {
                    org.slf4j.LoggerFactory.getLogger("CenturyBridge").warn("[centurybridge] a datapack defines {} {}: its 1.21 data stands, and"
                        + " the 1.20.6 object registered under that name answers only 1.20.6 callers", worldKey.method_29177(), key.method_29177());
                    continue;
                }
                registry.method_10272(key, record, net.minecraft.class_9248.field_49136);
            }
        }

        /** every entry of a world registry gets its object now, on the thread that loaded it */
        @SuppressWarnings("unchecked")
        void facadesFor(net.minecraft.class_2378<W> registry) {
            for (net.minecraft.class_5321<W> key : registry.method_42021()) {
                facadeFor(key);
            }
        }

        /** the world's tags over the stand-in's holders, bound into the stand-in */
        @SuppressWarnings({"unchecked", "rawtypes"})
        void bindWorldTags(Map tags) {
            Map<net.minecraft.class_6862<V>, List<net.minecraft.class_6880<V>>> mapped = new LinkedHashMap<>();
            for (Object o : tags.entrySet()) {
                Map.Entry e = (Map.Entry) o;
                List<net.minecraft.class_6880<V>> list = new ArrayList<>();
                for (Object h : (List) e.getValue()) {
                    Optional<net.minecraft.class_5321<W>> key = ((net.minecraft.class_6880) h).method_40230();
                    if (key.isPresent() && facadeFor(key.get()) != null) {
                        Optional<net.minecraft.class_6880.class_6883<V>> own = this.method_40264((net.minecraft.class_5321<V>) (net.minecraft.class_5321) key.get());
                        own.ifPresent(list::add);
                    }
                }
                mapped.put((net.minecraft.class_6862) e.getKey(), list);
            }
            this.method_40257(mapped);
        }
    }

    // ---- what the mixins report ----

    /** RegistryDataLoader.Loader.loadFromResources finished (a server world, before the registry freezes) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void onServerLoaded(net.minecraft.class_5321<?> key, net.minecraft.class_2385<?> registry, net.minecraft.class_6903.class_7863 lookup) {
        Worlds w = worlds(key);
        w.loaded = true;
        Legacy standin = STANDINS.get(key);
        if (standin != null) {
            standin.inject((net.minecraft.class_2385) registry, lookup);
        }
        w.server = (net.minecraft.class_2378<?>) registry;
        if (standin != null) {
            standin.facadesFor((net.minecraft.class_2378) registry);
        }
    }

    /** RegistryDataLoader.Loader.loadFromNetwork finished (the client's registry, built from the server's data) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void onClientLoaded(net.minecraft.class_5321<?> key, net.minecraft.class_2385<?> registry) {
        Worlds w = worlds(key);
        w.loaded = true;
        w.clientThread = Thread.currentThread();
        w.client = (net.minecraft.class_2378<?>) registry;
        Legacy standin = STANDINS.get(key);
        if (standin != null) {
            standin.facadesFor((net.minecraft.class_2378) registry);
        }
    }

    /** MappedRegistry.bindTags of any registry: a world registry with a stand-in binds the stand-in's tags too */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map onBindTags(net.minecraft.class_2378<?> registry, Map tags) {
        if (registry instanceof Legacy) {
            return tags;
        }
        Legacy standin = STANDINS.get(registry.method_30517());
        if (standin == null) {
            return tags;
        }
        Map bound = standin.augmentTags(registry, tags);
        standin.bindWorldTags(bound);
        return bound;
    }

    /** a server stopped (CbServers.onStopped): its registries are no world's any more */
    public static void onServerStopped() {
        for (Worlds w : WORLDS.values()) {
            w.server = null;
        }
    }

    /** the client left its world */
    public static void onClientDisconnected() {
        for (Worlds w : WORLDS.values()) {
            w.client = null;
        }
    }
}
