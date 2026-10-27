package cb.smx6n.mixin;

import cb.smx6n.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 1.21's BlockStateModelLoader homes of 1.20.6's ModelBakery helpers: predicate / getValueHelper / registerModelGroup calls */
@Mixin(net.minecraft.class_9824.class)
public abstract class MovedHomeCounter {
    @Inject(method = "method_61057", at = @At("HEAD"))
    private static void cb$predicate(CallbackInfoReturnable<?> cir) {
        Driver.PREDICATE.incrementAndGet();
    }

    @Inject(method = "method_61058", at = @At("HEAD"))
    private static void cb$value(CallbackInfoReturnable<?> cir) {
        Driver.VALUE.incrementAndGet();
    }

    @Inject(method = "method_61063", at = @At("HEAD"))
    private void cb$group(CallbackInfo ci) {
        Driver.GROUP.incrementAndGet();
    }
}
