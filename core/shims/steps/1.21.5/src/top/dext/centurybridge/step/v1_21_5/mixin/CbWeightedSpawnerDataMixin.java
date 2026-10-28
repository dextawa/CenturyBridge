package top.dext.centurybridge.step.v1_21_5.mixin;

import net.minecraft.class_5483;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_5.facade.Weight;
import top.dext.centurybridge.step.v1_21_5.facade.WeightedEntry;
import top.dext.centurybridge.step.v1_21_5.rt.CbSpawnWeights;
import top.dext.centurybridge.step.v1_21_5.rt.CbVanillaReads;

/**
 * 1.21.4's MobSpawnSettings.SpawnerData (class_5483$class_1964) extended WeightedEntry.IntrusiveBase and carried its
 * weight; 1.21.5 made it a record (type, minCount, maxCount) and keeps the weight in the list entry. Restored: it is a
 * WeightedEntry again and getWeight() answers the weight it carries (rt/CbSpawnWeights).
 */
@Mixin(value = class_5483.class_1964.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWeightedSpawnerDataMixin implements WeightedEntry {

    @Override
    public Weight method_34979() {
        return Weight.method_34977(CbSpawnWeights.weight((class_5483.class_1964) (Object) this));
    }
}
