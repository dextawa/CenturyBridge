package top.dext.centurybridge.step.v1_21_5.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_5.rt.CbGlRenderSystem;
import top.dext.centurybridge.step.v1_21_5.rt.CbShardRenderSystem;

/**
 * 1.21.4's RenderStateShard.WriteMaskStateShard (net/minecraft/class_4668$class_4686), gone in 1.21.5 (colour / depth
 * write on the RenderPipeline). Converted references name this class (classRenames). 1.21.4's members (javap -c -p):
 * name "write_mask_state", writeColor field_21400, writeDepth field_21401; setup = if !depth depthMask(false); if !color
 * colorMask(false x4); clear restores true; toString name + "[writeColor=" + color + ", writeDepth=" + depth + "]".
 */
@Environment(EnvType.CLIENT)
public class RenderStateShard$class_4686 extends net.minecraft.class_4668 {
    /** writeColor */
    public final boolean field_21400;
    /** writeDepth */
    public final boolean field_21401;

    public RenderStateShard$class_4686(boolean color, boolean depth) {
        super("write_mask_state", () -> {
            if (!depth) {
                CbGlRenderSystem.depthMask(false);
            }
            if (!color) {
                CbShardRenderSystem.colorMask(false, false, false, false);
            }
        }, () -> {
            if (!depth) {
                CbGlRenderSystem.depthMask(true);
            }
            if (!color) {
                CbShardRenderSystem.colorMask(true, true, true, true);
            }
        });
        this.field_21400 = color;
        this.field_21401 = depth;
    }

    @Override
    public String toString() {
        return this.field_21363 + "[writeColor=" + this.field_21400 + ", writeDepth=" + this.field_21401 + "]";
    }
}
