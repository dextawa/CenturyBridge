package top.dext.centurybridge.step.v1_21_11.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2874;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_757;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_11.rt.CbDimensionEffectsRoutes;

/**
 * GameRenderer's two 1.21.10 effects reads, kept on 1.21.11's own calls (rt/CbDimensionEffectsRoutes decides whether
 * 1.21.10's answer stands in):
 * <ul>
 * <li>setLevel: 1.21.10 set up Lighting with effects().constantAmbientLight(); 1.21.11 passes
 * dimensionType().cardinalLightType() to Lighting.updateLevel -- that argument.</li>
 * <li>renderLevel: 1.21.10 took the world fog flag as isFoggyAt(camera x, z) || shouldCreateWorldFog() (it chose
 * the sky pass); 1.21.11 takes only shouldCreateWorldFog() -- the argument it hands LevelRenderer.renderLevel.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
@Mixin(class_757.class)
public abstract class CbDimEffectsLightingMixin {
    @Unique
    private class_638 cb$lightingLevel;

    @Inject(method = "method_71112(Lnet/minecraft/class_638;)V", at = @At("HEAD"))
    private void cb$lightingLevel(class_638 level, CallbackInfo ci) {
        this.cb$lightingLevel = level;
    }

    @ModifyArg(method = "method_71112(Lnet/minecraft/class_638;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_308;method_71036(Lnet/minecraft/class_2874$class_12325;)V"))
    private class_2874.class_12325 cb$lighting(class_2874.class_12325 own) {
        class_638 level = this.cb$lightingLevel;
        this.cb$lightingLevel = null;
        return CbDimensionEffectsRoutes.cardinalLight(level, own);
    }

    /**
     * renderLevel hands LevelRenderer.renderLevel !shouldCreateWorldFog() as its last argument (the sky pass): 1.21.10
     * handed !(isFoggyAt(camera x, z) || shouldCreateWorldFog())
     */
    @ModifyArg(method = "method_3188(Lnet/minecraft/class_9779;)V", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_761;method_22710(Lnet/minecraft/class_9922;Lnet/minecraft/class_9779;ZLnet/minecraft/class_4184;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V"),
        index = 9)
    private boolean cb$worldFog(boolean noFog) {
        class_757 self = (class_757) (Object) this;
        return !CbDimensionEffectsRoutes.worldFog(class_310.method_1551().field_1687, self.method_19418(), !noFog);
    }
}
