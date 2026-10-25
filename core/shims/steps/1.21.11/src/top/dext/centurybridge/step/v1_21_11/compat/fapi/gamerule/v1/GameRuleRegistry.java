package top.dext.centurybridge.step.v1_21_11.compat.fapi.gamerule.v1;

import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import top.dext.centurybridge.step.v1_21_11.facade.GameRules;
import top.dext.centurybridge.step.v1_21_11.rt.CbGameRules;

/**
 * 1.21.10's Fabric API GameRuleRegistry, which 1.21.11's Fabric API replaced by GameRuleBuilder.buildAndRegister.
 * As 1.21.10: register(name, category, type) is GameRules.register; with a custom category the rule's vanilla
 * category is MISC and the custom one is remembered; hasRegistration asks for a rule of that name, vanilla's
 * included. Each registration also registers the 1.21.11 rule (rt/CbGameRules.register).
 */
public final class GameRuleRegistry {
    private GameRuleRegistry() {
    }

    public static <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type) {
        return CbGameRules.register(name, category, type, null);
    }

    public static <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, CustomGameRuleCategory category, GameRules.Type<T> type) {
        return CbGameRules.register(name, GameRules.Category.field_24100, type, category);
    }

    public static boolean hasRegistration(String ruleName) {
        return CbGameRules.hasRegistration(ruleName);
    }
}
