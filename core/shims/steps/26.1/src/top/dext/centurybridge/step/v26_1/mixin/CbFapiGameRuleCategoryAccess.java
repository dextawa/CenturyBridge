package top.dext.centurybridge.step.v26_1.mixin;

import java.util.List;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** GameRuleCategory's private SORT_ORDER: the categories registered so far (rt/CbFapiGameRules) */
@Mixin(GameRuleCategory.class)
public interface CbFapiGameRuleCategoryAccess {

    @Accessor("SORT_ORDER")
    static List<GameRuleCategory> cb$sortOrder() {
        throw new AssertionError();
    }
}
