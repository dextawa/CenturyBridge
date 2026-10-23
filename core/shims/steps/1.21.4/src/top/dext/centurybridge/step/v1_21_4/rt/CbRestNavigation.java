package top.dext.centurybridge.step.v1_21_4.rt;

import net.minecraft.class_1407;
import net.minecraft.class_1409;

/**
 * 1.21.3's door accessors of FlyingPathNavigation (class_1407) and GroundPathNavigation (class_1409), gone
 * in 1.21.4. 1.21.3's bodies, over the node evaluator 1.21.4 still has: canPassDoors() and canOpenDoors()
 * both answered nodeEvaluator.canPassDoors() (1.21.3 bytecode: canOpenDoors reads canPassDoors too), and
 * setCanPassDoors(b) set nodeEvaluator.setCanPassDoors(b).
 * <p>
 * A mod call answers what 1.21.3's virtual call did: a mod subclass's own override when it has one (the
 * interfaces below, given to such a class by adapterHooks), otherwise 1.21.3's body; a mod's super call
 * is 1.21.3's body ({@code super*}). 1.21.3's vanilla consulted GroundPathNavigation.canOpenDoors in
 * DoorInteractGoal.canUse and MoveThroughVillageGoal.canUse; 1.21.4 dropped both reads, so a mod
 * navigation's own override is consulted there again by CbRestDoorGoalsMixin / CbRestVillageGoalMixin.
 */
public final class CbRestNavigation {

    private CbRestNavigation() {
    }

    public interface FlyingCanPassDoors { boolean method_35128(); }
    public interface FlyingSetCanPassDoors { void method_6331(boolean b); }
    public interface FlyingCanOpenDoors { boolean method_35129(); }
    public interface GroundCanPassDoors { boolean method_35140(); }
    public interface GroundSetCanPassDoors { void method_35139(boolean b); }
    public interface GroundCanOpenDoors { boolean method_6366(); }

    // ---- FlyingPathNavigation ----

    public static boolean method_35128(class_1407 nav) {
        return (Object) nav instanceof FlyingCanPassDoors a ? a.method_35128() : superCanPassDoors(nav);
    }

    public static void method_6331(class_1407 nav, boolean b) {
        if ((Object) nav instanceof FlyingSetCanPassDoors a) {
            a.method_6331(b);
        } else {
            superSetCanPassDoors(nav, b);
        }
    }

    public static boolean method_35129(class_1407 nav) {
        return (Object) nav instanceof FlyingCanOpenDoors a ? a.method_35129() : superCanOpenDoors(nav);
    }

    public static boolean superCanPassDoors(class_1407 nav) {
        return nav.method_6342().method_23();
    }

    public static void superSetCanPassDoors(class_1407 nav, boolean b) {
        nav.method_6342().method_15(b);
    }

    public static boolean superCanOpenDoors(class_1407 nav) {
        return nav.method_6342().method_23();
    }

    // ---- GroundPathNavigation ----

    public static boolean method_35140(class_1409 nav) {
        return (Object) nav instanceof GroundCanPassDoors a ? a.method_35140() : superCanPassDoors(nav);
    }

    public static void method_35139(class_1409 nav, boolean b) {
        if ((Object) nav instanceof GroundSetCanPassDoors a) {
            a.method_35139(b);
        } else {
            superSetCanPassDoors(nav, b);
        }
    }

    public static boolean method_6366(class_1409 nav) {
        return (Object) nav instanceof GroundCanOpenDoors a ? a.method_6366() : superCanOpenDoors(nav);
    }

    public static boolean superCanPassDoors(class_1409 nav) {
        return nav.method_6342().method_23();
    }

    public static void superSetCanPassDoors(class_1409 nav, boolean b) {
        nav.method_6342().method_15(b);
    }

    public static boolean superCanOpenDoors(class_1409 nav) {
        return nav.method_6342().method_23();
    }
}
