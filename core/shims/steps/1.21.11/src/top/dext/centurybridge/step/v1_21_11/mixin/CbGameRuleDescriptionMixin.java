package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.class_12279;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21_11.rt.CbGameRules;

/**
 * A 1.21.10 mod rule's description id stays "gamerule." + its 1.21.10 name (Key.getDescriptionId), where the mod's
 * language files translate it; 1.21.11 would derive one from the identifier the rule is registered under.
 */
@Mixin(class_12279.class)
public abstract class CbGameRuleDescriptionMixin {
    @Inject(method = "method_76150", at = @At("HEAD"), cancellable = true)
    private void cb$legacyDescriptionId(CallbackInfoReturnable<String> cir) {
        String id = CbGameRules.descriptionId((class_12279<?>) (Object) this);
        if (id != null) {
            cir.setReturnValue(id);
        }
    }
}
