package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 1.21.1 RenderTarget's flagged resize / createBuffers / clear / blitToScreen, on 1.21.2's unflagged ones plus what the
 * flag did (CbRenderTargets): read glGetError once afterwards / disable blending first.
 * And 1.21.1's blit through GameRenderer.blitShader: 1.21.1's blitToScreen drew with the shader in that field
 * (binding the colour texture as DiffuseSampler), 1.21.2's with the blit_screen program (as InSampler). When a mod set
 * the field (CbRenderGame.setBlitShader) the blit draws with that shader, the texture bound under 1.21.1's sampler
 * name too; the quad (POSITION, the same four vertices) is the same in both.
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

    @Shadow
    protected int field_1475;

    @Redirect(method = "method_1233", require = 1, at = @At(value = "INVOKE",
        target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShader(Lnet/minecraft/class_10156;)Lnet/minecraft/class_5944;"))
    private net.minecraft.class_5944 cb$blitShader(net.minecraft.class_10156 program) {
        net.minecraft.class_757 renderer = net.minecraft.class_310.method_1551().field_1773;
        net.minecraft.class_5944 legacy = renderer == null ? null : ((top.dext.centurybridge.step.v1_21_2.rt.CbRenderGame.Blit) renderer).cb$blitShader();
        if (legacy == null) {
            return com.mojang.blaze3d.systems.RenderSystem.setShader(program);
        }
        com.mojang.blaze3d.systems.RenderSystem.setShader(legacy);
        legacy.method_62899("DiffuseSampler", this.field_1475);
        return legacy;
    }
}
