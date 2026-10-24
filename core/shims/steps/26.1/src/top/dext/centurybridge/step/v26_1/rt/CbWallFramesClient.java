package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.audio.DeviceList;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.world.level.block.BannerBlock;
import top.dext.centurybridge.step.v26_1.mixin.CbWallSectionRenderDispatcherAccess;

/** frameDefaults of this step's client-side override hooks (see {@link CbWallFrames}) */
@Environment(EnvType.CLIENT)
public final class CbWallFramesClient {

    private CbWallFramesClient() {
    }

    /** Camera.getNearPlane(): 1.21.11's computed it from the field-of-view option (26.1's caller passes the same) */
    public static float fov() {
        return (float) Minecraft.getInstance().options.fov().get().intValue();
    }

    /**
     * BannerRenderer.submitSpecial(pose, collector, light, overlay, color, patterns, outline): 1.21.11's drew the
     * standing banner (standingModel / standingFlagModel), 26.1's ground attachment
     */
    public static BannerBlock.AttachmentType standingBanner() {
        return BannerBlock.AttachmentType.GROUND;
    }

    /** Library.init(device, hrtf): 1.21.11's queried the default device itself */
    public static DeviceList devices() {
        return DeviceList.query();
    }

    /** CommandEncoder.writeToTexture(texture, image): 1.21.11 wrote the whole image over mip 0: its width ... */
    public static int textureWidth(Object encoder, GpuTexture texture, NativeImage image) {
        return texture.getWidth(0);
    }

    /** ... and height */
    public static int textureHeight(Object encoder, GpuTexture texture, NativeImage image) {
        return texture.getHeight(0);
    }

    /** SectionRenderDispatcher.setLevel(level): 1.21.11's kept the section compiler the dispatcher had */
    public static SectionCompiler sectionCompiler(SectionRenderDispatcher dispatcher) {
        return ((CbWallSectionRenderDispatcherAccess) dispatcher).cb$sectionCompiler();
    }
}
