package top.dext.centurybridge.step.v26_2.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26BlockOutlineHook;

/**
 * 26.2's submitHitOutline follows the restored 26.1.2 renderHitOutline call it stands for
 * (mixin/CbS26BlockOutlineRestoreMixin): 26.2's submitBlockOutline submits the outline in the calls, and the order,
 * 26.1.2's renderBlockOutline drew it in (with high contrast the secondary outline, then the lines; javap of both). When
 * CbS26BlockOutlineRouteMixin ran the restored renderBlockOutline for this outline and a mixin stopped the matching
 * restored renderHitOutline before its drawing (or the call was never made), this submit is cancelled; otherwise, and
 * whenever no route ran, it runs as 26.2 has it.
 * <p>Where: at the method's first instruction, the DEBUG_SHAPES read, after any injector at its head.
 */
@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class CbS26HitOutlineRouteMixin {
    @Inject(method = "submitHitOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/rendertype/RenderType;Lnet/minecraft/client/renderer/state/level/BlockOutlineRenderState;IFZ)V",
        at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;DEBUG_SHAPES:Z", ordinal = 0), cancellable = true)
    private void cbS26$routeHitOutline(PoseStack poseStack, SubmitNodeCollector collector, RenderType renderType,
                                       BlockOutlineRenderState outline, int color, float lineWidth, boolean translucent, CallbackInfo ci) {
        if (!((CbS26BlockOutlineHook) (Object) this).cbS26$nextHitDrawn()) {
            ci.cancel();
        }
    }
}
