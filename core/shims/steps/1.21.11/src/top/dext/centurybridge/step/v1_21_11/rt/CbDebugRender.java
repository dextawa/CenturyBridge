package top.dext.centurybridge.step.v1_21_11.rt;

import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_259;
import net.minecraft.class_265;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_9;
import net.minecraft.class_9848;

/**
 * 1.21.10 DebugRenderer's (class_863) and PathfindingRenderer's (class_868) static draw helpers that 1.21.11 dropped:
 * renderFilledBox (four shapes), renderFloatingText (five), the per-mob / per-block text lines, renderVoxelShape and
 * PathfindingRenderer.renderPath / renderPathLine. 1.21.11's debug renderers emit gizmos instead (net/minecraft/gizmos:
 * world-space primitives the level renderer draws itself, never into a caller's buffer source); no 1.21.11 method draws
 * a box / text / path into the buffers it is handed. Given back with 1.21.10's bodies (javap -c -p of 1.21.10's
 * class_863 / class_868), over 1.21.11's calls: the render types 1.21.11 dropped come back through the step's own
 * statics (rt/CbRenderTypes debugFilledBox, rt/CbRenderTypeCreate debugLineStrip), ShapeRenderer's writers through
 * rt/CbShapeRenderer, Camera.getPosition() is 1.21.11's method_71156 (same field). Line vertices written here get the
 * render type's 1.21.10 line width from mixin/CbLineWidthBuilderMixin.
 */
@Environment(EnvType.CLIENT)
public final class CbDebugRender {
    private CbDebugRender() {
    }

    // ---- DebugRenderer (class_863) ----

    /** renderFilledBox(stack, buffers, minX..maxZ, r, g, b, a): the box as a filled strip into debugFilledBox */
    public static void method_23098(class_4587 stack, class_4597 buffers, double x0, double y0, double z0, double x1, double y1, double z1,
                                    float r, float g, float b, float a) {
        class_4588 consumer = buffers.method_73477(CbRenderTypes.method_49047());
        CbShapeRenderer.method_62300(stack, consumer, x0, y0, z0, x1, y1, z1, r, g, b, a);
    }

    /** renderFilledBox(stack, buffers, box, r, g, b, a) */
    public static void method_23102(class_4587 stack, class_4597 buffers, class_238 box, float r, float g, float b, float a) {
        method_23098(stack, buffers, box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1324, r, g, b, a);
    }

    /** renderFilledBox(stack, buffers, pos, grow, r, g, b, a): the block, camera-relative, grown */
    public static void method_23103(class_4587 stack, class_4597 buffers, class_2338 pos, float grow, float r, float g, float b, float a) {
        class_4184 camera = class_310.method_1551().field_1773.method_19418();
        if (!camera.method_19332()) {
            return;
        }
        class_243 off = camera.method_71156().method_22882();
        class_238 box = new class_238(pos).method_997(off).method_1014(grow);
        method_23102(stack, buffers, box, r, g, b, a);
    }

    /** renderFilledBox(stack, buffers, from, to, r, g, b, a): the blocks from..to, camera-relative */
    public static void method_23104(class_4587 stack, class_4597 buffers, class_2338 from, class_2338 to, float r, float g, float b, float a) {
        class_4184 camera = class_310.method_1551().field_1773.method_19418();
        if (!camera.method_19332()) {
            return;
        }
        class_243 off = camera.method_71156().method_22882();
        class_238 box = class_238.method_54784(from, to).method_997(off);
        method_23102(stack, buffers, box, r, g, b, a);
    }

    /** renderFilledUnitCube(stack, buffers, pos, r, g, b, a) */
    public static void method_54808(class_4587 stack, class_4597 buffers, class_2338 pos, float r, float g, float b, float a) {
        method_23104(stack, buffers, pos, pos.method_10069(1, 1, 1), r, g, b, a);
    }

    /** renderFloatingText(stack, buffers, text, x, y, z, color) */
    public static void method_23105(class_4587 stack, class_4597 buffers, String text, double x, double y, double z, int color) {
        method_23106(stack, buffers, text, x, y, z, color, 0.02F);
    }

    /** renderFloatingText(stack, buffers, text, x, y, z, color, scale) */
    public static void method_23106(class_4587 stack, class_4597 buffers, String text, double x, double y, double z, int color, float scale) {
        method_23107(stack, buffers, text, x, y, z, color, scale, true, 0.0F, false);
    }

    /** renderFloatingText(stack, buffers, text, x, y, z, color, scale, center, xOffset, seeThrough): billboarded at the camera */
    public static void method_23107(class_4587 stack, class_4597 buffers, String text, double x, double y, double z, int color, float scale,
                                    boolean center, float xOffset, boolean seeThrough) {
        class_310 mc = class_310.method_1551();
        class_4184 camera = mc.field_1773.method_19418();
        if (!camera.method_19332() || mc.method_1561().field_4692 == null) {
            return;
        }
        class_327 font = mc.field_1772;
        double cx = camera.method_71156().field_1352;
        double cy = camera.method_71156().field_1351;
        double cz = camera.method_71156().field_1350;
        stack.method_22903();
        stack.method_46416((float) (x - cx), (float) (y - cy) + 0.07F, (float) (z - cz));
        stack.method_22907(camera.method_23767());
        stack.method_22905(scale, -scale, scale);
        float left = center ? (float) (-font.method_1727(text)) / 2.0F : 0.0F;
        left -= xOffset / scale;
        font.method_27521(text, left, 0.0F, color, false, stack.method_23760().method_23761(), buffers,
            seeThrough ? class_327.class_6415.field_33994 : class_327.class_6415.field_33993, 0, 15728880);
        stack.method_22909();
    }

    /** renderFloatingText(stack, buffers, text, blockX, blockY, blockZ, color): the block's centre */
    public static void method_23108(class_4587 stack, class_4597 buffers, String text, int x, int y, int z, int color) {
        method_23105(stack, buffers, text, x + 0.5, y + 0.5, z + 0.5, color);
    }

    /** renderTextOverMob(stack, buffers, entity, line, text, color, scale) */
    public static void method_74784(class_4587 stack, class_4597 buffers, class_1297 entity, int line, String text, int color, float scale) {
        double x = entity.method_31477() + 0.5;
        double y = entity.method_23318() + 2.4 + line * 0.25;
        double z = entity.method_31479() + 0.5;
        method_23107(stack, buffers, text, x, y, z, color, scale, false, 0.5F, true);
    }

    /** renderTextOverBlock(stack, buffers, text, pos, line, color, scale) */
    public static void method_74785(class_4587 stack, class_4597 buffers, String text, class_2338 pos, int line, int color, float scale) {
        double x = pos.method_10263() + 0.5;
        double y = pos.method_10264() + 1.3 + line * 0.2;
        double z = pos.method_10260() + 0.5;
        method_23107(stack, buffers, text, x, y, z, color, scale, true, 0.0F, true);
    }

    /**
     * renderVoxelShape(stack, consumer, shape, x, y, z, r, g, b, a, colorPerBox): the first box's edges in the colour, each
     * further box in a hue-shifted one (n = boxes, or boxes * 8 when not colorPerBox)
     */
    public static void method_62350(class_4587 stack, class_4588 consumer, class_265 shape, double x, double y, double z, float r, float g,
                                    float b, float a, boolean colorPerBox) {
        List<class_238> boxes = shape.method_1090();
        if (boxes.isEmpty()) {
            return;
        }
        int n = colorPerBox ? boxes.size() : boxes.size() * 8;
        CbShapeRenderer.method_62296(stack, consumer, class_259.method_1078(boxes.get(0)), x, y, z, class_9848.method_61318(a, r, g, b));
        for (int i = 1; i < boxes.size(); i++) {
            class_238 box = boxes.get(i);
            float t = (float) i / (float) n;
            class_243 c = shifted(r, g, b, t);
            CbShapeRenderer.method_62296(stack, consumer, class_259.method_1078(box), x, y, z,
                class_9848.method_61318(a, (float) c.field_1352, (float) c.field_1351, (float) c.field_1350));
        }
    }

    /** 1.21.10 DebugRenderer.method_62349: the colour mixed over three hues a third apart, normalised to its largest channel */
    private static class_243 shifted(float r, float g, float b, float t) {
        class_243 cr = hue(t).method_1021(r);
        class_243 cg = hue((t + 0.33333334F) % 1.0F).method_1021(g);
        class_243 cb = hue((t + 0.6666667F) % 1.0F).method_1021(b);
        class_243 sum = cr.method_1019(cg).method_1019(cb);
        double max = Math.max(Math.max(1.0, sum.field_1352), Math.max(sum.field_1351, sum.field_1350));
        return new class_243(sum.field_1352 / max, sum.field_1351 / max, sum.field_1350 / max);
    }

    /** 1.21.10 DebugRenderer.method_62348: a fully saturated hue (0..1) as an RGB vector */
    private static class_243 hue(float h) {
        int i = (int) (class_3532.method_15363(h, 0.0F, 1.0F) * 5.99999F);
        float f = h * 5.99999F - i;
        return switch (i) {
            case 0 -> new class_243(1.0, f, 0.0);
            case 1 -> new class_243(1.0F - f, 1.0, 0.0);
            case 2 -> new class_243(0.0, 1.0, f);
            case 3 -> new class_243(0.0, 1.0 - f, 1.0);
            case 4 -> new class_243(f, 0.0, 1.0);
            case 5 -> new class_243(1.0, 0.0, 1.0 - f);
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    // ---- PathfindingRenderer (class_868) ----

    /**
     * renderPath(stack, buffers, path, width, showOpenClosed, showDistances, camX, camY, camZ): the path line, the target
     * and each node as a box (the next node red), the open / closed sets, and each node's type and cost malus
     */
    public static void method_23118(class_4587 stack, class_4597 buffers, class_11 path, float width, boolean showOpenClosed,
                                    boolean showDistances, double x, double y, double z) {
        method_23117(stack, buffers.method_73477(CbRenderTypeCreate.method_49043(6.0)), path, x, y, z);
        class_2338 target = path.method_48();
        if (distance(target, x, y, z) <= 80.0F) {
            method_23102(stack, buffers, new class_238((double) ((float) target.method_10263() + 0.25F), (double) ((float) target.method_10264() + 0.25F),
                target.method_10260() + 0.25, (double) ((float) target.method_10263() + 0.75F), (double) ((float) target.method_10264() + 0.75F),
                (double) ((float) target.method_10260() + 0.75F)).method_989(-x, -y, -z), 0.0F, 1.0F, 0.0F, 0.5F);
            for (int i = 0; i < path.method_38(); i++) {
                class_9 node = path.method_40(i);
                if (distance(node.method_22879(), x, y, z) <= 80.0F) {
                    float r = i == path.method_39() ? 1.0F : 0.0F;
                    float b = i == path.method_39() ? 0.0F : 1.0F;
                    method_23102(stack, buffers, new class_238((double) ((float) node.field_40 + 0.5F - width),
                        (double) ((float) node.field_39 + 0.01F * (float) i), (double) ((float) node.field_38 + 0.5F - width),
                        (double) ((float) node.field_40 + 0.5F + width), (double) ((float) node.field_39 + 0.25F + 0.01F * (float) i),
                        (double) ((float) node.field_38 + 0.5F + width)).method_989(-x, -y, -z), r, 0.0F, b, 0.5F);
                }
            }
        }
        class_11.class_8644 debug = path.method_52604();
        if (showOpenClosed && debug != null) {
            for (class_9 node : debug.comp_1588()) {
                if (distance(node.method_22879(), x, y, z) <= 80.0F) {
                    method_23102(stack, buffers, setBox(node, width).method_989(-x, -y, -z), 1.0F, 0.8F, 0.8F, 0.5F);
                }
            }
            for (class_9 node : debug.comp_1587()) {
                if (distance(node.method_22879(), x, y, z) <= 80.0F) {
                    method_23102(stack, buffers, setBox(node, width).method_989(-x, -y, -z), 0.8F, 1.0F, 1.0F, 0.5F);
                }
            }
        }
        if (showDistances) {
            for (int i = 0; i < path.method_38(); i++) {
                class_9 node = path.method_40(i);
                if (distance(node.method_22879(), x, y, z) <= 80.0F) {
                    method_23107(stack, buffers, String.valueOf(node.field_41), node.field_40 + 0.5, node.field_39 + 0.75, node.field_38 + 0.5, -1,
                        0.02F, true, 0.0F, true);
                    method_23107(stack, buffers, String.format(Locale.ROOT, "%.2f", node.field_43), node.field_40 + 0.5, node.field_39 + 0.25,
                        node.field_38 + 0.5, -1, 0.02F, true, 0.0F, true);
                }
            }
        }
    }

    /** an open / closed set node's box in renderPath */
    private static class_238 setBox(class_9 node, float width) {
        return new class_238((double) ((float) node.field_40 + 0.5F - width / 2.0F), (double) ((float) node.field_39 + 0.01F),
            (double) ((float) node.field_38 + 0.5F - width / 2.0F), (double) ((float) node.field_40 + 0.5F + width / 2.0F), node.field_39 + 0.1,
            (double) ((float) node.field_38 + 0.5F + width / 2.0F));
    }

    /** renderPathLine(stack, consumer, path, camX, camY, camZ): one vertex per node in range, coloured along the path */
    public static void method_23117(class_4587 stack, class_4588 consumer, class_11 path, double x, double y, double z) {
        for (int i = 0; i < path.method_38(); i++) {
            class_9 node = path.method_40(i);
            if (distance(node.method_22879(), x, y, z) > 80.0F) {
                continue;
            }
            float h = (float) i / (float) path.method_38() * 0.33F;
            int color = i == 0 ? -16777216 : class_9848.method_61334(class_3532.method_15369(h, 0.9F, 0.9F));
            consumer.method_56824(stack.method_23760(), (float) (node.field_40 - x + 0.5), (float) (node.field_39 - y + 0.5),
                (float) (node.field_38 - z + 0.5)).method_39415(color);
        }
    }

    /** 1.21.10 PathfindingRenderer.distanceToCamera: the Manhattan distance */
    private static float distance(class_2338 pos, double x, double y, double z) {
        return (float) (Math.abs(pos.method_10263() - x) + Math.abs(pos.method_10264() - y) + Math.abs(pos.method_10260() - z));
    }
}
