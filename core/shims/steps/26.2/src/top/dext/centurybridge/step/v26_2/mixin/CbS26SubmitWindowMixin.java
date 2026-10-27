package top.dext.centurybridge.step.v26_2.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26SubmitStorage;

/**
 * The level's part of 26.1.2's one submit storage (rt/CbS26SubmitStorage), at 26.2's LevelRenderer.render (javap):
 * its head opens the submit window (submitFeatures fills the level's storage), prepareFrame drains the storage and opens
 * the frame, PreparedFrame.close (@591, after the frame graph ran and the model-view stack was popped) ends it; there
 * this layer's storage is drawn with the model-view matrix render multiplied in (its Matrix4fc argument).
 * Injections only: 26.2's body and the mixins on it run unchanged; with nothing in this layer's storage nothing is drawn.
 */
@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class CbS26SubmitWindowMixin {
    @Shadow
    @Final
    private SubmitNodeStorage submitNodeStorage;

    @Shadow
    @Final
    private FeatureRenderDispatcher featureRenderDispatcher;

    @Inject(method = "render", at = @At("HEAD"))
    private void cbS26$submitWindow(GraphicsResourceAllocator allocator, DeltaTracker deltaTracker, boolean renderOutline,
            CameraRenderState camera, Matrix4fc modelView, GpuBufferSlice fog, Vector4f fogColor, boolean sky,
            CallbackInfo ci) {
        CbS26SubmitStorage.levelStart(this.submitNodeStorage);
    }

    @Inject(method = "render", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;prepareFrame(Lnet/minecraft/client/renderer/SubmitNodeStorage;)Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher$PreparedFrame;",
        shift = At.Shift.AFTER))
    private void cbS26$frameOpen(GraphicsResourceAllocator allocator, DeltaTracker deltaTracker, boolean renderOutline,
            CameraRenderState camera, Matrix4fc modelView, GpuBufferSlice fog, Vector4f fogColor, boolean sky,
            CallbackInfo ci) {
        CbS26SubmitStorage.levelFrameOpen();
    }

    @Inject(method = "render", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher$PreparedFrame;close()V",
        shift = At.Shift.AFTER))
    private void cbS26$frameClosed(GraphicsResourceAllocator allocator, DeltaTracker deltaTracker, boolean renderOutline,
            CameraRenderState camera, Matrix4fc modelView, GpuBufferSlice fog, Vector4f fogColor, boolean sky,
            CallbackInfo ci) {
        CbS26SubmitStorage.levelFrameClosed(this.featureRenderDispatcher, modelView);
    }
}
