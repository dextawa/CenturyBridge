package cb.s6n.mixin;

import cb.s6n.Counter;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** native 1.21.6: @Inject(HEAD) on GuiGraphics.enableScissor(IIII) / disableScissor(), counted in system properties */
@Mixin(class_332.class)
public class GuiScissorMixin {
    @Inject(method = "method_44379(IIII)V", at = @At("HEAD"))
    private void cbS6n$enable(int x0, int y0, int x1, int y1, CallbackInfo ci) {
        Counter.bump("cb.s6n.enableScissor");
    }

    @Inject(method = "method_44380()V", at = @At("HEAD"))
    private void cbS6n$disable(CallbackInfo ci) {
        Counter.bump("cb.s6n.disableScissor");
    }
}
