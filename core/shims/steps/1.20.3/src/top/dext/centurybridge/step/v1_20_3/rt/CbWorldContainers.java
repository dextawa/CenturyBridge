package top.dext.centurybridge.step.v1_20_3.rt;

import net.minecraft.class_1799;
import net.minecraft.class_2586;
import net.minecraft.class_8181;

/**
 * ContainerSingleItem: 1.20.2's had getFirstItem / setFirstItem / removeFirstItem default methods over the
 * container's slot 0, its implementations supplying the Container methods; 1.20.3's asks for getTheItem /
 * setTheItem / splitTheItem / getContainerBlockEntity and builds the Container methods on them. Calls, super
 * calls and the implementations 1.20.3 now demands of a 1.20.2 class, each the 1.20.2 way.
 */
public final class CbWorldContainers {

    // calls: dispatch to the 1.20.3 method, which a 1.20.2 class's override of the old one answers (override hook)
    public static class_1799 getFirstItem(class_8181 c) {
        return c.method_54079();
    }

    public static void setFirstItem(class_8181 c, class_1799 stack) {
        c.method_54077(stack);
    }

    public static class_1799 removeFirstItem(class_8181 c) {
        return c.method_54099();
    }

    // super calls: 1.20.2's default bodies
    public static class_1799 superGetFirstItem(class_8181 c) {
        return c.method_5438(0);
    }

    public static void superSetFirstItem(class_8181 c, class_1799 stack) {
        c.method_5447(0, stack);
    }

    public static class_1799 superRemoveFirstItem(class_8181 c) {
        return c.method_5441(0);
    }

    // what 1.20.3 demands of a 1.20.2 implementation that does not override the old methods: 1.20.2's defaults
    public static class_1799 getTheItem(class_8181 c) {
        return c.method_5438(0);
    }

    public static void setTheItem(class_8181 c, class_1799 stack) {
        c.method_5447(0, stack);
    }

    /** 1.20.3's split of the one stack: the container's own removeItem on slot 0 */
    public static class_1799 splitTheItem(class_8181 c, int amount) {
        return c.method_5434(0, amount);
    }

    /**
     * 1.20.3 reads it only in ContainerSingleItem's default stillValid, which a 1.20.2 implementation overrides
     * (Container.stillValid was abstract in 1.20.2). Every 1.20.3 implementation is the block entity itself;
     * a 1.20.2 one that is not a block entity has none to report.
     */
    public static class_2586 getContainerBlockEntity(class_8181 c) {
        if (c instanceof class_2586 be) {
            return be;
        }
        throw new UnsupportedOperationException("ContainerSingleItem.getContainerBlockEntity(): " + c.getClass().getName()
            + " is a 1.20.2 single-item container that is not a block entity");
    }

    private CbWorldContainers() {
    }
}
