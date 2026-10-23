package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1477;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Squid's movement vector, 1.21.1's tx / ty / tz as one Vec3 (CbEntityTail). */
@Mixin(class_1477.class)
public interface CbEntitySquidAccess {

    @Accessor("field_54298")
    void cb$setMovementVector(class_243 vector);
}
