package top.dext.centurybridge.step.v1_21_5.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_5.rt.CbShardRenderSystem;

/**
 * 1.21.4's RenderStateShard.CullStateShard (net/minecraft/class_4668$class_4671), gone in 1.21.5 (cull on the
 * RenderPipeline). Its parent BooleanStateShard (class_4670, package-private, public 4-arg constructor) is still in
 * 1.21.5 and is its parent here too (opened by the layer's access widener), so toString is 1.21.4's: "cull[" + enabled +
 * "]". Setup = if !cull disableCull, clear = if !cull enableCull (javap -c -p 1.21.4 method_23546 / method_23545).
 */
@Environment(EnvType.CLIENT)
public class RenderStateShard$class_4671 extends net.minecraft.class_4668.class_4670 {
    /** whether faces are culled */
    public final boolean cbCull;

    public RenderStateShard$class_4671(boolean cull) {
        super("cull", () -> {
            if (!cull) {
                CbShardRenderSystem.disableCull();
            }
        }, () -> {
            if (!cull) {
                CbShardRenderSystem.enableCull();
            }
        }, cull);
        this.cbCull = cull;
    }
}
