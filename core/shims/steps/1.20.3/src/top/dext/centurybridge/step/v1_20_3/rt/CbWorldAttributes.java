package top.dext.centurybridge.step.v1_20_3.rt;

import java.util.function.Supplier;

/**
 * Attributes: 1.20.2's AttributeModifier(UUID, Supplier name, amount, operation) and getName() on 1.20.3,
 * whose modifier stores its name as a plain String and has no getter. Wired from s2world.json; the mixin
 * side is CbWorldAttributeModifierMixin.
 */
public final class CbWorldAttributes {

    private record Pending(String marker, Supplier<String> nameGetter) {
    }

    private static final ThreadLocal<Pending> PENDING = new ThreadLocal<>();

    /**
     * AttributeModifier(UUID, Supplier, ..) became (UUID, String, ..). 1.20.2 asked the supplier each time it
     * read the name and never at construction, so the supplier goes to the modifier under construction (taken
     * when its constructor returns) and the 1.20.3 field gets an empty placeholder of its own. Every 1.20.3
     * reader of that field -- save, toString, the codec -- reads the name through the supplier instead.
     */
    public static String legacyName(Supplier<String> nameGetter) {
        // a String object of its own: the constructor takes the supplier only for this very argument
        String marker = new String(new char[0]);
        PENDING.set(new Pending(marker, nameGetter));
        return marker;
    }

    /** the name supplier a 1.20.2 constructor gave the modifier whose 1.20.3 constructor got {@code name} */
    public static Supplier<String> takeLegacyName(String name) {
        Pending p = PENDING.get();
        if (p == null || p.marker() != name) {
            return null;
        }
        PENDING.remove();
        return p.nameGetter();
    }

    private CbWorldAttributes() {
    }
}
