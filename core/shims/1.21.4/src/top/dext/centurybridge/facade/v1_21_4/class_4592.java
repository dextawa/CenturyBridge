package top.dext.centurybridge.facade.v1_21_4;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public abstract class class_4592<E extends net.minecraft.class_1297> extends net.minecraft.class_583 {
    public final boolean field_20915;
    public final float field_20916;
    public final float field_20917;
    public final float field_20918;
    public final float field_20919;
    public final float field_20920;

    public class_4592(boolean field_20915, float field_20916, float field_20917) {
        this(field_20915, field_20916, field_20917, 2.0F, 2.0F, 24.0F);
    }

    public class_4592(
            boolean field_20915,
            float field_20916,
            float field_20917,
            float field_20918,
            float field_20919,
            float field_20920) {
        this(
                net.minecraft.class_1921::method_23578,
                field_20915,
                field_20916,
                field_20917,
                field_20918,
                field_20919,
                field_20920
        );
    }

    public class_4592(
            java.util.function.Function<net.minecraft.class_2960, net.minecraft.class_1921> layerFactory,
            boolean field_20915,
            float field_20916,
            float field_20917,
            float field_20918,
            float field_20919,
            float field_20920) {
        super((net.minecraft.class_630) null, layerFactory);
        this.field_20915 = field_20915;
        this.field_20916 = field_20916;
        this.field_20917 = field_20917;
        this.field_20918 = field_20918;
        this.field_20919 = field_20919;
        this.field_20920 = field_20920;
    }

    public class_4592() {
        this(false, 5.0F, 2.0F);
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
                "CenturyBridge: legacy entity-model rendering is incompatible with the 1.21.4 render-state model API"
        );
    }

    public abstract java.lang.Iterable<net.minecraft.class_630> method_22946();

    public abstract java.lang.Iterable<net.minecraft.class_630> method_22948();

    private static void method_22947(
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
                "CenturyBridge: legacy child-head model-part rendering is incompatible with 1.21.4"
        );
    }

    private static void method_22949(
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
                "CenturyBridge: legacy child-body model-part rendering is incompatible with 1.21.4"
        );
    }

    private static void method_22950(
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
                "CenturyBridge: legacy adult-head model-part rendering is incompatible with 1.21.4"
        );
    }

    private static void method_22951(
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
                "CenturyBridge: legacy adult-body model-part rendering is incompatible with 1.21.4"
        );
    }
}