package top.dext.centurybridge.step.v1_21_11.compat.fapi.gamerule.v1;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.google.common.base.Preconditions;
import java.util.function.BiConsumer;
import net.minecraft.class_7699;
import net.minecraft.server.MinecraftServer;
import top.dext.centurybridge.step.v1_21_11.compat.fapi.gamerule.v1.rule.DoubleRule;
import top.dext.centurybridge.step.v1_21_11.compat.fapi.gamerule.v1.rule.EnumRule;
import top.dext.centurybridge.step.v1_21_11.facade.GameRules;

/**
 * 1.21.10's Fabric API GameRuleFactory, which 1.21.11's Fabric API replaced by GameRuleBuilder. The same types as
 * 1.21.10 (facade/GameRules.Type): argument, value factory, callback, visitor call, value class and the vanilla
 * features; GameRuleRegistry.register turns them into 1.21.11 rules (rt/CbGameRules). 1.21.10's bounded integer
 * values (Fabric's BoundedIntRule) are integer values whose argument carries the bounds; 1.21.11's integer rule
 * enforces them (GameRuleBuilder.range).
 */
public final class GameRuleFactory {
    private GameRuleFactory() {
    }

    public static GameRules.Type<GameRules.BooleanValue> createBooleanRule(boolean defaultValue) {
        return createBooleanRule(defaultValue, (server, rule) -> {
        });
    }

    public static GameRules.Type<GameRules.BooleanValue> createBooleanRule(boolean defaultValue, BiConsumer<MinecraftServer, GameRules.BooleanValue> changedCallback) {
        return GameRules.BooleanValue.method_20760(defaultValue, changedCallback);
    }

    public static GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue) {
        return createIntRule(defaultValue, (server, rule) -> {
        });
    }

    public static GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue, int minimumValue) {
        return createIntRule(defaultValue, minimumValue, Integer.MAX_VALUE, (server, rule) -> {
        });
    }

    public static GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue, int minimumValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> changedCallback) {
        return createIntRule(defaultValue, minimumValue, Integer.MAX_VALUE, changedCallback);
    }

    public static GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue, int minimumValue, int maximumValue) {
        return createIntRule(defaultValue, minimumValue, maximumValue, (server, rule) -> {
        });
    }

    public static GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> changedCallback) {
        return createIntRule(defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE, changedCallback);
    }

    public static GameRules.Type<GameRules.IntegerValue> createIntRule(int defaultValue, int minimumValue, int maximumValue,
                                                                       BiConsumer<MinecraftServer, GameRules.IntegerValue> changedCallback) {
        return new GameRules.Type<>(() -> IntegerArgumentType.integer(minimumValue, maximumValue),
            type -> new GameRules.IntegerValue(type, defaultValue), changedCallback,
            GameRules.GameRuleTypeVisitor::method_27330, GameRules.IntegerValue.class, class_7699.method_45397());
    }

    public static GameRules.Type<DoubleRule> createDoubleRule(double defaultValue) {
        return createDoubleRule(defaultValue, (server, rule) -> {
        });
    }

    public static GameRules.Type<DoubleRule> createDoubleRule(double defaultValue, double minimumValue) {
        return createDoubleRule(defaultValue, minimumValue, Double.MAX_VALUE, (server, rule) -> {
        });
    }

    public static GameRules.Type<DoubleRule> createDoubleRule(double defaultValue, double minimumValue, BiConsumer<MinecraftServer, DoubleRule> changedCallback) {
        return createDoubleRule(defaultValue, minimumValue, Double.MAX_VALUE, changedCallback);
    }

    public static GameRules.Type<DoubleRule> createDoubleRule(double defaultValue, double minimumValue, double maximumValue) {
        return createDoubleRule(defaultValue, minimumValue, maximumValue, (server, rule) -> {
        });
    }

    public static GameRules.Type<DoubleRule> createDoubleRule(double defaultValue, BiConsumer<MinecraftServer, DoubleRule> changedCallback) {
        return createDoubleRule(defaultValue, Double.MIN_VALUE, Double.MAX_VALUE, changedCallback);
    }

    public static GameRules.Type<DoubleRule> createDoubleRule(double defaultValue, double minimumValue, double maximumValue,
                                                             BiConsumer<MinecraftServer, DoubleRule> changedCallback) {
        return new GameRules.Type<>(() -> DoubleArgumentType.doubleArg(minimumValue, maximumValue),
            type -> new DoubleRule(type, defaultValue, minimumValue, maximumValue), changedCallback,
            GameRuleFactory::visitDouble, DoubleRule.class, class_7699.method_45397());
    }

    public static <E extends Enum<E>> GameRules.Type<EnumRule<E>> createEnumRule(E defaultValue) {
        return createEnumRule(defaultValue, (server, rule) -> {
        });
    }

    public static <E extends Enum<E>> GameRules.Type<EnumRule<E>> createEnumRule(E defaultValue, BiConsumer<MinecraftServer, EnumRule<E>> changedCallback) {
        return createEnumRule(defaultValue, defaultValue.getDeclaringClass().getEnumConstants(), changedCallback);
    }

    public static <E extends Enum<E>> GameRules.Type<EnumRule<E>> createEnumRule(E defaultValue, E[] supportedValues) {
        return createEnumRule(defaultValue, supportedValues, (server, rule) -> {
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <E extends Enum<E>> GameRules.Type<EnumRule<E>> createEnumRule(E defaultValue, E[] supportedValues,
                                                                                BiConsumer<MinecraftServer, EnumRule<E>> changedCallback) {
        Preconditions.checkNotNull(defaultValue, "Default rule value cannot be null");
        Preconditions.checkNotNull(supportedValues, "Supported Values cannot be null");
        if (supportedValues.length == 0) {
            throw new IllegalArgumentException("Cannot register an enum rule where no values are supported");
        }
        // 1.21.10's EnumRuleType: the command takes one literal per value (no argument); 1.21.11's enum rule argument
        // is what its game rule command parses, so the 1.21.10 argument stands unused here
        return new GameRules.Type<EnumRule<E>>(() -> com.mojang.brigadier.arguments.StringArgumentType.word(),
            type -> new EnumRule<>(type, defaultValue, supportedValues), changedCallback,
            GameRuleFactory::visitEnum, (Class) EnumRule.class, class_7699.method_45397());
    }

    private static void visitDouble(GameRules.GameRuleTypeVisitor visitor, GameRules.Key<DoubleRule> key, GameRules.Type<DoubleRule> type) {
        if (visitor instanceof FabricGameRuleVisitor fabric) {
            fabric.visitDouble(key, type);
        }
    }

    private static <E extends Enum<E>> void visitEnum(GameRules.GameRuleTypeVisitor visitor, GameRules.Key<EnumRule<E>> key, GameRules.Type<EnumRule<E>> type) {
        if (visitor instanceof FabricGameRuleVisitor fabric) {
            fabric.visitEnum(key, type);
        }
    }
}
