package top.dext.centurybridge.facade.v26_2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * Facade for 1.20.1 net/minecraft/class_2754 (EnumProperty / the old
 * DirectionProperty). 26.2 made EnumProperty {@code final}, so a mod that
 * {@code extends class_2754} to define a custom block-state property (e.g.
 * Ad Astra's EightDirectionProperty) can no longer subclass the real type.
 *
 * The real {@code Property<T>} base is still {@code public abstract}, and the
 * core state machinery (StateDefinition / StateHolder / the value codecs) only
 * ever {@code checkcast}s to {@code Property} -- it never does
 * {@code instanceof EnumProperty} -- so a facade that extends {@code Property}
 * and implements its four abstract methods is treated as a first-class
 * property by vanilla. This facade mirrors EnumProperty's behaviour (ordered
 * value list + name lookup) exactly, so a subclassing mod's property registers,
 * serializes and round-trips as before.
 *
 * The old (name, class, Collection) constructor is reconstructed for subclass
 * {@code super(...)} calls; the four static {@code create} overloads mirror the
 * 1.20.1 factory methods (intermediary names method_11850/11848/11849/11847).
 * Member names carry both the mojang abstract overrides and the intermediary
 * tokens the corpus calls.
 */
public class class_2754<T extends Enum<T> & StringRepresentable> extends Property<T> {

    private final List<T> values;
    private final Map<String, T> byName;

    protected class_2754(String name, Class<T> clazz, Collection<T> vals) {
        super(name, clazz);
        this.values = new ArrayList<>(vals);
        this.byName = new HashMap<>();
        for (T v : this.values) {
            this.byName.put(v.getSerializedName(), v);
        }
    }

    // --- Property<T> abstract surface (mojang names) ---

    @Override
    public List<T> getPossibleValues() {
        return values;
    }

    @Override
    public String getName(T value) {
        return value.getSerializedName();
    }

    @Override
    public Optional<T> getValue(String name) {
        return Optional.ofNullable(byName.get(name));
    }

    @Override
    public int getInternalIndex(T value) {
        return values.indexOf(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof class_2754)) {
            return false;
        }
        class_2754<?> other = (class_2754<?>) o;
        return getValueClass().equals(other.getValueClass())
            && getName().equals(other.getName())
            && values.equals(other.values);
    }

    @Override
    public int generateHashCode() {
        int h = super.generateHashCode();
        h = 31 * h + values.hashCode();
        return h;
    }

    // --- 1.20.1 intermediary surface (method tokens the corpus calls) ---

    /** 1.20.1 getPossibleValues() */
    public Collection<T> method_11898() {
        return values;
    }

    /** 1.20.1 getValue(String) */
    public Optional<T> method_11900(String name) {
        return getValue(name);
    }

    /** 1.20.1 getName(T) */
    public String method_11846(T value) {
        return value.getSerializedName();
    }

    /** 1.20.1 getInternalIndex() marker (method_11799) */
    public int method_11799() {
        return System.identityHashCode(this);
    }

    /** 1.20.1 getName(Comparable) bridge (method_11901) */
    @SuppressWarnings("unchecked")
    public String method_11901(Comparable<?> value) {
        return getName((T) value);
    }

    /** 1.20.1 EnumProperty.create(name, class) -- method_11850 */
    public static <T extends Enum<T> & StringRepresentable> class_2754<T> method_11850(String name, Class<T> clazz) {
        return method_11847(name, clazz, List.of(clazz.getEnumConstants()));
    }

    /** 1.20.1 EnumProperty.create(name, class, predicate) -- method_11848 */
    public static <T extends Enum<T> & StringRepresentable> class_2754<T> method_11848(
            String name, Class<T> clazz, java.util.function.Predicate<T> filter) {
        List<T> kept = new ArrayList<>();
        for (T v : clazz.getEnumConstants()) {
            if (filter.test(v)) {
                kept.add(v);
            }
        }
        return method_11847(name, clazz, kept);
    }

    /** 1.20.1 EnumProperty.create(name, class, T...) -- method_11849 */
    @SafeVarargs
    public static <T extends Enum<T> & StringRepresentable> class_2754<T> method_11849(String name, Class<T> clazz, T... vals) {
        return method_11847(name, clazz, List.of(vals));
    }

    /** 1.20.1 EnumProperty.create(name, class, Collection) -- method_11847 */
    public static <T extends Enum<T> & StringRepresentable> class_2754<T> method_11847(
            String name, Class<T> clazz, Collection<T> vals) {
        return new class_2754<>(name, clazz, vals);
    }
}
