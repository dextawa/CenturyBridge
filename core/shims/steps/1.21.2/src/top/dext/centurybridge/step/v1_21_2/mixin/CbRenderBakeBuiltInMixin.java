package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderBakeOverrides;

/** BuiltInModel: the overrides 1.21.1's constructor took, answered by getOverrides() (CbRenderBakeOverrides) */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_1090.class)
public abstract class CbRenderBakeBuiltInMixin implements CbRenderBakeOverrides.Holder {

    @Unique
    private net.minecraft.class_806 cb$overrides;

    @Override
    public void cb$overrides(net.minecraft.class_806 overrides) {
        this.cb$overrides = overrides;
    }

    public net.minecraft.class_806 method_4710() {
        return CbRenderBakeOverrides.answer(this.cb$overrides);
    }
}
