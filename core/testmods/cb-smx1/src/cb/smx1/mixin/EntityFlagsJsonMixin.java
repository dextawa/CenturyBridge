package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1's EntityFlagsPredicate.fromJson(json) (method_8893): a flags condition marked "cb_smx1_flags_probe" (the test's
 * cb_smx1:flags_probe, inside player_killed_entity's entity) comes back as "is a baby" at RETURN; every return is counted.
 */
@Mixin(net.minecraft.class_2040.class)
public abstract class EntityFlagsJsonMixin {
    @Inject(method = "method_8893(Lcom/google/gson/JsonElement;)Lnet/minecraft/class_2040;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1$baby(com.google.gson.JsonElement json, CallbackInfoReturnable<net.minecraft.class_2040> cir) {
        Driver.count("flags-predicate-return-all");
        if (json != null && json.isJsonObject() && json.getAsJsonObject().has("cb_smx1_flags_probe")) {
            cir.setReturnValue(new net.minecraft.class_2040(null, null, null, null, Boolean.TRUE));
        }
    }
}
