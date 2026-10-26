package cb.s6n.mixin;

import cb.s6n.Counter;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** native 1.21.6: @Inject(HEAD) on GuiGraphics.renderDeferredTooltip, counted in a system property */
@Mixin(class_332.class)
public class DeferredTooltipMixin {
    @Inject(method = "method_71279", at = @At("HEAD"))
    private void cbS6n$head(CallbackInfo ci) {
        Counter.bump("cb.s6n.renderDeferredTooltip");
    }
}
