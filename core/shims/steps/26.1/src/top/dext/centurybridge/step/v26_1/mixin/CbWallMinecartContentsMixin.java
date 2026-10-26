package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;
import top.dext.centurybridge.step.v26_1.rt.CbWallMinecarts;

/**
 * 1.21.11's AbstractMinecartRenderer.submitMinecartContents(state, blockState, pose, collector, light)
 * (class_925.method_4064) given back with 1.21.11's body (rt/CbWallMinecarts); a subclass overriding it, or a converted
 * mod mixing it, is called where 26.1 submits the displayed block's model (CbWallMinecartContentsReadsMixin), with the
 * block state extracted for the frame (kept beside the render state: 1.21.11's displayBlockState field).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = AbstractMinecartRenderer.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallMinecartContentsMixin implements CbWallLegacyCalls.MinecartContentsLegacy {

    /** 1.21.11 AbstractMinecartRenderer.submitMinecartContents (class_925.method_4064) */
    @CbVanillaReads("CbWallMinecartContentsReadsMixin: 26.1's submitMinecartContents(state, model, ..) hands the frame to this "
        + "method when a subclass overrides it or a converted mod mixes it")
    protected void submitMinecartContents(MinecartRenderState state, BlockState block, PoseStack pose, SubmitNodeCollector collector, int light) {
        CbWallMinecarts.renderContents(state, block, pose, collector, light);
    }

    @Override
    public void cb$submitMinecartContents(MinecartRenderState state, BlockState block, PoseStack pose, SubmitNodeCollector collector, int light) {
        submitMinecartContents(state, block, pose, collector, light);
    }
}
