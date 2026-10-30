package top.dext.centurybridge.step.v1_21_11.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_238;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * 1.21.10 ShapeRenderer's (class_9974) vertex writers that 1.21.11 dropped: renderLineBox (three shapes), addChainedFilledBoxVertices
 * (two), renderFace, renderVector, and renderShape without the line width. 1.21.11's own callers (BlockEntityWithBoundingBoxRenderer,
 * LightSectionDebugRenderer, the debug renderers) moved to gizmos (net/minecraft/gizmos: world-space primitives the level renderer
 * draws itself), which write into no caller's VertexConsumer, so nothing in 1.21.11 takes the place of a writer that draws into the
 * consumer it is handed. Given back with 1.21.10's bodies (javap -c -p of 1.21.10's class_9974, every vertex in its order): each uses
 * only VertexConsumer members 1.21.11 keeps (addVertex(Matrix4f, ..) takes the Matrix4fc a Matrix4f is). 1.21.10 drew the line width
 * from the render type at draw time; a line vertex written here without one gets that width from mixin/CbLineWidthBuilderMixin.
 */
@Environment(EnvType.CLIENT)
public final class CbShapeRenderer {
    private CbShapeRenderer() {
    }

    /** renderShape(pose, consumer, shape, x, y, z, color) of 1.21.10: every edge of the shape, offset, one segment each */
    public static void method_62296(class_4587 stack, class_4588 consumer, class_265 shape, double x, double y, double z, int color) {
        class_4587.class_4665 pose = stack.method_23760();
        shape.method_1104((x0, y0, z0, x1, y1, z1) -> {
            Vector3f n = new Vector3f((float) (x1 - x0), (float) (y1 - y0), (float) (z1 - z0)).normalize();
            consumer.method_56824(pose, (float) (x0 + x), (float) (y0 + y), (float) (z0 + z)).method_39415(color).method_61959(pose, n);
            consumer.method_56824(pose, (float) (x1 + x), (float) (y1 + y), (float) (z1 + z)).method_39415(color).method_61959(pose, n);
        });
    }

    /** renderLineBox(pose, consumer, box, r, g, b, a) */
    public static void method_62295(class_4587.class_4665 pose, class_4588 consumer, class_238 box, float r, float g, float b, float a) {
        method_62293(pose, consumer, box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1324, r, g, b, a,
            r, g, b);
    }

    /** renderLineBox(pose, consumer, minX..maxZ, r, g, b, a): the axes in the box's colour */
    public static void method_62292(class_4587.class_4665 pose, class_4588 consumer, double x0, double y0, double z0, double x1, double y1,
                                    double z1, float r, float g, float b, float a) {
        method_62293(pose, consumer, x0, y0, z0, x1, y1, z1, r, g, b, a, r, g, b);
    }

    /** renderLineBox(pose, consumer, minX..maxZ, r, g, b, a, xr, xg, xb): twelve edges, the three at the minimum corner in the axis colours */
    public static void method_62293(class_4587.class_4665 pose, class_4588 consumer, double x0, double y0, double z0, double x1, double y1,
                                    double z1, float r, float g, float b, float a, float xr, float xg, float xb) {
        float fx0 = (float) x0;
        float fy0 = (float) y0;
        float fz0 = (float) z0;
        float fx1 = (float) x1;
        float fy1 = (float) y1;
        float fz1 = (float) z1;
        consumer.method_56824(pose, fx0, fy0, fz0).method_22915(r, xg, xb, a).method_60831(pose, 1.0F, 0.0F, 0.0F);
        consumer.method_56824(pose, fx1, fy0, fz0).method_22915(r, xg, xb, a).method_60831(pose, 1.0F, 0.0F, 0.0F);
        consumer.method_56824(pose, fx0, fy0, fz0).method_22915(xr, g, xb, a).method_60831(pose, 0.0F, 1.0F, 0.0F);
        consumer.method_56824(pose, fx0, fy1, fz0).method_22915(xr, g, xb, a).method_60831(pose, 0.0F, 1.0F, 0.0F);
        consumer.method_56824(pose, fx0, fy0, fz0).method_22915(xr, xg, b, a).method_60831(pose, 0.0F, 0.0F, 1.0F);
        consumer.method_56824(pose, fx0, fy0, fz1).method_22915(xr, xg, b, a).method_60831(pose, 0.0F, 0.0F, 1.0F);
        consumer.method_56824(pose, fx1, fy0, fz0).method_22915(r, g, b, a).method_60831(pose, 0.0F, 1.0F, 0.0F);
        consumer.method_56824(pose, fx1, fy1, fz0).method_22915(r, g, b, a).method_60831(pose, 0.0F, 1.0F, 0.0F);
        consumer.method_56824(pose, fx1, fy1, fz0).method_22915(r, g, b, a).method_60831(pose, -1.0F, 0.0F, 0.0F);
        consumer.method_56824(pose, fx0, fy1, fz0).method_22915(r, g, b, a).method_60831(pose, -1.0F, 0.0F, 0.0F);
        consumer.method_56824(pose, fx0, fy1, fz0).method_22915(r, g, b, a).method_60831(pose, 0.0F, 0.0F, 1.0F);
        consumer.method_56824(pose, fx0, fy1, fz1).method_22915(r, g, b, a).method_60831(pose, 0.0F, 0.0F, 1.0F);
        consumer.method_56824(pose, fx0, fy1, fz1).method_22915(r, g, b, a).method_60831(pose, 0.0F, -1.0F, 0.0F);
        consumer.method_56824(pose, fx0, fy0, fz1).method_22915(r, g, b, a).method_60831(pose, 0.0F, -1.0F, 0.0F);
        consumer.method_56824(pose, fx0, fy0, fz1).method_22915(r, g, b, a).method_60831(pose, 1.0F, 0.0F, 0.0F);
        consumer.method_56824(pose, fx1, fy0, fz1).method_22915(r, g, b, a).method_60831(pose, 1.0F, 0.0F, 0.0F);
        consumer.method_56824(pose, fx1, fy0, fz1).method_22915(r, g, b, a).method_60831(pose, 0.0F, 0.0F, -1.0F);
        consumer.method_56824(pose, fx1, fy0, fz0).method_22915(r, g, b, a).method_60831(pose, 0.0F, 0.0F, -1.0F);
        consumer.method_56824(pose, fx0, fy1, fz1).method_22915(r, g, b, a).method_60831(pose, 1.0F, 0.0F, 0.0F);
        consumer.method_56824(pose, fx1, fy1, fz1).method_22915(r, g, b, a).method_60831(pose, 1.0F, 0.0F, 0.0F);
        consumer.method_56824(pose, fx1, fy0, fz1).method_22915(r, g, b, a).method_60831(pose, 0.0F, 1.0F, 0.0F);
        consumer.method_56824(pose, fx1, fy1, fz1).method_22915(r, g, b, a).method_60831(pose, 0.0F, 1.0F, 0.0F);
        consumer.method_56824(pose, fx1, fy1, fz0).method_22915(r, g, b, a).method_60831(pose, 0.0F, 0.0F, 1.0F);
        consumer.method_56824(pose, fx1, fy1, fz1).method_22915(r, g, b, a).method_60831(pose, 0.0F, 0.0F, 1.0F);
    }

    /** addChainedFilledBoxVertices(stack, consumer, minX..maxZ, r, g, b, a) */
    public static void method_62300(class_4587 stack, class_4588 consumer, double x0, double y0, double z0, double x1, double y1, double z1,
                                    float r, float g, float b, float a) {
        method_62294(stack, consumer, (float) x0, (float) y0, (float) z0, (float) x1, (float) y1, (float) z1, r, g, b, a);
    }

    /** addChainedFilledBoxVertices(stack, consumer, minX..maxZ, r, g, b, a): the box as one triangle strip */
    public static void method_62294(class_4587 stack, class_4588 consumer, float x0, float y0, float z0, float x1, float y1, float z1,
                                    float r, float g, float b, float a) {
        Matrix4f m = stack.method_23760().method_23761();
        consumer.method_22918(m, x0, y0, z0).method_22915(r, g, b, a);
        consumer.method_22918(m, x0, y0, z0).method_22915(r, g, b, a);
        consumer.method_22918(m, x0, y0, z0).method_22915(r, g, b, a);
        consumer.method_22918(m, x0, y0, z1).method_22915(r, g, b, a);
        consumer.method_22918(m, x0, y1, z0).method_22915(r, g, b, a);
        consumer.method_22918(m, x0, y1, z1).method_22915(r, g, b, a);
        consumer.method_22918(m, x0, y1, z1).method_22915(r, g, b, a);
        consumer.method_22918(m, x0, y0, z1).method_22915(r, g, b, a);
        consumer.method_22918(m, x1, y1, z1).method_22915(r, g, b, a);
        consumer.method_22918(m, x1, y0, z1).method_22915(r, g, b, a);
        consumer.method_22918(m, x1, y0, z1).method_22915(r, g, b, a);
        consumer.method_22918(m, x1, y0, z0).method_22915(r, g, b, a);
        consumer.method_22918(m, x1, y1, z1).method_22915(r, g, b, a);
        consumer.method_22918(m, x1, y1, z0).method_22915(r, g, b, a);
        consumer.method_22918(m, x1, y1, z0).method_22915(r, g, b, a);
        consumer.method_22918(m, x1, y0, z0).method_22915(r, g, b, a);
        consumer.method_22918(m, x0, y1, z0).method_22915(r, g, b, a);
        consumer.method_22918(m, x0, y0, z0).method_22915(r, g, b, a);
        consumer.method_22918(m, x0, y0, z0).method_22915(r, g, b, a);
        consumer.method_22918(m, x1, y0, z0).method_22915(r, g, b, a);
        consumer.method_22918(m, x0, y0, z1).method_22915(r, g, b, a);
        consumer.method_22918(m, x1, y0, z1).method_22915(r, g, b, a);
        consumer.method_22918(m, x1, y0, z1).method_22915(r, g, b, a);
        consumer.method_22918(m, x0, y1, z0).method_22915(r, g, b, a);
        consumer.method_22918(m, x0, y1, z0).method_22915(r, g, b, a);
        consumer.method_22918(m, x0, y1, z1).method_22915(r, g, b, a);
        consumer.method_22918(m, x1, y1, z0).method_22915(r, g, b, a);
        consumer.method_22918(m, x1, y1, z1).method_22915(r, g, b, a);
        consumer.method_22918(m, x1, y1, z1).method_22915(r, g, b, a);
        consumer.method_22918(m, x1, y1, z1).method_22915(r, g, b, a);
    }

    /** renderFace(matrix, consumer, face, minX..maxZ, r, g, b, a): the face's quad */
    public static void method_62297(Matrix4f m, class_4588 consumer, class_2350 face, float x0, float y0, float z0, float x1, float y1, float z1,
                                    float r, float g, float b, float a) {
        if (face == class_2350.field_11033) { // DOWN
            consumer.method_22918(m, x0, y0, z0).method_22915(r, g, b, a);
            consumer.method_22918(m, x1, y0, z0).method_22915(r, g, b, a);
            consumer.method_22918(m, x1, y0, z1).method_22915(r, g, b, a);
            consumer.method_22918(m, x0, y0, z1).method_22915(r, g, b, a);
            return;
        }
        if (face == class_2350.field_11036) { // UP
            consumer.method_22918(m, x0, y1, z0).method_22915(r, g, b, a);
            consumer.method_22918(m, x0, y1, z1).method_22915(r, g, b, a);
            consumer.method_22918(m, x1, y1, z1).method_22915(r, g, b, a);
            consumer.method_22918(m, x1, y1, z0).method_22915(r, g, b, a);
            return;
        }
        if (face == class_2350.field_11043) { // NORTH
            consumer.method_22918(m, x0, y0, z0).method_22915(r, g, b, a);
            consumer.method_22918(m, x0, y1, z0).method_22915(r, g, b, a);
            consumer.method_22918(m, x1, y1, z0).method_22915(r, g, b, a);
            consumer.method_22918(m, x1, y0, z0).method_22915(r, g, b, a);
            return;
        }
        if (face == class_2350.field_11035) { // SOUTH
            consumer.method_22918(m, x0, y0, z1).method_22915(r, g, b, a);
            consumer.method_22918(m, x1, y0, z1).method_22915(r, g, b, a);
            consumer.method_22918(m, x1, y1, z1).method_22915(r, g, b, a);
            consumer.method_22918(m, x0, y1, z1).method_22915(r, g, b, a);
            return;
        }
        if (face == class_2350.field_11039) { // WEST
            consumer.method_22918(m, x0, y0, z0).method_22915(r, g, b, a);
            consumer.method_22918(m, x0, y0, z1).method_22915(r, g, b, a);
            consumer.method_22918(m, x0, y1, z1).method_22915(r, g, b, a);
            consumer.method_22918(m, x0, y1, z0).method_22915(r, g, b, a);
            return;
        }
        if (face == class_2350.field_11034) { // EAST
            consumer.method_22918(m, x1, y0, z0).method_22915(r, g, b, a);
            consumer.method_22918(m, x1, y1, z0).method_22915(r, g, b, a);
            consumer.method_22918(m, x1, y1, z1).method_22915(r, g, b, a);
            consumer.method_22918(m, x1, y0, z1).method_22915(r, g, b, a);
            return;
        }
    }

    /** renderVector(stack, consumer, start, vector, color): a segment from start to start + vector, the vector as its normal */
    public static void method_62298(class_4587 stack, class_4588 consumer, Vector3f start, class_243 vector, int color) {
        class_4587.class_4665 pose = stack.method_23760();
        consumer.method_61032(pose, start).method_39415(color).method_60831(pose, (float) vector.field_1352, (float) vector.field_1351,
            (float) vector.field_1350);
        consumer.method_56824(pose, (float) (start.x() + vector.field_1352), (float) (start.y() + vector.field_1351),
            (float) (start.z() + vector.field_1350)).method_39415(color).method_60831(pose, (float) vector.field_1352,
            (float) vector.field_1351, (float) vector.field_1350);
    }
}
