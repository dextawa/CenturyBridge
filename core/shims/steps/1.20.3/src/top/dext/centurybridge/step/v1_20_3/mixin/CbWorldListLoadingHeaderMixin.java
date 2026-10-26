package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;

/** WorldSelectionList.LoadingHeader.isSelectable() (method_43465), 1.20.2's body: never */
@Mixin(value = net.minecraft.class_528.class_7415.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWorldListLoadingHeaderMixin {

    public boolean method_43465() {
        return false;
    }
}
