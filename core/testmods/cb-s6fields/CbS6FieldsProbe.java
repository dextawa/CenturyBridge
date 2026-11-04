package cb.s6fields;

/** 1.21.5-shaped probe whose converted bytecode must show the s6fields call/field rewrites. */
public final class CbS6FieldsProbe {
    public static java.util.Optional<net.minecraft.class_1296> read(net.minecraft.class_4095<?> brain) {
        return brain.method_18904(net.minecraft.class_4140.field_25359);
    }

    public static java.util.Optional<net.minecraft.class_1296> readRegistered(net.minecraft.class_4095<?> brain) {
        return brain.method_46873(net.minecraft.class_4140.field_25359);
    }

    public static void write(net.minecraft.class_4095<?> brain, net.minecraft.class_1296 value) {
        brain.method_18878(net.minecraft.class_4140.field_25359, value);
    }

    public static void writeExpiring(net.minecraft.class_4095<?> brain, net.minecraft.class_1296 value, long expiry) {
        brain.method_24525(net.minecraft.class_4140.field_25359, value, expiry);
    }

    public static void writeOptional(net.minecraft.class_4095<?> brain,
            java.util.Optional<? extends net.minecraft.class_1296> value) {
        brain.method_18879(net.minecraft.class_4140.field_25359, value);
    }

    public static boolean same(net.minecraft.class_4095<?> brain, net.minecraft.class_1296 value) {
        return brain.method_29519(net.minecraft.class_4140.field_25359, value);
    }

    public static java.util.Optional<? extends net.minecraft.class_4831<?>> rawRead(
            net.minecraft.class_4095<?> brain) {
        return brain.method_35058().get(net.minecraft.class_4140.field_25359);
    }

    public static Object rawValue(net.minecraft.class_4095<?> brain) {
        java.util.Optional<? extends net.minecraft.class_4831<?>> value = rawRead(brain);
        return value.isPresent() ? value.get().method_24637() : null;
    }

    public static java.util.Map<net.minecraft.class_1921, net.minecraft.class_9801> layers(
            net.minecraft.class_9810.class_9811 results) {
        return results.field_52168;
    }
}
