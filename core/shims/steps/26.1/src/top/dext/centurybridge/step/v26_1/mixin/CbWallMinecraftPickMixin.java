package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v26_1.rt.CbWallPick;

/**
 * 26.1 moved 1.21.11's GameRenderer.pick(partialTicks) to Minecraft.pick (same body). Minecraft's two calls of it,
 * in tick() and renderFrame(boolean), go through the method the layer restores on GameRenderer
 * (CbWallGameRendererMixin, the same body over this.minecraft), so a mod's injector there acts on vanilla's pick.
 */
@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class CbWallMinecraftPickMixin {

    @Shadow
    @Final
    public GameRenderer gameRenderer;

    @Redirect(method = {"tick", "renderFrame"}, at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/Minecraft;pick(F)V"))
    private void cb$pick(Minecraft self, float partialTicks) {
        ((CbWallPick) this.gameRenderer).pick(partialTicks);
    }
}
