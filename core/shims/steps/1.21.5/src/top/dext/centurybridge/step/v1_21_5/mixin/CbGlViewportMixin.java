package top.dext.centurybridge.step.v1_21_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.21.4's GlStateManager.Viewport (facade GlStateManager$class_1040) answered the last viewport _viewport set; 1.21.5's
 * _viewport (the only glViewport call in 1.21.5) no longer records it. Recorded here at its head, so the facade's
 * getters answer as 1.21.4's did. The call itself runs as 1.21.5 has it.
 */
@Environment(EnvType.CLIENT)
@Mixin(com.mojang.blaze3d.opengl.GlStateManager.class)
public abstract class CbGlViewportMixin {

    @Inject(method = "_viewport(IIII)V", at = @At("HEAD"), remap = false)
    private static void cb$recordViewport(int x, int y, int width, int height, CallbackInfo ci) {
        top.dext.centurybridge.step.v1_21_5.facade.GlStateManager$class_1040.cb$record(x, y, width, height);
    }
}
