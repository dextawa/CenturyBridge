package top.dext.centurybridge.step.v1_21_5.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_5.facade.GlStateManager$class_1030;
import top.dext.centurybridge.step.v1_21_5.facade.GlStateManager$class_4534;
import top.dext.centurybridge.step.v1_21_5.facade.GlStateManager$class_4535;

/**
 * 1.21.4's RenderSystem.blendFunc / blendFuncSeparate / logicOp over GlStateManager's SourceFactor / DestFactor /
 * LogicOp enums (facades GlStateManager$class_4535 / $class_4534 / $class_1030), which 1.21.5 dropped with the enums.
 * 1.21.4's bodies (javap -c -p 1.21.4 RenderSystem): the render-thread check, then GlStateManager with each constant's GL
 * value. They go through 1.21.5's own cached GlStateManager entry points (_blendFuncSeparate, _logicOp; _blendFunc via
 * {@link CbGlStateManager}), so 1.21.5's state caches stay true and its render pipeline re-applies its own blend and
 * logic-op state over them at its next pass exactly as it would over its own calls.
 */
@Environment(EnvType.CLIENT)
public final class CbGlRenderSystem {

    private CbGlRenderSystem() {
    }

    /**
     * 1.21.4 RenderSystem.recordRenderCall(call): added the call to a queue the render thread replayed in flipFrame
     * (each frame, before the buffer swap) and in finishInitialization, from any thread, the render thread included
     * (javap -c -p 1.21.4 RenderSystem). 1.21.5 has no such queue; the call is scheduled on Minecraft's task queue
     * (BlockableEventLoop.schedule: always queued, never run inline, as 1.21.4), which 1.21.5 drains on the render
     * thread every frame (at another point of the frame).
     */
    public static void recordRenderCall(top.dext.centurybridge.step.v1_21_5.facade.RenderCall call) {
        net.minecraft.class_310.method_1551().method_63588(call::execute);
    }

    public static void blendFunc(GlStateManager$class_4535 src, GlStateManager$class_4534 dst) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        CbGlStateManager._blendFunc(src.value, dst.value);
    }

    public static void blendFuncSeparate(GlStateManager$class_4535 srcRgb, GlStateManager$class_4534 dstRgb, GlStateManager$class_4535 srcAlpha,
            GlStateManager$class_4534 dstAlpha) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._blendFuncSeparate(srcRgb.value, dstRgb.value, srcAlpha.value, dstAlpha.value);
    }

    public static void logicOp(GlStateManager$class_1030 op) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._logicOp(op.field_5108);
    }

    // ---- 1.21.4 RenderSystem state calls 1.21.5 removed (javap -c -p 1.21.4 RenderSystem): the same GlStateManager
    // calls, now 1.21.5's com.mojang.blaze3d.opengl.GlStateManager (cached, so 1.21.5's caches stay true); the
    // render-thread check where 1.21.4 had it ----

    public static void enableDepthTest() {
        com.mojang.blaze3d.opengl.GlStateManager._enableDepthTest();
    }

    public static void disableDepthTest() {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._disableDepthTest();
    }

    public static void depthFunc(int func) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._depthFunc(func);
    }

    public static void depthMask(boolean mask) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._depthMask(mask);
    }

    public static void enableBlend() {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._enableBlend();
    }

    public static void disableBlend() {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._disableBlend();
    }

    /** blendFuncSeparate(SRC_ALPHA, ONE_MINUS_SRC_ALPHA, ONE, ZERO) (GL 0x302, 0x303, 1, 0) */
    public static void defaultBlendFunc() {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._blendFuncSeparate(0x302, 0x303, 1, 0);
    }

    public static void blendEquation(int mode) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        CbGlStateManager._blendEquation(mode);
    }

    public static void activeTexture(int texture) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.opengl.GlStateManager._activeTexture(texture);
    }

    public static void texParameter(int target, int name, int value) {
        com.mojang.blaze3d.opengl.GlStateManager._texParameter(target, name, value);
    }

    public static void bindTexture(int texture) {
        com.mojang.blaze3d.opengl.GlStateManager._bindTexture(texture);
    }

    public static void viewport(int x, int y, int w, int h) {
        com.mojang.blaze3d.opengl.GlStateManager._viewport(x, y, w, h);
    }

    public static void clearColor(float r, float g, float b, float a) {
        CbGlStateManager._clearColor(r, g, b, a);
    }

    public static void clear(int mask) {
        com.mojang.blaze3d.opengl.GlStateManager._clear(mask);
    }

    /**
     * isInInit || isOnRenderThread. 1.21.5 has no initialization window: 1.21.4 set isInInit between
     * beginInitialization / finishInitialization in client Main, on the thread initRenderThread had just made the
     * render thread, so there the answer was the render-thread test's; only another thread during that window
     * differs (release_notes_known.md).
     */
    public static boolean isOnRenderThreadOrInit() {
        return com.mojang.blaze3d.systems.RenderSystem.isOnRenderThread();
    }

    /** see isOnRenderThreadOrInit */
    public static void assertOnRenderThreadOrInit() {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
    }

    // ---- 1.21.4 RenderSystem's current shader (javap -c -p 1.21.4 RenderSystem: private static CompiledShaderProgram
    // shader; setShader(program) / clearShader / getShader each check the render thread, then write / null / read it).
    // 1.21.5 has no current shader: a draw takes its program from its RenderPipeline. CompiledShaderProgram (class_5944)
    // stays through 26.2 (GlProgram), so the state is kept here with 1.21.4's bodies. In 1.21.4 the field was read by
    // BufferUploader.drawWithShader / VertexBuffer.drawWithShader; those are rows of their own (gone at 1.21.5), so a
    // shader set here affects only the converted code that reads it back. setShader(ShaderProgram) (1.21.4: compiled
    // the program, then wrote the same field) belongs with the ShaderProgram facade and must write this field. ----

    private static net.minecraft.class_5944 shader;

    public static void setShader(net.minecraft.class_5944 program) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        shader = program;
    }

    public static void clearShader() {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        shader = null;
    }

    public static net.minecraft.class_5944 getShader() {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        return shader;
    }

    // ---- 1.21.4 RenderSystem shader textures (javap -c -p 1.21.4: int[12] shaderTextures of GL ids). 1.21.5 keeps the
    // twelve slots as GpuTextures (setShaderTexture(int, GpuTexture) / getShaderTexture(int), same render-thread check
    // and range test) and its draws bind them as Sampler0..11 (CompositeRenderType.draw, CompiledShaderProgram). ----

    /**
     * 1.21.4 setShaderTexture(unit, location): Minecraft.getTextureManager().getTexture(location).getId() into the slot.
     * 1.21.5: the same texture's GpuTexture into the same slot, as 1.21.5's own TextureStateShard does
     * (getTexture(location).getTexture(), javap class_4668$class_4683).
     */
    public static void setShaderTexture(int unit, net.minecraft.class_2960 location) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        if (unit >= 0 && unit < 12) {
            net.minecraft.class_1044 texture = net.minecraft.class_310.method_1551().method_1531().method_4619(location);
            com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(unit, texture.method_68004());
        }
    }

    /**
     * 1.21.4 getShaderTexture(unit): the slot's GL id, 0 out of range (and for a slot never set). 1.21.5: the GL id of the
     * slot's GpuTexture (GlTexture.glId, class_10868.method_68427, kept through 26.2), 0 for an empty slot or a texture
     * of another backend.
     */
    public static int getShaderTexture(int unit) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        com.mojang.blaze3d.textures.GpuTexture texture = com.mojang.blaze3d.systems.RenderSystem.getShaderTexture(unit);
        return texture instanceof net.minecraft.class_10868 gl ? gl.method_68427() : 0;
    }
}
