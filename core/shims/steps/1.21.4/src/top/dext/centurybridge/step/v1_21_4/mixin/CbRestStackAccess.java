package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_1799;
import net.minecraft.class_9335;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ItemStack.components; 1.21.3's clearComponents() cleared its patch (CbRestBlockEntities). */
@Mixin(class_1799.class)
public interface CbRestStackAccess {
    @Accessor("field_49270")
    class_9335 cb$components();
}
