package top.dext.centurybridge.step.v1_21_2.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_2.rt.CbLevelBlocks;

/**
 * BlockBehaviour.getDescriptionId is final since 1.21.2: a 1.21.1 block class's getDescriptionId override is renamed
 * (overrideRenames) and the final method hands the call to it, as 1.21.1's virtual call reached it (CbLevelBlocks).
 */
@Mixin(net.minecraft.class_4970.class)
public abstract class CbLevelBlockNameMixin {

    @Inject(method = "method_63499", at = @At("HEAD"), cancellable = true)
    private void cb$legacyDescriptionId(CallbackInfoReturnable<String> cir) {
        String id = CbLevelBlocks.legacyDescriptionId(this);
        if (id != null) {
            cir.setReturnValue(id);
        }
    }
}
