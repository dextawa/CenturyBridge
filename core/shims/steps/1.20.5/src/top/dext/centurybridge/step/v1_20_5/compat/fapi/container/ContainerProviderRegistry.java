package top.dext.centurybridge.step.v1_20_5.compat.fapi.container;

import java.util.function.Consumer;
import top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.container.CbContainerProviderImpl;

/**
 * Fabric API 0.97.3's (1.20.4) fabric-containers-v0 ContainerProviderRegistry: container factories by id,
 * and opening one for a player (the server's screen handler, and its id, sync id and extra data sent to the
 * client on fabric:container/open, as in 1.20.4 -- impl/container).
 */
@Deprecated
public interface ContainerProviderRegistry {

    ContainerProviderRegistry INSTANCE = new CbContainerProviderImpl();

    void registerFactory(net.minecraft.class_2960 identifier, ContainerFactory<net.minecraft.class_1703> factory);

    void openContainer(net.minecraft.class_2960 identifier, net.minecraft.class_3222 player, Consumer<net.minecraft.class_2540> writer);

    void openContainer(net.minecraft.class_2960 identifier, net.minecraft.class_1657 player, Consumer<net.minecraft.class_2540> writer);
}
