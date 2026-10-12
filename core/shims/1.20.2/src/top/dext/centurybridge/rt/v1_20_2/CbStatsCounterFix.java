package top.dext.centurybridge.rt.v1_20_2;

import java.util.ArrayDeque;
import java.util.function.Function;

/**
 * StatsCounterFix.upgradeItem(name) / upgradeBlock(name): protected instance methods in 1.20.1, which
 * the fix's stats rule called on the fix itself (this.upgradeBlock / this.upgradeItem in its typed
 * function), so a subclass could change how legacy stat keys upgrade. 1.20.2 made both private static
 * and calls them from static lambdas: a 1.20.1 subclass's call no longer links and its override is
 * never asked.
 * <ul>
 * <li>calls (this.upgradeItem(name) in a subclass that inherits it) run the override when the receiver
 *     has one, else 1.20.1's body -- which is 1.20.2's too: ItemStackTheFlatteningFix.updateItem(name, 0)
 *     and BlockStateData.upgradeBlock(name) ({@link #method_5172}, {@link #method_5173});</li>
 * <li>a subclass's super call runs that body ({@link #superUpgradeItem}, {@link #superUpgradeBlock});</li>
 * <li>a subclass overriding either gets {@link LegacyItem} / {@link LegacyBlock} (adapterHooks), and the
 *     stats rule it builds applies with the fix recorded ({@link #applying}): 1.20.2's private static
 *     upgrade methods answer through the override while it runs (CbStatsCounterFixLegacy). Only the
 *     stats rule: 1.20.2's second rule (ObjectiveStatFix, objective criteria) is 1.20.2's own, and no
 *     1.20.1 override was ever asked about objectives.</li>
 * </ul>
 */
public final class CbStatsCounterFix {

    /** a 1.20.1 subclass's own upgradeItem(name) */
    public interface LegacyItem {
        String method_5172(String name);
    }

    /** a 1.20.1 subclass's own upgradeBlock(name) */
    public interface LegacyBlock {
        String method_5173(String name);
    }

    /** the fixes whose stats rule is applying on this thread, innermost first */
    private static final ThreadLocal<ArrayDeque<Object>> APPLYING = ThreadLocal.withInitial(ArrayDeque::new);

    /** fix.upgradeItem(name) */
    public static String method_5172(net.minecraft.class_1214 fix, String name) {
        return fix instanceof LegacyItem own ? own.method_5172(name) : superUpgradeItem(fix, name);
    }

    /** fix.upgradeBlock(name) */
    public static String method_5173(net.minecraft.class_1214 fix, String name) {
        return fix instanceof LegacyBlock own ? own.method_5173(name) : superUpgradeBlock(fix, name);
    }

    /** super.upgradeItem(name): 1.20.1's body, ItemStackTheFlatteningFix.updateItem(name, 0) */
    public static String superUpgradeItem(net.minecraft.class_1214 fix, String name) {
        return net.minecraft.class_1188.method_5042(name, 0);
    }

    /** super.upgradeBlock(name): 1.20.1's body, BlockStateData.upgradeBlock(name) */
    public static String superUpgradeBlock(net.minecraft.class_1214 fix, String name) {
        return net.minecraft.class_3580.method_15600(name);
    }

    /** the stats rule's typed function, run with the fix recorded when the fix overrides an upgrade */
    public static <A, B> Function<A, B> applying(Object fix, Function<A, B> function) {
        if (!(fix instanceof LegacyItem) && !(fix instanceof LegacyBlock)) {
            return function;
        }
        return value -> {
            ArrayDeque<Object> stack = APPLYING.get();
            stack.push(fix);
            try {
                return function.apply(value);
            } finally {
                stack.pop();
            }
        };
    }

    /** the override of upgradeItem of the fix whose stats rule is applying, or null */
    public static LegacyItem itemOverride() {
        return APPLYING.get().peek() instanceof LegacyItem own ? own : null;
    }

    /** the override of upgradeBlock of the fix whose stats rule is applying, or null */
    public static LegacyBlock blockOverride() {
        return APPLYING.get().peek() instanceof LegacyBlock own ? own : null;
    }

    private CbStatsCounterFix() {
    }
}
