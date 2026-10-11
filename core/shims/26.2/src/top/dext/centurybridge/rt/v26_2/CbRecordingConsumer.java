package top.dext.centurybridge.rt.v26_2;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Arrays;

/**
 * A VertexConsumer that keeps what it is told, element by element, so it can be told again
 * to a real buffer later (CbRecordingBuffers). Only the elements the writer set are
 * replayed: a buffer whose format lacks one ignores it on the real path too, and one the
 * writer forgot is the same mistake it would have been in 1.20.1.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbRecordingConsumer implements VertexConsumer {

    private static final int POS = 1, COLOR = 2, UV0 = 4, UV1 = 8, UV2 = 16, NORMAL = 32, LINE = 64;

    private int count;
    private int[] mask = new int[64];
    private float[] pos = new float[64 * 3];
    private int[] color = new int[64];
    private float[] uv0 = new float[64 * 2];
    private int[] uv1 = new int[64];
    private int[] uv2 = new int[64];
    private float[] normal = new float[64 * 3];
    private float[] line = new float[64];

    public boolean isEmpty() {
        return count == 0;
    }

    private int cur() {
        if (count == 0) {
            throw new IllegalStateException("vertex element set before addVertex");
        }
        return count - 1;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        if (count == mask.length) {
            int n = count * 2;
            mask = Arrays.copyOf(mask, n);
            pos = Arrays.copyOf(pos, n * 3);
            color = Arrays.copyOf(color, n);
            uv0 = Arrays.copyOf(uv0, n * 2);
            uv1 = Arrays.copyOf(uv1, n);
            uv2 = Arrays.copyOf(uv2, n);
            normal = Arrays.copyOf(normal, n * 3);
            line = Arrays.copyOf(line, n);
        }
        int i = count++;
        mask[i] = POS;
        pos[i * 3] = x;
        pos[i * 3 + 1] = y;
        pos[i * 3 + 2] = z;
        return this;
    }

    @Override
    public VertexConsumer setColor(int r, int g, int b, int a) {
        return setColor((a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF);
    }

    @Override
    public VertexConsumer setColor(int argb) {
        int i = cur();
        mask[i] |= COLOR;
        color[i] = argb;
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        int i = cur();
        mask[i] |= UV0;
        uv0[i * 2] = u;
        uv0[i * 2 + 1] = v;
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        int i = cur();
        mask[i] |= UV1;
        uv1[i] = (v & 0xFFFF) << 16 | (u & 0xFFFF);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        int i = cur();
        mask[i] |= UV2;
        uv2[i] = (v & 0xFFFF) << 16 | (u & 0xFFFF);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z) {
        int i = cur();
        mask[i] |= NORMAL;
        normal[i * 3] = x;
        normal[i * 3 + 1] = y;
        normal[i * 3 + 2] = z;
        return this;
    }

    @Override
    public VertexConsumer setLineWidth(float width) {
        int i = cur();
        mask[i] |= LINE;
        line[i] = width;
        return this;
    }

    /** everything recorded, in order, into a real buffer */
    public void replay(VertexConsumer out) {
        for (int i = 0; i < count; i++) {
            int m = mask[i];
            out.addVertex(pos[i * 3], pos[i * 3 + 1], pos[i * 3 + 2]);
            if ((m & COLOR) != 0) out.setColor(color[i]);
            if ((m & UV0) != 0) out.setUv(uv0[i * 2], uv0[i * 2 + 1]);
            if ((m & UV1) != 0) out.setUv1(uv1[i] & 0xFFFF, uv1[i] >>> 16);
            if ((m & UV2) != 0) out.setUv2(uv2[i] & 0xFFFF, uv2[i] >>> 16);
            if ((m & NORMAL) != 0) out.setNormal(normal[i * 3], normal[i * 3 + 1], normal[i * 3 + 2]);
            if ((m & LINE) != 0) out.setLineWidth(line[i]);
        }
    }
}
