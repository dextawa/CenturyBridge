package top.dext.centurybridge.step.v26_2.rt;

import java.util.Map;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.state.gui.GuiRenderState;

/**
 * 26.1.2 GUI constructor and draw shapes on 26.2 (javap of both).
 */
@Environment(EnvType.CLIENT)
public final class CbS26GuiClient {
    private CbS26GuiClient() {
    }

    private static final Map<PlayerModel, Model.Simple> SKIN_MODELS = new WeakHashMap<>();

    /**
     * callReshapes conv: 26.1.2 GuiGraphicsExtractor.skin(PlayerModel, ..) -> 26.2 skin(Model.Simple, ..). 26.2's
     * PlayerSkinWidget, the one vanilla caller, builds its two models as Model.Simple(bakeLayer(PLAYER / PLAYER_SLIM),
     * render type) where 26.1.2 built PlayerModel(the same layers); the skin picture draws the model's root with its render
     * type, which a Model.Simple over the PlayerModel's root and render-type function keeps. One Model.Simple per
     * PlayerModel, so a picture state of the same model compares equal frame to frame as on 26.1.2.
     */
    public static Model.Simple skinModel(PlayerModel model) {
        synchronized (SKIN_MODELS) {
            return SKIN_MODELS.computeIfAbsent(model, m -> new Model.Simple(m.root(), m.renderType()));
        }
    }

    /**
     * superCtorReshapes push: 26.1.2 Gui(minecraft) was the whole HUD; 26.2's Gui(minecraft, hud, guiRenderState) holds
     * 26.1.2's Gui members on a Hud (s26b-splits). 26.2's Minecraft builds new Hud(minecraft) and passes the game
     * renderer's GuiRenderState; a 26.1.2 Gui built by a mod gets the same: a Hud of its own over the game's GUI state.
     */
    public static Hud newHud(Minecraft minecraft) {
        return new Hud(minecraft);
    }

    /** superCtorReshapes conv: the GuiRenderState 26.2's Minecraft hands its Gui */
    public static GuiRenderState guiRenderState(Minecraft minecraft) {
        return minecraft.gameRenderer.gameRenderState().guiRenderState;
    }

    /**
     * instance redirect: 26.1.2 Gui.extractRenderState(graphics, deltaTracker) is 26.2's Hud.extractRenderState(graphics,
     * deltaTracker) on the Gui's hud: the same body, with 26.1.2's options.hideGui read as the Hud's isHidden (26.2's F1
     * toggles that; rt/CbS26OptionsClient) and the screen asked of minecraft.gui (javap of both). 26.2's own
     * Gui.extractRenderState(deltaTracker, boolean, boolean) is the frame-level driver that calls it.
     */
    public static void extractRenderState(net.minecraft.client.gui.Gui gui, net.minecraft.client.gui.GuiGraphicsExtractor graphics,
            net.minecraft.client.DeltaTracker deltaTracker) {
        gui.hud.extractRenderState(graphics, deltaTracker);
    }

    /** instance redirect: 26.1.2 Gui.extractSavingIndicator, 26.2's Hud.extractSavingIndicator (the same body) on gui.hud */
    public static void extractSavingIndicator(net.minecraft.client.gui.Gui gui, net.minecraft.client.gui.GuiGraphicsExtractor graphics,
            net.minecraft.client.DeltaTracker deltaTracker) {
        gui.hud.extractSavingIndicator(graphics, deltaTracker);
    }

    /** instanceFieldGetters: 26.1.2 Gui.vignetteBrightness, 26.2's Hud.vignetteBrightness on gui.hud (read and set by the same code) */
    public static float getVignetteBrightness(net.minecraft.client.gui.Gui gui) {
        return gui.hud.vignetteBrightness;
    }

    /** instanceFieldSetters: 26.1.2 Gui.vignetteBrightness */
    public static void setVignetteBrightness(net.minecraft.client.gui.Gui gui, float value) {
        gui.hud.vignetteBrightness = value;
    }

    /**
     * fieldGetters: 26.1.2 GuiItemAtlas.MAXIMUM_TEXTURE_SIZE (private static; its clinit stored
     * RenderSystem.getDevice().getMaxTextureSize(), and computeTextureSizeFor clamped the atlas size to it). 26.2 drops
     * the field and GpuDevice.getMaxTextureSize; its computeTextureSizeFor clamps to
     * getDeviceInfo().limits().maxTextureSizeForFormat(RGBA8_UNORM), the format its atlas texture is made in (javap of
     * both). The getter answers that cap.
     */
    public static int guiItemAtlasMaximumTextureSize() {
        return com.mojang.blaze3d.systems.RenderSystem.getDevice().getDeviceInfo().limits()
            .maxTextureSizeForFormat(com.mojang.blaze3d.GpuFormat.RGBA8_UNORM);
    }
}
