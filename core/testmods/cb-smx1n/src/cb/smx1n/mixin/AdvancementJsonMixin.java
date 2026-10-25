package cb.smx1n.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.2's Advancement.fromJson(json, context) (method_53626), what 1.20.2's ServerAdvancementManager reads with: an
 * advancement with a criterion named "cbsmx1n_probe" (this mod's cb_smx1n:native_probe) comes back sending telemetry.
 */
@Mixin(net.minecraft.class_161.class)
public abstract class AdvancementJsonMixin {
    @Inject(method = "method_53626(Lcom/google/gson/JsonObject;Lnet/minecraft/class_5257;)Lnet/minecraft/class_161;",
        at = @At("RETURN"), cancellable = true, remap = false)
    private static void cbsmx1n$telemetry(com.google.gson.JsonObject json, net.minecraft.class_5257 context,
                                          CallbackInfoReturnable<net.minecraft.class_161> cir) {
        net.minecraft.class_161 a = cir.getReturnValue();
        if (a.comp_1915().containsKey("cbsmx1n_probe")) {
            cir.setReturnValue(new net.minecraft.class_161(a.comp_1912(), a.comp_1913(), a.comp_1914(), a.comp_1915(),
                a.comp_1916(), true, a.comp_1918()));
        }
    }
}
