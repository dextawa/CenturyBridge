package top.dext.centurybridge.step.v26_2.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26GlowingEntities;
import top.dext.centurybridge.step.v26_2.rt.CbS26OutlineFrame;

/**
 * Hands the frame the level state's restored haveGlowingEntities (rt/CbS26GlowingEntities) when 26.2's
 * LevelRenderer.addMainPass starts (javap: render calls it with the prepared frame and the level state after the
 * extraction set the flag, and both hasAnyOutline reads come after: addMainPass's own @154, render's @401), where
 * 26.1.2's renderLevel read the flag (mixin/CbS26GlowingFrameMixin).
 */
@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public abstract class CbS26GlowingPassMixin {
    @Inject(method = "addMainPass", at = @At("HEAD"))
    private void cbS26$frameGlowing(FrameGraphBuilder frame, FeatureRenderDispatcher.PreparedFrame preparedFrame,
                                    GpuBufferSlice fog, LevelRenderState state, ProfilerFiller profiler,
                                    ChunkSectionsToRender sections, CallbackInfo ci) {
        ((CbS26OutlineFrame) preparedFrame).cbS26$setLegacyOutline(((CbS26GlowingEntities) state).cbS26$haveGlowingEntities());
    }
}
