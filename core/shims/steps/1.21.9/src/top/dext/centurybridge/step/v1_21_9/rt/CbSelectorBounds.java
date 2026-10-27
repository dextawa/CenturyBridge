package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.Optional;
import net.minecraft.class_2096;
import top.dext.centurybridge.step.v1_21_9.facade.WrappedMinMaxBounds;

/**
 * EntitySelectorParser's x / y rotation bounds: 1.21.8 held a WrappedMinMaxBounds (nullable min, max), 1.21.9 a
 * MinMaxBounds.FloatDegrees (optional min, max). Both releases wrap the degrees where the predicate is built, with the
 * same defaults (javap -c method_9859: wrapDegrees(min or 0), wrapDegrees(max or 359)), so the two carry the same
 * values and these convert without loss in either direction.
 */
public final class CbSelectorBounds {
    private CbSelectorBounds() {
    }

    /** 1.21.9's bounds as 1.21.8's */
    public static WrappedMinMaxBounds legacy(class_2096.class_11894 bounds) {
        if (bounds == null) {
            return null;
        }
        return new WrappedMinMaxBounds(bounds.method_74106().orElse(null), bounds.method_74107().orElse(null));
    }

    /** 1.21.8's bounds as 1.21.9's */
    public static class_2096.class_11894 degrees(WrappedMinMaxBounds bounds) {
        if (bounds == null) {
            return null;
        }
        return new class_2096.class_11894(new class_2096.class_11893<>(Optional.ofNullable(bounds.comp_1840()),
                Optional.ofNullable(bounds.comp_1841())));
    }
}
