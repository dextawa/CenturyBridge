package top.dext.centurybridge.compat.fapi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

/**
 * fabric-item-group-api-v1's ItemGroupEvents as 1.20.x mods call it. The module
 * became fabric-creative-tab-api-v1 in the 26.x mojang-name era:
 * ItemGroupEvents -&gt; CreativeModeTabEvents, ModifyEntries -&gt; ModifyOutput
 * (SAM name changed, so a pure class rename would leave every 1.20.1 lambda an
 * AbstractMethodError), FabricItemGroupEntries -&gt; FabricCreativeModeTabOutput.
 * The converter class-renames the old API here; each old-shape listener is
 * forwarded from the real new event with the output wrapped in
 * {@link CbItemGroupEntries}, so timing and per-tab semantics are identical.
 */
public final class CbItemGroupEvents {

    private static final Map<ResourceKey<CreativeModeTab>, Event<CbModifyEntries>> EVENTS = new ConcurrentHashMap<>();

    /** was ItemGroupEvents.MODIFY_ENTRIES_ALL */
    public static final Event<CbModifyEntriesAll> MODIFY_ENTRIES_ALL = EventFactory.createArrayBacked(CbModifyEntriesAll.class,
        listeners -> (tab, entries) -> {
            for (CbModifyEntriesAll l : listeners) {
                l.modifyEntries(tab, entries);
            }
        });

    static {
        CreativeModeTabEvents.MODIFY_OUTPUT_ALL.register((tab, output) ->
            MODIFY_ENTRIES_ALL.invoker().modifyEntries(tab, new CbItemGroupEntries(output)));
    }

    /** was ItemGroupEvents.modifyEntriesEvent(RegistryKey&lt;ItemGroup&gt;) */
    public static Event<CbModifyEntries> modifyEntriesEvent(ResourceKey<CreativeModeTab> key) {
        return EVENTS.computeIfAbsent(key, k -> {
            Event<CbModifyEntries> ev = EventFactory.createArrayBacked(CbModifyEntries.class,
                listeners -> entries -> {
                    for (CbModifyEntries l : listeners) {
                        l.modifyEntries(entries);
                    }
                });
            CreativeModeTabEvents.modifyOutputEvent(k).register(output ->
                ev.invoker().modifyEntries(new CbItemGroupEntries(output)));
            return ev;
        });
    }

    private CbItemGroupEvents() {
    }

}
