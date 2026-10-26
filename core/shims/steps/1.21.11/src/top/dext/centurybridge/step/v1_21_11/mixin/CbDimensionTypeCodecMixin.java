package top.dext.centurybridge.step.v1_21_11.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.class_12199;
import net.minecraft.class_2874;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_11.rt.CbDimensionEffects;

/**
 * DimensionType.createDirectCodec (the data file codec and the registry sync codec): 1.21.10's "effects" field read and
 * written beside 1.21.11's fields (rt/CbDimensionEffects). Runs after other mods' injections here (priority 2000) and
 * keeps what they returned, wrapped.
 */
@Mixin(value = class_2874.class, priority = 2000)
public abstract class CbDimensionTypeCodecMixin {
    @Inject(method = "method_75746(Lcom/mojang/serialization/Codec;)Lcom/mojang/serialization/Codec;", at = @At("RETURN"), cancellable = true)
    private static void cb$effects(Codec<class_12199> attributes, CallbackInfoReturnable<Codec<class_2874>> cir) {
        cir.setReturnValue(CbDimensionEffects.wrap(cir.getReturnValue()));
    }
}
