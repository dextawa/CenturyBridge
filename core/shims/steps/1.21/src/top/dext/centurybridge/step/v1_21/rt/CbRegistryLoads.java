package top.dext.centurybridge.step.v1_21.rt;

/**
 * Where this layer's world-registry recording (CbLegacyRegistries.onServerLoaded / onClientLoaded) is installed for a
 * later layer that owns the registry-load point. Up to 1.21.11 this layer's own CbLegacyRegistryLoaderMixin
 * (RegistryDataLoader.Loader) records the registries and nothing reads the holder. At 1.21.11 -> 26.1 that class is
 * gone: this class and {@link Hook} retire and the wall's classRenames send this layer's references to the 26.1 layer's
 * rt/CbWallRegistryLoads, whose mixin calls the installed hook at 26.1's equivalent point.
 */
public final class CbRegistryLoads {
    private CbRegistryLoads() {
    }

    private static volatile Hook hook;

    public static void install(Hook h) {
        hook = h;
    }

    public static Hook hook() {
        return hook;
    }

    /** 26.1: CbWallRegistryLoads.Hook */
    public interface Hook {
        void cb$serverLoaded(net.minecraft.class_5321 key, net.minecraft.class_2385 registry, net.minecraft.class_6903.class_7863 lookup);

        void cb$clientLoaded(net.minecraft.class_5321 key, net.minecraft.class_2385 registry);
    }
}
