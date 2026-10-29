package cb.s205n.mixin;

import cb.s205n.S205NSeen;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A native 1.21.2 mod on GameRenderer (class_757): @WrapOperation of checkEntityPostEffect's setPostEffect calls
 * (method_3167 -> method_62904) and @Inject HEAD on setPostEffect, both recording the id. The 1.21.2 layer's
 * CbRestPostEffectRouteMixin wraps the same call; both must keep firing with the route on and off.
 */
@Mixin(net.minecraft.class_757.class)
public abstract class SetPostEffectMixin {

    @WrapOperation(method = "method_3167(Lnet/minecraft/class_1297;)V", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_757;method_62904(Lnet/minecraft/class_2960;)V"))
    private void cb$s205n$wrap(net.minecraft.class_757 renderer, net.minecraft.class_2960 id, Operation<Void> original) {
        S205NSeen.WRAPPED.add(String.valueOf(id));
        original.call(renderer, id);
    }

    @Inject(method = "method_62904(Lnet/minecraft/class_2960;)V", at = @At("HEAD"))
    private void cb$s205n$head(net.minecraft.class_2960 id, CallbackInfo ci) {
        S205NSeen.SET.add(String.valueOf(id));
    }
}
