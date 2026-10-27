package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.BlendFactor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Optional;

/**
 * 26.1.2's single-target pipeline colour state on 26.2's multi-target one (javap of both).
 * <p>26.1.2 RenderPipeline.Snippet held one Optional&lt;ColorTargetState&gt; (Builder.withColorTargetState(state) set it;
 * empty = the snippet does not set it); 26.2's holds ColorTargetState[] colorTargetStates and activeColorTargetStateCount,
 * and Builder.withColorTargetState(state) is withColorTargetState(0, state) (target 0, count max(count, 1)). 26.1.2's
 * ColorTargetState(Optional&lt;BlendFunction&gt;, int writeMask) had no format; 26.2's ColorTargetState(BlendFunction), its
 * construction for a single target, passes GpuFormat.RGBA8_UNORM. 26.1.2 BlendFunction held four factors with the
 * implicit GL add equation; 26.2's holds a colour and an alpha BlendEquation (source factor, dest factor, op), its
 * four-factor constructor building ADD equations.
 */
@Environment(EnvType.CLIENT)
public final class CbS26Pipeline {
    private CbS26Pipeline() {
    }

    /** 26.1.2 RenderPipeline.Snippet.colorTargetState(): 26.2's colour target 0 when the snippet sets one */
    public static Optional<ColorTargetState> colorTargetState(RenderPipeline.Snippet snippet) {
        ColorTargetState[] states = snippet.colorTargetStates();
        if (snippet.activeColorTargetStateCount() > 0 && states != null && states.length > 0) {
            return Optional.ofNullable(states[0]);
        }
        return Optional.empty();
    }

    /** 26.1.2 new ColorTargetState(blendFunction, writeMask): 26.2's single-target format RGBA8_UNORM */
    public static ColorTargetState newColorTargetState(Optional<BlendFunction> blendFunction, int writeMask) {
        return new ColorTargetState(blendFunction, GpuFormat.RGBA8_UNORM, writeMask);
    }

    /** 26.1.2 BlendFunction.sourceColor() (SourceFactor is 26.2's BlendFactor) */
    public static BlendFactor sourceColor(BlendFunction f) {
        return f.color().sourceFactor();
    }

    /** 26.1.2 BlendFunction.destColor() (DestFactor is 26.2's BlendFactor) */
    public static BlendFactor destColor(BlendFunction f) {
        return f.color().destFactor();
    }

    /** 26.1.2 BlendFunction.sourceAlpha() */
    public static BlendFactor sourceAlpha(BlendFunction f) {
        return f.alpha().sourceFactor();
    }

    /** 26.1.2 BlendFunction.destAlpha() */
    public static BlendFactor destAlpha(BlendFunction f) {
        return f.alpha().destFactor();
    }
}
