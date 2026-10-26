package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.entity.state.MinecartTntRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;
import top.dext.centurybridge.step.v26_1.rt.CbWallMinecarts;

/**
 * 1.21.11's TntMinecartRenderer.submitMinecartContents(tntState, blockState, pose, collector, light)
 * (class_957.method_4137) given back with 1.21.11's body -- the swell before the explosion and the white flash every
 * other five ticks of the fuse -- and the bridge the class had from the generic submitMinecartContents to it. A
 * subclass overriding it is called where 26.1's TNT renderer submits its block (mixin/CbWallTntMinecartContentsReadsMixin).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = TntMinecartRenderer.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallTntMinecartContentsMixin implements CbWallLegacyCalls.TntContentsLegacy {

    /** 1.21.11 TntMinecartRenderer.submitMinecartContents (class_957.method_4137) */
    protected void submitMinecartContents(MinecartTntRenderState state, BlockState block, PoseStack pose, SubmitNodeCollector collector, int light) {
        float fuse = state.fuseRemainingInTicks;
        if (fuse > -1.0F && fuse < 10.0F) {
            float f = 1.0F - fuse / 10.0F;
            f = Mth.clamp(f, 0.0F, 1.0F);
            f *= f;
            f *= f;
            float s = 1.0F + f * 0.3F;
            pose.scale(s, s, s);
        }
        CbWallMinecarts.submitWhiteSolidBlock(block, pose, collector, light, fuse > -1.0F && (int) fuse / 5 % 2 == 0, state.outlineColor);
    }

    /** the 1.21.11 bridge from AbstractMinecartRenderer's submitMinecartContents (overrides the one CbWallMinecartContentsMixin adds) */
    protected void submitMinecartContents(MinecartRenderState state, BlockState block, PoseStack pose, SubmitNodeCollector collector, int light) {
        submitMinecartContents((MinecartTntRenderState) state, block, pose, collector, light);
    }

    @Override
    public void cb$submitLegacyContents(MinecartTntRenderState state, BlockState block, PoseStack pose, SubmitNodeCollector collector, int light) {
        submitMinecartContents(state, block, pose, collector, light);
    }
}
