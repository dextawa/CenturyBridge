package top.dext.centurybridge.step.v1_21_5.facade;

import com.mojang.blaze3d.pipeline.BlendFunction;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.4's RenderStateShard.TransparencyStateShard (net/minecraft/class_4668$class_4685), gone in 1.21.5 (blend is a
 * RenderPipeline property there). Converted references name this class (classRenames). 1.21.4's only member is the
 * (name, setup, clear) constructor (javap -p). The blend a render type's pipeline gets from it is {@link #cbBlend}:
 * vanilla's constants carry the blend their runnables set (rt/CbRenderStates, from 1.21.4's lambda bodies); a shard a
 * mod builds from its own runnables has none known (null) and rt/CbRenderTypes reports it.
 */
@Environment(EnvType.CLIENT)
public class RenderStateShard$class_4685 extends net.minecraft.class_4668 {
    /** the blend 1.21.4's setup runnable set (empty = blending off), null when not known */
    public final Optional<BlendFunction> cbBlend;

    public RenderStateShard$class_4685(String name, Runnable setup, Runnable clear) {
        super(name, setup, clear);
        this.cbBlend = null;
    }

    public RenderStateShard$class_4685(String name, Runnable setup, Runnable clear, Optional<BlendFunction> blend) {
        super(name, setup, clear);
        this.cbBlend = blend;
    }
}
