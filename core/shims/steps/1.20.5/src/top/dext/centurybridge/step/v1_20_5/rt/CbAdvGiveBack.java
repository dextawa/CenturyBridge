package top.dext.centurybridge.step.v1_20_5.rt;

import top.dext.centurybridge.step.v1_20_5.facade.LootDataManager;

/**
 * ServerAdvancementManager's 1.20.4 state, given back for the code that still reads it -- mods' mixins and the climbing
 * 1.20.2 / 1.20.3 layers' (CbAdvJsonVanillaMixin, CbAdvServerAdvancementManagerMixin; instanceFieldGetters, MixinBridge
 * carries their @Shadow reads through it).
 */
public final class CbAdvGiveBack {

    /** one LootDataManager per manager, as 1.20.4's manager held one */
    private static final java.util.Map<net.minecraft.class_2989, LootDataManager> LOOT =
        java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    /**
     * ServerAdvancementManager.lootData (class_2989.field_44468): 1.20.4's manager was given the server's LootDataManager
     * and validated advancements' loot references against it (validate -> method_54921); 1.20.5's takes the reload's
     * registry lookup instead (field_48787, javap: the same validation reads the loot registries through it). The 1.20.4
     * manager answering from that lookup (CbLootData.manager, the step's one view of the loot registries).
     */
    public static LootDataManager field_44468(net.minecraft.class_2989 manager) {
        return LOOT.computeIfAbsent(manager, m -> CbLootData.manager(m.field_48787));
    }

    private CbAdvGiveBack() {
    }
}
