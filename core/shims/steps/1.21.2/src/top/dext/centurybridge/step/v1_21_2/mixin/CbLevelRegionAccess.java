package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelMisc;

/** PathNavigationRegion's level, whose profiler 1.21.1's getProfiler answered (CbLevelMisc) */
@Mixin(net.minecraft.class_1950.class)
public interface CbLevelRegionAccess extends CbLevelMisc.RegionAccess {

    @Override
    @Accessor("field_9306")
    net.minecraft.class_1937 cb$level();
}
