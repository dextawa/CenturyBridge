package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1156;
import net.minecraft.class_310;
import net.minecraft.class_372;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbPrioritySplitClient;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestTutorial;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * Tutorial's timed toasts of 1.21.1 (addTimedToast / removeTimedToast), back: a toast added for a number of
 * ticks shows its progress filling over them and hides when they are over (each tick, as 1.21.1's tick
 * did first; CbRestTutorialMixinHooks, default priority); removing one hides it at once. 1.21.2 dropped them
 * with the bundle tutorial, their only user.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = class_1156.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestTutorialMixin implements CbRestTutorial.TimedToasts, CbPrioritySplitClient.TutorialToasts {

    @Shadow
    @Final
    private class_310 field_5645;

    @Unique
    private final List<Object[]> cb$timedToasts = new ArrayList<>();

    @Override
    public List<Object[]> cb$v1_21_2$timedToasts() {
        return this.cb$timedToasts;
    }

    @Override
    public void method_31365(class_372 toast, int durationTicks) {
        this.cb$timedToasts.add(new Object[] {toast, durationTicks, new int[] {0}});
        this.field_5645.method_1566().method_1999(toast);
    }

    @Override
    public void method_31364(class_372 toast) {
        this.cb$timedToasts.removeIf(t -> t[0] == toast);
        toast.method_1993();
    }
}
