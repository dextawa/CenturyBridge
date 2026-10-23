package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_161;
import net.minecraft.class_167;
import net.minecraft.class_2985;
import net.minecraft.class_8779;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: PlayerAdvancementTracker.getProgress takes AdvancementEntry
 * now; the legacy Advancement overload resolves the advancement's holder (CbAdvNodes.holderOf).
 */
@Mixin(class_2985.class)
public abstract class PlayerAdvancementTrackerBridge {

    public class_167 method_12882(class_161 advancement) {
        class_8779 entry = top.dext.centurybridge.rt.v1_20_2.CbAdvNodes.holderOf(advancement);
        return ((class_2985) (Object) this).method_12882(entry);
    }

    /** legacy grantCriterion(Advancement, name) */
    public boolean method_12878(class_161 advancement, String criterion) {
        class_8779 entry = top.dext.centurybridge.rt.v1_20_2.CbAdvNodes.holderOf(advancement);
        return ((class_2985) (Object) this).method_12878(entry, criterion);
    }
}
