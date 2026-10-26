package top.dext.centurybridge.step.v26_2.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbMixedMembers;
import top.dext.centurybridge.step.v26_2.rt.CbS26BlockOutlineHook;
import top.dext.centurybridge.step.v26_2.rt.CbS26RenderBuffers;

/**
 * Route of 26.2's submitBlockOutline through the restored 26.1.2 renderBlockOutline (mixin/CbS26BlockOutlineRestoreMixin),
 * only when a converted mod mixed it (CbMixedMembers): unmixed, submitBlockOutline runs exactly as 26.2 has it.
 * <p>Where: at submitBlockOutline's first pushPose, i.e. after its outline null-check and its camera read -- after every
 * injector at its head and at that read (26.2 Fabric API's BEFORE_BLOCK_OUTLINE, LevelRendererMixin at the
 * CameraRenderState.pos GETFIELD, cancellable, and any native 26.2 mod's), so those still fire first and a cancel there
 * keeps the route from running. The restored member gets what 26.1.2's main pass handed it: the frame's main buffer
 * source (26.1.2: RenderBuffers.bufferSource(); the layer's CbS26RenderBuffers source, whose batches CbS26LevelFlushMixin
 * ends in the main pass after the solid features, so what a listener draws there is drawn in the frame), the pose stack,
 * the outline's translucency (26.2 submits one outline, translucent or not, where 26.1.2 called the member once per
 * translucency and drew in the call whose flag matched) and the frame's level state. When a mixin on the restored member
 * stopped it before 26.1.2's drawing, 26.2's submit of the outline is cancelled; otherwise 26.2 submits it as always,
 * and native mixins inside the rest of submitBlockOutline fire.
 */
@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class CbS26BlockOutlineRouteMixin {
    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Inject(method = "submitBlockOutline(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/LevelRenderState;)V",
        at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", ordinal = 0), cancellable = true)
    private void cbS26$routeBlockOutline(PoseStack poseStack, SubmitNodeCollector collector, LevelRenderState state, CallbackInfo ci) {
        if (!CbMixedMembers.mixed(CbS26BlockOutlineHook.MEMBER)) {
            return;
        }
        boolean translucent = state.blockOutlineRenderState.isTranslucent();
        if (!((CbS26BlockOutlineHook) (Object) this).cbS26$restoredBlockOutline(CbS26RenderBuffers.of(this.renderBuffers).bufferSource(),
                poseStack, translucent, state)) {
            ci.cancel();
        }
    }
}
