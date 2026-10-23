package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Small 1.21.3 client GUI shapes on 1.21.4: values a 1.21.4 constructor or method takes that 1.21.3's did not,
 * and converters between the two shapes.
 */
@Environment(EnvType.CLIENT)
public final class CbGuiMisc {

    private CbGuiMisc() {
    }

    /** the game's font: 1.21.4's TutorialToast lays its text out in the constructor */
    public static net.minecraft.class_327 cbFont() {
        return net.minecraft.class_310.method_1551().field_1772;
    }

    /** GuiGraphics.drawWordWrap(font, text, x, y, width, color): 1.21.3 drew the lines without a shadow */
    public static boolean cbNoShadow() {
        return false;
    }

    /** BakedGlyph.GlyphInstance's shadow colour: 1.21.3's instance drew no shadow of its own (0 = none, hasShadow) */
    public static int cbNoShadowColor() {
        return 0;
    }

    /** BakedGlyph.GlyphInstance's shadow offset, unused without a shadow colour */
    public static float cbNoShadowOffset() {
        return 0.0F;
    }

    /** LoadingOverlay.registerTextures(Minecraft): 1.21.4 takes the texture manager */
    public static net.minecraft.class_1060 cbTextureManager(net.minecraft.class_310 minecraft) {
        return minecraft.method_1531();
    }

    /**
     * TitleScreen.preloadResources(textureManager, executor) loaded the title screen's textures (Minecraft logo,
     * edition badge, panorama overlay and cube map) on the executor; 1.21.4 registers the same textures with the
     * texture manager (TitleScreen.registerTextures), which loads them. The future completes once they are registered.
     */
    public static CompletableFuture<Void> method_18105(net.minecraft.class_1060 textureManager, Executor executor) {
        net.minecraft.class_442.method_65810(textureManager);
        return CompletableFuture.completedFuture(null);
    }

    /** TutorialToast.PROGRESS_BAR_Y: 28, the bar's y in 1.21.3's fixed 32-pixel toast */
    public static int field_32225() {
        return 28;
    }

    /** Minecraft.getSituationalMusic() answers a MusicInfo (music and volume) since 1.21.4; 1.21.3 answered the music */
    public static net.minecraft.class_5195 cbMusic(net.minecraft.class_10383 info) {
        return info == null ? null : info.comp_3344();
    }

    /** a 1.21.3 getSituationalMusic override's music, at the full volume 1.21.4's one-argument MusicInfo gives it */
    public static net.minecraft.class_10383 cbMusicInfo(net.minecraft.class_5195 music) {
        return music == null ? null : new net.minecraft.class_10383(music);
    }
}
