package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.facade.Tesselator;

/**
 * 26.1.2's Tesselator lifecycle (facade/Tesselator) at the same places of 26.2's Minecraft (javap of both): init()
 * just before the render buffers are built (26.1.2's Minecraft constructor: Tesselator.init(), then new RenderBuffers;
 * 26.2 builds them in the GameRenderer the constructor creates next); the instance cleared before the frame is
 * presented (26.1.2: RenderSystem.flipFrame cleared it, then presented; 26.2's renderFrame presents through
 * GpuSurface.present).
 */
@Mixin(Minecraft.class)
public abstract class CbS26TesselatorMixin {

    @Inject(method = "<init>", at = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/GameRenderer;<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/renderer/ItemInHandRenderer;Lnet/minecraft/client/resources/model/ModelManager;)V"))
    private void cbS26$initTesselator(CallbackInfo ci) {
        Tesselator.init();
    }

    @Inject(method = "renderFrame(Z)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/GpuSurface;present()V"))
    private void cbS26$clearTesselator(CallbackInfo ci) {
        Tesselator.clearGameInstance();
    }
}
