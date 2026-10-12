package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's DispenserBlock.getDispenseMethod(stack) on 1.20.5's getDispenseMethod(level, stack). 1.20.4
 * answered from the item alone: the behavior registered for it (DispenserBlock), the plain drop
 * (DropperBlock); 1.20.5 also hands the level, whose enabled features it checks first. The old shape
 * keeps 1.20.4's bodies -- a mod's own call (this.getDispenseMethod(stack) in its dispenseFrom, where
 * no level is passed) and its super call alike; 1.20.5 asking a dispenser class that overrides the old
 * shape reaches the override with the stack.
 */
public final class CbBlockDispense {

    /** 1.20.4's getDispenseMethod(stack) a mod dispenser class overrides */
    public interface Legacy {
        net.minecraft.class_2357 method_10011(net.minecraft.class_1799 stack);
    }

    /** getDispenseMethod(stack) called on any dispenser: the override, else the 1.20.4 body of its vanilla class */
    public static net.minecraft.class_2357 method_10011(net.minecraft.class_2315 self, net.minecraft.class_1799 stack) {
        if (self instanceof Legacy legacy) {
            return legacy.method_10011(stack);
        }
        return self instanceof net.minecraft.class_2325 dropper ? superDropperDispenseMethod(dropper, stack) : superDispenseMethod(self, stack);
    }

    /** getDispenseMethod(stack) called on a dropper (DropperBlock declared its own in 1.20.4) */
    public static net.minecraft.class_2357 method_10011(net.minecraft.class_2325 self, net.minecraft.class_1799 stack) {
        return self instanceof Legacy legacy ? legacy.method_10011(stack) : superDropperDispenseMethod(self, stack);
    }

    /** 1.20.4's DispenserBlock.getDispenseMethod body: the behavior registered for the item (the registry's default without one) */
    public static net.minecraft.class_2357 superDispenseMethod(net.minecraft.class_2315 self, net.minecraft.class_1799 stack) {
        return net.minecraft.class_2315.field_10919.get(stack.method_7909());
    }

    /** 1.20.4's DropperBlock.getDispenseMethod body: the plain drop */
    public static net.minecraft.class_2357 superDropperDispenseMethod(net.minecraft.class_2325 self, net.minecraft.class_1799 stack) {
        return top.dext.centurybridge.step.v1_20_5.mixin.CbBlockDropperAccessor.cb$dispenseBehaviour();
    }

    /** 1.20.5's getDispenseMethod(level, stack) asked of a dispenser overriding 1.20.4's */
    public static net.minecraft.class_2357 getDispenseMethod(Legacy self, net.minecraft.class_1937 level, net.minecraft.class_1799 stack) {
        return self.method_10011(stack);
    }

    private CbBlockDispense() {
    }
}
