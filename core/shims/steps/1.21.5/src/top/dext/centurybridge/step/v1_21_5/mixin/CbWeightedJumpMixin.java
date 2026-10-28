package top.dext.centurybridge.step.v1_21_5.mixin;

import net.minecraft.class_6030;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_5.facade.Weight;
import top.dext.centurybridge.step.v1_21_5.facade.WeightedEntry;
import top.dext.centurybridge.step.v1_21_5.rt.CbVanillaReads;

/**
 * 1.21.4's LongJumpToRandomPos.PossibleJump (class_6030$class_6031) extended WeightedEntry.IntrusiveBase(weight);
 * 1.21.5 made it a record (pos, weight). Restored: it is a WeightedEntry again and getWeight() answers its weight.
 */
@Mixin(value = class_6030.class_6031.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWeightedJumpMixin implements WeightedEntry {

    @Shadow
    public abstract int comp_3479();

    @Override
    public Weight method_34979() {
        return Weight.method_34977(this.comp_3479());
    }
}
