package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_4358;
import net.minecraft.class_437;
import net.minecraft.class_4394;
import net.minecraft.class_4398;
import net.minecraft.class_4905;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.facade.v1_20_2.class_5221;
import top.dext.centurybridge.rt.v1_20_2.CbNetLegacyTask;
import top.dext.centurybridge.rt.v1_20_2.CbNetRealmsAccess;

/**
 * RealmsLongRunningMcoTaskScreen: the side of 1.20.1's task protocol a task called (error, aborted,
 * setTitle; the screen was an ErrorCallback), and the hand-over of the screen to a 1.20.1 task when the
 * screen is made, before the task's thread starts -- 1.20.1's constructor called task.setScreen(this).
 */
@Mixin(class_4398.class)
public abstract class CbNetRealmsTaskScreenBridge extends class_4905 implements class_5221, CbNetRealmsAccess.TaskScreen {

    @Shadow
    private class_4358 field_19919;

    @Shadow
    @Final
    private class_437 field_19912;

    @Shadow
    public abstract void method_53797(class_2561 title);

    protected CbNetRealmsTaskScreenBridge() {
        super(null);
    }

    /**
     * was error(message): the task failed with this message. 1.20.1 showed it in place of the progress,
     * with a Back button that aborted the task and returned to the screen before; here the task is
     * aborted at once (as 1.20.2 aborts a task that reports an error itself) and the message is shown
     * on the generic error screen, whose button leads to that same screen. Narrated as in 1.20.1.
     */
    @Override
    public void method_21067(class_2561 message) {
        field_19919.method_21071();
        class_310 client = class_310.method_1551();
        client.method_44713().method_37015(message);
        client.execute(() -> client.method_1507(new class_4394(message, field_19912)));
    }

    /** was aborted(): whether the player cancelled -- the task's own flag, which this screen's cancel sets */
    @Override
    public boolean method_21291() {
        return field_19919.method_21065();
    }

    /** was setTitle(title) */
    @Override
    public void method_21292(class_2561 title) {
        method_53797(title);
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_4358;method_53808()Lnet/minecraft/class_2561;"), require = 1)
    private void cb$handOver(class_437 lastScreen, class_4358 task, CallbackInfo ci) {
        if (task instanceof CbNetLegacyTask) {
            ((CbNetRealmsAccess.Task) task).method_21066((class_4398) (Object) this);
        }
    }
}
