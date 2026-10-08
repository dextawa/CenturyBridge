package top.dext.centurybridge.facade.v26_2;

import java.util.List;
import java.util.Optional;

import net.minecraft.world.level.block.state.properties.Property;

/**
 * Facade for 1.20.1 net/minecraft/class_2746 (BooleanProperty), made {@code
 * final} in 26.2. A mod that {@code extends class_2746} to define a custom
 * boolean block-state property can no longer subclass the real type. The real
 * {@code Property<Boolean>} base is still abstract and the core state machinery
 * only {@code checkcast}s to {@code Property}, so this facade extends it and
 * mirrors BooleanProperty's behaviour (values [true,false], name = toString).
 * See {@link class_2754} for the full rationale. Member names carry both the
 * mojang abstract overrides and the 1.20.1 intermediary tokens.
 */
public class class_2746 extends Property<Boolean> {

    private static final List<Boolean> VALUES = List.of(true, false);

    protected class_2746(String name) {
        super(name, Boolean.class);
    }

    // --- Property<Boolean> abstract surface (mojang names) ---

    @Override
    public List<Boolean> getPossibleValues() {
        return VALUES;
    }

    @Override
    public String getName(Boolean value) {
        return value.toString();
    }

    @Override
    public Optional<Boolean> getValue(String name) {
        if ("true".equals(name)) {
            return Optional.of(Boolean.TRUE);
        }
        if ("false".equals(name)) {
            return Optional.of(Boolean.FALSE);
        }
        return Optional.empty();
    }

    @Override
    public int getInternalIndex(Boolean value) {
        return value ? 0 : 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof class_2746)) {
            return false;
        }
        return getName().equals(((class_2746) o).getName());
    }

    @Override
    public int generateHashCode() {
        return 31 * super.generateHashCode() + VALUES.hashCode();
    }

    // --- 1.20.1 intermediary surface ---

    /** getPossibleValues() */
    public java.util.Collection<Boolean> method_11898() {
        return VALUES;
    }

    /** create(name) -- method_11825 */
    public static class_2746 method_11825(String name) {
        return new class_2746(name);
    }

    /** getValue(String) -- method_11900 */
    public Optional<Boolean> method_11900(String name) {
        return getValue(name);
    }

    /** getName(Boolean) -- method_11826 */
    public String method_11826(Boolean value) {
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
