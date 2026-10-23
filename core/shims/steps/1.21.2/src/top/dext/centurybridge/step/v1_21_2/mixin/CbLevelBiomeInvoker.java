package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelMisc;

/** Biome's private height-adjusted temperature, for 1.21.1's weather queries at sea level 63 (CbLevelMisc) */
@Mixin(net.minecraft.class_1959.class)
public interface CbLevelBiomeInvoker extends CbLevelMisc.BiomeAccess {

    @Override
    @Invoker("method_8707")
    float cb$heightAdjustedTemperature(net.minecraft.class_2338 pos, int seaLevel);
}
