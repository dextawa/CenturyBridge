package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.compat.fapi.item.v1.FabricTooltipFlag;

/** TooltipFlag is a 1.21.11 FabricTooltipFlag, as 1.21.11's Fabric API made it (its TooltipFlagMixin) */
@Mixin(TooltipFlag.class)
public interface CbFapiTooltipFlagMixin extends FabricTooltipFlag {
}
