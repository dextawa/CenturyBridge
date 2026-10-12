package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.function.Supplier;
import net.minecraft.class_1322;

/**
 * Attributes: 1.20.2's AttributeModifier(UUID, Supplier name, amount, operation) and getName() on 1.20.3,
 * whose modifier stores its name as a plain String and has no getter. Wired from s2world.json; the mixin
 * side is CbWorldAttributeModifierMixin.
 */
public final class CbWorldAttributes {

    /**
     * AttributeModifier(UUID, Supplier, ..) became (UUID, String, ..). 1.20.2 asked the supplier each time it read
     * the name and never at construction: the 1.20.3 field gets this empty placeholder, and every 1.20.3 reader
     * of the field -- save, toString, the codec -- reads the name through the supplier instead
     * ({@link #attachName}).
     */
    public static String placeholderName() {
        return "";
    }

    /** the constructor reshape's post step: the constructed modifier keeps the 1.20.2 constructor's supplier */
    public static void attachName(class_1322 modifier, Supplier<String> nameGetter) {
        ((CbWorldAttributeModifierName) modifier).cb$setNameGetter(nameGetter);
    }

    private CbWorldAttributes() {
    }
}
