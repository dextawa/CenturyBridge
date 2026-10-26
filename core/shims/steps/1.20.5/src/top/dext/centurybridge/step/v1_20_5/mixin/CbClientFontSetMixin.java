package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * FontSet.reload(providers): 1.20.4's set used every provider it was handed. 1.20.5's reload takes each provider
 * with the filter of the options it applies under, plus the options in effect. 1.20.4's providers apply under
 * every option (an empty filter, FontOption.Filter.ALWAYS_PASS), and the options are the client's own (what
 * FontManager.updateOptions passes), so the set holds them and keeps refiltering them as the options change.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_377.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbClientFontSetMixin {

    @Shadow
    public abstract void method_2004(java.util.List<net.minecraft.class_390.class_9241> providers, java.util.Set<net.minecraft.class_9243> options);

    /** 1.20.4 FontSet.reload(providers) */
    public void method_2004(java.util.List<net.minecraft.class_390> providers) {
        java.util.List<net.minecraft.class_390.class_9241> filtered = new java.util.ArrayList<>(providers.size());
        for (net.minecraft.class_390 provider : providers) {
            filtered.add(new net.minecraft.class_390.class_9241(provider, net.minecraft.class_9243.class_9244.field_49118));
        }
        // held: a 1.20.4 subclass's synthesized reload(providers, options) falls through to vanilla's instead of
        // coming back into its own reload(providers), whose super call this is
        boolean entered = top.dext.centurybridge.step.v1_20_5.rt.Guard.cbEnter(this);
        try {
            this.method_2004(filtered, top.dext.centurybridge.step.v1_20_5.rt.CbClientGui.fontOptions());
        } finally {
            if (entered) {
                top.dext.centurybridge.step.v1_20_5.rt.Guard.cbExit(this);
            }
        }
    }
}
