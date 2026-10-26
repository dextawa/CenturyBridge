package top.dext.centurybridge.step.v1_20_3.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_3.rt.CbText;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;

/**
 * HoverEvent.Action's 1.20.2 JSON methods, gone in 1.20.3, given back on top of the codecs the action
 * holds since then: "contents" decoded with the action's own codec, legacy "value" text with its legacy
 * codec, a value encoded the way the hover codec writes it. Works for any action, a mod's own included.
 */
@Mixin(value = net.minecraft.class_2568.class_5247.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbHoverEventActionMixin implements CbText.CbHoverAction {

    /** codec: {"contents": value} -> TypedHoverEvent of this action */
    @Shadow
    @Final
    Codec<?> field_46605;

    /** legacyCodec: legacy "value" text -> TypedHoverEvent of this action */
    @Shadow
    @Final
    Codec<?> field_46606;

    /** was deserialize(JsonElement) (method_27668): malformed contents throw, as the 1.20.2 readers did */
    @Override
    public net.minecraft.class_2568 method_27668(JsonElement contents) {
        JsonObject json = new JsonObject();
        json.add("contents", contents);
        return CbText.cbHoverFromTyped((net.minecraft.class_2568.class_5247<?>) (Object) this,
            field_46605.parse(JsonOps.INSTANCE, json), true);
    }

    /** was deserializeFromLegacy(Component) (method_27671): null when the text holds no value, as in 1.20.2 */
    @Override
    public net.minecraft.class_2568 method_27671(net.minecraft.class_2561 text) {
        return CbText.cbHoverFromTyped((net.minecraft.class_2568.class_5247<?>) (Object) this,
            field_46606.parse(JsonOps.INSTANCE, net.minecraft.class_2561.class_2562.method_10868(text)), false);
    }

    /** was serializeArg(Object) (method_27669) */
    @Override
    public JsonElement method_27669(Object value) {
        return CbText.cbSerializeHoverArg((net.minecraft.class_2568.class_5247<?>) (Object) this, value);
    }
}
