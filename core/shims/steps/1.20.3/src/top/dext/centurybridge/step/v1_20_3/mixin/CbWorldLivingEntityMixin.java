package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_1309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldEntities;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldLegacyEntity;

/**
 * canBreatheUnderwater for an entity class converted from 1.20.2: the 1.20.2 answer
 * (CbWorldEntities.legacyBreathesUnderwater), not 1.20.3's entity type tag, which knows vanilla types only.
 * The method's final is lifted by the layer's access widener, so a 1.20.2 override loads and is called again.
 */
@Mixin(class_1309.class)
public abstract class CbWorldLivingEntityMixin {

    @Inject(method = "method_6094", at = @At("HEAD"), cancellable = true)
    private void cb$legacyBreathing(CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof CbWorldLegacyEntity) {
            cir.setReturnValue(CbWorldEntities.legacyBreathesUnderwater((class_1309) (Object) this));
        }
    }
}
