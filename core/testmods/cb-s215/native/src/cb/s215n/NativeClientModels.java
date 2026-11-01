package cb.s215n;

import java.util.ArrayList;
import java.util.List;

/**
 * Native 1.21.5 control for cb-s215's client cases: the same measurements through 1.21.5's own model API (a
 * BlockStateModel of one part with one quad per side incl. unculled, drawn by ModelBlockRenderer.renderModel; vanilla
 * stone's parts). Logs "CB-S215NC ok|FAIL <case>" and "CB-S215NC done n/m".
 */
public final class NativeClientModels {
    private static int ok;
    private static int n;
    private static boolean done;

    private NativeClientModels() {
    }

    static void check(String c, boolean cond, String detail) {
        n++;
        if (cond) {
            ok++;
        }
        System.out.println("CB-S215NC " + (cond ? "ok " : "FAIL ") + c + " " + detail);
    }

    static final class Part implements net.minecraft.class_10889 {
        public List<net.minecraft.class_777> method_68509(net.minecraft.class_2350 side) {
            List<net.minecraft.class_777> l = new ArrayList<>();
            l.add(new net.minecraft.class_777(new int[32], -1, side == null ? net.minecraft.class_2350.field_11036 : side, null, true, 0));
            return l;
        }

        public boolean comp_3751() {
            return true;
        }

        public net.minecraft.class_1058 comp_3752() {
            return null;
        }
    }

    static final class Model implements net.minecraft.class_1087 {
        public void method_68513(net.minecraft.class_5819 random, List<net.minecraft.class_10889> parts) {
            parts.add(new Part());
        }

        public net.minecraft.class_1058 method_68511() {
            return null;
        }
    }

    static final class Counter implements net.minecraft.class_4588 {
        int vertices;

        public net.minecraft.class_4588 method_22912(float x, float y, float z) {
            vertices++;
            return this;
        }

        public net.minecraft.class_4588 method_1336(int r, int g, int b, int a) {
            return this;
        }

        public net.minecraft.class_4588 method_22913(float u, float v) {
            return this;
        }

        public net.minecraft.class_4588 method_60796(int u, int v) {
            return this;
        }

        public net.minecraft.class_4588 method_22921(int u, int v) {
            return this;
        }

        public net.minecraft.class_4588 method_22914(float x, float y, float z) {
            return this;
        }
    }

    public static synchronized void runOnce(net.minecraft.class_310 mc) {
        if (done || mc.method_18506() != null) {
            return;
        }
        done = true;
        try {
            Counter c = new Counter();
            net.minecraft.class_778.method_3367(new net.minecraft.class_4587().method_23760(), c, new Model(), 1.0F, 1.0F, 1.0F, 0xF000F0, 0);
            check("native-model-render", c.vertices == 28, "renderModel emitted " + c.vertices + " vertices (7 quads x 4)");
            net.minecraft.class_1087 stone = mc.method_1541().method_3349(net.minecraft.class_2246.field_10340.method_9564());
            List<net.minecraft.class_10889> parts = stone.method_68512(net.minecraft.class_5819.method_43049(42L));
            check("native-stone-parts", parts.size() == 1 && parts.get(0).method_68509(net.minecraft.class_2350.field_11036).size() == 1
                && parts.get(0).comp_3751() && stone.method_68511() != null,
                "parts=" + parts.size() + " up=" + (parts.isEmpty() ? -1 : parts.get(0).method_68509(net.minecraft.class_2350.field_11036).size()));
        } catch (Throwable t) {
            n++;
            System.out.println("CB-S215NC FAIL threw " + t);
            t.printStackTrace(System.out);
        }
        System.out.println("CB-S215NC done " + ok + "/" + n);
    }
}
