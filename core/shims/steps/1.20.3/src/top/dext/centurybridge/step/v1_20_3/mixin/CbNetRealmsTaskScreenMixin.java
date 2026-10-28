package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_3.rt.CbNetRealmsTasks;

/**
 * RealmsLongRunningMcoTaskScreen's 1.20.2 field task (field_19919, the one long-running task the screen ran), which
 * 1.20.3 replaced by a list of tasks run in sequence (field_46707), given back on the class and kept as 1.20.2 kept it.
 * At the layer's CbVanillaReads.PRIORITY: merged before every mod's @Shadow / @Accessor on it resolves, and before the
 * 1.20.2 layer's CbNetRealmsTaskScreenBridge (that layer's PRIORITY, one higher: layer order) shadows it.
 * <p>
 * 1.20.2: the constructor (lastScreen, task) stored the task; cancel() aborted it and went back to lastScreen. 1.20.3:
 * the constructor (lastScreen, tasks...) keeps the list, and cancel() aborts every task of it. A 1.20.2-shaped screen
 * (one task: the step reshapes 1.20.2's constructor onto the list of that one) holds that task; a screen 1.20.3 made
 * with several holds their sequence as one task (CbNetRealmsTasks.Sequence: aborting it aborts each, as 1.20.3's
 * cancel does).
 * <p>
 * Its injectors are in CbNetRealmsTaskScreenMixinHooks (default priority: mixin_priority.py rule 2).
 * <p>
 * cancel() stays 1.20.3's unless a mod stored another task in the field: 1.20.2's cancel then aborted that task, not
 * the one the screen was made with, and went back to lastScreen.
 */
@Mixin(value = net.minecraft.class_4398.class, priority = top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads.PRIORITY)
public abstract class CbNetRealmsTaskScreenMixin implements CbNetRealmsTasks.Holder {

    /** 1.20.2 RealmsLongRunningMcoTaskScreen.task (restored) */
    private net.minecraft.class_4358 field_19919;

    @Override
    public net.minecraft.class_4358 cb$task() {
        return field_19919;
    }

    @Override
    public void cb$setTask(net.minecraft.class_4358 task) {
        field_19919 = task;
    }
}
