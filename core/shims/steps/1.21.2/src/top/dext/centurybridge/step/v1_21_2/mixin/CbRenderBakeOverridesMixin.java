package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderBakeOverrides;

/**
 * SimpleBakedModel: 1.21.1's protected overrides field (field_5412), set right after construction from the argument
 * 1.21.1's constructor took (CbRenderBakeOverrides), and getOverrides() answering it (1.21.2's EMPTY when unset).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_1093.class)
public abstract class CbRenderBakeOverridesMixin implements CbRenderBakeOverrides.Holder {

    protected net.minecraft.class_806 field_5412;

    @Override
    public void cb$overrides(net.minecraft.class_806 overrides) {
        this.field_5412 = overrides;
    }

    public net.minecraft.class_806 method_4710() {
        return CbRenderBakeOverrides.answer(this.field_5412);
    }
}
