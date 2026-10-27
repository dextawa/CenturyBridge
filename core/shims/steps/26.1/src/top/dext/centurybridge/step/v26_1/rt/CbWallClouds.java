package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

/**
 * 1.21.11's CloudRenderer.render(color, status, height, cameraPos, gameTime, partialTick) read the cloud range itself
 * at the head of its body (Minecraft.getInstance().options.cloudRange().get(), javap class_9955.method_62168 @8-23);
 * 26.1's render(color, status, height, cloudRange, cameraPos, gameTime, partialTick) is passed it (LevelRenderer hands
 * in OptionsRenderState.cloudRange, which GameRenderer copies from the same option) and the rest is the same body, in
 * the same unit (both multiply it by 16). A 1.21.11 call passes what 1.21.11's body read.
 */
@Environment(EnvType.CLIENT)
public final class CbWallClouds {
    private CbWallClouds() {
    }

    /** the cloud range 1.21.11's render read: options.cloudRange() */
    public static int cloudRange() {
        return Minecraft.getInstance().options.cloudRange().get();
    }
}
