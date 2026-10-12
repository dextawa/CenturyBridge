package top.dext.centurybridge.step.v1_21.rt;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import top.dext.centurybridge.step.v1_21.facade.class_1887;

/**
 * 1.20.6's enchantments on 1.21.
 *
 * <p><b>The objects.</b> In 1.20.6 an enchantment was an object of a class extending Enchantment,
 * registered in the static BuiltInRegistries.ENCHANTMENT; in 1.21 it is a final record loaded from
 * datapacks into each world's dynamic registry (minecraft:enchantment), its behaviour data-driven
 * effect components. A 1.20.6 mod sees 1.20.6's Enchantment as the facade
 * {@link top.dext.centurybridge.step.v1_21.facade.class_1887} (classRenames), and every such object is
 * identified by its registry key -- {@code ResourceKey<Enchantment>} under the registry name
 * minecraft:enchantment, the same key a 1.21 world uses for the enchantment it stands for:
 * <ul>
 * <li>a 1.20.6 mod's own enchantment: an object of the mod's subclass, registered by the mod where it
 *     registered it in 1.20.6 -- BuiltInRegistries.ENCHANTMENT reads (fieldGetter) as {@link #legacy()},
 *     a registry of facades under the same registry key. When a world's enchantment registry loads
 *     from its datapacks, CenturyBridge adds a 1.21 record for every mod enchantment the datapacks do
 *     not define themselves, built from the object's definition and description and marked with the
 *     component {@link #LEGACY} naming it; 1.21's enchantment code hands the record's questions and
 *     effects back to the object (mixins CbEnch*);</li>
 * <li>1.20.6's vanilla enchantments: objects of the very classes 1.20.6 built them of, with the
 *     definitions it gave them ({@link CbEnchVanilla}, generated from 1.20.6's Enchantments), bound
 *     to the world's record of the same key for every data-driven answer;</li>
 * <li>a 1.21 enchantment no 1.20.6 class stands behind (a datapack's): a facade made on first sight,
 *     bound to its record.</li>
 * </ul>
 *
 * <p><b>The worlds.</b> A 1.21 holder belongs to one world's registry: the server's (a dedicated
 * server's, or the integrated server's), or the client's, built from what the server sent. The
 * converters answer with the registry of the side they run on: the client's on the client's main
 * thread, the server's anywhere else (its own thread, the world generation workers), the client's
 * when there is no server here. Both are recorded when their enchantment registry loads
 * (CbEnchLoaderMixin) and dropped when that world goes (the integrated server stops, the client
 * disconnects). With no world registries at all there is no 1.21 holder to give, and the converters
 * throw rather than answer one that would fail later.
 *
 * <p><b>Registration window.</b> 1.20.6 froze BuiltInRegistries.ENCHANTMENT after start-up; an
 * enchantment registered later was refused. The stand-in registry refuses a mod's registration once a
 * world has read it (the records are made then and never again for that world), with 1.20.6's
 * message, so a late registration fails as it did instead of silently never existing.
 *
 * <p><b>Raw ids.</b> 1.20.6 synchronised BuiltInRegistries.ENCHANTMENT's ids from server to client.
 * The stand-in registry answers getId / byId with the world registry's ids, which 1.21 synchronises
 * the same way.
 */
public final class CbEnchantments {

    private CbEnchantments() {
    }

    /** registry name of both 1.20.6's enchantment registry and 1.21's (minecraft:enchantment) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static net.minecraft.class_5321<net.minecraft.class_2378<class_1887>> legacyRegistryKey() {
        return (net.minecraft.class_5321) net.minecraft.class_7924.field_41265;
    }

    /** CenturyBridge's stand-in for 1.20.6's BuiltInRegistries.ENCHANTMENT */
    public static final class Legacy extends net.minecraft.class_2370<class_1887> {
        /** set once a world has read the registry: later registrations by a mod are refused */
        volatile boolean closed;
        /** CenturyBridge's own registrations (a datapack enchantment's facade) bypass the window */
        final ThreadLocal<Boolean> cbAdding = ThreadLocal.withInitial(() -> Boolean.FALSE);

        Legacy() {
            super(legacyRegistryKey(), com.mojang.serialization.Lifecycle.stable(), true);
        }

        /** register: refused once closed, as 1.20.6's frozen registry refused it */
        @Override
        public net.minecraft.class_6880.class_6883<class_1887> method_10272(net.minecraft.class_5321<class_1887> key, class_1887 value,
                net.minecraft.class_9248 info) {
            if (closed && !cbAdding.get()) {
                throw new IllegalStateException("Registry is already frozen (trying to add key " + key + ")");
            }
            return super.method_10272(key, value, info);
        }

        /** getId: the world registry's id of the same key, as 1.20.6's synchronised registry answered */
        @Override
        public int method_10206(class_1887 value) {
            net.minecraft.class_2378<net.minecraft.class_1887> w = current();
            if (w == null) {
                return super.method_10206(value);
            }
            Optional<net.minecraft.class_5321<class_1887>> key = this.method_29113(value);
            if (key.isEmpty()) {
                return -1;
            }
            net.minecraft.class_1887 record = w.method_29107(wKey(key.get()));
            return record == null ? -1 : w.method_10206(record);
        }

        /** byId: the facade of the world registry's entry with that id */
        @Override
        public class_1887 method_10200(int id) {
            net.minecraft.class_2378<net.minecraft.class_1887> w = current();
            if (w == null) {
                return super.method_10200(id);
            }
            net.minecraft.class_1887 record = w.method_10200(id);
            if (record == null) {
                return null;
            }
            return w.method_29113(record).map(CbEnchantments::facadeFor).orElse(null);
        }

        /** getHolder(int): byId's holder */
        @Override
        public Optional<net.minecraft.class_6880.class_6883<class_1887>> method_40265(int id) {
            class_1887 e = method_10200(id);
            return e == null ? Optional.empty() : Optional.of(e.field_45769);
        }
    }

    private static final class Instance {
        static final Legacy REGISTRY = new Legacy();
    }

    /** 1.20.6's BuiltInRegistries.ENCHANTMENT, with 1.20.6's vanilla enchantments registered first */
    public static Legacy legacy() {
        CbEnchVanilla.bootstrap();
        return Instance.REGISTRY;
    }

    /** BuiltInRegistries.ENCHANTMENT (fieldGetter of net/minecraft/class_7923.field_41176) */
    public static net.minecraft.class_2378<class_1887> field_41176() {
        return legacy();
    }

    /** the intrusive holder an Enchantment's constructor takes, as 1.20.6's took one of BuiltInRegistries.ENCHANTMENT */
    public static net.minecraft.class_6880.class_6883<class_1887> intrusiveHolder(class_1887 enchantment) {
        return Instance.REGISTRY.method_40269(enchantment);
    }

    /** CbEnchVanilla's registration of one of 1.20.6's vanilla enchantments under its 1.20.6 name */
    static class_1887 registerVanilla(String name, class_1887 enchantment) {
        enchantment.cb$kind = class_1887.CB_VANILLA;
        Instance.REGISTRY.method_10272(net.minecraft.class_5321.method_29179(legacyRegistryKey(), net.minecraft.class_2960.method_60656(name)),
            enchantment, net.minecraft.class_9248.field_49136);
        return enchantment;
    }

    /** the marker of a datapack enchantment's facade constructor */
    public static final class DataOnly {
        private DataOnly() {
        }
    }

    private static final DataOnly DATA_ONLY = new DataOnly();

    /**
     * The facade of a world enchantment's key: the registered 1.20.6 object (vanilla, a mod's), else a
     * facade made for it now and registered under the key -- the facade of a datapack enchantment.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_1887 facadeFor(net.minecraft.class_5321<net.minecraft.class_1887> key) {
        Legacy legacy = legacy();
        net.minecraft.class_5321<class_1887> lk = (net.minecraft.class_5321) key;
        class_1887 e = legacy.method_29107(lk);
        if (e != null) {
            return e;
        }
        synchronized (legacy) {
            e = legacy.method_29107(lk);
            if (e != null) {
                return e;
            }
            e = new class_1887(DATA_ONLY);
            legacy.cbAdding.set(Boolean.TRUE);
            try {
                legacy.method_10272(lk, e, net.minecraft.class_9248.field_49136);
            } finally {
                legacy.cbAdding.set(Boolean.FALSE);
            }
            return e;
        }
    }

    // ---- the worlds' registries ----

    private static volatile net.minecraft.class_2378<net.minecraft.class_1887> serverEnchantments;
    private static volatile net.minecraft.class_2378<net.minecraft.class_1887> clientEnchantments;
    private static volatile Thread clientThread;

    /** the enchantment registry of the world this thread works for (see the class comment), or null */
    public static net.minecraft.class_2378<net.minecraft.class_1887> current() {
        net.minecraft.class_2378<net.minecraft.class_1887> c = clientEnchantments;
        if (c != null && Thread.currentThread() == clientThread) {
            return c;
        }
        net.minecraft.class_2378<net.minecraft.class_1887> s = serverEnchantments;
        return s != null ? s : c;
    }

    private static net.minecraft.class_2378<net.minecraft.class_1887> requireWorld() {
        net.minecraft.class_2378<net.minecraft.class_1887> w = current();
        if (w == null) {
            throw new IllegalStateException("no world's enchantments are loaded: a 1.21 enchantment holder exists only in a world's"
                + " registries, and a 1.20.6 enchantment stands for one only once a world (or a server's registry data) has loaded");
        }
        return w;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static net.minecraft.class_5321<net.minecraft.class_1887> wKey(net.minecraft.class_5321<class_1887> key) {
        return (net.minecraft.class_5321) key;
    }

    /** the registry key of a 1.20.6 enchantment, as a 1.21 enchantment key; IllegalStateException when it was never registered */
    public static net.minecraft.class_5321<net.minecraft.class_1887> wKeyOf(class_1887 enchantment) {
        return wKey(legacy().method_29113(enchantment).orElseThrow(() -> new IllegalStateException(
            "a 1.20.6 enchantment that was never registered (" + enchantment.getClass().getName() + ") stands for no 1.21 enchantment")));
    }

    /** the registry name of an enchantment, for messages ("unregistered" when it has none) */
    public static String keyOf(class_1887 enchantment) {
        net.minecraft.class_2960 id = legacy().method_10221(enchantment);
        return id == null ? "(unregistered " + enchantment.getClass().getName() + ")" : id.toString();
    }

    /** the world's record of a bound enchantment's key, or null when no world registry holds it */
    public static net.minecraft.class_1887 boundRecord(class_1887 enchantment) {
        net.minecraft.class_2378<net.minecraft.class_1887> w = current();
        if (w == null) {
            return null;
        }
        Optional<net.minecraft.class_5321<class_1887>> key = legacy().method_29113(enchantment);
        return key.isEmpty() ? null : w.method_29107(wKey(key.get()));
    }

    /** whether a bound enchantment's world holder is in a 1.21 enchantment tag (its 1.21 data for a 1.20.6 flag) */
    public static boolean boundIn(class_1887 enchantment, net.minecraft.class_6862<net.minecraft.class_1887> tag) {
        return cbHolder(enchantment).method_40220(tag);
    }

    /** a datapack enchantment's translation key, when its description is a translatable text (else null) */
    public static String translationKeyOf(class_1887 enchantment) {
        net.minecraft.class_1887 r = boundRecord(enchantment);
        if (r != null && r.comp_2686().method_10851() instanceof net.minecraft.class_2588 t) {
            return t.method_11022();
        }
        return null;
    }

    // ---- converters ----

    /** V Enchantment -> W Holder&lt;Enchantment&gt;: the world registry's holder of the same key (null -> null) */
    public static net.minecraft.class_6880<net.minecraft.class_1887> cbHolder(class_1887 enchantment) {
        if (enchantment == null) {
            return null;
        }
        net.minecraft.class_2378<net.minecraft.class_1887> w = requireWorld();
        net.minecraft.class_5321<net.minecraft.class_1887> key = wKeyOf(enchantment);
        return w.method_40264(key).orElseThrow(() -> new IllegalStateException(
            "enchantment " + key.method_29177() + " is not in the world's enchantment registry"));
    }

    /** W Holder&lt;Enchantment&gt; -> V Enchantment: the object of the holder's key (null -> null) */
    public static class_1887 cbEnchantment(net.minecraft.class_6880<net.minecraft.class_1887> holder) {
        if (holder == null) {
            return null;
        }
        return facadeFor(holder.method_40230().orElseThrow(() -> new IllegalArgumentException(
            "a 1.21 enchantment holder without a registry key (" + holder + ") stands for no 1.20.6 enchantment")));
    }

    /** adaptive Holder -> W holder: a holder of a 1.20.6 enchantment (its builtInRegistryHolder) becomes the world's; a W holder passes */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_6880 cbHolderW(net.minecraft.class_6880 holder) {
        if (holder != null && holder.method_40227() && holder.comp_349() instanceof class_1887 legacy) {
            return cbHolder(legacy);
        }
        return holder;
    }

    /** adaptive Holder -> V holder: a world enchantment's holder becomes its 1.20.6 object's holder; anything else passes */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_6880 cbHolderV(net.minecraft.class_6880 holder) {
        if (holder != null && holder.method_40227() && holder.comp_349() instanceof net.minecraft.class_1887) {
            return cbEnchantment(holder).field_45769;
        }
        return holder;
    }

    /** cbHolderV for a Holder.Reference */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_6880.class_6883 cbReferenceV(net.minecraft.class_6880.class_6883 holder) {
        if (holder != null && holder.method_40227() && holder.comp_349() instanceof net.minecraft.class_1887) {
            return cbEnchantment(holder).field_45769;
        }
        return holder;
    }

    // ---- 1.21 records for a 1.20.6 mod's enchantments ----

    /** effect component marking a 1.21 record as standing for the 1.20.6 enchantment of that registry name */
    public static net.minecraft.class_9331<net.minecraft.class_2960> LEGACY;

    /** registers {@link #LEGACY} with 1.21's enchantment effect components (CbEnchEffectComponentsMixin) */
    public static void registerComponents(net.minecraft.class_2378<net.minecraft.class_9331<?>> registry) {
        if (LEGACY == null) {
            LEGACY = net.minecraft.class_2378.method_10230(registry, net.minecraft.class_2960.method_60655("centurybridge", "legacy_enchantment"),
                net.minecraft.class_9331.<net.minecraft.class_2960>method_57873().method_57881(net.minecraft.class_2960.field_25139).method_57880());
        }
    }

    /** the 1.20.6 object a world record stands for, when it is one of CenturyBridge's records of a mod's enchantment */
    public static class_1887 legacyOf(net.minecraft.class_1887 record) {
        if (record == null || LEGACY == null) {
            return null;
        }
        net.minecraft.class_2960 id = record.comp_2689().method_57829(LEGACY);
        return id == null ? null : legacy().method_10223(id);
    }

    /** a mod enchantment's 1.21 record: its definition, its description, compatibility asked of the objects, the marker */
    private static net.minecraft.class_1887 recordFor(class_1887 e, net.minecraft.class_7871<net.minecraft.class_1792> items) {
        top.dext.centurybridge.step.v1_21.facade.class_1887$class_9427 d = e.field_50013;
        net.minecraft.class_6885<net.minecraft.class_1792> supported = items.method_46735(d.comp_2506);
        Optional<net.minecraft.class_6885<net.minecraft.class_1792>> primary =
            d.comp_2507.map(t -> (net.minecraft.class_6885<net.minecraft.class_1792>) items.method_46735(t));
        List<net.minecraft.class_9274> slots = new ArrayList<>();
        for (net.minecraft.class_1304 slot : d.comp_2513) {
            net.minecraft.class_9274 group = net.minecraft.class_9274.method_59524(slot);
            if (!slots.contains(group)) {
                slots.add(group);
            }
        }
        net.minecraft.class_1887.class_9427 definition = new net.minecraft.class_1887.class_9427(supported, primary, d.comp_2508, d.comp_2509,
            d.comp_2510, d.comp_2511, d.comp_2512, slots);
        net.minecraft.class_9323 effects = net.minecraft.class_9323.method_57827().method_57840(LEGACY, legacy().method_10221(e)).method_57838();
        // the exclusive set stays empty: 1.20.6 asked the objects (isCompatibleWith), and 1.21's only reader of the set,
        // Enchantment.areCompatible, asks them too for a record of CenturyBridge's (CbEnchRecordMixin)
        return new net.minecraft.class_1887(net.minecraft.class_2561.method_43471(e.method_8184()), definition,
            net.minecraft.class_6885.method_40246(), effects);
    }

    /**
     * A server world's enchantment registry has loaded its datapacks (CbEnchLoaderMixin, before it
     * freezes): every mod enchantment the datapacks do not define gets its record; every enchantment of
     * the world gets its facade; the registry becomes the server's.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void onServerLoaded(net.minecraft.class_2385<net.minecraft.class_1887> registry, net.minecraft.class_6903.class_7863 lookup) {
        Legacy legacy = legacy();
        legacy.closed = true;
        net.minecraft.class_7871<net.minecraft.class_1792> items = (net.minecraft.class_7871) lookup.method_46623(net.minecraft.class_7924.field_41197)
            .orElseThrow(() -> new IllegalStateException("the enchantment registry loads without the item registry in reach")).comp_1131();
        List<class_1887> mods = new ArrayList<>();
        synchronized (legacy) {
            for (class_1887 e : legacy) {
                if (e.cb$kind == class_1887.CB_MOD) {
                    mods.add(e);
                }
            }
        }
        for (class_1887 e : mods) {
            net.minecraft.class_5321<net.minecraft.class_1887> key = wKeyOf(e);
            if (registry.method_35842(key)) {
                org.slf4j.LoggerFactory.getLogger("CenturyBridge").warn("[centurybridge] a datapack defines enchantment {}: its 1.21 data"
                    + " stands, and the 1.20.6 object registered under that name answers only 1.20.6 callers", key.method_29177());
                continue;
            }
            registry.method_10272(key, recordFor(e, items), net.minecraft.class_9248.field_49136);
        }
        serverEnchantments = (net.minecraft.class_2378) registry;
        for (net.minecraft.class_5321<net.minecraft.class_1887> key : ((net.minecraft.class_2378<net.minecraft.class_1887>) registry).method_42021()) {
            facadeFor(key);
        }
    }

    /** the client's enchantment registry has been built from the server's data (CbEnchLoaderMixin) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void onClientLoaded(net.minecraft.class_2385<net.minecraft.class_1887> registry) {
        legacy().closed = true;
        clientThread = Thread.currentThread();
        clientEnchantments = (net.minecraft.class_2378) registry;
        for (net.minecraft.class_5321<net.minecraft.class_1887> key : ((net.minecraft.class_2378<net.minecraft.class_1887>) registry).method_42021()) {
            facadeFor(key);
        }
    }

    /** the integrated server stopped (CbEnchServerMixin): its registry is no world's any more */
    public static void onServerStopped(net.minecraft.class_2378<?> enchantments) {
        if (enchantments == null || serverEnchantments == enchantments) {
            serverEnchantments = null;
        }
    }

    /** the client left its world (CbEnchClientMixin) */
    public static void onClientDisconnected() {
        clientEnchantments = null;
    }

    // ---- 1.20.6's flags as 1.21's tags ----

    /**
     * The tags 1.21 reads where 1.20.6 read an enchantment's flags, with the rule that puts a 1.20.6
     * enchantment in each: treasure / non_treasure (isTreasureOnly: the enchanting table, mob spawn
     * equipment, villagers' enchanted items and non-treasure loot left it out), curse (isCurse:
     * grindstone, red name), tradeable (isTradeable: librarian books), double_trade_price
     * (isTreasureOnly: the librarian doubled the price), on_random_loot (isDiscoverable: random
     * enchantment loot), and in_enchanting_table, on_mob_spawn_equipment, on_traded_equipment (not
     * isTreasureOnly and isDiscoverable: 1.20.6's EnchantmentHelper.getAvailableEnchantmentResults
     * without treasure). 1.21's vanilla data assigns its own enchantments to these tags by the same
     * rules, read off 1.20.6's flags.
     */
    private static boolean wants(class_1887 e, net.minecraft.class_6862<net.minecraft.class_1887> tag) {
        if (tag == net.minecraft.class_9636.field_51557 || tag == net.minecraft.class_9636.field_51546) {
            return e.method_8193();
        }
        if (tag == net.minecraft.class_9636.field_51558) {
            return !e.method_8193();
        }
        if (tag == net.minecraft.class_9636.field_51551) {
            return e.method_8195();
        }
        if (tag == net.minecraft.class_9636.field_51545) {
            return e.method_25949();
        }
        if (tag == net.minecraft.class_9636.field_51550) {
            return e.method_25950();
        }
        if (tag == net.minecraft.class_9636.field_51547 || tag == net.minecraft.class_9636.field_51548 || tag == net.minecraft.class_9636.field_51549) {
            return !e.method_8193() && e.method_25950();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static final net.minecraft.class_6862<net.minecraft.class_1887>[] FLAG_TAGS = new net.minecraft.class_6862[] {
        net.minecraft.class_9636.field_51557, net.minecraft.class_9636.field_51558, net.minecraft.class_9636.field_51551,
        net.minecraft.class_9636.field_51545, net.minecraft.class_9636.field_51546, net.minecraft.class_9636.field_51550,
        net.minecraft.class_9636.field_51547, net.minecraft.class_9636.field_51548, net.minecraft.class_9636.field_51549};

    /**
     * The tags an enchantment registry binds, with CenturyBridge's records of mod enchantments added to
     * the tags their objects' flags put them in (CbEnchTagsMixin on MappedRegistry.bindTags). Idempotent:
     * a client binding the server's tags, which already list them, adds nothing twice.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Map withLegacyTags(net.minecraft.class_2378 registry, Map tags) {
        if (LEGACY == null) {
            return tags;
        }
        Map<net.minecraft.class_6862<net.minecraft.class_1887>, List<net.minecraft.class_6880<net.minecraft.class_1887>>> out = null;
        java.util.Iterator<net.minecraft.class_6880.class_6883<net.minecraft.class_1887>> it =
            ((java.util.stream.Stream<net.minecraft.class_6880.class_6883<net.minecraft.class_1887>>) registry.method_40270()).iterator();
        while (it.hasNext()) {
            net.minecraft.class_6880.class_6883<net.minecraft.class_1887> h = it.next();
            class_1887 e = legacyOf(h.comp_349());
            if (e == null) {
                continue;
            }
            for (net.minecraft.class_6862<net.minecraft.class_1887> tag : FLAG_TAGS) {
                if (!wants(e, tag)) {
                    continue;
                }
                if (out == null) {
                    out = new LinkedHashMap<>(tags);
                }
                List<net.minecraft.class_6880<net.minecraft.class_1887>> list = new ArrayList<>(out.getOrDefault(tag, List.of()));
                if (!list.contains(h)) {
                    list.add(h);
                }
                out.put(tag, list);
            }
        }
        return out == null ? tags : out;
    }
}
