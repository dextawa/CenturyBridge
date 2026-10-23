package top.dext.centurybridge.step.v1_20_5.compat.fapi.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.container.ContainerFactory;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.container.CbScreenProviderRegistryImpl;

/**
 * Fabric API 0.97.3's (1.20.4) fabric-containers-v0 ScreenProviderRegistry: the client's screen factories
 * for container ids, opened when the server's fabric:container/open arrives (impl/container).
 */
@Deprecated
@Environment(EnvType.CLIENT)
public interface ScreenProviderRegistry {

    ScreenProviderRegistry INSTANCE = new CbScreenProviderRegistryImpl();

    <C extends net.minecraft.class_1703> void registerFactory(net.minecraft.class_2960 identifier, ContainerScreenFactory<C> containerScreenFactory);

    void registerFactory(net.minecraft.class_2960 identifier, ContainerFactory<net.minecraft.class_465> factory);
}
