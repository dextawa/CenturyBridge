package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelFields;

/** 1.21.1 FelineModel's protected state (field_3434; 1, standing, as 1.21.1's constructor set it), see CbRenderModelFields */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_582.class)
public abstract class CbRenderFelineMixin implements CbRenderModelFields.Feline {

    protected int field_3434 = 1;

    @Override
    public int cb$felineState() {
        return this.field_3434;
    }

    @Override
    public void cb$felineState(int state) {
        this.field_3434 = state;
    }
}
