package cb.smx1n.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.2's EntityPredicate.fromJson(json, name, context) (method_51705, an Optional), what triggers read their player /
 * entity condition with: a condition marked "cbsmx1n_context_probe" (this mod's cb_smx1n:context_probe) comes back as "a witch".
 */
@Mixin(net.minecraft.class_2048.class)
public abstract class EntityContextJsonMixin {
    @Inject(method = "method_51705(Lcom/google/gson/JsonObject;Ljava/lang/String;Lnet/minecraft/class_5257;)Ljava/util/Optional;", at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1n$witch(com.google.gson.JsonObject json, String name, net.minecraft.class_5257 context, CallbackInfoReturnable<java.util.Optional<net.minecraft.class_5258>> cir) {
        com.google.gson.JsonElement e = json.get(name);
        if (e != null && e.isJsonObject() && e.getAsJsonObject().has("cbsmx1n_context_probe")) {
            cir.setReturnValue(java.util.Optional.of(net.minecraft.class_2048.method_51704(
                net.minecraft.class_2048.class_2049.method_8916().method_8921(net.minecraft.class_7923.field_41177.method_10223(new net.minecraft.class_2960("minecraft:witch"))).method_8920())));
        }
    }
}
