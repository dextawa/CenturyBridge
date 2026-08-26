package top.dext.centurybridge.facade.v1_21_4;

public class class_1772 extends net.minecraft.class_1792 {
    public static final java.lang.String field_30874 = "StoredEnchantments";

    public class_1772(net.minecraft.class_1792.class_1793 settings) {
        super(settings);
    }

    public boolean method_7886(net.minecraft.class_1799 stack) {
        throw new UnsupportedOperationException("CenturyBridge: enchanted-book glint checks require the incompatible 1.20.1 item-stack API");
    }

    public boolean method_7870(net.minecraft.class_1799 stack) {
        throw new UnsupportedOperationException("CenturyBridge: enchanted-book enchantment checks require the incompatible 1.20.1 item-stack API");
    }

    public static net.minecraft.class_2499 method_7806(net.minecraft.class_1799 stack) {
        throw new UnsupportedOperationException("CenturyBridge: StoredEnchantments NBT access was replaced by the 1.21.4 data-component API");
    }

    public void method_7851(
            net.minecraft.class_1799 stack,
            net.minecraft.class_1937 world,
            java.util.List<net.minecraft.class_2561> tooltip,
            net.minecraft.class_1836 context
    ) {
        throw new UnsupportedOperationException("CenturyBridge: enchanted-book tooltip generation requires the incompatible 1.20.1 enchantment API");
    }

    public static void method_7807(
            net.minecraft.class_1799 stack,
            net.minecraft.class_1889 enchantment
    ) {
        throw new UnsupportedOperationException("CenturyBridge: adding stored enchantments requires the incompatible 1.20.1 NBT enchantment API");
    }

    public static net.minecraft.class_1799 method_7808(net.minecraft.class_1889 enchantment) {
        throw new UnsupportedOperationException("CenturyBridge: enchanted-book stack creation requires the incompatible 1.20.1 enchantment API");
    }
}