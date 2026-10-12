package top.dext.centurybridge.compat.fapi.impl.registry.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The log lines of 1.20.1's registry alias handling, kept out of the mixin classes. */
public final class CbFapiRegistryAliases {

    private static final Logger LOGGER = LoggerFactory.getLogger(CbFapiRegistryAliases.class);

    private CbFapiRegistryAliases() {
    }

    public static void warnMissingTarget(net.minecraft.class_2960 old, net.minecraft.class_2960 newId, Object registryKey) {
        LOGGER.warn("Adding {} as an alias for {}, but the latter doesn't exist in registry {}", old, newId, registryKey);
    }

    public static void debugAdded(net.minecraft.class_2960 old, net.minecraft.class_2960 newId, Object registryKey) {
        LOGGER.debug("Adding alias {} for {} in registry {}", old, newId, registryKey);
    }
}
