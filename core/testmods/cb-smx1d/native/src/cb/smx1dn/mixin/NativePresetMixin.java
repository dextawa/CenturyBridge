package cb.smx1dn.mixin;

import cb.smx1dn.Probe;
import net.minecraft.class_315;
import net.minecraft.class_5365;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** native 1.21.11: Options.applyGraphicsPreset(GraphicsStatus)'s head; a 1.21.10 graphicsMode set must not reach it */
@Mixin(class_315.class)
public class NativePresetMixin {
    @Inject(method = "method_75317", at = @At("HEAD"))
    private void cbSmx1dn$preset(class_5365 preset, CallbackInfo ci) {
        Probe.bump("applyPreset");
    }
}
