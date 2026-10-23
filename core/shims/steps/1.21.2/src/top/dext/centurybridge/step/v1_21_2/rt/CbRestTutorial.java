package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** Tutorial's 1.21.1 timed toasts (CbRestTutorialMixin) and the bundle tutorial's option flag (CbRestOptionsMixin) */
@Environment(EnvType.CLIENT)
public final class CbRestTutorial {

    private CbRestTutorial() {
    }

    /** Tutorial's 1.21.1 timed-toast methods, which CbRestTutorialMixin gives back */
    public interface TimedToasts {
        void method_31364(net.minecraft.class_372 toast);

        void method_31365(net.minecraft.class_372 toast, int durationTicks);
    }

    /** Options.hideBundleTutorial, the field CbRestOptionsMixin restores */
    public interface BundleOption {
        boolean cb$hideBundleTutorial();

        void cb$setHideBundleTutorial(boolean hide);
    }

    public static boolean hideBundleTutorial(net.minecraft.class_315 options) {
        return ((BundleOption) options).cb$hideBundleTutorial();
    }

    public static void setHideBundleTutorial(net.minecraft.class_315 options) {
        ((BundleOption) options).cb$setHideBundleTutorial(true);
    }
}
