package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_1368;
import net.minecraft.class_1409;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestNavigation;

/**
 * MoveThroughVillageGoal.canUse: 1.21.3 read the navigation's canOpenDoors() before each temporary
 * setCanOpenDoors(supplier) and set that value back after the path search; 1.21.4 sets true back. For a
 * 1.21.3 mod navigation overriding canOpenDoors (CbRestNavigation.GroundCanOpenDoors) its own value is read
 * before the temporary set and restored, as 1.21.3 did; vanilla navigations keep 1.21.4's behaviour.
 */
@Mixin(class_1368.class)
public abstract class CbRestVillageGoalMixin {

    @Unique
    private boolean cb$savedOpenDoors;

    @Redirect(method = "method_6264", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1409;method_6363(Z)V", ordinal = 0))
    private void cb$setTemporary0(class_1409 nav, boolean b) {
        this.cb$save(nav);
        nav.method_6363(b);
    }

    @Redirect(method = "method_6264", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1409;method_6363(Z)V", ordinal = 2))
    private void cb$setTemporary2(class_1409 nav, boolean b) {
        this.cb$save(nav);
        nav.method_6363(b);
    }

    @Redirect(method = "method_6264", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1409;method_6363(Z)V", ordinal = 1))
    private void cb$restore1(class_1409 nav, boolean b) {
        nav.method_6363((Object) nav instanceof CbRestNavigation.GroundCanOpenDoors ? this.cb$savedOpenDoors : b);
    }

    @Redirect(method = "method_6264", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1409;method_6363(Z)V", ordinal = 3))
    private void cb$restore3(class_1409 nav, boolean b) {
        nav.method_6363((Object) nav instanceof CbRestNavigation.GroundCanOpenDoors ? this.cb$savedOpenDoors : b);
    }

    @Unique
    private void cb$save(class_1409 nav) {
        if ((Object) nav instanceof CbRestNavigation.GroundCanOpenDoors a) {
            this.cb$savedOpenDoors = a.method_6366();
        }
    }
}
