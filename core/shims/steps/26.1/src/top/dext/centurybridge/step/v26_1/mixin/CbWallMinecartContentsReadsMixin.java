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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbMixedMembers;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacySplit;
import top.dext.centurybridge.step.v26_1.rt.CbWallMinecarts;

/**
 * 26.1's minecart renderer submits the displayed block's model state (submitMinecartContents(state, model, ..)); the
 * extracted block state is kept beside the render state, and when this renderer's class overrides 1.21.11's
 * submitMinecartContents(state, blockState, ..) or a converted mod mixes it (rt/CbMixedMembers), 26.1's submit hands the
 * frame to it (CbWallMinecartContentsMixin) instead. Otherwise 26.1's submit runs as it is. Default priority: injectors
 * into 26.1 methods.
 */
@Environment(EnvType.CLIENT)
@Mixin(AbstractMinecartRenderer.class)
public abstract class CbWallMinecartContentsReadsMixin {

    /** the restored member, as rt/CbMixedMembers names it */
    @Unique
    private static final String CB_CONTENTS = "net/minecraft/client/renderer/entity/AbstractMinecartRenderer.submitMinecartContents"
        + "(Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;Lnet/minecraft/world/level/block/state/BlockState;"
        + "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V";

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;F)V",
        at = @At("TAIL"))
    private void cb$displayBlockState(AbstractMinecart minecart, MinecartRenderState state, float partialTick, CallbackInfo ci) {
        CbWallMinecarts.extracted(state, minecart.getDisplayBlockState());
    }

    @Inject(method = "submitMinecartContents(Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;Lnet/minecraft/client/renderer/block/BlockModelRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V", at = @At("HEAD"), cancellable = true)
    private void cb$legacyContents(MinecartRenderState state, BlockModelRenderState model, PoseStack pose, SubmitNodeCollector collector, int light,
                                   CallbackInfo ci) {
        if (CbWallLegacySplit.overrides(getClass(), AbstractMinecartRenderer.class, "submitMinecartContents",
                MinecartRenderState.class, BlockState.class, PoseStack.class, SubmitNodeCollector.class, int.class)
            || CbMixedMembers.mixed(CB_CONTENTS)) {
            ci.cancel();
            ((CbWallLegacyCalls.MinecartContentsLegacy) this).cb$submitMinecartContents(state, CbWallMinecarts.displayBlockState(state),
                pose, collector, light);
        }
    }
}
