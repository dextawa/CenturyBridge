package top.dext.centurybridge.step.v1_21.facade;

/** 1.20.6's ArrowInfiniteEnchantment (net/minecraft/class_1880): infinity excludes mending */
public class class_1880 extends class_1887 {

    public class_1880(class_1887$class_9427 definition) {
        super(definition);
    }

    @Override
    public boolean method_8180(class_1887 other) {
        if (other instanceof class_1899) {
            return false;
        }
        return super.method_8180(other);
    }
}
