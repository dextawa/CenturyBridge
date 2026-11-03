package top.dext.centurybridge.step.v1_21_5.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_5.rt.CbGlRenderSystem;

/**
 * 1.21.4's RenderStateShard.DepthTestStateShard (net/minecraft/class_4668$class_4672), gone in 1.21.5 (DepthTestFunction
 * on the RenderPipeline). Converted references name this class (classRenames). 1.21.4's members (javap -c -p): name
 * "depth_test", functionName field_22242; setup = if func != GL_ALWAYS (519) { enableDepthTest; depthFunc(func) }; clear
 * = if func != 519 { disableDepthTest; depthFunc(GL_LEQUAL 515) }; toString name + "[" + functionName + "]".
 */
@Environment(EnvType.CLIENT)
public class RenderStateShard$class_4672 extends net.minecraft.class_4668 {
    /** functionName */
    public final String field_22242;
    /** the GL depth function 1.21.4 passed */
    public final int cbFunction;

    public RenderStateShard$class_4672(String functionName, int func) {
        super("depth_test", () -> {
            if (func != 519) {
                CbGlRenderSystem.enableDepthTest();
                CbGlRenderSystem.depthFunc(func);
            }
        }, () -> {
            if (func != 519) {
                CbGlRenderSystem.disableDepthTest();
                CbGlRenderSystem.depthFunc(515);
            }
        });
        this.field_22242 = functionName;
        this.cbFunction = func;
    }

    @Override
    public String toString() {
        return this.field_21363 + "[" + this.field_22242 + "]";
    }
}
