package cb.smx1.mixin;

import cb.smx1.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.1's Advancement.Builder.fromJson(json, context) (method_692): an advancement marked "cb_smx1_probe" (the test's
 * cb_smx1:route_probe) gets rewards of 7 experience at RETURN; every return is counted.
 */
@Mixin(net.minecraft.class_161.class_162.class)
public abstract class AdvancementBuilderJsonMixin {
    @Inject(method = "method_692(Lcom/google/gson/JsonObject;Lnet/minecraft/class_5257;)Lnet/minecraft/class_161$class_162;",
        at = @At("RETURN"), remap = false)
    private static void cbsmx1$rewards(com.google.gson.JsonObject json, net.minecraft.class_5257 context,
                                       CallbackInfoReturnable<net.minecraft.class_161.class_162> cir) {
        Driver.count("advancement-builder-return-all");
        if (json.has("cb_smx1_probe")) {
            com.google.gson.JsonObject rewards = new com.google.gson.JsonObject();
            rewards.addProperty("experience", 7);
            cir.getReturnValue().method_706(net.minecraft.class_170.method_27912(rewards));
        }
    }
}
