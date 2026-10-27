package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;

/**
 * 26.1.2 LevelRenderer.destroyBlockProgress(id, pos, progress), which 26.2 moved with its maps (destroyingBlocks,
 * destructionProgress) to ClientLevel.destroyBlockProgress(id, pos, progress): the same body, the tick stamped from the
 * level's game time instead of LevelRenderer.ticks (javap of both; s26e-mixin.json mixinMoves). 26.1.2's ClientLevel
 * forwarded its own destroyBlockProgress to the renderer; 26.2's holds the code itself. So a 26.1.2 call on the renderer
 * runs 26.2's on the level the renderer draws (Minecraft.level; the renderer's own level field is gone with the code).
 * Known difference: with no level loaded, 26.1.2 recorded into the renderer's maps (setLevel does not clear them,
 * javap), so the next level showed that progress until it expired; 26.2 keeps the maps per level, and with no level
 * there is nowhere to record it: the call records nothing.
 */
@Environment(EnvType.CLIENT)
public final class CbS26DestroyProgress {
    private CbS26DestroyProgress() {
    }

    /** instance redirect: 26.1.2 LevelRenderer.destroyBlockProgress(int, BlockPos, int) */
    public static void destroyBlockProgress(LevelRenderer renderer, int id, BlockPos pos, int progress) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            level.destroyBlockProgress(id, pos, progress);
        }
    }
}
