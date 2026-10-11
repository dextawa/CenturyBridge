package top.dext.centurybridge.facade.v1_21_4;

public class class_1271<T> {
    public final net.minecraft.class_1269 field_5815;
    public final T field_5816;

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

    /** consume(value): CONSUME survives as a Success constant */
    public static <T> class_1271<T> method_22431(T value) {
        return new class_1271<>(net.minecraft.class_1269.field_21466, value);
    }

    /** the 1.21.2+ shape of this result: a Success carries the transformed held item the value meant */
    public net.minecraft.class_1269 cbToVanilla() {
        if (field_5815 instanceof net.minecraft.class_1269.class_9860 success && field_5816 instanceof net.minecraft.class_1799 stack) {
            return success.method_61393(stack);
        }
        return field_5815;
    }

    /** wrap a 1.21.2+ result back into the old pair (the value is the hand stack unless Success carried a new one) */
    public static class_1271<net.minecraft.class_1799> cbFrom(net.minecraft.class_1269 result, net.minecraft.class_1799 held) {
        if (result instanceof net.minecraft.class_1269.class_9860 success && success.method_61396() != null) {
            return new class_1271<>(result, success.method_61396());
        }
        return new class_1271<>(result, held);
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