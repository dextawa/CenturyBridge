package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockBanner;

/** The world's banner pattern tags, bound on 1.20.4's registry of patterns too, by key (CbBlockBanner.tagsBound) */
@Mixin(net.minecraft.class_2370.class)
public abstract class CbBlockBannerTagsMixin {

    @Inject(method = "method_40257", at = @At("TAIL"))
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void cb$mirrorBannerTags(java.util.Map tags, CallbackInfo ci) {
        CbBlockBanner.tagsBound((net.minecraft.class_2378) (Object) this, tags);
    }
}
