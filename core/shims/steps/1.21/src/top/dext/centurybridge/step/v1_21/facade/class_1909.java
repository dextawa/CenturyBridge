package top.dext.centurybridge.step.v1_21.facade;

import top.dext.centurybridge.step.v1_21.rt.CbEnchVanilla;

/** 1.20.6's UntouchingEnchantment (net/minecraft/class_1909, silk touch; constructor protected): excludes fortune */
public class class_1909 extends class_1887 {

    public class_1909(class_1887$class_9427 definition) {
        super(definition);
    }

    @Override
    public boolean method_8180(class_1887 other) {
        return super.method_8180(other) && other != CbEnchVanilla.field_9130();
    }
}
