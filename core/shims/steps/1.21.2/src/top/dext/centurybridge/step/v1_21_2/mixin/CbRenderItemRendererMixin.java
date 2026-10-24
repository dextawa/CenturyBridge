package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** 1.21.1 ItemRenderer.getItemModelShaper(): 1.21.2 keeps the shaper (field_4732), without the getter */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_918.class)
public abstract class CbRenderItemRendererMixin {

    @Shadow
    @Final
    private net.minecraft.class_763 field_4732;

    /** 1.21.1 ItemRenderer.getItemModelShaper() */
    public net.minecraft.class_763 method_4012() {
        return this.field_4732;
    }
}
