package top.dext.centurybridge.step.v1_21_3.rt;

/**
 * Fabric API's registry aliases, native again in 1.21.3 (fabric-registry-sync-v0 0.114.1: FabricRegistry injected into
 * Registry, SimpleRegistryMixin keeps the alias map, addAlias and the alias-resolving lookups). The 1.21.2 layer's copy
 * retires here; its static form addAlias(registry, old, new) -- how converted calls through Registry reach it -- calls
 * the native member.
 */
public final class CbFapiRegistryAliases {

    private CbFapiRegistryAliases() {
    }

    /** registry.addAlias(old, new) through Registry, as the 1.21.2 layer's facade static took it */
    public static void addAlias(net.minecraft.class_2378<?> registry, net.minecraft.class_2960 old, net.minecraft.class_2960 newId) {
        ((net.fabricmc.fabric.api.event.registry.FabricRegistry) registry).addAlias(old, newId);
    }
}
