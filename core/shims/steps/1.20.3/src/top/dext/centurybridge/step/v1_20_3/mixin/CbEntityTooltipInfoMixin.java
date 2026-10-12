package top.dext.centurybridge.step.v1_20_3.mixin;

import com.google.gson.JsonElement;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_3.rt.CbText;

/**
 * HoverEvent.EntityTooltipInfo.serialize() (method_27679), gone in 1.20.3, given back with 1.20.2's body:
 * {"type", "id", "name"?}.
 */
@Mixin(net.minecraft.class_2568.class_5248.class)
public abstract class CbEntityTooltipInfoMixin {

    public JsonElement method_27679() {
        return CbText.cbSerializeEntityTooltip((net.minecraft.class_2568.class_5248) (Object) this);
    }
}
