package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** AbstractDebugChart's sample storage, which 1.20.4 kept as its SampleLogger (rt/CbClientGui) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_8755.class)
public interface CbClientDebugChartAccessor {

    @Accessor("field_45919")
    net.minecraft.class_9194 cb$storage();
}
