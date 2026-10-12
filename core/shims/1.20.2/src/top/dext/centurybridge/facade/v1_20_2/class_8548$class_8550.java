package top.dext.centurybridge.facade.v1_20_2;

/**
 * Facade for 1.20.1 net/minecraft/class_8548$class_8550 (AllOfCondition.Serializer), deleted in
 * 1.20.2. 1.20.1's create(terms) was new AllOfCondition(terms); 1.20.2's AllOfCondition.allOf(List)
 * is that constructor (the composite's predicate is built from the terms in both).
 */
public class class_8548$class_8550 extends class_186$class_188 {

    public class_8548$class_8550() {
    }

    /** create(terms) */
    public net.minecraft.class_8548 method_51725(net.minecraft.class_5341[] terms) {
        return net.minecraft.class_8548.method_53404(java.util.List.of(terms));
    }

    @Override
    public net.minecraft.class_186 method_51726(net.minecraft.class_5341[] terms) {
        return method_51725(terms);
    }
}
