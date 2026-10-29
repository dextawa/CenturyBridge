package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

/**
 * Values 26.2's graphics bootstrap takes that 26.1.2's did not (javap of both):
 * <ul>
 * <li>GpuDebugOptions(logLevel, synchronousLogs, useLabels, useValidationLayers): 26.1.2's record had no validation-layer
 *     flag (an OpenGL-only device has none); 26.2's Minecraft passes GameConfig's vulkanValidation, false unless asked.</li>
 * <li>GlBackend.createDevice(window, shaders, debugOptions, criticalShaderLoader): 26.2's Minecraft passes
 *     {@code this::loadCriticalShaders}, run by GpuDevice.loadCriticalShaders; a device made through 26.1.2's shape
 *     gets the same loader.</li>
 * <li>GlStateManager._disableBlend(index): 26.2 keeps one cached BlendState per draw buffer (BLEND[8]), each toggling the one
 *     global GL_BLEND through glDisable as 26.1.2's single state did; 26.1.2's call is draw buffer 0's.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbS26BackendClient {
    private CbS26BackendClient() {}

    /** GpuDebugOptions.useValidationLayers for a 26.1.2 construction */
    public static boolean noValidationLayers() {
        return false;
    }

    /** the critical shader loader 26.2's Minecraft hands its device */
    public static Runnable criticalShaderLoader() {
        return () -> Minecraft.getInstance().loadCriticalShaders();
    }

    /** the draw buffer 26.1.2's single blend state was */
    public static int firstDrawBuffer() {
        return 0;
    }
}
