package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.21.1 RenderTarget's flagged resize / createBuffers / clear / blitToScreen, on 1.21.2's unflagged ones plus what the
 * flag did (CbRenderTargets): read glGetError once afterwards / disable blending first.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_276.class)
public abstract class CbRenderTargetMixin {

    public void method_1234(int width, int height, boolean clearError) {
        ((net.minecraft.class_276) (Object) this).method_1234(width, height);
        if (clearError) {
            com.mojang.blaze3d.platform.GlStateManager._getError();
        }
    }

    public void method_1231(int width, int height, boolean clearError) {
        ((net.minecraft.class_276) (Object) this).method_1231(width, height);
        if (clearError) {
            com.mojang.blaze3d.platform.GlStateManager._getError();
        }
    }

    public void method_1230(boolean clearError) {
        ((net.minecraft.class_276) (Object) this).method_1230();
        if (clearError) {
            com.mojang.blaze3d.platform.GlStateManager._getError();
        }
    }

    public void method_22594(int width, int height, boolean disableBlend) {
        if (disableBlend) {
            com.mojang.blaze3d.platform.GlStateManager._disableBlend();
        }
        ((net.minecraft.class_276) (Object) this).method_1233(width, height);
    }
}
