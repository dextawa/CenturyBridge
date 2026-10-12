package top.dext.centurybridge.step.v1_20_5.compat.fapi.event.registry;

import net.fabricmc.fabric.api.event.Event;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.registry.CbFapiRegistries;

/**
 * Fabric API 0.97.3's (1.20.4) RegistryEntryRemovedCallback, removed in 1.20.5: told when a registry entry
 * gives way to another object under the same id. 1.20.4 fired it from SimpleRegistry.set; 1.20.5 has no set,
 * and the one path left that puts another object under a registered id -- SimpleRegistry.add of a key the
 * registry already has, which 1.20.5 reports and goes on with outside development -- fires it here, with the
 * old object's raw id (CbFapiRegistryRemovalMixin).
 */
@FunctionalInterface
public interface RegistryEntryRemovedCallback<T> {

    void onEntryRemoved(int rawId, net.minecraft.class_2960 id, T object);

    static <T> Event<RegistryEntryRemovedCallback<T>> event(net.minecraft.class_2378<T> registry) {
        return CbFapiRegistries.removedEvent(registry);
    }
}
