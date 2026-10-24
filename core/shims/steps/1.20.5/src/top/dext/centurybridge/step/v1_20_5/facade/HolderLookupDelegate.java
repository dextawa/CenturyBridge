package top.dext.centurybridge.step.v1_20_5.facade;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.class_5321;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_6885;
import net.minecraft.class_7225;

/**
 * HolderLookup.Delegate (gone in 1.20.5): a lookup forwarding everything to its parent, for
 * subclasses that change some of it. 1.20.4's class as it was.
 */
public class HolderLookupDelegate<T> implements class_7225<T> {

    /** parent (protected in 1.20.4; public: a facade member is reached without access wideners) */
    public final class_7225<T> field_40933;

    public HolderLookupDelegate(class_7225<T> parent) {
        this.field_40933 = parent;
    }

    @Override
    public Optional<class_6880.class_6883<T>> method_46746(class_5321<T> id) {
        return this.field_40933.method_46746(id);
    }

    @Override
    public Stream<class_6880.class_6883<T>> method_42017() {
        return this.field_40933.method_42017();
    }

    @Override
    public Optional<class_6885.class_6888<T>> method_46733(class_6862<T> tag) {
        return this.field_40933.method_46733(tag);
    }

    @Override
    public Stream<class_6885.class_6888<T>> method_42020() {
        return this.field_40933.method_42020();
    }
}
