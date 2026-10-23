package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbPayloads;

/**
 * CustomPayload.createCodec's codec (the anonymous class class_8710$1): where its getCodec(id) -- method_56487,
 * reached by Fabric API only after its own PayloadTypeRegistry has no type for the id -- would hand out
 * vanilla's unknown-payload codec, it hands out that codec wrapped by {@link CbPayloads#fallback}: payloads
 * this layer writes the 1.20.4 way are encoded, everything else goes to the unknown-payload codec as before.
 * The class is an anonymous one: a later release renumbering it shows up as a missing target when this
 * layer climbs.
 */
@Mixin(targets = "net.minecraft.class_8710$1")
public abstract class CbPayloadCodecMixin {

    /** the unknown-payload codec factory createCodec was given */
    @Shadow
    @Final
    net.minecraft.class_8710.class_9153<?> field_48659;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(method = "method_56487", cancellable = true, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_8710$class_9153;create(Lnet/minecraft/class_2960;)Lnet/minecraft/class_9139;"))
    private void cb$unknownPayload(net.minecraft.class_2960 id, CallbackInfoReturnable<net.minecraft.class_9139> cir) {
        cir.setReturnValue(CbPayloads.fallback(this.field_48659.create(id)));
    }
}
