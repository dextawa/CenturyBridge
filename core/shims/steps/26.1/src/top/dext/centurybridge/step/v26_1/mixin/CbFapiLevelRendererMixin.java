package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.compat.fapi.client.rendering.v1.world.CbWorldRenderEvents;

/**
 * 1.21.11's WorldRenderEvents that 26.1 dropped, fired in 26.1's main pass (see CbWorldRenderEvents). The points are
 * picked so they survive 26.2 unchanged (javap of client-26.1.2 and client-26.2):
 * <ul>
 *   <li>BEFORE_ENTITIES at submitEntities' head: on 26.1 the main pass's popPush("submitFeatures") (@207) is followed
 *       by nothing but submitEntities' argument loads (@220), the main pass being its only caller; 26.2 calls the
 *       unchanged submitEntities from render()'s submitFeatures, before the frame graph runs the main pass;</li>
 *   <li>AFTER_ENTITIES at the main pass's first ProfilerFiller.pop(): on 26.1 right after renderSolidFeatures and the
 *       batch end (@273, @278), before the first pose-stack check (@290); on 26.2 right after
 *       PreparedFrame.executeSolid (@177, @181);</li>
 *   <li>BEFORE_DEBUG_RENDER at finalizeGizmoCollection's head: on 26.1 called only by the main pass (@440); on 26.2 by
 *       render()'s submitFeatures, before the main pass;</li>
 *   <li>BEFORE_TRANSLUCENT at push("translucentTerrain") in the main pass, present in both.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class CbFapiLevelRendererMixin {

    @Inject(method = "submitEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V",
        at = @At("HEAD"))
    private void cb$beforeEntities(CallbackInfo ci) {
        LevelRenderContext context = CbWorldRenderEvents.frame();
        if (context != null) {
            CbWorldRenderEvents.BEFORE_ENTITIES.invoker().beforeEntities(context);
        }
    }

    @Inject(method = "lambda$addMainPass$0", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V", ordinal = 0))
    private void cb$afterEntities(CallbackInfo ci) {
        LevelRenderContext context = CbWorldRenderEvents.frame();
        if (context != null) {
            CbWorldRenderEvents.AFTER_ENTITIES.invoker().afterEntities(context);
        }
    }

    @Inject(method = "finalizeGizmoCollection()V", at = @At("HEAD"))
    private void cb$beforeDebugRender(CallbackInfo ci) {
        LevelRenderContext context = CbWorldRenderEvents.frame();
        if (context != null) {
            CbWorldRenderEvents.BEFORE_DEBUG_RENDER.invoker().beforeDebugRender(context);
        }
    }

    @Inject(method = "lambda$addMainPass$0", at = @At(value = "INVOKE_STRING",
        target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V", args = "ldc=translucentTerrain"))
    private void cb$beforeTranslucent(CallbackInfo ci) {
        LevelRenderContext context = CbWorldRenderEvents.frame();
        if (context != null) {
            CbWorldRenderEvents.BEFORE_TRANSLUCENT.invoker().beforeTranslucent(context);
        }
    }
}
