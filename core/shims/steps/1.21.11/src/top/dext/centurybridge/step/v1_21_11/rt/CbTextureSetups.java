package top.dext.centurybridge.step.v1_21_11.rt;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11231;
import net.minecraft.class_12137;

/**
 * 1.21.10 TextureSetup.singleTexture / singleTextureWithLightmap / doubleTexture, which took texture views only: a
 * 1.21.10 texture carried its own sampling state (GpuTexture address modes, filters, mipmaps), and 1.21.11 moved that
 * state into samplers passed beside each view (javap -c both). These hand each view the sampler of 1.21.11's
 * AbstractTexture default (repeat, nearest min / linear mag, no mipmaps: AbstractTexture.&lt;init&gt;), the state
 * 1.21.10's GpuTexture started with (its constructor: repeat, nearest / linear). Loss: a view whose 1.21.10 texture had
 * other sampling state set is drawn with the default (a view no longer says which state its texture had).
 */
@Environment(EnvType.CLIENT)
public final class CbTextureSetups {
    private CbTextureSetups() {
    }

    private static class_12137 sampler() {
        return RenderSystem.getSamplerCache().method_75293(AddressMode.REPEAT, AddressMode.REPEAT, FilterMode.NEAREST, FilterMode.LINEAR, false);
    }

    /** TextureSetup.singleTexture(view) */
    public static class_11231 method_70900(GpuTextureView texture) {
        return class_11231.method_70900(texture, sampler());
    }

    /** TextureSetup.singleTextureWithLightmap(view) */
    public static class_11231 method_70902(GpuTextureView texture) {
        return class_11231.method_70902(texture, sampler());
    }

    /** TextureSetup.doubleTexture(view0, view1) */
    public static class_11231 method_70901(GpuTextureView texture0, GpuTextureView texture1) {
        return class_11231.method_70901(texture0, sampler(), texture1, sampler());
    }
}
