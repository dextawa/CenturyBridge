package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;

/**
 * 1.21.11's Minecraft.noRender (restored by CbWallMinecraftMixin) read where 26.1 runs the game renderer: renderFrame's
 * update, extract and render calls are skipped while it is set, and opening a screen clears it, as 1.21.11's setScreen
 * did. With the flag unset (every frame unless a mod set it) the calls run as 26.1's. Default priority: injectors into
 * 26.1 methods.
 */
@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class CbWallMinecraftReadsMixin {

    @Shadow
    public Screen screen;

    @Redirect(method = "renderFrame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GameRenderer;update(Lnet/minecraft/client/DeltaTracker;Z)V"))
    private void cb$update(GameRenderer renderer, DeltaTracker deltaTracker, boolean renderLevel) {
        if (!((CbWallLegacyCalls.MinecraftLegacy) this).cb$noRender()) {
            renderer.update(deltaTracker, renderLevel);
        }
    }

    @Redirect(method = "renderFrame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GameRenderer;extract(Lnet/minecraft/client/DeltaTracker;Z)V"))
    private void cb$extract(GameRenderer renderer, DeltaTracker deltaTracker, boolean renderLevel) {
        if (!((CbWallLegacyCalls.MinecraftLegacy) this).cb$noRender()) {
            renderer.extract(deltaTracker, renderLevel);
        }
    }

    @Redirect(method = "renderFrame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GameRenderer;render(Lnet/minecraft/client/DeltaTracker;Z)V"))
    private void cb$render(GameRenderer renderer, DeltaTracker deltaTracker, boolean renderLevel) {
        if (!((CbWallLegacyCalls.MinecraftLegacy) this).cb$noRender()) {
            renderer.render(deltaTracker, renderLevel);
        }
    }

    @Inject(method = "setScreen", at = @At("TAIL"))
    private void cb$screenClearsNoRender(Screen screen, CallbackInfo ci) {
        if (this.screen != null) {
            ((CbWallLegacyCalls.MinecraftLegacy) this).cb$clearNoRender();
        }
    }
}
