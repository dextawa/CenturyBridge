package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.Optional;

/**
 * 1.20.4's registry members (area s4fin core) on 1.20.5's:
 * <ul>
 * <li>Per-entry lifecycles: 1.20.4's Registry.lifecycle(value) is 1.20.5's registrationInfo(key).lifecycle(); a
 *     1.20.4 registry's own lifecycle(value) answers 1.20.5's registrationInfo(key) (adapter hook, guarded so a
 *     subclass's super call reaches the vanilla registry's), its pack identity kept.</li>
 * <li>Registry.getHolder(id) (1.20.5 added it abstract): a 1.20.4 registry's getHolder of the key with that id.</li>
 * <li>MappedRegistry.registerMapping(id, key, value, lifecycle): 1.20.5 registers only at the next id; that is the
 *     id it registers at, any other refused as 1.20.5 has no place to put it.</li>
 * <li>Holder.is(holder) (1.20.5 added it abstract): a 1.20.4 holder is the same as another holding the same
 *     value.</li>
 * </ul>
 */
public final class CbFinRegistries {

    /** a 1.20.4 registry declaring lifecycle(value) */
    public interface Lifecycles {
        com.mojang.serialization.Lifecycle method_31139(Object value);
    }

    /** registry.lifecycle(value): the class's own, else the entry's registration lifecycle (null when unregistered) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static com.mojang.serialization.Lifecycle method_31139(net.minecraft.class_2378 registry, Object value) {
        if (registry instanceof Lifecycles l) {
            return l.method_31139(value);
        }
        return superLifecycle(registry, value);
    }

    @SuppressWarnings("rawtypes")
    public static com.mojang.serialization.Lifecycle method_31139(net.minecraft.class_2370 registry, Object value) {
        return method_31139((net.minecraft.class_2378) registry, value);
    }

    /** super.lifecycle(value) reaching MappedRegistry's: its entry's registration lifecycle */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static com.mojang.serialization.Lifecycle superLifecycle(net.minecraft.class_2378 registry, Object value) {
        Optional<net.minecraft.class_5321> key = registry.method_29113(value);
        return (com.mojang.serialization.Lifecycle) key.flatMap(k -> registry.method_57058(k)).map(i -> ((net.minecraft.class_9248) i).comp_2355()).orElse(null);
    }

    @SuppressWarnings("rawtypes")
    public static com.mojang.serialization.Lifecycle superLifecycle(net.minecraft.class_2370 registry, Object value) {
        return superLifecycle((net.minecraft.class_2378) registry, value);
    }

    /** registrationInfo(key) of a 1.20.4 registry declaring lifecycle(value): the registry's info, with its lifecycle */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional registrationInfo(Lifecycles legacy, net.minecraft.class_5321 key) {
        net.minecraft.class_2378 registry = (net.minecraft.class_2378) legacy;
        Object value = registry.method_29107(key);
        if (value == null) {
            return Optional.empty();
        }
        com.mojang.serialization.Lifecycle lifecycle = legacy.method_31139(value);
        // a MappedRegistry subclass: its vanilla info (guarded re-entry falls through to MappedRegistry's), for the pack identity
        Optional<net.minecraft.class_9226> pack = registry instanceof net.minecraft.class_2370
            ? ((Optional<net.minecraft.class_9248>) registry.method_57058(key)).flatMap(net.minecraft.class_9248::comp_2354)
            : Optional.empty();
        return Optional.of(new net.minecraft.class_9248(pack, lifecycle));
    }

    /** Registry.getHolder(id) of a 1.20.4 registry: its getHolder of the key with that id */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional getHolderById(net.minecraft.class_2378 registry, net.minecraft.class_2960 id) {
        return registry.method_40264(net.minecraft.class_5321.method_29179(registry.method_30517(), id));
    }

    /** MappedRegistry.registerMapping(id, key, value, lifecycle) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_6880.class_6883 registerMapping(net.minecraft.class_2370 registry, int id, net.minecraft.class_5321 key,
                                                                       Object value, com.mojang.serialization.Lifecycle lifecycle) {
        if (id != registry.method_10204()) {
            throw new IllegalArgumentException("1.20.5 registers " + key + " in " + registry.method_30517() + " at id "
                + registry.method_10204() + " only, not " + id);
        }
        return registry.method_10272(key, value, new net.minecraft.class_9248(Optional.empty(), lifecycle));
    }

    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_6880.class_6883 registerMapping(net.minecraft.class_2348 registry, int id, net.minecraft.class_5321 key,
                                                                       Object value, com.mojang.serialization.Lifecycle lifecycle) {
        return registerMapping((net.minecraft.class_2370) registry, id, key, value, lifecycle);
    }

    /** Holder.is(other) of a 1.20.4 holder: the same holder, or one holding the same value */
    @SuppressWarnings("rawtypes")
    public static boolean holderIs(net.minecraft.class_6880 self, net.minecraft.class_6880 other) {
        return self == other || self.comp_349() == other.comp_349();
    }

    private CbFinRegistries() {
    }
}
