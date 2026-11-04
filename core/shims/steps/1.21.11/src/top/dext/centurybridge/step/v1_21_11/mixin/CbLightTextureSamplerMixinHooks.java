package top.dext.centurybridge.step.v1_21_11.mixin;

import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_757;
import net.minecraft.class_765;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_11.rt.CbSamplers;

/**
 * LightTexture(gameRenderer, minecraft) (class_765.&lt;init&gt;, javap -c both): 1.21.10 set its texture to LINEAR without
 * mipmaps (address mode left at the default REPEAT); 1.21.11 creates the same texture and drops the state. Replays
 * 1.21.10's write into the restored GpuTexture state (rt/CbSamplers, a 1.21.10 bindSampler of the lightmap view).
 * Reads and changes nothing 1.21.11 uses; default priority.
 */
@Environment(EnvType.CLIENT)
@Mixin(class_765.class)
public abstract class CbLightTextureSamplerMixinHooks {

    @Shadow
    @Final
    private GpuTexture field_57927;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void cb$v1_21_11$samplerState(class_757 gameRenderer, class_310 minecraft, CallbackInfo ci) {
        CbSamplers.vanillaState(this.field_57927, null, FilterMode.LINEAR);
    }
}
