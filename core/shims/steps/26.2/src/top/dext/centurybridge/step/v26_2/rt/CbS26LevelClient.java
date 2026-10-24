package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.debug.GameTestBlockHighlightRenderer;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 26.1.2 LevelRenderer members whose state 26.2 moved into LevelExtractor (Minecraft.levelExtractor beside
 * Minecraft.levelRenderer), each the LevelExtractor method 26.2's callers call where 26.1.2's called LevelRenderer's
 * (javap of both: the same caller methods) -- the ones core/tools/gen_delegate_moves.py cannot pair by itself because
 * the callers' own names moved too (countRenderedSections is called by getSectionStatistics, now sectionStatistics),
 * the class's own frame method calls it (allChanged), or three siblings share the name (setLevel: Minecraft
 * .updateLevelInEngines hands the level to levelExtractor, particleEngine and gameRenderer; 26.1.2's LevelRenderer
 * .setLevel body -- the level, allChanged, entityRenderDispatcher.resetCamera, the section state, the game test
 * highlights -- is LevelExtractor.setLevel's). They serve Minecraft's own LevelRenderer only: its state lives on
 * Minecraft's LevelExtractor.
 */
@Environment(EnvType.CLIENT)
public final class CbS26LevelClient {
    private CbS26LevelClient() {}

    private static LevelExtractor extractor(LevelRenderer self) {
        Minecraft g = Minecraft.getInstance();
        if (self != g.levelRenderer) {
            throw new IllegalStateException("26.2 keeps this state on Minecraft.getInstance().levelExtractor, which serves only Minecraft.getInstance().levelRenderer");
        }
        return g.levelExtractor;
    }

    /** 26.1.2 LevelRenderer.allChanged(): KeyboardHandler.handleDebugKeys, Minecraft, VideoSettingsScreen call LevelExtractor's */
    public static void allChanged(LevelRenderer self) {
        extractor(self).allChanged();
    }

    /** 26.1.2 LevelRenderer.setLevel(level) */
    public static void setLevel(LevelRenderer self, ClientLevel level) {
        extractor(self).setLevel(level);
    }

    /** 26.1.2 LevelRenderer.countRenderedSections(): GameRenderer.takeAutoScreenshot and the statistics call LevelExtractor's */
    public static int countRenderedSections(LevelRenderer self) {
        return extractor(self).countRenderedSections();
    }

    /** 26.1.2 LevelRenderer.getTotalSections(): the section count, 26.2's LevelExtractor.totalSections() */
    public static double getTotalSections(LevelRenderer self) {
        return extractor(self).totalSections();
    }

    /** 26.1.2 LevelRenderer.resetSampler(): 26.2's LevelExtractor.resetSampler() (the chunk layer sampler, reset at the next frame) */
    public static void resetSampler(LevelRenderer self) {
        extractor(self).resetSampler();
    }

    /** 26.1.2 LevelRenderer.onResourceManagerReload: 26.2 registers LevelExtractor as the reload listener in its place (Minecraft.<init>) */
    public static void onResourceManagerReload(LevelRenderer self, ResourceManager manager) {
        extractor(self).onResourceManagerReload(manager);
    }

    /**
     * 26.1.2 LevelRenderer.blockChanged(level, pos, oldState, newState, flags): its body was setBlockDirty(pos, (flags &amp; 8)
     * != 0), level and states unread; 26.2's LevelExtractor.blockChanged(pos, flags), which ClientLevel.sendBlockUpdated calls
     * where 26.1.2's called LevelRenderer's
     */
    public static void blockChanged(LevelRenderer self, BlockGetter level, BlockPos pos, BlockState oldState, BlockState newState, int flags) {
        extractor(self).blockChanged(pos, flags);
    }

    /** 26.1.2 LevelRenderer.debugRenderer: 26.2's LevelExtractor.debugRenderer */
    public static DebugRenderer debugRenderer(LevelRenderer self) {
        return extractor(self).debugRenderer;
    }

    /** 26.1.2 LevelRenderer.gameTestBlockHighlightRenderer: 26.2's LevelExtractor.gameTestBlockHighlightRenderer */
    public static GameTestBlockHighlightRenderer gameTestBlockHighlightRenderer(LevelRenderer self) {
        return extractor(self).gameTestBlockHighlightRenderer;
    }
}
