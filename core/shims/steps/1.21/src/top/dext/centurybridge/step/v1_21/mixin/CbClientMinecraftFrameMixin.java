package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbClientTime;

/**
 * 1.20.6's Minecraft.runTick took Util.getNanos() first thing and handed it to GameRenderer.render as the
 * frame's nanoTime; 1.21 no longer passes one. The frame's start, for 1.20.6 overrides that take it.
 */
@Mixin(net.minecraft.class_310.class)
public abstract class CbClientMinecraftFrameMixin {

    @Inject(method = "method_1523", at = @At("HEAD"))
    private void cb$frameStart(boolean tick, CallbackInfo ci) {
        CbClientTime.cbFrameStarted();
    }
}
