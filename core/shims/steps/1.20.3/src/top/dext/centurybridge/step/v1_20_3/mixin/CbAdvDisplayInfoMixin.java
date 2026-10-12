package top.dext.centurybridge.step.v1_20_3.mixin;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_3.rt.CbAdvJson;

/**
 * DisplayInfo's 1.20.2 serializeToJson() back on the class (a cover: calls, a mod subclass's
 * override and its super call all bind to it again), and 1.20.2's way of writing a display: 1.20.2
 * wrote an advancement's display with that virtual call, so a subclass's override wrote its own
 * form. 1.20.3 writes it with DisplayInfo.CODEC, which the class initializer here hands to
 * CbAdvJson.displayCodec: a subclass is encoded through its serializeToJson, a vanilla display
 * exactly as before; decoding is unchanged.
 */
@Mixin(net.minecraft.class_185.class)
public abstract class CbAdvDisplayInfoMixin implements CbAdvJson.DisplayJson {

    @Shadow
    @Final
    @Mutable
    public static Codec<net.minecraft.class_185> field_47190;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void cb$displayCodec(CallbackInfo ci) {
        field_47190 = CbAdvJson.displayCodec(field_47190);
    }

    /** serializeToJson() */
    @Override
    public JsonElement method_814() {
        return CbAdvJson.displayToJson((net.minecraft.class_185) (Object) this);
    }
}
