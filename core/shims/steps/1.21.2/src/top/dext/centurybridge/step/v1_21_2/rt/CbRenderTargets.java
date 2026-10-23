package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1 RenderTarget / GlStateManager members 1.21.2 narrowed (see CbRenderTargetMixin for the put-back members).
 *
 * 1.21.1's resize / createBuffers / clear took a clearError flag: GlStateManager._clear(mask, checkError) read
 * glGetError once after the glClear when it was set (every vanilla caller passed Minecraft.ON_OSX). 1.21.2 dropped the
 * flag and the read. blitToScreen(width, height, disableBlend) disabled blending before drawing when the flag was set
 * (1.21.1's two-argument blitToScreen passed true); 1.21.2's blitToScreen(width, height) leaves blending as it is.
 *
 * A mod RenderTarget overriding one of the flagged shapes is reached from 1.21.2's unflagged one through the override
 * hooks below (bridge, guarded), with the flag 1.21.1's callers passed.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderTargets {

    private CbRenderTargets() {
    }

    /** GlStateManager._clear(mask, checkError) */
    public static void _clear(int mask, boolean checkError) {
        com.mojang.blaze3d.platform.GlStateManager._clear(mask);
        if (checkError) {
            com.mojang.blaze3d.platform.GlStateManager._getError();
        }
    }

    /** GlStateManager.glShaderSource(shader, lines): 1.21.1 concatenated the lines as they are (no separator) */
    public static void glShaderSource(int shader, java.util.List<String> lines) {
        com.mojang.blaze3d.platform.GlStateManager.glShaderSource(shader, String.join("", lines));
    }

    /** new TextureTarget(width, height, useDepth, clearError): 1.21.1's constructor resized with the flag */
    public static net.minecraft.class_6367 textureTarget(int width, int height, boolean useDepth, boolean clearError) {
        net.minecraft.class_6367 t = new net.minecraft.class_6367(width, height, useDepth);
        if (clearError) {
            com.mojang.blaze3d.platform.GlStateManager._getError();
        }
        return t;
    }

    /** a 1.21.1 RenderTarget overriding resize(width, height, clearError) */
    public interface Resize {
        void method_1234(int width, int height, boolean clearError);
    }

    /** ... createBuffers(width, height, clearError) */
    public interface CreateBuffers {
        void method_1231(int width, int height, boolean clearError);
    }

    /** ... clear(clearError) */
    public interface Clear {
        void method_1230(boolean clearError);
    }

    /** ... blitToScreen(width, height, disableBlend) */
    public interface Blit {
        void method_22594(int width, int height, boolean disableBlend);
    }

    public static void resize(Resize self, int width, int height) {
        self.method_1234(width, height, net.minecraft.class_310.field_1703);
    }

    public static void createBuffers(CreateBuffers self, int width, int height) {
        self.method_1231(width, height, net.minecraft.class_310.field_1703);
    }

    public static void clear(Clear self) {
        self.method_1230(net.minecraft.class_310.field_1703);
    }

    public static void blit(Blit self, int width, int height) {
        self.method_22594(width, height, true);
    }
}
