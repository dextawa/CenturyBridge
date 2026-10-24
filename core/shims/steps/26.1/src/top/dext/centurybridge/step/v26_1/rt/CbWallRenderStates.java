package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;

/**
 * The game render state 26.1's FeatureRenderDispatcher and LevelRenderer take. 1.21.11's took the level render
 * state alone (LevelRenderer) or nothing (FeatureRenderDispatcher); 26.1's GameRenderState holds the level state
 * beside the lightmap, GUI, options and window states the game renderer extracts each frame.
 */
@Environment(EnvType.CLIENT)
public final class CbWallRenderStates {

    private CbWallRenderStates() {
    }

    /** the game renderer's state (the one 26.1's own feature dispatcher reads); a fresh one before there is one */
    public static GameRenderState gameRenderState() {
        GameRenderer renderer = Minecraft.getInstance().gameRenderer;
        return renderer != null ? renderer.getGameRenderState() : new GameRenderState();
    }

    /**
     * the game render state around a 1.21.11 level render state: the game's own when it is the game's level state,
     * otherwise a state holding it beside the game's lightmap, GUI, options and window states (which 1.21.11's
     * LevelRenderer read from the game directly)
     */
    public static GameRenderState gameRenderState(LevelRenderState level) {
        GameRenderState game = gameRenderState();
        if (level == null || game.levelRenderState == level) {
            return game;
        }
        GameRenderState own = new GameRenderState();
        own.levelRenderState = level;
        own.lightmapRenderState = game.lightmapRenderState;
        own.guiRenderState = game.guiRenderState;
        own.optionsRenderState = game.optionsRenderState;
        own.windowRenderState = game.windowRenderState;
        return own;
    }
}
