package top.dext.centurybridge.step.v1_20_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import top.dext.centurybridge.step.v1_20_5.mixin.CbClientLightingAccessor;
import top.dext.centurybridge.step.v1_20_5.mixin.CbClientNativeImageAccessor;
import top.dext.centurybridge.step.v1_20_5.mixin.CbClientPoseInvoker;

/**
 * 1.20.4's render-system matrix API, glyph metrics and vertex helpers on 1.20.5 (s4client, group A).
 * <p>
 * 1.20.5 moved the camera rotation out of the PoseStack that 1.20.4 threaded through level rendering:
 * RenderSystem's model-view stack is a JOML Matrix4fStack, shaders lost the inverse view rotation, and
 * the PoseStack.Pose replaced (Matrix4f, Matrix3f) pairs in the vertex helpers. Each static below is
 * the 1.20.4 member, its body 1.20.4's computed with 1.20.5's API.
 */
@Environment(EnvType.CLIENT)
public final class CbClientRender {

    // ------------------------------------------------------------------ RenderSystem

    /** 1.20.4 RenderSystem.inverseViewRotationMatrix: zero until the first frame sets it, as 1.20.4's static initializer left it */
    private static Matrix3f inverseViewRotationMatrix = new Matrix3f().zero();

    private static CbClientModelViewStack modelViewStack;

    /**
     * 1.20.4 RenderSystem.getModelViewStack(): the model-view PoseStack. 1.20.5's is a Matrix4fStack; the
     * PoseStack answered here pushes, pops and transforms that same stack, so what a 1.20.4 caller does to
     * it is what 1.20.5's applyModelViewMatrix() uploads.
     */
    public static net.minecraft.class_4587 getModelViewStack() {
        org.joml.Matrix4fStack stack = com.mojang.blaze3d.systems.RenderSystem.getModelViewStack();
        CbClientModelViewStack view = modelViewStack;
        if (view == null || view.stack != stack) {
            view = new CbClientModelViewStack(stack);
            modelViewStack = view;
        }
        return view;
    }

    /** 1.20.4 RenderSystem.setInverseViewRotationMatrix(matrix): a copy, set on the render thread */
    public static void setInverseViewRotationMatrix(Matrix3f matrix) {
        Matrix3f copy = new Matrix3f(matrix);
        if (!com.mojang.blaze3d.systems.RenderSystem.isOnRenderThread()) {
            com.mojang.blaze3d.systems.RenderSystem.recordRenderCall(() -> inverseViewRotationMatrix = copy);
        } else {
            inverseViewRotationMatrix = copy;
        }
    }

    /** 1.20.4 RenderSystem.getInverseViewRotationMatrix() */
    public static Matrix3f getInverseViewRotationMatrix() {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        return inverseViewRotationMatrix;
    }

    /**
     * What 1.20.4's GameRenderer.renderLevel set every frame: the inverse of the camera rotation it put on
     * the level PoseStack (x rotation, then y + 180 degrees) -- the rotation 1.20.5's renderLevel builds as
     * its view matrix (mixin/CbClientGameRendererMixin)
     */
    public static void cameraRotation(Matrix4f rotation) {
        setInverseViewRotationMatrix(new Matrix3f(rotation).invert());
    }

    /**
     * 1.20.4 RenderSystem.setupLevelDiffuseLighting(light0, light1, matrix) (GlStateManager's): each light
     * direction transformed by the matrix as a point (w = 1), as 1.20.4 did, then set as the shader lights
     */
    public static void setupLevelDiffuseLighting(Vector3f light0, Vector3f light1, Matrix4f matrix) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        Vector4f a = matrix.transform(new Vector4f(light0, 1.0F));
        Vector4f b = matrix.transform(new Vector4f(light1, 1.0F));
        com.mojang.blaze3d.systems.RenderSystem.setShaderLights(new Vector3f(a.x(), a.y(), a.z()), new Vector3f(b.x(), b.y(), b.z()));
    }

    /** 1.20.4 Lighting.setupLevel(matrix): the level lights through setupLevelDiffuseLighting */
    public static void setupLevel(Matrix4f matrix) {
        setupLevelDiffuseLighting(CbClientLightingAccessor.cb$level0(), CbClientLightingAccessor.cb$level1(), matrix);
    }

    /** 1.20.4 Lighting.setupNetherLevel(matrix): the nether lights through setupLevelDiffuseLighting */
    public static void setupNetherLevel(Matrix4f matrix) {
        setupLevelDiffuseLighting(CbClientLightingAccessor.cb$nether0(), CbClientLightingAccessor.cb$nether1(), matrix);
    }

    /**
     * 1.20.4 ShaderInstance.INVERSE_VIEW_ROTATION_MATRIX: the shader's IViewRotMat uniform, looked up once
     * the program's uniforms are known (1.20.4's constructor did getUniform("IViewRotMat")); null when the
     * shader does not declare it. Read from the uniform map, as a field read never asserted the render thread.
     */
    public static net.minecraft.class_284 inverseViewRotationUniform(net.minecraft.class_5944 shader) {
        return ((top.dext.centurybridge.step.v1_20_5.mixin.CbClientShaderAccessor) shader).cb$uniforms().get("IViewRotMat");
    }

    // ------------------------------------------------------------------ RenderTarget

    /**
     * A 1.20.4 RenderTarget subclass that declares setFilterMode(int) itself: vanilla 1.20.4 called it where
     * 1.20.5 calls setFilterMode(int) (method_58226) and the private (int, boolean) form, so those calls are
     * handed to it (mixin/CbClientRenderTargetMixin)
     */
    public interface CbLegacyFilterMode {
        void method_1232(int mode);
    }

    /** what the layer's RenderTarget mixin adds: 1.20.4's setFilterMode, which always applied the mode */
    public interface RenderTargetApi {
        void cb$forceFilterMode(int mode);
    }

    /** 1.20.4 RenderTarget.setFilterMode(mode): a 1.20.4 subclass's own, else the mode applied unconditionally */
    public static void method_1232(net.minecraft.class_276 target, int mode) {
        if (target instanceof CbLegacyFilterMode legacy) {
            legacy.method_1232(mode);
        } else {
            ((RenderTargetApi) target).cb$forceFilterMode(mode);
        }
    }

    /** a 1.20.4 subclass's super.setFilterMode(mode): 1.20.4's body, which applied the mode unconditionally */
    public static void cbSuperFilterMode(net.minecraft.class_276 target, int mode) {
        // held, so the RenderTarget mixin runs vanilla's body instead of handing the call back to the subclass
        boolean entered = Guard.cbEnter(target);
        try {
            ((RenderTargetApi) target).cb$forceFilterMode(mode);
        } finally {
            if (entered) {
                Guard.cbExit(target);
            }
        }
    }

    // ------------------------------------------------------------------ poses and vertices

    /**
     * a PoseStack.Pose holding copies of 1.20.4's (pose, normal) pair; its normals are trusted, so 1.20.5's
     * transformNormal applies the matrix without renormalizing, as 1.20.4's normal(Matrix3f, ..) did
     */
    public static net.minecraft.class_4587.class_4665 pose(Matrix4f pose, Matrix3f normal) {
        return CbClientPoseInvoker.cb$newPose(new Matrix4f(pose), new Matrix3f(normal));
    }

    /** a Pose over these very matrices (no copy): the model-view stack's live top */
    static net.minecraft.class_4587.class_4665 livePose(Matrix4f pose, Matrix3f normal) {
        return CbClientPoseInvoker.cb$newPose(pose, normal);
    }

    /** a Pose with the given normal matrix (identity position matrix), for 1.20.4's normal(Matrix3f, x, y, z) */
    public static net.minecraft.class_4587.class_4665 normalPose(Matrix3f normal) {
        return CbClientPoseInvoker.cb$newPose(new Matrix4f(), new Matrix3f(normal));
    }

    /** what 1.20.5 hands a 1.20.4 VertexConsumer's normal(Matrix3f, ..) override: the pose's normal matrix */
    public static Matrix3f normalMatrix(net.minecraft.class_4587.class_4665 pose) {
        return pose.method_23762();
    }

    /** the alpha 1.20.4's putBulkData wrote: opaque (1.20.5 takes it as a parameter) */
    public static float opaque() {
        return 1.0F;
    }

    /** a 1.20.4 ArrowRenderer subclass's own vertex(pose, normal, consumer, ..) */
    public interface CbLegacyArrowVertex {
        void method_23153(Matrix4f pose, Matrix3f normal, net.minecraft.class_4588 consumer, int x, int y, int z, float u, float v,
                          int normalX, int normalZ, int normalY, int light);
    }

    /** 1.20.5's ArrowRenderer.vertex(Pose, ..) reaching a 1.20.4 subclass's vertex(Matrix4f, Matrix3f, ..) */
    public static void cbArrowVertex(CbLegacyArrowVertex renderer, net.minecraft.class_4587.class_4665 pose, net.minecraft.class_4588 consumer,
                                     int x, int y, int z, float u, float v, int normalX, int normalZ, int normalY, int light) {
        renderer.method_23153(pose.method_23761(), pose.method_23762(), consumer, x, y, z, u, v, normalX, normalZ, normalY, light);
    }

    // ------------------------------------------------------------------ glyphs

    /**
     * 1.20.4 GlyphBitmap.getBearingY() read as 1.20.5's getBearingTop(): 1.20.4's top edge was getBearingY()
     * and its BakedGlyph drew 3 units higher; 1.20.5's top edge is 7 - getBearingTop(). So
     * bearingY = 10 - bearingTop (both defaults agree: 3 = 10 - 7), and the conversion is its own inverse.
     */
    public static float cbBearingTop(float bearing) {
        return 10.0F - bearing;
    }

    /** 1.20.4 GlyphBitmap.getBearingY() on any glyph: 10 - its 1.20.5 getBearingTop() */
    public static float method_41713(net.minecraft.class_383 glyph) {
        return 10.0F - glyph.method_56130();
    }

    /** a 1.20.4 glyph's GlyphBitmap.super.getBearingY(): 1.20.4's default */
    public static float cbSuperBearingY(net.minecraft.class_383 glyph) {
        return 3.0F;
    }

    /** what the layer's TrueTypeGlyphProvider mixin adds: the 1.20.4 font info it frees on close, as 1.20.4's close() did */
    public interface TrueTypeApi {
        void cb$adoptFontInfo(org.lwjgl.stb.STBTTFontinfo info);
    }

    /**
     * 1.20.4's TrueTypeGlyphProvider(buffer, STBTTFontinfo, ..) read the font through stb_truetype; 1.20.5's
     * reads it through FreeType. The same font bytes as a FreeType face, opened as 1.20.5's
     * TrueTypeGlyphProviderDefinition opens one (shared library, under its lock, unicode charmap).
     */
    public static org.lwjgl.util.freetype.FT_Face cbFace(java.nio.ByteBuffer buffer, org.lwjgl.stb.STBTTFontinfo info) {
        synchronized (net.minecraft.class_9111.field_51483) {
            try (org.lwjgl.system.MemoryStack stack = org.lwjgl.system.MemoryStack.stackPush()) {
                org.lwjgl.PointerBuffer face = stack.mallocPointer(1);
                net.minecraft.class_9111.method_59837(org.lwjgl.util.freetype.FreeType.FT_New_Memory_Face(
                    net.minecraft.class_9111.method_56143(), buffer, 0L, face), "Initializing font face");
                org.lwjgl.util.freetype.FT_Face ft = org.lwjgl.util.freetype.FT_Face.create(face.get());
                net.minecraft.class_9111.method_59837(org.lwjgl.util.freetype.FreeType.FT_Select_Charmap(ft,
                    org.lwjgl.util.freetype.FreeType.FT_ENCODING_UNICODE), "Find unicode charmap");
                return ft;
            }
        }
    }

    /** after the constructor: the provider owns the 1.20.4 font info and frees it on close */
    public static void cbTrueTypeBuilt(net.minecraft.class_395 provider, org.lwjgl.stb.STBTTFontinfo info) {
        ((TrueTypeApi) provider).cb$adoptFontInfo(info);
    }

    /**
     * 1.20.4 NativeImage.copyFromFont(info, glyph, width, height, scaleX, scaleY, shiftX, shiftY, x, y): the
     * glyph rasterized by stb_truetype straight into the image's pixels at (x, y), as 1.20.4 did
     */
    public static void method_4316(net.minecraft.class_1011 image, org.lwjgl.stb.STBTTFontinfo info, int glyph, int width, int height,
                                   float scaleX, float scaleY, float shiftX, float shiftY, int x, int y) {
        if (x < 0 || x + width > image.method_4307() || y < 0 || y + height > image.method_4323()) {
            throw new IllegalArgumentException(String.format(java.util.Locale.ROOT, "Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s",
                x, y, width, height, image.method_4307(), image.method_4323()));
        }
        if (image.method_4318().method_4335() != 1) {
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
        }
        org.lwjgl.stb.STBTruetype.nstbtt_MakeGlyphBitmapSubpixel(info.address(),
            ((CbClientNativeImageAccessor) (Object) image).cb$pixels() + x + (long) y * image.method_4307(),
            width, height, image.method_4307(), scaleX, scaleY, shiftX, shiftY, glyph);
    }

    private CbClientRender() {
    }
}
