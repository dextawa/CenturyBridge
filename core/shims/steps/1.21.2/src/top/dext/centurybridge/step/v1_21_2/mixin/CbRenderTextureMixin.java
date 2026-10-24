package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.21.1 AbstractTexture's protected blur flag (field_5203), which 1.21.1's setFilter(blur, mipmap) stored (as it
 * stored mipmap, which 1.21.2 keeps): put back and stored by 1.21.2's setFilter the same way.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_1044.class)
public abstract class CbRenderTextureMixin {

    protected boolean field_5203;

    @Inject(method = "method_4527", at = @At("HEAD"))
    private void cb$storeBlur(boolean blur, boolean mipmap, CallbackInfo ci) {
        this.field_5203 = blur;
    }
}
