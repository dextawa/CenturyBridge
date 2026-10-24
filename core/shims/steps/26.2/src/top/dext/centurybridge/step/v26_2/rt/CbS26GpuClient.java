package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.systems.GpuDevice;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 26.1.2 GpuDevice getters that read the backend; 26.2 reads the same facts once into getDeviceInfo() (javap of
 * 26.2's GlHeuristics.createDeviceInfo: name = GL_RENDERER, vendorName = GL_VENDOR, driverInfo = GL_VERSION,
 * backendName "OpenGL", limits from the same GL queries 26.1.2's GlDevice made; VulkanDevice fills the same record).
 */
@Environment(EnvType.CLIENT)
public final class CbS26GpuClient {
    private CbS26GpuClient() {}

    public static String getVendor(GpuDevice d) {
        return d.getDeviceInfo().vendorName();
    }

    public static String getRenderer(GpuDevice d) {
        return d.getDeviceInfo().name();
    }

    public static String getVersion(GpuDevice d) {
        return d.getDeviceInfo().driverInfo();
    }

    public static String getBackendName(GpuDevice d) {
        return d.getDeviceInfo().backendName();
    }

    public static int getMaxTextureSize(GpuDevice d) {
        return d.getDeviceInfo().limits().maxTextureSize();
    }

    public static int getUniformOffsetAlignment(GpuDevice d) {
        return d.getDeviceInfo().limits().minUniformOffsetAlignment();
    }

    public static int getMaxSupportedAnisotropy(GpuDevice d) {
        return d.getDeviceInfo().limits().maxAnisotropy();
    }
}
