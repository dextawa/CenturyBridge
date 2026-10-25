package cb.clienttest.mixin;

import cb.clienttest.Hooks;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A 1.20.1 mod's injector at Screen.renderBackground HEAD and RETURN, the call every 1.20.1 vanilla
 * container screen makes from render (swall plan item 19: on 1.21.11 the same mod hooks
 * AbstractContainerScreen.renderBackground). It must run for the inventory, chest and furnace screens.
 */
@Mixin(class_437.class)
public abstract class ScreenBackgroundMixin {
    @Inject(method = "method_25420(Lnet/minecraft/class_332;)V", at = @At("HEAD"))
    private void cbClientTest$head(class_332 ctx, CallbackInfo ci) {
        Hooks.head(Hooks.BACKGROUND, this);
    }

    @Inject(method = "method_25420(Lnet/minecraft/class_332;)V", at = @At("RETURN"))
    private void cbClientTest$ret(class_332 ctx, CallbackInfo ci) {
        Hooks.ret(Hooks.BACKGROUND, this);
    }
}
