package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11659;
import net.minecraft.class_11661;
import net.minecraft.class_11684;
import net.minecraft.class_12075;
import net.minecraft.class_1921;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4599;
import net.minecraft.class_9799;

/**
 * 1.21.8's immediate entity / block-entity / layer rendering (a MultiBufferSource, drawn into as the renderer
 * runs) over 1.21.9's submit queue (a SubmitNodeCollector, drawn after every renderer has submitted).
 *
 * A 1.21.8 renderer method reached from 1.21.9's submit is handed a {@link Capture}: a MultiBufferSource whose
 * buffers record every vertex the renderer writes (already through its class_4587, as 1.21.8's buffers received
 * them) and submit them, at the end of the call, as custom geometry of their RenderType with an identity pose
 * (the recorded positions are in the space the submit's class_4587 works in: camera-relative, as 1.21.8's). The
 * renderer's own calls into 1.21.9's renderers pass the capture's collector and camera on ({@link #collector},
 * {@link #camera}), so vanilla's part (the model, its layers, the name tag) is submitted as 1.21.9 submits it.
 *
 * A 1.21.8 call with a MultiBufferSource of the mod's own (a world-render event's, a GUI's) has no submit
 * queue behind it: {@link #collector} answers an {@link Immediate} collector that renders each submission
 * into that buffer source right away through 1.21.9's FeatureRenderDispatcher -- what 1.21.8's renderers did
 * with it.
 */
@Environment(EnvType.CLIENT)
public final class CbSubmit {

    private CbSubmit() {
    }

    // ------------------------------------------------------------------ capture: MultiBufferSource over a collector

    public static final class Capture implements class_4597 {
        final class_11659 collector;
        final class_12075 camera;
        private final Map<class_1921, Recorder> recorders = new LinkedHashMap<>();

        public Capture(class_11659 collector, class_12075 camera) {
            this.collector = collector;
            this.camera = camera;
        }

        @Override
        public class_4588 method_73477(class_1921 type) {
            return recorders.computeIfAbsent(type, t -> new Recorder());
        }

        /** submit what the renderer drew (once per RenderType, in the order the types were first asked for) */
        public void flush() {
            for (Map.Entry<class_1921, Recorder> e : recorders.entrySet()) {
                Recorder r = e.getValue();
                if (r.count > 0) {
                    Recorder copy = r.take();
                    collector.method_73483(new class_4587(), e.getKey(), (pose, vc) -> copy.replay(vc));
                }
            }
        }
    }

    /** a capture for a bridged renderer call; the caller flushes it when the old method returns */
    public static Capture capture(class_11659 collector, class_12075 camera) {
        return new Capture(collector, camera);
    }

    /** a vertex consumer that keeps every vertex and the attributes set on it, to replay into the real buffer */
    static final class Recorder implements class_4588 {
        private static final int POS = 1, COLOR = 2, UV0 = 4, UV1 = 8, UV2 = 16, NORMAL = 32;
        // per vertex: flags, x, y, z, r, g, b, a, u, v, u1, v1, u2, v2, nx, ny, nz (floats, ints stored exactly)
        private static final int STRIDE = 17;
        private float[] data = new float[STRIDE * 64];
        int count;

        private int base() {
            return (count - 1) * STRIDE;
        }

        private void set(int flag, int at, float a, float b) {
            if (count == 0) {
                throw new IllegalStateException("vertex attribute set before addVertex");
            }
            int o = base();
            data[o] = Float.intBitsToFloat(Float.floatToRawIntBits(data[o]) | flag);
            data[o + at] = a;
            data[o + at + 1] = b;
        }

        @Override
        public class_4588 method_22912(float x, float y, float z) {
            if ((count + 1) * STRIDE > data.length) {
                data = java.util.Arrays.copyOf(data, data.length * 2);
            }
            count++;
            int o = base();
            java.util.Arrays.fill(data, o, o + STRIDE, 0.0F);
            data[o] = Float.intBitsToFloat(POS);
            data[o + 1] = x;
            data[o + 2] = y;
            data[o + 3] = z;
            return this;
        }

        @Override
        public class_4588 method_1336(int r, int g, int b, int a) {
            set(COLOR, 4, r, g);
            int o = base();
            data[o + 6] = b;
            data[o + 7] = a;
            return this;
        }

        @Override
        public class_4588 method_22913(float u, float v) {
            set(UV0, 8, u, v);
            return this;
        }

        @Override
        public class_4588 method_60796(int u, int v) {
            set(UV1, 10, u, v);
            return this;
        }

        @Override
        public class_4588 method_22921(int u, int v) {
            set(UV2, 12, u, v);
            return this;
        }

        @Override
        public class_4588 method_22914(float x, float y, float z) {
            set(NORMAL, 14, x, y);
            data[base() + 16] = z;
            return this;
        }

        Recorder take() {
            Recorder r = new Recorder();
            r.data = java.util.Arrays.copyOf(data, count * STRIDE);
            r.count = count;
            count = 0;
            return r;
        }

        void replay(class_4588 vc) {
            for (int i = 0; i < count; i++) {
                int o = i * STRIDE;
                int f = Float.floatToRawIntBits(data[o]);
                vc.method_22912(data[o + 1], data[o + 2], data[o + 3]);
                if ((f & COLOR) != 0) {
                    vc.method_1336((int) data[o + 4], (int) data[o + 5], (int) data[o + 6], (int) data[o + 7]);
                }
                if ((f & UV0) != 0) {
                    vc.method_22913(data[o + 8], data[o + 9]);
                }
                if ((f & UV1) != 0) {
                    vc.method_60796((int) data[o + 10], (int) data[o + 11]);
                }
                if ((f & UV2) != 0) {
                    vc.method_22921((int) data[o + 12], (int) data[o + 13]);
                }
                if ((f & NORMAL) != 0) {
                    vc.method_22914(data[o + 14], data[o + 15], data[o + 16]);
                }
            }
        }
    }

    // ------------------------------------------------------------------ calls: MultiBufferSource -> collector / camera

    /** the collector behind a 1.21.8 MultiBufferSource: a capture's, else one rendering into the buffers right away */
    public static class_11659 collector(class_4597 buffers) {
        if (buffers instanceof Capture c) {
            return c.collector;
        }
        return new Immediate(buffers);
    }

    /** the camera of the frame a capture belongs to, else the game renderer's current one */
    public static class_12075 camera(class_4597 buffers) {
        if (buffers instanceof Capture c && c.camera != null) {
            return c.camera;
        }
        return currentCamera();
    }

    public static class_12075 currentCamera() {
        class_310 mc = class_310.method_1551();
        class_12075 cam = mc.field_1773.field_61732.field_63082;
        if (cam != null && cam.field_63079) {
            return cam;
        }
        class_4184 c = mc.field_1773.method_19418();
        class_12075 s = new class_12075();
        s.field_63079 = c.method_19332();
        s.field_63078 = c.method_19326();
        s.field_63077 = c.method_19328();
        s.field_63080 = c.method_19331() != null ? c.method_19331().method_30950(1.0F) : c.method_19326();
        s.field_63081 = new org.joml.Quaternionf(c.method_23767());
        return s;
    }

    // ------------------------------------------------------------------ immediate: a collector drawing into buffers now

    private static final Map<class_4597, class_11684> DISPATCHERS = new WeakHashMap<>();

    /** 1.21.9's feature dispatcher over these buffers (one per buffer source) */
    static class_11684 dispatcher(class_4597 buffers) {
        synchronized (DISPATCHERS) {
            return DISPATCHERS.computeIfAbsent(buffers, b -> {
                class_310 mc = class_310.method_1551();
                class_4599 rb = mc.method_22940();
                class_4597.class_4598 source = b instanceof class_4597.class_4598 bs ? bs : new Delegating(b);
                return new class_11684(new class_11661(), mc.method_1541(), source, mc.method_72703(), rb.method_23003(),
                    rb.method_23001(), mc.field_1772);
            });
        }
    }

    /** a BufferSource handing out another MultiBufferSource's buffers (batches end where that source ends them) */
    static final class Delegating extends class_4597.class_4598 {
        private final class_4597 target;

        Delegating(class_4597 target) {
            super(new class_9799(256), new java.util.LinkedHashMap<>());
            this.target = target;
        }

        @Override
        public class_4588 method_73477(class_1921 type) {
            return target.method_73477(type);
        }

        @Override
        public void method_37104() {
        }

        @Override
        public void method_22993() {
        }

        @Override
        public void method_22994(class_1921 type) {
        }
    }

    /**
     * Each submission is queued in the dispatcher's own storage and rendered at once (FeatureRenderDispatcher
     * renders every feature of its storage into its buffer source and clears it).
     */
    public static final class Immediate extends class_11661 {
        private final class_4597 buffers;

        Immediate(class_4597 buffers) {
            this.buffers = buffers;
        }

        private void render() {
            class_11684 d = dispatcher(buffers);
            class_11661 storage = d.method_73003();
            for (var e : this.method_73532().int2ObjectEntrySet()) {
                storage.method_73532().put(e.getIntKey(), e.getValue());
            }
            this.method_73532().clear();
            d.method_73002();
        }

        /** ordered submissions render as they come too: an immediate collector has no frame to order within */
        @Override
        public net.minecraft.class_11785 method_73529(int order) {
            return this;
        }

        // every submission goes through method_73531 (the order's collection) in SubmitNodeStorage; rendering after each
        // public submission keeps 1.21.8's draw-as-you-go order

        @Override
        public void method_73487(class_4587 p, net.minecraft.class_10017 s, net.minecraft.class_10933 sh) {
            super.method_73487(p, s, sh);
            render();
        }

        @Override
        public void method_73479(class_4587 p, float f, List<net.minecraft.class_10017.class_11680> l) {
            super.method_73479(p, f, l);
            render();
        }

        @Override
        public void method_73482(class_4587 p, net.minecraft.class_243 v, int i, net.minecraft.class_2561 t, boolean b, int j, double d,
                                 class_12075 c) {
            super.method_73482(p, v, i, t, b, j, d, c);
            render();
        }

        @Override
        public void method_73478(class_4587 p, float x, float y, net.minecraft.class_5481 t, boolean b, net.minecraft.class_327.class_6415 m,
                                 int i, int j, int k, int l) {
            super.method_73478(p, x, y, t, b, m, i, j, k, l);
            render();
        }

        @Override
        public void method_73488(class_4587 p, net.minecraft.class_10017 s, org.joml.Quaternionf q) {
            super.method_73488(p, s, q);
            render();
        }

        @Override
        public void method_73486(class_4587 p, net.minecraft.class_10017.class_10018 l) {
            super.method_73486(p, l);
            render();
        }

        @Override
        public <S> void method_73490(net.minecraft.class_3879<? super S> m, S s, class_4587 p, class_1921 t, int i, int j, int k,
                                     net.minecraft.class_1058 sp, int l, net.minecraft.class_11683.class_11792 c) {
            super.method_73490(m, s, p, t, i, j, k, sp, l, c);
            render();
        }

        @Override
        public void method_73494(net.minecraft.class_630 part, class_4587 p, class_1921 t, int i, int j, net.minecraft.class_1058 sp,
                                 boolean a, boolean b, int k, net.minecraft.class_11683.class_11792 c, int l) {
            super.method_73494(part, p, t, i, j, sp, a, b, k, c, l);
            render();
        }

        @Override
        public void method_73481(class_4587 p, net.minecraft.class_2680 s, int i, int j, int k) {
            super.method_73481(p, s, i, j, k);
            render();
        }

        @Override
        public void method_73485(class_4587 p, net.minecraft.class_11791 s) {
            super.method_73485(p, s);
            render();
        }

        @Override
        public void method_73484(class_4587 p, class_1921 t, net.minecraft.class_1087 m, float r, float g, float b, int i, int j, int k) {
            super.method_73484(p, t, m, r, g, b, i, j, k);
            render();
        }

        @Override
        public void method_73480(class_4587 p, net.minecraft.class_811 c, int i, int j, int k, int[] tints,
                                 List<net.minecraft.class_777> q, class_1921 t, net.minecraft.class_10444.class_10445 f) {
            super.method_73480(p, c, i, j, k, tints, q, t, f);
            render();
        }

        @Override
        public void method_73483(class_4587 p, class_1921 t, class_11659.class_11660 r) {
            super.method_73483(p, t, r);
            render();
        }

        @Override
        public void method_74315(class_11659.class_11947 r) {
            super.method_74315(r);
            render();
        }
    }

    /** what a 1.21.8 call passes for 1.21.9's outline colour argument: none (EntityRenderState.NO_OUTLINE) */
    public static int noOutline() {
        return 0;
    }

    /** helper for generated bridges: the old method's light when 1.21.9 dropped the argument (the state carries it) */
    public static int light(net.minecraft.class_10017 state) {
        return state.field_61820;
    }
}
