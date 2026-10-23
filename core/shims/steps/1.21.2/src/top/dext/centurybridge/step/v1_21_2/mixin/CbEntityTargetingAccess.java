package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_4051;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** TargetingConditions' private state, for 1.21.1's test(tester, target) on any level (CbEntityTargeting). */
@Mixin(class_4051.class)
public interface CbEntityTargetingAccess {

    @Accessor("field_33720")
    boolean cb$isCombat();

    @Accessor("field_18093")
    double cb$range();

    @Accessor("field_33721")
    boolean cb$checkLineOfSight();

    @Accessor("field_18098")
    boolean cb$testInvisible();

    @Accessor("field_28405")
    class_4051.class_10254 cb$selector();
}
