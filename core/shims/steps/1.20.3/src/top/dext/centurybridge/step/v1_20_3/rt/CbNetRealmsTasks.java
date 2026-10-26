package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.2's one long-running task of a RealmsLongRunningMcoTaskScreen, on 1.20.3's screens, which run a sequence of
 * tasks (CbNetRealmsTaskScreenMixin).
 */
@Environment(EnvType.CLIENT)
public final class CbNetRealmsTasks {

    private CbNetRealmsTasks() {
    }

    /** RealmsLongRunningMcoTaskScreen with its restored 1.20.2 task field (CbNetRealmsTaskScreenMixin) */
    public interface Holder {
        net.minecraft.class_4358 cb$task();

        void cb$setTask(net.minecraft.class_4358 task);
    }

    /**
     * read of 1.20.2 RealmsLongRunningMcoTaskScreen.task (instanceFieldGetters): the restored field. Mixins of a jar
     * converted where the layer's restored field is known shadow the field itself; this serves the others (a layer
     * climbed before this layer was built).
     */
    public static net.minecraft.class_4358 field_19919(net.minecraft.class_4398 screen) {
        return ((Holder) screen).cb$task();
    }

    /** write of 1.20.2 RealmsLongRunningMcoTaskScreen.task (instanceFieldSetters) */
    public static void field_19919(net.minecraft.class_4398 screen, net.minecraft.class_4358 task) {
        ((Holder) screen).cb$setTask(task);
    }

    /** the screen's tasks as 1.20.2's one task: the task itself when there is one, else their sequence */
    public static net.minecraft.class_4358 asOne(List<net.minecraft.class_4358> tasks) {
        return tasks.size() == 1 ? tasks.get(0) : new Sequence(tasks);
    }

    /**
     * Several tasks as one, the way 1.20.3's screen treats them: run in order while none is aborted (the screen's task
     * thread), aborting aborts each (the screen's cancel), aborted when any is; its title is the first task's (the
     * screen's title when it is made).
     */
    public static final class Sequence extends net.minecraft.class_4358 {
        private final List<net.minecraft.class_4358> tasks;

        Sequence(List<net.minecraft.class_4358> tasks) {
            this.tasks = List.copyOf(tasks);
        }

        @Override
        public void run() {
            for (net.minecraft.class_4358 t : tasks) {
                if (t.method_21065()) {
                    return;
                }
                t.run();
            }
        }

        @Override
        public net.minecraft.class_2561 method_53808() {
            return tasks.get(0).method_53808();
        }

        @Override
        public boolean method_21065() {
            for (net.minecraft.class_4358 t : tasks) {
                if (t.method_21065()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void method_21071() {
            for (net.minecraft.class_4358 t : tasks) {
                t.method_21071();
            }
        }
    }
}
