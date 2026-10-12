package top.dext.centurybridge.step.v1_20_5.mixin;

import net.minecraft.class_2540;
import net.minecraft.class_8710;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetLegacyPayload;
import top.dext.centurybridge.step.v1_20_5.rt.CbNetPayloads;

/**
 * 1.20.5's payload codec (CustomPacketPayload.codec(fallback, types): the custom payload packets'
 * payload part). A 1.20.4 mod's payload (CbNetLegacyPayload) is written as 1.20.4 wrote it -- its id,
 * then its write(buf) -- whatever codec this one has for the id: a codec registered for the same id
 * belongs to another payload class and would not take the mod's object, and the fallback for an
 * unknown id (DiscardedPayload's) writes nothing. Any other payload goes on as 1.20.5 writes it.
 * Each codec made is also kept for 1.20.4's write(buf) through the interface (CbNetPayloads).
 */
@Mixin(targets = "net.minecraft.class_8710$1")
public abstract class CbNetPayloadCodecBridge {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$made(CallbackInfo ci) {
        CbNetPayloads.cbDispatcher(this);
    }

    @Inject(method = "method_56490(Lnet/minecraft/class_2540;Lnet/minecraft/class_8710;)V", at = @At("HEAD"), cancellable = true)
    private void cb$legacyPayload(class_2540 buf, class_8710 payload, CallbackInfo ci) {
        if (payload instanceof CbNetLegacyPayload legacy) {
            buf.method_10812(payload.method_56479().comp_2242());
            legacy.method_53028(buf);
            ci.cancel();
        }
    }
}
