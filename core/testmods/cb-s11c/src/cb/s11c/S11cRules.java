package cb.s11c;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.minecraft.class_1928;
import net.minecraft.class_2350;
import net.minecraft.class_2561;
import net.minecraft.class_2960;

/** the rules, registered through 1.21.10's Fabric game rule API */
public class S11cRules implements ModInitializer {
    public static class_1928.class_4313<class_1928.class_4310> BOOL;
    public static class_1928.class_4313<class_1928.class_4312> INT;
    public static class_1928.class_4313<DoubleRule> DOUBLE;
    public static class_1928.class_4313<EnumRule<class_2350>> ENUM;
    public static class_1928.class_4313<class_1928.class_4310> CUSTOM;
    public static class_1928.class_4313<class_1928.class_4310> SUB_BOOL;
    public static class_1928.class_4313<class_1928.class_4312> SUB_INT;
    public static final CustomGameRuleCategory CATEGORY =
        new CustomGameRuleCategory(class_2960.method_60655("cb_s11", "custom"), class_2561.method_43470("CB Custom Category"));

    @Override
    public void onInitialize() {
        BOOL = GameRuleRegistry.register("cbBoolRule", class_1928.class_5198.field_24094, GameRuleFactory.createBooleanRule(true));
        INT = GameRuleRegistry.register("cbIntRule", class_1928.class_5198.field_24095, GameRuleFactory.createIntRule(7, 0, 100));
        DOUBLE = GameRuleRegistry.register("cbDoubleRule", class_1928.class_5198.field_24100, GameRuleFactory.createDoubleRule(0.5, 0.0, 1.0));
        ENUM = GameRuleRegistry.register("cbEnumRule", class_1928.class_5198.field_24100, GameRuleFactory.createEnumRule(class_2350.field_11043));
        CUSTOM = GameRuleRegistry.register("cbCustomRule", CATEGORY, GameRuleFactory.createBooleanRule(false));
        // the rows the mod builds itself (check 5); kept off the vanilla screen's own visitor by living in a custom
        // category the subclass rows stand for
        SUB_BOOL = GameRuleRegistry.register("cbSubBool", CATEGORY, GameRuleFactory.createBooleanRule(false));
        SUB_INT = GameRuleRegistry.register("cbSubInt", CATEGORY, GameRuleFactory.createIntRule(3, 0, 50));
    }
}
