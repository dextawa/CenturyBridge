package top.dext.centurybridge.step.v1_20_3.mixin;

import java.util.List;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_3.rt.CbNetRealmsTasks;

/**
 * The injectors of CbNetRealmsTaskScreenMixin (the restorer of RealmsLongRunningMcoTaskScreen's 1.20.2 task field), at
 * the default priority so they run among mods' at their points (mixin_priority.py rule 2): the constructor stores the
 * task it was made with, and cancel() aborts another task a mod stored in the field, as 1.20.2's did.
 */
@Mixin(net.minecraft.class_4398.class)
public abstract class CbNetRealmsTaskScreenMixinHooks {

    /** 1.20.2 RealmsLongRunningMcoTaskScreen.task (restored by CbNetRealmsTaskScreenMixin) */
    @Shadow
    private net.minecraft.class_4358 field_19919;

    @Shadow
    @Final
    private List<net.minecraft.class_4358> field_46707;

    @Shadow
    @Final
    private net.minecraft.class_437 field_19912;

    /** the task the constructor stored: the field holds another one only when a mod stored it */
    @Unique
    private net.minecraft.class_4358 cb$madeWith;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cb$storeTask(net.minecraft.class_437 lastScreen, net.minecraft.class_4358[] tasks, CallbackInfo ci) {
        net.minecraft.class_4358 task = CbNetRealmsTasks.asOne(field_46707);
        field_19919 = task;
        cb$madeWith = task;
    }

    @Inject(method = "method_53798", at = @At("HEAD"), cancellable = true)
    private void cb$cancelStoredTask(CallbackInfo ci) {
        net.minecraft.class_4358 task = field_19919;
        if (task != cb$madeWith) {
            if (task != null) {
                task.method_21071();
            }
            net.minecraft.class_310.method_1551().method_1507(field_19912);
            ci.cancel();
        }
    }
}
