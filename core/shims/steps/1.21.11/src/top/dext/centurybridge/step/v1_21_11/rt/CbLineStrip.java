package top.dext.centurybridge.step.v1_21_11.rt;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1921;
import net.minecraft.class_287;
import net.minecraft.class_4588;

/**
 * 1.21.10's line strips, which 1.21.11 cannot draw: its VertexFormat.Mode has no LINE_STRIP (1.21.11 draws lines as
 * a quad per segment, LINES: two vertices per segment). 1.21.10's RenderType.lineStrip() / LINE_STRIP drew the
 * vertices a batch received as one strip (LINES_SNIPPET's shader, LINE_STRIP mode). Here the strip render type is
 * 1.21.11's LINES pipeline with 1.21.10's state (rt/CbRenderTypeCreate.method_34572), and the vertex consumer a buffer
 * source hands out for it turns the strip into its segments: each vertex after the first closes the segment from the
 * one before it (vertex i-1, vertex i), which is what LINE_STRIP drew.
 */
@Environment(EnvType.CLIENT)
public final class CbLineStrip {
    private static final Set<class_1921> TYPES = Collections.newSetFromMap(new WeakHashMap<>());
    private static final Map<class_287, Strip> STRIPS = new WeakHashMap<>();

    private CbLineStrip() {
    }

    public static synchronized void register(class_1921 type) {
        TYPES.add(type);
    }

    /** the consumer for a strip render type's builder (one per builder: the strip continues across getBuffer calls) */
    public static synchronized class_4588 consumer(class_1921 type, class_287 builder) {
        if (!TYPES.contains(type)) {
            return null;
        }
        return STRIPS.computeIfAbsent(builder, Strip::new);
    }

    /** the batch is drawn: the strip's last segment is written and the strip ends */
    public static synchronized void flush(class_287 builder) {
        Strip s = STRIPS.remove(builder);
        if (s != null) {
            s.flush();
        }
    }

    /** one vertex as 1.21.10's line formats carried it */
    private static final class Vertex {
        float x;
        float y;
        float z;
        int color = -1;
        float nx;
        float ny;
        float nz;
        float width = Float.NaN;
    }

    @Environment(EnvType.CLIENT)
    static final class Strip implements class_4588 {
        private final class_287 out;
        private Vertex prev;
        private Vertex cur;

        Strip(class_287 out) {
            this.out = out;
        }

        private void write(Vertex v) {
            this.out.method_22912(v.x, v.y, v.z).method_39415(v.color).method_22914(v.nx, v.ny, v.nz);
            if (!Float.isNaN(v.width)) {
                this.out.method_75298(v.width);
            }
        }

        private void close() {
            if (this.cur != null) {
                if (this.prev != null) {
                    this.write(this.prev);
                    this.write(this.cur);
                }
                this.prev = this.cur;
            }
        }

        void flush() {
            this.close();
            this.prev = null;
            this.cur = null;
        }

        @Override
        public class_4588 method_22912(float x, float y, float z) {
            this.close();
            Vertex v = new Vertex();
            v.x = x;
            v.y = y;
            v.z = z;
            this.cur = v;
            return this;
        }

        @Override
        public class_4588 method_1336(int r, int g, int b, int a) {
            return this.method_39415((a & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | b & 255);
        }

        @Override
        public class_4588 method_39415(int argb) {
            if (this.cur != null) {
                this.cur.color = argb;
            }
            return this;
        }

        @Override
        public class_4588 method_22913(float u, float v) {
            return this; // LINES' format has no UV0: 1.21.10's LINE_STRIP format (POSITION_COLOR_NORMAL) had none either
        }

        @Override
        public class_4588 method_60796(int u, int v) {
            return this; // no UV1 in either format
        }

        @Override
        public class_4588 method_22921(int u, int v) {
            return this; // no UV2 in either format
        }

        @Override
        public class_4588 method_22914(float x, float y, float z) {
            if (this.cur != null) {
                this.cur.nx = x;
                this.cur.ny = y;
                this.cur.nz = z;
            }
            return this;
        }

        @Override
        public class_4588 method_75298(float width) {
            if (this.cur != null) {
                this.cur.width = width;
            }
            return this;
        }
    }
}
