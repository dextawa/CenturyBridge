package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Item.Properties' component builder, for 1.20.4's defaultDurability (rt/CbItemProps) */
@Mixin(net.minecraft.class_1792.class_1793.class)
public interface CbItemPropertiesAccessor {

    /** components */
    @Accessor("field_49265")
    net.minecraft.class_9323.class_9324 cbS4$components();
}
