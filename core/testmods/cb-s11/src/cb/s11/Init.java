package cb.s11;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.minecraft.class_1928;
import net.minecraft.class_2561;
import net.minecraft.class_2960;

/** registers four 1.21.10 Fabric API game rules, the way a 1.21.10 mod did */
public final class Init implements ModInitializer {
    public enum Mode { A, B, C }

    public static class_1928.class_4313<class_1928.class_4310> FLAG;
    public static class_1928.class_4313<class_1928.class_4312> COUNT;
    public static class_1928.class_4313<DoubleRule> RATIO;
    public static class_1928.class_4313<EnumRule<Mode>> MODE;
    public static final CustomGameRuleCategory CATEGORY =
        new CustomGameRuleCategory(class_2960.method_60655("cb_s11", "tests"), class_2561.method_43470("CB s11"));
    public static volatile int flagCalls;
    public static volatile Boolean lastFlag;

    @Override
    public void onInitialize() {
        FLAG = GameRuleRegistry.register("cbS11Flag", class_1928.class_5198.field_24100,
            GameRuleFactory.createBooleanRule(false, (server, value) -> {
                flagCalls++;
                lastFlag = value.method_20753();
            }));
        COUNT = GameRuleRegistry.register("cbS11Count", class_1928.class_5198.field_24100, GameRuleFactory.createIntRule(5, 0, 10));
        RATIO = GameRuleRegistry.register("cbS11Ratio", class_1928.class_5198.field_24100, GameRuleFactory.createDoubleRule(0.5, 0.0, 1.0));
        MODE = GameRuleRegistry.register("cbS11Mode", CATEGORY, GameRuleFactory.createEnumRule(Mode.A));
    }
}
