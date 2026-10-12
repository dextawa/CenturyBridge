package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.function.Supplier;

/** what CbWorldAttributeModifierMixin adds to AttributeModifier: 1.20.2's getName(), and its name supplier */
public interface CbWorldAttributeModifierName {
    String method_6185();

    /** the name supplier a 1.20.2 constructor was given (CbWorldAttributes.attachName) */
    void cb$setNameGetter(Supplier<String> nameGetter);
}
