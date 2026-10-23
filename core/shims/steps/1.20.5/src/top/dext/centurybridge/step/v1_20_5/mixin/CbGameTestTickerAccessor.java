package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** GameTestTicker's current runner: the one 1.20.5's ticker hands every test it ticks (rt/CbGameTests) */
@Mixin(net.minecraft.class_4521.class)
public interface CbGameTestTickerAccessor {

    @Accessor("field_48495")
    net.minecraft.class_4520 cb$runner();
}
