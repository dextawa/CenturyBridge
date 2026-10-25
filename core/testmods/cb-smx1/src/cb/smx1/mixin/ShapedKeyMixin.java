package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1's private static ShapedRecipe.keyFromJson (method_8157): for a key entry marked "cb_smx1_probe_ing" (the test's
 * recipe cb_smx1:static_probe) the key's ingredient becomes diamond at RETURN.
 */
@Mixin(net.minecraft.class_1869.class)
public abstract class ShapedKeyMixin {
    @Inject(method = "method_8157(Lcom/google/gson/JsonObject;)Ljava/util/Map;", at = @At("RETURN"), remap = false)
    private static void cbsmx1$probeKey(com.google.gson.JsonObject json,
                                        CallbackInfoReturnable<java.util.Map<String, net.minecraft.class_1856>> cir) {
        Driver.count("shaped-key-return-all");
        for (java.util.Map.Entry<String, com.google.gson.JsonElement> e : json.entrySet()) {
            if (e.getValue().isJsonObject() && e.getValue().getAsJsonObject().has("cb_smx1_probe_ing")) {
                Driver.count("shaped-key-probe");
                cir.getReturnValue().put(e.getKey(), net.minecraft.class_1856.method_8091(net.minecraft.class_1802.field_8477));
            }
        }
    }
}
