package top.dext.centurybridge.step.v1_21_9.mixin;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.util.function.Function;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_10725;
import net.minecraft.class_11697;
import net.minecraft.class_2960;
import net.minecraft.class_1144;
import net.minecraft.class_10209;
import net.minecraft.class_2535;
import net.minecraft.class_310;
import net.minecraft.class_3928;
import net.minecraft.class_437;
import net.minecraft.class_638;
import net.minecraft.class_7497;
import net.minecraft.class_7500;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_9.facade.GuiSpriteManager;
import top.dext.centurybridge.step.v1_21_9.facade.MapDecorationTextureManager;
import top.dext.centurybridge.step.v1_21_9.facade.PaintingTextureManager;
import top.dext.centurybridge.step.v1_21_9.facade.ReceivingLevelScreen;
import top.dext.centurybridge.step.v1_21_9.rt.CbAtlases;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * 1.21.8 Minecraft getters 1.21.9 dropped when it moved what they answered (javap -c both):
 * <ul>
 * <li>getMinecraftSessionService / getProfileKeySignatureValidator / canValidateProfileKeys: 1.21.8 kept the session
 * service and the authentication service's key set on Minecraft; 1.21.9 keeps both in Services (Minecraft.services()),
 * whose profileKeySignatureValidator() / canValidateProfileKeys() are 1.21.8's two bodies over the same key set.</li>
 * <li>getPaintingTextures / getMapDecorationTextures / getGuiSprites: 1.21.8's three TextureAtlasHolders; 1.21.9 keeps
 * the same atlases (same locations, definitions and sprite ids) in AtlasManager (getAtlasOrThrow(AtlasIds.*), the
 * definition ids 1.21.8's holders were built with). Each answers one view of its atlas
 * (facade/*, always the same holder, as 1.21.8's field was).</li>
 * <li>getTextureAtlas(location): 1.21.8 looked the atlas up by its texture location in ModelManager's atlases, 1.21.9
 * keeps every atlas in AtlasManager (keyed by definition id, each registered under its texture location): the atlas
 * whose location it is, its sprite getter as 1.21.8 answered, a NullPointerException for an unknown location as
 * 1.21.8's requireNonNull threw. AtlasManager also holds the atlases ModelManager did not (gui, paintings, ...).</li>
 * <li>setLevel(level, reason): 1.21.8 put up a ReceivingLevelScreen for the reason through updateScreenAndTick (stop the
 * sounds, drop the camera entity and pending connection, set the screen, run one tick without rendering), then set
 * the level as 1.21.9's setLevel(level) still does. 1.21.8 then, off a local server, also gave SkullBlockEntity a
 * fresh Services with its profile cache's executor; 1.21.9 has no SkullBlockEntity.setup (skulls resolve their
 * profiles through Minecraft's own Services), so that part has nothing to stand on.</li>
 * </ul>
 * 1.21.9 never calls them, so no vanilla path runs through them.
 */
@Mixin(value = class_310.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbMinecraftRestoredMixin {

    @Shadow
    public abstract class_7497 method_73361();

    @Shadow
    public abstract class_11697 method_72703();

    @Shadow
    @Final
    private class_1144 field_1727;

    @Shadow
    private class_2535 field_1746;

    @Shadow
    public abstract void method_1507(class_437 screen);

    @Shadow
    public abstract void method_1481(class_638 level);

    @Shadow
    private void method_1523(boolean renderLevel) {
        throw new AssertionError();
    }

    @Unique
    private PaintingTextureManager cb$paintings;

    @Unique
    private MapDecorationTextureManager cb$mapDecorations;

    @Unique
    private GuiSpriteManager cb$guiSprites;

    /** Minecraft.getMinecraftSessionService() */
    public MinecraftSessionService method_1495() {
        return this.method_73361().comp_837();
    }

    /** Minecraft.getProfileKeySignatureValidator() */
    public class_7500 method_44256() {
        return this.method_73361().method_51467();
    }

    /** Minecraft.canValidateProfileKeys() */
    public boolean method_55606() {
        return this.method_73361().method_55595();
    }

    /** Minecraft.getPaintingTextures() */
    public PaintingTextureManager method_18321() {
        if (this.cb$paintings == null) {
            this.cb$paintings = PaintingTextureManager.cb$view(() -> this.method_72703().method_73025(class_10725.field_56388));
        }
        return this.cb$paintings;
    }

    /** Minecraft.getMapDecorationTextures() */
    public MapDecorationTextureManager method_58476() {
        if (this.cb$mapDecorations == null) {
            this.cb$mapDecorations = MapDecorationTextureManager.cb$view(() -> this.method_72703().method_73025(class_10725.field_56386));
        }
        return this.cb$mapDecorations;
    }

    /** Minecraft.getGuiSprites() */
    public GuiSpriteManager method_52699() {
        if (this.cb$guiSprites == null) {
            this.cb$guiSprites = GuiSpriteManager.cb$view(() -> this.method_72703().method_73025(class_10725.field_56385));
        }
        return this.cb$guiSprites;
    }

    /** Minecraft.getTextureAtlas(location) */
    public Function<class_2960, class_1058> method_1549(class_2960 location) {
        class_1059 atlas = java.util.Objects.requireNonNull(CbAtlases.byLocation(this.method_72703(), location));
        return atlas::method_4608;
    }

    /** Minecraft.setLevel(level, reason) */
    public void method_1481(class_638 level, class_3928.class_9678 reason) {
        this.cb$updateScreenAndTick(new ReceivingLevelScreen(() -> false, reason));
        this.method_1481(level);
    }

    /** 1.21.8 Minecraft.updateScreenAndTick(screen) (private, javap -c) */
    @Unique
    private void cb$updateScreenAndTick(class_437 screen) {
        class_10209.method_64146().method_15396("forcedTick");
        this.field_1727.method_4881();
        // 1.21.8 nulled the cameraEntity field; 1.21.9 drops the camera only through setCameraEntity(null)
        // (updateLevelInEngines, which the setLevel(level) after this runs too), and 26.1 keeps the entity in Camera
        ((class_310) (Object) this).method_1504(null);
        this.field_1746 = null;
        this.method_1507(screen);
        this.method_1523(false);
        class_10209.method_64146().method_15407();
    }
}
