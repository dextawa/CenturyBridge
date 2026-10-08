package top.dext.centurybridge.facade.v26_2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.world.level.block.state.properties.Property;

/**
 * Facade for 1.20.1 net/minecraft/class_2758 (IntegerProperty), made {@code
 * final} in 26.2. A mod that {@code extends class_2758} to define a custom
 * integer block-state property can no longer subclass the real type. Extends
 * the still-abstract {@code Property<Integer>} and mirrors IntegerProperty's
 * behaviour (values [min..max], name = toString). See {@link class_2754} for
 * the full rationale. Member names carry both the mojang abstract overrides
 * and the 1.20.1 intermediary tokens.
 */
public class class_2758 extends Property<Integer> {

    private final List<Integer> values;
    private final int min;
    private final int max;

    protected class_2758(String name, int min, int max) {
        super(name, Integer.class);
        this.min = min;
        this.max = max;
        List<Integer> v = new ArrayList<>();
        for (int i = min; i <= max; i++) {
            v.add(i);
        }
        this.values = List.copyOf(v);
    }

    // --- Property<Integer> abstract surface (mojang names) ---

    @Override
    public List<Integer> getPossibleValues() {
        return values;
    }

    @Override
    public String getName(Integer value) {
        return value.toString();
    }

    @Override
    public Optional<Integer> getValue(String name) {
        try {
            int i = Integer.parseInt(name);
            return (i >= min && i <= max) ? Optional.of(i) : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public int getInternalIndex(Integer value) {
        return value - min;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof class_2758)) {
            return false;
        }
        class_2758 other = (class_2758) o;
        return getName().equals(other.getName()) && values.equals(other.values);
    }

    @Override
    public int generateHashCode() {
        int h = 31 * super.generateHashCode() + values.hashCode();
        return h;
    }

    // --- 1.20.1 intermediary surface ---

    /** getPossibleValues() */
    public java.util.Collection<Integer> method_11898() {
        return values;
    }

    /** create(name, min, max) -- method_11867 */
    public static class_2758 method_11867(String name, int min, int max) {
        return new class_2758(name, min, max);
    }

    /** getValue(String) -- method_11900 */
    public Optional<Integer> method_11900(String name) {
        return getValue(name);
    }

    /** getName(Integer) -- method_11868 */
    public String method_11868(Integer value) {
        return value.toString();
    }

    /** getInternalIndex marker -- method_11799 */
    public int method_11799() {
        return System.identityHashCode(this);
    }

    /** getName(Comparable) -- method_11901 */
    public String method_11901(Comparable<?> value) {
        return value.toString();
    }
}
