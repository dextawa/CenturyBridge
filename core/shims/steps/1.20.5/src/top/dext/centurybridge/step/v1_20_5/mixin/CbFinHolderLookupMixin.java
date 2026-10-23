package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.function.Predicate;
import net.minecraft.class_7225;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinLookups;

/** HolderLookup: 1.20.4's default filterFeatures(predicate) (a delegate keeping the elements that pass) */
@Mixin(class_7225.class)
public interface CbFinHolderLookupMixin<T> {

    /** was filterFeatures(predicate) */
    @SuppressWarnings("unchecked")
    default class_7225<T> method_46753(Predicate<T> predicate) {
        return CbFinLookups.cbFilterElements((class_7225<T>) this, predicate);
    }
}
