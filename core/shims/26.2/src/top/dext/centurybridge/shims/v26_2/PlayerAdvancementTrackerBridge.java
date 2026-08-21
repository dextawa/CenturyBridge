package top.dext.centurybridge.shims.v26_2;

import top.dext.centurybridge.rt.v26_2.Trackers;

import org.spongepowered.asm.mixin.Mixin;

/**
 * 1.20.1 -> 1.20.2: PlayerAdvancementTracker.getProgress takes AdvancementEntry
 * now; the legacy Advancement overload resolves the entry via the tracker map.
 */
@Mixin(net.minecraft.server.PlayerAdvancements.class)
public abstract class PlayerAdvancementTrackerBridge {

    public net.minecraft.advancements.AdvancementProgress getOrStartProgress(net.minecraft.advancements.Advancement advancement) {
        net.minecraft.advancements.AdvancementHolder entry = Trackers.ADVANCEMENT_ENTRIES.get(advancement);
        if (entry == null) {
            throw new UnsupportedOperationException(
                "CenturyBridge: advancement has no known id (not loaded via ServerAdvancementLoader)");
        }
        return ((net.minecraft.server.PlayerAdvancements) (Object) this).getOrStartProgress(entry);
    }

    /** legacy grantCriterion(Advancement, name) */
    public boolean award(net.minecraft.advancements.Advancement advancement, String criterion) {
        net.minecraft.advancements.AdvancementHolder entry = Trackers.ADVANCEMENT_ENTRIES.get(advancement);
        if (entry == null) {
            throw new UnsupportedOperationException(
                "CenturyBridge: advancement has no known entry (not loaded via ServerAdvancementLoader)");
        }
        return ((net.minecraft.server.PlayerAdvancements) (Object) this).award(entry, criterion);
    }
}
