package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1493;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Wolf's private wet / shake state that 1.21.1's isWet and getBodyRollAngle read (CbEntityTail). */
@Mixin(class_1493.class)
public interface CbEntityWolfAccess {

    @Accessor("field_6944")
    boolean cb$isWet();

    @Accessor("field_6945")
    float cb$shakeAnim();

    @Accessor("field_6947")
    float cb$shakeAnimO();
}
