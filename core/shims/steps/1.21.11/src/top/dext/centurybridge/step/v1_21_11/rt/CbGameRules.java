package top.dext.centurybridge.step.v1_21_11.rt;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicLike;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
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
    /** per GameRules: the values getRule handed out (1.21.10's rules map) */
    private static final Map<class_1928, Map<String, GameRules.Value<?>>> VALUES = new WeakHashMap<>();
    /** per GameRules: allowFireTicksAwayFromPlayer while fire spread is off (1.21.11's radius 0 cannot hold it) */
    private static final Map<class_1928, Boolean> FIRE_AWAY_WHILE_OFF = new WeakHashMap<>();

    static {
        CbGameRulesVanilla.register();
    }

    // ---- the tables

    static void vanilla(String id, GameRules.Category category, Kind kind, class_12279<?> rule) {
        GameRules.Key<?> key = new GameRules.Key<>(id, category);
        GameRules.Type<?> type = vanillaType(kind, rule);
        BY_ID.put(id, new Entry(key, type, rule, kind, false, null));
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
        return server.method_30002().method_64395().method_76185(class_1928.field_56559);
    }

    /** the key of a vanilla 1.21.10 rule (GameRules.RULE_* reads) */
    public static GameRules.Key<?> vanillaKey(String id) {
        return BY_ID.get(id).key;
    }

    private static Entry entry(GameRules.Key<?> key) {
        return key == null ? null : BY_ID.get(key.method_20771());
    }

    // ---- reading and writing a bound value

    private static boolean has(class_1928 rules, class_12279<?> rule) {
        return rules.method_76181().anyMatch(r -> r == rule);
    }

    /** a bound value's 1.21.10 reading */
    public static Object read(class_1928 rules, GameRules.Key<?> key) {
        Entry e = entry(key);
        Object w = rules.method_76185(e.rule);
        return switch (e.kind) {
            case INVERT -> !(Boolean) w;
            case FIRE_TICK -> (Integer) w != 0;
            case FIRE_AWAY -> (Integer) w == 0 ? fireAwayWhileOff(rules) : (Integer) w < 0;
            default -> w;
        };
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
            case FIRE_TICK, FIRE_AWAY -> fireRadius((Integer) rules.method_76185(e.rule), e.kind, (Boolean) v, rules);
            default -> v;
        };
        ((class_1928) rules).method_76186((class_12279) e.rule, w, server);
    }

    /** a bound value's onChanged(server): 1.21.11's game rule hook with the rule's current value */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void changed(class_1928 rules, GameRules.Key<?> key, MinecraftServer server) {
        if (server != null) {
            Entry e = entry(key);
            server.method_74058((class_12279) e.rule, rules.method_76185(e.rule));
        }
    }

    // ---- 1.21.10 GameRules members

    /** GameRules.getRule(key): this GameRules' value of the key; invalid (not registered or not enabled) throws */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static GameRules.Value method_20746(class_1928 rules, GameRules.Key key) {
        Entry e = entry(key);
        if (e == null || !has(rules, e.rule)) {
            throw new IllegalArgumentException("Tried to access invalid game rule");
        }
        synchronized (VALUES) {
            Map<String, GameRules.Value<?>> m = VALUES.computeIfAbsent(rules, r -> new HashMap<>());
            GameRules.Value v = m.get(e.key.method_20771());
            if (v == null) {
                v = e.type.method_20773();
                v.cb$bind(rules, e.key);
                m.put(e.key.method_20771(), v);
            }
            return v;
        }
    }

    public static boolean method_8355(class_1928 rules, GameRules.Key<GameRules.BooleanValue> key) {
        return ((GameRules.BooleanValue) method_20746(rules, key)).method_20753();
    }

    public static int method_8356(class_1928 rules, GameRules.Key<GameRules.IntegerValue> key) {
        return ((GameRules.IntegerValue) method_20746(rules, key)).method_20763();
    }

    /** GameRules.createTag(): each enabled rule's 1.21.10 string under its 1.21.10 id */
    public static class_2487 method_8358(class_1928 rules) {
        class_2487 tag = new class_2487();
        for (Entry e : BY_ID.values()) {
            if (has(rules, e.rule)) {
                tag.method_10582(e.key.method_20771(), method_20746(rules, e.key).method_20779());
            }
        }
        return tag;
    }

    /** new GameRules(features, dynamic): the rules of those features, each read from its 1.21.10 id where present */
    public static class_1928 newGameRules(class_7699 features, DynamicLike<?> dynamic) {
        class_1928 rules = new class_1928(features);
        for (Entry e : BY_ID.values()) {
            if (has(rules, e.rule)) {
                GameRules.Value<?> value = method_20746(rules, e.key);
                dynamic.get(e.key.method_20771()).asString().ifSuccess(value::cb$deserialize);
            }
        }
        return rules;
    }

    /** GameRules.visitGameRuleTypes(visitor): every enabled rule in id order, visit then the type's own call */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void method_20744(class_1928 rules, GameRules.GameRuleTypeVisitor visitor) {
        for (Entry e : BY_ID.values()) {
            if (has(rules, e.rule)) {
                GameRules.Type type = e.type;
                visitor.method_20762((GameRules.Key) e.key, type);
                type.method_27336(visitor, (GameRules.Key) e.key);
            }
        }
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
            Entry e = BY_ID.get(id);
            return e != null && e.type.cb$valueClass() == valueClass
                ? DataResult.success(e.key)
                : DataResult.error(() -> "Invalid game rule ID for type: " + id);
        }, k -> ((GameRules.Key<?>) k).method_20771());
    }

    /** GameRules.availableRules(features) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Stream method_61725(class_7699 features) {
        return BY_ID.values().stream().filter(e -> e.type.method_61728().method_45400(features))
            .map(e -> new AbstractMap.SimpleImmutableEntry(e.key, e.type));
    }

    /** MinecraftServer.getGameRules(): the overworld's (1.21.10's body) */
    public static class_1928 method_3767(MinecraftServer server) {
        return server.method_30002().method_64395();
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

    /** the value a 1.21.10 value has, as the 1.21.11 rule holds it */
    private static Object toW(Entry e, GameRules.Value<?> value, MinecraftServer server) {
        Object v = value instanceof GameRules.BooleanValue b ? (Object) b.method_20753()
            : value instanceof GameRules.IntegerValue i ? (Object) i.method_20763()
            : value instanceof DoubleRule d ? (Object) d.get()
            : value instanceof EnumRule<?> en ? en.get() : null;
        return switch (e.kind) {
            case INVERT -> !(Boolean) v;
            case FIRE_TICK, FIRE_AWAY -> fireRadius(currentRadius(server), e.kind, (Boolean) v, null);
            default -> v;
        };
    }

    /**
     * MinecraftServer.onGameRuleChanged(name, value): 1.21.10 told the server's notification manager (its body);
     * 1.21.11's manager is told the rule and its value.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void method_74058(MinecraftServer server, String name, GameRules.Value<?> value) {
        Entry e = BY_ID.get(name);
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
        GameRules.Value value = method_20746(method_3767(server), e.key);
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
