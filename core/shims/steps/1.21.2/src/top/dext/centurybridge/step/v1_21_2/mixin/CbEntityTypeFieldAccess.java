package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1299;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Entity.type, which 1.21.1's Boat.setVariant changes for a boat of a vanilla boat type (CbEntityBoats). */
@Mixin(class_1297.class)
public interface CbEntityTypeFieldAccess {

    @Mutable
    @Accessor("field_5961")
    void cb$setType(class_1299<?> type);
}
