package cb.s215;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Client cases (run once the first resource load is done): a 1.21.4 BakedModel of this mod's own and the 1.21.4 calls a
 * mod makes on the game's models. Logs "CB-S215C ok|FAIL <case>" and "CB-S215C done n/m".
 * <ul>
 * <li>own model: one quad per side (7 incl. unculled), getQuads counts its calls and records the state it was given;
 *     the target's collectParts / particleIcon (looked up at run time: this mod is compiled against 1.21.4) and the
 *     target's own ModelBlockRenderer.renderModel draw it (4 vertices per quad = 28).</li>
 * <li>vanilla stone: getQuads / useAmbientOcclusion / getParticleIcon called the 1.21.4 way.</li>
 * </ul>
 */
public final class ClientModels {
    private static int ok;
    private static int n;
    private static boolean done;

    private ClientModels() {
    }

    static void check(String c, boolean cond, String detail) {
        n++;
        if (cond) {
            ok++;
        }
        System.out.println("CB-S215C " + (cond ? "ok " : "FAIL ") + c + " " + detail);
    }

    interface Body {
        void run() throws Throwable;
    }

    static void run(String c, Body b) {
        try {
            b.run();
        } catch (Throwable t) {
            n++;
            System.out.println("CB-S215C FAIL " + c + " threw " + t);
            t.printStackTrace(System.out);
        }
    }

    /** a 1.21.4 model: one quad per side, sprite-less (only counted) */
    static final class OwnModel implements net.minecraft.class_1087 {
        int calls;
        int nonNullStates;
        net.minecraft.class_1058 sprite;

        @Override
        public List<net.minecraft.class_777> method_4707(net.minecraft.class_2680 state, net.minecraft.class_2350 side, net.minecraft.class_5819 random) {
            calls++;
            if (state != null) {
                nonNullStates++;
            }
            List<net.minecraft.class_777> l = new ArrayList<>();
            l.add(new net.minecraft.class_777(new int[32], -1, side == null ? net.minecraft.class_2350.field_11036 : side, sprite, true, 0));
            return l;
        }

        @Override
        public boolean method_4708() {
            return true;
        }

        @Override
        public boolean method_4712() {
            return false;
        }

        @Override
        public boolean method_24304() {
            return true;
        }

        @Override
        public net.minecraft.class_1058 method_4711() {
            return sprite;
        }

        @Override
        public net.minecraft.class_809 method_4709() {
            return net.minecraft.class_809.field_4301;
        }
    }

    /** counts the vertices a renderer emits */
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
        OwnModel own = new OwnModel();
        net.minecraft.class_2680 stone = net.minecraft.class_2246.field_10340.method_9564();
        own.sprite = mc.method_1541().method_3349(stone).method_4711();
        run("model-collect-parts", () -> {
            Method collect = own.getClass().getMethod("method_68512", net.minecraft.class_5819.class);
            List<?> parts = (List<?>) collect.invoke(own, net.minecraft.class_5819.method_43049(7L));
            Object part = parts.isEmpty() ? null : parts.get(0);
            Method quads = part == null ? null : part.getClass().getMethod("method_68509", net.minecraft.class_2350.class);
            Method ao = part == null ? null : part.getClass().getMethod("comp_3751");
            int up = quads == null ? -1 : ((List<?>) quads.invoke(part, net.minecraft.class_2350.field_11036)).size();
            int none = quads == null ? -1 : ((List<?>) quads.invoke(part, (Object) null)).size();
            check("model-collect-parts", parts.size() == 1 && up == 1 && none == 1 && Boolean.TRUE.equals(ao.invoke(part))
                && own.calls == 7 && own.nonNullStates == 0, "parts=" + parts.size() + " up=" + up + " unculled=" + none
                + " getQuads calls=" + own.calls + " non-null states=" + own.nonNullStates);
        });
        run("model-particle-icon", () -> {
            Method icon = own.getClass().getMethod("method_68511");
            check("model-particle-icon", own.sprite != null && icon.invoke(own) == own.sprite, "particleIcon answers getParticleIcon: " + own.sprite);
        });
        run("model-render", () -> {
            Method render = null;
            for (Method m : net.minecraft.class_778.class.getMethods()) {
                if (m.getName().equals("method_3367") && java.lang.reflect.Modifier.isStatic(m.getModifiers())) {
                    render = m;
                }
            }
            Counter c = new Counter();
            render.invoke(null, new net.minecraft.class_4587().method_23760(), c, own, 1.0F, 1.0F, 1.0F, 0xF000F0, 0);
            check("model-render", c.vertices == 28, "vanilla renderModel emitted " + c.vertices + " vertices (7 quads x 4)");
        });
        run("model-calls-vanilla", () -> {
            net.minecraft.class_1087 m = mc.method_1541().method_3349(stone);
            List<net.minecraft.class_777> up = m.method_4707(stone, net.minecraft.class_2350.field_11036, net.minecraft.class_5819.method_43049(42L));
            boolean ao = m.method_4708();
            net.minecraft.class_1058 particle = m.method_4711();
            check("model-calls-vanilla", up.size() == 1 && ao && particle != null,
                "stone getQuads(UP)=" + up.size() + " ao=" + ao + " particle=" + particle);
        });
        System.out.println("CB-S215C done " + ok + "/" + n);
    }
}
