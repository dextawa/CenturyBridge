package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1 RenderSystem statics gone in 1.21.2 (staticRedirects, each under its 1.21.1 name and shape), on 1.21.2's API:
 * - fog: 1.21.1 kept start / end / shape / colour as separate fields with a setter and a getter each; 1.21.2 keeps one
 *   FogParameters record (setShaderFog / getShaderFog). A setter replaces its component of the current record, a getter
 *   reads it. getShaderFogColor answered 1.21.1's internal array; here a new array of the current colour (writing into
 *   it no longer changes the fog -- the record is immutable).
 * - projection: 1.21.1 set the projection matrix with a VertexSorting; 1.21.2 with a ProjectionType, which carries its
 *   sorting (PERSPECTIVE: distance to origin, ORTHOGRAPHIC: orthographic Z). The type whose sorting is the one given is
 *   used; a sorting neither has (VertexSorting.byDistance(x, y, z)) is a perspective one, as every 1.21.1 caller of such
 *   a sorting drew in perspective. getVertexSorting answers the current type's sorting.
 * - the rest carry their 1.21.1 bodies over to 1.21.2's calls.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderSystem {

    private CbRenderSystem() {
    }

    private static net.minecraft.class_9958 fog() {
        return com.mojang.blaze3d.systems.RenderSystem.getShaderFog();
    }

    private static void fog(float start, float end, net.minecraft.class_6854 shape, float r, float g, float b, float a) {
        com.mojang.blaze3d.systems.RenderSystem.setShaderFog(new net.minecraft.class_9958(start, end, shape, r, g, b, a));
    }

    public static void setShaderFogStart(float start) {
        net.minecraft.class_9958 f = fog();
        fog(start, f.comp_3010(), f.comp_3011(), f.comp_3012(), f.comp_3013(), f.comp_3014(), f.comp_3015());
    }

    public static float getShaderFogStart() {
        return fog().comp_3009();
    }

    public static void setShaderFogEnd(float end) {
        net.minecraft.class_9958 f = fog();
        fog(f.comp_3009(), end, f.comp_3011(), f.comp_3012(), f.comp_3013(), f.comp_3014(), f.comp_3015());
    }

    public static float getShaderFogEnd() {
        return fog().comp_3010();
    }

    public static void setShaderFogShape(net.minecraft.class_6854 shape) {
        net.minecraft.class_9958 f = fog();
        fog(f.comp_3009(), f.comp_3010(), shape, f.comp_3012(), f.comp_3013(), f.comp_3014(), f.comp_3015());
    }

    public static net.minecraft.class_6854 getShaderFogShape() {
        return fog().comp_3011();
    }

    public static void setShaderFogColor(float r, float g, float b, float a) {
        net.minecraft.class_9958 f = fog();
        fog(f.comp_3009(), f.comp_3010(), f.comp_3011(), r, g, b, a);
    }

    /** 1.21.1: setShaderFogColor(r, g, b, 1.0F) */
    public static void setShaderFogColor(float r, float g, float b) {
        setShaderFogColor(r, g, b, 1.0F);
    }

    public static float[] getShaderFogColor() {
        net.minecraft.class_9958 f = fog();
        return new float[] {f.comp_3012(), f.comp_3013(), f.comp_3014(), f.comp_3015()};
    }

    private static net.minecraft.class_10366 projectionType(net.minecraft.class_8251 sorting) {
        for (net.minecraft.class_10366 t : net.minecraft.class_10366.values()) {
            if (t.method_65045() == sorting) {
                return t;
            }
        }
        for (net.minecraft.class_10366 t : net.minecraft.class_10366.values()) {
            if (t.method_65045() == net.minecraft.class_8251.field_43360) {
                return t;
            }
        }
        throw new IllegalStateException("no perspective ProjectionType");
    }

    /** 1.21.1: off the render thread, queued (recordRenderCall); on it, set at once */
    public static void setProjectionMatrix(org.joml.Matrix4f matrix, net.minecraft.class_8251 sorting) {
        org.joml.Matrix4f copy = new org.joml.Matrix4f(matrix);
        net.minecraft.class_10366 type = projectionType(sorting);
        if (!com.mojang.blaze3d.systems.RenderSystem.isOnRenderThread()) {
            com.mojang.blaze3d.systems.RenderSystem.recordRenderCall(() -> com.mojang.blaze3d.systems.RenderSystem.setProjectionMatrix(copy, type));
        } else {
            com.mojang.blaze3d.systems.RenderSystem.setProjectionMatrix(copy, type);
        }
    }

    public static net.minecraft.class_8251 getVertexSorting() {
        return com.mojang.blaze3d.systems.RenderSystem.getProjectionType().method_65045();
    }

    /**
     * 1.21.1 applyModelViewMatrix(): copied the model-view stack into the matrix the shaders were given. 1.21.2 gives
     * the shaders the stack itself at every draw (getModelViewMatrix answers modelViewStack), so what the copy made
     * current -- the stack as it stands -- is what 1.21.2 already uses; there is nothing left to copy.
     */
    public static void applyModelViewMatrix() {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThreadOrInit();
    }

    /** 1.21.1 clear(mask, checkError): GlStateManager._clear(mask, checkError); 1.21.2's clear has no error check to ask for */
    public static void clear(int mask, boolean checkError) {
        com.mojang.blaze3d.systems.RenderSystem.clear(mask);
    }

    /** 1.21.1 flipFrame(window): 1.21.2's, with no Tracy frame capture (1.21.1 had none) */
    public static void flipFrame(long window) {
        com.mojang.blaze3d.systems.RenderSystem.flipFrame(window, null);
    }

    public static void glGenBuffers(Consumer<Integer> consumer) {
        if (!com.mojang.blaze3d.systems.RenderSystem.isOnRenderThread()) {
            com.mojang.blaze3d.systems.RenderSystem.recordRenderCall(() -> consumer.accept(com.mojang.blaze3d.platform.GlStateManager._glGenBuffers()));
        } else {
            consumer.accept(com.mojang.blaze3d.platform.GlStateManager._glGenBuffers());
        }
    }

    public static void glGenVertexArrays(Consumer<Integer> consumer) {
        if (!com.mojang.blaze3d.systems.RenderSystem.isOnRenderThread()) {
            com.mojang.blaze3d.systems.RenderSystem.recordRenderCall(() -> consumer.accept(com.mojang.blaze3d.platform.GlStateManager._glGenVertexArrays()));
        } else {
            consumer.accept(com.mojang.blaze3d.platform.GlStateManager._glGenVertexArrays());
        }
    }

    /** 1.21.1 runAsFancy: with shader transparency (Fabulous) on, runs with the graphics mode set to Fancy, then back */
    public static void runAsFancy(Runnable task) {
        if (!net.minecraft.class_310.method_29611()) {
            task.run();
            return;
        }
        net.minecraft.class_7172<net.minecraft.class_5365> mode = net.minecraft.class_310.method_1551().field_1690.method_42534();
        net.minecraft.class_5365 was = mode.method_41753();
        mode.method_41748(net.minecraft.class_5365.field_25428);
        task.run();
        mode.method_41748(was);
    }

    /** 1.21.1 _setShaderTexture(unit, location): the texture's id into the unit, as setShaderTexture does in 1.21.2 */
    public static void _setShaderTexture(int unit, net.minecraft.class_2960 location) {
        com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(unit, location);
    }

    public static void _setShaderTexture(int unit, int id) {
        com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(unit, id);
    }
}
