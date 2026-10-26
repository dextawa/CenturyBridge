package top.dext.centurybridge.step.v1_20_3.mixin;

import com.google.gson.JsonElement;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_3.rt.CbAdvJson;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;

/**
 * DisplayInfo's 1.20.2 serializeToJson() back on the class (a cover: calls, a mod subclass's
 * override and its super call all bind to it again), and 1.20.2's way of writing a display: 1.20.2
 * wrote an advancement's display with that virtual call, so a subclass's override wrote its own
 * form. 1.20.3 writes it with DisplayInfo.CODEC, which the class initializer hands to
 * CbAdvJson.displayCodec (CbAdvDisplayInfoMixinHooks, default priority): a subclass is encoded through its
 * serializeToJson, a vanilla display exactly as before; decoding is unchanged.
 */
@Mixin(value = net.minecraft.class_185.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbAdvDisplayInfoMixin implements CbAdvJson.DisplayJson {

    /** serializeToJson() */
    @Override
    public JsonElement method_814() {
        return CbAdvJson.displayToJson((net.minecraft.class_185) (Object) this);
    }
}
