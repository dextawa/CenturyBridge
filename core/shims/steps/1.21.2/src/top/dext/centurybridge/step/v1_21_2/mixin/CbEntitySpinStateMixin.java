package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10039;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntitySmall;

/** The spin a mod item entity's 1.21.1 getSpin override answered at extraction (CbEntitySpinRenderMixin). */
@Environment(EnvType.CLIENT)
@Mixin(class_10039.class)
public abstract class CbEntitySpinStateMixin implements CbEntitySmall.SpinState {

    @Unique
    private float cb$spin = Float.NaN;

    @Override
    public float cb$spin() {
        return this.cb$spin;
    }

    @Override
    public void cb$spin(float spin) {
        this.cb$spin = spin;
    }
}
