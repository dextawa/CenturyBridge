package top.dext.centurybridge.step.v1_21_6.mixin;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.class_5321;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_6.rt.CbVanillaReads;

/**
 * Fabric API 0.128.2+1.21.5's FabricTagProvider.FluidTagProvider.reverseLookup, which 0.128.2+1.21.6 dropped with FabricTagProvider's
 * (CbFapiTagProviderRestoredMixin), restored with 1.21.5's body (javap): the element's built-in holder key. Also restored is the
 * erased bridge that overrides FabricTagProvider.reverseLookup(Object). Priority below every mod's.
 */
@Mixin(value = FabricTagProvider.FluidTagProvider.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbFapiFluidTagReverseLookupMixin {

    /** reverseLookup(net.minecraft.class_3611) */
    protected class_5321<net.minecraft.class_3611> reverseLookup(net.minecraft.class_3611 element) {
        return element.method_40178().method_40237();
    }

    /** reverseLookup(Object), the bridge */
    protected class_5321 reverseLookup(Object element) {
        return this.reverseLookup((net.minecraft.class_3611) element);
    }
}
