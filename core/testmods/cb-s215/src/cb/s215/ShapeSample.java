package cb.s215;

import java.util.ArrayList;
import java.util.List;

/**
 * The shape sample: VoxelShape statics 1.21.5 removed or narrowed and the 1.21.5 step rebuilds (rt/CbShapes), read the
 * way a 1.21.4 mod reads them (public ones directly, protected ones from a subclass). Each line lists the shape's
 * boxes (VoxelShape.toAabbs); native/ShapeDump prints the same lines from native 1.21.4 for comparison.
 */
final class ShapeSample {
    private ShapeSample() {
    }

    static String boxes(net.minecraft.class_265 s) {
        List<String> out = new ArrayList<>();
        for (net.minecraft.class_238 b : s.method_1090()) {
            out.add(b.field_1323 + "," + b.field_1322 + "," + b.field_1321 + "," + b.field_1320 + "," + b.field_1325 + "," + b.field_1324);
        }
        return String.join(";", out);
    }

    static List<String> lines() {
        List<String> l = new ArrayList<>();
        l.add("net/minecraft/class_3713.field_16338 " + boxes(net.minecraft.class_3713.field_16338));
        l.add("net/minecraft/class_3713.field_16356 " + boxes(net.minecraft.class_3713.field_16356));
        l.add("net/minecraft/class_3713.field_16361 " + boxes(net.minecraft.class_3713.field_16361));
        l.add("net/minecraft/class_3713.field_16370 " + boxes(net.minecraft.class_3713.field_16370));
        l.add("net/minecraft/class_3715.field_17371 " + boxes(net.minecraft.class_3715.field_17371));
        l.add("net/minecraft/class_3715.field_17372 " + boxes(net.minecraft.class_3715.field_17372));
        l.add("net/minecraft/class_3715.field_17369 " + boxes(net.minecraft.class_3715.field_17369));
        l.add("net/minecraft/class_3715.field_17370 " + boxes(net.minecraft.class_3715.field_17370));
        l.add("net/minecraft/class_7715.field_40322 " + boxes(net.minecraft.class_7715.field_40322));
        l.add("net/minecraft/class_7715.field_40323 " + boxes(net.minecraft.class_7715.field_40323));
        l.add("net/minecraft/class_7715.field_40320 " + boxes(net.minecraft.class_7715.field_40320));
        l.add("net/minecraft/class_7715.field_40321 " + boxes(net.minecraft.class_7715.field_40321));
        l.add("net/minecraft/class_4777.field_22172 " + boxes(net.minecraft.class_4777.field_22172));
        l.add("net/minecraft/class_4950.field_23325 " + boxes(net.minecraft.class_4950.field_23325));
        l.add("net/minecraft/class_4951.field_23326 " + boxes(net.minecraft.class_4951.field_23326));
        l.add("net/minecraft/class_3749.field_16546 " + P0.get());
        l.add("net/minecraft/class_2333.field_10955 " + P1.get());
        l.add("net/minecraft/class_2260.field_10701 " + P2.get());
        l.add("net/minecraft/class_8810.field_46271 " + P3.get());
        l.add("net/minecraft/class_7268.field_38231 " + P4.get());
        return l;
    }

    abstract static class P0 extends net.minecraft.class_3749 {
        P0(net.minecraft.class_4970.class_2251 p) {
            super(p);
        }

        static String get() {
            return boxes(field_16546);
        }
    }

    abstract static class P1 extends net.minecraft.class_2333 {
        P1(net.minecraft.class_4970.class_2251 p) {
            super(p);
        }

        static String get() {
            return boxes(field_10955);
        }
    }

    abstract static class P2 extends net.minecraft.class_2260 {
        P2(net.minecraft.class_4970.class_2251 p) {
            super(p);
        }

        static String get() {
            return boxes(field_10701);
        }
    }

    abstract static class P3 extends net.minecraft.class_8810 {
        P3(net.minecraft.class_4970.class_2251 p) {
            super(p);
        }

        static String get() {
            return boxes(field_46271);
        }
    }

    abstract static class P4 extends net.minecraft.class_7268 {
        P4(net.minecraft.class_4970.class_2251 p) {
            super(p);
        }

        static String get() {
            return boxes(field_38231);
        }
    }

}
