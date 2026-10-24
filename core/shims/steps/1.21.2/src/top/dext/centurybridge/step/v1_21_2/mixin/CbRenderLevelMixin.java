package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.21.1 LevelRenderer methods with nothing left to do in 1.21.2:
 * - method_3267: empty in 1.21.1 itself (its body is a bare return);
 * - graphicsChanged (method_35774): set up or tore down the Fabulous transparency targets for the new graphics mode;
 *   1.21.2 sets them up for every frame from the current mode (its renderLevel asks Minecraft.useShaderTransparency()),
 *   so a mode change takes effect without it.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_761.class)
public abstract class CbRenderLevelMixin {

    /** 1.21.1 LevelRenderer.method_3267 (empty there) */
    public void method_3267() {
    }

    /** 1.21.1 LevelRenderer.graphicsChanged() */
    public void method_35774() {
    }
}
