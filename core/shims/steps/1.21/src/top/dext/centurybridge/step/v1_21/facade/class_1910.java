package top.dext.centurybridge.step.v1_21.facade;

import top.dext.centurybridge.step.v1_21.rt.CbEnchVanilla;

/** 1.20.6's TridentRiptideEnchantment (net/minecraft/class_1910): riptide excludes loyalty and channeling */
public class class_1910 extends class_1887 {

    public class_1910(class_1887$class_9427 definition) {
        super(definition);
    }

    @Override
    public boolean method_8180(class_1887 other) {
        return super.method_8180(other) && other != CbEnchVanilla.field_9120() && other != CbEnchVanilla.field_9117();
    }
}
