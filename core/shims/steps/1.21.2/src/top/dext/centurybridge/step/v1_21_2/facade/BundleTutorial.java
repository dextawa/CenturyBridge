package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * BundleTutorial (class_5829) of 1.21.1: the hint toast for putting items into a bundle, shown until the
 * player has done it once (Options.hideBundleTutorial, restored by CbRestOptionsMixin). 1.21.2 dropped it;
 * this is 1.21.1's class, its toasts timed through Tutorial's 1.21.1 timed-toast methods
 * (CbRestTutorialMixin).
 */
@Environment(EnvType.CLIENT)
public class BundleTutorial {

    private final net.minecraft.class_1156 tutorial;
    private final net.minecraft.class_315 options;
    private net.minecraft.class_372 toast;

    public BundleTutorial(net.minecraft.class_1156 tutorial, net.minecraft.class_315 options) {
        this.tutorial = tutorial;
        this.options = options;
    }

    private void showToast() {
        if (this.toast != null) {
            ((top.dext.centurybridge.step.v1_21_2.rt.CbRestTutorial.TimedToasts) this.tutorial).method_31364(this.toast);
        }
        net.minecraft.class_2561 title = net.minecraft.class_2561.method_43471("tutorial.bundleInsert.title");
        net.minecraft.class_2561 description = net.minecraft.class_2561.method_43471("tutorial.bundleInsert.description");
        this.toast = new net.minecraft.class_372(net.minecraft.class_372.class_373.field_28782, title, description, true);
        ((top.dext.centurybridge.step.v1_21_2.rt.CbRestTutorial.TimedToasts) this.tutorial).method_31365(this.toast, 160);
    }

    private void clearToast() {
        if (this.toast != null) {
            ((top.dext.centurybridge.step.v1_21_2.rt.CbRestTutorial.TimedToasts) this.tutorial).method_31364(this.toast);
            this.toast = null;
        }
        if (!top.dext.centurybridge.step.v1_21_2.rt.CbRestTutorial.hideBundleTutorial(this.options)) {
            top.dext.centurybridge.step.v1_21_2.rt.CbRestTutorial.setHideBundleTutorial(this.options);
            this.options.method_1640();
        }
    }

    /** onInventoryAction(carried, slotItem, action), 1.21.1's body */
    public void method_33702(net.minecraft.class_1799 carried, net.minecraft.class_1799 slotItem, net.minecraft.class_5536 action) {
        if (top.dext.centurybridge.step.v1_21_2.rt.CbRestTutorial.hideBundleTutorial(this.options)) {
            return;
        }
        if (!carried.method_7960() && slotItem.method_31574(net.minecraft.class_1802.field_27023)) {
            if (action == net.minecraft.class_5536.field_27013) {
                this.showToast();
            } else if (action == net.minecraft.class_5536.field_27014) {
                this.clearToast();
            }
        } else if (carried.method_31574(net.minecraft.class_1802.field_27023) && !slotItem.method_7960()
                && action == net.minecraft.class_5536.field_27014) {
            this.clearToast();
        }
    }
}
