package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbAdvNodes;

/**
 * Every AdvancementNode records the advancement it places: a 1.20.1 mod's Advancement finds its parent,
 * children and tree from it (CbAdvNodes).
 */
@Mixin(net.minecraft.class_8781.class)
public abstract class CbAdvNodeTrack {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$track(CallbackInfo ci) {
        CbAdvNodes.track((net.minecraft.class_8781) (Object) this);
    }
}
