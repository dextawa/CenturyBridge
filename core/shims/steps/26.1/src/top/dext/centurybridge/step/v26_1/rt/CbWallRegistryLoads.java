package top.dext.centurybridge.step.v26_1.rt;

import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

/**
 * 26.1's point where a world registry has its contents (elements), its tags not yet bound
 * (mixin/CbWallRegistryLoadMixin: RegistryLoadTask.registerTags HEAD), handed to the climbing 1.21 layer. Up to 1.21.11
 * the 1.21 layer learnt its world registries from RegistryDataLoader.Loader (class_7655$class_9158, gone in 26.1); its
 * rt/CbRegistryLoads and Hook become these at the wall (classRenames), so its install call reaches this holder directly.
 * Without a 1.21 layer nothing is installed and nothing runs.
 */
public final class CbWallRegistryLoads {
    private CbWallRegistryLoads() {
    }

    private static volatile Hook hook;

    public static void install(Hook h) {
        hook = h;
    }

    public static Hook hook() {
        return hook;
    }

    /** the 1.21 layer's CbRegistryLoads.Hook */
    public interface Hook {
        /** a server's registry loaded from its datapacks (1.21.11: Loader.loadFromResources TAIL) */
        void cb$serverLoaded(ResourceKey key, WritableRegistry registry, RegistryOps.RegistryInfoLookup lookup);

        /** a client's registry built from the server's data (1.21.11: Loader.loadFromNetwork TAIL) */
        void cb$clientLoaded(ResourceKey key, WritableRegistry registry);
    }

    /** the lookup a load task's load(lookup, executor) was given, kept for its freeze (CbWallRegistryLoadMixin) */
    public interface LookupHolder {
        void cb$wallLookup(RegistryOps.RegistryInfoLookup lookup);
    }
}
