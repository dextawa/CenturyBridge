package top.dext.centurybridge.step.v1_21_11.rt;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_11827;
import net.minecraft.class_11829;
import net.minecraft.class_11838;
import net.minecraft.class_11844;
import net.minecraft.class_11845;
import net.minecraft.class_11848;
import net.minecraft.class_11871;
import net.minecraft.class_11993;
import net.minecraft.class_12279;
import net.minecraft.class_12280;
import net.minecraft.class_1928;
import net.minecraft.server.MinecraftServer;
import top.dext.centurybridge.step.v1_21_11.facade.GameRules;
import top.dext.centurybridge.step.v1_21_11.facade.GameRulesService;
import top.dext.centurybridge.step.v1_21_11.facade.SetGameRulesEntry;

/**
 * 1.21.10's game rule members of the server management protocol (jsonrpc GameRulesService, MinecraftGameRuleService),
 * the notification service, MinecraftServer.onGameRuleChanged and gametest SetGameRules on 1.21.11, which names rules
 * by GameRule&lt;T&gt; and values by T where 1.21.10 named them by id and Value.
 *
 * <p>A 1.21.10 key stands for one 1.21.11 rule (CbGameRules); the fire spread radius stands for two 1.21.10 keys
 * (doFireTick, allowFireTicksAwayFromPlayer). Where 1.21.11 hands a converted mod's 1.21.10-shaped method a rule, that
 * method is asked once per 1.21.10 key the rule stands for -- 1.21.10 would have named each of those rules on its own --
 * and its answers are combined back into the 1.21.11 value ({@link #combine}); a 1.21.11 rule no 1.21.10 key stands for
 * (one 1.21.10 did not have) is not handed to it, as 1.21.10 never could.
 */
public final class CbGameRulesRpc {
    private CbGameRulesRpc() {
    }

    // ---- shared conversions

    private static CbGameRules.Entry entry(String name) {
        CbGameRules.Entry e = CbGameRules.byName(name);
        if (e == null) {
            throw new class_11848("Game rule '" + name + "' does not exist");
        }
        return e;
    }

    /** a TypedRule for a key and its 1.21.10 reading: 1.21.10 typed only boolean and integer values */
    private static GameRulesService.TypedRule typed(CbGameRules.Entry e, Object v) {
        if (v instanceof Boolean b) {
            return new GameRulesService.TypedRule(CbGameRules.id(e), String.valueOf(b), class_11845.field_62400);
        }
        if (v instanceof Integer i) {
            return new GameRulesService.TypedRule(CbGameRules.id(e), String.valueOf(i), class_11845.field_62399);
        }
        throw new class_11848("Unknown rule type");
    }

    /** the TypedRule of a 1.21.11 update for the key it was asked under */
    private static GameRulesService.TypedRule typed(CbGameRules.Entry e, class_11844.class_12254<?> update, class_1928 rules) {
        return typed(e, CbGameRules.fromW(e, update.comp_5162(), rules));
    }

    /** 1.21.10 MinecraftGameRuleServiceImpl's parse of an untyped value for a key: boolean or integer, else refused */
    private static Object parse(CbGameRules.Entry e, String value) {
        GameRules.Value<?> probe = e.type.method_20773();
        if (probe instanceof GameRules.BooleanValue) {
            return Boolean.parseBoolean(value);
        }
        if (probe instanceof GameRules.IntegerValue) {
            return Integer.parseInt(value);
        }
        throw new class_11848("Unknown rule type for key: " + CbGameRules.id(e));
    }

    /**
     * The 1.21.11 value of a rule from the 1.21.10 readings of the keys standing for it: the reading itself for a rule
     * one key stands for; for the fire spread radius, GameRuleRegistryFix's mapping of doFireTick and
     * allowFireTicksAwayFromPlayer, keeping a positive radius 1.21.11 had where the pair does not change it.
     */
    private static Object combine(class_12279<?> rule, Map<CbGameRules.Entry, Object> readings, Object current) {
        if (readings.size() == 1) {
            Map.Entry<CbGameRules.Entry, Object> only = readings.entrySet().iterator().next();
            return CbGameRules.toW(only.getKey(), only.getValue(), null, null);
        }
        Boolean tick = null;
        Boolean away = null;
        for (Map.Entry<CbGameRules.Entry, Object> r : readings.entrySet()) {
            if (r.getKey().kind == CbGameRules.Kind.FIRE_TICK) {
                tick = (Boolean) r.getValue();
            } else if (r.getKey().kind == CbGameRules.Kind.FIRE_AWAY) {
                away = (Boolean) r.getValue();
            }
        }
        int radius = current instanceof Integer i ? i : 128;
        if (tick != null && !tick) {
            return 0;
        }
        if (away != null && away) {
            return -1;
        }
        return radius > 0 ? radius : 128;
    }

    private static class_1928 serverRules(class_11829 service) {
        return service instanceof class_11993 impl ? impl.field_63174 : null;
    }

    // ---- GameRulesService (statics) and MinecraftGameRuleService (calls on any implementation)

    /** GameRulesService.updateGameRule(api, untyped, user): 1.21.10 asked the api's game rule service */
    public static GameRulesService.TypedRule method_73875(class_11827 api, GameRulesService.UntypedRule rule, class_11838 user) {
        return method_73754(api.method_73742(), rule, user);
    }

    /** GameRulesService.getTypedRule(api, name, value): 1.21.10 asked the api's game rule service */
    public static GameRulesService.TypedRule method_73876(class_11827 api, String name, GameRules.Value<?> value) {
        return method_73757(api.method_73742(), name, value);
    }

    /** MinecraftGameRuleService.updateGameRule(untyped, user): the value parsed for the key's type, set by 1.21.11 */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static GameRulesService.TypedRule method_73754(class_11829 service, GameRulesService.UntypedRule rule, class_11838 user) {
        CbGameRules.Entry e = entry(rule.comp_4722());
        class_1928 rules = serverRules(service);
        Object v = parse(e, rule.comp_4723());
        Object w = rules != null && (e.kind == CbGameRules.Kind.FIRE_TICK || e.kind == CbGameRules.Kind.FIRE_AWAY)
            ? CbGameRules.toW(e, v, rules, null) : CbGameRules.toW(e, v, null, null);
        class_11844.class_12254<?> update = service.method_73754(new class_11844.class_12254(CbGameRules.rule(e), w), user);
        return typed(e, update, rules);
    }

    /** MinecraftGameRuleService.getRuleValue(key): the server's live value (vanilla's service), else the answer held */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static GameRules.Value<?> method_73755(class_11829 service, GameRules.Key<?> key) {
        CbGameRules.Entry e = entry(key.method_20771());
        class_1928 rules = serverRules(service);
        if (rules != null) {
            return CbGameRules.live(rules, key);
        }
        return CbGameRules.standalone(e, CbGameRules.fromW(e, service.method_73755((class_12279) CbGameRules.rule(e)), null));
    }

    /** MinecraftGameRuleService.getTypedRule(name, value) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static GameRulesService.TypedRule method_73757(class_11829 service, String name, GameRules.Value<?> value) {
        CbGameRules.Entry e = entry(name);
        class_11844.class_12254<?> update = service.method_73757((class_12279) CbGameRules.rule(e), CbGameRules.toW(e, value, null));
        return typed(e, update, value.cb$rules());
    }

    // ---- a converted mod's implementation of the 1.21.10 MinecraftGameRuleService (overrideHooks bridges)

    /** the 1.21.10 MinecraftGameRuleService.updateGameRule a converted mod's implementation declares */
    public interface LegacyUpdate {
        GameRulesService.TypedRule method_73754(GameRulesService.UntypedRule rule, class_11838 user);
    }

    /** the 1.21.10 MinecraftGameRuleService.getRuleValue a converted mod's implementation declares */
    public interface LegacyGet {
        <T extends GameRules.Value<T>> T method_73755(GameRules.Key<T> key);
    }

    /** the 1.21.10 MinecraftGameRuleService.getTypedRule a converted mod's implementation declares */
    public interface LegacyTyped {
        GameRulesService.TypedRule method_73757(String name, GameRules.Value<?> value);
    }

    /** 1.21.11 updateGameRule(update, user) on such an implementation: its 1.21.10 one, once per key of the rule */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_11844.class_12254 updateBridge(LegacyUpdate self, class_11844.class_12254 update, class_11838 user) {
        Map<CbGameRules.Entry, Object> readings = new LinkedHashMap<>();
        for (CbGameRules.Entry e : CbGameRules.byRule(update.comp_5161())) {
            Object v = CbGameRules.fromW(e, update.comp_5162(), null);
            GameRulesService.TypedRule t = self.method_73754(new GameRulesService.UntypedRule(CbGameRules.id(e), stringOf(v)), user);
            readings.put(e, parse(e, t.comp_4720()));
        }
        return readings.isEmpty() ? update : new class_11844.class_12254(update.comp_5161(), combine(update.comp_5161(), readings, update.comp_5162()));
    }

    /** 1.21.11 getRuleValue(rule) on such an implementation */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object getBridge(LegacyGet self, class_12279 rule) {
        Map<CbGameRules.Entry, Object> readings = new LinkedHashMap<>();
        for (CbGameRules.Entry e : CbGameRules.byRule(rule)) {
            readings.put(e, CbGameRules.plain(self.method_73755((GameRules.Key) e.key)));
        }
        return readings.isEmpty() ? rule.method_76156() : combine(rule, readings, null);
    }

    /** 1.21.11 getTypedRule(rule, value) on such an implementation */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class_11844.class_12254 typedBridge(LegacyTyped self, class_12279 rule, Object value) {
        Map<CbGameRules.Entry, Object> readings = new LinkedHashMap<>();
        for (CbGameRules.Entry e : CbGameRules.byRule(rule)) {
            Object v = CbGameRules.fromW(e, value, null);
            GameRulesService.TypedRule t = self.method_73757(CbGameRules.id(e), CbGameRules.standalone(e, v));
            readings.put(e, parse(e, t.comp_4720()));
        }
        return new class_11844.class_12254(rule, readings.isEmpty() ? value : combine(rule, readings, value));
    }

    /** 1.21.11 MinecraftGameRuleServiceImpl's methods, reached by a converted mod subclass's 1.21.10 super calls */
    public interface SuperService {
        class_11844.class_12254 cb$super$method_73754(class_11844.class_12254 update, class_11838 user);

        Object cb$super$method_73755(class_12279 rule);

        class_11844.class_12254 cb$super$method_73757(class_12279 rule, Object value);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static GameRulesService.TypedRule superUpdate(SuperService self, GameRulesService.UntypedRule rule, class_11838 user) {
        CbGameRules.Entry e = entry(rule.comp_4722());
        class_1928 rules = serverRules((class_11829) self);
        Object w = CbGameRules.toW(e, parse(e, rule.comp_4723()), rules, null);
        return typed(e, self.cb$super$method_73754(new class_11844.class_12254(CbGameRules.rule(e), w), user), rules);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static GameRules.Value<?> superGet(SuperService self, GameRules.Key<?> key) {
        CbGameRules.Entry e = entry(key.method_20771());
        class_1928 rules = serverRules((class_11829) self);
        if (rules != null) {
            return CbGameRules.live(rules, key);
        }
        return CbGameRules.standalone(e, CbGameRules.fromW(e, self.cb$super$method_73755(CbGameRules.rule(e)), null));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static GameRulesService.TypedRule superTyped(SuperService self, String name, GameRules.Value<?> value) {
        CbGameRules.Entry e = entry(name);
        return typed(e, self.cb$super$method_73757(CbGameRules.rule(e), CbGameRules.toW(e, value, null)), value.cb$rules());
    }

    private static String stringOf(Object v) {
        return v instanceof Enum<?> en ? en.name() : String.valueOf(v);
    }

    // ---- NotificationService.onGameRuleChanged(name, value)

    /** a converted mod's call: the 1.21.11 notification of the key's rule */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void method_73667(class_11871 service, String name, GameRules.Value<?> value) {
        CbGameRules.Entry e = CbGameRules.byName(name);
        if (e != null) {
            service.method_73667((class_12279) CbGameRules.rule(e), CbGameRules.toW(e, value, null));
        }
    }

    /** the 1.21.10 NotificationService.onGameRuleChanged a converted mod's notification service declares */
    public interface LegacyNotification {
        void method_73667(String name, GameRules.Value<?> value);
    }

    /** 1.21.11 onGameRuleChanged(rule, value) on such a service: its 1.21.10 one, once per key of the rule */
    public static void notifyBridge(LegacyNotification self, class_12279<?> rule, Object value) {
        for (CbGameRules.Entry e : CbGameRules.byRule(rule)) {
            self.method_73667(CbGameRules.id(e), CbGameRules.standalone(e, CbGameRules.fromW(e, value, null)));
        }
    }

    /** 1.21.11's onGameRuleChanged(rule, value) of the vanilla service a converted mod's subclass extends */
    public interface SuperNotification {
        void cb$super$method_73667(class_12279 rule, Object value);
    }

    /** such a subclass's 1.21.10 super.onGameRuleChanged(name, value) */
    public static void superNotify(SuperNotification self, String name, GameRules.Value<?> value) {
        CbGameRules.Entry e = CbGameRules.byName(name);
        if (e != null) {
            self.cb$super$method_73667(CbGameRules.rule(e), CbGameRules.toW(e, value, null));
        }
    }

    // ---- MinecraftServer.onGameRuleChanged(name, value) overridden by a converted mod's server

    /** the 1.21.10 MinecraftServer.onGameRuleChanged a converted mod's server subclass declares */
    public interface LegacyServer {
        void method_74058(String name, GameRules.Value<?> value);
    }

    /**
     * 1.21.11 onGameRuleChanged(rule, value) on such a server: its 1.21.10 one, once per key of the rule, with the
     * server's live value of that key (1.21.10 passed the value that had just changed).
     */
    public static void serverBridge(LegacyServer self, class_12279<?> rule, Object value) {
        class_1928 rules = CbGameRules.serverRules((MinecraftServer) self);
        for (CbGameRules.Entry e : CbGameRules.byRule(rule)) {
            if (CbGameRules.has(e, rules)) {
                self.method_74058(CbGameRules.id(e), CbGameRules.live(rules, e.key));
            }
        }
    }

    /** 1.21.11's MinecraftServer.onGameRuleChanged(rule, value) (notification and the rule's effects) */
    public interface SuperServer {
        void cb$super$method_74058(class_12279 rule, Object value);
    }

    /**
     * Such a server's 1.21.10 super.onGameRuleChanged(name, value): 1.21.11's, which notifies and runs the rule's
     * effects (1.21.10 ran the effects from the rule's own callback before calling it).
     */
    public static void superServer(SuperServer self, String name, GameRules.Value<?> value) {
        CbGameRules.Entry e = CbGameRules.byName(name);
        if (e != null) {
            self.cb$super$method_74058(CbGameRules.rule(e), CbGameRules.toW(e, value, (MinecraftServer) self));
        }
    }

    // ---- gametest TestEnvironmentDefinition.SetGameRules(boolRules, intRules)

    /**
     * new SetGameRules(boolRules, intRules): 1.21.11's SetGameRules of a GameRuleMap with the 1.21.11 value of each
     * entry's rule (setup sets them, teardown restores, as 1.21.10). An entry of only one of the fire pair takes the
     * other's 1.21.10 default (doFireTick true, allowFireTicksAwayFromPlayer false): the map holds one radius.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static net.minecraft.class_10665.class_10668 newSetGameRules(List<SetGameRulesEntry<Boolean, GameRules.BooleanValue>> boolRules,
                                                                          List<SetGameRulesEntry<Integer, GameRules.IntegerValue>> intRules) {
        Map<class_12279<?>, Map<CbGameRules.Entry, Object>> byRule = new LinkedHashMap<>();
        for (List<? extends SetGameRulesEntry<?, ?>> list : List.of(boolRules, intRules)) {
            for (SetGameRulesEntry<?, ?> entry : list) {
                CbGameRules.Entry e = entry(entry.comp_3554().method_20771());
                byRule.computeIfAbsent(CbGameRules.rule(e), r -> new LinkedHashMap<>()).put(e, entry.comp_3555());
            }
        }
        class_12280.class_12281 b = new class_12280.class_12281();
        byRule.forEach((rule, readings) -> b.method_76180((class_12279) rule, combine(rule, readings, null)));
        return new net.minecraft.class_10665.class_10668(b.method_76179());
    }

    /** SetGameRules.boolRules(): the 1.21.10 boolean keys of the map's rules, as entries */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List comp_3552(net.minecraft.class_10665.class_10668 rules) {
        return entries(rules.comp_5194(), Boolean.class);
    }

    /** SetGameRules.intRules(): the 1.21.10 integer keys of the map's rules, as entries */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List comp_3553(net.minecraft.class_10665.class_10668 rules) {
        return entries(rules.comp_5194(), Integer.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static List<SetGameRulesEntry<?, ?>> entries(class_12280 map, Class<?> type) {
        List<SetGameRulesEntry<?, ?>> out = new java.util.ArrayList<>();
        for (class_12279<?> rule : map.method_76172()) {
            for (CbGameRules.Entry e : CbGameRules.byRule(rule)) {
                Object v = CbGameRules.fromW(e, map.method_76173(rule), null);
                if (type.isInstance(v)) {
                    out.add(new SetGameRulesEntry(e.key, v));
                }
            }
        }
        return out;
    }

    /** SetGameRules.entry(key, value) */
    public static <S, T extends GameRules.Value<T>> SetGameRulesEntry<S, T> method_67063(GameRules.Key<T> key, S value) {
        return new SetGameRulesEntry<>(key, value);
    }
}
