package top.dext.centurybridge.step.v1_21.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.6 model rendering on 1.21. 1.20.6 tinted a model with four floats (renderToBuffer(pose, consumer,
 * light, overlay, r, g, b, a), ModelPart.render, Cube.compile, BookModel.render); 1.21 passes one packed
 * ARGB int, which is what reaches the vertex consumer in both releases (1.20.6's BufferBuilder stored each
 * channel as the byte (int) (f * 255)). Packing therefore keeps exactly those bytes; unpacking hands a
 * 1.20.6 override the channel over 255, and k / 255f * 255f is k again for every byte (checked for all
 * 256), so an override handing the floats back to super packs to the very color vanilla passed.
 */
@Environment(EnvType.CLIENT)
public final class CbClientModels {

    private CbClientModels() {
    }

    /** the 1.20.6 floats (r, g, b, a) as 1.21's ARGB int: per channel the byte 1.20.6's consumer wrote */
    public static int cbColor(float r, float g, float b, float a) {
        return (channel(a) << 24) | (channel(r) << 16) | (channel(g) << 8) | channel(b);
    }

    private static int channel(float f) {
        return ((int) (f * 255.0F)) & 0xFF;
    }

    public static float cbRed(int color) {
        return ((color >> 16) & 0xFF) / 255.0F;
    }

    public static float cbGreen(int color) {
        return ((color >> 8) & 0xFF) / 255.0F;
    }

    public static float cbBlue(int color) {
        return (color & 0xFF) / 255.0F;
    }

    public static float cbAlpha(int color) {
        return ((color >>> 24) & 0xFF) / 255.0F;
    }

    /** an opaque color of three 1.20.6 floats (the colorable models' setColor) */
    public static int cbOpaque(float r, float g, float b) {
        return cbColor(r, g, b, 1.0F);
    }

    // ------------------------------------------------------------------ Model.renderToBuffer (class_3879.method_2828)

    /** Model.renderToBuffer(pose, consumer, light, overlay, r, g, b, a) of 1.20.6, as a model class declares it */
    public interface CbLegacyModel {
        void method_2828(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, int light, int overlay,
                         float r, float g, float b, float a);
    }

    public static void cbRenderToBuffer(CbLegacyModel model, net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer,
                                        int light, int overlay, int color) {
        model.method_2828(pose, consumer, light, overlay, cbRed(color), cbGreen(color), cbBlue(color), cbAlpha(color));
    }

    /** ModelPart.Cube.compile(pose, consumer, light, overlay, r, g, b, a) of 1.20.6 */
    public interface CbLegacyCube {
        void method_32089(net.minecraft.class_4587.class_4665 pose, net.minecraft.class_4588 consumer, int light, int overlay,
                          float r, float g, float b, float a);
    }

    public static void cbCompile(CbLegacyCube cube, net.minecraft.class_4587.class_4665 pose, net.minecraft.class_4588 consumer,
                                 int light, int overlay, int color) {
        cube.method_32089(pose, consumer, light, overlay, cbRed(color), cbGreen(color), cbBlue(color), cbAlpha(color));
    }

    /** BookModel.render(pose, consumer, light, overlay, r, g, b, a) of 1.20.6 */
    public interface CbLegacyBook {
        void method_24184(net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer, int light, int overlay,
                          float r, float g, float b, float a);
    }

    public static void cbBookRender(CbLegacyBook book, net.minecraft.class_4587 pose, net.minecraft.class_4588 consumer,
                                    int light, int overlay, int color) {
        book.method_24184(pose, consumer, light, overlay, cbRed(color), cbGreen(color), cbBlue(color), cbAlpha(color));
    }

    // ------------------------------------------------------------------ colorable models' setColor

    /**
     * ColorableAgeableListModel / ColorableHierarchicalModel.setColor(r, g, b) of 1.20.6 kept three floats
     * and multiplied the floats renderToBuffer was given by them; 1.21's setColor(int) keeps an ARGB int and
     * multiplies the int it is given (ARGB32.multiply). A 1.20.6 setColor keeps its floats next to 1.21's
     * int (mixin/CbClientColorableModelMixin), and while they are set renderToBuffer multiplies in floats,
     * as 1.20.6 did; 1.21's own setColor(int) clears them.
     */
    public interface CbLegacyColorable {
        void cb$setLegacyColor(float r, float g, float b);
    }

    /** a 1.20.6 call of setColor(r, g, b): the model's own override when it has one, else the kept floats */
    public static void cbSetColor(net.minecraft.class_4593<?> model, float r, float g, float b) {
        if (model instanceof CbLegacyAgeableSetColor legacy) {
            legacy.method_22955(r, g, b);
        } else {
            ((CbLegacyColorable) model).cb$setLegacyColor(r, g, b);
        }
    }

    public static void cbSetColor(net.minecraft.class_4594<?> model, float r, float g, float b) {
        if (model instanceof CbLegacyHierarchicalSetColor legacy) {
            legacy.method_22956(r, g, b);
        } else {
            ((CbLegacyColorable) model).cb$setLegacyColor(r, g, b);
        }
    }

    /** a 1.20.6 subclass's super.setColor(r, g, b): the model's own 1.20.6 setColor, the kept floats */
    public static void cbSuperSetColor(net.minecraft.class_4593<?> model, float r, float g, float b) {
        ((CbLegacyColorable) model).cb$setLegacyColor(r, g, b);
    }

    public static void cbSuperSetColor(net.minecraft.class_4594<?> model, float r, float g, float b) {
        ((CbLegacyColorable) model).cb$setLegacyColor(r, g, b);
    }

    /** 1.20.6's renderToBuffer tint: each float of the color passed times the float kept, alpha as passed */
    public static int cbLegacyTint(int passed, float r, float g, float b) {
        return cbColor(cbRed(passed) * r, cbGreen(passed) * g, cbBlue(passed) * b, cbAlpha(passed));
    }

    /** a 1.20.6 colorable model subclass's setColor(r, g, b), reached by 1.21's setColor(int) */
    public interface CbLegacyAgeableSetColor {
        void method_22955(float r, float g, float b);
    }

    public static void cbAgeableSetColor(CbLegacyAgeableSetColor model, int color) {
        model.method_22955(cbRed(color), cbGreen(color), cbBlue(color));
    }

    public interface CbLegacyHierarchicalSetColor {
        void method_22956(float r, float g, float b);
    }

    public static void cbHierarchicalSetColor(CbLegacyHierarchicalSetColor model, int color) {
        model.method_22956(cbRed(color), cbGreen(color), cbBlue(color));
    }

    // ------------------------------------------------------------------ BreezeWindLayer, MobRenderer

    /**
     * BreezeWindLayer(parent) of 1.20.6 built its breeze model from the code-defined layer at class load; 1.21's
     * (context, parent) bakes ModelLayers.BREEZE_WIND, which is that same layer definition, from the context.
     * A context made the way 1.21's EntityRenderDispatcher makes its own, from the running game.
     */
    public static net.minecraft.class_5617.class_5618 cbRendererContext() {
        net.minecraft.class_310 mc = net.minecraft.class_310.method_1551();
        net.minecraft.class_898 dispatcher = mc.method_1561();
        return new net.minecraft.class_5617.class_5618(dispatcher, mc.method_1480(), mc.method_1541(),
            dispatcher.method_43336(), mc.method_1478(), mc.method_31974(), mc.field_1772);
    }

    /** MobRenderer.LEASH_RENDER_STEPS of 1.20.6 (EntityRenderer's since 1.21, the same 24) */
    public static final int field_32940 = 24;

    /** MobRenderer's super side, kept by mixin/CbClientMobRendererMixin */
    public interface CbMobRendererSuper {
        boolean cb$superShouldRender(net.minecraft.class_1308 mob, net.minecraft.class_4604 frustum, double x, double y, double z);

        void cb$superRender(net.minecraft.class_1308 mob, float yaw, float partialTick, net.minecraft.class_4587 pose,
                            net.minecraft.class_4597 buffers, int light);
    }

    /**
     * MobRenderer.shouldRender(mob, ...) of 1.20.6 (EntityRenderer's test, or the leash holder in view) is
     * EntityRenderer.shouldRender since 1.21, which checks the leash holder of any Leashable itself: the call,
     * dispatched as before through the renderer's own override.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean cbShouldRender(net.minecraft.class_927 renderer, net.minecraft.class_1308 mob,
                                         net.minecraft.class_4604 frustum, double x, double y, double z) {
        return ((net.minecraft.class_897) renderer).method_3933(mob, frustum, x, y, z);
    }

    public static boolean cbSuperShouldRender(net.minecraft.class_927 renderer, net.minecraft.class_1308 mob,
                                              net.minecraft.class_4604 frustum, double x, double y, double z) {
        return ((CbMobRendererSuper) renderer).cb$superShouldRender(mob, frustum, x, y, z);
    }

    /**
     * MobRenderer.render(mob, ...) of 1.20.6 (LivingEntityRenderer.render, then the leash) is gone since 1.21,
     * where EntityRenderer.render draws the leash of any Leashable: LivingEntityRenderer's render is the whole of
     * it. The call, dispatched through the renderer's own override.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void cbMobRender(net.minecraft.class_927 renderer, net.minecraft.class_1308 mob, float yaw, float partialTick,
                                   net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, int light) {
        ((net.minecraft.class_897) renderer).method_3936(mob, yaw, partialTick, pose, buffers, light);
    }

    public static void cbSuperMobRender(net.minecraft.class_927 renderer, net.minecraft.class_1308 mob, float yaw,
                                        float partialTick, net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers,
                                        int light) {
        ((CbMobRendererSuper) renderer).cb$superRender(mob, yaw, partialTick, pose, buffers, light);
    }
}
