package top.dext.centurybridge.facade.v1_21_4;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public abstract class class_8184<E extends net.minecraft.class_1297> {
    private final float field_42875;
    private final float field_42876;

    public class_8184(float childScale, float childTranslation) {
        this.field_42875 = childScale;
        this.field_42876 = childTranslation;
    }

    public class_8184(
            float childScale,
            float childTranslation,
            java.util.function.Function<net.minecraft.class_2960, net.minecraft.class_1921> layerFactory) {
        this.field_42875 = childScale;
        this.field_42876 = childTranslation;
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
        throw new UnsupportedOperationException(
                "CenturyBridge: legacy floating-point model rendering is incompatible with the 1.21.4 rendering API");
    }
}