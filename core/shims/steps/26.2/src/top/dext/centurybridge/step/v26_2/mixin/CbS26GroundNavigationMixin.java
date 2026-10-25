package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.pathfinder.PathType;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * GroundPathNavigation.hasValidPathType(PathType), which 26.2 dropped, restored with 26.1.2's body (javap): false for
 * WATER, LAVA and OPEN, true otherwise. 26.1.2's vanilla never asked it (its only caller was the
 * Strider$StriderPathNavigation override's super call), so a 26.1.2 subclass's override or call, and a mod's mixin on
 * it, work as on 26.1.2.
 */
@Mixin(value = GroundPathNavigation.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbS26GroundNavigationMixin {

    /** 26.1.2 GroundPathNavigation.hasValidPathType(pathType) */
    protected boolean hasValidPathType(PathType pathType) {
        if (pathType == PathType.WATER) {
            return false;
        }
        if (pathType == PathType.LAVA) {
            return false;
        }
        return pathType != PathType.OPEN;
    }
}
