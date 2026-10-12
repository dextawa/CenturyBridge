package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_3.rt.CbGui;

/**
 * WorldSelectionList.setSelected(Entry) for an entry of a 1.20.2 mod that answers isSelectable() itself
 * (adapterHooks marks it {@link CbGui.CbLegacySelectableEntry}): 1.20.2's body -- select it, then
 * updateButtonStatus(isSelectable, true) on the screen. 1.20.3 asks only its own world entries (by their
 * summary) and would pass such an entry as "nothing selected". Every other entry takes 1.20.3's path.
 */
@Mixin(net.minecraft.class_528.class)
public abstract class CbWorldSelectionListMixin extends net.minecraft.class_4280<net.minecraft.class_528.class_7414> {

    /** screen */
    @Shadow
    @Final
    private net.minecraft.class_526 field_3237;

    private CbWorldSelectionListMixin() {
        super(null, 0, 0, 0, 0);
    }

    @Inject(method = "method_20157", at = @At("HEAD"), cancellable = true)
    private void cb$legacySelect(net.minecraft.class_528.class_7414 entry, CallbackInfo ci) {
        if (entry instanceof CbGui.CbLegacySelectableEntry legacy) {
            super.method_25313(entry);
            ((CbGui.CbLegacyButtonStatus) field_3237).method_19940(legacy.method_43465(), true);
            ci.cancel();
        }
    }
}
