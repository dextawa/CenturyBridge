package top.dext.centurybridge.rt.v1_20_2;

/**
 * The 1.20.1 UserCache.findByNameAsync(name, consumer) shape (1.20.2: findByNameAsync(name), a
 * future). A converted cache subclass overriding it gets this interface and the 1.20.2 shape
 * synthesized to reach it (overrideHooks bridge, CbServer.findByNameAsync).
 */
public interface CbLegacyUserCacheLookup {

    /** findByNameAsync(name, consumer) */
    void method_37156(String name, java.util.function.Consumer<java.util.Optional<com.mojang.authlib.GameProfile>> consumer);
}
