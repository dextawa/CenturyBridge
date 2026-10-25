package top.dext.centurybridge.step.v1_21_11.rt;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicLike;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.class_12279;
import net.minecraft.class_1928;
import net.minecraft.class_2378;
import net.minecraft.class_2487;
import net.minecraft.class_2960;
import net.minecraft.class_7699;
import net.minecraft.class_7923;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.step.v1_21_11.compat.fapi.gamerule.v1.rule.DoubleRule;
import top.dext.centurybridge.step.v1_21_11.compat.fapi.gamerule.v1.rule.EnumRule;
import top.dext.centurybridge.step.v1_21_11.facade.GameRules;

/**
 * 1.21.10's game rule API on 1.21.11 (facade/GameRules holds the 1.21.10 classes).
 *
 * <p>1.21.10 kept a static table Key -&gt; Type (sorted by id) and, per GameRules, a Key -&gt; mutable Value map;
 * 1.21.11 registers GameRule&lt;T&gt; objects and a GameRules holds their values. Every 1.21.10 key is tied to the
 * 1.21.11 rule that carries its meaning: the vanilla ones by CbGameRulesVanilla (1.21.11's GameRuleRegistryFix
 * pairing: as is, negated for the three "disable" rules, and doFireTick / allowFireTicksAwayFromPlayer both read
 * the fire spread radius), a mod's by register, which registers a 1.21.11 rule for it. getRule(key) gives one
 * value per GameRules and key, bound to it (facade Value.cb$bind): reads and writes go to 1.21.11's GameRules.
 */
public final class CbGameRules {
    public static final Logger LOGGER = LoggerFactory.getLogger("centurybridge/gamerules");

    private CbGameRules() {
    }

    /** how a 1.21.10 value stands for the 1.21.11 one */
    public enum Kind {
        /** the same boolean */
        BOOL,
        /** the same integer */
        INT,
        /** a boolean 1.21.11 negated (disableRaids -> raids) */
        INVERT,
        /** doFireTick: fire spreads at all (radius != 0) */
        FIRE_TICK,
        /** allowFireTicksAwayFromPlayer: fire spreads without a radius (radius < 0) */
        FIRE_AWAY,
        /** the same value object (a Fabric API double or enum rule) */
        OBJECT
    }

    /** 1.21.11's GameRuleRegistryFix: doFireTick true, allowFireTicksAwayFromPlayer false */
    private static final int FIRE_NEAR_PLAYERS = 128;

    static final class Entry {
        final GameRules.Key<?> key;
        final GameRules.Type<?> type;
        final class_12279<?> rule;
        final Kind kind;
        /** registered by a mod (its own callback runs from 1.21.11's game rule hook) */
        final boolean mod;
        final CustomGameRuleCategory custom;

        Entry(GameRules.Key<?> key, GameRules.Type<?> type, class_12279<?> rule, Kind kind, boolean mod, CustomGameRuleCategory custom) {
            this.key = key;
            this.type = type;
            this.rule = rule;
            this.kind = kind;
            this.mod = mod;
            this.custom = custom;
        }
    }

    /** 1.21.10 GameRules.GAME_RULE_TYPES: sorted by id */
    private static final Map<String, Entry> BY_ID = new TreeMap<>();
    private static final Map<class_12279<?>, Entry> MOD_BY_RULE = new IdentityHashMap<>();
    /** 1.21.11 rule -> the 1.21.10 keys standing for it, in id order (two for the fire spread radius) */
    private static final Map<class_12279<?>, List<Entry>> BY_RULE = new java.util.concurrent.ConcurrentHashMap<>();
    /** per GameRules: allowFireTicksAwayFromPlayer while fire spread is off (1.21.11's radius 0 cannot hold it) */
    private static final Map<class_1928, Boolean> FIRE_AWAY_WHILE_OFF = new WeakHashMap<>();

    static {
        CbGameRulesVanilla.register();
    }

    // ---- the tables

    private static void index(Entry e) {
        BY_RULE.compute(e.rule, (r, old) -> {
            List<Entry> l = old == null ? new java.util.ArrayList<>() : new java.util.ArrayList<>(old);
            l.add(e);
            l.sort(java.util.Comparator.comparing(x -> x.key.method_20771()));
            return java.util.List.copyOf(l);
        });
    }

    static void vanilla(String id, GameRules.Category category, Kind kind, class_12279<?> rule) {
        GameRules.Key<?> key = new GameRules.Key<>(id, category);
        GameRules.Type<?> type = vanillaType(kind, rule);
        Entry e = new Entry(key, type, rule, kind, false, null);
        BY_ID.put(id, e);
        index(e);
    }

    @SuppressWarnings("unchecked")
    private static GameRules.Type<?> vanillaType(Kind kind, class_12279<?> rule) {
        class_7699 features = rule.method_45322();
        Object def = rule.method_76156();
        if (kind == Kind.INT) {
            class_12279<Integer> r = (class_12279<Integer>) rule;
            IntegerArgumentType arg = (IntegerArgumentType) r.method_76154();
            return GameRules.IntegerValue.cb$create((Integer) def, arg.getMinimum(), arg.getMaximum(), features,
                (server, value) -> vanillaChanged(server, rule, value.method_20763()));
        }
        boolean d = switch (kind) {
            case INVERT -> !(Boolean) def;
            case FIRE_TICK -> (Integer) def != 0;
            case FIRE_AWAY -> (Integer) def < 0;
            default -> (Boolean) def;
        };
        return GameRules.BooleanValue.method_20760(d, (server, value) -> {
            // a value of its own (Type.createRule()) changed: 1.21.10's vanilla callback acted on that value
            Object w = switch (kind) {
                case INVERT -> !value.method_20753();
                case FIRE_TICK, FIRE_AWAY -> fireRadius(currentRadius(server), kind, value.method_20753(), null);
                default -> value.method_20753();
            };
            vanillaChanged(server, rule, w);
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void vanillaChanged(MinecraftServer server, class_12279 rule, Object w) {
        server.method_74058(rule, w);
    }

    private static int currentRadius(MinecraftServer server) {
        return (Integer) raw(server.method_30002().method_64395(), class_1928.field_56559);
    }

    /** the key of a vanilla 1.21.10 rule (GameRules.RULE_* reads) */
    public static GameRules.Key<?> vanillaKey(String id) {
        return BY_ID.get(id).key;
    }

    private static Entry entry(GameRules.Key<?> key) {
        return key == null ? null : BY_ID.get(key.method_20771());
    }

    // ---- reading and writing a bound value

    /** whether a GameRules holds a rule (1.21.11's GameRuleMap.has on its own map: enabled for its features) */
    private static boolean has(class_1928 rules, class_12279<?> rule) {
        return rules.field_9196.method_76164(rule);
    }

    /** a rule's value in a GameRules' own map (not through GameRules.get, which CbGameRulesMixin routes) */
    private static Object raw(class_1928 rules, class_12279<?> rule) {
        return rules.field_9196.method_76173(rule);
    }

    /** a bound value's 1.21.10 reading */
    public static Object read(class_1928 rules, GameRules.Key<?> key) {
        Entry e = entry(key);
        return fromW(e, raw(rules, e.rule), rules);
    }

    private static boolean fireAwayWhileOff(class_1928 rules) {
        synchronized (FIRE_AWAY_WHILE_OFF) {
            return FIRE_AWAY_WHILE_OFF.getOrDefault(rules, false);
        }
    }

    /**
     * The fire spread radius after a 1.21.10 write of doFireTick (FIRE_TICK) or allowFireTicksAwayFromPlayer
     * (FIRE_AWAY): GameRuleRegistryFix's mapping (off 0, near players 128, anywhere -1), keeping a radius 1.21.11
     * set otherwise where the 1.21.10 pair would not change it. rules (may be null) keeps the "anywhere" flag
     * while fire spread is off.
     */
    private static int fireRadius(int radius, Kind kind, boolean v, class_1928 rules) {
        if (kind == Kind.FIRE_TICK) {
            if (!v) {
                if (radius != 0 && rules != null) {
                    synchronized (FIRE_AWAY_WHILE_OFF) {
                        FIRE_AWAY_WHILE_OFF.put(rules, radius < 0);
                    }
                }
                return 0;
            }
            return radius != 0 ? radius : rules != null && fireAwayWhileOff(rules) ? -1 : FIRE_NEAR_PLAYERS;
        }
        if (radius == 0) {
            if (rules != null) {
                synchronized (FIRE_AWAY_WHILE_OFF) {
                    FIRE_AWAY_WHILE_OFF.put(rules, v);
                }
            }
            return 0;
        }
        return v ? -1 : radius < 0 ? FIRE_NEAR_PLAYERS : radius;
    }

    /** a bound value's 1.21.10 write; 1.21.11's set runs the server's game rule hook when server is not null */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void write(class_1928 rules, GameRules.Key<?> key, Object v, MinecraftServer server) {
        Entry e = entry(key);
        Object w = switch (e.kind) {
            case INVERT -> !(Boolean) v;
            case FIRE_TICK, FIRE_AWAY -> fireRadius((Integer) raw(rules, e.rule), e.kind, (Boolean) v, rules);
            default -> v;
        };
        WRITING.set(Boolean.TRUE);
        try {
            rules.method_76186((class_12279) e.rule, w, server);
        } finally {
            WRITING.remove();
        }
    }

    /** a 1.21.10 value's own write in progress on this thread (routeWrite lets 1.21.11's set through) */
    private static final ThreadLocal<Boolean> WRITING = new ThreadLocal<>();

    /**
     * 1.21.11's GameRules.set(rule, value, server) through the 1.21.10 value of the key the rule stands for (its set,
     * which writes back through {@link #write}): a mod's 1.21.10 mixin on BooleanValue.set / IntegerValue.set sees the
     * game rule command, the edit screen's apply and every other 1.21.11 write, as it saw 1.21.10's. True: done (the
     * caller cancels 1.21.11's own body). Not routed: the fire spread radius (two 1.21.10 keys, no one value set) and
     * rules no boolean / integer key stands for.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean routeWrite(class_1928 rules, class_12279<?> rule, Object value, MinecraftServer server) {
        if (WRITING.get() != null) {
            return false;
        }
        List<Entry> es = BY_RULE.get(rule);
        if (es == null || es.size() != 1 || !has(rules, rule)) {
            return false;
        }
        Entry e = es.get(0);
        GameRules.Value v = live(rules, e.key);
        switch (e.kind) {
            case BOOL -> ((GameRules.BooleanValue) v).method_20758((Boolean) value, server);
            case INVERT -> ((GameRules.BooleanValue) v).method_20758(!(Boolean) value, server);
            case INT -> ((GameRules.IntegerValue) v).method_35236((Integer) value, server);
            default -> {
                return false;
            }
        }
        return true;
    }

    /** a bound value's onChanged(server): 1.21.11's game rule hook with the rule's current value */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void changed(class_1928 rules, GameRules.Key<?> key, MinecraftServer server) {
        if (server != null) {
            Entry e = entry(key);
            server.method_74058((class_12279) e.rule, raw(rules, e.rule));
        }
    }

    // ---- 1.21.10 GameRules members (restored on GameRules by CbGameRulesMixin, whose bodies these are)

    /** GameRules.getRule(key): this GameRules' value of the key (one per GameRules and key); invalid ones throw */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static GameRules.Value getRule(class_1928 rules, Map<String, GameRules.Value<?>> values, GameRules.Key key) {
        Entry e = entry(key);
        if (e == null || !has(rules, e.rule)) {
            throw new IllegalArgumentException("Tried to access invalid game rule");
        }
        return values.computeIfAbsent(e.key.method_20771(), id -> {
            GameRules.Value v = e.type.method_20773();
            v.cb$bind(rules, e.key);
            return v;
        });
    }

    /** the live 1.21.10 value of a key in a GameRules, through its (restored, overridable) getRule */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static GameRules.Value<?> live(class_1928 rules, GameRules.Key<?> key) {
        return ((CbLegacyGameRules) rules).method_20746((GameRules.Key) key);
    }

    /** GameRules.createTag(): each enabled rule's 1.21.10 string under its 1.21.10 id (1.21.10 read its own map) */
    public static class_2487 createTag(class_1928 rules) {
        class_2487 tag = new class_2487();
        for (Entry e : entries()) {
            if (has(rules, e.rule)) {
                tag.method_10582(e.key.method_20771(), getRule(rules, ((CbLegacyGameRules) rules).cb$values(), e.key).method_20779());
            }
        }
        return tag;
    }

    /** new GameRules(features, dynamic): the rules of those features, each read from its 1.21.10 id where present */
    public static class_1928 newGameRules(class_7699 features, DynamicLike<?> dynamic) {
        class_1928 rules = new class_1928(features);
        loadInto(rules, dynamic);
        return rules;
    }

    /**
     * 1.21.10 GameRules(features, dynamic)'s loadFromTag: each rule read from its 1.21.10 id where present. Also run
     * after a mod subclass's super(features, dynamic), reshaped to 1.21.11's GameRules(features) (wiring "post").
     */
    public static void loadInto(class_1928 rules, DynamicLike<?> dynamic) {
        for (Entry e : entries()) {
            if (has(rules, e.rule)) {
                GameRules.Value<?> value = getRule(rules, ((CbLegacyGameRules) rules).cb$values(), e.key);
                dynamic.get(e.key.method_20771()).asString().ifSuccess(value::cb$deserialize);
            }
        }
    }

    /** GameRules.visitGameRuleTypes(visitor): every enabled rule in id order, visit then the type's own call */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void visit(class_1928 rules, GameRules.GameRuleTypeVisitor visitor) {
        for (Entry e : entries()) {
            if (has(rules, e.rule)) {
                GameRules.Type type = e.type;
                visitor.method_20762((GameRules.Key) e.key, type);
                type.method_27336(visitor, (GameRules.Key) e.key);
            }
        }
    }

    /**
     * 1.21.10 GameRules.assignFrom: for each of other's rules, this.getRule(key).setFrom(other.getRule(key), server)
     * (both through the overridable getRule); 1.21.11's set for the 1.21.11 rules no 1.21.10 key stands for (rules
     * 1.21.10 did not have), as 1.21.11's assignFrom does.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void assignFrom(class_1928 rules, class_1928 other, MinecraftServer server) {
        java.util.Set<class_12279<?>> done = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
        for (Entry e : entries()) {
            if (has(other, e.rule) && has(rules, e.rule)) {
                GameRules.Value mine = live(rules, e.key);
                mine.method_27337(live(other, e.key), server);
                done.add(e.rule);
            }
        }
        // a rule several 1.21.10 keys stand for (the fire spread radius): where its keys now read as other's do, it takes
        // other's own value (the radius both readings leave open), as 1.21.11's setAll would copy it
        for (class_12279<?> r : done) {
            List<Entry> es = BY_RULE.get(r);
            if (es.size() > 1 && es.stream().allMatch(e -> java.util.Objects.equals(read(rules, e.key), read(other, e.key)))
                    && !java.util.Objects.equals(raw(rules, r), raw(other, r))) {
                rules.method_76186((class_12279) r, raw(other, r), server);
            }
        }
        other.method_76181().filter(r -> !done.contains(r) && has(rules, r))
            .forEach(r -> rules.method_76186((class_12279) r, raw(other, r), server));
    }

    /**
     * 1.21.11's GameRules.get(rule) through the restored 1.21.10 readers (CbGameRulesMixin): a rule a 1.21.10 boolean
     * or integer key stands for is answered by getBoolean / getInt of that key, so a mod's 1.21.10 mixin on those (or
     * an override in a GameRules subclass) changes what vanilla reads, as it did on 1.21.10; the fire spread radius
     * from doFireTick and allowFireTicksAwayFromPlayer (GameRuleRegistryFix's mapping, the radius kept where they
     * agree with it). Null: not routed (a 1.21.11 rule no 1.21.10 key stands for, or a mod double / enum rule).
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object routeRead(class_1928 rules, class_12279<?> rule) {
        List<Entry> es = BY_RULE.get(rule);
        if (es == null || !has(rules, rule)) {
            return null;
        }
        CbLegacyGameRules legacy = (CbLegacyGameRules) rules;
        if (es.size() == 1) {
            Entry e = es.get(0);
            return switch (e.kind) {
                case BOOL -> legacy.method_8355((GameRules.Key) e.key);
                case INVERT -> !legacy.method_8355((GameRules.Key) e.key);
                case INT -> legacy.method_8356((GameRules.Key) e.key);
                default -> null;
            };
        }
        boolean tick = true;
        boolean away = false;
        for (Entry e : es) {
            if (e.kind == Kind.FIRE_TICK) {
                tick = legacy.method_8355((GameRules.Key) e.key);
            } else if (e.kind == Kind.FIRE_AWAY) {
                away = legacy.method_8355((GameRules.Key) e.key);
            }
        }
        int radius = (Integer) raw(rules, rule);
        return !tick ? 0 : away ? -1 : radius > 0 ? radius : FIRE_NEAR_PLAYERS;
    }

    /** GameRules.getType(key) */
    @SuppressWarnings("rawtypes")
    public static GameRules.Type method_66702(GameRules.Key key) {
        Entry e = entry(key);
        return e == null ? null : e.type;
    }

    /** GameRules.keyCodec(valueClass): a rule id of that value type */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Codec method_66703(Class valueClass) {
        return Codec.STRING.comapFlatMap(id -> {
            Entry e = byName(id);
            return e != null && e.type.cb$valueClass() == valueClass
                ? DataResult.success(e.key)
                : DataResult.error(() -> "Invalid game rule ID for type: " + id);
        }, k -> ((GameRules.Key<?>) k).method_20771());
    }

    /** GameRules.availableRules(features) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Stream method_61725(class_7699 features) {
        return entries().stream().filter(e -> e.type.method_61728().method_45400(features))
            .map(e -> new AbstractMap.SimpleImmutableEntry(e.key, e.type));
    }

    /** MinecraftServer.getGameRules(): the overworld's (1.21.10's body; restored on MinecraftServer by CbGameRulesServerMixin) */
    public static class_1928 serverRules(MinecraftServer server) {
        return server.method_30002().method_64395();
    }


    /**
     * 1.21.11's GameRules.visitGameRuleTypes(visitor) through the restored 1.21.10 one: this GameRules'
     * visitGameRuleTypes (a mod's override or mixin applies) with a visitor handing the rule of each 1.21.10 key it
     * visits to 1.21.11's visitor, each rule once (the fire spread radius for doFireTick and
     * allowFireTicksAwayFromPlayer); then 1.21.11's rules no 1.21.10 key stands for, in 1.21.11's order. Each rule is
     * visited as 1.21.11's own visit does it: visit(rule), then the rule's callVisitor.
     */
    public static void visitW(class_1928 rules, net.minecraft.class_4311 visitor) {
        java.util.Set<class_12279<?>> seen = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
        ((CbLegacyGameRules) rules).method_20744(new GameRules.GameRuleTypeVisitor() {
            @Override
            public <T extends GameRules.Value<T>> void method_20762(GameRules.Key<T> key, GameRules.Type<T> type) {
                Entry e = entry(key);
                if (e != null && has(rules, e.rule) && seen.add(e.rule)) {
                    visitW(visitor, e.rule);
                }
            }
        });
        rules.field_9196.method_76172().stream().filter(r -> !BY_RULE.containsKey(r) && seen.add(r)).forEach(r -> visitW(visitor, r));
    }

    private static <T> void visitW(net.minecraft.class_4311 visitor, class_12279<T> rule) {
        visitor.method_20762(rule);
        rule.method_76145(visitor);
    }

    /** NotificationService.serverActivityOccured(), which a 1.21.10 implementation never had: 1.21.10 sent no such notification */
    public static void serverActivityOccured(net.minecraft.class_11871 service) {
    }

    /** the keys in id order (a copy: mods may register while it is walked) */
    private static List<Entry> entries() {
        synchronized (BY_ID) {
            return new java.util.ArrayList<>(BY_ID.values());
        }
    }

    /** 1.21.10 GameRules.DEFAULT_RANDOM_TICK_SPEED (a constant: 3) */
    public static int field_30963() {
        return 3;
    }

    /**
     * The 1.21.11 rule a 1.21.10 value stands for: a value getRule handed out by its key; a value of its own by
     * its type, which the key it was registered (or is a vanilla rule) under has.
     */
    public static class_12279<?> ruleOf(GameRules.Value<?> value) {
        if (value.cb$key() != null) {
            return entry(value.cb$key()).rule;
        }
        for (Entry e : BY_ID.values()) {
            if (e.type == value.field_19417) {
                return e.rule;
            }
        }
        throw new IllegalArgumentException("CenturyBridge: a game rule value of an unregistered type has no 1.21.11 rule");
    }

    /** a 1.21.10 value's own reading (a Boolean, Integer, Double or enum constant) */
    static Object plain(GameRules.Value<?> value) {
        return value instanceof GameRules.BooleanValue b ? (Object) b.method_20753()
            : value instanceof GameRules.IntegerValue i ? (Object) i.method_20763()
            : value instanceof DoubleRule d ? (Object) d.get()
            : value instanceof EnumRule<?> en ? en.get() : null;
    }

    /**
     * The value the 1.21.11 rule of a 1.21.10 key holds for a 1.21.10 reading. The fire pair needs the current radius:
     * the bound value's GameRules, else the server's, else none (then as from radius 128: GameRuleRegistryFix's
     * doFireTick true with allowFireTicksAwayFromPlayer false, the 1.21.10 defaults).
     */
    static Object toW(Entry e, Object v, class_1928 rules, MinecraftServer server) {
        return switch (e.kind) {
            case INVERT -> !(Boolean) v;
            case FIRE_TICK, FIRE_AWAY -> fireRadius(rules != null ? (Integer) rules.method_76185(e.rule)
                : server != null ? currentRadius(server) : FIRE_NEAR_PLAYERS, e.kind, (Boolean) v, rules);
            default -> v;
        };
    }

    /** the value the 1.21.11 rule of a 1.21.10 value's key holds for it */
    static Object toW(Entry e, GameRules.Value<?> value, MinecraftServer server) {
        return toW(e, plain(value), value.cb$rules(), server);
    }

    /** the 1.21.10 reading of a 1.21.11 rule value for a 1.21.10 key (rules, may be null: the fire pair's memory) */
    static Object fromW(Entry e, Object w, class_1928 rules) {
        return switch (e.kind) {
            case INVERT -> !(Boolean) w;
            case FIRE_TICK -> (Integer) w != 0;
            case FIRE_AWAY -> (Integer) w == 0 ? rules != null && fireAwayWhileOff(rules) : (Integer) w < 0;
            default -> w;
        };
    }

    /** a 1.21.10 value of its own (not bound to any GameRules) for a key, holding a 1.21.10 reading */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static GameRules.Value<?> standalone(Entry e, Object v) {
        GameRules.Value value = e.type.method_20773();
        value.cb$deserialize(v instanceof Enum<?> en ? en.name() : String.valueOf(v));
        return value;
    }

    /** the key registered under a 1.21.10 id, or null */
    static Entry byName(String id) {
        synchronized (BY_ID) {
            return BY_ID.get(id);
        }
    }

    /** the 1.21.10 keys a 1.21.11 rule stands for, in id order (two for the fire spread radius) */
    static java.util.List<Entry> byRule(class_12279<?> rule) {
        return BY_RULE.getOrDefault(rule, java.util.List.of());
    }

    static String id(Entry e) {
        return e.key.method_20771();
    }

    static class_12279<?> rule(Entry e) {
        return e.rule;
    }

    static boolean has(Entry e, class_1928 rules) {
        return has(rules, e.rule);
    }

    /**
     * MinecraftServer.onGameRuleChanged(name, value) called by a mod: 1.21.10's body told the server's notification
     * manager the rule's name and value; 1.21.11's onGameRuleChanged(rule, value), which a mod would reach here, also
     * runs the rule's effects -- the 1.21.10 call did not, so this tells the manager only, as 1.21.10 did.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void method_74058(MinecraftServer server, String name, GameRules.Value<?> value) {
        Entry e = byName(name);
        if (e != null) {
            server.method_74055().method_73667((class_12279) e.rule, toW(e, value, server));
        }
    }

    // ---- registration (1.21.10 Fabric API GameRuleRegistry)

    /** whether a rule of that 1.21.10 id is registered (vanilla's included) */
    public static boolean hasRegistration(String id) {
        return BY_ID.containsKey(id);
    }

    /**
     * Register a 1.21.10 rule: its key under the 1.21.10 id and a 1.21.11 rule of the same value type, default,
     * argument range, category (or Fabric API custom category) and required features, built by 1.21.11's Fabric
     * API GameRuleBuilder under {@link #identifier(String)}.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends GameRules.Value<T>> GameRules.Key<T> register(String id, GameRules.Category category,
                                                                        GameRules.Type<T> type, CustomGameRuleCategory custom) {
        synchronized (BY_ID) {
            if (BY_ID.containsKey(id)) {
                throw new IllegalStateException("Duplicate game rule registration for " + id);
            }
            GameRules.Key<T> key = new GameRules.Key<>(id, category);
            T def = type.method_20773();
            GameRuleBuilder<?> b;
            Kind kind;
            if (def instanceof GameRules.BooleanValue v) {
                b = GameRuleBuilder.forBoolean(v.method_20753());
                kind = Kind.BOOL;
            } else if (def instanceof GameRules.IntegerValue v) {
                GameRuleBuilder.IntegerRuleBuilder ib = GameRuleBuilder.forInteger(v.method_20763());
                if (type.cb$argument() instanceof IntegerArgumentType a) {
                    ib.range(a.getMinimum(), a.getMaximum());
                }
                b = ib;
                kind = Kind.INT;
            } else if (def instanceof DoubleRule v) {
                GameRuleBuilder.DoubleRuleBuilder db = GameRuleBuilder.forDouble(v.get());
                if (type.cb$argument() instanceof DoubleArgumentType a) {
                    db.range(a.getMinimum(), a.getMaximum());
                }
                b = db;
                kind = Kind.OBJECT;
            } else if (def instanceof EnumRule v) {
                b = GameRuleBuilder.forEnum((Enum) v.get()).supportedValues(v.cb$supported());
                kind = Kind.OBJECT;
            } else {
                throw new IllegalArgumentException("CenturyBridge: game rule " + id + " has a value type 1.21.11 cannot hold ("
                    + def.getClass().getName() + "): only boolean, integer, double and enum rules have a 1.21.11 rule type");
            }
            if (custom != null) {
                b.category(custom);
            } else {
                b.category(category.cb$toW());
            }
            b.requiredFeatures(type.method_61728());
            class_12279<?> rule = b.build();
            class_2378.method_10230(class_7923.field_64251, identifier(id), rule);
            Entry e = new Entry(key, type, rule, kind, true, custom);
            BY_ID.put(id, e);
            MOD_BY_RULE.put(rule, e);
            index(e);
            return key;
        }
    }

    /**
     * The 1.21.11 id of a 1.21.10 rule id: the id itself where it is a valid identifier, else its camelCase as
     * snake_case (1.21.11's own renames, keepInventory -> keep_inventory), lower case, characters an identifier
     * refuses as '_', in the namespace before a ':' or minecraft; under centurybridge:legacy/ where that id is
     * already registered (a 1.21.11 vanilla rule of that name).
     */
    public static class_2960 identifier(String id) {
        class_2960 direct = class_2960.method_12829(id);
        class_2960 r = direct;
        if (r == null) {
            int colon = id.indexOf(':');
            String ns = colon < 0 ? "minecraft" : sanitize(id.substring(0, colon), false);
            String path = sanitize(snake(colon < 0 ? id : id.substring(colon + 1)), true);
            r = class_2960.method_60655(ns.isEmpty() ? "minecraft" : ns, path.isEmpty() ? "rule" : path);
        }
        if (class_7923.field_64251.method_10250(r)) {
            r = class_2960.method_60655("centurybridge", "legacy/" + sanitize(snake(r.method_12836() + "_" + r.method_12832()), true));
        }
        return r;
    }

    private static String snake(String s) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c) && i > 0 && (Character.isLowerCase(s.charAt(i - 1)) || Character.isDigit(s.charAt(i - 1)))) {
                b.append('_');
            }
            b.append(c);
        }
        return b.toString().toLowerCase(Locale.ROOT);
    }

    private static String sanitize(String s, boolean path) {
        StringBuilder b = new StringBuilder();
        for (char c : s.toLowerCase(Locale.ROOT).toCharArray()) {
            boolean ok = c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '_' || c == '-' || c == '.' || path && c == '/';
            b.append(ok ? c : '_');
        }
        return b.toString();
    }

    /** the Fabric API custom category a 1.21.10 key was registered under */
    public static Optional<CustomGameRuleCategory> customCategory(GameRules.Key<?> key) {
        Entry e = entry(key);
        return Optional.ofNullable(e == null ? null : e.custom);
    }

    // ---- 1.21.11's game rule hook (CbGameRulesServerMixin)

    /** a mod rule changed on 1.21.11: its 1.21.10 callback, with the server's value (MinecraftServer.getGameRules()) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void onRuleChanged(MinecraftServer server, class_12279<?> rule) {
        Entry e;
        synchronized (BY_ID) {
            e = MOD_BY_RULE.get(rule);
        }
        if (e == null) {
            return;
        }
        GameRules.Value value = live(serverRules(server), e.key);
        ((GameRules.Type) e.type).cb$callback().accept(server, value);
    }

    /** the 1.21.10 description id of a mod rule ("gamerule." + its 1.21.10 id: where its language files name it) */
    public static String descriptionId(class_12279<?> rule) {
        Entry e;
        synchronized (BY_ID) {
            e = MOD_BY_RULE.get(rule);
        }
        return e == null ? null : e.key.method_27334();
    }
}
