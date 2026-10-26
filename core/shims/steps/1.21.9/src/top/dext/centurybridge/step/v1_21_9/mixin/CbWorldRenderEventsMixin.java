package top.dext.centurybridge.step.v1_21_9.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.class_11658;
import net.minecraft.class_12074;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4063;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4599;
import net.minecraft.class_4604;
import net.minecraft.class_638;
import net.minecraft.class_761;
import net.minecraft.class_9779;
import net.minecraft.class_9909;
import net.minecraft.class_9922;
import net.minecraft.class_9960;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_9.rt.CbDimensionRendering;
import top.dext.centurybridge.step.v1_21_9.rt.CbWorldRender;
import top.dext.centurybridge.step.v1_21_9.rt.CbWorldRenderContextImpl;
import top.dext.centurybridge.step.v1_21_9.rt.CbWorldRenderHolder;

/**
 * Fires Fabric API 0.136's WorldRenderEvents (deleted by Fabric API for 1.21.9, restored by the layer's facade) from
 * 1.21.9's LevelRenderer at the points 0.136's WorldRendererMixin used on 1.21.8's:
 * START at renderLevel's head; the frustum the frame's terrain setup takes; AFTER_SETUP after the main pass pushes
 * "terrain"; the main pass's pose stack; BEFORE_ENTITIES before the entities are submitted; AFTER_ENTITIES once the
 * feature dispatcher has drawn what was submitted (1.21.9 draws entities and block entities in that one pass);
 * BEFORE_BLOCK_OUTLINE at each block-outline head and BLOCK_OUTLINE after the outline's translucency test, as 1.21.8;
 * BEFORE_DEBUG_RENDER before the debug renderer; the afterTranslucent frame pass before the clouds; LAST at the main
 * pass's return; END at renderLevel's return. Every injection only adds: it returns at once until a converted mod
 * initialized the restored events, cancels nothing a listener did not refuse, and leaves 1.21.9's own and other mods'
 * injections at these methods in place.
 */
@Mixin(class_761.class)
public abstract class CbWorldRenderEventsMixin implements CbWorldRenderHolder {

    @Shadow
    @Final
    private class_310 field_4088;

    @Shadow
    private class_638 field_4085;

    @Shadow
    @Final
    private class_4599 field_20951;

    @Shadow
    @Final
    private class_9960 field_53081;

    @Unique
    private CbWorldRenderContextImpl cb$worldRenderContext;

    @Unique
    private class_9909 cb$frameGraph;

    @Override
    public CbWorldRenderContextImpl cb$worldRenderContext() {
        CbWorldRenderContextImpl context = this.cb$worldRenderContext;
        if (context == null) {
            context = new CbWorldRenderContextImpl();
            this.cb$worldRenderContext = context;
        }
        return context;
    }

    @Inject(method = "method_22710(Lnet/minecraft/class_9922;Lnet/minecraft/class_9779;ZLnet/minecraft/class_4184;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V",
        at = @At("HEAD"))
    private void cb$start(class_9922 allocator, class_9779 tickCounter, boolean renderBlockOutline, class_4184 camera, Matrix4f positionMatrix,
            Matrix4f projectionMatrix, Matrix4f cullProjectionMatrix, GpuBufferSlice fog, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
        this.cb$frameGraph = null;
        if (!CbWorldRender.cbActive()) {
            return;
        }
        CbWorldRender.cbStart((class_761) (Object) this, tickCounter, renderBlockOutline, camera, this.field_4088.field_1773, positionMatrix,
            projectionMatrix, this.field_20951.method_23000(), class_310.method_29611(), this.field_4085);
    }

    @Inject(method = "method_74752(Lnet/minecraft/class_4184;Lnet/minecraft/class_4604;Z)V", at = @At("RETURN"))
    private void cb$frustum(class_4184 camera, class_4604 frustum, boolean spectator, CallbackInfo ci) {
        CbWorldRenderContextImpl context = CbWorldRender.cbLive((class_761) (Object) this);
        if (context != null) {
            CbWorldRender.cbFrustum(context, frustum);
        }
    }

    @Inject(method = "method_62214(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/class_11658;Lnet/minecraft/class_3695;Lorg/joml/Matrix4f;Lnet/minecraft/class_9925;Lnet/minecraft/class_9925;ZLnet/minecraft/class_4604;Lnet/minecraft/class_9925;Lnet/minecraft/class_9925;)V",
        at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/class_3695;method_15396(Ljava/lang/String;)V", args = "ldc=terrain",
            shift = At.Shift.AFTER))
    private void cb$afterSetup(CallbackInfo ci) {
        CbWorldRenderContextImpl context = CbWorldRender.cbLive((class_761) (Object) this);
        if (context != null) {
            CbWorldRender.cbAfterSetup(context);
        }
    }

    @ModifyVariable(method = "method_62214(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/class_11658;Lnet/minecraft/class_3695;Lorg/joml/Matrix4f;Lnet/minecraft/class_9925;Lnet/minecraft/class_9925;ZLnet/minecraft/class_4604;Lnet/minecraft/class_9925;Lnet/minecraft/class_9925;)V",
        at = @At("STORE"))
    private class_4587 cb$matrixStack(class_4587 matrixStack) {
        CbWorldRenderContextImpl context = CbWorldRender.cbLive((class_761) (Object) this);
        if (context != null) {
            CbWorldRender.cbMatrixStack(context, matrixStack);
        }
        return matrixStack;
    }

    @Inject(method = "method_62214(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/class_11658;Lnet/minecraft/class_3695;Lorg/joml/Matrix4f;Lnet/minecraft/class_9925;Lnet/minecraft/class_9925;ZLnet/minecraft/class_4604;Lnet/minecraft/class_9925;Lnet/minecraft/class_9925;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_761;method_72916(Lnet/minecraft/class_4587;Lnet/minecraft/class_11658;Lnet/minecraft/class_11659;)V"))
    private void cb$beforeEntities(CallbackInfo ci) {
        CbWorldRenderContextImpl context = CbWorldRender.cbLive((class_761) (Object) this);
        if (context != null) {
            CbWorldRender.cbBeforeEntities(context);
        }
    }

    @Inject(method = "method_62214(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/class_11658;Lnet/minecraft/class_3695;Lorg/joml/Matrix4f;Lnet/minecraft/class_9925;Lnet/minecraft/class_9925;ZLnet/minecraft/class_4604;Lnet/minecraft/class_9925;Lnet/minecraft/class_9925;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_11684;method_73002()V", shift = At.Shift.AFTER))
    private void cb$afterEntities(CallbackInfo ci) {
        CbWorldRenderContextImpl context = CbWorldRender.cbLive((class_761) (Object) this);
        if (context != null) {
            CbWorldRender.cbAfterEntities(context);
        }
    }

    @Inject(method = "method_62210(Lnet/minecraft/class_4597$class_4598;Lnet/minecraft/class_4587;ZLnet/minecraft/class_11658;)V", at = @At("HEAD"))
    private void cb$beforeBlockOutline(class_4597.class_4598 consumers, class_4587 matrices, boolean translucent, class_11658 state, CallbackInfo ci) {
        CbWorldRenderContextImpl context = CbWorldRender.cbLive((class_761) (Object) this);
        if (context != null) {
            CbWorldRender.cbBeforeBlockOutline(context, translucent, this.field_4088.field_1765);
        }
    }

    @Inject(method = "method_62210(Lnet/minecraft/class_4597$class_4598;Lnet/minecraft/class_4587;ZLnet/minecraft/class_11658;)V",
        at = @At(value = "FIELD", target = "Lnet/minecraft/class_12075;field_63078:Lnet/minecraft/class_243;", ordinal = 0), cancellable = true)
    private void cb$blockOutline(class_4597.class_4598 consumers, class_4587 matrices, boolean translucent, class_11658 state, CallbackInfo ci) {
        CbWorldRenderContextImpl context = CbWorldRender.cbLive((class_761) (Object) this);
        if (context == null) {
            return;
        }
        if (!CbWorldRender.cbRenderBlockOutline(context)) {
            ci.cancel();
            return;
        }
        class_12074 outline = state.field_63083;
        class_243 cameraPos = state.field_63082.field_63078;
        if (!CbWorldRender.cbBlockOutline(context, context.camera().method_19331(), cameraPos.field_1352, cameraPos.field_1351, cameraPos.field_1350,
                outline.comp_4932(), this.field_4085.method_8320(outline.comp_4932()))) {
            consumers.method_37104();
            ci.cancel();
        }
    }

    @Inject(method = "method_62214(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/class_11658;Lnet/minecraft/class_3695;Lorg/joml/Matrix4f;Lnet/minecraft/class_9925;Lnet/minecraft/class_9925;ZLnet/minecraft/class_4604;Lnet/minecraft/class_9925;Lnet/minecraft/class_9925;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_863;method_23099(Lnet/minecraft/class_4587;Lnet/minecraft/class_4604;Lnet/minecraft/class_4597$class_4598;DDDZ)V", ordinal = 0))
    private void cb$beforeDebugRender(CallbackInfo ci) {
        CbWorldRenderContextImpl context = CbWorldRender.cbLive((class_761) (Object) this);
        if (context != null) {
            CbWorldRender.cbBeforeDebugRender(context);
        }
    }

    @ModifyVariable(method = "method_22710(Lnet/minecraft/class_9922;Lnet/minecraft/class_9779;ZLnet/minecraft/class_4184;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V",
        at = @At("STORE"))
    private class_9909 cb$frameGraph(class_9909 frameGraph) {
        if (CbWorldRender.cbActive()) {
            this.cb$frameGraph = frameGraph;
        }
        return frameGraph;
    }

    @Inject(method = "method_22710(Lnet/minecraft/class_9922;Lnet/minecraft/class_9779;ZLnet/minecraft/class_4184;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_315;method_1632()Lnet/minecraft/class_4063;"))
    private void cb$afterTranslucent(CallbackInfo ci) {
        CbWorldRenderContextImpl context = CbWorldRender.cbLive((class_761) (Object) this);
        if (context != null) {
            CbWorldRender.cbAfterTranslucentPass(context, this.cb$frameGraph, this.field_53081);
        }
    }

    /** sky, clouds, weather: a DimensionRenderingRegistry renderer registered for the world's dimension replaces the pass, as 0.136 */
    @Inject(method = "method_62200(Lnet/minecraft/class_9909;Lnet/minecraft/class_4184;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
        at = @At("HEAD"), cancellable = true)
    private void cb$sky(class_9909 frameGraph, class_4184 camera, GpuBufferSlice fog, CallbackInfo ci) {
        CbWorldRenderContextImpl context = CbWorldRender.cbLive((class_761) (Object) this);
        if (context != null && this.field_4088.field_1687 != null && CbDimensionRendering.cbRenderSky(context, this.field_4085)) {
            ci.cancel();
        }
    }

    @Inject(method = "method_62204(Lnet/minecraft/class_9909;Lnet/minecraft/class_4063;Lnet/minecraft/class_243;FIF)V",
        at = @At("HEAD"), cancellable = true)
    private void cb$clouds(class_9909 frameGraph, class_4063 mode, class_243 cameraPos, float ticks, int color, float height, CallbackInfo ci) {
        CbWorldRenderContextImpl context = CbWorldRender.cbLive((class_761) (Object) this);
        if (context != null && this.field_4088.field_1687 != null && CbDimensionRendering.cbRenderClouds(context, this.field_4085)) {
            ci.cancel();
        }
    }

    @Inject(method = "method_62203(Lnet/minecraft/class_9909;Lnet/minecraft/class_243;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
        at = @At("HEAD"), cancellable = true)
    private void cb$weather(class_9909 frameGraph, class_243 cameraPos, GpuBufferSlice fog, CallbackInfo ci) {
        CbWorldRenderContextImpl context = CbWorldRender.cbLive((class_761) (Object) this);
        if (context != null && this.field_4088.field_1687 != null && CbDimensionRendering.cbRenderWeather(context, this.field_4085)) {
            ci.cancel();
        }
    }

    @Inject(method = "method_62214(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/class_11658;Lnet/minecraft/class_3695;Lorg/joml/Matrix4f;Lnet/minecraft/class_9925;Lnet/minecraft/class_9925;ZLnet/minecraft/class_4604;Lnet/minecraft/class_9925;Lnet/minecraft/class_9925;)V",
        at = @At("RETURN"))
    private void cb$last(CallbackInfo ci) {
        CbWorldRenderContextImpl context = CbWorldRender.cbLive((class_761) (Object) this);
        if (context != null) {
            CbWorldRender.cbLast(context);
        }
    }

    @Inject(method = "method_22710(Lnet/minecraft/class_9922;Lnet/minecraft/class_9779;ZLnet/minecraft/class_4184;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V",
        at = @At("RETURN"))
    private void cb$end(CallbackInfo ci) {
        this.cb$frameGraph = null;
        CbWorldRenderContextImpl context = CbWorldRender.cbLive((class_761) (Object) this);
        if (context != null) {
            CbWorldRender.cbEnd(context);
        }
    }
}
