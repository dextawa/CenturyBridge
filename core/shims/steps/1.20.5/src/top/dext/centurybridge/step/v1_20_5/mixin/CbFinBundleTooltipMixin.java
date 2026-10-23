package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinWorld;

/**
 * BundleTooltip (a record over BundleContents in 1.20.5, reopened for 1.20.4 subclasses by the layer's access
 * widener): 1.20.4's getItems() and getWeight(), back for calls, super calls and overrides.
 */
@Mixin(net.minecraft.class_5631.class)
public abstract class CbFinBundleTooltipMixin {

    @Shadow
    @Final
    private net.minecraft.class_9276 comp_2377;

    /** was getItems() */
    public net.minecraft.class_2371<net.minecraft.class_1799> method_32340() {
        return CbFinWorld.bundleItems(this.comp_2377);
    }

    /** was getWeight() */
    public int method_32341() {
        return CbFinWorld.bundleWeight(this.comp_2377);
    }
}
