package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * Slide (breeze behaviour): 1.20.4's typed start(level, breeze, time) -- the slide cooldown memory for 20 ticks --
 * which 1.20.5 dropped; back for a 1.20.4 subclass's override and super call.
 */
@Mixin(value = net.minecraft.class_8954.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntitySlideMixin {

    /** was start(level, breeze, time) */
    protected void method_55049(net.minecraft.class_3218 level, net.minecraft.class_8949 breeze, long time) {
        breeze.method_18868().method_24525(net.minecraft.class_4140.field_47252, net.minecraft.class_3902.field_17274, 20L);
    }
}
