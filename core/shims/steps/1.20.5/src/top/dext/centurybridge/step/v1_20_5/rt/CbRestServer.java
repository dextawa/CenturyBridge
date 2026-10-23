package top.dext.centurybridge.step.v1_20_5.rt;

import java.util.Set;

/**
 * 1.20.4's server API that 1.20.5 reshaped, with 1.20.4's bodies over 1.20.5's members: pack selection
 * from a DataPackConfig and the world's feature flags, loading server resources over a frozen registry
 * access, a 1.20.4 server subclass's tick time hook, ReloadableServerResources.updateRegistryTags(access).
 */
public final class CbRestServer {

    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    /**
     * MinecraftServer.configurePackRepository(repository, dataPacks, safeMode, enabledFeatures): 1.20.4's
     * body. 1.20.5 takes a WorldDataConfiguration and an init flag and, in safe mode, reports the
     * selection it made instead of the default configuration 1.20.4 answered.
     */
    public static net.minecraft.class_7712 method_29736(net.minecraft.class_3283 repository, net.minecraft.class_5359 dataPacks,
                                                       boolean safeMode, net.minecraft.class_7699 enabledFeatures) {
        repository.method_14445();
        if (safeMode) {
            repository.method_14447(java.util.Collections.singleton("vanilla"));
            return net.minecraft.class_7712.field_40260;
        }
        Set<String> selected = com.google.common.collect.Sets.newLinkedHashSet();
        for (String id : dataPacks.method_29547()) {
            if (repository.method_29207(id)) {
                selected.add(id);
            } else {
                LOGGER.warn("Missing data pack {}", id);
            }
        }
        for (net.minecraft.class_3288 pack : repository.method_14441()) {
            String id = pack.method_14463();
            if (dataPacks.method_29550().contains(id)) {
                continue;
            }
            net.minecraft.class_7699 requested = pack.method_45276();
            boolean isSelected = selected.contains(id);
            if (!isSelected && pack.method_29483().method_45279()) {
                if (requested.method_45400(enabledFeatures)) {
                    LOGGER.info("Found new data pack {}, loading it automatically", id);
                    selected.add(id);
                } else {
                    LOGGER.info("Found new data pack {}, but can't load it due to missing features {}", id,
                        net.minecraft.class_7701.method_45407(enabledFeatures, requested));
                }
            }
            if (isSelected && !requested.method_45400(enabledFeatures)) {
                LOGGER.warn("Pack {} requires features {} that are not enabled for this world, disabling pack.", id,
                    net.minecraft.class_7701.method_45407(enabledFeatures, requested));
                selected.remove(id);
            }
        }
        if (selected.isEmpty()) {
            LOGGER.info("No datapacks selected, forcing vanilla");
            selected.add("vanilla");
        }
        repository.method_14447(selected);
        return new net.minecraft.class_7712(selectedPacks(repository), repository.method_45278());
    }

    /** 1.20.4 MinecraftServer.getSelectedPacks(repository): the selected ids enabled, the other available ones disabled */
    private static net.minecraft.class_5359 selectedPacks(net.minecraft.class_3283 repository) {
        java.util.Collection<String> selected = repository.method_29210();
        java.util.List<String> enabled = com.google.common.collect.ImmutableList.copyOf(selected);
        java.util.List<String> disabled = repository.method_29206().stream().filter(id -> !selected.contains(id))
            .collect(com.google.common.collect.ImmutableList.toImmutableList());
        return new net.minecraft.class_5359(enabled, disabled);
    }

    /**
     * ReloadableServerResources.loadResources(resources, registries, ...): 1.20.4 loaded the server's
     * resources over the frozen registries it was given, loot data among them; 1.20.5 loads loot data
     * as a reloadable layer of the layered registry access first. The given registries are the running
     * server's own composite (what 1.20.4 code passes) -- then that server's layered access is the one
     * 1.20.5 reloads -- or else the layers below the reloadable one: a layered access whose first layer
     * holds them (without loot registries, which 1.20.4's never had).
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.concurrent.CompletableFuture<net.minecraft.class_5350> method_29466(
            net.minecraft.class_3300 resources, net.minecraft.class_5455.class_6890 registries, net.minecraft.class_7699 features,
            net.minecraft.class_2170.class_5364 selection, int functionLevel, java.util.concurrent.Executor background,
            java.util.concurrent.Executor game) {
        return net.minecraft.class_5350.method_29466(resources, layered(registries), features, selection, functionLevel, background, game);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static net.minecraft.class_7780<net.minecraft.class_7659> layered(net.minecraft.class_5455.class_6890 registries) {
        net.minecraft.server.MinecraftServer server = CbRegistries.server();
        if (server != null && server.method_46221().method_45926() == registries) {
            return server.method_46221();
        }
        net.minecraft.class_5455.class_6890 base = new net.minecraft.class_5455.class_6891(registries.method_40311()
            .filter(e -> net.minecraft.class_8490.method_51215().noneMatch(t -> t.comp_2519().equals(((net.minecraft.class_5455.class_6892) e).comp_350())))
            .map(e -> (net.minecraft.class_5455.class_6892) e)).method_40316();
        return new net.minecraft.class_7780<>(java.util.List.of(net.minecraft.class_7659.values()))
            .method_45930(net.minecraft.class_7659.field_39971, base);
    }

    /** a 1.20.4 ReloadableServerResources subclass overriding updateRegistryTags(access) (adapter hook) */
    public interface LegacyRegistryTags {
        void method_40421(net.minecraft.class_5455 registries);
    }

    /**
     * 1.20.5 calls updateRegistryTags() on its resources' own registries; 1.20.4 called
     * updateRegistryTags(access) with the server's registry access, which those are
     */
    public static void updateRegistryTags(LegacyRegistryTags resources) {
        resources.method_40421(((net.minecraft.class_5350) resources).method_58297().method_58289());
    }

    // ---------------------------------------------------------------- 1.20.4 server subclasses

    /**
     * MinecraftServer.isTickTimeLoggingEnabled(), which 1.20.5 made abstract: a 1.20.4 server subclass
     * logged tick times through its logTickTime(nanos) override, which this step calls every tick
     * (mixin/CbRestMinecraftServerMixin); 1.20.5's own debug-chart logging had no 1.20.4 counterpart: off
     */
    public static boolean tickTimeLogging(net.minecraft.server.MinecraftServer server) {
        return false;
    }

    /** MinecraftServer.getTickTimeLogger(), abstract in 1.20.5: a local logger of 1.20.5's tick dimensions (never fed: logging is off) */
    public static net.minecraft.class_8743 tickTimeLogger(net.minecraft.server.MinecraftServer server) {
        return new net.minecraft.class_9191(net.minecraft.class_9160.values().length);
    }

    private CbRestServer() {
    }
}
