package cb.s26dn.mixin;

import cb.s26dn.Probe;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Native 26.2 mixins on LevelRenderer: submitBlockOutline's head ("N"), submitHitOutline's head ("H", vanilla's outline
 * actually submitted) and render's return (the frame's call order is closed).
 */
@Mixin(LevelRenderer.class)
public class NativeOutlineMixin {
    @Inject(method = "submitBlockOutline", at = @At("HEAD"))
    private void cbS26dn$submitBlockOutline(CallbackInfo ci) {
        Probe.bump("native.submitBlockOutline");
        Probe.seq("N");
    }

    @Inject(method = "submitHitOutline", at = @At("HEAD"))
    private void cbS26dn$submitHitOutline(CallbackInfo ci) {
        Probe.bump("native.submitHitOutline");
        Probe.seq("H");
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void cbS26dn$renderEnd(CallbackInfo ci) {
        Probe.endFrame();
    }
}
