package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderModelFields;

/** 1.21.1 EndermanModel's public carrying / creepy (field_3371 / field_3370), see CbRenderModelFields */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_566.class)
public abstract class CbRenderEndermanMixin implements CbRenderModelFields.Enderman {

    public boolean field_3371;

    public boolean field_3370;

    @Override
    public boolean cb$carrying() {
        return this.field_3371;
    }

    @Override
    public boolean cb$creepy() {
        return this.field_3370;
    }

    @Override
    public void cb$enderman(boolean carrying, boolean creepy) {
        this.field_3371 = carrying;
        this.field_3370 = creepy;
    }
}
