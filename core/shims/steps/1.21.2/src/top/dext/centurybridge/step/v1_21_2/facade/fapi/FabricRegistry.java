package top.dext.centurybridge.step.v1_21_2.facade.fapi;

/**
 * FabricRegistry of 1.21.1's Fabric API (0.116; absent from 1.21.2's 0.106.1), classRenamed: 0.116 injected it
 * into Registry, and MappedRegistry implemented addAlias (an old id that lookups resolve to another id). The
 * layer implements it on MappedRegistry as 0.116 did (CbRestFapiRegistryAliasMixin); any other registry
 * answers 0.116's default.
 */
public interface FabricRegistry {

    default void addAlias(net.minecraft.class_2960 old, net.minecraft.class_2960 newId) {
        throw new UnsupportedOperationException("implemented via mixin");
    }

    /** registry.addAlias(old, new) called through Registry (0.116's injected interface member) */
    static void addAlias(net.minecraft.class_2378<?> registry, net.minecraft.class_2960 old, net.minecraft.class_2960 newId) {
        if (registry instanceof FabricRegistry fabric) {
            fabric.addAlias(old, newId);
            return;
        }
        throw new UnsupportedOperationException("implemented via mixin");
    }
}
