package top.dext.centurybridge.facade.v26_2;

/**
 * Facade for net/minecraft/class_3545 (net.minecraft.util.Tuple), a trivial
 * two-value pair container present through 26.1 but DELETED in 26.2 (replaced
 * internally by records / com.mojang.datafixers.util.Pair). 26 corpus mods
 * carry Tuple as a field / parameter / return type in their own code and never
 * hand it back to vanilla, so a CB-namespace reimplementation keeps those
 * classes loadable and their own pair logic working.
 *
 * Both the mojang accessor names (getA/getB/setA/setB) and the intermediary
 * tokens (method_15442/method_34964/method_15441/method_34965) are provided:
 * a 1.20.1-era call site may carry either, depending on whether its reference
 * survived the namespace-wall member rename.
 */
public class class_3545<A, B> {

    private A a;
    private B b;

    public class_3545(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }

    // intermediary-named aliases (pre-wall-rename call sites)
    public A method_15442() {
        return a;
    }

    public void method_34964(A a) {
        this.a = a;
    }

    public B method_15441() {
        return b;
    }

    public void method_34965(B b) {
        this.b = b;
    }
}
