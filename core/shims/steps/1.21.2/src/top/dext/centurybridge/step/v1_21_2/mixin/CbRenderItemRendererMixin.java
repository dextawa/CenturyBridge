package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderBake;

/**
 * 1.21.1 ItemRenderer.getItemModelShaper(): 1.21.2 keeps the shaper (field_4732), without the getter.
 * Item model overrides: 1.21.1 asked the model's ItemOverrides resolve(model, stack, level, entity, seed); 1.21.2 asks
 * findOverride(stack, level, entity, seed), which a 1.21.1 subclass overriding resolve does not answer, so its resolve
 * is asked with the model, as 1.21.1 asked it (CbRenderBake.Legacy).
 */
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

    @Redirect(method = "method_64256(Lnet/minecraft/class_1087;Lnet/minecraft/class_1799;Lnet/minecraft/class_1937;Lnet/minecraft/class_1309;I)Lnet/minecraft/class_1087;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_806;method_64063(Lnet/minecraft/class_1799;Lnet/minecraft/class_638;Lnet/minecraft/class_1309;I)Lnet/minecraft/class_1087;"))
    private net.minecraft.class_1087 cb$legacyResolve(net.minecraft.class_806 overrides, net.minecraft.class_1799 stack, net.minecraft.class_638 level,
                                                       net.minecraft.class_1309 entity, int seed, net.minecraft.class_1087 model) {
        if (overrides instanceof CbRenderBake.LegacyOverrides legacy) {
            return legacy.method_3495(model, stack, level, entity, seed);
        }
        return overrides.method_64063(stack, level, entity, seed);
    }
}
