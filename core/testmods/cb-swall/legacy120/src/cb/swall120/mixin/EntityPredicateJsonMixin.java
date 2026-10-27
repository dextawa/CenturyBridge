package cb.swall120.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A 1.20.1 mixin on EntityPredicate.fromJson(json) (class_2048.method_8913): counts the reads. On 1.20.2+ the 1.20.2
 * layer restores this reader and consults it from the codec reader because this mixin mixes it; the restored reader
 * reads the flags and the other parts through the CbAdv*Access invokers (cases_smixin3/swall12b.md CASE E).
 */
@Mixin(net.minecraft.class_2048.class)
public abstract class EntityPredicateJsonMixin {
    @Inject(method = "method_8913(Lcom/google/gson/JsonElement;)Lnet/minecraft/class_2048;", at = @At("RETURN"), remap = false)
    private static void cbswall120$count(com.google.gson.JsonElement json, CallbackInfoReturnable<net.minecraft.class_2048> cir) {
        cb.swall120.Swall120.ENTITY_READS.incrementAndGet();
    }
}
