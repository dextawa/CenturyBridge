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
 * (registerListener / removeListener / stopListening). 26.2's own bodies still run (a 26.2 mod's mixins on them still
 * fire): they keep the instance in 26.2's trigger map as for every trigger, which only SimpleCriterionTrigger reads.
 */
@Mixin(PlayerAdvancements.class)
public abstract class CbS26PlayerAdvancementsMixin {

    @Inject(method = "addListener", at = @At("HEAD"))
    private void cb$legacyAdd(Criterion<?> criterion, PlayerAdvancements.TriggerInstanceKey key, CallbackInfo ci) {
        CbS26Triggers.added((PlayerAdvancements) (Object) this, criterion, key);
    }

    @Inject(method = "removeListener", at = @At("HEAD"))
    private void cb$legacyRemove(CriterionTrigger<?> trigger, PlayerAdvancements.TriggerInstanceKey key, CallbackInfo ci) {
        CbS26Triggers.removed((PlayerAdvancements) (Object) this, trigger, key);
    }

    @Inject(method = "clearTriggers", at = @At("HEAD"))
    private void cb$legacyClear(CallbackInfo ci) {
        CbS26Triggers.cleared((PlayerAdvancements) (Object) this);
    }
}
