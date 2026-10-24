package top.dext.centurybridge.step.v1_21_4.mixin;

import net.minecraft.class_11;
import net.minecraft.class_1308;
import net.minecraft.class_1343;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestNavigation;

/**
 * DoorInteractGoal.canUse: 1.21.3 went on only when the path was not done AND the navigation's
 * canOpenDoors() said so; 1.21.4 dropped the second read. A 1.21.3 mod navigation's own canOpenDoors
 * override (CbRestNavigation.GroundCanOpenDoors) is read there again, after isDone as 1.21.3 read it;
 * vanilla navigations keep 1.21.4's behaviour.
 */
@Mixin(class_1343.class)
public abstract class CbRestDoorGoalsMixin {

    @Shadow
    protected class_1308 field_6413;

    @Redirect(method = "method_6264", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_11;method_46()Z"))
    private boolean cb$doneOrClosed(class_11 path) {
        if (path.method_46()) {
            return true;
        }
        return (Object) this.field_6413.method_5942() instanceof CbRestNavigation.GroundCanOpenDoors a && !a.method_6366();
    }
}
