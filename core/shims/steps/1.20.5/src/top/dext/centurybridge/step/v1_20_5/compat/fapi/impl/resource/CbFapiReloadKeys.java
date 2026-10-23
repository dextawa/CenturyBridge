package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.resource;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

/**
 * ResourceReloadListenerKeys.LOOT_TABLES (fieldGetters), which 1.20.5's Fabric API dropped with the loot
 * table reload listener: 1.20.5 loads loot tables as reloadable registries before any reload listener runs.
 * A 1.20.4 listener naming the key among its dependencies asked to run after loot tables were loaded; 1.20.5's
 * Fabric API would leave it out of the reload altogether ("Could not resolve dependencies"), as no listener
 * has that id. So the key comes with the listener it names: {@link LootTablesLoaded}, registered for server
 * data when the key is first read, which stands where 1.20.4's loot table listener stood for the ordering --
 * among the listeners after vanilla's -- and has nothing left to load when it runs.
 */
public final class CbFapiReloadKeys {

    private static final net.minecraft.class_2960 LOOT_TABLES = new net.minecraft.class_2960("minecraft:loot_tables");
    private static boolean registered;

    private CbFapiReloadKeys() {
    }

    public static synchronized net.minecraft.class_2960 LOOT_TABLES() {
        if (!registered) {
            registered = true;
            ResourceManagerHelper.get(net.minecraft.class_3264.field_14190).registerReloadListener(new LootTablesLoaded());
        }
        return LOOT_TABLES;
    }

    /** the point after which loot tables are loaded, for listeners ordered after it */
    static final class LootTablesLoaded implements IdentifiableResourceReloadListener {
        @Override
        public net.minecraft.class_2960 getFabricId() {
            return LOOT_TABLES;
        }

        @Override
        public Collection<net.minecraft.class_2960> getFabricDependencies() {
            return List.of();
        }

        @Override
        public CompletableFuture<Void> method_25931(net.minecraft.class_3302.class_4045 synchronizer, net.minecraft.class_3300 manager,
                                                     net.minecraft.class_3695 prepareProfiler, net.minecraft.class_3695 applyProfiler,
                                                     Executor prepareExecutor, Executor applyExecutor) {
            return synchronizer.method_18352(null).thenRunAsync(() -> { }, applyExecutor);
        }
    }
}
