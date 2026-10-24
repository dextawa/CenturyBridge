package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.serialization.Dynamic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import top.dext.centurybridge.step.v26_1.mixin.CbWallWorldOpenFlowsAccess;

/**
 * 1.21.11's WorldOpenFlows.loadWorldStem(levelData, safeMode, packRepository): 26.1's also takes the world's
 * level storage access, which its loader reads the generation settings and saved data through (1.21.11's
 * level data carried them). 26.1's own caller passes the access it opened the world with; a 1.21.11 caller
 * holds that access open too -- the one open access of this flow's level source (rt/CbWallLevelAccess).
 */
@Environment(EnvType.CLIENT)
public final class CbWallWorldOpen {

    private CbWallWorldOpen() {
    }

    /** a mod's call of the old loadWorldStem */
    public static WorldStem loadWorldStem(WorldOpenFlows flows, Dynamic<?> levelData, boolean safeMode, PackRepository packs) throws Exception {
        return flows.loadWorldStem(access(flows, levelData, safeMode, packs), levelData, safeMode, packs);
    }

    /** the access 26.1's loadWorldStem takes; also the frameDefaults of the old loadWorldStem hook */
    public static LevelStorageSource.LevelStorageAccess access(WorldOpenFlows flows, Dynamic<?> levelData, boolean safeMode, PackRepository packs) {
        return CbWallLevelAccess.onlyOpen(((CbWallWorldOpenFlowsAccess) flows).cb$levelSource(), "WorldOpenFlows.loadWorldStem(levelData, safeMode, packRepository)");
    }
}
