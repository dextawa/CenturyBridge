package top.dext.centurybridge.step.v1_21_2.rt;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Pieces of 1.21.1's post-processing stack (facade PostChain / PostPass / EffectInstance / EffectProgram) that 1.21.2
 * narrowed in classes it keeps:
 * - RenderTarget's flagged clear / resize, as the facades call them (1.21.1 bodies: 1.21.2's unflagged call, then one
 *   glGetError when the flag is set -- see CbRenderTargets; the unflagged call is virtual, so a mod RenderTarget's
 *   flagged override is reached through its override hook as from 1.21.1's call);
 * - TextureManager.bindForSetup(location), gone in 1.21.2: 1.21.1 bound the texture (registered on first use as a
 *   SimpleTexture, which 1.21.2's getTexture does the same way) on the render thread, or recorded the call;
 * - Uniform: the constructor taking its parent Shader (1.21.1 used the parent only for markDirty, whose flag no
 *   1.21.1 reader consults; 1.21.2's Uniform has no parent), and the attribute statics 1.21.1's Uniform forwarded to
 *   GlStateManager.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderPost {

    private CbRenderPost() {
    }

    public static void clear(net.minecraft.class_276 target, boolean clearError) {
        target.method_1230();
        if (clearError) {
            GlStateManager._getError();
        }
    }

    public static void resize(net.minecraft.class_276 target, int width, int height, boolean clearError) {
        target.method_1234(width, height);
        if (clearError) {
            GlStateManager._getError();
        }
    }

    /** TextureManager.bindForSetup(location) */
    public static void bindForSetup(net.minecraft.class_1060 textureManager, net.minecraft.class_2960 location) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> textureManager.method_4619(location).method_23207());
        } else {
            textureManager.method_4619(location).method_23207();
        }
    }

    /** new MapRenderer(textureManager, decorationTextures): 1.21.1's renderer owned its map textures, a texture manager of its own */
    public static net.minecraft.class_330 mapRenderer(net.minecraft.class_1060 textureManager, net.minecraft.class_9443 decorationTextures) {
        return new net.minecraft.class_330(decorationTextures, new net.minecraft.class_10093(textureManager));
    }

    /** new Uniform(name, type, count, parent) */
    public static net.minecraft.class_284 uniform(String name, int type, int count, top.dext.centurybridge.step.v1_21_2.facade.Shader parent) {
        return new net.minecraft.class_284(name, type, count);
    }

    /** Uniform.glGetAttribLocation(program, name) */
    public static int method_22097(int program, CharSequence name) {
        return GlStateManager._glGetAttribLocation(program, name);
    }

    /** Uniform.glBindAttribLocation(program, index, name) */
    public static void method_34419(int program, int index, CharSequence name) {
        GlStateManager._glBindAttribLocation(program, index, name);
    }
}
