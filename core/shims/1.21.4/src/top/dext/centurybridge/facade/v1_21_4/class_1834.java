package top.dext.centurybridge.facade.v1_21_4;

public enum class_1834 {
    field_8922(0, 59, 2.0F, 0.0F, 15, class_1834::method_24361),
    field_8927(1, 131, 4.0F, 1.0F, 5, class_1834::method_8033),
    field_8923(2, 250, 6.0F, 2.0F, 14, class_1834::method_8032),
    field_8930(3, 1561, 8.0F, 3.0F, 10, class_1834::method_8030),
    field_8929(0, 32, 12.0F, 0.0F, 22, class_1834::method_8031),
    field_22033(4, 2031, 9.0F, 4.0F, 15, class_1834::method_8029);

    public final int field_8925;
    public final int field_8924;
    public final float field_8932;
    public final float field_8931;
    public final int field_8933;
    public final java.util.function.Supplier<net.minecraft.class_1856> field_8928;
    private static final class_1834[] field_8926 = method_36684();

    private class_1834(
            int miningLevel,
            int itemDurability,
            float miningSpeed,
            float attackDamage,
            int enchantability,
            java.util.function.Supplier<net.minecraft.class_1856> repairIngredient
    ) {
        this.field_8925 = miningLevel;
        this.field_8924 = itemDurability;
        this.field_8932 = miningSpeed;
        this.field_8931 = attackDamage;
        this.field_8933 = enchantability;
        this.field_8928 = repairIngredient;
    }

    public int method_8025() {
        return this.field_8924;
    }

    public float method_8027() {
        return this.field_8932;
    }

    public float method_8028() {
        return this.field_8931;
    }

    public int method_8024() {
        return this.field_8925;
    }

    public int method_8026() {
        return this.field_8933;
    }

    public net.minecraft.class_1856 method_8023() {
        return this.field_8928.get();
    }

    private static net.minecraft.class_1856 method_24361() {
        throw new UnsupportedOperationException();
    }

    private static net.minecraft.class_1856 method_8033() {
        throw new UnsupportedOperationException();
    }

    private static net.minecraft.class_1856 method_8032() {
        throw new UnsupportedOperationException();
    }

    private static net.minecraft.class_1856 method_8030() {
        throw new UnsupportedOperationException();
    }

    private static net.minecraft.class_1856 method_8031() {
        throw new UnsupportedOperationException();
    }

    private static net.minecraft.class_1856 method_8029() {
        throw new UnsupportedOperationException();
    }

    private static class_1834[] method_36684() {
        return values();
    }
}