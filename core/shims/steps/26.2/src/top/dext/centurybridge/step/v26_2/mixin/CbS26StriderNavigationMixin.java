package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.pathfinder.PathType;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_2.rt.CbS26Navigation;
import top.dext.centurybridge.step.v26_2.rt.CbVanillaReads;

/**
 * Strider$StriderPathNavigation.hasValidPathType(PathType), which 26.2 dropped with GroundPathNavigation's, restored
 * with 26.1.2's body (javap): true for LAVA, FIRE and FIRE_IN_NEIGHBOR, otherwise GroundPathNavigation's answer
 * (super, non-virtual: rt/CbS26Navigation).
 */
@Mixin(targets = "net.minecraft.world.entity.monster.Strider$StriderPathNavigation", priority = CbVanillaReads.PRIORITY)
public abstract class CbS26StriderNavigationMixin {

    /** 26.1.2 Strider$StriderPathNavigation.hasValidPathType(pathType) */
    protected boolean hasValidPathType(PathType pathType) {
        if (pathType == PathType.LAVA || pathType == PathType.FIRE || pathType == PathType.FIRE_IN_NEIGHBOR) {
            return true;
        }
        return CbS26Navigation.superHasValidPathType((GroundPathNavigation) (Object) this, pathType);
    }
}
