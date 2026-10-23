package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * The depth and colour state a 26.1 RenderPipeline.Builder holds so far, for rt/CbWallPipeline when a
 * 1.21.11 mod reaches a builder it did not start (its 1.21.11 view is read off what the builder holds).
 */
@Mixin(RenderPipeline.Builder.class)
public interface CbWallPipelineBuilderAccess {

    @Accessor("depthStencilState")
    Optional<DepthStencilState> cb$depthStencilState();

    @Accessor("colorTargetState")
    Optional<ColorTargetState> cb$colorTargetState();
}
