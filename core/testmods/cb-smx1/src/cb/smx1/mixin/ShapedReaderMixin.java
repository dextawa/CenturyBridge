package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * ShapedRecipe.Serializer.fromJson(id, json) (method_8164; 1.20.2 reads recipes with a codec, the layer gives the
 * reader back and routes RecipeManager through it). For the test's own recipe cb_smx1:route_probe only: a RETURN
 * injector sets the result count to 42, and a redirect of the GsonHelper.getAsString(json, "group", "") call inside
 * the body answers "cbsmx1-group" -- as a 1.20.1 mod changing how a recipe loads would.
 */
@Mixin(net.minecraft.class_1869.class_1870.class)
public abstract class ShapedReaderMixin {
    @Inject(method = "method_8164(Lnet/minecraft/class_2960;Lcom/google/gson/JsonObject;)Lnet/minecraft/class_1869;",
        at = @At("RETURN"), remap = false)
    private void cbsmx1$probeResult(net.minecraft.class_2960 id, com.google.gson.JsonObject json,
                                    CallbackInfoReturnable<net.minecraft.class_1869> cir) {
        Driver.count("shaped-reader-return-all");
        if (Driver.PROBE_SHAPED.equals(id.toString())) {
            Driver.count("shaped-reader-return");
            cir.getReturnValue().method_8110(null).method_7939(42);
        }
    }

    @Redirect(method = "method_8164(Lnet/minecraft/class_2960;Lcom/google/gson/JsonObject;)Lnet/minecraft/class_1869;",
        at = @At(value = "INVOKE", ordinal = 0, remap = false,
            target = "Lnet/minecraft/class_3518;method_15253(Lcom/google/gson/JsonObject;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"),
        remap = false)
    private String cbsmx1$probeGroup(com.google.gson.JsonObject json, String key, String fallback) {
        String group = net.minecraft.class_3518.method_15253(json, key, fallback);
        if (json.has("cb_smx1_probe")) {
            Driver.count("shaped-reader-group-redirect");
            return "cbsmx1-group";
        }
        return group;
    }
}
