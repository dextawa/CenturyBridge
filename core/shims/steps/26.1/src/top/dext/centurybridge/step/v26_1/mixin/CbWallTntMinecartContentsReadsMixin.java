package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartTntRenderState;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacySplit;
import top.dext.centurybridge.step.v26_1.rt.CbWallMinecarts;

/**
 * A 1.21.11 subclass overriding TntMinecartRenderer.submitMinecartContents(tntState, blockState, ..) (restored by
 * CbWallTntMinecartContentsMixin) is called where 26.1's TNT renderer submits its block; a renderer without such an
 * override runs 26.1's body untouched. Default priority: injector into a 26.1 method.
 */
@Environment(EnvType.CLIENT)
@Mixin(TntMinecartRenderer.class)
public abstract class CbWallTntMinecartContentsReadsMixin {

    @Inject(method = "submitMinecartContents(Lnet/minecraft/client/renderer/entity/state/MinecartTntRenderState;Lnet/minecraft/client/renderer/block/BlockModelRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
        at = @At("HEAD"), cancellable = true)
    private void cb$legacyTntContents(MinecartTntRenderState state, BlockModelRenderState model, PoseStack pose, SubmitNodeCollector collector, int light,
                                      CallbackInfo ci) {
        if (CbWallLegacySplit.overrides(getClass(), TntMinecartRenderer.class, "submitMinecartContents",
                MinecartTntRenderState.class, BlockState.class, PoseStack.class, SubmitNodeCollector.class, int.class)) {
            ci.cancel();
            ((CbWallLegacyCalls.TntContentsLegacy) (Object) this).cb$submitLegacyContents(state, CbWallMinecarts.displayBlockState(state), pose,
                    collector, light);
        }
    }
}
