package top.dext.centurybridge.step.v1_21_4.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_4.rt.CbRenderTail;

/**
 * 1.21.3's AbstractHorseRenderer took a scale and applied it in scale(state, pose) before LivingEntityRenderer's; 1.21.4 scales
 * horses in their model layers instead. A renderer built with 1.21.3's constructor keeps its scale and applies it as 1.21.3 did;
 * 1.21.4's own renderers have scale 1 (no change).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_875.class)
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class CbTailHorseScaleMixin extends net.minecraft.class_9990 implements CbRenderTail.CbHorseScale {

    private float cb$scale = 1.0f;

    private CbTailHorseScaleMixin() {
        super(null, null, null, 0.0f);
    }

    @Override
    public void cb$setScale(float scale) {
        this.cb$scale = scale;
    }

    @Override
    protected void method_4042(net.minecraft.class_10042 state, net.minecraft.class_4587 pose) {
        if (this.cb$scale != 1.0f) {
            pose.method_22905(this.cb$scale, this.cb$scale, this.cb$scale);
        }
        super.method_4042(state, pose);
    }
}
