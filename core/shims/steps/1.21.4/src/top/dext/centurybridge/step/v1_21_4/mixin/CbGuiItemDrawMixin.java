package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_4.rt.CbItemRender;

/**
 * GuiGraphics.renderItem (method_51425) draws its item through ItemRenderer's restored 1.21.3 getModel / render, as
 * 1.21.3's renderItem did ({@link CbItemRender#guiDraw}); the drawn state and its arguments are 1.21.4's.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_332.class)
public abstract class CbGuiItemDrawMixin {

    @Redirect(method = "method_51425", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/class_10444;method_65604(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;II)V"))
    private void cb$drawThroughRestored(net.minecraft.class_10444 state, net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers,
            int light, int overlay, net.minecraft.class_1309 entity, net.minecraft.class_1937 level, net.minecraft.class_1799 stack,
            int x, int y, int seed, int z) {
        CbItemRender.guiDraw(state, pose, buffers, light, overlay, entity, level, stack, seed);
    }
}
