package top.dext.centurybridge.compat.fapi.event.registry;

/**
 * Fabric API registry sync v0's FabricRegistry (0.92 backported it to 1.20.1; 1.20.2's Fabric API does
 * not have it, and ships it again from 1.21.1): registry aliases -- an old id that resolves to the id
 * an entry is registered under. Every Registry extends it through this layer's interface mixin, and
 * SimpleRegistry implements it (CbFapiSimpleRegistryAliases), as 1.20.1's registry sync made them; any
 * other registry keeps this default, as in 1.20.1.
 */
public interface FabricRegistry {

    default void addAlias(net.minecraft.class_2960 old, net.minecraft.class_2960 newId) {
        throw new UnsupportedOperationException("implemented via mixin");
    }
}
