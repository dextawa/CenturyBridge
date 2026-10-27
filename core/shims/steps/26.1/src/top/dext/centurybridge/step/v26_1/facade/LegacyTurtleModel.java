package top.dext.centurybridge.step.v26_1.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.animal.turtle.AdultTurtleModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.TurtleRenderState;

/**
 * 1.21.11's TurtleModel (class_614) for a 1.21.11 mod's subclass (superRenames). 1.21.11's TurtleModel(root) was the
 * whole turtle: QuadrupedModel(root) with the default entityCutout render type, the egg belly child, and setupAnim
 * showing the belly and lowering the root when the turtle has an egg. 26.1 split it: TurtleModel is abstract and
 * AdultTurtleModel(root) is 1.21.11's class -- the same render type (RenderTypes.entityCutout, javap of both lambdas),
 * the same egg_belly child and the same setupAnim body after TurtleModel's (javap class_614.method_17125 /
 * AdultTurtleModel.setupAnim). This class is that AdultTurtleModel, so a subclass keeps 1.21.11's parent.
 * <p>
 * Super calls into this class keep their intermediary names past the namespace wall (the subclass's own methods take
 * 26.1's), so 1.21.11's members are declared under them: setupAnim(TurtleRenderState) is 1.21.11's body; the two
 * bridges dispatch to the subclass's override, as 1.21.11's bridges did.
 */
@Environment(EnvType.CLIENT)
public class LegacyTurtleModel extends AdultTurtleModel {
    public LegacyTurtleModel(ModelPart root) {
        super(root);
    }

    /** a subclass's super.setupAnim(state): 1.21.11's body (AdultTurtleModel's) */
    public void method_17125(TurtleRenderState state) {
        super.setupAnim(state);
    }

    /** a subclass's super.setupAnim(LivingEntityRenderState): 1.21.11's bridge, into the subclass's override */
    public void method_62089(LivingEntityRenderState state) {
        this.setupAnim((TurtleRenderState) state);
    }

    /** a subclass's super.setupAnim(Object): 1.21.11's bridge, into the subclass's override */
    public void method_2819(Object state) {
        this.setupAnim((TurtleRenderState) state);
    }
}
