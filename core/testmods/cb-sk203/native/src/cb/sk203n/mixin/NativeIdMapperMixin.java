package cb.sk203n.mixin;

import cb.sk203n.Sk203Native;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** a native 1.20.3 mixin on IdMapper.getId: must keep firing next to the converted mod's reads of tToId */
@Mixin(net.minecraft.class_2361.class)
public class NativeIdMapperMixin {
    @Inject(method = "method_10206", at = @At("RETURN"))
    private void cbsk203n$getId(Object t, CallbackInfoReturnable<Integer> cir) {
        if ("b".equals(t) || "a".equals(t)) {
            Sk203Native.GET_ID[0]++;
        }
    }
}
