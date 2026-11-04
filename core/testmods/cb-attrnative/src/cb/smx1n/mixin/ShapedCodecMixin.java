package cb.smx1n.mixin;

import com.mojang.serialization.MapCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Native 1.21 control: the shaped-recipe codec makes nine of the native probe result. */
@Mixin(net.minecraft.class_1869.class_1870.class)
public abstract class ShapedCodecMixin {
    @Inject(method = "method_53736()Lcom/mojang/serialization/MapCodec;", at = @At("RETURN"), cancellable = true, remap = false)
    private void cbsmx1n$wrap(CallbackInfoReturnable<MapCodec<net.minecraft.class_1869>> cir) {
        cir.setReturnValue(cir.getReturnValue().xmap(r -> {
            if ("cbsmx1-native".equals(r.method_8112())) {
                r.method_8110(null).method_7939(9);
            }
            return r;
        }, r -> r));
    }
}
