package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/**
 * 1.21.1 ItemRenderer.getItemModelShaper(): 1.21.2 keeps the shaper (field_4732), without the getter.
 * Item model overrides: 1.21.1 asked the model's ItemOverrides resolve(model, stack, level, entity, seed); 1.21.2 asks
 * findOverride(stack, level, entity, seed), which a 1.21.1 subclass overriding resolve does not answer, so its resolve
 * is asked with the model, as 1.21.1 asked it (CbRenderBake.Legacy; CbRenderItemRendererMixinHooks, default priority).
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_918.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderItemRendererMixin {

    @Shadow
    @Final
    private net.minecraft.class_763 field_4732;

    /** 1.21.1 ItemRenderer.getItemModelShaper() */
    public net.minecraft.class_763 method_4012() {
        return this.field_4732;
    }
}
