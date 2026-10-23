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
        return registry.method_10272(key, value, registrationInfo(registry, key, lifecycle));
    }

    /**
     * The registration info of a registerMapping: the one 1.20.5 handed {@link #cbRegister} for this
     * registration when the 1.20.4 override passes it on unchanged (its pack identity kept), else the
     * lifecycle alone (1.20.4 had no pack identity).
     */
    private static net.minecraft.class_9248 registrationInfo(net.minecraft.class_2370<?> registry, net.minecraft.class_5321<?> key,
                                                            com.mojang.serialization.Lifecycle lifecycle) {
        Object[] top = REGISTERING.get().peek();
        if (top != null && top[0] == registry && top[1] == key && ((net.minecraft.class_9248) top[2]).comp_2355() == lifecycle) {
            return (net.minecraft.class_9248) top[2];
        }
        return new net.minecraft.class_9248(Optional.empty(), lifecycle);
    }

    /** registrations in progress through a 1.20.4 override: {registry, key, info} */
    private static final ThreadLocal<java.util.ArrayDeque<Object[]>> REGISTERING = ThreadLocal.withInitial(java.util.ArrayDeque::new);

    /** a 1.20.4 MappedRegistry subclass's registerMapping(id, key, value, lifecycle) (the adapter the override hook adds) */
    public interface LegacyRegisterMapping {
        net.minecraft.class_6880.class_6883<?> method_46744(int id, net.minecraft.class_5321<?> key, Object value,
                                                            com.mojang.serialization.Lifecycle lifecycle);
    }

    /**
     * 1.20.5 registers through register(key, value, info); 1.20.4's register(key, value, lifecycle) was
     * registerMapping(next id, key, value, lifecycle), which a subclass overrode to see every
     * registration. Its override gets exactly that; a super call passing the lifecycle on registers
     * with the info 1.20.5 gave.
     */
    public static net.minecraft.class_6880.class_6883<?> cbRegister(LegacyRegisterMapping registry, net.minecraft.class_5321<?> key,
                                                                    Object value, net.minecraft.class_9248 info) {
        java.util.ArrayDeque<Object[]> stack = REGISTERING.get();
        stack.push(new Object[] {registry, key, info});
        try {
            return registry.method_46744(((net.minecraft.class_2370<?>) registry).method_10204(), key, value, info.comp_2355());
        } finally {
            stack.pop();
        }
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

    // ------------------------------------------------------------------ BuiltInRegistries.POTION

    private static volatile net.minecraft.class_7922<net.minecraft.class_1842> potionRegistry;

    /**
     * BuiltInRegistries.POTION, a DefaultedRegistry in 1.20.4 (default minecraft:empty, Potions.EMPTY) and
     * a plain registry in 1.20.5, which has no empty potion. The 1.20.4 field reads a view of 1.20.5's
     * registry that is also a WritableRegistry (Registry.register writes through it): every call goes to
     * 1.20.5's registry, the DefaultedRegistry methods answer as 1.20.4's did -- the default key is
     * Potions.EMPTY_ID, an unknown id or number is the empty potion (CbPotions.EMPTY), and the empty
     * potion's key is the default key.
     */
    @SuppressWarnings("unchecked")
    public static net.minecraft.class_7922<net.minecraft.class_1842> potionRegistry() {
        net.minecraft.class_7922<net.minecraft.class_1842> view = potionRegistry;
        if (view == null) {
            net.minecraft.class_2378<net.minecraft.class_1842> real = net.minecraft.class_7923.field_41179;
            view = (net.minecraft.class_7922<net.minecraft.class_1842>) java.lang.reflect.Proxy.newProxyInstance(CbFinRegistries.class.getClassLoader(),
                new Class<?>[] {net.minecraft.class_7922.class, net.minecraft.class_2385.class},
                (proxy, method, args) -> potionCall(real, proxy, method, args));
            potionRegistry = view;
        }
        return view;
    }

    private static Object potionCall(net.minecraft.class_2378<net.minecraft.class_1842> real, Object proxy,
                                     java.lang.reflect.Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "method_10137": // getDefaultKey()
                if (method.getParameterCount() == 0) {
                    return CbPotions.emptyId().method_29177();
                }
                break;
            case "method_10221": // getKey(value)
                if (method.getParameterCount() == 1) {
                    net.minecraft.class_2960 key = real.method_10221((net.minecraft.class_1842) args[0]);
                    return key != null ? key : CbPotions.emptyId().method_29177();
                }
                break;
            case "method_10223": // get(id)
                if (method.getParameterCount() == 1 && args[0] instanceof net.minecraft.class_2960 id) {
                    net.minecraft.class_1842 value = real.method_10223(id);
                    return value != null ? value : CbPotions.EMPTY;
                }
                break;
            case "method_10200": // byId(id)
                if (method.getParameterCount() == 1 && args[0] instanceof Integer id) {
                    net.minecraft.class_1842 value = real.method_10200(id);
                    return value != null ? value : CbPotions.EMPTY;
                }
                break;
            case "equals":
                if (method.getParameterCount() == 1) {
                    return proxy == args[0];
                }
                break;
            case "hashCode":
                if (method.getParameterCount() == 0) {
                    return System.identityHashCode(proxy);
                }
                break;
            default:
                break;
        }
        try {
            return method.invoke(real, args);
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw e.getCause();
        }
    }

    // ------------------------------------------------------------------ RegistrySynchronization.NETWORK_CODEC

    private static volatile com.mojang.serialization.Codec<net.minecraft.class_5455> registryNetworkCodec;

    /**
     * RegistrySynchronization.NETWORK_CODEC (1.20.5 syncs each registry in a packet of its own and
     * dropped it): 1.20.4's codec of a registry access's synchronized registries -- a map from registry
     * id to {type: registry id, value: the registry in RegistryCodecs.networkCodec form (experimental)},
     * decoded into an immutable registry access. The synchronized registries and their element codecs
     * are 1.20.5's (RegistryDataLoader.SYNCHRONIZED_REGISTRIES), the ones the running game sends.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static com.mojang.serialization.Codec<net.minecraft.class_5455> registryNetworkCodec() {
        com.mojang.serialization.Codec<net.minecraft.class_5455> codec = registryNetworkCodec;
        if (codec == null) {
            java.util.Map<net.minecraft.class_5321<?>, com.mojang.serialization.Codec<?>> elements = new java.util.LinkedHashMap<>();
            for (net.minecraft.class_7655.class_7657<?> data : net.minecraft.class_7655.field_48709) {
                elements.put(data.comp_985(), data.comp_986());
            }
            com.mojang.serialization.Codec<net.minecraft.class_5321<? extends net.minecraft.class_2378<?>>> keyCodec =
                net.minecraft.class_2960.field_25139.xmap(net.minecraft.class_5321::method_29180, net.minecraft.class_5321::method_29177);
            java.util.function.Function<net.minecraft.class_2378<?>, com.mojang.serialization.DataResult<net.minecraft.class_5321<? extends net.minecraft.class_2378<?>>>> typeOf =
                registry -> com.mojang.serialization.DataResult.success((net.minecraft.class_5321) registry.method_30517());
            java.util.function.Function<net.minecraft.class_5321<? extends net.minecraft.class_2378<?>>,
                    com.mojang.serialization.DataResult<com.mojang.serialization.MapCodec<net.minecraft.class_2378<?>>>> codecOf = key -> {
                com.mojang.serialization.Codec element = elements.get(key);
                if (element == null) {
                    return com.mojang.serialization.DataResult.error(() -> "Unknown or not serializable registry: " + key);
                }
                return com.mojang.serialization.DataResult.success((com.mojang.serialization.MapCodec)
                    CbFinData.networkCodec((net.minecraft.class_5321) key, com.mojang.serialization.Lifecycle.experimental(), element)
                        .fieldOf("value"));
            };
            com.mojang.serialization.Codec<net.minecraft.class_2378<?>> registryCodec =
                keyCodec.<net.minecraft.class_2378<?>>partialDispatch("type", typeOf, codecOf);
            codec = com.mojang.serialization.Codec.unboundedMap(keyCodec, registryCodec).xmap(
                map -> new net.minecraft.class_5455.class_6891(map),
                access -> {
                    java.util.Map<net.minecraft.class_5321<? extends net.minecraft.class_2378<?>>, net.minecraft.class_2378<?>> out =
                        new java.util.LinkedHashMap<>();
                    access.method_40311().filter(e -> elements.containsKey(e.comp_350())).forEach(e -> out.put(e.comp_350(), e.comp_351()));
                    return out;
                });
            registryNetworkCodec = codec;
        }
        return codec;
    }

    private CbFinRegistries() {
    }
}
