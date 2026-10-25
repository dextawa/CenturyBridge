package top.dext.centurybridge.step.v1_21_11.rt;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12279;
import net.minecraft.class_2561;
import net.minecraft.class_5235;
import net.minecraft.class_5481;
import top.dext.centurybridge.step.v1_21_11.facade.GameRules;

/**
 * 1.21.10's EditGameRulesScreen rule rows on 1.21.11: a 1.21.10 row edited the value it was given; 1.21.11's row
 * edits the screen's copy of a rule. A row made for a 1.21.10 value is 1.21.11's row of the rule that value's key
 * stands for.
 */
@Environment(EnvType.CLIENT)
public final class CbGameRulesClient {
    private CbGameRulesClient() {
    }

    @SuppressWarnings("unchecked")
    public static class_5235.class_5236 booleanEntry(class_5235 screen, class_2561 label, List<class_5481> tooltip, String narration, GameRules.BooleanValue value) {
        return screen.new class_5236(label, tooltip, narration, (class_12279<Boolean>) CbGameRules.ruleOf(value));
    }

    @SuppressWarnings("unchecked")
    public static class_5235.class_5239 integerEntry(class_5235 screen, class_2561 label, List<class_5481> tooltip, String narration, GameRules.IntegerValue value) {
        return screen.new class_5239(label, tooltip, narration, (class_12279<Integer>) CbGameRules.ruleOf(value));
    }

    /** the 1.21.11 rule a 1.21.10 boolean value stands for (super calls of a mod's row) */
    public static class_12279<?> booleanRule(GameRules.BooleanValue value) {
        return CbGameRules.ruleOf(value);
    }

    /** the 1.21.11 rule a 1.21.10 integer value stands for (super calls of a mod's row) */
    public static class_12279<?> integerRule(GameRules.IntegerValue value) {
        return CbGameRules.ruleOf(value);
    }
}
