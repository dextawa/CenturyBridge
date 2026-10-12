package top.dext.centurybridge.step.v1_21.facade;

/** 1.20.6's DigDurabilityEnchantment (net/minecraft/class_1885, unbreaking); its constructor was protected */
public class class_1885 extends class_1887 {

    public class_1885(class_1887$class_9427 definition) {
        super(definition);
    }

    /** shouldIgnoreDurabilityDrop(ItemStack, int, RandomSource): armor ignores 60% of the chance */
    public static boolean method_8176(net.minecraft.class_1799 stack, int level, net.minecraft.class_5819 random) {
        if (stack.method_7909() instanceof net.minecraft.class_1738 && random.method_43057() < 0.6F) {
            return false;
        }
        return random.method_43048(level + 1) > 0;
    }
}
