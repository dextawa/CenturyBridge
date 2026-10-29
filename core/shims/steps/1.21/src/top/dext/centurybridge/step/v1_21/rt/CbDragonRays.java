package top.dext.centurybridge.step.v1_21.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * EnderDragonRenderer's death rays (class_895). 1.20.6 drew each ray as three triangles through four private static
 * helpers, one vertex each: method_23157 (the centre, white, the fading alpha), method_23156 / method_23158 / method_23159
 * (the corners (-sqrt(3)/2 w, h, -w/2), (sqrt(3)/2 w, h, -w/2), (0, h, w), magenta, alpha 0), in the order centre A B,
 * centre B C, centre C A. 1.21 inlined all of them into the new private static method_61158(PoseStack, float, VertexConsumer):
 * per ray the same nine vertices as addVertex(pose, vector) + setColor(int) pairs (javap of both).
 * CbRenderDragonRaysRestoredMixin restores the four helpers with 1.20.6's bodies and hands them over here from
 * EnderDragonRenderer's static init (no @Invoker: see CbReportComments); CbRenderDragonRaysRouteMixin sends each of 1.21's
 * vertices through its helper while a converted mod mixes any of them (CbMixedMembers).
 */
@Environment(EnvType.CLIENT)
public final class CbDragonRays {

    /** the restored helpers, as CbMixedMembers keys */
    public static final String[] KEYS = {
        "net/minecraft/class_895.method_23156(Lnet/minecraft/class_4588;Lorg/joml/Matrix4f;FF)V",
        "net/minecraft/class_895.method_23157(Lnet/minecraft/class_4588;Lorg/joml/Matrix4f;I)V",
        "net/minecraft/class_895.method_23158(Lnet/minecraft/class_4588;Lorg/joml/Matrix4f;FF)V",
        "net/minecraft/class_895.method_23159(Lnet/minecraft/class_4588;Lorg/joml/Matrix4f;FF)V"};

    /** a corner helper: (consumer, matrix, h, w) */
    public interface Corner {
        void vertex(net.minecraft.class_4588 consumer, org.joml.Matrix4f matrix, float h, float w);
    }

    /** the centre helper: (consumer, matrix, alpha) */
    public interface Centre {
        void vertex(net.minecraft.class_4588 consumer, org.joml.Matrix4f matrix, int alpha);
    }

    public static volatile Corner cornerA;
    public static volatile Centre centre;
    public static volatile Corner cornerB;
    public static volatile Corner cornerC;

    /** a vertex 1.21 began whose color has not been set yet: the route draws it through its helper at setColor */
    private static final ThreadLocal<Pending> PENDING = new ThreadLocal<>();

    private record Pending(org.joml.Matrix4f matrix, float x, float y, float z) {
    }

    private CbDragonRays() {
    }

    /** EnderDragonRenderer's static init (CbRenderDragonRaysRestoredMixin): its restored helpers */
    public static boolean publish(Corner a, Centre c, Corner b, Corner cc) {
        cornerA = a;
        centre = c;
        cornerB = b;
        cornerC = cc;
        return true;
    }

    /** whether a loaded converted mod mixes a restored helper */
    public static boolean routed() {
        if (centre == null) {
            return false;
        }
        for (String k : KEYS) {
            if (CbMixedMembers.mixed(k)) {
                return true;
            }
        }
        return false;
    }

    /** 1.21's addVertex(pose, vector) of a ray vertex, routed: remembered for its setColor, nothing drawn yet */
    public static void begin(net.minecraft.class_4587.class_4665 pose, org.joml.Vector3f v) {
        PENDING.set(new Pending(pose.method_23761(), v.x(), v.y(), v.z()));
    }

    /**
     * 1.21's setColor(color) after a remembered vertex: the vertex drawn through the 1.20.6 helper that wrote it, told
     * apart by its position (the centre is the origin, A has x &lt; 0, B x &gt; 0, C x = 0 off the origin; 1.21's w is at
     * least 1 and h above 0), h and w read back from it as 1.20.6 passed them, the centre's alpha from 1.21's color.
     * False when nothing was remembered.
     */
    public static boolean end(net.minecraft.class_4588 consumer, int color) {
        Pending p = PENDING.get();
        if (p == null) {
            return false;
        }
        PENDING.remove();
        if (p.x() == 0.0F && p.y() == 0.0F && p.z() == 0.0F) {
            centre.vertex(consumer, p.matrix(), color >>> 24);
        } else if (p.x() < 0.0F) {
            cornerA.vertex(consumer, p.matrix(), p.y(), -2.0F * p.z());
        } else if (p.x() > 0.0F) {
            cornerB.vertex(consumer, p.matrix(), p.y(), -2.0F * p.z());
        } else {
            cornerC.vertex(consumer, p.matrix(), p.y(), p.z());
        }
        return true;
    }
}
