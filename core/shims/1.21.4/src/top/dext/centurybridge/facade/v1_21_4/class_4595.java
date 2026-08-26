package top.dext.centurybridge.facade.v1_21_4;

public abstract class class_4595<E extends net.minecraft.class_1297> extends net.minecraft.class_583 {
    public class_4595() {
        super(method_60000());
    }

    public class_4595(java.util.function.Function<net.minecraft.class_2960, net.minecraft.class_1921> function) {
        super(method_60000());
    }

    public void method_2828(
            net.minecraft.class_4587 matrices,
            net.minecraft.class_4588 vertices,
            int light,
            int overlay,
            float red,
            float green,
            float blue,
            float alpha) {
        for (net.minecraft.class_630 part : this.method_22960()) {
            method_22961(matrices, vertices, light, overlay, red, green, blue, alpha, part);
        }
    }

    public abstract java.lang.Iterable<net.minecraft.class_630> method_22960();

    private static net.minecraft.class_630 method_60000() {
        throw new UnsupportedOperationException(
                "CenturyBridge: a model root is required by Minecraft 1.21.4");
    }

    private static void method_22961(
            net.minecraft.class_4587 matrices,
            net.minecraft.class_4588 vertices,
            int light,
            int overlay,
            float red,
            float green,
            float blue,
            float alpha,
            net.minecraft.class_630 part) {
        throw new UnsupportedOperationException(
                "CenturyBridge: class_630 rendering with legacy RGBA parameters is incompatible with Minecraft 1.21.4");
    }
}