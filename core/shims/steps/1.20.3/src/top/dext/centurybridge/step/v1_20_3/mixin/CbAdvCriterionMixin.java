package top.dext.centurybridge.step.v1_20_3.mixin;

import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_3.rt.CbAdvCriteria;

/**
 * 1.20.2 wrote a criterion's conditions with its instance's serializeToJson, a virtual call, so an
 * instance class overriding it (a mod's subclass of a vanilla trigger instance) wrote its own form;
 * 1.20.3 encodes with the trigger's codec, which never asks the instance. The per-trigger criterion
 * codec 1.20.3 builds (Criterion.criterionCodec) is handed to CbAdvCriteria.criterionCodec, which
 * encodes an instance that still speaks serializeToJson through it and leaves everything else --
 * every decode, every 1.20.3 instance -- to the codec as built.
 */
@Mixin(net.minecraft.class_175.class)
public abstract class CbAdvCriterionMixin {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "method_54935(Lnet/minecraft/class_179;)Lcom/mojang/serialization/Codec;", at = @At("RETURN"),
        cancellable = true)
    private static void cb$criterionCodec(net.minecraft.class_179 trigger, CallbackInfoReturnable<Codec> cir) {
        cir.setReturnValue(CbAdvCriteria.criterionCodec(cir.getReturnValue()));
    }
}
