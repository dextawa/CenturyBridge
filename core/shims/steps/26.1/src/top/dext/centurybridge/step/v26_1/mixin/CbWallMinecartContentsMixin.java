package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacySplit;
import top.dext.centurybridge.step.v26_1.rt.CbWallMinecarts;

/**
 * 1.21.11's AbstractMinecartRenderer.submitMinecartContents(state, blockState, pose, collector, light)
 * (class_925.method_4064) given back with 1.21.11's body (rt/CbWallMinecarts); a subclass overriding it is called
 * where 26.1 submits the displayed block's model (submitMinecartContents), with the block state extracted for the
 * frame. The extracted block state is kept beside the render state (1.21.11's displayBlockState field).
 */
@Environment(EnvType.CLIENT)
@Mixin(AbstractMinecartRenderer.class)
public abstract class CbWallMinecartContentsMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;F)V",
        at = @At("TAIL"))
    private void cb$displayBlockState(AbstractMinecart minecart, MinecartRenderState state, float partialTick, CallbackInfo ci) {
        CbWallMinecarts.extracted(state, minecart.getDisplayBlockState());
    }

    /** 1.21.11 AbstractMinecartRenderer.submitMinecartContents (class_925.method_4064) */
    protected void submitMinecartContents(MinecartRenderState state, BlockState block, PoseStack pose, SubmitNodeCollector collector, int light) {
        CbWallMinecarts.renderContents(state, block, pose, collector, light);
    }

    @Inject(method = "submitMinecartContents(Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;Lnet/minecraft/client/renderer/block/BlockModelRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V", at = @At("HEAD"), cancellable = true)
    private void cb$legacyContents(MinecartRenderState state, BlockModelRenderState model, PoseStack pose, SubmitNodeCollector collector, int light,
                                   CallbackInfo ci) {
        if (CbWallLegacySplit.overrides(getClass(), AbstractMinecartRenderer.class, "submitMinecartContents",
                MinecartRenderState.class, BlockState.class, PoseStack.class, SubmitNodeCollector.class, int.class)) {
            ci.cancel();
            submitMinecartContents(state, CbWallMinecarts.displayBlockState(state), pose, collector, light);
        }
    }
}
