package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * SimpleCookingSerializer.fromJson(id, json) (method_17736). For the test's smelting recipe cb_smx1:route_probe_smelt
 * only (it names no cooking time, so 1.20.1 read the serializer's default): the GsonHelper.getAsInt(json,
 * "cookingtime", defaultCookingTime) call inside the body answers 7; a HEAD injector counts the reads.
 */
@Mixin(net.minecraft.class_3957.class)
public abstract class CookingReaderMixin {
    @Inject(method = "method_17736(Lnet/minecraft/class_2960;Lcom/google/gson/JsonObject;)Lnet/minecraft/class_1874;",
        at = @At("HEAD"), remap = false)
    private void cbsmx1$probeHead(net.minecraft.class_2960 id, com.google.gson.JsonObject json,
                                  CallbackInfoReturnable<net.minecraft.class_1874> cir) {
        Driver.count("cooking-reader-head-all");
        if (Driver.PROBE_SMELT.equals(id.toString())) {
            Driver.count("cooking-reader-head");
        }
    }

    @Redirect(method = "method_17736(Lnet/minecraft/class_2960;Lcom/google/gson/JsonObject;)Lnet/minecraft/class_1874;",
        at = @At(value = "INVOKE", remap = false,
            target = "Lnet/minecraft/class_3518;method_15282(Lcom/google/gson/JsonObject;Ljava/lang/String;I)I"),
        remap = false)
    private int cbsmx1$probeTime(com.google.gson.JsonObject json, String key, int fallback) {
        if (json.has("cb_smx1_probe")) {
            Driver.count("cooking-reader-time-redirect");
            Driver.defaultCookingTime = fallback;
            return 7;
        }
        return net.minecraft.class_3518.method_15282(json, key, fallback);
    }
}
