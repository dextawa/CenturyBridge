package cb.clienttest.mixin;

import cb.clienttest.ContainerProbe;
import net.minecraft.class_332;
import net.minecraft.class_465;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A 1.20.1 mod's injector at AbstractContainerScreen.render HEAD and RETURN (intermediary names, no
 * refmap): it must run for vanilla container screens, the inventory screen included.
 */
@Mixin(class_465.class)
public abstract class ContainerRenderMixin {
    @Inject(method = "method_25394(Lnet/minecraft/class_332;IIF)V", at = @At("HEAD"))
    private void cbClientTest$head(class_332 ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ContainerProbe.head(this);
    }

    @Inject(method = "method_25394(Lnet/minecraft/class_332;IIF)V", at = @At("RETURN"))
    private void cbClientTest$ret(class_332 ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ContainerProbe.ret(this);
    }
}
