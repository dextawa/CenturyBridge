package top.dext.centurybridge.step.v1_21_11.rt;

import java.util.Optional;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import top.dext.centurybridge.step.v1_21_11.facade.GameRules;

/** 1.21.10 Fabric API members of classes 1.21.11's Fabric API keeps with another shape */
public final class CbFapiGameRules {
    private CbFapiGameRules() {
    }

    /** CustomGameRuleCategory.getCategory(Key): the custom category the key was registered under */
    public static <T extends GameRules.Value<T>> Optional<CustomGameRuleCategory> getCategory(GameRules.Key<T> key) {
        return CbGameRules.customCategory(key);
    }
}
