package top.dext.centurybridge.facade.v1_21_4;

public abstract class class_5597<E extends net.minecraft.class_1297> extends net.minecraft.class_583 {
    private static final org.joml.Vector3f field_39195;

    public class_5597() {
        super(new net.minecraft.class_630(
                java.util.Collections.emptyList(),
                java.util.Collections.emptyMap()
        ));
    }

    public class_5597(java.util.function.Function<net.minecraft.class_2960, net.minecraft.class_1921> layerFactory) {
        super(new net.minecraft.class_630(
                java.util.Collections.emptyList(),
                java.util.Collections.emptyMap()
        ));
    }

    public void method_2828(
            net.minecraft.class_4587 matrices,
            net.minecraft.class_4588 vertices,
            int light,
            int overlay,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        throw new UnsupportedOperationException(
                "CenturyBridge: Single-part entity model rendering is incompatible with the 1.21.4 rendering API"
        );
    }

    public abstract net.minecraft.class_630 method_32008();

    public java.util.Optional<net.minecraft.class_630> method_41909(java.lang.String name) {
        net.minecraft.class_630 root = this.method_32008();
        if (root == null) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.ofNullable(method_41910(name, root));
    }

    protected void method_43781(
            net.minecraft.class_7094 animationState,
            net.minecraft.class_7184 animation,
            float animationProgress
    ) {
        throw new UnsupportedOperationException(
                "CenturyBridge: Animation application is incompatible with the 1.21.4 model API"
        );
    }

    protected void method_48741(
            net.minecraft.class_7184 animation,
            float animationProgress,
            float speedMultiplier,
            float scale,
            float timeScale
    ) {
        throw new UnsupportedOperationException(
                "CenturyBridge: Animation application is incompatible with the 1.21.4 model API"
        );
    }

    protected void method_43782(
            net.minecraft.class_7094 animationState,
            net.minecraft.class_7184 animation,
            float animationProgress,
            float speedMultiplier
    ) {
        throw new UnsupportedOperationException(
                "CenturyBridge: Animation application is incompatible with the 1.21.4 model API"
        );
    }

    protected void method_52185(net.minecraft.class_7184 animation) {
        throw new UnsupportedOperationException(
                "CenturyBridge: Static animation application is incompatible with the 1.21.4 model API"
        );
    }

    private void method_43783(
            net.minecraft.class_7184 animation,
            net.minecraft.class_7094 animationState
    ) {
        throw new UnsupportedOperationException(
                "CenturyBridge: Animation traversal is incompatible with the 1.21.4 model API"
        );
    }

    private static net.minecraft.class_630 method_41910(
            java.lang.String name,
            net.minecraft.class_630 part
    ) {
        if (method_41911(name, part)) {
            return part;
        }
        return null;
    }

    private static boolean method_41911(
            java.lang.String name,
            net.minecraft.class_630 part
    ) {
        throw new UnsupportedOperationException(
                "CenturyBridge: Model-part name traversal is incompatible with the 1.21.4 model API"
        );
    }

    static {
        field_39195 = new org.joml.Vector3f();
    }
}