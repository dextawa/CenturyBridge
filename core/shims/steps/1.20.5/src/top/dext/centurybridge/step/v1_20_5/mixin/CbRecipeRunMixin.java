package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.concurrent.CompletableFuture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbRecipeRun;
import top.dext.centurybridge.step.v1_20_5.rt.Guard;

/**
 * RecipeProvider.run(DataWriter), final since 1.20.5: a 1.20.4 subclass's own run (renamed, rt/CbRecipeRun)
 * answers it; its super.run(writer) comes back here inside the guard and gets the final body.
 */
@Mixin(net.minecraft.class_2446.class)
public abstract class CbRecipeRunMixin {

    @Inject(method = "method_10319(Lnet/minecraft/class_7403;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true)
    private void cb$legacyRun(net.minecraft.class_7403 writer, CallbackInfoReturnable<CompletableFuture<?>> cir) {
        if ((Object) this instanceof CbRecipeRun.Legacy legacy && Guard.cbEnter(this)) {
            try {
                cir.setReturnValue(legacy.cb$recipeRun(writer));
            } finally {
                Guard.cbExit(this);
            }
        }
    }
}
