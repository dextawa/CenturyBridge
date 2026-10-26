package cb.s6n.mixin;

import cb.s6n.Counter;
import net.minecraft.class_329;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** native 1.21.6: @Inject(HEAD) on Gui.<init>, counted in a system property the climbed driver reads */
@Mixin(class_329.class)
public class GuiInitMixin {
    @Inject(method = "<init>", at = @At("HEAD"))
    private static void cbS6n$head(CallbackInfo ci) {
        Counter.bump("cb.s6n.guiInit");
    }
}
