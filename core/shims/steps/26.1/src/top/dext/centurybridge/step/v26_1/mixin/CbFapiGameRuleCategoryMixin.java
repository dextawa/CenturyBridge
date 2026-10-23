package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v26_1.rt.CbFapiGameRules;

/**
 * GameRuleCategory.label() of a category standing in for a 1.21.11 Fabric API CustomGameRuleCategory: the
 * custom category's name, which 1.21.11's edit-rules screen headed its rules with (26.1 would translate
 * gamerule.category.<namespace>.<path>, a key the 1.21.11 mod never shipped). Every other category is
 * untouched (rt/CbFapiGameRules).
 */
@Mixin(GameRuleCategory.class)
public abstract class CbFapiGameRuleCategoryMixin {

    @Inject(method = "label", at = @At("HEAD"), cancellable = true)
    private void cb$customName(CallbackInfoReturnable<MutableComponent> cir) {
        Component name = CbFapiGameRules.customName((GameRuleCategory) (Object) this);
        if (name != null) {
            cir.setReturnValue(name.copy());
        }
    }
}
