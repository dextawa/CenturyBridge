package top.dext.centurybridge.step.v1_21_11.facade;

import java.util.Objects;
import net.minecraft.class_3218;

/**
 * 1.21.10's SavedData.Context (net/minecraft/class_18$class_10740, a record (ServerLevel level, long seed)), gone from
 * 1.21.11 with every member that took it (SavedDataType's Function forms, DimensionDataStorage's Context constructor;
 * javap -p both). Member names are 1.21.10's intermediary names; bodies are 1.21.10's.
 */
public final class SavedDataContext {
    public final class_3218 comp_3639;
    public final long comp_3640;

    /** Context(level): the level's seed */
    public SavedDataContext(class_3218 level) {
        this(level, level.method_8412());
    }

    public SavedDataContext(class_3218 level, long seed) {
        this.comp_3639 = level;
        this.comp_3640 = seed;
    }

    /** levelOrThrow() */
    public class_3218 method_67417() {
        return Objects.requireNonNull(this.comp_3639);
    }

    public class_3218 comp_3639() {
        return this.comp_3639;
    }

    public long comp_3640() {
        return this.comp_3640;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SavedDataContext c && c.comp_3639 == this.comp_3639 && c.comp_3640 == this.comp_3640;
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(this.comp_3639) + Long.hashCode(this.comp_3640);
    }

    @Override
    public String toString() {
        return "Context[level=" + this.comp_3639 + ", seed=" + this.comp_3640 + "]";
    }
}
