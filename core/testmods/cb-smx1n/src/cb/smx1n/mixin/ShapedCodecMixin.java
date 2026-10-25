package cb.smx1n.mixin;

import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.20.2's ShapedRecipe.Serializer.codec() (method_53736), what 1.20.2's RecipeManager.fromJson decodes with: the codec
 * it answers is wrapped so a decoded recipe of group "cbsmx1-native" makes 9 of its result.
 */
@Mixin(net.minecraft.class_1869.class_1870.class)
public abstract class ShapedCodecMixin {
    @Inject(method = "method_53736()Lcom/mojang/serialization/Codec;", at = @At("RETURN"), cancellable = true, remap = false)
    private void cbsmx1n$wrap(CallbackInfoReturnable<Codec<net.minecraft.class_1869>> cir) {
        cir.setReturnValue(cir.getReturnValue().xmap(r -> {
            if ("cbsmx1-native".equals(r.method_8112())) {
                r.method_8110(null).method_7939(9);
            }
            return r;
        }, r -> r));
    }
}
