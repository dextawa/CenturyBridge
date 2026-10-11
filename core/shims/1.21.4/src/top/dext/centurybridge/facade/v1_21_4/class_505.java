package top.dext.centurybridge.facade.v1_21_4;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class class_505 {
    public net.minecraft.class_1860<?> field_3079;
    public final java.util.List<class_506> field_3081;
    public float field_3080;

    public class_505() {
        this.field_3081 = new java.util.ArrayList<>();
    }

    public void method_2571() {
        this.field_3079 = null;
        this.field_3081.clear();
        this.field_3080 = 0.0F;
    }

    public void method_2569(net.minecraft.class_1856 ingredient, int x, int y) {
        this.field_3081.add(new class_506(ingredient, x, y));
    }

    public class_506 method_2570(int index) {
        return this.field_3081.get(index);
    }

    public int method_2572() {
        return this.field_3081.size();
    }

    public net.minecraft.class_1860<?> method_2566() {
        return this.field_3079;
    }

    public void method_2565(net.minecraft.class_1860<?> recipe) {
        this.field_3079 = recipe;
    }

    public void method_2567(
            net.minecraft.class_332 context,
            net.minecraft.class_310 client,
            int x,
            int y,
            boolean notInventory,
            float tickDelta) {
        throw new UnsupportedOperationException(
                "CenturyBridge: ghost recipe rendering depends on the incompatible Minecraft 1.20.1 client rendering API");
    }

    public class class_506 {
        private final net.minecraft.class_1856 field_3082;
        private final int field_3084;
        private final int field_3083;

        public class_506(net.minecraft.class_1856 ingredient, int x, int y) {
            this.field_3082 = java.util.Objects.requireNonNull(ingredient, "ingredient");
            this.field_3084 = x;
            this.field_3083 = y;
        }

        public net.minecraft.class_1799 method_2573() {
            throw new UnsupportedOperationException(
                    "CenturyBridge: ingredient stack selection depends on the incompatible Minecraft 1.20.1 Ingredient API");
        }

        public int method_2574() {
            return this.field_3084;
        }

        public int method_2575() {
            return this.field_3083;
        }
    }
}