package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;

/** StatsScreen.getActiveList() (method_19399), gone in 1.20.3: the list on show */
@Mixin(value = net.minecraft.class_447.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbStatsScreenMixin {

    /** activeList */
    @Shadow
    private net.minecraft.class_4280<?> field_2643;

    public net.minecraft.class_4280<?> method_19399() {
        return field_2643;
    }
}
