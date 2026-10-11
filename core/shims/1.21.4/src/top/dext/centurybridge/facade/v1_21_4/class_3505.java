package top.dext.centurybridge.facade.v1_21_4;

public class class_3505 implements net.minecraft.class_3302 {
    public static final java.util.Map<
            net.minecraft.class_5321<? extends net.minecraft.class_2378<?>>,
            java.lang.String> field_36395 = java.util.Collections.emptyMap();

    public final net.minecraft.class_5455 field_28312;
    public java.util.List<class_6863<?>> field_36396 = java.util.Collections.emptyList();

    public class_3505(net.minecraft.class_5455 arg) {
        this.field_28312 = java.util.Objects.requireNonNull(arg, "registryManager");
    }

    public java.util.List<class_6863<?>> method_40096() {
        return this.field_36396;
    }

    public static java.lang.String method_40099(
            net.minecraft.class_5321<? extends net.minecraft.class_2378<?>> arg) {
        return field_36395.get(arg);
    }

    @Override
    public java.util.concurrent.CompletableFuture<java.lang.Void> method_25931(
            net.minecraft.class_3302.class_4045 arg,
            net.minecraft.class_3300 arg2,
            java.util.concurrent.Executor arg3,
            java.util.concurrent.Executor arg4) {
        throw new UnsupportedOperationException(
                "CenturyBridge: registry tag reload depends on the deleted 1.20.1 tag loading API");
    }

    public <T> java.util.concurrent.CompletableFuture<class_6863<T>> method_33178(
            net.minecraft.class_3300 arg,
            java.util.concurrent.Executor executor,
            net.minecraft.class_5455.class_6892<T> registry) {
        throw new UnsupportedOperationException(
                "CenturyBridge: registry tag loading is unavailable in 1.21.4");
    }

    private static class_6863<?> method_33181(
            net.minecraft.class_5321<?> registryKey,
            net.minecraft.class_3503 tagGroupLoader,
            net.minecraft.class_3300 resourceManager) {
        throw new UnsupportedOperationException(
                "CenturyBridge: registry tag loading is unavailable in 1.21.4");
    }

    private static java.util.Optional<?> method_40097(
            net.minecraft.class_2378<?> registry,
            net.minecraft.class_5321<?> registryKey,
            net.minecraft.class_2960 id) {
        throw new UnsupportedOperationException(
                "CenturyBridge: registry lookup is unavailable in 1.21.4");
    }

    private void method_40098(java.util.List<?> values, java.lang.Void ignored) {
        throw new UnsupportedOperationException(
                "CenturyBridge: registry tag application is unavailable in 1.21.4");
    }

    private static java.util.concurrent.CompletableFuture<?>[] method_33177(int size) {
        return new java.util.concurrent.CompletableFuture<?>[size];
    }

    private java.util.concurrent.CompletableFuture<?> method_40100(
            net.minecraft.class_3300 resourceManager,
            java.util.concurrent.Executor executor,
            net.minecraft.class_5455.class_6892<?> registry) {
        throw new UnsupportedOperationException(
                "CenturyBridge: registry tag loading is unavailable in 1.21.4");
    }

    public static final class class_6863<T> {
        private final net.minecraft.class_5321<? extends net.minecraft.class_2378<T>> field_36400;
        private final java.util.Map<net.minecraft.class_2960, java.util.List<?>> field_36401;

        public class_6863(
                net.minecraft.class_5321<? extends net.minecraft.class_2378<T>> registryKey,
                java.util.Map<net.minecraft.class_2960, java.util.List<?>> tags) {
            this.field_36400 = registryKey;
            this.field_36401 = tags;
        }

        public net.minecraft.class_5321<? extends net.minecraft.class_2378<T>> comp_1100() {
            return this.field_36400;
        }

        public java.util.Map<net.minecraft.class_2960, java.util.List<?>> comp_1101() {
            return this.field_36401;
        }
    }
}