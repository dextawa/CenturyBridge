package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_765;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_11.rt.CbDimensionEffectsRoutes;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * LightTexture.updateLightTexture: 1.21.10 read effects().forceBrightLightmap() twice, for the sky light factor and
 * the sky light color; 1.21.11 reads its SKY_LIGHT_FACTOR and SKY_LIGHT_COLOR attributes there and hands them to the
 * lightmap uniform: the color through ARGB.vector3fFromRGB24 (its only call here), the factor plus the end flash as the
 * second Std140Builder.putFloat. Those arguments, with 1.21.11's attribute reads kept, may carry 1.21.10's answers
 * (rt/CbDimensionEffectsRoutes); the ambient color and the flash follow the level's EndFlashState, which
 * CbClientLevelEffectsMixin decides the same way.
 * Route order: CbVanillaReads.ROUTE_FIRST, so a native 1.21.11 mod's injector at the same point runs after the route and
 * sees (and may change) the routed value instead of having its own replaced.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = class_765.class, priority = CbVanillaReads.ROUTE_FIRST)
public abstract class CbDimEffectsLightTextureMixin {
    @Unique
    private float cb$partialTick;

    @Inject(method = "method_3313(F)V", at = @At("HEAD"))
    private void cb$partialTick(float partialTick, CallbackInfo ci) {
        this.cb$partialTick = partialTick;
    }

    @ModifyArg(method = "method_3313(F)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9848;method_64963(I)Lorg/joml/Vector3f;", ordinal = 0))
    private int cb$skyLightColor(int own) {
        return CbDimensionEffectsRoutes.skyLightColor(class_310.method_1551().field_1687, own);
    }

    @ModifyArg(method = "method_3313(F)V", at = @At(value = "INVOKE",
        target = "Lcom/mojang/blaze3d/buffers/Std140Builder;putFloat(F)Lcom/mojang/blaze3d/buffers/Std140Builder;", ordinal = 1))
    private float cb$skyLightFactor(float own) {
        class_310 mc = class_310.method_1551();
        return CbDimensionEffectsRoutes.skyLightFactorSent(mc.field_1687, mc.field_1773.method_19418(), this.cb$partialTick, own);
    }
}
