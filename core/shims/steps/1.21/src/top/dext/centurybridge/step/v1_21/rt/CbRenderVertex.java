package top.dext.centurybridge.step.v1_21.rt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.common.collect.MapMaker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21.mixin.CbRenderDoubleAccess;
import top.dext.centurybridge.step.v1_21.mixin.CbRenderMultipleAccess;
import top.dext.centurybridge.step.v1_21.mixin.CbRenderSpriteAccess;

/**
 * 1.20.6's VertexConsumer calls on 1.21 (the call side of CbRenderLegacyConsumer). A 1.20.6 caller's
 * vertex(double..), vertex(matrix, ..), vertex(pose, ..), overlayCoords(int, int), uv2(int), normal(pose, ..), the
 * whole vertex of fourteen arguments, endVertex(), defaultColor / unsetDefaultColor reach
 * <ul>
 * <li>a 1.20.6 consumer's own (through its entries, CbRenderLegacyConsumer);</li>
 * <li>a BufferBuilder's 1.20.6 methods (CbRenderBufferBuilderMixin);</li>
 * <li>1.21's sprite expander and multi-consumers as 1.20.6's answered them: handed on to what they wrap, the
 *     expander with the sprite's UVs on the whole vertex, answering what its delegate answered;</li>
 * <li>any other 1.21 consumer (the decal, the outline generator, one of a mod built for 1.21) through its 1.21
 *     equivalent: addVertex, setUv1, setLight, setNormal(pose, ..), its whole vertex with the packed color.
 *     endVertex() hands the decal's and outline generator's vertex on to their delegates (they write each element
 *     on as it comes) and is 1.21's implicit end for the rest; defaultColor is 1.21's decal's and outline
 *     generator's to ignore, as 1.20.6's ignored it, and for a consumer CenturyBridge does not know it colors the
 *     vertices 1.20.6 code begins on it from then on.</li>
 * </ul>
 * Also the bookkeeping of vertices 1.21 code begins on a 1.20.6 consumer without ending them (CbRenderLegacyConsumer).
 */
@Environment(EnvType.CLIENT)
public final class CbRenderVertex {

    private CbRenderVertex() {
    }

    // ------------------------------------------------------------------ vertices 1.21 code begins on a 1.20.6 consumer

    private static final ThreadLocal<List<CbRenderLegacyConsumer>> PENDING = ThreadLocal.withInitial(ArrayList::new);

    private static int indexOf(List<CbRenderLegacyConsumer> pending, Object consumer) {
        for (int i = 0; i < pending.size(); i++) {
            if (pending.get(i) == consumer) {
                return i;
            }
        }
        return -1;
    }

    /** 1.21 code has begun a vertex on the consumer: it is open until ended */
    public static void cbMarkPending(CbRenderLegacyConsumer consumer) {
        List<CbRenderLegacyConsumer> pending = PENDING.get();
        if (indexOf(pending, consumer) < 0) {
            pending.add(consumer);
        }
    }

    /** a vertex 1.21 code left open on the consumer ends (its endVertex()) before anything else begins one */
    public static void cbEndPendingVertex(CbRenderLegacyConsumer consumer) {
        List<CbRenderLegacyConsumer> pending = PENDING.get();
        if (pending.isEmpty()) {
            return;
        }
        int i = indexOf(pending, consumer);
        if (i >= 0) {
            pending.remove(i).cbLegacyEndVertex();
        }
    }

    /** the consumer's vertex is being ended by its endVertex(): no longer open */
    public static void cbVertexEnded(CbRenderLegacyConsumer consumer) {
        List<CbRenderLegacyConsumer> pending = PENDING.get();
        if (pending.isEmpty()) {
            return;
        }
        int i = indexOf(pending, consumer);
        if (i >= 0) {
            pending.remove(i);
        }
    }

    /** every vertex 1.21 code left open on a 1.20.6 consumer ends (a mesh is about to be built) */
    public static void cbEndAllPendingVertices() {
        List<CbRenderLegacyConsumer> pending = PENDING.get();
        while (!pending.isEmpty()) {
            pending.remove(0).cbLegacyEndVertex();
        }
    }

    // ------------------------------------------------------------------ the default color of a consumer CenturyBridge does not know

    private static final Map<net.minecraft.class_4588, int[]> DEFAULTS = new MapMaker().weakKeys().makeMap();

    private static net.minecraft.class_4588 cbWithDefault(net.minecraft.class_4588 consumer, net.minecraft.class_4588 next) {
        if (!DEFAULTS.isEmpty()) {
            int[] c = DEFAULTS.get(consumer);
            if (c != null) {
                consumer.method_1336(c[0], c[1], c[2], c[3]);
            }
        }
        return next;
    }

    private static boolean cbHandsOn(net.minecraft.class_4588 consumer) {
        return consumer instanceof CbRenderSpriteAccess || consumer instanceof CbRenderDoubleAccess
            || consumer instanceof CbRenderMultipleAccess;
    }

    // ------------------------------------------------------------------ 1.20.6 calls

    /** vertex(x, y, z) */
    public static net.minecraft.class_4588 cbVertex(net.minecraft.class_4588 consumer, double x, double y, double z) {
        if (consumer instanceof CbRenderLegacyConsumer legacy) {
            return legacy.method_22912(x, y, z);
        }
        if (consumer instanceof CbRenderBufferBuilderV builder) {
            return builder.method_22912(x, y, z);
        }
        if (consumer instanceof CbRenderSpriteAccess sprite) {
            return cbVertex(sprite.cb$delegate(), x, y, z);
        }
        if (consumer instanceof CbRenderDoubleAccess pair) {
            cbVertex(pair.cb$first(), x, y, z);
            cbVertex(pair.cb$second(), x, y, z);
            return consumer;
        }
        if (consumer instanceof CbRenderMultipleAccess many) {
            for (net.minecraft.class_4588 c : many.cb$delegates()) {
                cbVertex(c, x, y, z);
            }
            return consumer;
        }
        return cbWithDefault(consumer, consumer.method_22912((float) x, (float) y, (float) z));
    }

    /** vertex(matrix, x, y, z) */
    public static net.minecraft.class_4588 cbVertexMatrix(net.minecraft.class_4588 consumer, org.joml.Matrix4f matrix, float x, float y,
                                                          float z) {
        if (consumer instanceof CbRenderLegacyConsumer legacy) {
            cbEndPendingVertex(legacy);
            return legacy.cbLegacyVertexMatrix(matrix, x, y, z);
        }
        if (consumer instanceof CbRenderBufferBuilderV builder) {
            return builder.cbLegacyVertexMatrix(matrix, x, y, z);
        }
        if (cbHandsOn(consumer)) {
            org.joml.Vector3f p = matrix.transformPosition(x, y, z, new org.joml.Vector3f());
            return cbVertex(consumer, p.x(), p.y(), p.z());
        }
        return cbWithDefault(consumer, consumer.method_22918(matrix, x, y, z));
    }

    /** vertex(pose, x, y, z) */
    public static net.minecraft.class_4588 cbVertexPose(net.minecraft.class_4588 consumer, net.minecraft.class_4587.class_4665 pose,
                                                        float x, float y, float z) {
        if (consumer instanceof CbRenderLegacyConsumer legacy) {
            cbEndPendingVertex(legacy);
            return legacy.cbLegacyVertexPose(pose, x, y, z);
        }
        if (consumer instanceof CbRenderBufferBuilderV builder) {
            return builder.cbLegacyVertexPose(pose, x, y, z);
        }
        if (cbHandsOn(consumer)) {
            return cbVertexMatrix(consumer, pose.method_23761(), x, y, z);
        }
        return cbWithDefault(consumer, consumer.method_56824(pose, x, y, z));
    }

    /** overlayCoords(u, v) */
    public static net.minecraft.class_4588 cbOverlayCoords(net.minecraft.class_4588 consumer, int u, int v) {
        if (consumer instanceof CbRenderLegacyConsumer legacy) {
            return legacy.method_22917(u, v);
        }
        if (consumer instanceof CbRenderBufferBuilderV builder) {
            return builder.method_22917(u, v);
        }
        if (consumer instanceof CbRenderSpriteAccess sprite) {
            return cbOverlayCoords(sprite.cb$delegate(), u, v);
        }
        if (consumer instanceof CbRenderDoubleAccess pair) {
            cbOverlayCoords(pair.cb$first(), u, v);
            cbOverlayCoords(pair.cb$second(), u, v);
            return consumer;
        }
        if (consumer instanceof CbRenderMultipleAccess many) {
            for (net.minecraft.class_4588 c : many.cb$delegates()) {
                cbOverlayCoords(c, u, v);
            }
            return consumer;
        }
        return consumer.method_60796(u, v);
    }

    /** uv2(packed): 1.20.6's default split it into uv2(u, v), as 1.21's setLight does */
    public static net.minecraft.class_4588 cbUv2(net.minecraft.class_4588 consumer, int packed) {
        if (consumer instanceof CbRenderLegacyConsumer legacy) {
            return legacy.method_22916(packed);
        }
        if (consumer instanceof CbRenderBufferBuilderV builder) {
            return builder.method_22916(packed);
        }
        return consumer.method_60803(packed);
    }

    /** normal(pose, x, y, z): 1.20.6's default transformed and called normal(x, y, z), as 1.21's setNormal(pose, ..) does */
    public static net.minecraft.class_4588 cbNormalPose(net.minecraft.class_4588 consumer, net.minecraft.class_4587.class_4665 pose,
                                                        float x, float y, float z) {
        if (consumer instanceof CbRenderLegacyConsumer legacy) {
            return legacy.method_23763(pose, x, y, z);
        }
        if (consumer instanceof CbRenderBufferBuilderV builder) {
            return builder.method_23763(pose, x, y, z);
        }
        return consumer.method_60831(pose, x, y, z);
    }

    /** vertex(x, y, z, r, g, b, a, u, v, overlay, light, nx, ny, nz): the whole vertex, ended */
    public static void cbBulkVertex(net.minecraft.class_4588 consumer, float x, float y, float z, float r, float g, float b, float a,
                                    float u, float v, int overlay, int light, float nx, float ny, float nz) {
        if (consumer instanceof CbRenderLegacyConsumer legacy) {
            cbEndPendingVertex(legacy);
            legacy.method_23919(x, y, z, r, g, b, a, u, v, overlay, light, nx, ny, nz);
            return;
        }
        if (consumer instanceof CbRenderBufferBuilderV builder) {
            builder.method_23919(x, y, z, r, g, b, a, u, v, overlay, light, nx, ny, nz);
            return;
        }
        if (consumer instanceof CbRenderSpriteAccess sprite) {
            net.minecraft.class_1058 s = sprite.cb$sprite();
            cbBulkVertex(sprite.cb$delegate(), x, y, z, r, g, b, a, s.method_4580(u), s.method_4570(v), overlay, light, nx, ny, nz);
            return;
        }
        if (consumer instanceof CbRenderDoubleAccess pair) {
            cbBulkVertex(pair.cb$first(), x, y, z, r, g, b, a, u, v, overlay, light, nx, ny, nz);
            cbBulkVertex(pair.cb$second(), x, y, z, r, g, b, a, u, v, overlay, light, nx, ny, nz);
            return;
        }
        if (consumer instanceof CbRenderMultipleAccess many) {
            for (net.minecraft.class_4588 c : many.cb$delegates()) {
                cbBulkVertex(c, x, y, z, r, g, b, a, u, v, overlay, light, nx, ny, nz);
            }
            return;
        }
        consumer.method_23919(x, y, z, CbClientModels.cbColor(r, g, b, a), u, v, overlay, light, nx, ny, nz);
    }

    /** endVertex() */
    public static void cbEndVertex(net.minecraft.class_4588 consumer) {
        if (consumer instanceof CbRenderLegacyConsumer legacy) {
            legacy.method_1344();
            return;
        }
        if (consumer instanceof CbRenderBufferBuilderV builder) {
            builder.method_1344();
            return;
        }
        List<net.minecraft.class_4588> wrapped = CbRenderWrappers.cbWrapped(consumer);
        if (wrapped != null) {
            for (net.minecraft.class_4588 w : wrapped) {
                cbEndVertex(w);
            }
        }
    }

    /** defaultColor(r, g, b, a) */
    public static void cbDefaultColor(net.minecraft.class_4588 consumer, int r, int g, int b, int a) {
        if (consumer instanceof CbRenderLegacyConsumer legacy) {
            legacy.method_22901(r, g, b, a);
            return;
        }
        if (consumer instanceof CbRenderBufferBuilderV builder) {
            builder.method_22901(r, g, b, a);
            return;
        }
        List<net.minecraft.class_4588> wrapped = CbRenderWrappers.cbWrappedForColor(consumer);
        if (wrapped != null) {
            for (net.minecraft.class_4588 w : wrapped) {
                cbDefaultColor(w, r, g, b, a);
            }
            return;
        }
        DEFAULTS.put(consumer, new int[] {r, g, b, a});
    }

    /** unsetDefaultColor() */
    public static void cbUnsetDefaultColor(net.minecraft.class_4588 consumer) {
        if (consumer instanceof CbRenderLegacyConsumer legacy) {
            legacy.method_35666();
            return;
        }
        if (consumer instanceof CbRenderBufferBuilderV builder) {
            builder.method_35666();
            return;
        }
        List<net.minecraft.class_4588> wrapped = CbRenderWrappers.cbWrappedForColor(consumer);
        if (wrapped != null) {
            for (net.minecraft.class_4588 w : wrapped) {
                cbUnsetDefaultColor(w);
            }
            return;
        }
        DEFAULTS.remove(consumer);
    }

    // ------------------------------------------------------------------ 1.20.6 calls through BufferVertexConsumer

    /*
     * 1.20.6's BufferVertexConsumer is a facade a 1.20.6 class implements; 1.21's BufferBuilder answers the same
     * calls with its own cursor (CbRenderBufferBuilderMixin) without implementing it, so a call through the
     * interface type reaches whichever of the two the object is.
     */

    /** currentElement() */
    public static net.minecraft.class_296 cbBufferCurrentElement(top.dext.centurybridge.step.v1_21.facade.BufferVertexConsumer c) {
        if ((Object) c instanceof CbRenderBufferBuilderV b) {
            return b.method_22900();
        }
        return c.method_22900();
    }

    /** nextElement() */
    public static void cbBufferNextElement(top.dext.centurybridge.step.v1_21.facade.BufferVertexConsumer c) {
        if ((Object) c instanceof CbRenderBufferBuilderV b) {
            b.method_1325();
            return;
        }
        c.method_1325();
    }

    /** putByte(index, value) */
    public static void cbBufferPutByte(top.dext.centurybridge.step.v1_21.facade.BufferVertexConsumer c, int index, byte value) {
        if ((Object) c instanceof CbRenderBufferBuilderV b) {
            b.method_22896(index, value);
            return;
        }
        c.method_22896(index, value);
    }

    /** putShort(index, value) */
    public static void cbBufferPutShort(top.dext.centurybridge.step.v1_21.facade.BufferVertexConsumer c, int index, short value) {
        if ((Object) c instanceof CbRenderBufferBuilderV b) {
            b.method_22898(index, value);
            return;
        }
        c.method_22898(index, value);
    }

    /** putFloat(index, value) */
    public static void cbBufferPutFloat(top.dext.centurybridge.step.v1_21.facade.BufferVertexConsumer c, int index, float value) {
        if ((Object) c instanceof CbRenderBufferBuilderV b) {
            b.method_22897(index, value);
            return;
        }
        c.method_22897(index, value);
    }

    /** vertex(x, y, z) */
    public static net.minecraft.class_4588 cbBufferVertex(top.dext.centurybridge.step.v1_21.facade.BufferVertexConsumer c, double x,
                                                          double y, double z) {
        return cbVertex((net.minecraft.class_4588) (Object) c, x, y, z);
    }

    /** color(r, g, b, a) */
    public static net.minecraft.class_4588 cbBufferColor(top.dext.centurybridge.step.v1_21.facade.BufferVertexConsumer c, int r, int g,
                                                         int b, int a) {
        return ((net.minecraft.class_4588) (Object) c).method_1336(r, g, b, a);
    }

    /** uv(u, v) */
    public static net.minecraft.class_4588 cbBufferUv(top.dext.centurybridge.step.v1_21.facade.BufferVertexConsumer c, float u, float v) {
        return ((net.minecraft.class_4588) (Object) c).method_22913(u, v);
    }

    /** overlayCoords(u, v) */
    public static net.minecraft.class_4588 cbBufferOverlayCoords(top.dext.centurybridge.step.v1_21.facade.BufferVertexConsumer c, int u,
                                                                 int v) {
        return cbOverlayCoords((net.minecraft.class_4588) (Object) c, u, v);
    }

    /** uv2(u, v) */
    public static net.minecraft.class_4588 cbBufferUv2(top.dext.centurybridge.step.v1_21.facade.BufferVertexConsumer c, int u, int v) {
        return ((net.minecraft.class_4588) (Object) c).method_22921(u, v);
    }

    /** uvShort(u, v, index) */
    public static net.minecraft.class_4588 cbBufferUvShort(top.dext.centurybridge.step.v1_21.facade.BufferVertexConsumer c, short u,
                                                           short v, int index) {
        if ((Object) c instanceof CbRenderBufferBuilderV b) {
            return b.method_22899(u, v, index);
        }
        return c.method_22899(u, v, index);
    }

    /** normal(x, y, z) */
    public static net.minecraft.class_4588 cbBufferNormal(top.dext.centurybridge.step.v1_21.facade.BufferVertexConsumer c, float x,
                                                          float y, float z) {
        return ((net.minecraft.class_4588) (Object) c).method_22914(x, y, z);
    }

    // ------------------------------------------------------------------ 1.20.6's putBulkData body

    /**
     * putBulkData(pose, quad, brightness, r, g, b, a, lights, overlay, readExistingColor) of 1.20.6: each vertex of
     * the quad as one whole 1.20.6 vertex, position through the pose, the face normal through its normal matrix,
     * color = (the quad's own color when asked) * brightness * (r, g, b), the light of that corner.
     */
    public static void cbLegacyBulk(CbRenderLegacyConsumer consumer, net.minecraft.class_4587.class_4665 pose, net.minecraft.class_777 quad,
                                    float[] brightness, float r, float g, float b, float a, int[] lights, int overlay,
                                    boolean readExistingColor) {
        float[] bright = {brightness[0], brightness[1], brightness[2], brightness[3]};
        int[] light = {lights[0], lights[1], lights[2], lights[3]};
        int[] vertices = quad.method_3357();
        net.minecraft.class_2382 dir = quad.method_3358().method_10163();
        org.joml.Matrix4f matrix = pose.method_23761();
        org.joml.Vector3f normal = pose.method_56820((float) dir.method_10263(), (float) dir.method_10264(), (float) dir.method_10260(),
            new org.joml.Vector3f());
        int count = vertices.length / 8;
        try (org.lwjgl.system.MemoryStack stack = org.lwjgl.system.MemoryStack.stackPush()) {
            java.nio.ByteBuffer buf = stack.malloc(net.minecraft.class_290.field_1590.method_1362());
            java.nio.IntBuffer ints = buf.asIntBuffer();
            for (int k = 0; k < count; k++) {
                ints.clear();
                ints.put(vertices, k * 8, 8);
                float x = buf.getFloat(0);
                float y = buf.getFloat(4);
                float z = buf.getFloat(8);
                float rr;
                float gg;
                float bb;
                if (readExistingColor) {
                    float qr = (float) (buf.get(12) & 255) / 255.0F;
                    float qg = (float) (buf.get(13) & 255) / 255.0F;
                    float qb = (float) (buf.get(14) & 255) / 255.0F;
                    rr = qr * bright[k] * r;
                    gg = qg * bright[k] * g;
                    bb = qb * bright[k] * b;
                } else {
                    rr = bright[k] * r;
                    gg = bright[k] * g;
                    bb = bright[k] * b;
                }
                int l = light[k];
                float u = buf.getFloat(16);
                float v = buf.getFloat(20);
                org.joml.Vector4f p = matrix.transform(new org.joml.Vector4f(x, y, z, 1.0F));
                consumer.method_23919(p.x(), p.y(), p.z(), rr, gg, bb, a, u, v, overlay, l, normal.x(), normal.y(), normal.z());
            }
        }
    }
}
