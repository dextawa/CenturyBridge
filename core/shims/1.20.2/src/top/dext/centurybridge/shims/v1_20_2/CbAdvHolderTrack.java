package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbAdvancements;

/**
 * Every AdvancementHolder records its Advancement record as it is constructed: the id 1.20.1's
 * Advancement carried is found from the record a 1.20.1 mod holds (CbAdvancements).
 */
@Mixin(net.minecraft.class_8779.class)
public abstract class CbAdvHolderTrack {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$track(CallbackInfo ci) {
        CbAdvancements.track((net.minecraft.class_8779) (Object) this);
    }
}
