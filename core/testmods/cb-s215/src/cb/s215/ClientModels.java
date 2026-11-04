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
        run("model-item-props-vanilla", () -> {
            // 1.21.4: stone's BlockModel (block/stone -> cube_all -> cube -> block) baked isGui3d true, usesBlockLight
            // true (gui_light side), transforms from block/block.json (gui rotation 30, 225, 0)
            net.minecraft.class_1087 m = mc.method_1541().method_3349(stone);
            boolean gui3d = m.method_4712();
            boolean blockLight = m.method_24304();
            net.minecraft.class_809 t = m.method_4709();
            net.minecraft.class_804 gui = t.method_3503(net.minecraft.class_811.field_4317);
            // (ItemTransform's rotation field reads are their own open rows: compared by identity to the constants only)
            check("model-item-props-vanilla", gui3d && blockLight && t != net.minecraft.class_809.field_4301 && gui != net.minecraft.class_804.field_4284,
                "stone isGui3d=" + gui3d + " usesBlockLight=" + blockLight + " gui transform=" + gui);
        });
        run("model-item-props-own", () -> {
            check("model-item-props-own", !own.method_4712() && own.method_24304() && own.method_4709() == net.minecraft.class_809.field_4301,
                "own model answers itself");
        });
        run("model-location-lookup", () -> {
            // 1.21.4: ModelManager.getModel(BlockModelShaper.stateToModelLocation(state)) is the state's block model;
            // an unknown location answers the missing model
            net.minecraft.class_1092 manager = mc.method_1554();
            net.minecraft.class_1091 loc = net.minecraft.class_773.method_3340(stone);
            net.minecraft.class_1087 byLoc = manager.method_4742(loc);
            net.minecraft.class_1087 byState = mc.method_1541().method_3349(stone);
            net.minecraft.class_1087 unknown = manager.method_4742(new net.minecraft.class_1091(net.minecraft.class_2960.method_60656("stone"), "nonsense=1"));
            net.minecraft.class_1087 frame = manager.method_4742(net.minecraft.class_9824.field_53652);
            check("model-location-lookup", byLoc == byState && unknown == manager.method_4744() && frame != manager.method_4744()
                && loc.toString().equals("minecraft:stone#"),
                "location=" + loc + " same model=" + (byLoc == byState) + " unknown->missing=" + (unknown == manager.method_4744())
                + " item frame model=" + frame);
        });
        run("model-simple-builder", () -> {
            // 1.21.4 SimpleBakedModel.Builder: one culled UP quad, one unculled quad; build() -> SimpleBakedModel
            net.minecraft.class_1093.class_1094 b = new net.minecraft.class_1093.class_1094(true, true, false, net.minecraft.class_809.field_4301);
            b.method_4745(net.minecraft.class_2350.field_11036, new net.minecraft.class_777(new int[32], -1, net.minecraft.class_2350.field_11036, own.sprite, true, 0));
            b.method_4748(new net.minecraft.class_777(new int[32], -1, net.minecraft.class_2350.field_11036, own.sprite, true, 0));
            net.minecraft.class_1087 m = b.method_4747(own.sprite).method_35809().method_4746();
            boolean simple = m instanceof net.minecraft.class_1093;
            int up = m.method_4707(null, net.minecraft.class_2350.field_11036, net.minecraft.class_5819.method_43049(1L)).size();
            int down = m.method_4707(null, net.minecraft.class_2350.field_11033, net.minecraft.class_5819.method_43049(1L)).size();
            int none = m.method_4707(null, null, net.minecraft.class_5819.method_43049(1L)).size();
            Counter c = new Counter();
            Method render = null;
            for (Method r : net.minecraft.class_778.class.getMethods()) {
                if (r.getName().equals("method_3367") && java.lang.reflect.Modifier.isStatic(r.getModifiers())) {
                    render = r;
                }
            }
            render.invoke(null, new net.minecraft.class_4587().method_23760(), c, m, 1.0F, 1.0F, 1.0F, 0xF000F0, 0);
            boolean missing = false;
            try {
                new net.minecraft.class_1093.class_1094(true, true, true, net.minecraft.class_809.field_4301).method_4746();
            } catch (RuntimeException e) {
                missing = "Missing particle!".equals(e.getMessage());
            }
            check("model-simple-builder", simple && up == 1 && down == 0 && none == 1 && !m.method_4712() && m.method_24304()
                && m.method_4711() == own.sprite && c.vertices == 8 && missing,
                "simple=" + simple + " up=" + up + " down=" + down + " unculled=" + none + " rendered=" + c.vertices + " missing-particle=" + missing);
        });
        run("model-delegate", () -> {
            // 1.21.4 DelegateBakedModel subclass (anonymous) over stone's model: forwards, and 1.21.5 renders it
            net.minecraft.class_1087 stoneModel = mc.method_1541().method_3349(stone);
            // a local subclass: field_54188 is protected in 1.21.4, read from inside the subclass
            final class Delegate extends net.minecraft.class_10200 {
                Delegate(net.minecraft.class_1087 parent) {
                    super(parent);
                }

                net.minecraft.class_1087 parent() {
                    return this.field_54188;
                }
            }
            Delegate delegate = new Delegate(stoneModel);
            int dUp = delegate.method_4707(stone, net.minecraft.class_2350.field_11036, net.minecraft.class_5819.method_43049(42L)).size();
            Counter c = new Counter();
            Method render = null;
            for (Method r : net.minecraft.class_778.class.getMethods()) {
                if (r.getName().equals("method_3367") && java.lang.reflect.Modifier.isStatic(r.getModifiers())) {
                    render = r;
                }
            }
            render.invoke(null, new net.minecraft.class_4587().method_23760(), c, delegate, 1.0F, 1.0F, 1.0F, 0xF000F0, 0);
            check("model-delegate", dUp == 1 && delegate.method_4711() == stoneModel.method_4711() && delegate.method_4712()
                && delegate.parent() == stoneModel && c.vertices == 24,
                "delegate up=" + dUp + " gui3d=" + delegate.method_4712() + " rendered=" + c.vertices + " (stone: 6 quads x 4)");
        });
        run("creaking-active", () -> {
            // 1.21.4 CreakingHeartBlock.ACTIVE / BlockStateProperties.ACTIVE over 1.21.5's CreakingHeartState
            net.minecraft.class_2680 base = net.minecraft.class_2246.field_54717.method_9564();
            boolean d = base.method_11654(net.minecraft.class_10319.field_55064);
            net.minecraft.class_2680 on = base.method_11657(net.minecraft.class_10319.field_55064, true);
            net.minecraft.class_2680 onAgain = on.method_11657(net.minecraft.class_2741.field_55087, true);
            net.minecraft.class_2680 off = on.method_11657(net.minecraft.class_2741.field_55087, false);
            boolean has = base.method_28498(net.minecraft.class_10319.field_55064);
            // the native 1.21.5 property, read reflectively (this mod's own field ref is the 1.21.4 boolean)
            @SuppressWarnings({"rawtypes"})
            net.minecraft.class_2769 st = (net.minecraft.class_2769) net.minecraft.class_10319.class.getField("field_55064").get(null);
            @SuppressWarnings("unchecked")
            String sb = String.valueOf(base.method_11654(st)), so = String.valueOf(on.method_11654(st)), sf = String.valueOf(off.method_11654(st));
            boolean ok = !d && has && on.method_11654(net.minecraft.class_10319.field_55064)
                && !off.method_11654(net.minecraft.class_2741.field_55087) && onAgain == on
                && sb.equals("uprooted") && so.equals("dormant") && sf.equals("uprooted");
            check("creaking-active", ok, "default active=" + d + " state=" + sb + "; set true -> " + so + " (again same=" + (onAgain == on)
                + "); set false -> " + sf + "; hasProperty=" + has);
        });
        System.out.println("CB-S215C done " + ok + "/" + n);
    }
}
