package top.dext.centurybridge.step.v1_21_5.facade;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.4's RenderStateShard.ShaderStateShard (net/minecraft/class_4668$class_5942), gone in 1.21.5 (a 1.21.5 render
 * type's program is its RenderPipeline). Converted references name this class (classRenames); a render type built from
 * a composite state holding it takes the program into its pipeline (rt/CbRenderTypes). 1.21.4's members (javap -c -p):
 * name "shader", field_29455 Optional of the program, toString name + "[" + Optional + "]"; setup =
 * RenderSystem.setShader(program) (RenderSystem.clearShader for the no-program form), clear = nothing. 1.21.5's command
 * encoder applies each pipeline's own program at every draw, so the runnables only reach a caller that runs
 * setupRenderState itself; they go to the layer's shader slot through rt/CbRenderTypes.setupShader.
 */
@Environment(EnvType.CLIENT)
public class RenderStateShard$class_5942 extends net.minecraft.class_4668 {
    /** shader */
    public final Optional<ShaderProgram> field_29455;

    public RenderStateShard$class_5942(ShaderProgram program) {
        super("shader", () -> top.dext.centurybridge.step.v1_21_5.rt.CbRenderTypes.setupShader(Optional.of(program)), () -> {
        });
        this.field_29455 = Optional.of(program);
    }

    public RenderStateShard$class_5942() {
        super("shader", () -> top.dext.centurybridge.step.v1_21_5.rt.CbRenderTypes.setupShader(Optional.empty()), () -> {
        });
        this.field_29455 = Optional.empty();
    }

    @Override
    public String toString() {
        return this.field_21363 + "[" + this.field_29455 + "]";
    }
}
