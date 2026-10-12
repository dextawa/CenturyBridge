package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2561;
import net.minecraft.class_4358;
import net.minecraft.class_4398;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.facade.v1_20_2.class_5221;
import top.dext.centurybridge.rt.v1_20_2.CbNetLegacyTask;
import top.dext.centurybridge.rt.v1_20_2.CbNetRealmsAccess;

/**
 * LongRunningTask: 1.20.1's task worked through the screen running it -- the screen handed itself
 * over (setScreen), the task titled it (setTitle) and reported a failure to it (error), and the task
 * was an ErrorCallback. 1.20.2's screen reads the task's getTitle() once, and a task's error opens the
 * generic error screen. Put back for 1.20.1 tasks (the mod classes CbNetLegacyTask marks); a 1.20.2
 * task is left as it is.
 */
@Mixin(class_4358.class)
public abstract class CbNetRealmsTaskBridge implements class_5221, CbNetRealmsAccess.Task {

    /** was longRunningMcoTaskScreen */
    protected class_4398 field_19638;

    @Unique
    private volatile class_2561 cb$title;

    /** was setScreen(screen): the screen this task reports to (1.20.1's screen constructor called it) */
    @Override
    public void method_21066(class_4398 screen) {
        this.field_19638 = screen;
    }

    /**
     * was setTitle(title): the title of the screen running the task. Kept on the task as well, for
     * the getTitle() 1.20.2's screen asks a 1.20.1 task when it is made (CbNetRealms.cbTaskTitle).
     */
    public void method_21069(class_2561 title) {
        this.cb$title = title;
        if (field_19638 != null) {
            ((CbNetRealmsAccess.TaskScreen) field_19638).method_21292(title);
        }
    }

    @Override
    public class_2561 cb$title() {
        return cb$title;
    }

    /** a 1.20.1 task reports a failure to its screen, as 1.20.1's error(message) did */
    @Inject(method = "method_21067", at = @At("HEAD"), cancellable = true)
    private void cb$legacyError(class_2561 message, CallbackInfo ci) {
        if ((Object) this instanceof CbNetLegacyTask && field_19638 != null) {
            ((CbNetRealmsAccess.TaskScreen) field_19638).method_21067(message);
            ci.cancel();
        }
    }
}
