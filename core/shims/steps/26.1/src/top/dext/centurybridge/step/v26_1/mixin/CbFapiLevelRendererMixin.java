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

/** 1.21.11's WorldRenderEvents that 26.1 dropped, fired in 26.1's main pass (see CbWorldRenderEvents) */
@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class CbFapiLevelRendererMixin {

    @Inject(method = "lambda$addMainPass$0", at = @At(value = "INVOKE_STRING",
        target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", args = "ldc=submitFeatures"))
    private void cb$beforeEntities(CallbackInfo ci) {
        LevelRenderContext context = CbWorldRenderEvents.frame();
        if (context != null) {
            CbWorldRenderEvents.BEFORE_ENTITIES.invoker().beforeEntities(context);
        }
    }

    @Inject(method = "lambda$addMainPass$0", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/LevelRenderer;checkPoseStack(Lcom/mojang/blaze3d/vertex/PoseStack;)V", ordinal = 0))
    private void cb$afterEntities(CallbackInfo ci) {
        LevelRenderContext context = CbWorldRenderEvents.frame();
        if (context != null) {
            CbWorldRenderEvents.AFTER_ENTITIES.invoker().afterEntities(context);
        }
    }

    @Inject(method = "lambda$addMainPass$0", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/LevelRenderer;finalizeGizmoCollection()V"))
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
