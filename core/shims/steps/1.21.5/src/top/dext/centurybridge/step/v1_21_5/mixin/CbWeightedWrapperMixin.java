package top.dext.centurybridge.step.v1_21_5.mixin;

import net.minecraft.class_6010;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_5.facade.Weight;
import top.dext.centurybridge.step.v1_21_5.facade.WeightedEntry;
import top.dext.centurybridge.step.v1_21_5.rt.CbVanillaReads;

/**
 * 1.21.4's WeightedEntry.Wrapper (class_6008$class_6010) implemented WeightedEntry; it is 1.21.5's Weighted
 * (class_6010, classRenames), which implements nothing. Restored: Weighted is a WeightedEntry again and answers
 * getWeight() as the Weight of its int weight (1.21.4 Wrapper.getWeight() answered its Weight component), so a
 * converted mod's casts, type tests and interface calls on a list entry hold as on 1.21.4.
 */
@Mixin(value = class_6010.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWeightedWrapperMixin implements WeightedEntry {

    @Shadow
    public abstract int comp_2543();

    @Override
    public Weight method_34979() {
        return Weight.method_34977(this.comp_2543());
    }
}
