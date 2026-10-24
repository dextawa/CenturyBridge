package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1 FogRenderer's statics over 1.21.2's.
 *
 * <p>1.21.1 set the fog as render state: setupColor computed the fog colour into FogRenderer's fogRed/Green/Blue and
 * cleared to it (RenderSystem.clearColor), levelFogColor set the shader fog colour from them, setupFog set the shader
 * fog's start, end and shape (keeping its colour), setupNoFog pushed the fog start out of reach. 1.21.2 computes the same
 * values and hands them on as FogParameters (start, end, shape, colour), which RenderSystem.setShaderFog sets: the fog
 * colour is computeFogColor (the same computation), setupFog answers the parameters for a colour it is given, and
 * FogParameters.NO_FOG is no fog. Here each 1.21.1 call sets 1.21.2's shader fog to what 1.21.1's left it as; the fog
 * colour of the last setupColor is kept as 1.21.1 kept it.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderFog {

    private static float red;
    private static float green;
    private static float blue;

    private CbRenderFog() {
    }

    /** 1.21.1 FogRenderer.setupColor(camera, partialTick, level, renderDistance, darkenWorldAmount) */
    public static void method_3210(net.minecraft.class_4184 camera, float partialTick, net.minecraft.class_638 level, int renderDistance, float darkenWorldAmount) {
        org.joml.Vector4f color = net.minecraft.class_758.method_62185(camera, partialTick, level, renderDistance, darkenWorldAmount);
        red = color.x();
        green = color.y();
        blue = color.z();
        com.mojang.blaze3d.systems.RenderSystem.clearColor(red, green, blue, 0.0F);
    }

    /** 1.21.1 FogRenderer.levelFogColor(): the shader fog colour from the last setupColor */
    public static void method_23792() {
        net.minecraft.class_9958 fog = com.mojang.blaze3d.systems.RenderSystem.getShaderFog();
        com.mojang.blaze3d.systems.RenderSystem.setShaderFog(new net.minecraft.class_9958(fog.comp_3009(), fog.comp_3010(), fog.comp_3011(), red, green, blue, 1.0F));
    }

    /** 1.21.1 FogRenderer.setupNoFog() */
    public static void method_3212() {
        com.mojang.blaze3d.systems.RenderSystem.setShaderFog(net.minecraft.class_9958.field_53065);
    }

    /** 1.21.1 FogRenderer.setupFog(camera, fogMode, renderDistance, thickFog, partialTick): the fog's distances and shape, its colour kept */
    public static void method_3211(net.minecraft.class_4184 camera, net.minecraft.class_758.class_4596 fogMode, float renderDistance, boolean thickFog, float partialTick) {
        net.minecraft.class_9958 current = com.mojang.blaze3d.systems.RenderSystem.getShaderFog();
        org.joml.Vector4f color = new org.joml.Vector4f(current.comp_3012(), current.comp_3013(), current.comp_3014(), current.comp_3015());
        com.mojang.blaze3d.systems.RenderSystem.setShaderFog(net.minecraft.class_758.method_3211(camera, fogMode, color, renderDistance, thickFog, partialTick));
    }
}
