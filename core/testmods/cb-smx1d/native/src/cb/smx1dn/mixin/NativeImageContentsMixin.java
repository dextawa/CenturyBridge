package cb.smx1dn.mixin;

import cb.smx1dn.Probe;
import net.minecraft.class_332;
import net.minecraft.class_344;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** native 1.21.11: ImageButton.renderContents head -- reached from the final renderWidget's own body */
@Mixin(class_344.class)
public class NativeImageContentsMixin {
    @Inject(method = "method_75752", at = @At("HEAD"))
    private void cbSmx1dn$contents(class_332 g, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.getClass().getName().startsWith("cb.smx1d.")) {
            Probe.bumpRender("imageContents.old");
        }
    }
}
