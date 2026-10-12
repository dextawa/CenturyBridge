package top.dext.centurybridge.step.v1_21.mixin;

import net.minecraft.class_3551;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21.rt.CbWorldConstants;

/**
 * DataFixers: 1.20.6 built the fixer in this class's static initializer, optimized for
 * SharedConstants.DATA_FIX_TYPES_TO_OPTIMIZE as it stood then. 1.21's initializer builds it unoptimized; at its
 * end the value a 1.20.6 mod wrote gets that effect (CbWorldConstants.dataFixerBuilt).
 */
@Mixin(class_3551.class)
public abstract class CbWorldDataFixersMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void cb$fixerBuilt(CallbackInfo ci) {
        CbWorldConstants.dataFixerBuilt();
    }
}
