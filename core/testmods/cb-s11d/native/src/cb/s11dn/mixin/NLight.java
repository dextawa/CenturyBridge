package cb.s11dn.mixin;

import cb.s11dn.Counters;
import net.minecraft.class_11398;
import net.minecraft.class_12076;
import net.minecraft.class_12197;
import net.minecraft.class_12202;
import net.minecraft.class_12206;
import net.minecraft.class_2874;
import net.minecraft.class_308;
import net.minecraft.class_337;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_7285;
import net.minecraft.class_765;
import net.minecraft.class_9779;
import net.minecraft.class_9975;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** native 1.21.11 mixin kept call counter (cb-s11d) */
@Mixin(class_765.class)
public abstract class NLight {
    @Inject(method = "method_3313", at = @At("HEAD"))
    private void cbn$update(float partialTick, CallbackInfo ci) {
        Counters.hit("LightTexture.updateLightTexture");
    }

    /** the sky light color the lightmap takes (identity: records it; runs after the layer's route) */
    @ModifyArg(method = "method_3313",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_9848;method_64963(I)Lorg/joml/Vector3f;", ordinal = 0))
    private int cbn$skyLightColor(int color) {
        Counters.skyLightColor = color;
        return color;
    }

    /** the sky light factor the lightmap takes (identity) */
    @ModifyArg(method = "method_3313", at = @At(value = "INVOKE",
        target = "Lcom/mojang/blaze3d/buffers/Std140Builder;putFloat(F)Lcom/mojang/blaze3d/buffers/Std140Builder;", ordinal = 1))
    private float cbn$skyLightFactor(float factor) {
        Counters.skyLightFactor = factor;
        return factor;
    }
}
