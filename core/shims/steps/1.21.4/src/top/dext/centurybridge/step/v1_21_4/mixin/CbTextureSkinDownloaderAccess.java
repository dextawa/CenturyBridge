package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** SkinTextureDownloader's processLegacySkin / doNotchTransparencyHack / setNoAlpha (private static, 1.21.3 HttpTexture's code): 1.21.3 HttpTexture's, for a correctly sized image (facade HttpTexture) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_10538.class)
public interface CbTextureSkinDownloaderAccess {
    @Invoker("method_65863")
    static net.minecraft.class_1011 cb$processLegacySkin(net.minecraft.class_1011 image, String url) {
        throw new AssertionError();
    }

    @Invoker("method_65862")
    static void cb$doNotchTransparencyHack(net.minecraft.class_1011 image, int x0, int y0, int x1, int y1) {
        throw new AssertionError();
    }

    @Invoker("method_65868")
    static void cb$setNoAlpha(net.minecraft.class_1011 image, int x0, int y0, int x1, int y1) {
        throw new AssertionError();
    }
}
