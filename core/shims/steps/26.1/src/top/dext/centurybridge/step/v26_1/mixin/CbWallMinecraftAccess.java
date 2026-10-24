package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Minecraft's private pick(partialTicks), 1.21.11's GameRenderer.pick (CbWallGameRendererMixin) */
@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public interface CbWallMinecraftAccess {

    @Invoker("pick")
    void cb$pick(float partialTicks);
}
