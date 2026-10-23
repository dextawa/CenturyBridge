package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.function.DoubleSupplier;
import java.util.function.Predicate;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_3959;

/**
 * LivingEntity.isLookingAtMe and hasLineOfSight in their 1.21.3 shapes. 1.21.3 took the target's eye heights
 * as DoubleSuppliers read lazily (isLookingAtMe: one per candidate height, in order, stopping at the first
 * that sees; hasLineOfSight: once, after the same-level test) and isLookingAtMe also took a target predicate
 * tested first. 1.21.4 takes plain doubles and no predicate. The 1.21.3 shapes keep 1.21.3's order: the
 * predicate first, then 1.21.4's method asked one height at a time, each supplier read only when 1.21.3 read
 * it. A 1.21.3 override is reached from 1.21.4's shape with the doubles as suppliers and, for
 * isLookingAtMe, a predicate passing every target: 1.21.4's callers test what 1.21.3's predicate tested
 * before they call.
 */
public final class CbRestLooking {

    private CbRestLooking() {
    }

    // ---- isLookingAtMe ----

    /** a mod's call of 1.21.3's isLookingAtMe(target, tolerance, scaleByDistance, visual, predicate, yValues) */
    public static boolean method_64619(class_1309 self, class_1309 target, double tolerance, boolean scale, boolean visual,
            Predicate<class_1309> predicate, DoubleSupplier[] ys) {
        if (!predicate.test(target)) {
            return false;
        }
        for (DoubleSupplier y : ys) {
            if (self.method_64619(target, tolerance, scale, visual, new double[] {y.getAsDouble()})) {
                return true;
            }
        }
        return false;
    }

    public interface LegacyLooking {
        boolean method_64619(class_1309 target, double tolerance, boolean scale, boolean visual, Predicate<class_1309> predicate,
            DoubleSupplier[] ys);
    }

    /** 1.21.4's isLookingAtMe(target, tolerance, scale, visual, yValues) of a class overriding 1.21.3's shape */
    public static boolean isLookingAtMe(LegacyLooking self, class_1309 target, double tolerance, boolean scale, boolean visual, double[] ys) {
        return self.method_64619(target, tolerance, scale, visual, t -> true, suppliers(ys));
    }

    public interface SuperLooking {
        boolean cb$super$method_64619(class_1309 target, double tolerance, boolean scale, boolean visual, double[] ys);
    }

    /** a mod's super.isLookingAtMe(.., predicate, yValues): 1.21.3's body over super's 1.21.4 implementation */
    public static boolean superIsLookingAtMe(SuperLooking self, class_1309 target, double tolerance, boolean scale, boolean visual,
            Predicate<class_1309> predicate, DoubleSupplier[] ys) {
        if (!predicate.test(target)) {
            return false;
        }
        for (DoubleSupplier y : ys) {
            if (self.cb$super$method_64619(target, tolerance, scale, visual, new double[] {y.getAsDouble()})) {
                return true;
            }
        }
        return false;
    }

    private static DoubleSupplier[] suppliers(double[] ys) {
        DoubleSupplier[] out = new DoubleSupplier[ys.length];
        for (int i = 0; i < ys.length; i++) {
            double y = ys[i];
            out[i] = () -> y;
        }
        return out;
    }

    // ---- hasLineOfSight ----

    /** a mod's call of 1.21.3's hasLineOfSight(target, block, fluid, eyeY) */
    public static boolean method_64618(class_1309 self, class_1297 target, class_3959.class_3960 block, class_3959.class_242 fluid,
            DoubleSupplier y) {
        if (target.method_37908() != self.method_37908()) {
            return false;
        }
        return self.method_64618(target, block, fluid, y.getAsDouble());
    }

    public interface LegacySight {
        boolean method_64618(class_1297 target, class_3959.class_3960 block, class_3959.class_242 fluid, DoubleSupplier y);
    }

    /** 1.21.4's hasLineOfSight(target, block, fluid, eyeY) of a class overriding 1.21.3's shape */
    public static boolean hasLineOfSight(LegacySight self, class_1297 target, class_3959.class_3960 block, class_3959.class_242 fluid, double y) {
        return self.method_64618(target, block, fluid, () -> y);
    }

    public interface SuperSight {
        boolean cb$super$method_64618(class_1297 target, class_3959.class_3960 block, class_3959.class_242 fluid, double y);
    }

    /** a mod's super.hasLineOfSight(.., eyeY): super's 1.21.4 implementation, the supplier read after the level test */
    public static boolean superHasLineOfSight(SuperSight self, class_1297 target, class_3959.class_3960 block, class_3959.class_242 fluid,
            DoubleSupplier y) {
        if (target.method_37908() != ((class_1309) self).method_37908()) {
            return false;
        }
        return self.cb$super$method_64618(target, block, fluid, y.getAsDouble());
    }
}
