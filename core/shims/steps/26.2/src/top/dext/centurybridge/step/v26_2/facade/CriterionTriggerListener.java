package top.dext.centurybridge.step.v26_2.facade;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.server.PlayerAdvancements;

/**
 * 26.1.2's CriterionTrigger.Listener (gone on 26.2, which keys a player's active triggers by
 * PlayerAdvancements.TriggerInstanceKey(advancement, criterion) in PlayerAdvancements itself): reached by
 * classRenames. The same record, with 26.1.2's run (award the criterion).
 */
public record CriterionTriggerListener<T extends CriterionTriggerInstance>(T trigger, AdvancementHolder advancement, String criterion) {
    public void run(PlayerAdvancements advancements) {
        advancements.award(advancement, criterion);
    }
}
