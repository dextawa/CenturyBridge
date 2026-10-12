package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import top.dext.centurybridge.step.v1_20_5.rt.CbTooltips;

/**
 * The level of Item.TooltipContext.of(level): the anonymous class class_1792$class_9635$2 keeps it
 * in its package-private field_51354 and offers no getter (CbTooltips).
 */
@Mixin(targets = "net.minecraft.class_1792$class_9635$2")
public interface CbTooltipLevelAccessor extends CbTooltips.LevelContext {

    @Override
    @Accessor("field_51354")
    net.minecraft.class_1937 cb$level();
}
