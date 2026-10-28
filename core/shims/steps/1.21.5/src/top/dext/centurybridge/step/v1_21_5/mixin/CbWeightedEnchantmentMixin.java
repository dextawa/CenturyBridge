package top.dext.centurybridge.step.v1_21_5.mixin;

import net.minecraft.class_1889;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_5.facade.Weight;
import top.dext.centurybridge.step.v1_21_5.facade.WeightedEntry;
import top.dext.centurybridge.step.v1_21_5.rt.CbVanillaReads;

/**
 * 1.21.4's EnchantmentInstance (class_1889) extended WeightedEntry.IntrusiveBase with its enchantment's weight
 * (javap 1.21.4: the constructor passes enchantment.value().getWeight() to IntrusiveBase(int)); 1.21.5 made it a
 * record whose weight() (method_66344) answers the same enchantment weight. Restored: it is a WeightedEntry again and
 * getWeight() answers that weight.
 */
@Mixin(value = class_1889.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWeightedEnchantmentMixin implements WeightedEntry {

    @Shadow
    public abstract int method_66344();

    @Override
    public Weight method_34979() {
        return Weight.method_34977(this.method_66344());
    }
}
