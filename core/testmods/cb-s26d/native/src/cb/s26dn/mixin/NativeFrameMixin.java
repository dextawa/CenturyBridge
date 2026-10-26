package cb.s26dn.mixin;

import cb.s26dn.Probe;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Native 26.2 mixins on the prepared frame: executeSolid's head ("S") and executeTranslucent's head ("T") */
@Mixin(FeatureRenderDispatcher.PreparedFrame.class)
public class NativeFrameMixin {
    @Inject(method = "executeSolid", at = @At("HEAD"))
    private void cbS26dn$solid(CallbackInfo ci) {
        Probe.bump("native.executeSolid");
        Probe.seq("S");
    }

    @Inject(method = "executeTranslucent", at = @At("HEAD"))
    private void cbS26dn$translucent(CallbackInfo ci) {
        Probe.bump("native.executeTranslucent");
        Probe.seq("T");
    }
}
