package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** WorldOpenFlows's level source (rt/CbWallWorldOpen) */
@Environment(EnvType.CLIENT)
@Mixin(WorldOpenFlows.class)
public interface CbWallWorldOpenFlowsAccess {

    @Accessor("levelSource")
    LevelStorageSource cb$levelSource();
}
