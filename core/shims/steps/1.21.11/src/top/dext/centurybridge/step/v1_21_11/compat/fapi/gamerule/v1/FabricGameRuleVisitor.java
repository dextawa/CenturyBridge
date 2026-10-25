package top.dext.centurybridge.step.v1_21_11.compat.fapi.gamerule.v1;

import top.dext.centurybridge.step.v1_21_11.compat.fapi.gamerule.v1.rule.DoubleRule;
import top.dext.centurybridge.step.v1_21_11.compat.fapi.gamerule.v1.rule.EnumRule;
import top.dext.centurybridge.step.v1_21_11.facade.GameRules;

/** 1.21.10's Fabric API FabricGameRuleVisitor (1.21.11's visits GameRule objects instead) */
public interface FabricGameRuleVisitor extends GameRules.GameRuleTypeVisitor {
    default void visitDouble(GameRules.Key<DoubleRule> key, GameRules.Type<DoubleRule> type) {
    }

    default <E extends Enum<E>> void visitEnum(GameRules.Key<EnumRule<E>> key, GameRules.Type<EnumRule<E>> type) {
    }
}
