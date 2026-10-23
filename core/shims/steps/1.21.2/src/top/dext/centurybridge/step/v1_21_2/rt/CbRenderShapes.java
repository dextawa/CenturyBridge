package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1 LevelRenderer's static outline / box helpers (staticRedirects), moved to 1.21.2's ShapeRenderer (class_9974).
 * Each 1.21.1 helper whose 1.21.2 counterpart has the same descriptor and the same code (instruction for instruction,
 * checked) calls it; the two without one carry their 1.21.1 bodies.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderShapes {

    private CbRenderShapes() {
    }

    /** renderLineBox(pose, consumer, box, r, g, b, a) = ShapeRenderer's (class_9974.method_62295, same code) */
    public static void method_22982(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, net.minecraft.class_238 box, float r, float g, float b, float a) {
        net.minecraft.class_9974.method_62295(pose, consumer, box, r, g, b, a);
    }

    /** renderLineBox(pose, consumer, minX..maxZ, r, g, b, a) = class_9974.method_62292 (same code) */
    public static void method_22980(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, double x0, double y0, double z0, double x1,
                                    double y1, double z1, float r, float g, float b, float a) {
        net.minecraft.class_9974.method_62292(pose, consumer, x0, y0, z0, x1, y1, z1, r, g, b, a);
    }

    /** renderLineBox(pose, consumer, minX..maxZ, r, g, b, a, xr, xg, xb) = class_9974.method_62293 (same code) */
    public static void method_22981(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, double x0, double y0, double z0, double x1,
                                    double y1, double z1, float r, float g, float b, float a, float r2, float g2, float b2) {
        net.minecraft.class_9974.method_62293(pose, consumer, x0, y0, z0, x1, y1, z1, r, g, b, a, r2, g2, b2);
    }

    /** addChainedFilledBoxVertices(pose, consumer, minX..maxZ, r, g, b, a) = class_9974.method_62300 (same code) */
    public static void method_3258(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, double x0, double y0, double z0, double x1,
                                   double y1, double z1, float r, float g, float b, float a) {
        net.minecraft.class_9974.method_62300(pose, consumer, x0, y0, z0, x1, y1, z1, r, g, b, a);
    }

    /** addChainedFilledBoxVertices(pose, consumer, 6 floats, r, g, b, a) = class_9974.method_62294 (same code) */
    public static void method_49041(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, float x0, float y0, float z0, float x1, float y1,
                                    float z1, float r, float g, float b, float a) {
        net.minecraft.class_9974.method_62294(pose, consumer, x0, y0, z0, x1, y1, z1, r, g, b, a);
    }

    /** renderFace(pose, consumer, direction, 6 floats, r, g, b, a) = class_9974.method_62297 (same code) */
    public static void method_61044(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, net.minecraft.class_2350 face, float x0, float y0,
                                    float z0, float x1, float y1, float z1, float r, float g, float b, float a) {
        net.minecraft.class_9974.method_62297(pose, consumer, face, x0, y0, z0, x1, y1, z1, r, g, b, a);
    }

    /** renderLineBox(consumer, minX..maxZ, r, g, b, a): 1.21.1's body -- a fresh PoseStack, the colour also for the axes */
    public static void method_35773(net.minecraft.class_4588 consumer, double x0, double y0, double z0, double x1, double y1, double z1, float r,
                                    float g, float b, float a) {
        method_22981(new net.minecraft.class_4587(), consumer, x0, y0, z0, x1, y1, z1, r, g, b, a, r, g, b);
    }

    /**
     * renderShape(pose, consumer, shape, x, y, z, r, g, b, a, colorPerBox): 1.21.1's body -- the shape's boxes, the first
     * in the given colour, each further one (when colorPerBox) in a hue-shifted one, or all edges of the shape in one
     * colour per box index otherwise; each box's edges are drawn by 1.21.2's ShapeRenderer.renderShape (the same edge
     * walk as 1.21.1's private renderShape, with the colour packed as 1.21.1's VertexConsumer.setColor(float...) packed
     * it: (int) (channel * 255)).
     */
    public static void method_22983(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, net.minecraft.class_265 shape, double x, double y,
                                    double z, float r, float g, float b, float a, boolean colorPerBox) {
        java.util.List<net.minecraft.class_238> boxes = shape.method_1090();
        if (boxes.isEmpty()) {
            return;
        }
        int n = colorPerBox ? boxes.size() : boxes.size() * 8;
        edges(pose, consumer, net.minecraft.class_259.method_1078(boxes.get(0)), x, y, z, r, g, b, a);
        for (int i = 1; i < boxes.size(); i++) {
            net.minecraft.class_238 box = boxes.get(i);
            float t = (float) i / (float) n;
            net.minecraft.class_243 c = shifted(r, g, b, t);
            edges(pose, consumer, net.minecraft.class_259.method_1078(box), x, y, z, (float) c.field_1352, (float) c.field_1351, (float) c.field_1350, a);
        }
    }

    private static void edges(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, net.minecraft.class_265 shape, double x, double y, double z,
                              float r, float g, float b, float a) {
        int color = (int) (a * 255.0F) << 24 | (int) (r * 255.0F) << 16 | (int) (g * 255.0F) << 8 | (int) (b * 255.0F);
        net.minecraft.class_9974.method_62296(pose, consumer, shape, x, y, z, color);
    }

    /** 1.21.1 LevelRenderer.method_51779: the colour mixed over three hues a third apart, normalised to its largest channel */
    private static net.minecraft.class_243 shifted(float r, float g, float b, float t) {
        net.minecraft.class_243 cr = hue(t).method_1021(r);
        net.minecraft.class_243 cg = hue((t + 0.33333334F) % 1.0F).method_1021(g);
        net.minecraft.class_243 cb = hue((t + 0.6666667F) % 1.0F).method_1021(b);
        net.minecraft.class_243 sum = cr.method_1019(cg).method_1019(cb);
        double max = Math.max(1.0, Math.max(sum.field_1352, Math.max(sum.field_1351, sum.field_1350)));
        return new net.minecraft.class_243(sum.field_1352 / max, sum.field_1351 / max, sum.field_1350 / max);
    }

    /** 1.21.1 LevelRenderer.method_51778: a fully saturated hue (0..1) as an RGB vector */
    private static net.minecraft.class_243 hue(float h) {
        int i = (int) (net.minecraft.class_3532.method_15363(h, 0.0F, 1.0F) * 5.99999F);
        float f = h * 5.99999F - i;
        return switch (i) {
            case 0 -> new net.minecraft.class_243(1.0, f, 0.0);
            case 1 -> new net.minecraft.class_243(1.0F - f, 1.0, 0.0);
            case 2 -> new net.minecraft.class_243(0.0, 1.0, f);
            case 3 -> new net.minecraft.class_243(0.0, 1.0 - f, 1.0);
            case 4 -> new net.minecraft.class_243(f, 0.0, 1.0);
            case 5 -> new net.minecraft.class_243(1.0, 0.0, 1.0 - f);
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }
}
