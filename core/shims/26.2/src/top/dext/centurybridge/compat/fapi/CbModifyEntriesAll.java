package top.dext.centurybridge.compat.fapi;

import net.minecraft.world.item.CreativeModeTab;

/**
 * Stand-in for fabric-item-group-api-v1's ItemGroupEvents$ModifyEntriesAll
 * (1.20.x); see CbModifyEntries / CbItemGroupEvents.
 */
@FunctionalInterface
public interface CbModifyEntriesAll {
    void modifyEntries(CreativeModeTab group, CbItemGroupEntries entries);
}
