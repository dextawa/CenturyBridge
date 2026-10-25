package top.dext.centurybridge.step.v26_2.mixin;

import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.server.PlayerAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_2.rt.CbS26Triggers;

/**
 * A 26.1.2 trigger that keeps its own listeners (rt/CbS26Triggers.Legacy) is handed them where 26.2's
 * PlayerAdvancements registers, removes and clears its trigger map, as 26.1.2's PlayerAdvancements handed them
 * (registerListener / removeListener / stopListening); such a trigger's listeners stay out of 26.2's map, which
 * only SimpleCriterionTrigger reads.
 */
@Mixin(PlayerAdvancements.class)
public abstract class CbS26PlayerAdvancementsMixin {

    @Inject(method = "addListener", at = @At("HEAD"), cancellable = true)
    private void cb$legacyAdd(Criterion<?> criterion, PlayerAdvancements.TriggerInstanceKey key, CallbackInfo ci) {
        if (CbS26Triggers.added((PlayerAdvancements) (Object) this, criterion, key)) {
            ci.cancel();
        }
    }

    @Inject(method = "removeListener", at = @At("HEAD"), cancellable = true)
    private void cb$legacyRemove(CriterionTrigger<?> trigger, PlayerAdvancements.TriggerInstanceKey key, CallbackInfo ci) {
        if (CbS26Triggers.removed((PlayerAdvancements) (Object) this, trigger, key)) {
            ci.cancel();
        }
    }

    @Inject(method = "clearTriggers", at = @At("HEAD"))
    private void cb$legacyClear(CallbackInfo ci) {
        CbS26Triggers.cleared((PlayerAdvancements) (Object) this);
    }
}
