package top.dext.centurybridge.facade.v1_21_4;

public class class_1866<T extends net.minecraft.class_3955> implements net.minecraft.class_1865<T> {
    private final class_7711<T> field_9046;

    public class_1866(class_7711<T> factory) {
        this.field_9046 = java.util.Objects.requireNonNull(factory, "factory");
    }

    @Override
    public com.mojang.serialization.MapCodec<T> method_53736() {
        throw new UnsupportedOperationException(
                "CenturyBridge: Special recipe codec is incompatible with Minecraft 1.21.4"
        );
    }

    @Override
    public net.minecraft.class_9139<net.minecraft.class_9129, T> method_56104() {
        throw new UnsupportedOperationException(
                "CenturyBridge: Special recipe packet codec is incompatible with Minecraft 1.21.4"
        );
    }

    public T method_45443(net.minecraft.class_2960 id, com.google.gson.JsonObject json) {
        throw new UnsupportedOperationException(
                "CenturyBridge: Special recipe JSON deserialization is incompatible with Minecraft 1.21.4"
        );
    }

    public T method_45444(net.minecraft.class_2960 id, net.minecraft.class_2540 buf) {
        throw new UnsupportedOperationException(
                "CenturyBridge: Special recipe packet deserialization is incompatible with Minecraft 1.21.4"
        );
    }

    public void method_45442(net.minecraft.class_2540 buf, T recipe) {
        throw new UnsupportedOperationException(
                "CenturyBridge: Special recipe packet serialization is incompatible with Minecraft 1.21.4"
        );
    }

    public void method_8124(net.minecraft.class_2540 buf, net.minecraft.class_1860 recipe) {
        throw new UnsupportedOperationException(
                "CenturyBridge: Legacy recipe packet serialization is incompatible with Minecraft 1.21.4"
        );
    }

    public net.minecraft.class_1860 method_8122(
            net.minecraft.class_2960 id,
            net.minecraft.class_2540 buf
    ) {
        throw new UnsupportedOperationException(
                "CenturyBridge: Legacy recipe packet deserialization is incompatible with Minecraft 1.21.4"
        );
    }

    public net.minecraft.class_1860 method_8121(
            net.minecraft.class_2960 id,
            com.google.gson.JsonObject json
    ) {
        throw new UnsupportedOperationException(
                "CenturyBridge: Legacy recipe JSON deserialization is incompatible with Minecraft 1.21.4"
        );
    }

    @FunctionalInterface
    public interface class_7711<T extends net.minecraft.class_3955> {
        T create();
    }
}