package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.registry;

import java.util.Map;
import java.util.WeakHashMap;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.registry.DynamicRegistryView;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.event.registry.RegistryEntryRemovedCallback;

/**
 * The registry side of Fabric API 1.20.4's API on 1.20.5: the per-registry entry-removed events
 * (RegistryEntryRemovedCallback, fired by CbFapiRegistryRemovalMixin), DynamicRegistryView's
 * registerEntryRemoved (instanceRedirects: 1.20.4's body, on the view's own lookup), and the registry
 * key a 1.20.4 call names an object by (SculkSensorFrequencyRegistry.register: a GameEvent, 1.20.5 its key).
 */
public final class CbFapiRegistries {

    private static final Map<net.minecraft.class_2378<?>, Event<?>> REMOVED = new WeakHashMap<>();

    private CbFapiRegistries() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static synchronized <T> Event<RegistryEntryRemovedCallback<T>> removedEvent(net.minecraft.class_2378<T> registry) {
        return (Event<RegistryEntryRemovedCallback<T>>) REMOVED.computeIfAbsent(registry, r -> EventFactory.createArrayBacked(
            RegistryEntryRemovedCallback.class, (RegistryEntryRemovedCallback[] callbacks) -> (rawId, id, object) -> {
                for (RegistryEntryRemovedCallback callback : callbacks) {
                    callback.onEntryRemoved(rawId, id, object);
                }
            }));
    }

    /** the registry's entry-removed event, or null when nobody listens (SimpleRegistry.add's check) */
    @SuppressWarnings("unchecked")
    public static synchronized <T> Event<RegistryEntryRemovedCallback<T>> removedEventIfAny(net.minecraft.class_2378<T> registry) {
        return (Event<RegistryEntryRemovedCallback<T>>) REMOVED.get(registry);
    }

    /** DynamicRegistryView.registerEntryRemoved(registryRef, callback): 1.20.4's body */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void registerEntryRemoved(DynamicRegistryView view, net.minecraft.class_5321 registryRef,
                                            RegistryEntryRemovedCallback callback) {
        view.getOptional(registryRef).ifPresent(registry -> removedEvent((net.minecraft.class_2378) registry).register(callback));
    }

    /** a game event's registry key (1.20.5's SculkSensorFrequencyRegistry.register names the event by it) */
    public static net.minecraft.class_5321<net.minecraft.class_5712> gameEventKey(net.minecraft.class_5712 event) {
        return net.minecraft.class_7923.field_41171.method_29113(event).orElseThrow(
            () -> new IllegalArgumentException("Game event " + event + " is not registered"));
    }
}
