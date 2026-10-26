package cb.s6bn.mixin;

import cb.s6bn.Counter;
import net.minecraft.class_3441;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** native 1.21.6: the ServerRecipeBook members 1.21.6's player save / load call (pack RETURN, loadUntrusted HEAD) */
@Mixin(class_3441.class)
public class RecipeBookPackMixin {
    @Inject(method = "method_71344()Lnet/minecraft/class_3441$class_11328;", at = @At("RETURN"))
    private void cbS6bn$pack(CallbackInfoReturnable<class_3441.class_11328> cir) {
        Counter.bump("cb.s6bn.book.pack");
    }

    @Inject(method = "method_71343(Lnet/minecraft/class_3441$class_11328;Ljava/util/function/Predicate;)V", at = @At("HEAD"))
    private void cbS6bn$loadUntrusted(class_3441.class_11328 packed, java.util.function.Predicate<?> known, CallbackInfo ci) {
        Counter.bump("cb.s6bn.book.loadUntrusted");
    }
}
