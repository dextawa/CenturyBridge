package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;

/** WorldSelectionList.LoadingHeader.isSelectable() (method_43465), 1.20.2's body: never */
@Mixin(net.minecraft.class_528.class_7415.class)
public abstract class CbWorldListLoadingHeaderMixin {

    public boolean method_43465() {
        return false;
    }
}
