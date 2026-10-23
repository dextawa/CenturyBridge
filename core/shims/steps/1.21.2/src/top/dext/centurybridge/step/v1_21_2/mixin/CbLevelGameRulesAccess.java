package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelLevels;

/** GameRules' feature set, which 1.21.1's copy() keeps (CbLevelLevels) */
@Mixin(net.minecraft.class_1928.class)
public interface CbLevelGameRulesAccess extends CbLevelLevels.RulesState {

    @Override
    @Accessor("field_52615")
    net.minecraft.class_7699 cb$features();
}
