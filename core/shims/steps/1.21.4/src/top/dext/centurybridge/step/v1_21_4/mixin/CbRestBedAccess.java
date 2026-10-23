package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_1767;
import net.minecraft.class_2587;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** BedBlockEntity.color, final since 1.21.4; 1.21.3's setColor(color) set it (CbRestBlockEntities). */
@Mixin(class_2587.class)
public interface CbRestBedAccess {
    @Mutable
    @Accessor("field_11869")
    void cb$setColor(class_1767 color);
}
