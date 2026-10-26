package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1 RenderTarget's flagged resize / createBuffers / clear / blitToScreen, on 1.21.2's unflagged ones plus what the
 * flag did (CbRenderTargets): read glGetError once afterwards / disable blending first.
 * And 1.21.1's blit through GameRenderer.blitShader: 1.21.1's blitToScreen drew with the shader in that field
 * (binding the colour texture as DiffuseSampler), 1.21.2's with the blit_screen program (as InSampler). When a mod set
 * the field (CbRenderGame.setBlitShader) the blit draws with that shader, the texture bound under 1.21.1's sampler
 * name too; the quad (POSITION, the same four vertices) is the same in both (CbRenderTargetMixinHooks, default priority).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_276.class, priority = CbVanillaReads.PRIORITY)
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
