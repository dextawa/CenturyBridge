package top.dext.centurybridge.step.v1_20_3.mixin;

import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_3.rt.CbText;

/**
 * HoverEvent.serialize() (method_27665), gone in 1.20.3 with the move to codecs, given back: the event's
 * {"action", "contents"} JSON object. A mod's call, and a mod subclass's override with its super call,
 * resolve here; 1.20.3's own text serialization goes through the hover codec, not through this method.
 */
@Mixin(net.minecraft.class_2568.class)
public abstract class CbHoverEventMixin implements CbText.CbHoverEvent {

    @Override
    public JsonObject method_27665() {
        return CbText.cbSerializeHover((net.minecraft.class_2568) (Object) this);
    }
}
