package top.dext.centurybridge.facade.v1_21_4;

public class class_1271<T> {
    private final net.minecraft.class_1269 field_5815;
    private final T field_5816;

    public class_1271(net.minecraft.class_1269 field_5815, T field_5816) {
        this.field_5815 = field_5815;
        this.field_5816 = field_5816;
    }

    public net.minecraft.class_1269 method_5467() {
        return this.field_5815;
    }

    public T method_5466() {
        return this.field_5816;
    }

    public static <T> class_1271<T> method_22427(T value) {
        return new class_1271<>(net.minecraft.class_1269.field_5812, value);
    }

    public static <T> class_1271<T> method_22428(T value) {
        return new class_1271<>(net.minecraft.class_1269.field_5814, value);
    }

    public static <T> class_1271<T> method_22430(T value) {
        return new class_1271<>(net.minecraft.class_1269.field_5811, value);
    }

    public static <T> class_1271<T> method_22431(T value) {
        throw new UnsupportedOperationException();
    }

    public static <T> class_1271<T> method_29237(T value, boolean swingHand) {
        return new class_1271<>(
            swingHand
                ? net.minecraft.class_1269.field_5812
                : net.minecraft.class_1269.field_5814,
            value
        );
    }
}