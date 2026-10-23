package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

/**
 * Registry.register for the 1.21.11 values 26.1 registers in another form: a 1.21.11 mod registers its loot
 * function type (a record around a MapCodec) where 26.1's registry holds the MapCodec itself. A value this
 * layer gives back as a {@link Wrapper} is registered as what 26.1 holds; the mod gets its own value back,
 * as 1.21.11's Registry.register returned the value it was given. Every other value is registered as it is.
 */
public final class CbWallRegistry {

    private CbWallRegistry() {
    }

    /** a 1.21.11 value that 26.1 registers as {@link #cb$unwrap()} */
    public interface Wrapper {
        Object cb$unwrap();
    }

    private static Object registered(Object value) {
        return value instanceof Wrapper w ? w.cb$unwrap() : value;
    }

    /** 1.21.11 Registry.register(Registry, String, T) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object register(Registry registry, String name, Object value) {
        Object r = Registry.register(registry, name, registered(value));
        return value instanceof Wrapper ? value : r;
    }

    /** 1.21.11 Registry.register(Registry, Identifier, T) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object register(Registry registry, Identifier id, Object value) {
        Object r = Registry.register(registry, id, registered(value));
        return value instanceof Wrapper ? value : r;
    }

    /** 1.21.11 Registry.register(Registry, ResourceKey, T) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object register(Registry registry, ResourceKey key, Object value) {
        Object r = Registry.register(registry, key, registered(value));
        return value instanceof Wrapper ? value : r;
    }

    /**
     * 1.21.11 Registry.registerForHolder(Registry, ResourceKey, T): the holder 26.1 makes, of the registered
     * form (a wrapped value's holder holds what 26.1 registered)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Holder.Reference registerForHolder(Registry registry, ResourceKey key, Object value) {
        return Registry.registerForHolder(registry, key, registered(value));
    }

    /** 1.21.11 Registry.registerForHolder(Registry, Identifier, T) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Holder.Reference registerForHolder(Registry registry, Identifier id, Object value) {
        return Registry.registerForHolder(registry, id, registered(value));
    }
}
