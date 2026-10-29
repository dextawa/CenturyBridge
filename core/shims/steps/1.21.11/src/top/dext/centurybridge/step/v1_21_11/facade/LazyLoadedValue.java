package top.dext.centurybridge.step.v1_21_11.facade;

import java.util.function.Supplier;

/**
 * 1.21.10's LazyLoadedValue (net/minecraft/class_3528), gone from 1.21.11: the supplier memoized on first get, as
 * 1.21.10's body did it (Guava Suppliers.memoize). Member names are 1.21.10's intermediary names.
 */
public class LazyLoadedValue<T> {
    public final Supplier<T> field_15719;

    public LazyLoadedValue(Supplier<T> factory) {
        java.util.Objects.requireNonNull(factory);
        this.field_15719 = com.google.common.base.Suppliers.memoize(factory::get);
    }

    /** get() */
    public T method_15332() {
        return this.field_15719.get();
    }
}
