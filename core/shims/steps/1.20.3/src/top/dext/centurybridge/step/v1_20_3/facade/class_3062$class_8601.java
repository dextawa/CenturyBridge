package top.dext.centurybridge.step.v1_20_3.facade;

/**
 * Facade for 1.20.2's net/minecraft/class_3062$class_8601 (FunctionCommand.FunctionResult), gone in 1.20.3:
 * what running a function answered -- the value it returned (isReturn), else how many of its commands ran.
 * 1.20.2's record, as a final class with public fields (a facade's members must all be public; a record's
 * fields are private), its accessors, and the record's equality, hash and string.
 */
public final class class_3062$class_8601 {

    /** value */
    public final int comp_1572;
    /** isReturn */
    public final boolean comp_1573;

    public class_3062$class_8601(int value, boolean isReturn) {
        this.comp_1572 = value;
        this.comp_1573 = isReturn;
    }

    public int comp_1572() {
        return comp_1572;
    }

    public boolean comp_1573() {
        return comp_1573;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof class_3062$class_8601 other && other.comp_1572 == comp_1572 && other.comp_1573 == comp_1573;
    }

    @Override
    public int hashCode() {
        return 31 * Integer.hashCode(comp_1572) + Boolean.hashCode(comp_1573);
    }

    @Override
    public String toString() {
        return "class_8601[comp_1572=" + comp_1572 + ", comp_1573=" + comp_1573 + "]";
    }
}
