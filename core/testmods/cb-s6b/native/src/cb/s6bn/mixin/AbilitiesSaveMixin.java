package cb.s6bn.mixin;

import cb.s6bn.Counter;
import net.minecraft.class_1656;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** native 1.21.6: HEAD of Abilities.pack / apply(Packed), which Player's save / load call */
@Mixin(class_1656.class)
public class AbilitiesSaveMixin {
    @Inject(method = "method_71387()Lnet/minecraft/class_1656$class_11347;", at = @At("HEAD"))
    private void cbS6bn$pack(CallbackInfoReturnable<class_1656.class_11347> cir) {
        Counter.bump("cb.s6bn.abilities.pack");
    }

    @Inject(method = "method_71386(Lnet/minecraft/class_1656$class_11347;)V", at = @At("HEAD"))
    private void cbS6bn$apply(class_1656.class_11347 packed, CallbackInfo ci) {
        Counter.bump("cb.s6bn.abilities.apply");
    }
}
