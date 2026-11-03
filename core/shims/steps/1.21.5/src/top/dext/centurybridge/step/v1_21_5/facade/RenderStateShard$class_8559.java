package top.dext.centurybridge.step.v1_21_5.facade;

import com.mojang.blaze3d.platform.LogicOp;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.4's RenderStateShard.ColorLogicStateShard (net/minecraft/class_4668$class_8559), gone in 1.21.5 (colour logic is
 * a RenderPipeline property there). Converted references name this class (classRenames). 1.21.4's only member is the
 * (name, setup, clear) constructor (javap -p). The logic op a render type's pipeline gets from it is {@link #cbLogic}:
 * vanilla's two constants carry theirs (rt/CbRenderStates: no_color_logic NONE, or_reverse OR_REVERSE); a shard a mod
 * builds from its own runnables has none known (null) and rt/CbRenderTypes reports it.
 */
@Environment(EnvType.CLIENT)
public class RenderStateShard$class_8559 extends net.minecraft.class_4668 {
    /** the logic op 1.21.4's setup runnable set, null when not known */
    public final LogicOp cbLogic;

    public RenderStateShard$class_8559(String name, Runnable setup, Runnable clear) {
        super(name, setup, clear);
        this.cbLogic = null;
    }

    public RenderStateShard$class_8559(String name, Runnable setup, Runnable clear, LogicOp logic) {
        super(name, setup, clear);
        this.cbLogic = logic;
    }
}
