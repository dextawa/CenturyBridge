package top.dext.centurybridge.facade.v1_20_2;

import java.util.Optional;

/**
 * Facade for 1.20.1 net/minecraft/class_2096 (MinMaxBounds), an abstract CLASS
 * with state (min/max fields, a protected (min,max) constructor, getters) that
 * mods subclass to define custom numeric-range advancement criteria
 * (extrabotany MinMaxBoundsExtension, hexcasting MinMaxLongs). On 1.20.2 it
 * became an INTERFACE (comp_1805/comp_1806 Optionals, record-style), so a legacy
 * `extends class_2096` fails to load ("extends an interface").
 *
 * This facade bridges both sides: it is an abstract CLASS (so the mod's
 * `extends` + super(min,max) + field/getter access keep working, re-declared
 * here) that `implements` the real 1.20.2 class_2096 interface (so the subclass
 * instance IS a genuine MinMaxBounds and hands back to vanilla). The interface's
 * two abstract accessors comp_1805/comp_1806 are implemented off the stored
 * fields. Reached via superRenames. Member names are intermediary tokens.
 */
public abstract class class_2096<T extends Number> implements net.minecraft.class_2096<T> {

    protected final T field_9702; // min
    protected final T field_9699; // max

    protected class_2096(T min, T max) {
        this.field_9702 = min;
        this.field_9699 = max;
    }

    /** getMin() */
    public T method_9038() {
        return field_9702;
    }

    /** getMax() */
    public T method_9042() {
        return field_9699;
    }

    /** interface accessor min -> Optional */
    @Override
    public Optional<T> comp_1805() {
        return Optional.ofNullable(field_9702);
    }

    /** interface accessor max -> Optional */
    @Override
    public Optional<T> comp_1806() {
        return Optional.ofNullable(field_9699);
    }
}
